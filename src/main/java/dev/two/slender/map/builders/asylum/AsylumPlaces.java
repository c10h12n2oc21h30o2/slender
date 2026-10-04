package dev.two.slender.map.builders.asylum;

import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Place;
import org.bukkit.Material;

final class AsylumPlaces {
    static final int[][] BREACHES = new int[][]{{-46, -12, -46, -9}, {46, 4, 46, 7}, {19, -44, 22, -44}, {-30, 30, -27, 30}};

    private AsylumPlaces() {
    }

    static void grounds(Builder var0) {
        wall(var0);
        garden(var0);
        fountain(var0, AsylumLayout.place("fuente"));
        greenhouse(var0, AsylumLayout.place("invernadero"));
        ambulance(var0, AsylumLayout.place("ambulancia"));
        gatehouse(var0, AsylumLayout.place("porteria"));
        cemetery(var0, AsylumLayout.place("cementerio"));
    }

    static void tower(Builder var0) {
        byte var1 = 18;
        byte var2 = 30;

        for(int var3 = var1 + 1; var3 <= var1 + 9; ++var3) {
            for(int var4 = -2; var4 <= 2; ++var4) {
                for(int var5 = -2; var5 <= 2; ++var5) {
                    boolean var6 = Math.abs(var4) == 2 || Math.abs(var5) == 2;
                    boolean var7 = var3 >= var1 + 6 && var3 <= var1 + 7 && (Math.abs(var4) <= 1 || Math.abs(var5) <= 1);
                    if (var6 && !var7) {
                        var0.set(var4, var3, var5, (String)var0.pick("stone_bricks", "stone_bricks", "cracked_stone_bricks"));
                    } else if (!var6) {
                        var0.set(var4, var3, var5, "air");
                    }
                }
            }
        }

        var0.set(0, var1 + 9, 0, (String)"stone_bricks");
        var0.set(0, var1 + 8, 0, (String)"bell[attachment=ceiling,facing=north]");
        var0.fill(-2, var1 + 10, -2, 2, var1 + 10, 2, (String)"deepslate_tiles");
        var0.fill(-1, var1 + 11, -1, 1, var1 + 11, 1, (String)"deepslate_tile_slab[type=bottom]");
        var0.set(0, var1 + 11, 0, (String)"deepslate_tile_wall");
        var0.set(0, var2, 0, (String)"redstone_lamp[lit=true]");
        var0.set(0, var2 + 1, 0, (String)"end_rod[facing=up]");
    }

    private static void wall(Builder var0) {
        int[] var1 = AsylumLayout.GROUNDS;

        int var2;
        for(var2 = var1[0]; var2 <= var1[2]; ++var2) {
            wallColumn(var0, var2, var1[1]);
            if (Math.abs(var2) > 3) {
                wallColumn(var0, var2, var1[3]);
            }
        }

        for(var2 = var1[1]; var2 <= var1[3]; ++var2) {
            wallColumn(var0, var1[0], var2);
            wallColumn(var0, var1[2], var2);
        }

        int[] var6 = new int[]{-4, 4};
        int var3 = var6.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var6[var4];
            var0.fill(var5, 9, var1[3], var5, 12, var1[3], (String)"stone_bricks");
            var0.set(var5, 13, var1[3], (String)"lantern[hanging=false]");
        }

        var0.fill(-3, 9, var1[3], -3, 10, var1[3], (String)"iron_bars[east=true]");
        var0.fill(3, 9, var1[3], 3, 10, var1[3], (String)"iron_bars[west=true]");
    }

    private static void wallColumn(Builder var0, int var1, int var2) {
        int[][] var3 = BREACHES;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int[] var6 = var3[var5];
            if (var1 >= var6[0] && var1 <= var6[2] && var2 >= var6[1] && var2 <= var6[3]) {
                if (var0.chance(0.6)) {
                    var0.set(var1, 9, var2, (String)((String)var0.pick("cobblestone", "mossy_cobblestone", "stone_brick_slab[type=bottom]")));
                }

                return;
            }
        }

        for(int var7 = 9; var7 <= 11; ++var7) {
            var0.set(var1, var7, var2, (String)var0.pick("stone_bricks", "stone_bricks", "mossy_stone_bricks", "cracked_stone_bricks", "cobblestone"));
        }

        if (var0.chance(0.3)) {
            var0.set(var1, 11, var2, (String)"air");
        }

    }

    private static void garden(Builder var0) {
        int[] var1 = new int[]{-1, 1};
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var1[var3];

            for(int var5 = 7; var5 <= 13; ++var5) {
                for(int var6 = 13; var6 <= 22; ++var6) {
                    int var7 = var4 * var5;
                    boolean var8 = var5 == 7 || var5 == 13 || var6 == 13 || var6 == 22;
                    if (var8) {
                        var0.set(var7, 9, var6, (String)"stone_brick_slab[type=bottom]");
                    } else {
                        var0.set(var7, 8, var6, (String)((String)var0.pick("coarse_dirt", "rooted_dirt", "podzol")));
                        double var9 = var0.rnd.nextDouble();
                        if (var9 < 0.2) {
                            var0.set(var7, 9, var6, (String)"dead_bush");
                        } else if (var9 < 0.23) {
                            var0.set(var7, 9, var6, (String)"wither_rose");
                        }
                    }
                }
            }

            AsylumNature.deadTree(var0, var4 * 10, 9, 17, "oak_log", 4, 6);
        }

    }

    private static void fountain(Builder var0, Place var1) {
        int var2 = var1.x();
        int var3 = var1.z();
        byte var4 = 8;

        for(int var5 = -5; var5 <= 5; ++var5) {
            for(int var6 = -5; var6 <= 5; ++var6) {
                double var7 = Math.hypot((double)var5, (double)var6);
                if (var7 <= 5.5) {
                    var0.set(var2 + var5, var4, var3 + var6, (String)((String)var0.pick("gravel", "cobblestone", "gravel", "andesite")));
                }

                if (var7 > 2.5 && var7 <= 3.5) {
                    var0.set(var2 + var5, var4 + 1, var3 + var6, (String)var0.pick("stone_bricks", "stone_bricks", "cracked_stone_bricks"));
                } else if (var7 <= 2.5) {
                    var0.set(var2 + var5, var4, var3 + var6, (String)"cobblestone");
                    if (var0.chance(0.15)) {
                        var0.set(var2 + var5, var4 + 1, var3 + var6, "dead_bush");
                    }
                }
            }
        }

        var0.set(var2, var4 + 1, var3, "stone_brick_wall");
        var0.set(var2, var4 + 2, var3, "stone_bricks");
        var0.set(var2, var4 + 3, var3, "stone_brick_wall");
        var0.set(var2, var4 + 4, var3, "chiseled_stone_bricks");
    }

    private static void greenhouse(Builder var0, Place var1) {
        int var2 = var1.x() - 6;
        int var3 = var1.x() + 6;
        int var4 = var1.z() - 3;
        int var5 = var1.z() + 3;
        byte var6 = 8;

        int var7;
        for(var7 = var2; var7 <= var3; ++var7) {
            for(int var8 = var4; var8 <= var5; ++var8) {
                boolean var9 = var8 == var4 || var8 == var5;
                boolean var10 = var7 == var2 || var7 == var3;
                var0.set(var7, var6, var8, (String)((String)var0.pick("coarse_dirt", "mud_bricks", "coarse_dirt")));
                if (!var9 && !var10) {
                    if (var0.chance(0.8)) {
                        var0.set(var7, var6 + 5, var8, "glass");
                    }
                } else if (var9 && var10) {
                    var0.fill(var7, var6 + 1, var8, var7, var6 + 5, var8, "iron_bars");
                } else {
                    var0.set(var7, var6 + 1, var8, "bricks");
                    String var11 = var9 ? "glass_pane[east=true,west=true]" : "glass_pane[north=true,south=true]";

                    for(int var12 = 2; var12 <= 4; ++var12) {
                        if (var0.chance(0.55)) {
                            var0.set(var7, var6 + var12, var8, var11);
                        }
                    }

                    var0.set(var7, var6 + 5, var8, "bricks");
                }
            }
        }

        var0.air(var1.x() + 2, var6 + 1, var5, var1.x() + 2, var6 + 3, var5);

        for(var7 = var2 + 2; var7 <= var3 - 2; ++var7) {
            int[] var13 = new int[]{var4 + 2, var5 - 2};
            int var14 = var13.length;

            for(int var15 = 0; var15 < var14; ++var15) {
                int var16 = var13[var15];
                if (var7 != var1.x() + 2) {
                    var0.set(var7, var6 + 1, var16, "spruce_slab[type=top]");
                    if (var0.chance(0.5)) {
                        var0.set(var7, var6 + 2, var16, (String)var0.pick("potted_dead_bush", "flower_pot", "potted_wither_rose"));
                    }
                }
            }
        }

    }

    private static void ambulance(Builder var0, Place var1) {
        int var2 = var1.x();
        int var3 = var1.z();
        byte var4 = 8;
        var0.fill(var2 - 3, var4 + 1, var3 - 1, var2 + 3, var4 + 2, var3 + 1, "white_concrete");
        var0.fill(var2 - 1, var4 + 1, var3 - 1, var2 + 1, var4 + 1, var3 - 1, "red_concrete");
        var0.fill(var2 - 1, var4 + 1, var3 + 1, var2 + 1, var4 + 1, var3 + 1, "red_concrete");
        var0.fill(var2 - 3, var4 + 2, var3 - 1, var2 - 3, var4 + 2, var3 + 1, "black_stained_glass");
        int[][] var5 = new int[][]{{-2, -1}, {2, -1}, {-2, 1}, {2, 1}};
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int[] var8 = var5[var7];
            var0.set(var2 + var8[0], var4 + 1, var3 + var8[1], "coal_block");
        }

        var0.fill(var2 - 2, var4 + 3, var3 - 1, var2 + 3, var4 + 3, var3 + 1, "smooth_quartz_slab[type=bottom]");
        int[] var9 = AsylumLayout.AMBULANCE_LAMP;
        var0.set(var2 + var9[0], var4 + var9[1], var3 + var9[2], "redstone_lamp[lit=true]");
        var0.set(var2 + 3, var4 + 1, var3, "iron_trapdoor[facing=east,half=bottom,open=true]");
        var0.set(var2 + 4, var4 + 1, var3, "white_carpet");
    }

    private static void gatehouse(Builder var0, Place var1) {
        int var2 = var1.x();
        int var3 = var1.z();
        byte var4 = 8;
        var0.fill(var2 - 2, var4, var3 - 2, var2 + 2, var4, var3 + 2, (String)"stone");
        var0.wallsMixed(var2 - 2, var4 + 1, var3 - 2, var2 + 2, var4 + 3, var3 + 2, Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS);
        var0.air(var2 - 1, var4 + 1, var3 - 1, var2 + 1, var4 + 3, var3 + 1);
        var0.fill(var2 - 2, var4 + 4, var3 - 2, var2 + 2, var4 + 4, var3 + 2, "spruce_slab[type=bottom]");
        var0.air(var2 - 2, var4 + 1, var3, var2 - 2, var4 + 2, var3);
        var0.set(var2, var4 + 2, var3 - 2, "glass_pane[east=true,west=true]");
        var0.set(var2, var4 + 2, var3 + 2, "air");
        var0.set(var2 + 1, var4 + 1, var3 - 1, "spruce_stairs[facing=south,half=bottom,shape=straight]");
        var0.set(var2 + 1, var4 + 1, var3 + 1, "spruce_slab[type=top]");
        var0.set(var2 + 1, var4 + 2, var3 + 1, "lantern[hanging=false]");
    }

    private static void cemetery(Builder var0, Place var1) {
        int var2 = var1.x();
        int var3 = var1.z();
        byte var4 = 8;
        var0.fill(var2 - 2, var4, var3 - 2, var2 + 2, var4, var3 + 2, (String)"polished_andesite");
        var0.walls(var2 - 2, var4 + 1, var3 - 2, var2 + 2, var4 + 4, var3 + 2, Material.POLISHED_ANDESITE);
        var0.air(var2 - 1, var4 + 1, var3 - 1, var2 + 1, var4 + 4, var3 + 1);
        var0.fill(var2 - 2, var4 + 5, var3 - 2, var2 + 2, var4 + 5, var3 + 2, "polished_andesite_slab[type=bottom]");
        var0.set(var2, var4 + 5, var3, "chiseled_stone_bricks");
        var0.air(var2, var4 + 1, var3 + 2, var2, var4 + 2, var3 + 2);
        var0.fill(var2 - 1, var4 + 1, var3 - 1, var2 + 1, var4 + 1, var3 - 1, "smooth_stone_slab[type=top]");
        var0.set(var2, var4 + 2, var3 - 1, "candle[candles=2,lit=false]");
        int[] var5 = new int[]{-12, -9, -6, 6, 9, 12};
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var5[var7];
            int[] var9 = new int[]{-3, 0, 3};
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                int var12 = var9[var11];
                var0.set(var2 + var8, var4 + 1, var3 + var12, (String)var0.pick("andesite_wall", "stone_brick_wall", "mossy_stone_brick_wall", "cobblestone_wall"));
                var0.set(var2 + var8, var4, var3 + var12 + 1, (String)"coarse_dirt");
            }
        }

        int var13;
        for(var13 = -14; var13 <= 14; ++var13) {
            if (Math.abs(var13) > 1) {
                var0.set(var2 + var13, var4 + 1, var3 + 5, "iron_bars[east=true,west=true]");
            }
        }

        for(var13 = -3; var13 <= 5; ++var13) {
            var0.set(var2 - 14, var4 + 1, var3 + var13, "iron_bars[north=true,south=true]");
            var0.set(var2 + 14, var4 + 1, var3 + var13, "iron_bars[north=true,south=true]");
        }

    }
}
