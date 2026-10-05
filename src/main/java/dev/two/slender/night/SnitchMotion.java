package dev.two.slender.night;

import dev.two.slender.logic.Vec3;
import java.util.Random;

/** Vuelo de la Snitch Dorada: a saltos, huyendo del vivo más cercano, solo sobre suelo alcanzable (spec §8). */
public final class SnitchMotion {
    /** ¿Es (x, y, z) una celda de pies válida (suelo alcanzable de la sección)? */
    public interface Cells {
        boolean ok(int x, int y, int z);
    }

    private final Random random;
    private Vec3 pos;
    private Vec3 dir;
    private double turnIn;

    public SnitchMotion(Vec3 start, Random random) {
        this.pos = start;
        this.random = random;
        this.dir = this.randomDir();
    }

    public Vec3 pos() {
        return this.pos;
    }

    public boolean caught(Vec3 player, double dist) {
        return this.pos.distance(player) <= dist;
    }

    public void step(double dt, Vec3 nearestAlive, double flee, double speed, Cells cells) {
        this.turnIn -= dt;
        if (nearestAlive != null && this.pos.sub(nearestAlive).horizontal().length() <= flee) {
            Vec3 away = this.pos.sub(nearestAlive).horizontal();
            this.dir = away.length() < 1e-6 ? this.randomDir() : away.normalize();
        } else if (this.turnIn <= 0) {
            this.dir = this.randomDir();
            this.turnIn = 0.5 + this.random.nextDouble();
        }
        Vec3 next = this.pos.add(this.dir.mul(speed * dt));
        if (valid(next, cells)) {
            this.pos = next;
            return;
        }
        for (int i = 0; i < 8; i++) {
            Vec3 d = this.randomDir();
            Vec3 alt = this.pos.add(d.mul(speed * dt));
            if (valid(alt, cells)) {
                this.dir = d;
                this.pos = alt;
                return;
            }
        }
    }

    private static boolean valid(Vec3 p, Cells cells) {
        int x = (int) Math.floor(p.x());
        int y = (int) Math.floor(p.y());
        int z = (int) Math.floor(p.z());
        return cells.ok(x, y, z) || cells.ok(x, y - 1, z);
    }

    private Vec3 randomDir() {
        double a = this.random.nextDouble() * Math.PI * 2;
        return new Vec3(Math.cos(a), 0, Math.sin(a));
    }
}
