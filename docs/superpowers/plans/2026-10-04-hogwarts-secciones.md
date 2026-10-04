# Hogwarts por secciones + andén 9¾ (Slender 1.8.0) — Plan de implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convertir el castillo de Hogwarts de Gabbel (1.16.4) en 7 mapas de Slender, uno por sección y con su propia copia del castillo, a los que se entra desde un andén 9¾ nuevo en la estación. Sale como Slender 1.8.0.

**Architecture:**
- Una herramienta Python (`tools/hogwarts/`) lee el mundo 1.16.4 y genera tres recursos del jar:
  - `maps/hogwarts.schem` (Sponge v3);
  - `maps/hogwarts/extra.json.gz` (carteles, estandartes, cabezas y cuadros);
  - `maps/hogwarts/secciones.json` (por sección: derrumbes, páginas, pilas, spawns, salida, tablón, notas, frases, sonidos y luces).
- El plugin, recuperado de `Slender.jar` 1.7.0, tiene un `HogwartsBuilder` por sección: pega el schem en su propio origen, aplica los extras y su sección, y devuelve un `ArenaMap`.
- `HubBuilder` añade el andén 9¾ con 7 puertas.

**Tech Stack:** Python 3 + numpy + nbtlib 2 + scipy + Pillow + pytest; Java 25, Paper API 26.3 y JUnit 5 (`build.sh`, sin Gradle); Procyon 0.6 para descompilar.

**Spec:** `docs/superpowers/specs/2026-10-04-hogwarts-secciones-design.md` (en el proyecto: `hogwarts/spec-secciones.md`).

## Prerrequisitos (los hace el usuario antes de la Task 1)

Copiar del servidor a `C:\Users\kivi2\IdeaProjects\slender\servidor\` (desde PowerShell, un comando por carpeta):

```
scp -r two@ustwo:~/minecraft-26.3/libraries C:\Users\kivi2\IdeaProjects\slender\servidor\
scp -r two@ustwo:~/minecraft-26.3/cache     C:\Users\kivi2\IdeaProjects\slender\servidor\
```

- `libraries/` trae la API de Paper y sus dependencias, que se usan para compilar.
- `cache/mojang_26.3*.jar` es el servidor vanilla. Con él se genera la lista oficial de bloques (`--reports`) contra la que se validan los estados.

El mundo de Gabbel ya está conectado a la sesión (`Downloads\Hogwarts Castle - v.1.1 - by Gabbel\Hogwarts Castle - v.1.1.0 - by Gabbel`). Las regiones se suben al contenedor con `device_stage_files`.

## Global Constraints

- Versión del plugin **1.8.0**; `api-version: '26.3'`; compilar con `javac --release 25`.
- Repo nuevo en `codigo-1.8/`. Se entrega en `IdeaProjects\slender\codigo-1.8\` y el jar en `IdeaProjects\slender\Slender.jar`.
- Schematic: Sponge v3, gzip, `DataVersion = 5023`, `Offset = [0,0,0]` y orden de `Data` `x + z*W + y*W*L`, igual que `instituto.schem`.
- Recorte del mundo original: x −383…−1, z 326…738, y 0…254 (W = 383, L = 413, H = 255). El origen del schem (0,0,0) es el original (−383, 0, 326).
- Las coordenadas de `secciones.json` y `extra.json.gz` son **relativas al schem**.
- Ids de mapa:

  | Orden | id | Nombre |
  |---|---|---|
  | 1 | `hw_mazmorras` | Mazmorras |
  | 2 | `hw_comedor` | Gran Comedor |
  | 3 | `hw_ala_este` | Ala Este |
  | 4 | `hw_patio` | Patio de Transformaciones |
  | 5 | `hw_aulas` | Aulas altas |
  | 6 | `hw_torres` | Torres |
  | 7 | `hw_terrenos` | Terrenos |

  El `displayName` es "Hogwarts: " + nombre.
- Origen de cada copia: `(1000*(orden-1), 64, 6000)`. Pegado con `builder.shifted(-191, -34, -206)`, así que y_mundo = y_original + 30.
- Caja de limpieza relativa al origen: `(-192, -34, -207)` a `(192, 221, 207)`. Los mapas viejos siguen con `(-r,-12,-r)`…`(r,60,r)` y origen `(índice*2000, 64, 0)`.
- Iconos del menú: mazmorras `CAULDRON`, comedor `CAKE`, ala_este `JUKEBOX`, patio `BOOKSHELF`, aulas `BELL`, torres `SPYGLASS`, terrenos `OAK_BOAT`.
- Anillo de lago: 25 bloques alrededor de la isla. Pared de `minecraft:barrier` en el borde del recorte, de y = 0 a y = 30.
- Luces: se queda encendido el 10 % de las fuentes de luz (semilla 1234). Hay 8–12 `MapLight` FLICKER por sección.
- Por sección: 12–16 sitios de página, ≥8 pilas (objetivo 10), 3–4 spawns, 2–3 notas, 10 frases y 4–8 zonas de sonido.
- Derrumbe: `cobblestone`, `mossy_cobblestone`, `cracked_stone_bricks` y `stone_bricks`, más `cobweb` solo en la fila de arriba de cada hueco. Nada con gravedad. Semilla 1234.
- Carteles: 4 líneas de ≤15 caracteres. Todo el texto nuevo va en español. Las notas de Filch son texto original.
- Textos fijos:
  - créditos: `"Hogwarts por", "Gabbel", "v1.1.0", "planetminecraft"`;
  - cartel en la taquilla: `"DESAPARECIDO", "Argus Filch", "conserje de", "Hogwarts"`;
  - 8.ª puerta: `"LA NOCHE", "ENTERA", "", "próximamente"`;
  - muro: `"9¾"` y `"ANDÉN 9¾", "HOGWARTS", "", ""`.
- Andén 9¾ (relativo al origen de la estación −2000, 64, 0): ocupa x 0…34, z −24…−4. Hueco en la valla en z = −3, de 3 de ancho. Los `block_display` llevan la etiqueta de scoreboard `slender_hub`.
- `hogwarts` es id reservado en `MapIds`. `/slender build hogwarts` se expande a los 7 ids en orden.

## Review Focus

1. **Página en un hueco**: si la pared de un sitio de página es un panel, una losa o aire, el marco no se coloca y la página falta. El validador de la Task 9 exige un bloque sólido entero detrás y aire delante.
2. **Spawn o pila encerrados**: si caen en una celda de derrumbe o donde no se puede estar de pie, el jugador queda atrapado o la pila flota. La Task 9 lo comprueba.
3. **Reconstruir una sección con otra en juego**: la caja de limpieza de una copia no puede tocar la de otra ni la de los mapas viejos. Test en la Task 12.
4. **Entidades duplicadas al reconstruir**: si `clearJob` no borra cuadros ni `block_display`, cada reconstrucción los duplica. Comprobación en las Tasks 11 y 13.
5. **Mapas viejos movidos**: `origin()`/`clearBox()` por defecto tienen que dar exactamente lo de la 1.7, o `/slender build bosque` borraría lo que hay. Test en la Task 10.

---

### Task 1: Recuperar el código de Slender 1.7.0 como proyecto que compila

**Files:**
- Create: `codigo-1.8/` (git), `src/main/java/**` (de Procyon), `src/main/resources/{config.yml,plugin.yml,maps/**}` (del jar), `src/test/java/**` (de `slender-codigo.zip` 1.0), `build.sh`, `tools/libs.sh`, `.gitignore` (`libs/`, `build/`, `tools/hogwarts/input/`, `servidor/`)
- Modify: `src/main/java/dev/two/slender/game/GameManager.java` (rehacer `join`)

**Interfaces:**
- Produces: el árbol de fuentes de la 1.7.0, que compila con `./build.sh` contra `libs/*.jar`. Todas las clases del jar 1.7.0 existen con las mismas firmas públicas.

- [ ] **Step 1:** `git init codigo-1.8`. Copiar la salida de `procyon -o src/main/java Slender.jar` (descompilado con JDK 21, que lee class 69) y los recursos del jar. Copiar `build.sh` y `src/test/java` del zip 1.0. Commit `chore: código descompilado de la 1.7.0`.
- [ ] **Step 2:** `tools/libs.sh <carpeta servidor>` copia a `libs/` todos los `*.jar` de `servidor/libraries/`. Ejecutarlo. Debe quedar un `libs/paper-api-26.3*.jar`.
- [ ] **Step 3:** `./build.sh --no-tests`. Arreglar errores de descompilación hasta que compile:
  - casts genéricos (`(Collection<? extends E>)`);
  - lambdas con variables renombradas (`list2`, `n5`… que no existen en su ámbito: usar las de fuera);
  - `switch` sobre strings;
  - `while (n2 < (n2 = …))`, que hay que reescribir como bucles normales.
  Ejemplos en `HubBuilder.platform` y `arches`.
- [ ] **Step 4:** Reescribir `GameManager.join(Player, String mapId, String mode)` a partir del bytecode que Procyon deja en el comentario del método (es la única función que no descompila). Comprobar con `javap -c` que la secuencia de llamadas coincide con la del jar original.
- [ ] **Step 5:** Adaptar los tests de la 1.0 que no compilen a las firmas 1.7 (`ArenaMapTest` con el constructor de 16 argumentos y `Ambience.NONE`). Borrar solo los que prueben API que ya no existe y apuntarlo en el mensaje de commit.
- [ ] **Step 6:** `./build.sh` → `OK -> build/Slender.jar`, con todos los tests en verde.
- [ ] **Step 7: equivalencia de API pública.** Ejecutar `javap -public` sobre cada clase del jar original y del nuevo y comparar con `diff`. Esperado: sin diferencias en las firmas públicas y la misma lista de clases (los nombres de clases anónimas `$1`, `$2` pueden variar).
- [ ] **Step 8:** Commit `chore: 1.7.0 recompilable (GameManager.join reescrito)`.

### Task 2: Lector Anvil 1.16 y volumen

**Files:**
- Create: `tools/hogwarts/hogwarts/__init__.py`, `anvil.py`, `volume.py`, `tools/hogwarts/tests/test_anvil.py`, `tools/hogwarts/requirements.txt` (`numpy nbtlib scipy pillow pytest`)

**Interfaces:**
- Produces:
  - `anvil.iter_chunks(path: str) -> Iterator[nbtlib.Compound]`.
  - `anvil.decode_section(sec) -> tuple[list[str], np.ndarray]`. Devuelve la paleta como estados `"minecraft:x[a=b,...]"` (propiedades ordenadas alfabéticamente) y los índices con forma (16,16,16) = (y,z,x). Formato 1.16: bits = max(4, ⌈log2(len)⌉) y los valores no cruzan longs.
  - `volume.Volume(data: np.ndarray[uint16] (Y,Z,X), palette: list[str])`, con `idx(state) -> int`, `get(x,y,z) -> str`, `set(x,y,z,state)`, `names() -> np.ndarray[str]` (nombre sin propiedades por índice) y `mask(pred: Callable[[str],bool]) -> np.ndarray[bool]`.
  - `anvil.load_world(region_dir: str, box=(-383,-1,326,738,0,254)) -> tuple[Volume, dict[tuple,nbtlib.Compound], list[nbtlib.Compound]]`. Devuelve el volumen recortado (coordenadas relativas al recorte), las block entities por posición original (x,y,z) y las entidades.

- [ ] **Step 1: tests**
  - `test_decode_bits`: sección sintética con paleta de 5 estados y `BlockStates` empaquetados a 4 bits (construir los longs en el test) → los índices vuelven en el orden y,z,x.
  - `test_decode_17_states`: paleta de 17 estados → 5 bits por entrada y 12 entradas por long.
  - `test_real_column` (se salta si falta `tools/hogwarts/input/region`): columna original (−200, *, 520) → `bedrock` en y = 0; `spruce_planks` en y = 64; `coarse_dirt` en y = 84; aire en y = 85…191.
  - `test_volume_shape`: `load_world` → `data.shape == (255, 413, 383)`.
- [ ] **Step 2:** `pytest tools/hogwarts/tests/test_anvil.py -v` → FAIL.
- [ ] **Step 3:** Implementar. Las regiones se leen con zlib/gzip según el byte de compresión. Se ignoran los chunks fuera del recorte.
- [ ] **Step 4:** PASS. Commit `feat(hogwarts): lector anvil 1.16 y volumen`.

### Task 3: Registro de bloques 26.3 y conversión 1.16.4 → 26.3

**Files:**
- Create: `tools/hogwarts/hogwarts/registry.py`, `convert.py`, `tools/hogwarts/data/renames.json`, `tools/hogwarts/data/blocks_26.3.json` (generado), `tools/hogwarts/tests/test_convert.py`

**Interfaces:**
- Consumes: `Volume` (Task 2).
- Produces:
  - `registry.generate(mojang_jar: str, out_dir: str)`: ejecuta `java -DbundlerMainClass=net.minecraft.data.Main -jar <jar> --reports --output <tmp>`. Reduce `reports/blocks.json` a `out_dir/blocks_26.3.json` = `{ "minecraft:x": {"prop": ["v1", ...]} }` y saca de `reports/registries.json` → `minecraft:sound_event` la lista `out_dir/sounds_26.3.json` (ids sin `minecraft:`).
  - `registry.load(path) -> dict[str, dict[str, list[str]]]`.
  - `convert.convert_state(state: str) -> str`.
  - `convert.convert_volume(vol: Volume) -> None`: convierte la paleta en el sitio y fusiona duplicados.
  - `convert.invalid_states(palette: list[str], reg) -> list[str]`. Un estado es válido si el bloque existe y cada propiedad dada existe con un valor permitido; las que falten toman el valor por defecto.

- [ ] **Step 1:** `python -m hogwarts.registry <servidor/cache/mojang_26.3*.jar> tools/hogwarts/data/`. Commit de los dos json generados.
- [ ] **Step 2: tests**
  - `test_renames`:
    - `grass_path` → `minecraft:dirt_path`;
    - `grass` → `minecraft:short_grass`;
    - `chain[axis=y]` → `minecraft:iron_chain[axis=y,waterlogged=false]`, o sin waterlogged si no está en el origen;
    - `cauldron[level=2]` → `minecraft:water_cauldron[level=2]`;
    - `cauldron[level=0]` → `minecraft:cauldron`.
  - `test_lectern_without_book`: `lectern[facing=north,has_book=true,powered=false]` → `has_book=false` (los libros se quitan).
  - `test_unknown_property_flagged`: `invalid_states(["minecraft:stone[foo=bar]"], reg) == ["minecraft:stone[foo=bar]"]`.
  - `test_full_palette_valid` (se salta sin input): tras `convert_volume` sobre el mundo real, `invalid_states(...) == []`.
- [ ] **Step 3:** FAIL.
- [ ] **Step 4:** Implementar. `renames.json` es `{ "minecraft:grass_path": "minecraft:dirt_path", ... }` más reglas de propiedades en código (cauldron con nivel, lectern). Iterar el Step 6 hasta que no quede nada inválido y añadir a `renames.json` cada caso que salga.
- [ ] **Step 5:** PASS (incluido `test_full_palette_valid`). Commit `feat(hogwarts): conversión de bloques a 26.3 validada contra el registro oficial`.

### Task 4: Limpieza, luces y lago

**Files:**
- Create: `tools/hogwarts/hogwarts/clean.py`, `tools/hogwarts/data/luces.json`, `tools/hogwarts/tests/test_clean.py`

**Interfaces:**
- Consumes: `Volume`, `random.Random(1234)`.
- Produces:
  - `clean.strip(vol) -> dict[str,int]`: recuento de lo quitado. Pasa a aire: `command_block`, `chain_command_block`, `repeating_command_block`, `spawner`, `end_gateway`, `structure_block`, `redstone_wire`, `redstone_torch`, `redstone_wall_torch`, `repeater`, `comparator` y `observer`. El `fire` solo se queda si debajo hay `netherrack`, `soul_sand` o `soul_soil`.
  - `clean.dim_lights(vol, rng, keep=0.10) -> list[tuple[int,int,int]]`: devuelve las posiciones que siguen encendidas. Las demás se cambian según `luces.json`.
  - `clean.contain_lake(vol) -> None`: pared de `barrier` en las 4 caras exteriores del recorte, de y = 0 a y = 30.
- `luces.json` (bloque con luz → sustituto; `"air"` = quitar):
  - `lantern`, `soul_lantern`, `torch`, `wall_torch`, `soul_torch`, `soul_wall_torch` y `conduit` → `air`;
  - `glowstone` → `honeycomb_block`;
  - `sea_lantern` → `prismarine_bricks`;
  - `end_rod` → `lightning_rod`, conservando `facing`;
  - `jack_o_lantern` → `carved_pumpkin`, conservando `facing`;
  - `shroomlight` → `nether_wart_block`;
  - `beacon` → `glass`;
  - `campfire`/`soul_campfire` → la misma con `lit=false`.

- [ ] **Step 1: tests**
  - `test_strip_command_blocks`: volumen 3×3×3 con un `command_block` → aire y `report["minecraft:command_block"] == 1`.
  - `test_fire_rules`: `fire` sobre `netherrack` se queda; sobre `oak_planks` → aire.
  - `test_dim_lights_ratio`: 100 `glowstone` → `len(kept) == 10`; los otros 90 son `honeycomb_block`. Misma semilla → mismo resultado.
  - `test_end_rod_keeps_facing`: `end_rod[facing=up]` apagado → `lightning_rod[facing=up]`.
  - `test_lake_wall`: tras `contain_lake`, `vol.get(0,15,200) == "minecraft:barrier"` y `vol.get(0,31,200) != "minecraft:barrier"`.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(hogwarts): limpieza, luces y contención del lago`.

### Task 5: Extras — carteles, estandartes, cabezas y cuadros

**Files:**
- Create: `tools/hogwarts/hogwarts/extras.py`, `tools/hogwarts/tests/test_extras.py`

**Interfaces:**
- Consumes: block entities y entidades de `load_world`, más el desplazamiento del recorte `(dx,dy,dz) = (383, 0, -326)`.
- Produces: `extras.extract(tes, ents, offset) -> dict`, con este formato (es el que lee la Task 11):

```json
{"signs":    [{"pos":[x,y,z], "lines":["","","",""], "color":"black", "glow":false}],
 "banners":  [{"pos":[x,y,z], "patterns":[["minecraft:stripe_top","red"], ...]}],
 "heads":    [{"pos":[x,y,z], "texture":"<base64>"}],
 "paintings":[{"pos":[x,y,z], "facing":"NORTH", "variant":"minecraft:kebab"}]}
```

  más `extras.write(data, path)`, que escribe JSON en gzip.
- Conversiones:
  - Texto del cartel: `Text1..4` son JSON y se aplanan (`text` + `extra` recursivo).
  - Color de dye por entero: 0 white, 1 orange, 2 magenta, 3 light_blue, 4 yellow, 5 lime, 6 pink, 7 gray, 8 light_gray, 9 cyan, 10 purple, 11 blue, 12 brown, 13 green, 14 red, 15 black.
  - Códigos de patrón de estandarte → id `minecraft:`:
    - `b` base, `bs` stripe_bottom, `ts` stripe_top, `ls` stripe_left, `rs` stripe_right, `cs` stripe_center, `ms` stripe_middle;
    - `drs` stripe_downright, `dls` stripe_downleft, `ss` small_stripes, `cr` cross, `sc` straight_cross;
    - `ld` diagonal_left, `rd` diagonal_right, `lud` diagonal_up_left, `rud` diagonal_up_right;
    - `bl` square_bottom_left, `br` square_bottom_right, `tl` square_top_left, `tr` square_top_right;
    - `bt` triangle_bottom, `tt` triangle_top, `bts` triangles_bottom, `tts` triangles_top;
    - `mc` circle, `mr` rhombus, `bo` border, `cbo` curly_border, `bri` bricks, `gra` gradient, `gru` gradient_up;
    - `cre` creeper, `sku` skull, `flo` flower, `moj` mojang, `glb` globe, `pig` piglin.
  - Cabeza: `SkullOwner.Properties.textures[0].Value`. Las cabezas sin textura no se incluyen.
  - Cuadro: `Motive` → `variant`; `Facing` byte 0 = SOUTH, 1 = WEST, 2 = NORTH, 3 = EAST; `pos` = `TileX/Y/Z` desplazado. Solo se incluyen los que caen dentro del recorte.

- [ ] **Step 1: tests**
  - `test_sign_flatten`: `Text1 = '{"text":"","extra":[{"text":"Great "},{"text":"Hall"}]}'` → `lines[0] == "Great Hall"`.
  - `test_banner_codes`: `Patterns = [{Pattern:"ts",Color:14}]` → `[["minecraft:stripe_top","red"]]`.
  - `test_head_texture`: compound con `SkullOwner.Properties.textures[0].Value = "abc"` → `{"texture":"abc"}`.
  - `test_painting`: `Motive "minecraft:kebab", Facing 2b, TileX -200, TileY 90, TileZ 500` → `{"pos":[183,90,174], "facing":"NORTH", "variant":"minecraft:kebab"}`.
  - `test_real_counts` (se salta sin input): 2.974 carteles en total (698 con texto), 1.930 estandartes y 190 cuadros (± los que caigan fuera del recorte).
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS. Commit `feat(hogwarts): extras de bloques y cuadros`.

### Task 6: Traducción de carteles

**Files:**
- Create: `tools/hogwarts/hogwarts/signs.py`, `tools/hogwarts/data/carteles_es.json`, `tools/hogwarts/tests/test_signs.py`

**Interfaces:**
- Consumes: `extras["signs"]` (Task 5).
- Produces:
  - `signs.key(lines: list[str]) -> str`: las 4 líneas recortadas y unidas por `"\n"`.
  - `signs.translate_all(signs: list[dict], table: dict[str,list[str]]) -> None`: sustituye `lines` en el sitio. Lanza `KeyError` con la clave si falta alguna. Los carteles vacíos no se tocan.
  - `signs.dump_missing(signs, table) -> list[str]`: para la CLI `python -m hogwarts.signs --missing`.

- [ ] **Step 1: tests**
  - `test_translate`: tabla `{"\nLibrary\n\n": ["","Biblioteca","",""]}` → la línea 1 traducida.
  - `test_missing_raises`.
  - `test_table_complete` (se salta sin input): todas las claves de los 581 textos únicos están en `carteles_es.json`.
  - `test_line_length`: cada valor tiene 4 líneas de `len(...) <= 15`.
- [ ] **Step 2:** FAIL.
- [ ] **Step 3:** Implementar `signs.py`. Generar las 581 claves con `--missing` y escribir `carteles_es.json` a mano:
  - nombres de sala en su forma española habitual (Biblioteca, Sección Prohibida, Gran Comedor, Torre de Astronomía, Enfermería…);
  - nombres propios tal cual;
  - fórmulas y símbolos sin tocar;
  - chistes adaptados para que tengan gracia en español.
- [ ] **Step 4:** PASS. Commit `feat(hogwarts): carteles traducidos al español`.

### Task 7: Escritor de schematic y pipeline

**Files:**
- Create: `tools/hogwarts/hogwarts/schem.py`, `tools/hogwarts/hogwarts/main.py`, `tools/hogwarts/tests/test_schem.py`, `tools/hogwarts/LEEME.md`
- Output: `src/main/resources/maps/hogwarts.schem`, `src/main/resources/maps/hogwarts/extra.json.gz`

**Interfaces:**
- Consumes: Tasks 2–6.
- Produces:
  - `schem.write(vol: Volume, path: str, data_version: int = 5023) -> None`;
  - `schem.read(path) -> tuple[tuple[int,int,int], list[str], np.ndarray]`;
  - `python -m hogwarts.main --world <dir> --out <src/main/resources>`, que hace load → convert → strip → dim_lights → contain_lake → extras → translate → escribir los ficheros, y guarda `tools/hogwarts/build/luces_encendidas.json` (la Task 9 lo usa).

- [ ] **Step 1: tests**
  - `test_roundtrip_markers`: volumen 5×4×3 con `stone` en (4,0,0), `dirt` en (0,3,0) y `glass` en (0,0,2) → tras escribir y leer coinciden y el resto es aire.
  - `test_varint_300`: 300 estados distintos → ida y vuelta idéntica.
  - `test_header`: `Version == 3`, `DataVersion == 5023`, `Offset == [0,0,0]` y W/H/L como Short.
  - `test_plugin_reader_compat` (Java, en el Step 4): `SchematicReader.read` del plugin lee el fichero de prueba y da las mismas dimensiones y el mismo estado en (4,0,0).
- [ ] **Step 2:** FAIL. **Step 3:** implementar `schem.py` y `main.py`.
- [ ] **Step 4:** Ejecutar el pipeline sobre el mundo real. Esperado:
  - dimensiones 383 × 255 × 413;
  - paleta sin estados inválidos;
  - `extra.json.gz` con carteles, estandartes, cabezas y cuadros;
  - un resumen impreso con el top 15 de bloques y lo quitado.
  Añadir `SchematicCompatTest` en Java, que lee `maps/hogwarts.schem` del classpath con `SchematicReader.resource` y comprueba las dimensiones.
- [ ] **Step 5:** Commit `feat(hogwarts): schematic y extras generados`. El `.schem` va al repo como en el instituto.

### Task 8: Andabilidad, secciones, derrumbes y verificación

**Files:**
- Create: `tools/hogwarts/hogwarts/walk.py`, `sections.py`, `render.py`, `tools/hogwarts/data/secciones_def.json`, `tools/hogwarts/tests/test_sections.py`

**Interfaces:**
- Consumes: el volumen ya convertido y limpio (Task 7).
- Produces:
  - `walk.passable(vol) -> np.ndarray[bool]`: sin colisión. Cuentan aire, agua, antorchas, carteles, estandartes, botones, placas, alfombras, plantas, enredaderas, escaleras de mano, telarañas, raíles, puertas/trampillas/puertas de valla de madera y andamios. Las puertas de hierro no.
  - `walk.standable(vol, passable) -> np.ndarray[bool]`: pies y cabeza libres y debajo algo no pasable; o pies en losa inferior/escalera con dos celdas libres encima; o celda de escalada (`ladder`, `vine`, `scaffolding`, `water`).
  - `walk.flood(standable, starts: list[tuple], blocked: np.ndarray[bool]|None) -> np.ndarray[bool]`: horizontal en la misma y, ±1 en y con un vecino horizontal, y vertical en celdas de escalada.
  - `sections.load_defs(path) -> list[SectionDef]`, con `SectionDef(id, name, order, boxes: list[tuple[x1,y1,z1,x2,y2,z2]], outdoor: bool)` en coordenadas del schem.
  - `sections.section_mask(d, standable, covered) -> np.ndarray[bool]`.
  - `sections.seal_cells(mask, passable, keep: set[tuple]) -> list[tuple]`: celdas pasables fuera de la sección, adyacentes (6 vecinos) a una celda andable de la sección, y su columna de 2 de alto. `keep` son las celdas del gate, que no se sellan.
  - `sections.rubble(cells, rng) -> list[tuple[x,y,z,str]]`: paleta y semilla de los Global Constraints; `cobweb` solo si no hay otra celda de derrumbe encima.
  - `render.section_png(...)`: planos por bandas de altura con la sección y los derrumbes resaltados, para revisar a ojo.
- `secciones_def.json`: las cajas de cada sección se sacan de la propuesta aprobada (`sections.py` del análisis: alturas originales 26–65 / 66–131 / 66–105 / 106–131 / 132–228 / exterior ≤95; el Ala Este es la cuña al noreste `x > -150 + (z-420)*0.55, z < 515`; el Gran Comedor es z < 528). Pasarlas a coordenadas del schem y ajustarlas mirando los PNG.

- [ ] **Step 1: tests**
  - `test_flood_blocked`: un pasillo 1×2 con una celda de derrumbe en medio → el flood no la cruza.
  - `test_stairs_step`: escalón de `oak_stairs` → sube.
  - `test_seal_closes_door`: dos salas unidas por una `oak_door` → `seal_cells` incluye las 2 celdas de la puerta.
  - `test_real_sections_closed` (se salta sin input): por cada sección, flood desde su primer spawn (de `contenido/<id>.json`, Task 9; mientras no exista, desde la celda andable más cercana al centro de la sección) con los derrumbes y el gate cerrados → `alcanzado & ~mask == ∅`. Si `alcanzado.sum() < 0.6 * mask.sum()`, aviso (no fallo).
- [ ] **Step 2:** FAIL. **Step 3:** implementar y ajustar `secciones_def.json` hasta que `test_real_sections_closed` pase para las 7.
- [ ] **Step 4:** Generar `tools/hogwarts/build/plano_<id>.png` (7 ficheros) y revisarlos a ojo: cada sección es la de la tabla de la spec y no hay derrumbes en ventanas ni en mitad de las salas.
- [ ] **Step 5:** Commit `feat(hogwarts): secciones selladas y verificadas`.

### Task 9: Contenido de las secciones y `secciones.json`

**Files:**
- Create: `tools/hogwarts/hogwarts/content.py`, `tools/hogwarts/data/contenido/<id>.json` (7), `tools/hogwarts/tests/test_content.py`
- Output: `src/main/resources/maps/hogwarts/secciones.json`

**Interfaces:**
- Consumes: Task 8 (`standable`, máscaras, `seal_cells`, `rubble`), `luces_encendidas.json` (Task 7) y los carteles traducidos (Task 6, para nombrar las salas).
- `contenido/<id>.json` (escrito a mano, coordenadas del schem):

```json
{"spawns":[[x,y,z,yaw]], "pages":[[x,y,z,"NORTH","Biblioteca"]], "batteries":[[x,y,z]],
 "gate":[[x,y,z]], "gate_block":"minecraft:mossy_cobblestone", "exit":[x1,y1,z1,x2,y2,z2],
 "board":{"pos":[x,y,z], "title":["MAZMORRAS",""], "places":[["Pociones",x,z]]},
 "notes":[{"pos":[x,y,z],"facing":"NORTH","lines":["","","",""],"support":"minecraft:spruce_planks"}],
 "phrases":["..."], "sounds":["x,y,z|radio|sonido|vol|pitch|prob"], "lights":[[x,y,z,"minecraft:candle[candles=4,lit=true]"]]}
```

  En `pages`, (x,y,z) es el bloque de la pared y `facing` la dirección hacia la que mira la página (como `Builder.pageOnWall`).
- Produces:
  - `content.candidates(id) -> list`: CLI `python -m hogwarts.content candidatos hw_patio`. Lista paredes sólidas enteras con aire delante, dentro de la sección y a ≤6 bloques de un cartel traducido (que da la etiqueta de la sala). Sirve para elegir las páginas a mano.
  - `content.compile(defs, contents, vol) -> dict`: escribe `secciones.json`. La lee la Task 12 con este formato (coordenadas del schem):

```json
{"version":1,"size":[383,255,413],
 "sections":[{"id":"hw_mazmorras","name":"Mazmorras","order":1,"icon":"CAULDRON",
   "bounds":[x1,z1,x2,z2],"fallY":y,
   "spawns":["x,y,z,yaw"],"pages":["x,y,z|NORTH|Biblioteca"],"batteries":["x,y,z"],
   "gate":["x,y,z"],"gateBlock":"minecraft:mossy_cobblestone","exit":["x,y,z","x,y,z"],
   "seal":[[x,y,z,"minecraft:cobblestone"]],
   "board":{"pos":"x,y,z","title":["",""],"places":[["Pociones",x,z]]},
   "notes":[{"pos":"x,y,z","facing":"NORTH","lines":["","","",""],"support":"minecraft:..."}],
   "lights":["x,y,z|flicker"],"lightBlocks":[["x,y,z","minecraft:candle[candles=4,lit=true]"]],
   "sounds":["x,y,z|r|snd|v|p|c"],"phrases":["..."],"story":1}]}
```

  `bounds` es el rectángulo de la máscara más 3; `fallY` es la y mínima andable de la sección menos 6; `story` es el orden de las notas para el modo historia (= `order`).
- Contenido que hay que escribir:
  - **Notas de Filch** (2–3 por sección, 4 líneas de ≤15 caracteres, texto original). En orden 1→7 cuentan: la ronda de noche; los alumnos que no están en sus camas; los retratos vacíos; la Señora Norris que no vuelve; el hombre sin cara en los pasillos; Filch subiendo a las torres; la última nota en el embarcadero.
  - **Frases**: 10 por sección, voz de Filch ("¡Alumnos fuera de la cama!"…).
  - **Sonidos**: 4–8 por sección según la spec. Solo ids que estén en `tools/hogwarts/data/sounds_26.3.json` (Task 3). No hay sonido de lechuza: en las Torres van alas (`entity.bat.takeoff`, `entity.parrot.fly`) y viento.
  - **Salidas**: las de la tabla de la spec. Si una no tiene un recorrido limpio, otra salida temática de la misma sección, apuntada en `LEEME.md`.

- [ ] **Step 1: tests** (por sección, sobre el mundo real; se saltan sin input)
  - `test_counts`: 12 ≤ páginas ≤ 16, pilas ≥ 8, 3 ≤ spawns ≤ 4, 2 ≤ notas ≤ 3, frases == 10, 4 ≤ sonidos ≤ 8, 8 ≤ luces ≤ 12.
  - `test_page_walls` (**Review Focus 1**): el bloque de la pared es sólido entero (no pasable, no `_slab`, `_stairs`, `_pane`, `iron_bars`, `_fence`, `_wall` ni `glass_pane`) y la celda de delante (`pos + facing`) es pasable y está en la máscara de la sección.
  - `test_spawns_batteries_standable` (**Review Focus 2**): cada spawn y cada pila está en una celda andable de la máscara y no en una celda de `seal` ni de `gate`.
  - `test_reachable`: flood desde el spawn 0 con derrumbes y gate cerrados → llega a todas las celdas de delante de las páginas y a todas las pilas. Con el gate abierto → llega a la zona `exit`.
  - `test_note_lines`: 4 líneas de ≤15 caracteres.
  - `test_sounds_exist`: cada id de sonido está en `sounds_26.3.json`.
  - `test_lights_lightable`: cada `lightBlocks` es `candle`, `redstone_lamp`, `campfire` o `copper_bulb` con `lit=true`, y su posición es la de un `lights`.
- [ ] **Step 2:** FAIL. **Step 3:** implementar `content.py`. Escribir los 7 `contenido/<id>.json` usando `candidatos` y los PNG de la Task 8.
- [ ] **Step 4:** PASS. `python -m hogwarts.main` regenera también `secciones.json`.
- [ ] **Step 5:** Commit `feat(hogwarts): contenido de las 7 secciones`.

### Task 10: Origen y limpieza por mapa, y bloques inválidos

**Files:**
- Modify: `src/main/java/dev/two/slender/map/MapBuilder.java`, `map/BuildManager.java` (`originOf`, `build`), `map/Builder.java` (`data(String)`)
- Create: `src/main/java/dev/two/slender/map/SafeBlockData.java`, `src/test/java/dev/two/slender/map/MapBuilderDefaultsTest.java`, `src/test/java/dev/two/slender/map/SafeBlockDataTest.java`

**Interfaces:**
- Produces:
  - `MapBuilder.origin(int index)`: `default BlockPos origin(int index) { return new BlockPos(index * 2000, 64, 0); }`.
  - `MapBuilder.clearBox()`: `default Zone clearBox()` → `Zone.of(new BlockPos(-r,-12,-r), new BlockPos(r,60,r))` con `r = radius()`, relativa al origen.
  - `BuildManager.originOf(id)` llama a `builder.origin(índice en la lista)`, y `build()` limpia `clearBox()`.
  - `SafeBlockData.resolve(String state, Function<String,T> parser, T fallback, Set<String> warned, Consumer<String> log) -> T`: si `parser` lanza `IllegalArgumentException`, devuelve `fallback` y avisa una sola vez por estado (`"Bloque no válido en 26.3, uso piedra: " + state`).
  - `Builder.data(String)` usa `SafeBlockData.resolve` con `Material.STONE.createBlockData()` y el logger del plugin (nuevo parámetro opcional en el constructor de `Builder`; el actual delega con `Logger.getLogger("Slender")`).

- [ ] **Step 1: tests** (**Review Focus 5**)
  - `MapBuilderDefaultsTest.oldOriginsUnchanged`: con un `MapBuilder` mínimo de radio 232, `origin(3) == new BlockPos(6000,64,0)` y `clearBox() == Zone.of(new BlockPos(-232,-12,-232), new BlockPos(232,60,232))`.
  - `SafeBlockDataTest.fallbackAndWarnOnce`: un parser que lanza para `"x"` → dos llamadas con `"x"` devuelven el fallback y `log` se llama una sola vez.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** `./build.sh` → PASS.
- [ ] **Step 5:** Commit `feat: origen, limpieza por mapa y bloques inválidos tolerados`.

### Task 11: Extras de schematic en el plugin

**Files:**
- Create: `src/main/java/dev/two/slender/map/schem/SchematicExtras.java`, `src/test/java/dev/two/slender/map/schem/SchematicExtrasTest.java`
- Modify: `map/BlockQueue.java` (nuevo `worldJob`), `map/Builder.java` (nuevo `extras`)

**Interfaces:**
- Consumes: el formato de `extra.json.gz` de la Task 5. Se parsea con Gson, que ya viene en Paper.
- Produces:
  - `record SchematicExtras(List<SignText> signs, List<BannerData> banners, List<HeadData> heads, List<PaintingData> paintings)`.
    - `record SignText(BlockPos pos, List<String> lines, String color, boolean glow)`
    - `record BannerData(BlockPos pos, List<String[]> patterns)`, donde cada patrón es `{patternKey, dyeColorName}`
    - `record HeadData(BlockPos pos, String texture)`
    - `record PaintingData(BlockPos pos, String facing, String variant)`
  - `static SchematicExtras read(InputStream gzip)` y `static SchematicExtras resource(String path)`.
  - `BlockQueue.worldJob(Consumer<World> action) -> Job`: se ejecuta una vez y gasta 1 de presupuesto.
  - `Builder.extras(SchematicExtras e, int dx, int dy, int dz)`: encola un `worldJob` por elemento, desplazado con `ox+dx`…
    - Cartel: `Sign` → `getSide(Side.FRONT)`, líneas con `Component.text`, `setColor` y `setGlowingText`.
    - Estandarte: `Banner#setPatterns` con `Pattern(DyeColor, RegistryAccess…PatternType por key)`.
    - Cabeza: `Skull#setPlayerProfile`, con un perfil que lleva la propiedad `textures`.
    - Cuadro: `world.spawn(loc, Painting.class, p -> { p.setArt(art, true); p.setFacingDirection(face, true); })`.
    - Cada elemento va en `try/catch`: si falla, aviso y sigue.

- [ ] **Step 1: tests**
  - `SchematicExtrasTest.readsAllKinds`: un gzip pequeño con un elemento de cada tipo → los cuatro records con sus valores.
  - `SchematicExtrasTest.resourceExists`: `resource("maps/hogwarts/extra.json.gz")` → `signs().size() >= 698` y `banners().size() >= 1900`.
- [ ] **Step 2:** FAIL. **Step 3:** implementar.
- [ ] **Step 4 (Review Focus 4):** leer `BlockQueue.clearJob` y confirmar que quita todas las entidades no jugador del área, incluidos `Painting` y `BlockDisplay`. Si solo quita algunos tipos, ampliar a `!(e instanceof Player)` y añadir un test con una lista de tipos sobre la función de filtro extraída (`static boolean clearable(EntityType)`).
- [ ] **Step 5:** `./build.sh` → PASS. Commit `feat: extras de schematic (carteles, estandartes, cabezas y cuadros)`.

### Task 12: `HogwartsBuilder` × 7, registro y comandos

**Files:**
- Create: `src/main/java/dev/two/slender/map/builders/hogwarts/HogwartsSections.java`, `HogwartsBuilder.java`, `HogwartsMaps.java`, `src/test/java/dev/two/slender/map/builders/hogwarts/HogwartsSectionsTest.java`, `HogwartsMapsTest.java`
- Modify: `SlenderPlugin.java` (lista de builders), `map/MapIds.java` (`RESERVED` + `"hogwarts"`), `command/SlenderCommand.java` (`build hogwarts` y autocompletado)

**Interfaces:**
- Consumes: `secciones.json` (Task 9), `SchematicExtras` y `Builder.extras` (Task 11), y `origin`/`clearBox` (Task 10).
- Produces:
  - `HogwartsSections.Section`: record con los campos de `secciones.json`, ya parseados a `BlockPos`, `Loc`, `PageSpot`, `SoundZone` y `MapLight` y sin desplazar. `static List<Section> read(InputStream)` y `static List<Section> resource()`.
  - `HogwartsBuilder(Section s)` implementa `MapBuilder`:
    - `id()`, `displayName()` ("Hogwarts: " + nombre) e `icon()` (`Material.valueOf(s.icon())`);
    - `radius() = 210`;
    - `origin(int i) = new BlockPos(1000*(s.order()-1), 64, 6000)`;
    - `clearBox() = Zone.of(new BlockPos(-192,-34,-207), new BlockPos(192,221,207))`.
  - `build(builder, world, seed)`:
    - `b = builder.shifted(-191,-34,-206)`;
    - `b.paste(schem)` y `b.extras(extras,0,0,0)`;
    - derrumbes con `b.set`, `lightBlocks` con `b.set`, `Decor.board`, `Decor.note` por nota y `b.pageOnWall` por página;
    - devuelve `new ArenaMap(id, displayName, world, builder.abs(0,0,0), min, max, spawns[0], spawns, pages, batteries, gate, gateBlock, exitMin, exitMax, seed, new Ambience(sounds, lights, phrases).offset(b.ox,b.oy,b.oz))`;
    - `min = b.abs(bounds.x1, fallY, bounds.z1)` y `max = b.abs(bounds.x2, 250, bounds.z2)`.
    - El schem y los extras se cargan una sola vez (caché estática en `HogwartsMaps`).
  - `HogwartsMaps.all() -> List<MapBuilder>`: los 7 en orden.
  - `HogwartsMaps.IDS -> List<String>`.
  - `SlenderCommand`: en `build`, `"hogwarts"` se expande a `HogwartsMaps.IDS`; el autocompletado de `build` añade `"hogwarts"`.

- [ ] **Step 1: tests**
  - `HogwartsSectionsTest.readsSeven`: `resource()` devuelve 7 secciones con ids y orden de la tabla de los Global Constraints, y cada una tiene 12–16 páginas.
  - `HogwartsMapsTest.originsAndBoxesDontOverlap` (**Review Focus 3**): para cada par de secciones, las cajas `origin + clearBox` no se cruzan, y ninguna cruza la de un mapa viejo (`origin(i)` por defecto con radio 232, i = 0…3) ni la estación (−2000±41).
  - `HogwartsMapsTest.idsValidAndReserved`: cada id pasa `MapIds.valid` y `MapIds.valid("hogwarts") == false`.
  - `HogwartsMapsTest.buildAliasExpands`: la función pura extraída `SlenderCommand.expandBuildTargets(String arg, List<String> allIds) -> List<String>` con `"hogwarts"` devuelve los 7 ids en orden; con `"all"` devuelve todos los builders más `"vestibulo"`.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. Registrar en `SlenderPlugin` `List.of(Forest, Asylum, Cornfield, Instituto)` + `HogwartsMaps.all()`, manteniendo los 4 primeros en sus índices.
- [ ] **Step 4:** `./build.sh` → PASS. Commit `feat: Hogwarts por secciones (7 mapas)`.

### Task 13: Andén 9¾ en la estación

**Files:**
- Create: `src/main/java/dev/two/slender/hub/Anden934.java`, `src/test/java/dev/two/slender/hub/Anden934Test.java`
- Modify: `hub/HubBuilder.java` (llamar a `Anden934.build`, juntar puertas, cartel de Filch y hueco en la valla), `map/Builder.java` (nuevo `display`)

**Interfaces:**
- Consumes: `HogwartsMaps.IDS` y los nombres de las secciones.
- Produces:
  - `Anden934.doors() -> LinkedHashMap<String, Zone>`: función pura en coordenadas relativas a la estación (una zona 3×3×1 en cada puerta de vagón).
  - `Anden934.build(Builder b) -> Map<String, Zone>`: construye y devuelve `doors()` en coordenadas absolutas (`b.abs`).
    - **Muro**: `bricks` y pilares de `stone_bricks`, unos 11 de ancho por 6 de alto, en z = −6, x ≈ 12…22, con el hueco central de 3×3 en aire y carteles con los textos de los Global Constraints.
    - **Andén escondido** al norte del muro: suelo de `stone_bricks`/`polished_andesite`, vía con `rail` y 2–3 farolas de luz débil.
    - **Expreso** a lo largo de x: locomotora de `black_concrete`/`red_concrete` con chimenea y 3 vagones de `red_terracotta`/`red_concrete`, 8 puertas.
    - Puertas 1–7 con su cartel; la 8.ª tapiada con `bricks` y su cartel.
    - Cartel de créditos.
  - `Builder.display(int x, int y, int z, String blockState, String tag)`: `worldJob` que hace spawn de un `BlockDisplay` con ese bloque y `addScoreboardTag(tag)`. Se usa para las 9 celdas del hueco con `"minecraft:bricks"` y `"slender_hub"`.
  - `HubBuilder.build`:
    - quita la valla de z = −3 en las 3 x frente al muro;
    - pone el cartel de Filch en la pared interior de la taquilla, en un sitio libre de x ∈ [−23,−7], con los textos de los Global Constraints;
    - el `Hub` final lleva `arches ∪ Anden934.build(...)`.

- [ ] **Step 1: tests**
  - `Anden934Test.sevenDoorsInYard`: `doors()` tiene las claves de `HogwartsMaps.IDS` en orden. Cada zona está dentro de x 0…34, z −24…−4, sin solaparse con otra ni con las zonas de los arcos (x −26…26, z 17…19).
  - `Anden934Test.wallGapCells`: `Anden934.gapCells()` (función pura) devuelve 9 celdas, todas con z = −6.
- [ ] **Step 2:** FAIL. **Step 3:** implementar.
- [ ] **Step 4 (Review Focus 4):** confirmar que `buildHub` limpia con `clearJob` (que borra entidades, Task 11) antes de construir, de modo que reconstruir la estación no duplica los `block_display`.
- [ ] **Step 5:** `./build.sh` → PASS. Commit `feat: andén 9¾ con el Expreso de Hogwarts`.

### Task 14: Versión 1.8.0, documentación y entrega

**Files:**
- Modify: `src/main/resources/plugin.yml` (`version: 1.8.0`), `INSTALACION.md`
- Create: en el proyecto `hogwarts/plan-implementacion.md` (este plan), `hogwarts/entrega.md`

**Interfaces:**
- Consumes: todo lo anterior.

- [ ] **Step 1:** Añadir a `INSTALACION.md` un apartado "Novedades 1.8: Hogwarts", con el mismo estilo que los de la 1.4–1.7. Debe decir:
  - cambiar el jar, reiniciar y ejecutar `/slender build hogwarts` (15–20 min, sin nadie jugando);
  - después `/slender build vestibulo` para el andén 9¾;
  - las 7 secciones y qué hay en cada una;
  - el muro (Java vs Bedrock);
  - el diario de Filch;
  - los créditos a Gabbel.
  Cambiar el título a v1.8.0.
- [ ] **Step 2:** `./build.sh` → `OK`, con todos los tests (Java y `pytest tools/hogwarts`) en verde. Comprobar que `unzip -l build/Slender.jar` incluye `maps/hogwarts.schem`, `maps/hogwarts/extra.json.gz` y `maps/hogwarts/secciones.json`.
- [ ] **Step 3:** Entregar en el PC del usuario con `device_commit_files` (antes, copiar los ficheros a `/mnt/user-data/outputs/`):
  - `IdeaProjects\slender\Slender.jar`;
  - `IdeaProjects\slender\INSTALACION.md`;
  - `IdeaProjects\slender\codigo-1.8.zip`, el repo sin `libs/` ni `build/`.
- [ ] **Step 4:** Escribir en el proyecto `hogwarts/entrega.md`:
  - qué hay y cómo instalarlo;
  - las salidas finales de cada sección;
  - las desviaciones de la spec.
  Y la **lista de comprobación en el servidor**:
  1. `/version Slender` → 1.8.0.
  2. `/slender build hogwarts` termina sin avisos de bloque no válido (o se apuntan).
  3. `/slender build vestibulo` y cruzar el muro 9¾ desde Java; desde Bedrock se ve un hueco.
  4. Las 7 puertas del Expreso llevan a su sección; la 8.ª no hace nada.
  5. En cada sección, en coop:
     - aparecen las páginas en paredes;
     - las pilas están en el suelo;
     - no se puede pasar los derrumbes;
     - al tener las 8 páginas se abre la salida y se escapa.
  6. Los carteles salen en español, los estandartes con sus dibujos, las cabezas con textura y los cuadros en su pared.
  7. Las luces parpadean y casi todo está a oscuras.
- [ ] **Step 5:** Commit `release: Slender 1.8.0 con Hogwarts`.
