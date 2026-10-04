from nbtlib import Compound, List, String, Int, Byte, Double
from conftest import needs_world, REGION
from hogwarts import extras

OFF = (383, 0, -326)

def te(id, x, y, z, **kw):
    c = Compound({'id': String(id), 'x': Int(x), 'y': Int(y), 'z': Int(z)})
    c.update(kw)
    return c

def test_sign_flatten():
    t = te('minecraft:sign', -200, 90, 500,
           Text1=String('{"text":"","extra":[{"text":"Great "},{"text":"Hall"}]}'),
           Text2=String('{"text":""}'), Text3=String('{"text":""}'), Text4=String('{"text":""}'),
           Color=String('black'))
    out = extras.extract({(-200, 90, 500): t}, [], OFF)
    assert out['signs'][0]['lines'][0] == 'Great Hall'
    assert out['signs'][0]['pos'] == [183, 90, 174]
    assert out['signs'][0]['color'] == 'black'
    assert out['signs'][0]['glow'] is False

def test_banner_codes():
    pats = List[Compound]([Compound({'Pattern': String('ts'), 'Color': Int(14)})])
    t = te('minecraft:banner', -200, 90, 500, Patterns=pats)
    out = extras.extract({(-200, 90, 500): t}, [], OFF)
    assert out['banners'][0]['patterns'] == [['minecraft:stripe_top', 'red']]

def test_head_texture():
    owner = Compound({'Properties': Compound({'textures': List[Compound]([Compound({'Value': String('abc')})])})})
    t = te('minecraft:skull', -200, 90, 500, SkullOwner=owner)
    out = extras.extract({(-200, 90, 500): t}, [], OFF)
    assert out['heads'][0] == {'pos': [183, 90, 174], 'texture': 'abc'}

def test_head_without_texture_skipped():
    t = te('minecraft:skull', -200, 90, 500)
    assert extras.extract({(-200, 90, 500): t}, [], OFF)['heads'] == []

def test_painting():
    e = Compound({'id': String('minecraft:painting'), 'Motive': String('minecraft:kebab'), 'Facing': Byte(2),
                  'TileX': Int(-200), 'TileY': Int(90), 'TileZ': Int(500),
                  'Pos': List[Double]([Double(-199.5), Double(90.5), Double(500.9)])})
    out = extras.extract({}, [e], OFF)
    assert out['paintings'] == [{'pos': [183, 90, 174], 'facing': 'NORTH', 'variant': 'minecraft:kebab'}]

@needs_world
def test_real_counts():
    from hogwarts import anvil
    _, tes, ents = anvil.load_world(REGION)
    out = extras.extract(tes, ents, OFF)
    assert len(out['signs']) >= 2900
    assert sum(any(l.strip() for l in s['lines']) for s in out['signs']) >= 690
    assert len(out['banners']) >= 1900
    assert len(out['paintings']) >= 185
    assert len(out['heads']) > 500
