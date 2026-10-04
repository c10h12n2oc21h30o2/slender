// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.instituto;

import dev.two.slender.map.SoundZone;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.schem.Schematic;
import dev.two.slender.logic.Heightmap;
import java.io.InputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import dev.two.slender.map.gen.FloorPlan;
import java.util.Iterator;
import dev.two.slender.map.gen.Post;
import dev.two.slender.map.gen.Note;
import dev.two.slender.map.gen.PageWall;
import dev.two.slender.map.gen.Place;
import java.util.Map;
import java.util.List;

public final class InstitutoLayout
{
    public static final String SCHEMATIC = "maps/instituto.schem";
    public static final int SHIFT_X = -200;
    public static final int SHIFT_Y = -1;
    public static final int SHIFT_Z = -218;
    public static final int GROUND = 1;
    public static final int STOREY = 5;
    public static final int[] BOUNDS;
    public static final int FENCE_X1 = 1;
    public static final int FENCE_X2 = 300;
    public static final int FENCE_Z1 = 1;
    public static final int GATE_Z = 432;
    public static final int GATE_X1 = 4;
    public static final int GATE_X2 = 35;
    public static final int[] EXIT;
    public static final int[] BEACON;
    public static final int[][] SPAWNS;
    public static final int[] BOARD;
    public static final List<Room> ROOMS;
    public static final Map<String, int[]> BUILDINGS;
    public static final List<Place> PLACES;
    public static final List<PageWall> PAGES;
    public static final List<Note> NOTES;
    public static final int[][] BATTERIES_OUT;
    public static final int[][] BATTERIES_IN;
    public static final List<Post> POSTS;
    public static final List<String> PHRASES;
    
    private InstitutoLayout() {
    }
    
    public static Place place(final String anObject) {
        for (final Place place : InstitutoLayout.PLACES) {
            if (place.id().equals(anObject)) {
                return place;
            }
        }
        throw new IllegalArgumentException("Lugar desconocido: " + anObject);
    }
    
    public static FloorPlan plan(final String s) {
        final int[] array = InstitutoLayout.BUILDINGS.get(s);
        if (array == null) {
            throw new IllegalArgumentException("Edificio desconocido: " + s);
        }
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array[2]; ++i) {
            list.add(rows("maps/instituto/" + s + i + ".txt"));
        }
        return new FloorPlan(array[0], array[1], 0, 5, list);
    }
    
    static List<String> rows(final String name) {
        try (final InputStream resourceAsStream = InstitutoLayout.class.getClassLoader().getResourceAsStream(name)) {
            if (resourceAsStream == null) {
                throw new IllegalStateException("No est\u00e1 en el jar: " + name);
            }
            return new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)).lines().toList();
        }
        catch (final IOException cause) {
            throw new UncheckedIOException(cause);
        }
    }
    
    public static Heightmap ground() {
        final Heightmap heightmap = new Heightmap(460, -10);
        for (int i = 0; i <= 460; ++i) {
            for (int j = 0; j <= 460; ++j) {
                heightmap.set(i, j, 1);
            }
        }
        return heightmap;
    }
    
    public static List<Note> signs() {
        final ArrayList list = new ArrayList();
        final Map<String, String> of = Map.of("A", "a", "B", "b", "C", "c", "P", "poli");
        for (final Room room : InstitutoLayout.ROOMS) {
            if (room.sign() == null) {
                continue;
            }
            int doorX = room.doorX();
            int doorZ = room.doorZ();
            String facing;
            if (doorZ == room.z1()) {
                --doorZ;
                facing = "NORTH";
            }
            else if (doorZ == room.z2()) {
                ++doorZ;
                facing = "SOUTH";
            }
            else if (doorX == room.x1()) {
                --doorX;
                facing = "WEST";
            }
            else {
                ++doorX;
                facing = "EAST";
            }
            final Place place = place(of.get(room.building()));
            list.add(new Note(place.id(), doorX - place.x(), 3 + room.floor() * 5, doorZ - place.z(), facing, List.of("", room.sign(), "", ""), "white_concrete"));
        }
        return list;
    }
    
    public static List<int[]> streetLamps(final Schematic schematic) {
        final ArrayList list = new ArrayList();
        for (int i = 12; i <= 420; i += 24) {
            list.add(new int[] { 21, i });
        }
        for (final int n : new int[] { 56, 96 }) {
            final int[] array2 = { 24, 64, 104 };
            for (int length2 = array2.length, k = 0; k < length2; ++k) {
                list.add(new int[] { n, array2[k] });
            }
        }
        for (int l = 150; l <= 260; l += 36) {
            list.add(new int[] { 54, l });
        }
        final ArrayList list2 = new ArrayList();
        for (final int[] array3 : list) {
            boolean b = true;
            for (int n2 = -1; n2 <= 1; ++n2) {
                for (int n3 = -1; n3 <= 1; ++n3) {
                    for (int n4 = 2; n4 <= 9; ++n4) {
                        if (!schematic.isAir(array3[0] + n2, n4, array3[1] + n3)) {
                            b = false;
                        }
                    }
                }
            }
            if (b && Math.abs(array3[1] - 146) > 5) {
                list2.add(array3);
            }
        }
        return list2;
    }
    
    public static Ambience ambience(final Schematic schematic) {
        final ArrayList sounds = new ArrayList();
        sounds.add(zone(100, 2, 190, 60, "BLOCK_BELL_USE", 0.4f, 0.5f, 0.01));
        sounds.add(zone(100, 2, 190, 30, "BLOCK_WOODEN_DOOR_CLOSE", 0.4f, 0.7f, 0.03));
        sounds.add(zone(205, 2, 170, 25, "BLOCK_WOODEN_DOOR_CLOSE", 0.4f, 0.7f, 0.03));
        sounds.add(zone(100, 2, 70, 30, "BLOCK_WOODEN_DOOR_CLOSE", 0.4f, 0.7f, 0.03));
        sounds.add(zone(212, 2, 240, 18, "ENTITY_SLIME_JUMP", 0.5f, 0.5f, 0.06));
        sounds.add(zone(170, 2, 330, 55, "ENTITY_FROG_AMBIENT", 0.5f, 0.6f, 0.08));
        sounds.add(zone(170, 2, 330, 45, "BLOCK_WATER_AMBIENT", 0.4f, 0.6f, 0.08));
        sounds.add(zone(150, 2, 220, 260, "ITEM_ELYTRA_FLYING", 0.15f, 0.5f, 0.04));
        sounds.add(zone(86, 7, 152, 12, "ENTITY_VEX_AMBIENT", 0.3f, 0.5f, 0.03));
        sounds.add(zone(99, 2, 200, 25, "BLOCK_BEACON_AMBIENT", 0.2f, 0.7f, 0.05));
        final ArrayList lights = new ArrayList();
        lights.add(new MapLight(new BlockPos(InstitutoLayout.BEACON[0], InstitutoLayout.BEACON[1], InstitutoLayout.BEACON[2]), "blink"));
        final Iterator<String> iterator = List.of("A", "B", "C", "P").iterator();
        while (iterator.hasNext()) {
            final FloorPlan plan = plan(iterator.next());
            for (final FloorPlan.Cell cell : plan.cells('L')) {
                lights.add(new MapLight(new BlockPos(cell.x(), 1 + plan.lampDy(cell.floor()), cell.z()), "flicker"));
            }
        }
        for (final int[] array : streetLamps(schematic)) {
            lights.add(new MapLight(new BlockPos(array[0], 7, array[1]), "flicker"));
        }
        return new Ambience(sounds, lights, InstitutoLayout.PHRASES);
    }
    
    private static SoundZone zone(final int x, final int y, final int z, final int radius, final String sound, final float volume, final float pitch, final double chance) {
        return new SoundZone(new BlockPos(x, y, z), radius, sound, volume, pitch, chance);
    }
    
    static {
        BOUNDS = new int[] { 1, 1, 300, 444 };
        EXIT = new int[] { 4, 434, 35, 443 };
        BEACON = new int[] { 97, 31, 201 };
        SPAWNS = new int[][] { { 146, 168 }, { 149, 170 }, { 152, 168 }, { 155, 170 }, { 146, 173 }, { 149, 175 }, { 152, 173 }, { 155, 175 } };
        BOARD = new int[] { 166, 167 };
        ROOMS = List.of(new Room[] { new Room("A", 0, "profes", 72, 138, 87, 166, "PROFESORES", 80, 166), new Room("A", 0, "aula", 87, 145, 98, 166, "AULA 0.01", 92, 166), new Room("A", 0, "aula", 98, 145, 109, 166, "AULA 0.02", 103, 166), new Room("A", 0, "aula", 109, 144, 120, 166, "AULA 0.03", 114, 166), new Room("A", 0, "banos", 120, 144, 132, 166, "ASEOS", 126, 166), new Room("A", 0, "despacho", 72, 170, 87, 191, "DIRECCI\u00d3N", 80, 170), new Room("A", 0, "aula", 87, 170, 97, 191, "AULA 0.04", 92, 170), new Room("A", 0, "aula", 101, 170, 112, 191, "AULA 0.05", 106, 170), new Room("A", 0, "comedor", 120, 170, 132, 191, "CAFETER\u00cdA", 120, 180), new Room("A", 0, "conserje", 76, 191, 85, 199, "CONSERJER\u00cdA", 85, 196), new Room("A", 0, "banos", 101, 191, 108, 199, null, 0, 0), new Room("A", 0, "aula", 88, 211, 97, 228, "AULA 0.06", 97, 219), new Room("A", 0, "aula", 88, 228, 97, 245, "AULA 0.07", 97, 236), new Room("A", 0, "aula", 101, 199, 112, 214, "AULA 0.08", 101, 207), new Room("A", 0, "aula", 101, 214, 112, 229, "AULA 0.09", 101, 221), new Room("A", 0, "almacen", 101, 229, 112, 245, "ALMAC\u00c9N", 101, 233), new Room("A", 1, "biblioteca", 72, 138, 97, 166, "BIBLIOTECA", 84, 166), new Room("A", 1, "lab", 72, 172, 84, 193, "LABORATORIO", 84, 180), new Room("A", 1, "aula", 88, 210, 97, 227, "AULA 1.01", 97, 218), new Room("A", 1, "aula", 88, 227, 97, 246, "AULA 1.02", 97, 235), new Room("A", 1, "aula", 101, 198, 113, 214, "AULA 1.03", 101, 206), new Room("A", 1, "aula", 101, 214, 113, 230, "AULA 1.04", 101, 222), new Room("A", 1, "despacho", 101, 230, 113, 236, "SEMINARIO", 101, 233), new Room("A", 1, "aula", 101, 236, 113, 246, "AULA 1.05", 101, 244), new Room("B", 0, "comedor", 209, 133, 229, 166, null, 0, 0), new Room("B", 0, "cocina", 209, 166, 229, 177, null, 0, 0), new Room("B", 0, "aula", 213, 177, 229, 191, "AULA B.01", 213, 184), new Room("B", 0, "aula", 213, 191, 229, 206, "AULA B.02", 213, 198), new Room("B", 0, "aula", 171, 158, 185, 170, "AULA B.03", 179, 158), new Room("B", 0, "banos", 173, 183, 181, 188, "ASEOS", 177, 183), new Room("B", 1, "musica", 209, 133, 229, 149, "M\u00daSICA", 219, 149), new Room("B", 1, "plastica", 216, 149, 229, 166, "PL\u00c1STICA", 216, 158), new Room("B", 1, "aula", 216, 166, 229, 183, "AULA B.11", 216, 175), new Room("B", 1, "aula", 216, 183, 229, 206, "AULA B.12", 216, 192), new Room("C", 0, "aula", 74, 21, 84, 37, "AULA C.01", 84, 29), new Room("C", 0, "aula", 74, 37, 84, 53, "AULA C.02", 84, 45), new Room("C", 0, "aula", 74, 53, 84, 69, "AULA C.03", 84, 61), new Room("C", 0, "despacho", 74, 69, 84, 81, "SECRETAR\u00cdA", 84, 75), new Room("C", 0, "aula", 88, 21, 99, 38, "AULA C.04", 88, 30), new Room("C", 0, "aula", 88, 38, 97, 53, "AULA C.05", 88, 45), new Room("C", 0, "aula", 88, 53, 97, 68, "AULA C.06", 88, 60), new Room("C", 0, "almacen", 88, 68, 91, 81, null, 0, 0), new Room("C", 0, "despacho", 91, 78, 104, 92, "DEPARTAMENTOS", 98, 92), new Room("C", 0, "salon", 104, 76, 133, 92, "SAL\u00d3N DE ACTOS", 118, 92), new Room("C", 0, "taller", 79, 96, 98, 117, "TALLER 1", 88, 96), new Room("C", 0, "taller", 98, 96, 116, 117, "TALLER 2", 107, 96), new Room("C", 0, "taller", 116, 96, 132, 117, "TALLER 3", 124, 96), new Room("C", 1, "info", 74, 21, 84, 45, "INFORM\u00c1TICA", 84, 33), new Room("C", 1, "aula", 74, 45, 84, 69, "AULA C.11", 84, 57), new Room("C", 1, "aula", 88, 21, 99, 45, "AULA C.12", 88, 33), new Room("C", 1, "aula", 88, 45, 97, 68, "AULA C.13", 88, 56), new Room("C", 1, "aula", 91, 78, 112, 92, "AULA C.14", 100, 92), new Room("C", 1, "aula", 112, 76, 133, 92, "AULA C.15", 122, 92), new Room("C", 1, "aula", 79, 96, 105, 117, "AULA C.16", 92, 96), new Room("C", 1, "aula", 105, 96, 133, 117, "AULA C.17", 118, 96), new Room("P", 0, "vestuario", 196, 252, 213, 265, "VESTUARIO 1", 204, 252), new Room("P", 0, "vestuario", 213, 252, 229, 265, "VESTUARIO 2", 221, 252) });
        BUILDINGS = Map.of("A", new int[] { 72, 138, 2 }, "B", new int[] { 171, 133, 2 }, "C", new int[] { 74, 21, 2 }, "P", new int[] { 196, 216, 1 });
        PLACES = List.of(new Place[] { new Place("a", "Edificio A", 100, 180, 0), new Place("b", "Edificio B", 219, 170, 0), new Place("c", "Edificio C", 100, 70, 0), new Place("poli", "Polideportivo", 212, 240, 0), new Place("cubierta", "Cubierta", 182, 102, 0), new Place("cubierta2", "Cubierta sur", 176, 228, 0), new Place("roja", "Pista roja", 142, 228, 0), new Place("invernadero", "Invernadero", 167, 25, 0), new Place("aparcamiento", "Aparcamiento", 60, 60, 0), new Place("ruinas", "Ruinas", 130, 305, 0), new Place("ruinas2", "Ruinas sur", 180, 395, 0), new Place("parada", "Parada de bus", 30, 146, 0) });
        PAGES = List.of(new PageWall[] { new PageWall("a", -19, 2, 19, "NORTH", "Conserjer\u00eda", "white_concrete"), new PageWall("a", -13, 2, -28, "WEST", "Sala de profesores", "white_concrete"), new PageWall("a", -22, 2, -10, "SOUTH", "Direcci\u00f3n", "white_concrete"), new PageWall("a", 7, 2, 34, "NORTH", "Aula 0.08", "white_concrete"), new PageWall("a", -14, 7, -14, "NORTH", "Biblioteca", "white_concrete"), new PageWall("a", -16, 7, 3, "WEST", "Laboratorio", "white_concrete"), new PageWall("b", -2, 2, -4, "NORTH", "Comedor", "white_concrete"), new PageWall("b", -10, 7, -22, "EAST", "Aula de m\u00fasica", "white_concrete"), new PageWall("c", 16, 2, 37, "WEST", "Taller", "white_concrete"), new PageWall("c", 20, 2, 22, "NORTH", "Sal\u00f3n de actos", "white_concrete"), new PageWall("c", -21, 7, -25, "NORTH", "Aula de inform\u00e1tica", "white_concrete"), new PageWall("poli", 1, 2, 19, "WEST", "Vestuarios", "white_concrete"), new PageWall("cubierta", 2, 2, -21, "SOUTH", "Pista cubierta", "light_gray_concrete"), new PageWall("cubierta2", -17, 2, -2, "EAST", "Pista cubierta sur", "light_gray_concrete"), new PageWall("invernadero", -34, 2, 0, "EAST", "Invernadero", "glass"), new PageWall("roja", 0, 2, -30, "SOUTH", "Canasta", "light_gray_concrete"), new PageWall("aparcamiento", -20, 2, -11, "SOUTH", "Coche abandonado", "red_concrete"), new PageWall("ruinas", -3, 2, -17, "SOUTH", "Ruinas", "cobblestone"), new PageWall("ruinas2", -16, 2, 5, "EAST", "Ruinas del sur", "cobblestone"), new PageWall("parada", 3, 2, 0, "WEST", "Parada de autob\u00fas", "light_gray_concrete") });
        NOTES = List.of(new Note[] { new Note("a", -20, 2, 18, "NORTH", List.of("Ronda de noche", "Xos\u00e9 Lamas,", "conserje.", "Todo cerrado."), "white_concrete"), new Note("a", -22, 2, -15, "NORTH", List.of("Alguien deja", "las luces", "encendidas.", "No soy yo."), "white_concrete"), new Note("a", -13, 7, -15, "NORTH", List.of("La laguna se", "sec\u00f3 en 1958.", "El agua no", "se fue."), "white_concrete"), new Note("a", -22, 7, -7, "SOUTH", List.of("Barro del", "patio: es", "fondo de", "laguna."), "white_concrete"), new Note("b", 2, 2, -5, "NORTH", List.of("Platos puestos", "para nadie.", "Otra vez.", ""), "white_concrete"), new Note("c", -10, 2, 27, "SOUTH", List.of("Las m\u00e1quinas", "se encienden", "solas a las", "tres."), "white_concrete"), new Note("c", 21, 2, 21, "NORTH", List.of("Ensayo cerrado", "y aun as\u00ed", "aplauden.", ""), "white_concrete"), new Note("poli", 7, 2, 13, "SOUTH", List.of("Duchas", "abiertas. Agua", "turbia, huele", "a laguna."), "white_concrete"), new Note("cubierta", 8, 2, -20, "SOUTH", List.of("Pasos en la", "pista. Nadie", "en la pista.", ""), "light_gray_concrete"), new Note("cubierta2", -16, 2, 4, "EAST", List.of("Balones en la", "pista. No hay", "nadie jugando.", ""), "light_gray_concrete"), new Note("roja", 0, 2, -31, "NORTH", List.of("Canasta", "doblada.", "Como si algo", "colgara."), "light_gray_concrete"), new Note("invernadero", -5, 2, 18, "NORTH", List.of("Aqu\u00ed crecen", "juncos. Nadie", "los plant\u00f3.", ""), "glass"), new Note("aparcamiento", -17, 2, -12, "EAST", List.of("El coche de", "Xos\u00e9 lleva", "aqu\u00ed desde", "el invierno."), "red_concrete"), new Note("ruinas", -43, 2, 17, "EAST", List.of("Dicen que bajo", "la Lagoa de", "Antela duerme", "una ciudad."), "cobblestone"), new Note("ruinas2", 60, 2, -68, "SOUTH", List.of("Sus campanas", "suenan bajo", "la tierra.", ""), "cobblestone"), new Note("parada", 2, 2, 2, "WEST", List.of("\u00daltimo bus", "22:40.", "Xos\u00e9 nunca", "lo cogi\u00f3."), "light_gray_concrete") });
        BATTERIES_OUT = new int[][] { { 150, 100 }, { 176, 240 }, { 140, 230 }, { 167, 30 }, { 60, 55 }, { 130, 300 }, { 180, 405 }, { 28, 300 }, { 250, 200 }, { 210, 290 }, { 110, 128 }, { 25, 60 } };
        BATTERIES_IN = new int[][] { { 0, 99, 180 }, { 0, 90, 205 }, { 1, 99, 220 }, { 1, 90, 180 }, { 0, 211, 190 }, { 1, 214, 170 }, { 0, 86, 50 }, { 1, 100, 94 }, { 0, 205, 240 } };
        POSTS = List.of(new Post(140, 186, "SOUTH", List.of("c", "roja", "ruinas", "salida")), new Post(42, 191, "SOUTH", List.of("a", "aparcamiento", "parada", "salida")), new Post(100, 124, "NORTH", List.of("c", "invernadero", "cubierta", "aparcamiento")), new Post(201, 210, "WEST", List.of("poli", "b", "cubierta2", "ruinas")));
        PHRASES = List.of(new String[] { "LA RONDA NO TERMINA", "XOS\u00c9 CIERRA LAS PUERTAS", "NO HAY CLASE ESTA NOCHE", "LA LAGUNA VUELVE", "BAJO EL PATIO HAY AGUA", "EL TIMBRE NO SUENA", "NO TIENE CARA", "LAS LUCES NO SE APAGAN", "LA CIUDAD HUNDIDA", "NO MIRES POR LA VENTANA", "ALGUIEN PASA LISTA", "ME HA VISTO" });
    }
    
    record Room(String building, int floor, String kind, int x1, int z1, int x2, int z2, String sign, int doorX, int doorZ) {
        public boolean contains(final int n, final int n2) {
            return n > this.x1 && n < this.x2 && n2 > this.z1 && n2 < this.z2;
        }
    }
}
