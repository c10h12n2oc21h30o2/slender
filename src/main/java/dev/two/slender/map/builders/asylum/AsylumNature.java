package dev.two.slender.map.builders.asylum;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.gen.Post;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.TreeType;

final class AsylumNature {
    private AsylumNature() {
    }

    static boolean inGrounds(int var0, int var1, int var2) {
        int[] var3 = AsylumLayout.GROUNDS;
        return var0 >= var3[0] - var2 && var0 <= var3[2] + var2 && var1 >= var3[1] - var2 && var1 <= var3[3] + var2;
    }

    static Material surface(Builder var0, Heightmap var1, Noise var2, int var3, int var4) {
        if (var1.is(var3, var4, (byte)1)) {
            return (Material)var0.pick(Material.GRAVEL, Material.GRAVEL, Material.GRAVEL, Material.COARSE_DIRT, Material.ANDESITE);
        } else if (inGrounds(var3, var4, 0)) {
            return (Material)var0.pick(Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.COARSE_DIRT, Material.ROOTED_DIRT);
        } else {
            return var2.value((double)var3, (double)var4, 18.0) > 0.6 ? (Material)var0.pick(Material.PODZOL, Material.COARSE_DIRT, Material.PODZOL) : (Material)var0.pick(Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.PACKED_MUD, Material.ROOTED_DIRT);
        }
    }

    static void plant(Builder var0, Heightmap var1, Noise var2) {
        int var3 = var1.radius() - 2;
        byte var4 = 76;

        int var5;
        int var6;
        int var7;
        for(var5 = -var3; var5 <= var3; var5 += 4) {
            for(var6 = -var3; var6 <= var3; var6 += 4) {
                var7 = var5 + var0.range(-1, 1);
                int var8 = var6 + var0.range(-1, 1);
                boolean var9 = Math.abs(var7) >= var4 - 1 || Math.abs(var8) >= var4 - 1;
                if ((Math.abs(var7) > 8 || var8 < var4 - 4) && !inGrounds(var7, var8, 3) && clear(var1, var7, var8, 3) && !nearPost(var7, var8, 3) && var0.chance(var9 ? 0.85 : 0.3)) {
                    int var10 = var1.at(var7, var8) + 1;
                    double var11 = var0.rnd.nextDouble();
                    if (var11 < 0.25) {
                        var0.tree(var7, var10, var8, TreeType.PALE_OAK);
                    } else if (var11 < 0.6) {
                        deadTree(var0, var7, var10, var8, "oak_log", 5, 9);
                    } else if (var11 < 0.85) {
                        deadTree(var0, var7, var10, var8, "spruce_log", 6, 11);
                    } else {
                        deadTree(var0, var7, var10, var8, "dark_oak_log", 4, 7);
                    }
                }
            }
        }

        for(var5 = -var3; var5 <= var3; ++var5) {
            for(var6 = -var3; var6 <= var3; ++var6) {
                if (clear(var1, var5, var6, 0) && !nearPlace(var5, var6) && !nearPost(var5, var6, 1) && AsylumLayout.PLAN.at(0, var5, var6) == ' ' && AsylumLayout.PLAN.at(-1, var5, var6) == ' ' && Math.abs(var5) != AsylumLayout.GROUNDS[2] && var6 != AsylumLayout.GROUNDS[1] && var6 != AsylumLayout.GROUNDS[3]) {
                    var7 = var1.at(var5, var6) + 1;
                    double var13 = var0.rnd.nextDouble();
                    if (inGrounds(var5, var6, 0)) {
                        if (var13 < 0.05) {
                            var0.set(var5, var7, var6, "short_grass");
                        } else if (var13 < 0.07) {
                            var0.set(var5, var7, var6, "dead_bush");
                        }
                    } else if (var13 < 0.05) {
                        var0.set(var5, var7, var6, "dead_bush");
                    } else if (var13 < 0.1) {
                        var0.set(var5, var7, var6, "short_grass");
                    } else if (var13 < 0.105) {
                        var0.set(var5, var7, var6, "wither_rose");
                    }
                }
            }
        }

    }

    static void deadTree(Builder var0, int var1, int var2, int var3, String var4, int var5, int var6) {
        int var7 = var0.range(var5, var6);
        var0.fill(var1, var2, var3, var1, var2 + var7 - 1, var3, var4);

        for(int var8 = var0.range(2, 3); var8 > 0; --var8) {
            int var9 = var2 + var0.range(2, Math.max(2, var7 - 2));
            boolean var10 = var0.chance(0.5);
            int var11 = var0.chance(0.5) ? 1 : -1;
            int var12 = var10 ? var11 : 0;
            int var13 = var10 ? 0 : var11;
            String var14 = var4 + "[axis=" + (var10 ? "x" : "z") + "]";
            var0.set(var1 + var12, var9, var3 + var13, var14);
            if (var0.chance(0.5)) {
                var0.set(var1 + 2 * var12, var9 + 1, var3 + 2 * var13, var14);
            }
        }

    }

    private static boolean clear(Heightmap var0, int var1, int var2, int var3) {
        for(int var4 = -var3; var4 <= var3; ++var4) {
            for(int var5 = -var3; var5 <= var3; ++var5) {
                if (var0.is(var1 + var4, var2 + var5, (byte)3)) {
                    return false;
                }
            }
        }

        return Math.abs(var1) > 9 || var2 < 60;
    }

    private static boolean nearPlace(int var0, int var1) {
        Iterator var2 = AsylumLayout.PLACES.iterator();

        Place var3;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            var3 = (Place)var2.next();
        } while(!(Math.hypot((double)(var0 - var3.x()), (double)(var1 - var3.z())) <= (double)(var3.pad() + 2)));

        return true;
    }

    private static boolean nearPost(int var0, int var1, int var2) {
        Iterator var3 = AsylumLayout.POSTS.iterator();

        Post var4;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            var4 = (Post)var3.next();
        } while(Math.abs(var0 - var4.x()) > var2 || Math.abs(var1 - var4.z()) > var2);

        return true;
    }
}
