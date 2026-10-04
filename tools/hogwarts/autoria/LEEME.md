# Scripts de autoría (secciones y contenido)

Se ejecutan desde `tools/hogwarts/` y usan la caché de `build/` (no va al repo):

1. `python3 autoria/ana.py`: carga el mundo convertido y guarda `build/vol.pkl`, `st.npy`, `roof.npy` y `pas.npy`.
2. `python3 autoria/iter.py [ids]`: comprueba cada sección (celdas, alcanzadas, fugas, derrumbes) y dibuja `build/plano_<id>.png`.
3. `python3 autoria/propose_all.py`: propone spawns, páginas, pilas, luces y notas → `build/propuesta_<id>.json`.
4. `python3 autoria/exits.py <id>`: lista los huecos tapados que pueden servir de salida.
5. `python3 autoria/exits2.py`: con la salida elegida a mano, calcula gate, zona de salida y sitio del tablón → `build/exits.json`.
6. `python3 autoria/write_content.py`: junta todo con las etiquetas, notas, frases y sonidos escritos a mano y escribe
   `data/contenido/<id>.json` (coordenadas originales).

Después, `python3 -m hogwarts.main ...` compila `secciones.json`.
