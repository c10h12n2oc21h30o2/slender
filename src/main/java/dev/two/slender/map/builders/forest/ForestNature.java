package dev.two.slender.map.builders.forest;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.Post;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.TreeType;

final class ForestNature {
    private static final byte BLOCKED = 7;
    private static final Material[] STONE;

    private ForestNature() {
    }

    static Material surface(Builder var0, Heightmap var1, Noise var2, int var3, int var4) {
        if (var1.is(var3, var4, (byte)2)) {
            return (Material)var0.pick(Material.MUD, Material.GRAVEL, Material.CLAY, Material.MUD);
        } else if (var1.is(var3, var4, (byte)1)) {
            return (Material)var0.pick(Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.COARSE_DIRT, Material.GRAVEL);
        } else if (var1.is(var3, var4, (byte)8)) {
            return (Material)var0.pick(Material.MUD, Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.MOSS_BLOCK);
        } else {
            Material var10000;
            switch (ForestLayout.biome(var3, var4, var2)) {
                case PINE -> var10000 = (Material)var0.pick(Material.PODZOL, Material.PODZOL, Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.MOSS_BLOCK);
                case BURNT -> var10000 = (Material)var0.pick(Material.COARSE_DIRT, Material.COARSE_DIRT, Material.SOUL_SOIL, Material.PACKED_MUD, Material.ROOTED_DIRT);
                case DEAD -> var10000 = (Material)var0.pick(Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.ROOTED_DIRT, Material.PODZOL);
                case SWAMP -> var10000 = (Material)var0.pick(Material.MUD, Material.GRASS_BLOCK, Material.MOSS_BLOCK, Material.MUDDY_MANGROVE_ROOTS, Material.GRASS_BLOCK);
                case MIXED -> var10000 = (Material)var0.pick(Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.PODZOL, Material.COARSE_DIRT, Material.ROOTED_DIRT);
                default -> throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
        }
    }

    static void plant(Builder var0, Heightmap var1, Noise var2) {
        int var3 = var1.radius() - 2;
        byte var4 = 80;
        HashSet var5 = new HashSet();

        int var6;
        int var7;
        for(var6 = -var3; var6 <= var3; var6 += 4) {
            for(var7 = -var3; var7 <= var3; var7 += 4) {
                int var8 = var6 + var0.range(-1, 1);
                int var9 = var7 + var0.range(-1, 1);
                boolean var10 = Math.abs(var8) >= var4 - 1 || Math.abs(var9) >= var4 - 1;
                if ((Math.abs(var8) > 8 || var9 < var4 - 4) && clear(var1, var8, var9, 3) && !nearPost(var8, var9, 3) && (var10 || !(var2.value((double)(var8 + 500), (double)(var9 - 500), 22.0) > 0.5))) {
                    ForestLayout.Biome var11 = ForestLayout.biome(var8, var9, var2);
                    if (var0.chance(var10 ? 0.9 : density(var11))) {
                        tree(var0, var11, var8, var1.at(var8, var9) + 1, var9);
                        var5.add(key(var8, var9));
                    }
                }
            }
        }

        for(var6 = -var3; var6 <= var3; ++var6) {
            for(var7 = -var3; var7 <= var3; ++var7) {
                if (var1.is(var6, var7, (byte)2)) {
                    if (!var1.is(var6, var7, (byte)1) && ForestLayout.biome(var6, var7, var2) == ForestLayout.Biome.SWAMP && var0.chance(0.06)) {
                        var0.set(var6, var1.waterLevel() + 1, var7, "lily_pad");
                    }
                } else if (clear(var1, var6, var7, 0) && !nearTrunk(var5, var6, var7) && !nearPost(var6, var7, 1)) {
                    undergrowth(var0, ForestLayout.biome(var6, var7, var2), var6, var1.at(var6, var7) + 1, var7);
                }
            }
        }

        logs(var0, var1, var2, var5);
        rocks(var0, var1, var5);
    }

    private static double density(ForestLayout.Biome var0) {
        double var10000;
        switch (var0) {
            case PINE -> var10000 = 0.8;
            case BURNT -> var10000 = 0.45;
            case DEAD -> var10000 = 0.5;
            case SWAMP -> var10000 = 0.35;
            case MIXED -> var10000 = 0.55;
            default -> throw new MatchException((String)null, (Throwable)null);
        }

        return var10000;
    }

    private static void tree(Builder var0, ForestLayout.Biome var1, int var2, int var3, int var4) {
        switch (var1) {
            case PINE:
                var0.tree(var2, var3, var4, (TreeType)var0.pick(TreeType.REDWOOD, TreeType.TALL_REDWOOD, TreeType.TALL_REDWOOD, TreeType.MEGA_PINE));
                break;
            case BURNT:
                charred(var0, var2, var3, var4);
                break;
            case DEAD:
                deadTree(var0, var2, var3, var4, "birch_log", 6, 10);
                break;
            case SWAMP:
                if (var0.chance(0.6)) {
                    var0.tree(var2, var3, var4, TreeType.SWAMP);
                } else {
                    deadTree(var0, var2, var3, var4, "oak_log", 4, 7);
                }
                break;
            case MIXED:
                var0.tree(var2, var3, var4, (TreeType)var0.pick(TreeType.TREE, TreeType.TREE, TreeType.BIRCH, TreeType.REDWOOD, TreeType.TALL_BIRCH));
        }

    }

    private static void deadTree(Builder var0, int var1, int var2, int var3, String var4, int var5, int var6) {
        int var7 = var0.range(var5, var6);
        var0.fill(var1, var2, var3, var1, var2 + var7 - 1, var3, var4);

        for(int var8 = var0.range(2, 3); var8 > 0; --var8) {
            int var9 = var2 + var0.range(2, var7 - 2);
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

    private static void charred(Builder var0, int var1, int var2, int var3) {
        int var4 = var0.chance(0.3) ? var0.range(1, 2) : var0.range(4, 8);
        var0.fill(var1, var2, var3, var1, var2 + var4 - 1, var3, "dark_oak_wood");
        if (var4 > 3 && var0.chance(0.6)) {
            var0.set(var1 + (var0.chance(0.5) ? 1 : -1), var2 + var4 - 2, var3, "dark_oak_wood");
        }

    }

    private static void undergrowth(Builder var0, ForestLayout.Biome var1, int var2, int var3, int var4) {
        double var5 = var0.rnd.nextDouble();
        switch (var1) {
            case PINE:
                if (var5 < 0.07) {
                    var0.set(var2, var3, var4, "fern");
                } else if (var5 < 0.09) {
                    tall(var0, var2, var3, var4, "large_fern");
                } else if (var5 < 0.1) {
                    var0.set(var2, var3, var4, "brown_mushroom");
                } else if (var5 < 0.13) {
                    var0.set(var2, var3, var4, "moss_carpet");
                }
                break;
            case BURNT:
                if (var5 < 0.04) {
                    var0.set(var2, var3, var4, "dead_bush");
                } else if (var5 < 0.05) {
                    var0.set(var2, var3, var4, "short_grass");
                }
                break;
            case DEAD:
                if (var5 < 0.05) {
                    var0.set(var2, var3, var4, "dead_bush");
                } else if (var5 < 0.1) {
                    var0.set(var2, var3, var4, "short_grass");
                }
                break;
            case SWAMP:
                if (var5 < 0.08) {
                    var0.set(var2, var3, var4, "short_grass");
                } else if (var5 < 0.11) {
                    var0.set(var2, var3, var4, "fern");
                } else if (var5 < 0.15) {
                    var0.set(var2, var3, var4, "moss_carpet");
                } else if (var5 < 0.16) {
                    var0.set(var2, var3, var4, "brown_mushroom");
                }
                break;
            case MIXED:
                if (var5 < 0.1) {
                    var0.set(var2, var3, var4, "short_grass");
                } else if (var5 < 0.12) {
                    var0.set(var2, var3, var4, "fern");
                } else if (var5 < 0.135) {
                    tall(var0, var2, var3, var4, "tall_grass");
                } else if (var5 < 0.14) {
                    var0.set(var2, var3, var4, "wither_rose");
                }
        }

    }

    private static void tall(Builder var0, int var1, int var2, int var3, String var4) {
        var0.set(var1, var2, var3, var4 + "[half=lower]");
        var0.set(var1, var2 + 1, var3, var4 + "[half=upper]");
    }

    private static void logs(Builder var0, Heightmap var1, Noise var2, Set<Long> var3) {
        byte var4 = 77;

        for(int var5 = 0; var5 < 45; ++var5) {
            int var6 = var0.range(-var4, var4);
            int var7 = var0.range(-var4, var4);
            int var8 = var0.range(3, 6);
            boolean var9 = var0.chance(0.5);
            ForestLayout.Biome var10 = ForestLayout.biome(var6, var7, var2);
            if (var10 != ForestLayout.Biome.SWAMP) {
                int var11 = var1.at(var6, var7);
                boolean var12 = true;

                int var15;
                for(int var13 = 0; var13 < var8 && var12; ++var13) {
                    int var14 = var6 + (var9 ? var13 : 0);
                    var15 = var7 + (var9 ? 0 : var13);
                    var12 = clear(var1, var14, var15, 1) && var1.at(var14, var15) == var11 && !nearTrunk(var3, var14, var15) && !nearPost(var14, var15, 2);
                }

                if (var12) {
                    String var10000;
                    switch (var10) {
                        case PINE -> var10000 = "spruce_log";
                        case BURNT -> var10000 = "dark_oak_wood";
                        case DEAD -> var10000 = "birch_log";
                        default -> var10000 = "oak_log";
                    }

                    String var16 = var10000;
                    String var17 = var16 + "[axis=" + (var9 ? "x" : "z") + "]";

                    for(var15 = 0; var15 < var8; ++var15) {
                        var0.set(var6 + (var9 ? var15 : 0), var11 + 1, var7 + (var9 ? 0 : var15), var17);
                    }
                }
            }
        }

    }

    private static void rocks(Builder var0, Heightmap var1, Set<Long> var2) {
        byte var3 = 77;

        for(int var4 = 0; var4 < 30; ++var4) {
            int var5 = var0.range(-var3, var3);
            int var6 = var0.range(-var3, var3);
            if (clear(var1, var5, var6, 2) && !nearTrunk(var2, var5, var6) && !nearPost(var5, var6, 3)) {
                for(int var7 = -1; var7 <= 1; ++var7) {
                    for(int var8 = -1; var8 <= 1; ++var8) {
                        if (var0.chance(0.7)) {
                            var0.set(var5 + var7, var1.at(var5 + var7, var6 + var8) + 1, var6 + var8, (Material)var0.pick(STONE));
                        }
                    }
                }

                if (var0.chance(0.6)) {
                    var0.set(var5, var1.at(var5, var6) + 2, var6, (Material)var0.pick(STONE));
                }
            }
        }

    }

    static boolean clear(Heightmap var0, int var1, int var2, int var3) {
        for(int var4 = -var3; var4 <= var3; ++var4) {
            for(int var5 = -var3; var5 <= var3; ++var5) {
                if (var0.is(var1 + var4, var2 + var5, (byte)7)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean nearPost(int var0, int var1, int var2) {
        Iterator var3 = ForestLayout.POSTS.iterator();

        Post var4;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            var4 = (Post)var3.next();
        } while(Math.abs(var4.x() - var0) > var2 || Math.abs(var4.z() - var1) > var2);

        return true;
    }

    private static boolean nearTrunk(Set<Long> var0, int var1, int var2) {
        for(int var3 = -1; var3 <= 1; ++var3) {
            for(int var4 = -1; var4 <= 1; ++var4) {
                if (var0.contains(key(var1 + var3, var2 + var4))) {
                    return true;
                }
            }
        }

        return false;
    }

    private static long key(int var0, int var1) {
        return (long)var0 << 32 ^ (long)var1 & 4294967295L;
    }

    static {
        STONE = new Material[]{Material.MOSSY_COBBLESTONE, Material.COBBLESTONE, Material.ANDESITE, Material.STONE, Material.MOSSY_COBBLESTONE};
    }
}
