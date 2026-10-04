// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import org.bukkit.Bukkit;
import java.util.concurrent.CompletableFuture;
import dev.two.slender.hub.HubBuilder;
import java.util.function.Consumer;
import java.util.Random;
import org.bukkit.command.CommandSender;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;
import java.util.Map;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.Plugin;

public final class BuildManager
{
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final MapStore store;
    private final Map<String, ArenaMap> maps;
    private final Map<String, MapBuilder> builders;
    private BukkitTask task;
    private String building;
    
    public BuildManager(final Plugin plugin, final SlenderConfig cfg, final Msg msg, final MapStore store, final Map<String, ArenaMap> maps, final List<MapBuilder> list) {
        this.builders = new LinkedHashMap<String, MapBuilder>();
        this.plugin = plugin;
        this.cfg = cfg;
        this.msg = msg;
        this.store = store;
        this.maps = maps;
        for (final MapBuilder mapBuilder : list) {
            this.builders.put(mapBuilder.id(), mapBuilder);
        }
    }
    
    public Collection<MapBuilder> builders() {
        return this.builders.values();
    }
    
    public Optional<MapBuilder> builder(final String s) {
        return Optional.ofNullable(this.builders.get(s));
    }
    
    public boolean isBuilding() {
        return this.building != null;
    }
    
    public BlockPos originOf(final String o) {
        return new BlockPos(Math.max(0, new ArrayList(this.builders.keySet()).indexOf(o)) * 2000, 64, 0);
    }
    
    public void build(final String s, final World world2, final CommandSender commandSender, final Runnable runnable) {
        final MapBuilder mapBuilder2 = this.builders.get(s);
        if (mapBuilder2 == null || this.building != null) {
            return;
        }
        final BlockPos origin = this.originOf(s);
        final long n = s.hashCode() * 31L + 1234L;
        final int radius = mapBuilder2.radius();
        final BlockQueue blockQueue = new BlockQueue();
        new Builder(blockQueue, origin.x(), origin.y(), origin.z(), new Random(n)).clear(-radius, -12, -radius, radius, 60, radius);
        this.run(mapBuilder2.displayName(), origin, radius, blockQueue, world2, commandSender, () -> {
            final MapBuilder mapBuilder2;
            final long n;
            final Builder builder;
            mapBuilder2.build(builder, world2.getName(), n);
            this.store.save(arenaMap);
            this.maps.put(arenaMap.id(), arenaMap);
        }, runnable);
    }
    
    public void buildHub(final World world2, final CommandSender commandSender, final Consumer<Hub> consumer, final Runnable runnable) {
        if (this.building != null) {
            return;
        }
        final BlockPos origin = HubBuilder.ORIGIN;
        final int n = 41;
        final BlockQueue blockQueue = new BlockQueue();
        new Builder(blockQueue, origin.x(), origin.y(), origin.z(), new Random(1313L)).clear(-n, -12, -n, n, 60, n);
        this.run(this.msg.raw("hub-name"), origin, n, blockQueue, world2, commandSender, () -> {
            final Builder builder;
            HubBuilder.build(builder, world2.getName());
            consumer2.accept(hub);
        }, runnable);
    }
    
    private void run(final String building, final BlockPos blockPos, final int n, final BlockQueue blockQueue, final World world, final CommandSender commandSender, final Runnable runnable, final Runnable runnable2) {
        this.building = building;
        this.msg.send(commandSender, "build-start", "map", building);
        final int n2 = blockPos.x() - n >> 4;
        final int n3 = blockPos.x() + n >> 4;
        final int n4 = blockPos.z() - n >> 4;
        final int n5 = blockPos.z() + n >> 4;
        final ArrayList list = new ArrayList();
        for (int i = n2; i <= n3; ++i) {
            for (int j = n4; j <= n5; ++j) {
                list.add(world.getChunkAtAsync(i, j));
                world.addPluginChunkTicket(i, j, this.plugin);
            }
        }
        CompletableFuture.allOf((CompletableFuture<?>[])list.toArray(new CompletableFuture[0])).whenComplete((p10, p11) -> Bukkit.getScheduler().runTask(this.plugin, () -> {
            final long n6 = this.cfg.buildMaxMsPerTick * 1000000L;
            this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
                final Object o = { 0 };
                blockQueue2.tick(world2, this.cfg.buildMaxBlocksPerTick, n7);
                final boolean b;
                if (++array[0] % 100 == 0 && !b) {
                    this.msg.send(commandSender2, "build-progress", "map", s, "percent", String.valueOf((int)(blockQueue2.progress() * 100.0)));
                }
                if (b) {
                    this.task.cancel();
                    this.task = null;
                    int k = 0;
                    while (k <= n8) {
                        int l = 0;
                        while (l <= n9) {
                            world2.removePluginChunkTicket(k, l, this.plugin);
                            ++l;
                        }
                        ++k;
                    }
                    runnable3.run();
                    this.building = null;
                    this.msg.send(commandSender2, "build-done", "map", s);
                    runnable4.run();
                }
            }, 1L, 1L);
        }));
    }
    
    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
        }
        this.task = null;
        this.building = null;
    }
}
