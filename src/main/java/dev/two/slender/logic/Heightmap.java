package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Heightmap {
    public static final byte ROAD = 1;
    public static final byte WATER = 2;
    public static final byte PAD = 4;
    public static final byte BANK = 8;
    private static final int[][] DIRS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private final int r;
    private final int size;
    private final int water;
    private final int[] h;
    private final byte[] tags;

    public Heightmap(int var1, int var2) {
        this.r = var1;
        this.size = var1 * 2 + 1;
        this.water = var2;
        this.h = new int[this.size * this.size];
        this.tags = new byte[this.size * this.size];
    }

    public int radius() {
        return this.r;
    }

    public int waterLevel() {
        return this.water;
    }

    public boolean inside(int var1, int var2) {
        return Math.abs(var1) <= this.r && Math.abs(var2) <= this.r;
    }

    private int i(int var1, int var2) {
        return (var1 + this.r) * this.size + var2 + this.r;
    }

    private int edge(int var1) {
        return Math.max(-this.r, Math.min(this.r, var1));
    }

    public int at(int var1, int var2) {
        return this.h[this.i(this.edge(var1), this.edge(var2))];
    }

    public void set(int var1, int var2, int var3) {
        if (this.inside(var1, var2)) {
            this.h[this.i(var1, var2)] = var3;
        }

    }

    public boolean is(int var1, int var2, byte var3) {
        return this.inside(var1, var2) && (this.tags[this.i(var1, var2)] & var3) != 0;
    }

    public void mark(int var1, int var2, byte var3) {
        if (this.inside(var1, var2)) {
            byte[] var10000 = this.tags;
            int var10001 = this.i(var1, var2);
            var10000[var10001] |= var3;
        }

    }

    private boolean fixed(int var1, int var2) {
        return this.is(var1, var2, (byte)14);
    }

    public void hills(Noise var1, double var2, int var4, double var5, double var7, int var9, int var10) {
        for(int var11 = -this.r; var11 <= this.r; ++var11) {
            for(int var12 = -this.r; var12 <= this.r; ++var12) {
                int var13 = (int)Math.round(var5 + var7 * var1.fractal((double)var11, (double)var12, var2, var4));
                this.h[this.i(var11, var12)] = Math.max(var9, Math.min(var10, var13));
            }
        }

    }

    public void flatten(int var1, int var2, int var3, int var4, int var5) {
        for(int var6 = var1 - var3 - var5; var6 <= var1 + var3 + var5; ++var6) {
            for(int var7 = var2 - var3 - var5; var7 <= var2 + var3 + var5; ++var7) {
                if (this.inside(var6, var7) && !this.is(var6, var7, (byte)2)) {
                    this.blendTo(var6, var7, Math.hypot((double)(var6 - var1), (double)(var7 - var2)) - (double)var3, var4, var5);
                }
            }
        }

    }

    public void flattenRect(int var1, int var2, int var3, int var4, int var5, int var6) {
        int var7 = Math.min(var1, var3);
        int var8 = Math.max(var1, var3);
        int var9 = Math.min(var2, var4);
        int var10 = Math.max(var2, var4);

        for(int var11 = var7 - var6; var11 <= var8 + var6; ++var11) {
            for(int var12 = var9 - var6; var12 <= var10 + var6; ++var12) {
                if (this.inside(var11, var12) && !this.is(var11, var12, (byte)2)) {
                    this.blendTo(var11, var12, (double)Math.max(Math.max(var7 - var11, var11 - var8), Math.max(var9 - var12, var12 - var10)), var5, var6);
                }
            }
        }

    }

    private void blendTo(int var1, int var2, double var3, int var5, int var6) {
        if (var3 <= 0.5) {
            this.set(var1, var2, var5);
            this.mark(var1, var2, (byte)4);
        } else if (!(var3 > (double)var6) && !this.is(var1, var2, (byte)4)) {
            double var7 = var3 / (double)(var6 + 1);
            this.set(var1, var2, (int)Math.round((double)var5 + (double)(this.at(var1, var2) - var5) * var7));
        }
    }

    private void flood(int var1, int var2, int var3) {
        this.set(var1, var2, var3);
        this.tags[this.i(var1, var2)] = (byte)((this.tags[this.i(var1, var2)] | 2) & -9);
    }

    public void channel(int[][] var1, int var2, int var3) {
        Iterator var4 = samples(var1).iterator();

        while(var4.hasNext()) {
            double[] var5 = (double[])var4.next();
            int var6 = (int)Math.round(var5[0]);
            int var7 = (int)Math.round(var5[1]);

            for(int var8 = var6 - var2 - 3; var8 <= var6 + var2 + 3; ++var8) {
                for(int var9 = var7 - var2 - 3; var9 <= var7 + var2 + 3; ++var9) {
                    if (this.inside(var8, var9)) {
                        double var10 = Math.hypot((double)var8 - var5[0], (double)var9 - var5[1]);
                        if (var10 <= (double)var2 + 0.5) {
                            this.flood(var8, var9, this.water - var3);
                        } else if (var10 <= (double)var2 + 2.5 && !this.is(var8, var9, (byte)2) && !this.is(var8, var9, (byte)4)) {
                            this.set(var8, var9, this.water);
                            this.mark(var8, var9, (byte)8);
                        }
                    }
                }
            }
        }

    }

    public void lake(int var1, int var2, int var3, int var4, int var5) {
        for(int var6 = var1 - var3 - 3; var6 <= var1 + var3 + 3; ++var6) {
            for(int var7 = var2 - var4 - 3; var7 <= var2 + var4 + 3; ++var7) {
                if (this.inside(var6, var7)) {
                    double var8 = Math.pow((double)(var6 - var1) / (double)var3, 2.0) + Math.pow((double)(var7 - var2) / (double)var4, 2.0);
                    if (var8 <= 1.0) {
                        this.flood(var6, var7, this.water - 1 - (int)Math.round((double)(var5 - 1) * (1.0 - var8)));
                    } else {
                        double var10 = Math.pow((double)(var6 - var1) / (double)(var3 + 2), 2.0) + Math.pow((double)(var7 - var2) / (double)(var4 + 2), 2.0);
                        if (var10 <= 1.0 && !this.is(var6, var7, (byte)2) && !this.is(var6, var7, (byte)4)) {
                            this.set(var6, var7, this.water);
                            this.mark(var6, var7, (byte)8);
                        }
                    }
                }
            }
        }

    }

    public void road(int[][] var1, int var2) {
        List var3 = samples(var1);
        double[] var4 = new double[var3.size()];

        int var6;
        int var7;
        for(int var5 = 0; var5 < var3.size(); ++var5) {
            var6 = (int)Math.round(((double[])var3.get(var5))[0]);
            var7 = (int)Math.round(((double[])var3.get(var5))[1]);
            var4[var5] = !this.is(var6, var7, (byte)2) && !this.is(var6, var7, (byte)8) ? (double)this.at(var6, var7) : (double)this.water;
        }

        double[] var15 = new double[var4.length];

        int var9;
        int var10;
        for(var6 = 0; var6 < var4.length; ++var6) {
            double var17 = 0.0;
            var9 = 0;

            for(var10 = Math.max(0, var6 - 6); var10 <= Math.min(var4.length - 1, var6 + 6); ++var10) {
                var17 += var4[var10];
                ++var9;
            }

            var15[var6] = var17 / (double)var9;
        }

        double[] var16 = new double[this.size * this.size];
        Arrays.fill(var16, Double.MAX_VALUE);

        for(var7 = 0; var7 < var3.size(); ++var7) {
            double[] var8 = (double[])var3.get(var7);
            var9 = (int)Math.round(var8[0]);
            var10 = (int)Math.round(var8[1]);

            for(int var11 = var9 - var2 - 1; var11 <= var9 + var2 + 1; ++var11) {
                for(int var12 = var10 - var2 - 1; var12 <= var10 + var2 + 1; ++var12) {
                    if (this.inside(var11, var12)) {
                        double var13 = Math.hypot((double)var11 - var8[0], (double)var12 - var8[1]);
                        if (!(var13 > (double)var2 + 0.5) && !(var13 >= var16[this.i(var11, var12)])) {
                            var16[this.i(var11, var12)] = var13;
                            this.mark(var11, var12, (byte)1);
                            if (!this.fixed(var11, var12)) {
                                this.set(var11, var12, (int)Math.round(var15[var7]));
                            }
                        }
                    }
                }
            }
        }

    }

    static List<double[]> samples(int[][] var0) {
        ArrayList var1 = new ArrayList();

        for(int var2 = 0; var2 + 1 < var0.length; ++var2) {
            double var3 = (double)var0[var2][0];
            double var5 = (double)var0[var2][1];
            double var7 = (double)var0[var2 + 1][0];
            double var9 = (double)var0[var2 + 1][1];
            int var11 = Math.max(1, (int)Math.ceil(Math.hypot(var7 - var3, var9 - var5)));

            for(int var12 = 0; var12 < var11; ++var12) {
                var1.add(new double[]{var3 + (var7 - var3) * (double)var12 / (double)var11, var5 + (var9 - var5) * (double)var12 / (double)var11});
            }
        }

        int[] var13 = var0[var0.length - 1];
        var1.add(new double[]{(double)var13[0], (double)var13[1]});
        return var1;
    }

    public void clampSlope() {
        this.relax(true);
        this.relax(false);
    }

    private void relax(boolean var1) {
        boolean var2 = true;

        for(int var3 = 0; var2 && var3 < 1000; ++var3) {
            var2 = false;
            boolean var4 = var3 % 2 == 0;

            for(int var5 = 0; var5 < this.size; ++var5) {
                for(int var6 = 0; var6 < this.size; ++var6) {
                    int var7 = (var4 ? var5 : this.size - 1 - var5) - this.r;
                    int var8 = (var4 ? var6 : this.size - 1 - var6) - this.r;
                    if (!this.fixed(var7, var8)) {
                        int var9 = this.h[this.i(var7, var8)];
                        int var10 = var9;
                        int[][] var11 = DIRS;
                        int var12 = var11.length;

                        for(int var13 = 0; var13 < var12; ++var13) {
                            int[] var14 = var11[var13];
                            int var15 = var7 + var14[0];
                            int var16 = var8 + var14[1];
                            if (this.inside(var15, var16) && !this.is(var15, var16, (byte)2)) {
                                int var17 = this.h[this.i(var15, var16)];
                                var10 = var1 ? Math.min(var10, var17 + 1) : Math.max(var10, var17 - 1);
                            }
                        }

                        if (var10 != var9) {
                            this.h[this.i(var7, var8)] = var10;
                            var2 = true;
                        }
                    }
                }
            }
        }

    }

    public int slopeViolations() {
        int var1 = 0;

        for(int var2 = -this.r; var2 <= this.r; ++var2) {
            for(int var3 = -this.r; var3 <= this.r; ++var3) {
                if (!this.is(var2, var3, (byte)2)) {
                    if (var2 < this.r && !this.is(var2 + 1, var3, (byte)2) && Math.abs(this.at(var2, var3) - this.at(var2 + 1, var3)) > 1) {
                        ++var1;
                    }

                    if (var3 < this.r && !this.is(var2, var3 + 1, (byte)2) && Math.abs(this.at(var2, var3) - this.at(var2, var3 + 1)) > 1) {
                        ++var1;
                    }
                }
            }
        }

        return var1;
    }
}
