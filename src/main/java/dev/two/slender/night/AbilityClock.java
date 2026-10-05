package dev.two.slender.night;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Enfriamientos por habilidad y jugador (who = null: el de la habilidad) y bloqueo global de suplantar/perseguir. */
public final class AbilityClock {
    private final Map<String, Double> readyAt = new HashMap<>();
    private Ability locked;

    private static String key(Ability a, UUID who) {
        return a.name() + "/" + who;
    }

    public boolean ready(Ability a, UUID who, double now) {
        return now >= this.readyAt.getOrDefault(key(a, who), Double.NEGATIVE_INFINITY);
    }

    public void used(Ability a, UUID who, double now, double cooldown) {
        this.readyAt.put(key(a, who), now + cooldown);
    }

    public static boolean roll(double chancePerSecond, double frequency, double dt, double r) {
        return r < chancePerSecond * frequency * dt;
    }

    public boolean lock(Ability a) {
        if ((a != Ability.MIMIC && a != Ability.CHASE) || this.locked != null) {
            return false;
        }
        this.locked = a;
        return true;
    }

    public void unlock(Ability a) {
        if (this.locked == a) {
            this.locked = null;
        }
    }

    public Ability locked() {
        return this.locked;
    }
}
