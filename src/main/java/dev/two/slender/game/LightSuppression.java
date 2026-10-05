package dev.two.slender.game;

import dev.two.slender.map.BlockPos;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;

/** Luces del mapa apagadas a la fuerza hasta un tick (Slender las apaga en LA NOCHE ENTERA). */
public final class LightSuppression {
    private final Map<BlockPos, Long> until = new HashMap<>();

    /** Bloques que el ambiente enciende y apaga: lámparas de redstone, velas, hogueras y bombillas de cobre. */
    public static boolean toggles(Material m) {
        String n = m.name();
        return m == Material.REDSTONE_LAMP || n.endsWith("CANDLE") || m == Material.CAMPFIRE || m == Material.SOUL_CAMPFIRE
                || n.endsWith("COPPER_BULB");
    }

    public void suppress(BlockPos pos, long untilTick) {
        this.until.merge(pos, untilTick, Math::max);
    }

    public boolean suppressed(BlockPos pos, long tick) {
        Long t = this.until.get(pos);
        return t != null && tick < t;
    }

    public void clear() {
        this.until.clear();
    }
}
