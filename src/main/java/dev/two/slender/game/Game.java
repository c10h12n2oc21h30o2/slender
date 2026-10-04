package dev.two.slender.game;

import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.ChunkArea;
import dev.two.slender.logic.DirectorMath;
import dev.two.slender.logic.EscapeLedger;
import dev.two.slender.logic.EscapeTime;
import dev.two.slender.logic.PageSelector;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.PageSpot;
import dev.two.slender.player.SnapshotStore;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public final class Game {
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final Items items;
    private final HubAccess hub;
    private final ArenaMap map;
    private final World world;
    private final Consumer<UUID> onRemove;
    private final Map<UUID, Participant> participants = new LinkedHashMap();
    private final Random random = new Random();
    /** Frases de las páginas de esta partida, sin repetir (se baraja al empezar). */
    private PhraseDeck phrases;
    private final SightService sight;
    private final Flashlights flashlights;
    private final SurvivorEffects effects;
    private final Atmosphere atmosphere = new Atmosphere();
    private final Hud hud = new Hud();
    private final PageManager pages;
    private final BatteryManager batteries;
    private final List<Entity> corpses = new ArrayList();
    private final EscapeLedger escapes = new EscapeLedger();
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

    public Game(Plugin var1, SlenderConfig var2, Msg var3, Items var4, HubAccess var5, ArenaMap var6, World var7, Consumer<UUID> var8) {
        this.state = GameState.WAITING;
        this.plugin = var1;
        this.cfg = var2;
        this.msg = var3;
        this.items = var4;
        this.hub = var5;
        this.map = var6;
        this.world = var7;
        this.onRemove = var8;
        this.sight = new SightService(var2);
        this.flashlights = new Flashlights(var2);
        this.effects = new SurvivorEffects(var3);
        this.pages = new PageManager(var1, var4);
        this.batteries = new BatteryManager(var4);
        Flashlights var10000 = this.flashlights;
        PageManager var10001 = this.pages;
        Objects.requireNonNull(var10001);
        var10000.reserve(var10001::framePositions);
        this.ambience = new MapAmbience(var1, var7, var6.ambience());
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
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.participants.keySet().iterator();

        while(var2.hasNext()) {
            UUID var3 = (UUID)var2.next();
            Player var4 = Bukkit.getPlayer(var3);
            if (var4 != null) {
                var1.add(var4);
            }
        }

        return var1;
    }

    private List<Player> aliveSurvivors() {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.participants.values().iterator();

        while(var2.hasNext()) {
            Participant var3 = (Participant)var2.next();
            if (var3.role == Role.SURVIVOR && var3.state != null && var3.state.active()) {
                Player var4 = Bukkit.getPlayer(var3.id);
                if (var4 != null) {
                    var1.add(var4);
                }
            }
        }

        return var1;
    }

    private SurvivorState stateOf(UUID var1) {
        Participant var2 = (Participant)this.participants.get(var1);
        return var2 == null ? null : var2.state;
    }

    public boolean isAliveSurvivor(Player var1) {
        Participant var2 = (Participant)this.participants.get(var1.getUniqueId());
        return this.running() && var2 != null && var2.role == Role.SURVIVOR && var2.state != null && var2.state.active();
    }

    public boolean isSlender(Player var1) {
        return this.running() && this.slenderCtl != null && this.slenderCtl.isSlender(var1.getUniqueId());
    }

    public void join(Player var1, Mode var2) {
        if (this.participants.isEmpty()) {
            this.mode = var2;
        }

        this.participants.put(var1.getUniqueId(), new Participant(var1.getUniqueId(), var1.getName()));
        this.hud.show(var1);
        this.teamAdd(var1);
        this.broadcast("player-joined", "player", var1.getName(), "count", String.valueOf(this.size()), "max", String.valueOf(this.cfg.maxPlayers(this.mode.id())));
        this.ensureLoop();
        this.checkCountdown();
    }

    public void leave(UUID var1, LeaveReason var2) {
        boolean var3 = this.state == GameState.PLAYING || this.state == GameState.ESCAPE || this.state == GameState.ENDING;
        Participant var4 = (Participant)this.participants.remove(var1);
        if (var4 != null) {
            this.onRemove.accept(var1);
            Player var5 = Bukkit.getPlayer(var1);
            List var6 = this.online();
            this.flashlights.clear(var1, var6, this.world);
            if (this.slenderCtl != null && this.slenderCtl.isSlender(var1)) {
                this.slenderCtl.cleanup(var5, var6);
            } else if (this.slenderCtl != null && var5 != null) {
                this.slenderCtl.revealTo(var5);
            }

            if (var5 != null) {
                this.hud.hide(var5);
                this.teamRemove(var5);
                if (var3 && (var2 == LeaveReason.COMMAND || var2 == LeaveReason.RESET)) {
                    SnapshotStore.resetForGame(var5);
                    this.hub.backToHub(var5);
                }
            }

            if (var2 != LeaveReason.RESET) {
                this.broadcast("player-left", "player", var4.name, "count", String.valueOf(this.size()), "max", String.valueOf(this.cfg.maxPlayers(this.mode == null ? "coop" : this.mode.id())));
                if (this.acceptingPlayers()) {
                    this.checkCountdown();
                } else if (this.running()) {
                    if (var4.role == Role.SLENDER) {
                        this.broadcast("slender-left");
                        this.end(Game.Winner.SURVIVORS);
                    } else {
                        this.checkEnd();
                    }
                }

                if (this.participants.isEmpty() && this.state != GameState.ENDING) {
                    this.reset();
                }

            }
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
                case WAITING:
                    if (this.tick % 20L == 0L && this.mode != null) {
                        this.hud.update(this.msg.get("bossbar-waiting", "count", String.valueOf(this.size()), "max", String.valueOf(this.cfg.maxPlayers(this.mode.id()))), 0.0F);
                    }
                    break;
                case COUNTDOWN:
                    if (this.tick % 20L == 0L) {
                        this.countdownSecond();
                    }
                    break;
                case PLAYING:
                case ESCAPE:
                    this.playStep();
                    break;
                case ENDING:
                    if (this.tick % 20L == 0L && (--this.endLeft <= 0 || this.participants.isEmpty())) {
                        this.reset();
                    }
            }
        } catch (RuntimeException var2) {
            this.plugin.getLogger().log(Level.SEVERE, "Error en la partida " + this.map.id(), var2);
        }

    }

    private void checkCountdown() {
        if (this.mode != null) {
            int var1 = this.size();
            int var2 = this.cfg.minPlayers(this.mode.id());
            int var3 = this.cfg.maxPlayers(this.mode.id());
            if (this.state == GameState.WAITING && var1 >= var2) {
                this.state = GameState.COUNTDOWN;
                this.countdown = var1 >= var3 ? this.cfg.fullCountdownSeconds : this.cfg.countdownSeconds;
                this.broadcast("countdown", "seconds", String.valueOf(this.countdown));
            } else if (this.state == GameState.COUNTDOWN) {
                if (var1 < var2) {
                    this.state = GameState.WAITING;
                    this.broadcast("countdown-cancelled");
                } else if (var1 >= var3) {
                    this.countdown = Math.min(this.countdown, this.cfg.fullCountdownSeconds);
                }
            }

        }
    }

    private void countdownSecond() {
        --this.countdown;
        this.hud.update(this.msg.get("bossbar-countdown", "seconds", String.valueOf(Math.max(0, this.countdown))), (float)Math.max(0, this.countdown) / (float)Math.max(1, this.cfg.countdownSeconds));
        if (this.countdown > 0 && this.countdown <= 5) {
            Iterator var1 = this.online().iterator();

            while(var1.hasNext()) {
                Player var2 = (Player)var1.next();
                Hud.title(var2, this.msg.parse("<dark_red>" + this.countdown + "</dark_red>"), Component.empty(), 0, 20, 5);
                var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 1.0F, 0.6F);
            }
        }

        if (this.countdown <= 0) {
            this.start();
        }

    }

    public boolean forceStart() {
        if (this.acceptingPlayers() && !this.participants.isEmpty() && this.mode != null) {
            if (this.mode == Mode.ASIM && this.size() < 2) {
                return false;
            } else {
                this.start();
                return true;
            }
        } else {
            return false;
        }
    }

    private void start() {
        List var1 = this.online();
        if (var1.isEmpty()) {
            this.reset();
        } else {
            this.state = GameState.PLAYING;
            this.escapes.clear();
            this.holdChunks(true);
            this.startTick = this.tick;
            this.lastPageTick = this.tick;
            this.pagesCollected = 0;
            this.phrases = new PhraseDeck(this.map.ambience().phrasesOr(this.cfg.pagePhrases), this.random);
            this.timeLeft = this.cfg.timeLimit(this.mode.id());
            this.body = new SlenderBody(this.plugin, this.cfg);
            this.scares = new ScareScheduler(this.plugin, this.cfg, this.msg, this.sight, this.flashlights, this.pages, this::stateOf, this::freePageSpots);
            Collections.shuffle(var1, this.random);
            Player var2 = this.mode == Mode.ASIM && var1.size() >= 2 ? (Player)var1.get(0) : null;
            int var3 = 0;
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
                Player var5 = (Player)var4.next();
                Participant var6 = (Participant)this.participants.get(var5.getUniqueId());
                if (var5 == var2) {
                    var6.role = Role.SLENDER;
                } else {
                    var6.role = Role.SURVIVOR;
                    var6.state = new SurvivorState(var5.getUniqueId(), this.cfg);
                    var5.teleport(this.toLocation((Loc)this.map.spawns().get(var3++ % this.map.spawns().size())));
                    var5.getInventory().clear();
                    var5.getInventory().setItem(0, this.items.make("flashlight"));
                    var5.getInventory().setHeldItemSlot(0);
                    this.flashlights.register(var5.getUniqueId());
                    this.atmosphere.darkness(var5);
                    this.atmosphere.applyFog(var5, 0, false);
                    var5.setLevel(0);
                    Hud.title(var5, this.msg.get(this.mode == Mode.COOP ? "start-coop" : "start-asim-survivor", "pages", String.valueOf(this.cfg.pages)), Component.empty(), 10, 70, 20);
                }
            }

            this.pages.spawnAll(this.world, PageSelector.choose(this.map.pageSpots(), this.cfg.pages, this.random));
            this.totalPages = Math.max(1, this.pages.remaining());
            this.batteries.spawn(this.world, PageSelector.choose(this.map.batterySpots(), this.cfg.batteriesPerGame, this.random));
            if (var2 == null) {
                this.director = new Director(this.cfg, this.sight, this.body, this.map, this::kill);
            } else {
                this.slenderCtl = new SlenderPlayerController(this.plugin, this.cfg, this.msg, this.items, this.sight, this.body, this.flashlights, this.scares, this.map, this::kill);
                ArrayList var7 = new ArrayList(var1);
                var7.remove(var2);
                var2.getInventory().clear();
                this.slenderCtl.setup(var2, this.farthestSpot(), var7);
                Hud.title(var2, this.msg.get("start-asim-slender"), Component.empty(), 10, 70, 20);
            }

            this.world.setTime(18000L);
        }
    }

    private Location farthestSpot() {
        Location var1 = this.toLocation((Loc)this.map.spawns().get(0));
        Location var2 = var1;
        double var3 = -1.0;
        Iterator var5 = this.map.pageSpots().iterator();

        while(var5.hasNext()) {
            PageSpot var6 = (PageSpot)var5.next();
            Location var7 = new Location(this.world, (double)var6.pos().x() + 0.5, (double)var6.pos().y(), (double)var6.pos().z() + 0.5);
            double var8 = var7.distance(var1);
            if (var8 > var3) {
                Location var10 = SlenderBody.ground(var7, 2, 4);
                if (var10 != null) {
                    var3 = var8;
                    var2 = var10;
                }
            }
        }

        return var2;
    }

    private void playStep() {
        List var1 = this.online();
        List var2 = this.aliveSurvivors();
        boolean var3 = this.state == GameState.ESCAPE;
        double var4 = this.aggression();
        this.flashlights.tick(var2, var1, this.tick, this.random, (var1x) -> {
            this.msg.send(var1x, "battery-dead");
        });
        if (this.tick % 10L == 0L) {
            this.ambience.lights(this.tick, this.random);
        }

        boolean var6 = this.body != null && this.body.alive();
        Iterator var7 = var2.iterator();

        do {
            while(true) {
                Player var8;
                SurvivorState var9;
                do {
                    do {
                        if (!var7.hasNext()) {
                            this.scares.tickApparitions(this.tick);
                            if (this.slenderCtl != null && this.slenderCtl.slender() != null) {
                                Player var15 = Bukkit.getPlayer(this.slenderCtl.slender());
                                if (var15 != null) {
                                    this.slenderCtl.tick(var15, this.aliveSurvivors(), this.pagesCollected, this.tick);
                                }
                            }

                            if (this.tick % 10L == 0L) {
                                if (this.director != null) {
                                    boolean var16 = !var3 && DirectorMath.dormant(this.pagesCollected, (double)(this.tick - this.startTick) / 20.0);
                                    Director var10000 = this.director;
                                    List var10001 = this.aliveSurvivors();
                                    Function<UUID, SurvivorState> var10002 = this::stateOf;
                                    Flashlights var10003 = this.flashlights;
                                    Objects.requireNonNull(var10003);
                                    var10000.tick(var10001, var10002, var10003::isOn, var4, var16, this.tick, this.random);
                                    if (!this.running()) {
                                        return;
                                    }
                                }

                                var7 = this.aliveSurvivors().iterator();

                                while(var7.hasNext()) {
                                    var8 = (Player)var7.next();
                                    var9 = this.stateOf(var8.getUniqueId());
                                    var8.sendActionBar((Component)this.effects.actionBar(var9, this.flashlights.battery(var8.getUniqueId()), this.flashlights.isOn(var8.getUniqueId()), this.random));
                                }
                            }

                            if (this.tick % 20L == 0L) {
                                this.secondStep(var4);
                            }

                            return;
                        }

                        var8 = (Player)var7.next();
                        var9 = this.stateOf(var8.getUniqueId());
                    } while(var9 == null);
                } while(!var9.active());

                this.effects.applyStamina(var8, var9, var8.isSprinting(), 0.1);
                double var10 = var6 && this.body.location().getWorld() == var8.getWorld() ? this.body.location().distance(var8.getLocation()) : 999.0;
                boolean var12 = var6 && this.sight.staticLook(var8, this.body.center(), this.body.head());
                var9.stat.update(0.1, var12, var10);
                this.effects.applyStatic(var8, var9, this.tick);
                this.atmosphere.heartbeat(var8, var9, var10, this.tick);
                if (var9.stat.lethal()) {
                    this.kill(var8, "static");
                    break;
                }

                Location var13 = var8.getLocation();
                if (var3 && this.map.inExit(var13.getX(), var13.getY(), var13.getZ())) {
                    this.escape(var8);
                    if (!this.running()) {
                        return;
                    }
                } else {
                    boolean var14 = !this.map.inBounds(var13.getX(), var13.getZ()) && !this.map.inExit(var13.getX(), var13.getY(), var13.getZ());
                    if (var14 || var13.getY() < (double)this.map.fallY()) {
                        var8.teleport(this.toLocation((Loc)this.map.spawns().get(this.random.nextInt(this.map.spawns().size()))));
                    }
                }
            }
        } while(this.running());

    }

    private void secondStep(double var1) {
        List var3 = this.aliveSurvivors();
        boolean var4 = this.state == GameState.ESCAPE;
        this.scares.tick(var3, var1, this.tick, this.random);
        this.ambience.second(var3, this.random);
        Iterator var5 = var3.iterator();

        Player var6;
        while(var5.hasNext()) {
            var6 = (Player)var5.next();
            this.atmosphere.ambient(var6, this.pagesCollected, var4, this.random);
        }

        if (this.tick % 200L == 0L) {
            var5 = var3.iterator();

            while(var5.hasNext()) {
                var6 = (Player)var5.next();
                this.atmosphere.darkness(var6);
                this.atmosphere.applyFog(var6, this.pagesCollected, var4);
            }
        }

        if (var1 >= 4.0 && this.random.nextDouble() < 0.004166666666666667) {
            var5 = var3.iterator();

            while(var5.hasNext()) {
                var6 = (Player)var5.next();
                this.flashlights.disable(var6.getUniqueId(), this.tick + 100L);
                var6.playSound(var6.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.8F, 0.5F);
            }
        }

        if (!var4) {
            --this.timeLeft;
            this.hud.update(this.msg.get("bossbar-playing", "pages", String.valueOf(this.pagesCollected), "total", String.valueOf(this.totalPages), "time", mmss(this.timeLeft)), (float)this.pagesCollected / (float)this.totalPages);
            if (this.timeLeft <= 0) {
                this.broadcast("time-up");
                this.end(this.outcome());
            }
        } else {
            --this.escapeLeft;
            this.hud.update(this.msg.get("bossbar-escape", "seconds", String.valueOf(Math.max(0, this.escapeLeft))), (float)Math.max(0, this.escapeLeft) / (float)Math.max(1, this.escapeTotal));
            if (this.escapeLeft <= 0) {
                this.end(this.outcome());
            }
        }

    }

    private double aggression() {
        return DirectorMath.aggression(this.pagesCollected, (double)(this.tick - this.lastPageTick) / 20.0, this.state == GameState.ESCAPE);
    }

    public void interactFrame(Player var1, Entity var2) {
        if (this.isAliveSurvivor(var1)) {
            if (!this.scares.onFakePage(var1, var2, this.random)) {
                if (this.pages.isPage(var2)) {
                    this.pages.collect(var2);
                    ++this.pagesCollected;
                    this.lastPageTick = this.tick;
                    ++this.stateOf(var1.getUniqueId()).pages;
                    if (this.phrases == null) {
                        this.phrases = new PhraseDeck(this.map.ambience().phrasesOr(this.cfg.pagePhrases), this.random);
                    }
                    String var4 = this.phrases.next();
                    Iterator var5 = this.online().iterator();

                    Player var6;
                    while(var5.hasNext()) {
                        var6 = (Player)var5.next();
                        Hud.title(var6, this.msg.get("page-title", "phrase", var4), Component.empty(), 5, 40, 15);
                        var6.playSound(var6.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 1.0F, 0.7F);
                        var6.setLevel(this.pagesCollected);
                    }

                    this.broadcast("page-collected", "player", var1.getName(), "pages", String.valueOf(this.pagesCollected), "total", String.valueOf(this.totalPages));
                    var5 = this.aliveSurvivors().iterator();

                    while(var5.hasNext()) {
                        var6 = (Player)var5.next();
                        this.atmosphere.applyFog(var6, this.pagesCollected, false);
                    }

                    if (this.pagesCollected >= this.totalPages) {
                        this.startEscape();
                    }

                }
            }
        }
    }

    public void pickupBattery(Player var1, Item var2) {
        if (this.batteries.isBattery(var2) && this.isAliveSurvivor(var1)) {
            this.batteries.remove(var2);
            this.flashlights.charge(var1.getUniqueId(), this.cfg.batteryCharge);
            var1.playSound(var1.getLocation(), Sound.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.8F, 1.2F);
            var1.sendActionBar((Component)this.msg.get("battery-picked", "battery", String.valueOf((int)this.flashlights.battery(var1.getUniqueId()))));
        }
    }

    public void toggleFlashlight(Player var1) {
        if (this.isAliveSurvivor(var1)) {
            this.flashlights.toggle(var1);
        }

    }

    public void useAbility(Player var1, String var2) {
        if (this.isSlender(var1)) {
            this.slenderCtl.use(var1, var2, this.aliveSurvivors(), this.pagesCollected, this.tick, this.random);
        }

    }

    public void slenderHit(Player var1, Player var2) {
        if (this.isSlender(var1) && this.isAliveSurvivor(var2)) {
            this.slenderCtl.tryGrab(var1, var2, this.pagesCollected, this.tick);
        }

    }

    private void startEscape() {
        this.state = GameState.ESCAPE;
        this.escapeTotal = EscapeTime.seconds(this.cfg.escapeSeconds, this.map.farthestPageFromExit());
        this.escapeLeft = this.escapeTotal;
        Iterator var1 = this.map.gate().iterator();

        while(var1.hasNext()) {
            BlockPos var2 = (BlockPos)var1.next();
            org.bukkit.block.Block cell = this.world.getBlockAt(var2.x(), var2.y(), var2.z());
            boolean waterlogged = cell.getBlockData() instanceof org.bukkit.block.data.Waterlogged w && w.isWaterlogged();
            cell.setType(GateWater.opened(waterlogged), false);
        }

        this.broadcast("escape-start", "seconds", String.valueOf(this.escapeTotal));
        this.atmosphere.escapeHorn(this.online());
        var1 = this.aliveSurvivors().iterator();

        while(var1.hasNext()) {
            Player var3 = (Player)var1.next();
            this.atmosphere.applyFog(var3, this.pagesCollected, true);
        }

    }

    private void escape(Player var1) {
        SurvivorState var2 = this.stateOf(var1.getUniqueId());
        var2.escaped = true;
        this.escapes.escaped(var1.getUniqueId());
        this.flashlights.clear(var1.getUniqueId(), this.online(), this.world);
        var1.setGameMode(GameMode.SPECTATOR);
        this.broadcast("escaped", "player", var1.getName());
        this.checkEnd();
    }

    public void kill(Player var1, String var2) {
        SurvivorState var3 = this.stateOf(var1.getUniqueId());
        if (var3 != null && var3.active() && this.running()) {
            var3.alive = false;
            this.flashlights.clear(var1.getUniqueId(), this.online(), this.world);
            this.scares.jumpscare(var1);
            Hud.title(var1, this.msg.parse("<dark_red><obfuscated>XXXXXXXXXX</obfuscated></dark_red>"), Component.empty(), 0, 30, 10);
            var1.setWalkSpeed(0.0F);
            this.spawnCorpse(var1);
            this.broadcast("died", "player", var1.getName());
            UUID var4 = var1.getUniqueId();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Player var2x = Bukkit.getPlayer(var4);
                if (var2x != null && this.participants.containsKey(var4)) {
                    var2x.setWalkSpeed(0.2F);
                    var2x.setGameMode(GameMode.SPECTATOR);
                    this.msg.send(var2x, "spectating");
                }

            }, 30L);
            this.checkEnd();
        }
    }

    private void spawnCorpse(Player var1) {
        try {
            Location var2 = var1.getLocation();
            var2.setPitch(0.0F);
            Mannequin var3 = (Mannequin)this.world.spawn(var2, Mannequin.class, (var1x) -> {
                var1x.setProfile(ResolvableProfile.resolvableProfile(var1.getPlayerProfile()));
                var1x.setImmovable(true);
                var1x.setInvulnerable(true);
                var1x.setPersistent(false);
                var1x.setSilent(true);
                var1x.setDescription(Component.empty());
                var1x.setPose(Mannequin.validPoses().contains(Pose.SLEEPING) ? Pose.SLEEPING : Pose.SWIMMING, true);
                var1x.getScoreboardTags().add("slender_body");
            });
            this.corpses.add(var3);
        } catch (RuntimeException var4) {
            this.plugin.getLogger().fine("Sin cadáver: " + var4.getMessage());
        }

    }

    private boolean anyEscaped() {
        return this.escapes.anyEscaped();
    }

    private Winner outcome() {
        if (this.anyEscaped()) {
            return Game.Winner.SURVIVORS;
        } else {
            return this.mode == Mode.ASIM ? Game.Winner.SLENDER : Game.Winner.NOBODY;
        }
    }

    private void checkEnd() {
        if (this.running()) {
            Iterator var1 = this.participants.values().iterator();

            Participant var2;
            do {
                if (!var1.hasNext()) {
                    this.end(this.outcome());
                    return;
                }

                var2 = (Participant)var1.next();
            } while(var2.role != Role.SURVIVOR || var2.state == null || !var2.state.active() || Bukkit.getPlayer(var2.id) == null);

        }
    }

    public void end(Winner var1) {
        if (this.running()) {
            this.state = GameState.ENDING;
            this.endLeft = this.cfg.endSeconds;
            Msg var10000 = this.msg;
            String var10001;
            switch (var1.ordinal()) {
                case 0 -> var10001 = "win-survivors";
                case 1 -> var10001 = "win-slender";
                case 2 -> var10001 = "lose-coop";
                default -> throw new MatchException((String)null, (Throwable)null);
            }

            Component var2 = var10000.get(var10001);
            ArrayList var3 = new ArrayList();
            Iterator var4 = this.participants.values().iterator();

            while(var4.hasNext()) {
                Participant var5 = (Participant)var4.next();
                if (var5.role == Role.SURVIVOR && var5.state != null) {
                    var3.add(this.msg.get(var5.state.escaped ? "result-escaped" : "result-dead", "player", var5.name));
                }
            }

            var4 = this.online().iterator();

            while(var4.hasNext()) {
                Player var6 = (Player)var4.next();
                Hud.title(var6, var2, Component.empty(), 10, 80, 20);
                var6.sendMessage((Component)var2);
                for (Object line : var3) {
                    var6.sendMessage((Component)line);
                }
            }

            this.hud.update(var2, 1.0F);
        }
    }

    public void stop() {
        if (this.running()) {
            this.end(Game.Winner.NOBODY);
        }

        this.reset();
    }

    public void reset() {
        Iterator var1 = (new ArrayList(this.participants.keySet())).iterator();

        while(var1.hasNext()) {
            UUID var2 = (UUID)var1.next();
            this.leave(var2, LeaveReason.RESET);
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
            } catch (IllegalStateException var3) {
            }

            this.team = null;
        }

        this.state = GameState.WAITING;
        this.mode = null;
        this.escapes.clear();
        this.pagesCollected = 0;
        this.totalPages = 0;
    }

    private void holdChunks(boolean var1) {
        Iterator var2 = ChunkArea.chunks(this.map.min().x(), this.map.min().z(), this.map.max().x(), this.map.max().z()).iterator();

        while(var2.hasNext()) {
            int[] var3 = (int[])var2.next();
            if (var1) {
                this.world.addPluginChunkTicket(var3[0], var3[1], this.plugin);
            } else {
                this.world.removePluginChunkTicket(var3[0], var3[1], this.plugin);
            }
        }

    }

    private void cleanupWorld() {
        List var1 = this.online();
        this.pages.clear();
        this.batteries.clear();
        if (this.scares != null) {
            this.scares.clear();
        }

        if (this.director != null) {
            this.director.clear();
        }

        if (this.slenderCtl != null) {
            this.slenderCtl.cleanup((Player)null, List.of());
        }

        if (this.body != null) {
            this.body.remove();
        }

        this.flashlights.clearAll(var1, this.world);
        Iterator var2 = this.corpses.iterator();

        while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var3.remove();
        }

        this.corpses.clear();
        BlockData var5 = Bukkit.createBlockData(this.map.gateBlock());
        Iterator var6 = this.map.gate().iterator();

        while(var6.hasNext()) {
            BlockPos var4 = (BlockPos)var6.next();
            org.bukkit.block.Block cell = this.world.getBlockAt(var4.x(), var4.y(), var4.z());
            BlockData closed = var5.clone();
            if (closed instanceof org.bukkit.block.data.Waterlogged w) {
                w.setWaterlogged(GateWater.wet(cell.getType(),
                        cell.getBlockData() instanceof org.bukkit.block.data.Levelled l ? l.getLevel() : 0,
                        cell.getBlockData() instanceof org.bukkit.block.data.Waterlogged c && c.isWaterlogged()));
            }
            cell.setBlockData(closed, false);
        }

        this.ambience.reset();
        this.holdChunks(false);
        this.director = null;
        this.slenderCtl = null;
        this.scares = null;
        this.body = null;
    }

    private void teamAdd(Player var1) {
        if (this.team == null) {
            Scoreboard var2 = Bukkit.getScoreboardManager().getMainScoreboard();
            String var3 = "slender_" + this.map.id();
            this.team = var2.getTeam(var3);
            if (this.team == null) {
                this.team = var2.registerNewTeam(var3);
            }

            this.team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
            this.team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        }

        this.team.addEntry(var1.getName());
    }

    private void teamRemove(Player var1) {
        if (this.team != null) {
            this.team.removeEntry(var1.getName());
        }

    }

    private void broadcast(String var1, String... var2) {
        Component var3 = this.msg.prefixed(var1, var2);
        Iterator var4 = this.online().iterator();

        while(var4.hasNext()) {
            Player var5 = (Player)var4.next();
            var5.sendMessage((Component)var3);
        }

    }

    private Location toLocation(Loc var1) {
        return new Location(this.world, var1.x(), var1.y(), var1.z(), var1.yaw(), 0.0F);
    }

    private List<PageSpot> freePageSpots() {
        Set var1 = this.pages.activeSpots();
        return this.map.pageSpots().stream().filter((var1x) -> {
            return !var1.contains(var1x);
        }).toList();
    }

    private static String mmss(int var0) {
        var0 = Math.max(0, var0);
        return String.format("%d:%02d", var0 / 60, var0 % 60);
    }

    public static enum Winner {
        SURVIVORS,
        SLENDER,
        NOBODY;
    }
}
