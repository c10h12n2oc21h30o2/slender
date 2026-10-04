import random
from hogwarts.volume import Volume
from hogwarts import clean

def vol(w=3, h=3, l=3):
    return Volume.empty(w, h, l)

def test_strip_command_blocks():
    v = vol()
    v.set(1, 1, 1, 'minecraft:command_block[conditional=false,facing=north]')
    report = clean.strip(v)
    assert v.get(1, 1, 1) == 'minecraft:air'
    assert report['minecraft:command_block'] == 1

def test_fire_rules():
    v = vol()
    v.set(0, 0, 0, 'minecraft:netherrack'); v.set(0, 1, 0, 'minecraft:fire[age=0,east=false,north=false,south=false,up=false,west=false]')
    v.set(2, 0, 2, 'minecraft:oak_planks'); v.set(2, 1, 2, 'minecraft:fire[age=0,east=false,north=false,south=false,up=false,west=false]')
    clean.strip(v)
    assert v.get(0, 1, 0).startswith('minecraft:fire')
    assert v.get(2, 1, 2) == 'minecraft:air'

def test_dim_lights_ratio():
    v = Volume.empty(10, 1, 10)
    for x in range(10):
        for z in range(10):
            v.set(x, 0, z, 'minecraft:glowstone')
    kept = clean.dim_lights(v, random.Random(1234))
    assert len(kept) == 10
    assert sum(v.get(x, 0, z) == 'minecraft:honeycomb_block' for x in range(10) for z in range(10)) == 90
    v2 = Volume.empty(10, 1, 10)
    for x in range(10):
        for z in range(10):
            v2.set(x, 0, z, 'minecraft:glowstone')
    assert clean.dim_lights(v2, random.Random(1234)) == kept

def test_end_rod_keeps_facing():
    v = Volume.empty(1, 1, 1)
    v.set(0, 0, 0, 'minecraft:end_rod[facing=up]')
    clean.dim_lights(v, random.Random(1234), keep=0.0)
    assert v.get(0, 0, 0) == 'minecraft:lightning_rod[facing=up]'

def test_lake_wall():
    v = Volume.empty(5, 40, 400)
    clean.contain_lake(v)
    assert v.get(0, 15, 200) == 'minecraft:barrier'
    assert v.get(4, 15, 200) == 'minecraft:barrier'
    assert v.get(2, 15, 0) == 'minecraft:barrier'
    assert v.get(0, 31, 200) != 'minecraft:barrier'
    assert v.get(2, 15, 200) == 'minecraft:air'

def test_hollow_only_hidden_terrain():
    v = Volume.empty(5, 5, 5)
    for x in range(5):
        for y in range(5):
            for z in range(5):
                v.set(x, y, z, 'minecraft:stone')
    v.set(2, 4, 2, 'minecraft:air')  # hueco en la cara de arriba
    n = clean.hollow(v)
    assert v.get(2, 2, 2) == 'minecraft:air'      # escondido: se vacía
    assert v.get(2, 3, 2) == 'minecraft:stone'    # toca el hueco de arriba: se queda
    assert v.get(0, 2, 2) == 'minecraft:stone'    # borde del volumen: se queda
    assert n == 26                                # 3x3x3 interior menos (2,3,2)
