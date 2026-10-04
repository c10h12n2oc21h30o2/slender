package dev.two.slender.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class GateWaterTest {
    /** Al abrir la verja: una reja anegada deja agua, no una bolsa de aire bajo el lago. */
    @Test
    void openingAWetBarLeavesWater() {
        assertEquals(Material.WATER, GateWater.opened(true));
        assertEquals(Material.AIR, GateWater.opened(false));
    }

    /** Al cerrar tras la partida: la reja va anegada si en esa celda hay agua. */
    @Test
    void closingOverWaterWaterlogs() {
        assertTrue(GateWater.wet(Material.WATER, 0, false));
        assertTrue(GateWater.wet(Material.IRON_BARS, 0, true));
        assertFalse(GateWater.wet(Material.AIR, 0, false));
        assertFalse(GateWater.wet(Material.IRON_BARS, 0, false));
        // agua que entró corriendo durante la partida: no se convierte en una fuente nueva
        assertFalse(GateWater.wet(Material.WATER, 3, false));
    }
}
