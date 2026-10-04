import json, os
import numpy as np
from conftest import needs_world, REGION
from hogwarts.volume import Volume
from hogwarts import walk, sections

def room(w=7, h=6, l=5):
    """Caja de piedra hueca con suelo en y=0 y techo en y=h-1."""
    v = Volume.empty(w, h, l)
    for x in range(w):
        for z in range(l):
            for y in range(h):
                if y in (0, h - 1) or x in (0, w - 1) or z in (0, l - 1):
                    v.set(x, y, z, 'minecraft:stone')
    return v

def test_flood_blocked():
    v = room(9, 5, 3)          # pasillo interior x 1..7, z 1, suelo y=0
    pas = walk.passable(v)
    st = walk.standable(v, pas)
    blocked = np.zeros_like(st)
    blocked[1, 1, 4] = True     # derrumbe en medio, 2 de alto (como los de verdad)
    blocked[2, 1, 4] = True
    r = walk.flood(v, st, [(1, 1, 1)], blocked)
    assert r[1, 1, 3] and not r[1, 1, 5]

def test_stairs_step():
    v = room(9, 6, 3)
    v.set(4, 1, 1, 'minecraft:oak_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]')
    for x in range(5, 8):
        v.set(x, 1, 1, 'minecraft:stone')   # plataforma un bloque más alta
    pas = walk.passable(v); st = walk.standable(v, pas)
    r = walk.flood(v, st, [(1, 1, 1)])
    assert r[2, 1, 6]

def test_seal_closes_door():
    v = room(11, 5, 3)
    for y in range(1, 4):
        v.set(5, y, 1, 'minecraft:stone')   # pared en medio
    v.set(5, 1, 1, 'minecraft:oak_door[facing=east,half=lower,hinge=left,open=false,powered=false]')
    v.set(5, 2, 1, 'minecraft:oak_door[facing=east,half=upper,hinge=left,open=false,powered=false]')
    pas = walk.passable(v); st = walk.standable(v, pas)
    region = np.zeros_like(st); region[:, :, :5] = True     # sección = mitad oeste
    cells = sections.seal_cells(region, st, pas, keep=set())
    assert (5, 1, 1) in cells and (5, 2, 1) in cells

def test_section_mask_indoor_vs_outdoor():
    v = Volume.empty(6, 8, 3)
    for x in range(6):
        for z in range(3):
            v.set(x, 0, z, 'minecraft:stone')
    for x in range(3):
        for z in range(3):
            v.set(x, 5, z, 'minecraft:stone_bricks')   # techo solo sobre x 0..2
    pas = walk.passable(v); st = walk.standable(v, pas); roof = walk.roofed(v)
    indoor = sections.SectionDef('a', 'A', 1, [{'x': [0, 5], 'y': [0, 7], 'z': [0, 2]}], False)
    outdoor = sections.SectionDef('b', 'B', 2, [{'x': [0, 5], 'y': [0, 7], 'z': [0, 2]}], True)
    mi = sections.section_mask(indoor, st, roof); mo = sections.section_mask(outdoor, st, roof)
    assert mi[1, 1, 1] and not mi[1, 1, 4]
    assert mo[1, 1, 4] and not mo[1, 1, 1]

@needs_world
def test_real_sections_closed():
    from hogwarts import main
    vol, _, _, _ = main.build_volume(REGION)
    ctx = sections.Context(vol)
    defs = sections.load_defs()
    others = ctx.indoor_union(defs)
    for d in defs:
        res = ctx.check(d, others=others if d.rest else None)
        assert res['leak'] == 0, (d.id, res)
        assert res['reached'] > 0, (d.id, res)

def test_snap_keeps_rooms_whole():
    # dos plantas: suelo y=0 y forjado y=5; techo y=10. Corte nominal en y=7 (en medio de la planta de arriba)
    v = Volume.empty(5, 12, 5)
    for x in range(5):
        for z in range(5):
            for y in (0, 5, 10):
                v.set(x, y, z, 'minecraft:stone')
    pas = walk.passable(v)
    low = sections.SectionDef('l', 'L', 1, [{'x': [0, 4], 'y': [0, 6], 'z': [0, 4], 'snap': True}], False)
    high = sections.SectionDef('h', 'H', 2, [{'x': [0, 4], 'y': [7, 11], 'z': [0, 4], 'snap': True}], False)
    roof = np.ones_like(pas)
    rl = sections.region(low, roof, pas); rh = sections.region(high, roof, pas)
    assert rh[6, 2, 2] and rh[9, 2, 2]        # toda la planta de arriba (y 6..9) va a la sección alta
    assert not rl[6, 2, 2] and rl[3, 2, 2]

def test_seal_covers_stairs_outside_region():
    # escalón (escalera) justo fuera de la región: el jugador pisaría la escalera → también se tapa
    v = room(11, 6, 3)
    v.set(6, 1, 1, 'minecraft:oak_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]')
    pas = walk.passable(v); st = walk.standable(v, pas)
    region = np.zeros_like(st); region[:, :, :6] = True
    cells = sections.seal_cells(region, st, pas, keep=set())
    assert (6, 1, 1) in cells

def test_flood_blocked_stairs_not_walkable():
    # la escalera tapada por un derrumbe ya no es un sitio donde poner los pies (aunque encima haya aire)
    v = room(9, 6, 3)
    v.set(4, 1, 1, 'minecraft:oak_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]')
    pas = walk.passable(v); st = walk.standable(v, pas)
    blocked = np.zeros_like(st); blocked[1, 1, 4] = True
    r = walk.flood(v, st, [(1, 1, 1)], blocked)
    assert not r[1, 1, 4]

def test_seal_closes_pit_through_region_floor():
    # pozo: dentro de la región hay un agujero que cae por debajo de la región
    v = room(7, 12, 5)
    for x in range(1, 6):
        for z in range(1, 4):
            v.set(x, 6, z, 'minecraft:stone')       # forjado a y=6
    v.set(3, 6, 2, 'minecraft:air')                  # agujero en el forjado
    pas = walk.passable(v); st = walk.standable(v, pas)
    region = np.zeros_like(st); region[7:, :, :] = True     # sección = planta de arriba
    cells = sections.seal_cells(region, st, pas, keep=set())
    assert (3, 6, 2) in cells

def test_seal_iterates_on_new_floors():
    # pozo en la columna x=3: al taparlo con derrumbe se puede pisar encima, así que la celda vecina
    # de fuera de la región (x=4, misma altura) también hay que taparla
    v = room(7, 9, 3)
    for x in range(1, 6):
        v.set(x, 4, 1, 'minecraft:stone')       # forjado y=4
    v.set(3, 4, 1, 'minecraft:air')              # pozo que baja hasta el suelo y=0
    v.set(4, 4, 1, 'minecraft:air')              # y otro hueco al lado, fuera de la región
    pas = walk.passable(v); st = walk.standable(v, pas)
    region = np.zeros_like(st); region[5:, :, :4] = True    # región: planta de arriba, solo x < 4
    cells = set(sections.seal_cells(region, st, pas, keep=set()))
    assert (3, 4, 1) in cells        # tapa del pozo
    assert (4, 5, 1) in cells        # vecino a la altura de los pies sobre la tapa

def test_rest_section_is_box_minus_other_sections():
    v = Volume.empty(6, 6, 3)
    for x in range(6):
        for z in range(3):
            v.set(x, 0, z, 'minecraft:stone')
    for z in range(3):
        v.set(0, 4, z, 'minecraft:stone_bricks'); v.set(1, 4, z, 'minecraft:stone_bricks')  # alero sobre x 0..1
    pas = walk.passable(v); roof = walk.roofed(v)
    indoor = np.zeros_like(pas); indoor[:, :, 0] = True          # otra sección ocupa x=0
    rest = sections.SectionDef('t', 'T', 7, [{'x': [0, 5], 'y': [0, 5], 'z': [0, 2]}], True, rest=True)
    reg = sections.region(rest, roof, pas, others=indoor)
    assert reg[1, 1, 1]          # bajo el alero, pero no es de ninguna sección: es de Terrenos
    assert not reg[1, 1, 0]      # de otra sección
