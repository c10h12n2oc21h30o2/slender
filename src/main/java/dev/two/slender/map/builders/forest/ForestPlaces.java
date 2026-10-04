package dev.two.slender.map.builders.forest;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Place;
import org.bukkit.Material;

final class ForestPlaces {
    private static final int[][] CORNERS = new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
    private static final Material[] RUIN;

    private ForestPlaces() {
    }

    static void build(Builder var0, Heightmap var1) {
        radioTower(var0, spot(var1, "torre"));
        cabin(var0, spot(var1, "cabana"));
        camp(var0, spot(var1, "campamento"));
        chapel(var0, spot(var1, "capilla"));
        stones(var0, spot(var1, "piedras"));
        caravan(var0, spot(var1, "caravana"));
        huntingStand(var0, spot(var1, "caza"));
        shelter(var0, spot(var1, "refugio"));
        crash(var0, spot(var1, "coche"));
        well(var0, spot(var1, "pozo"));
        tunnel(var0, spot(var1, "tunel"));
        dock(var0, spot(var1, "embarcadero"), var1.waterLevel());
    }

    private static Spot spot(Heightmap var0, String var1) {
        Place var2 = ForestLayout.place(var1);
        return new Spot(var2.x(), var0.at(var2.x(), var2.z()), var2.z());
    }

    private static void radioTower(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        int var5 = var3 + 31;
        var0.fill(var2 - 2, var3, var4 - 2, var2 + 2, var3, var4 + 2, Material.POLISHED_ANDESITE);

        for(int var6 = var3 + 1; var6 < var5; ++var6) {
            Material var7 = (var6 - var3) / 5 % 2 == 0 ? Material.WHITE_CONCRETE : Material.RED_CONCRETE;
            int[][] var8 = CORNERS;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                int[] var11 = var8[var10];
                var0.set(var2 + var11[0], var6, var4 + var11[1], var7);
            }

            if ((var6 - var3) % 4 == 0) {
                var0.set(var2, var6, var4 - 1, "iron_bars[east=true,west=true]");
                var0.set(var2, var6, var4 + 1, "iron_bars[east=true,west=true]");
                var0.set(var2 - 1, var6, var4, "iron_bars[north=true,south=true]");
                var0.set(var2 + 1, var6, var4, "iron_bars[north=true,south=true]");
            }
        }

        var0.fill(var2 - 1, var5 - 1, var4 - 1, var2 + 1, var5 - 1, var4 + 1, "smooth_stone_slab[type=top]");
        var0.set(var2, var5, var4, "redstone_lamp[lit=true]");
        var0.set(var2, var5 + 1, var4, "lightning_rod");
        var0.set(var2, var5 + 2, var4, "end_rod[facing=up]");
        var0.room(var2 + 2, var3, var4 - 2, var2 + 6, var3 + 4, var4 + 2, Material.ANDESITE, Material.SMOOTH_STONE, Material.SMOOTH_STONE);
        var0.wallsMixed(var2 + 2, var3 + 1, var4 - 2, var2 + 6, var3 + 3, var4 + 2, Material.ANDESITE, Material.ANDESITE, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS);
        var0.air(var2 + 2, var3 + 1, var4, var2 + 2, var3 + 2, var4);
        var0.set(var2 + 4, var3 + 2, var4 + 2, "glass_pane[east=true,west=true]");
        var0.fill(var2 + 3, var3 + 1, var4 - 1, var2 + 5, var3 + 1, var4 - 1, "dark_oak_slab[type=top]");
        var0.set(var2 + 3, var3 + 2, var4 - 1, Material.JUKEBOX);
        var0.set(var2 + 5, var3 + 2, var4 - 1, Material.NOTE_BLOCK);
        var0.stairs(var2 + 4, var3 + 1, var4 + 1, "spruce_stairs", "north");
        var0.set(var2 + 4, var3 + 3, var4, "lantern[hanging=true]");
    }

    private static void cabin(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        var0.room(var2 - 4, var3, var4 - 3, var2 + 4, var3 + 4, var4 + 3, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS);
        int[][] var5 = new int[][]{{-4, -3}, {4, -3}, {-4, 3}, {4, 3}};
        int var6 = var5.length;

        int var7;
        for(var7 = 0; var7 < var6; ++var7) {
            int[] var8 = var5[var7];
            var0.fill(var2 + var8[0], var3 + 1, var4 + var8[1], var2 + var8[0], var3 + 3, var4 + var8[1], "spruce_log");
        }

        int var10;
        for(var10 = 0; var10 <= 3; ++var10) {
            for(var6 = var2 - 5; var6 <= var2 + 5; ++var6) {
                var0.set(var6, var3 + 4 + var10, var4 + 4 - var10, "dark_oak_stairs[facing=north,half=bottom,shape=straight]");
                var0.set(var6, var3 + 4 + var10, var4 - 4 + var10, "dark_oak_stairs[facing=south,half=bottom,shape=straight]");
            }
        }

        var0.fill(var2 - 5, var3 + 8, var4, var2 + 5, var3 + 8, var4, "dark_oak_slab[type=bottom]");

        for(var10 = 1; var10 <= 3; ++var10) {
            for(var6 = var4 - 3 + var10; var6 <= var4 + 3 - var10; ++var6) {
                var0.set(var2 - 4, var3 + 4 + var10, var6, Material.SPRUCE_PLANKS);
                var0.set(var2 + 4, var3 + 4 + var10, var6, Material.SPRUCE_PLANKS);
            }
        }

        int[] var11 = ForestLayout.CABIN_LAMP;
        var0.set(var2 + var11[0], var3 + var11[1], var4 + var11[2], "redstone_lamp[lit=true]");
        var0.fill(var2, var3 + 1, var4 - 2, var2, var3 + 3, var4 + 2, Material.SPRUCE_PLANKS);
        var0.air(var2, var3 + 1, var4, var2, var3 + 2, var4);
        var0.set(var2 - 3, var3 + 1, var4 - 2, "red_bed[part=head,facing=north]");
        var0.set(var2 - 3, var3 + 1, var4 - 1, "red_bed[part=foot,facing=north]");
        var0.set(var2 - 1, var3 + 1, var4 - 2, "chest[facing=south]");
        var0.set(var2 + 2, var3 + 1, var4 + 1, "spruce_fence");
        var0.set(var2 + 2, var3 + 2, var4 + 1, "spruce_pressure_plate");
        var0.stairs(var2 + 3, var3 + 1, var4 + 1, "spruce_stairs", "west");
        var0.stairs(var2 + 1, var3 + 1, var4 + 1, "spruce_stairs", "east");
        var0.set(var2 + 3, var3 + 1, var4 - 2, "furnace[facing=south]");
        var0.set(var2 + 1, var3 + 1, var4 - 2, "barrel[facing=up]");
        var0.door(var2 - 2, var3 + 1, var4 + 3, "spruce_door", "south");
        var0.set(var2 + 2, var3 + 2, var4 + 3, "glass_pane[east=true,west=true]");
        var0.set(var2 - 4, var3 + 2, var4, "glass_pane[north=true,south=true]");
        var0.set(var2 + 4, var3 + 2, var4 - 1, "glass_pane[north=true,south=true]");
        var0.fill(var2 - 3, var3, var4 + 4, var2 + 3, var3, var4 + 5, Material.SPRUCE_PLANKS);
        int[] var12 = new int[]{var2 - 3, var2 + 3};
        var7 = var12.length;

        for(int var13 = 0; var13 < var7; ++var13) {
            int var9 = var12[var13];
            var0.fill(var9, var3 + 1, var4 + 5, var9, var3 + 3, var4 + 5, "spruce_fence");
        }

        var0.fill(var2 + 5, var3 + 1, var4 - 1, var2 + 5, var3 + 9, var4 - 1, Material.COBBLESTONE);
    }

    private static void camp(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        int var5;
        for(var5 = -1; var5 <= 1; ++var5) {
            for(int var6 = -1; var6 <= 1; ++var6) {
                var0.set(var2 + var5, var3, var4 + var6, Material.COBBLESTONE);
            }
        }

        var0.set(var2, var3, var4, Material.COARSE_DIRT);
        var0.set(var2, var3 + 1, var4, "campfire[lit=false]");
        var0.set(var2 - 2, var3 + 1, var4 + 2, "spruce_log[axis=x]");
        var0.set(var2 - 1, var3 + 1, var4 + 2, "spruce_log[axis=x]");
        var0.set(var2 + 2, var3 + 1, var4 - 1, "spruce_log[axis=z]");
        tent(var0, var2 - 4, var3, var4 - 4, Material.WHITE_WOOL);
        tent(var0, var2 + 2, var3, var4 - 5, Material.LIGHT_GRAY_WOOL);

        for(var5 = var2 + 3; var5 <= var2 + 5; ++var5) {
            var0.set(var5, var3 + 1, var4 + 3, "brown_carpet");
        }

        var0.set(var2 - 5, var3 + 1, var4 + 3, "barrel[facing=up]");
        var0.set(var2 - 1, var3 + 1, var4 - 3, "white_carpet");
    }

    private static void tent(Builder var0, int var1, int var2, int var3, Material var4) {
        for(int var5 = var1; var5 <= var1 + 2; ++var5) {
            var0.set(var5, var2 + 1, var3, var4);
            var0.set(var5, var2 + 1, var3 + 2, var4);
            var0.set(var5, var2 + 2, var3 + 1, var4);
        }

    }

    private static void chapel(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        var0.fill(var2 - 3, var3, var4 - 6, var2 + 3, var3, var4 + 4, Material.STONE_BRICKS);
        var0.air(var2 - 2, var3 + 1, var4 - 5, var2 + 2, var3 + 7, var4 + 3);

        int var5;
        int var6;
        int var7;
        for(var5 = var3 + 1; var5 <= var3 + 6; ++var5) {
            var6 = var5 - var3;

            for(var7 = var4 - 6; var7 <= var4 + 4; ++var7) {
                ruin(var0, var2 - 3, var5, var7, var6);
                ruin(var0, var2 + 3, var5, var7, var6);
            }

            for(var7 = var2 - 2; var7 <= var2 + 2; ++var7) {
                ruin(var0, var7, var5, var4 - 6, var6);
                if (var6 <= 4) {
                    ruin(var0, var7, var5, var4 + 4, var6);
                }
            }
        }

        int[] var13 = new int[]{var4 - 4, var4 - 1, var4 + 2};
        var6 = var13.length;

        int var8;
        for(var7 = 0; var7 < var6; ++var7) {
            var8 = var13[var7];

            for(int var9 = var3 + 3; var9 <= var3 + 4; ++var9) {
                var0.set(var2 - 3, var9, var8, Material.AIR);
                var0.set(var2 + 3, var9, var8, Material.AIR);
            }
        }

        for(var5 = var4 - 6; var5 <= var4 - 3; ++var5) {
            var0.fill(var2 - 3, var3 + 7, var5, var2 + 3, var3 + 7, var5, "stone_brick_slab[type=bottom]");
        }

        var0.walls(var2 - 1, var3 + 1, var4 + 4, var2 + 1, var3 + 9, var4 + 6, Material.STONE_BRICKS);
        var0.air(var2, var3 + 1, var4 + 4, var2, var3 + 2, var4 + 6);
        var0.fill(var2 - 1, var3 + 10, var4 + 4, var2 + 1, var3 + 10, var4 + 6, "stone_brick_slab[type=bottom]");
        var0.set(var2, var3 + 9, var4 + 5, "bell[attachment=ceiling,facing=north]");
        var0.set(var2, var3 + 1, var4 - 5, Material.POLISHED_ANDESITE);
        var0.set(var2 - 1, var3 + 1, var4 - 5, "candle[candles=3,lit=false]");
        var0.set(var2 + 1, var3 + 1, var4 - 5, "candle[candles=2,lit=false]");
        var13 = new int[]{var4 - 2, var4, var4 + 2};
        var6 = var13.length;

        for(var7 = 0; var7 < var6; ++var7) {
            var8 = var13[var7];
            int[] var14 = new int[]{var2 - 2, var2 - 1, var2 + 1, var2 + 2};
            int var10 = var14.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                int var12 = var14[var11];
                var0.stairs(var12, var3 + 1, var8, "spruce_stairs", "south");
            }
        }

        for(var5 = var2 - 7; var5 <= var2 - 5; var5 += 2) {
            for(var6 = var4 - 5; var6 <= var4 + 3; var6 += 3) {
                var0.set(var5, var3 + 1, var6, (String)var0.pick("mossy_stone_brick_wall", "stone_brick_wall", "cobblestone_wall", "andesite_wall"));
                var0.set(var5, var3, var6 + 1, Material.COARSE_DIRT);
            }
        }

        for(var5 = var4 - 6; var5 <= var4 + 4; ++var5) {
            if (var0.chance(0.7)) {
                var0.set(var2 - 8, var3 + 1, var5, "iron_bars[north=true,south=true]");
            }
        }

    }

    private static void ruin(Builder var0, int var1, int var2, int var3, int var4) {
        if (var4 <= 2 || var0.chance(0.9 - (double)var4 * 0.1)) {
            var0.set(var1, var2, var3, (Material)var0.pick(RUIN));
        }

    }

    private static void stones(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        for(int var5 = 0; var5 < 8; ++var5) {
            double var6 = (double)var5 * Math.PI / 4.0;
            int var8 = var2 + (int)Math.round(Math.sin(var6) * 5.0);
            int var9 = var4 - (int)Math.round(Math.cos(var6) * 5.0);
            int var10 = var5 % 2 == 0 ? 3 : 2;

            for(int var11 = var3 + 1; var11 <= var3 + var10; ++var11) {
                var0.set(var8, var11, var9, (Material)var0.pick(Material.MOSSY_COBBLESTONE, Material.COBBLESTONE, Material.ANDESITE, Material.TUFF));
            }
        }

        var0.set(var2, var3 + 1, var4, Material.SMOOTH_STONE);
        var0.set(var2 + 1, var3 + 1, var4, "smooth_stone_slab[type=bottom]");
        var0.set(var2, var3 + 2, var4, "candle[candles=4,lit=false]");
    }

    private static void caravan(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        int[][] var5 = new int[][]{{-3, -1}, {3, -1}, {-3, 2}, {3, 2}};
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int[] var8 = var5[var7];
            var0.set(var2 + var8[0], var3 + 1, var4 + var8[1], Material.POLISHED_BLACKSTONE);
        }

        var0.fill(var2 - 4, var3 + 2, var4 - 1, var2 + 4, var3 + 2, var4 + 2, Material.SPRUCE_PLANKS);
        var0.walls(var2 - 4, var3 + 3, var4 - 1, var2 + 4, var3 + 4, var4 + 2, Material.WHITE_TERRACOTTA);
        var0.fill(var2 - 4, var3 + 5, var4 - 1, var2 + 4, var3 + 5, var4 + 2, "smooth_stone_slab[type=bottom]");
        var0.air(var2 + 2, var3 + 3, var4 + 2, var2 + 2, var3 + 4, var4 + 2);
        var0.stairs(var2 + 2, var3 + 1, var4 + 3, "spruce_stairs", "north");
        var0.set(var2 - 1, var3 + 4, var4 + 2, "glass_pane[east=true,west=true]");
        var0.set(var2 + 4, var3 + 4, var4, "glass_pane[north=true,south=true]");
        var0.set(var2 - 3, var3 + 3, var4, "red_bed[part=head,facing=west]");
        var0.set(var2 - 2, var3 + 3, var4, "red_bed[part=foot,facing=west]");
        var0.set(var2 + 1, var3 + 3, var4, "spruce_fence");
        var0.set(var2 + 1, var3 + 4, var4, "spruce_pressure_plate");
    }

    private static void huntingStand(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        int[][] var5 = CORNERS;
        int var6 = var5.length;

        int var7;
        int[] var8;
        for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            var0.fill(var2 + var8[0], var3 + 1, var4 + var8[1], var2 + var8[0], var3 + 8, var4 + var8[1], "spruce_log");
        }

        var0.fill(var2, var3 + 1, var4 + 1, var2, var3 + 5, var4 + 1, Material.SPRUCE_PLANKS);
        var5 = new int[][]{{0, -1}, {-1, 0}, {0, 0}, {1, 0}, {0, 1}};
        var6 = var5.length;

        for(var7 = 0; var7 < var6; ++var7) {
            var8 = var5[var7];
            var0.set(var2 + var8[0], var3 + 6, var4 + var8[1], Material.SPRUCE_PLANKS);
        }

        var0.set(var2, var3 + 7, var4 - 1, "spruce_fence");
        var0.set(var2 - 1, var3 + 7, var4, "spruce_fence");
        var0.set(var2 + 1, var3 + 7, var4, "spruce_fence");
        var0.fill(var2 - 1, var3 + 9, var4 - 1, var2 + 1, var3 + 9, var4 + 1, "spruce_slab[type=bottom]");

        for(int var9 = var3 + 1; var9 <= var3 + 7; ++var9) {
            var0.set(var2, var9, var4 + 2, "ladder[facing=south]");
        }

    }

    private static void shelter(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        var0.fill(var2 - 3, var3, var4 - 2, var2 + 3, var3, var4 + 2, Material.COBBLESTONE);

        int var5;
        for(var5 = var3 + 1; var5 <= var3 + 3; ++var5) {
            for(int var6 = var2 - 3; var6 <= var2 + 3; ++var6) {
                for(int var7 = var4 - 2; var7 <= var4 + 2; ++var7) {
                    boolean var8 = var6 == var2 - 3 || var6 == var2 + 3 || var7 == var4 - 2 || var7 == var4 + 2;
                    if (var8 && (var5 - var3 <= 2 || var0.chance(0.6))) {
                        var0.set(var6, var5, var7, (Material)var0.pick(Material.COBBLESTONE, Material.MOSSY_COBBLESTONE, Material.STONE));
                    }
                }
            }
        }

        var0.air(var2 - 3, var3 + 1, var4, var2 - 3, var3 + 2, var4);

        for(var5 = var2 - 3; var5 <= var2 + 3; var5 += 2) {
            if (var0.chance(0.6)) {
                var0.fill(var5, var3 + 4, var4 - 2, var5, var3 + 4, var4 + 2, "spruce_log[axis=z]");
            }
        }

        var0.set(var2 + 1, var3 + 1, var4 - 1, "cobblestone_slab[type=bottom]");
        var0.set(var2 - 2, var3 + 3, var4 - 1, Material.COBWEB);
        var0.set(var2 + 2, var3 + 3, var4 + 1, Material.COBWEB);
        var0.set(var2 - 1, var3 + 1, var4 - 1, "campfire[lit=false]");
    }

    private static void crash(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        var0.fill(var2 + 3, var3 + 1, var4, var2 + 3, var3 + 8, var4, "oak_log");

        for(int var5 = -2; var5 <= 2; ++var5) {
            for(int var6 = -1; var6 <= 2; ++var6) {
                for(int var7 = -2; var7 <= 2; ++var7) {
                    if (var5 * var5 + var6 * var6 + var7 * var7 <= 6 && var0.chance(0.8) && (var5 != 0 || var7 != 0 || var6 > 0)) {
                        var0.set(var2 + 3 + var5, var3 + 8 + var6, var4 + var7, "oak_leaves[persistent=true]");
                    }
                }
            }
        }

        var0.fill(var2 - 2, var3 + 1, var4 - 1, var2 + 2, var3 + 1, var4 + 1, Material.RED_CONCRETE);
        var0.fill(var2 - 1, var3 + 2, var4 - 1, var2 + 1, var3 + 2, var4 + 1, Material.BLACK_STAINED_GLASS);
        var0.fill(var2 - 1, var3 + 3, var4 - 1, var2 + 1, var3 + 3, var4 + 1, Material.RED_CONCRETE);
        int[] var8 = ForestLayout.CAR_LAMP;
        var0.set(var2 + var8[0], var3 + var8[1], var4 + var8[2], "redstone_lamp[lit=true]");
        var0.set(var2 + 2, var3 + 1, var4 + 1, "redstone_lamp[lit=false]");
        var0.set(var2, var3 + 2, var4 + 1, Material.AIR);
        var0.set(var2 - 2, var3 + 1, var4 + 1, Material.BLACK_CONCRETE);
    }

    private static void well(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        int var5;
        int var6;
        for(var5 = -1; var5 <= 1; ++var5) {
            for(var6 = -1; var6 <= 1; ++var6) {
                if (var5 != 0 || var6 != 0) {
                    var0.set(var2 + var5, var3 + 1, var4 + var6, Material.COBBLESTONE);
                }
            }
        }

        for(var5 = var3 - 2; var5 <= var3; ++var5) {
            var0.set(var2, var5, var4, Material.WATER);
        }

        int[][] var9 = CORNERS;
        var6 = var9.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int[] var8 = var9[var7];
            var0.fill(var2 + var8[0], var3 + 2, var4 + var8[1], var2 + var8[0], var3 + 3, var4 + var8[1], "spruce_fence");
        }

        var0.fill(var2 - 1, var3 + 4, var4 - 1, var2 + 1, var3 + 4, var4 + 1, "spruce_slab[type=bottom]");
        var0.fill(var2 + 3, var3 + 1, var4, var2 + 3, var3 + 2, var4, Material.SPRUCE_PLANKS);
    }

    private static void tunnel(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        int var5;
        int var6;
        for(var5 = -6; var5 <= -2; ++var5) {
            for(var6 = -5; var6 <= 5; ++var6) {
                int var7 = var3 + 8 - Math.max(0, Math.abs(var6) - 2);

                for(int var8 = var3 - 1; var8 < var7; ++var8) {
                    var0.set(var2 + var5, var8, var4 + var6, Material.DIRT);
                }

                var0.set(var2 + var5, var7, var4 + var6, Material.GRASS_BLOCK);
            }
        }

        for(var5 = -4; var5 <= 4; ++var5) {
            for(var6 = var3 + 1; var6 <= var3 + 8; ++var6) {
                var0.set(var2 - 1, var6, var4 + var5, (Material)var0.pick(RUIN));
            }
        }

        for(var5 = -2; var5 <= 2; ++var5) {
            for(var6 = var3 + 1; var6 <= var3 + 5; ++var6) {
                if (var6 != var3 + 5 || Math.abs(var5) != 2) {
                    var0.set(var2 - 1, var6, var4 + var5, var0.chance(0.75) ? Material.SPRUCE_PLANKS : Material.BLACK_CONCRETE);
                    var0.set(var2 - 2, var6, var4 + var5, Material.BLACK_CONCRETE);
                }
            }
        }

        for(var5 = 0; var5 <= 6; ++var5) {
            var0.set(var2 + var5, var3, var4, Material.GRAVEL);
            var0.set(var2 + var5, var3 + 1, var4, "rail[shape=east_west]");
        }

    }

    private static void dock(Builder var0, Spot var1, int var2) {
        int var3 = var1.x();
        int var4 = var1.y();
        int var5 = var1.z();
        var0.room(var3 - 3, var4, var5 - 1, var3, var4 + 4, var5 + 1, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS, Material.SPRUCE_PLANKS);
        var0.air(var3 - 1, var4 + 1, var5 - 1, var3 - 1, var4 + 2, var5 - 1);

        int var6;
        for(var6 = 4; var6 <= 13; ++var6) {
            for(int var7 = -1; var7 <= 1; ++var7) {
                var0.set(var3 + var6, var2 + 1, var5 + var7, Material.SPRUCE_PLANKS);
            }
        }

        for(var6 = 4; var6 <= 13; var6 += 3) {
            int[] var12 = new int[]{-1, 1};
            int var8 = var12.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                int var10 = var12[var9];

                for(int var11 = var2 - 3; var11 <= var2; ++var11) {
                    var0.set(var3 + var6, var11, var5 + var10, "spruce_log");
                }
            }
        }

        for(var6 = 15; var6 <= 18; ++var6) {
            var0.set(var3 + var6, var2, var5 + 2, "spruce_slab[type=bottom,waterlogged=true]");
            var0.set(var3 + var6, var2, var5 + 4, "spruce_slab[type=bottom,waterlogged=true]");
        }

        var0.set(var3 + 15, var2, var5 + 3, "spruce_slab[type=bottom,waterlogged=true]");
        var0.set(var3 + 18, var2, var5 + 3, "spruce_slab[type=bottom,waterlogged=true]");
    }

    static {
        RUIN = new Material[]{Material.STONE_BRICKS, Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CRACKED_STONE_BRICKS};
    }

    private static record Spot(int x, int y, int z) {
        private Spot(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int x() {
            return this.x;
        }

        public int y() {
            return this.y;
        }

        public int z() {
            return this.z;
        }
    }
}
