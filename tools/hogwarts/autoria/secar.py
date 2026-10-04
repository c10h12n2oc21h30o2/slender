"""Cambia de sitio las pilas, velas y notas que quedaron bajo el agua, y las pilas que comparten celda con una
vela (revisión final de la 1.8).

Solo toca las que están mojadas; las demás y los textos de las notas se quedan igual. Los sitios nuevos
se eligen por muestreo del punto más lejano respecto a los que ya hay, en celdas secas y alcanzables.
Uso: python3 autoria/secar.py   (luego python3 -m hogwarts.main para regenerar secciones.json)
"""
import json, os, sys
import numpy as np
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, '..'))
sys.path.insert(0, os.path.join(HERE, '..', 'tests'))
from conftest import REGION
from hogwarts import main, sections, content
from hogwarts.content import OX, OY, OZ, FACES, CANDLE, conv

vol, _, _, _ = main.build_volume(REGION)
ctx = sections.Context(vol)
content.compile_all(vol, ctx)
wet = content.wet_mask(vol)


def far(cands: np.ndarray, taken: list, avoid: set) -> np.ndarray:
    """La candidata más lejana (y pesa doble) de las ya cogidas."""
    pts = cands.astype(float) * [1, 2, 1]
    d = np.full(len(pts), 1e9)
    for t in taken:
        d = np.minimum(d, np.linalg.norm(pts - np.array(t, float) * [1, 2, 1], axis=1))
    for i, c in enumerate(cands):
        if tuple(int(v) for v in c) in avoid:
            d[i] = -1
    return cands[int(np.argmax(d))]


for d in sections.load_defs():
    path = os.path.join(HERE, '..', 'data', 'contenido', d.id + '.json')
    c = json.load(open(path, encoding='utf-8'))
    res = content.CHECKS[d.id]
    reached = res['reached_mask'] & res['region']
    feet = content.dry_feet(vol, ctx, reached)
    sealed = {tuple(conv(g))[:3] for g in c['gate']}
    changed = []

    def is_wet(x, y, z, head=False):
        return wet[y, z, x] or (head and wet[y + 1, z, x])

    bats = [tuple(conv(b))[:3] for b in c['batteries']]
    lights = [tuple(conv(l[:3]))[:3] for l in c['lights']]
    def bad_b(b):
        return is_wet(*b, head=True) or b in lights   # ni mojada ni en la celda de una vela

    keep_b = [b for b in bats if not bad_b(b)]
    for i, b in enumerate(bats):
        if bad_b(b):
            nb = tuple(int(v) for v in far(feet, keep_b, sealed | set(lights) | set(keep_b)))
            keep_b.append(nb)
            c['batteries'][i] = [nb[0] - OX, nb[1] - OY, nb[2] - OZ]
            changed.append(f'pila {b} -> {nb}')
    keep_l = [l for l in lights if not is_wet(*l)]
    for i, l in enumerate(lights):
        if is_wet(*l):
            nl = tuple(int(v) for v in far(feet, keep_l, sealed | set(keep_b) | set(keep_l)))
            keep_l.append(nl)
            c['lights'][i] = [nl[0] - OX, nl[1] - OY, nl[2] - OZ, CANDLE]
            changed.append(f'vela {l} -> {nl}')
    walls = [(x, y, z, f) for x, y, z, f in content.wall_candidates(vol, ctx, reached)
             if not wet[y, z + FACES[f][1], x + FACES[f][0]] and not wet[y - 1, z + FACES[f][1], x + FACES[f][0]]]
    fronts = np.array([[x + FACES[f][0], y, z + FACES[f][1]] for x, y, z, f in walls])
    notes = [tuple(conv(n['pos']))[:3] for n in c['notes']]
    keep_n = [n for n in notes if not is_wet(*n)] + [tuple(conv(p[:3]))[:3] for p in c['pages']]
    for i, n in enumerate(notes):
        if is_wet(*n):
            fx, fy, fz = (int(v) for v in far(fronts, keep_n, set(keep_n)))
            j = next(k for k, (x, y, z, f) in enumerate(walls) if (x + FACES[f][0], y, z + FACES[f][1]) == (fx, fy, fz))
            x, y, z, f = walls[j]
            keep_n.append((fx, fy, fz))
            c['notes'][i].update({'pos': [fx - OX, fy - OY, fz - OZ], 'facing': f, 'support': vol.get(x, y, z)})
            changed.append(f'nota {n} -> {(fx, fy, fz)} {f}')
    if changed:
        with open(path, 'w', encoding='utf-8') as fo:
            json.dump(c, fo, ensure_ascii=False, indent=1)
        print(d.id, *changed, sep='\n  ')


# ---- 2.ª pasada: velas que cortan el paso (una vela en un pasillo de 2 de alto no se puede pisar)
def open_floor(reached_feet: np.ndarray, pas: np.ndarray, cands: np.ndarray) -> np.ndarray:
    """Candidatas en suelo abierto: las 8 de alrededor también son pies alcanzables y hay 3 de aire encima."""
    ok = []
    for x, y, z in cands:
        if all(reached_feet[y, z + b, x + a] for a in (-1, 0, 1) for b in (-1, 0, 1)) and pas[y + 1, z, x] and pas[y + 2, z, x]:
            ok.append((x, y, z))
    return np.array(ok)


defs = {d.id: d for d in sections.load_defs()}
others = ctx.indoor_union(defs.values())
for d in sections.load_defs():
    path = os.path.join(HERE, '..', 'data', 'contenido', d.id + '.json')
    for _round in range(6):
        c = json.load(open(path, encoding='utf-8'))
        gate = frozenset(tuple(conv(g))[:3] for g in c['gate'])
        spawns = [conv(s) for s in c['spawns']]
        oth = others if d.rest else None
        base = ctx.check(d, spawns=spawns, keep=gate, others=oth)
        lights = [(*conv(l[:3])[:3], l[3]) for l in c['lights']]
        full = ctx.with_blocks(lights).check(d, spawns=spawns, keep=gate, others=oth)
        own = np.zeros_like(base['region'])          # la celda de la vela ya no es de pies: eso no cuenta
        for x, y, z, _ in lights:
            own[y, z, x] = True
        lost = base['reached_mask'] & base['region'] & ~full['reached_mask'] & ~own
        if not lost.any():
            break
        ly, lz, lx = np.nonzero(lost)
        lost_pts = np.c_[lx, ly, lz]
        near = [i for i, l in enumerate(lights) if np.abs(lost_pts - np.array(l[:3])).max(axis=1).min() <= 16]
        culprits = [i for i, l in enumerate(lights) if i in near
                    and (base['reached_mask'] & base['region'] & ~own & ~ctx.with_blocks([l]).check(
                        d, spawns=spawns, keep=gate, others=oth)['reached_mask']).any()]
        if not culprits:
            print(d.id, 'se pierden', int(lost.sum()), 'celdas sin una vela culpable sola; se deja')
            break
        reached = base['reached_mask'] & base['region']
        feet = open_floor(reached, ctx.pas, content.dry_feet(vol, ctx, reached))
        bats = {tuple(conv(b))[:3] for b in c['batteries']}
        keep_l = [l[:3] for i, l in enumerate(lights) if i not in culprits]
        for i in culprits:
            nl = tuple(int(v) for v in far(feet, keep_l, bats | set(keep_l) | set(gate) | {l[:3] for l in lights}))
            keep_l.append(nl)
            print(d.id, f'vela {lights[i][:3]} corta {int(lost.sum())} celdas -> {nl}')
            c['lights'][i] = [nl[0] - OX, nl[1] - OY, nl[2] - OZ, CANDLE]
        with open(path, 'w', encoding='utf-8') as fo:
            json.dump(c, fo, ensure_ascii=False, indent=1)
