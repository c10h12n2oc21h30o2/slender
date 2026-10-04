// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class Noise
{
    private final long seed;
    
    public Noise(final long seed) {
        this.seed = seed;
    }
    
    private double lattice(final int n, final int n2) {
        final long n3 = this.seed ^ n * -7046029254386353131L ^ n2 * -4417276706812531889L;
        final long n4 = (n3 ^ n3 >>> 33) * -49064778989728563L;
        final long n5 = (n4 ^ n4 >>> 33) * -4265267296055464877L;
        return ((n5 ^ n5 >>> 33) >>> 11) * 1.1102230246251565E-16 * 2.0 - 1.0;
    }
    
    private static double smooth(final double n) {
        return n * n * (3.0 - 2.0 * n);
    }
    
    public double value(final double n, final double n2, final double n3) {
        final double a = n / n3;
        final double a2 = n2 / n3;
        final int n4 = (int)Math.floor(a);
        final int n5 = (int)Math.floor(a2);
        final double smooth = smooth(a - n4);
        final double smooth2 = smooth(a2 - n5);
        final double lattice = this.lattice(n4, n5);
        final double lattice2 = this.lattice(n4 + 1, n5);
        final double lattice3 = this.lattice(n4, n5 + 1);
        final double lattice4 = this.lattice(n4 + 1, n5 + 1);
        final double n6 = lattice + (lattice2 - lattice) * smooth;
        return n6 + (lattice3 + (lattice4 - lattice3) * smooth - n6) * smooth2;
    }
    
    public double fractal(final double n, final double n2, final double n3, final int n4) {
        double n5 = 0.0;
        double n6 = 1.0;
        double n7 = 0.0;
        double n8 = n3;
        for (int i = 0; i < n4; ++i) {
            n5 += this.value(n + i * 1013.0, n2 - i * 719.0, n8) * n6;
            n7 += n6;
            n6 *= 0.5;
            n8 /= 2.0;
        }
        return n5 / n7;
    }
}
