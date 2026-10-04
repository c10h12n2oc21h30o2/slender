package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DirectorMath {
    private DirectorMath() {
    }

    public static boolean dormant(int var0, double var1) {
        return var0 == 0 && var1 < 60.0;
    }

    public static double aggression(int var0, double var1, boolean var3) {
        return var3 ? 10.0 : Math.min(8.0, (double)var0 + Math.floor(var1 / 90.0));
    }

    public static double teleportIntervalSeconds(double var0) {
        return Math.max(4.0, 12.0 - var0);
    }

    public static double teleportDistance(double var0) {
        return Math.max(7.0, 35.0 - 3.2 * var0);
    }

    public static double creepSpeedPerTick(double var0) {
        return 0.05 + 0.02 * var0;
    }

    public static double frontAppearChance(double var0) {
        return var0 >= 3.0 ? Math.min(0.6, 0.06 * var0) : 0.0;
    }

    public static int levelRank(int var0, int var1) {
        int var2 = Math.abs(var1 - var0);
        return var2 <= 1 ? 0 : (var2 <= 4 ? 1 : -1);
    }

    public static List<Vec3> teleportCandidates(Vec3 var0, Vec3 var1, double var2, int var4, double var5, Random var7, Bounds var8) {
        Vec3 var9 = var1.horizontal().normalize();
        if (var9.length() < 1.0E-9) {
            var9 = new Vec3(0.0, 0.0, 1.0);
        }

        ArrayList var10 = new ArrayList();

        for(int var11 = 0; var11 < var4; ++var11) {
            double var12 = var5 + var7.nextDouble() * (180.0 - var5);
            if (var7.nextBoolean()) {
                var12 = -var12;
            }

            Vec3 var14 = var0.add(var9.rotateYaw(var12).mul(var2));
            Vec3 var15 = new Vec3(var14.x(), var0.y(), var14.z());
            if (var8.contains(var15)) {
                var10.add(var15);
            }
        }

        return var10;
    }

    public static Vec3 frontPosition(Vec3 var0, Vec3 var1, double var2) {
        Vec3 var4 = var1.horizontal().normalize();
        if (var4.length() < 1.0E-9) {
            var4 = new Vec3(0.0, 0.0, 1.0);
        }

        Vec3 var5 = var0.add(var4.mul(var2));
        return new Vec3(var5.x(), var0.y(), var5.z());
    }

    public static List<Vec3> subSteps(Vec3 var0, Vec3 var1, double var2, double var4) {
        Vec3 var6 = stepTowards(var0, var1, var2);
        double var7 = var6.sub(var0).horizontal().length();
        ArrayList var9 = new ArrayList();
        if (var7 < 1.0E-9) {
            return var9;
        } else {
            int var10 = (int)Math.ceil(var7 / var4 - 1.0E-9);

            for(int var11 = 1; var11 <= var10; ++var11) {
                double var12 = (double)var11 / (double)var10;
                var9.add(new Vec3(var0.x() + (var6.x() - var0.x()) * var12, var0.y(), var0.z() + (var6.z() - var0.z()) * var12));
            }

            return var9;
        }
    }

    public static Vec3 stepTowards(Vec3 var0, Vec3 var1, double var2) {
        Vec3 var4 = var1.sub(var0).horizontal();
        double var5 = var4.length();
        if (var5 <= var2) {
            return new Vec3(var1.x(), var0.y(), var1.z());
        } else {
            Vec3 var7 = var4.mul(var2 / var5);
            return new Vec3(var0.x() + var7.x(), var0.y(), var0.z() + var7.z());
        }
    }

    public static record Bounds(double minX, double minZ, double maxX, double maxZ) {
        public Bounds(double minX, double minZ, double maxX, double maxZ) {
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
        }

        public boolean contains(Vec3 var1) {
            return var1.x() >= this.minX && var1.x() <= this.maxX && var1.z() >= this.minZ && var1.z() <= this.maxZ;
        }

        public double minX() {
            return this.minX;
        }

        public double minZ() {
            return this.minZ;
        }

        public double maxX() {
            return this.maxX;
        }

        public double maxZ() {
            return this.maxZ;
        }
    }
}
