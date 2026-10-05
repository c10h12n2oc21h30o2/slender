package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

/** Salir del mundo a mitad de fase (revisión final, hallazgo 5): nada de distancias entre mundos. */
class PlacesTest {
    private static World world(String name) {
        return (World) Proxy.newProxyInstance(World.class.getClassLoader(), new Class<?>[]{World.class}, (p, m, a) -> switch (m.getName()) {
            case "getName", "toString" -> name;
            case "hashCode" -> name.hashCode();
            case "equals" -> p == a[0];
            default -> null;
        });
    }

    private final World slender = world("slender");
    private final World overworld = world("world");

    @Test
    void nearNeedsTheSameWorld() {
        assertTrue(Places.near(new Location(this.slender, 0, 0, 0), new Location(this.slender, 2, 0, 0), 3));
        assertFalse(Places.near(new Location(this.slender, 0, 0, 0), new Location(this.slender, 4, 0, 0), 3));
        assertFalse(Places.near(new Location(this.slender, 0, 0, 0), new Location(this.overworld, 0, 0, 0), 3));
        assertFalse(Places.near(null, new Location(this.slender, 0, 0, 0), 3));
    }

    @Test
    void dropSpotStaysInThePhaseWorld() {
        Location last = new Location(this.slender, 5, 70, 5);
        Location altar = new Location(this.slender, 0, 64, 0);
        assertSame(last, Places.dropSpot(last, this.slender, altar));
        assertSame(altar, Places.dropSpot(new Location(this.overworld, 5, 70, 5), this.slender, altar));
        assertSame(altar, Places.dropSpot(null, this.slender, altar));
    }

    /** Pendiente 9: una cabeza fuera del mapa (por abajo o por un lado) vuelve al caldero. */
    @Test
    void outsideTheMap() {
        dev.two.slender.map.BlockPos min = new dev.two.slender.map.BlockPos(0, 40, 0);
        dev.two.slender.map.BlockPos max = new dev.two.slender.map.BlockPos(100, 120, 100);
        assertFalse(Places.outside(50, 60, 50, min, max, 30));
        assertTrue(Places.outside(50, 29, 50, min, max, 30));
        assertTrue(Places.outside(-3, 60, 50, min, max, 30));
        assertTrue(Places.outside(50, 60, 101.5, min, max, 30));
        assertFalse(Places.outside(100.9, 60, 0, min, max, 30));
    }
}
