# Hogwarts por secciones + andén 9¾ (Slender 1.8.0): diseño

Fecha: 2026-10-04 · Estado: pendiente del OK del usuario

## Objetivo

Meter el castillo de Hogwarts de Gabbel (v1.1.0) en el minijuego Slender como **7 mapas, uno por sección del castillo**. Se entra desde un **andén 9¾** nuevo en la estación de Valdemora.

Es el **subproyecto 1 de 2**. El subproyecto 2, el **modo historia** ("La noche entera": un mapa largo con una fase por sección, un Slender que gana poder y habilidades y compañeros que se pueden revivir estilo R.E.P.O.), se diseña después sobre estas mismas secciones. Esta spec deja preparado lo que el modo historia reutiliza: la definición de las secciones, las notas en orden y la 8.ª puerta del Expreso.

## Decisiones del usuario

- 7 secciones: Mazmorras, Gran Comedor, Ala Este, Patio de Transformaciones, Aulas altas, Torres y Terrenos.
- Cada sección es un mapa normal de Slender: 8 páginas por partida, salida propia, modo coop y asimétrico.
- Una copia completa del castillo por sección (7 copias): las partidas no se ven ni se afectan entre sí.
- Andén 9¾ en el patio sin usar al este de la taquilla:
  - muro de ladrillo atravesable con "ladrillos fantasma";
  - detrás, el Expreso de Hogwarts con 7 puertas (una por sección) y una 8.ª tapiada para el modo historia.
- Historia: el diario de Argus Filch. Cartel nuevo en la estación: "DESAPARECIDO — Argus Filch".
- Luz: casi todo a oscuras, con unas pocas luces que parpadean.
- Los carteles de Gabbel se traducen al español.

## El mapa de origen

- Mundo 1.16.4 (DataVersion 2584). Solo bloques vanilla: tiene un `fabricRegistry.dat` y un `forge-server.toml`, pero ningún bloque de mod.
- Isla en un lago. El agua va de y=4 a y=29 (superficie en y=29). La isla ocupa x −358…−26, z 351…713 (coordenadas originales).
- El castillo va de x −270 a −80 y de z 405 a 645, con plantas desde y≈26 (mazmorras) hasta y≈228 (torres). Lo más alto llega a y=254.
- Contenido: unas 400 salas con cartel (698 carteles con texto), 1.930 estandartes, 867 cabezas, 190 cuadros, 79 marcos, 48 soportes de armadura, 278 bloques de comandos y unas 1.500 luces.
- Licencia: Gabbel permite usarlo en servidores siempre que se le dé crédito. Va un cartel de créditos en el andén 9¾.

## Arquitectura

```
mundo 1.16.4 ──► tools/hogwarts (Python) ──► src/main/resources/maps/hogwarts.schem
                                         ├─► maps/hogwarts/extra.json.gz  (carteles, estandartes, cabezas, cuadros)
                                         └─► maps/hogwarts/secciones.json (derrumbes, páginas, pilas, spawns, salidas,
                                                                           notas, sonidos, luces y límites por sección)
plugin: HogwartsBuilder(sección) × 7 ──► pega el schem, aplica extra, sella la sección, coloca su contenido y devuelve su ArenaMap
```

### Herramienta de conversión (`tools/hogwarts/`, Python 3 + numpy + nbtlib)

1. **Lectura**: regiones Anvil 1.16 (paleta por sección y BlockStates empaquetados sin cruzar longs). Recorte: la isla más un anillo de lago de 25 bloques, con todas las alturas de y=0 a y=254.
2. **Lago**:
   - se queda el fondo original;
   - en el borde del recorte va una pared de `barrier` de y=0 a y=30 para que el agua no se derrame al vacío;
   - por debajo de y=0 no hay nada, y el fondo ya es `bedrock`/piedra.
3. **Bloques 1.16.4 → 26.3**:
   - una tabla de renombres (`grass_path` → `dirt_path` y los que salgan);
   - una normalización de propiedades.
   Todos los estados se validan contra la lista de bloques de la API de Paper 26.3. Un estado desconocido corta la conversión con un error que dice dónde está.
4. **Limpieza**:
   - **Fuera**: bloques de comandos (los 3 tipos), spawners, `end_gateway`, `structure_block` y todas las entidades salvo los cuadros.
   - **Mecanismos**: se desconectan (fuera `redstone_wire`, antorchas de redstone, repetidores, comparadores y observadores). Pistones, puertas y trampillas se quedan en el estado en que estén.
   - **Fuego**: solo se queda sobre `netherrack` y bloques de alma (chimeneas).
5. **Luces**:
   - Unas 1.500 fuentes de luz (faroles, antorchas, piedra luminosa, linternas de mar, varas del End, calabazas iluminadas…). Se queda ~10 %; el resto pasa a un bloque parecido sin luz (tabla en la herramienta) o a aire en el caso de antorchas y faroles.
   - En unos 8–12 sitios clave por sección se ponen bloques que el plugin puede encender y apagar (velas o lámparas de redstone) como `MapLight` que parpadea. Las velas flotantes del Gran Comedor van aquí.
6. **Datos extra** (`extra.json.gz`), en coordenadas relativas al schem:
   - carteles: texto traducido, color y si brilla;
   - estandartes: patrones convertidos a los ids de 26.3;
   - cabezas: textura;
   - cuadros: posición, orientación y variante.
7. **Traducción de carteles**: tabla `carteles_es.json` (texto original de 4 líneas → 4 líneas en español de ≤15 caracteres cada una). Se hace a mano sobre los textos únicos. La herramienta falla si queda alguno sin traducir.
8. **Schematic**: Sponge v3, gzip, DataVersion 5023 (26.3) y Offset 0. Mismo formato que `instituto.schem`, que ya lee `SchematicReader`.
9. **Secciones** (`secciones.json`): ver más abajo. La herramienta genera los candidatos y la verificación; la elección final de páginas, salidas, etc. se hace a mano en ficheros de datos de la herramienta.

### Coordenadas en el mundo `slender`

- Origen de cada copia: x = 1000·i (i = 0…6, en el orden de la tabla de secciones), y = 64, z = 6000. Es una fila nueva, lejos de los mapas actuales (z = 0, x = 0…6000, radio ≤ 232) y de x = 8000, donde el editor recomienda hacer mapas propios.
- Altura: y_mundo = y_original + 30. El fondo del lago queda en 30, la superficie del agua en 59 y lo más alto en 284 (el límite es 319).
- La limpieza antes de construir cubre la caja del schem entera (≈ 383 × 255 × 413), no el cubo fijo de −12…+60 que usan los demás mapas.

## Las 7 secciones

| # | id | Nombre | Contenido principal | Altura original |
|---|---|---|---|---|
| 1 | `hw_mazmorras` | Mazmorras | Pociones, despacho de Snape, sala común de Slytherin, mazmorras 1–6, Estudios de Necrófagos, sala de calderas, túneles bajo el lago y cámara redonda | 26–65 |
| 2 | `hw_comedor` | Gran Comedor | Gran Comedor, vestíbulo, cocinas y despensas, edificio del patio del reloj, Historia de la Magia, Aparición, sala de profesores | 66–131 |
| 3 | `hw_ala_este` | Ala Este | Música, Estudios Muggles, salas de Lockhart, DCAO, Alquimia, club de Gobstones, salas del silencio y del ruido | 66–131 |
| 4 | `hw_patio` | Patio de Transformaciones | Patio, biblioteca y sección prohibida, sala de trofeos, Gran Escalera, Filosofía, club de ajedrez, tienda de los Weasley | 66–105 |
| 5 | `hw_aulas` | Aulas altas | Encantamientos, enfermería, sala de duelos, Aritmancia, galería de criaturas, sala de las campanas | 106–131 |
| 6 | `hw_torres` | Torres | Despacho del director, torre de Gryffindor con dormitorios, Astronomía, Adivinación, lechucería, despacho de Filch | 132–228 |
| 7 | `hw_terrenos` | Terrenos | Isla al aire libre, invernaderos, patios exteriores, puente cubierto, orilla del lago | exterior, ≤ 95 |

### Cómo se define una sección

- En `secciones.json`, cada sección es la **unión de cajas 3D** (x, y, z en coordenadas originales). Una celda por la que se puede andar pertenece a la sección si está dentro de alguna de sus cajas. Las de las secciones 1–6 son interiores (tienen techo); las de la 7 son exteriores.
- Una celda "se puede andar" si tiene los pies y la cabeza libres (aire o algo sin colisión) y suelo debajo. Losas inferiores y escaleras cuentan como escalón. Escaleras de mano, enredaderas y agua dejan subir y bajar.
- **Derrumbes**: cada celda por la que se puede andar o pasar y que, estando fuera de la sección, toca una celda de la sección es un hueco hacia fuera, y se rellena con derrumbe. Valen puertas, escaleras, pasadizos y puertas al exterior (en las secciones 1–6). El derrumbe mezcla `cobblestone`, `mossy_cobblestone`, `cracked_stone_bricks`, `stone_bricks` y `cobweb` (solo en la fila de arriba), con semilla fija. Nada con gravedad. Las ventanas no se tocan.
- En Terrenos, las puertas del castillo se tapan igual: con derrumbe o con rejas (`iron_bars`) si son grandes.
- **Verificación** (test de la herramienta): flood fill de celdas andables desde cada spawn, con los derrumbes puestos. No puede salir de la sección, y tiene que llegar a todas las páginas, todas las pilas y la salida. Si el área conectada es menos del 60 % de las celdas de la sección, avisa.

### Contenido de cada sección

- **Spawns**: 3–4 en una sala amplia y céntrica de la sección.
- **Páginas**: 12–16 sitios en paredes de salas con nombre. Salen 8 por partida, como en todos los mapas (`game.pages`). Los candidatos los genera la herramienta: pared maciza mirando a una celda andable, dentro de una sala con cartel. Se eligen a mano para repartirlos por toda la sección y por varias plantas.
- **Pilas**: unas 10 por sección en celdas andables (salen `batteries-per-game` por partida).
- **Tablón** junto al spawn con las salas de la sección, su dirección y la distancia (`Decor.board`).
- **Salida** (`gate` + zona de salida). Los bloques del `gate` se ponen al empezar y se quitan al tener las páginas, como ahora. Se ajusta al construir según el recorrido real; la idea de partida:

  | Sección | Salida |
  |---|---|
  | Mazmorras | túnel bajo el lago hasta una boca en la orilla |
  | Gran Comedor | el portón del vestíbulo, que da a los terrenos |
  | Ala Este | el puente cubierto |
  | Patio de Transformaciones | la puerta del patio hacia el viaducto |
  | Aulas altas | una escalera derrumbada que se despeja y baja al exterior |
  | Torres | lo alto de la Torre de Astronomía (escoba) |
  | Terrenos | el embarcadero |

  La zona de salida queda fuera de los límites, igual que en el instituto. El tiempo para huir se calcula solo (`EscapeTime`).
- **Límites**: el rectángulo x/z de la sección más 3 bloques, y `fallY` por debajo de su planta más baja. La contención en altura la hacen los derrumbes.
- **Notas — el diario de Filch**:
  - 2–3 notas por sección, unas 18 en total, con texto original en español (no se copia nada de los libros).
  - Cada sección se entiende sola. Leídas en el orden de las secciones (1→7), cuentan la historia completa: la ronda de noche de Filch, el castillo vaciándose, la Señora Norris y el hombre sin cara. El orden queda guardado en `secciones.json` para el modo historia.
  - Van como carteles o atriles con el mismo sistema que las otras notas (`Decor.notes`).
- **Frases al coger páginas**: de Filch, unas 10 por sección (`Ambience.phrases`).
- **Sonidos por zona** (`SoundZone`), 4–8 por sección:
  - Mazmorras: goteo y cadenas.
  - Gran Comedor: eco y cubiertos.
  - Ala Este: un gramófono y la sala del ruido.
  - Patio: viento y pasos.
  - Aulas altas: las campanas y la enfermería.
  - Torres: lechuzas y viento fuerte.
  - Terrenos: agua y ranas.
- **Luces que parpadean** (`MapLight`): las 8–12 de la conversión que caen en la sección.
- **Ambiente**: noche, sin mobs y la niebla del juego, como los otros mapas.

## Andén 9¾ (estación de Valdemora)

Coordenadas relativas al origen de la estación (−2000, 64, 0). Va en el patio al norte del andén 1 y al este de la taquilla (x 0…34, z −24…−4).

- **Hueco en la valla** del andén 1 (z = −3), de 3 de ancho, frente al muro.
- **Muro 9¾**:
  - ladrillo (`bricks`) con pilares de piedra, de ~11 de ancho y 6 de alto;
  - cartel "9¾" y otro de "ANDÉN 9¾ — HOGWARTS";
  - en el centro, 3×3 de aire con `block_display` de ladrillo encima: en Java parece pared y se atraviesa; en Bedrock se ve un hueco.
  - Los displays llevan una etiqueta de scoreboard (`slender_hub`). Se quitan y se vuelven a poner cada vez que se construye la estación.
- **Andén escondido**: suelo de piedra, farolas (pocas y de luz débil), vías y bancos.
- **Expreso de Hogwarts** sobre las vías: locomotora negra y roja con chimenea y 3 vagones rojos. Tiene 8 puertas:
  - 7 son puertas de la estación (`Hub.doors`) a `hw_mazmorras` … `hw_terrenos`, cada una con su cartel;
  - la 8.ª está tapiada, con el cartel "LA NOCHE ENTERA — próximamente", y no es puerta todavía.
- **Créditos**: "Castillo de Hogwarts por Gabbel — v1.1.0 — planetminecraft".
- **Cartel en la taquilla**: "DESAPARECIDO — Argus Filch — conserje de Hogwarts — ronda de noche".
- Los 6 arcos de ahora (BOSQUE, SANATORIO, MAIZAL, INSTITUTO y los dos ???) no cambian.

## Cambios en el plugin (1.8.0)

1. **Recuperar el código de la 1.7.0**:
   - descompilar `Slender.jar` 1.7.0 (Procyon) y rehacer a mano `GameManager.join`, que no se descompila;
   - arreglar lo que no compile y volver a dejar los recursos (`config.yml`, `plugin.yml` y `maps/`);
   - los tests de la 1.0 que sigan siendo válidos se adaptan y deben pasar.
   El proyecto queda en `IdeaProjects\slender\codigo-1.8\`, con `build.sh` y la carpeta `libs`.
2. **Datos extra de schematic**: `map/schem/SchematicExtras` lee `extra.json.gz`. Después de pegar los bloques, `Builder`/`BlockQueue` aplica carteles (`Sign` lado delantero), estandartes (`Banner#setPatterns`), cabezas (`Skull#setPlayerProfile` con la textura) y cuadros (`Painting` con variante y orientación). Si algo falla, se registra y se sigue.
3. **Origen y limpieza por mapa**: métodos por defecto en `MapBuilder` (`origin()` y `clearBox()`), de modo que los mapas actuales no cambian. `BuildManager.originOf` y `build()` los usan.
4. **Bloques inválidos**: `Builder.data(String)` devuelve `stone` y registra un aviso una sola vez por estado, en vez de lanzar una excepción.
5. **`HogwartsBuilder`** (uno por sección, con el mismo código parametrizado) y **`HogwartsSections`**, que lee `secciones.json`:
   - pega `hogwarts.schem` y los extras;
   - aplica los derrumbes de su sección y coloca tablón, notas, `gate` y luces;
   - devuelve el `ArenaMap` con spawns, páginas, pilas, salida, límites y ambiente.
6. **Comandos**:
   - `/slender build hogwarts` construye las 7 secciones una detrás de otra;
   - `/slender build hw_<sección>` construye solo una;
   - `build all` también las incluye;
   - `hogwarts` pasa a ser un id reservado en `MapIds`.
7. **Estación**: `HubBuilder` añade el andén 9¾, las 7 puertas nuevas en el `Hub`, el cartel de Filch y los `block_display` del muro.
8. **Menú de la brújula**: las 7 secciones salen con un icono propio (por ejemplo `CAULDRON`, `CAKE`, `JUKEBOX`, `BOOKSHELF`, `BELL`, `SPYGLASS`, `OAK_BOAT`) y el nombre "Hogwarts: <sección>".
9. `plugin.yml` 1.8.0 e `INSTALACION.md` con un apartado "Novedades 1.8".

## Compilación

- Hace falta la API de Paper 26.3 y sus dependencias. El usuario copia la carpeta `libraries` del servidor (`~/minecraft-26.3/libraries`) a `IdeaProjects\slender\libs-servidor` con `scp -r`. Desde aquí no se llega a repo.papermc.io.
- JDK 25 y `build.sh` como hasta ahora: compila, pasa los tests y genera `Slender.jar`.

## Pruebas

- **Herramienta (pytest)**:
  - decodificación de BlockStates 1.16 (sección conocida → bloques esperados);
  - tabla de renombres;
  - todos los estados de la paleta existen en 26.3;
  - todos los carteles tienen traducción de ≤15 caracteres por línea;
  - ida y vuelta del schem (dimensiones, paleta y marcadores);
  - cierre de cada sección (el flood fill descrito arriba);
  - cada sección tiene ≥12 páginas, ≥8 pilas, ≥3 spawns y una salida a la que se llega.
- **Plugin (JUnit)**: lectura de `secciones.json` y `extra.json.gz`, desplazamiento de coordenadas, `origin()`/`clearBox()` por defecto (los mapas viejos no cambian) y `MapIds` con `hogwarts` reservado. Los tests de la 1.7 pasan.
- **En el servidor** (lista para el usuario): `/slender build hogwarts` sin errores en la consola; jugar cada sección en coop (páginas, pilas, salida y que no se pueda salir de la sección); y atravesar el andén 9¾ desde Java y desde Bedrock.

## Entrega

- `IdeaProjects\slender\Slender.jar` (1.8.0), `INSTALACION.md` actualizado y `codigo-1.8\` con el código y `tools/hogwarts/`.
- En el proyecto: esta spec, el plan de implementación y una nota de entrega (`hogwarts/entrega.md`).

## Fuera de alcance

- El modo historia (subproyecto 2): fases, mejoras de Slender, revivir y el mapa que junta las secciones.
- Marcos con mapas (79), soportes de armadura (48), los cofres de botín y los puzzles de Gabbel.
- Hacer que vuelvan a funcionar los mecanismos de redstone o de comandos.
- Traducir los libros de los atriles (se quitan).

## Riesgos

- **Tiempo de construcción**: 7 copias de ~2–3 M de bloques a 2.000 bloques por tick son ~15–20 min en total. Se hace una sola vez y sin nadie jugando.
- **Estados de bloque 1.16 → 26.3** que no estén en la tabla: la conversión falla y lo dice, y el plugin tiene como red de seguridad poner piedra con un aviso.
- **Bedrock** no ve los `block_display`: en el muro 9¾ verá un hueco. Aceptado.
- **Salidas**: las de la tabla son la idea de partida. Si alguna no tiene un recorrido limpio, se cambia por otra salida temática de la misma sección y se apunta en la nota de entrega.
