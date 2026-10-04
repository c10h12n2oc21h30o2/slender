package dev.two.slender.map;

import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import dev.two.slender.hub.HubBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class BuildManager {
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final MapStore store;
    private final Map<String, ArenaMap> maps;
    private final Map<String, MapBuilder> builders = new LinkedHashMap();
    private BukkitTask task;
    private String building;

    public BuildManager(Plugin var1, SlenderConfig var2, Msg var3, MapStore var4, Map<String, ArenaMap> var5, List<MapBuilder> var6) {
        this.plugin = var1;
        this.cfg = var2;
        this.msg = var3;
        this.store = var4;
        this.maps = var5;
        Iterator var7 = var6.iterator();

        while(var7.hasNext()) {
            MapBuilder var8 = (MapBuilder)var7.next();
            this.builders.put(var8.id(), var8);
        }

    }

    public Collection<MapBuilder> builders() {
        return this.builders.values();
    }

    public Optional<MapBuilder> builder(String var1) {
        return Optional.ofNullable((MapBuilder)this.builders.get(var1));
    }

    public boolean isBuilding() {
        return this.building != null;
    }

    public BlockPos originOf(String var1) {
        int var2 = (new ArrayList(this.builders.keySet())).indexOf(var1);
        return new BlockPos(Math.max(0, var2) * 2000, 64, 0);
    }

    public void build(String var1, World var2, CommandSender var3, Runnable var4) {
        MapBuilder var5 = (MapBuilder)this.builders.get(var1);
        if (var5 != null && this.building == null) {
            BlockPos var6 = this.originOf(var1);
            long var7 = (long)var1.hashCode() * 31L + 1234L;
            int var9 = var5.radius();
            BlockQueue var10 = new BlockQueue();
            Builder var11 = new Builder(var10, var6.x(), var6.y(), var6.z(), new Random(var7));
            var11.clear(-var9, -12, -var9, var9, 60, var9);
            ArenaMap var12 = var5.build(var11, var2.getName(), var7);
            this.run(var5.displayName(), var6, var9, var10, var2, var3, () -> {
                this.store.save(var12);
                this.maps.put(var12.id(), var12);
            }, var4);
        }
    }

    public void buildHub(World var1, CommandSender var2, Consumer<Hub> var3, Runnable var4) {
        if (this.building == null) {
            BlockPos var5 = HubBuilder.ORIGIN;
            byte var6 = 41;
            BlockQueue var7 = new BlockQueue();
            Builder var8 = new Builder(var7, var5.x(), var5.y(), var5.z(), new Random(1313L));
            var8.clear(-var6, -12, -var6, var6, 60, var6);
            Hub var9 = HubBuilder.build(var8, var1.getName());
            this.run(this.msg.raw("hub-name"), var5, var6, var7, var1, var2, () -> {
                var3.accept(var9);
            }, var4);
        }
    }

    private void run(String var1, BlockPos var2, int var3, BlockQueue var4, World var5, CommandSender var6, Runnable var7, Runnable var8) {
        this.building = var1;
        this.msg.send(var6, "build-start", "map", var1);
        int var9 = var2.x() - var3 >> 4;
        int var10 = var2.x() + var3 >> 4;
        int var11 = var2.z() - var3 >> 4;
        int var12 = var2.z() + var3 >> 4;
        ArrayList var13 = new ArrayList();

        for(int var14 = var9; var14 <= var10; ++var14) {
            for(int var15 = var11; var15 <= var12; ++var15) {
                var13.add(var5.getChunkAtAsync(var14, var15));
                var5.addPluginChunkTicket(var14, var15, this.plugin);
            }
        }

        CompletableFuture.allOf((CompletableFuture[])var13.toArray(new CompletableFuture[0])).whenComplete((ignoredA, ignoredB) -> {
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                long budgetNanos = (long)this.cfg.buildMaxMsPerTick * 1000000L;
                int[] ticks = new int[]{0};
                this.task = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
                    boolean var14 = var4.tick(var5, this.cfg.buildMaxBlocksPerTick, budgetNanos);
                    if (++ticks[0] % 100 == 0 && !var14) {
                        this.msg.send(var6, "build-progress", "map", var1, "percent", String.valueOf((int)(var4.progress() * 100.0)));
                    }

                    if (var14) {
                        this.task.cancel();
                        this.task = null;

                        for(int var15 = var9; var15 <= var10; ++var15) {
                            for(int var16 = var11; var16 <= var12; ++var16) {
                                var5.removePluginChunkTicket(var15, var16, this.plugin);
                            }
                        }

                        var7.run();
                        this.building = null;
                        this.msg.send(var6, "build-done", "map", var1);
                        var8.run();
                    }

                }, 1L, 1L);
            });
        });
    }

    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
        }

        this.task = null;
        this.building = null;
    }
}
