package dev.two.slender.logic;

public record Vec3(double x, double y, double z) {
    public static final Vec3 ZERO = new Vec3(0.0, 0.0, 0.0);

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 var1) {
        return new Vec3(this.x + var1.x, this.y + var1.y, this.z + var1.z);
    }

    public Vec3 sub(Vec3 var1) {
        return new Vec3(this.x - var1.x, this.y - var1.y, this.z - var1.z);
    }

    public Vec3 mul(double var1) {
        return new Vec3(this.x * var1, this.y * var1, this.z * var1);
    }

    public double dot(Vec3 var1) {
        return this.x * var1.x + this.y * var1.y + this.z * var1.z;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double distance(Vec3 var1) {
        return this.sub(var1).length();
    }

    public Vec3 horizontal() {
        return new Vec3(this.x, 0.0, this.z);
    }

    public Vec3 normalize() {
        double var1 = this.length();
        return var1 < 1.0E-9 ? ZERO : new Vec3(this.x / var1, this.y / var1, this.z / var1);
    }

    public Vec3 rotateYaw(double var1) {
        double var3 = Math.toRadians(var1);
        double var5 = Math.cos(var3);
        double var7 = Math.sin(var3);
        return new Vec3(this.x * var5 - this.z * var7, this.y, this.z * var5 + this.x * var7);
    }

    public static Vec3 fromYawPitch(float var0, float var1) {
        double var2 = Math.toRadians((double)var0);
        double var4 = Math.toRadians((double)var1);
        double var6 = Math.cos(var4);
        return new Vec3(-var6 * Math.sin(var2), -Math.sin(var4), var6 * Math.cos(var2));
    }

    public static float yawOf(Vec3 var0) {
        return (float)Math.toDegrees(Math.atan2(-var0.x, var0.z));
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }
}
