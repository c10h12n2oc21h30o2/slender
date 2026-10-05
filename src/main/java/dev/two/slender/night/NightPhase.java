package dev.two.slender.night;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.game.Game;
import dev.two.slender.game.PhaseHooks;
import dev.two.slender.game.PhaseResult;
import dev.two.slender.game.SightService;
import dev.two.slender.logic.Vec3;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.SpotRules;
import dev.two.slender.map.builders.hogwarts.HogwartsBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/** Una fase de LA NOCHE ENTERA sobre una sección de Hogwarts: cabezas, caldero, misión, Snitch y poderes (spec §6–8). */
public final class NightPhase implements PhaseHooks {
    private static final Material[] HOUSES = {Material.RED_WOOL, Material.GREEN_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL};
    private final BiConsumer<NightPhase, PhaseResult> onDone;
    private final NightState state;
    private final NightPlan plan;
    private final NightSettings set;
    private final SlenderConfig cfg;
    private final HogwartsBuilder section;
    private final Set<UUID> spectators;
    private final int players;
    private final int phase;
    private final Random random = new Random();
    private final Map<UUID, Item> headItems = new HashMap<>();
    private final List<BlockPos> placed = new ArrayList<>();
    private HeadLedger heads;
    private Ritual ritual;
    private NightPowers powers;
    private Location altar;
    private boolean ritualLured;
    private Cat cat;
    private Item card;
    private BlockPos brazier;
    private SnitchMotion snitch;
    private ItemDisplay snitchDisplay;
    private double snitchNextAt = Double.MAX_VALUE;
    private double snitchUntil;
    private boolean aborted;

    public NightPhase(BiConsumer<NightPhase, PhaseResult> onDone, NightState state, NightPlan plan, NightSettings set,
                      SlenderConfig cfg, HogwartsBuilder section, Set<UUID> spectators, int players) {
        this.onDone = onDone;
        this.state = state;
        this.plan = plan;
        this.set = set;
        this.cfg = cfg;
        this.section = section;
        this.spectators = Set.copyOf(spectators);
        this.players = players;
        this.phase = state.phase;
    }

    public int phase() {
        return this.phase;
    }

    public String sectionId() {
        return this.section.id();
    }

    /** Marca la fase como anulada (reload, terminar): al acabar no cuenta ni gasta vela. */
    public void abort() {
        this.aborted = true;
    }

    public boolean aborted() {
        return this.aborted;
    }

    public Set<UUID> rescued() {
        return this.heads == null ? Set.of() : this.heads.rescued();
    }

    public Set<UUID> lostNow() {
        return this.heads == null ? Set.of() : this.heads.lostAtEnd();
    }

    // ------------------------------------------------------------------ reglas

    @Override
    public int pages(int spots) {
        return this.plan.pages(this.phase, this.players, spots);
    }

    @Override
    public int timeSeconds(int pages) {
        return this.plan.timeSeconds(pages);
    }

    @Override
    public double aggression(int collected, int total, double secsSincePage, boolean escape) {
        return this.plan.aggression(this.phase, collected, total, secsSincePage, escape);
    }

    @Override
    public boolean dormantAllowed() {
        return this.plan.dormantAllowed(this.phase);
    }

    @Override
    public double staticFactor() {
        return this.plan.staticFactor(this.phase);
    }

    @Override
    public boolean startsAsSpectator(UUID id) {
        return this.spectators.contains(id);
    }

    // ------------------------------------------------------------------ ciclo

    @Override
    public void onStart(Game g) {
        World w = g.world();
        List<UUID> alive = g.aliveSurvivors().stream().map(Player::getUniqueId).toList();
        this.heads = new HeadLedger(alive);
        this.ritual = new Ritual(this.set.altar().seconds(), this.set.altar().radius(), this.set.altar().cooldown());
        this.powers = new NightPowers(this.plan, this.set, this.cfg, this.phase, this.heads,
                id -> Optional.ofNullable(this.headItems.get(id)).filter(Item::isValid));
        BlockPos a = this.section.altarWorld();
        if (a != null) {
            this.altar = new Location(w, a.x(), a.y(), a.z());
            this.place(w, a, "minecraft:soul_campfire[lit=true]");
            this.place(w, new BlockPos(a.x(), a.y() + 1, a.z()), "minecraft:cauldron");
        }
        if (this.state.mission.appearsIn(this.phase)) {
            this.spawnMission(g, w);
        }
        if (this.state.snitchPhase == this.phase && !this.state.snitchCaught) {
            this.snitchNextAt = this.now(g) + this.between(this.set.snitch().everyMin(), this.set.snitch().everyMax());
        }
    }

    @Override
    public void onTick(Game g) {
        this.powers.tick(g);
        if (this.snitch != null) {
            this.tickSnitch(g);
        }
    }

    @Override
    public void onSecond(Game g) {
        this.tickRitual(g);
        this.powers.second(g);
        double now = this.now(g);
        if (this.snitch == null && now >= this.snitchNextAt) {
            this.spawnSnitch(g);
        } else if (this.snitch != null && now >= this.snitchUntil) {
            this.removeSnitch();
            this.snitchNextAt = now + this.between(this.set.snitch().everyMin(), this.set.snitch().everyMax());
        }
        if (this.altar != null) {
            for (Item it : this.headItems.values()) {
                if (it.isValid() && it.getLocation().getY() < g.map().fallY()) {
                    it.teleport(this.besideAltar());
                    it.setVelocity(new Vector());
                }
            }
        }
    }

    @Override
    public void onKill(Game g, Player p) {
        this.dropHeads(g, p.getUniqueId(), p, p.getLocation());
    }

    @Override
    public void onLeave(Game g, UUID id, Location last) {
        if (last != null) {
            this.dropHeads(g, id, Bukkit.getPlayer(id), last);
        }
    }

    @Override
    public void onEscape(Game g, Player p) {
        List<UUID> saved = this.heads.escaped(p.getUniqueId());
        removeHeads(p, saved);
    }

    @Override
    public boolean onInteractBlock(Game g, Player p, Block b) {
        BlockPos at = new BlockPos(b.getX(), b.getY(), b.getZ());
        if (this.altar != null && at.x() == this.altar.getBlockX() && at.z() == this.altar.getBlockZ()
                && (at.y() == this.altar.getBlockY() || at.y() == this.altar.getBlockY() + 1)) {
            Optional<UUID> owner = HeadItems.ownerOf(p.getInventory().getItemInMainHand());
            if (owner.isPresent() && this.heads.carriedBy(p.getUniqueId()).contains(owner.get())
                    && this.ritual.start(p.getUniqueId(), owner.get(), this.now(g))) {
                this.ritualLured = false;
                g.msg().send(p, "noche-ritual", "seconds", String.valueOf((int) this.set.altar().seconds()));
            }
            return true;
        }
        if (at.equals(this.brazier) && b.getBlockData() instanceof Lightable l && !l.isLit()) {
            l.setLit(true);
            b.setBlockData(l, false);
            w(g).playSound(b.getLocation(), Sound.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1F, 1F);
            this.missionFound(g);
            return true;
        }
        return false;
    }

    @Override
    public boolean onInteractEntity(Game g, Player p, Entity e) {
        if (this.cat != null && e.equals(this.cat)) {
            this.cat.remove();
            this.cat = null;
            this.missionFound(g);
            return true;
        }
        return false;
    }

    @Override
    public Pickup onPickup(Game g, Player p, Item item) {
        Optional<UUID> owner = HeadItems.ownerOf(item.getItemStack());
        if (owner.isPresent()) {
            if (this.heads.pickUp(owner.get(), p.getUniqueId())) {
                this.headItems.remove(owner.get());
                g.msg().send(p, "noche-cabeza", "player", this.nameOf(owner.get()));
                return Pickup.ALLOW;
            }
            return Pickup.DENY;
        }
        if (HeadItems.isCard(item.getItemStack())) {
            if (g.isAliveSurvivor(p)) {
                item.remove();
                this.card = null;
                this.missionFound(g);
            }
            return Pickup.DENY;
        }
        return Pickup.IGNORE;
    }

    @Override
    public void onCleanup(Game g) {
        World w = g.world();
        for (BlockPos p : this.placed) {
            w.getBlockAt(p.x(), p.y(), p.z()).setType(Material.AIR, false);
        }
        this.placed.clear();
        this.headItems.values().forEach(Entity::remove);
        this.headItems.clear();
        if (this.cat != null) {
            this.cat.remove();
        }
        if (this.card != null) {
            this.card.remove();
        }
        this.removeSnitch();
        if (this.powers != null) {
            this.powers.cleanup(g);
        }
    }

    @Override
    public void onFinish(Game g, PhaseResult r) {
        boolean won = !this.aborted && !r.aborted() && r.winner() == Game.Winner.SURVIVORS;
        if (won) {
            boolean wasFailed = this.state.mission.failed();
            this.state.mission.phaseEnded(this.phase);
            if (!wasFailed && this.state.mission.failed()) {
                for (Player p : g.online()) {
                    g.msg().send(p, "noche-mision-fallida");
                }
            }
        }
        this.onDone.accept(this, r);
    }

    // ------------------------------------------------------------------ cabezas y caldero

    private void dropHeads(Game g, UUID who, Player p, Location at) {
        List<UUID> owners = this.heads.died(who);
        if (p != null) {
            removeHeads(p, owners);
        }
        for (UUID owner : owners) {
            Player o = Bukkit.getPlayer(owner);
            if (o == null) {
                continue;
            }
            Item it = at.getWorld().dropItem(at, HeadItems.make(o), i -> {
                i.setGlowing(true);
                i.setUnlimitedLifetime(true);
                i.setPickupDelay(20);
                i.setVelocity(new Vector());
                i.getScoreboardTags().add(NightPowers.TAG);
            });
            this.headItems.put(owner, it);
        }
    }

    private static void removeHeads(Player p, List<UUID> owners) {
        ItemStack[] inv = p.getInventory().getContents();
        for (int i = 0; i < inv.length; i++) {
            Optional<UUID> o = HeadItems.ownerOf(inv[i]);
            if (o.isPresent() && owners.contains(o.get())) {
                p.getInventory().setItem(i, null);
            }
        }
    }

    private void tickRitual(Game g) {
        if (!this.ritual.running() || this.altar == null) {
            return;
        }
        Player carrier = Bukkit.getPlayer(this.ritual.carrier());
        double dist = carrier == null || !carrier.getWorld().equals(this.altar.getWorld()) ? 999
                : horizontal(carrier.getLocation(), this.altar.clone().add(0.5, 0, 0.5));
        double now = this.now(g);
        switch (this.ritual.tick(now, dist, carrier != null && this.heads.alive(carrier.getUniqueId()))) {
            case RUNNING -> {
                int left = (int) Math.ceil(this.set.altar().seconds() * (1 - this.ritual.progress(now)));
                carrier.sendActionBar(g.msg().get("noche-ritual", "seconds", String.valueOf(left)));
                Location top = this.altar.clone().add(0.5, 1.6, 0.5);
                this.altar.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, top, 12, 0.3, 0.4, 0.3, 0.02);
                this.altar.getWorld().playSound(top, Sound.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1.2F, 0.6F);
                if (!this.ritualLured && g.director() != null) {
                    g.director().lureTo(this.altar);
                    this.ritualLured = true;
                }
            }
            case DONE -> {
                UUID owner = this.ritual.owner();
                removeHeads(carrier, List.of(owner));
                this.heads.revived(owner);
                g.revive(owner, this.besideAltar(), this.set.altar().battery());
                for (Player p : g.online()) {
                    g.msg().send(p, "noche-revivido", "player", this.nameOf(owner));
                }
            }
            default -> {
            }
        }
    }

    private Location besideAltar() {
        return this.altar.clone().add(1.5, 0, 0.5);
    }

    private static double horizontal(Location a, Location b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    // ------------------------------------------------------------------ misión y Snitch

    private void spawnMission(Game g, World w) {
        BlockPos at = this.floorAwayFromSpawns(20);
        Location loc = new Location(w, at.x() + 0.5, at.y(), at.z() + 0.5);
        switch (this.state.mission.type()) {
            case NORRIS -> this.cat = w.spawn(loc, Cat.class, c -> {
                c.setSitting(true);
                c.setAI(false);
                c.setInvulnerable(true);
                c.setSilent(true);
                c.setPersistent(false);
                c.getScoreboardTags().add(NightPowers.TAG);
            });
            case CROMOS -> this.card = w.dropItem(loc, HeadItems.card(), i -> {
                i.setUnlimitedLifetime(true);
                i.setVelocity(new Vector());
                i.getScoreboardTags().add(NightPowers.TAG);
            });
            case BRASEROS -> {
                int house = Math.max(0, this.state.mission.phases().indexOf(this.phase)) % HOUSES.length;
                this.place(w, at, HOUSES[house].getKey().toString());
                this.brazier = new BlockPos(at.x(), at.y() + 1, at.z());
                this.place(w, this.brazier, "minecraft:campfire[lit=false]");
            }
        }
    }

    private void missionFound(Game g) {
        this.state.mission.found(this.phase);
        if (this.state.mission.done() && !this.state.missionRewarded) {
            this.state.missionRewarded = true;
            this.gainCandle();
            for (Player p : g.online()) {
                g.msg().send(p, "noche-mision-hecha");
            }
        }
    }

    private void gainCandle() {
        Candles c = new Candles(this.state.candles, this.set.candlesMax());
        c.gain();
        this.state.candles = c.count();
    }

    private void spawnSnitch(Game g) {
        BlockPos at = this.floorAwayFromSpawns(15);
        Vec3 start = new Vec3(at.x() + 0.5, at.y() + 0.5, at.z() + 0.5);
        this.snitch = new SnitchMotion(start, this.random);
        this.snitchUntil = this.now(g) + this.set.snitch().duration();
        World w = g.world();
        this.snitchDisplay = w.spawn(new Location(w, start.x(), start.y(), start.z()), ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.GOLD_NUGGET));
            d.setGlowing(true);
            d.setPersistent(false);
            d.getScoreboardTags().add(NightPowers.TAG);
        });
    }

    private void tickSnitch(Game g) {
        World w = g.world();
        String mapId = g.map().id();
        Vec3 nearest = null;
        double best = Double.MAX_VALUE;
        for (Player p : g.aliveSurvivors()) {
            Vec3 v = SightService.vec(p.getLocation().add(0, 0.9, 0));
            double d = v.distance(this.snitch.pos());
            if (d < best) {
                best = d;
                nearest = v;
            }
            if (this.snitch.caught(v, this.set.snitch().catchDist())) {
                this.state.snitchCaught = true;
                this.gainCandle();
                for (Player o : g.online()) {
                    g.msg().send(o, "noche-snitch", "player", p.getName());
                    o.playSound(o.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1F, 1.6F);
                }
                this.removeSnitch();
                this.snitchNextAt = Double.MAX_VALUE;
                return;
            }
        }
        this.snitch.step(0.1, nearest, this.set.snitch().flee(), this.set.snitch().speed(), (x, y, z) -> SpotRules.allows(mapId, x, y, z));
        Vec3 pos = this.snitch.pos();
        Location loc = new Location(w, pos.x(), pos.y(), pos.z());
        if (this.snitchDisplay != null && this.snitchDisplay.isValid()) {
            this.snitchDisplay.teleport(loc);
        }
        w.spawnParticle(Particle.WAX_ON, loc, 2, 0.1, 0.1, 0.1, 0);
        if (g.tick() % 10 == 0) {
            w.playSound(loc, Sound.ENTITY_BEE_LOOP, SoundCategory.AMBIENT, 0.8F, 2.0F);
        }
    }

    private void removeSnitch() {
        if (this.snitchDisplay != null) {
            this.snitchDisplay.remove();
            this.snitchDisplay = null;
        }
        this.snitch = null;
    }

    // ------------------------------------------------------------------ utilidades

    private BlockPos floorAwayFromSpawns(double minDist) {
        List<BlockPos> spawns = this.section.spawnCellsWorld();
        BlockPos pick = this.section.floorWorld(this.random.nextInt(this.section.floorCount()));
        for (int i = 0; i < 60; i++) {
            BlockPos c = this.section.floorWorld(this.random.nextInt(this.section.floorCount()));
            boolean far = spawns.stream().allMatch(s -> Math.hypot(s.x() - c.x(), s.z() - c.z()) >= minDist);
            if (far) {
                return c;
            }
            pick = c;
        }
        return pick;
    }

    private void place(World w, BlockPos p, String state) {
        w.getBlockAt(p.x(), p.y(), p.z()).setBlockData(Bukkit.createBlockData(state), false);
        this.placed.add(p);
    }

    private double now(Game g) {
        return g.tick() / 20.0;
    }

    private double between(double a, double b) {
        return a + this.random.nextDouble() * (b - a);
    }

    private String nameOf(UUID id) {
        String n = this.state.members.get(id);
        return n != null ? n : Optional.ofNullable(Bukkit.getOfflinePlayer(id).getName()).orElse("?");
    }

    private static World w(Game g) {
        return g.world();
    }
}
