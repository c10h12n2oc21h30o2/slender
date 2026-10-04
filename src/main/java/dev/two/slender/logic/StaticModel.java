// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class StaticModel
{
    private final double lookRate;
    private final double proximityRate;
    private final double proximityRadius;
    private final double decayRate;
    private double value;
    
    public StaticModel(final double lookRate, final double proximityRate, final double proximityRadius, final double decayRate) {
        this.lookRate = lookRate;
        this.proximityRate = proximityRate;
        this.proximityRadius = proximityRadius;
        this.decayRate = decayRate;
    }
    
    public static double distanceFactor(final double n) {
        return Math.max(0.4, Math.min(1.8, 1.8 - n / 40.0));
    }
    
    public void update(final double n, final boolean b, final double n2) {
        double n3 = 0.0;
        if (b) {
            n3 += this.lookRate * distanceFactor(n2);
        }
        if (n2 < this.proximityRadius) {
            n3 += this.proximityRate * (1.0 - n2 / this.proximityRadius);
        }
        this.value = clamp((n3 > 0.0) ? (this.value + n3 * n) : (this.value - this.decayRate * n));
    }
    
    public void bump(final double n) {
        this.value = clamp(this.value + n);
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
        }
        if (this.value >= 70.0) {
            return 3;
        }
        if (this.value >= 45.0) {
            return 2;
        }
        if (this.value >= 20.0) {
            return 1;
        }
        return 0;
    }
    
    private static double clamp(final double b) {
        return Math.max(0.0, Math.min(100.0, b));
    }
}
