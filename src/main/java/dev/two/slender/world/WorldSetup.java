// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.world;

import org.bukkit.Difficulty;
import org.bukkit.GameRules;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.WorldCreator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class WorldSetup
{
    private WorldSetup() {
    }
    
    public static World ensure(final Plugin plugin, final String s) {
        World world = Bukkit.getWorld(s);
        if (world == null) {
            plugin.getLogger().info("Creando el mundo vac\u00edo '" + s + "'\u2026");
            world = new WorldCreator(s).generator((ChunkGenerator)new VoidGenerator()).environment(World.Environment.NORMAL).generateStructures(false).createWorld();
        }
        if (world != null) {
            applyRules(world);
        }
        return world;
    }
    
    public static void applyRules(final World world) {
        world.setGameRule(GameRules.ADVANCE_TIME, (Object)false);
        world.setGameRule(GameRules.ADVANCE_WEATHER, (Object)false);
        world.setGameRule(GameRules.SPAWN_MOBS, (Object)false);
        world.setGameRule(GameRules.SPAWN_MONSTERS, (Object)false);
        world.setGameRule(GameRules.SPAWN_PHANTOMS, (Object)false);
        world.setGameRule(GameRules.SPAWN_PATROLS, (Object)false);
        world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, (Object)false);
        world.setGameRule(GameRules.SPAWN_WARDENS, (Object)false);
        world.setGameRule(GameRules.FALL_DAMAGE, (Object)false);
        world.setGameRule(GameRules.FIRE_DAMAGE, (Object)false);
        world.setGameRule(GameRules.MOB_GRIEFING, (Object)false);
        world.setGameRule(GameRules.KEEP_INVENTORY, (Object)true);
        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, (Object)true);
        world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, (Object)false);
        world.setGameRule(GameRules.SHOW_DEATH_MESSAGES, (Object)false);
        world.setGameRule(GameRules.LOCATOR_BAR, (Object)false);
        world.setGameRule(GameRules.RANDOM_TICK_SPEED, (Object)0);
        world.setDifficulty(Difficulty.EASY);
        world.setTime(18000L);
        world.setStorm(false);
        world.setThundering(false);
    }
}
