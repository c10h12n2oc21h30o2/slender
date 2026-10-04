import os
import pytest
from conftest import needs_world, REGION
from hogwarts import convert, registry

DATA = os.path.join(os.path.dirname(__file__), '..', 'data')
REG = registry.load(os.path.join(DATA, 'blocks_26.3.json'))

def test_renames():
    assert convert.convert_state('minecraft:grass_path') == 'minecraft:dirt_path'
    assert convert.convert_state('minecraft:grass') == 'minecraft:short_grass'
    assert convert.convert_state('minecraft:chain[axis=y]') == 'minecraft:iron_chain[axis=y]'
    assert convert.convert_state('minecraft:cauldron[level=2]') == 'minecraft:water_cauldron[level=2]'
    assert convert.convert_state('minecraft:cauldron[level=0]') == 'minecraft:cauldron'

def test_lectern_without_book():
    out = convert.convert_state('minecraft:lectern[facing=north,has_book=true,powered=false]')
    assert out == 'minecraft:lectern[facing=north,has_book=false,powered=false]'

def test_unknown_property_flagged():
    assert convert.invalid_states(['minecraft:stone[foo=bar]'], REG) == ['minecraft:stone[foo=bar]']
    assert convert.invalid_states(['minecraft:oak_stairs[facing=north]'], REG) == []
    assert convert.invalid_states(['minecraft:nope'], REG) == ['minecraft:nope']

@needs_world
def test_full_palette_valid():
    from hogwarts import anvil
    vol, _, _ = anvil.load_world(REGION)
    convert.convert_volume(vol)
    assert convert.invalid_states(vol.palette, REG) == []
