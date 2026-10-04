// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.asylum;

import dev.two.slender.map.MapLight;
import dev.two.slender.map.SoundZone;
import dev.two.slender.map.BlockPos;
import java.util.ArrayList;
import dev.two.slender.map.Ambience;
import dev.two.slender.logic.Noise;
import dev.two.slender.logic.Heightmap;
import java.util.Iterator;
import dev.two.slender.map.gen.Note;
import dev.two.slender.map.gen.PageWall;
import dev.two.slender.map.gen.Post;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.gen.FloorPlan;
import java.util.List;

public final class AsylumLayout
{
    public static final int R = 76;
    public static final int TERRAIN = 88;
    public static final int PLATEAU = 8;
    public static final int STOREY = 5;
    public static final int TOWER_TOP = 22;
    public static final int[] GROUNDS;
    public static final int[] AMBULANCE_LAMP;
    static final List<String> BASEMENT;
    static final List<String> GROUND;
    static final List<String> FIRST;
    public static final FloorPlan PLAN;
    public static final List<Place> PLACES;
    public static final int[][] ROAD;
    public static final int[][] PATH;
    public static final int[][] BACK_PATH;
    public static final int[][] SPAWNS;
    public static final int[][] BATTERIES_OUT;
    public static final int[][] BATTERIES_IN;
    public static final List<Post> POSTS;
    public static final List<PageWall> PAGES;
    public static final List<Note> NOTES;
    public static final List<Note> SIGNS;
    public static final List<String> PHRASES;
    
    private AsylumLayout() {
    }
    
    public static Place place(final String anObject) {
        for (final Place place : AsylumLayout.PLACES) {
            if (place.id().equals(anObject)) {
                return place;
            }
        }
        throw new IllegalArgumentException("Lugar desconocido: " + anObject);
    }
    
    public static Heightmap terrain(final long n) {
        final Heightmap heightmap = new Heightmap(88, -10);
        heightmap.hills(new Noise(n), 44.0, 3, 2.0, 5.0, -2, 9);
        heightmap.flattenRect(AsylumLayout.GROUNDS[0], AsylumLayout.GROUNDS[1], AsylumLayout.GROUNDS[2], AsylumLayout.GROUNDS[3], 8, 10);
        heightmap.flattenRect(-9, 60, 9, 88, 0, 6);
        heightmap.road(AsylumLayout.ROAD, 1);
        heightmap.road(AsylumLayout.PATH, 1);
        heightmap.road(AsylumLayout.BACK_PATH, 1);
        heightmap.clampSlope();
        return heightmap;
    }
    
    public static Ambience ambience(final Heightmap heightmap) {
        final int n = 4;
        final ArrayList sounds = new ArrayList();
        sounds.add(zone(heightmap, 0, -14, 40, "BLOCK_IRON_DOOR_CLOSE", 0.5f, 0.6f, 0.03));
        sounds.add(zone(heightmap, 23, 0, 14, "ENTITY_ZOMBIE_VILLAGER_AMBIENT", 0.4f, 0.5f, 0.05));
        sounds.add(new SoundZone(new BlockPos(0, n, -28), 9, "BLOCK_POINTED_DRIPSTONE_DRIP_WATER", 0.7f, 1.0f, 0.5));
        sounds.add(new SoundZone(new BlockPos(-32, n, 0), 8, "ENTITY_GHAST_AMBIENT", 0.15f, 0.5f, 0.02));
        sounds.add(zone(heightmap, 0, 5, 8, "AMBIENT_SOUL_SAND_VALLEY_ADDITIONS", 0.6f, 0.8f, 0.15));
        sounds.add(zone(heightmap, 0, 0, 22, "BLOCK_BELL_RESONATE", 0.3f, 0.5f, 0.01));
        sounds.add(zone(heightmap, 0, 52, 30, "ITEM_ELYTRA_FLYING", 0.15f, 0.5f, 0.05));
        sounds.add(zone(heightmap, 0, -40, 12, "ENTITY_VEX_AMBIENT", 0.3f, 0.5f, 0.03));
        sounds.add(zone(heightmap, -26, 18, 8, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.3f, 0.7f, 0.03));
        final ArrayList lights = new ArrayList();
        lights.add(new MapLight(new BlockPos(0, 30, 0), "blink"));
        for (final FloorPlan.Cell cell : AsylumLayout.PLAN.cells('L')) {
            lights.add(new MapLight(new BlockPos(cell.x(), 8 + AsylumLayout.PLAN.lampDy(cell.floor()), cell.z()), "flicker"));
        }
        final Place place = place("ambulancia");
        lights.add(new MapLight(new BlockPos(place.x() + AsylumLayout.AMBULANCE_LAMP[0], 8 + AsylumLayout.AMBULANCE_LAMP[1], place.z() + AsylumLayout.AMBULANCE_LAMP[2]), "flicker"));
        return new Ambience(sounds, lights, AsylumLayout.PHRASES);
    }
    
    private static SoundZone zone(final Heightmap heightmap, final int x, final int z, final int radius, final String sound, final float volume, final float pitch, final double chance) {
        return new SoundZone(new BlockPos(x, heightmap.at(x, z) + 1, z), radius, sound, volume, pitch, chance);
    }
    
    static {
        GROUNDS = new int[] { -46, -44, 46, 30 };
        AMBULANCE_LAMP = new int[] { 0, 3, 0 };
        BASEMENT = List.of(new String[] { "                               #############", "                               #~~~~~~~~~~~#", "                               #~~~~~~~~~~~#", "                               #~~~~~~~~~~~#", "                               #~~L~~~~~~~~#", "                               #...........#", "                               #.....^.....#", "                               #.....^.....#", "                               #.k...^..Lk.#", "                               #.....^.....#", "                               #.....^.....#", "                               #...........#", "                               #####DDD#####", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #.L.#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                            ########DDD########", "                            #.................#", "##########                  #.................#", "#mmmm....#                  #..............c..#", "#........#                  #.................#", "#........#                  #.................#", "#........####################.................#", "#........D..................D.................#", "#..<<<<<.D.........L........D...L.........L...#", "#........D..................D.................#", "#....L...####################.................#", "#........#                  #.................#", "#........#                  #.................#", "#mmmm....#                  #..c...........c..#", "##########                  #.................#", "                            #.................#", "                            ###################" });
        GROUND = List.of(new String[] { "                               ######D######", "                               #...#...#...#", "                               #...#...#...#", "                               W...d...d...W", "                               #...#L..#...#", "                               #...#...#...#", "                               #...#.o.#...#", "                               W####.o.####W", "                               #...#.o.#...#", "                               #...#.o.#...#", "                               #...d.o.d...#", "                               W...#...#...W", "                               #...#...#...#", "                               #...#...#...#", "                               #####...#####", "                               W...#...#...W", "                               #.k.#L..#...#", "                               #...d...d...#", "                               #.k.#...#.t.#", "                               W...#...#...W", "                               #####...#...#", "                               #...#...#...#", "                               #...#L..#.t.#", "                               W...d...d...W", "                               #...#...#...#", "                               #...#...#...#", "                            ########DDD########", "                            #.................#", "###W###W###W###W###W###W#####.................###W###W###W###W###W###W#####", "#.........#.....#.....#.....#.................#.b...#.b...#.b...#.b...#...#", "#..b.b.b..#.....#.....#.....#.......^^^.......#.....#.....#.....#.....#...#", "#....t....#.....#.....#.....#.......^^^.......#.....#.....#.....#.....#...#", "######d######d#####d#####d###.......^^^.......###d#####d#####d#####d####d##", "#...........................D.......^^^.......D..L.....L.....L...........L#", "D..ooooo....................D...L...^^^...L...D...........................D", "#.L..........L.....L.....L..D.................D...>>>>>...................#", "####d########d#####d#####d###.................###d#####d#####d#####d####d##", "#.........#.....#.....#.....#.................#.....#.....#.....#.....#...#", "#.........#.....#.....#.....#.rrrr............#.....#.....#.....#.....#...#", "#.........#.....#.....#.....#........L........#.b...#.b...#.b...#.b...#...#", "###W###W###W###W###W###W#####.................###W###W###W###W###W###W#####", "                            #.................#", "                            ###WW###DDD###WW###" });
        FIRST = List.of(new String[] { "                               #############", "                               #...#...#...#", "                               #b..#...#..b#", "                               W...d...d...W", "                               #...#L..#...#", "                               #...#...#...#", "                               #...#...#...#", "                               W####...####W", "                               #...#...#...#", "                               #b..#...#..b#", "                               #...d...d...#", "                               W...#...#...W", "                               #...#...#...#", "                               #...#.xxxx..#", "                               #####.xx#####", "                               W...#.xx#...W", "                               #b..#...#..b#", "                               #...d...d...#", "                               #...#...#...#", "                               W...#...#...W", "                               #####...#####", "                               #...#L..#...#", "                               #b..#...#..b#", "                               W...d...d...W", "                               #...#...#...#", "                               #...#...#...#", "                            ########DDD########", "                            #.................#", "###W###W###W###W###W###W#####.................###W###W###W###W###W###W#####", "#b..#...b.#...b.#...b.#...b.#.................#.b...#.b...#.b...#.b...#...#", "#...#.....#.....#.....#.....#.......ooo.......#.....#.....#.....#.....#...#", "#...#.....#.....#.....#.....#.......ooo.......#.....#.....#.....#.....#...#", "##d####d#####d#####d#####d###.......ooo.......###d#####d#####d#####d####d##", "#L..........L...........L...D.......ooo.......D...L...........L.........L.#", "#...........................D.......ooo.......D...........................#", "#...........................D.................D...ooooo...................#", "##d####d#####d#####d#####d###########D###########d#####d#####d#####d####d##", "#...#.....#.....#.....#.....#.................#.....#.....#.....#.....#...#", "#...#.....#.....#.....#.....#...ppp.....ppp...#.....#.....#.....#.....#...#", "#b..#...b.#...b.#...b.#...b.#........L........#.b...#.b...#.b...#.b...#...#", "###W###W###W###W###W###W#####...ppp.....ppp...###W###W###W###W###W###W#####", "                            #........a........#", "                            ###W###########W###" });
        PLAN = new FloorPlan(-37, -34, -1, 5, List.of(AsylumLayout.BASEMENT, AsylumLayout.GROUND, AsylumLayout.FIRST));
        PLACES = List.of(new Place("sanatorio", "Sanatorio", 0, 0, 0, 8), new Place("fuente", "Fuente", 0, 18, 4, 8), new Place("invernadero", "Invernadero", 28, 18, 6, 8), new Place("ambulancia", "Ambulancia", -26, 18, 5, 8), new Place("porteria", "Porter\u00eda", 10, 25, 4, 8), new Place("cementerio", "Cementerio", 0, -40, 4, 8));
        ROAD = new int[][] { { 0, 76 }, { 0, 60 }, { 18, 54 }, { 30, 46 }, { 22, 36 }, { 6, 32 }, { 0, 30 } };
        PATH = new int[][] { { 0, 30 }, { 0, 9 } };
        BACK_PATH = new int[][] { { 0, -35 }, { 0, -38 } };
        SPAWNS = new int[][] { { -6, 70 }, { -3, 72 }, { 0, 70 }, { 3, 72 }, { 6, 70 }, { -4, 67 }, { 0, 66 }, { 4, 67 } };
        BATTERIES_OUT = new int[][] { { -4, 58 }, { 24, 50 }, { 14, 33 }, { -30, 24 }, { 34, 24 }, { -20, -40 }, { 20, -40 }, { 40, -20 }, { -40, -20 } };
        BATTERIES_IN = new int[][] { { 0, 0, 6 }, { 0, 24, 0 }, { 0, -20, 0 }, { 0, 0, -20 }, { 1, 30, 0 }, { 1, -30, 0 }, { 1, 0, -15 }, { -1, 0, -15 }, { -1, -20, 0 }, { -1, 4, -28 } };
        POSTS = List.of(new Post(-4, 61, "SOUTH", List.of("sanatorio", "cementerio", "invernadero", "salida")), new Post(-5, 26, "SOUTH", List.of("sanatorio", "ambulancia", "invernadero", "cementerio")));
        PAGES = List.of(new PageWall[] { new PageWall("sanatorio", -9, 2, 4, "EAST", "Recepci\u00f3n", "white_terracotta"), new PageWall("sanatorio", 13, 2, -6, "SOUTH", "Habitaci\u00f3n A-1", "stone_bricks"), new PageWall("sanatorio", 25, 7, 6, "NORTH", "Habitaci\u00f3n A-12", "stone_bricks"), new PageWall("sanatorio", 30, 7, -6, "SOUTH", "Habitaci\u00f3n A-15", "stone_bricks"), new PageWall("sanatorio", -35, 2, -6, "SOUTH", "Quir\u00f3fano", "stone_bricks"), new PageWall("sanatorio", -25, 2, 6, "NORTH", "Farmacia", "stone_bricks"), new PageWall("sanatorio", -37, -3, 3, "EAST", "Morgue", "deepslate_bricks"), new PageWall("sanatorio", 6, -3, -27, "WEST", "Sala de calderas", "deepslate_bricks"), new PageWall("sanatorio", 6, 2, -12, "WEST", "Comedor", "stone_bricks"), new PageWall("sanatorio", -6, 2, -17, "EAST", "Cocina", "stone_bricks"), new PageWall("sanatorio", 2, 7, 8, "NORTH", "Capilla", "stone_bricks"), new PageWall("sanatorio", -6, 7, -28, "EAST", "Dormitorio C", "stone_bricks"), new PageWall("invernadero", 0, 2, -3, "NORTH", "Invernadero", "bricks"), new PageWall("ambulancia", 0, 2, 1, "SOUTH", "Ambulancia", "white_concrete"), new PageWall("porteria", 2, 2, 0, "EAST", "Porter\u00eda", "stone_bricks"), new PageWall("cementerio", 2, 2, 0, "EAST", "Pante\u00f3n", "polished_andesite") });
        NOTES = List.of(new Note[] { new Note("sanatorio", -8, 2, 3, "EAST", List.of("Luc\u00eda Abad", "enfermera de", "noche. Turno", "pabell\u00f3n A."), "white_terracotta"), new Note("sanatorio", -36, -3, -2, "EAST", List.of("Aqu\u00ed bajaban", "a los que", "no volv\u00edan", "a su cama."), "deepslate_bricks"), new Note("sanatorio", -5, -3, -24, "EAST", List.of("Las calderas", "se apagaron", "en 1987.", "El fr\u00edo no."), "deepslate_bricks"), new Note("sanatorio", 14, 7, -4, "WEST", List.of("Paciente 14:", "dibuja al", "hombre alto", "cada noche."), "blue_terracotta"), new Note("sanatorio", -36, 2, -3, "EAST", List.of("No operamos.", "Solo", "esper\u00e1bamos", "a que viniera."), "stone_bricks"), new Note("sanatorio", -8, 7, 5, "EAST", List.of("Rezamos para", "que no nos", "mire. Nos", "mir\u00f3. L.A."), "white_terracotta"), new Note("fuente", 0, 2, 1, "SOUTH", List.of("Nunca tuvo", "agua.", "", ""), "stone_bricks"), new Note("invernadero", 0, 2, 4, "SOUTH", List.of("Todo lo que", "plantamos", "sali\u00f3 muerto.", ""), "bricks"), new Note("ambulancia", 0, 2, -2, "NORTH", List.of("Vino a por", "un paciente.", "Se llev\u00f3", "a todos."), "white_concrete"), new Note("porteria", -1, 2, 1, "EAST", List.of("Nadie entra", "despu\u00e9s de", "las diez.", "Nadie sale."), "stone_bricks"), new Note("cementerio", 1, 2, 3, "SOUTH", List.of("Sin nombre", "1981-1987", "", ""), "polished_andesite") });
        SIGNS = List.of(new Note("sanatorio", 8, 3, 0, "WEST", List.of("PABELL\u00d3N A", "Habitaciones", "", "(azul)"), "white_terracotta"), new Note("sanatorio", -8, 3, 0, "EAST", List.of("PABELL\u00d3N B", "Quir\u00f3fano", "Farmacia", "Morgue: s\u00f3tano"), "white_terracotta"), new Note("sanatorio", 0, 3, -7, "SOUTH", List.of("PABELL\u00d3N C", "Comedor", "Cocina", "(verde)"), "white_terracotta"), new Note("sanatorio", 8, 8, 0, "WEST", List.of("PABELL\u00d3N A", "Habitaciones", "", "(azul)"), "white_terracotta"), new Note("sanatorio", -8, 8, 0, "EAST", List.of("PABELL\u00d3N B", "Habitaciones", "", "(rojo)"), "white_terracotta"), new Note("sanatorio", 0, 8, -7, "SOUTH", List.of("PABELL\u00d3N C", "Dormitorios", "¡Derrumbe!", "(verde)"), "white_terracotta"), new Note("sanatorio", -8, -2, 0, "EAST", List.of("", "MORGUE", "", ""), "deepslate_bricks"), new Note("sanatorio", 0, -2, -7, "SOUTH", List.of("", "CALDERAS", "", ""), "deepslate_bricks"), new Note("sanatorio", 0, 4, 9, "SOUTH", List.of("SANATORIO", "DE VALDEMORA", "1931", ""), "stone_bricks"));
        PHRASES = List.of(new String[] { "PACIENTE 14 LO DIBUJA", "NO APAGU\u00c9IS LAS LUCES", "LA MORGUE EST\u00c1 ABIERTA", "NADIE LLAM\u00d3 A LA PUERTA", "EST\u00c1 EN EL PASILLO", "NO TIENE CARA", "LAS CAMAS EST\u00c1N VAC\u00cdAS", "SE LLEV\u00d3 AL PABELL\u00d3N A", "NO BAJES AL S\u00d3TANO", "LA CAMPANA SON\u00d3 SOLA", "ME HA VISTO", "D\u00c9JAME SALIR" });
    }
}
