import json, os
import numpy as np
import pytest
from conftest import needs_world, REGION
from hogwarts import content, sections, walk, registry
from hogwarts.volume import Volume

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
            # se llega a la página: pies delante (en esa celda o justo debajo) o en una celda de al lado
            # (se coge con clic desde ahí; p. ej. si debajo hay una vela)
            r = res['reached_mask']
            fx, fz = x + dx, z + dz
            near = [(fx + a, fz + b) for a, b in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1))]
            assert any(r[yy, zz, xx] for xx, zz in near for yy in (y, y - 1)), (s['id'], pg, 'delante no se llega')

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


OPP = {'NORTH': 'SOUTH', 'SOUTH': 'NORTH', 'EAST': 'WEST', 'WEST': 'EAST'}

def placed(vol, s):
    """Copia del volumen con lo que pone HogwartsBuilder: derrumbes, verja cerrada, velas, notas y aire de páginas."""
    v = Volume(vol.data.copy(), vol.palette)
    for x, y, z, st in s['seal']:
        v.set(x, y, z, st)
    for g in s['gate']:
        x, y, z = p3(g)
        v.set(x, y, z, s['gateBlock'])
    for p, st in s['lightBlocks']:
        x, y, z = p3(p)
        v.set(x, y, z, st)
    for n in s['notes']:
        x, y, z = p3(n['pos'])
        dx, dz = FACE[OPP[n['facing']]]
        v.set(x + dx, y, z + dz, n['support'])
        v.set(x, y, z, 'minecraft:oak_wall_sign[facing=%s]' % n['facing'].lower())
    for pg in s['pages']:
        (x, y, z), f = p3(pg), pg.split('|')[1]
        dx, dz = FACE[f]
        v.set(x + dx, y, z + dz, 'minecraft:air')
    return v

@needs_world
def test_sealed_with_real_blocks(built):
    """Con los bloques tal cual quedan en el mundo, desde los spawns no se sale de la sección (sin máscara 'blocked')."""
    vol, ctx, data = built
    for s in data['sections']:
        res = content.CHECKS[s['id']]
        v = placed(vol, s)
        pas = walk.passable(v)
        st = walk.standable(v, pas)
        r = walk.flood(v, st, [p3(sp) for sp in s['spawns']])
        leak = int((r & ~res['region']).sum())
        assert leak == 0, (s['id'], leak)
        # con la verja abierta (aire, como en la huida) se llega a la salida
        for g in s['gate']:
            x, y, z = p3(g)
            v.set(x, y, z, 'minecraft:air')
        pas = walk.passable(v)
        st = walk.standable(v, pas)
        r = walk.flood(v, st, [p3(s['spawns'][0])])
        (x1, y1, z1), (x2, y2, z2) = (p3(e) for e in s['exit'])
        box = r[min(y1, y2):max(y1, y2) + 1, min(z1, z2):max(z1, z2) + 1, min(x1, x2):max(x1, x2) + 1]
        assert box.any(), (s['id'], 'no se llega a la salida con la verja abierta')

WET = ('minecraft:water', 'minecraft:bubble_column', 'minecraft:kelp', 'minecraft:kelp_plant', 'minecraft:seagrass',
       'minecraft:tall_seagrass')

def wet(state):
    return state.split('[')[0] in WET or 'waterlogged=true' in state

@needs_world
def test_content_not_underwater(built):
    """Pilas, velas y notas en seco: una pila en el fondo del lago sube flotando y una nota no se lee."""
    vol, _, data = built
    for s in data['sections']:
        for b in s['batteries']:
            x, y, z = p3(b)
            assert not wet(vol.get(x, y, z)) and not wet(vol.get(x, y + 1, z)), (s['id'], 'pila', b, vol.get(x, y, z))
        for p, _st in s['lightBlocks']:
            x, y, z = p3(p)
            assert not wet(vol.get(x, y, z)), (s['id'], 'vela', p)
        for n in s['notes']:
            x, y, z = p3(n['pos'])
            assert not wet(vol.get(x, y, z)), (s['id'], 'nota', n['pos'])

@needs_world
def test_floor_for_slender(built):
    """'floor' = celdas de pies alcanzables y secas de la sección: Slender solo aparece ahí (no tras un derrumbe ni
    dentro de la roca vaciada)."""
    vol, ctx, data = built
    H, L, W = vol.data.shape
    for s in data['sections']:
        floor = content.decode_cells(s['floor'])
        assert len(floor) > 1000, s['id']
        cells = {(i % W, i // (W * L), (i // W) % L) for i in floor}
        for sp in s['spawns']:
            assert p3(sp) in cells, (s['id'], sp)
        assert not cells & {tuple(c[:3]) for c in s['seal']}, s['id']
        assert not any(wet(vol.get(x, y, z)) for x, y, z in cells), s['id']
        assert list(floor) == sorted(floor)

def test_cells_codec_roundtrip():
    m = np.zeros((4, 5, 6), bool)
    m[1, 2, 3] = m[3, 4, 5] = m[0, 0, 0] = True
    enc = content.encode_cells(m)
    assert content.decode_cells(enc) == [0, 3 + 2 * 6 + 1 * 6 * 5, 5 + 4 * 6 + 3 * 6 * 5]

def test_phrases_unique_across_sections():
    """En LA NOCHE ENTERA se juegan las 7 seguidas: ninguna frase de página se repite entre secciones."""
    seen = {}
    for d in sections.load_defs():
        for ph in content.load_content(d.id)['phrases']:
            assert ph not in seen, (ph, seen.get(ph), d.id)
            seen[ph] = d.id
