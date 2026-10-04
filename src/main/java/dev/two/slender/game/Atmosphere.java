package dev.two.slender.game;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Atmosphere {
    public void applyFog(Player var1, int var2, boolean var3) {
        int var4 = var3 ? 2 : Math.max(2, 6 - var2 / 2);
        if (var1.getViewDistance() != var4) {
            var1.setViewDistance(var4);
        }

        if (var1.getSendViewDistance() != var4) {
            var1.setSendViewDistance(var4);
        }

    }

    public void darkness(Player var1) {
        var1.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 0, false, false, false));
    }

    public void ambient(Player var1, int var2, boolean var3, Random var4) {
        if (var4.nextDouble() < 0.1) {
            play(var1, around(var1, var4, 8.0), Sound.AMBIENT_CAVE, 0.6F, 0.8F + var4.nextFloat() * 0.2F);
        }

        if (var2 >= 2 && var4.nextDouble() < 0.125) {
            play(var1, var1.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 0.7F, 0.6F);
        }

        if (var2 >= 4 && var4.nextDouble() < 0.05) {
            play(var1, around(var1, var4, 20.0), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 0.3F, 0.5F);
        }

        if (var2 >= 6 && var4.nextDouble() < 0.16) {
            play(var1, var1.getLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 0.8F, 0.5F);
        }

        if (var2 >= 6 && var4.nextDouble() < 0.07) {
            play(var1, around(var1, var4, 12.0), Sound.ENTITY_WARDEN_AMBIENT, 0.4F, 0.5F);
        }

        if (var3 && var4.nextDouble() < 0.07) {
            play(var1, var1.getLocation(), Sound.EVENT_RAID_HORN, 0.6F, 0.5F);
        }

    }

    public void heartbeat(Player var1, SurvivorState var2, double var3, long var5) {
        if (!(var3 > 15.0) && var5 >= var2.nextHeartbeatTick) {
            play(var1, var1.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0F, 1.0F);
            var2.nextHeartbeatTick = var5 + Math.round(8.0 + var3 / 15.0 * 32.0);
        }
    }

    public void escapeHorn(Collection<Player> var1) {
        Iterator var2 = var1.iterator();

        while(var2.hasNext()) {
            Player var3 = (Player)var2.next();
            play(var3, var3.getLocation(), Sound.EVENT_RAID_HORN, 1.0F, 0.5F);
        }

    }

    static Location around(Player var0, Random var1, double var2) {
        double var4 = var1.nextDouble() * Math.PI * 2.0;
        return var0.getLocation().add(Math.cos(var4) * var2, 1.0, Math.sin(var4) * var2);
    }

    static void play(Player var0, Location var1, Sound var2, float var3, float var4) {
        var0.playSound(var1, var2, SoundCategory.AMBIENT, var3, var4);
    }
}
