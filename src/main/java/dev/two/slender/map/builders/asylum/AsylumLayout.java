package dev.two.slender.map.builders.asylum;

import dev.two.slender.logic.Heightmap;
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

public final class AsylumLayout {
    public static final int R = 76;
    public static final int TERRAIN = 88;
    public static final int PLATEAU = 8;
    public static final int STOREY = 5;
    public static final int TOWER_TOP = 22;
    public static final int[] GROUNDS = new int[]{-46, -44, 46, 30};
    public static final int[] AMBULANCE_LAMP = new int[]{0, 3, 0};
    static final List<String> BASEMENT = List.of("                               #############", "                               #~~~~~~~~~~~#", "                               #~~~~~~~~~~~#", "                               #~~~~~~~~~~~#", "                               #~~L~~~~~~~~#", "                               #...........#", "                               #.....^.....#", "                               #.....^.....#", "                               #.k...^..Lk.#", "                               #.....^.....#", "                               #.....^.....#", "                               #...........#", "                               #####DDD#####", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #.L.#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                                   #...#", "                            ########DDD########", "                            #.................#", "##########                  #.................#", "#mmmm....#                  #..............c..#", "#........#                  #.................#", "#........#                  #.................#", "#........####################.................#", "#........D..................D.................#", "#..<<<<<.D.........L........D...L.........L...#", "#........D..................D.................#", "#....L...####################.................#", "#........#                  #.................#", "#........#                  #.................#", "#mmmm....#                  #..c...........c..#", "##########                  #.................#", "                            #.................#", "                            ###################");
    static final List<String> GROUND = List.of("                               ######D######", "                               #...#...#...#", "                               #...#...#...#", "                               W...d...d...W", "                               #...#L..#...#", "                               #...#...#...#", "                               #...#.o.#...#", "                               W####.o.####W", "                               #...#.o.#...#", "                               #...#.o.#...#", "                               #...d.o.d...#", "                               W...#...#...W", "                               #...#...#...#", "                               #...#...#...#", "                               #####...#####", "                               W...#...#...W", "                               #.k.#L..#...#", "                               #...d...d...#", "                               #.k.#...#.t.#", "                               W...#...#...W", "                               #####...#...#", "                               #...#...#...#", "                               #...#L..#.t.#", "                               W...d...d...W", "                               #...#...#...#", "                               #...#...#...#", "                            ########DDD########", "                            #.................#", "###W###W###W###W###W###W#####.................###W###W###W###W###W###W#####", "#.........#.....#.....#.....#.................#.b...#.b...#.b...#.b...#...#", "#..b.b.b..#.....#.....#.....#.......^^^.......#.....#.....#.....#.....#...#", "#....t....#.....#.....#.....#.......^^^.......#.....#.....#.....#.....#...#", "######d######d#####d#####d###.......^^^.......###d#####d#####d#####d####d##", "#...........................D.......^^^.......D..L.....L.....L...........L#", "D..ooooo....................D...L...^^^...L...D...........................D", "#.L..........L.....L.....L..D.................D...>>>>>...................#", "####d########d#####d#####d###.................###d#####d#####d#####d####d##", "#.........#.....#.....#.....#.................#.....#.....#.....#.....#...#", "#.........#.....#.....#.....#.rrrr............#.....#.....#.....#.....#...#", "#.........#.....#.....#.....#........L........#.b...#.b...#.b...#.b...#...#", "###W###W###W###W###W###W#####.................###W###W###W###W###W###W#####", "                            #.................#", "                            ###WW###DDD###WW###");
    static final List<String> FIRST = List.of("                               #############", "                               #...#...#...#", "                               #b..#...#..b#", "                               W...d...d...W", "                               #...#L..#...#", "                               #...#...#...#", "                               #...#...#...#", "                               W####...####W", "                               #...#...#...#", "                               #b..#...#..b#", "                               #...d...d...#", "                               W...#...#...W", "                               #...#...#...#", "                               #...#.xxxx..#", "                               #####.xx#####", "                               W...#.xx#...W", "                               #b..#...#..b#", "                               #...d...d...#", "                               #...#...#...#", "                               W...#...#...W", "                               #####...#####", "                               #...#L..#...#", "                               #b..#...#..b#", "                               W...d...d...W", "                               #...#...#...#", "                               #...#...#...#", "                            ########DDD########", "                            #.................#", "###W###W###W###W###W###W#####.................###W###W###W###W###W###W#####", "#b..#...b.#...b.#...b.#...b.#.................#.b...#.b...#.b...#.b...#...#", "#...#.....#.....#.....#.....#.......ooo.......#.....#.....#.....#.....#...#", "#...#.....#.....#.....#.....#.......ooo.......#.....#.....#.....#.....#...#", "##d####d#####d#####d#####d###.......ooo.......###d#####d#####d#####d####d##", "#L..........L...........L...D.......ooo.......D...L...........L.........L.#", "#...........................D.......ooo.......D...........................#", "#...........................D.................D...ooooo...................#", "##d####d#####d#####d#####d###########D###########d#####d#####d#####d####d##", "#...#.....#.....#.....#.....#.................#.....#.....#.....#.....#...#", "#...#.....#.....#.....#.....#...ppp.....ppp...#.....#.....#.....#.....#...#", "#b..#...b.#...b.#...b.#...b.#........L........#.b...#.b...#.b...#.b...#...#", "###W###W###W###W###W###W#####...ppp.....ppp...###W###W###W###W###W###W#####", "                            #........a........#", "                            ###W###########W###");
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

    public static Place place(String var0) {
        Iterator var1 = PLACES.iterator();

        Place var2;
        do {
            if (!var1.hasNext()) {
                throw new IllegalArgumentException("Lugar desconocido: " + var0);
            }

            var2 = (Place)var1.next();
        } while(!var2.id().equals(var0));

        return var2;
    }

    public static Heightmap terrain(long var0) {
        Heightmap var2 = new Heightmap(88, -10);
        var2.hills(new Noise(var0), 44.0, 3, 2.0, 5.0, -2, 9);
        var2.flattenRect(GROUNDS[0], GROUNDS[1], GROUNDS[2], GROUNDS[3], 8, 10);
        var2.flattenRect(-9, 60, 9, 88, 0, 6);
        var2.road(ROAD, 1);
        var2.road(PATH, 1);
        var2.road(BACK_PATH, 1);
        var2.clampSlope();
        return var2;
    }

    public static Ambience ambience(Heightmap var0) {
        byte var1 = 4;
        ArrayList var2 = new ArrayList();
        var2.add(zone(var0, 0, -14, 40, "BLOCK_IRON_DOOR_CLOSE", 0.5F, 0.6F, 0.03));
        var2.add(zone(var0, 23, 0, 14, "ENTITY_ZOMBIE_VILLAGER_AMBIENT", 0.4F, 0.5F, 0.05));
        var2.add(new SoundZone(new BlockPos(0, var1, -28), 9, "BLOCK_POINTED_DRIPSTONE_DRIP_WATER", 0.7F, 1.0F, 0.5));
        var2.add(new SoundZone(new BlockPos(-32, var1, 0), 8, "ENTITY_GHAST_AMBIENT", 0.15F, 0.5F, 0.02));
        var2.add(zone(var0, 0, 5, 8, "AMBIENT_SOUL_SAND_VALLEY_ADDITIONS", 0.6F, 0.8F, 0.15));
        var2.add(zone(var0, 0, 0, 22, "BLOCK_BELL_RESONATE", 0.3F, 0.5F, 0.01));
        var2.add(zone(var0, 0, 52, 30, "ITEM_ELYTRA_FLYING", 0.15F, 0.5F, 0.05));
        var2.add(zone(var0, 0, -40, 12, "ENTITY_VEX_AMBIENT", 0.3F, 0.5F, 0.03));
        var2.add(zone(var0, -26, 18, 8, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.3F, 0.7F, 0.03));
        ArrayList var3 = new ArrayList();
        var3.add(new MapLight(new BlockPos(0, 30, 0), "blink"));
        Iterator var4 = PLAN.cells('L').iterator();

        while(var4.hasNext()) {
            FloorPlan.Cell var5 = (FloorPlan.Cell)var4.next();
            var3.add(new MapLight(new BlockPos(var5.x(), 8 + PLAN.lampDy(var5.floor()), var5.z()), "flicker"));
        }

        Place var6 = place("ambulancia");
        var3.add(new MapLight(new BlockPos(var6.x() + AMBULANCE_LAMP[0], 8 + AMBULANCE_LAMP[1], var6.z() + AMBULANCE_LAMP[2]), "flicker"));
        return new Ambience(var2, var3, PHRASES);
    }

    private static SoundZone zone(Heightmap var0, int var1, int var2, int var3, String var4, float var5, float var6, double var7) {
        return new SoundZone(new BlockPos(var1, var0.at(var1, var2) + 1, var2), var3, var4, var5, var6, var7);
    }

    static {
        PLAN = new FloorPlan(-37, -34, -1, 5, List.of(BASEMENT, GROUND, FIRST));
        PLACES = List.of(new Place("sanatorio", "Sanatorio", 0, 0, 0, 8), new Place("fuente", "Fuente", 0, 18, 4, 8), new Place("invernadero", "Invernadero", 28, 18, 6, 8), new Place("ambulancia", "Ambulancia", -26, 18, 5, 8), new Place("porteria", "Portería", 10, 25, 4, 8), new Place("cementerio", "Cementerio", 0, -40, 4, 8));
        ROAD = new int[][]{{0, 76}, {0, 60}, {18, 54}, {30, 46}, {22, 36}, {6, 32}, {0, 30}};
        PATH = new int[][]{{0, 30}, {0, 9}};
        BACK_PATH = new int[][]{{0, -35}, {0, -38}};
        SPAWNS = new int[][]{{-6, 70}, {-3, 72}, {0, 70}, {3, 72}, {6, 70}, {-4, 67}, {0, 66}, {4, 67}};
        BATTERIES_OUT = new int[][]{{-4, 58}, {24, 50}, {14, 33}, {-30, 24}, {34, 24}, {-20, -40}, {20, -40}, {40, -20}, {-40, -20}};
        BATTERIES_IN = new int[][]{{0, 0, 6}, {0, 24, 0}, {0, -20, 0}, {0, 0, -20}, {1, 30, 0}, {1, -30, 0}, {1, 0, -15}, {-1, 0, -15}, {-1, -20, 0}, {-1, 4, -28}};
        POSTS = List.of(new Post(-4, 61, "SOUTH", List.of("sanatorio", "cementerio", "invernadero", "salida")), new Post(-5, 26, "SOUTH", List.of("sanatorio", "ambulancia", "invernadero", "cementerio")));
        PAGES = List.of(new PageWall("sanatorio", -9, 2, 4, "EAST", "Recepción", "white_terracotta"), new PageWall("sanatorio", 13, 2, -6, "SOUTH", "Habitación A-1", "stone_bricks"), new PageWall("sanatorio", 25, 7, 6, "NORTH", "Habitación A-12", "stone_bricks"), new PageWall("sanatorio", 30, 7, -6, "SOUTH", "Habitación A-15", "stone_bricks"), new PageWall("sanatorio", -35, 2, -6, "SOUTH", "Quirófano", "stone_bricks"), new PageWall("sanatorio", -25, 2, 6, "NORTH", "Farmacia", "stone_bricks"), new PageWall("sanatorio", -37, -3, 3, "EAST", "Morgue", "deepslate_bricks"), new PageWall("sanatorio", 6, -3, -27, "WEST", "Sala de calderas", "deepslate_bricks"), new PageWall("sanatorio", 6, 2, -12, "WEST", "Comedor", "stone_bricks"), new PageWall("sanatorio", -6, 2, -17, "EAST", "Cocina", "stone_bricks"), new PageWall("sanatorio", 2, 7, 8, "NORTH", "Capilla", "stone_bricks"), new PageWall("sanatorio", -6, 7, -28, "EAST", "Dormitorio C", "stone_bricks"), new PageWall("invernadero", 0, 2, -3, "NORTH", "Invernadero", "bricks"), new PageWall("ambulancia", 0, 2, 1, "SOUTH", "Ambulancia", "white_concrete"), new PageWall("porteria", 2, 2, 0, "EAST", "Portería", "stone_bricks"), new PageWall("cementerio", 2, 2, 0, "EAST", "Panteón", "polished_andesite"));
        NOTES = List.of(new Note("sanatorio", -8, 2, 3, "EAST", List.of("Lucía Abad", "enfermera de", "noche. Turno", "pabellón A."), "white_terracotta"), new Note("sanatorio", -36, -3, -2, "EAST", List.of("Aquí bajaban", "a los que", "no volvían", "a su cama."), "deepslate_bricks"), new Note("sanatorio", -5, -3, -24, "EAST", List.of("Las calderas", "se apagaron", "en 1987.", "El frío no."), "deepslate_bricks"), new Note("sanatorio", 14, 7, -4, "WEST", List.of("Paciente 14:", "dibuja al", "hombre alto", "cada noche."), "blue_terracotta"), new Note("sanatorio", -36, 2, -3, "EAST", List.of("No operamos.", "Solo", "esperábamos", "a que viniera."), "stone_bricks"), new Note("sanatorio", -8, 7, 5, "EAST", List.of("Rezamos para", "que no nos", "mire. Nos", "miró. L.A."), "white_terracotta"), new Note("fuente", 0, 2, 1, "SOUTH", List.of("Nunca tuvo", "agua.", "", ""), "stone_bricks"), new Note("invernadero", 0, 2, 4, "SOUTH", List.of("Todo lo que", "plantamos", "salió muerto.", ""), "bricks"), new Note("ambulancia", 0, 2, -2, "NORTH", List.of("Vino a por", "un paciente.", "Se llevó", "a todos."), "white_concrete"), new Note("porteria", -1, 2, 1, "EAST", List.of("Nadie entra", "después de", "las diez.", "Nadie sale."), "stone_bricks"), new Note("cementerio", 1, 2, 3, "SOUTH", List.of("Sin nombre", "1981-1987", "", ""), "polished_andesite"));
        SIGNS = List.of(new Note("sanatorio", 8, 3, 0, "WEST", List.of("PABELLÓN A", "Habitaciones", "", "(azul)"), "white_terracotta"), new Note("sanatorio", -8, 3, 0, "EAST", List.of("PABELLÓN B", "Quirófano", "Farmacia", "Morgue: sótano"), "white_terracotta"), new Note("sanatorio", 0, 3, -7, "SOUTH", List.of("PABELLÓN C", "Comedor", "Cocina", "(verde)"), "white_terracotta"), new Note("sanatorio", 8, 8, 0, "WEST", List.of("PABELLÓN A", "Habitaciones", "", "(azul)"), "white_terracotta"), new Note("sanatorio", -8, 8, 0, "EAST", List.of("PABELLÓN B", "Habitaciones", "", "(rojo)"), "white_terracotta"), new Note("sanatorio", 0, 8, -7, "SOUTH", List.of("PABELLÓN C", "Dormitorios", "¡Derrumbe!", "(verde)"), "white_terracotta"), new Note("sanatorio", -8, -2, 0, "EAST", List.of("", "MORGUE", "", ""), "deepslate_bricks"), new Note("sanatorio", 0, -2, -7, "SOUTH", List.of("", "CALDERAS", "", ""), "deepslate_bricks"), new Note("sanatorio", 0, 4, 9, "SOUTH", List.of("SANATORIO", "DE VALDEMORA", "1931", ""), "stone_bricks"));
        PHRASES = List.of("PACIENTE 14 LO DIBUJA", "NO APAGUÉIS LAS LUCES", "LA MORGUE ESTÁ ABIERTA", "NADIE LLAMÓ A LA PUERTA", "ESTÁ EN EL PASILLO", "NO TIENE CARA", "LAS CAMAS ESTÁN VACÍAS", "SE LLEVÓ AL PABELLÓN A", "NO BAJES AL SÓTANO", "LA CAMPANA SONÓ SOLA", "ME HA VISTO", "DÉJAME SALIR");
    }
}
