import numpy as np
import nbtlib
from nbtlib import Compound, List, String, LongArray, Byte
from conftest import needs_world, REGION
from hogwarts import anvil

def pack(values, bits):
    per = 64 // bits
    longs = []
    for i in range(0, len(values), per):
        v = 0
        for j, x in enumerate(values[i:i + per]):
            v |= x << (j * bits)
        if v >= 1 << 63:
            v -= 1 << 64
        longs.append(v)
    return LongArray(longs)

def section(names, values, bits):
    pal = List[Compound]([Compound({'Name': String(n)}) for n in names])
    return Compound({'Y': Byte(0), 'Palette': pal, 'BlockStates': pack(values, bits)})

def test_decode_bits():
    names = ['minecraft:air', 'minecraft:stone', 'minecraft:dirt', 'minecraft:glass', 'minecraft:sand']
    vals = [(i * 7) % 5 for i in range(4096)]
    pal, arr = anvil.decode_section(section(names, vals, 4))
    assert pal == names
    assert arr.shape == (16, 16, 16)
    # índice = y*256 + z*16 + x
    assert arr[0, 0, 1] == vals[1]
    assert arr[0, 1, 0] == vals[16]
    assert arr[1, 0, 0] == vals[256]
    assert arr[15, 15, 15] == vals[4095]

def test_decode_17_states():
    names = ['minecraft:air'] + [f'minecraft:b{i}' for i in range(16)]
    vals = [i % 17 for i in range(4096)]
    pal, arr = anvil.decode_section(section(names, vals, 5))
    flat = arr.reshape(-1)
    assert list(flat[:40]) == vals[:40]
    assert flat[4095] == vals[4095]

def test_state_props_sorted():
    c = Compound({'Name': String('minecraft:oak_stairs'),
                  'Properties': Compound({'half': String('bottom'), 'facing': String('north')})})
    assert anvil.state_str(c) == 'minecraft:oak_stairs[facing=north,half=bottom]'

@needs_world
def test_real_column():
    vol, tes, ents = anvil.load_world(REGION, box=(-201, -199, 519, 521, 0, 254))
    col = [vol.get(1, y, 1).split('[')[0] for y in range(255)]
    assert col[0] == 'minecraft:bedrock'
    assert col[64] == 'minecraft:spruce_planks'
    assert col[84] == 'minecraft:coarse_dirt'
    assert all(c == 'minecraft:air' for c in col[85:192])

@needs_world
def test_volume_shape():
    vol, tes, ents = anvil.load_world(REGION)
    assert vol.data.shape == (255, 413, 383)
    assert len(tes) > 10000
