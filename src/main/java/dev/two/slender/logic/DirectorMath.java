// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DirectorMath
{
    private DirectorMath() {
    }
    
    public static boolean dormant(final int n, final double n2) {
        return n == 0 && n2 < 60.0;
    }
    
    public static double aggression(final int n, final double n2, final boolean b) {
        if (b) {
            return 10.0;
        }
        return Math.min(8.0, n + Math.floor(n2 / 90.0));
    }
    
    public static double teleportIntervalSeconds(final double n) {
        return Math.max(4.0, 12.0 - n);
    }
    
    public static double teleportDistance(final double n) {
        return Math.max(7.0, 35.0 - 3.2 * n);
    }
    
    public static double creepSpeedPerTick(final double n) {
        return 0.05 + 0.02 * n;
    }
    
    public static double frontAppearChance(final double n) {
        return (n >= 3.0) ? Math.min(0.6, 0.06 * n) : 0.0;
    }
    
    public static int levelRank(final int n, final int n2) {
        final int abs = Math.abs(n2 - n);
        return (abs <= 1) ? 0 : ((abs <= 4) ? 1 : -1);
    }
    
    public static List<Vec3> teleportCandidates(final Vec3 vec3, final Vec3 vec4, final double n, final int n2, final double n3, final Random random, final Bounds bounds) {
        Vec3 normalize = vec4.horizontal().normalize();
        if (normalize.length() < 1.0E-9) {
            normalize = new Vec3(0.0, 0.0, 1.0);
        }
        final ArrayList list = new ArrayList();
        for (int i = 0; i < n2; ++i) {
            double n4 = n3 + random.nextDouble() * (180.0 - n3);
            if (random.nextBoolean()) {
                n4 = -n4;
            }
            final Vec3 add = vec3.add(normalize.rotateYaw(n4).mul(n));
            final Vec3 vec5 = new Vec3(add.x(), vec3.y(), add.z());
            if (bounds.contains(vec5)) {
                list.add(vec5);
            }
        }
        return list;
    }
    
    public static Vec3 frontPosition(final Vec3 vec3, final Vec3 vec4, final double n) {
        Vec3 normalize = vec4.horizontal().normalize();
        if (normalize.length() < 1.0E-9) {
            normalize = new Vec3(0.0, 0.0, 1.0);
        }
        final Vec3 add = vec3.add(normalize.mul(n));
        return new Vec3(add.x(), vec3.y(), add.z());
    }
    
    public static List<Vec3> subSteps(final Vec3 vec3, final Vec3 vec4, final double n, final double n2) {
        final Vec3 stepTowards = stepTowards(vec3, vec4, n);
        final double length = stepTowards.sub(vec3).horizontal().length();
        final ArrayList list = new ArrayList();
        if (length < 1.0E-9) {
            return list;
        }
        for (int n3 = (int)Math.ceil(length / n2 - 1.0E-9), i = 1; i <= n3; ++i) {
            final double n4 = i / (double)n3;
            list.add(new Vec3(vec3.x() + (stepTowards.x() - vec3.x()) * n4, vec3.y(), vec3.z() + (stepTowards.z() - vec3.z()) * n4));
        }
        return list;
    }
    
    public static Vec3 stepTowards(final Vec3 vec3, final Vec3 vec4, final double n) {
        final Vec3 horizontal = vec4.sub(vec3).horizontal();
        final double length = horizontal.length();
        if (length <= n) {
            return new Vec3(vec4.x(), vec3.y(), vec4.z());
        }
        final Vec3 mul = horizontal.mul(n / length);
        return new Vec3(vec3.x() + mul.x(), vec3.y(), vec3.z() + mul.z());
    }
    
    record Bounds(double minX, double minZ, double maxX, double maxZ) {
        public boolean contains(final Vec3 vec3) {
            return vec3.x() >= this.minX && vec3.x() <= this.maxX && vec3.z() >= this.minZ && vec3.z() <= this.maxZ;
        }
    }
}
