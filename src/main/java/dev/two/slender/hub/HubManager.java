package dev.two.slender.hub;

import dev.two.slender.SlenderPlugin;
import dev.two.slender.game.Game;
import dev.two.slender.game.HubAccess;
import dev.two.slender.logic.DoorTracker;
import dev.two.slender.logic.HubFog;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Hub;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapStore;
import dev.two.slender.player.RestoreMode;
import dev.two.slender.player.SnapshotStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public final class HubManager implements HubAccess {
    private final SlenderPlugin plugin;
    private final MapStore store;
    private final Set<UUID> inside = new LinkedHashSet();
    private final DoorTracker doors = new DoorTracker();
    private final Map<UUID, ItemStack[]> leaving = new HashMap();
    private Hub hub;
    private BukkitTask task;
    private final Random mistRandom = new Random();

    public HubManager(SlenderPlugin var1, MapStore var2) {
        this.plugin = var1;
        this.store = var2;
        this.hub = (Hub)var2.loadHub().orElse(null);
    }

    public Optional<Hub> hub() {
        return Optional.ofNullable(this.hub);
    }

    public boolean inside(UUID var1) {
        return this.inside.contains(var1);
    }

    public int size() {
        return this.inside.size();
    }

    public void setHub(Hub var1) {
        this.hub = var1;
        this.store.saveHub(var1);
    }

    public void start() {
        if (this.task == null) {
            this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, (Runnable)(this::tick), 20L, 10L);
        }

    }

    private Location spawn() {
        if (this.hub == null) {
            return null;
        } else {
            World var1 = Bukkit.getWorld(this.hub.world());
            if (var1 == null) {
                return null;
            } else {
                Loc var2 = this.hub.spawn();
                return new Location(var1, var2.x(), var2.y(), var2.z(), var2.yaw(), 0.0F);
            }
        }
    }

    public boolean enter(Player var1) {
        UUID var2 = var1.getUniqueId();
        if (this.inside.contains(var2)) {
            return true;
        } else {
            Location var3 = this.spawn();
            if (var3 == null) {
                this.plugin.msg().send(var1, "hub-missing");
                return false;
            } else {
                SnapshotStore var4 = this.plugin.snapshots();
                boolean var5 = !var4.has(var2);
                if (var5) {
                    var4.save(var2, var4.capture(var1));
                }

                this.inside.add(var2);
                if (var1.teleport(var3) && var1.getWorld().equals(var3.getWorld())) {
                    this.doors.forget(var2);
                    this.prepare(var1);
                    this.plugin.msg().send(var1, "hub-welcome");
                    return true;
                } else {
                    this.inside.remove(var2);
                    if (var5) {
                        var4.delete(var2);
                    }

                    this.plugin.msg().send(var1, "hub-tp-failed");
                    return false;
                }
            }
        }
    }

    public void join(Player var1, String var2, String var3) {
        if (this.enter(var1)) {
            this.plugin.games().join(var1, var2, var3);
        }

    }

    private void prepare(Player var1) {
        SnapshotStore.resetForGame(var1);
        this.plugin.snapshots().load(var1.getUniqueId()).ifPresent((var1x) -> {
            SnapshotStore.applyView(var1, var1x);
        });
        int var2 = HubFog.viewDistance(var1.getViewDistance(), this.plugin.cfg().hubFog);
        if (var1.getViewDistance() != var2) {
            var1.setViewDistance(var2);
        }

        if (var1.getSendViewDistance() != var2) {
            var1.setSendViewDistance(var2);
        }

        var1.getInventory().setItem(4, this.plugin.items().make("hub-menu"));
        var1.getInventory().setItem(8, this.plugin.items().make("hub-exit"));
        var1.getInventory().setHeldItemSlot(4);
    }

    public void backToHub(Player var1) {
        if (this.inside.contains(var1.getUniqueId())) {
            Location var2 = this.spawn();
            if (var2 == null) {
                this.exit(var1);
            } else {
                var1.teleport(var2);
                this.doors.forget(var1.getUniqueId());
                this.prepare(var1);
            }
        }
    }

    public void exit(Player var1) {
        UUID var2 = var1.getUniqueId();
        if (this.plugin.games().gameOf(var2).isPresent()) {
            this.plugin.games().leave(var1);
        }

        if (this.inside.remove(var2)) {
            this.doors.forget(var2);
            this.goHome(var1);
            this.plugin.msg().send(var1, "hub-left");
        }
    }

    private void goHome(Player var1) {
        if (!this.plugin.snapshots().restore(var1, true)) {
            SnapshotStore.resetForGame(var1);
            var1.teleport(((World)Bukkit.getWorlds().get(0)).getSpawnLocation());
            var1.setGameMode(GameMode.SURVIVAL);
        }

    }

    public void noteLeaving(Player var1) {
        if (this.inside.contains(var1.getUniqueId())) {
            ItemStack[] var2 = var1.getInventory().getContents();
            ItemStack[] var3 = new ItemStack[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
                var3[var4] = var2[var4] == null ? null : var2[var4].clone();
            }

            this.leaving.put(var1.getUniqueId(), var3);
        }
    }

    public void leftWorld(Player var1) {
        UUID var2 = var1.getUniqueId();
        ItemStack[] var3 = (ItemStack[])this.leaving.remove(var2);
        if (this.inside.remove(var2)) {
            this.doors.forget(var2);
            if (RestoreMode.after(var3, var1.getInventory().getContents()) == RestoreMode.FULL) {
                this.plugin.snapshots().restore(var1, false);
            } else {
                this.plugin.snapshots().restoreState(var1);
            }

        }
    }

    public void quit(Player var1) {
        this.leaving.remove(var1.getUniqueId());
        this.inside.remove(var1.getUniqueId());
        this.doors.forget(var1.getUniqueId());
    }

    public void shutdown() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        Iterator var1 = (new ArrayList(this.inside)).iterator();

        while(var1.hasNext()) {
            UUID var2 = (UUID)var1.next();
            this.inside.remove(var2);
            Player var3 = Bukkit.getPlayer(var2);
            if (var3 != null) {
                this.goHome(var3);
            }
        }

    }

    private void tick() {
        if (this.hub != null && !this.inside.isEmpty()) {
            Iterator var1 = (new ArrayList(this.inside)).iterator();

            while(true) {
                while(true) {
                    UUID var2;
                    Player var3;
                    do {
                        do {
                            if (!var1.hasNext()) {
                                return;
                            }

                            var2 = (UUID)var1.next();
                            var3 = Bukkit.getPlayer(var2);
                        } while(var3 == null);
                    } while(!var3.getWorld().getName().equals(this.hub.world()));

                    Optional var4 = this.plugin.games().gameOf(var2);
                    if (var4.isPresent() && !((Game)var4.get()).acceptingPlayers()) {
                        this.doors.forget(var2);
                    } else {
                        Location var5 = var3.getLocation();
                        if (var5.getY() < this.hub.spawn().y() - 20.0) {
                            this.backToHub(var3);
                        } else if (this.hub.inExit(var5.getX(), var5.getY(), var5.getZ())) {
                            this.exit(var3);
                        } else {
                            String var6 = this.doors.update(var2, (String)this.hub.doorAt(var5.getX(), var5.getY(), var5.getZ()).orElse(null));
                            if (var6 != null) {
                                this.door(var3, var6);
                            }

                            if (this.plugin.cfg().hubMist) {
                                this.mist(var3, var5);
                            }
                        }
                    }
                }
            }
        }
    }

    private void mist(Player var1, Location var2) {
        BlockPos var3 = HubBuilder.ORIGIN;
        if (!(Math.abs(var2.getY() - (double)var3.y()) > 12.0)) {
            Iterator var4 = HubFog.mist(var2.getX() - (double)var3.x(), var2.getZ() - (double)var3.z(), 8, 14.0, this.mistRandom).iterator();

            while(var4.hasNext()) {
                double[] var5 = (double[])var4.next();
                var1.spawnParticle(Particle.CLOUD, (double)var3.x() + var5[0], (double)var3.y() + var5[1], (double)var3.z() + var5[2], 0, this.mistRandom.nextDouble() - 0.5, 0.05, this.mistRandom.nextDouble() - 0.5, 0.03);
            }

        }
    }

    private void door(Player var1, String var2) {
        Optional var3 = this.plugin.games().gameOf(var1.getUniqueId());
        if (!var3.isPresent() || !((Game)var3.get()).map().id().equals(var2)) {
            if (!this.plugin.maps().containsKey(var2)) {
                this.plugin.msg().send(var1, "map-not-built", "map", var2);
            } else {
                Optional var4 = this.plugin.games().game(var2);
                if (var4.isPresent() && ((Game)var4.get()).size() > 0) {
                    this.plugin.games().join(var1, var2, (String)null);
                } else {
                    this.plugin.menu().openModes(var1, var2);
                }

            }
        }
    }
}
