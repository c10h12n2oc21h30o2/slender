package dev.two.slender.game;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.BatteryModel;
import dev.two.slender.logic.LightDiff;
import io.papermc.paper.math.Position;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.LongPredicate;
import java.util.function.Supplier;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class Flashlights {
    private final SlenderConfig cfg;
    private final Map<UUID, State> states = new HashMap();
    private final BlockData[] lightData = new BlockData[16];
    private Supplier<Set<Long>> reserved = Set::of;

    public Flashlights(SlenderConfig var1) {
        this.cfg = var1;

        for(int var2 = 0; var2 < 16; ++var2) {
            final int level = var2;
            this.lightData[var2] = Material.LIGHT.createBlockData((var1x) -> {
                ((Levelled)var1x).setLevel(level);
            });
        }

    }

    public void reserve(Supplier<Set<Long>> var1) {
        this.reserved = var1;
    }

    public void register(UUID var1) {
        this.states.put(var1, new State(new BatteryModel(this.cfg.batteryDrainPerSecond)));
    }

    public void toggle(Player var1) {
        State var2 = (State)this.states.get(var1.getUniqueId());
        if (var2 != null) {
            var2.on = !var2.on;
            var1.playSound(var1.getLocation(), Sound.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.6F, var2.on ? 1.4F : 0.9F);
        }
    }

    public boolean isOn(UUID var1) {
        State var2 = (State)this.states.get(var1);
        return var2 != null && var2.on && !var2.battery.empty();
    }

    public double battery(UUID var1) {
        State var2 = (State)this.states.get(var1);
        return var2 == null ? 0.0 : var2.battery.level();
    }

    /** Gasta pila de golpe (LA NOCHE ENTERA: mirar a Slender con la linterna encendida). */
    public void drain(UUID id, double amount) {
        State st = (State)this.states.get(id);
        if (st != null) {
            st.battery.drain(amount);
        }
    }

    public void set(UUID id, double level) {
        State st = (State)this.states.get(id);
        if (st != null) {
            st.battery.set(level);
        }
    }

    public void charge(UUID var1, double var2) {
        State var4 = (State)this.states.get(var1);
        if (var4 != null) {
            var4.battery.charge(var2);
            var4.announcedEmpty = false;
        }
    }

    public void flicker(UUID var1, int var2) {
        State var3 = (State)this.states.get(var1);
        if (var3 != null) {
            var3.flickerTicks = Math.max(var3.flickerTicks, var2);
        }

    }

    public void disable(UUID var1, long var2) {
        State var4 = (State)this.states.get(var1);
        if (var4 != null) {
            var4.disabledUntil = Math.max(var4.disabledUntil, var2);
        }

    }

    public void tick(Collection<Player> var1, Collection<Player> var2, long var3, Random var5, Consumer<Player> var6) {
        Iterator var7 = var1.iterator();

        while(true) {
            Player var8;
            State var9;
            do {
                if (!var7.hasNext()) {
                    return;
                }

                var8 = (Player)var7.next();
                var9 = (State)this.states.get(var8.getUniqueId());
            } while(var9 == null);

            boolean var10 = var9.on && !var9.battery.empty();
            if (var10) {
                var9.battery.drain(0.1);
                if (var9.battery.empty() && !var9.announcedEmpty) {
                    var9.announcedEmpty = true;
                    var6.accept(var8);
                }

                if (var9.battery.low() && var5.nextDouble() < 0.08) {
                    var9.flickerTicks = Math.max(var9.flickerTicks, 2 + var5.nextInt(5));
                }
            }

            boolean var11 = var10 && var9.flickerTicks <= 0 && var3 >= var9.disabledUntil;
            if (var9.flickerTicks > 0) {
                var9.flickerTicks -= 2;
            }

            Set var12 = (Set)this.reserved.get();
            this.send(var9, var11 ? this.lights(var8, var12) : Map.of(), var2, var8.getWorld(), var12);
        }
    }

    private Map<Long, Integer> lights(Player var1, Set<Long> var2) {
        HashMap var3 = new HashMap();
        World var4 = var1.getWorld();
        Location var5 = var1.getEyeLocation();
        Vector var6 = var5.getDirection();
        RayTraceResult var7 = var4.rayTraceBlocks(var5, var6, this.cfg.flashlightRange, FluidCollisionMode.NEVER, true);
        double var8 = var7 == null ? this.cfg.flashlightRange : Math.max(0.5, var7.getHitPosition().distance(var5.toVector()) - 0.6);
        ArrayList var10 = new ArrayList();

        Block var12;
        for(int var11 = 0; var11 <= 4; ++var11) {
            var12 = var5.clone().add(var6.clone().multiply(Math.max(0.5, var8 - (double)var11))).getBlock();
            var10.add(LightDiff.pack(var12.getX(), var12.getY(), var12.getZ()));
        }

        LongPredicate var18 = (var1x) -> {
            return var4.getBlockAt(LightDiff.unpackX(var1x), LightDiff.unpackY(var1x), LightDiff.unpackZ(var1x)).getType().isAir();
        };
        LightDiff.firstFree(var10, var18, var2).ifPresent((var1x) -> {
            var3.put(var1x, 15);
        });
        var12 = var5.clone().add(var6.clone().multiply(var8 / 2.0)).getBlock();
        long var13 = LightDiff.pack(var12.getX(), var12.getY(), var12.getZ());
        if (var12.getType().isAir() && !var2.contains(var13)) {
            var3.putIfAbsent(var13, 11);
        }

        Block var15 = var5.getBlock();
        long var16 = LightDiff.pack(var15.getX(), var15.getY(), var15.getZ());
        if (var15.getType().isAir() && !var2.contains(var16)) {
            var3.putIfAbsent(var16, 8);
        }

        return var3;
    }

    private void send(State var1, Map<Long, Integer> var2, Collection<Player> var3, World var4, Set<Long> var5) {
        LightDiff.Result var6 = LightDiff.diff(var1.lit, var2);
        if (!var6.toRestore().isEmpty() || !var6.toLight().isEmpty()) {
            HashMap var7 = new HashMap();
            Iterator var8 = var6.toRestore().iterator();

            while(var8.hasNext()) {
                long var9 = (Long)var8.next();
                if (!var5.contains(var9)) {
                    int var11 = LightDiff.unpackX(var9);
                    int var12 = LightDiff.unpackY(var9);
                    int var13 = LightDiff.unpackZ(var9);
                    var7.put(Position.block(var11, var12, var13), var4.getBlockAt(var11, var12, var13).getBlockData());
                }
            }

            var8 = var6.toLight().entrySet().iterator();

            while(var8.hasNext()) {
                Map.Entry var14 = (Map.Entry)var8.next();
                long var10 = (Long)var14.getKey();
                var7.put(Position.block(LightDiff.unpackX(var10), LightDiff.unpackY(var10), LightDiff.unpackZ(var10)), this.lightData[(Integer)var14.getValue()]);
            }

            var8 = var3.iterator();

            while(var8.hasNext()) {
                Player var15 = (Player)var8.next();
                if (var15.getWorld() == var4) {
                    var15.sendMultiBlockChange(var7);
                }
            }

            var1.lit = new HashMap(var2);
        }
    }

    public void clear(UUID var1, Collection<Player> var2, World var3) {
        State var4 = (State)this.states.remove(var1);
        if (var4 != null) {
            this.send(var4, Map.of(), var2, var3, (Set)this.reserved.get());
        }

    }

    public void clearAll(Collection<Player> var1, World var2) {
        Iterator var3 = (new ArrayList(this.states.keySet())).iterator();

        while(var3.hasNext()) {
            UUID var4 = (UUID)var3.next();
            this.clear(var4, var1, var2);
        }

    }

    private static final class State {
        final BatteryModel battery;
        boolean on = true;
        boolean announcedEmpty;
        int flickerTicks;
        long disabledUntil;
        Map<Long, Integer> lit = new HashMap();

        State(BatteryModel var1) {
            this.battery = var1;
        }
    }
}
