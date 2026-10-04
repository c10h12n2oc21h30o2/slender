// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.cornfield;

import dev.two.slender.map.SoundZone;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Ambience;
import dev.two.slender.logic.MazeGenerator;
import java.util.Random;
import dev.two.slender.logic.Noise;
import dev.two.slender.logic.Heightmap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import dev.two.slender.map.gen.Note;
import dev.two.slender.map.gen.PageWall;
import dev.two.slender.map.gen.Post;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.gen.Place;
import java.util.List;

public final class CornfieldLayout
{
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
    public static final List<Place> PLACES;
    public static final List<Place> SCARECROWS;
    public static final int[][] LOOP;
    public static final int[][][] SPURS;
    public static final int[][] BRIDGE;
    public static final int[][] STREAM;
    public static final int[][] SPAWNS;
    static final List<String> CASA_0;
    static final List<String> CASA_1;
    static final List<String> GRANERO_0;
    static final List<String> GRANERO_1;
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
    
    public static Place place(final String s) {
        for (final Place place : CornfieldLayout.PLACES) {
            if (place.id().equals(s)) {
                return place;
            }
        }
        for (final Place place2 : CornfieldLayout.SCARECROWS) {
            if (place2.id().equals(s)) {
                return place2;
            }
        }
        throw new IllegalArgumentException("Lugar desconocido: " + s);
    }
    
    public static List<Place> all() {
        final ArrayList list = new ArrayList((Collection<? extends E>)CornfieldLayout.PLACES);
        list.addAll(CornfieldLayout.SCARECROWS);
        return list;
    }
    
    public static boolean inMaze(final int n, final int n2) {
        return n >= -42 && n <= 39 && n2 >= -36 && n2 <= 33;
    }
    
    public static Heightmap terrain(final long n) {
        final Heightmap heightmap = new Heightmap(88, -1);
        heightmap.hills(new Noise(n), 60.0, 3, 1.5, 3.0, -1, 5);
        heightmap.channel(CornfieldLayout.STREAM, 1, 2);
        for (final Place place : all()) {
            if (place.pad() <= 0) {
                continue;
            }
            heightmap.flatten(place.x(), place.z(), place.pad(), (place.y() == Integer.MIN_VALUE) ? heightmap.at(place.x(), place.z()) : place.y(), place.id().equals("molino") ? 9 : 3);
        }
        heightmap.flattenRect(-9, 60, 9, 88, 0, 6);
        heightmap.road(CornfieldLayout.LOOP, 2);
        final int[][][] spurs = CornfieldLayout.SPURS;
        for (int length = spurs.length, i = 0; i < length; ++i) {
            heightmap.road(spurs[i], 1);
        }
        heightmap.road(CornfieldLayout.BRIDGE, 2);
        heightmap.clampSlope();
        return heightmap;
    }
    
    public static boolean[][] maze(final long seed) {
        final boolean[][] generate = MazeGenerator.generate(20, 17, 0.35, new Random(seed));
        for (final Place place : all()) {
            if (!inMaze(place.x(), place.z())) {
                continue;
            }
            final int n = place.pad() + 1;
            MazeGenerator.carve(generate, gx(place.x() - n), gz(place.z() - n), gx(place.x() + n), gz(place.z() + n));
        }
        final int gx = gx(0);
        final int gz = gz(-2);
        generate[gx][0] = true;
        generate[gx][generate[0].length - 1] = true;
        generate[0][gz] = true;
        generate[generate.length - 1][gz] = true;
        return generate;
    }
    
    public static int gx(final int n) {
        return Math.floorDiv(n + 42, 2);
    }
    
    public static int gz(final int n) {
        return Math.floorDiv(n + 36, 2);
    }
    
    public static boolean corn(final boolean[][] array, final Heightmap heightmap, final int n, final int n2) {
        return inMaze(n, n2) && !array[gx(n)][gz(n2)] && !heightmap.is(n, n2, (byte)7);
    }
    
    public static List<int[]> poles(final Heightmap heightmap) {
        final ArrayList list = new ArrayList();
        int n = 0;
        for (int n2 = 0; n2 + 1 < CornfieldLayout.LOOP.length; ++n2) {
            final double n3 = CornfieldLayout.LOOP[n2][0];
            final double n4 = CornfieldLayout.LOOP[n2][1];
            final double n5 = CornfieldLayout.LOOP[n2 + 1][0];
            final double n6 = CornfieldLayout.LOOP[n2 + 1][1];
            for (int n7 = (int)Math.ceil(Math.hypot(n5 - n3, n6 - n4)), i = 0; i < n7; ++i, ++n) {
                if (n % 14 == 7) {
                    final double x = n3 + (n5 - n3) * i / n7;
                    final double y = n4 + (n6 - n4) * i / n7;
                    final double hypot = Math.hypot(x, y);
                    final int n8 = (int)Math.round(x + 4.0 * x / hypot);
                    final int n9 = (int)Math.round(y + 4.0 * y / hypot);
                    if (poleFits(heightmap, n8, n9)) {
                        list.add(new int[] { n8, n9 });
                    }
                }
            }
        }
        if (poleFits(heightmap, 4, 54)) {
            list.add(new int[] { 4, 54 });
        }
        return list;
    }
    
    private static boolean poleFits(final Heightmap heightmap, final int a, final int a2) {
        if (Math.abs(a) > 73 || Math.abs(a2) > 73 || inMaze(a, a2)) {
            return false;
        }
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (heightmap.is(a + i, a2 + j, (byte)15)) {
                    return false;
                }
            }
        }
        for (final Post post : CornfieldLayout.POSTS) {
            if (Math.abs(post.x() - a) <= 3 && Math.abs(post.z() - a2) <= 3) {
                return false;
            }
        }
        return true;
    }
    
    public static Ambience ambience(final Heightmap heightmap) {
        final ArrayList sounds = new ArrayList();
        sounds.add(zone(heightmap, 0, -2, 42, "BLOCK_BAMBOO_STEP", 0.5f, 0.6f, 0.12));
        sounds.add(zone(heightmap, 0, -2, 46, "ENTITY_BAT_TAKEOFF", 0.3f, 0.6f, 0.03));
        sounds.add(zone(heightmap, -62, -12, 14, "BLOCK_CHAIN_STEP", 0.6f, 0.5f, 0.15));
        sounds.add(zone(heightmap, 12, -50, 10, "ENTITY_CHICKEN_HURT", 0.4f, 0.8f, 0.06));
        sounds.add(zone(heightmap, 0, 0, 90, "ITEM_ELYTRA_FLYING", 0.15f, 0.5f, 0.04));
        sounds.add(zone(heightmap, 0, 49, 14, "BLOCK_WATER_AMBIENT", 0.6f, 0.8f, 0.2));
        sounds.add(zone(heightmap, -26, -60, 14, "ENTITY_COW_AMBIENT", 0.3f, 0.5f, 0.03));
        sounds.add(zone(heightmap, 28, -62, 12, "BLOCK_WOODEN_DOOR_CLOSE", 0.4f, 0.7f, 0.03));
        sounds.add(zone(heightmap, 0, -2, 10, "BLOCK_BEACON_AMBIENT", 0.5f, 0.6f, 0.3));
        sounds.add(zone(heightmap, -22, 16, 8, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.3f, 0.6f, 0.03));
        final ArrayList lights = new ArrayList();
        final Place place = place("molino");
        lights.add(new MapLight(new BlockPos(place.x(), place.y() + 16, place.z()), "blink"));
        lamps(lights, CornfieldLayout.CASA, heightmap.at(place("casa").x(), place("casa").z()));
        lamps(lights, CornfieldLayout.GRANERO, heightmap.at(place("granero").x(), place("granero").z()));
        for (final int[] array : poles(heightmap)) {
            lights.add(new MapLight(new BlockPos(array[0], heightmap.at(array[0], array[1]) + 7, array[1]), "flicker"));
        }
        return new Ambience(sounds, lights, CornfieldLayout.PHRASES);
    }
    
    private static void lamps(final List<MapLight> list, final FloorPlan floorPlan, final int n) {
        for (final FloorPlan.Cell cell : floorPlan.cells('L')) {
            list.add(new MapLight(new BlockPos(cell.x(), n + floorPlan.lampDy(cell.floor()), cell.z()), "flicker"));
        }
    }
    
    private static SoundZone zone(final Heightmap heightmap, final int x, final int z, final int radius, final String sound, final float volume, final float pitch, final double chance) {
        return new SoundZone(new BlockPos(x, heightmap.at(x, z) + 1, z), radius, sound, volume, pitch, chance);
    }
    
    static {
        PLACES = List.of(new Place[] { new Place("casa", "Casa Rold\u00e1n", 28, -62, 11), new Place("granero", "Granero", -26, -60, 12), new Place("silo", "Silo", -6, -67, 5), new Place("molino", "Molino", -62, -12, 4, 5), new Place("deposito", "Dep\u00f3sito", -52, -56, 4), new Place("circulo", "C\u00edrculo", 0, -2, 7), new Place("cosechadora", "Cosechadora", -22, 16, 4), new Place("calabazas", "Calabazas", 64, 22, 6), new Place("gallinero", "Gallinero", 12, -50, 4), new Place("pozo", "Pozo", 2, -58, 3), new Place("puente", "Puente", 0, 47, 0, -1), new Place("letrina", "Letrina", 44, -52, 3) });
        SCARECROWS = List.of(new Place("espantapajaros1", "Espantap\u00e1jaros", -28, -22, 3), new Place("espantapajaros2", "Espantap\u00e1jaros", 26, -24, 3), new Place("espantapajaros3", "Espantap\u00e1jaros", 28, 18, 3), new Place("espantapajaros4", "Espantap\u00e1jaros", -6, 24, 3));
        LOOP = new int[][] { { 0, 40 }, { 48, 38 }, { 56, 0 }, { 48, -40 }, { 0, -42 }, { -48, -40 }, { -56, 0 }, { -48, 38 }, { 0, 40 } };
        SPURS = new int[][][] { { { 0, 76 }, { 0, 40 } }, { { 0, 33 }, { 0, 40 } }, { { 0, -36 }, { 0, -42 } }, { { -42, -2 }, { -55, -2 } }, { { 39, -2 }, { 55, -2 } }, { { -26, -42 }, { -26, -49 } }, { { 28, -41 }, { 28, -53 } }, { { 0, -42 }, { 0, -55 } }, { { 44, -41 }, { 44, -49 } }, { { -48, -41 }, { -51, -51 } }, { { 0, -50 }, { -6, -61 } }, { { 1, -46 }, { 12, -46 } }, { { -54, -12 }, { -58, -12 } }, { { 53, 22 }, { 57, 22 } } };
        BRIDGE = new int[][] { { 0, 56 }, { 0, 42 } };
        STREAM = new int[][] { { -92, 47 }, { -60, 52 }, { -30, 46 }, { 0, 50 }, { 28, 45 }, { 58, 51 }, { 92, 47 } };
        SPAWNS = new int[][] { { -6, 70 }, { -3, 72 }, { 0, 70 }, { 3, 72 }, { 6, 70 }, { -4, 67 }, { 0, 66 }, { 4, 67 } };
        CASA_0 = List.of(new String[] { "#################", "#k.mm.#...#.....#", "#.....#.L.#.sss.#", "W.....d...d.....W", "#.tt..#..^#..t..#", "#.tt..#..^#.....#", "W....L#..^#..L..W", "#.....#..^#.....#", "#c....d...d....r#", "#.....#...#.....#", "###W####d####W###" });
        CASA_1 = List.of(new String[] { "#################", "#b...b#...#.bb..#", "#.....#.L.#.bb..#", "W..L..d...d.....W", "#.....#..o#..L..#", "#t....#..o#.....#", "#######..o#.....#", "#.....#..o#.....W", "#.u...#...d....t#", "W.....d...#.....#", "#################" });
        GRANERO_0 = List.of(new String[] { "###############", "#hh.f.....f.hh#", "#hh.f.....f.hh#", "#......L......#", "#fff.......fff#", "W.w.f.....f.w.W", "#...f.....f...#", "#.............#", "#fff...L...fff#", "W.w.f.....f.w.W", "#...f.....f...#", "#.............#", "#^.....cc.....#", "#^.....cc.....#", "#^......L.....#", "#^............#", "#^.........hh.#", "#...........hh#", "#####DDDDD#####" });
        GRANERO_1 = List.of(new String[] { "###############", "#hhh.....hhhhh#", "#hh.......hhhh#", "#......L....hh#", "#.............#", "W.............W", "#..........h..#", "#.............#", "#......L......#", "W.............W", "#hh...........#", "#.............#", "#o............#", "#o............#", "#o.....L......#", "#o............#", "#o..........h.#", "#...........hh#", "#####WWWWW#####" });
        CASA = new FloorPlan(20, -67, 0, 4, List.of(CornfieldLayout.CASA_0, CornfieldLayout.CASA_1));
        GRANERO = new FloorPlan(-33, -69, 0, 5, List.of(CornfieldLayout.GRANERO_0, CornfieldLayout.GRANERO_1));
        BATTERIES_OUT = new int[][] { { -4, 58 }, { 10, 43 }, { 58, 30 }, { 60, -20 }, { -60, 20 }, { -58, -28 }, { -44, -52 }, { 40, -46 }, { 66, 18 }, { -30, -46 }, { 16, -56 }, { -25, -22 }, { 29, -24 }, { -19, 16 }, { 5, -2 } };
        BATTERIES_CASA = new int[][] { { 0, 22, -61 }, { 1, 33, -61 } };
        BATTERIES_GRANERO = new int[][] { { 0, -24, -56 }, { 1, -26, -62 } };
        POSTS = List.of(new Post(-4, 43, "SOUTH", List.of("circulo", "casa", "molino", "calabazas")), new Post(-4, -45, "SOUTH", List.of("casa", "granero", "silo", "deposito")), new Post(-60, 4, "EAST", List.of("molino", "deposito", "cosechadora", "salida")), new Post(60, -6, "WEST", List.of("calabazas", "casa", "letrina", "salida")));
        PAGES = List.of(new PageWall[] { new PageWall("casa", -8, 2, -1, "EAST", "Cocina", "spruce_planks"), new PageWall("casa", 8, 2, 2, "WEST", "Sal\u00f3n", "spruce_planks"), new PageWall("casa", -5, 6, -5, "SOUTH", "Cuarto de los ni\u00f1os", "spruce_planks"), new PageWall("casa", 8, 6, 1, "WEST", "Cuarto de los padres", "spruce_planks"), new PageWall("granero", 7, 2, -2, "WEST", "Establo", "red_terracotta"), new PageWall("granero", 0, 7, -9, "SOUTH", "Pajar", "red_terracotta"), new PageWall("silo", -4, 2, 0, "EAST", "Silo", "light_gray_terracotta"), new PageWall("molino", -2, 2, 0, "EAST", "Molino", "cobblestone"), new PageWall("deposito", 2, 2, 2, "SOUTH", "Dep\u00f3sito de agua", "spruce_log"), new PageWall("circulo", 0, 2, 0, "SOUTH", "C\u00edrculo en el ma\u00edz", "polished_blackstone"), new PageWall("cosechadora", 0, 2, 1, "SOUTH", "Cosechadora", "yellow_concrete"), new PageWall("calabazas", 0, 2, -6, "SOUTH", "Huerto de calabazas", "spruce_planks"), new PageWall("gallinero", 0, 2, -2, "SOUTH", "Gallinero", "oak_planks"), new PageWall("pozo", 2, 2, 0, "EAST", "Pozo", "spruce_log"), new PageWall("puente", 2, 2, 1, "WEST", "Puente", "spruce_log"), new PageWall("letrina", 0, 2, -1, "SOUTH", "Letrina", "spruce_planks"), new PageWall("espantapajaros1", 0, 2, 0, "SOUTH", "Espantap\u00e1jaros del oeste", "hay_block"), new PageWall("espantapajaros3", 0, 2, 0, "SOUTH", "Espantap\u00e1jaros del este", "hay_block") });
        NOTES = List.of(new Note[] { new Note("casa", -7, 2, -3, "EAST", List.of("Familia Rold\u00e1n", "Anselmo, Pilar,", "Marta y Tom\u00e1s.", "Cosecha 1994."), "spruce_planks"), new Note("casa", -7, 6, -1, "EAST", List.of("Marta dibuja", "un hombre muy", "alto entre el", "ma\u00edz. Sin cara."), "spruce_planks"), new Note("casa", 7, 6, -3, "WEST", List.of("Anselmo sali\u00f3", "a buscarlos.", "No volvi\u00f3.", "P."), "spruce_planks"), new Note("granero", -6, 2, -3, "EAST", List.of("Las vacas no", "quieren entrar", "en el ma\u00edz.", ""), "red_terracotta"), new Note("granero", 6, 7, 2, "WEST", List.of("Tom\u00e1s se", "escond\u00eda aqu\u00ed.", "Dijo que lo", "ve\u00eda venir."), "red_terracotta"), new Note("silo", 5, 2, 0, "EAST", List.of("Algo golpea", "el silo por", "dentro. Est\u00e1", "vac\u00edo."), "light_gray_terracotta"), new Note("molino", 0, 2, 3, "SOUTH", List.of("El molino gira", "sin viento.", "Las cadenas", "suenan solas."), "cobblestone"), new Note("deposito", -2, 2, 3, "SOUTH", List.of("Desde arriba", "se ve un", "camino nuevo", "cada noche."), "spruce_log"), new Note("circulo", 0, 2, -1, "NORTH", List.of("Amaneci\u00f3 as\u00ed.", "Nadie pis\u00f3", "el ma\u00edz.", ""), "polished_blackstone"), new Note("cosechadora", 0, 2, -2, "NORTH", List.of("Se par\u00f3 sola", "a medio campo.", "La cabina", "estaba vac\u00eda."), "yellow_concrete"), new Note("calabazas", -2, 2, -5, "SOUTH", List.of("Marta tall\u00f3", "todas las", "calabazas.", "Ninguna sonr\u00ede."), "spruce_planks"), new Note("gallinero", 3, 2, 0, "EAST", List.of("Las gallinas", "ya no ponen.", "Miran al ma\u00edz", "toda la noche."), "oak_planks"), new Note("pozo", -3, 2, 0, "WEST", List.of("No bajes", "el cubo", "de noche.", ""), "spruce_log"), new Note("puente", -1, 2, 1, "EAST", List.of("Al otro lado", "del arroyo no", "nos sigue.", "Eso cre\u00edamos."), "spruce_log"), new Note("letrina", 2, 2, 0, "EAST", List.of("No salgas", "de noche", "al retrete.", "Usa el orinal."), "spruce_planks"), new Note("espantapajaros2", 0, 2, 1, "SOUTH", List.of("Pap\u00e1 los puso", "para los", "cuervos.", "No eran ellos."), "hay_block"), new Note("espantapajaros4", 0, 2, 1, "SOUTH", List.of("Este no lo", "puso nadie.", "", ""), "hay_block") });
        HOUSE_SIGN = new Note("casa", 0, 3, 6, "SOUTH", List.of("", "GRANJA", "ROLD\u00c1N", ""), "spruce_planks");
        PHRASES = List.of(new String[] { "EL MA\u00cdZ SUSURRA", "NO ENTRES EN EL MA\u00cdZ", "PAP\u00c1 NO VOLVI\u00d3", "EST\u00c1 EN EL C\u00cdRCULO", "LOS ESPANTAP\u00c1JAROS MIRAN", "EL MOLINO GIRA SOLO", "MAM\u00c1 ME LLAMA DEL MA\u00cdZ", "NO TIENE CARA", "LAS GALLINAS CALLARON", "NINGUNA CALABAZA SONR\u00cdE", "EL CAMINO CAMBIA", "ME HA VISTO" });
    }
}
