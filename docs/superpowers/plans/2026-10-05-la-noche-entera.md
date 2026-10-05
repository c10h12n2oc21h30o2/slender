# LA NOCHE ENTERA — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Modo historia cooperativo de 7 fases sobre las secciones de Hogwarts, con Slender que gana poder, revivir con cabezas, velas, misión, Snitch, Sala de los Menesteres y guardado (Slender 1.9.0).

**Architecture:**
- Paquete nuevo `dev.two.slender.night`. Toda la lógica de reglas es pura y tiene tests; las clases con Bukkit solo la orquestan.
- `Game` gana un único punto de enganche (`PhaseHooks`): con `null`, nada cambia para las partidas sueltas.
- `NightManager` lleva una noche activa: sala → fase (un `Game` de sección con `NightPhase` como hooks) → resultado → sala, guardando en yml.

**Tech Stack:** Java 25 (`javac --release 25`), Paper 26.3 API, JUnit 5 (`./build.sh`), Python 3 + numpy + pytest (`tools/hogwarts`).

**Spec:** `docs/superpowers/specs/2026-10-05-la-noche-entera-design.md`

## Global Constraints

- Versión del plugin **1.9.0** (`plugin.yml`). Nada de lo nuevo cambia las partidas sueltas: con `PhaseHooks == null` el comportamiento de `Game` es idéntico al de 1.8.1.
- **Orden de fases:** `hw_terrenos, hw_comedor, hw_mazmorras, hw_ala_este, hw_patio, hw_aulas, hw_torres` (t = 1…7).
- **Páginas y tiempo:**
  - `páginas = clamp(redondeo_.5_arriba(base[t]·factor(n)), 2, min(12, sitios))`;
  - `base = [3,4,5,5,6,7,8]`;
  - `factor`: 1 jugador ×0,75; 2–3 ×1,0; 4–5 ×1,25; 6+ ×1,5;
  - tiempo `180 + 60·páginas` s.
- **Poder de Slender:**
  - agresividad `min(8, 8·cogidas/total + ⌊s/90⌋) + 0,5·(t−1)`;
  - huida `10 + 0,3·(t−1)`;
  - estática ×`(1 + 0,08·(t−1))`;
  - minuto de gracia solo en t = 1.
- **Habilidades:**
  - estreno: luces t2, correr t3, cabezas t4, fogonazo t5, suplantar t6, perseguir t7;
  - frecuencia `1 + 0,25·(t − estreno)`;
  - bloqueo global solo entre suplantar y perseguir.
- **Velas:** empiezan en 2, máximo 4. Misión +1 y Snitch +1, una vez cada una por noche.
- **Textos** en español, en `config.yml` → `messages:` con prefijo `noche-` (§10 de la spec). Números en `config.yml` → `noche:` con los valores de la spec §11.7.
- **Correcciones a la spec (decididas al planear):**
  - **Sala:** va dentro de la caja de la estación (relativo a `HubBuilder.ORIGIN`: x −7…7, y 0…7, z 24…38, detrás de los arcos), para que se construya y limpie con `/slender build vestibulo` y sus chunks ya estén cargados.
  - **`ConfigMigration`** no sube de versión: `copyDefaults(true)` ya añade al config del servidor las claves nuevas.
  - **`ChaseModel`** no se crea: basta `AbilityClock` más `DirectorMath.subSteps`.
  - **Perdidos:** juegan la fase como participantes que empiezan muertos (`PhaseHooks.startsAsSpectator`).

## Review Focus

1. **Revivir dentro de los 30 ticks** tras morir: la tarea que pone espectador no debe pisar a un jugador revivido. Test en la Task 9 (comprobación `alive` en la tarea).
2. **Se van todos a mitad de fase** (o `/slender reload`, o `terminar`): la fase se anula, no se gasta vela y el guardado de la última sala queda intacto. Test `NightRulesTest.annulledKeepsState` (Task 7).
3. **Repetir una fase reutiliza el mismo `Game`:** tras `reset()` con hooks no puede quedar nada de la fase anterior (hooks a null, caldero, cabezas, gata, braseros, Snitch, luces apagadas). Comprobación en `NightPhase.onCleanup` (Task 11) y en la lista del servidor.
4. **Jugadores en la sala o espectadores de la fase:** `HubManager.tick` no debe devolverlos a la estación (por y < spawn − 20) ni dispararles puertas. Los perdidos son participantes del `Game` (Task 9) y la sala está a la altura de la estación (Task 12).
5. **Clics y recogidas indebidas:**
   - el caldero y los braseros deben funcionar aunque `ProtectionListener` deniegue el uso del bloque;
   - las cabezas y los cromos solo los cogen vivos de la fase (no espectadores, ni escapados, ni gente de fuera).
   Tests `HeadLedgerTest.onlyAliveCanPickUp` (Task 5) y ruta de eventos en la Task 13.

---

### Task 1: Velas que parpadean (Hogwarts) y apagado forzado de luces

En 1.8 `MapAmbience.apply` solo cambia `REDSTONE_LAMP`, así que las velas de Hogwarts no parpadean. Hace falta para la 1.8.x y para "apagar luces".

**Files:**
- Modify: `src/main/java/dev/two/slender/game/MapAmbience.java`, `src/main/java/dev/two/slender/game/Game.java`
- Create: `src/main/java/dev/two/slender/game/LightSuppression.java`
- Test: `src/test/java/dev/two/slender/game/LightSuppressionTest.java`

**Interfaces:**
- Produces:
  - `LightSuppression`: `void suppress(BlockPos pos, long untilTick)`, `boolean suppressed(BlockPos pos, long tick)`, `void clear()`;
  - `static boolean toggles(Material m)` (true para `REDSTONE_LAMP`, toda vela `*CANDLE`, `CAMPFIRE`, `SOUL_CAMPFIRE` y `*COPPER_BULB`);
  - `Game.lightsOut(Location center, double radius, long untilTick)`: apaga y suprime las `MapLight` en el radio.

- [ ] **Step 1: Test**
  - `toggles(Material.CANDLE)`, `toggles(Material.RED_CANDLE)`, `toggles(Material.REDSTONE_LAMP)` y `toggles(Material.SOUL_CAMPFIRE)` son true; `toggles(Material.STONE)` es false.
  - `suppress(p, 100)`: `suppressed(p, 99)` es true y `suppressed(p, 100)` es false.
  - `clear()` lo quita.
- [ ] **Step 2:** `./build.sh` → FAIL (no existe la clase).
- [ ] **Step 3: Implementar**
  - `MapAmbience.apply` usa `LightSuppression.toggles(type)` y `BlockData instanceof Lightable`.
  - `lights(tick, …)` deja apagada (lit = false) toda luz suprimida.
  - `MapAmbience.suppress(Location, double radius, long until)` y `reset()` limpian la supresión.
  - `Game.lightsOut` delega.
- [ ] **Step 4:** `./build.sh` → PASS.
- [ ] **Step 5:** Commit `fix: las velas de Hogwarts parpadean; apagado forzado de luces`.

### Task 2: Ajustes de la noche y textos

**Files:**
- Create: `src/main/java/dev/two/slender/night/NightSettings.java`
- Modify: `src/main/resources/config.yml` (`noche:` y mensajes `noche-*`), `src/main/java/dev/two/slender/config/SlenderConfig.java` (campo `public final NightSettings night`)
- Test: `src/test/java/dev/two/slender/night/NightSettingsTest.java`

**Interfaces:**
- Produces `record NightSettings(...)` con los valores de la spec §11.7:
  - `List<Integer> pagesBase`, `NavigableMap<Integer, Double> playerFactor`, `int pagesMin`, `int pagesMax`, `int timeBase`, `int timePerPage`;
  - `double aggressionPerPhase`, `double escapePerPhase`, `double staticPerPhase`, `int candlesStart`, `int candlesMax`, `double frequencyPerPhase`;
  - records anidados `Lights(radius, minSec, maxSec, lookDrain)`, `Sprint(threshold, dropPerPhase, distance)`, `Heads(radius, seconds, grabSeconds)`, `Flash(cooldown, chance, seconds, staticBump)`, `Mimic(cooldown, chance, minDist, maxDist, reveal, duration, staticBump)`, `Chase(radius, chance, speed, duration, cooldown)`, `Altar(seconds, radius, cooldown, battery)`, `Snitch(everyMin, everyMax, duration, speed, flee, catchDist)`;
  - `static NightSettings defaults()` y `static NightSettings from(ConfigurationSection noche)`: lo que falte, por defecto.
- Mensajes: claves de la §10 de la spec, más la UI.
  - Historia: `noche-premisa`, `noche-diario-1` … `noche-diario-7`, `noche-final-bueno`, `noche-final-malo`.
  - Fase: `noche-fase-titulo` (`{n}`, `{name}`).
  - Menú: `noche-menu-titulo`, `noche-menu-nueva`, `noche-menu-continuar` (`{phase}`, `{candles}`), `noche-menu-unirse`.
  - Sala y estado: `noche-ocupada`, `noche-sin-hogwarts`, `noche-esperando` (`{name}`), `noche-listos` (`{seconds}`), `noche-vela-gastada` (`{player}`).
  - Velas y fases: `noche-velas` (`{candles}`), `noche-fase-ganada`, `noche-fase-perdida` (`{candles}`).
  - Cabezas y caldero: `noche-te-oigo`, `noche-cabeza-robada` (`{player}`), `noche-cabeza` (`{player}`), `noche-ritual` (`{seconds}`), `noche-revivido` (`{player}`).
  - Misiones: `noche-mision-norris`, `noche-mision-cromos`, `noche-mision-braseros`, `noche-mision-hecha`, `noche-mision-fallida`.
  - Snitch y extras: `noche-snitch` (`{player}`), `noche-perdido` (`{player}`), `noche-estado`.
- Los textos exactos de historia están en la spec §10. Los de UI los escribe el implementador, en español, con ≤ 1 línea cada uno.

- [ ] **Step 1: Test**
  - `defaults().pagesBase()` es `[3,4,5,5,6,7,8]`.
  - `defaults().altar().seconds()` es 8.0.
  - `from(yaml)` con `noche.velas-inicio: 3` da 3 y el resto por defecto.
  - Cargar `src/main/resources/config.yml` con `YamlConfiguration` da `from(...)` igual a `defaults()`.
  - Todas las claves de mensajes de arriba existen en `messages:` (cargando el config.yml del jar).
- [ ] **Step 2:** FAIL. **Step 3:** implementar y rellenar `config.yml`. **Step 4:** PASS (con `ConfigFileTest` incluido).
- [ ] **Step 5:** Commit `feat(noche): ajustes y textos`.

### Task 3: `NightPlan` y `Ability`

**Files:**
- Create: `src/main/java/dev/two/slender/night/NightPlan.java`, `src/main/java/dev/two/slender/night/Ability.java`
- Test: `src/test/java/dev/two/slender/night/NightPlanTest.java`

**Interfaces:**
- Consumes `NightSettings`.
- Produces:
  - `enum Ability { LIGHTS(2), SPRINT(3), HEADS(4), FLASH(5), MIMIC(6), CHASE(7); int unlock(); }`;
  - `NightPlan(NightSettings s)`;
  - `static final List<String> ORDER` (Global Constraints), `static String sectionOf(int phase)`;
  - `int pages(int phase, int players, int spots)`, `int timeSeconds(int pages)`;
  - `double aggression(int phase, int collected, int total, double secsSincePage, boolean escape)`;
  - `double staticFactor(int phase)`, `boolean dormantAllowed(int phase)`;
  - `Set<Ability> abilities(int phase)`, `double frequency(Ability a, int phase)` (0 si no ha estrenado);
  - `double sprintThreshold(int phase)` = `threshold − dropPerPhase·(phase − 3)` (6 en t3 y 4 en t7).

- [ ] **Step 1: Test**
  - Tabla completa de la spec §5.2 con `spots = 14` (las 4 filas × 7 fases).
  - `pages(7, 8, 9)` es 9 (tope de sitios).
  - `timeSeconds(3)` es 360.
  - `aggression(1,0,3,0,false)` es 0.0; `aggression(7,3,3,0,false)` es 11.0; `aggression(7,0,8,0,true)` es 11.8 (±1e-9).
  - `staticFactor(7)` es 1.48.
  - `dormantAllowed(1)` es true y `dormantAllowed(2)` es false.
  - `abilities(1)` está vacío y `abilities(7)` tiene las 6.
  - `frequency(LIGHTS,4)` es 1.5 y `frequency(CHASE,6)` es 0.
  - `sprintThreshold(3)` es 6.0 y `sprintThreshold(7)` es 4.0.
- [ ] **Step 2:** FAIL. **Step 3:** implementar (el redondeo es `Math.floor(x + 0.5)`). **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): plan de fases`.

### Task 4: Modelos puros: velas, ritual, correr, relojes de habilidades

**Files:**
- Create: `night/Candles.java`, `night/Ritual.java`, `night/SprintEar.java`, `night/AbilityClock.java` (en `src/main/java/dev/two/slender/`)
- Test: `CandlesTest`, `RitualTest`, `SprintEarTest`, `AbilityClockTest` (en `src/test/java/dev/two/slender/night/`)

**Interfaces:**
- Produces:
  - `Candles(int count, int max)`: `int count()`, `boolean onWipe()` (true = repetir la fase, count − 1; false = noche perdida), `boolean spendForLost()`, `boolean gain()` (sin pasar de max).
  - `Ritual(double seconds, double radius, double cooldown)`:
    - `boolean start(UUID carrier, UUID owner, double now)` (false si hay uno en curso o se está enfriando);
    - `enum Status { IDLE, RUNNING, CANCELLED, DONE }`, `Status tick(double now, double carrierDistance, boolean carrierAlive)`;
    - `double progress(double now)` (0…1), `UUID owner()`, `UUID carrier()`.
  - `SprintEar`: `boolean update(UUID id, boolean sprinting, double dt, double threshold)` (true una sola vez al cruzar el umbral), `void reset(UUID id)`, `double seconds(UUID id)`.
  - `AbilityClock`:
    - `boolean ready(Ability a, UUID who, double now)`, `void used(Ability a, UUID who, double now, double cooldown)`; `who == null` es el reloj global de la habilidad;
    - `static boolean roll(double chancePerSecond, double frequency, double dt, double r)` = `r < chance·frequency·dt`;
    - `boolean lock(Ability a)` (solo MIMIC o CHASE, y solo si no hay otra), `void unlock(Ability a)`, `Ability locked()`.

- [ ] **Step 1: Tests**
  - Velas: 2 → `onWipe` true, 1 → true, 0 → false. `gain` no pasa de 4. `spendForLost` con 0 es false.
  - Ritual: DONE al llegar a 8 s; CANCELLED si `carrierDistance > 3` o si el portador muere; tras DONE, `start` es false durante 60 s y true después.
  - Correr: 6 s seguidos a dt 0,5 con umbral 6 → true exactamente una vez; un tick sin correr reinicia.
  - Relojes: `lock(MIMIC)` y luego `lock(CHASE)` da false; tras `unlock(MIMIC)`, true. `lock(LIGHTS)` es false. `roll(0.02, 1.5, 1.0, 0.029)` es true y con r = 0.031, false.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): modelos de velas, ritual, correr y habilidades`.

### Task 5: `HeadLedger`

**Files:**
- Create: `src/main/java/dev/two/slender/night/HeadLedger.java`
- Test: `src/test/java/dev/two/slender/night/HeadLedgerTest.java`

**Interfaces:**
- Produces `HeadLedger(Collection<UUID> alive)`:
  - `List<UUID> died(UUID who)`: devuelve los dueños cuyas cabezas caen ahora, la suya más las que llevaba;
  - `boolean pickUp(UUID owner, UUID carrier)`, `void revived(UUID owner)`, `List<UUID> escaped(UUID carrier)` (rescatados) y `void stolen(UUID owner)`;
  - consultas: `Set<UUID> onGround()`, `Set<UUID> carriedBy(UUID carrier)`, `boolean alive(UUID id)`, `Set<UUID> rescued()` y `Set<UUID> lostAtEnd()` (muertos ni revividos ni rescatados).

- [ ] **Step 1: Tests**
  - `diedDropsOwnAndCarried`: A coge la cabeza de B y muere → `died(A)` contiene A y B, y ambas están en el suelo.
  - `onlyAliveCanPickUp` (Review Focus 5): un muerto o escapado no puede `pickUp`; tampoco una cabeza que no está en el suelo.
  - `escapeRescues`: A lleva la de B y escapa → `escaped(A) == [B]`, B en `rescued()` y no en `lostAtEnd()`.
  - `stolenIsLost`: `stolen(B)` → B en `lostAtEnd()`; ya no está en el suelo.
  - `reviveThenDieAgain`: B revive, vuelve a morir → hay cabeza nueva en el suelo.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): registro de cabezas`.

### Task 6: `MissionModel` y `SnitchMotion`

**Files:**
- Create: `night/MissionModel.java`, `night/SnitchMotion.java`
- Test: `MissionModelTest`, `SnitchMotionTest`

**Interfaces:**
- Produces:
  - `enum MissionType { NORRIS, CROMOS, BRASEROS }`;
  - `MissionModel`:
    - `static MissionModel create(MissionType t, Random r)`. Fases: NORRIS 1 al azar en 2…6; CROMOS las 7, meta 5; BRASEROS 4 distintas en 2…7;
    - `boolean appearsIn(int phase)`, `void found(int phase)`, `void phaseEnded(int phase)`;
    - `int progress()`, `int goal()`, `boolean done()`, `boolean failed()`, `MissionType type()`, `List<Integer> phases()`, `Set<Integer> foundIn()`;
    - `static MissionModel restore(MissionType, List<Integer> phases, Set<Integer> foundIn, boolean failed)`.
  - `SnitchMotion(Vec3 start, Random r)`:
    - `interface Cells { boolean ok(int x, int y, int z); }`;
    - `void step(double dt, Vec3 nearestAlive /*nullable*/, double flee, double speed, Cells cells)`;
    - `Vec3 pos()`, `boolean caught(Vec3 player, double dist)`;
    - solo se mueve a celdas donde `cells.ok` acepta el bloque de los pies o el de debajo (vuela hasta 1 por encima del suelo).

- [ ] **Step 1: Tests**
  - NORRIS: si se acaba su fase sin `found` → `failed()`. Si se encuentra → `done()`.
  - CROMOS: 5 encontrados → `done()`. Con 3 encontrados y 1 fase restante → `failed()`.
  - BRASEROS: 4 fases distintas en 2…7 (100 semillas).
  - Snitch: con un jugador a 3 bloques, tras 10 pasos de 0,1 s la distancia ha crecido; con `cells` que solo acepta x ≤ 5, nunca pasa de x = 5,99; `caught` a 1,1 es true y a 1,3 es false.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): misiones y Snitch`.

### Task 7: Estado, guardado y reglas tras una fase

**Files:**
- Create: `night/NightState.java`, `night/NightCodec.java`, `night/NightStore.java`, `night/NightRules.java`, `night/PhaseOutcome.java`
- Test: `NightCodecTest`, `NightRulesTest`

**Interfaces:**
- Consumes `Candles`, `MissionModel`.
- Produces:
  - `NightState` (mutable). Campos:
    - `String id`, `LinkedHashMap<UUID,String> members`;
    - `int phase` (1…7), `int candles`;
    - `MissionModel mission`, `boolean missionRewarded`;
    - `int snitchPhase`, `boolean snitchCaught`;
    - `Map<UUID,Integer> lost` (dueño → fase en que se perdió);
    - `long created`, `long updated`.
    - Constructor: `static NightState create(UUID first, String name, Random r)`, con la misión al azar y `snitchPhase` en 3…7.
  - `NightCodec`: `static Map<String,Object> toMap(NightState)`, `static NightState fromMap(Map<String,?>)` (lanza `IllegalArgumentException` si falta algo).
  - `NightStore(File dir, Logger log)`:
    - `save(NightState)`, `Optional<NightState> load(String id)`, `Optional<NightState> latestFor(UUID member)`, `delete(String id)`;
    - los ficheros corruptos se ignoran con aviso.
  - `record PhaseOutcome(boolean aborted, boolean won, Set<UUID> rescued, Set<UUID> lostNow)`.
  - `enum NightRules.Next { NEXT_PHASE, REPEAT, NIGHT_WON, NIGHT_LOST, ANNULLED }`.
  - `static Next NightRules.afterPhase(NightState s, PhaseOutcome o, int candlesMax)`. Muta `s`:
    - **aborted:** nada cambia y devuelve ANNULLED.
    - **won:** perdidos = `lostNow` (los de antes ya cumplieron), `phase + 1`, y `NIGHT_WON` si era la 7.
    - **perdida:** `Candles.onWipe`; con vela, perdidos vacíos y REPEAT; sin vela, NIGHT_LOST.
  - `static boolean NightRules.spendForLost(NightState s, UUID who)`.

- [ ] **Step 1: Tests**
  - Ida y vuelta de un estado con todos los campos llenos (misión BRASEROS a medias, 2 perdidos).
  - `fromMap` con un mapa vacío lanza.
  - `annulledKeepsState` (Review Focus 2): fase 3 con 1 vela, `aborted` → `ANNULLED`, misma fase y misma vela.
  - Ganar la fase 3 con `lostNow={B}` → `NEXT_PHASE`, fase 4, `lost={B:3}`.
  - Ganar la fase 7 → `NIGHT_WON`.
  - Perder con 1 vela → REPEAT, 0 velas, perdidos vacíos; perder con 0 → `NIGHT_LOST`.
  - `spendForLost` quita al perdido y una vela; con 0 velas devuelve false.
  - `NightStore` (con `@TempDir`): `latestFor` devuelve el más reciente del miembro y un yml corrupto se ignora.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): estado, guardado y reglas de fase`.

### Task 8: Caldero por sección (herramienta) y acceso al suelo

**Files:**
- Modify: `tools/hogwarts/hogwarts/content.py` (`altar` en `compile_section`), `tools/hogwarts/tests/test_content.py`, `src/main/resources/maps/hogwarts/secciones.json` (regenerado), `map/builders/hogwarts/HogwartsSections.java`, `map/builders/hogwarts/HogwartsBuilder.java`
- Test: `test_altar_spot` (Python), `HogwartsBuilderTest.altarAndFloorCells` (Java)

**Interfaces:**
- Produces:
  - campo JSON `"altar": "x,y,z"` (schem, la celda de los pies);
  - `HogwartsSections.Section.altar()` (`BlockPos`, `null` si falta);
  - `HogwartsBuilder`: `BlockPos altarWorld()`, `int floorCount()`, `BlockPos floorWorld(int i)` (la celda i de `floor` en coordenadas del mundo), `List<BlockPos> spawnCellsWorld()`.

- [ ] **Step 1: Tests**
  - Python `test_altar_spot`, para cada sección:
    - la celda está en `floor` y las 8 de alrededor también;
    - aire en `y`, `y+1` e `y+2`;
    - seca;
    - a ≥ 25 bloques (horizontal) del primer spawn;
    - nada a ≤ 2 bloques: página (celda de delante), pila, spawn, verja, nota, vela ni derrumbe.
  - Java: `altarWorld()` no es nulo en las 7. `floorWorld(0)` cumple `SpotRules.allows(id, …)` y `floorCount() > 1000`.
- [ ] **Step 2:** FAIL. **Step 3: Implementar**
  - `content.pick_altar(res, ctx, s)`: entre las celdas de `floor` que cumplen el test, la más cercana al centroide de `reached ∩ region`.
  - Regenerar con `python3 -m hogwarts.main --world input/region --out ../../src/main/resources` y restaurar `hogwarts.schem` y `extra.json.gz` si su contenido no cambia (`git checkout`).
- [ ] **Step 4:** `pytest tools/hogwarts/tests -q` y `./build.sh` → PASS.
- [ ] **Step 5:** Commit `feat(noche): caldero por sección`.

### Task 9: Punto de enganche en `Game` (`PhaseHooks`)

**Files:**
- Create: `game/PhaseHooks.java`, `game/PhaseResult.java`
- Modify:
  - `game/Game.java`;
  - `game/SurvivorState.java` (constructor con `double staticFactor`);
  - `logic/StaticModel.java` (nada si se escala en el constructor);
  - `game/Flashlights.java` (`void drain(UUID id, double amount)`, `void set(UUID id, double level)`);
  - `game/Director.java`, `game/GameManager.java`, `hub/HubManager.java`.
- Test: `src/test/java/dev/two/slender/game/PhaseHooksContractTest.java`

**Interfaces:**
- Produces:
  - `interface PhaseHooks` (todos `default` que no hacen nada salvo los marcados):
    - `int pages(int spots)` (obligatorio), `int timeSeconds(int pages)` (obligatorio), `double aggression(int collected, int total, double secsSincePage, boolean escape)` (obligatorio);
    - `boolean dormantAllowed()`, `double staticFactor()`, `boolean startsAsSpectator(UUID id)`;
    - `void onStart(Game g)`, `void onTick(Game g)` (cada 2 ticks), `void onSecond(Game g)`;
    - `void onKill(Game g, Player p)`, `void onLeave(Game g, UUID id, Location last)`, `void onEscape(Game g, Player p)`;
    - `boolean onInteractBlock(Game g, Player p, Block b)`, `boolean onPickup(Game g, Player p, Item item)`, `boolean onInteractEntity(Game g, Player p, Entity e)`;
    - `void onFinish(Game g, PhaseResult r)`, `void onCleanup(Game g)`.
  - `record PhaseResult(Game.Winner winner, boolean aborted, Set<UUID> escaped, Set<UUID> dead, List<UUID> participants)`.
  - `Game`:
    - `void hooks(PhaseHooks h)` (solo en WAITING) y `PhaseHooks hooks()`;
    - `void revive(UUID id, Location at, double battery)`;
    - getters públicos: `world()`, `body()`, `director()`, `flashlights()`, `sight()`, `msg()`, `random()`, `tick()`, `aliveSurvivors()`, `stateOf(UUID)`, `online()`, `participantIds()`;
    - `boolean interactBlock(Player, Block)`, `boolean pickup(Player, Item)` y `boolean interactEntity(Player, Entity)`, que delegan en los hooks si hay.
  - `Director`:
    - `void lurePlayer(UUID id, double distanceFactor)` y `void lureTo(Location loc)`, que valen para el próximo teletransporte;
    - `void suspend(boolean on)`, `boolean suspended()`;
    - `boolean observed(List<Player> players)`.
  - `GameManager.startPhase(String mapId, List<Player> players, PhaseHooks hooks)`: devuelve `Optional<String>` con la clave del error (`map-not-built`, `noche-ocupada`) o vacío si arrancó. Une en COOP, pone hooks y hace `forceStart()`.
  - `HubManager.sendTo(Player p, Location loc)`: como `backToHub`, pero a `loc`.

- [ ] **Step 1: Test `PhaseHooksContractTest`**
  - Un `PhaseHooks` anónimo que solo implementa los 3 obligatorios compila.
  - Los `default` devuelven `false`, `1.0` y `true` (para `dormantAllowed`).
  - `PhaseResult` conserva los conjuntos.
  - Este test fija el contrato; el resto necesita servidor.
- [ ] **Step 2:** FAIL. **Step 3: Implementar en `Game`, con `hooks != null` en cada punto:**
  - **`start()`:**
    - páginas `hooks.pages(map.pageSpots().size())` y tiempo `hooks.timeSeconds(pages)`;
    - `SurvivorState` con `hooks.staticFactor()`;
    - `startsAsSpectator` → `alive=false`, `SPECTATOR`, sin linterna;
    - al final, `hooks.onStart(this)`.
  - **Agresividad:** `aggression()` delega. `dormant` solo si `hooks.dormantAllowed()`.
  - **Ticks:** `playStep` → `onTick`; `secondStep` → `onSecond`.
  - **Muerte:**
    - `kill` → `onKill` tras el cadáver;
    - la tarea de 30 ticks pone espectador **solo si `stateOf(id).alive` sigue false** (Review Focus 1);
    - `leave(QUIT/WORLD_CHANGE)` en `running()` → `onLeave` con la última posición.
  - **Huida:** `escape` → `onEscape` antes de limpiar.
  - **`reset()`:**
    - con hooks: se arma `PhaseResult` (`aborted = state != ENDING` al entrar) y se sacan los participantes sin `backToHub` (motivo `RESET`, sin hub);
    - `cleanupWorld` → `onCleanup`; `this.hooks = null`; luego `onFinish(result)`.
  - **`Director.tick`:**
    - si está suspendido, solo actualiza el aislamiento;
    - `lurePlayer` fuerza el objetivo y escala la distancia del siguiente `teleport`/`appear` (mínimo 7);
    - `lureTo` coloca el cuerpo a 6–10 bloques de `loc` con `SlenderBody.ground` y `SpotRules`, si no lo observan;
    - ambos se consumen al usarse.
  - `GameListener` llama a `game.interactBlock` / `pickup` / `interactEntity` antes de su lógica actual y para si devuelven true.
- [ ] **Step 4:** `./build.sh` → PASS, y los 90 tests de antes siguen en verde.
- [ ] **Step 5:** Commit `feat(noche): punto de enganche PhaseHooks en Game`.

### Task 10: Habilidades de Slender (`NightPowers`)

**Files:**
- Create: `night/NightPowers.java`
- Test: `src/test/java/dev/two/slender/night/NightPowersMathTest.java`

**Interfaces:**
- Consumes `NightPlan`, `NightSettings`, `AbilityClock`, `SprintEar`, `HeadLedger`, `Game` (Task 9), `DirectorMath`.
- Produces:
  - `NightPowers(NightPlan plan, NightSettings s, int phase, HeadLedger heads, Function<UUID, Optional<Item>> headEntity)`;
  - `void second(Game g)` (luces, fogonazo, suplantar, cazar cabezas);
  - `void tick(Game g)` (correr, persecución en curso, pila al mirar, suplantación en curso);
  - `void cleanup(Game g)`;
  - estáticos puros: `static double chaseStep(double speed, double dtSeconds)` y `static Vec3 mimicSpot(Vec3 target, Vec3 view, double min, double max, Random r)`.
- **Comportamiento** (valores en spec §6):
  - **Luces:** al ver que el cuerpo apareció o se movió > 5 bloques desde el último segundo, `g.lightsOut(body, radius, tick + rand(min..max)·20)`. Si un jugador lo mira con la linterna encendida, `flashlights.drain(id, lookDrain·0,1)` cada tick de 2.
  - **Correr:** `SprintEar.update(…, plan.sprintThreshold(phase))` → `director.lurePlayer(id, distance)` y mensaje `noche-te-oigo`.
  - **Cabezas:**
    - cada cabeza en el suelo cuenta segundos sin vivos a ≤ radius;
    - a los `seconds`, si no lo observan, `director.lureTo(head)`;
    - si el cuerpo está a ≤ 3 de la cabeza durante `grabSeconds`: la quita, `heads.stolen(owner)`, sonido y `noche-cabeza-robada`.
  - **Fogonazo:** `title` de 24 `█` blancos con `Title.Times(0, 6, 30)` ticks, ceguera y lentitud II durante `seconds`, sonido de tono alto y `stat.bump(staticBump)`.
  - **Suplantar:**
    - `lock(MIMIC)` y `director.suspend(true)`; el cuerpo se quita;
    - se crea un `Mannequin` con el perfil de otro vivo en `mimicSpot`, que anda a 1 bloque/s hacia el objetivo;
    - si un vivo llega a ≤ reveal: `scares.jumpscare` y `bump`;
    - fin: el mannequin se quita, `suspend(false)` y `unlock`.
  - **Perseguir:**
    - al empezar a ser observado por el objetivo a ≤ radius: tirada de `chance`, `lock(CHASE)` y `suspend(true)`;
    - cada tick, `body.moveTo` hacia el objetivo con `DirectorMath.subSteps` y `chaseStep(speed, 0.1)`;
    - atrapa con la regla de `catchDistance`;
    - fin a los `duration` s: `body.remove()`, `suspend(false)` y `unlock`.
  - Cada habilidad solo actúa si `plan.abilities(phase)` la contiene, y su probabilidad por segundo es `chance · plan.frequency(a, phase)`.

- [ ] **Step 1: Test**
  - `chaseStep(3.5, 0.1)` es 0.35.
  - `mimicSpot` da una distancia horizontal entre min y max, y queda detrás o a un lado de `view` (producto escalar ≤ 0,5) en 200 semillas.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): habilidades nuevas de Slender`.

### Task 11: Una fase de la noche (`NightPhase`): cabezas, caldero, misión y Snitch

**Files:**
- Create: `night/NightPhase.java`, `night/HeadItems.java`
- Test: `src/test/java/dev/two/slender/night/HeadItemsTest.java`

**Interfaces:**
- Consumes todo lo anterior y `HogwartsBuilder` (`altarWorld`, `floorWorld`, `floorCount`).
- Produces:
  - `HeadItems`:
    - `static final String KEY = "head"` (PDC `slender:head`);
    - `static String displayName(String player)` → "Cabeza de " + player;
    - `static ItemStack make(Player owner)`, `static Optional<UUID> ownerOf(ItemStack)`.
  - `NightPhase implements PhaseHooks`:
    - `NightPhase(NightManager m, NightState s, NightPlan plan, NightSettings set, HogwartsBuilder section, Set<UUID> spectators)`;
    - `Set<UUID> rescued()`, `Set<UUID> lostNow()` (los usa `NightManager` tras `onFinish`).
- **Comportamiento:**
  - **`startsAsSpectator(id)`:** `spectators.contains(id)` (los perdidos de la fase anterior).
  - **`onStart`:**
    - coloca el caldero (`soul_campfire` y `cauldron` encima en `altarWorld()`);
    - crea `HeadLedger`, `Ritual` y `NightPowers`;
    - si `mission.appearsIn(phase)`, coloca su objeto en una celda `floorWorld(r)` a ≥ 20 bloques de los spawns:
      - NORRIS: `Cat` sentada, `setAI(false)`, invulnerable, muda, tag `slender_night`;
      - CROMOS: `Item` papel con nombre `"Cromo de rana de chocolate"` y PDC `slender:cromo`;
      - BRASEROS: lana del color de la casa y `campfire[lit=false]` encima.
    - Si `snitchPhase == phase` y no se ha cogido, programa la Snitch.
  - **`onKill`:** `heads.died` → suelta las cabezas (`Item` con brillo, `setUnlimitedLifetime(true)`, `setPickupDelay(20)`) en la posición del muerto.
  - **`onLeave`:** igual, en la última posición.
  - **`onPickup`:**
    - cabeza → `heads.pickUp`; se deja coger solo si devuelve true; si no, se cancela;
    - cromo → `mission.found`;
    - true para que `Game` no siga.
  - **`onInteractBlock`:**
    - caldero con cabeza en la mano → `ritual.start` y mensaje;
    - brasero apagado → encenderlo y `mission.found`;
    - true si lo ha manejado.
  - **`onInteractEntity`:** gata → `mission.found`.
  - **`onSecond`:**
    - `ritual.tick` (DONE → `g.revive(owner, altar + 1 hacia arriba, altar.battery)` y `heads.revived`; mientras RUNNING, `director.lureTo(altar)` una vez y partículas);
    - `powers.second`;
    - Snitch: aparece y desaparece según `everyMin/everyMax/duration`;
    - una cabeza con y < `map.fallY()` se lleva junto al caldero.
  - **`onTick`:** `powers.tick` y la Snitch (`SnitchMotion.step` con `SpotRules::allows`, `ItemDisplay` y partículas `WAX_ON` en su posición; si la atrapan, `state.snitchCaught = true` y `Candles.gain`).
  - **`onEscape`:** `heads.escaped(p)` y quita esas cabezas del inventario.
  - **`onCleanup`:** quita el caldero, las cabezas, la gata, los cromos, los braseros, la Snitch y el mannequin, y llama a `powers.cleanup` (Review Focus 3).
  - **`onFinish`:** `mission.phaseEnded(phase)`; si se cumple y `!missionRewarded`, `Candles.gain` y `missionRewarded = true`; después `manager.phaseFinished(this, result)`.

- [ ] **Step 1: Test** `HeadItemsTest`: `displayName("Ana")` es `"Cabeza de Ana"`; `KEY` es `"head"`. Las partes con `ItemStack` necesitan servidor.
- [ ] **Step 2:** FAIL. **Step 3:** implementar. **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): fase con cabezas, caldero, misión y Snitch`.

### Task 12: Sala de los Menesteres y 8.ª puerta

**Files:**
- Create: `hub/Sala.java`
- Modify: `hub/HubBuilder.java` (llamar a `Sala.build`), `hub/Anden934.java` (8.ª puerta abierta con zona `noche`), `map/MapIds.java` (reservar `noche`), `hub/HubManager.java` (`door(p,"noche")` → `plugin.night().openMenu(p)`)
- Test: `src/test/java/dev/two/slender/hub/SalaTest.java`, `Anden934Test` (añadir)

**Interfaces:**
- Produces:
  - `Sala` (relativo a `HubBuilder.ORIGIN`, caja x −7…7, y 0…7, z 24…38):
    - `static Zone readyCircle()`, `static Zone candleShrine()`, `static Zone exitDoor()`;
    - `static Loc spawn(Builder b)`, `static List<BlockPos> boardSigns()` (8 carteles, de arriba abajo y de izquierda a derecha);
    - `static void build(Builder b)`.
  - `Anden934`:
    - `static final String NIGHT_DOOR = "noche"`, `static Zone nightDoor()` (relativa: x 25…27, y 3…5, z −18);
    - `build` la incluye en las puertas devueltas;
    - el cartel de la 8.ª puerta pasa a "LA NOCHE", "ENTERA", "", "8.ª puerta".

- [ ] **Step 1: Tests**
  - Las zonas de la sala están dentro de la caja, no se solapan entre ellas y quedan dentro del radio de limpieza de la estación (41).
  - La sala no se solapa con los arcos (z 17…20) ni con los muros de la estación (z = 20/21).
  - `nightDoor()` no se solapa con las 7 puertas del Expreso y está dentro de x 0…34, z −24…−4.
  - `MapIds.valid("noche")` es false.
- [ ] **Step 2:** FAIL. **Step 3: Implementar**
  - La sala, en `HubBuilder.build`, después de los arcos:
    - **Sala:** suelo de `dark_oak_planks` y `polished_andesite`; paredes de `stone_bricks` con estanterías; techo de `dark_oak_slab`; chimenea con `campfire` detrás de `iron_bars`; muchas `candle[lit=true]`; sin abertura al exterior.
    - **Zonas:** círculo de listos con anillo de `soul_lantern`; altar de la vela (`candle` sobre `chiseled_stone_bricks`); puerta de salida (hueco de 1×2 con hormigón negro detrás, como los arcos).
    - **Tablón:** 8 carteles en la pared norte.
  - La 8.ª puerta del Expreso queda abierta (sin ladrillos) y con su zona.
- [ ] **Step 4:** PASS.
- [ ] **Step 5:** Commit `feat(noche): Sala de los Menesteres y 8.ª puerta`.

### Task 13: `NightManager`, menú, eventos y comandos

**Files:**
- Create: `night/NightManager.java`, `night/NightMenu.java`
- Modify:
  - `SlenderPlugin.java` (crear `NightStore` en `plugins/Slender/noches`, `NightManager`, `night()`, cerrar al apagar);
  - `ui/MenuHolder.java` (`Type.NIGHT`), `ui/MenuGui.java` (pasar los clics de `NIGHT` a `NightMenu`);
  - `listener/GameListener.java` (quit y cambio de mundo → `night().left(p)`);
  - `command/SlenderCommand.java` (`noche estado|terminar|fase <n>`, más el tab).
- Test: `src/test/java/dev/two/slender/night/NightMenuTest.java`

**Interfaces:**
- Consumes todo lo anterior.
- Produces `NightManager`:
  - `openMenu(Player)`, `newNight(Player)`, `continueNight(Player)`, `join(Player)`;
  - `void tick()` (cada 10 ticks: zonas de la sala, cuenta atrás, sección ocupada);
  - `void phaseFinished(NightPhase phase, PhaseResult r)`;
  - `void left(Player)`, `void shutdown()` (anula la fase y guarda);
  - `Optional<NightState> active()`, `String status()`, `boolean setPhase(int n)`, `void terminate()`.
- `NightMenu` puro, para el texto de los huecos: `static List<String> options(boolean activeExists, boolean isMember, Optional<NightState> saved)` → claves `noche-menu-nueva`, `noche-menu-continuar`, `noche-menu-unirse` en el orden en que salen.
- **Flujo** (spec §4):
  - **Empezar una fase:**
    - `GameManager.startPhase(NightPlan.sectionOf(phase), presentes, new NightPhase(..., perdidos))`;
    - si falla por estar ocupada, `noche-esperando` y se reintenta cada segundo;
    - si falta Hogwarts, `noche-sin-hogwarts`.
  - **Al crear la noche,** `noche-premisa` en el chat.
  - **Al empezar,** título `noche-fase-titulo` y `noche-diario-<phase>` en el chat.
  - **`phaseFinished`:**
    1. `PhaseOutcome(r.aborted() || nadie online, r.winner()==SURVIVORS, phase.rescued(), phase.lostNow())` → `NightRules.afterPhase`.
    2. Según `Next`:
       - NEXT_PHASE / REPEAT: todos a `Sala.spawn` con `HubManager.sendTo`, tablón actualizado y `NightStore.save`;
       - NIGHT_WON / NIGHT_LOST: final en el chat y título, `delete` y todos a la estación;
       - ANNULLED: `save` (sin cambios) y se libera la noche.
  - **Sala:**
    - círculo: todos los presentes no perdidos dentro → 10 s; o alguno dentro 30 s seguidos → empieza;
    - altar de la vela: 3 s dentro con perdidos y velas → `NightRules.spendForLost` y `noche-vela-gastada`;
    - salida: `save`, a la estación, y si no queda nadie en la sala, se libera.
  - **Comandos:**
    - `estado` → `status()` (fase, velas, miembros, misión `progress/goal`);
    - `terminar` → `terminate()` (como `shutdown`);
    - `fase <n>` → solo con la noche esperando en la sala.

- [ ] **Step 1: Test** `NightMenuTest.options`:
  - sin noche activa ni guardada → `[nueva]`;
  - con guardada del jugador → `[nueva, continuar]`;
  - con noche activa → `[unirse]`.
- [ ] **Step 2:** FAIL. **Step 3:** implementar y cablear. **Step 4:** `./build.sh` → PASS (todos).
- [ ] **Step 5:** Commit `feat(noche): gestor de la noche, menú y comandos`.

### Task 14: Versión 1.9.0, documentación y entrega

**Files:**
- Modify: `src/main/resources/plugin.yml` (`version: 1.9.0`), `docs/INSTALACION.md` (apartado "Novedades 1.9: LA NOCHE ENTERA")
- Create (proyecto): `noche-entera/entrega.md` y `noche-entera/plan-implementacion.md` (este plan)

- [ ] **Step 1: `INSTALACION.md`**
  - Qué es, cómo se entra (8.ª puerta), la sala, velas, cabezas y caldero, misión y Snitch, guardado.
  - `/slender build vestibulo` (sala y 8.ª puerta) y `/slender build hogwarts` (calderos).
  - Comandos de admin y el apartado `noche:` del config para ajustar.
  - La corrección de velas de la Task 1.
- [ ] **Step 2: Verificación**
  - `./build.sh` → OK y `python3 -m pytest tools/hogwarts/tests -q` en verde.
  - `unzip -p build/Slender.jar plugin.yml` → 1.9.0.
- [ ] **Step 3: Entrega** en `IdeaProjects\slender\`: `Slender.jar`, `INSTALACION.md` y `codigo-1.8.zip` (sin `libs/`, `build/` ni `tools/hogwarts/input`).
- [ ] **Step 4: `noche-entera/entrega.md`**
  - Qué hay, cómo instalar y las desviaciones (las 4 de Global Constraints).
  - **Lista de comprobación en el servidor** (spec §13), más:
    - velas que parpadean en Hogwarts;
    - repetir una fase dos veces y que no quede nada de la anterior;
    - que los perdidos ven la fase y no vuelven a la estación.
- [ ] **Step 5:** Commit `release: Slender 1.9.0 con LA NOCHE ENTERA`.
