package dev.two.slender.night;

import org.bukkit.Location;
import org.bukkit.World;

/** Distancias y sitios que no se cruzan de mundo. */
public final class Places {
    private Places() {
    }

    /** ¿a y b en el mismo mundo y a ≤ r? (Location.distance lanza una excepción entre mundos.) */
    public static boolean near(Location a, Location b, double r) {
        return a != null && b != null && a.getWorld() != null && a.getWorld().equals(b.getWorld())
                && a.distanceSquared(b) <= r * r;
    }

    /** Dónde soltar las cabezas de quien sale: su última posición si sigue en el mundo de la fase; si no, el caldero. */
    public static Location dropSpot(Location last, World phaseWorld, Location fallback) {
        return last != null && phaseWorld != null && phaseWorld.equals(last.getWorld()) ? last : fallback;
    }
}
