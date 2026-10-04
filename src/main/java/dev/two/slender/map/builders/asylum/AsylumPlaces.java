// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.asylum;

import org.bukkit.Material;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.Builder;

final class AsylumPlaces
{
    static final int[][] BREACHES;
    
    private AsylumPlaces() {
    }
    
    static void grounds(final Builder builder) {
        wall(builder);
        garden(builder);
        fountain(builder, AsylumLayout.place("fuente"));
        greenhouse(builder, AsylumLayout.place("invernadero"));
        ambulance(builder, AsylumLayout.place("ambulancia"));
        gatehouse(builder, AsylumLayout.place("porteria"));
        cemetery(builder, AsylumLayout.place("cementerio"));
    }
    
    static void tower(final Builder builder) {
        final int n = 18;
        final int n2 = 30;
        for (int i = n + 1; i <= n + 9; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    final boolean b = Math.abs(j) == 2 || Math.abs(k) == 2;
                    final boolean b2 = i >= n + 6 && i <= n + 7 && (Math.abs(j) <= 1 || Math.abs(k) <= 1);
                    if (b && !b2) {
                        builder.set(j, i, k, builder.pick("stone_bricks", "stone_bricks", "cracked_stone_bricks"));
                    }
                    else if (!b) {
                        builder.set(j, i, k, "air");
                    }
                }
            }
        }
        builder.set(0, n + 9, 0, "stone_bricks");
        builder.set(0, n + 8, 0, "bell[attachment=ceiling,facing=north]");
        builder.fill(-2, n + 10, -2, 2, n + 10, 2, "deepslate_tiles");
        builder.fill(-1, n + 11, -1, 1, n + 11, 1, "deepslate_tile_slab[type=bottom]");
        builder.set(0, n + 11, 0, "deepslate_tile_wall");
        builder.set(0, n2, 0, "redstone_lamp[lit=true]");
        builder.set(0, n2 + 1, 0, "end_rod[facing=up]");
    }
    
    private static void wall(final Builder builder) {
        final int[] grounds = AsylumLayout.GROUNDS;
        for (int i = grounds[0]; i <= grounds[2]; ++i) {
            wallColumn(builder, i, grounds[1]);
            if (Math.abs(i) > 3) {
                wallColumn(builder, i, grounds[3]);
            }
        }
        for (int j = grounds[1]; j <= grounds[3]; ++j) {
            wallColumn(builder, grounds[0], j);
            wallColumn(builder, grounds[2], j);
        }
        for (final int n : new int[] { -4, 4 }) {
            builder.fill(n, 9, grounds[3], n, 12, grounds[3], "stone_bricks");
            builder.set(n, 13, grounds[3], "lantern[hanging=false]");
        }
        builder.fill(-3, 9, grounds[3], -3, 10, grounds[3], "iron_bars[east=true]");
        builder.fill(3, 9, grounds[3], 3, 10, grounds[3], "iron_bars[west=true]");
    }
    
    private static void wallColumn(final Builder builder, final int n, final int n2) {
        for (final int[] array : AsylumPlaces.BREACHES) {
            if (n >= array[0] && n <= array[2] && n2 >= array[1] && n2 <= array[3]) {
                if (builder.chance(0.6)) {
                    builder.set(n, 9, n2, builder.pick("cobblestone", "mossy_cobblestone", "stone_brick_slab[type=bottom]"));
                }
                return;
            }
        }
        for (int j = 9; j <= 11; ++j) {
            builder.set(n, j, n2, builder.pick("stone_bricks", "stone_bricks", "mossy_stone_bricks", "cracked_stone_bricks", "cobblestone"));
        }
        if (builder.chance(0.3)) {
            builder.set(n, 11, n2, "air");
        }
    }
    
    private static void garden(final Builder builder) {
        for (final int n : new int[] { -1, 1 }) {
            for (int j = 7; j <= 13; ++j) {
                for (int k = 13; k <= 22; ++k) {
                    final int n2 = n * j;
                    if (j == 7 || j == 13 || k == 13 || k == 22) {
                        builder.set(n2, 9, k, "stone_brick_slab[type=bottom]");
                    }
                    else {
                        builder.set(n2, 8, k, builder.pick("coarse_dirt", "rooted_dirt", "podzol"));
                        final double nextDouble = builder.rnd.nextDouble();
                        if (nextDouble < 0.2) {
                            builder.set(n2, 9, k, "dead_bush");
                        }
                        else if (nextDouble < 0.23) {
                            builder.set(n2, 9, k, "wither_rose");
                        }
                    }
                }
            }
            AsylumNature.deadTree(builder, n * 10, 9, 17, "oak_log", 4, 6);
        }
    }
    
    private static void fountain(final Builder builder, final Place place) {
        final int x = place.x();
        final int z = place.z();
        final int n = 8;
        for (int i = -5; i <= 5; ++i) {
            for (int j = -5; j <= 5; ++j) {
                final double hypot = Math.hypot(i, j);
                if (hypot <= 5.5) {
                    builder.set(x + i, n, z + j, builder.pick("gravel", "cobblestone", "gravel", "andesite"));
                }
                if (hypot > 2.5 && hypot <= 3.5) {
                    builder.set(x + i, n + 1, z + j, builder.pick("stone_bricks", "stone_bricks", "cracked_stone_bricks"));
                }
                else if (hypot <= 2.5) {
                    builder.set(x + i, n, z + j, "cobblestone");
                    if (builder.chance(0.15)) {
                        builder.set(x + i, n + 1, z + j, "dead_bush");
                    }
                }
            }
        }
        builder.set(x, n + 1, z, "stone_brick_wall");
        builder.set(x, n + 2, z, "stone_bricks");
        builder.set(x, n + 3, z, "stone_brick_wall");
        builder.set(x, n + 4, z, "chiseled_stone_bricks");
    }
    
    private static void greenhouse(final Builder builder, final Place place) {
        final int n = place.x() - 6;
        final int n2 = place.x() + 6;
        final int n3 = place.z() - 3;
        final int n4 = place.z() + 3;
        final int n5 = 8;
        for (int i = n; i <= n2; ++i) {
            for (int j = n3; j <= n4; ++j) {
                final boolean b = j == n3 || j == n4;
                final boolean b2 = i == n || i == n2;
                builder.set(i, n5, j, builder.pick("coarse_dirt", "mud_bricks", "coarse_dirt"));
                if (!b && !b2) {
                    if (builder.chance(0.8)) {
                        builder.set(i, n5 + 5, j, "glass");
                    }
                }
                else if (b && b2) {
                    builder.fill(i, n5 + 1, j, i, n5 + 5, j, "iron_bars");
                }
                else {
                    builder.set(i, n5 + 1, j, "bricks");
                    final String s = b ? "glass_pane[east=true,west=true]" : "glass_pane[north=true,south=true]";
                    for (int k = 2; k <= 4; ++k) {
                        if (builder.chance(0.55)) {
                            builder.set(i, n5 + k, j, s);
                        }
                    }
                    builder.set(i, n5 + 5, j, "bricks");
                }
            }
        }
        builder.air(place.x() + 2, n5 + 1, n4, place.x() + 2, n5 + 3, n4);
        for (int l = n + 2; l <= n2 - 2; ++l) {
            for (final int n7 : new int[] { n3 + 2, n4 - 2 }) {
                if (l != place.x() + 2) {
                    builder.set(l, n5 + 1, n7, "spruce_slab[type=top]");
                    if (builder.chance(0.5)) {
                        builder.set(l, n5 + 2, n7, builder.pick("potted_dead_bush", "flower_pot", "potted_wither_rose"));
                    }
                }
            }
        }
    }
    
    private static void ambulance(final Builder builder, final Place place) {
        final int x = place.x();
        final int z = place.z();
        final int n = 8;
        builder.fill(x - 3, n + 1, z - 1, x + 3, n + 2, z + 1, "white_concrete");
        builder.fill(x - 1, n + 1, z - 1, x + 1, n + 1, z - 1, "red_concrete");
        builder.fill(x - 1, n + 1, z + 1, x + 1, n + 1, z + 1, "red_concrete");
        builder.fill(x - 3, n + 2, z - 1, x - 3, n + 2, z + 1, "black_stained_glass");
        for (final int[] array2 : new int[][] { { -2, -1 }, { 2, -1 }, { -2, 1 }, { 2, 1 } }) {
            builder.set(x + array2[0], n + 1, z + array2[1], "coal_block");
        }
        builder.fill(x - 2, n + 3, z - 1, x + 3, n + 3, z + 1, "smooth_quartz_slab[type=bottom]");
        final int[] ambulance_LAMP = AsylumLayout.AMBULANCE_LAMP;
        builder.set(x + ambulance_LAMP[0], n + ambulance_LAMP[1], z + ambulance_LAMP[2], "redstone_lamp[lit=true]");
        builder.set(x + 3, n + 1, z, "iron_trapdoor[facing=east,half=bottom,open=true]");
        builder.set(x + 4, n + 1, z, "white_carpet");
    }
    
    private static void gatehouse(final Builder builder, final Place place) {
        final int x = place.x();
        final int z = place.z();
        final int n = 8;
        builder.fill(x - 2, n, z - 2, x + 2, n, z + 2, "stone");
        builder.wallsMixed(x - 2, n + 1, z - 2, x + 2, n + 3, z + 2, Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS);
        builder.air(x - 1, n + 1, z - 1, x + 1, n + 3, z + 1);
        builder.fill(x - 2, n + 4, z - 2, x + 2, n + 4, z + 2, "spruce_slab[type=bottom]");
        builder.air(x - 2, n + 1, z, x - 2, n + 2, z);
        builder.set(x, n + 2, z - 2, "glass_pane[east=true,west=true]");
        builder.set(x, n + 2, z + 2, "air");
        builder.set(x + 1, n + 1, z - 1, "spruce_stairs[facing=south,half=bottom,shape=straight]");
        builder.set(x + 1, n + 1, z + 1, "spruce_slab[type=top]");
        builder.set(x + 1, n + 2, z + 1, "lantern[hanging=false]");
    }
    
    private static void cemetery(final Builder builder, final Place place) {
        final int x = place.x();
        final int z = place.z();
        final int n = 8;
        builder.fill(x - 2, n, z - 2, x + 2, n, z + 2, "polished_andesite");
        builder.walls(x - 2, n + 1, z - 2, x + 2, n + 4, z + 2, Material.POLISHED_ANDESITE);
        builder.air(x - 1, n + 1, z - 1, x + 1, n + 4, z + 1);
        builder.fill(x - 2, n + 5, z - 2, x + 2, n + 5, z + 2, "polished_andesite_slab[type=bottom]");
        builder.set(x, n + 5, z, "chiseled_stone_bricks");
        builder.air(x, n + 1, z + 2, x, n + 2, z + 2);
        builder.fill(x - 1, n + 1, z - 1, x + 1, n + 1, z - 1, "smooth_stone_slab[type=top]");
        builder.set(x, n + 2, z - 1, "candle[candles=2,lit=false]");
        for (final int n2 : new int[] { -12, -9, -6, 6, 9, 12 }) {
            for (final int n3 : new int[] { -3, 0, 3 }) {
                builder.set(x + n2, n + 1, z + n3, builder.pick("andesite_wall", "stone_brick_wall", "mossy_stone_brick_wall", "cobblestone_wall"));
                builder.set(x + n2, n, z + n3 + 1, "coarse_dirt");
            }
        }
        for (int k = -14; k <= 14; ++k) {
            if (Math.abs(k) > 1) {
                builder.set(x + k, n + 1, z + 5, "iron_bars[east=true,west=true]");
            }
        }
        for (int l = -3; l <= 5; ++l) {
            builder.set(x - 14, n + 1, z + l, "iron_bars[north=true,south=true]");
            builder.set(x + 14, n + 1, z + l, "iron_bars[north=true,south=true]");
        }
    }
    
    static {
        BREACHES = new int[][] { { -46, -12, -46, -9 }, { 46, 4, 46, 7 }, { 19, -44, 22, -44 }, { -30, 30, -27, 30 } };
    }
}
