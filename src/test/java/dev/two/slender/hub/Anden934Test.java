package dev.two.slender.hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Zone;
import dev.two.slender.map.builders.hogwarts.HogwartsMaps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Anden934Test {
    private static boolean overlap(Zone a, Zone b) {
        return a.min().x() <= b.max().x() && b.min().x() <= a.max().x()
                && a.min().y() <= b.max().y() && b.min().y() <= a.max().y()
                && a.min().z() <= b.max().z() && b.min().z() <= a.max().z();
    }

    @Test
    void sevenDoorsInYard() {
        Map<String, Zone> doors = Anden934.doors();
        assertEquals(HogwartsMaps.IDS, new ArrayList<>(doors.keySet()));
        Zone arches = Zone.of(new BlockPos(-26, -100, 17), new BlockPos(26, 100, 19));
        List<Zone> zones = new ArrayList<>(doors.values());
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            assertTrue(z.min().x() >= 0 && z.max().x() <= 34, "x fuera del andén: " + z);
            assertTrue(z.min().z() >= -24 && z.max().z() <= -4, "z fuera del andén: " + z);
            assertEquals(3, z.max().x() - z.min().x() + 1);
            assertEquals(3, z.max().y() - z.min().y() + 1);
            assertEquals(1, z.max().z() - z.min().z() + 1);
            assertFalse(overlap(z, arches), "pisa los arcos: " + z);
            for (int j = i + 1; j < zones.size(); j++) {
                assertFalse(overlap(z, zones.get(j)), "puertas solapadas: " + z + " / " + zones.get(j));
            }
        }
    }

    @Test
    void wallGapCells() {
        List<BlockPos> gap = Anden934.gapCells();
        assertEquals(9, gap.size());
        assertEquals(9, gap.stream().distinct().count());
        assertTrue(gap.stream().allMatch(p -> p.z() == -6));
    }

    @Test
    void doorSignWrapsLongNames() {
        assertEquals(List.of("- 4 -", "Patio de", "Transforma-", "ciones"), Anden934.doorSign(4, "Patio de Transformaciones"));
        assertEquals(List.of("- 2 -", "Gran Comedor", "", ""), Anden934.doorSign(2, "Gran Comedor"));
        for (String l : Anden934.doorSign(7, "Terrenos")) {
            assertTrue(l.length() <= 15);
        }
    }

    /** El andén 1 está a y = 1 (se pisa a y = 2): el hueco del muro y el andén 9¾ van a esa misma altura, sin bajar. */
    @Test
    void levelWithPlatformOne() {
        assertEquals(2, Anden934.gapCells().stream().mapToInt(BlockPos::y).min().orElseThrow());
        assertEquals(4, Anden934.gapCells().stream().mapToInt(BlockPos::y).max().orElseThrow());
        assertEquals(Anden934.FLOOR_Y + 1, 2);
        for (Zone z : Anden934.doors().values()) {
            // la puerta del vagón está un escalón por encima del andén (se pisa a y = 3)
            assertEquals(3, z.min().y());
        }
    }
}
