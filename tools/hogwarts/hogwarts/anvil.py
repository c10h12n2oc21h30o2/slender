"""Lectura de regiones Anvil de Minecraft 1.16 (paleta + BlockStates sin cruzar longs)."""
import glob, gzip, io, math, os, struct, zlib
from typing import Iterator
import numpy as np
import nbtlib
from .volume import Volume

BOX = (-383, -1, 326, 738, 0, 254)  # x1, x2, z1, z2, y1, y2 (inclusivos, coordenadas originales)


def iter_chunks(path: str) -> Iterator[nbtlib.Compound]:
    with open(path, 'rb') as f:
        data = f.read()
    if len(data) < 8192:
        return
    for i in range(1024):
        off = int.from_bytes(data[i * 4:i * 4 + 3], 'big')
        if off == 0:
            continue
        p = off * 4096
        length = struct.unpack('>I', data[p:p + 4])[0]
        comp = data[p + 4]
        raw = data[p + 5:p + 4 + length]
        if comp == 2:
            raw = zlib.decompress(raw)
        elif comp == 1:
            raw = gzip.decompress(raw)
        else:
            continue
        yield nbtlib.File.parse(io.BytesIO(raw))


def state_str(c) -> str:
    name = str(c['Name'])
    props = c.get('Properties')
    if props:
        return name + '[' + ','.join(f'{k}={props[k]}' for k in sorted(props.keys())) + ']'
    return name


def decode_section(sec) -> tuple[list[str], np.ndarray]:
    pal = [state_str(c) for c in sec['Palette']]
    bits = max(4, math.ceil(math.log2(len(pal)))) if len(pal) > 1 else 4
    per = 64 // bits
    longs = np.array(sec['BlockStates'], dtype=np.int64).view(np.uint64)
    idx = np.arange(4096)
    vals = (longs[idx // per] >> ((idx % per) * bits).astype(np.uint64)) & np.uint64((1 << bits) - 1)
    return pal, vals.astype(np.int32).reshape(16, 16, 16)


def load_world(region_dir: str, box=BOX):
    """Devuelve (volumen recortado, block entities por posición original, entidades dentro del recorte)."""
    x1, x2, z1, z2, y1, y2 = box
    w, l, h = x2 - x1 + 1, z2 - z1 + 1, y2 - y1 + 1
    vol = Volume.empty(w, h, l)
    tes, ents = {}, []
    for path in sorted(glob.glob(os.path.join(region_dir, 'r.*.mca'))):
        _, rx, rz, _ = os.path.basename(path).split('.')
        rx, rz = int(rx), int(rz)
        if rx * 512 > x2 or rx * 512 + 511 < x1 or rz * 512 > z2 or rz * 512 + 511 < z1:
            continue
        for nbt in iter_chunks(path):
            lv = nbt['Level']
            cx, cz = int(lv['xPos']) * 16, int(lv['zPos']) * 16
            if cx > x2 or cx + 15 < x1 or cz > z2 or cz + 15 < z1:
                continue
            xa, xb = max(cx, x1), min(cx + 15, x2)
            za, zb = max(cz, z1), min(cz + 15, z2)
            for sec in lv.get('Sections', []):
                if 'Palette' not in sec or 'BlockStates' not in sec:
                    continue
                sy = int(sec['Y']) * 16
                if sy + 15 < y1 or sy > y2:
                    continue
                pal, vals = decode_section(sec)
                lut = np.array([vol.idx(s) for s in pal], np.uint16)
                ya, yb = max(sy, y1), min(sy + 15, y2)
                vol.data[ya - y1:yb - y1 + 1, za - z1:zb - z1 + 1, xa - x1:xb - x1 + 1] = \
                    lut[vals[ya - sy:yb - sy + 1, za - cz:zb - cz + 1, xa - cx:xb - cx + 1]]
            for t in lv.get('TileEntities', []):
                x, y, z = int(t['x']), int(t['y']), int(t['z'])
                if x1 <= x <= x2 and z1 <= z <= z2 and y1 <= y <= y2:
                    tes[(x, y, z)] = t
            for e in lv.get('Entities', []):
                pos = e.get('Pos')
                if pos is not None and x1 <= float(pos[0]) < x2 + 1 and z1 <= float(pos[2]) < z2 + 1:
                    ents.append(e)
    return vol, tes, ents
