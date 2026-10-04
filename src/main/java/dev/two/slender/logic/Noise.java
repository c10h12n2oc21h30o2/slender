package dev.two.slender.logic;

public final class Noise {
    private final long seed;

    public Noise(long var1) {
        this.seed = var1;
    }

    private double lattice(int var1, int var2) {
        long var3 = this.seed ^ (long)var1 * -7046029254386353131L ^ (long)var2 * -4417276706812531889L;
        var3 ^= var3 >>> 33;
        var3 *= -49064778989728563L;
        var3 ^= var3 >>> 33;
        var3 *= -4265267296055464877L;
        var3 ^= var3 >>> 33;
        return (double)(var3 >>> 11) * 1.1102230246251565E-16 * 2.0 - 1.0;
    }

    private static double smooth(double var0) {
        return var0 * var0 * (3.0 - 2.0 * var0);
    }

    public double value(double var1, double var3, double var5) {
        double var7 = var1 / var5;
        double var9 = var3 / var5;
        int var11 = (int)Math.floor(var7);
        int var12 = (int)Math.floor(var9);
        double var13 = smooth(var7 - (double)var11);
        double var15 = smooth(var9 - (double)var12);
        double var17 = this.lattice(var11, var12);
        double var19 = this.lattice(var11 + 1, var12);
        double var21 = this.lattice(var11, var12 + 1);
        double var23 = this.lattice(var11 + 1, var12 + 1);
        double var25 = var17 + (var19 - var17) * var13;
        double var27 = var21 + (var23 - var21) * var13;
        return var25 + (var27 - var25) * var15;
    }

    public double fractal(double var1, double var3, double var5, int var7) {
        double var8 = 0.0;
        double var10 = 1.0;
        double var12 = 0.0;
        double var14 = var5;

        for(int var16 = 0; var16 < var7; ++var16) {
            var8 += this.value(var1 + (double)var16 * 1013.0, var3 - (double)var16 * 719.0, var14) * var10;
            var12 += var10;
            var10 *= 0.5;
            var14 /= 2.0;
        }

        return var8 / var12;
    }
}
