// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class StaminaModel
{
    private final double drainPerSec;
    private final double regenPerSec;
    private final double recoverAt;
    private double value;
    private boolean exhausted;
    
    public StaminaModel(final double drainPerSec, final double regenPerSec, final double recoverAt) {
        this.value = 100.0;
        this.drainPerSec = drainPerSec;
        this.regenPerSec = regenPerSec;
        this.recoverAt = recoverAt;
    }
    
    public void update(final double n, final boolean b) {
        if (b && !this.exhausted) {
            this.value -= this.drainPerSec * n;
            if (this.value <= 0.0) {
                this.value = 0.0;
                this.exhausted = true;
            }
        }
        else {
            this.value = Math.min(100.0, this.value + this.regenPerSec * n);
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
