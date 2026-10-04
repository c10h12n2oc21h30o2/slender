// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.asylum;

import dev.two.slender.map.gen.Post;
import java.util.Iterator;
import dev.two.slender.map.gen.Place;
import org.bukkit.TreeType;
import org.bukkit.Material;
import dev.two.slender.logic.Noise;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Builder;

final class AsylumNature
{
    private AsylumNature() {
    }
    
    static boolean inGrounds(final int n, final int n2, final int n3) {
        final int[] grounds = AsylumLayout.GROUNDS;
        return n >= grounds[0] - n3 && n <= grounds[2] + n3 && n2 >= grounds[1] - n3 && n2 <= grounds[3] + n3;
    }
    
    static Material surface(final Builder builder, final Heightmap heightmap, final Noise noise, final int n, final int n2) {
        if (heightmap.is(n, n2, (byte)1)) {
            return builder.pick(Material.GRAVEL, Material.GRAVEL, Material.GRAVEL, Material.COARSE_DIRT, Material.ANDESITE);
        }
        if (inGrounds(n, n2, 0)) {
            return builder.pick(Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.COARSE_DIRT, Material.ROOTED_DIRT);
        }
        if (noise.value(n, n2, 18.0) > 0.6) {
            return builder.pick(Material.PODZOL, Material.COARSE_DIRT, Material.PODZOL);
        }
        return builder.pick(Material.COARSE_DIRT, Material.GRASS_BLOCK, Material.GRASS_BLOCK, Material.PACKED_MUD, Material.ROOTED_DIRT);
    }
    
    static void plant(final Builder builder, final Heightmap heightmap, final Noise noise) {
        final int n = heightmap.radius() - 2;
        final int n2 = 76;
        for (int i = -n; i <= n; i += 4) {
            for (int j = -n; j <= n; j += 4) {
                final int n3 = i + builder.range(-1, 1);
                final int a = j + builder.range(-1, 1);
                final boolean b = Math.abs(n3) >= n2 - 1 || Math.abs(a) >= n2 - 1;
                if (Math.abs(n3) > 8 || a < n2 - 4) {
                    if (!inGrounds(n3, a, 3) && clear(heightmap, n3, a, 3)) {
                        if (!nearPost(n3, a, 3)) {
                            if (builder.chance(b ? 0.85 : 0.3)) {
                                final int n4 = heightmap.at(n3, a) + 1;
                                final double nextDouble = builder.rnd.nextDouble();
                                if (nextDouble < 0.25) {
                                    builder.tree(n3, n4, a, TreeType.PALE_OAK);
                                }
                                else if (nextDouble < 0.6) {
                                    deadTree(builder, n3, n4, a, "oak_log", 5, 9);
                                }
                                else if (nextDouble < 0.85) {
                                    deadTree(builder, n3, n4, a, "spruce_log", 6, 11);
                                }
                                else {
                                    deadTree(builder, n3, n4, a, "dark_oak_log", 4, 7);
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int k = -n; k <= n; ++k) {
            for (int l = -n; l <= n; ++l) {
                if (clear(heightmap, k, l, 0) && !nearPlace(k, l)) {
                    if (!nearPost(k, l, 1)) {
                        if (AsylumLayout.PLAN.at(0, k, l) == ' ') {
                            if (AsylumLayout.PLAN.at(-1, k, l) == ' ') {
                                if (Math.abs(k) != AsylumLayout.GROUNDS[2] && l != AsylumLayout.GROUNDS[1]) {
                                    if (l != AsylumLayout.GROUNDS[3]) {
                                        final int n5 = heightmap.at(k, l) + 1;
                                        final double nextDouble2 = builder.rnd.nextDouble();
                                        if (inGrounds(k, l, 0)) {
                                            if (nextDouble2 < 0.05) {
                                                builder.set(k, n5, l, "short_grass");
                                            }
                                            else if (nextDouble2 < 0.07) {
                                                builder.set(k, n5, l, "dead_bush");
                                            }
                                        }
                                        else if (nextDouble2 < 0.05) {
                                            builder.set(k, n5, l, "dead_bush");
                                        }
                                        else if (nextDouble2 < 0.1) {
                                            builder.set(k, n5, l, "short_grass");
                                        }
                                        else if (nextDouble2 < 0.105) {
                                            builder.set(k, n5, l, "wither_rose");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    static void deadTree(final Builder builder, final int n, final int n2, final int n3, final String s, final int n4, final int n5) {
        final int range = builder.range(n4, n5);
        builder.fill(n, n2, n3, n, n2 + range - 1, n3, s);
        for (int i = builder.range(2, 3); i > 0; --i) {
            final int n6 = n2 + builder.range(2, Math.max(2, range - 2));
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
    
    private static boolean clear(final Heightmap heightmap, final int a, final int n, final int n2) {
        for (int i = -n2; i <= n2; ++i) {
            for (int j = -n2; j <= n2; ++j) {
                if (heightmap.is(a + i, n + j, (byte)3)) {
                    return false;
                }
            }
        }
        return Math.abs(a) > 9 || n < 60;
    }
    
    private static boolean nearPlace(final int n, final int n2) {
        for (final Place place : AsylumLayout.PLACES) {
            if (Math.hypot(n - place.x(), n2 - place.z()) <= place.pad() + 2) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean nearPost(final int n, final int n2, final int n3) {
        for (final Post post : AsylumLayout.POSTS) {
            if (Math.abs(n - post.x()) <= n3 && Math.abs(n2 - post.z()) <= n3) {
                return true;
            }
        }
        return false;
    }
}
