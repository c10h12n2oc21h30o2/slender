// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.Material;
import dev.two.slender.map.MapLight;
import java.util.Iterator;
import org.bukkit.SoundCategory;
import org.bukkit.Location;
import dev.two.slender.map.SoundZone;
import java.util.Objects;
import java.util.Random;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.HashMap;
import dev.two.slender.map.BlockPos;
import org.bukkit.Sound;
import java.util.Map;
import dev.two.slender.map.Ambience;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

final class MapAmbience
{
    private final Plugin plugin;
    private final World world;
    private final Ambience amb;
    private final Map<String, Sound> sounds;
    private final Map<BlockPos, Boolean> lit;
    
    MapAmbience(final Plugin plugin, final World world, final Ambience amb) {
        this.sounds = new HashMap<String, Sound>();
        this.lit = new HashMap<BlockPos, Boolean>();
        this.plugin = plugin;
        this.world = world;
        this.amb = amb;
    }
    
    void second(final List<Player> list, final Random obj) {
        if (this.amb.sounds().isEmpty()) {
            return;
        }
        for (final Player player : list) {
            final Location location = player.getLocation();
            final List<SoundZone> sounds = this.amb.sounds();
            final double x = location.getX();
            final double z = location.getZ();
            Objects.requireNonNull(obj);
            for (final SoundZone soundZone : SoundZone.due(sounds, x, z, obj::nextDouble)) {
                final Sound sound = this.sound(soundZone.sound());
                if (sound == null) {
                    continue;
                }
                player.playSound((soundZone.radius() <= 10) ? new Location(this.world, soundZone.center().x() + 0.5, soundZone.center().y() + 0.5, soundZone.center().z() + 0.5) : Atmosphere.around(player, obj, 4.0 + obj.nextDouble() * 6.0), sound, SoundCategory.AMBIENT, soundZone.volume(), soundZone.pitch());
            }
        }
    }
    
    void lights(final long n, final Random random) {
        for (final MapLight mapLight : this.amb.lights()) {
            final boolean lit = mapLight.litAt(n, random.nextDouble());
            if (!Boolean.valueOf(lit).equals(this.lit.get(mapLight.pos())) && this.apply(mapLight.pos(), lit)) {
                this.lit.put(mapLight.pos(), lit);
            }
        }
    }
    
    void reset() {
        final Iterator<BlockPos> iterator = this.lit.keySet().iterator();
        while (iterator.hasNext()) {
            this.apply(iterator.next(), true);
        }
        this.lit.clear();
    }
    
    private boolean apply(final BlockPos blockPos, final boolean lit) {
        final Block block = this.world.getBlockAt(blockPos.x(), blockPos.y(), blockPos.z());
        if (block.getType() != Material.REDSTONE_LAMP) {
            return false;
        }
        final Lightable lightable = (Lightable)block.getBlockData();
        if (lightable.isLit() != lit) {
            lightable.setLit(lit);
            block.setBlockData((BlockData)lightable, false);
        }
        return true;
    }
    
    private Sound sound(final String name) {
        if (this.sounds.containsKey(name)) {
            return this.sounds.get(name);
        }
        Sound sound = null;
        try {
            final Object value = Sound.class.getField(name).get(null);
            if (value instanceof final Sound sound2) {
                sound = sound2;
            }
        }
        catch (final ReflectiveOperationException ex) {
            this.plugin.getLogger().warning("Sonido desconocido en el mapa: " + name);
        }
        this.sounds.put(name, sound);
        return sound;
    }
}
