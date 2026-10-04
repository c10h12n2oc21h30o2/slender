package dev.two.slender.map.builders.cornfield;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.MazeGenerator;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.SoundZone;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.gen.Note;
import dev.two.slender.map.gen.PageWall;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.gen.Post;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class CornfieldLayout {
    public static final int R = 76;
    public static final int TERRAIN = 88;
    public static final int WATER = -1;
    public static final int MILL_TOP = 16;
    public static final int FX = -42;
    public static final int FZ = -36;
    public static final int CELLS_X = 20;
    public static final int CELLS_Z = 17;
    public static final int MAZE_X2 = 39;
    public static final int MAZE_Z2 = 33;
    public static final List<Place> PLACES = List.of(new Place("casa", "Casa Roldán", 28, -62, 11), new Place("granero", "Granero", -26, -60, 12), new Place("silo", "Silo", -6, -67, 5), new Place("molino", "Molino", -62, -12, 4, 5), new Place("deposito", "Depósito", -52, -56, 4), new Place("circulo", "Círculo", 0, -2, 7), new Place("cosechadora", "Cosechadora", -22, 16, 4), new Place("calabazas", "Calabazas", 64, 22, 6), new Place("gallinero", "Gallinero", 12, -50, 4), new Place("pozo", "Pozo", 2, -58, 3), new Place("puente", "Puente", 0, 47, 0, -1), new Place("letrina", "Letrina", 44, -52, 3));
    public static final List<Place> SCARECROWS = List.of(new Place("espantapajaros1", "Espantapájaros", -28, -22, 3), new Place("espantapajaros2", "Espantapájaros", 26, -24, 3), new Place("espantapajaros3", "Espantapájaros", 28, 18, 3), new Place("espantapajaros4", "Espantapájaros", -6, 24, 3));
    public static final int[][] LOOP = new int[][]{{0, 40}, {48, 38}, {56, 0}, {48, -40}, {0, -42}, {-48, -40}, {-56, 0}, {-48, 38}, {0, 40}};
    public static final int[][][] SPURS = new int[][][]{{{0, 76}, {0, 40}}, {{0, 33}, {0, 40}}, {{0, -36}, {0, -42}}, {{-42, -2}, {-55, -2}}, {{39, -2}, {55, -2}}, {{-26, -42}, {-26, -49}}, {{28, -41}, {28, -53}}, {{0, -42}, {0, -55}}, {{44, -41}, {44, -49}}, {{-48, -41}, {-51, -51}}, {{0, -50}, {-6, -61}}, {{1, -46}, {12, -46}}, {{-54, -12}, {-58, -12}}, {{53, 22}, {57, 22}}};
    public static final int[][] BRIDGE = new int[][]{{0, 56}, {0, 42}};
    public static final int[][] STREAM = new int[][]{{-92, 47}, {-60, 52}, {-30, 46}, {0, 50}, {28, 45}, {58, 51}, {92, 47}};
    public static final int[][] SPAWNS = new int[][]{{-6, 70}, {-3, 72}, {0, 70}, {3, 72}, {6, 70}, {-4, 67}, {0, 66}, {4, 67}};
    static final List<String> CASA_0 = List.of("#################", "#k.mm.#...#.....#", "#.....#.L.#.sss.#", "W.....d...d.....W", "#.tt..#..^#..t..#", "#.tt..#..^#.....#", "W....L#..^#..L..W", "#.....#..^#.....#", "#c....d...d....r#", "#.....#...#.....#", "###W####d####W###");
    static final List<String> CASA_1 = List.of("#################", "#b...b#...#.bb..#", "#.....#.L.#.bb..#", "W..L..d...d.....W", "#.....#..o#..L..#", "#t....#..o#.....#", "#######..o#.....#", "#.....#..o#.....W", "#.u...#...d....t#", "W.....d...#.....#", "#################");
    static final List<String> GRANERO_0 = List.of("###############", "#hh.f.....f.hh#", "#hh.f.....f.hh#", "#......L......#", "#fff.......fff#", "W.w.f.....f.w.W", "#...f.....f...#", "#.............#", "#fff...L...fff#", "W.w.f.....f.w.W", "#...f.....f...#", "#.............#", "#^.....cc.....#", "#^.....cc.....#", "#^......L.....#", "#^............#", "#^.........hh.#", "#...........hh#", "#####DDDDD#####");
    static final List<String> GRANERO_1 = List.of("###############", "#hhh.....hhhhh#", "#hh.......hhhh#", "#......L....hh#", "#.............#", "W.............W", "#..........h..#", "#.............#", "#......L......#", "W.............W", "#hh...........#", "#.............#", "#o............#", "#o............#", "#o.....L......#", "#o............#", "#o..........h.#", "#...........hh#", "#####WWWWW#####");
    public static final FloorPlan CASA;
    public static final FloorPlan GRANERO;
    public static final int[][] BATTERIES_OUT;
    public static final int[][] BATTERIES_CASA;
    public static final int[][] BATTERIES_GRANERO;
    public static final List<Post> POSTS;
    public static final List<PageWall> PAGES;
    public static final List<Note> NOTES;
    public static final Note HOUSE_SIGN;
    public static final List<String> PHRASES;

    private CornfieldLayout() {
    }

    public static Place place(String var0) {
        Iterator var1 = PLACES.iterator();

        Place var2;
        do {
            if (!var1.hasNext()) {
                var1 = SCARECROWS.iterator();

                do {
                    if (!var1.hasNext()) {
                        throw new IllegalArgumentException("Lugar desconocido: " + var0);
                    }

                    var2 = (Place)var1.next();
                } while(!var2.id().equals(var0));

                return var2;
            }

            var2 = (Place)var1.next();
        } while(!var2.id().equals(var0));

        return var2;
    }

    public static List<Place> all() {
        ArrayList var0 = new ArrayList(PLACES);
        var0.addAll(SCARECROWS);
        return var0;
    }

    public static boolean inMaze(int var0, int var1) {
        return var0 >= -42 && var0 <= 39 && var1 >= -36 && var1 <= 33;
    }

    public static Heightmap terrain(long var0) {
        Heightmap var2 = new Heightmap(88, -1);
        var2.hills(new Noise(var0), 60.0, 3, 1.5, 3.0, -1, 5);
        var2.channel(STREAM, 1, 2);
        Iterator var3 = all().iterator();

        while(var3.hasNext()) {
            Place var4 = (Place)var3.next();
            if (var4.pad() > 0) {
                var2.flatten(var4.x(), var4.z(), var4.pad(), var4.y() == Integer.MIN_VALUE ? var2.at(var4.x(), var4.z()) : var4.y(), var4.id().equals("molino") ? 9 : 3);
            }
        }

        var2.flattenRect(-9, 60, 9, 88, 0, 6);
        var2.road(LOOP, 2);
        int[][][] var7 = SPURS;
        int var8 = var7.length;

        for(int var5 = 0; var5 < var8; ++var5) {
            int[][] var6 = var7[var5];
            var2.road(var6, 1);
        }

        var2.road(BRIDGE, 2);
        var2.clampSlope();
        return var2;
    }

    public static boolean[][] maze(long var0) {
        boolean[][] var2 = MazeGenerator.generate(20, 17, 0.35, new Random(var0));
        Iterator var3 = all().iterator();

        while(var3.hasNext()) {
            Place var4 = (Place)var3.next();
            if (inMaze(var4.x(), var4.z())) {
                int var5 = var4.pad() + 1;
                MazeGenerator.carve(var2, gx(var4.x() - var5), gz(var4.z() - var5), gx(var4.x() + var5), gz(var4.z() + var5));
            }
        }

        int var6 = gx(0);
        int var7 = gz(-2);
        var2[var6][0] = true;
        var2[var6][var2[0].length - 1] = true;
        var2[0][var7] = true;
        var2[var2.length - 1][var7] = true;
        return var2;
    }

    public static int gx(int var0) {
        return Math.floorDiv(var0 - -42, 2);
    }

    public static int gz(int var0) {
        return Math.floorDiv(var0 - -36, 2);
    }

    public static boolean corn(boolean[][] var0, Heightmap var1, int var2, int var3) {
        if (inMaze(var2, var3) && !var0[gx(var2)][gz(var3)]) {
            return !var1.is(var2, var3, (byte)7);
        } else {
            return false;
        }
    }

    public static List<int[]> poles(Heightmap var0) {
        ArrayList var1 = new ArrayList();
        int var2 = 0;

        for(int var3 = 0; var3 + 1 < LOOP.length; ++var3) {
            double var4 = (double)LOOP[var3][0];
            double var6 = (double)LOOP[var3][1];
            double var8 = (double)LOOP[var3 + 1][0];
            double var10 = (double)LOOP[var3 + 1][1];
            int var12 = (int)Math.ceil(Math.hypot(var8 - var4, var10 - var6));

            for(int var13 = 0; var13 < var12; ++var2) {
                if (var2 % 14 == 7) {
                    double var14 = var4 + (var8 - var4) * (double)var13 / (double)var12;
                    double var16 = var6 + (var10 - var6) * (double)var13 / (double)var12;
                    double var18 = Math.hypot(var14, var16);
                    int var20 = (int)Math.round(var14 + 4.0 * var14 / var18);
                    int var21 = (int)Math.round(var16 + 4.0 * var16 / var18);
                    if (poleFits(var0, var20, var21)) {
                        var1.add(new int[]{var20, var21});
                    }
                }

                ++var13;
            }
        }

        if (poleFits(var0, 4, 54)) {
            var1.add(new int[]{4, 54});
        }

        return var1;
    }

    private static boolean poleFits(Heightmap var0, int var1, int var2) {
        if (Math.abs(var1) <= 73 && Math.abs(var2) <= 73 && !inMaze(var1, var2)) {
            for(int var3 = -1; var3 <= 1; ++var3) {
                for(int var4 = -1; var4 <= 1; ++var4) {
                    if (var0.is(var1 + var3, var2 + var4, (byte)15)) {
                        return false;
                    }
                }
            }

            Iterator var5 = POSTS.iterator();

            Post var6;
            do {
                if (!var5.hasNext()) {
                    return true;
                }

                var6 = (Post)var5.next();
            } while(Math.abs(var6.x() - var1) > 3 || Math.abs(var6.z() - var2) > 3);

            return false;
        } else {
            return false;
        }
    }

    public static Ambience ambience(Heightmap var0) {
        ArrayList var1 = new ArrayList();
        var1.add(zone(var0, 0, -2, 42, "BLOCK_BAMBOO_STEP", 0.5F, 0.6F, 0.12));
        var1.add(zone(var0, 0, -2, 46, "ENTITY_BAT_TAKEOFF", 0.3F, 0.6F, 0.03));
        var1.add(zone(var0, -62, -12, 14, "BLOCK_CHAIN_STEP", 0.6F, 0.5F, 0.15));
        var1.add(zone(var0, 12, -50, 10, "ENTITY_CHICKEN_HURT", 0.4F, 0.8F, 0.06));
        var1.add(zone(var0, 0, 0, 90, "ITEM_ELYTRA_FLYING", 0.15F, 0.5F, 0.04));
        var1.add(zone(var0, 0, 49, 14, "BLOCK_WATER_AMBIENT", 0.6F, 0.8F, 0.2));
        var1.add(zone(var0, -26, -60, 14, "ENTITY_COW_AMBIENT", 0.3F, 0.5F, 0.03));
        var1.add(zone(var0, 28, -62, 12, "BLOCK_WOODEN_DOOR_CLOSE", 0.4F, 0.7F, 0.03));
        var1.add(zone(var0, 0, -2, 10, "BLOCK_BEACON_AMBIENT", 0.5F, 0.6F, 0.3));
        var1.add(zone(var0, -22, 16, 8, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.3F, 0.6F, 0.03));
        ArrayList var2 = new ArrayList();
        Place var3 = place("molino");
        var2.add(new MapLight(new BlockPos(var3.x(), var3.y() + 16, var3.z()), "blink"));
        lamps(var2, CASA, var0.at(place("casa").x(), place("casa").z()));
        lamps(var2, GRANERO, var0.at(place("granero").x(), place("granero").z()));
        Iterator var4 = poles(var0).iterator();

        while(var4.hasNext()) {
            int[] var5 = (int[])var4.next();
            var2.add(new MapLight(new BlockPos(var5[0], var0.at(var5[0], var5[1]) + 7, var5[1]), "flicker"));
        }

        return new Ambience(var1, var2, PHRASES);
    }

    private static void lamps(List<MapLight> var0, FloorPlan var1, int var2) {
        Iterator var3 = var1.cells('L').iterator();

        while(var3.hasNext()) {
            FloorPlan.Cell var4 = (FloorPlan.Cell)var3.next();
            var0.add(new MapLight(new BlockPos(var4.x(), var2 + var1.lampDy(var4.floor()), var4.z()), "flicker"));
        }

    }

    private static SoundZone zone(Heightmap var0, int var1, int var2, int var3, String var4, float var5, float var6, double var7) {
        return new SoundZone(new BlockPos(var1, var0.at(var1, var2) + 1, var2), var3, var4, var5, var6, var7);
    }

    static {
        CASA = new FloorPlan(20, -67, 0, 4, List.of(CASA_0, CASA_1));
        GRANERO = new FloorPlan(-33, -69, 0, 5, List.of(GRANERO_0, GRANERO_1));
        BATTERIES_OUT = new int[][]{{-4, 58}, {10, 43}, {58, 30}, {60, -20}, {-60, 20}, {-58, -28}, {-44, -52}, {40, -46}, {66, 18}, {-30, -46}, {16, -56}, {-25, -22}, {29, -24}, {-19, 16}, {5, -2}};
        BATTERIES_CASA = new int[][]{{0, 22, -61}, {1, 33, -61}};
        BATTERIES_GRANERO = new int[][]{{0, -24, -56}, {1, -26, -62}};
        POSTS = List.of(new Post(-4, 43, "SOUTH", List.of("circulo", "casa", "molino", "calabazas")), new Post(-4, -45, "SOUTH", List.of("casa", "granero", "silo", "deposito")), new Post(-60, 4, "EAST", List.of("molino", "deposito", "cosechadora", "salida")), new Post(60, -6, "WEST", List.of("calabazas", "casa", "letrina", "salida")));
        PAGES = List.of(new PageWall("casa", -8, 2, -1, "EAST", "Cocina", "spruce_planks"), new PageWall("casa", 8, 2, 2, "WEST", "Salón", "spruce_planks"), new PageWall("casa", -5, 6, -5, "SOUTH", "Cuarto de los niños", "spruce_planks"), new PageWall("casa", 8, 6, 1, "WEST", "Cuarto de los padres", "spruce_planks"), new PageWall("granero", 7, 2, -2, "WEST", "Establo", "red_terracotta"), new PageWall("granero", 0, 7, -9, "SOUTH", "Pajar", "red_terracotta"), new PageWall("silo", -4, 2, 0, "EAST", "Silo", "light_gray_terracotta"), new PageWall("molino", -2, 2, 0, "EAST", "Molino", "cobblestone"), new PageWall("deposito", 2, 2, 2, "SOUTH", "Depósito de agua", "spruce_log"), new PageWall("circulo", 0, 2, 0, "SOUTH", "Círculo en el maíz", "polished_blackstone"), new PageWall("cosechadora", 0, 2, 1, "SOUTH", "Cosechadora", "yellow_concrete"), new PageWall("calabazas", 0, 2, -6, "SOUTH", "Huerto de calabazas", "spruce_planks"), new PageWall("gallinero", 0, 2, -2, "SOUTH", "Gallinero", "oak_planks"), new PageWall("pozo", 2, 2, 0, "EAST", "Pozo", "spruce_log"), new PageWall("puente", 2, 2, 1, "WEST", "Puente", "spruce_log"), new PageWall("letrina", 0, 2, -1, "SOUTH", "Letrina", "spruce_planks"), new PageWall("espantapajaros1", 0, 2, 0, "SOUTH", "Espantapájaros del oeste", "hay_block"), new PageWall("espantapajaros3", 0, 2, 0, "SOUTH", "Espantapájaros del este", "hay_block"));
        NOTES = List.of(new Note("casa", -7, 2, -3, "EAST", List.of("Familia Roldán", "Anselmo, Pilar,", "Marta y Tomás.", "Cosecha 1994."), "spruce_planks"), new Note("casa", -7, 6, -1, "EAST", List.of("Marta dibuja", "un hombre muy", "alto entre el", "maíz. Sin cara."), "spruce_planks"), new Note("casa", 7, 6, -3, "WEST", List.of("Anselmo salió", "a buscarlos.", "No volvió.", "P."), "spruce_planks"), new Note("granero", -6, 2, -3, "EAST", List.of("Las vacas no", "quieren entrar", "en el maíz.", ""), "red_terracotta"), new Note("granero", 6, 7, 2, "WEST", List.of("Tomás se", "escondía aquí.", "Dijo que lo", "veía venir."), "red_terracotta"), new Note("silo", 5, 2, 0, "EAST", List.of("Algo golpea", "el silo por", "dentro. Está", "vacío."), "light_gray_terracotta"), new Note("molino", 0, 2, 3, "SOUTH", List.of("El molino gira", "sin viento.", "Las cadenas", "suenan solas."), "cobblestone"), new Note("deposito", -2, 2, 3, "SOUTH", List.of("Desde arriba", "se ve un", "camino nuevo", "cada noche."), "spruce_log"), new Note("circulo", 0, 2, -1, "NORTH", List.of("Amaneció así.", "Nadie pisó", "el maíz.", ""), "polished_blackstone"), new Note("cosechadora", 0, 2, -2, "NORTH", List.of("Se paró sola", "a medio campo.", "La cabina", "estaba vacía."), "yellow_concrete"), new Note("calabazas", -2, 2, -5, "SOUTH", List.of("Marta talló", "todas las", "calabazas.", "Ninguna sonríe."), "spruce_planks"), new Note("gallinero", 3, 2, 0, "EAST", List.of("Las gallinas", "ya no ponen.", "Miran al maíz", "toda la noche."), "oak_planks"), new Note("pozo", -3, 2, 0, "WEST", List.of("No bajes", "el cubo", "de noche.", ""), "spruce_log"), new Note("puente", -1, 2, 1, "EAST", List.of("Al otro lado", "del arroyo no", "nos sigue.", "Eso creíamos."), "spruce_log"), new Note("letrina", 2, 2, 0, "EAST", List.of("No salgas", "de noche", "al retrete.", "Usa el orinal."), "spruce_planks"), new Note("espantapajaros2", 0, 2, 1, "SOUTH", List.of("Papá los puso", "para los", "cuervos.", "No eran ellos."), "hay_block"), new Note("espantapajaros4", 0, 2, 1, "SOUTH", List.of("Este no lo", "puso nadie.", "", ""), "hay_block"));
        HOUSE_SIGN = new Note("casa", 0, 3, 6, "SOUTH", List.of("", "GRANJA", "ROLDÁN", ""), "spruce_planks");
        PHRASES = List.of("EL MAÍZ SUSURRA", "NO ENTRES EN EL MAÍZ", "PAPÁ NO VOLVIÓ", "ESTÁ EN EL CÍRCULO", "LOS ESPANTAPÁJAROS MIRAN", "EL MOLINO GIRA SOLO", "MAMÁ ME LLAMA DEL MAÍZ", "NO TIENE CARA", "LAS GALLINAS CALLARON", "NINGUNA CALABAZA SONRÍE", "EL CAMINO CAMBIA", "ME HA VISTO");
    }
}
