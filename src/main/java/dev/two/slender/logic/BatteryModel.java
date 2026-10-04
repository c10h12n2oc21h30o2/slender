// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class BatteryModel
{
    private final double drainPerSec;
    private double level;
    
    public BatteryModel(final double drainPerSec) {
        this.level = 100.0;
        this.drainPerSec = drainPerSec;
    }
    
    public void drain(final double n) {
        this.level = Math.max(0.0, this.level - this.drainPerSec * n);
    }
    
    public void charge(final double n) {
        this.level = Math.min(100.0, this.level + n);
    }
    
    public void set(final double b) {
        this.level = Math.max(0.0, Math.min(100.0, b));
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
