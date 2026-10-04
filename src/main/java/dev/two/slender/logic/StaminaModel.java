package dev.two.slender.logic;

public final class StaminaModel {
    private final double drainPerSec;
    private final double regenPerSec;
    private final double recoverAt;
    private double value = 100.0;
    private boolean exhausted;

    public StaminaModel(double var1, double var3, double var5) {
        this.drainPerSec = var1;
        this.regenPerSec = var3;
        this.recoverAt = var5;
    }

    public void update(double var1, boolean var3) {
        if (var3 && !this.exhausted) {
            this.value -= this.drainPerSec * var1;
            if (this.value <= 0.0) {
                this.value = 0.0;
                this.exhausted = true;
            }
        } else {
            this.value = Math.min(100.0, this.value + this.regenPerSec * var1);
            if (this.exhausted && this.value >= this.recoverAt) {
                this.exhausted = false;
            }
        }

    }

    public boolean canSprint() {
        return !this.exhausted;
    }

    public double value() {
        return this.value;
    }

    public double fraction() {
        return this.value / 100.0;
    }
}
