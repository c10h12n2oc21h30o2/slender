# LA NOCHE ENTERA: modo historia de Hogwarts (proyecto A)

Fecha: 2026-10-05 · Versión objetivo: Slender **1.9.0** · Base: `main` en 1.8.1

## 1. Objetivo

Un modo cooperativo largo que recorre las 7 secciones de Hogwarts como 7 fases seguidas:

- Slender gana poder y habilidades en cada fase.
- Los jugadores pueden revivirse al estilo REPO, llevando la cabeza del caído.
- Se entra por la 8.ª puerta del Expreso, en el andén 9¾.
- Entre fases se pasa por una sala donde se guarda el progreso.

**Éxito:** un grupo de 1 a 8 jugadores juega una noche de unos 75–90 min (con 2–3 jugadores), puede dejarla a medias y seguir otro día, y la dificultad sube de forma que se nota.

**Proyecto A (esta spec):**
- 7 fases encadenadas;
- páginas según jugadores;
- poder de Slender y 6 habilidades nuevas;
- cabezas, caldero, salida y perdidos;
- velas, misión de la noche y la Snitch;
- la Sala de los Menesteres;
- guardado, historia, comandos de admin.

**Proyecto B (fuera de esta spec):**
- clases con kit, objetos (Chocolate, Varita Lumos Máxima, Mapa del Merodeador, Capa de invisibilidad, Poción multijugos), recompensas al pasar de fase y mejoras de objetos;
- reajustar a Slender para esos objetos.

**Alcance del modo:** todo lo de esta spec solo actúa en LA NOCHE ENTERA. Las partidas sueltas de cualquier mapa se quedan como están.

## 2. Decisiones del usuario (resumen)

| Tema | Decisión |
|---|---|
| Revivir | Las dos vías: cabeza al caldero de la fase (revive ya) y cabeza por la salida (vuelve en la fase siguiente) |
| Duración | Rampa de páginas 3→8 por fase, multiplicada según el número de jugadores |
| Derrota | 2 velas; si caen todos se gasta una y se repite la fase; sin velas se acaba la noche. Velas extra: misión que varía + easter egg |
| Habilidades nuevas | Apagar luces y linternas, oír correr (solo tras correr mucho rato seguido), cazar cabezas, fogonazo (flashbang), hacerse pasar por un jugador, perseguir |
| Unir fases | Encadenado: cada fase en su copia de la sección, con la Sala de los Menesteres entre fases |
| Guardar | Sí, por fases |
| Vela extra | Misión al azar por noche + easter egg secreto (máx. +2) |
| Proyectos | A (esta spec) y luego B (objetos y clases) |

## 3. Glosario

- **Noche:** una partida completa del modo, de la fase 1 a la 7.
- **Fase:** una partida sobre una sección de Hogwarts con las reglas de la noche.
- **Miembro:** jugador apuntado a la noche (guardado en la partida).
- **Cabeza:** objeto `player_head` con la skin del caído que deja al morir.
- **Perdido:** miembro muerto cuya cabeza no se recuperó en la fase; se salta la fase siguiente.
- **Vela:** reintento de fase.
- **Nivel `t`:** número de la fase en el orden de la historia (1–7).

## 4. Flujo de una noche

1. **Entrada.** La 8.ª puerta del Expreso deja de estar tapiada. Su zona tiene el id reservado `noche` y abre el **menú de la noche**:
   - **Nueva noche.**
   - **Continuar (fase N, X velas)**, si el jugador es miembro de una noche guardada.
   - **Unirse**, si hay una noche esperando en la sala.
2. **Sala de los Menesteres** (§9). Los jugadores aparecen allí. La noche está *esperando*.
3. **Listos.** Cuando todos los miembros presentes y no perdidos están dentro del **círculo de listos**, hay una cuenta atrás de 10 s y empieza la fase. Si al menos uno está en el círculo durante 30 s seguidos, también empieza con los que haya en la sala.
4. **Fase.** Todos los miembros presentes van a la sección de la fase:
   - los vivos, como supervivientes;
   - los perdidos, como espectadores.
   Título: "Fase N: <nombre>" y la entrada del diario (§10).
5. **Fin de fase:**
   - **Victoria** (al menos 1 escapa): todos a la sala; se calculan rescatados y perdidos (§7); fase + 1; se guarda.
   - **Derrota** (nadie escapa, porque mueren todos o se acaba el tiempo):
     - con velas, −1 vela, todos a la sala y se repite la misma fase con todos los miembros (sin perdidos);
     - sin velas, final malo, se borra la partida y vuelta a la estación.
6. **Tras ganar la fase 7:** epílogo (final bueno), se borra la partida y vuelta a la estación.

**Una sola noche activa a la vez en el servidor.** Si alguien intenta empezar otra, recibe "Ya hay una noche en marcha; únete desde la 8.ª puerta o espera".

## 5. Fases

### 5.1 Orden de la historia

| t | Sección | id |
|---|---|---|
| 1 | Terrenos | `hw_terrenos` |
| 2 | Gran Comedor | `hw_comedor` |
| 3 | Mazmorras | `hw_mazmorras` |
| 4 | Ala Este | `hw_ala_este` |
| 5 | Patio de Transformaciones | `hw_patio` |
| 6 | Aulas altas | `hw_aulas` |
| 7 | Torres | `hw_torres` |

### 5.2 Páginas

- `n` = miembros vivos (no perdidos) al empezar la fase.
- `base = [3, 4, 5, 5, 6, 7, 8]`.
- `factor(n)` = 0,75 si n = 1; 1,0 si n = 2–3; 1,25 si n = 4–5; 1,5 si n ≥ 6.
- `páginas(t, n) = clamp(redondeo_hacia_arriba_en_.5(base[t] · factor(n)), 2, min(12, sitios de la sección))`.

| n | t1 | t2 | t3 | t4 | t5 | t6 | t7 | total |
|---|---|---|---|---|---|---|---|---|
| 1 | 2 | 3 | 4 | 4 | 5 | 5 | 6 | 29 |
| 2–3 | 3 | 4 | 5 | 5 | 6 | 7 | 8 | 38 |
| 4–5 | 4 | 5 | 6 | 6 | 8 | 9 | 10 | 48 |
| 6+ | 5 | 6 | 8 | 8 | 9 | 11 | 12 | 59 |

### 5.3 Tiempo

- Tiempo de la fase: `180 + 60 · páginas` segundos.
- La huida, como en las partidas sueltas: `EscapeTime`, que se alarga si la salida está lejos.

### 5.4 Poder de Slender

- **Agresividad de la noche:**
  - `a = min(8, 8 · cogidas / total + ⌊s / 90⌋) + 0,5 · (t − 1)`, donde `s` son los segundos desde la última página.
  - En la huida: `a = 10 + 0,3 · (t − 1)`.
  - Las funciones de `DirectorMath` (intervalo y distancia de teletransporte, velocidad al acercarse, aparición de frente) ya limitan sus valores.
- **Minuto de gracia** (`dormant`): solo en t = 1.
- **Estática:** `lookRate` y `proximityRate` se multiplican por `1 + 0,08 · (t − 1)`.

### 5.5 Calendario de habilidades

| t | Nueva habilidad |
|---|---|
| 1 | — (Slender de siempre) |
| 2 | Apagar luces y linternas |
| 3 | Oír correr |
| 4 | Cazar cabezas |
| 5 | Fogonazo |
| 6 | Hacerse pasar por un jugador |
| 7 | Perseguir |

- Cada habilidad sigue activa en las fases siguientes.
- Su frecuencia se multiplica por `1 + 0,25 · (t − t_estreno)`, que acorta el enfriamiento o sube la probabilidad.
- **Bloqueo global:** solo puede haber en curso una de *suplantar* o *perseguir* a la vez. El resto son instantáneas.

Todos los números de §5 y §6 van en `config.yml`, apartado `noche:` (§12).

## 6. Habilidades nuevas de Slender (solo en la noche)

### Apagar luces y linternas (t ≥ 2)

- Al aparecer o teletransportarse, apaga las luces de la sección (bloques `Lightable` de `MapLight`) que tenga a ≤ 12 bloques durante 30–60 s (al azar). Mientras están apagadas no parpadean.
- Mientras un jugador lo mira (la misma comprobación que da estática) con la linterna encendida, la pila se gasta ×6.

### Oír correr (t ≥ 3)

- Por jugador: segundos corriendo seguidos. Si deja de correr, vuelve a 0.
- Umbral: `6 − 0,5 · (t − 3)` s (6 s en t3 y 4 s en t7).
- Al superarlo, el jugador queda **ruidoso**: el siguiente teletransporte escogido (aparición oculta) va a por él, a 0,6 × la distancia normal (mínimo 7). Recibe el susurro "noche-te-oigo".
- Al teletransportarse, el jugador deja de estar ruidoso.

### Cazar cabezas (t ≥ 4)

- Una cabeza en el suelo (entidad `Item`) sin ningún vivo a ≤ 8 bloques durante 20 s queda *expuesta*.
- Si Slender no está siendo observado, se teletransporta junto a ella. A los 2 s, si sigue sin vivos a ≤ 8, se la lleva:
  - la entidad desaparece;
  - todos oyen un sonido;
  - mensaje "noche-cabeza-robada";
  - el dueño queda **perdido** (§7).

### Fogonazo (t ≥ 5)

- **A quién:** un jugador vivo que no está mirando a Slender, con Slender presente.
- **Frecuencia:** enfriamiento de 90 s por jugador. Probabilidad por segundo `0,02 · frecuencia`.
- **Efecto:**
  - título de bloques blancos `█` en toda la línea, sin aparecer y desvaneciéndose en 1,5 s;
  - ceguera y lentitud II durante 3 s;
  - pitido agudo (sonido vanilla con tono alto);
  - +15 de estática.

### Hacerse pasar por un jugador (t ≥ 6)

- **Cuándo:** enfriamiento de 120 s; probabilidad por segundo `0,03 · frecuencia`. Hacen falta al menos 2 vivos.
- **Aspecto:** en vez de Slender aparece un `Mannequin` con la skin y el nombre de un superviviente vivo, que no es el objetivo. Sale en un sitio oculto a 15–30 bloques del objetivo y anda despacio (1 bloque/s) o se queda quieto.
- **Al acercarse:** si un vivo llega a ≤ 4 bloques, se convierte en Slender, susto (`jumpscare`) y +30 de estática para ese jugador.
- **Duración:** como mucho 30 s; después desaparece.
- Mientras dura, el Director no teletransporta (cuenta para el bloqueo global).

### Perseguir (t = 7)

- **Cuándo:** el objetivo lo está mirando a ≤ 20 bloques. Probabilidad 0,35 por cada vez que empieza a ser observado; enfriamiento de 30 s.
- **Movimiento:** anda hacia el objetivo a 3,5 bloques/s durante hasta 8 s, con línea de visión y subpasos como `creep`. Atrapa como siempre (`catchDistance`).
- **Final:** se esfuma y sigue el ciclo normal.
- Cuenta para el bloqueo global.

## 7. Muerte, cabezas y revivir

### Al morir en la noche

- Queda el cuerpo como ahora y además se suelta **su cabeza**: entidad `Item` con un `player_head` con su perfil.
  - Nombre: "Cabeza de <nombre>".
  - PDC `slender:head=<uuid>`.
  - Brilla, no desaparece nunca y tarda 1 s en poder cogerse.
- Solo la recogen supervivientes vivos de la fase.
- Si muere quien lleva cabezas, caen todas junto a la suya.
- Revivir anula el paso a espectador que `kill` programa a los 30 ticks.

### Caldero (revivir en la fase)

- **Uno por sección,** en la posición `altar` de `secciones.json` (§11.6). Se coloca al empezar la fase y se quita al acabarla:
  - `soul_campfire[lit=true]` en la celda del suelo;
  - encima, un `cauldron`.
- **Ritual:**
  1. Clic derecho en el caldero con una cabeza en la mano empieza un ritual de 8 s.
  2. Hay que quedarse a ≤ 3 bloques (horizontal); salirse o morir lo corta. El progreso se ve en la barra de acción.
  3. Mientras dura hay partículas de alma y sonido, y el siguiente teletransporte de Slender va hacia el caldero, como si fuera un ruidoso.
- **Al acabar el ritual:**
  - se gasta la cabeza;
  - el dueño revive junto al caldero: estado `alive`, `StaticModel` a 0, linterna registrada al 50 %, modo aventura, efectos de la fase;
  - el caldero queda 60 s sin poder usarse.

### Salida

- Quien escapa con cabezas en el inventario **rescata** a esos jugadores: en la sala están vivos y juegan la fase siguiente.

### Perdidos

- Al acabar una fase ganada, queda perdido todo miembro muerto que no revivió ni fue rescatado (cabeza abandonada o robada).
- Un perdido juega la fase siguiente como espectador y después vuelve a estar vivo.
- En la sala se puede gastar una vela para que un perdido vuelva ya (§9).

Morir otra vez tras revivir suelta una cabeza nueva.

## 8. Velas, misión y easter egg

### Velas

- Empiezan en 2; máximo 4.
- **Derrota de fase:**
  - si velas > 0: −1 y se repite la fase (los perdidos vuelven);
  - si velas = 0: se acaba la noche.
- **Gastar en la sala:** −1 vela y un perdido vuelve.

### Misión de la noche

Se elige una al azar al crear la noche y se guarda. Cumplirla da +1 vela (sin pasar de 4), una sola vez. Los escondites se eligen al azar al empezar cada fase entre las celdas `floor` de la sección (las del filtro de Slender).

- **La Señora Norris.**
  - Sale en una fase al azar de la 2 a la 6.
  - Es un `Cat` sentado, mudo, invulnerable, sin IA y no persistente.
  - Clic derecho para encontrarla. Si se acaba esa fase sin encontrarla, la misión falla.
- **Cromos de ranas de chocolate.**
  - Hay 1 por fase: un `Item` papel con nombre "Cromo de rana de chocolate", sin brillo.
  - Hay que reunir 5 de 7. Recogerlo cuenta para el grupo.
- **Los braseros de las casas.**
  - Hay 4 fases al azar de la 2 a la 7.
  - En cada una, un `campfire[lit=false]` con un bloque de lana del color de una casa debajo (rojo, verde, azul, amarillo).
  - Clic derecho lo enciende; con los 4 encendidos se cumple.

### Easter egg: la Snitch Dorada

- **Cuándo:** en una fase al azar de la 3 a la 7. Aparece cada 120–240 s y dura 45 s en una celda `floor` al azar.
- **Movimiento:**
  - 5 bloques/s, cambiando de dirección cada 0,5–1,5 s;
  - huye en dirección contraria del vivo más cercano si está a ≤ 6 bloques;
  - solo se mueve por celdas `floor` (o 1 bloque por encima).
- **Aspecto:**
  - un `ItemDisplay` de `gold_nugget` brillante (Java);
  - estela de partículas doradas, que también ve Bedrock;
  - un zumbido de alas.
- **Atraparla:** un vivo a ≤ 1,2 bloques la atrapa: +1 vela (sin pasar de 4), mensaje a todos y no vuelve a salir esa noche.

## 9. Sala de los Menesteres

- **Lugar:** el mundo de Slender, origen `(-2000, 64, 120)`. Se construye con `/slender build vestibulo`, en su propia caja (no toca la estación, que tiene radio 41).
- **Sala:** piso de piedra y madera oscura de unos 15 × 15 × 6, muchas velas, chimenea y estanterías.
- **Tablón** (carteles que actualiza el plugin):
  - fase siguiente y su nombre;
  - velas;
  - misión y su progreso;
  - siguiente entrada del diario de Filch.
- **Círculo de listos:** un anillo de farolillos de alma en el suelo (§4.3).
- **Vela por un perdido:** un pequeño altar con una vela. Quedarse 3 s dentro de su zona, si hay perdidos y velas, gasta una vela y devuelve al perdido que lleve más tiempo.
- **Puerta de salida:** al cruzarla se guarda y se vuelve a la estación. La noche sigue *esperando* si queda alguien en la sala; si no queda nadie, se libera.
- Los jugadores en la sala cuentan como dentro de la estación (`HubManager`): instantánea, objetos de la estación y salida normal.

## 10. Historia y textos

Todos en `config.yml`, en `messages:`, con claves `noche-*` y en español.

- **Premisa** (al crear la noche): "Llegáis tarde en el Expreso. No hay nadie esperando en el andén. Filch salió anoche a hacer la ronda con la Señora Norris y no ha vuelto. En el castillo solo queda Él."
- **Entradas del diario de Filch** (una por fase, al empezar y en el tablón):
  1. Terrenos: "Las barcas han vuelto vacías. Bajo a la orilla con el farol."
  2. Gran Comedor: "Las velas del Comedor bajan solas. Alguien se sienta en la mesa de profesores. No tiene cara."
  3. Mazmorras: "Ahí abajo el eco devuelve pasos que no son míos. Norris no quiere bajar."
  4. Ala Este: "El gramófono suena sin disco. He oído mi nombre entre la música."
  5. Patio: "Las armaduras me siguen con la cabeza. Si corro, me oye."
  6. Aulas altas: "He visto a un alumno al fondo del pasillo. Al acercarme ya no era un alumno."
  7. Torres: "Subo a la torre. Si alguien lee esto, que no suba detrás de mí."
- **Final bueno** (amanecer): "Amanece. En lo alto de la torre, en un armario de escobas, encontráis a Filch temblando, abrazado a la Señora Norris. 'Ha estado aquí toda la noche', susurra. 'Vosotros también.'"
- **Final malo** (sin velas): "La última vela se apaga. Por la mañana, en el tablón de la estación hay carteles nuevos de DESAPARECIDOS. Llevan vuestras caras."

## 11. Arquitectura

### 11.1 Paquete nuevo `dev.two.slender.night`

**Lógica pura, sin Bukkit y con tests:**

| Clase | Qué hace |
|---|---|
| `NightPlan` | Orden de fases e ids, `pages(t, n, spots)`, `timeSeconds(pages)`, `aggression(t, cogidas, total, s, escape)`, `staticFactor(t)`, `abilities(t)`, `frequency(ability, t)` |
| `NightSettings` | Los números de `noche:` del config (con sus valores por defecto); los usa `NightPlan` |
| `NightState` | Progreso: id, miembros, fase, velas, misión y progreso, fase de la Snitch y si se cogió, perdidos con la fase en que se perdieron, creada y actualizada |
| `NightCodec` | `NightState` ↔ `Map` de yml, ida y vuelta |
| `Candles` | Reglas de velas: derrota, gastar por perdido, sumar por misión o Snitch, máximo |
| `HeadLedger` | Quién está muerto, quién lleva qué cabeza, cabezas en el suelo, rescatados al escapar, robadas, revividos y perdidos al acabar |
| `Ritual` | Temporizador del caldero: empezar, cortar por distancia o muerte, completar y enfriamiento |
| `SprintEar` | Segundos corriendo seguidos y umbral por nivel |
| `AbilityClock` | Enfriamientos y probabilidades por habilidad y jugador, y bloqueo global |
| `ChaseModel` | Estado de la persecución (duración y velocidad) |
| `MissionModel` | Tipo, fases donde sale cada cosa, progreso y cumplida o fallida |
| `SnitchMotion` | Paso de la Snitch dados la posición, el vivo más cercano, un azar y un predicado de "celda válida" |

**Con Bukkit:**

| Clase | Qué hace |
|---|---|
| `NightStore` | Ficheros `plugins/Slender/noches/<id>.yml` (como `MapStore`) |
| `NightManager` | La noche activa: menú, sala, empezar fase, recoger resultado, velas, guardado, finales y comandos |
| `NightPhase` | Implementa `PhaseHooks` (§11.2) para una fase: cabezas, caldero, misión, Snitch y poderes |
| `NightPowers` | Ejecuta las 6 habilidades con las clases puras |
| `NightMenu` | Inventario de 27 huecos: Nueva / Continuar / Unirse |
| `NightListener` | Recoger cabezas y cromos, clic en caldero, gata y braseros (permitidos aunque `ProtectionListener` los niegue) y salir del servidor durante una fase |
| `SalaBuilder` | Construye la sala |
| `SalaRoom` | Zonas de la sala (listos, vela, salida) y actualización del tablón |

### 11.2 Cambios en `Game` (mínimos, con un punto de enganche)

- Constructor o setter opcional `PhaseHooks hooks`. Si es `null`, todo sigue igual que ahora.
- `PhaseHooks` (interfaz en `game`):
  - `int pages(int spots)` y `int timeSeconds(int pages)`;
  - `double aggression(int collected, int total, double secsSincePage, boolean escape)`;
  - `boolean dormantAllowed()`;
  - `double staticFactor()`;
  - `void onStart(Game)`, `onTick(Game)` (cada 2 ticks) y `onSecond(Game)`;
  - `void onKill(Game, Player)` y `onEscape(Game, Player)`;
  - `boolean onInteract(Game, Player, Block)` y `onPickup(Game, Player, Item)`;
  - `void onFinish(Game, PhaseResult)` (en vez de mandar a la estación);
  - `void onCleanup(Game)`.
- Métodos nuevos en `Game`:
  - `revive(UUID, Location, double battery)`;
  - acceso de lectura a `stateOf`, `aliveSurvivors`, `body`, `director`, `world`, `map`, `flashlights`, `sight`;
  - `Director.lure(Location)` para que el siguiente teletransporte vaya a un sitio o jugador;
  - `Director.suspend(boolean)` para el bloqueo global.
- **`reset()`:** si hay hooks, en vez de `leave(id, RESET)` hacia la estación, llama a `hooks.onFinish`, limpia el mundo y deja los participantes para `NightManager`.
- `PhaseResult(Winner, Set<UUID> escaped, Set<UUID> dead, Set<UUID> rescued)`.

### 11.3 `GameManager`

- `startPhase(String mapId, List<Player> alive, List<Player> spectators, PhaseHooks)`:
  - falla si el mapa no existe o su `Game` no está en WAITING sin jugadores;
  - si no, une a los jugadores en COOP, los espectadores como espectadores, y arranca sin cuenta atrás (`forceStart`).
- Mientras una fase usa un mapa, las uniones normales se rechazan con el mensaje de partida en curso de siempre.

### 11.4 Estación y menú

- `Anden934`:
  - la 8.ª puerta (x 26) pasa a ser un hueco abierto con zona `noche`;
  - el cartel cambia a "LA NOCHE" / "ENTERA" / "" / "8.ª puerta".
- `MapIds`: reservar `noche`.
- `HubManager.door(p, "noche")` abre `NightMenu`.

### 11.5 Comandos (admin)

- `/slender noche estado`: noche activa, fase, velas, miembros y misión.
- `/slender noche terminar`: cierra la noche activa (guarda en la última sala) y devuelve a todos a la estación.
- `/slender noche fase <1-7>`: pone la fase de la noche activa (solo en la sala, para probar).

### 11.6 Herramienta de Python

- Nuevo campo por sección en `secciones.json`: `"altar": "x,y,z"` (coordenadas del schem, la celda de los pies).
- Se elige entre las celdas de `floor`:
  - 3 × 3 de suelo alcanzable alrededor, y aire en la celda y en las dos de encima;
  - seca, y sin página, pila, spawn, verja, nota, vela ni derrumbe a ≤ 2 bloques;
  - a ≥ 25 bloques del primer spawn y lo más cerca posible del centro de la zona alcanzada.
- Test `test_altar_spot`.

### 11.7 Config (`noche:`, con estos valores por defecto)

```yaml
noche:
  paginas-base: [3, 4, 5, 5, 6, 7, 8]
  factor-jugadores: {1: 0.75, 2: 1.0, 4: 1.25, 6: 1.5}   # a partir de N jugadores
  paginas-min: 2
  paginas-max: 12
  tiempo-base: 180
  tiempo-por-pagina: 60
  agresividad-por-fase: 0.5
  huida-por-fase: 0.3
  estatica-por-fase: 0.08
  velas-inicio: 2
  velas-max: 4
  frecuencia-por-fase: 0.25
  luces: {radio: 12, min: 30, max: 60, pila-mirando: 6.0}
  correr: {umbral: 6.0, baja-por-fase: 0.5, distancia: 0.6}
  cabezas: {radio: 8, segundos: 20, coger: 2}
  fogonazo: {enfriamiento: 90, prob: 0.02, segundos: 3, estatica: 15}
  suplantar: {enfriamiento: 120, prob: 0.03, min: 15, max: 30, revela: 4, dura: 30, estatica: 30}
  perseguir: {radio: 20, prob: 0.35, velocidad: 3.5, dura: 8, enfriamiento: 30}
  caldero: {segundos: 8, radio: 3, enfriamiento: 60, pila: 50}
  snitch: {cada-min: 120, cada-max: 240, dura: 45, velocidad: 5.0, huye: 6, atrapa: 1.2}
```

- `ConfigMigration` pasa a la versión 4 y añade `noche:` y los mensajes `noche-*` a configs viejos.

## 12. Errores y casos límite

- **Sin Hogwarts construido** (falta alguna sección en `maps()`): "Construye Hogwarts con /slender build hogwarts" y no empieza.
- **Sección de la fase ocupada** por una partida suelta: la sala muestra "Esperando a que se libere <sección>…" y la fase arranca al quedar libre (comprobación cada segundo).
- **Otra noche activa:** se avisa y solo se ofrece *Unirse*.
- **Alguien se va del servidor a mitad de fase:** cuenta como muerto y suelta su cabeza.
- **Se van todos a mitad de fase:** la fase se anula, sin gastar vela; la partida queda guardada en la última sala y la noche se libera.
- **`/slender reload`, reinicio o `/slender noche terminar`:** igual que irse todos.
- **Guardado corrupto:** se ignora con un aviso en consola, como `MapStore`.
- **Revivir dentro de los 30 ticks del paso a espectador:** la tarea programada comprueba `alive` antes de poner espectador.
- **Una cabeza cae al vacío o fuera del mapa:** se teletransporta al caldero de la fase.

## 13. Pruebas

**Tests unitarios (Java):**
- `NightPlanTest`: tabla §5.2 completa, tiempo, agresividad, estática y calendario.
- `NightCodecTest`: ida y vuelta.
- `CandlesTest`, `HeadLedgerTest`, `RitualTest`, `SprintEarTest`, `AbilityClockTest`, `ChaseModelTest`, `MissionModelTest`, `SnitchMotionTest`.
- `Anden934Test`: la 8.ª zona es `noche`, no se solapa y está dentro del patio.
- Menú y `MapIds` con `noche`.

**Test de Python:** `test_altar_spot` (§11.6).

**Lista de comprobación en el servidor** (en la entrega):
- una noche entera con 2 jugadores;
- morir, coger cabeza, ritual, revivir;
- escapar con cabeza;
- perdido y gastar vela;
- derrota y repetición;
- misión y Snitch;
- guardar, salir y continuar;
- cada habilidad en su fase;
- Bedrock: cabezas, fogonazo, partículas de la Snitch.

## 14. Riesgos conocidos

- **`ItemDisplay`** (la Snitch) no se ve en Bedrock. Lo cubren las partículas.
- **`Mannequin`** (suplantar) en Bedrock: el cuerpo de Slender ya lo usa con skin. Si Geyser no lo dibujara, en Bedrock la suplantación se vería como Slender.
- **`ProtectionListener`** niega clics en bloques: `NightListener` tiene que ir antes y permitir solo el caldero y los braseros de la fase.
- **Equilibrio:** los números son una primera estimación y van todos en `config.yml`.
