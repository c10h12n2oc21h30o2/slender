package dev.two.slender.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.BlockPos;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class LightSuppressionTest {
    /** Las velas de Hogwarts tienen que parpadear igual que las lámparas del instituto. */
    @Test
    void togglesCandlesLampsAndCampfires() {
        assertTrue(LightSuppression.toggles(Material.CANDLE));
        assertTrue(LightSuppression.toggles(Material.RED_CANDLE));
        assertTrue(LightSuppression.toggles(Material.REDSTONE_LAMP));
        assertTrue(LightSuppression.toggles(Material.SOUL_CAMPFIRE));
        assertTrue(LightSuppression.toggles(Material.COPPER_BULB));
        assertFalse(LightSuppression.toggles(Material.STONE));
        assertFalse(LightSuppression.toggles(Material.CANDLE_CAKE));
    }

    @Test
    void suppressedUntilTick() {
        LightSuppression s = new LightSuppression();
        BlockPos p = new BlockPos(1, 2, 3);
        s.suppress(p, 100);
        assertTrue(s.suppressed(p, 99));
        assertFalse(s.suppressed(p, 100));
        assertFalse(s.suppressed(new BlockPos(1, 2, 4), 50));
        s.clear();
        assertFalse(s.suppressed(p, 99));
    }
}
