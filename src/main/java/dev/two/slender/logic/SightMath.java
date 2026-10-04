package dev.two.slender.logic;

public final class SightMath {
    private SightMath() {
    }

    public static double angleBetweenDeg(Vec3 var0, Vec3 var1) {
        double var2 = var0.length();
        double var4 = var1.length();
        if (!(var2 < 1.0E-9) && !(var4 < 1.0E-9)) {
            double var6 = Math.max(-1.0, Math.min(1.0, var0.dot(var1) / (var2 * var4)));
            return Math.toDegrees(Math.acos(var6));
        } else {
            return 180.0;
        }
    }

    public static boolean inCone(Vec3 var0, Vec3 var1, Vec3 var2, double var3, double var5) {
        Vec3 var7 = var2.sub(var0);
        double var8 = var7.length();
        if (var8 > var5) {
            return false;
        } else if (var8 < 1.0E-6) {
            return true;
        } else {
            return angleBetweenDeg(var1, var7) <= var3;
        }
    }

    public static double horizontalAngleDeg(Vec3 var0, Vec3 var1, Vec3 var2) {
        return angleBetweenDeg(var1.horizontal(), var2.sub(var0).horizontal());
    }
}
