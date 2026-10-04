// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.forest;

import dev.two.slender.map.gen.Place;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;
import org.bukkit.Material;

final class ForestPlaces
{
    private static final int[][] CORNERS;
    private static final Material[] RUIN;
    
    private ForestPlaces() {
    }
    
    static void build(final Builder builder, final Heightmap heightmap) {
        radioTower(builder, spot(heightmap, "torre"));
        cabin(builder, spot(heightmap, "cabana"));
        camp(builder, spot(heightmap, "campamento"));
        chapel(builder, spot(heightmap, "capilla"));
        stones(builder, spot(heightmap, "piedras"));
        caravan(builder, spot(heightmap, "caravana"));
        huntingStand(builder, spot(heightmap, "caza"));
        shelter(builder, spot(heightmap, "refugio"));
        crash(builder, spot(heightmap, "coche"));
        well(builder, spot(heightmap, "pozo"));
        tunnel(builder, spot(heightmap, "tunel"));
        dock(builder, spot(heightmap, "embarcadero"), heightmap.waterLevel());
    }
    
    private static Spot spot(final Heightmap heightmap, final String s) {
        final Place place = ForestLayout.place(s);
        return new Spot(place.x(), heightmap.at(place.x(), place.z()), place.z());
    }
    
    private static void radioTower(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        final int n = y + 31;
        builder.fill(x - 2, y, z - 2, x + 2, y, z + 2, Material.POLISHED_ANDESITE);
        for (int i = y + 1; i < n; ++i) {
            final Material material = ((i - y) / 5 % 2 == 0) ? Material.WHITE_CONCRETE : Material.RED_CONCRETE;
            for (final int[] array : ForestPlaces.CORNERS) {
                builder.set(x + array[0], i, z + array[1], material);
            }
            if ((i - y) % 4 == 0) {
                builder.set(x, i, z - 1, "iron_bars[east=true,west=true]");
                builder.set(x, i, z + 1, "iron_bars[east=true,west=true]");
                builder.set(x - 1, i, z, "iron_bars[north=true,south=true]");
                builder.set(x + 1, i, z, "iron_bars[north=true,south=true]");
            }
        }
        builder.fill(x - 1, n - 1, z - 1, x + 1, n - 1, z + 1, "smooth_stone_slab[type=top]");
        builder.set(x, n, z, "redstone_lamp[lit=true]");
        builder.set(x, n + 1, z, "lightning_rod");
        builder.set(x, n + 2, z, "end_rod[facing=up]");
        builder.room(x + 2, y, z - 2, x + 6, y + 4, z + 2, Material.ANDESITE, Material.SMOOTH_STONE, Material.SMOOTH_STONE);
        builder.wallsMixed(x + 2, y + 1, z - 2, x + 6, y + 3, z + 2, Material.ANDESITE, Material.ANDESITE, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS);
        builder.air(x + 2, y + 1, z, x + 2, y + 2, z);
        builder.set(x + 4, y + 2, z + 2, "glass_pane[east=true,west=true]");
        builder.fill(x + 3, y + 1, z - 1, x + 5, y + 1, z - 1, "dark_oak_slab[type=top]");
        builder.set(x + 3, y + 2, z - 1, Material.JUKEBOX);
        builder.set(x + 5, y + 2, z - 1, Material.NOTE_BLOCK);
        builder.stairs(x + 4, y + 1, z + 1, "spruce_stairs", "north");
        builder.set(x + 4, y + 3, z, "lantern[hanging=true]");
    }
    
    private static void cabin(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        builder.room(x - 4, y, z - 3, x + 4, y + 4, z + 3, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS);
        for (final int[] array2 : new int[][] { { -4, -3 }, { 4, -3 }, { -4, 3 }, { 4, 3 } }) {
            builder.fill(x + array2[0], y + 1, z + array2[1], x + array2[0], y + 3, z + array2[1], "spruce_log");
        }
        for (int j = 0; j <= 3; ++j) {
            for (int k = x - 5; k <= x + 5; ++k) {
                builder.set(k, y + 4 + j, z + 4 - j, "dark_oak_stairs[facing=north,half=bottom,shape=straight]");
                builder.set(k, y + 4 + j, z - 4 + j, "dark_oak_stairs[facing=south,half=bottom,shape=straight]");
            }
        }
        builder.fill(x - 5, y + 8, z, x + 5, y + 8, z, "dark_oak_slab[type=bottom]");
        for (int l = 1; l <= 3; ++l) {
            for (int n = z - 3 + l; n <= z + 3 - l; ++n) {
                builder.set(x - 4, y + 4 + l, n, Material.SPRUCE_PLANKS);
                builder.set(x + 4, y + 4 + l, n, Material.SPRUCE_PLANKS);
            }
        }
        final int[] cabin_LAMP = ForestLayout.CABIN_LAMP;
        builder.set(x + cabin_LAMP[0], y + cabin_LAMP[1], z + cabin_LAMP[2], "redstone_lamp[lit=true]");
        builder.fill(x, y + 1, z - 2, x, y + 3, z + 2, Material.SPRUCE_PLANKS);
        builder.air(x, y + 1, z, x, y + 2, z);
        builder.set(x - 3, y + 1, z - 2, "red_bed[part=head,facing=north]");
        builder.set(x - 3, y + 1, z - 1, "red_bed[part=foot,facing=north]");
        builder.set(x - 1, y + 1, z - 2, "chest[facing=south]");
        builder.set(x + 2, y + 1, z + 1, "spruce_fence");
        builder.set(x + 2, y + 2, z + 1, "spruce_pressure_plate");
        builder.stairs(x + 3, y + 1, z + 1, "spruce_stairs", "west");
        builder.stairs(x + 1, y + 1, z + 1, "spruce_stairs", "east");
        builder.set(x + 3, y + 1, z - 2, "furnace[facing=south]");
        builder.set(x + 1, y + 1, z - 2, "barrel[facing=up]");
        builder.door(x - 2, y + 1, z + 3, "spruce_door", "south");
        builder.set(x + 2, y + 2, z + 3, "glass_pane[east=true,west=true]");
        builder.set(x - 4, y + 2, z, "glass_pane[north=true,south=true]");
        builder.set(x + 4, y + 2, z - 1, "glass_pane[north=true,south=true]");
        builder.fill(x - 3, y, z + 4, x + 3, y, z + 5, Material.SPRUCE_PLANKS);
        for (final int n3 : new int[] { x - 3, x + 3 }) {
            builder.fill(n3, y + 1, z + 5, n3, y + 3, z + 5, "spruce_fence");
        }
        builder.fill(x + 5, y + 1, z - 1, x + 5, y + 9, z - 1, Material.COBBLESTONE);
    }
    
    private static void camp(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                builder.set(x + i, y, z + j, Material.COBBLESTONE);
            }
        }
        builder.set(x, y, z, Material.COARSE_DIRT);
        builder.set(x, y + 1, z, "campfire[lit=false]");
        builder.set(x - 2, y + 1, z + 2, "spruce_log[axis=x]");
        builder.set(x - 1, y + 1, z + 2, "spruce_log[axis=x]");
        builder.set(x + 2, y + 1, z - 1, "spruce_log[axis=z]");
        tent(builder, x - 4, y, z - 4, Material.WHITE_WOOL);
        tent(builder, x + 2, y, z - 5, Material.LIGHT_GRAY_WOOL);
        for (int k = x + 3; k <= x + 5; ++k) {
            builder.set(k, y + 1, z + 3, "brown_carpet");
        }
        builder.set(x - 5, y + 1, z + 3, "barrel[facing=up]");
        builder.set(x - 1, y + 1, z - 3, "white_carpet");
    }
    
    private static void tent(final Builder builder, final int n, final int n2, final int n3, final Material material) {
        for (int i = n; i <= n + 2; ++i) {
            builder.set(i, n2 + 1, n3, material);
            builder.set(i, n2 + 1, n3 + 2, material);
            builder.set(i, n2 + 2, n3 + 1, material);
        }
    }
    
    private static void chapel(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        builder.fill(x - 3, y, z - 6, x + 3, y, z + 4, Material.STONE_BRICKS);
        builder.air(x - 2, y + 1, z - 5, x + 2, y + 7, z + 3);
        for (int i = y + 1; i <= y + 6; ++i) {
            final int n = i - y;
            for (int j = z - 6; j <= z + 4; ++j) {
                ruin(builder, x - 3, i, j, n);
                ruin(builder, x + 3, i, j, n);
            }
            for (int k = x - 2; k <= x + 2; ++k) {
                ruin(builder, k, i, z - 6, n);
                if (n <= 4) {
                    ruin(builder, k, i, z + 4, n);
                }
            }
        }
        for (final int n2 : new int[] { z - 4, z - 1, z + 2 }) {
            for (int n3 = y + 3; n3 <= y + 4; ++n3) {
                builder.set(x - 3, n3, n2, Material.AIR);
                builder.set(x + 3, n3, n2, Material.AIR);
            }
        }
        for (int n4 = z - 6; n4 <= z - 3; ++n4) {
            builder.fill(x - 3, y + 7, n4, x + 3, y + 7, n4, "stone_brick_slab[type=bottom]");
        }
        builder.walls(x - 1, y + 1, z + 4, x + 1, y + 9, z + 6, Material.STONE_BRICKS);
        builder.air(x, y + 1, z + 4, x, y + 2, z + 6);
        builder.fill(x - 1, y + 10, z + 4, x + 1, y + 10, z + 6, "stone_brick_slab[type=bottom]");
        builder.set(x, y + 9, z + 5, "bell[attachment=ceiling,facing=north]");
        builder.set(x, y + 1, z - 5, Material.POLISHED_ANDESITE);
        builder.set(x - 1, y + 1, z - 5, "candle[candles=3,lit=false]");
        builder.set(x + 1, y + 1, z - 5, "candle[candles=2,lit=false]");
        for (final int n6 : new int[] { z - 2, z, z + 2 }) {
            final int[] array3 = { x - 2, x - 1, x + 1, x + 2 };
            for (int length3 = array3.length, n7 = 0; n7 < length3; ++n7) {
                builder.stairs(array3[n7], y + 1, n6, "spruce_stairs", "south");
            }
        }
        for (int n8 = x - 7; n8 <= x - 5; n8 += 2) {
            for (int n9 = z - 5; n9 <= z + 3; n9 += 3) {
                builder.set(n8, y + 1, n9, builder.pick("mossy_stone_brick_wall", "stone_brick_wall", "cobblestone_wall", "andesite_wall"));
                builder.set(n8, y, n9 + 1, Material.COARSE_DIRT);
            }
        }
        for (int n10 = z - 6; n10 <= z + 4; ++n10) {
            if (builder.chance(0.7)) {
                builder.set(x - 8, y + 1, n10, "iron_bars[north=true,south=true]");
            }
        }
    }
    
    private static void ruin(final Builder builder, final int n, final int n2, final int n3, final int n4) {
        if (n4 <= 2 || builder.chance(0.9 - n4 * 0.1)) {
            builder.set(n, n2, n3, builder.pick(ForestPlaces.RUIN));
        }
    }
    
    private static void stones(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = 0; i < 8; ++i) {
            final double n = i * 3.141592653589793 / 4.0;
            final int n2 = x + (int)Math.round(Math.sin(n) * 5.0);
            final int n3 = z - (int)Math.round(Math.cos(n) * 5.0);
            for (int n4 = (i % 2 == 0) ? 3 : 2, j = y + 1; j <= y + n4; ++j) {
                builder.set(n2, j, n3, builder.pick(Material.MOSSY_COBBLESTONE, Material.COBBLESTONE, Material.ANDESITE, Material.TUFF));
            }
        }
        builder.set(x, y + 1, z, Material.SMOOTH_STONE);
        builder.set(x + 1, y + 1, z, "smooth_stone_slab[type=bottom]");
        builder.set(x, y + 2, z, "candle[candles=4,lit=false]");
    }
    
    private static void caravan(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (final int[] array2 : new int[][] { { -3, -1 }, { 3, -1 }, { -3, 2 }, { 3, 2 } }) {
            builder.set(x + array2[0], y + 1, z + array2[1], Material.POLISHED_BLACKSTONE);
        }
        builder.fill(x - 4, y + 2, z - 1, x + 4, y + 2, z + 2, Material.SPRUCE_PLANKS);
        builder.walls(x - 4, y + 3, z - 1, x + 4, y + 4, z + 2, Material.WHITE_TERRACOTTA);
        builder.fill(x - 4, y + 5, z - 1, x + 4, y + 5, z + 2, "smooth_stone_slab[type=bottom]");
        builder.air(x + 2, y + 3, z + 2, x + 2, y + 4, z + 2);
        builder.stairs(x + 2, y + 1, z + 3, "spruce_stairs", "north");
        builder.set(x - 1, y + 4, z + 2, "glass_pane[east=true,west=true]");
        builder.set(x + 4, y + 4, z, "glass_pane[north=true,south=true]");
        builder.set(x - 3, y + 3, z, "red_bed[part=head,facing=west]");
        builder.set(x - 2, y + 3, z, "red_bed[part=foot,facing=west]");
        builder.set(x + 1, y + 3, z, "spruce_fence");
        builder.set(x + 1, y + 4, z, "spruce_pressure_plate");
    }
    
    private static void huntingStand(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (final int[] array : ForestPlaces.CORNERS) {
            builder.fill(x + array[0], y + 1, z + array[1], x + array[0], y + 8, z + array[1], "spruce_log");
        }
        builder.fill(x, y + 1, z + 1, x, y + 5, z + 1, Material.SPRUCE_PLANKS);
        for (final int[] array3 : new int[][] { { 0, -1 }, { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }) {
            builder.set(x + array3[0], y + 6, z + array3[1], Material.SPRUCE_PLANKS);
        }
        builder.set(x, y + 7, z - 1, "spruce_fence");
        builder.set(x - 1, y + 7, z, "spruce_fence");
        builder.set(x + 1, y + 7, z, "spruce_fence");
        builder.fill(x - 1, y + 9, z - 1, x + 1, y + 9, z + 1, "spruce_slab[type=bottom]");
        for (int k = y + 1; k <= y + 7; ++k) {
            builder.set(x, k, z + 2, "ladder[facing=south]");
        }
    }
    
    private static void shelter(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        builder.fill(x - 3, y, z - 2, x + 3, y, z + 2, Material.COBBLESTONE);
        for (int i = y + 1; i <= y + 3; ++i) {
            for (int j = x - 3; j <= x + 3; ++j) {
                for (int k = z - 2; k <= z + 2; ++k) {
                    if ((j == x - 3 || j == x + 3 || k == z - 2 || k == z + 2) && (i - y <= 2 || builder.chance(0.6))) {
                        builder.set(j, i, k, builder.pick(Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE));
                    }
                }
            }
        }
        builder.air(x - 3, y + 1, z, x - 3, y + 2, z);
        for (int l = x - 3; l <= x + 3; l += 2) {
            if (builder.chance(0.6)) {
                builder.fill(l, y + 4, z - 2, l, y + 4, z + 2, "spruce_log[axis=z]");
            }
        }
        builder.set(x + 1, y + 1, z - 1, "cobblestone_slab[type=bottom]");
        builder.set(x - 2, y + 3, z - 1, Material.COBWEB);
        builder.set(x + 2, y + 3, z + 1, Material.COBWEB);
        builder.set(x - 1, y + 1, z - 1, "campfire[lit=false]");
    }
    
    private static void crash(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        builder.fill(x + 3, y + 1, z, x + 3, y + 8, z, "oak_log");
        for (int i = -2; i <= 2; ++i) {
            for (int j = -1; j <= 2; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    if (i * i + j * j + k * k <= 6 && builder.chance(0.8) && (i != 0 || k != 0 || j > 0)) {
                        builder.set(x + 3 + i, y + 8 + j, z + k, "oak_leaves[persistent=true]");
                    }
                }
            }
        }
        builder.fill(x - 2, y + 1, z - 1, x + 2, y + 1, z + 1, Material.RED_CONCRETE);
        builder.fill(x - 1, y + 2, z - 1, x + 1, y + 2, z + 1, Material.BLACK_STAINED_GLASS);
        builder.fill(x - 1, y + 3, z - 1, x + 1, y + 3, z + 1, Material.RED_CONCRETE);
        final int[] car_LAMP = ForestLayout.CAR_LAMP;
        builder.set(x + car_LAMP[0], y + car_LAMP[1], z + car_LAMP[2], "redstone_lamp[lit=true]");
        builder.set(x + 2, y + 1, z + 1, "redstone_lamp[lit=false]");
        builder.set(x, y + 2, z + 1, Material.AIR);
        builder.set(x - 2, y + 1, z + 1, Material.BLACK_CONCRETE);
    }
    
    private static void well(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i != 0 || j != 0) {
                    builder.set(x + i, y + 1, z + j, Material.COBBLESTONE);
                }
            }
        }
        for (int k = y - 2; k <= y; ++k) {
            builder.set(x, k, z, Material.WATER);
        }
        for (final int[] array : ForestPlaces.CORNERS) {
            builder.fill(x + array[0], y + 2, z + array[1], x + array[0], y + 3, z + array[1], "spruce_fence");
        }
        builder.fill(x - 1, y + 4, z - 1, x + 1, y + 4, z + 1, "spruce_slab[type=bottom]");
        builder.fill(x + 3, y + 1, z, x + 3, y + 2, z, Material.SPRUCE_PLANKS);
    }
    
    private static void tunnel(final Builder builder, final Spot spot) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        for (int i = -6; i <= -2; ++i) {
            for (int j = -5; j <= 5; ++j) {
                final int n = y + 8 - Math.max(0, Math.abs(j) - 2);
                for (int k = y - 1; k < n; ++k) {
                    builder.set(x + i, k, z + j, Material.DIRT);
                }
                builder.set(x + i, n, z + j, Material.GRASS_BLOCK);
            }
        }
        for (int l = -4; l <= 4; ++l) {
            for (int n2 = y + 1; n2 <= y + 8; ++n2) {
                builder.set(x - 1, n2, z + l, builder.pick(ForestPlaces.RUIN));
            }
        }
        for (int a = -2; a <= 2; ++a) {
            for (int n3 = y + 1; n3 <= y + 5; ++n3) {
                if (n3 != y + 5 || Math.abs(a) != 2) {
                    builder.set(x - 1, n3, z + a, builder.chance(0.75) ? Material.SPRUCE_PLANKS : Material.BLACK_CONCRETE);
                    builder.set(x - 2, n3, z + a, Material.BLACK_CONCRETE);
                }
            }
        }
        for (int n4 = 0; n4 <= 6; ++n4) {
            builder.set(x + n4, y, z, Material.GRAVEL);
            builder.set(x + n4, y + 1, z, "rail[shape=east_west]");
        }
    }
    
    private static void dock(final Builder builder, final Spot spot, final int n) {
        final int x = spot.x();
        final int y = spot.y();
        final int z = spot.z();
        builder.room(x - 3, y, z - 1, x, y + 4, z + 1, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS);
        builder.air(x - 1, y + 1, z - 1, x - 1, y + 2, z - 1);
        for (int i = 4; i <= 13; ++i) {
            for (int j = -1; j <= 1; ++j) {
                builder.set(x + i, n + 1, z + j, Material.SPRUCE_PLANKS);
            }
        }
        for (int k = 4; k <= 13; k += 3) {
            for (final int n2 : new int[] { -1, 1 }) {
                for (int n3 = n - 3; n3 <= n; ++n3) {
                    builder.set(x + k, n3, z + n2, "spruce_log");
                }
            }
        }
        for (int n4 = 15; n4 <= 18; ++n4) {
            builder.set(x + n4, n, z + 2, "spruce_slab[type=bottom,waterlogged=true]");
            builder.set(x + n4, n, z + 4, "spruce_slab[type=bottom,waterlogged=true]");
        }
        builder.set(x + 15, n, z + 3, "spruce_slab[type=bottom,waterlogged=true]");
        builder.set(x + 18, n, z + 3, "spruce_slab[type=bottom,waterlogged=true]");
    }
    
    static {
        CORNERS = new int[][] { { -1, -1 }, { 1, -1 }, { -1, 1 }, { 1, 1 } };
        RUIN = new Material[] { Material.STONE_BRICKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CRACKED_STONE_BRICKS };
    }
    
    record Spot(int x, int y, int z) {}
}
