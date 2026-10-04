// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.SoundCategory;
import org.bukkit.Location;
import java.util.Iterator;
import java.util.Collection;
import org.bukkit.Sound;
import java.util.Random;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;

public final class Atmosphere
{
    public void applyFog(final Player player, final int n, final boolean b) {
        final int n2 = b ? 2 : Math.max(2, 6 - n / 2);
        if (player.getViewDistance() != n2) {
            player.setViewDistance(n2);
        }
        if (player.getSendViewDistance() != n2) {
            player.setSendViewDistance(n2);
        }
    }
    
    public void darkness(final Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 0, false, false, false));
    }
    
    public void ambient(final Player player, final int n, final boolean b, final Random random) {
        if (random.nextDouble() < 0.1) {
            play(player, around(player, random, 8.0), Sound.AMBIENT_CAVE, 0.6f, 0.8f + random.nextFloat() * 0.2f);
        }
        if (n >= 2 && random.nextDouble() < 0.125) {
            play(player, player.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 0.7f, 0.6f);
        }
        if (n >= 4 && random.nextDouble() < 0.05) {
            play(player, around(player, random, 20.0), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 0.3f, 0.5f);
        }
        if (n >= 6 && random.nextDouble() < 0.16) {
            play(player, player.getLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 0.8f, 0.5f);
        }
        if (n >= 6 && random.nextDouble() < 0.07) {
            play(player, around(player, random, 12.0), Sound.ENTITY_WARDEN_AMBIENT, 0.4f, 0.5f);
        }
        if (b && random.nextDouble() < 0.07) {
            play(player, player.getLocation(), Sound.EVENT_RAID_HORN, 0.6f, 0.5f);
        }
    }
    
    public void heartbeat(final Player player, final SurvivorState survivorState, final double n, final long n2) {
        if (n > 15.0 || n2 < survivorState.nextHeartbeatTick) {
            return;
        }
        play(player, player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);
        survivorState.nextHeartbeatTick = n2 + Math.round(8.0 + n / 15.0 * 32.0);
    }
    
    public void escapeHorn(final Collection<Player> collection) {
        for (final Player player : collection) {
            play(player, player.getLocation(), Sound.EVENT_RAID_HORN, 1.0f, 0.5f);
        }
    }
    
    static Location around(final Player player, final Random random, final double n) {
        final double n2 = random.nextDouble() * 3.141592653589793 * 2.0;
        return player.getLocation().add(Math.cos(n2) * n, 1.0, Math.sin(n2) * n);
    }
    
    static void play(final Player player, final Location location, final Sound sound, final float n, final float n2) {
        player.playSound(location, sound, SoundCategory.AMBIENT, n, n2);
    }
}
