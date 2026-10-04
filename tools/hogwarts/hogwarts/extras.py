"""Datos que no son bloques: texto de carteles, dibujos de estandartes, texturas de cabezas y cuadros."""
import gzip, json

DYES = ['white', 'orange', 'magenta', 'light_blue', 'yellow', 'lime', 'pink', 'gray', 'light_gray', 'cyan',
        'purple', 'blue', 'brown', 'green', 'red', 'black']
PATTERNS = {
    'b': 'base', 'bs': 'stripe_bottom', 'ts': 'stripe_top', 'ls': 'stripe_left', 'rs': 'stripe_right',
    'cs': 'stripe_center', 'ms': 'stripe_middle', 'drs': 'stripe_downright', 'dls': 'stripe_downleft',
    'ss': 'small_stripes', 'cr': 'cross', 'sc': 'straight_cross', 'ld': 'diagonal_left', 'rd': 'diagonal_right',
    'lud': 'diagonal_up_left', 'rud': 'diagonal_up_right', 'bl': 'square_bottom_left', 'br': 'square_bottom_right',
    'tl': 'square_top_left', 'tr': 'square_top_right', 'bt': 'triangle_bottom', 'tt': 'triangle_top',
    'bts': 'triangles_bottom', 'tts': 'triangles_top', 'mc': 'circle', 'mr': 'rhombus', 'bo': 'border',
    'cbo': 'curly_border', 'bri': 'bricks', 'gra': 'gradient', 'gru': 'gradient_up', 'cre': 'creeper',
    'sku': 'skull', 'flo': 'flower', 'moj': 'mojang', 'glb': 'globe', 'pig': 'piglin'}
FACING = {0: 'SOUTH', 1: 'WEST', 2: 'NORTH', 3: 'EAST'}


def _flatten(j) -> str:
    if isinstance(j, str):
        return j
    if isinstance(j, list):
        return ''.join(_flatten(a) for a in j)
    if isinstance(j, dict):
        return str(j.get('text', '')) + ''.join(_flatten(e) for e in j.get('extra', []))
    return ''


def sign_text(raw: str) -> str:
    try:
        return _flatten(json.loads(raw))
    except (ValueError, TypeError):
        return raw


def extract(tes: dict, ents: list, offset: tuple[int, int, int]) -> dict:
    dx, dy, dz = offset
    out = {'signs': [], 'banners': [], 'heads': [], 'paintings': []}
    for (x, y, z), t in sorted(tes.items()):
        pos = [x + dx, y + dy, z + dz]
        tid = str(t['id'])
        if tid == 'minecraft:sign':
            lines = [sign_text(str(t.get(f'Text{i}', ''))) for i in range(1, 5)]
            out['signs'].append({'pos': pos, 'lines': lines, 'color': str(t.get('Color', 'black')),
                                 'glow': bool(int(t.get('GlowingText', 0)))})
        elif tid == 'minecraft:banner':
            pats = [['minecraft:' + PATTERNS[str(p['Pattern'])], DYES[int(p['Color'])]]
                    for p in t.get('Patterns', []) if str(p['Pattern']) in PATTERNS]
            out['banners'].append({'pos': pos, 'patterns': pats})
        elif tid == 'minecraft:skull':
            try:
                tex = str(t['SkullOwner']['Properties']['textures'][0]['Value'])
            except (KeyError, IndexError, TypeError):
                continue
            out['heads'].append({'pos': pos, 'texture': tex})
    for e in ents:
        if str(e.get('id')) != 'minecraft:painting':
            continue
        pos = [int(e['TileX']) + dx, int(e['TileY']) + dy, int(e['TileZ']) + dz]
        out['paintings'].append({'pos': pos, 'facing': FACING[int(e['Facing'])], 'variant': str(e['Motive'])})
    return out


def write(data: dict, path: str) -> None:
    with gzip.open(path, 'wt', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, separators=(',', ':'))


def read(path: str) -> dict:
    with gzip.open(path, 'rt', encoding='utf-8') as f:
        return json.load(f)
