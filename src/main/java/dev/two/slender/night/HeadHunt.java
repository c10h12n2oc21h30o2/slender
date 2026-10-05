package dev.two.slender.night;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Cazar cabezas (spec §6), por cabeza en el suelo: si nadie la vigila, cuenta el tiempo expuesta; pasado ese tiempo pide
 * a Slender que aparezca a su lado y, si se queda junto a ella, se la lleva en {@code grabSeconds}. Puro.
 */
public final class HeadHunt {
    public enum Action { NONE, LURE, STEAL }

    private final double exposeSeconds;
    private final double grabSeconds;
    private final double grabRadius;
    private final Map<UUID, Double> exposed = new HashMap<>();
    private final Map<UUID, Double> grabbing = new HashMap<>();

    public HeadHunt(double exposeSeconds, double grabSeconds, double grabRadius) {
        this.exposeSeconds = exposeSeconds;
        this.grabSeconds = grabSeconds;
        this.grabRadius = grabRadius;
    }

    /** Un paso de {@code dt} s. {@code bodyDistance}: distancia de Slender a la cabeza (infinito si no está). */
    public Action step(UUID owner, boolean guarded, double bodyDistance, double dt) {
        if (guarded) {
            this.forget(owner);
            return Action.NONE;
        }
        double secs = this.exposed.merge(owner, dt, Double::sum);
        if (secs < this.exposeSeconds) {
            this.grabbing.remove(owner);
            return Action.NONE;
        }
        if (bodyDistance > this.grabRadius) {
            this.grabbing.remove(owner);
            return Action.LURE;
        }
        if (this.grabbing.merge(owner, dt, Double::sum) >= this.grabSeconds) {
            this.forget(owner);
            return Action.STEAL;
        }
        return Action.NONE;
    }

    public void forget(UUID owner) {
        this.exposed.remove(owner);
        this.grabbing.remove(owner);
    }

    public void clear() {
        this.exposed.clear();
        this.grabbing.clear();
    }
}
