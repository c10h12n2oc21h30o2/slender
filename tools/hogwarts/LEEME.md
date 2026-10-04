# tools/hogwarts

Convierte el castillo de Hogwarts de Gabbel (v1.1.0, Minecraft 1.16.4) en los recursos que usa Slender 1.8:

- `src/main/resources/maps/hogwarts.schem`: el castillo y la isla con el lago (Sponge v3, DataVersion 5023).
- `src/main/resources/maps/hogwarts/extra.json.gz`: textos de carteles (traducidos), estandartes, cabezas y cuadros.
- `src/main/resources/maps/hogwarts/secciones.json`: las 7 secciones (derrumbes, páginas, pilas, spawns, salida, notas, sonidos y luces).

## Cómo se regenera

1. Copia las regiones del mundo de Gabbel a `tools/hogwarts/input/region/` (`r.*.mca`).
2. `pip install -r tools/hogwarts/requirements.txt`
3. Desde `tools/hogwarts/`: `python3 -m hogwarts.main --world input/region --out ../../src/main/resources`
4. Tests: `python3 -m pytest tools/hogwarts/tests -q` (los que necesitan el mundo se saltan si no está).

## Datos que se editan a mano (`data/`)

- `renames.json`: bloques que cambian de nombre entre 1.16.4 y 26.3.
- `luces.json`: por qué bloque sin luz se cambia cada fuente de luz que se apaga.
- `carteles_es.json`: traducción de cada cartel (4 líneas de 15 caracteres como mucho).
  Para sacar los que falten: `python3 -m hogwarts.signs --missing input/region`.
- `secciones_def.json` y `contenido/<id>.json`: cajas de cada sección y su contenido.
- `blocks_26.3.json` y `sounds_26.3.json` salen del servidor vanilla:
  `python3 -m hogwarts.registry <servidor>/cache/mojang_26.3.jar data/`.

## Notas

- Coordenadas: el origen del schem (0,0,0) es el punto original (−383, 0, 326). Las de `secciones.json` y
  `extra.json.gz` son relativas al schem.
- Al escribir el schem se vacía el terreno que no se ve (piedra rodeada de piedra por las 6 caras): son ~3,1 M de
  bloques menos que pegar en cada copia. El análisis de secciones usa el volumen sin vaciar.
