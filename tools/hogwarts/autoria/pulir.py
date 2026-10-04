"""Pulido 1.8.1: cambia de sitio (al más cercano que valga) las páginas con algo delante que no es aire o colgadas
de un bloque que no es entero (maceta, huevo…), y los spawns que caen en una puerta. La etiqueta se queda.
Uso: python3 autoria/pulir.py   (luego python3 -m hogwarts.main … para regenerar secciones.json)
"""
import json, os, sys
import numpy as np
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, os.path.join(HERE, '..'))
sys.path.insert(0, os.path.join(HERE, '..', 'tests'))
from conftest import REGION
from hogwarts import main, sections, content
from hogwarts.content import OX, OY, OZ, FACES, NON_FULL, conv

vol, _, _, _ = main.build_volume(REGION)
ctx = sections.Context(vol)
content.compile_all(vol, ctx)
air = content.air_mask(vol)


def full(st):
    return not any(k in st for k in NON_FULL)


for d in sections.load_defs():
    path = os.path.join(HERE, '..', 'data', 'contenido', d.id + '.json')
    c = json.load(open(path, encoding='utf-8'))
    res = content.CHECKS[d.id]
    reached = res['reached_mask'] & res['region']
    changed = []
    walls = content.wall_candidates(vol, ctx, reached)
    wpts = np.array([[x, y * 2, z] for x, y, z, _ in walls], float)
    pages = [tuple(conv(p[:3]))[:3] for p in c['pages']]
    for i, p in enumerate(c['pages']):
        x, y, z = pages[i]
        dx, dz = FACES[p[3]]
        if air[y, z + dz, x + dx] and full(vol.get(x, y, z)) and not ctx.pas[y, z, x]:
            continue
        others = np.array([q for j, q in enumerate(pages) if j != i], float) * [1, 2, 1]
        dist = np.linalg.norm(wpts - np.array([x, y * 2, z], float), axis=1)
        far_from_others = np.linalg.norm(wpts[:, None, :] - others[None, :, :], axis=2).min(axis=1) >= 4
        dist[~far_from_others] = 1e9
        k = int(np.argmin(dist))
        nx, ny, nz, nf = walls[k]
        pages[i] = (nx, ny, nz)
        c['pages'][i] = [nx - OX, ny - OY, nz - OZ, nf, p[4]]
        changed.append(f'página {p[4]!r} {(x, y, z)} -> {(nx, ny, nz)} {nf}')
    feet = content.dry_feet(vol, ctx, reached)
    feet = np.array([f for f in feet if air[f[1] + 1, f[2], f[0]]])
    spawns = [tuple(conv(s))[:3] for s in c['spawns']]
    for i, s in enumerate(c['spawns']):
        x, y, z = spawns[i]
        if air[y, z, x] and air[y + 1, z, x]:
            continue
        ok = np.abs(feet[:, 1] - y) <= 1
        for j, q in enumerate(spawns):
            if j != i:
                ok &= np.abs(feet[:, 0] - q[0]) + np.abs(feet[:, 2] - q[2]) >= 2
        dist = np.abs(feet - [x, y, z]).sum(axis=1).astype(float)
        dist[~ok] = 1e9
        nx, ny, nz = (int(v) for v in feet[int(np.argmin(dist))])
        spawns[i] = (nx, ny, nz)
        c['spawns'][i] = [nx - OX, ny - OY, nz - OZ, s[3]]
        changed.append(f'spawn {(x, y, z)} -> {(nx, ny, nz)}')
    if changed:
        with open(path, 'w', encoding='utf-8') as fo:
            json.dump(c, fo, ensure_ascii=False, indent=1)
        print(d.id, *changed, sep='\n  ')
