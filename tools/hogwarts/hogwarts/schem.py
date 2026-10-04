"""Sponge Schematic v3 (gzip), el mismo formato que lee SchematicReader del plugin."""
import nbtlib
import numpy as np
from nbtlib import Compound, Int, Short, IntArray, ByteArray, List
from .volume import Volume


def _varints(values: np.ndarray) -> np.ndarray:
    v = values.astype(np.int64).reshape(-1)
    if v.max(initial=0) >= 1 << 14:
        raise ValueError('paleta demasiado grande para varint de 2 bytes')
    two = v >= 128
    out = np.empty(len(v) + int(two.sum()), np.uint8)
    pos = np.arange(len(v)) + np.concatenate(([0], np.cumsum(two)[:-1]))
    out[pos] = np.where(two, (v & 0x7F) | 0x80, v)
    out[pos[two] + 1] = (v[two] >> 7)
    return out


def _unvarints(b: np.ndarray, n: int) -> np.ndarray:
    b = b.astype(np.int64)
    cont = (b & 0x80) != 0
    start = np.ones(len(b), bool)
    start[1:] = ~cont[:-1]
    idx = np.nonzero(start)[0]
    if len(idx) != n:
        raise ValueError(f'esperaba {n} valores y hay {len(idx)}')
    val = b[idx] & 0x7F
    c = cont[idx]
    if np.any(cont[idx[c] + 1]):
        raise ValueError('varint de más de 2 bytes')
    val[c] |= (b[idx[c] + 1] & 0x7F) << 7
    return val


def write(vol: Volume, path: str, data_version: int = 5023) -> None:
    h, l, w = vol.data.shape
    used = np.unique(vol.data)
    remap = np.zeros(len(vol.palette), np.int64)
    remap[used] = np.arange(len(used))
    palette = Compound({vol.palette[i]: Int(j) for j, i in enumerate(used)})
    data = _varints(remap[vol.data])
    schem = Compound({
        'Version': Int(3), 'DataVersion': Int(data_version),
        'Width': Short(w), 'Height': Short(h), 'Length': Short(l),
        'Offset': IntArray([0, 0, 0]),
        'Blocks': Compound({'Palette': palette, 'Data': ByteArray(data.view(np.int8)),
                            'BlockEntities': List[Compound]([])}),
    })
    nbtlib.File({'Schematic': schem}).save(path, gzipped=True)


def read(path: str):
    s = nbtlib.load(path)['Schematic']
    w, h, l = int(s['Width']) & 0xFFFF, int(s['Height']) & 0xFFFF, int(s['Length']) & 0xFFFF
    pal = [None] * len(s['Blocks']['Palette'])
    for k, v in s['Blocks']['Palette'].items():
        pal[int(v)] = str(k)
    raw = np.array(s['Blocks']['Data'], dtype=np.int8).view(np.uint8)
    data = _unvarints(raw, w * h * l).reshape(h, l, w)
    return (w, h, l), pal, data
