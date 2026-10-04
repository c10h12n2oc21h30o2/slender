package dev.two.slender.logic;

public final class BatteryModel {
    private final double drainPerSec;
    private double level = 100.0;

    public BatteryModel(double var1) {
        this.drainPerSec = var1;
    }

    public void drain(double var1) {
        this.level = Math.max(0.0, this.level - this.drainPerSec * var1);
    }

    public void charge(double var1) {
        this.level = Math.min(100.0, this.level + var1);
    }

    public void set(double var1) {
        this.level = Math.max(0.0, Math.min(100.0, var1));
    }

    public double level() {
        return this.level;
    }

    public boolean empty() {
        return this.level <= 0.0;
    }

    public boolean low() {
        return this.level < 20.0;
    }
}
