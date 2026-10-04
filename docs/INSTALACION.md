# Instalar Slender (v1.8.0)

1. Copia `Slender.jar` a la carpeta `plugins/` del servidor (desde tu PC):
   `scp C:\Users\kivi2\IdeaProjects\slender\Slender.jar USUARIO@IP:RUTA_DEL_SERVIDOR/plugins/`
2. Reinicia el servidor (no uses /reload). `/version Slender` debe decir **1.8.0**.
3. No hace falta importar `slender` en Multiverse: el plugin crea y carga el mundo él solo en cada arranque.
   (Si aun así lo quieres en Multiverse: `/mv import slender normal --generator Slender --skip-folder-check`,
   pero entonces Multiverse puede forzar modo supervivencia en ese mundo.)
4. (Opcional) Grupo propio de Multiverse-Inventories para `slender`:
   `/mvinv create-group slender slender all`
   El plugin ya guarda y devuelve tu inventario al entrar y salir, así que esto es solo una capa extra.
5. Skin de Slender (funciona aunque el servidor sea offline / AuthMe):
   1. Con una cuenta de Java **comprada** (tuya o de un amigo), en el launcher oficial → Skins → añade
      `el-hueco.png` (modelo clásico) y póntela.
   2. En el servidor (o en consola): `/slender skin <nombre de esa cuenta>`.
   3. Esa cuenta ya puede volver a su skin de siempre: Slender se queda con El Hueco.
   - Otras formas: entrar desde Bedrock con la skin puesta y escribir `/slender skin` sin nombre,
     o https://mineskin.org pegando *value* y *signature* en `slender.skin` del config.
   Sin skin, Slender es un Enderman.
6. Genera el vestíbulo y los mapas (uno detrás de otro): `/slender build all`
   (solo el vestíbulo: `/slender build vestibulo`). Con las 7 secciones de Hogwarts, `all` tarda unos 25 min.

## Novedades 1.8: Hogwarts

Para tenerlo: cambia el jar, reinicia y usa `/slender build hogwarts`, que construye las 7 secciones una detrás de
otra (los otros mapas no se tocan). Cada sección es una copia entera del castillo, así que tarda **unos 15–20 min
en total** (el progreso sale en el chat): hazlo sin nadie jugando. Una sola sección: `/slender build hw_patio`
(o el id que sea). Después, `/slender build vestibulo` sin nadie en la estación, para que salga el andén 9¾.

- **El castillo de Hogwarts de Gabbel**, partido en 7 mapas. Cada uno es una parte del castillo con sus 8 páginas;
  el resto queda cerrado con derrumbes de piedra y adoquín que no se pueden cruzar.

  | Puerta | id | Sección | Qué hay |
  |---|---|---|---|
  | 1 | `hw_mazmorras` | Mazmorras | Pociones, sala común de Slytherin, mazmorras y túneles bajo el lago |
  | 2 | `hw_comedor` | Gran Comedor | Gran Comedor, cocinas, patio del reloj y sala de Aparición |
  | 3 | `hw_ala_este` | Ala Este | Música, Estudios Muggles, aula de Lockhart y el gramófono |
  | 4 | `hw_patio` | Patio de Transformaciones | Patio, Gran Escalera, baño de Myrtle y Sortilegios Weasley |
  | 5 | `hw_aulas` | Aulas altas | Sala de duelos, enfermería, galería de criaturas y las campanas |
  | 6 | `hw_torres` | Torres | Sala común y dormitorios de Gryffindor, sala de trofeos y la torre cuadrada |
  | 7 | `hw_terrenos` | Terrenos | La isla por fuera: invernaderos, murallas, lechucería y la orilla del lago |

- **Salidas:** Mazmorras, por el túnel al lago; Gran Comedor, por la puerta este del patio del reloj; Ala Este,
  por la puerta del puente; Patio, por la puerta este; Aulas altas, por una escalera hundida; Torres, por la
  puerta al tejado de la torre cuadrada; Terrenos, por la cueva del lago bajo el castillo. El tiempo para huir se
  alarga solo si la salida queda lejos, como en el instituto.
- **Orientarse:** en todas menos Mazmorras y Terrenos hay un tablón de lugares junto al spawn. Los carteles del
  castillo están traducidos al español; los estandartes, cabezas y cuadros salen como en el original.
- **Ambiente:** casi todo a oscuras (solo queda encendida 1 de cada 10 luces) y luces que parpadean. Goteo y
  cadenas en las mazmorras, eco en el Gran Comedor, el gramófono del Ala Este, campanas, lechuzas, viento, agua…
- **Historia:** las notas son el diario de **Argus Filch**, el conserje, que hacía la ronda de noche y no volvió.
  Su cartel de DESAPARECIDO está en la sala de espera de la estación, al lado del de Xosé.
- **El andén 9¾:** en el patio de la estación, al norte del andén 1, hay un hueco en la valla frente a un muro de
  ladrillo con el cartel **9¾**. Cruza el muro por el centro:
  - desde **Java** se ve un muro entero y se atraviesa;
  - desde **Bedrock** se ve un hueco en el muro (Bedrock no dibuja esos ladrillos), pero se cruza igual.

  Detrás está el **Expreso de Hogwarts**: cada puerta lleva a su sección (métete en el hueco de la puerta).
  La 8.ª puerta está tapiada: **LA NOCHE ENTERA**, el modo historia que viene después.
- **Créditos:** el castillo es *Hogwarts Castle v1.1.0* de **Gabbel** (planetminecraft); hay un cartel con los
  créditos en el andén 9¾. Si lo enseñas fuera del servidor, cítalo.

## Novedades 1.7: el instituto

Para tenerlo: cambia el jar, reinicia y usa `/slender build instituto` (los otros mapas no se tocan). Es el mapa
más grande: tarda más que los otros (el progreso sale en el chat) y conviene hacerlo sin nadie jugando. Después,
`/slender build vestibulo` sin nadie en la estación, para que salgan el arco **INSTITUTO** (el que era ??? al lado
del MAIZAL) y el cartel de Xosé en la sala de espera. (`/slender build all` también lo hace, pero regenera
todos los mapas y se pierden los retoques que les hayas hecho con el editor.)

- **El IES Lagoa de Antela** de tu schematic, tal cual: edificios A, B y C, polideportivo, pistas cubiertas,
  pista roja, invernadero, aparcamiento y ruinas. De noche y sin mobs.
- **Por dentro:** la planta baja y la 1ª de A, B y C están amuebladas, con una escalera en cada edificio.
  - **A:** conserjería, dirección, sala de profesores, cafetería y aulas; arriba, biblioteca, laboratorio y aulas.
  - **B:** comedor y cocina, aulas y taquillas; arriba, música, plástica y aulas.
  - **C:** secretaría, archivo, salón de actos, talleres y aulas; arriba, informática y aulas.
  - **Polideportivo:** pista con porterías, gradas y vestuarios.
  - Carteles en las puertas de las salas y fluorescentes que parpadean. Las plantas 2ª y 3ª siguen cerradas.
- **Fuera:** una valla rodea el recinto (el prado del este se ve, pero no se llega), farolas que parpadean en la
  avenida y los aparcamientos, la parada del bus, un coche abandonado y las canastas.
- **La salida:** al final de la avenida, al sur, hay una barrera de obras roja. Se abre al tener las páginas y
  detrás se sigue por la carretera hasta escapar. Como queda lejos de casi todo, aquí hay **unos 2 min 40 s**
  para huir en vez de 60 s. Va solo: en cualquier mapa con páginas muy lejos de la salida (también los tuyos del
  editor) se alarga lo justo; en el bosque, el sanatorio y el maizal sigue siendo 1 minuto.
- **Orientarse:** el faro (antena con luz que parpadea en el tejado de A), postes con flechas en los cruces y el
  tablón con los 12 lugares junto al spawn, en el patio entre A y B.
- **Historia:** las notas son de Xosé Lamas, el conserje de noche que desapareció en su ronda, y hablan de la
  Lagoa de Antela: la laguna desecada y la ciudad que dicen que duerme debajo. 20 sitios de página (salen 8 por
  partida).
- **Ambiente:** el timbre que suena solo, portazos, un balón en el polideportivo, ranas y agua junto a las
  ruinas, susurros en la biblioteca, viento, el zumbido del faro…
- Si habías puesto un mapa tuyo en ese arco libre, ahora es el del instituto: pon su puerta en el otro arco ???.

## Novedades 1.6.1: niebla en la estación

- En el andén hay niebla a lo lejos (los extremos de las vías y los túneles se pierden en gris) y una bruma
  baja de humo sobre el andén y las vías. Al salir de la estación vuelves a ver a tu distancia de siempre.
- Se ajusta en `config.yml` (sección `vestibulo`, se añade sola al arrancar la 1.6.1):
  - `niebla: 2` → distancia de visión en la estación en chunks: 2 es la más espesa, 3–4 más suave,
    0 sin niebla.
  - `bruma: true` → `false` para quitar el humo bajo.
  - Después de cambiarlo: `/slender reload` (la niebla se aplica al volver a entrar en la estación).
- No hace falta regenerar nada para la niebla.

## Novedades 1.6: el maizal nuevo

Para tenerlo: cambia el jar, reinicia y usa `/slender build maizal` (solo el maizal; los otros mapas y la
estación no se tocan). El progreso sale en el chat.

- **El laberinto de maíz** en el centro, sobre un campo ondulado (el maíz sube y baja con el terreno). Lo
  rodea un camino de tierra y se entra por los cuatro lados. Dentro hay claros: un **círculo aplastado** con un
  obelisco negro en medio, una **cosechadora** parada y cuatro **espantapájaros** en postes altos con farol, que
  asoman por encima del maíz y sirven para orientarse.
- **La granja de los Roldán**, al norte:
  - **Casa de dos plantas**: cocina, salón, y arriba el cuarto de los niños, el baño y el de los padres.
    Porche delante y tejado a dos aguas.
  - **Granero** con establos y un **pajar** al que se sube por una escalera.
  - Silo, gallinero, pozo y letrina.
- **Referencias altas:** el **molino** en un cerro al oeste (su luz parpadea; es el faro del mapa), el
  depósito de agua y el silo. Los **postes de la luz** del camino también parpadean.
- **Más sitios:** huerto de calabazas al este y el **arroyo** al sur, que se cruza por un puente con faroles.
- **Orientarse:** postes con flechas en los cruces del camino y el tablón de la entrada con los 12 lugares.
- **Historia:** las notas cuentan qué le pasó a la familia Roldán (los otros desaparecidos de los carteles de
  la estación). 18 sitios de página (salen 8 por partida).
- **Ambiente:** el maíz cruje, aleteos, cadenas en el molino, gallinas inquietas, agua junto al puente,
  viento, un zumbido en el círculo…
- Si habías retocado el maizal viejo con el editor, esos cambios se pierden al regenerarlo.

## Novedades 1.5: el sanatorio nuevo

Para tenerlo: cambia el jar, reinicia y usa `/slender build sanatorio` (solo el sanatorio; el bosque y la
estación no se tocan). Tarda un poco: el progreso sale en el chat.

- **En lo alto de una colina:** una carretera en curvas sube desde la entrada hasta la verja del recinto, que
  tiene un muro de piedra con algunos tramos caídos (por ahí se sale al bosque seco que lo rodea).
- **Un edificio de verdad, con tres pabellones**, dos plantas y sótano:
  - **Vestíbulo** con la escalera grande y la recepción; encima, la **capilla** y el **campanario**, con una
    luz que parpadea (sirve de referencia).
  - **Pabellón A** (este, franja **azul**): habitaciones de pacientes en las dos plantas.
  - **Pabellón B** (oeste, franja **roja**): quirófano y farmacia; desde allí se baja a la **morgue**.
  - **Pabellón C** (norte, franja **verde**): comedor y cocina; arriba, dormitorios con el pasillo derrumbado.
  - **Sótano:** túneles, la morgue y la sala de calderas medio inundada.
  - Sobre cada puerta del vestíbulo hay un cartel "PABELLÓN A/B/C" que dice qué hay.
- **Fuera:** patio con la fuente seca y el jardín muerto, invernadero en ruinas, una ambulancia abandonada
  (con el rotativo parpadeando), la portería junto a la verja y el cementerio detrás del edificio.
- **Historia:** las notas son de Lucía Abad, la enfermera de noche (otra de las desaparecidas de los carteles
  de la estación); las frases al coger páginas también son de allí. 16 sitios de página (salen 8 por partida).
- **Ambiente:** fluorescentes que parpadean en los pasillos, goteo en el sótano, puertas metálicas a lo lejos,
  susurros en la capilla, la campana que suena sola…
- **Slender y las plantas:** ahora aparece en la planta en la que estás (antes, en edificios de varias plantas,
  podía acabar en el tejado). Vale para todos los mapas.
- Si habías retocado el sanatorio viejo con el editor, esos cambios se pierden al regenerarlo.

## Novedades 1.4: el bosque nuevo

Para tenerlo: cambia el jar, reinicia y usa `/slender build bosque` (solo el bosque; la estación y sus arcos
no se tocan). Tarda algo más que antes porque ahora hay relieve; el progreso sale en el chat.

- **Relieve de verdad:** colinas, un arroyo que cruza el bosque de norte a sur con dos puentes y un lago al
  sureste con embarcadero. Zonas distintas: pinar oscuro al norte, zona quemada al oeste, abedules muertos al
  este y ciénaga junto al lago.
- **Doce lugares** unidos por un camino en anillo: torre de radio (en lo alto, con una luz roja que parpadea:
  sirve de referencia), cabaña del guardabosques, campamento, capilla con cementerio, círculo de piedras,
  caravana, puesto de caza, refugio de piedra, coche accidentado, pozo, boca del túnel del tren y embarcadero.
- **Orientarse:** postes con flechas en los cruces y un tablón junto a la salida con todos los lugares, su
  dirección y la distancia.
- **Historia:** cada lugar tiene una nota del diario de Íñigo Varela (el de los carteles de la estación) y las
  frases al coger páginas son suyas. Hay 16 sitios de página (salen 8 por partida).
- **Ambiente por zonas:** agua junto al arroyo y el lago, estática junto a la torre, crujidos en la cabaña,
  campana en la capilla, gruñidos y truenos en el pinar… La lámpara de la cabaña y el faro del coche
  parpadean durante la partida.
- Si habías retocado el bosque viejo con el editor, esos cambios se pierden al regenerarlo.

## Jugar

- `/slender` → vas a la **estación de Valdemora** (tu inventario se guarda). En el inventario tienes una
  **brújula** (elegir mapa) y una **puerta** (volver a casa).
- Cruza las vías y entra en un **arco**: BOSQUE, SANATORIO, MAIZAL o INSTITUTO. Si la partida está vacía eliges modo;
  si no, entras directamente. Se espera en la estación hasta que empieza.
- Al acabar la partida vuelves a la estación. Para irte a casa: el pasillo negro de **SALIDA** al fondo de la
  estación, la puerta del inventario o `/slender leave`. Si te desconectas, recuperas todo al volver a entrar.
- La linterna dura ahora unos 8 minutos y cada pila da 40 %. Si tu `config.yml` tenía los valores antiguos
  (0.6667 y 35) se cambian solos al arrancar; si los habías tocado a mano, se respetan.

## Hacer tu propio mapa (editor)

Todo dentro del mundo `slender`, con permiso de admin. Construye el mapa como quieras (a mano, WorldEdit o
un schematic descargado) en una zona libre, lejos de los mapas generados (por ejemplo x = 8000) y vete allí
con `/mv tp slender` o en creativo.

1. `/slender map create cripta La cripta` — crea el mapa donde estás (límites ±60 a tu alrededor).
2. `/slender map cripta spawn add` — en cada sitio donde quieras que aparezcan los jugadores (mirando hacia
   donde deben mirar).
3. `/slender map cripta page add Altar` — mirando a una pared: ahí se colgará una página. Hacen falta al
   menos 8 (mejor 12-16, se eligen al azar). `page remove` quita la más cercana.
4. `/slender map cripta battery add` — a tus pies, donde pueden salir pilas.
5. `/slender map cripta gate add` — mirando a los bloques que tapan la salida (se abren al tener las páginas).
6. `/slender map cripta exit pos1` y `exit pos2` — dos esquinas de la zona de salida (donde se escapa).
7. Si el mapa es más grande que ±60: `bounds pos1` y `bounds pos2` en dos esquinas opuestas.
8. En la estación, dentro de un arco libre (los que dicen ???): `/slender map cripta door pos1` en una
   esquina del suelo del arco y `door pos2` en la otra.
   El cartel ??? lo puedes cambiar en creativo: rómpelo y pon otro con el nombre de tu mapa.
9. `/slender map cripta check` — te dice lo que falta. `show` enseña con partículas lo que has puesto.
10. Pruébalo: `/slender join cripta coop`.

Otros: `/slender map list`, `/slender map cripta name <nombre>`, `/slender map cripta delete confirm`.
Para cambiar la estación a mano: `/slender hub set` (punto de llegada) y `/slender hub exit pos1|pos2`.
Los mapas generados también se pueden retocar con el editor (mover páginas, etc.); si vuelves a hacer
`/slender build <mapa>` se pierden esos cambios.
