// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

public final class Heightmap
{
    public static final byte ROAD = 1;
    public static final byte WATER = 2;
    public static final byte PAD = 4;
    public static final byte BANK = 8;
    private static final int[][] DIRS;
    private final int r;
    private final int size;
    private final int water;
    private final int[] h;
    private final byte[] tags;
    
    public Heightmap(final int r, final int water) {
        this.r = r;
        this.size = r * 2 + 1;
        this.water = water;
        this.h = new int[this.size * this.size];
        this.tags = new byte[this.size * this.size];
    }
    
    public int radius() {
        return this.r;
    }
    
    public int waterLevel() {
        return this.water;
    }
    
    public boolean inside(final int a, final int a2) {
        return Math.abs(a) <= this.r && Math.abs(a2) <= this.r;
    }
    
    private int i(final int n, final int n2) {
        return (n + this.r) * this.size + (n2 + this.r);
    }
    
    private int edge(final int b) {
        return Math.max(-this.r, Math.min(this.r, b));
    }
    
    public int at(final int n, final int n2) {
        return this.h[this.i(this.edge(n), this.edge(n2))];
    }
    
    public void set(final int n, final int n2, final int n3) {
        if (this.inside(n, n2)) {
            this.h[this.i(n, n2)] = n3;
        }
    }
    
    public boolean is(final int n, final int n2, final byte b) {
        return this.inside(n, n2) && (this.tags[this.i(n, n2)] & b) != 0x0;
    }
    
    public void mark(final int n, final int n2, final byte b) {
        if (this.inside(n, n2)) {
            final byte[] tags = this.tags;
            final int i = this.i(n, n2);
            tags[i] |= b;
        }
    }
    
    private boolean fixed(final int n, final int n2) {
        return this.is(n, n2, (byte)14);
    }
    
    public void hills(final Noise noise, final double n, final int n2, final double n3, final double n4, final int a, final int a2) {
        for (int i = -this.r; i <= this.r; ++i) {
            for (int j = -this.r; j <= this.r; ++j) {
                this.h[this.i(i, j)] = Math.max(a, Math.min(a2, (int)Math.round(n3 + n4 * noise.fractal(i, j, n, n2))));
            }
        }
    }
    
    public void flatten(final int n, final int n2, final int n3, final int n4, final int n5) {
        for (int i = n - n3 - n5; i <= n + n3 + n5; ++i) {
            for (int j = n2 - n3 - n5; j <= n2 + n3 + n5; ++j) {
                if (this.inside(i, j)) {
                    if (!this.is(i, j, (byte)2)) {
                        this.blendTo(i, j, Math.hypot(i - n, j - n2) - n3, n4, n5);
                    }
                }
            }
        }
    }
    
    public void flattenRect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final int min = Math.min(n, n3);
        final int max = Math.max(n, n3);
        final int min2 = Math.min(n2, n4);
        final int max2 = Math.max(n2, n4);
        for (int i = min - n6; i <= max + n6; ++i) {
            for (int j = min2 - n6; j <= max2 + n6; ++j) {
                if (this.inside(i, j)) {
                    if (!this.is(i, j, (byte)2)) {
                        this.blendTo(i, j, Math.max(Math.max(min - i, i - max), Math.max(min2 - j, j - max2)), n5, n6);
                    }
                }
            }
        }
    }
    
    private void blendTo(final int n, final int n2, final double n3, final int n4, final int n5) {
        if (n3 <= 0.5) {
            this.set(n, n2, n4);
            this.mark(n, n2, (byte)4);
            return;
        }
        if (n3 > n5 || this.is(n, n2, (byte)4)) {
            return;
        }
        this.set(n, n2, (int)Math.round(n4 + (this.at(n, n2) - n4) * (n3 / (n5 + 1))));
    }
    
    private void flood(final int n, final int n2, final int n3) {
        this.set(n, n2, n3);
        this.tags[this.i(n, n2)] = (byte)((this.tags[this.i(n, n2)] | 0x2) & 0xFFFFFFF7);
    }
    
    public void channel(final int[][] array, final int n, final int n2) {
        for (final double[] array2 : samples(array)) {
            final int n3 = (int)Math.round(array2[0]);
            final int n4 = (int)Math.round(array2[1]);
            for (int i = n3 - n - 3; i <= n3 + n + 3; ++i) {
                for (int j = n4 - n - 3; j <= n4 + n + 3; ++j) {
                    if (this.inside(i, j)) {
                        final double hypot = Math.hypot(i - array2[0], j - array2[1]);
                        if (hypot <= n + 0.5) {
                            this.flood(i, j, this.water - n2);
                        }
                        else if (hypot <= n + 2.5 && !this.is(i, j, (byte)2) && !this.is(i, j, (byte)4)) {
                            this.set(i, j, this.water);
                            this.mark(i, j, (byte)8);
                        }
                    }
                }
            }
        }
    }
    
    public void lake(final int n, final int n2, final int n3, final int n4, final int n5) {
        for (int i = n - n3 - 3; i <= n + n3 + 3; ++i) {
            for (int j = n2 - n4 - 3; j <= n2 + n4 + 3; ++j) {
                if (this.inside(i, j)) {
                    final double n6 = Math.pow((i - n) / (double)n3, 2.0) + Math.pow((j - n2) / (double)n4, 2.0);
                    if (n6 <= 1.0) {
                        this.flood(i, j, this.water - 1 - (int)Math.round((n5 - 1) * (1.0 - n6)));
                    }
                    else if (Math.pow((i - n) / (double)(n3 + 2), 2.0) + Math.pow((j - n2) / (double)(n4 + 2), 2.0) <= 1.0 && !this.is(i, j, (byte)2) && !this.is(i, j, (byte)4)) {
                        this.set(i, j, this.water);
                        this.mark(i, j, (byte)8);
                    }
                }
            }
        }
    }
    
    public void road(final int[][] array, final int n) {
        final List<double[]> samples = samples(array);
        final double[] array2 = new double[samples.size()];
        for (int i = 0; i < samples.size(); ++i) {
            final int n2 = (int)Math.round(((double[])samples.get(i))[0]);
            final int n3 = (int)Math.round(((double[])samples.get(i))[1]);
            array2[i] = ((this.is(n2, n3, (byte)2) || this.is(n2, n3, (byte)8)) ? this.water : ((double)this.at(n2, n3)));
        }
        final double[] array3 = new double[array2.length];
        for (int j = 0; j < array2.length; ++j) {
            double n4 = 0.0;
            int n5 = 0;
            for (int k = Math.max(0, j - 6); k <= Math.min(array2.length - 1, j + 6); ++k) {
                n4 += array2[k];
                ++n5;
            }
            array3[j] = n4 / n5;
        }
        final double[] a = new double[this.size * this.size];
        Arrays.fill(a, Double.MAX_VALUE);
        for (int l = 0; l < samples.size(); ++l) {
            final double[] array4 = samples.get(l);
            final int n6 = (int)Math.round(array4[0]);
            final int n7 = (int)Math.round(array4[1]);
            for (int n8 = n6 - n - 1; n8 <= n6 + n + 1; ++n8) {
                for (int n9 = n7 - n - 1; n9 <= n7 + n + 1; ++n9) {
                    if (this.inside(n8, n9)) {
                        final double hypot = Math.hypot(n8 - array4[0], n9 - array4[1]);
                        if (hypot <= n + 0.5) {
                            if (hypot < a[this.i(n8, n9)]) {
                                a[this.i(n8, n9)] = hypot;
                                this.mark(n8, n9, (byte)1);
                                if (!this.fixed(n8, n9)) {
                                    this.set(n8, n9, (int)Math.round(array3[l]));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static List<double[]> samples(final int[][] array) {
        final ArrayList list = new ArrayList();
        for (int n = 0; n + 1 < array.length; ++n) {
            final double n2 = array[n][0];
            final double n3 = array[n][1];
            final double n4 = array[n + 1][0];
            final double n5 = array[n + 1][1];
            for (int max = Math.max(1, (int)Math.ceil(Math.hypot(n4 - n2, n5 - n3))), i = 0; i < max; ++i) {
                list.add(new double[] { n2 + (n4 - n2) * i / max, n3 + (n5 - n3) * i / max });
            }
        }
        final int[] array2 = array[array.length - 1];
        list.add(new double[] { array2[0], array2[1] });
        return list;
    }
    
    public void clampSlope() {
        this.relax(true);
        this.relax(false);
    }
    
    private void relax(final boolean b) {
        for (int n = 1, n2 = 0; n != 0 && n2 < 1000; ++n2) {
            n = 0;
            final boolean b2 = n2 % 2 == 0;
            for (int i = 0; i < this.size; ++i) {
                for (int j = 0; j < this.size; ++j) {
                    final int n3 = (b2 ? i : (this.size - 1 - i)) - this.r;
                    final int n4 = (b2 ? j : (this.size - 1 - j)) - this.r;
                    if (!this.fixed(n3, n4)) {
                        int n6;
                        final int n5 = n6 = this.h[this.i(n3, n4)];
                        for (final int[] array : Heightmap.DIRS) {
                            final int n7 = n3 + array[0];
                            final int n8 = n4 + array[1];
                            if (this.inside(n7, n8)) {
                                if (!this.is(n7, n8, (byte)2)) {
                                    final int n9 = this.h[this.i(n7, n8)];
                                    n6 = (b ? Math.min(n6, n9 + 1) : Math.max(n6, n9 - 1));
                                }
                            }
                        }
                        if (n6 != n5) {
                            this.h[this.i(n3, n4)] = n6;
                            n = 1;
                        }
                    }
                }
            }
        }
    }
    
    public int slopeViolations() {
        int n = 0;
        for (int i = -this.r; i <= this.r; ++i) {
            for (int j = -this.r; j <= this.r; ++j) {
                if (!this.is(i, j, (byte)2)) {
                    if (i < this.r && !this.is(i + 1, j, (byte)2) && Math.abs(this.at(i, j) - this.at(i + 1, j)) > 1) {
                        ++n;
                    }
                    if (j < this.r && !this.is(i, j + 1, (byte)2) && Math.abs(this.at(i, j) - this.at(i, j + 1)) > 1) {
                        ++n;
                    }
                }
            }
        }
        return n;
    }
    
    static {
        DIRS = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    }
}
