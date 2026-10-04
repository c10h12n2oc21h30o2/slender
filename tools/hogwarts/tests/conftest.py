import os, sys
import pytest
HERE = os.path.dirname(__file__)
sys.path.insert(0, os.path.abspath(os.path.join(HERE, '..')))
REGION = os.path.abspath(os.path.join(HERE, '..', 'input', 'region'))
needs_world = pytest.mark.skipif(not os.path.isdir(REGION), reason='falta tools/hogwarts/input/region')
