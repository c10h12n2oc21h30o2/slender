package dev.two.slender.map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

/** Los mapas de antes no se pueden mover: mismo origen y misma caja de limpieza que en la 1.7. */
class MapBuilderDefaultsTest {
    static MapBuilder old(int radius) {
        return new MapBuilder() {
            public String id() { return "viejo"; }
            public String displayName() { return "Viejo"; }
            public Material icon() { return null; }
            public int radius() { return radius; }
            public ArenaMap build(Builder b, String world, long seed) { return null; }
        };
    }

    @Test
    void oldOriginsUnchanged() {
        MapBuilder m = old(232);
        assertEquals(new BlockPos(6000, 64, 0), m.origin(3));
        assertEquals(new BlockPos(0, 64, 0), m.origin(0));
        assertEquals(Zone.of(new BlockPos(-232, -12, -232), new BlockPos(232, 60, 232)), m.clearBox());
    }
}
