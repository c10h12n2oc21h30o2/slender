package dev.two.slender.map.builders.cornfield;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.gen.Post;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.TreeType;

final class CornfieldNature {
    private CornfieldNature() {
    }

    static Material surface(Builder var0, Heightmap var1, boolean[][] var2, int var3, int var4) {
        if (var1.is(var3, var4, (byte)2)) {
            return (Material)var0.pick(Material.MUD, Material.GRAVEL, Material.CLAY);
        } else if (var1.is(var3, var4, (byte)1)) {
            return (Material)var0.pick(Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.COARSE_DIRT);
        } else if (var1.is(var3, var4, (byte)8)) {
            return (Material)var0.pick(Material.MUD, Material.GRASS_BLOCK, Material.COARSE_DIRT);
        } else if (CornfieldLayout.corn(var2, var1, var3, var4)) {
            return (Material)var0.pick(Material.DIRT, Material.COARSE_DIRT, Material.ROOTED_DIRT);
        } else if (CornfieldLayout.inMaze(var3, var4)) {
            return (Material)var0.pick(Material.COARSE_DIRT, Material.DIRT, Material.GRASS_BLOCK, Material.COARSE_DIRT);
        } else {
            return var1.is(var3, var4, (byte)4) && var4 < -40 ? (Material)var0.pick(Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.GRAVEL, Material.GRASS_BLOCK) : (Material)var0.pick(Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.COARSE_DIRT);
        }
    }

    static void corn(Builder var0, Heightmap var1, boolean[][] var2) {
        for(int var3 = -42; var3 <= 39; ++var3) {
            for(int var4 = -36; var4 <= 33; ++var4) {
                int var5 = var1.at(var3, var4) + 1;
                if (!CornfieldLayout.corn(var2, var1, var3, var4)) {
                    if (!var1.is(var3, var4, (byte)1) && !var1.is(var3, var4, (byte)4) && var0.chance(0.06)) {
                        var0.set(var3, var5, var4, "short_grass");
                    }
                } else {
                    String var6 = var0.chance(0.7) ? "bamboo_block[axis=y]" : "stripped_bamboo_block[axis=y]";
                    var0.set(var3, var5, var4, var6);
                    var0.set(var3, var5 + 1, var4, var6);
                    int var7 = var0.range(1, 2);

                    for(int var8 = 1; var8 <= var7; ++var8) {
                        String var9 = var8 == var7 ? "bamboo[age=1,leaves=large,stage=0]" : "bamboo[age=1,leaves=small,stage=0]";
                        var0.set(var3, var5 + 1 + var8, var4, var9);
                    }
                }
            }
        }

    }

    static void plant(Builder var0, Heightmap var1, Noise var2, List<int[]> var3) {
        int var4 = var1.radius() - 2;
        byte var5 = 76;

        int var6;
        int var7;
        int var8;
        for(var6 = -var4; var6 <= var4; var6 += 5) {
            for(var7 = -var4; var7 <= var4; var7 += 5) {
                var8 = var6 + var0.range(-1, 1);
                int var9 = var7 + var0.range(-1, 1);
                boolean var10 = Math.abs(var8) >= var5 - 1 || Math.abs(var9) >= var5 - 1;
                if ((Math.abs(var8) > 12 || var9 < 56) && clear(var1, var8, var9, 3) && !nearPost(var3, var8, var9, 3) && !nearPlace(var8, var9, 4) && (var10 || !CornfieldLayout.inMaze(var8, var9) && (Math.abs(var8) > 62 || var9 <= -48 || var9 >= 42)) && var0.chance(var10 ? 0.8 : 0.25)) {
                    int var11 = var1.at(var8, var9) + 1;
                    double var12 = var0.rnd.nextDouble();
                    if (var12 < 0.45) {
                        var0.tree(var8, var11, var9, TreeType.TREE);
                    } else if (var12 < 0.7) {
                        var0.tree(var8, var11, var9, TreeType.PALE_OAK);
                    } else if (var12 < 0.85) {
                        var0.tree(var8, var11, var9, TreeType.BIRCH);
                    } else {
                        deadTree(var0, var8, var11, var9);
                    }
                }
            }
        }

        for(var6 = -var4; var6 <= var4; ++var6) {
            for(var7 = -var4; var7 <= var4; ++var7) {
                if (!CornfieldLayout.inMaze(var6, var7) && clear(var1, var6, var7, 0) && !nearPost(var3, var6, var7, 1) && !nearPlace(var6, var7, 1) && Math.abs(var6) != var5 && Math.abs(var7) != var5 && (Math.abs(var6) > 9 || var7 < 60) && !nearBattery(var6, var7)) {
                    var8 = var1.at(var6, var7) + 1;
                    double var14 = var0.rnd.nextDouble();
                    if (var14 < 0.12) {
                        var0.set(var6, var8, var7, "short_grass");
                    } else if (var14 < 0.135) {
                        var0.set(var6, var8, var7, "dead_bush");
                    } else if (var14 < 0.14 && var2.value((double)var6, (double)var7, 16.0) > 0.5) {
                        var0.set(var6, var8, var7, "hay_block[axis=y]");
                    }
                }
            }
        }

    }

    static void deadTree(Builder var0, int var1, int var2, int var3) {
        int var4 = var0.range(4, 7);
        var0.fill(var1, var2, var3, var1, var2 + var4 - 1, var3, "oak_log");

        for(int var5 = 0; var5 < 2; ++var5) {
            int var6 = var2 + var0.range(2, var4 - 2);
            boolean var7 = var0.chance(0.5);
            int var8 = var0.chance(0.5) ? 1 : -1;
            int var9 = var7 ? var8 : 0;
            int var10 = var7 ? 0 : var8;
            String var11 = var7 ? "oak_log[axis=x]" : "oak_log[axis=z]";
            var0.set(var1 + var9, var6, var3 + var10, var11);
        }

    }

    private static boolean clear(Heightmap var0, int var1, int var2, int var3) {
        for(int var4 = -var3; var4 <= var3; ++var4) {
            for(int var5 = -var3; var5 <= var3; ++var5) {
                if (var0.is(var1 + var4, var2 + var5, (byte)11)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean nearBattery(int var0, int var1) {
        int[][] var2 = CornfieldLayout.BATTERIES_OUT;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int[] var5 = var2[var4];
            if (Math.abs(var0 - var5[0]) <= 1 && Math.abs(var1 - var5[1]) <= 1) {
                return true;
            }
        }

        return false;
    }

    private static boolean nearPlace(int var0, int var1, int var2) {
        Iterator var3 = CornfieldLayout.all().iterator();

        Place var4;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            var4 = (Place)var3.next();
        } while(!(Math.hypot((double)(var0 - var4.x()), (double)(var1 - var4.z())) <= (double)(var4.pad() + var2)));

        return true;
    }

    private static boolean nearPost(List<int[]> var0, int var1, int var2, int var3) {
        Iterator var4 = CornfieldLayout.POSTS.iterator();

        Post var5;
        do {
            if (!var4.hasNext()) {
                var4 = var0.iterator();

                int[] var6;
                do {
                    if (!var4.hasNext()) {
                        return false;
                    }

                    var6 = (int[])var4.next();
                } while(Math.abs(var1 - var6[0]) > var3 || Math.abs(var2 - var6[1]) > var3);

                return true;
            }

            var5 = (Post)var4.next();
        } while(Math.abs(var1 - var5.x()) > var3 || Math.abs(var2 - var5.z()) > var3);

        return true;
    }
}
