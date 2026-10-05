package dev.two.slender.night;

import dev.two.slender.SlenderPlugin;
import dev.two.slender.config.Msg;
import dev.two.slender.game.Game;
import dev.two.slender.game.PhaseResult;
import dev.two.slender.hub.HubBuilder;
import dev.two.slender.hub.Sala;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.Zone;
import dev.two.slender.map.builders.hogwarts.HogwartsBuilder;
import dev.two.slender.ui.MenuHolder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * LA NOCHE ENTERA en el servidor (spec §4, §9): una sola noche activa, la Sala de los Menesteres entre fases, el
 * arranque de cada fase con {@link NightPhase} y lo que pasa al acabar ({@link NightRules}).
 */
public final class NightManager {
    /** Pasos de 10 ticks (0,5 s). */
    private static final int READY_STEPS = 20;
    private static final int ANY_STEPS = 60;
    private static final int SHRINE_STEPS = 6;

    private final SlenderPlugin plugin;
    private final NightStore store;
    private final NightPlan plan;
    private final NightSettings set;
    private final Msg msg;
    private final Random random = new Random();
    private final Map<UUID, List<String>> menus = new HashMap<>();
    private final Map<UUID, Integer> shrine = new HashMap<>();
    private final Set<UUID> shrineUsed = new HashSet<>();
    private NightState active;
    private NightPhase current;
    private BukkitTask task;
    private long step;
    private int countdown = -1;
    private long anyInSince = -1;
    private boolean pending;
    private List<String> lastBoard = List.of();
    private boolean worksChecked;

    public NightManager(SlenderPlugin plugin, NightStore store) {
        this.plugin = plugin;
        this.store = store;
        this.set = plugin.cfg().night;
        this.plan = new NightPlan(this.set);
        this.msg = plugin.msg();
    }

    public void start() {
        if (this.task == null) {
            this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, this::tick, 20L, 10L);
        }
    }

    public Optional<NightState> active() {
        return Optional.ofNullable(this.active);
    }

    // ------------------------------------------------------------------ menú de la 8.ª puerta

    public void openMenu(Player p) {
        UUID id = p.getUniqueId();
        Optional<NightState> saved = this.active != null ? Optional.empty() : this.store.latestFor(id);
        List<String> opts = NightMenu.options(this.active != null,
                this.active != null && this.active.members.containsKey(id), saved);
        MenuHolder h = new MenuHolder(MenuHolder.Type.NIGHT, null);
        Inventory inv = Bukkit.createInventory(h, 27, this.msg.get("noche-menu-titulo"));
        h.setInventory(inv);
        int[] slots = NightMenu.slots(opts.size());
        for (int i = 0; i < opts.size(); i++) {
            String key = opts.get(i);
            Material icon = switch (key) {
                case NightMenu.CONTINUE -> Material.WRITABLE_BOOK;
                case NightMenu.JOIN -> Material.SOUL_LANTERN;
                default -> Material.CANDLE;
            };
            Component name = saved.isPresent() && key.equals(NightMenu.CONTINUE)
                    ? this.msg.get(key, "phase", String.valueOf(saved.get().phase), "candles", String.valueOf(saved.get().candles))
                    : this.msg.get(key);
            ItemStack it = new ItemStack(icon);
            it.editMeta(m -> m.displayName(name.decoration(TextDecoration.ITALIC, false)));
            inv.setItem(slots[i], it);
        }
        this.menus.put(id, opts);
        p.openInventory(inv);
    }

    public void click(Player p, int slot) {
        List<String> opts = this.menus.getOrDefault(p.getUniqueId(), List.of());
        int[] slots = NightMenu.slots(opts.size());
        for (int i = 0; i < opts.size(); i++) {
            if (slots[i] == slot) {
                String key = opts.get(i);
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    p.closeInventory();
                    switch (key) {
                        case NightMenu.NEW -> this.newNight(p);
                        case NightMenu.CONTINUE -> this.continueNight(p);
                        default -> this.join(p);
                    }
                });
                return;
            }
        }
    }

    public void newNight(Player p) {
        if (this.active != null) {
            this.msg.send(p, "noche-ocupada");
            return;
        }
        if (!this.hogwartsBuilt()) {
            this.msg.send(p, "noche-sin-hogwarts");
            return;
        }
        NightState s = NightState.create(p.getUniqueId(), p.getName(), this.random);
        s.candles = this.set.candlesStart();
        this.store.save(s);
        this.activate(s);
        if (this.toSala(p)) {
            this.msg.send(p, "noche-premisa");
        }
    }

    public void continueNight(Player p) {
        if (this.active != null) {
            this.msg.send(p, "noche-ocupada");
            return;
        }
        if (!this.hogwartsBuilt()) {
            this.msg.send(p, "noche-sin-hogwarts");
            return;
        }
        Optional<NightState> s = this.store.latestFor(p.getUniqueId());
        if (s.isEmpty()) {
            this.openMenu(p);
            return;
        }
        this.activate(s.get());
        this.toSala(p);
    }

    public void join(Player p) {
        if (this.active == null) {
            this.openMenu(p);
            return;
        }
        UUID id = p.getUniqueId();
        if (!NightRules.canJoin(this.active, id, this.set.maxMembers())) {
            this.msg.send(p, "noche-llena", "max", String.valueOf(this.set.maxMembers()));
            return;
        }
        if (!this.active.members.containsKey(id)) {
            this.active.members.put(id, p.getName());
            if (this.current != null) {
                // a mitad de fase: en disco solo el miembro nuevo, no lo que va pasando en la fase
                this.store.addMember(this.active.id, id, p.getName());
            } else {
                this.store.save(this.active);
            }
        }
        this.toSala(p);
    }

    private void activate(NightState s) {
        this.active = s;
        this.current = null;
        this.resetTimers();
        this.lastBoard = List.of();
    }

    private boolean hogwartsBuilt() {
        return NightPlan.ORDER.stream().allMatch(this.plugin.maps()::containsKey);
    }

    // ------------------------------------------------------------------ sala

    /** Cada 10 ticks: salida, altar de la vela, círculo de listos y tablón. */
    public void tick() {
        this.step++;
        if (!this.worksChecked) {
            this.recoverWorks();
        }
        this.exitDoor();
        if (this.active == null || this.current != null) {
            return;
        }
        List<Player> present = this.present(null);
        if (present.isEmpty()) {
            this.release();
            return;
        }
        this.shrine(present);
        if (this.pending) {
            if (this.step % 2 == 0) {
                this.beginPhase();
            }
        } else {
            this.ready(present);
        }
        if (this.active != null && this.current == null && this.step % 2 == 0) {
            this.board();
        }
    }

    /**
     * Si el servidor se cayó a mitad de fase, lo que puso (caldero, brasero) sigue en el castillo: se deshace al
     * arrancar, en cuanto el mundo está cargado.
     */
    private void recoverWorks() {
        Optional<Map.Entry<String, List<String>>> works = this.store.loadWorks();
        if (works.isEmpty()) {
            this.worksChecked = true;
            return;
        }
        World w = Bukkit.getWorld(works.get().getKey());
        if (w == null) {
            return; // aún no está cargado: se reintenta en el siguiente tick
        }
        for (Map.Entry<BlockPos, String> e : BlockRestore.parse(works.get().getValue()).restoreOrder()) {
            BlockPos p = e.getKey();
            try {
                w.getBlockAt(p.x(), p.y(), p.z()).setBlockData(Bukkit.createBlockData(e.getValue()), false);
            } catch (IllegalArgumentException ex) {
                this.plugin.getLogger().warning("Bloque sin deshacer en " + p + ": " + e.getValue());
            }
        }
        this.store.clearWorks();
        this.worksChecked = true;
        this.plugin.getLogger().info("Deshecho lo que dejó a medias una fase de la noche.");
    }

    /** Cualquiera en el hueco de la salida vuelve a la estación (haya noche o no); si es miembro, se guarda. */
    private void exitDoor() {
        World w = this.hubWorld();
        if (w == null) {
            return;
        }
        Zone exit = Sala.absolute(Sala.exitDoor());
        for (Player p : List.copyOf(w.getPlayers())) {
            UUID id = p.getUniqueId();
            if (in(exit, p) && this.plugin.hub().inside(id) && this.plugin.games().gameOf(id).isEmpty()) {
                if (this.active != null && this.current == null && this.active.members.containsKey(id)) {
                    this.store.save(this.active);
                }
                this.plugin.hub().backToHub(p);
            }
        }
    }

    /** Todos los que quedan en la sala, a la estación (la noche se ha acabado o se ha terminado). */
    private void evacuateSala() {
        World w = this.hubWorld();
        if (w == null) {
            return;
        }
        Zone box = Sala.absolute(Sala.box());
        for (Player p : List.copyOf(w.getPlayers())) {
            UUID id = p.getUniqueId();
            if (in(box, p) && this.plugin.hub().inside(id) && this.plugin.games().gameOf(id).isEmpty()) {
                this.plugin.hub().backToHub(p);
            }
        }
    }

    private World hubWorld() {
        return this.plugin.hub().hub().map(h -> Bukkit.getWorld(h.world())).orElse(null);
    }

    /** Miembros de la noche que están en la sala (dentro de la estación y en su caja). */
    private List<Player> present(Player except) {
        List<Player> out = new ArrayList<>();
        if (this.active == null) {
            return out;
        }
        Zone box = Sala.absolute(Sala.box());
        for (UUID id : this.active.members.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p != except && p.isOnline() && this.plugin.hub().inside(id)
                    && this.plugin.games().gameOf(id).isEmpty() && in(box, p)) {
                out.add(p);
            }
        }
        return out;
    }

    private static boolean in(Zone z, Player p) {
        Location l = p.getLocation();
        return z.contains(l.getX(), l.getY(), l.getZ());
    }

    private void ready(List<Player> present) {
        Zone ring = Sala.absolute(Sala.readyCircle());
        List<Player> eligible = present.stream().filter(p -> !this.active.lost.containsKey(p.getUniqueId())).toList();
        if (eligible.isEmpty()) {
            eligible = present;
        }
        boolean all = eligible.stream().allMatch(p -> in(ring, p));
        boolean any = present.stream().anyMatch(p -> in(ring, p));
        if (!any) {
            this.anyInSince = -1;
        } else if (this.anyInSince < 0) {
            this.anyInSince = this.step;
        }
        if (any && this.step - this.anyInSince >= ANY_STEPS) {
            this.beginPhase();
            return;
        }
        if (!all) {
            this.countdown = -1;
            return;
        }
        this.countdown = this.countdown < 0 ? READY_STEPS : this.countdown - 1;
        if (this.countdown <= 0) {
            this.beginPhase();
        } else if (this.countdown % 2 == 0) {
            Component bar = this.msg.get("noche-listos", "seconds", String.valueOf(this.countdown / 2));
            for (Player p : present) {
                p.sendActionBar(bar);
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.MASTER, 0.6F, 0.8F);
            }
        }
    }

    /** 3 s en el altar con perdidos y velas: −1 vela y vuelve el perdido que más lleva (antes los que están aquí). */
    private void shrine(List<Player> present) {
        Zone z = Sala.absolute(Sala.candleShrine());
        for (Player p : present) {
            UUID id = p.getUniqueId();
            if (!in(z, p)) {
                this.shrine.remove(id);
                this.shrineUsed.remove(id);
                continue;
            }
            if (this.shrineUsed.contains(id) || this.shrine.merge(id, 1, Integer::sum) < SHRINE_STEPS) {
                continue;
            }
            this.shrine.remove(id);
            this.shrineUsed.add(id);
            Set<UUID> here = new HashSet<>();
            present.forEach(q -> here.add(q.getUniqueId()));
            Optional<UUID> who = this.active.lost.entrySet().stream()
                    .sorted(Comparator.<Map.Entry<UUID, Integer>, Boolean>comparing(e -> !here.contains(e.getKey()))
                            .thenComparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey).findFirst();
            if (who.isEmpty() || !NightRules.spendForLost(this.active, who.get())) {
                this.msg.send(p, "noche-velas", "candles", String.valueOf(this.active.candles));
                continue;
            }
            this.store.save(this.active);
            String name = this.active.members.getOrDefault(who.get(), "?");
            for (Player q : present) {
                this.msg.send(q, "noche-vela-gastada", "player", name);
                q.playSound(q.getLocation(), Sound.BLOCK_CANDLE_EXTINGUISH, SoundCategory.MASTER, 1F, 0.8F);
            }
        }
    }

    private void release() {
        if (this.active != null && this.current == null) {
            this.store.save(this.active);
        }
        this.active = null;
        this.current = null;
        this.resetTimers();
        this.writeBoard(java.util.Collections.nCopies(32, ""));
        this.lastBoard = List.of();
    }

    private void resetTimers() {
        this.countdown = -1;
        this.anyInSince = -1;
        this.pending = false;
        this.shrine.clear();
        this.shrineUsed.clear();
    }

    private Location salaSpawn() {
        World w = this.hubWorld();
        if (w == null) {
            return null;
        }
        Loc l = Sala.spawn(HubBuilder.ORIGIN);
        return new Location(w, l.x(), l.y(), l.z(), l.yaw(), 0F);
    }

    /** A la sala (pasando por la estación si hace falta). */
    private boolean toSala(Player p) {
        UUID id = p.getUniqueId();
        Optional<Game> g = this.plugin.games().gameOf(id);
        if (g.isPresent() && g.get().hooks() == null) {
            this.plugin.games().leave(p);
        }
        Location spawn = this.salaSpawn();
        if (spawn == null || !this.plugin.hub().enter(p)) {
            if (spawn == null) {
                this.msg.send(p, "hub-missing");
            }
            return false;
        }
        this.plugin.hub().sendTo(p, spawn);
        return true;
    }

    // ------------------------------------------------------------------ fases

    private void beginPhase() {
        this.countdown = -1;
        this.anyInSince = -1;
        NightState s = this.active;
        String section = NightPlan.sectionOf(s.phase);
        Optional<MapBuilder> b = this.plugin.builds().builder(section);
        List<Player> players = this.present(null);
        if (players.isEmpty()) {
            this.pending = false;
            return;
        }
        if (!(b.orElse(null) instanceof HogwartsBuilder hb) || !this.plugin.maps().containsKey(section)) {
            this.pending = false;
            players.forEach(p -> this.msg.send(p, "noche-sin-hogwarts"));
            return;
        }
        Set<UUID> spectators = new LinkedHashSet<>();
        for (Player p : players) {
            if (s.lost.containsKey(p.getUniqueId())) {
                spectators.add(p.getUniqueId());
            }
        }
        if (spectators.size() == players.size()) {
            // sin nadie vivo en la sala, los perdidos vuelven: una fase sin supervivientes no tiene sentido
            spectators.forEach(s.lost::remove);
            spectators.clear();
        }
        this.store.save(s);
        NightPhase phase = new NightPhase(this::phaseFinished, s, this.plan, this.set, this.plugin.cfg(), hb, spectators,
                players.size() - spectators.size());
        String worldName = this.plugin.maps().get(section).world();
        phase.journal(r -> {
            if (r.isEmpty()) {
                this.store.clearWorks();
            } else {
                this.store.saveWorks(worldName, r.serialize());
            }
        });
        this.current = phase;
        Optional<String> err = this.plugin.games().startPhase(section, players, phase);
        if (err.isPresent()) {
            this.current = null;
            if ("busy".equals(err.get())) {
                if (!this.pending) {
                    String name = this.plugin.menu().nameOf(section);
                    players.forEach(p -> this.msg.send(p, "noche-esperando", "name", name));
                }
                this.pending = true;
            } else {
                this.pending = false;
                players.forEach(p -> this.msg.send(p, "noche-sin-hogwarts"));
            }
            return;
        }
        this.pending = false;
        if (this.current != phase) {
            return; // la fase ya ha acabado (p. ej. nadie pudo entrar)
        }
        Component title = this.msg.get("noche-fase-titulo", "n", String.valueOf(s.phase), "name", this.plugin.menu().nameOf(section));
        Title t = Title.title(title, Component.empty(), Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4),
                Duration.ofSeconds(1)));
        for (Player p : players) {
            p.showTitle(t);
            this.msg.send(p, "noche-diario-" + s.phase);
            if (spectators.contains(p.getUniqueId())) {
                this.msg.send(p, "noche-perdido", "player", p.getName());
            }
        }
    }

    /** Lo llama la fase al acabar (desde Game.reset). */
    public void phaseFinished(NightPhase phase, PhaseResult r) {
        if (phase != this.current || this.active == null) {
            return;
        }
        this.current = null;
        NightState s = this.active;
        List<Player> online = r.participants().stream().map(Bukkit::getPlayer).filter(Objects::nonNull)
                .filter(Player::isOnline).toList();
        if (phase.missionFailedNow()) {
            online.forEach(p -> this.msg.send(p, "noche-mision-fallida"));
        }
        boolean nobody = s.members.keySet().stream().map(Bukkit::getPlayer).noneMatch(Objects::nonNull);
        PhaseOutcome o = new PhaseOutcome(r.aborted() || phase.aborted() || nobody || online.isEmpty(),
                r.winner() == Game.Winner.SURVIVORS, phase.rescued(), phase.lostNow());
        switch (NightRules.afterPhase(s, o, this.set.candlesMax())) {
            case NEXT_PHASE -> {
                this.store.save(s);
                for (Player p : online) {
                    this.msg.send(p, "noche-fase-ganada");
                    this.toSala(p);
                }
                for (UUID lost : s.lost.keySet()) {
                    String name = s.members.getOrDefault(lost, "?");
                    online.forEach(p -> this.msg.send(p, "noche-perdido", "player", name));
                }
            }
            case REPEAT -> {
                this.store.save(s);
                for (Player p : online) {
                    this.msg.send(p, "noche-fase-perdida", "candles", String.valueOf(s.candles));
                    this.toSala(p);
                }
            }
            case NIGHT_WON -> this.ending(s, online, "noche-final-bueno", NamedTextColor.GOLD);
            case NIGHT_LOST -> this.ending(s, online, "noche-final-malo", NamedTextColor.DARK_RED);
            case ANNULLED -> {
                // lo guardado es la noche al empezar la fase: lo que pasó en ella no cuenta
                online.forEach(this.plugin.hub()::backToHub);
                this.resetTimers();
                if (phase.aborted()) {
                    this.active = null;
                    this.evacuateSala();
                } else {
                    // se fueron todos de la fase: si queda alguien esperando en la sala, la noche sigue (spec §9)
                    this.active = this.store.load(s.id).orElse(null);
                }
            }
        }
        this.lastBoard = List.of();
    }

    private void ending(NightState s, List<Player> online, String key, NamedTextColor color) {
        String text = plain(this.msg.get(key));
        int dot = text.indexOf('.');
        Component head = Component.text(dot > 0 ? text.substring(0, dot) : text, color);
        Title t = Title.title(head, Component.empty(), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5),
                Duration.ofSeconds(2)));
        for (Player p : online) {
            this.msg.send(p, key);
            p.showTitle(t);
            this.plugin.hub().backToHub(p);
        }
        this.store.delete(s.id);
        this.active = null;
        this.resetTimers();
        this.evacuateSala();
        this.writeBoard(java.util.Collections.nCopies(32, ""));
    }

    // ------------------------------------------------------------------ tablón

    private void board() {
        NightState s = this.active;
        String section = NightPlan.sectionOf(s.phase);
        String mission = plain(this.msg.get("noche-mision-" + s.mission.type().name().toLowerCase(Locale.ROOT)));
        String state = s.mission.done() ? "cumplida" : s.mission.failed() ? "fallida"
                : s.mission.progress() + "/" + s.mission.goal();
        String diary = plain(this.msg.get("noche-diario-" + s.phase));
        int colon = diary.indexOf(':');
        diary = colon >= 0 ? diary.substring(colon + 1).trim() : diary;
        List<String> lines = NightMenu.board(s.phase, this.plugin.menu().nameOf(section), s.candles, this.set.candlesMax(),
                mission, state, diary);
        if (!lines.equals(this.lastBoard)) {
            this.writeBoard(lines);
            this.lastBoard = lines;
        }
    }

    private void writeBoard(List<String> lines) {
        World w = this.hubWorld();
        if (w == null) {
            return;
        }
        BlockPos o = HubBuilder.ORIGIN;
        List<BlockPos> signs = Sala.boardSigns();
        for (int i = 0; i < signs.size(); i++) {
            BlockPos rel = signs.get(i);
            if (!w.isChunkLoaded(o.x() + rel.x() >> 4, o.z() + rel.z() >> 4)) {
                continue;
            }
            Block b = w.getBlockAt(o.x() + rel.x(), o.y() + rel.y(), o.z() + rel.z());
            if (b.getState() instanceof Sign sign) {
                SignSide side = sign.getSide(Side.FRONT);
                for (int j = 0; j < 4; j++) {
                    side.line(j, Component.text(lines.get(i * 4 + j)));
                }
                side.setGlowingText(true);
                sign.update(false, false);
            }
        }
    }

    private static String plain(Component c) {
        return PlainTextComponentSerializer.plainText().serialize(c);
    }

    // ------------------------------------------------------------------ salidas, apagado y comandos

    /** Al salir del servidor o del mundo (después de que la estación lo haya soltado). */
    public void left(Player p) {
        UUID id = p.getUniqueId();
        this.menus.remove(id);
        this.shrine.remove(id);
        this.shrineUsed.remove(id);
        if (this.active != null && this.current == null && this.active.members.containsKey(id)
                && this.present(p).isEmpty()) {
            this.release();
        }
    }

    /** Al apagar o recargar: la fase en curso se anula; la noche queda guardada como estaba al empezarla. */
    public void shutdown() {
        this.terminate();
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public void terminate() {
        if (this.active == null) {
            return;
        }
        if (this.current != null) {
            NightPhase phase = this.current;
            phase.abort();
            this.plugin.games().stop(phase.sectionId());
            // stop → reset → phaseFinished (ANNULLED) los manda a la estación y libera la noche
            if (this.current == phase) {
                this.current = null;
                this.active = null;
                this.resetTimers();
                this.evacuateSala();
            }
            return;
        }
        this.release();
        this.evacuateSala();
    }

    public String status() {
        NightState s = this.active;
        if (s == null) {
            return null;
        }
        String mission = s.mission.type().name().toLowerCase(Locale.ROOT) + " " + s.mission.progress() + "/" + s.mission.goal()
                + (s.mission.failed() ? " (fallida)" : "");
        return this.msg.fill("noche-estado", "phase", s.phase + (this.current != null ? " (en juego)" : ""),
                "candles", String.valueOf(s.candles), "members", String.join(", ", s.members.values()), "mission", mission);
    }

    public boolean setPhase(int n) {
        if (this.active == null || this.current != null || n < 1 || n > NightPlan.ORDER.size()) {
            return false;
        }
        this.active.phase = n;
        this.resetTimers();
        this.store.save(this.active);
        this.lastBoard = List.of();
        return true;
    }
}
