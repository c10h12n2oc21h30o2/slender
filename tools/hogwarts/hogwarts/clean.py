"""Limpieza del mapa: comandos y mecanismos fuera, luces atenuadas y lago contenido."""
import collections, json, os, random
import numpy as np
from .volume import Volume, AIR
from .convert import parse, fmt

STRIP = {f'minecraft:{n}' for n in (
    'command_block', 'chain_command_block', 'repeating_command_block', 'spawner', 'end_gateway',
    'structure_block', 'redstone_wire', 'redstone_torch', 'redstone_wall_torch', 'repeater', 'comparator', 'observer')}
FIRE_BASES = {'minecraft:netherrack', 'minecraft:soul_sand', 'minecraft:soul_soil'}
_LIGHTS_PATH = os.path.join(os.path.dirname(__file__), '..', 'data', 'luces.json')
with open(_LIGHTS_PATH) as _f:
    LIGHTS: dict = json.load(_f)
BARRIER = 'minecraft:barrier'


def strip(vol: Volume) -> dict[str, int]:
    names = vol.names()
    report = collections.Counter()
    air = vol.idx(AIR)
    for i, n in enumerate(names):
        if n in STRIP:
            m = vol.data == i
            report[n] += int(m.sum())
            vol.data[m] = air
    fire = [i for i, n in enumerate(vol.names()) if n in ('minecraft:fire',)]
    base_ok = np.array([n in FIRE_BASES for n in vol.names()])
    for i in fire:
        ys, zs, xs = np.nonzero(vol.data == i)
        for y, z, x in zip(ys, zs, xs):
            if y == 0 or not base_ok[vol.data[y - 1, z, x]]:
                vol.data[y, z, x] = air
                report['minecraft:fire'] += 1
    return dict(report)


def _dimmed(state: str) -> str:
    name, props = parse(state)
    rule = LIGHTS[name]
    keep = rule.get('keep', [])
    out = dict(props) if keep == 'all' else {k: v for k, v in props.items() if k in keep}
    out.update(rule.get('set', {}))
    return fmt(rule['to'], out)


def dim_lights(vol: Volume, rng: random.Random, keep: float = 0.10) -> list[tuple[int, int, int]]:
    """Deja encendido el `keep` de las fuentes de luz (al azar con `rng`) y apaga el resto.
    Devuelve las posiciones (x, y, z) que siguen encendidas."""
    names = vol.names()
    light_idx = [i for i, n in enumerate(names) if n in LIGHTS and _dimmed(vol.palette[i]) != vol.palette[i]]
    if not light_idx:
        return []
    mask = np.isin(vol.data, light_idx)
    ys, zs, xs = np.nonzero(mask)  # orden y, z, x: determinista
    n = len(ys)
    chosen = set(rng.sample(range(n), round(keep * n)))
    kept = []
    repl = {i: vol.idx(_dimmed(vol.palette[i])) for i in light_idx}
    for k in range(n):
        y, z, x = int(ys[k]), int(zs[k]), int(xs[k])
        if k in chosen:
            kept.append((x, y, z))
        else:
            vol.data[y, z, x] = repl[int(vol.data[y, z, x])]
    return kept


def contain_lake(vol: Volume, top: int = 30) -> None:
    b = vol.idx(BARRIER)
    vol.data[0:top + 1, :, 0] = b
    vol.data[0:top + 1, :, -1] = b
    vol.data[0:top + 1, 0, :] = b
    vol.data[0:top + 1, -1, :] = b


TERRAIN = {f'minecraft:{n}' for n in (
    'stone', 'andesite', 'diorite', 'granite', 'dirt', 'bedrock', 'gravel', 'sand', 'coarse_dirt', 'podzol',
    'grass_block', 'clay', 'coal_ore', 'iron_ore', 'gold_ore', 'lapis_ore', 'redstone_ore', 'diamond_ore',
    'emerald_ore')}


def hollow(vol: Volume) -> int:
    """Vacía el terreno que no se ve (las 6 caras tocan terreno). Solo para el schem final: acelera el pegado."""
    solid = vol.mask(lambda s: s.split('[')[0] in TERRAIN)
    inner = solid.copy()
    inner[0] = inner[-1] = False
    inner[:, 0] = inner[:, -1] = False
    inner[:, :, 0] = inner[:, :, -1] = False
    for ax in range(3):
        for s in (1, -1):
            inner &= np.roll(solid, s, axis=ax)
    vol.data[inner] = vol.idx(AIR)
    return int(inner.sum())
