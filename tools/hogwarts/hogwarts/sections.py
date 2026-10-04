"""Secciones del castillo: región de cada una, derrumbes que la cierran y comprobación con flood fill."""
import json, os, random
from dataclasses import dataclass, field
import numpy as np
from matplotlib.path import Path
from . import walk
from .volume import Volume

DEFS = os.path.join(os.path.dirname(__file__), '..', 'data', 'secciones_def.json')
OFFSET = (383, 0, -326)  # coordenadas originales → schem
RUBBLE = ['minecraft:cobblestone', 'minecraft:mossy_cobblestone', 'minecraft:cracked_stone_bricks',
          'minecraft:stone_bricks']


@dataclass
class SectionDef:
    id: str
    name: str
    order: int
    boxes: list  # [{"x":[a,b],"y":[a,b],"z":[a,b]} | {"poly":[[x,z],..],"y":[a,b]}, opcional "force"/"exclude"]
    outdoor: bool
    icon: str = 'FILLED_MAP'
    start: list = field(default_factory=list)  # celda de arranque para comprobar (schem) si aún no hay spawns
    rest: bool = False  # True: la región es la caja menos las otras secciones (sin mirar techos)


def _conv_box(b: dict) -> dict:
    dx, _, dz = OFFSET
    o = dict(b)
    if 'x' in b:
        o['x'] = [b['x'][0] + dx, b['x'][1] + dx]
        o['z'] = [b['z'][0] + dz, b['z'][1] + dz]
    if 'poly' in b:
        o['poly'] = [[p[0] + dx, p[1] + dz] for p in b['poly']]
    return o


def load_defs(path: str = DEFS) -> list[SectionDef]:
    """El fichero va en coordenadas ORIGINALES (las del mundo de Gabbel); se pasan aquí a coordenadas del schem."""
    with open(path, encoding='utf-8') as f:
        raw = json.load(f)
    out = []
    for s in raw['sections']:
        start = []
        if s.get('start'):
            x, y, z = s['start']
            start = [x + OFFSET[0], y, z + OFFSET[2]]
        out.append(SectionDef(s['id'], s['name'], s['order'], [_conv_box(b) for b in s['boxes']], s['outdoor'],
                              s.get('icon', 'FILLED_MAP'), start, s.get('rest', False)))
    return out


def _box_mask(b: dict, shape) -> np.ndarray:
    H, L, W = shape
    m = np.zeros(shape, bool)
    y1, y2 = max(0, b['y'][0]), min(H - 1, b['y'][1])
    if 'poly' in b:
        path = Path(b['poly'])
        zz, xx = np.mgrid[0:L, 0:W]
        inside = path.contains_points(np.c_[xx.ravel() + 0.5, zz.ravel() + 0.5]).reshape(L, W)
        m[y1:y2 + 1] = inside
    else:
        x1, x2 = max(0, b['x'][0]), min(W - 1, b['x'][1])
        z1, z2 = max(0, b['z'][0]), min(L - 1, b['z'][1])
        m[y1:y2 + 1, z1:z2 + 1, x1:x2 + 1] = True
    return m


def _snap(cut: int, solid: np.ndarray, window: int = 6) -> np.ndarray:
    """Por columna, el bloque macizo más cercano al corte `cut` (±window); -1 si no hay ninguno."""
    H = solid.shape[0]
    best = np.full(solid.shape[1:], -1, np.int32)
    bestd = np.full(solid.shape[1:], 10 ** 6, np.int32)
    for y in range(max(0, cut - window), min(H, cut + window + 1)):
        d = abs(y - cut)
        upd = solid[y] & (d < bestd)
        best[upd] = y
        bestd[upd] = d
    return best


def _snapped_box(b: dict, shape, solid: np.ndarray) -> np.ndarray:
    m = _box_mask({**b, 'y': [0, shape[0] - 1]}, shape)
    ys = np.arange(shape[0])[:, None, None]
    y1, y2 = b['y']
    lo = _snap(y1, solid)
    keep = np.where(lo[None] >= 0, ys > lo[None], ys >= y1)
    hi = _snap(y2 + 1, solid)
    keep &= np.where(hi[None] >= 0, ys <= hi[None], ys <= y2)
    return m & keep


def region(d: SectionDef, roof: np.ndarray, pas: np.ndarray = None, others: np.ndarray = None) -> np.ndarray:
    shape = roof.shape
    base = np.zeros(shape, bool)
    forced = np.zeros(shape, bool)
    excl = np.zeros(shape, bool)
    for b in d.boxes:
        if b.get('snap'):
            if pas is None:
                raise ValueError('una caja con snap necesita pas')
            m = _snapped_box(b, shape, ~pas)
        else:
            m = _box_mask(b, shape)
        if b.get('exclude'):
            excl |= m
        elif b.get('force'):
            forced |= m
        else:
            base |= m
    if d.rest:
        crit = ~others if others is not None else np.ones_like(base)
        forced &= crit
    else:
        crit = ~roof if d.outdoor else roof
    return ((base & crit) | forced) & ~excl


def section_mask(d: SectionDef, st: np.ndarray, roof: np.ndarray, pas: np.ndarray = None) -> np.ndarray:
    return st & region(d, roof, pas)


def _neighbors6(a: np.ndarray) -> np.ndarray:
    adj = np.zeros_like(a)
    adj[1:] |= a[:-1]; adj[:-1] |= a[1:]
    adj[:, 1:] |= a[:, :-1]; adj[:, :-1] |= a[:, 1:]
    adj[:, :, 1:] |= a[:, :, :-1]; adj[:, :, :-1] |= a[:, :, 1:]
    return adj


def seal_cells(reg: np.ndarray, st: np.ndarray, pas: np.ndarray, keep: set) -> list[tuple[int, int, int]]:
    """Celdas por las que se saldría de la región, tapadas con 2 de alto:
    - pasables o pisables (losas/escaleras) fuera de la región que tocan el cuerpo de un jugador dentro;
    - la celda bajo el aire de la región (pozos);
    y se repite con los derrumbes como suelo nuevo hasta que no salga ninguna más."""
    keep_m = np.zeros_like(reg)
    for x, y, z in keep:
        keep_m[y, z, x] = True
    occupiable = pas | st
    blocked = np.zeros_like(reg)
    below = np.zeros_like(reg)
    below[:-1] = (reg & pas)[1:]
    for _ in range(50):
        pas_e = pas & ~blocked
        on_rubble = np.zeros_like(reg)
        on_rubble[1:] = blocked[:-1]
        above_free = np.zeros_like(reg)
        above_free[:-1] = pas_e[1:]
        st_e = (st & ~blocked) | (pas_e & on_rubble & above_free)
        feet = st_e & reg
        body = feet.copy()
        body[1:] |= feet[:-1]
        cand = _neighbors6(body) & occupiable & ~reg
        cand |= below & pas & ~reg
        up = np.zeros_like(cand)
        up[1:] = cand[:-1]
        cand |= up & occupiable & ~reg
        new = cand & ~blocked & ~keep_m
        if not new.any():
            break
        blocked |= new
    ys, zs, xs = np.nonzero(blocked)
    return sorted((int(x), int(y), int(z)) for y, z, x in zip(ys, zs, xs))


def rubble(cells, rng: random.Random) -> list[tuple[int, int, int, str]]:
    """Derrumbe de bloques enteros. Sin telarañas: no tienen colisión y un sello de 1 de alto se cruzaría."""
    return [(x, y, z, rng.choice(RUBBLE)) for x, y, z in cells]


class Context:
    """Arrays compartidos para analizar varias secciones sobre el mismo volumen."""

    def __init__(self, vol: Volume):
        self.vol = vol
        self.pas = walk.passable(vol)
        self.st = walk.standable(vol, self.pas)
        self.roof = walk.roofed(vol)

    def with_blocks(self, blocks) -> 'Context':
        """Contexto de una copia del volumen con bloques puestos (x, y, z, estado): p. ej. las velas de una sección,
        que dan suelo nuevo y tienen que entrar en el cálculo de derrumbes. El techo (roof) se comparte."""
        if not blocks:
            return self
        c = Context.__new__(Context)
        c.vol = Volume(self.vol.data.copy(), self.vol.palette)
        for x, y, z, st in blocks:
            c.vol.set(x, y, z, st)
        c.pas = walk.passable(c.vol)
        c.st = walk.standable(c.vol, c.pas)
        c.roof = self.roof
        return c

    def start_cell(self, d: SectionDef, mask: np.ndarray, spawns=None):
        if spawns:
            return [tuple(s[:3]) for s in spawns]
        ys, zs, xs = np.nonzero(mask)
        if d.start:
            tx, ty, tz = d.start
            i = int(np.argmin((xs - tx) ** 2 + (zs - tz) ** 2 + 4 * (ys - ty) ** 2))
        else:
            i = int(np.argmin((xs - xs.mean()) ** 2 + (zs - zs.mean()) ** 2))
        return [(int(xs[i]), int(ys[i]), int(zs[i]))]

    def indoor_union(self, defs) -> np.ndarray:
        u = np.zeros_like(self.roof)
        for o in defs:
            if not o.rest:
                u |= region(o, self.roof, self.pas)
        return u

    def check(self, d: SectionDef, spawns=None, keep=frozenset(), others=None):
        reg = region(d, self.roof, self.pas, others)
        mask = self.st & reg
        seal = seal_cells(reg, self.st, self.pas, set(keep))
        blocked = np.zeros_like(mask)
        for x, y, z in seal:
            blocked[y, z, x] = True
        for x, y, z in keep:
            blocked[y, z, x] = True
        starts = self.start_cell(d, mask, spawns)
        reached = walk.flood(self.vol, self.st, starts, blocked)
        return {'region': reg, 'mask': mask, 'seal': seal, 'reached_mask': reached,
                'reached': int(reached.sum()), 'cells': int(mask.sum()), 'leak': int((reached & ~reg).sum()),
                'starts': starts}
