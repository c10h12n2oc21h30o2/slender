// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.SoundCategory;
import org.bukkit.Sound;
import org.bukkit.World;
import dev.two.slender.map.Zone;
import java.util.Optional;
import java.util.ArrayList;
import dev.two.slender.logic.TargetSelector;
import java.util.Iterator;
import java.util.Collection;
import org.bukkit.Location;
import dev.two.slender.logic.DirectorMath;
import dev.two.slender.logic.Vec3;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.List;
import java.util.UUID;
import dev.two.slender.logic.IsolationTracker;
import org.bukkit.entity.Player;
import java.util.function.BiConsumer;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.config.SlenderConfig;

public final class Director
{
    private final SlenderConfig cfg;
    private final SightService sight;
    private final SlenderBody body;
    private final ArenaMap map;
    private final BiConsumer<Player, String> killer;
    private final IsolationTracker isolation;
    private UUID target;
    private long targetSince;
    private long nextTeleport;
    
    public Director(final SlenderConfig cfg, final SightService sight, final SlenderBody body, final ArenaMap map, final BiConsumer<Player, String> killer) {
        this.cfg = cfg;
        this.sight = sight;
        this.body = body;
        this.map = map;
        this.killer = killer;
        this.isolation = new IsolationTracker(cfg.isolationMinPlayersExclusive, cfg.isolationRadius, cfg.isolationSeconds);
    }
    
    public UUID target() {
        return this.target;
    }
    
    public IsolationTracker isolation() {
        return this.isolation;
    }
    
    public void tick(final List<Player> list, final Function<UUID, SurvivorState> function, final Predicate<UUID> predicate, final double n, final boolean b, final long n2, final Random random) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (final Player player : list) {
            linkedHashMap.put(player.getUniqueId(), SightService.vec(player.getLocation()));
        }
        this.isolation.update(linkedHashMap, 0.5);
        if (list.isEmpty() || b) {
            return;
        }
        final Player pickTarget = this.pickTarget(list, function, predicate, n2, random);
        if (pickTarget == null) {
            return;
        }
        if (!this.body.alive()) {
            if (this.appear(pickTarget, list, n, random)) {
                this.nextTeleport = n2 + ticks(DirectorMath.teleportIntervalSeconds(n));
            }
            return;
        }
        final boolean observedByAny = this.sight.observedByAny(list, this.body.center(), this.body.head());
        if (!observedByAny && n2 >= this.nextTeleport) {
            this.teleport(pickTarget, list, n, random);
            this.nextTeleport = n2 + ticks(DirectorMath.teleportIntervalSeconds(n));
        }
        else if (!observedByAny) {
            this.creep(pickTarget, n);
        }
        this.body.moveTo(this.body.location(), pickTarget.getLocation());
        if (this.body.location().distance(pickTarget.getLocation()) < this.cfg.catchDistance && this.sight.lineOfSight(this.body.head(), pickTarget.getEyeLocation())) {
            this.killer.accept(pickTarget, "caught");
        }
    }
    
    private static long ticks(final double n) {
        return Math.round(n * 20.0);
    }
    
    private Player pickTarget(final List<Player> list, final Function<UUID, SurvivorState> function, final Predicate<UUID> predicate, final long targetSince, final Random random) {
        final Optional<UUID> priorityTarget = this.isolation.priorityTarget();
        if (TargetSelector.shouldRetarget(this.target, this.target != null && list.stream().anyMatch(player3 -> player3.getUniqueId().equals(this.target)), (targetSince - this.targetSince) / 20.0, priorityTarget)) {
            final ArrayList list2 = new ArrayList();
            for (final Player player : list) {
                final SurvivorState survivorState = function.apply(player.getUniqueId());
                list2.add(new TargetSelector.Candidate(player.getUniqueId(), (survivorState == null) ? 0 : survivorState.pages, predicate.test(player.getUniqueId()), this.isolation.isIsolated(player.getUniqueId())));
            }
            this.target = priorityTarget.orElseGet(() -> TargetSelector.pick(list3, random2).orElse(null));
            this.targetSince = targetSince;
        }
        for (final Player player2 : list) {
            if (player2.getUniqueId().equals(this.target)) {
                return player2;
            }
        }
        return null;
    }
    
    private DirectorMath.Bounds bounds() {
        final Zone roamArea = this.map.roamArea();
        return new DirectorMath.Bounds(roamArea.min().x(), roamArea.min().z(), roamArea.max().x(), roamArea.max().z());
    }
    
    private Location hiddenSpot(final Player player, final List<Player> list, final double n, final Random random) {
        final World world = player.getWorld();
        final Vec3 dir = SightService.dir(player.getEyeLocation());
        final double n2 = this.body.alive() ? this.body.height() : 2.6;
        final int blockY = player.getLocation().getBlockY();
        Location location = null;
        for (final Vec3 vec3 : DirectorMath.teleportCandidates(SightService.vec(player.getLocation()), dir, n, 24, 100.0, random, this.bounds())) {
            final Location ground = SlenderBody.ground(new Location(world, vec3.x(), vec3.y(), vec3.z()), 6, 8);
            if (ground == null) {
                continue;
            }
            final int levelRank = DirectorMath.levelRank(blockY, ground.getBlockY());
            if (levelRank < 0) {
                continue;
            }
            if (levelRank == 1 && location != null) {
                continue;
            }
            if (this.sight.observedByAny(list, ground.clone().add(0.0, n2 * 0.55, 0.0), ground.clone().add(0.0, n2 * 0.9, 0.0))) {
                continue;
            }
            if (levelRank == 0) {
                return ground;
            }
            location = ground;
        }
        return location;
    }
    
    private boolean appear(final Player player, final List<Player> list, final double n, final Random random) {
        Location location = this.hiddenSpot(player, list, DirectorMath.teleportDistance(n), random);
        if (location == null) {
            location = this.hiddenSpot(player, list, 30.0, random);
        }
        if (location == null) {
            return false;
        }
        this.body.spawn(location, true);
        this.body.moveTo(location, player.getLocation());
        return true;
    }
    
    private void teleport(final Player player, final List<Player> list, final double n, final Random random) {
        if (random.nextDouble() < DirectorMath.frontAppearChance(n)) {
            final Vec3 frontPosition = DirectorMath.frontPosition(SightService.vec(player.getLocation()), SightService.dir(player.getEyeLocation()), 4.0 + random.nextDouble() * 2.0);
            final Location ground = SlenderBody.ground(new Location(player.getWorld(), frontPosition.x(), frontPosition.y(), frontPosition.z()), 3, 4);
            if (ground != null && DirectorMath.levelRank(player.getLocation().getBlockY(), ground.getBlockY()) == 0 && this.map.inBounds(ground.getX(), ground.getZ()) && this.sight.lineOfSight(player.getEyeLocation(), ground.clone().add(0.0, 1.5, 0.0))) {
                this.body.moveTo(ground, player.getLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 0.8f, 0.6f);
                return;
            }
        }
        final Location hiddenSpot = this.hiddenSpot(player, list, DirectorMath.teleportDistance(n), random);
        if (hiddenSpot != null) {
            this.body.moveTo(hiddenSpot, player.getLocation());
        }
    }
    
    private void creep(final Player player, final double n) {
        final World world = player.getWorld();
        Location location = this.body.location();
        Location location2 = null;
        for (final Vec3 vec3 : DirectorMath.subSteps(SightService.vec(location), SightService.vec(player.getLocation()), DirectorMath.creepSpeedPerTick(n) * 10.0, 0.5)) {
            final Location ground = SlenderBody.ground(new Location(world, vec3.x(), location.getY(), vec3.z()), 1, 2);
            if (ground == null) {
                break;
            }
            final Location location3 = new Location(world, vec3.x(), ground.getY(), vec3.z());
            if (!this.sight.lineOfSight(location.clone().add(0.0, 1.0, 0.0), location3.clone().add(0.0, 1.0, 0.0))) {
                break;
            }
            location = location3;
            location2 = location3;
        }
        if (location2 != null) {
            this.body.moveTo(location2, player.getLocation());
        }
    }
    
    public void clear() {
        this.body.remove();
        this.target = null;
    }
}
