package dev.two.slender.map.builders.instituto;

import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Builder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;

final class InstitutoPlaces {
    private InstitutoPlaces() {
    }

    static void outside(Builder var0, List<int[]> var1) {
        fence(var0);
        exitRoad(var0);
        beacon(var0);
        Iterator var2 = var1.iterator();

        while(var2.hasNext()) {
            int[] var3 = (int[])var2.next();
            lamp(var0, var3[0], var3[1]);
        }

        busStop(var0);
        car(var0);
        hoop(var0, 142, 198, 199);
        hoop(var0, 142, 258, 257);
    }

    private static void fence(Builder var0) {
        int var1;
        for(var1 = 1; var1 <= 300; ++var1) {
            post(var0, var1, 1, "spruce_fence[east=true,west=true]");
            if (var1 < 4 || var1 > 35) {
                post(var0, var1, 432, "spruce_fence[east=true,west=true]");
            }
        }

        for(var1 = 1; var1 <= 432; ++var1) {
            post(var0, 1, var1, "spruce_fence[north=true,south=true]");
            post(var0, 300, var1, "spruce_fence[north=true,south=true]");
        }

    }

    private static void post(Builder var0, int var1, int var2, String var3) {
        var0.set(var1, 2, var2, (String)var3);
        var0.set(var1, 3, var2, (String)var3);

        for(int var4 = 4; var4 <= 17; ++var4) {
            var0.set(var1, var4, var2, Material.BARRIER);
        }

    }

    static List<BlockPos> gate(Builder var0) {
        ArrayList var1 = new ArrayList();

        for(int var2 = 4; var2 <= 35; ++var2) {
            int var3;
            for(var3 = 2; var3 <= 4; ++var3) {
                var0.set(var2, var3, 432, (String)"red_concrete");
                var1.add(var0.abs(var2, var3, 432));
            }

            for(var3 = 5; var3 <= 17; ++var3) {
                var0.set(var2, var3, 432, (Material)Material.BARRIER);
            }
        }

        return var1;
    }

    private static void exitRoad(Builder var0) {
        int var1;
        int var2;
        for(var1 = 433; var1 <= InstitutoLayout.EXIT[3] + 1; ++var1) {
            for(var2 = 4; var2 <= 35; ++var2) {
                var0.set(var2, 0, var1, (String)"dirt");
                var0.set(var2, 1, var1, (String)(var2 == 12 && var1 % 6 < 3 ? "yellow_concrete" : (var2 >= 20 ? "smooth_stone" : "gray_concrete")));

                for(int var3 = 2; var3 <= 5; ++var3) {
                    var0.set(var2, var3, var1, Material.AIR);
                }
            }

            for(var2 = 2; var2 <= 15; ++var2) {
                var0.set(3, var2, var1, (Material)Material.BARRIER);
                var0.set(36, var2, var1, (Material)Material.BARRIER);
            }
        }

        for(var1 = 3; var1 <= 36; ++var1) {
            for(var2 = 2; var2 <= 15; ++var2) {
                var0.set(var1, var2, InstitutoLayout.EXIT[3] + 2, Material.BARRIER);
            }
        }

        var0.fill(20, 2, InstitutoLayout.EXIT[3] + 1, 20, 3, InstitutoLayout.EXIT[3] + 1, (String)"spruce_fence");
        var0.set(20, 4, InstitutoLayout.EXIT[3] + 1, (String)"lantern[hanging=false]");
    }

    private static void beacon(Builder var0) {
        var0.fill(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1] - 6, InstitutoLayout.BEACON[2], InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1] - 1, InstitutoLayout.BEACON[2], "iron_bars");
        var0.set(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1], InstitutoLayout.BEACON[2], "redstone_lamp[lit=true]");
        var0.set(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1] + 1, InstitutoLayout.BEACON[2], "lightning_rod[facing=up]");
    }

    private static void lamp(Builder var0, int var1, int var2) {
        var0.fill(var1, 2, var2, var1, 6, var2, (String)"iron_bars");
        var0.set(var1, 7, var2, (String)"redstone_lamp[lit=true]");
        var0.set(var1, 8, var2, (String)"smooth_stone_slab[type=bottom]");
    }

    private static void busStop(Builder var0) {
        var0.fill(33, 2, 142, 33, 4, 149, (String)"light_gray_concrete");

        for(int var1 = 31; var1 <= 32; ++var1) {
            for(int var2 = 2; var2 <= 4; ++var2) {
                var0.set(var1, var2, 142, (String)"light_blue_stained_glass");
                var0.set(var1, var2, 149, (String)"light_blue_stained_glass");
            }
        }

        var0.fill(30, 5, 141, 34, 5, 150, (String)"smooth_stone_slab[type=bottom]");
        var0.fill(32, 2, 143, 32, 2, 144, (String)"spruce_slab[type=bottom]");
    }

    private static void car(Builder var0) {
        var0.fill(38, 2, 47, 42, 2, 49, (String)"red_concrete");
        int[][] var1 = new int[][]{{38, 47}, {42, 47}, {38, 49}, {42, 49}};
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            int[] var4 = var1[var3];
            var0.set(var4[0], 2, var4[1], (String)"coal_block");
        }

        var0.fill(39, 3, 47, 41, 3, 49, (String)"black_stained_glass");
        var0.fill(38, 3, 47, 38, 3, 49, (String)"red_concrete");
        var0.fill(42, 3, 47, 42, 3, 49, (String)"red_concrete");
        var0.fill(39, 4, 47, 41, 4, 49, (String)"red_concrete");
    }

    private static void hoop(Builder var0, int var1, int var2, int var3) {
        var0.fill(var1, 2, var2, var1, 6, var2, (String)"light_gray_concrete");
        var0.fill(var1 - 1, 6, var3, var1 + 1, 7, var3, (String)"white_concrete");
        var0.set(var1, 5, var3 + (var3 > var2 ? 1 : -1), (String)"iron_trapdoor[facing=north,half=top,open=false]");
    }
}
