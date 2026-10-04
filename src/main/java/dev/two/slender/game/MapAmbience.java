package dev.two.slender.game;

import dev.two.slender.map.Ambience;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.SoundZone;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class MapAmbience {
    private final Plugin plugin;
    private final World world;
    private final Ambience amb;
    private final Map<String, Sound> sounds = new HashMap();
    private final Map<BlockPos, Boolean> lit = new HashMap();

    MapAmbience(Plugin var1, World var2, Ambience var3) {
        this.plugin = var1;
        this.world = var2;
        this.amb = var3;
    }

    void second(List<Player> var1, Random var2) {
        if (!this.amb.sounds().isEmpty()) {
            Iterator var3 = var1.iterator();

            while(var3.hasNext()) {
                Player var4 = (Player)var3.next();
                Location var5 = var4.getLocation();
                List var10000 = this.amb.sounds();
                double var10001 = var5.getX();
                double var10002 = var5.getZ();
                Objects.requireNonNull(var2);
                Iterator var6 = SoundZone.due(var10000, var10001, var10002, var2::nextDouble).iterator();

                while(var6.hasNext()) {
                    SoundZone var7 = (SoundZone)var6.next();
                    Sound var8 = this.sound(var7.sound());
                    if (var8 != null) {
                        Location var9 = var7.radius() <= 10 ? new Location(this.world, (double)var7.center().x() + 0.5, (double)var7.center().y() + 0.5, (double)var7.center().z() + 0.5) : Atmosphere.around(var4, var2, 4.0 + var2.nextDouble() * 6.0);
                        var4.playSound(var9, var8, SoundCategory.AMBIENT, var7.volume(), var7.pitch());
                    }
                }
            }

        }
    }

    void lights(long var1, Random var3) {
        Iterator var4 = this.amb.lights().iterator();

        while(var4.hasNext()) {
            MapLight var5 = (MapLight)var4.next();
            boolean var6 = var5.litAt(var1, var3.nextDouble());
            if (!Boolean.valueOf(var6).equals(this.lit.get(var5.pos())) && this.apply(var5.pos(), var6)) {
                this.lit.put(var5.pos(), var6);
            }
        }

    }

    void reset() {
        Iterator var1 = this.lit.keySet().iterator();

        while(var1.hasNext()) {
            BlockPos var2 = (BlockPos)var1.next();
            this.apply(var2, true);
        }

        this.lit.clear();
    }

    private boolean apply(BlockPos var1, boolean var2) {
        Block var3 = this.world.getBlockAt(var1.x(), var1.y(), var1.z());
        if (var3.getType() != Material.REDSTONE_LAMP) {
            return false;
        } else {
            Lightable var4 = (Lightable)var3.getBlockData();
            if (var4.isLit() != var2) {
                var4.setLit(var2);
                var3.setBlockData(var4, false);
            }

            return true;
        }
    }

    private Sound sound(String var1) {
        if (this.sounds.containsKey(var1)) {
            return (Sound)this.sounds.get(var1);
        } else {
            Sound var2 = null;

            try {
                Object var4 = Sound.class.getField(var1).get((Object)null);
                if (var4 instanceof Sound) {
                    Sound var3 = (Sound)var4;
                    var2 = var3;
                }
            } catch (ReflectiveOperationException var5) {
                this.plugin.getLogger().warning("Sonido desconocido en el mapa: " + var1);
            }

            this.sounds.put(var1, var2);
            return var2;
        }
    }
}
