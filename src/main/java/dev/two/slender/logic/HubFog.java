package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class HubFog {
    private HubFog() {
    }

    public static int viewDistance(int var0, int var1) {
        if (var1 <= 0) {
            return var0;
        } else {
            int var2 = Math.max(2, var1);
            return var0 <= 1 ? var2 : Math.min(var0, var2);
        }
    }

    public static boolean onPlatform(double var0, double var2) {
        int var4 = (int)Math.floor(var0);
        int var5 = (int)Math.floor(var2);
        return var4 >= -30 && var4 <= 30 && var5 >= -2 && var5 <= 7;
    }

    public static boolean onTracks(double var0, double var2) {
        int var4 = (int)Math.floor(var0);
        int var5 = (int)Math.floor(var2);
        return var4 >= -35 && var4 <= 35 && var5 >= 8 && var5 <= 16;
    }

    public static List<double[]> mist(double var0, double var2, int var4, double var5, Random var7) {
        ArrayList var8 = new ArrayList();

        for(int var9 = 0; var9 < var4 * 3 && var8.size() < var4; ++var9) {
            double var10 = var7.nextDouble() * 2.0 * Math.PI;
            double var12 = var5 * Math.sqrt(var7.nextDouble());
            double var14 = var0 + Math.cos(var10) * var12;
            double var16 = var2 + Math.sin(var10) * var12;
            if (onPlatform(var14, var16)) {
                var8.add(new double[]{var14, 2.0 + var7.nextDouble() * 0.6, var16});
            } else if (onTracks(var14, var16)) {
                var8.add(new double[]{var14, 1.0 + var7.nextDouble() * 0.6, var16});
            }
        }

        return var8;
    }
}
