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

    /** ¿Fuera del mapa? Por debajo de fallY o fuera de las x/z de los límites (los bloques max cuentan como dentro). */
    public static boolean outside(double x, double y, double z, dev.two.slender.map.BlockPos min,
                                  dev.two.slender.map.BlockPos max, double fallY) {
        return y < fallY || x < min.x() || x >= max.x() + 1 || z < min.z() || z >= max.z() + 1;
    }

    /** Dónde soltar las cabezas de quien sale: su última posición si sigue en el mundo de la fase; si no, el caldero. */
    public static Location dropSpot(Location last, World phaseWorld, Location fallback) {
        return last != null && phaseWorld != null && phaseWorld.equals(last.getWorld()) ? last : fallback;
    }
}
