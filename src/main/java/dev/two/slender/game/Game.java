// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.block.data.BlockData;
import dev.two.slender.logic.ChunkArea;
import org.bukkit.entity.Pose;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.entity.Mannequin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import dev.two.slender.map.BlockPos;
import dev.two.slender.logic.EscapeTime;
import org.bukkit.entity.Item;
import java.util.function.Function;
import dev.two.slender.logic.DirectorMath;
import org.bukkit.command.CommandSender;
import dev.two.slender.map.PageSpot;
import org.bukkit.Location;
import dev.two.slender.logic.PageSelector;
import dev.two.slender.map.Loc;
import java.util.Collections;
import org.bukkit.SoundCategory;
import org.bukkit.Sound;
import net.kyori.adventure.text.Component;
import java.util.logging.Level;
import dev.two.slender.player.SnapshotStore;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Set;
import java.util.function.Supplier;
import java.util.Objects;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.bukkit.scoreboard.Team;
import org.bukkit.scheduler.BukkitTask;
import dev.two.slender.logic.EscapeLedger;
import org.bukkit.entity.Entity;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.World;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.Plugin;

public final class Game
{
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final Items items;
    private final HubAccess hub;
    private final ArenaMap map;
    private final World world;
    private final Consumer<UUID> onRemove;
    private final Map<UUID, Participant> participants;
    private final Random random;
    private final SightService sight;
    private final Flashlights flashlights;
    private final SurvivorEffects effects;
    private final Atmosphere atmosphere;
    private final Hud hud;
    private final PageManager pages;
    private final BatteryManager batteries;
    private final List<Entity> corpses;
    private final EscapeLedger escapes;
    private final MapAmbience ambience;
    private GameState state;
    private Mode mode;
    private BukkitTask loop;
    private long tick;
    private long startTick;
    private long lastPageTick;
    private int countdown;
    private int timeLeft;
    private int escapeLeft;
    private int escapeTotal;
    private int endLeft;
    private int pagesCollected;
    private int totalPages;
    private SlenderBody body;
    private ScareScheduler scares;
    private Director director;
    private SlenderPlayerController slenderCtl;
    private Team team;
    
    public Game(final Plugin plugin, final SlenderConfig cfg, final Msg msg, final Items items, final HubAccess hub, final ArenaMap map, final World world, final Consumer<UUID> onRemove) {
        this.participants = new LinkedHashMap<UUID, Participant>();
        this.random = new Random();
        this.atmosphere = new Atmosphere();
        this.hud = new Hud();
        this.corpses = new ArrayList<Entity>();
        this.escapes = new EscapeLedger();
        this.state = GameState.WAITING;
        this.plugin = plugin;
        this.cfg = cfg;
        this.msg = msg;
        this.items = items;
        this.hub = hub;
        this.map = map;
        this.world = world;
        this.onRemove = onRemove;
        this.sight = new SightService(cfg);
        this.flashlights = new Flashlights(cfg);
        this.effects = new SurvivorEffects(msg);
        this.pages = new PageManager(plugin, items);
        this.batteries = new BatteryManager(items);
        final Flashlights flashlights = this.flashlights;
        final PageManager pages = this.pages;
        Objects.requireNonNull(pages);
        flashlights.reserve((Supplier<Set<Long>>)pages::framePositions);
        this.ambience = new MapAmbience(plugin, world, map.ambience());
    }
    
    public ArenaMap map() {
        return this.map;
    }
    
    public GameState state() {
        return this.state;
    }
    
    public Mode mode() {
        return this.mode;
    }
    
    public int size() {
        return this.participants.size();
    }
    
    public boolean acceptingPlayers() {
        return this.state == GameState.WAITING || this.state == GameState.COUNTDOWN;
    }
    
    public boolean running() {
        return this.state == GameState.PLAYING || this.state == GameState.ESCAPE;
    }
    
    private List<Player> online() {
        final ArrayList list = new ArrayList();
        final Iterator<UUID> iterator = this.participants.keySet().iterator();
        while (iterator.hasNext()) {
            final Player player = Bukkit.getPlayer((UUID)iterator.next());
            if (player != null) {
                list.add(player);
            }
        }
        return list;
    }
    
    private List<Player> aliveSurvivors() {
        final ArrayList list = new ArrayList();
        for (final Participant participant : this.participants.values()) {
            if (participant.role == Role.SURVIVOR && participant.state != null) {
                if (!participant.state.active()) {
                    continue;
                }
                final Player player = Bukkit.getPlayer(participant.id);
                if (player == null) {
                    continue;
                }
                list.add(player);
            }
        }
        return list;
    }
    
    private SurvivorState stateOf(final UUID uuid) {
        final Participant participant = this.participants.get(uuid);
        return (participant == null) ? null : participant.state;
    }
    
    public boolean isAliveSurvivor(final Player player) {
        final Participant participant = this.participants.get(player.getUniqueId());
        return this.running() && participant != null && participant.role == Role.SURVIVOR && participant.state != null && participant.state.active();
    }
    
    public boolean isSlender(final Player player) {
        return this.running() && this.slenderCtl != null && this.slenderCtl.isSlender(player.getUniqueId());
    }
    
    public void join(final Player player, final Mode mode) {
        if (this.participants.isEmpty()) {
            this.mode = mode;
        }
        this.participants.put(player.getUniqueId(), new Participant(player.getUniqueId(), player.getName()));
        this.hud.show(player);
        this.teamAdd(player);
        this.broadcast("player-joined", "player", player.getName(), "count", String.valueOf(this.size()), "max", String.valueOf(this.cfg.maxPlayers(this.mode.id())));
        this.ensureLoop();
        this.checkCountdown();
    }
    
    public void leave(final UUID uuid, final LeaveReason leaveReason) {
        final boolean b = this.state == GameState.PLAYING || this.state == GameState.ESCAPE || this.state == GameState.ENDING;
        final Participant participant = this.participants.remove(uuid);
        if (participant == null) {
            return;
        }
        this.onRemove.accept(uuid);
        final Player player = Bukkit.getPlayer(uuid);
        final List<Player> online = this.online();
        this.flashlights.clear(uuid, online, this.world);
        if (this.slenderCtl != null && this.slenderCtl.isSlender(uuid)) {
            this.slenderCtl.cleanup(player, online);
        }
        else if (this.slenderCtl != null && player != null) {
            this.slenderCtl.revealTo(player);
        }
        if (player != null) {
            this.hud.hide(player);
            this.teamRemove(player);
            if (b && (leaveReason == LeaveReason.COMMAND || leaveReason == LeaveReason.RESET)) {
                SnapshotStore.resetForGame(player);
                this.hub.backToHub(player);
            }
        }
        if (leaveReason == LeaveReason.RESET) {
            return;
        }
        this.broadcast("player-left", "player", participant.name, "count", String.valueOf(this.size()), "max", String.valueOf(this.cfg.maxPlayers((this.mode == null) ? "coop" : this.mode.id())));
        if (this.acceptingPlayers()) {
            this.checkCountdown();
        }
        else if (this.running()) {
            if (participant.role == Role.SLENDER) {
                this.broadcast("slender-left", new String[0]);
                this.end(Winner.SURVIVORS);
            }
            else {
                this.checkEnd();
            }
        }
        if (this.participants.isEmpty() && this.state != GameState.ENDING) {
            this.reset();
        }
    }
    
    private void ensureLoop() {
        if (this.loop == null) {
            this.loop = Bukkit.getScheduler().runTaskTimer(this.plugin, this::step, 2L, 2L);
        }
    }
    
    private void step() {
        this.tick += 2L;
        try {
            switch (this.state) {
                case WAITING: {
                    if (this.tick % 20L == 0L && this.mode != null) {
                        this.hud.update(this.msg.get("bossbar-waiting", "count", String.valueOf(this.size()), "max", String.valueOf(this.cfg.maxPlayers(this.mode.id()))), 0.0f);
                        break;
                    }
                    break;
                }
                case COUNTDOWN: {
                    if (this.tick % 20L == 0L) {
                        this.countdownSecond();
                        break;
                    }
                    break;
                }
                case PLAYING:
                case ESCAPE: {
                    this.playStep();
                    break;
                }
                case ENDING: {
                    if (this.tick % 20L != 0L) {
                        break;
                    }
                    final int endLeft = this.endLeft - 1;
                    this.endLeft = endLeft;
                    if (endLeft <= 0 || this.participants.isEmpty()) {
                        this.reset();
                        break;
                    }
                    break;
                }
            }
        }
        catch (final RuntimeException thrown) {
            this.plugin.getLogger().log(Level.SEVERE, "Error en la partida " + this.map.id(), (Throwable)thrown);
        }
    }
    
    private void checkCountdown() {
        if (this.mode == null) {
            return;
        }
        final int size = this.size();
        final int minPlayers = this.cfg.minPlayers(this.mode.id());
        final int maxPlayers = this.cfg.maxPlayers(this.mode.id());
        if (this.state == GameState.WAITING && size >= minPlayers) {
            this.state = GameState.COUNTDOWN;
            this.countdown = ((size >= maxPlayers) ? this.cfg.fullCountdownSeconds : this.cfg.countdownSeconds);
            this.broadcast("countdown", "seconds", String.valueOf(this.countdown));
        }
        else if (this.state == GameState.COUNTDOWN) {
            if (size < minPlayers) {
                this.state = GameState.WAITING;
                this.broadcast("countdown-cancelled", new String[0]);
            }
            else if (size >= maxPlayers) {
                this.countdown = Math.min(this.countdown, this.cfg.fullCountdownSeconds);
            }
        }
    }
    
    private void countdownSecond() {
        --this.countdown;
        this.hud.update(this.msg.get("bossbar-countdown", "seconds", String.valueOf(Math.max(0, this.countdown))), Math.max(0, this.countdown) / (float)Math.max(1, this.cfg.countdownSeconds));
        if (this.countdown > 0 && this.countdown <= 5) {
            for (Player player : this.online()) {
                Hud.title(player, this.msg.parse("<dark_red>" + this.countdown + "</dark_red>"), (Component)Component.empty(), 0, 20, 5);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1.0f, 0.6f);
            }
        }
        if (this.countdown <= 0) {
            this.start();
        }
    }
    
    public boolean forceStart() {
        if (!this.acceptingPlayers() || this.participants.isEmpty() || this.mode == null) {
            return false;
        }
        if (this.mode == Mode.ASIM && this.size() < 2) {
            return false;
        }
        this.start();
        return true;
    }
    
    private void start() {
        final List<Player> online = this.online();
        if (online.isEmpty()) {
            this.reset();
            return;
        }
        this.state = GameState.PLAYING;
        this.escapes.clear();
        this.holdChunks(true);
        this.startTick = this.tick;
        this.lastPageTick = this.tick;
        this.pagesCollected = 0;
        this.timeLeft = this.cfg.timeLimit(this.mode.id());
        this.body = new SlenderBody(this.plugin, this.cfg);
        this.scares = new ScareScheduler(this.plugin, this.cfg, this.msg, this.sight, this.flashlights, this.pages, this::stateOf, this::freePageSpots);
        Collections.shuffle(online, this.random);
        final Player player = (this.mode == Mode.ASIM && online.size() >= 2) ? online.get(0) : null;
        int n = 0;
        for (final Player player2 : online) {
            final Participant participant = this.participants.get(player2.getUniqueId());
            if (player2 == player) {
                participant.role = Role.SLENDER;
            }
            else {
                participant.role = Role.SURVIVOR;
                participant.state = new SurvivorState(player2.getUniqueId(), this.cfg);
                player2.teleport(this.toLocation(this.map.spawns().get(n++ % this.map.spawns().size())));
                player2.getInventory().clear();
                player2.getInventory().setItem(0, this.items.make("flashlight"));
                player2.getInventory().setHeldItemSlot(0);
                this.flashlights.register(player2.getUniqueId());
                this.atmosphere.darkness(player2);
                this.atmosphere.applyFog(player2, 0, false);
                player2.setLevel(0);
                Hud.title(player2, this.msg.get((this.mode == Mode.COOP) ? "start-coop" : "start-asim-survivor", "pages", String.valueOf(this.cfg.pages)), (Component)Component.empty(), 10, 70, 20);
            }
        }
        this.pages.spawnAll(this.world, PageSelector.choose(this.map.pageSpots(), this.cfg.pages, this.random));
        this.totalPages = Math.max(1, this.pages.remaining());
        this.batteries.spawn(this.world, PageSelector.choose(this.map.batterySpots(), this.cfg.batteriesPerGame, this.random));
        if (player == null) {
            this.director = new Director(this.cfg, this.sight, this.body, this.map, this::kill);
        }
        else {
            this.slenderCtl = new SlenderPlayerController(this.plugin, this.cfg, this.msg, this.items, this.sight, this.body, this.flashlights, this.scares, this.map, this::kill);
            final ArrayList list = new ArrayList(online);
            list.remove(player);
            player.getInventory().clear();
            this.slenderCtl.setup(player, this.farthestSpot(), (Collection<Player>)list);
            Hud.title(player, this.msg.get("start-asim-slender", new String[0]), (Component)Component.empty(), 10, 70, 20);
        }
        this.world.setTime(18000L);
    }
    
    private Location farthestSpot() {
        Location location = this.toLocation(this.map.spawns().get(0));
        double n = -1.0;
        for (final PageSpot pageSpot : this.map.pageSpots()) {
            final Location location2 = new Location(this.world, pageSpot.pos().x() + 0.5, (double)pageSpot.pos().y(), pageSpot.pos().z() + 0.5);
            final double distance = location2.distance(location);
            if (distance > n) {
                final Location ground = SlenderBody.ground(location2, 2, 4);
                if (ground == null) {
                    continue;
                }
                n = distance;
                location = ground;
            }
        }
        return location;
    }
    
    private void playStep() {
        final List<Player> online = this.online();
        final List<Player> aliveSurvivors = this.aliveSurvivors();
        final boolean b = this.state == GameState.ESCAPE;
        final double aggression = this.aggression();
        this.flashlights.tick(aliveSurvivors, online, this.tick, this.random, player4 -> this.msg.send((CommandSender)player4, "battery-dead", new String[0]));
        if (this.tick % 10L == 0L) {
            this.ambience.lights(this.tick, this.random);
        }
        final boolean b2 = this.body != null && this.body.alive();
        for (final Player player : aliveSurvivors) {
            final SurvivorState state = this.stateOf(player.getUniqueId());
            if (state != null) {
                if (!state.active()) {
                    continue;
                }
                this.effects.applyStamina(player, state, player.isSprinting(), 0.1);
                final double n = (b2 && this.body.location().getWorld() == player.getWorld()) ? this.body.location().distance(player.getLocation()) : 999.0;
                state.stat.update(0.1, b2 && this.sight.staticLook(player, this.body.center(), this.body.head()), n);
                this.effects.applyStatic(player, state, this.tick);
                this.atmosphere.heartbeat(player, state, n, this.tick);
                if (state.stat.lethal()) {
                    this.kill(player, "static");
                    if (!this.running()) {
                        return;
                    }
                    continue;
                }
                else {
                    final Location location = player.getLocation();
                    if (b && this.map.inExit(location.getX(), location.getY(), location.getZ())) {
                        this.escape(player);
                        if (!this.running()) {
                            return;
                        }
                        continue;
                    }
                    else {
                        if ((this.map.inBounds(location.getX(), location.getZ()) || this.map.inExit(location.getX(), location.getY(), location.getZ())) && location.getY() >= this.map.fallY()) {
                            continue;
                        }
                        player.teleport(this.toLocation(this.map.spawns().get(this.random.nextInt(this.map.spawns().size()))));
                    }
                }
            }
        }
        this.scares.tickApparitions(this.tick);
        if (this.slenderCtl != null && this.slenderCtl.slender() != null) {
            final Player player2 = Bukkit.getPlayer(this.slenderCtl.slender());
            if (player2 != null) {
                this.slenderCtl.tick(player2, this.aliveSurvivors(), this.pagesCollected, this.tick);
            }
        }
        if (this.tick % 10L == 0L) {
            if (this.director != null) {
                final boolean b3 = !b && DirectorMath.dormant(this.pagesCollected, (this.tick - this.startTick) / 20.0);
                final Director director = this.director;
                final List<Player> aliveSurvivors2 = this.aliveSurvivors();
                final Function<UUID, SurvivorState> function = this::stateOf;
                final Flashlights flashlights = this.flashlights;
                Objects.requireNonNull(flashlights);
                director.tick(aliveSurvivors2, function, flashlights::isOn, aggression, b3, this.tick, this.random);
                if (!this.running()) {
                    return;
                }
            }
            for (final Player player3 : this.aliveSurvivors()) {
                player3.sendActionBar(this.effects.actionBar(this.stateOf(player3.getUniqueId()), this.flashlights.battery(player3.getUniqueId()), this.flashlights.isOn(player3.getUniqueId()), this.random));
            }
        }
        if (this.tick % 20L == 0L) {
            this.secondStep(aggression);
        }
    }
    
    private void secondStep(final double n) {
        final List<Player> aliveSurvivors = this.aliveSurvivors();
        final boolean b = this.state == GameState.ESCAPE;
        this.scares.tick(aliveSurvivors, n, this.tick, this.random);
        this.ambience.second(aliveSurvivors, this.random);
        final Iterator<Player> iterator = aliveSurvivors.iterator();
        while (iterator.hasNext()) {
            this.atmosphere.ambient(iterator.next(), this.pagesCollected, b, this.random);
        }
        if (this.tick % 200L == 0L) {
            for (final Player player : aliveSurvivors) {
                this.atmosphere.darkness(player);
                this.atmosphere.applyFog(player, this.pagesCollected, b);
            }
        }
        if (n >= 4.0 && this.random.nextDouble() < 0.004166666666666667) {
            for (final Player player2 : aliveSurvivors) {
                this.flashlights.disable(player2.getUniqueId(), this.tick + 100L);
                player2.playSound(player2.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.8f, 0.5f);
            }
        }
        if (!b) {
            --this.timeLeft;
            this.hud.update(this.msg.get("bossbar-playing", "pages", String.valueOf(this.pagesCollected), "total", String.valueOf(this.totalPages), "time", mmss(this.timeLeft)), this.pagesCollected / (float)this.totalPages);
            if (this.timeLeft <= 0) {
                this.broadcast("time-up", new String[0]);
                this.end(this.outcome());
            }
        }
        else {
            --this.escapeLeft;
            this.hud.update(this.msg.get("bossbar-escape", "seconds", String.valueOf(Math.max(0, this.escapeLeft))), Math.max(0, this.escapeLeft) / (float)Math.max(1, this.escapeTotal));
            if (this.escapeLeft <= 0) {
                this.end(this.outcome());
            }
        }
    }
    
    private double aggression() {
        return DirectorMath.aggression(this.pagesCollected, (this.tick - this.lastPageTick) / 20.0, this.state == GameState.ESCAPE);
    }
    
    public void interactFrame(final Player player, final Entity entity) {
        if (!this.isAliveSurvivor(player)) {
            return;
        }
        if (this.scares.onFakePage(player, entity, this.random)) {
            return;
        }
        if (!this.pages.isPage(entity)) {
            return;
        }
        this.pages.collect(entity);
        ++this.pagesCollected;
        this.lastPageTick = this.tick;
        final SurvivorState state = this.stateOf(player.getUniqueId());
        ++state.pages;
        final List<String> phrasesOr = this.map.ambience().phrasesOr(this.cfg.pagePhrases);
        final String s = phrasesOr.get(this.random.nextInt(phrasesOr.size()));
        for (final Player player2 : this.online()) {
            Hud.title(player2, this.msg.get("page-title", "phrase", s), (Component)Component.empty(), 5, 40, 15);
            player2.playSound(player2.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 1.0f, 0.7f);
            player2.setLevel(this.pagesCollected);
        }
        this.broadcast("page-collected", "player", player.getName(), "pages", String.valueOf(this.pagesCollected), "total", String.valueOf(this.totalPages));
        final Iterator<Player> iterator2 = this.aliveSurvivors().iterator();
        while (iterator2.hasNext()) {
            this.atmosphere.applyFog(iterator2.next(), this.pagesCollected, false);
        }
        if (this.pagesCollected >= this.totalPages) {
            this.startEscape();
        }
    }
    
    public void pickupBattery(final Player player, final Item item) {
        if (!this.batteries.isBattery(item) || !this.isAliveSurvivor(player)) {
            return;
        }
        this.batteries.remove(item);
        this.flashlights.charge(player.getUniqueId(), this.cfg.batteryCharge);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.8f, 1.2f);
        player.sendActionBar(this.msg.get("battery-picked", "battery", String.valueOf((int)this.flashlights.battery(player.getUniqueId()))));
    }
    
    public void toggleFlashlight(final Player player) {
        if (this.isAliveSurvivor(player)) {
            this.flashlights.toggle(player);
        }
    }
    
    public void useAbility(final Player player, final String s) {
        if (this.isSlender(player)) {
            this.slenderCtl.use(player, s, this.aliveSurvivors(), this.pagesCollected, this.tick, this.random);
        }
    }
    
    public void slenderHit(final Player player, final Player player2) {
        if (this.isSlender(player) && this.isAliveSurvivor(player2)) {
            this.slenderCtl.tryGrab(player, player2, this.pagesCollected, this.tick);
        }
    }
    
    private void startEscape() {
        this.state = GameState.ESCAPE;
        this.escapeTotal = EscapeTime.seconds(this.cfg.escapeSeconds, this.map.farthestPageFromExit());
        this.escapeLeft = this.escapeTotal;
        for (final BlockPos blockPos : this.map.gate()) {
            this.world.getBlockAt(blockPos.x(), blockPos.y(), blockPos.z()).setType(Material.AIR, false);
        }
        this.broadcast("escape-start", "seconds", String.valueOf(this.escapeTotal));
        this.atmosphere.escapeHorn(this.online());
        final Iterator<Player> iterator2 = this.aliveSurvivors().iterator();
        while (iterator2.hasNext()) {
            this.atmosphere.applyFog(iterator2.next(), this.pagesCollected, true);
        }
    }
    
    private void escape(final Player player) {
        this.stateOf(player.getUniqueId()).escaped = true;
        this.escapes.escaped(player.getUniqueId());
        this.flashlights.clear(player.getUniqueId(), this.online(), this.world);
        player.setGameMode(GameMode.SPECTATOR);
        this.broadcast("escaped", "player", player.getName());
        this.checkEnd();
    }
    
    public void kill(final Player player2, final String s) {
        final SurvivorState state = this.stateOf(player2.getUniqueId());
        if (state == null || !state.active() || !this.running()) {
            return;
        }
        state.alive = false;
        this.flashlights.clear(player2.getUniqueId(), this.online(), this.world);
        this.scares.jumpscare(player2);
        Hud.title(player2, this.msg.parse("<dark_red><obfuscated>XXXXXXXXXX</obfuscated></dark_red>"), (Component)Component.empty(), 0, 30, 10);
        player2.setWalkSpeed(0.0f);
        this.spawnCorpse(player2);
        this.broadcast("died", "player", player2.getName());
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            player2.getUniqueId();
            Bukkit.getPlayer(uuid);
            final Player player3;
            if (player3 != null && this.participants.containsKey(uuid)) {
                player3.setWalkSpeed(0.2f);
                player3.setGameMode(GameMode.SPECTATOR);
                this.msg.send((CommandSender)player3, "spectating", new String[0]);
            }
            return;
        }, 30L);
        this.checkEnd();
    }
    
    private void spawnCorpse(final Player player) {
        try {
            final Location location = player.getLocation();
            location.setPitch(0.0f);
            this.corpses.add(this.world.spawn(location, (Class)Mannequin.class, mannequin -> {
                mannequin.setProfile(ResolvableProfile.resolvableProfile(player2.getPlayerProfile()));
                mannequin.setImmovable((boolean)(1 != 0));
                mannequin.setInvulnerable((boolean)(1 != 0));
                mannequin.setPersistent((boolean)(0 != 0));
                mannequin.setSilent((boolean)(1 != 0));
                mannequin.setDescription((Component)Component.empty());
                mannequin.setPose(Mannequin.validPoses().contains(Pose.SLEEPING) ? Pose.SLEEPING : Pose.SWIMMING, (boolean)(1 != 0));
                mannequin.getScoreboardTags().add("slender_body");
            }));
        }
        catch (final RuntimeException ex) {
            this.plugin.getLogger().fine("Sin cad\u00e1ver: " + ex.getMessage());
        }
    }
    
    private boolean anyEscaped() {
        return this.escapes.anyEscaped();
    }
    
    private Winner outcome() {
        if (this.anyEscaped()) {
            return Winner.SURVIVORS;
        }
        return (this.mode == Mode.ASIM) ? Winner.SLENDER : Winner.NOBODY;
    }
    
    private void checkEnd() {
        if (!this.running()) {
            return;
        }
        for (final Participant participant : this.participants.values()) {
            if (participant.role == Role.SURVIVOR && participant.state != null && participant.state.active() && Bukkit.getPlayer(participant.id) != null) {
                return;
            }
        }
        this.end(this.outcome());
    }
    
    public void end(final Winner winner) {
        if (!this.running()) {
            return;
        }
        this.state = GameState.ENDING;
        this.endLeft = this.cfg.endSeconds;
        final Msg msg = this.msg;
        final Component value = msg.get(switch (winner.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "win-survivors";
            case 1 -> "win-slender";
            case 2 -> "lose-coop";
        }, new String[0]);
        final ArrayList list = new ArrayList();
        for (final Participant participant : this.participants.values()) {
            if (participant.role == Role.SURVIVOR && participant.state != null) {
                list.add(this.msg.get(participant.state.escaped ? "result-escaped" : "result-dead", "player", participant.name));
            }
        }
        for (final Player player : this.online()) {
            Hud.title(player, value, (Component)Component.empty(), 10, 80, 20);
            player.sendMessage(value);
            final ArrayList list2 = list;
            final CommandSender obj = (CommandSender)player;
            Objects.requireNonNull((Player)obj);
            list2.forEach(obj::sendMessage);
        }
        this.hud.update(value, 1.0f);
    }
    
    public void stop() {
        if (this.running()) {
            this.end(Winner.NOBODY);
        }
        this.reset();
    }
    
    public void reset() {
        final Iterator iterator = new ArrayList(this.participants.keySet()).iterator();
        while (iterator.hasNext()) {
            this.leave((UUID)iterator.next(), LeaveReason.RESET);
        }
        this.cleanupWorld();
        if (this.loop != null) {
            this.loop.cancel();
            this.loop = null;
        }
        this.hud.hideAll();
        if (this.team != null) {
            try {
                this.team.unregister();
            }
            catch (final IllegalStateException ex) {}
            this.team = null;
        }
        this.state = GameState.WAITING;
        this.mode = null;
        this.escapes.clear();
        this.pagesCollected = 0;
        this.totalPages = 0;
    }
    
    private void holdChunks(final boolean b) {
        for (final int[] array : ChunkArea.chunks(this.map.min().x(), this.map.min().z(), this.map.max().x(), this.map.max().z())) {
            if (b) {
                this.world.addPluginChunkTicket(array[0], array[1], this.plugin);
            }
            else {
                this.world.removePluginChunkTicket(array[0], array[1], this.plugin);
            }
        }
    }
    
    private void cleanupWorld() {
        final List<Player> online = this.online();
        this.pages.clear();
        this.batteries.clear();
        if (this.scares != null) {
            this.scares.clear();
        }
        if (this.director != null) {
            this.director.clear();
        }
        if (this.slenderCtl != null) {
            this.slenderCtl.cleanup(null, (Collection<Player>)List.of());
        }
        if (this.body != null) {
            this.body.remove();
        }
        this.flashlights.clearAll(online, this.world);
        final Iterator<Entity> iterator = this.corpses.iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
        }
        this.corpses.clear();
        final BlockData blockData = Bukkit.createBlockData(this.map.gateBlock());
        for (final BlockPos blockPos : this.map.gate()) {
            this.world.getBlockAt(blockPos.x(), blockPos.y(), blockPos.z()).setBlockData(blockData, false);
        }
        this.ambience.reset();
        this.holdChunks(false);
        this.director = null;
        this.slenderCtl = null;
        this.scares = null;
        this.body = null;
    }
    
    private void teamAdd(final Player player) {
        if (this.team == null) {
            final Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            final String s = "slender_" + this.map.id();
            this.team = mainScoreboard.getTeam(s);
            if (this.team == null) {
                this.team = mainScoreboard.registerNewTeam(s);
            }
            this.team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            this.team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
        this.team.addEntry(player.getName());
    }
    
    private void teamRemove(final Player player) {
        if (this.team != null) {
            this.team.removeEntry(player.getName());
        }
    }
    
    private void broadcast(final String s, final String... array) {
        final Component prefixed = this.msg.prefixed(s, array);
        final Iterator<Player> iterator = this.online().iterator();
        while (iterator.hasNext()) {
            iterator.next().sendMessage(prefixed);
        }
    }
    
    private Location toLocation(final Loc loc) {
        return new Location(this.world, loc.x(), loc.y(), loc.z(), loc.yaw(), 0.0f);
    }
    
    private List<PageSpot> freePageSpots() {
        return this.map.pageSpots().stream().filter(pageSpot -> {
            this.pages.activeSpots();
            return !set.contains(pageSpot);
        }).toList();
    }
    
    private static String mmss(int max) {
        max = Math.max(0, max);
        return String.format("%d:%02d", max / 60, max % 60);
    }
    
    public enum Winner
    {
        SURVIVORS, 
        SLENDER, 
        NOBODY;
    }
}
