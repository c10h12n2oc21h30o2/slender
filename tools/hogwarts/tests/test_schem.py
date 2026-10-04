import nbtlib
import numpy as np
from hogwarts.volume import Volume
from hogwarts import schem

def test_roundtrip_markers(tmp_path):
    v = Volume.empty(5, 4, 3)
    v.set(4, 0, 0, 'minecraft:stone'); v.set(0, 3, 0, 'minecraft:dirt'); v.set(0, 0, 2, 'minecraft:glass')
    p = tmp_path / 't.schem'
    schem.write(v, str(p))
    (w, h, l), pal, data = schem.read(str(p))
    assert (w, h, l) == (5, 4, 3)
    assert pal[data[0, 0, 4]] == 'minecraft:stone'
    assert pal[data[3, 0, 0]] == 'minecraft:dirt'
    assert pal[data[0, 2, 0]] == 'minecraft:glass'
    assert sum(pal[i] != 'minecraft:air' for i in data.reshape(-1)) == 3

def test_varint_300(tmp_path):
    v = Volume.empty(300, 1, 1)
    for x in range(300):
        v.set(x, 0, 0, f'minecraft:test_{x}')
    p = tmp_path / 'v.schem'
    schem.write(v, str(p))
    _, pal, data = schem.read(str(p))
    assert [pal[i] for i in data[0, 0]] == [f'minecraft:test_{x}' for x in range(300)]

def test_header(tmp_path):
    v = Volume.empty(2, 2, 2)
    p = tmp_path / 'h.schem'
    schem.write(v, str(p))
    root = nbtlib.load(str(p))
    s = root['Schematic']
    assert int(s['Version']) == 3
    assert int(s['DataVersion']) == 5023
    assert list(map(int, s['Offset'])) == [0, 0, 0]
    assert isinstance(s['Width'], nbtlib.Short) and isinstance(s['Length'], nbtlib.Short)
