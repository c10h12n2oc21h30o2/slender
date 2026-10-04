// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import net.kyori.adventure.text.Component;
import java.util.Random;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.SoundCategory;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import dev.two.slender.config.Msg;

public final class SurvivorEffects
{
    private static final String NOISE = "|/\\#@%&$!?";
    private final Msg msg;
    
    public SurvivorEffects(final Msg msg) {
        this.msg = msg;
    }
    
    public void applyStamina(final Player player, final SurvivorState survivorState, final boolean b, final double n) {
        survivorState.stamina.update(n, b);
        player.setExp((float)Math.min(0.999, survivorState.stamina.fraction()));
        if (!survivorState.stamina.canSprint()) {
            if (player.getFoodLevel() != 6) {
                player.setFoodLevel(6);
            }
            if (player.isSprinting()) {
                player.setSprinting(false);
            }
        }
        else if (player.getFoodLevel() != 20) {
            player.setFoodLevel(20);
        }
    }
    
    public void applyStatic(final Player player, final SurvivorState survivorState, final long n) {
        final int level = survivorState.stat.level();
        if (level >= 1 && n % 10L == 0L) {
            player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, SoundCategory.HOSTILE, 0.15f + 0.1f * level, 1.8f);
        }
        if (level >= 2 && n % 20L == 0L) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, SoundCategory.HOSTILE, 0.6f, 0.7f);
        }
        if (level >= 2) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 0, false, false, false));
        }
        if (level >= 3 && n % 40L == 0L) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, false, false, false));
        }
    }
    
    public Component actionBar(final SurvivorState survivorState, final double n, final boolean b, final Random random) {
        final int level = survivorState.stat.level();
        if (level >= 1) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6 + level * 6; ++i) {
                sb.append("|/\\#@%&$!?".charAt(random.nextInt("|/\\#@%&$!?".length())));
            }
            return this.msg.get("static-glitch", "noise", sb.toString());
        }
        return this.msg.get("actionbar-survivor", "battery", bar(n, b ? "yellow" : "gray"), "stamina", bar(survivorState.stamina.value(), "aqua"));
    }
    
    static String bar(final double n, final String s) {
        final int count = (int)Math.max(0L, Math.min(10L, Math.round(n / 10.0)));
        return "<" + s + ">" + "|".repeat(count) + "</" + s + "><dark_gray>" + "|".repeat(10 - count) + "</dark_gray>";
    }
}
