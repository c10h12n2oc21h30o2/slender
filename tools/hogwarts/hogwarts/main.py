"""Pipeline completo: mundo 1.16.4 de Gabbel → recursos del plugin.
Uso: python -m hogwarts.main --world input/region --out ../../src/main/resources"""
import argparse, collections, json, os, random
from . import anvil, convert, clean, extras, registry, schem, signs

HERE = os.path.dirname(__file__)
DATA = os.path.join(HERE, '..', 'data')
BUILD = os.path.join(HERE, '..', 'build')
OFFSET = (383, 0, -326)  # original → coordenadas del schem


def build_volume(world: str):
    vol, tes, ents = anvil.load_world(world)
    convert.convert_volume(vol)
    bad = convert.invalid_states(vol.palette, registry.load(os.path.join(DATA, 'blocks_26.3.json')))
    if bad:
        raise SystemExit('Estados no válidos en 26.3: ' + ', '.join(bad[:20]))
    report = clean.strip(vol)
    lit = clean.dim_lights(vol, random.Random(1234))
    clean.contain_lake(vol)
    ex = extras.extract(tes, ents, OFFSET)
    signs.translate_all(ex['signs'], signs.load_table())
    return vol, ex, report, lit


def clean_copy(vol):
    from .volume import Volume
    return Volume(vol.data.copy(), vol.palette)


def main(argv=None):
    ap = argparse.ArgumentParser()
    ap.add_argument('--world', required=True)
    ap.add_argument('--out', required=True)
    a = ap.parse_args(argv)
    vol, ex, report, lit = build_volume(a.world)
    os.makedirs(os.path.join(a.out, 'maps', 'hogwarts'), exist_ok=True)
    hollowed = clean_copy(vol)
    print('terreno escondido vaciado:', clean.hollow(hollowed))
    schem.write(hollowed, os.path.join(a.out, 'maps', 'hogwarts.schem'))
    extras.write(ex, os.path.join(a.out, 'maps', 'hogwarts', 'extra.json.gz'))
    os.makedirs(BUILD, exist_ok=True)
    with open(os.path.join(BUILD, 'luces_encendidas.json'), 'w') as f:
        json.dump(lit, f)
    try:
        from . import content
        content.write_all(vol, a.out)
    except ImportError:
        pass
    h, l, w = vol.data.shape
    print(f'schem {w} x {h} x {l}; paleta {len(vol.palette)}')
    names = vol.names()
    counts = collections.Counter()
    for i, c in zip(*__import__('numpy').unique(vol.data, return_counts=True)):
        counts[names[i]] += int(c)
    print('top 15:', counts.most_common(15))
    print('quitado:', report)
    print('luces encendidas:', len(lit), '| carteles', len(ex['signs']), 'estandartes', len(ex['banners']),
          'cabezas', len(ex['heads']), 'cuadros', len(ex['paintings']))


if __name__ == '__main__':
    main()
