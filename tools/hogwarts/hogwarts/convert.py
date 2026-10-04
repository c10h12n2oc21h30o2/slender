"""Conversión de estados de bloque 1.16.4 → 26.3."""
import json, os
from .volume import Volume

_RENAMES_PATH = os.path.join(os.path.dirname(__file__), '..', 'data', 'renames.json')
with open(_RENAMES_PATH) as _f:
    RENAMES: dict[str, str] = json.load(_f)


def parse(state: str) -> tuple[str, dict[str, str]]:
    if '[' not in state:
        return state, {}
    name, rest = state[:-1].split('[', 1)
    props = dict(p.split('=', 1) for p in rest.split(',') if p)
    return name, props


def fmt(name: str, props: dict[str, str]) -> str:
    if not props:
        return name
    return name + '[' + ','.join(f'{k}={props[k]}' for k in sorted(props)) + ']'


def convert_state(state: str) -> str:
    name, props = parse(state)
    name = RENAMES.get(name, name)
    if name == 'minecraft:cauldron' and 'level' in props:
        level = props.pop('level')
        if level != '0':
            name, props = 'minecraft:water_cauldron', {'level': level}
    if name == 'minecraft:lectern' and props.get('has_book') == 'true':
        props['has_book'] = 'false'
    return fmt(name, props)


def convert_volume(vol: Volume) -> None:
    vol.replace_palette([convert_state(s) for s in vol.palette])


def invalid_states(palette: list[str], reg: dict[str, dict[str, list[str]]]) -> list[str]:
    bad = []
    for s in palette:
        name, props = parse(s)
        allowed = reg.get(name)
        if allowed is None or any(k not in allowed or v not in allowed[k] for k, v in props.items()):
            bad.append(s)
    return bad
