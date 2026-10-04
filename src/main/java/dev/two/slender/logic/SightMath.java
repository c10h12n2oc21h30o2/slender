// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class SightMath
{
    private SightMath() {
    }
    
    public static double angleBetweenDeg(final Vec3 vec3, final Vec3 vec4) {
        final double length = vec3.length();
        final double length2 = vec4.length();
        if (length < 1.0E-9 || length2 < 1.0E-9) {
            return 180.0;
        }
        return Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, vec3.dot(vec4) / (length * length2)))));
    }
    
    public static boolean inCone(final Vec3 vec3, final Vec3 vec4, final Vec3 vec5, final double n, final double n2) {
        final Vec3 sub = vec5.sub(vec3);
        final double length = sub.length();
        return length <= n2 && (length < 1.0E-6 || angleBetweenDeg(vec4, sub) <= n);
    }
    
    public static double horizontalAngleDeg(final Vec3 vec3, final Vec3 vec4, final Vec3 vec5) {
        return angleBetweenDeg(vec4.horizontal(), vec5.sub(vec3).horizontal());
    }
}
