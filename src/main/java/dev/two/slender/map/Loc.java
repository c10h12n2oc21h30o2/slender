// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

record Loc(double x, double y, double z, float yaw) {
    public String serialize() {
        return this.x + "," + this.y + "," + this.z + "," + this.yaw;
    }
    
    public static Loc parse(final String s) {
        final String[] split = s.split(",");
        if (split.length != 4) {
            throw new IllegalArgumentException("Loc inv\u00e1lida: " + s);
        }
        try {
            return new Loc(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]));
        }
        catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Loc inv\u00e1lida: " + s, (Throwable)cause);
        }
    }
}
