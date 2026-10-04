// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import io.papermc.paper.math.Position;
import org.bukkit.block.Block;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.List;
import dev.two.slender.logic.LightDiff;
import java.util.ArrayList;
import org.bukkit.FluidCollisionMode;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.Random;
import java.util.Collection;
import org.bukkit.SoundCategory;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import dev.two.slender.logic.BatteryModel;
import org.bukkit.block.data.Levelled;
import org.bukkit.Material;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.block.data.BlockData;
import java.util.UUID;
import java.util.Map;
import dev.two.slender.config.SlenderConfig;

public final class Flashlights
{
    private final SlenderConfig cfg;
    private final Map<UUID, State> states;
    private final BlockData[] lightData;
    private Supplier<Set<Long>> reserved;
    
    public Flashlights(final SlenderConfig cfg) {
        this.states = new HashMap<UUID, State>();
        this.lightData = new BlockData[16];
        this.reserved = Set::of;
        this.cfg = cfg;
        for (int i = 0; i < 16; ++i) {
            this.lightData[i] = Material.LIGHT.createBlockData(blockData -> ((Levelled)blockData).setLevel(level));
        }
    }
    
    public void reserve(final Supplier<Set<Long>> reserved) {
        this.reserved = reserved;
    }
    
    public void register(final UUID uuid) {
        this.states.put(uuid, new State(new BatteryModel(this.cfg.batteryDrainPerSecond)));
    }
    
    public void toggle(final Player player) {
        final State state = this.states.get(player.getUniqueId());
        if (state == null) {
            return;
        }
        state.on = !state.on;
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.6f, state.on ? 1.4f : 0.9f);
    }
    
    public boolean isOn(final UUID uuid) {
        final State state = this.states.get(uuid);
        return state != null && state.on && !state.battery.empty();
    }
    
    public double battery(final UUID uuid) {
        final State state = this.states.get(uuid);
        return (state == null) ? 0.0 : state.battery.level();
    }
    
    public void charge(final UUID uuid, final double n) {
        final State state = this.states.get(uuid);
        if (state == null) {
            return;
        }
        state.battery.charge(n);
        state.announcedEmpty = false;
    }
    
    public void flicker(final UUID uuid, final int b) {
        final State state = this.states.get(uuid);
        if (state != null) {
            state.flickerTicks = Math.max(state.flickerTicks, b);
        }
    }
    
    public void disable(final UUID uuid, final long b) {
        final State state = this.states.get(uuid);
        if (state != null) {
            state.disabledUntil = Math.max(state.disabledUntil, b);
        }
    }
    
    public void tick(final Collection<Player> collection, final Collection<Player> collection2, final long n, final Random random, final Consumer<Player> consumer) {
        for (final Player player : collection) {
            final State state = this.states.get(player.getUniqueId());
            if (state == null) {
                continue;
            }
            final boolean b = state.on && !state.battery.empty();
            if (b) {
                state.battery.drain(0.1);
                if (state.battery.empty() && !state.announcedEmpty) {
                    state.announcedEmpty = true;
                    consumer.accept(player);
                }
                if (state.battery.low() && random.nextDouble() < 0.08) {
                    state.flickerTicks = Math.max(state.flickerTicks, 2 + random.nextInt(5));
                }
            }
            final boolean b2 = b && state.flickerTicks <= 0 && n >= state.disabledUntil;
            if (state.flickerTicks > 0) {
                final State state2 = state;
                state2.flickerTicks -= 2;
            }
            final Set set = this.reserved.get();
            this.send(state, b2 ? this.lights(player, set) : Map.of(), collection2, player.getWorld(), set);
        }
    }
    
    private Map<Long, Integer> lights(final Player player, final Set<Long> set) {
        final HashMap hashMap = new HashMap();
        final World world = player.getWorld();
        final Location eyeLocation = player.getEyeLocation();
        final Vector direction = eyeLocation.getDirection();
        final RayTraceResult rayTraceBlocks = world.rayTraceBlocks(eyeLocation, direction, this.cfg.flashlightRange, FluidCollisionMode.NEVER, true);
        final double n = (rayTraceBlocks == null) ? this.cfg.flashlightRange : Math.max(0.5, rayTraceBlocks.getHitPosition().distance(eyeLocation.toVector()) - 0.6);
        final ArrayList list = new ArrayList();
        for (int i = 0; i <= 4; ++i) {
            final Block block = eyeLocation.clone().add(direction.clone().multiply(Math.max(0.5, n - i))).getBlock();
            list.add(LightDiff.pack(block.getX(), block.getY(), block.getZ()));
        }
        LightDiff.firstFree(list, n3 -> world2.getBlockAt(LightDiff.unpackX(n3), LightDiff.unpackY(n3), LightDiff.unpackZ(n3)).getType().isAir(), set).ifPresent(l -> map.put(l, 15));
        final Block block2 = eyeLocation.clone().add(direction.clone().multiply(n / 2.0)).getBlock();
        final long pack = LightDiff.pack(block2.getX(), block2.getY(), block2.getZ());
        if (block2.getType().isAir() && !set.contains(pack)) {
            hashMap.putIfAbsent(pack, 11);
        }
        final Block block3 = eyeLocation.getBlock();
        final long pack2 = LightDiff.pack(block3.getX(), block3.getY(), block3.getZ());
        if (block3.getType().isAir() && !set.contains(pack2)) {
            hashMap.putIfAbsent(pack2, 8);
        }
        return hashMap;
    }
    
    private void send(final State state, final Map<Long, Integer> m, final Collection<Player> collection, final World world, final Set<Long> set) {
        final LightDiff.Result diff = LightDiff.diff(state.lit, m);
        if (diff.toRestore().isEmpty() && diff.toLight().isEmpty()) {
            return;
        }
        final HashMap hashMap = new HashMap();
        for (final long longValue : diff.toRestore()) {
            if (set.contains(longValue)) {
                continue;
            }
            final int unpackX = LightDiff.unpackX(longValue);
            final int unpackY = LightDiff.unpackY(longValue);
            final int unpackZ = LightDiff.unpackZ(longValue);
            hashMap.put(Position.block(unpackX, unpackY, unpackZ), world.getBlockAt(unpackX, unpackY, unpackZ).getBlockData());
        }
        for (final Map.Entry entry : diff.toLight().entrySet()) {
            final long longValue2 = (long)entry.getKey();
            hashMap.put(Position.block(LightDiff.unpackX(longValue2), LightDiff.unpackY(longValue2), LightDiff.unpackZ(longValue2)), this.lightData[(int)entry.getValue()]);
        }
        for (final Player player : collection) {
            if (player.getWorld() == world) {
                player.sendMultiBlockChange((Map)hashMap);
            }
        }
        state.lit = new HashMap<Long, Integer>(m);
    }
    
    public void clear(final UUID uuid, final Collection<Player> collection, final World world) {
        final State state = this.states.remove(uuid);
        if (state != null) {
            this.send(state, Map.of(), collection, world, this.reserved.get());
        }
    }
    
    public void clearAll(final Collection<Player> collection, final World world) {
        final Iterator iterator = new ArrayList(this.states.keySet()).iterator();
        while (iterator.hasNext()) {
            this.clear((UUID)iterator.next(), collection, world);
        }
    }
    
    private static final class State
    {
        final BatteryModel battery;
        boolean on;
        boolean announcedEmpty;
        int flickerTicks;
        long disabledUntil;
        Map<Long, Integer> lit;
        
        State(final BatteryModel battery) {
            this.on = true;
            this.lit = new HashMap<Long, Integer>();
            this.battery = battery;
        }
    }
}
