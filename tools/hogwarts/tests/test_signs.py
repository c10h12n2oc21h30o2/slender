import json, os
import pytest
from conftest import needs_world, REGION
from hogwarts import signs

TABLE = os.path.join(os.path.dirname(__file__), '..', 'data', 'carteles_es.json')

def test_key_strips_and_joins():
    assert signs.key([' Library ', '', '', '']) == 'Library\n\n\n'

def test_translate():
    s = [{'pos': [0, 0, 0], 'lines': ['', 'Library', '', ''], 'color': 'black', 'glow': False}]
    signs.translate_all(s, {'\nLibrary\n\n': ['', 'Biblioteca', '', '']})
    assert s[0]['lines'][1] == 'Biblioteca'

def test_empty_signs_untouched():
    s = [{'pos': [0, 0, 0], 'lines': ['', '', '', ''], 'color': 'black', 'glow': False}]
    signs.translate_all(s, {})
    assert s[0]['lines'] == ['', '', '', '']

def test_missing_raises():
    s = [{'pos': [0, 0, 0], 'lines': ['Foo', '', '', ''], 'color': 'black', 'glow': False}]
    with pytest.raises(KeyError):
        signs.translate_all(s, {})

def test_line_length():
    table = json.load(open(TABLE, encoding='utf-8'))
    for k, v in table.items():
        assert len(v) == 4, k
        assert all(len(l) <= 15 for l in v), (k, v)

@needs_world
def test_table_complete():
    from hogwarts import anvil, extras
    _, tes, ents = anvil.load_world(REGION)
    data = extras.extract(tes, ents, (383, 0, -326))
    table = json.load(open(TABLE, encoding='utf-8'))
    assert signs.dump_missing(data['signs'], table) == []
