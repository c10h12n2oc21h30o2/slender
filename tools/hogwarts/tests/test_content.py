import json, os
import numpy as np
import pytest
from conftest import needs_world, REGION
from hogwarts import content, sections, walk, registry

DATA = os.path.join(os.path.dirname(__file__), '..', 'data')
FACE = {'NORTH': (0, -1), 'SOUTH': (0, 1), 'EAST': (1, 0), 'WEST': (-1, 0)}
NON_FULL = ('_slab', '_stairs', '_pane', 'iron_bars', '_fence', '_wall', 'glass_pane', '_door', '_trapdoor', '_sign',
            '_banner', 'leaves', 'chain', 'lantern', 'ladder', 'barrier', '_carpet', 'head', 'skull', 'lectern',
            'cauldron', 'chest', 'bed', 'anvil', 'brewing_stand', 'hopper', 'campfire', 'bell', 'flower_pot',
            'end_rod', 'lightning_rod', 'scaffolding', 'cobweb', 'candle', 'cake', 'grindstone', 'composter')

def test_sound_field():
    assert content.sound_field('block.bell.use') == 'BLOCK_BELL_USE'

def test_fmt_helpers():
    assert content.pos([1, 2, 3]) == '1,2,3'
    assert content.loc([1, 2, 3, 90]) == '1.5,2.0,3.5,90.0'

@pytest.fixture(scope='module')
def built():
    if not os.path.isdir(REGION):
        pytest.skip('falta el mundo')
    from hogwarts import main
    vol, _, _, _ = main.build_volume(REGION)
    ctx = sections.Context(vol)
    data = content.compile_all(vol, ctx)
    return vol, ctx, data

def S(data):
    return {s['id']: s for s in data['sections']}

def p3(s):
    return tuple(int(float(v)) for v in s.split('|')[0].split(',')[:3])

@needs_world
def test_counts(built):
    _, _, data = built
    assert len(data['sections']) == 7
    for s in data['sections']:
        assert 12 <= len(s['pages']) <= 16, s['id']
        assert len(s['batteries']) >= 8, s['id']
        assert 3 <= len(s['spawns']) <= 4, s['id']
        assert 2 <= len(s['notes']) <= 3, s['id']
        assert len(s['phrases']) == 10, s['id']
        assert 4 <= len(s['sounds']) <= 8, s['id']
        assert 8 <= len(s['lights']) <= 12, s['id']
        assert s['gate'] and len(s['exit']) == 2, s['id']

@needs_world
def test_page_walls(built):
    vol, ctx, data = built
    for s in data['sections']:
        res = content.CHECKS[s['id']]
        for pg in s['pages']:
            (x, y, z), f = p3(pg), pg.split('|')[1]
            wall = vol.get(x, y, z)
            assert not ctx.pas[y, z, x] and not any(k in wall for k in NON_FULL), (s['id'], pg, wall)
            dx, dz = FACE[f]
            assert ctx.pas[y, z + dz, x + dx], (s['id'], pg, 'delante no hay aire')
            # delante de la página se puede llegar (pies en esa celda o justo debajo)
            r = res['reached_mask']
            assert r[y, z + dz, x + dx] or r[y - 1, z + dz, x + dx], (s['id'], pg, 'delante no se llega')

@needs_world
def test_spawns_batteries_standable(built):
    vol, ctx, data = built
    for s in data['sections']:
        res = content.CHECKS[s['id']]
        sealed = {tuple(c[:3]) for c in s['seal']} | {p3(g) for g in s['gate']}
        for sp in s['spawns'] + s['batteries']:
            x, y, z = p3(sp)
            assert res['reached_mask'][y, z, x], (s['id'], sp)
            assert (x, y, z) not in sealed and (x, y + 1, z) not in sealed, (s['id'], sp)

@needs_world
def test_reachable_exit_with_gate_open(built):
    vol, ctx, data = built
    for s in data['sections']:
        res = content.CHECKS[s['id']]
        blocked = np.zeros_like(ctx.st)
        for c in s['seal']:
            blocked[c[1], c[2], c[0]] = True
        r = walk.flood(vol, ctx.st, [p3(s['spawns'][0])], blocked)
        (x1, y1, z1), (x2, y2, z2) = (p3(e) for e in s['exit'])
        box = r[min(y1, y2):max(y1, y2) + 1, min(z1, z2):max(z1, z2) + 1, min(x1, x2):max(x1, x2) + 1]
        assert box.any(), (s['id'], 'no se llega a la salida con el gate abierto')

@needs_world
def test_note_lines(built):
    _, _, data = built
    for s in data['sections']:
        for n in s['notes']:
            assert len(n['lines']) == 4 and all(len(l) <= 15 for l in n['lines']), n

@needs_world
def test_sounds_exist(built):
    _, _, data = built
    fields = {content.sound_field(k) for k in registry.load_sounds(os.path.join(DATA, 'sounds_26.3.json'))}
    for s in data['sections']:
        for z in s['sounds']:
            assert z.split('|')[2] in fields, z

@needs_world
def test_lights_lightable(built):
    _, _, data = built
    for s in data['sections']:
        lp = {l.split('|')[0] for l in s['lights']}
        for p, state in s['lightBlocks']:
            base = state.split('[')[0]
            assert base in ('minecraft:candle', 'minecraft:redstone_lamp', 'minecraft:campfire', 'minecraft:copper_bulb')
            assert 'lit=true' in state and p in lp

@needs_world
def test_board_names_fit(built):
    _, _, data = built
    for s in data['sections']:
        if s['board']:
            assert all(len(p[0]) <= 13 for p in s['board']['places']), s['id']
            assert all(len(t) <= 15 for t in s['board']['title'])
