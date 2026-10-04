package dev.two.slender.map.gen;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Signposts;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Builder;
import dev.two.slender.map.EditOps;
import dev.two.slender.map.PageSpot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public final class Decor {
    private Decor() {
    }

    public static void signpost(Builder var0, int var1, int var2, int var3, String var4, List<String> var5) {
        var0.fill(var1, var2 + 1, var3, var1, var2 + 3, var3, "spruce_log");
        var0.set(var1, var2 + 4, var3, "lantern[hanging=false]");
        BlockPos var6 = EditOps.front(new BlockPos(var1, var2 + 2, var3), var4);
        var0.sign(var6.x(), var6.y(), var6.z(), var4.toLowerCase(Locale.ROOT), DyeColor.WHITE, (String[])var5.toArray(new String[0]));
    }

    public static void note(Builder var0, int var1, int var2, int var3, String var4, List<String> var5, String var6) {
        BlockPos var7 = EditOps.front(new BlockPos(var1, var2, var3), opposite(var4));
        var0.set(var7.x(), var7.y(), var7.z(), var6);
        var0.sign(var1, var2, var3, var4.toLowerCase(Locale.ROOT), DyeColor.LIGHT_GRAY, (String[])var5.toArray(new String[0]));
    }

    public static String opposite(String var0) {
        String var10000;
        switch (var0) {
            case "NORTH" -> var10000 = "SOUTH";
            case "SOUTH" -> var10000 = "NORTH";
            case "EAST" -> var10000 = "WEST";
            case "WEST" -> var10000 = "EAST";
            default -> throw new IllegalArgumentException("Cara no válida: " + var0);
        }

        return var10000;
    }

    public static List<BlockPos> border(Builder var0, Heightmap var1, int var2) {
        for(int var3 = -var2; var3 <= var2; ++var3) {
            column(var0, var1, var3, -var2, "spruce_fence[east=true,west=true]");
            if (Math.abs(var3) > 2) {
                column(var0, var1, var3, var2, "spruce_fence[east=true,west=true]");
            }

            column(var0, var1, -var2, var3, "spruce_fence[north=true,south=true]");
            column(var0, var1, var2, var3, "spruce_fence[north=true,south=true]");
        }

        ArrayList var6 = new ArrayList();

        for(int var4 = -2; var4 <= 2; ++var4) {
            int var5;
            for(var5 = 1; var5 <= 3; ++var5) {
                var0.set(var4, var5, var2, "iron_bars[east=true,west=true]");
                var6.add(var0.abs(var4, var5, var2));
            }

            for(var5 = 4; var5 <= 16; ++var5) {
                var0.set(var4, var5, var2, Material.BARRIER);
            }
        }

        return var6;
    }

    private static void column(Builder var0, Heightmap var1, int var2, int var3, String var4) {
        int var5 = var1.at(var2, var3);
        var0.set(var2, var5 + 1, var3, var4);
        var0.set(var2, var5 + 2, var3, var4);

        for(int var6 = var5 + 3; var6 <= var5 + 16; ++var6) {
            var0.set(var2, var6, var3, Material.BARRIER);
        }

    }

    public static void exitRoad(Builder var0, int var1) {
        int var2;
        int var3;
        for(var2 = var1 + 1; var2 <= var1 + 10; ++var2) {
            for(var3 = -3; var3 <= 3; ++var3) {
                var0.set(var3, 0, var2, (Material)Material.GRAVEL);
            }

            for(var3 = 1; var3 <= 14; ++var3) {
                var0.set(-5, var3, var2, (Material)Material.BARRIER);
                var0.set(5, var3, var2, (Material)Material.BARRIER);
            }
        }

        for(var2 = -5; var2 <= 5; ++var2) {
            for(var3 = 1; var3 <= 14; ++var3) {
                var0.set(var2, var3, var1 + 11, Material.BARRIER);
            }
        }

        var0.fill(0, 1, var1 + 9, 0, 2, var1 + 9, (String)"spruce_fence");
        var0.set(0, 3, var1 + 9, (String)"lantern[hanging=false]");
    }

    public static void board(Builder var0, List<Place> var1, String var2, String var3) {
        board(var0, 6, 0, 61, var1, var2, var3);
    }

    public static void board(Builder var0, int var1, int var2, int var3, List<Place> var4, String var5, String var6) {
        var0.fill(var1, var2 + 1, var3, var1, var2 + 3, var3 + 5, "dark_oak_planks");
        var0.fill(var1, var2 + 4, var3 + 2, var1, var2 + 4, var3 + 3, "dark_oak_planks");
        var0.sign(var1 - 1, var2 + 4, var3 + 2, "west", DyeColor.WHITE, var5, var6, "", "");
        var0.sign(var1 - 1, var2 + 4, var3 + 3, "west", DyeColor.WHITE, "", "Lugares", "desde aquí", "");

        for(int var7 = 0; var7 < var4.size() && var7 < 12; ++var7) {
            Place var8 = (Place)var4.get(var7);
            int var9 = var3 + var7 % 6;
            int var10 = var2 + 3 - var7 / 6;
            int var11 = var8.x() - (var1 - 1);
            int var12 = var8.z() - var9;
            int var10001 = var1 - 1;
            DyeColor var10005 = DyeColor.WHITE;
            String[] var10006 = new String[]{Signposts.line("WEST", var8.name(), var11, var12), "", null, null};
            long var10009 = Math.round(Math.hypot((double)var11, (double)var12));
            var10006[2] = "" + var10009 + " m";
            var10006[3] = "";
            var0.sign(var10001, var10, var9, "west", var10005, var10006);
        }

    }

    public static void posts(Builder var0, Heightmap var1, List<Post> var2, Function<String, Place> var3, int var4) {
        posts(var0, var1, var2, var3, 0, var4);
    }

    public static void posts(Builder var0, Heightmap var1, List<Post> var2, Function<String, Place> var3, int var4, int var5) {
        Iterator var6 = var2.iterator();

        while(var6.hasNext()) {
            Post var7 = (Post)var6.next();
            int var8 = var1.at(var7.x(), var7.z());
            int var9 = var7.base(var1);
            if (var9 > var8) {
                var0.fill(var7.x(), var8 + 1, var7.z(), var7.x(), var9, var7.z(), "spruce_log");
            }

            ArrayList var10 = new ArrayList();
            Iterator var11 = var7.targets().iterator();

            while(var11.hasNext()) {
                String var12 = (String)var11.next();
                if (var12.equals("salida")) {
                    var10.add(Signposts.line(var7.facing(), "Salida", var4 - var7.x(), var5 - var7.z()));
                } else {
                    Place var13 = (Place)var3.apply(var12);
                    var10.add(Signposts.line(var7.facing(), var13.name(), var13.x() - var7.x(), var13.z() - var7.z()));
                }
            }

            signpost(var0, var7.x(), var9, var7.z(), var7.facing(), var10);
        }

    }

    public static void notes(Builder var0, Heightmap var1, List<Note> var2, Function<String, Place> var3) {
        Iterator var4 = var2.iterator();

        while(var4.hasNext()) {
            Note var5 = (Note)var4.next();
            Place var6 = (Place)var3.apply(var5.place());
            note(var0, var6.x() + var5.dx(), var1.at(var6.x(), var6.z()) + var5.dy(), var6.z() + var5.dz(), var5.facing(), var5.lines(), var5.support());
        }

    }

    public static List<PageSpot> pages(Builder var0, Heightmap var1, List<PageWall> var2, Function<String, Place> var3) {
        ArrayList var4 = new ArrayList();
        Iterator var5 = var2.iterator();

        while(var5.hasNext()) {
            PageWall var6 = (PageWall)var5.next();
            Place var7 = (Place)var3.apply(var6.place());
            int var8 = var7.x() + var6.dx();
            int var9 = var1.at(var7.x(), var7.z()) + var6.dy();
            int var10 = var7.z() + var6.dz();
            var0.set(var8, var9, var10, var6.material());
            var4.add(var0.pageOnWall(var8, var9, var10, var6.facing(), var6.label()));
        }

        return var4;
    }
}
