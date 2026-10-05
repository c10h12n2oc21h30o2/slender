package dev.two.slender.night;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Cuánto lleva cada jugador corriendo seguido; avisa una vez al pasar del umbral (oír correr, spec §6). */
public final class SprintEar {
    private final Map<UUID, Double> secs = new HashMap<>();

    public boolean update(UUID id, boolean sprinting, double dt, double threshold) {
        if (!sprinting) {
            this.secs.remove(id);
            return false;
        }
        double before = this.secs.getOrDefault(id, 0.0);
        double after = before + dt;
        this.secs.put(id, after);
        return before < threshold && after >= threshold;
    }

    public void reset(UUID id) {
        this.secs.remove(id);
    }

    public double seconds(UUID id) {
        return this.secs.getOrDefault(id, 0.0);
    }
}
