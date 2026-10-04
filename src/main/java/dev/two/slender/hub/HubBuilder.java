// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.hub;

import java.util.LinkedHashMap;
import java.util.Map;
import dev.two.slender.map.Zone;
import dev.two.slender.map.Hub;
import dev.two.slender.map.Builder;
import org.bukkit.Material;
import org.bukkit.DyeColor;
import dev.two.slender.map.BlockPos;

public final class HubBuilder
{
    public static final BlockPos ORIGIN;
    public static final int RADIUS = 41;
    private static final int[] ARCH_X;
    private static final String[] ARCH_MAP;
    private static final String[] ARCH_LABEL;
    private static final DyeColor[] ARCH_COLOR;
    private static final Material[] WALL;
    private static final String[] WEEDS;
    
    private HubBuilder() {
    }
    
    public static Hub build(final Builder builder, final String world) {
        ground(builder);
        walls(builder);
        tunnels(builder);
        platform(builder);
        tracks(builder);
        station(builder);
        final Zone exitRoom = exitRoom(builder);
        final Map<String, Zone> arches = arches(builder);
        wagon(builder);
        yard(builder);
        return new Hub(world, builder.loc(0.0, 2.0, 3.0, 0.0f), exitRoom, arches);
    }
    
    private static void stone(final Builder builder, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        for (int i = Math.min(n, n4); i <= Math.max(n, n4); ++i) {
            for (int j = Math.min(n2, n5); j <= Math.max(n2, n5); ++j) {
                for (int k = Math.min(n3, n6); k <= Math.max(n3, n6); ++k) {
                    builder.set(i, j, k, builder.pick(HubBuilder.WALL));
                }
            }
        }
    }
    
    private static void weed(final Builder builder, final int n, final int n2) {
        builder.set(n, 0, n2, Material.COARSE_DIRT);
        builder.set(n, 1, n2, builder.pick(HubBuilder.WEEDS));
    }
    
    private static void ground(final Builder builder) {
        for (int i = -35; i <= 35; ++i) {
            for (int j = -25; j <= 21; ++j) {
                builder.set(i, 0, j, builder.pick(Material.COARSE_DIRT, Material.COARSE_DIRT, Material.GRAVEL, Material.DIRT, Material.PODZOL, Material.ROOTED_DIRT));
            }
        }
    }
    
    private static void walls(final Builder builder) {
        stone(builder, -35, 1, -25, 35, 9, -25);
        stone(builder, -35, 1, 20, 35, 9, 21);
        stone(builder, -35, 1, -25, -35, 9, 21);
        stone(builder, 35, 1, -25, 35, 9, 21);
        builder.fill(-35, 10, -25, 35, 13, -25, Material.BARRIER);
        builder.fill(-35, 10, 21, 35, 13, 21, Material.BARRIER);
        builder.fill(-35, 10, -25, -35, 13, 21, Material.BARRIER);
        builder.fill(35, 10, -25, 35, 13, 21, Material.BARRIER);
        for (int i = 0; i < 28; ++i) {
            final int range = builder.range(-34, 34);
            for (int range2 = builder.range(6, 9), range3 = builder.range(2, 5), j = range2; j > range2 - range3; --j) {
                builder.set(range, j, -24, "vine[north=true]");
            }
        }
    }
    
    private static void tunnels(final Builder builder) {
        for (final int n : new int[] { -1, 1 }) {
            final int n2 = 35 * n;
            builder.air(n2, 1, 9, n2, 5, 15);
            for (final int[] array3 : new int[][] { { 5, 9 }, { 5, 10 }, { 5, 14 }, { 5, 15 }, { 4, 9 }, { 4, 15 } }) {
                builder.set(n2, array3[0], array3[1], builder.pick(HubBuilder.WALL));
            }
            for (int k = 1; k <= 5; ++k) {
                final int n3 = n2 + k * n;
                stone(builder, n3, 1, 8, n3, 6, 8);
                stone(builder, n3, 1, 16, n3, 6, 16);
                stone(builder, n3, 6, 9, n3, 6, 15);
                builder.fill(n3, 0, 8, n3, 0, 16, Material.GRAVEL);
                builder.air(n3, 1, 9, n3, 5, 15);
                builder.set(n3, 1, 10, "rail[shape=east_west]");
                builder.set(n3, 1, 14, "rail[shape=east_west]");
            }
            builder.fill(n2 + 6 * n, 0, 8, n2 + 6 * n, 6, 16, Material.BLACK_CONCRETE);
            builder.fill(n2 + n, 1, 9, n2 + n, 5, 15, Material.BARRIER);
        }
    }
    
    private static void platform(final Builder builder) {
        builder.fill(-30, 0, -2, 30, 0, 7, Material.STONE);
        for (int i = -30; i <= 30; ++i) {
            for (int j = -2; j <= 7; ++j) {
                final int n;
                builder.set(i, 1, n, ((n = j) == 7) ? Material.SMOOTH_SANDSTONE : builder.pick(Material.SMOOTH_STONE, Material.SMOOTH_STONE, Material.SMOOTH_STONE, Material.POLISHED_ANDESITE, Material.ANDESITE, Material.CRACKED_STONE_BRICKS));
            }
        }
        for (int k = -28; k <= 28; k += 8) {
            final boolean b = k == -28 || k == 20;
            builder.fill(k, 2, 5, k, b ? 3 : 4, 5, "dark_oak_fence");
            if (!b) {
                builder.set(k, 5, 5, "lantern[hanging=false]");
            }
        }
        final int[] array = { -9, 7, 15 };
        for (int length = array.length, l = 0; l < length; ++l) {
            int n2 = 0;
            while (n2 < (n2 = array[l]) + 3) {
                builder.stairs(n2, 2, 1, "spruce_stairs", "north");
                ++n2;
            }
        }
        builder.set(-1, 2, 6, "barrel[facing=up]");
        builder.set(11, 2, 0, "barrel[facing=north]");
        builder.set(12, 2, 0, "chest[facing=south]");
        for (int n3 = -34; n3 <= 34; ++n3) {
            if (n3 < -24 || n3 > -6) {
                builder.fill(n3, 1, -3, n3, 2, -3, "dark_oak_fence[east=true,west=true]");
            }
        }
        builder.fill(-2, 2, -2, 2, 4, -2, Material.DARK_OAK_PLANKS);
        builder.sign(0, 4, -1, "south", DyeColor.WHITE, "ESTACI\u00d3N DE", "VALDEMORA", "- and\u00e9n 1 -", "");
        builder.sign(-1, 3, -1, "south", DyeColor.WHITE, "Cruza las v\u00edas", "y entra en un", "arco para", "jugar");
        builder.sign(1, 3, -1, "south", DyeColor.RED, "SE BUSCA", "\u00cd\u00f1igo Varela", "sali\u00f3 al bosque", "y no volvi\u00f3");
        builder.sign(0, 2, -1, "south", DyeColor.GRAY, "La puerta", "SALIDA de la", "estaci\u00f3n te", "lleva a casa");
    }
    
    private static void tracks(final Builder builder) {
        for (int i = -34; i <= 34; ++i) {
            for (int j = 8; j <= 16; ++j) {
                builder.set(i, 0, j, builder.chance(0.85) ? Material.GRAVEL : Material.COARSE_DIRT);
            }
        }
        for (int k = -35; k <= 35; ++k) {
            if (!builder.chance(0.04)) {
                builder.set(k, 1, 10, "rail[shape=east_west]");
            }
            if (!builder.chance(0.04)) {
                builder.set(k, 1, 14, "rail[shape=east_west]");
            }
        }
        for (int l = 0; l < 70; ++l) {
            weed(builder, builder.range(-34, 34), builder.pick(8, 9, 11, 12, 13, 15, 16));
        }
    }
    
    private static void station(final Builder builder) {
        builder.room(-24, 1, -18, -6, 7, -3, Material.STONE_BRICKS, Material.SPRUCE_PLANKS, Material.DARK_OAK_PLANKS);
        builder.wallsMixed(-24, 2, -18, -6, 6, -3, Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.MOSSY_STONE_BRICKS);
        for (int i = -25; i <= -5; ++i) {
            for (int j = -19; j <= -1; ++j) {
                if (!builder.chance(0.06)) {
                    builder.set(i, 8, j, "dark_oak_slab[type=bottom]");
                }
            }
        }
        builder.air(-16, 2, -3, -14, 4, -3);
        for (final int n : new int[] { -21, -20, -19, -11, -10, -9 }) {
            for (int l = 3; l <= 4; ++l) {
                builder.set(n, l, -3, builder.chance(0.4) ? "air" : "iron_bars[east=true,west=true]");
            }
        }
        builder.fill(-23, 2, -12, -19, 2, -12, Material.POLISHED_ANDESITE);
        builder.fill(-23, 3, -12, -19, 4, -12, "iron_bars[east=true,west=true]");
        builder.sign(-21, 2, -11, "south", DyeColor.WHITE, "TAQUILLA", "", "CERRADA", "");
        for (int n2 = -22; n2 <= -18; ++n2) {
            builder.stairs(n2, 2, -7, "spruce_stairs", "south");
        }
        for (int n3 = -12; n3 <= -8; ++n3) {
            builder.stairs(n3, 2, -7, "spruce_stairs", "south");
            builder.stairs(n3, 2, -11, "spruce_stairs", "north");
        }
        builder.sign(-23, 3, -15, "east", DyeColor.WHITE, "DESAPARECIDA", "Luc\u00eda Abad", "vista en el", "sanatorio");
        builder.sign(-23, 3, -8, "east", DyeColor.WHITE, "DESAPARECIDOS", "familia Rold\u00e1n", "camino del", "maizal");
        builder.sign(-7, 3, -14, "west", DyeColor.RED, "NO MIRES", "AL HOMBRE", "SIN CARA", "");
        builder.sign(-7, 3, -9, "west", DyeColor.WHITE, "\u00daLTIMO TREN", "02:13", "", "no esperes");
        builder.sign(-7, 3, -5, "west", DyeColor.WHITE, "DESAPARECIDO", "Xos\u00e9 Lamas", "conserje del", "instituto");
        builder.set(-20, 6, -8, "lantern[hanging=true]");
        builder.set(-10, 6, -14, "lantern[hanging=true]");
        builder.set(-15, 6, -16, "soul_lantern[hanging=true]");
        for (final int[] array3 : new int[][] { { -23, -17 }, { -7, -17 }, { -23, -4 }, { -7, -4 }, { -15, -10 } }) {
            builder.set(array3[0], 6, array3[1], Material.COBWEB);
        }
        builder.sign(-15, 6, -2, "south", DyeColor.WHITE, "", "VALDEMORA", "", "");
    }
    
    private static Zone exitRoom(final Builder builder) {
        builder.air(-16, 2, -18, -14, 4, -18);
        builder.fill(-17, 1, -22, -13, 5, -19, Material.BLACK_CONCRETE);
        builder.air(-16, 2, -21, -14, 4, -19);
        builder.sign(-15, 5, -17, "south", DyeColor.RED, "", "SALIDA", "", "");
        return Zone.of(builder.abs(-16, 2, -21), builder.abs(-14, 4, -20));
    }
    
    private static Map<String, Zone> arches(final Builder builder) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (int i = 0; i < HubBuilder.ARCH_X.length; ++i) {
            final int n = HubBuilder.ARCH_X[i];
            stone(builder, n - 2, 1, 17, n + 2, 6, 19);
            builder.air(n - 1, 1, 17, n + 1, 4, 19);
            for (int j = 17; j <= 19; ++j) {
                builder.set(n - 1, 4, j, builder.pick(HubBuilder.WALL));
                builder.set(n + 1, 4, j, builder.pick(HubBuilder.WALL));
            }
            builder.fill(n - 1, 0, 17, n + 1, 0, 19, Material.POLISHED_DEEPSLATE);
            builder.fill(n - 1, 1, 20, n + 1, 4, 20, Material.BLACK_CONCRETE);
            builder.set(n, 4, 18, "soul_lantern[hanging=true]");
            builder.sign(n, 5, 16, "north", HubBuilder.ARCH_COLOR[i], "", HubBuilder.ARCH_LABEL[i], "", "");
            if (HubBuilder.ARCH_MAP[i] != null) {
                linkedHashMap.put(HubBuilder.ARCH_MAP[i], Zone.of(builder.abs(n - 1, 1, 18), builder.abs(n + 1, 3, 19)));
            }
        }
        for (int k = 0; k < 10; ++k) {
            final int n2 = HubBuilder.ARCH_X[builder.range(0, HubBuilder.ARCH_X.length - 2)] + builder.range(3, 7);
            int l = 0;
            while (l > (l = builder.range(6, 9)) - builder.range(2, 4)) {
                builder.set(n2, l, 19, "vine[south=true]");
                --l;
            }
        }
        return linkedHashMap;
    }
    
    private static void wagon(final Builder builder) {
        for (final int n : new int[] { -33, -29 }) {
            final int[] array2 = { 8, 12 };
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                builder.set(n, 1, array2[j], Material.POLISHED_BLACKSTONE);
            }
        }
        builder.fill(-34, 2, 8, -28, 2, 12, Material.OXIDIZED_CUT_COPPER);
        builder.walls(-34, 3, 8, -28, 5, 12, Material.WEATHERED_COPPER);
        builder.air(-33, 3, 9, -29, 5, 11);
        for (final int n2 : new int[] { -33, -32, -30, -29 }) {
            builder.set(n2, 4, 8, "iron_bars[east=true,west=true]");
            builder.set(n2, 4, 12, "iron_bars[east=true,west=true]");
        }
        builder.air(-31, 3, 8, -31, 4, 8);
        builder.fill(-34, 6, 8, -28, 6, 12, "oxidized_cut_copper_slab[type=bottom]");
        builder.set(-33, 5, 11, Material.COBWEB);
        builder.set(-29, 3, 9, Material.COBWEB);
    }
    
    private static void yard(final Builder builder) {
        for (int i = 0; i < 120; ++i) {
            final int range = builder.range(-34, 34);
            final int range2 = builder.range(-24, -4);
            if (range < -25 || range > -5 || range2 < -23) {
                weed(builder, range, range2);
            }
        }
        for (int j = 0; j < 16; ++j) {
            final int range3 = builder.range(-33, 33);
            final int range4 = builder.range(-23, -6);
            if (range3 < -26 || range3 > -4) {
                final int range5 = builder.range(4, 8);
                builder.fill(range3, 1, range4, range3, range5, range4, "spruce_log");
                if (builder.chance(0.6)) {
                    builder.set(range3 + 1, range5 - 1, range4, "spruce_log[axis=x]");
                }
                if (builder.chance(0.6)) {
                    builder.set(range3, range5 - 2, range4 - 1, "spruce_log[axis=z]");
                }
            }
        }
    }
    
    static {
        ORIGIN = new BlockPos(-2000, 64, 0);
        ARCH_X = new int[] { -25, -15, -5, 5, 15, 25 };
        ARCH_MAP = new String[] { null, "bosque", "sanatorio", "maizal", "instituto", null };
        ARCH_LABEL = new String[] { "???", "BOSQUE", "SANATORIO", "MAIZAL", "INSTITUTO", "???" };
        ARCH_COLOR = new DyeColor[] { DyeColor.GRAY, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.GRAY };
        WALL = new Material[] { Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.COBBLESTONE, Material.MOSSY_COBBLESTONE };
        WEEDS = new String[] { "short_grass", "short_grass", "fern", "dead_bush" };
    }
}
