// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import dev.two.slender.map.PageSpot;
import java.util.Iterator;
import java.util.function.Function;
import dev.two.slender.logic.Signposts;
import org.bukkit.Material;
import java.util.ArrayList;
import dev.two.slender.logic.Heightmap;
import org.bukkit.DyeColor;
import java.util.Locale;
import dev.two.slender.map.EditOps;
import dev.two.slender.map.BlockPos;
import java.util.List;
import dev.two.slender.map.Builder;

public final class Decor
{
    private Decor() {
    }
    
    public static void signpost(final Builder builder, final int x, final int n, final int z, final String s, final List<String> list) {
        builder.fill(x, n + 1, z, x, n + 3, z, "spruce_log");
        builder.set(x, n + 4, z, "lantern[hanging=false]");
        final BlockPos front = EditOps.front(new BlockPos(x, n + 2, z), s);
        builder.sign(front.x(), front.y(), front.z(), s.toLowerCase(Locale.ROOT), DyeColor.WHITE, (String[])list.toArray(new String[0]));
    }
    
    public static void note(final Builder builder, final int x, final int y, final int z, final String s, final List<String> list, final String s2) {
        final BlockPos front = EditOps.front(new BlockPos(x, y, z), opposite(s));
        builder.set(front.x(), front.y(), front.z(), s2);
        builder.sign(x, y, z, s.toLowerCase(Locale.ROOT), DyeColor.LIGHT_GRAY, (String[])list.toArray(new String[0]));
    }
    
    public static String opposite(final String s) {
        return switch (s) {
            case "NORTH" -> "SOUTH";
            case "SOUTH" -> "NORTH";
            case "EAST" -> "WEST";
            case "WEST" -> "EAST";
            default -> throw new IllegalArgumentException("Cara no v\u00e1lida: " + s);
        };
    }
    
    public static List<BlockPos> border(final Builder builder, final Heightmap heightmap, final int n) {
        for (int i = -n; i <= n; ++i) {
            column(builder, heightmap, i, -n, "spruce_fence[east=true,west=true]");
            if (Math.abs(i) > 2) {
                column(builder, heightmap, i, n, "spruce_fence[east=true,west=true]");
            }
            column(builder, heightmap, -n, i, "spruce_fence[north=true,south=true]");
            column(builder, heightmap, n, i, "spruce_fence[north=true,south=true]");
        }
        final ArrayList list = new ArrayList();
        for (int j = -2; j <= 2; ++j) {
            for (int k = 1; k <= 3; ++k) {
                builder.set(j, k, n, "iron_bars[east=true,west=true]");
                list.add(builder.abs(j, k, n));
            }
            for (int l = 4; l <= 16; ++l) {
                builder.set(j, l, n, Material.BARRIER);
            }
        }
        return list;
    }
    
    private static void column(final Builder builder, final Heightmap heightmap, final int n, final int n2, final String s) {
        final int at = heightmap.at(n, n2);
        builder.set(n, at + 1, n2, s);
        builder.set(n, at + 2, n2, s);
        for (int i = at + 3; i <= at + 16; ++i) {
            builder.set(n, i, n2, Material.BARRIER);
        }
    }
    
    public static void exitRoad(final Builder builder, final int n) {
        for (int i = n + 1; i <= n + 10; ++i) {
            for (int j = -3; j <= 3; ++j) {
                builder.set(j, 0, i, Material.GRAVEL);
            }
            for (int k = 1; k <= 14; ++k) {
                builder.set(-5, k, i, Material.BARRIER);
                builder.set(5, k, i, Material.BARRIER);
            }
        }
        for (int l = -5; l <= 5; ++l) {
            for (int n2 = 1; n2 <= 14; ++n2) {
                builder.set(l, n2, n + 11, Material.BARRIER);
            }
        }
        builder.fill(0, 1, n + 9, 0, 2, n + 9, "spruce_fence");
        builder.set(0, 3, n + 9, "lantern[hanging=false]");
    }
    
    public static void board(final Builder builder, final List<Place> list, final String s, final String s2) {
        board(builder, 6, 0, 61, list, s, s2);
    }
    
    public static void board(final Builder builder, final int n, final int n2, final int n3, final List<Place> list, final String s, final String s2) {
        builder.fill(n, n2 + 1, n3, n, n2 + 3, n3 + 5, "dark_oak_planks");
        builder.fill(n, n2 + 4, n3 + 2, n, n2 + 4, n3 + 3, "dark_oak_planks");
        builder.sign(n - 1, n2 + 4, n3 + 2, "west", DyeColor.WHITE, s, s2, "", "");
        builder.sign(n - 1, n2 + 4, n3 + 3, "west", DyeColor.WHITE, "", "Lugares", "desde aqu\u00ed", "");
        for (int n4 = 0; n4 < list.size() && n4 < 12; ++n4) {
            final Place place = list.get(n4);
            final int n5 = n3 + n4 % 6;
            final int n6 = n2 + 3 - n4 / 6;
            final int n7 = place.x() - (n - 1);
            final int n8 = place.z() - n5;
            builder.sign(n - 1, n6, n5, "west", DyeColor.WHITE, Signposts.line("WEST", place.name(), n7, n8), "", Math.round(Math.hypot(n7, n8)) + " m", "");
        }
    }
    
    public static void posts(final Builder builder, final Heightmap heightmap, final List<Post> list, final Function<String, Place> function, final int n) {
        posts(builder, heightmap, list, function, 0, n);
    }
    
    public static void posts(final Builder builder, final Heightmap heightmap, final List<Post> list, final Function<String, Place> function, final int n, final int n2) {
        for (final Post post : list) {
            final int at = heightmap.at(post.x(), post.z());
            final int base = post.base(heightmap);
            if (base > at) {
                builder.fill(post.x(), at + 1, post.z(), post.x(), base, post.z(), "spruce_log");
            }
            final ArrayList list2 = new ArrayList();
            for (final String s : post.targets()) {
                if (s.equals("salida")) {
                    list2.add(Signposts.line(post.facing(), "Salida", n - post.x(), n2 - post.z()));
                }
                else {
                    final Place place = function.apply(s);
                    list2.add(Signposts.line(post.facing(), place.name(), place.x() - post.x(), place.z() - post.z()));
                }
            }
            signpost(builder, post.x(), base, post.z(), post.facing(), list2);
        }
    }
    
    public static void notes(final Builder builder, final Heightmap heightmap, final List<Note> list, final Function<String, Place> function) {
        for (final Note note : list) {
            final Place place = function.apply(note.place());
            note(builder, place.x() + note.dx(), heightmap.at(place.x(), place.z()) + note.dy(), place.z() + note.dz(), note.facing(), note.lines(), note.support());
        }
    }
    
    public static List<PageSpot> pages(final Builder builder, final Heightmap heightmap, final List<PageWall> list, final Function<String, Place> function) {
        final ArrayList list2 = new ArrayList();
        for (final PageWall pageWall : list) {
            final Place place = function.apply(pageWall.place());
            final int n = place.x() + pageWall.dx();
            final int n2 = heightmap.at(place.x(), place.z()) + pageWall.dy();
            final int n3 = place.z() + pageWall.dz();
            builder.set(n, n2, n3, pageWall.material());
            list2.add(builder.pageOnWall(n, n2, n3, pageWall.facing(), pageWall.label()));
        }
        return list2;
    }
}
