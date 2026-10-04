package dev.two.slender.map.gen;

import dev.two.slender.map.Builder;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;

public final class PlanPainter {
    private static final String WALLISH = "#WdD%";
    private static final String SHELL = " %E";

    private PlanPainter() {
    }

    public static void paint(Builder var0, FloorPlan var1, int var2, PlanStyle var3) {
        List var4 = var1.stairs();

        int var5;
        int var6;
        int var7;
        for(var5 = var1.lowest(); var5 <= var1.highest(); ++var5) {
            for(var6 = var1.minZ(); var6 <= var1.maxZ(); ++var6) {
                for(var7 = var1.minX(); var7 <= var1.maxX(); ++var7) {
                    FloorPlan.Cell var8 = new FloorPlan.Cell(var5, var7, var6);
                    if (var1.at(var8) != ' ') {
                        cell(var0, var1, var4, var2, var3, var8);
                    }
                }
            }
        }

        var5 = var1.storey();

        for(var6 = var1.lowest(); var6 <= var1.highest(); ++var6) {
            for(var7 = var1.minZ(); var7 <= var1.maxZ(); ++var7) {
                for(int var10 = var1.minX(); var10 <= var1.maxX(); ++var10) {
                    char var9 = var1.at(var6, var10, var7);
                    if (var9 != ' ' && var9 != 'x' && var1.at(var6 + 1, var10, var7) == ' ') {
                        var0.set(var10, var2 + (var6 + 1) * var5, var7, var3.roof(var10, var7));
                    }
                }
            }
        }

        lamps(var0, var1, var2);
    }

    public static void paintInside(Builder var0, FloorPlan var1, int var2, PlanStyle var3) {
        List var4 = var1.stairs();

        for(int var5 = var1.lowest(); var5 <= var1.highest(); ++var5) {
            for(int var6 = var1.minZ(); var6 <= var1.maxZ(); ++var6) {
                for(int var7 = var1.minX(); var7 <= var1.maxX(); ++var7) {
                    FloorPlan.Cell var8 = new FloorPlan.Cell(var5, var7, var6);
                    if (" %E".indexOf(var1.at(var8)) < 0) {
                        cell(var0, var1, var4, var2, var3, var8);
                    }
                }
            }
        }

        lamps(var0, var1, var2);
    }

    private static void lamps(Builder var0, FloorPlan var1, int var2) {
        Iterator var3 = var1.cells('L').iterator();

        while(var3.hasNext()) {
            FloorPlan.Cell var4 = (FloorPlan.Cell)var3.next();
            var0.set(var4.x(), var2 + var1.lampDy(var4.floor()), var4.z(), "redstone_lamp[lit=true]");
        }

    }

    private static void cell(Builder var0, FloorPlan var1, List<FloorPlan.Stair> var2, int var3, PlanStyle var4, FloorPlan.Cell var5) {
        int var6 = var1.storey();
        int var7 = var5.x();
        int var8 = var5.z();
        int var9 = var3 + var5.floor() * var6;
        char var10 = var1.at(var5);
        if (var10 != 'o') {
            var0.set(var7, var9, var8, var4.floor(var5));
        }

        for(int var11 = 1; var11 < var6; ++var11) {
            var0.set(var7, var9 + var11, var8, Material.AIR);
        }

        boolean var15 = outer(var1, var5);
        int var12;
        int var13;
        String var16;
        switch (var10) {
            case '#':
                for(var12 = 1; var12 < var6; ++var12) {
                    var0.set(var7, var9 + var12, var8, var4.wall(var5, var15));
                }

                var16 = var15 ? null : var4.stripe(var5);
                if (var16 != null) {
                    var0.set(var7, var9 + 2, var8, var16);
                }
            case '.':
            case 'E':
            case 'L':
            case 'o':
                break;
            case 'D':
                for(var12 = 3; var12 < var6; ++var12) {
                    var0.set(var7, var9 + var12, var8, var4.wall(var5, var15));
                }

                return;
            case 'W':
                boolean var10000 = alongX(var1, var5);
                var16 = "glass_pane[" + (var10000 ? "east=true,west=true" : "north=true,south=true") + "]";

                for(var13 = 1; var13 < var6; ++var13) {
                    if (var13 != 2 && var13 != 3) {
                        var0.set(var7, var9 + var13, var8, var4.wall(var5, var15));
                    } else if (!var0.chance(0.35)) {
                        var0.set(var7, var9 + var13, var8, var16);
                    }
                }

                return;
            case 'd':
                var0.door(var7, var9 + 1, var8, var4.door(), doorFacing(var1, var5));

                for(var12 = 3; var12 < var6; ++var12) {
                    var0.set(var7, var9 + var12, var8, var4.wall(var5, var15));
                }

                return;
            case 'x':
                if (var0.chance(0.7)) {
                    var0.set(var7, var9 + 1, var8, (String)var0.pick("cobblestone", "gravel", "andesite", "cobblestone_slab[type=bottom]"));
                }
                break;
            case '~':
                var0.set(var7, var9 + 1, var8, Material.WATER);
                break;
            default:
                FloorPlan.Stair var18 = stairAt(var2, var5);
                int var14;
                if (var18 != null) {
                    var13 = var18.index(var7, var8);

                    for(var14 = 1; var14 <= var13; ++var14) {
                        var0.set(var7, var9 + var14, var8, var4.wall(var5, false));
                    }

                    int var10002 = var9 + 1 + var13;
                    String var10004 = var4.stairs();
                    var0.set(var7, var10002, var8, var10004 + "[facing=" + facing(var18) + ",half=bottom,shape=straight]");

                    for(var14 = var13 + 2; var14 <= var6; ++var14) {
                        var0.set(var7, var9 + var14, var8, Material.AIR);
                    }
                } else {
                    List var17 = var4.furniture(var10);

                    for(var14 = 0; var14 < var17.size() && var14 < var6 - 1; ++var14) {
                        var0.set(var7, var9 + 1 + var14, var8, (String)var17.get(var14));
                    }
                }
        }

    }

    static boolean outer(FloorPlan var0, FloorPlan.Cell var1) {
        return var0.at(var1.floor(), var1.x() + 1, var1.z()) == ' ' || var0.at(var1.floor(), var1.x() - 1, var1.z()) == ' ' || var0.at(var1.floor(), var1.x(), var1.z() + 1) == ' ' || var0.at(var1.floor(), var1.x(), var1.z() - 1) == ' ';
    }

    static boolean alongX(FloorPlan var0, FloorPlan.Cell var1) {
        return "#WdD%".indexOf(var0.at(var1.floor(), var1.x() - 1, var1.z())) >= 0 || "#WdD%".indexOf(var0.at(var1.floor(), var1.x() + 1, var1.z())) >= 0;
    }

    static String doorFacing(FloorPlan var0, FloorPlan.Cell var1) {
        return alongX(var0, var1) ? "south" : "east";
    }

    static String facing(FloorPlan.Stair var0) {
        if (var0.dx() == 1) {
            return "east";
        } else if (var0.dx() == -1) {
            return "west";
        } else {
            return var0.dz() == -1 ? "north" : "south";
        }
    }

    private static FloorPlan.Stair stairAt(List<FloorPlan.Stair> var0, FloorPlan.Cell var1) {
        Iterator var2 = var0.iterator();

        FloorPlan.Stair var3;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            var3 = (FloorPlan.Stair)var2.next();
        } while(var3.floor() != var1.floor() || !var3.contains(var1.x(), var1.z()));

        return var3;
    }
}
