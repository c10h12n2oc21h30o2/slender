// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

record Vec3(double x, double y, double z) {
    public static final Vec3 ZERO;
    
    public Vec3 add(final Vec3 vec3) {
        return new Vec3(this.x + vec3.x, this.y + vec3.y, this.z + vec3.z);
    }
    
    public Vec3 sub(final Vec3 vec3) {
        return new Vec3(this.x - vec3.x, this.y - vec3.y, this.z - vec3.z);
    }
    
    public Vec3 mul(final double n) {
        return new Vec3(this.x * n, this.y * n, this.z * n);
    }
    
    public double dot(final Vec3 vec3) {
        return this.x * vec3.x + this.y * vec3.y + this.z * vec3.z;
    }
    
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public double distance(final Vec3 vec3) {
        return this.sub(vec3).length();
    }
    
    public Vec3 horizontal() {
        return new Vec3(this.x, 0.0, this.z);
    }
    
    public Vec3 normalize() {
        final double length = this.length();
        return (length < 1.0E-9) ? Vec3.ZERO : new Vec3(this.x / length, this.y / length, this.z / length);
    }
    
    public Vec3 rotateYaw(final double angdeg) {
        final double radians = Math.toRadians(angdeg);
        final double cos = Math.cos(radians);
        final double sin = Math.sin(radians);
        return new Vec3(this.x * cos - this.z * sin, this.y, this.z * cos + this.x * sin);
    }
    
    public static Vec3 fromYawPitch(final float n, final float n2) {
        final double radians = Math.toRadians(n);
        final double radians2 = Math.toRadians(n2);
        final double cos = Math.cos(radians2);
        return new Vec3(-cos * Math.sin(radians), -Math.sin(radians2), cos * Math.cos(radians));
    }
    
    public static float yawOf(final Vec3 vec3) {
        return (float)Math.toDegrees(Math.atan2(-vec3.x, vec3.z));
    }
    
    static {
        ZERO = new Vec3(0.0, 0.0, 0.0);
    }
}
