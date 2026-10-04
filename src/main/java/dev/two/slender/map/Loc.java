package dev.two.slender.map;

public record Loc(double x, double y, double z, float yaw) {
    public Loc(double x, double y, double z, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
    }

    public String serialize() {
        return this.x + "," + this.y + "," + this.z + "," + this.yaw;
    }

    public static Loc parse(String var0) {
        String[] var1 = var0.split(",");
        if (var1.length != 4) {
            throw new IllegalArgumentException("Loc inválida: " + var0);
        } else {
            try {
                return new Loc(Double.parseDouble(var1[0]), Double.parseDouble(var1[1]), Double.parseDouble(var1[2]), Float.parseFloat(var1[3]));
            } catch (NumberFormatException var3) {
                throw new IllegalArgumentException("Loc inválida: " + var0, var3);
            }
        }
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

    public float yaw() {
        return this.yaw;
    }
}
