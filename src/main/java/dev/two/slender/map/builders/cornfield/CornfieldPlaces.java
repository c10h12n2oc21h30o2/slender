package dev.two.slender.map.builders.cornfield;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Place;
import java.util.Iterator;
import java.util.List;

final class CornfieldPlaces {
    private CornfieldPlaces() {
    }

    private static Spot spot(Heightmap var0, String var1) {
        Place var2 = CornfieldLayout.place(var1);
        return new Spot(var2.x(), var0.at(var2.x(), var2.z()), var2.z());
    }

    static void build(Builder var0, Heightmap var1, List<int[]> var2) {
        silo(var0, spot(var1, "silo"));
        mill(var0, spot(var1, "molino"));
        waterTower(var0, spot(var1, "deposito"));
        cropCircle(var0, spot(var1, "circulo"));
        combine(var0, spot(var1, "cosechadora"));
        pumpkins(var0, spot(var1, "calabazas"));
        coop(var0, spot(var1, "gallinero"));
        well(var0, spot(var1, "pozo"));
        bridge(var0, spot(var1, "puente"));
        outhouse(var0, spot(var1, "letrina"));
        Iterator var3 = CornfieldLayout.SCARECROWS.iterator();

        while(var3.hasNext()) {
            Place var4 = (Place)var3.next();
            scarecrow(var0, var4.x(), var1.at(var4.x(), var4.z()), var4.z(), true);
        }

        var3 = var2.iterator();

        while(var3.hasNext()) {
            int[] var5 = (int[])var3.next();
            pole(var0, var5[0], var1.at(var5[0], var5[1]), var5[1]);
        }

    }

    static void roofs(Builder var0, Heightmap var1) {
        Spot var2 = spot(var1, "casa");
        Spot var3 = spot(var1, "granero");
        porch(var0, var2.y());
        houseRoof(var0, var2.y());
        barnRoof(var0, var3.y());
    }

    private static void silo(Builder var0, Spot var1) {
        for(int var2 = -5; var2 <= 5; ++var2) {
            for(int var3 = -5; var3 <= 5; ++var3) {
                double var4 = Math.hypot((double)var2, (double)var3);
                int var6 = var1.x() + var2;
                int var7 = var1.z() + var3;
                if (var4 <= 3.5) {
                    var0.set(var6, var1.y(), var7, "smooth_stone");
                }

                if (var4 > 3.5 && var4 <= 4.5) {
                    for(int var8 = 1; var8 <= 14; ++var8) {
                        var0.set(var6, var1.y() + var8, var7, (String)var0.pick("light_gray_terracotta", "light_gray_terracotta", "light_gray_terracotta", "white_terracotta"));
                    }
                }

                if (var4 <= 4.5) {
                    var0.set(var6, var1.y() + 15, var7, "polished_andesite");
                }

                if (var4 <= 3.0) {
                    var0.set(var6, var1.y() + 16, var7, "polished_andesite");
                }
            }
        }

        var0.air(var1.x(), var1.y() + 1, var1.z() + 4, var1.x(), var1.y() + 2, var1.z() + 4);
        var0.fill(var1.x(), var1.y() + 9, var1.z(), var1.x(), var1.y() + 14, var1.z(), "iron_chain[axis=y]");
        var0.set(var1.x(), var1.y() + 17, var1.z(), "lightning_rod[facing=up]");
        var0.fill(var1.x() - 2, var1.y() + 1, var1.z() + 2, var1.x() - 1, var1.y() + 1, var1.z() + 2, "hay_block[axis=y]");
    }

    private static void mill(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        int var5;
        int var6;
        for(var5 = -2; var5 <= 2; ++var5) {
            for(var6 = -2; var6 <= 2; ++var6) {
                var0.set(var2 + var5, var3, var4 + var6, "cobblestone");
                boolean var7 = Math.abs(var5) == 2 || Math.abs(var6) == 2;

                for(int var8 = 1; var8 <= 14; ++var8) {
                    if (var7) {
                        var0.set(var2 + var5, var3 + var8, var4 + var6, (String)var0.pick("cobblestone", "cobblestone", "mossy_cobblestone", "stone_bricks"));
                    } else {
                        var0.set(var2 + var5, var3 + var8, var4 + var6, "air");
                    }
                }

                var0.set(var2 + var5, var3 + 15, var4 + var6, "dark_oak_planks");
            }
        }

        for(var5 = -3; var5 <= 3; ++var5) {
            for(var6 = -3; var6 <= 3; ++var6) {
                if (Math.abs(var5) == 3 || Math.abs(var6) == 3) {
                    var0.set(var2 + var5, var3 + 15, var4 + var6, "dark_oak_slab[type=bottom]");
                }
            }
        }

        var0.air(var2 + 2, var3 + 1, var4, var2 + 2, var3 + 2, var4);
        var0.fill(var2, var3 + 9, var4, var2, var3 + 14, var4, "iron_chain[axis=y]");
        var0.set(var2, var3 + 16, var4, "redstone_lamp[lit=true]");
        int[][] var9 = new int[][]{{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        var6 = var9.length;

        int var10;
        for(var10 = 0; var10 < var6; ++var10) {
            int[] var11 = var9[var10];
            var0.set(var2 + var11[0], var3 + 16, var4 + var11[1], "dark_oak_fence");
        }

        var0.fill(var2 - 1, var3 + 17, var4 - 1, var2 + 1, var3 + 17, var4 + 1, "dark_oak_slab[type=bottom]");
        var5 = var2 + 3;
        var6 = var3 + 11;
        var0.set(var5, var6, var4, "oak_log[axis=x]");

        for(var10 = 1; var10 <= 6; ++var10) {
            var0.set(var5, var6 + var10, var4, "stripped_spruce_log[axis=y]");
            var0.set(var5, var6 + var10, var4 + 1, "white_wool");
            var0.set(var5, var6 - var10, var4, "stripped_spruce_log[axis=y]");
            var0.set(var5, var6 - var10, var4 - 1, "white_wool");
            var0.set(var5, var6, var4 + var10, "stripped_spruce_log[axis=z]");
            var0.set(var5, var6 - 1, var4 + var10, "white_wool");
            var0.set(var5, var6, var4 - var10, "stripped_spruce_log[axis=z]");
            var0.set(var5, var6 + 1, var4 - var10, "white_wool");
        }

    }

    private static void waterTower(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        int[][] var5 = new int[][]{{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int[] var8 = var5[var7];
            var0.fill(var2 + var8[0], var3 + 1, var4 + var8[1], var2 + var8[0], var3 + 10, var4 + var8[1], "spruce_log");
        }

        int var10;
        for(var10 = -1; var10 <= 1; ++var10) {
            var0.set(var2 + var10, var3 + 6, var4 - 2, "spruce_fence[east=true,west=true]");
            var0.set(var2 + var10, var3 + 6, var4 + 2, "spruce_fence[east=true,west=true]");
            var0.set(var2 - 2, var3 + 6, var4 + var10, "spruce_fence[north=true,south=true]");
            var0.set(var2 + 2, var3 + 6, var4 + var10, "spruce_fence[north=true,south=true]");
        }

        var0.fill(var2 - 3, var3 + 11, var4 - 3, var2 + 3, var3 + 11, var4 + 3, "spruce_planks");

        for(var10 = -3; var10 <= 3; ++var10) {
            for(var6 = -3; var6 <= 3; ++var6) {
                double var11 = Math.hypot((double)var10, (double)var6);
                if (!(var11 > 3.3)) {
                    if (var11 > 2.3) {
                        for(int var9 = 12; var9 <= 15; ++var9) {
                            var0.set(var2 + var10, var3 + var9, var4 + var6, var9 == 13 ? "dark_oak_planks" : "spruce_planks");
                        }
                    }

                    var0.set(var2 + var10, var3 + 16, var4 + var6, "spruce_slab[type=bottom]");
                }
            }
        }

    }

    private static void cropCircle(Builder var0, Spot var1) {
        int var2 = CornfieldLayout.place("circulo").pad();

        for(int var3 = -var2; var3 <= var2; ++var3) {
            for(int var4 = -var2; var4 <= var2; ++var4) {
                double var5 = Math.hypot((double)var3, (double)var4);
                if (!(var5 > (double)var2 + 0.5)) {
                    int var7 = var1.x() + var3;
                    int var8 = var1.z() + var4;
                    boolean var9 = Math.abs(var3) <= 1 || Math.abs(var4) <= 1;
                    var0.set(var7, var1.y(), var8, var5 <= 2.5 ? "soul_soil" : "coarse_dirt");
                    if ((var5 > 2.5 && var5 <= 3.5 || var5 > 5.5 && var5 <= 6.5) && !var9) {
                        var0.set(var7, var1.y() + 1, var8, "bamboo_block[axis=y]");
                        var0.set(var7, var1.y() + 2, var8, "bamboo_block[axis=y]");
                        var0.set(var7, var1.y() + 3, var8, "bamboo[age=1,leaves=large,stage=0]");
                    } else if (var5 > 3.5 && var5 <= 5.5) {
                        var0.set(var7, var1.y(), var8, "stripped_bamboo_block[axis=x]");
                    }
                }
            }
        }

        var0.fill(var1.x(), var1.y() + 1, var1.z(), var1.x(), var1.y() + 3, var1.z(), "polished_blackstone");
        var0.set(var1.x(), var1.y() + 4, var1.z(), "polished_blackstone_wall");
    }

    private static void combine(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();
        var0.fill(var2 - 2, var3 + 1, var4 - 1, var2 + 2, var3 + 3, var4 + 1, "yellow_concrete");
        var0.fill(var2 - 1, var3 + 4, var4 - 1, var2, var3 + 4, var4 + 1, "black_stained_glass");
        var0.fill(var2 - 1, var3 + 5, var4 - 1, var2, var3 + 5, var4 + 1, "yellow_concrete");

        for(int var5 = -3; var5 <= 3; ++var5) {
            var0.set(var2 - 3, var3 + 1, var4 + var5, "iron_bars[north=true,south=true]");
            var0.set(var2 - 4, var3 + 2, var4 + var5, "stripped_oak_log[axis=z]");
        }

        int[][] var9 = new int[][]{{-2, -2}, {2, -2}, {-2, 2}, {2, 2}};
        int var6 = var9.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            int[] var8 = var9[var7];
            var0.set(var2 + var8[0], var3 + 1, var4 + var8[1], "coal_block");
        }

        var0.set(var2 + 2, var3 + 4, var4, "iron_bars");
    }

    private static void pumpkins(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        for(int var5 = -5; var5 <= 5; ++var5) {
            for(int var6 = -2; var6 <= 5; ++var6) {
                if (!(Math.hypot((double)var5, (double)var6) > 5.8)) {
                    var0.set(var2 + var5, var3, var4 + var6, (String)var0.pick("coarse_dirt", "rooted_dirt", "dirt"));
                    if (Math.floorMod(var6, 2) == 0 && (Math.abs(var5) != 4 || var6 != 0) && (var5 != 4 || var6 != 2) && var0.chance(0.55)) {
                        var0.set(var2 + var5, var3 + 1, var4 + var6, (String)var0.pick("pumpkin", "pumpkin", "carved_pumpkin[facing=south]", "carved_pumpkin[facing=east]", "jack_o_lantern[facing=south]"));
                    }
                }
            }
        }

        var0.fill(var2 - 2, var3 + 1, var4 - 6, var2 + 2, var3 + 2, var4 - 6, "spruce_planks");
        var0.fill(var2 - 2, var3 + 3, var4 - 6, var2 + 2, var3 + 3, var4 - 6, "spruce_slab[type=bottom]");
        scarecrow(var0, var2 + 4, var3, var4 + 1, false);
    }

    private static void coop(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        for(int var5 = -2; var5 <= 2; ++var5) {
            for(int var6 = -2; var6 <= 2; ++var6) {
                boolean var7 = Math.abs(var5) == 2 || Math.abs(var6) == 2;
                var0.set(var2 + var5, var3, var4 + var6, var7 ? "oak_planks" : "hay_block[axis=y]");

                for(int var8 = 1; var8 <= 3; ++var8) {
                    var0.set(var2 + var5, var3 + var8, var4 + var6, var7 ? "oak_planks" : "air");
                }
            }
        }

        var0.fill(var2 - 3, var3 + 4, var4 - 3, var2 + 3, var3 + 4, var4 + 3, "oak_slab[type=bottom]");
        var0.air(var2, var3 + 1, var4 + 2, var2, var3 + 2, var4 + 2);
        var0.set(var2 - 2, var3 + 2, var4, "glass_pane[north=true,south=true]");
        var0.set(var2 - 1, var3 + 1, var4 - 1, "composter");
        var0.set(var2 + 1, var3 + 1, var4 - 1, "composter");
    }

    private static void well(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        for(int var5 = -1; var5 <= 1; ++var5) {
            for(int var6 = -1; var6 <= 1; ++var6) {
                if (var5 == 0 && var6 == 0) {
                    var0.set(var2, var3, var4, "water");
                    var0.set(var2, var3 - 1, var4, "water");
                } else {
                    var0.set(var2 + var5, var3 + 1, var4 + var6, "cobblestone");
                }
            }
        }

        var0.fill(var2 - 2, var3 + 1, var4, var2 - 2, var3 + 3, var4, "spruce_log");
        var0.fill(var2 + 2, var3 + 1, var4, var2 + 2, var3 + 3, var4, "spruce_log");
        var0.fill(var2 - 2, var3 + 4, var4 - 1, var2 + 2, var3 + 4, var4 + 1, "spruce_slab[type=bottom]");
        var0.set(var2, var3 + 3, var4, "iron_chain[axis=y]");
    }

    private static void bridge(Builder var0, Spot var1) {
        int[][] var2 = new int[][]{{-2, 48}, {2, 48}, {-2, 52}, {2, 52}};
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int[] var5 = var2[var4];
            var0.fill(var5[0], var1.y() + 1, var5[1], var5[0], var1.y() + 2, var5[1], "spruce_log");
            var0.set(var5[0], var1.y() + 3, var5[1], "lantern[hanging=false]");
        }

    }

    private static void outhouse(Builder var0, Spot var1) {
        int var2 = var1.x();
        int var3 = var1.y();
        int var4 = var1.z();

        for(int var5 = -1; var5 <= 1; ++var5) {
            for(int var6 = -1; var6 <= 1; ++var6) {
                var0.set(var2 + var5, var3, var4 + var6, "spruce_planks");
                boolean var7 = var5 != 0 || var6 != 0;

                for(int var8 = 1; var8 <= 3; ++var8) {
                    var0.set(var2 + var5, var3 + var8, var4 + var6, var7 ? "spruce_planks" : "air");
                }
            }
        }

        var0.fill(var2 - 2, var3 + 4, var4 - 2, var2 + 2, var3 + 4, var4 + 2, "spruce_slab[type=bottom]");
        var0.door(var2, var3 + 1, var4 + 1, "spruce_door", "south");
    }

    private static void scarecrow(Builder var0, int var1, int var2, int var3, boolean var4) {
        int var5 = var4 ? 3 : 1;
        var0.fill(var1, var2 + 1, var3, var1, var2 + var5, var3, "oak_fence");
        var0.set(var1, var2 + var5 + 1, var3, "hay_block[axis=y]");
        var0.set(var1 - 1, var2 + var5 + 1, var3, "oak_fence[east=true,west=true]");
        var0.set(var1 + 1, var2 + var5 + 1, var3, "oak_fence[east=true,west=true]");
        var0.set(var1, var2 + var5 + 2, var3, "carved_pumpkin[facing=south]");
        if (var4) {
            var0.set(var1, var2 + var5 + 3, var3, "lantern[hanging=false]");
        }

    }

    private static void pole(Builder var0, int var1, int var2, int var3) {
        var0.fill(var1, var2 + 1, var3, var1, var2 + 6, var3, "spruce_log");
        var0.set(var1 - 1, var2 + 6, var3, "spruce_fence[east=true,west=true]");
        var0.set(var1 + 1, var2 + 6, var3, "spruce_fence[east=true,west=true]");
        var0.set(var1, var2 + 7, var3, "redstone_lamp[lit=true]");
        var0.set(var1, var2 + 8, var3, "spruce_slab[type=bottom]");
    }

    private static void porch(Builder var0, int var1) {
        var0.fill(20, var1, -56, 36, var1, -54, (String)"spruce_planks");
        int[] var2 = new int[]{20, 24, 32, 36};
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            var0.fill(var5, var1 + 1, -54, var5, var1 + 3, -54, (String)"dark_oak_log[axis=y]");
        }

        for(int var6 = 21; var6 <= 35; ++var6) {
            if (var6 != 24 && var6 != 32 && (var6 < 27 || var6 > 29)) {
                var0.set(var6, var1 + 1, -54, (String)"oak_fence[east=true,west=true]");
            }
        }

        var0.fill(19, var1 + 4, -56, 37, var1 + 4, -53, (String)"spruce_slab[type=bottom]");
        var0.set(26, var1 + 3, -55, (String)"lantern[hanging=true]");
        var0.set(34, var1 + 1, -56, (String)"spruce_stairs[facing=south,half=bottom,shape=straight]");
    }

    private static void houseRoof(Builder var0, int var1) {
        for(int var2 = 0; var2 <= 5; ++var2) {
            int var3 = -68 + var2;
            int var4 = -56 - var2;
            int var5 = var1 + 8 + var2;

            int var6;
            for(var6 = 19; var6 <= 37; ++var6) {
                var0.set(var6, var5, var3, "spruce_stairs[facing=south,half=bottom,shape=straight]");
                var0.set(var6, var5, var4, "spruce_stairs[facing=north,half=bottom,shape=straight]");
            }

            if (var2 > 0) {
                for(var6 = var3 + 1; var6 <= var4 - 1; ++var6) {
                    var0.set(20, var5, var6, (String)"spruce_planks");
                    var0.set(36, var5, var6, (String)"spruce_planks");
                }
            }
        }

        var0.fill(19, var1 + 14, -62, 37, var1 + 14, -62, (String)"spruce_slab[type=bottom]");
        var0.fill(30, var1 + 9, -60, 30, var1 + 13, -60, (String)"bricks");
    }

    private static void barnRoof(Builder var0, int var1) {
        for(int var2 = 0; var2 <= 7; ++var2) {
            int var3 = -34 + var2;
            int var4 = -18 - var2;
            int var5 = var1 + 10 + var2;

            int var6;
            for(var6 = -70; var6 <= -50; ++var6) {
                var0.set(var3, var5, var6, "dark_oak_stairs[facing=east,half=bottom,shape=straight]");
                var0.set(var4, var5, var6, "dark_oak_stairs[facing=west,half=bottom,shape=straight]");
            }

            if (var2 > 0) {
                for(var6 = var3 + 1; var6 <= var4 - 1; ++var6) {
                    var0.set(var6, var5, -69, (String)"red_terracotta");
                    var0.set(var6, var5, -51, (String)"red_terracotta");
                }
            }
        }

        var0.fill(-26, var1 + 18, -70, -26, var1 + 18, -50, (String)"dark_oak_slab[type=bottom]");
        var0.set(-26, var1 + 13, -51, (String)"white_terracotta");
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
