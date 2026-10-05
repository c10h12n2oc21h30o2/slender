package dev.two.slender.night;

import java.util.UUID;

/** Ritual del caldero: quedarse cerca unos segundos con una cabeza revive a su dueño (spec §7). */
public final class Ritual {
    public enum Status { IDLE, RUNNING, CANCELLED, DONE }

    /** Por qué empieza o no un ritual. */
    public enum Start { STARTED, BUSY, COOLDOWN, OWNER_GONE }

    private final double seconds;
    private final double radius;
    private final double cooldown;
    private UUID carrier;
    private UUID owner;
    private double startedAt;
    private double coolUntil = Double.NEGATIVE_INFINITY;
    private boolean running;

    public Ritual(double seconds, double radius, double cooldown) {
        this.seconds = seconds;
        this.radius = radius;
        this.cooldown = cooldown;
    }

    public boolean start(UUID carrier, UUID owner, double now) {
        return this.tryStart(carrier, owner, now, true) == Start.STARTED;
    }

    /** {@code ownerCanReturn}: el dueño de la cabeza sigue en la fase (si se ha ido, revivirlo no sirve de nada). */
    public Start tryStart(UUID carrier, UUID owner, double now, boolean ownerCanReturn) {
        if (this.running) {
            return Start.BUSY;
        }
        if (now < this.coolUntil) {
            return Start.COOLDOWN;
        }
        if (!ownerCanReturn) {
            return Start.OWNER_GONE;
        }
        this.carrier = carrier;
        this.owner = owner;
        this.startedAt = now;
        this.running = true;
        return Start.STARTED;
    }

    /** Segundos que le quedan al caldero para poder usarse otra vez. */
    public double cooldownLeft(double now) {
        return Math.max(0, this.coolUntil - now);
    }

    public Status tick(double now, double carrierDistance, boolean carrierAlive) {
        if (!this.running) {
            return Status.IDLE;
        }
        if (!carrierAlive || carrierDistance > this.radius) {
            this.running = false;
            return Status.CANCELLED;
        }
        if (now - this.startedAt >= this.seconds) {
            this.running = false;
            this.coolUntil = now + this.cooldown;
            return Status.DONE;
        }
        return Status.RUNNING;
    }

    public boolean running() {
        return this.running;
    }

    public double progress(double now) {
        return this.running ? Math.min(1.0, (now - this.startedAt) / this.seconds) : 0.0;
    }

    public UUID owner() {
        return this.owner;
    }

    public UUID carrier() {
        return this.carrier;
    }
}
