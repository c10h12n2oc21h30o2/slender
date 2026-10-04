// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.hub;

import dev.two.slender.player.PlayerSnapshot;
import dev.two.slender.map.BlockPos;
import org.bukkit.Particle;
import dev.two.slender.game.Game;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import dev.two.slender.player.RestoreMode;
import org.bukkit.GameMode;
import dev.two.slender.logic.HubFog;
import dev.two.slender.player.SnapshotStore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.two.slender.map.Loc;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.Optional;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import org.bukkit.scheduler.BukkitTask;
import dev.two.slender.map.Hub;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import dev.two.slender.logic.DoorTracker;
import java.util.UUID;
import java.util.Set;
import dev.two.slender.map.MapStore;
import dev.two.slender.SlenderPlugin;
import dev.two.slender.game.HubAccess;

public final class HubManager implements HubAccess
{
    private final SlenderPlugin plugin;
    private final MapStore store;
    private final Set<UUID> inside;
    private final DoorTracker doors;
    private final Map<UUID, ItemStack[]> leaving;
    private Hub hub;
    private BukkitTask task;
    private final Random mistRandom;
    
    public HubManager(final SlenderPlugin plugin, final MapStore store) {
        this.inside = new LinkedHashSet<UUID>();
        this.doors = new DoorTracker();
        this.leaving = new HashMap<UUID, ItemStack[]>();
        this.mistRandom = new Random();
        this.plugin = plugin;
        this.store = store;
        this.hub = store.loadHub().orElse(null);
    }
    
    public Optional<Hub> hub() {
        return Optional.ofNullable(this.hub);
    }
    
    public boolean inside(final UUID uuid) {
        return this.inside.contains(uuid);
    }
    
    public int size() {
        return this.inside.size();
    }
    
    public void setHub(final Hub hub) {
        this.hub = hub;
        this.store.saveHub(hub);
    }
    
    public void start() {
        if (this.task == null) {
            this.task = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, this::tick, 20L, 10L);
        }
    }
    
    private Location spawn() {
        if (this.hub == null) {
            return null;
        }
        final World world = Bukkit.getWorld(this.hub.world());
        if (world == null) {
            return null;
        }
        final Loc spawn = this.hub.spawn();
        return new Location(world, spawn.x(), spawn.y(), spawn.z(), spawn.yaw(), 0.0f);
    }
    
    public boolean enter(final Player player) {
        final UUID uniqueId = player.getUniqueId();
        if (this.inside.contains(uniqueId)) {
            return true;
        }
        final Location spawn = this.spawn();
        if (spawn == null) {
            this.plugin.msg().send((CommandSender)player, "hub-missing", new String[0]);
            return false;
        }
        final SnapshotStore snapshots = this.plugin.snapshots();
        final boolean b = !snapshots.has(uniqueId);
        if (b) {
            snapshots.save(uniqueId, snapshots.capture(player));
        }
        this.inside.add(uniqueId);
        if (!player.teleport(spawn) || !player.getWorld().equals((Object)spawn.getWorld())) {
            this.inside.remove(uniqueId);
            if (b) {
                snapshots.delete(uniqueId);
            }
            this.plugin.msg().send((CommandSender)player, "hub-tp-failed", new String[0]);
            return false;
        }
        this.doors.forget(uniqueId);
        this.prepare(player);
        this.plugin.msg().send((CommandSender)player, "hub-welcome", new String[0]);
        return true;
    }
    
    public void join(final Player player, final String s, final String s2) {
        if (this.enter(player)) {
            this.plugin.games().join(player, s, s2);
        }
    }
    
    private void prepare(final Player player) {
        SnapshotStore.resetForGame(player);
        this.plugin.snapshots().load(player.getUniqueId()).ifPresent(playerSnapshot -> SnapshotStore.applyView(player2, playerSnapshot));
        final int viewDistance = HubFog.viewDistance(player.getViewDistance(), this.plugin.cfg().hubFog);
        if (player.getViewDistance() != viewDistance) {
            player.setViewDistance(viewDistance);
        }
        if (player.getSendViewDistance() != viewDistance) {
            player.setSendViewDistance(viewDistance);
        }
        player.getInventory().setItem(4, this.plugin.items().make("hub-menu"));
        player.getInventory().setItem(8, this.plugin.items().make("hub-exit"));
        player.getInventory().setHeldItemSlot(4);
    }
    
    @Override
    public void backToHub(final Player player) {
        if (!this.inside.contains(player.getUniqueId())) {
            return;
        }
        final Location spawn = this.spawn();
        if (spawn == null) {
            this.exit(player);
            return;
        }
        player.teleport(spawn);
        this.doors.forget(player.getUniqueId());
        this.prepare(player);
    }
    
    public void exit(final Player player) {
        final UUID uniqueId = player.getUniqueId();
        if (this.plugin.games().gameOf(uniqueId).isPresent()) {
            this.plugin.games().leave(player);
        }
        if (!this.inside.remove(uniqueId)) {
            return;
        }
        this.doors.forget(uniqueId);
        this.goHome(player);
        this.plugin.msg().send((CommandSender)player, "hub-left", new String[0]);
    }
    
    private void goHome(final Player player) {
        if (!this.plugin.snapshots().restore(player, true)) {
            SnapshotStore.resetForGame(player);
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.setGameMode(GameMode.SURVIVAL);
        }
    }
    
    public void noteLeaving(final Player player) {
        if (!this.inside.contains(player.getUniqueId())) {
            return;
        }
        final ItemStack[] contents = player.getInventory().getContents();
        final ItemStack[] array = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; ++i) {
            array[i] = ((contents[i] == null) ? null : contents[i].clone());
        }
        this.leaving.put(player.getUniqueId(), array);
    }
    
    public void leftWorld(final Player player) {
        final UUID uniqueId = player.getUniqueId();
        final ItemStack[] array = this.leaving.remove(uniqueId);
        if (!this.inside.remove(uniqueId)) {
            return;
        }
        this.doors.forget(uniqueId);
        if (RestoreMode.after(array, player.getInventory().getContents()) == RestoreMode.FULL) {
            this.plugin.snapshots().restore(player, false);
        }
        else {
            this.plugin.snapshots().restoreState(player);
        }
    }
    
    public void quit(final Player player) {
        this.leaving.remove(player.getUniqueId());
        this.inside.remove(player.getUniqueId());
        this.doors.forget(player.getUniqueId());
    }
    
    public void shutdown() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
        for (final UUID uuid : new ArrayList(this.inside)) {
            this.inside.remove(uuid);
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                this.goHome(player);
            }
        }
    }
    
    private void tick() {
        if (this.hub == null || this.inside.isEmpty()) {
            return;
        }
        for (final UUID uuid : new ArrayList(this.inside)) {
            final Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (!player.getWorld().getName().equals(this.hub.world())) {
                    continue;
                }
                final Optional<Game> game = this.plugin.games().gameOf(uuid);
                if (game.isPresent() && !game.get().acceptingPlayers()) {
                    this.doors.forget(uuid);
                }
                else {
                    final Location location = player.getLocation();
                    if (location.getY() < this.hub.spawn().y() - 20.0) {
                        this.backToHub(player);
                    }
                    else if (this.hub.inExit(location.getX(), location.getY(), location.getZ())) {
                        this.exit(player);
                    }
                    else {
                        final String update = this.doors.update(uuid, this.hub.doorAt(location.getX(), location.getY(), location.getZ()).orElse(null));
                        if (update != null) {
                            this.door(player, update);
                        }
                        if (!this.plugin.cfg().hubMist) {
                            continue;
                        }
                        this.mist(player, location);
                    }
                }
            }
        }
    }
    
    private void mist(final Player player, final Location location) {
        final BlockPos origin = HubBuilder.ORIGIN;
        if (Math.abs(location.getY() - origin.y()) > 12.0) {
            return;
        }
        for (final double[] array : HubFog.mist(location.getX() - origin.x(), location.getZ() - origin.z(), 8, 14.0, this.mistRandom)) {
            player.spawnParticle(Particle.CLOUD, origin.x() + array[0], origin.y() + array[1], origin.z() + array[2], 0, this.mistRandom.nextDouble() - 0.5, 0.05, this.mistRandom.nextDouble() - 0.5, 0.03);
        }
    }
    
    private void door(final Player player, final String anObject) {
        final Optional<Game> game = this.plugin.games().gameOf(player.getUniqueId());
        if (game.isPresent() && game.get().map().id().equals(anObject)) {
            return;
        }
        if (!this.plugin.maps().containsKey(anObject)) {
            this.plugin.msg().send((CommandSender)player, "map-not-built", "map", anObject);
            return;
        }
        final Optional<Game> game2 = this.plugin.games().game(anObject);
        if (game2.isPresent() && game2.get().size() > 0) {
            this.plugin.games().join(player, anObject, null);
        }
        else {
            this.plugin.menu().openModes(player, anObject);
        }
    }
}
