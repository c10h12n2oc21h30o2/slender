package dev.two.slender.map.gen;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;
import org.bukkit.Material;

public final class TerrainPainter {
    private static final int[][] DIRS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private TerrainPainter() {
    }

    public static void paint(Builder var0, Heightmap var1, Surface var2) {
        int var3 = var1.radius();
        int var4 = var1.waterLevel();

        for(int var5 = -var3; var5 <= var3; ++var5) {
            for(int var6 = -var3; var6 <= var3; ++var6) {
                int var7 = var1.at(var5, var6);

                int var8;
                for(var8 = var7 - 3; var8 < var7; ++var8) {
                    var0.set(var5, var8, var6, Material.DIRT);
                }

                var0.set(var5, var7, var6, var2.top(var5, var6, var7));
                if (var1.is(var5, var6, (byte)2)) {
                    for(var8 = var7 + 1; var8 <= var4; ++var8) {
                        var0.set(var5, var8, var6, Material.WATER);
                    }

                    if (var1.is(var5, var6, (byte)1)) {
                        var0.set(var5, var4, var6, Material.SPRUCE_PLANKS);
                        if (bridgeSide(var1, var5, var6)) {
                            var0.set(var5, var4 + 1, var6, "spruce_fence");
                        }
                    }
                }
            }
        }

    }

    private static boolean bridgeSide(Heightmap var0, int var1, int var2) {
        int[][] var3 = DIRS;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int[] var6 = var3[var5];
            if (var0.is(var1 + var6[0], var2 + var6[1], (byte)2) && !var0.is(var1 + var6[0], var2 + var6[1], (byte)1)) {
                return true;
            }
        }

        return false;
    }

    public interface Surface {
        Material top(int var1, int var2, int var3);
    }
}
