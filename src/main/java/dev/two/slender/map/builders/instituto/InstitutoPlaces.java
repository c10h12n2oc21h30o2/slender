// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.instituto;

import java.util.ArrayList;
import dev.two.slender.map.BlockPos;
import org.bukkit.Material;
import java.util.Iterator;
import java.util.List;
import dev.two.slender.map.Builder;

final class InstitutoPlaces
{
    private InstitutoPlaces() {
    }
    
    static void outside(final Builder builder, final List<int[]> list) {
        fence(builder);
        exitRoad(builder);
        beacon(builder);
        for (final int[] array : list) {
            lamp(builder, array[0], array[1]);
        }
        busStop(builder);
        car(builder);
        hoop(builder, 142, 198, 199);
        hoop(builder, 142, 258, 257);
    }
    
    private static void fence(final Builder builder) {
        for (int i = 1; i <= 300; ++i) {
            post(builder, i, 1, "spruce_fence[east=true,west=true]");
            if (i < 4 || i > 35) {
                post(builder, i, 432, "spruce_fence[east=true,west=true]");
            }
        }
        for (int j = 1; j <= 432; ++j) {
            post(builder, 1, j, "spruce_fence[north=true,south=true]");
            post(builder, 300, j, "spruce_fence[north=true,south=true]");
        }
    }
    
    private static void post(final Builder builder, final int n, final int n2, final String s) {
        builder.set(n, 2, n2, s);
        builder.set(n, 3, n2, s);
        for (int i = 4; i <= 17; ++i) {
            builder.set(n, i, n2, Material.BARRIER);
        }
    }
    
    static List<BlockPos> gate(final Builder builder) {
        final ArrayList list = new ArrayList();
        for (int i = 4; i <= 35; ++i) {
            for (int j = 2; j <= 4; ++j) {
                builder.set(i, j, 432, "red_concrete");
                list.add(builder.abs(i, j, 432));
            }
            for (int k = 5; k <= 17; ++k) {
                builder.set(i, k, 432, Material.BARRIER);
            }
        }
        return list;
    }
    
    private static void exitRoad(final Builder builder) {
        for (int i = 433; i <= InstitutoLayout.EXIT[3] + 1; ++i) {
            for (int j = 4; j <= 35; ++j) {
                builder.set(j, 0, i, "dirt");
                builder.set(j, 1, i, (j == 12 && i % 6 < 3) ? "yellow_concrete" : ((j >= 20) ? "smooth_stone" : "gray_concrete"));
                for (int k = 2; k <= 5; ++k) {
                    builder.set(j, k, i, Material.AIR);
                }
            }
            for (int l = 2; l <= 15; ++l) {
                builder.set(3, l, i, Material.BARRIER);
                builder.set(36, l, i, Material.BARRIER);
            }
        }
        for (int n = 3; n <= 36; ++n) {
            for (int n2 = 2; n2 <= 15; ++n2) {
                builder.set(n, n2, InstitutoLayout.EXIT[3] + 2, Material.BARRIER);
            }
        }
        builder.fill(20, 2, InstitutoLayout.EXIT[3] + 1, 20, 3, InstitutoLayout.EXIT[3] + 1, "spruce_fence");
        builder.set(20, 4, InstitutoLayout.EXIT[3] + 1, "lantern[hanging=false]");
    }
    
    private static void beacon(final Builder builder) {
        builder.fill(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1] - 6, InstitutoLayout.BEACON[2], InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1] - 1, InstitutoLayout.BEACON[2], "iron_bars");
        builder.set(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1], InstitutoLayout.BEACON[2], "redstone_lamp[lit=true]");
        builder.set(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1] + 1, InstitutoLayout.BEACON[2], "lightning_rod[facing=up]");
    }
    
    private static void lamp(final Builder builder, final int n, final int n2) {
        builder.fill(n, 2, n2, n, 6, n2, "iron_bars");
        builder.set(n, 7, n2, "redstone_lamp[lit=true]");
        builder.set(n, 8, n2, "smooth_stone_slab[type=bottom]");
    }
    
    private static void busStop(final Builder builder) {
        builder.fill(33, 2, 142, 33, 4, 149, "light_gray_concrete");
        for (int i = 31; i <= 32; ++i) {
            for (int j = 2; j <= 4; ++j) {
                builder.set(i, j, 142, "light_blue_stained_glass");
                builder.set(i, j, 149, "light_blue_stained_glass");
            }
        }
        builder.fill(30, 5, 141, 34, 5, 150, "smooth_stone_slab[type=bottom]");
        builder.fill(32, 2, 143, 32, 2, 144, "spruce_slab[type=bottom]");
    }
    
    private static void car(final Builder builder) {
        builder.fill(38, 2, 47, 42, 2, 49, "red_concrete");
        for (final int[] array2 : new int[][] { { 38, 47 }, { 42, 47 }, { 38, 49 }, { 42, 49 } }) {
            builder.set(array2[0], 2, array2[1], "coal_block");
        }
        builder.fill(39, 3, 47, 41, 3, 49, "black_stained_glass");
        builder.fill(38, 3, 47, 38, 3, 49, "red_concrete");
        builder.fill(42, 3, 47, 42, 3, 49, "red_concrete");
        builder.fill(39, 4, 47, 41, 4, 49, "red_concrete");
    }
    
    private static void hoop(final Builder builder, final int n, final int n2, final int n3) {
        builder.fill(n, 2, n2, n, 6, n2, "light_gray_concrete");
        builder.fill(n - 1, 6, n3, n + 1, 7, n3, "white_concrete");
        builder.set(n, 5, n3 + ((n3 > n2) ? 1 : -1), "iron_trapdoor[facing=north,half=top,open=false]");
    }
}
