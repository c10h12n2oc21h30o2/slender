package dev.two.slender.game;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.DirectorMath;
import dev.two.slender.logic.IsolationTracker;
import dev.two.slender.logic.TargetSelector;
import dev.two.slender.logic.Vec3;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.SpotRules;
import dev.two.slender.map.Zone;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class Director {
    private final SlenderConfig cfg;
    private final SightService sight;
    private final SlenderBody body;
    private final ArenaMap map;
    private final BiConsumer<Player, String> killer;
    private final IsolationTracker isolation;
    private UUID target;
    private long targetSince;
    private long nextTeleport;
    /** LA NOCHE ENTERA: a quién ir y a qué distancia en el próximo teletransporte, adónde ir, o parado. */
    private UUID lureTarget;
    private double lureFactor = 1.0;
    private Location lureLoc;
    private double lureMin = 6;
    private double lureMax = 10;
    private boolean suspended;

    public Director(SlenderConfig var1, SightService var2, SlenderBody var3, ArenaMap var4, BiConsumer<Player, String> var5) {
        this.cfg = var1;
        this.sight = var2;
        this.body = var3;
        this.map = var4;
        this.killer = var5;
        this.isolation = new IsolationTracker(var1.isolationMinPlayersExclusive, var1.isolationRadius, var1.isolationSeconds);
    }

    public UUID target() {
        return this.target;
    }

    public IsolationTracker isolation() {
        return this.isolation;
    }

    /** El próximo teletransporte va a por ese jugador, a distanceFactor de la distancia normal (mínimo 7). */
    public void lurePlayer(UUID id, double distanceFactor) {
        this.lureTarget = id;
        this.lureFactor = distanceFactor;
    }

    /** El próximo teletransporte coloca a Slender a 6–10 bloques de loc (si no lo ven). */
    public void lureTo(Location loc) {
        this.lureTo(loc, 6, 10);
    }

    /** Igual, a [min, max] bloques de loc (cazar cabezas: justo al lado). */
    public void lureTo(Location loc, double min, double max) {
        this.lureLoc = loc;
        this.lureMin = min;
        this.lureMax = max;
    }

    public void suspend(boolean on) {
        this.suspended = on;
    }

    public boolean suspended() {
        return this.suspended;
    }

    public boolean observed(List<Player> players) {
        return this.body.alive() && this.sight.observedByAny(players, this.body.center(), this.body.head());
    }

    private double distance(double aggression) {
        return Math.max(7.0, DirectorMath.teleportDistance(aggression) * this.lureFactor);
    }

    private void lureUsed() {
        this.lureTarget = null;
        this.lureFactor = 1.0;
    }

    /** Coloca a Slender cerca de lureLoc, en suelo válido y sin que lo vean. */
    private boolean placeNearLure(Player face, List<Player> players, Random r) {
        Location loc = this.lureLoc;
        this.lureLoc = null;
        if (loc == null) {
            return false;
        }
        for (int i = 0; i < 12; i++) {
            double ang = r.nextDouble() * Math.PI * 2;
            double d = this.lureMin + r.nextDouble() * (this.lureMax - this.lureMin);
            Location c = loc.clone().add(Math.cos(ang) * d, 0, Math.sin(ang) * d);
            Location g = SlenderBody.ground(c, 4, 6);
            if (g == null || !SpotRules.allows(this.map.id(), g.getBlockX(), g.getBlockY(), g.getBlockZ())) {
                continue;
            }
            if (this.sight.observedByAny(players, g.clone().add(0, 1.4, 0), g.clone().add(0, 2.3, 0))) {
                continue;
            }
            if (!this.body.alive()) {
                this.body.spawn(g, true);
            }
            this.body.moveTo(g, face.getLocation());
            return true;
        }
        return false;
    }

    public void tick(List<Player> var1, Function<UUID, SurvivorState> var2, Predicate<UUID> var3, double var4, boolean var6, long var7, Random var9) {
        LinkedHashMap var10 = new LinkedHashMap();
        Iterator var11 = var1.iterator();

        while(var11.hasNext()) {
            Player var12 = (Player)var11.next();
            var10.put(var12.getUniqueId(), SightService.vec(var12.getLocation()));
        }

        this.isolation.update(var10, 0.5);
        if (this.suspended) {
            return;
        }
        if (!var1.isEmpty() && !var6) {
            Player var13 = this.pickTarget(var1, var2, var3, var7, var9);
            if (var13 != null) {
                if (!this.body.alive()) {
                    if (this.placeNearLure(var13, var1, var9) || this.appear(var13, var1, var4, var9)) {
                        this.nextTeleport = var7 + ticks(DirectorMath.teleportIntervalSeconds(var4));
                    }

                } else {
                    boolean var14 = this.sight.observedByAny(var1, this.body.center(), this.body.head());
                    if (!var14 && var7 >= this.nextTeleport) {
                        if (!this.placeNearLure(var13, var1, var9)) {
                            this.teleport(var13, var1, var4, var9);
                        }
                        this.nextTeleport = var7 + ticks(DirectorMath.teleportIntervalSeconds(var4));
                    } else if (!var14) {
                        this.creep(var13, var4);
                    }

                    this.body.moveTo(this.body.location(), var13.getLocation());
                    if (this.body.location().distance(var13.getLocation()) < this.cfg.catchDistance && this.sight.lineOfSight(this.body.head(), var13.getEyeLocation())) {
                        this.killer.accept(var13, "caught");
                    }

                }
            }
        }
    }

    private static long ticks(double var0) {
        return Math.round(var0 * 20.0);
    }

    private Player pickTarget(List<Player> var1, Function<UUID, SurvivorState> var2, Predicate<UUID> var3, long var4, Random var6) {
        if (this.lureTarget != null) {
            for (Player p : var1) {
                if (p.getUniqueId().equals(this.lureTarget)) {
                    this.target = this.lureTarget;
                    this.targetSince = var4;
                    return p;
                }
            }
        }
        Optional var7 = this.isolation.priorityTarget();
        boolean var8 = this.target != null && var1.stream().anyMatch((var1x) -> {
            return var1x.getUniqueId().equals(this.target);
        });
        if (TargetSelector.shouldRetarget(this.target, var8, (double)(var4 - this.targetSince) / 20.0, var7)) {
            ArrayList var9 = new ArrayList();
            Iterator var10 = var1.iterator();

            while(var10.hasNext()) {
                Player var11 = (Player)var10.next();
                SurvivorState var12 = (SurvivorState)var2.apply(var11.getUniqueId());
                var9.add(new TargetSelector.Candidate(var11.getUniqueId(), var12 == null ? 0 : var12.pages, var3.test(var11.getUniqueId()), this.isolation.isIsolated(var11.getUniqueId())));
            }

            this.target = (UUID)var7.orElseGet(() -> {
                return (UUID)TargetSelector.pick(var9, var6).orElse(null);
            });
            this.targetSince = var4;
        }

        Iterator var14 = var1.iterator();

        Player var13;
        do {
            if (!var14.hasNext()) {
                return null;
            }

            var13 = (Player)var14.next();
        } while(!var13.getUniqueId().equals(this.target));

        return var13;
    }

    private DirectorMath.Bounds bounds() {
        Zone var1 = this.map.roamArea();
        return new DirectorMath.Bounds((double)var1.min().x(), (double)var1.min().z(), (double)var1.max().x(), (double)var1.max().z());
    }

    private Location hiddenSpot(Player var1, List<Player> var2, double var3, Random var5) {
        World var6 = var1.getWorld();
        Vec3 var7 = SightService.dir(var1.getEyeLocation());
        double var8 = this.body.alive() ? this.body.height() : 2.6;
        int var10 = var1.getLocation().getBlockY();
        Location var11 = null;
        Iterator var12 = DirectorMath.teleportCandidates(SightService.vec(var1.getLocation()), var7, var3, 24, 100.0, var5, this.bounds()).iterator();

        while(true) {
            Location var14;
            int var15;
            do {
                do {
                    do {
                        if (!var12.hasNext()) {
                            return var11;
                        }

                        Vec3 var13 = (Vec3)var12.next();
                        var14 = SlenderBody.ground(new Location(var6, var13.x(), var13.y(), var13.z()), 6, 8);
                        // fuera del suelo al que llegan los jugadores (tras un derrumbe, dentro de roca) no sirve de nada
                    } while(var14 == null || !SpotRules.allows(this.map.id(), var14.getBlockX(), var14.getBlockY(), var14.getBlockZ()));

                    var15 = DirectorMath.levelRank(var10, var14.getBlockY());
                } while(var15 < 0);
            } while(var15 == 1 && var11 != null);

            if (!this.sight.observedByAny(var2, var14.clone().add(0.0, var8 * 0.55, 0.0), var14.clone().add(0.0, var8 * 0.9, 0.0))) {
                if (var15 == 0) {
                    return var14;
                }

                var11 = var14;
            }
        }
    }

    private boolean appear(Player var1, List<Player> var2, double var3, Random var5) {
        Location var6 = this.hiddenSpot(var1, var2, this.distance(var3), var5);
        if (var6 == null) {
            var6 = this.hiddenSpot(var1, var2, 30.0, var5);
        }

        if (var6 == null) {
            return false;
        } else {
            this.body.spawn(var6, true);
            this.body.moveTo(var6, var1.getLocation());
            this.lureUsed();
            return true;
        }
    }

    private void teleport(Player var1, List<Player> var2, double var3, Random var5) {
        if (var5.nextDouble() < DirectorMath.frontAppearChance(var3)) {
            Vec3 var6 = DirectorMath.frontPosition(SightService.vec(var1.getLocation()), SightService.dir(var1.getEyeLocation()), 4.0 + var5.nextDouble() * 2.0);
            Location var7 = SlenderBody.ground(new Location(var1.getWorld(), var6.x(), var6.y(), var6.z()), 3, 4);
            boolean var8 = var7 != null && DirectorMath.levelRank(var1.getLocation().getBlockY(), var7.getBlockY()) == 0;
            if (var8 && this.map.inBounds(var7.getX(), var7.getZ()) && this.sight.lineOfSight(var1.getEyeLocation(), var7.clone().add(0.0, 1.5, 0.0))) {
                this.body.moveTo(var7, var1.getLocation());
                var1.playSound(var1.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 0.8F, 0.6F);
                return;
            }
        }

        Location var9 = this.hiddenSpot(var1, var2, this.distance(var3), var5);
        if (var9 != null) {
            this.body.moveTo(var9, var1.getLocation());
            this.lureUsed();
        }

    }

    private void creep(Player var1, double var2) {
        World var4 = var1.getWorld();
        Location var5 = this.body.location();
        Location var6 = null;

        Location var10;
        for(Iterator var7 = DirectorMath.subSteps(SightService.vec(var5), SightService.vec(var1.getLocation()), DirectorMath.creepSpeedPerTick(var2) * 10.0, 0.5).iterator(); var7.hasNext(); var6 = var10) {
            Vec3 var8 = (Vec3)var7.next();
            Location var9 = SlenderBody.ground(new Location(var4, var8.x(), var5.getY(), var8.z()), 1, 2);
            if (var9 == null) {
                break;
            }

            var10 = new Location(var4, var8.x(), var9.getY(), var8.z());
            if (!this.sight.lineOfSight(var5.clone().add(0.0, 1.0, 0.0), var10.clone().add(0.0, 1.0, 0.0))) {
                break;
            }

            var5 = var10;
        }

        if (var6 != null) {
            this.body.moveTo(var6, var1.getLocation());
        }

    }

    public void clear() {
        this.body.remove();
        this.target = null;
    }
}
