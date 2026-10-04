package dev.two.slender.logic;

public final class StaticModel {
    private final double lookRate;
    private final double proximityRate;
    private final double proximityRadius;
    private final double decayRate;
    private double value;

    public StaticModel(double var1, double var3, double var5, double var7) {
        this.lookRate = var1;
        this.proximityRate = var3;
        this.proximityRadius = var5;
        this.decayRate = var7;
    }

    public static double distanceFactor(double var0) {
        return Math.max(0.4, Math.min(1.8, 1.8 - var0 / 40.0));
    }

    public void update(double var1, boolean var3, double var4) {
        double var6 = 0.0;
        if (var3) {
            var6 += this.lookRate * distanceFactor(var4);
        }

        if (var4 < this.proximityRadius) {
            var6 += this.proximityRate * (1.0 - var4 / this.proximityRadius);
        }

        this.value = clamp(var6 > 0.0 ? this.value + var6 * var1 : this.value - this.decayRate * var1);
    }

    public void bump(double var1) {
        this.value = clamp(this.value + var1);
    }

    public void reset() {
        this.value = 0.0;
    }

    public double value() {
        return this.value;
    }

    public boolean lethal() {
        return this.value >= 100.0;
    }

    public int level() {
        if (this.value >= 100.0) {
            return 4;
        } else if (this.value >= 70.0) {
            return 3;
        } else if (this.value >= 45.0) {
            return 2;
        } else {
            return this.value >= 20.0 ? 1 : 0;
        }
    }

    private static double clamp(double var0) {
        return Math.max(0.0, Math.min(100.0, var0));
    }
}
