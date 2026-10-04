package dev.two.slender.logic;

public final class Signposts {
    private Signposts() {
    }

    public static String arrow(String var0, int var1, int var2) {
        byte var3;
        byte var4;
        switch (var0) {
            case "NORTH":
                var3 = 0;
                var4 = 1;
                break;
            case "SOUTH":
                var3 = 0;
                var4 = -1;
                break;
            case "EAST":
                var3 = -1;
                var4 = 0;
                break;
            case "WEST":
                var3 = 1;
                var4 = 0;
                break;
            default:
                throw new IllegalArgumentException("Cara no válida: " + var0);
        }

        int var5 = -var4;
        double var7 = (double)(var1 * var3 + var2 * var4);
        double var9 = (double)(var1 * var5 + var2 * var3);
        double var11 = Math.toDegrees(Math.atan2(var9, var7));
        if (Math.abs(var11) <= 45.0) {
            return "^";
        } else if (Math.abs(var11) >= 135.0) {
            return "v";
        } else {
            return var11 > 0.0 ? ">" : "<";
        }
    }

    public static String line(String var0, String var1, int var2, int var3) {
        String var4 = arrow(var0, var2, var3);
        return var4.equals(">") ? var1 + " >" : var4 + " " + var1;
    }
}
