// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.forest;

import java.util.Iterator;
import dev.two.slender.map.gen.Post;
import org.bukkit.TreeType;
import java.util.Set;
import java.util.HashSet;
import dev.two.slender.logic.Noise;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;
import org.bukkit.Material;

final class ForestNature
{
    private static final byte BLOCKED = 7;
    private static final Material[] STONE;
    
    private ForestNature() {
    }
    
    static Material surface(final Builder builder, final Heightmap heightmap, final Noise noise, final int n, final int n2) {
        if (heightmap.is(n, n2, (byte)2)) {
            return builder.pick(Material.MUD, Material.GRAVEL, Material.CLAY, Material.MUD);
        }
        if (heightmap.is(n, n2, (byte)1)) {
            return builder.pick(Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.COARSE_DIRT, Material.GRAVEL);
        }
        if (heightmap.is(n, n2, (byte)8)) {
            return builder.pick(Material.MUD, Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.MOSS_BLOCK);
        }
        return switch (ForestLayout.biome(n, n2, noise)) {
            default -> throw new MatchException(null, null);
            case PINE -> builder.pick(Material.PODZOL, Material.PODZOL, Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.MOSS_BLOCK);
            case BURNT -> builder.pick(Material.COARSE_DIRT, Material.COARSE_DIRT, Material.SOUL_SOIL, Material.PACKED_MUD, Material.ROOTED_DIRT);
            case DEAD -> builder.pick(Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.ROOTED_DIRT, Material.PODZOL);
            case SWAMP -> builder.pick(Material.MUD, Material.GRASS_BLOCK, Material.MOSS_BLOCK, Material.MUDDY_MANGROVE_ROOTS, Material.GRASS_BLOCK);
            case MIXED -> builder.pick(Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.PODZOL, Material.COARSE_DIRT, Material.ROOTED_DIRT);
        };
    }
    
    static void plant(final Builder builder, final Heightmap heightmap, final Noise noise) {
        final int n = heightmap.radius() - 2;
        final int n2 = 80;
        final HashSet set = new HashSet();
        for (int i = -n; i <= n; i += 4) {
            for (int j = -n; j <= n; j += 4) {
                final int n3 = i + builder.range(-1, 1);
                final int a = j + builder.range(-1, 1);
                final boolean b = Math.abs(n3) >= n2 - 1 || Math.abs(a) >= n2 - 1;
                if (Math.abs(n3) > 8 || a < n2 - 4) {
                    if (clear(heightmap, n3, a, 3)) {
                        if (!nearPost(n3, a, 3)) {
                            if (b || noise.value(n3 + 500, a - 500, 22.0) <= 0.5) {
                                final ForestLayout.Biome biome = ForestLayout.biome(n3, a, noise);
                                if (builder.chance(b ? 0.9 : density(biome))) {
                                    tree(builder, biome, n3, heightmap.at(n3, a) + 1, a);
                                    set.add(key(n3, a));
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int k = -n; k <= n; ++k) {
            for (int l = -n; l <= n; ++l) {
                if (heightmap.is(k, l, (byte)2)) {
                    if (!heightmap.is(k, l, (byte)1) && ForestLayout.biome(k, l, noise) == ForestLayout.Biome.SWAMP && builder.chance(0.06)) {
                        builder.set(k, heightmap.waterLevel() + 1, l, "lily_pad");
                    }
                }
                else if (clear(heightmap, k, l, 0) && !nearTrunk(set, k, l)) {
                    if (!nearPost(k, l, 1)) {
                        undergrowth(builder, ForestLayout.biome(k, l, noise), k, heightmap.at(k, l) + 1, l);
                    }
                }
            }
        }
        logs(builder, heightmap, noise, set);
        rocks(builder, heightmap, set);
    }
    
    private static double density(final ForestLayout.Biome biome) {
        return switch (biome) {
            default -> throw new MatchException(null, null);
            case PINE -> 0.8;
            case MIXED -> 0.55;
            case DEAD -> 0.5;
            case BURNT -> 0.45;
            case SWAMP -> 0.35;
        };
    }
    
    private static void tree(final Builder builder, final ForestLayout.Biome biome, final int n, final int n2, final int n3) {
        switch (biome) {
            case PINE: {
                builder.tree(n, n2, n3, builder.pick(TreeType.REDWOOD, TreeType.TALL_REDWOOD, TreeType.TALL_REDWOOD, TreeType.MEGA_PINE));
                break;
            }
            case MIXED: {
                builder.tree(n, n2, n3, builder.pick(TreeType.TREE, TreeType.TREE, TreeType.BIRCH, TreeType.REDWOOD, TreeType.TALL_BIRCH));
                break;
            }
            case DEAD: {
                deadTree(builder, n, n2, n3, "birch_log", 6, 10);
                break;
            }
            case BURNT: {
                charred(builder, n, n2, n3);
                break;
            }
            case SWAMP: {
                if (builder.chance(0.6)) {
                    builder.tree(n, n2, n3, TreeType.SWAMP);
                    break;
                }
                deadTree(builder, n, n2, n3, "oak_log", 4, 7);
                break;
            }
        }
    }
    
    private static void deadTree(final Builder builder, final int n, final int n2, final int n3, final String s, final int n4, final int n5) {
        final int range = builder.range(n4, n5);
        builder.fill(n, n2, n3, n, n2 + range - 1, n3, s);
        for (int i = builder.range(2, 3); i > 0; --i) {
            final int n6 = n2 + builder.range(2, range - 2);
            final int chance = builder.chance(0.5) ? 1 : 0;
            final int n7 = builder.chance(0.5) ? 1 : -1;
            final int n8 = (chance != 0) ? n7 : 0;
            final int n9 = (chance != 0) ? 0 : n7;
            final String s2 = s + "[axis=" + ((chance != 0) ? "x" : "z");
            builder.set(n + n8, n6, n3 + n9, s2);
            if (builder.chance(0.5)) {
                builder.set(n + 2 * n8, n6 + 1, n3 + 2 * n9, s2);
            }
        }
    }
    
    private static void charred(final Builder builder, final int n, final int n2, final int n3) {
        final int n4 = builder.chance(0.3) ? builder.range(1, 2) : builder.range(4, 8);
        builder.fill(n, n2, n3, n, n2 + n4 - 1, n3, "dark_oak_wood");
        if (n4 > 3 && builder.chance(0.6)) {
            builder.set(n + (builder.chance(0.5) ? 1 : -1), n2 + n4 - 2, n3, "dark_oak_wood");
        }
    }
    
    private static void undergrowth(final Builder builder, final ForestLayout.Biome biome, final int n, final int n2, final int n3) {
        final double nextDouble = builder.rnd.nextDouble();
        switch (biome) {
            case PINE: {
                if (nextDouble < 0.07) {
                    builder.set(n, n2, n3, "fern");
                    break;
                }
                if (nextDouble < 0.09) {
                    tall(builder, n, n2, n3, "large_fern");
                    break;
                }
                if (nextDouble < 0.1) {
                    builder.set(n, n2, n3, "brown_mushroom");
                    break;
                }
                if (nextDouble < 0.13) {
                    builder.set(n, n2, n3, "moss_carpet");
                    break;
                }
                break;
            }
            case MIXED: {
                if (nextDouble < 0.1) {
                    builder.set(n, n2, n3, "short_grass");
                    break;
                }
                if (nextDouble < 0.12) {
                    builder.set(n, n2, n3, "fern");
                    break;
                }
                if (nextDouble < 0.135) {
                    tall(builder, n, n2, n3, "tall_grass");
                    break;
                }
                if (nextDouble < 0.14) {
                    builder.set(n, n2, n3, "wither_rose");
                    break;
                }
                break;
            }
            case DEAD: {
                if (nextDouble < 0.05) {
                    builder.set(n, n2, n3, "dead_bush");
                    break;
                }
                if (nextDouble < 0.1) {
                    builder.set(n, n2, n3, "short_grass");
                    break;
                }
                break;
            }
            case BURNT: {
                if (nextDouble < 0.04) {
                    builder.set(n, n2, n3, "dead_bush");
                    break;
                }
                if (nextDouble < 0.05) {
                    builder.set(n, n2, n3, "short_grass");
                    break;
                }
                break;
            }
            case SWAMP: {
                if (nextDouble < 0.08) {
                    builder.set(n, n2, n3, "short_grass");
                    break;
                }
                if (nextDouble < 0.11) {
                    builder.set(n, n2, n3, "fern");
                    break;
                }
                if (nextDouble < 0.15) {
                    builder.set(n, n2, n3, "moss_carpet");
                    break;
                }
                if (nextDouble < 0.16) {
                    builder.set(n, n2, n3, "brown_mushroom");
                    break;
                }
                break;
            }
        }
    }
    
    private static void tall(final Builder builder, final int n, final int n2, final int n3, final String s) {
        builder.set(n, n2, n3, s + "[half=lower]");
        builder.set(n, n2 + 1, n3, s + "[half=upper]");
    }
    
    private static void logs(final Builder builder, final Heightmap heightmap, final Noise noise, final Set<Long> set) {
        final int n = 77;
        for (int i = 0; i < 45; ++i) {
            final int range = builder.range(-n, n);
            final int range2 = builder.range(-n, n);
            final int range3 = builder.range(3, 6);
            final boolean chance = builder.chance(0.5);
            final ForestLayout.Biome biome = ForestLayout.biome(range, range2, noise);
            if (biome != ForestLayout.Biome.SWAMP) {
                final int at = heightmap.at(range, range2);
                boolean b = true;
                int n3;
                int n4;
                for (int n2 = 0; n2 < range3 && b; b = (clear(heightmap, n3, n4, 1) && heightmap.at(n3, n4) == at && !nearTrunk(set, n3, n4) && !nearPost(n3, n4, 2)), ++n2) {
                    n3 = range + (chance ? n2 : 0);
                    n4 = range2 + (chance ? 0 : n2);
                }
                if (b) {
                    final String s2 = switch (biome) {
                        case PINE -> "spruce_log";
                        case DEAD -> "birch_log";
                        case BURNT -> "dark_oak_wood";
                        default -> "oak_log";
                    } + "[axis=" + (chance ? "x" : "z");
                    for (int j = 0; j < range3; ++j) {
                        builder.set(range + (chance ? j : false), at + 1, range2 + (chance ? 0 : j), s2);
                    }
                }
            }
        }
    }
    
    private static void rocks(final Builder builder, final Heightmap heightmap, final Set<Long> set) {
        final int n = 77;
        for (int i = 0; i < 30; ++i) {
            final int range = builder.range(-n, n);
            final int range2 = builder.range(-n, n);
            if (clear(heightmap, range, range2, 2) && !nearTrunk(set, range, range2)) {
                if (!nearPost(range, range2, 3)) {
                    for (int j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if (builder.chance(0.7)) {
                                builder.set(range + j, heightmap.at(range + j, range2 + k) + 1, range2 + k, builder.pick(ForestNature.STONE));
                            }
                        }
                    }
                    if (builder.chance(0.6)) {
                        builder.set(range, heightmap.at(range, range2) + 2, range2, builder.pick(ForestNature.STONE));
                    }
                }
            }
        }
    }
    
    static boolean clear(final Heightmap heightmap, final int n, final int n2, final int n3) {
        for (int i = -n3; i <= n3; ++i) {
            for (int j = -n3; j <= n3; ++j) {
                if (heightmap.is(n + i, n2 + j, (byte)7)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean nearPost(final int n, final int n2, final int n3) {
        for (final Post post : ForestLayout.POSTS) {
            if (Math.abs(post.x() - n) <= n3 && Math.abs(post.z() - n2) <= n3) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean nearTrunk(final Set<Long> set, final int n, final int n2) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (set.contains(key(n + i, n2 + j))) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static long key(final int n, final int n2) {
        return (long)n << 32 ^ ((long)n2 & 0xFFFFFFFFL);
    }
    
    static {
        STONE = new Material[] { Material.MOSSY_COBBLESTONE, Material.COBBLESTONE, Material.ANDESITE, Material.STONE, Material.MOSSY_COBBLESTONE };
    }
}
