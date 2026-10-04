package dev.two.slender.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dónde puede aparecer Slender en cada mapa. Por defecto, en cualquier sitio con suelo (lo que ya mira el Director);
 * un mapa puede registrar una regla más estricta (Hogwarts: solo suelo al que llegan los jugadores).
 */
public final class SpotRules {
    public interface Rule {
        boolean allows(int x, int y, int z);
    }

    private static final Map<String, Rule> RULES = new ConcurrentHashMap<>();

    private SpotRules() {
    }

    public static void register(String mapId, Rule rule) {
        RULES.put(mapId, rule);
    }

    public static boolean allows(String mapId, int x, int y, int z) {
        Rule r = RULES.get(mapId);
        return r == null || r.allows(x, y, z);
    }
}
