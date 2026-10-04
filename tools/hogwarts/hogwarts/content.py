"""Contenido de cada sección (spawns, páginas, pilas, salida, tablón, notas, frases, sonidos y luces)
y compilación a src/main/resources/maps/hogwarts/secciones.json (coordenadas del schem).

Los ficheros data/contenido/<id>.json van en coordenadas ORIGINALES, como secciones_def.json."""
import json, os, random, sys
from collections import deque
import numpy as np
from . import sections, walk, signs as signs_mod
from .volume import Volume

HERE = os.path.dirname(__file__)
CONTENT_DIR = os.path.join(HERE, '..', 'data', 'contenido')
OX, OY, OZ = sections.OFFSET
FACES = {'NORTH': (0, -1), 'SOUTH': (0, 1), 'EAST': (1, 0), 'WEST': (-1, 0)}
CANDLE = 'minecraft:candle[candles=4,lit=true,waterlogged=false]'
CHECKS: dict = {}  # id → resultado de Context.check (lo usan los tests)


def sound_field(key: str) -> str:
    return key.upper().replace('.', '_')


def pos(p) -> str:
    return f'{int(p[0])},{int(p[1])},{int(p[2])}'


def loc(p) -> str:
    return f'{p[0] + 0.5},{float(p[1])},{p[2] + 0.5},{float(p[3]) if len(p) > 3 else 0.0}'


def conv(p):
    """Original → schem."""
    return [p[0] + OX, p[1] + OY, p[2] + OZ] + list(p[3:])


def load_content(sid: str):
    path = os.path.join(CONTENT_DIR, f'{sid}.json')
    if not os.path.exists(path):
        return None
    with open(path, encoding='utf-8') as f:
        return json.load(f)


def compile_section(d: sections.SectionDef, c: dict, ctx: sections.Context, others) -> dict:
    gate = [tuple(conv(g)) for g in c['gate']]
    spawns = [conv(s) for s in c['spawns']]
    res = ctx.check(d, spawns=spawns, keep=frozenset(gate), others=others if d.rest else None)
    CHECKS[d.id] = res
    mask = res['reached_mask'] & res['region']
    ys, zs, xs = np.nonzero(mask)
    bounds = [int(xs.min()) - 3, int(zs.min()) - 3, int(xs.max()) + 3, int(zs.max()) + 3]
    e = c['exit']
    exit_ = [pos(conv(e[0:3])), pos(conv(e[3:6]))]
    for x, y, z in (conv(e[0:3])[:3], conv(e[3:6])[:3]):
        bounds = [min(bounds[0], x - 2), min(bounds[1], z - 2), max(bounds[2], x + 2), max(bounds[3], z + 2)]
    seal = sections.rubble(res['seal'], random.Random(1234))
    b = c.get('board')
    board = None
    if b:
        board = {'pos': pos(conv(b['pos'])), 'title': b['title'],
                 'places': [[n, x + OX, z + OZ] for n, x, z in b['places']]}
    lights = [conv(l[:3]) for l in c['lights']]
    return {
        'id': d.id, 'name': d.name, 'order': d.order, 'icon': d.icon, 'story': d.order,
        'bounds': bounds, 'fallY': int(ys.min()) - 6,
        'spawns': [loc(s) for s in spawns],
        'pages': [f'{pos(conv(p[:3]))}|{p[3]}|{p[4]}' for p in c['pages']],
        'batteries': [pos(conv(p)) for p in c['batteries']],
        'gate': [pos(g) for g in gate], 'gateBlock': c['gate_block'], 'exit': exit_,
        'seal': [[x, y, z, s] for x, y, z, s in seal],
        'board': board,
        'notes': [{'pos': pos(conv(n['pos'])), 'facing': n['facing'], 'lines': n['lines'], 'support': n['support']}
                  for n in c['notes']],
        'lights': [f'{pos(l)}|flicker' for l in lights],
        'lightBlocks': [[pos(conv(l[:3])), l[3]] for l in c['lights']],
        'sounds': [f'{pos(conv(s[:3]))}|{s[3]}|{s[4]}|{s[5]}|{s[6]}|{s[7]}' for s in c['sounds']],
        'phrases': c['phrases'],
    }


def compile_all(vol: Volume, ctx: sections.Context = None) -> dict:
    ctx = ctx or sections.Context(vol)
    defs = sections.load_defs()
    others = ctx.indoor_union(defs)
    out = []
    for d in defs:
        c = load_content(d.id)
        if c is None:
            raise FileNotFoundError(f'falta data/contenido/{d.id}.json')
        out.append(compile_section(d, c, ctx, others))
    h, l, w = vol.data.shape
    return {'version': 1, 'size': [w, h, l], 'sections': out}


def write_all(vol: Volume, out_dir: str) -> None:
    if not all(load_content(d.id) for d in sections.load_defs()):
        print('secciones.json: falta contenido, no se escribe')
        return
    data = compile_all(vol)
    path = os.path.join(out_dir, 'maps', 'hogwarts', 'secciones.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, separators=(',', ':'))
    print('secciones.json:', ', '.join(f"{s['id']}={len(s['pages'])}p/{len(s['seal'])}d" for s in data['sections']))


# ---------------------------------------------------------------- ayudas para escribir el contenido a mano

def _fps(points: np.ndarray, k: int, rng: random.Random, min_d: float = 0) -> list[int]:
    """Muestreo del punto más lejano: k índices repartidos."""
    if len(points) == 0:
        return []
    chosen = [rng.randrange(len(points))]
    d = np.linalg.norm(points - points[chosen[0]], axis=1)
    while len(chosen) < k:
        i = int(np.argmax(d))
        if d[i] <= min_d:
            break
        chosen.append(i)
        d = np.minimum(d, np.linalg.norm(points - points[i], axis=1))
    return chosen


NON_FULL = ('_slab', '_stairs', '_pane', 'iron_bars', '_fence', '_wall', '_door', '_trapdoor', '_sign', '_banner',
            'leaves', 'chain', 'lantern', 'ladder', 'barrier', '_carpet', 'head', 'skull', 'lectern', 'cauldron',
            'chest', 'bed', 'anvil', 'brewing_stand', 'hopper', 'campfire', 'bell', 'flower_pot', 'end_rod',
            'lightning_rod', 'scaffolding', 'cobweb', 'candle', 'cake', 'grindstone', 'composter', 'glass',
            'water', 'shulker', 'barrel', 'bookshelf', 'loom', 'blast_furnace', 'command', 'piston', 'observer',
            'dispenser', 'dropper', 'note_block', 'jukebox', 'crafting', 'furnace', 'smoker', 'beehive', 'enchanting')


def wall_candidates(vol: Volume, ctx: sections.Context, reached: np.ndarray):
    """(x, y, z, cara) de paredes macizas con aire delante a la altura de los ojos y suelo alcanzable debajo."""
    full = vol.mask(lambda s: not any(k in s for k in NON_FULL)) & ~ctx.pas
    out = []
    feet = reached
    for f, (dx, dz) in FACES.items():
        H, L, W = full.shape
        ys, zs, xs = np.nonzero(full)
        fx, fz = xs + dx, zs + dz
        ok = (fx >= 0) & (fx < W) & (fz >= 0) & (fz < L) & (ys >= 1)
        ys, zs, xs, fx, fz = ys[ok], zs[ok], xs[ok], fx[ok], fz[ok]
        ok = ctx.pas[ys, fz, fx] & feet[ys - 1, fz, fx]
        for y, z, x in zip(ys[ok], zs[ok], xs[ok]):
            out.append((int(x), int(y), int(z), f))
    return out


def sign_labels(vol: Volume):
    """Carteles traducidos (posición schem, nombre de sala)."""
    from . import extras
    path = os.path.join(HERE, '..', '..', '..', 'src', 'main', 'resources', 'maps', 'hogwarts', 'extra.json.gz')
    data = extras.read(path)
    out = []
    for s in data['signs']:
        text = ' '.join(l.strip() for l in s['lines'] if l.strip() and not set(l.strip()) <= set('<->|IV '))
        if text:
            out.append((s['pos'], text))
    return out


def propose(sid: str, vol: Volume, ctx: sections.Context, seed: int = 7) -> dict:
    """Propuesta de contenido (coordenadas originales) para editar a mano."""
    defs = {d.id: d for d in sections.load_defs()}
    d = defs[sid]
    others = ctx.indoor_union(defs.values()) if d.rest else None
    res = ctx.check(d, others=others)
    reached = res['reached_mask'] & res['region']
    rng = random.Random(seed)
    labels = sign_labels(vol)
    lab_pos = np.array([p for p, _ in labels])
    cands = wall_candidates(vol, ctx, reached)
    named = []
    for x, y, z, f in cands:
        dd = np.abs(lab_pos - np.array([x, y, z])).max(axis=1)
        j = int(np.argmin(dd))
        if dd[j] <= 6:
            named.append((x, y, z, f, labels[j][1]))
    pts = np.array([[x, y * 2, z] for x, y, z, _, _ in named], float)
    pick = _fps(pts, 40, rng, 6)
    seen_l, pages = set(), []
    for i in pick:
        x, y, z, f, lab = named[i]
        if lab in seen_l:
            continue
        seen_l.add(lab)
        pages.append([x - OX, y - OY, z - OZ, f, lab[:40]])
        if len(pages) == 14:
            break
    ys, zs, xs = np.nonzero(reached)
    feet = np.c_[xs, ys, zs]
    solid_below = ~ctx.pas[ys - 1, zs, xs]
    feet = feet[solid_below]
    bat = [feet[i] for i in _fps(feet.astype(float) * [1, 2, 1], 10, rng, 5)]
    lig = [feet[i] for i in _fps(feet.astype(float) * [1, 2, 1], 10, random.Random(seed + 1), 5)]
    sx, sy, sz = res['starts'][0]
    near = feet[np.argsort(np.abs(feet - [sx, sy, sz]).sum(axis=1))]
    spawns = []
    for x, y, z in near:
        if all(abs(x - a) + abs(z - c) >= 2 for a, _, c, _ in spawns) and abs(y - sy) <= 1:
            spawns.append([int(x), int(y), int(z), 0])
        if len(spawns) == 4:
            break
    notes = []
    for i in _fps(np.array([[x, y * 2, z] for x, y, z, _ in cands], float), 3, random.Random(seed + 2), 6):
        x, y, z, f = cands[i]
        dx, dz = FACES[f]
        notes.append({'pos': [x + dx - OX, y - OY, z + dz - OZ], 'facing': f, 'lines': ['', '', '', ''],
                      'support': vol.get(x, y, z)})
    return {'spawns': [[x - OX, y - OY, z - OZ, yaw] for x, y, z, yaw in spawns],
            'pages': pages,
            'batteries': [[int(x) - OX, int(y) - OY, int(z) - OZ] for x, y, z in bat],
            'lights': [[int(x) - OX, int(y) - OY, int(z) - OZ, CANDLE] for x, y, z in lig],
            'notes': notes}


def seal_clusters(res: dict, top: int = 25):
    """Grupos de celdas de derrumbe (huecos tapados): candidatos a salida/gate."""
    cells = set(res['seal'])
    seen, out = set(), []
    for c in res['seal']:
        if c in seen:
            continue
        comp, q = [], deque([c])
        seen.add(c)
        while q:
            x, y, z = q.popleft()
            comp.append((x, y, z))
            for dx, dy, dz in ((1, 0, 0), (-1, 0, 0), (0, 1, 0), (0, -1, 0), (0, 0, 1), (0, 0, -1)):
                n = (x + dx, y + dy, z + dz)
                if n in cells and n not in seen:
                    seen.add(n)
                    q.append(n)
        out.append(comp)
    out.sort(key=len)
    return out


def exit_zone(ctx: sections.Context, d: sections.SectionDef, gate: list, spawns: list, others=None, reach: int = 5):
    """Caja de salida justo detrás del gate (coordenadas schem): lo que se alcanza al abrirlo, fuera de la región."""
    res = ctx.check(d, spawns=spawns, keep=frozenset(gate), others=others)
    blocked = np.zeros_like(res['region'])
    for x, y, z in res['seal']:
        blocked[y, z, x] = True
    r = walk.flood(ctx.vol, ctx.st, [tuple(spawns[0][:3])], blocked)
    g = np.array(gate)
    gx, gy, gz = g.mean(0)
    ys, zs, xs = np.nonzero(r & ~res['region'])
    near = (np.abs(xs - gx) <= reach) & (np.abs(zs - gz) <= reach) & (np.abs(ys - gy) <= reach)
    if not near.any():
        return None
    xs, ys, zs = xs[near], ys[near], zs[near]
    return [int(xs.min()), int(ys.min()), int(zs.min()), int(xs.max()), int(ys.max()) + 2, int(zs.max())]


def find_board(ctx: sections.Context, reached: np.ndarray, start, radius: int = 25, strict: bool = True):
    """Pared al este de un pasillo donde cabe el tablón (6 de ancho en z, 4 de alto): (x, y, z) schem o None."""
    sx, sy, sz = start
    best = None
    for y in range(sy - 2, sy + 3):
        for x in range(sx - radius, sx + radius):
            for z in range(sz - radius, sz + radius):
                ok = True
                for dz in range(6):
                    if not reached[y + 1, z + dz, x - 1]:
                        ok = False
                        break
                    for dy in range(1, 5):
                        if not ctx.pas[y + dy, z + dz, x - 1]:
                            ok = False
                            break
                    for dy in range(1, 4):
                        if strict and ctx.pas[y + dy, z + dz, x]:
                            ok = False
                            break
                        if not strict and reached[y + dy, z + dz, x]:
                            ok = False
                            break
                    if not ok:
                        break
                if ok and not strict and not reached[y + 1, z:z + 6, x - 1].all():
                    ok = False
                if ok:
                    d = abs(x - sx) + abs(z - sz) + abs(y - sy)
                    if best is None or d < best[0]:
                        best = (d, (x, y, z))
    if best is None and strict:
        return find_board(ctx, reached, start, radius, strict=False)
    return best[1] if best else None
