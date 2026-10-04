package dev.two.slender.world;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

public final class WorldSetup {
    private WorldSetup() {
    }

    public static World ensure(Plugin var0, String var1) {
        World var2 = Bukkit.getWorld(var1);
        if (var2 == null) {
            var0.getLogger().info("Creando el mundo vacío '" + var1 + "'…");
            var2 = (new WorldCreator(var1)).generator((ChunkGenerator)(new VoidGenerator())).environment(Environment.NORMAL).generateStructures(false).createWorld();
        }

        if (var2 != null) {
            applyRules(var2);
        }

        return var2;
    }

    public static void applyRules(World var0) {
        var0.setGameRule(GameRules.ADVANCE_TIME, false);
        var0.setGameRule(GameRules.ADVANCE_WEATHER, false);
        var0.setGameRule(GameRules.SPAWN_MOBS, false);
        var0.setGameRule(GameRules.SPAWN_MONSTERS, false);
        var0.setGameRule(GameRules.SPAWN_PHANTOMS, false);
        var0.setGameRule(GameRules.SPAWN_PATROLS, false);
        var0.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
        var0.setGameRule(GameRules.SPAWN_WARDENS, false);
        var0.setGameRule(GameRules.FALL_DAMAGE, false);
        var0.setGameRule(GameRules.FIRE_DAMAGE, false);
        var0.setGameRule(GameRules.MOB_GRIEFING, false);
        var0.setGameRule(GameRules.KEEP_INVENTORY, true);
        var0.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
        var0.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
        var0.setGameRule(GameRules.SHOW_DEATH_MESSAGES, false);
        var0.setGameRule(GameRules.LOCATOR_BAR, false);
        var0.setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
        var0.setDifficulty(Difficulty.EASY);
        var0.setTime(18000L);
        var0.setStorm(false);
        var0.setThundering(false);
    }
}
