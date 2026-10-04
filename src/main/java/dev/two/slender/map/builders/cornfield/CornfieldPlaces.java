// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.cornfield;

import java.util.Iterator;
import java.util.List;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Place;
import dev.two.slender.logic.Heightmap;

final class CornfieldPlaces
{
    private CornfieldPlaces() {
    }
    
    private static Spot spot(final Heightmap heightmap, final String s) {
        final Place place = CornfieldLayout.place(s);
        return new Spot(place.x(), heightmap.at(place.x(), place.z()), place.z());
    }
    
    static void build(final Builder builder, final Heightmap heightmap, final List<int[]> list) {
        silo(builder, spot(heightmap, "silo"));
        mill(builder, spot(heightmap, "molino"));
        waterTower(builder, spot(heightmap, "deposito"));
        cropCircle(builder, spot(heightmap, "circulo"));
        combine(builder, spot(heightmap, "cosechadora"));
        pumpkins(builder, spot(heightmap, "calabazas"));
        coop(builder, spot(heightmap, "gallinero"));
        well(builder, spot(heightmap, "pozo"));
        bridge(builder, spot(heightmap, "puente"));
        outhouse(builder, spot(heightmap, "letrina"));
        for (final Place place : CornfieldLayout.SCARECROWS) {
            scarecrow(builder, place.x(), heightmap.at(place.x(), place.z()), place.z(), true);
        }
        for (final int[] array : list) {
            pole(builder, array[0], heightmap.at(array[0], array[1]), array[1]);
        }
    }
    
    static void roofs(final Builder builder, final Heightmap heightmap) {
        final Spot spot = spot(heightmap, "casa");
        final Spot spot2 = spot(heightmap, "granero");
        porch(builder, spot.y());
        houseRoof(builder, spot.y());
        barnRoof(builder, spot2.y());
    }
    
    private static void silo(final Builder builder, final Spot spot) {
        for (int i = -5; i <= 5; ++i) {
            for (int j = -5; j <= 5; ++j) {
                final double hypot = Math.hypot(i, j);
                final int n = spot.x() + i;
                final int n2 = spot.z() + j;
                if (hypot <= 3.5) {
                    builder.set(n, spot.y(), n2, "smooth_stone");
                }
                if (hypot > 3.5 && hypot <= 4.5) {
                    for (int k = 1; k <= 14; ++k) {
                        builder.set(n, spot.y() + k, n2, builder.pick("light_gray_terracotta", "light_gray_terracotta", "light_gray_terracotta", "white_terracotta"));
                    }
                }
                if (hypot <= 4.5) {
                    builder.set(n, spot.y() + 15, n2, "polished_andesite");
                }
                if (hypot <= 3.0) {
                    builder.set(n, spot.y() + 16, n2, "polished_andesite");
                }
            }
        }
        builder.air(spot.x(), spot.y() + 1, spot.z() + 4, spot.x(), spot.y() + 2, spot.z() + 4);
        builder.fill(spot.x(), spot.y() + 9, spot.z(), spot.x(), spot.y() + 14, spot.z(), "iron_chain[axis=y]");
        builder.set(spot.x(), spot.y() + 17, spot.z(), "lightning_rod[facing=up]");
        builder.fill(spot.x() - 2, spot.y() + 1, spot.z() + 2, spot.x() - 1, spot.y() + 1, spot.z() + 2, "hay_block[axis=y]");
    }
    
    private static void mill(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                builder.set(x + i, y, z + j, "cobblestone");
                final boolean b = Math.abs(i) == 2 || Math.abs(j) == 2;
                for (int k = 1; k <= 14; ++k) {
                    if (b) {
                        builder.set(x + i, y + k, z + j, builder.pick("cobblestone", "cobblestone", "mossy_cobblestone", "stone_bricks"));
                    }
                    else {
                        builder.set(x + i, y + k, z + j, "air");
                    }
                }
                builder.set(x + i, y + 15, z + j, "dark_oak_planks");
            }
        }
        for (int l = -3; l <= 3; ++l) {
            for (int a = -3; a <= 3; ++a) {
                if (Math.abs(l) == 3 || Math.abs(a) == 3) {
                    builder.set(x + l, y + 15, z + a, "dark_oak_slab[type=bottom]");
                }
            }
        }
        builder.air(x + 2, y + 1, z, x + 2, y + 2, z);
        builder.fill(x, y + 9, z, x, y + 14, z, "iron_chain[axis=y]");
        builder.set(x, y + 16, z, "redstone_lamp[lit=true]");
        for (final int[] array2 : new int[][] { { -1, -1 }, { 1, -1 }, { -1, 1 }, { 1, 1 } }) {
            builder.set(x + array2[0], y + 16, z + array2[1], "dark_oak_fence");
        }
        builder.fill(x - 1, y + 17, z - 1, x + 1, y + 17, z + 1, "dark_oak_slab[type=bottom]");
        final int n2 = x + 3;
        final int n3 = y + 11;
        builder.set(n2, n3, z, "oak_log[axis=x]");
        for (int n4 = 1; n4 <= 6; ++n4) {
            builder.set(n2, n3 + n4, z, "stripped_spruce_log[axis=y]");
            builder.set(n2, n3 + n4, z + 1, "white_wool");
            builder.set(n2, n3 - n4, z, "stripped_spruce_log[axis=y]");
            builder.set(n2, n3 - n4, z - 1, "white_wool");
            builder.set(n2, n3, z + n4, "stripped_spruce_log[axis=z]");
            builder.set(n2, n3 - 1, z + n4, "white_wool");
            builder.set(n2, n3, z - n4, "stripped_spruce_log[axis=z]");
            builder.set(n2, n3 + 1, z - n4, "white_wool");
        }
    }
    
    private static void waterTower(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (final int[] array2 : new int[][] { { -2, -2 }, { 2, -2 }, { -2, 2 }, { 2, 2 } }) {
            builder.fill(x + array2[0], y + 1, z + array2[1], x + array2[0], y + 10, z + array2[1], "spruce_log");
        }
        for (int j = -1; j <= 1; ++j) {
            builder.set(x + j, y + 6, z - 2, "spruce_fence[east=true,west=true]");
            builder.set(x + j, y + 6, z + 2, "spruce_fence[east=true,west=true]");
            builder.set(x - 2, y + 6, z + j, "spruce_fence[north=true,south=true]");
            builder.set(x + 2, y + 6, z + j, "spruce_fence[north=true,south=true]");
        }
        builder.fill(x - 3, y + 11, z - 3, x + 3, y + 11, z + 3, "spruce_planks");
        for (int k = -3; k <= 3; ++k) {
            for (int l = -3; l <= 3; ++l) {
                final double hypot = Math.hypot(k, l);
                if (hypot <= 3.3) {
                    if (hypot > 2.3) {
                        for (int n = 12; n <= 15; ++n) {
                            builder.set(x + k, y + n, z + l, (n == 13) ? "dark_oak_planks" : "spruce_planks");
                        }
                    }
                    builder.set(x + k, y + 16, z + l, "spruce_slab[type=bottom]");
                }
            }
        }
    }
    
    private static void cropCircle(final Builder builder, final Spot spot) {
        for (int pad = CornfieldLayout.place("circulo").pad(), i = -pad; i <= pad; ++i) {
            for (int j = -pad; j <= pad; ++j) {
                final double hypot = Math.hypot(i, j);
                if (hypot <= pad + 0.5) {
                    final int n = spot.x() + i;
                    final int n2 = spot.z() + j;
                    final boolean b = Math.abs(i) <= 1 || Math.abs(j) <= 1;
                    builder.set(n, spot.y(), n2, (hypot <= 2.5) ? "soul_soil" : "coarse_dirt");
                    if (((hypot > 2.5 && hypot <= 3.5) || (hypot > 5.5 && hypot <= 6.5)) && !b) {
                        builder.set(n, spot.y() + 1, n2, "bamboo_block[axis=y]");
                        builder.set(n, spot.y() + 2, n2, "bamboo_block[axis=y]");
                        builder.set(n, spot.y() + 3, n2, "bamboo[age=1,leaves=large,stage=0]");
                    }
                    else if (hypot > 3.5 && hypot <= 5.5) {
                        builder.set(n, spot.y(), n2, "stripped_bamboo_block[axis=x]");
                    }
                }
            }
        }
        builder.fill(spot.x(), spot.y() + 1, spot.z(), spot.x(), spot.y() + 3, spot.z(), "polished_blackstone");
        builder.set(spot.x(), spot.y() + 4, spot.z(), "polished_blackstone_wall");
    }
    
    private static void combine(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        builder.fill(x - 2, y + 1, z - 1, x + 2, y + 3, z + 1, "yellow_concrete");
        builder.fill(x - 1, y + 4, z - 1, x, y + 4, z + 1, "black_stained_glass");
        builder.fill(x - 1, y + 5, z - 1, x, y + 5, z + 1, "yellow_concrete");
        for (int i = -3; i <= 3; ++i) {
            builder.set(x - 3, y + 1, z + i, "iron_bars[north=true,south=true]");
            builder.set(x - 4, y + 2, z + i, "stripped_oak_log[axis=z]");
        }
        for (final int[] array2 : new int[][] { { -2, -2 }, { 2, -2 }, { -2, 2 }, { 2, 2 } }) {
            builder.set(x + array2[0], y + 1, z + array2[1], "coal_block");
        }
        builder.set(x + 2, y + 4, z, "iron_bars");
    }
    
    private static void pumpkins(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -5; i <= 5; ++i) {
            for (int j = -2; j <= 5; ++j) {
                if (Math.hypot(i, j) <= 5.8) {
                    builder.set(x + i, y, z + j, builder.pick("coarse_dirt", "rooted_dirt", "dirt"));
                    if (Math.floorMod(j, 2) == 0 && (Math.abs(i) != 4 || j != 0)) {
                        if (i != 4 || j != 2) {
                            if (builder.chance(0.55)) {
                                builder.set(x + i, y + 1, z + j, builder.pick("pumpkin", "pumpkin", "carved_pumpkin[facing=south]", "carved_pumpkin[facing=east]", "jack_o_lantern[facing=south]"));
                            }
                        }
                    }
                }
            }
        }
        builder.fill(x - 2, y + 1, z - 6, x + 2, y + 2, z - 6, "spruce_planks");
        builder.fill(x - 2, y + 3, z - 6, x + 2, y + 3, z - 6, "spruce_slab[type=bottom]");
        scarecrow(builder, x + 4, y, z + 1, false);
    }
    
    private static void coop(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                final boolean b = Math.abs(i) == 2 || Math.abs(j) == 2;
                builder.set(x + i, y, z + j, b ? "oak_planks" : "hay_block[axis=y]");
                for (int k = 1; k <= 3; ++k) {
                    builder.set(x + i, y + k, z + j, b ? "oak_planks" : "air");
                }
            }
        }
        builder.fill(x - 3, y + 4, z - 3, x + 3, y + 4, z + 3, "oak_slab[type=bottom]");
        builder.air(x, y + 1, z + 2, x, y + 2, z + 2);
        builder.set(x - 2, y + 2, z, "glass_pane[north=true,south=true]");
        builder.set(x - 1, y + 1, z - 1, "composter");
        builder.set(x + 1, y + 1, z - 1, "composter");
    }
    
    private static void well(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) {
                    builder.set(x, y, z, "water");
                    builder.set(x, y - 1, z, "water");
                }
                else {
                    builder.set(x + i, y + 1, z + j, "cobblestone");
                }
            }
        }
        builder.fill(x - 2, y + 1, z, x - 2, y + 3, z, "spruce_log");
        builder.fill(x + 2, y + 1, z, x + 2, y + 3, z, "spruce_log");
        builder.fill(x - 2, y + 4, z - 1, x + 2, y + 4, z + 1, "spruce_slab[type=bottom]");
        builder.set(x, y + 3, z, "iron_chain[axis=y]");
    }
    
    private static void bridge(final Builder builder, final Spot spot) {
        for (final int[] array2 : new int[][] { { -2, 48 }, { 2, 48 }, { -2, 52 }, { 2, 52 } }) {
            builder.fill(array2[0], spot.y() + 1, array2[1], array2[0], spot.y() + 2, array2[1], "spruce_log");
            builder.set(array2[0], spot.y() + 3, array2[1], "lantern[hanging=false]");
        }
    }
    
    private static void outhouse(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                builder.set(x + i, y, z + j, "spruce_planks");
                final boolean b = i != 0 || j != 0;
                for (int k = 1; k <= 3; ++k) {
                    builder.set(x + i, y + k, z + j, b ? "spruce_planks" : "air");
                }
            }
        }
        builder.fill(x - 2, y + 4, z - 2, x + 2, y + 4, z + 2, "spruce_slab[type=bottom]");
        builder.door(x, y + 1, z + 1, "spruce_door", "south");
    }
    
    private static void scarecrow(final Builder builder, final int n, final int n2, final int n3, final boolean b) {
        final int n4 = b ? 3 : 1;
        builder.fill(n, n2 + 1, n3, n, n2 + n4, n3, "oak_fence");
        builder.set(n, n2 + n4 + 1, n3, "hay_block[axis=y]");
        builder.set(n - 1, n2 + n4 + 1, n3, "oak_fence[east=true,west=true]");
        builder.set(n + 1, n2 + n4 + 1, n3, "oak_fence[east=true,west=true]");
        builder.set(n, n2 + n4 + 2, n3, "carved_pumpkin[facing=south]");
        if (b) {
            builder.set(n, n2 + n4 + 3, n3, "lantern[hanging=false]");
        }
    }
    
    private static void pole(final Builder builder, final int n, final int n2, final int n3) {
        builder.fill(n, n2 + 1, n3, n, n2 + 6, n3, "spruce_log");
        builder.set(n - 1, n2 + 6, n3, "spruce_fence[east=true,west=true]");
        builder.set(n + 1, n2 + 6, n3, "spruce_fence[east=true,west=true]");
        builder.set(n, n2 + 7, n3, "redstone_lamp[lit=true]");
        builder.set(n, n2 + 8, n3, "spruce_slab[type=bottom]");
    }
    
    private static void porch(final Builder builder, final int n) {
        builder.fill(20, n, -56, 36, n, -54, "spruce_planks");
        for (final int n2 : new int[] { 20, 24, 32, 36 }) {
            builder.fill(n2, n + 1, -54, n2, n + 3, -54, "dark_oak_log[axis=y]");
        }
        for (int j = 21; j <= 35; ++j) {
            if (j != 24 && j != 32) {
                if (j < 27 || j > 29) {
                    builder.set(j, n + 1, -54, "oak_fence[east=true,west=true]");
                }
            }
        }
        builder.fill(19, n + 4, -56, 37, n + 4, -53, "spruce_slab[type=bottom]");
        builder.set(26, n + 3, -55, "lantern[hanging=true]");
        builder.set(34, n + 1, -56, "spruce_stairs[facing=south,half=bottom,shape=straight]");
    }
    
    private static void houseRoof(final Builder builder, final int n) {
        for (int i = 0; i <= 5; ++i) {
            final int n2 = -68 + i;
            final int n3 = -56 - i;
            final int n4 = n + 8 + i;
            for (int j = 19; j <= 37; ++j) {
                builder.set(j, n4, n2, "spruce_stairs[facing=south,half=bottom,shape=straight]");
                builder.set(j, n4, n3, "spruce_stairs[facing=north,half=bottom,shape=straight]");
            }
            if (i > 0) {
                for (int k = n2 + 1; k <= n3 - 1; ++k) {
                    builder.set(20, n4, k, "spruce_planks");
                    builder.set(36, n4, k, "spruce_planks");
                }
            }
        }
        builder.fill(19, n + 14, -62, 37, n + 14, -62, "spruce_slab[type=bottom]");
        builder.fill(30, n + 9, -60, 30, n + 13, -60, "bricks");
    }
    
    private static void barnRoof(final Builder builder, final int n) {
        for (int i = 0; i <= 7; ++i) {
            final int n2 = -34 + i;
            final int n3 = -18 - i;
            final int n4 = n + 10 + i;
            for (int j = -70; j <= -50; ++j) {
                builder.set(n2, n4, j, "dark_oak_stairs[facing=east,half=bottom,shape=straight]");
                builder.set(n3, n4, j, "dark_oak_stairs[facing=west,half=bottom,shape=straight]");
            }
            if (i > 0) {
                for (int k = n2 + 1; k <= n3 - 1; ++k) {
                    builder.set(k, n4, -69, "red_terracotta");
                    builder.set(k, n4, -51, "red_terracotta");
                }
            }
        }
        builder.fill(-26, n + 18, -70, -26, n + 18, -50, "dark_oak_slab[type=bottom]");
        builder.set(-26, n + 13, -51, "white_terracotta");
    }
    
    record Spot(int x, int y, int z) {}
}
