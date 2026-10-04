// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import org.bukkit.Material;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;

public final class TerrainPainter
{
    private static final int[][] DIRS;
    
    private TerrainPainter() {
    }
    
    public static void paint(final Builder builder, final Heightmap heightmap, final Surface surface) {
        final int radius = heightmap.radius();
        final int waterLevel = heightmap.waterLevel();
        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                final int at = heightmap.at(i, j);
                for (int k = at - 3; k < at; ++k) {
                    builder.set(i, k, j, Material.DIRT);
                }
                builder.set(i, at, j, surface.top(i, j, at));
                if (heightmap.is(i, j, (byte)2)) {
                    for (int l = at + 1; l <= waterLevel; ++l) {
                        builder.set(i, l, j, Material.WATER);
                    }
                    if (heightmap.is(i, j, (byte)1)) {
                        builder.set(i, waterLevel, j, Material.SPRUCE_PLANKS);
                        if (bridgeSide(heightmap, i, j)) {
                            builder.set(i, waterLevel + 1, j, "spruce_fence");
                        }
                    }
                }
            }
        }
    }
    
    private static boolean bridgeSide(final Heightmap heightmap, final int n, final int n2) {
        for (final int[] array : TerrainPainter.DIRS) {
            if (heightmap.is(n + array[0], n2 + array[1], (byte)2) && !heightmap.is(n + array[0], n2 + array[1], (byte)1)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        DIRS = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    }
    
    public interface Surface
    {
        Material top(final int p0, final int p1, final int p2);
    }
}
