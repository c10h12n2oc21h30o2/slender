package dev.two.slender.game;

import org.bukkit.Material;

/** Verjas en agua (túneles bajo el lago): se cierran anegadas y al abrirse dejan agua, no una bolsa de aire. */
public final class GateWater {
    private GateWater() {
    }

    /** Qué queda en la celda al abrir la verja. */
    public static Material opened(boolean waterlogged) {
        return waterlogged ? Material.WATER : Material.AIR;
    }

    /**
     * ¿Va anegada la reja al cerrarla? Si en la celda hay una fuente de agua o algo anegado. El agua que entró
     * corriendo durante la partida (nivel &gt; 0) no cuenta: si no, la reja sería una fuente nueva fuera del lago.
     */
    public static boolean wet(Material current, int waterLevel, boolean currentWaterlogged) {
        return (current == Material.WATER && waterLevel == 0) || currentWaterlogged;
    }
}
