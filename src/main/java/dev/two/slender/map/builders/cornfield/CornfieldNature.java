// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.cornfield;

import dev.two.slender.map.gen.Post;
import java.util.Iterator;
import dev.two.slender.map.gen.Place;
import org.bukkit.TreeType;
import java.util.List;
import dev.two.slender.logic.Noise;
import org.bukkit.Material;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;

final class CornfieldNature
{
    private CornfieldNature() {
    }
    
    static Material surface(final Builder builder, final Heightmap heightmap, final boolean[][] array, final int n, final int n2) {
        if (heightmap.is(n, n2, (byte)2)) {
            return builder.pick(Material.MUD, Material.GRAVEL, Material.CLAY);
        }
        if (heightmap.is(n, n2, (byte)1)) {
            return builder.pick(Material.DIRT_PATH, Material.DIRT_PATH, Material.DIRT_PATH, Material.COARSE_DIRT);
        }
        if (heightmap.is(n, n2, (byte)8)) {
            return builder.pick(Material.MUD, Material.GRASS_BLOCK, Material.COARSE_DIRT);
        }
        if (CornfieldLayout.corn(array, heightmap, n, n2)) {
            return builder.pick(Material.DIRT, Material.COARSE_DIRT, Material.ROOTED_DIRT);
        }
        if (CornfieldLayout.inMaze(n, n2)) {
            return builder.pick(Material.COARSE_DIRT, Material.DIRT, Material.GRASS_BLOCK, Material.COARSE_DIRT);
        }
        if (heightmap.is(n, n2, (byte)4) && n2 < -40) {
            return builder.pick(Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.GRAVEL, Material.GRASS_BLOCK);
        }
        return builder.pick(Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.COARSE_DIRT);
    }
    
    static void corn(final Builder builder, final Heightmap heightmap, final boolean[][] array) {
        for (int i = -42; i <= 39; ++i) {
            for (int j = -36; j <= 33; ++j) {
                final int n = heightmap.at(i, j) + 1;
                if (!CornfieldLayout.corn(array, heightmap, i, j)) {
                    if (!heightmap.is(i, j, (byte)1) && !heightmap.is(i, j, (byte)4) && builder.chance(0.06)) {
                        builder.set(i, n, j, "short_grass");
                    }
                }
                else {
                    final String s = builder.chance(0.7) ? "bamboo_block[axis=y]" : "stripped_bamboo_block[axis=y]";
                    builder.set(i, n, j, s);
                    builder.set(i, n + 1, j, s);
                    for (int range = builder.range(1, 2), k = 1; k <= range; ++k) {
                        builder.set(i, n + 1 + k, j, (k == range) ? "bamboo[age=1,leaves=large,stage=0]" : "bamboo[age=1,leaves=small,stage=0]");
                    }
                }
            }
        }
    }
    
    static void plant(final Builder builder, final Heightmap heightmap, final Noise noise, final List<int[]> list) {
        final int n = heightmap.radius() - 2;
        final int n2 = 76;
        for (int i = -n; i <= n; i += 5) {
            for (int j = -n; j <= n; j += 5) {
                final int a = i + builder.range(-1, 1);
                final int a2 = j + builder.range(-1, 1);
                final boolean b = Math.abs(a) >= n2 - 1 || Math.abs(a2) >= n2 - 1;
                if (Math.abs(a) > 12 || a2 < 56) {
                    if (clear(heightmap, a, a2, 3) && !nearPost(list, a, a2, 3)) {
                        if (!nearPlace(a, a2, 4)) {
                            if (!b) {
                                if (CornfieldLayout.inMaze(a, a2)) {
                                    continue;
                                }
                                if (Math.abs(a) <= 62 && a2 > -48 && a2 < 42) {
                                    continue;
                                }
                            }
                            if (builder.chance(b ? 0.8 : 0.25)) {
                                final int n3 = heightmap.at(a, a2) + 1;
                                final double nextDouble = builder.rnd.nextDouble();
                                if (nextDouble < 0.45) {
                                    builder.tree(a, n3, a2, TreeType.TREE);
                                }
                                else if (nextDouble < 0.7) {
                                    builder.tree(a, n3, a2, TreeType.PALE_OAK);
                                }
                                else if (nextDouble < 0.85) {
                                    builder.tree(a, n3, a2, TreeType.BIRCH);
                                }
                                else {
                                    deadTree(builder, a, n3, a2);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int k = -n; k <= n; ++k) {
            for (int l = -n; l <= n; ++l) {
                if (!CornfieldLayout.inMaze(k, l) && clear(heightmap, k, l, 0) && !nearPost(list, k, l, 1)) {
                    if (!nearPlace(k, l, 1)) {
                        if (Math.abs(k) != n2 && Math.abs(l) != n2 && (Math.abs(k) > 9 || l < 60)) {
                            if (!nearBattery(k, l)) {
                                final int n4 = heightmap.at(k, l) + 1;
                                final double nextDouble2 = builder.rnd.nextDouble();
                                if (nextDouble2 < 0.12) {
                                    builder.set(k, n4, l, "short_grass");
                                }
                                else if (nextDouble2 < 0.135) {
                                    builder.set(k, n4, l, "dead_bush");
                                }
                                else if (nextDouble2 < 0.14 && noise.value(k, l, 16.0) > 0.5) {
                                    builder.set(k, n4, l, "hay_block[axis=y]");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static void deadTree(final Builder builder, final int n, final int n2, final int n3) {
        final int range = builder.range(4, 7);
        builder.fill(n, n2, n3, n, n2 + range - 1, n3, "oak_log");
        for (int i = 0; i < 2; ++i) {
            final int n4 = n2 + builder.range(2, range - 2);
            final boolean chance = builder.chance(0.5);
            final int n5 = builder.chance(0.5) ? 1 : -1;
            builder.set(n + (chance ? n5 : false), n4, n3 + (chance ? 0 : n5), chance ? "oak_log[axis=x]" : "oak_log[axis=z]");
        }
    }
    
    private static boolean clear(final Heightmap heightmap, final int n, final int n2, final int n3) {
        for (int i = -n3; i <= n3; ++i) {
            for (int j = -n3; j <= n3; ++j) {
                if (heightmap.is(n + i, n2 + j, (byte)11)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean nearBattery(final int n, final int n2) {
        for (final int[] array : CornfieldLayout.BATTERIES_OUT) {
            if (Math.abs(n - array[0]) <= 1 && Math.abs(n2 - array[1]) <= 1) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean nearPlace(final int n, final int n2, final int n3) {
        for (final Place place : CornfieldLayout.all()) {
            if (Math.hypot(n - place.x(), n2 - place.z()) <= place.pad() + n3) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean nearPost(final List<int[]> list, final int n, final int n2, final int n3) {
        for (final Post post : CornfieldLayout.POSTS) {
            if (Math.abs(n - post.x()) <= n3 && Math.abs(n2 - post.z()) <= n3) {
                return true;
            }
        }
        for (final int[] array : list) {
            if (Math.abs(n - array[0]) <= n3 && Math.abs(n2 - array[1]) <= n3) {
                return true;
            }
        }
        return false;
    }
}
