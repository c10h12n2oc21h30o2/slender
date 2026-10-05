package dev.two.slender.night;

import java.util.UUID;

/** Ritual del caldero: quedarse cerca unos segundos con una cabeza revive a su dueño (spec §7). */
public final class Ritual {
    public enum Status { IDLE, RUNNING, CANCELLED, DONE }

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
        if (this.running || now < this.coolUntil) {
            return false;
        }
        this.carrier = carrier;
        this.owner = owner;
        this.startedAt = now;
        this.running = true;
        return true;
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
