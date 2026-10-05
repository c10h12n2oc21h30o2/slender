package dev.two.slender.hub;

import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Hub;
import dev.two.slender.map.Zone;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.DyeColor;
import org.bukkit.Material;

public final class HubBuilder {
    public static final BlockPos ORIGIN = new BlockPos(-2000, 64, 0);
    public static final int RADIUS = 41;
    private static final int[] ARCH_X = new int[]{-25, -15, -5, 5, 15, 25};
    private static final String[] ARCH_MAP = new String[]{null, "bosque", "sanatorio", "maizal", "instituto", null};
    private static final String[] ARCH_LABEL = new String[]{"???", "BOSQUE", "SANATORIO", "MAIZAL", "INSTITUTO", "???"};
    private static final DyeColor[] ARCH_COLOR;
    private static final Material[] WALL;
    private static final String[] WEEDS;

    private HubBuilder() {
    }

    public static Hub build(Builder var0, String var1) {
        ground(var0);
        walls(var0);
        tunnels(var0);
        platform(var0);
        tracks(var0);
        station(var0);
        Zone var2 = exitRoom(var0);
        Map<String, Zone> var3 = arches(var0);
        Sala.build(var0);
        wagon(var0);
        yard(var0);
        LinkedHashMap<String, Zone> doors = new LinkedHashMap<>(var3);
        doors.putAll(Anden934.build(var0));
        return new Hub(var1, var0.loc(0.0, 2.0, 3.0, 0.0F), var2, doors);
    }

    private static void stone(Builder var0, int var1, int var2, int var3, int var4, int var5, int var6) {
        for(int var7 = Math.min(var1, var4); var7 <= Math.max(var1, var4); ++var7) {
            for(int var8 = Math.min(var2, var5); var8 <= Math.max(var2, var5); ++var8) {
                for(int var9 = Math.min(var3, var6); var9 <= Math.max(var3, var6); ++var9) {
                    var0.set(var7, var8, var9, (Material)var0.pick(WALL));
                }
            }
        }

    }

    private static void weed(Builder var0, int var1, int var2) {
        var0.set(var1, 0, var2, (Material)Material.COARSE_DIRT);
        var0.set(var1, 1, var2, (String)((String)var0.pick(WEEDS)));
    }

    private static void ground(Builder var0) {
        for(int var1 = -35; var1 <= 35; ++var1) {
            for(int var2 = -25; var2 <= 21; ++var2) {
                var0.set(var1, 0, var2, (Material)((Material)var0.pick(Material.COARSE_DIRT, Material.COARSE_DIRT, Material.GRAVEL, Material.DIRT, Material.PODZOL, Material.ROOTED_DIRT)));
            }
        }

    }

    private static void walls(Builder var0) {
        stone(var0, -35, 1, -25, 35, 9, -25);
        stone(var0, -35, 1, 20, 35, 9, 21);
        stone(var0, -35, 1, -25, -35, 9, 21);
        stone(var0, 35, 1, -25, 35, 9, 21);
        var0.fill(-35, 10, -25, 35, 13, -25, (Material)Material.BARRIER);
        var0.fill(-35, 10, 21, 35, 13, 21, (Material)Material.BARRIER);
        var0.fill(-35, 10, -25, -35, 13, 21, (Material)Material.BARRIER);
        var0.fill(35, 10, -25, 35, 13, 21, (Material)Material.BARRIER);

        for(int var1 = 0; var1 < 28; ++var1) {
            int var2 = var0.range(-34, 34);
            int var3 = var0.range(6, 9);
            int var4 = var0.range(2, 5);

            for(int var5 = var3; var5 > var3 - var4; --var5) {
                var0.set(var2, var5, -24, (String)"vine[north=true]");
            }
        }

    }

    private static void tunnels(Builder var0) {
        int[] var1 = new int[]{-1, 1};
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var1[var3];
            int var5 = 35 * var4;
            var0.air(var5, 1, 9, var5, 5, 15);
            int[][] var6 = new int[][]{{5, 9}, {5, 10}, {5, 14}, {5, 15}, {4, 9}, {4, 15}};
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                int[] var9 = var6[var8];
                var0.set(var5, var9[0], var9[1], (Material)var0.pick(WALL));
            }

            for(int var10 = 1; var10 <= 5; ++var10) {
                var7 = var5 + var10 * var4;
                stone(var0, var7, 1, 8, var7, 6, 8);
                stone(var0, var7, 1, 16, var7, 6, 16);
                stone(var0, var7, 6, 9, var7, 6, 15);
                var0.fill(var7, 0, 8, var7, 0, 16, (Material)Material.GRAVEL);
                var0.air(var7, 1, 9, var7, 5, 15);
                var0.set(var7, 1, 10, (String)"rail[shape=east_west]");
                var0.set(var7, 1, 14, (String)"rail[shape=east_west]");
            }

            var0.fill(var5 + 6 * var4, 0, 8, var5 + 6 * var4, 6, 16, (Material)Material.BLACK_CONCRETE);
            var0.fill(var5 + var4, 1, 9, var5 + var4, 5, 15, (Material)Material.BARRIER);
        }

    }

    private static void platform(Builder var0) {
        var0.fill(-30, 0, -2, 30, 0, 7, (Material)Material.STONE);

        int var1;
        int var2;
        for(var1 = -30; var1 <= 30; ++var1) {
            for(var2 = -2; var2 <= 7; ++var2) {
                var0.set(var1, 1, var2, (Material)(var2 == 7 ? Material.SMOOTH_SANDSTONE : (Material)var0.pick(Material.SMOOTH_STONE, Material.SMOOTH_STONE, Material.SMOOTH_STONE, Material.POLISHED_ANDESITE, Material.ANDESITE, Material.CRACKED_STONE_BRICKS)));
            }
        }

        for(var1 = -28; var1 <= 28; var1 += 8) {
            boolean var7 = var1 == -28 || var1 == 20;
            var0.fill(var1, 2, 5, var1, var7 ? 3 : 4, 5, (String)"dark_oak_fence");
            if (!var7) {
                var0.set(var1, 5, 5, (String)"lantern[hanging=false]");
            }
        }

        int[] var6 = new int[]{-9, 7, 15};
        var2 = var6.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var6[var3];

            for(int var5 = var4; var5 < var4 + 3; ++var5) {
                var0.stairs(var5, 2, 1, "spruce_stairs", "north");
            }
        }

        var0.set(-1, 2, 6, (String)"barrel[facing=up]");
        var0.set(11, 2, 0, (String)"barrel[facing=north]");
        var0.set(12, 2, 0, (String)"chest[facing=south]");

        for(var1 = -34; var1 <= 34; ++var1) {
            if (var1 < -24 || var1 > -6) {
                var0.fill(var1, 1, -3, var1, 2, -3, (String)"dark_oak_fence[east=true,west=true]");
            }
        }

        var0.fill(-2, 2, -2, 2, 4, -2, (Material)Material.DARK_OAK_PLANKS);
        var0.sign(0, 4, -1, "south", DyeColor.WHITE, "ESTACIÓN DE", "VALDEMORA", "- andén 1 -", "");
        var0.sign(-1, 3, -1, "south", DyeColor.WHITE, "Cruza las vías", "y entra en un", "arco para", "jugar");
        var0.sign(1, 3, -1, "south", DyeColor.RED, "SE BUSCA", "Íñigo Varela", "salió al bosque", "y no volvió");
        var0.sign(0, 2, -1, "south", DyeColor.GRAY, "La puerta", "SALIDA de la", "estación te", "lleva a casa");
    }

    private static void tracks(Builder var0) {
        int var1;
        for(var1 = -34; var1 <= 34; ++var1) {
            for(int var2 = 8; var2 <= 16; ++var2) {
                var0.set(var1, 0, var2, (Material)(var0.chance(0.85) ? Material.GRAVEL : Material.COARSE_DIRT));
            }
        }

        for(var1 = -35; var1 <= 35; ++var1) {
            if (!var0.chance(0.04)) {
                var0.set(var1, 1, 10, (String)"rail[shape=east_west]");
            }

            if (!var0.chance(0.04)) {
                var0.set(var1, 1, 14, (String)"rail[shape=east_west]");
            }
        }

        for(var1 = 0; var1 < 70; ++var1) {
            weed(var0, var0.range(-34, 34), (Integer)var0.pick(8, 9, 11, 12, 13, 15, 16));
        }

    }

    private static void station(Builder var0) {
        var0.room(-24, 1, -18, -6, 7, -3, Material.STONE_BRICKS, Material.SPRUCE_PLANKS, Material.DARK_OAK_PLANKS);
        var0.wallsMixed(-24, 2, -18, -6, 6, -3, Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.MOSSY_STONE_BRICKS);

        int var1;
        int var2;
        for(var1 = -25; var1 <= -5; ++var1) {
            for(var2 = -19; var2 <= -1; ++var2) {
                if (!var0.chance(0.06)) {
                    var0.set(var1, 8, var2, (String)"dark_oak_slab[type=bottom]");
                }
            }
        }

        var0.air(-16, 2, -3, -14, 4, -3);
        int[] var6 = new int[]{-21, -20, -19, -11, -10, -9};
        var2 = var6.length;

        int var3;
        for(var3 = 0; var3 < var2; ++var3) {
            int var4 = var6[var3];

            for(int var5 = 3; var5 <= 4; ++var5) {
                var0.set(var4, var5, -3, (String)(var0.chance(0.4) ? "air" : "iron_bars[east=true,west=true]"));
            }
        }

        var0.fill(-23, 2, -12, -19, 2, -12, (Material)Material.POLISHED_ANDESITE);
        var0.fill(-23, 3, -12, -19, 4, -12, (String)"iron_bars[east=true,west=true]");
        var0.sign(-21, 2, -11, "south", DyeColor.WHITE, "TAQUILLA", "", "CERRADA", "");

        for(var1 = -22; var1 <= -18; ++var1) {
            var0.stairs(var1, 2, -7, "spruce_stairs", "south");
        }

        for(var1 = -12; var1 <= -8; ++var1) {
            var0.stairs(var1, 2, -7, "spruce_stairs", "south");
            var0.stairs(var1, 2, -11, "spruce_stairs", "north");
        }

        var0.sign(-23, 3, -15, "east", DyeColor.WHITE, "DESAPARECIDA", "Lucía Abad", "vista en el", "sanatorio");
        var0.sign(-23, 3, -8, "east", DyeColor.WHITE, "DESAPARECIDOS", "familia Roldán", "camino del", "maizal");
        var0.sign(-7, 3, -14, "west", DyeColor.RED, "NO MIRES", "AL HOMBRE", "SIN CARA", "");
        var0.sign(-7, 3, -9, "west", DyeColor.WHITE, "ÚLTIMO TREN", "02:13", "", "no esperes");
        var0.sign(-7, 3, -5, "west", DyeColor.WHITE, "DESAPARECIDO", "Xosé Lamas", "conserje del", "instituto");
        var0.sign(-23, 3, -5, "east", DyeColor.WHITE, "DESAPARECIDO", "Argus Filch", "conserje de", "Hogwarts");
        var0.set(-20, 6, -8, (String)"lantern[hanging=true]");
        var0.set(-10, 6, -14, (String)"lantern[hanging=true]");
        var0.set(-15, 6, -16, (String)"soul_lantern[hanging=true]");
        int[][] var7 = new int[][]{{-23, -17}, {-7, -17}, {-23, -4}, {-7, -4}, {-15, -10}};
        var2 = var7.length;

        for(var3 = 0; var3 < var2; ++var3) {
            int[] var8 = var7[var3];
            var0.set(var8[0], 6, var8[1], (Material)Material.COBWEB);
        }

        var0.sign(-15, 6, -2, "south", DyeColor.WHITE, "", "VALDEMORA", "", "");
    }

    private static Zone exitRoom(Builder var0) {
        var0.air(-16, 2, -18, -14, 4, -18);
        var0.fill(-17, 1, -22, -13, 5, -19, (Material)Material.BLACK_CONCRETE);
        var0.air(-16, 2, -21, -14, 4, -19);
        var0.sign(-15, 5, -17, "south", DyeColor.RED, "", "SALIDA", "", "");
        return Zone.of(var0.abs(-16, 2, -21), var0.abs(-14, 4, -20));
    }

    private static Map<String, Zone> arches(Builder var0) {
        LinkedHashMap var1 = new LinkedHashMap();

        int var2;
        int var3;
        int var4;
        for(var2 = 0; var2 < ARCH_X.length; ++var2) {
            var3 = ARCH_X[var2];
            stone(var0, var3 - 2, 1, 17, var3 + 2, 6, 19);
            var0.air(var3 - 1, 1, 17, var3 + 1, 4, 19);

            for(var4 = 17; var4 <= 19; ++var4) {
                var0.set(var3 - 1, 4, var4, (Material)((Material)var0.pick(WALL)));
                var0.set(var3 + 1, 4, var4, (Material)((Material)var0.pick(WALL)));
            }

            var0.fill(var3 - 1, 0, 17, var3 + 1, 0, 19, (Material)Material.POLISHED_DEEPSLATE);
            var0.fill(var3 - 1, 1, 20, var3 + 1, 4, 20, (Material)Material.BLACK_CONCRETE);
            var0.set(var3, 4, 18, (String)"soul_lantern[hanging=true]");
            var0.sign(var3, 5, 16, "north", ARCH_COLOR[var2], "", ARCH_LABEL[var2], "", "");
            if (ARCH_MAP[var2] != null) {
                var1.put(ARCH_MAP[var2], Zone.of(var0.abs(var3 - 1, 1, 18), var0.abs(var3 + 1, 3, 19)));
            }
        }

        for(var2 = 0; var2 < 10; ++var2) {
            var3 = ARCH_X[var0.range(0, ARCH_X.length - 2)] + var0.range(3, 7);
            var4 = var0.range(6, 9);

            for(int var5 = var4; var5 > var4 - var0.range(2, 4); --var5) {
                var0.set(var3, var5, 19, (String)"vine[south=true]");
            }
        }

        return var1;
    }

    private static void wagon(Builder var0) {
        int[] var1 = new int[]{-33, -29};
        int var2 = var1.length;

        int var3;
        int var4;
        for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            int[] var5 = new int[]{8, 12};
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                int var8 = var5[var7];
                var0.set(var4, 1, var8, (Material)Material.POLISHED_BLACKSTONE);
            }
        }

        var0.fill(-34, 2, 8, -28, 2, 12, (Material)Material.OXIDIZED_CUT_COPPER);
        var0.walls(-34, 3, 8, -28, 5, 12, Material.WEATHERED_COPPER);
        var0.air(-33, 3, 9, -29, 5, 11);
        var1 = new int[]{-33, -32, -30, -29};
        var2 = var1.length;

        for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            var0.set(var4, 4, 8, (String)"iron_bars[east=true,west=true]");
            var0.set(var4, 4, 12, (String)"iron_bars[east=true,west=true]");
        }

        var0.air(-31, 3, 8, -31, 4, 8);
        var0.fill(-34, 6, 8, -28, 6, 12, (String)"oxidized_cut_copper_slab[type=bottom]");
        var0.set(-33, 5, 11, (Material)Material.COBWEB);
        var0.set(-29, 3, 9, (Material)Material.COBWEB);
    }

    private static void yard(Builder var0) {
        int var1;
        int var2;
        int var3;
        for(var1 = 0; var1 < 120; ++var1) {
            var2 = var0.range(-34, 34);
            var3 = var0.range(-24, -4);
            if (var2 < -25 || var2 > -5 || var3 < -23) {
                weed(var0, var2, var3);
            }
        }

        for(var1 = 0; var1 < 16; ++var1) {
            var2 = var0.range(-33, 33);
            var3 = var0.range(-23, -6);
            if (var2 < -26 || var2 > -4) {
                int var4 = var0.range(4, 8);
                var0.fill(var2, 1, var3, var2, var4, var3, (String)"spruce_log");
                if (var0.chance(0.6)) {
                    var0.set(var2 + 1, var4 - 1, var3, "spruce_log[axis=x]");
                }

                if (var0.chance(0.6)) {
                    var0.set(var2, var4 - 2, var3 - 1, "spruce_log[axis=z]");
                }
            }
        }

    }

    static {
        ARCH_COLOR = new DyeColor[]{DyeColor.GRAY, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.GRAY};
        WALL = new Material[]{Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE};
        WEEDS = new String[]{"short_grass", "short_grass", "fern", "dead_bush"};
    }
}
