"""Por dónde se puede andar: celdas sin colisión, celdas donde se está de pie y recorrido (flood fill)."""
from collections import deque
import numpy as np
from .volume import Volume

_NONCOL = set('air cave_air void_air water lava torch wall_torch soul_torch soul_wall_torch redstone_torch '
              'redstone_wall_torch redstone_wire tripwire tripwire_hook lever ladder vine fire soul_fire cobweb '
              'short_grass grass tall_grass fern large_fern dead_bush seagrass tall_seagrass kelp kelp_plant '
              'sugar_cane sweet_berry_bush rail powered_rail detector_rail activator_rail snow nether_portal '
              'end_portal light lily_pad twisting_vines twisting_vines_plant weeping_vines weeping_vines_plant '
              'crimson_roots warped_roots nether_sprouts wheat carrots potatoes beetroots melon_stem pumpkin_stem '
              'attached_melon_stem attached_pumpkin_stem nether_wart bamboo_sapling scaffolding poppy dandelion '
              'blue_orchid allium azure_bluet oxeye_daisy cornflower lily_of_the_valley wither_rose sunflower lilac '
              'rose_bush peony brown_mushroom red_mushroom crimson_fungus warped_fungus structure_void'.split())
_NONCOL_SUFFIX = ('_sign', '_banner', '_button', '_pressure_plate', '_carpet', '_sapling', '_coral', '_coral_fan',
                  '_tulip', '_wall_fan')
_CLIMB = {'ladder', 'vine', 'scaffolding', 'twisting_vines', 'twisting_vines_plant', 'weeping_vines',
          'weeping_vines_plant', 'water'}
_TREE = ('_leaves', '_log', '_wood')


def _base(state: str) -> str:
    return state.split('[')[0].removeprefix('minecraft:')


def is_passable(state: str) -> bool:
    n = _base(state)
    if n in _NONCOL or n.endswith(_NONCOL_SUFFIX):
        return True
    if n.endswith('_door') and n != 'iron_door':
        return True
    if n.endswith('_fence_gate') or (n.endswith('_trapdoor') and n != 'iron_trapdoor'):
        return True
    return False


def passable(vol: Volume) -> np.ndarray:
    return vol.mask(is_passable)


def climbable(vol: Volume) -> np.ndarray:
    return vol.mask(lambda s: _base(s) in _CLIMB)


def _half(state: str) -> bool:
    n = _base(state)
    return (n.endswith('_slab') and 'type=bottom' in state) or n.endswith('_stairs')


def standable(vol: Volume, pas: np.ndarray) -> np.ndarray:
    h = pas.shape[0]
    st = np.zeros_like(pas)
    st[1:h - 1] = pas[1:h - 1] & pas[2:h] & ~pas[0:h - 2]
    half = vol.mask(_half)
    st[0:h - 2] |= half[0:h - 2] & pas[1:h - 1] & pas[2:h]
    climb = climbable(vol)
    st[0:h - 1] |= climb[0:h - 1] & pas[1:h]
    return st


def roofed(vol: Volume) -> np.ndarray:
    """True si encima de la celda hay algún bloque macizo que no sea de árbol (techo, forjado o roca)."""
    pas = passable(vol)
    tree = vol.mask(lambda s: _base(s).endswith(_TREE))
    roof_block = ~pas & ~tree
    out = np.zeros_like(roof_block)
    acc = np.zeros(roof_block.shape[1:], bool)
    for y in range(roof_block.shape[0] - 1, -1, -1):
        out[y] = acc
        acc |= roof_block[y]
    return out


def flood(vol: Volume, st: np.ndarray, starts, blocked=None) -> np.ndarray:
    """Celdas (pies) a las que se llega andando desde `starts` [(x, y, z)]. `blocked` cuenta como bloque macizo."""
    pas = passable(vol)
    if blocked is not None:
        pas = pas & ~blocked
        st = standable(vol, pas) & ~blocked
    climb = climbable(vol) & pas
    H, L, W = st.shape
    seen = np.zeros_like(st)
    q = deque()
    for x, y, z in starts:
        if st[y, z, x] and not seen[y, z, x]:
            seen[y, z, x] = True
            q.append((y, z, x))

    def push(y, z, x):
        if not seen[y, z, x]:
            seen[y, z, x] = True
            q.append((y, z, x))

    while q:
        y, z, x = q.popleft()
        for dz, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            nz, nx = z + dz, x + dx
            if not (0 <= nz < L and 0 <= nx < W):
                continue
            if st[y, nz, nx]:
                push(y, nz, nx)
                continue
            if y + 2 < H and st[y + 1, nz, nx] and pas[y + 2, z, x]:
                push(y + 1, nz, nx)
                continue
            if y >= 1 and st[y - 1, nz, nx]:
                push(y - 1, nz, nx)
                continue
            if y + 1 < H and pas[y, nz, nx] and pas[y + 1, nz, nx]:
                yy = y - 1
                while yy >= 0 and pas[yy, nz, nx] and not st[yy, nz, nx]:
                    yy -= 1
                if yy >= 0 and st[yy, nz, nx]:
                    push(yy, nz, nx)
        if climb[y, z, x]:
            if y + 1 < H and st[y + 1, z, x]:
                push(y + 1, z, x)
            if y >= 1 and st[y - 1, z, x]:
                push(y - 1, z, x)
    return seen
