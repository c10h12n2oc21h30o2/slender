package dev.two.slender.game;

import dev.two.slender.config.Msg;
import java.util.Random;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SurvivorEffects {
    private static final String NOISE = "|/\\#@%&$!?";
    private final Msg msg;

    public SurvivorEffects(Msg var1) {
        this.msg = var1;
    }

    public void applyStamina(Player var1, SurvivorState var2, boolean var3, double var4) {
        var2.stamina.update(var4, var3);
        var1.setExp((float)Math.min(0.999, var2.stamina.fraction()));
        if (!var2.stamina.canSprint()) {
            if (var1.getFoodLevel() != 6) {
                var1.setFoodLevel(6);
            }

            if (var1.isSprinting()) {
                var1.setSprinting(false);
            }
        } else if (var1.getFoodLevel() != 20) {
            var1.setFoodLevel(20);
        }

    }

    public void applyStatic(Player var1, SurvivorState var2, long var3) {
        int var5 = var2.stat.level();
        if (var5 >= 1 && var3 % 10L == 0L) {
            var1.playSound(var1.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, SoundCategory.HOSTILE, 0.15F + 0.1F * (float)var5, 1.8F);
        }

        if (var5 >= 2 && var3 % 20L == 0L) {
            var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_STARE, SoundCategory.HOSTILE, 0.6F, 0.7F);
        }

        if (var5 >= 2) {
            var1.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 0, false, false, false));
        }

        if (var5 >= 3 && var3 % 40L == 0L) {
            var1.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 0, false, false, false));
        }

    }

    public Component actionBar(SurvivorState var1, double var2, boolean var4, Random var5) {
        int var6 = var1.stat.level();
        if (var6 < 1) {
            return this.msg.get("actionbar-survivor", "battery", bar(var2, var4 ? "yellow" : "gray"), "stamina", bar(var1.stamina.value(), "aqua"));
        } else {
            StringBuilder var7 = new StringBuilder();

            for(int var8 = 0; var8 < 6 + var6 * 6; ++var8) {
                var7.append("|/\\#@%&$!?".charAt(var5.nextInt("|/\\#@%&$!?".length())));
            }

            return this.msg.get("static-glitch", "noise", var7.toString());
        }
    }

    static String bar(double var0, String var2) {
        int var3 = (int)Math.max(0L, Math.min(10L, Math.round(var0 / 10.0)));
        return "<" + var2 + ">" + "|".repeat(var3) + "</" + var2 + "><dark_gray>" + "|".repeat(10 - var3) + "</dark_gray>";
    }
}
