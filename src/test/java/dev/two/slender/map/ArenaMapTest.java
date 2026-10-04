package dev.two.slender.map;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ArenaMapTest {
    static ArenaMap sample() {
        return new ArenaMap("bosque", "Bosque", "slender",
            new BlockPos(0, 64, 0), new BlockPos(-80, 60, -80), new BlockPos(80, 120, 80),
            new Loc(0.5, 105, 0.5, 180f),
            List.of(new Loc(1.5, 65, 70.5, 180f), new Loc(-2.5, 65, 70.5, 180f)),
            List.of(new PageSpot(new BlockPos(-50, 66, -43), "SOUTH", "Camión cisterna"),
                    new PageSpot(new BlockPos(45, 66, -45), "NORTH", "Silo")),
            List.of(new BlockPos(3, 65, 3)),
            List.of(new BlockPos(0, 65, 80), new BlockPos(0, 66, 80)), "minecraft:iron_bars",
            new BlockPos(-3, 64, 82), new BlockPos(3, 68, 88), 12345L);
    }

    @Test
    void roundTripThroughPlainMap() {
        ArenaMap a = sample();
        Map<String, Object> m = a.toMap();
        for (Object v : m.values()) assertTrue(v instanceof String || v instanceof List || v instanceof Long, "tipo YAML-friendly: " + v);
        assertEquals(a, ArenaMap.fromMap(m));
    }

    @Test
    void labelsWithSpacesAndAccentsSurvive() {
        PageSpot s = new PageSpot(new BlockPos(1, 2, 3), "EAST", "Árbol muerto | grande");
        assertEquals(s, PageSpot.parse(s.serialize()));
    }

    @Test
    void boundsAndExit() {
        ArenaMap a = sample();
        assertTrue(a.inBounds(0, 0));
        assertTrue(a.inBounds(80.9, -80.5));
        assertFalse(a.inBounds(90, 0));
        assertTrue(a.inExit(0.5, 65, 85.2));
        assertFalse(a.inExit(0.5, 65, 79));
    }

    @Test
    void parseRejectsGarbage() {
        assertThrows(IllegalArgumentException.class, () -> BlockPos.parse("1,2"));
        assertThrows(IllegalArgumentException.class, () -> Loc.parse("a,b,c,d"));
    }
}
