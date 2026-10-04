package dev.two.slender.map;

import java.util.ArrayList;
import java.util.List;

public final class EditOps {
    private EditOps() {
    }

    public static int nearest(List<BlockPos> var0, double var1, double var3, double var5, double var7) {
        int var9 = -1;
        double var10 = var7 * var7;

        for(int var12 = 0; var12 < var0.size(); ++var12) {
            BlockPos var13 = (BlockPos)var0.get(var12);
            double var14 = (double)var13.x() + 0.5 - var1;
            double var16 = (double)var13.y() + 0.5 - var3;
            double var18 = (double)var13.z() + 0.5 - var5;
            double var20 = var14 * var14 + var16 * var16 + var18 * var18;
            if (var20 <= var10) {
                var10 = var20;
                var9 = var12;
            }
        }

        return var9;
    }

    public static boolean wallFace(String var0) {
        boolean var10000;
        switch (var0) {
            case "NORTH":
            case "SOUTH":
            case "EAST":
            case "WEST":
                var10000 = true;
                break;
            default:
                var10000 = false;
        }

        return var10000;
    }

    public static BlockPos front(BlockPos var0, String var1) {
        BlockPos var10000;
        switch (var1) {
            case "NORTH" -> var10000 = var0.offset(0, 0, -1);
            case "SOUTH" -> var10000 = var0.offset(0, 0, 1);
            case "EAST" -> var10000 = var0.offset(1, 0, 0);
            case "WEST" -> var10000 = var0.offset(-1, 0, 0);
            default -> throw new IllegalArgumentException("Cara no válida: " + var1);
        }

        return var10000;
    }

    public static <T> List<T> plus(List<T> var0, T var1) {
        ArrayList var2 = new ArrayList(var0);
        var2.add(var1);
        return List.copyOf(var2);
    }

    public static <T> List<T> minus(List<T> var0, int var1) {
        ArrayList var2 = new ArrayList(var0);
        var2.remove(var1);
        return List.copyOf(var2);
    }
}
