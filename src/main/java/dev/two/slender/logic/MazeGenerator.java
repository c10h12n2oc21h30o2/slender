package dev.two.slender.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public final class MazeGenerator {
    private static final int[][] DIRS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private MazeGenerator() {
    }

    public static boolean[][] generate(int var0, int var1, double var2, Random var4) {
        boolean[][] var5 = new boolean[2 * var0 + 1][2 * var1 + 1];
        boolean[][] var6 = new boolean[var0][var1];
        ArrayDeque var7 = new ArrayDeque();
        var7.push(new int[]{0, 0});
        var6[0][0] = true;
        var5[1][1] = true;

        while(!var7.isEmpty()) {
            int[] var8 = (int[])var7.peek();
            ArrayList var9 = new ArrayList();
            int[][] var10 = DIRS;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                int[] var13 = var10[var12];
                int var14 = var8[0] + var13[0];
                int var15 = var8[1] + var13[1];
                if (var14 >= 0 && var15 >= 0 && var14 < var0 && var15 < var1 && !var6[var14][var15]) {
                    var9.add(new int[]{var14, var15});
                }
            }

            if (var9.isEmpty()) {
                var7.pop();
            } else {
                int[] var16 = (int[])var9.get(var4.nextInt(var9.size()));
                var6[var16[0]][var16[1]] = true;
                var5[var8[0] + var16[0] + 1][var8[1] + var16[1] + 1] = true;
                var5[2 * var16[0] + 1][2 * var16[1] + 1] = true;
                var7.push(var16);
            }
        }

        if (var2 > 0.0) {
            braid(var5, var0, var1, var2, var4);
        }

        return var5;
    }

    private static void braid(boolean[][] var0, int var1, int var2, double var3, Random var5) {
        for(int var6 = 0; var6 < var1; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
                int var8 = 2 * var6 + 1;
                int var9 = 2 * var7 + 1;
                int var10 = 0;
                ArrayList var11 = new ArrayList();
                int[][] var12 = DIRS;
                int var13 = var12.length;

                for(int var14 = 0; var14 < var13; ++var14) {
                    int[] var15 = var12[var14];
                    int var16 = var8 + var15[0];
                    int var17 = var9 + var15[1];
                    int var18 = var6 + var15[0];
                    int var19 = var7 + var15[1];
                    if (var0[var16][var17]) {
                        ++var10;
                    } else if (var18 >= 0 && var19 >= 0 && var18 < var1 && var19 < var2) {
                        var11.add(new int[]{var16, var17});
                    }
                }

                if (var10 == 1 && !var11.isEmpty() && var5.nextDouble() < var3) {
                    int[] var20 = (int[])var11.get(var5.nextInt(var11.size()));
                    var0[var20[0]][var20[1]] = true;
                }
            }
        }

    }

    public static void carve(boolean[][] var0, int var1, int var2, int var3, int var4) {
        int var5 = Math.max(1, Math.min(var1, var3));
        int var6 = Math.min(var0.length - 2, Math.max(var1, var3));
        int var7 = Math.max(1, Math.min(var2, var4));
        int var8 = Math.min(var0[0].length - 2, Math.max(var2, var4));

        for(int var9 = var5; var9 <= var6; ++var9) {
            for(int var10 = var7; var10 <= var8; ++var10) {
                var0[var9][var10] = true;
            }
        }

    }

    public static int openCount(boolean[][] var0) {
        int var1 = 0;
        boolean[][] var2 = var0;
        int var3 = var0.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            boolean[] var5 = var2[var4];
            boolean[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                boolean var9 = var6[var8];
                if (var9) {
                    ++var1;
                }
            }
        }

        return var1;
    }

    public static boolean isConnected(boolean[][] var0) {
        int var1 = openCount(var0);
        if (var1 == 0) {
            return true;
        } else {
            int var2 = -1;
            int var3 = -1;

            label60:
            for(int var4 = 0; var4 < var0.length; ++var4) {
                for(int var5 = 0; var5 < var0[0].length; ++var5) {
                    if (var0[var4][var5]) {
                        var2 = var4;
                        var3 = var5;
                        break label60;
                    }
                }
            }

            boolean[][] var14 = new boolean[var0.length][var0[0].length];
            ArrayDeque var15 = new ArrayDeque();
            var15.add(new int[]{var2, var3});
            var14[var2][var3] = true;
            int var6 = 0;

            while(!var15.isEmpty()) {
                int[] var7 = (int[])var15.poll();
                ++var6;
                int[][] var8 = DIRS;
                int var9 = var8.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    int[] var11 = var8[var10];
                    int var12 = var7[0] + var11[0];
                    int var13 = var7[1] + var11[1];
                    if (var12 >= 0 && var13 >= 0 && var12 < var0.length && var13 < var0[0].length && var0[var12][var13] && !var14[var12][var13]) {
                        var14[var12][var13] = true;
                        var15.add(new int[]{var12, var13});
                    }
                }
            }

            return var6 == var1;
        }
    }
}
