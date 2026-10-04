package dev.two.slender.map.builders.instituto;

import java.io.UncheckedIOException;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.io.InputStreamReader;

import java.io.BufferedReader;

import java.io.InputStream;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.SoundZone;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.gen.Note;
import dev.two.slender.map.gen.PageWall;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.gen.Post;
import dev.two.slender.map.schem.Schematic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class InstitutoLayout {
    public static final String SCHEMATIC = "maps/instituto.schem";
    public static final int SHIFT_X = -200;
    public static final int SHIFT_Y = -1;
    public static final int SHIFT_Z = -218;
    public static final int GROUND = 1;
    public static final int STOREY = 5;
    public static final int[] BOUNDS = new int[]{1, 1, 300, 444};
    public static final int FENCE_X1 = 1;
    public static final int FENCE_X2 = 300;
    public static final int FENCE_Z1 = 1;
    public static final int GATE_Z = 432;
    public static final int GATE_X1 = 4;
    public static final int GATE_X2 = 35;
    public static final int[] EXIT = new int[]{4, 434, 35, 443};
    public static final int[] BEACON = new int[]{97, 31, 201};
    public static final int[][] SPAWNS = new int[][]{{146, 168}, {149, 170}, {152, 168}, {155, 170}, {146, 173}, {149, 175}, {152, 173}, {155, 175}};
    public static final int[] BOARD = new int[]{166, 167};
    public static final List<Room> ROOMS = List.of(new Room("A", 0, "profes", 72, 138, 87, 166, "PROFESORES", 80, 166), new Room("A", 0, "aula", 87, 145, 98, 166, "AULA 0.01", 92, 166), new Room("A", 0, "aula", 98, 145, 109, 166, "AULA 0.02", 103, 166), new Room("A", 0, "aula", 109, 144, 120, 166, "AULA 0.03", 114, 166), new Room("A", 0, "banos", 120, 144, 132, 166, "ASEOS", 126, 166), new Room("A", 0, "despacho", 72, 170, 87, 191, "DIRECCIÓN", 80, 170), new Room("A", 0, "aula", 87, 170, 97, 191, "AULA 0.04", 92, 170), new Room("A", 0, "aula", 101, 170, 112, 191, "AULA 0.05", 106, 170), new Room("A", 0, "comedor", 120, 170, 132, 191, "CAFETERÍA", 120, 180), new Room("A", 0, "conserje", 76, 191, 85, 199, "CONSERJERÍA", 85, 196), new Room("A", 0, "banos", 101, 191, 108, 199, (String)null, 0, 0), new Room("A", 0, "aula", 88, 211, 97, 228, "AULA 0.06", 97, 219), new Room("A", 0, "aula", 88, 228, 97, 245, "AULA 0.07", 97, 236), new Room("A", 0, "aula", 101, 199, 112, 214, "AULA 0.08", 101, 207), new Room("A", 0, "aula", 101, 214, 112, 229, "AULA 0.09", 101, 221), new Room("A", 0, "almacen", 101, 229, 112, 245, "ALMACÉN", 101, 233), new Room("A", 1, "biblioteca", 72, 138, 97, 166, "BIBLIOTECA", 84, 166), new Room("A", 1, "lab", 72, 172, 84, 193, "LABORATORIO", 84, 180), new Room("A", 1, "aula", 88, 210, 97, 227, "AULA 1.01", 97, 218), new Room("A", 1, "aula", 88, 227, 97, 246, "AULA 1.02", 97, 235), new Room("A", 1, "aula", 101, 198, 113, 214, "AULA 1.03", 101, 206), new Room("A", 1, "aula", 101, 214, 113, 230, "AULA 1.04", 101, 222), new Room("A", 1, "despacho", 101, 230, 113, 236, "SEMINARIO", 101, 233), new Room("A", 1, "aula", 101, 236, 113, 246, "AULA 1.05", 101, 244), new Room("B", 0, "comedor", 209, 133, 229, 166, (String)null, 0, 0), new Room("B", 0, "cocina", 209, 166, 229, 177, (String)null, 0, 0), new Room("B", 0, "aula", 213, 177, 229, 191, "AULA B.01", 213, 184), new Room("B", 0, "aula", 213, 191, 229, 206, "AULA B.02", 213, 198), new Room("B", 0, "aula", 171, 158, 185, 170, "AULA B.03", 179, 158), new Room("B", 0, "banos", 173, 183, 181, 188, "ASEOS", 177, 183), new Room("B", 1, "musica", 209, 133, 229, 149, "MÚSICA", 219, 149), new Room("B", 1, "plastica", 216, 149, 229, 166, "PLÁSTICA", 216, 158), new Room("B", 1, "aula", 216, 166, 229, 183, "AULA B.11", 216, 175), new Room("B", 1, "aula", 216, 183, 229, 206, "AULA B.12", 216, 192), new Room("C", 0, "aula", 74, 21, 84, 37, "AULA C.01", 84, 29), new Room("C", 0, "aula", 74, 37, 84, 53, "AULA C.02", 84, 45), new Room("C", 0, "aula", 74, 53, 84, 69, "AULA C.03", 84, 61), new Room("C", 0, "despacho", 74, 69, 84, 81, "SECRETARÍA", 84, 75), new Room("C", 0, "aula", 88, 21, 99, 38, "AULA C.04", 88, 30), new Room("C", 0, "aula", 88, 38, 97, 53, "AULA C.05", 88, 45), new Room("C", 0, "aula", 88, 53, 97, 68, "AULA C.06", 88, 60), new Room("C", 0, "almacen", 88, 68, 91, 81, (String)null, 0, 0), new Room("C", 0, "despacho", 91, 78, 104, 92, "DEPARTAMENTOS", 98, 92), new Room("C", 0, "salon", 104, 76, 133, 92, "SALÓN DE ACTOS", 118, 92), new Room("C", 0, "taller", 79, 96, 98, 117, "TALLER 1", 88, 96), new Room("C", 0, "taller", 98, 96, 116, 117, "TALLER 2", 107, 96), new Room("C", 0, "taller", 116, 96, 132, 117, "TALLER 3", 124, 96), new Room("C", 1, "info", 74, 21, 84, 45, "INFORMÁTICA", 84, 33), new Room("C", 1, "aula", 74, 45, 84, 69, "AULA C.11", 84, 57), new Room("C", 1, "aula", 88, 21, 99, 45, "AULA C.12", 88, 33), new Room("C", 1, "aula", 88, 45, 97, 68, "AULA C.13", 88, 56), new Room("C", 1, "aula", 91, 78, 112, 92, "AULA C.14", 100, 92), new Room("C", 1, "aula", 112, 76, 133, 92, "AULA C.15", 122, 92), new Room("C", 1, "aula", 79, 96, 105, 117, "AULA C.16", 92, 96), new Room("C", 1, "aula", 105, 96, 133, 117, "AULA C.17", 118, 96), new Room("P", 0, "vestuario", 196, 252, 213, 265, "VESTUARIO 1", 204, 252), new Room("P", 0, "vestuario", 213, 252, 229, 265, "VESTUARIO 2", 221, 252));
    public static final Map<String, int[]> BUILDINGS = Map.of("A", new int[]{72, 138, 2}, "B", new int[]{171, 133, 2}, "C", new int[]{74, 21, 2}, "P", new int[]{196, 216, 1});
    public static final List<Place> PLACES = List.of(new Place("a", "Edificio A", 100, 180, 0), new Place("b", "Edificio B", 219, 170, 0), new Place("c", "Edificio C", 100, 70, 0), new Place("poli", "Polideportivo", 212, 240, 0), new Place("cubierta", "Cubierta", 182, 102, 0), new Place("cubierta2", "Cubierta sur", 176, 228, 0), new Place("roja", "Pista roja", 142, 228, 0), new Place("invernadero", "Invernadero", 167, 25, 0), new Place("aparcamiento", "Aparcamiento", 60, 60, 0), new Place("ruinas", "Ruinas", 130, 305, 0), new Place("ruinas2", "Ruinas sur", 180, 395, 0), new Place("parada", "Parada de bus", 30, 146, 0));
    public static final List<PageWall> PAGES = List.of(new PageWall("a", -19, 2, 19, "NORTH", "Conserjería", "white_concrete"), new PageWall("a", -13, 2, -28, "WEST", "Sala de profesores", "white_concrete"), new PageWall("a", -22, 2, -10, "SOUTH", "Dirección", "white_concrete"), new PageWall("a", 7, 2, 34, "NORTH", "Aula 0.08", "white_concrete"), new PageWall("a", -14, 7, -14, "NORTH", "Biblioteca", "white_concrete"), new PageWall("a", -16, 7, 3, "WEST", "Laboratorio", "white_concrete"), new PageWall("b", -2, 2, -4, "NORTH", "Comedor", "white_concrete"), new PageWall("b", -10, 7, -22, "EAST", "Aula de música", "white_concrete"), new PageWall("c", 16, 2, 37, "WEST", "Taller", "white_concrete"), new PageWall("c", 20, 2, 22, "NORTH", "Salón de actos", "white_concrete"), new PageWall("c", -21, 7, -25, "NORTH", "Aula de informática", "white_concrete"), new PageWall("poli", 1, 2, 19, "WEST", "Vestuarios", "white_concrete"), new PageWall("cubierta", 2, 2, -21, "SOUTH", "Pista cubierta", "light_gray_concrete"), new PageWall("cubierta2", -17, 2, -2, "EAST", "Pista cubierta sur", "light_gray_concrete"), new PageWall("invernadero", -34, 2, 0, "EAST", "Invernadero", "glass"), new PageWall("roja", 0, 2, -30, "SOUTH", "Canasta", "light_gray_concrete"), new PageWall("aparcamiento", -20, 2, -11, "SOUTH", "Coche abandonado", "red_concrete"), new PageWall("ruinas", -3, 2, -17, "SOUTH", "Ruinas", "cobblestone"), new PageWall("ruinas2", -16, 2, 5, "EAST", "Ruinas del sur", "cobblestone"), new PageWall("parada", 3, 2, 0, "WEST", "Parada de autobús", "light_gray_concrete"));
    public static final List<Note> NOTES = List.of(new Note("a", -20, 2, 18, "NORTH", List.of("Ronda de noche", "Xosé Lamas,", "conserje.", "Todo cerrado."), "white_concrete"), new Note("a", -22, 2, -15, "NORTH", List.of("Alguien deja", "las luces", "encendidas.", "No soy yo."), "white_concrete"), new Note("a", -13, 7, -15, "NORTH", List.of("La laguna se", "secó en 1958.", "El agua no", "se fue."), "white_concrete"), new Note("a", -22, 7, -7, "SOUTH", List.of("Barro del", "patio: es", "fondo de", "laguna."), "white_concrete"), new Note("b", 2, 2, -5, "NORTH", List.of("Platos puestos", "para nadie.", "Otra vez.", ""), "white_concrete"), new Note("c", -10, 2, 27, "SOUTH", List.of("Las máquinas", "se encienden", "solas a las", "tres."), "white_concrete"), new Note("c", 21, 2, 21, "NORTH", List.of("Ensayo cerrado", "y aun así", "aplauden.", ""), "white_concrete"), new Note("poli", 7, 2, 13, "SOUTH", List.of("Duchas", "abiertas. Agua", "turbia, huele", "a laguna."), "white_concrete"), new Note("cubierta", 8, 2, -20, "SOUTH", List.of("Pasos en la", "pista. Nadie", "en la pista.", ""), "light_gray_concrete"), new Note("cubierta2", -16, 2, 4, "EAST", List.of("Balones en la", "pista. No hay", "nadie jugando.", ""), "light_gray_concrete"), new Note("roja", 0, 2, -31, "NORTH", List.of("Canasta", "doblada.", "Como si algo", "colgara."), "light_gray_concrete"), new Note("invernadero", -5, 2, 18, "NORTH", List.of("Aquí crecen", "juncos. Nadie", "los plantó.", ""), "glass"), new Note("aparcamiento", -17, 2, -12, "EAST", List.of("El coche de", "Xosé lleva", "aquí desde", "el invierno."), "red_concrete"), new Note("ruinas", -43, 2, 17, "EAST", List.of("Dicen que bajo", "la Lagoa de", "Antela duerme", "una ciudad."), "cobblestone"), new Note("ruinas2", 60, 2, -68, "SOUTH", List.of("Sus campanas", "suenan bajo", "la tierra.", ""), "cobblestone"), new Note("parada", 2, 2, 2, "WEST", List.of("Último bus", "22:40.", "Xosé nunca", "lo cogió."), "light_gray_concrete"));
    public static final int[][] BATTERIES_OUT = new int[][]{{150, 100}, {176, 240}, {140, 230}, {167, 30}, {60, 55}, {130, 300}, {180, 405}, {28, 300}, {250, 200}, {210, 290}, {110, 128}, {25, 60}};
    public static final int[][] BATTERIES_IN = new int[][]{{0, 99, 180}, {0, 90, 205}, {1, 99, 220}, {1, 90, 180}, {0, 211, 190}, {1, 214, 170}, {0, 86, 50}, {1, 100, 94}, {0, 205, 240}};
    public static final List<Post> POSTS = List.of(new Post(140, 186, "SOUTH", List.of("c", "roja", "ruinas", "salida")), new Post(42, 191, "SOUTH", List.of("a", "aparcamiento", "parada", "salida")), new Post(100, 124, "NORTH", List.of("c", "invernadero", "cubierta", "aparcamiento")), new Post(201, 210, "WEST", List.of("poli", "b", "cubierta2", "ruinas")));
    public static final List<String> PHRASES = List.of("LA RONDA NO TERMINA", "XOSÉ CIERRA LAS PUERTAS", "NO HAY CLASE ESTA NOCHE", "LA LAGUNA VUELVE", "BAJO EL PATIO HAY AGUA", "EL TIMBRE NO SUENA", "NO TIENE CARA", "LAS LUCES NO SE APAGAN", "LA CIUDAD HUNDIDA", "NO MIRES POR LA VENTANA", "ALGUIEN PASA LISTA", "ME HA VISTO");

    private InstitutoLayout() {
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

    public static FloorPlan plan(String var0) {
        int[] var1 = (int[])BUILDINGS.get(var0);
        if (var1 == null) {
            throw new IllegalArgumentException("Edificio desconocido: " + var0);
        } else {
            ArrayList var2 = new ArrayList();

            for(int var3 = 0; var3 < var1[2]; ++var3) {
                var2.add(rows("maps/instituto/" + var0 + var3 + ".txt"));
            }

            return new FloorPlan(var1[0], var1[1], 0, 5, var2);
        }
    }

    static List<String> rows(String name) {
        try (InputStream in = InstitutoLayout.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("No está en el jar: " + name);
            }
            return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Heightmap ground() {
        Heightmap var0 = new Heightmap(460, -10);

        for(int var1 = 0; var1 <= 460; ++var1) {
            for(int var2 = 0; var2 <= 460; ++var2) {
                var0.set(var1, var2, 1);
            }
        }

        return var0;
    }

    public static List<Note> signs() {
        ArrayList var0 = new ArrayList();
        Map var1 = Map.of("A", "a", "B", "b", "C", "c", "P", "poli");
        Iterator var2 = ROOMS.iterator();

        while(var2.hasNext()) {
            Room var3 = (Room)var2.next();
            if (var3.sign() != null) {
                int var4 = var3.doorX();
                int var5 = var3.doorZ();
                String var6;
                if (var5 == var3.z1()) {
                    --var5;
                    var6 = "NORTH";
                } else if (var5 == var3.z2()) {
                    ++var5;
                    var6 = "SOUTH";
                } else if (var4 == var3.x1()) {
                    --var4;
                    var6 = "WEST";
                } else {
                    ++var4;
                    var6 = "EAST";
                }

                Place var7 = place((String)var1.get(var3.building()));
                var0.add(new Note(var7.id(), var4 - var7.x(), 3 + var3.floor() * 5, var5 - var7.z(), var6, List.of("", var3.sign(), "", ""), "white_concrete"));
            }
        }

        return var0;
    }

    public static List<int[]> streetLamps(Schematic var0) {
        ArrayList var1 = new ArrayList();

        int var2;
        for(var2 = 12; var2 <= 420; var2 += 24) {
            var1.add(new int[]{21, var2});
        }

        int[] var10 = new int[]{56, 96};
        int var3 = var10.length;

        int var7;
        int var8;
        for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var10[var4];
            int[] var6 = new int[]{24, 64, 104};
            var7 = var6.length;

            for(var8 = 0; var8 < var7; ++var8) {
                int var9 = var6[var8];
                var1.add(new int[]{var5, var9});
            }
        }

        for(var2 = 150; var2 <= 260; var2 += 36) {
            var1.add(new int[]{54, var2});
        }

        ArrayList var12 = new ArrayList();
        Iterator var11 = var1.iterator();

        while(var11.hasNext()) {
            int[] var13 = (int[])var11.next();
            boolean var14 = true;

            for(int var15 = -1; var15 <= 1; ++var15) {
                for(var7 = -1; var7 <= 1; ++var7) {
                    for(var8 = 2; var8 <= 9; ++var8) {
                        if (!var0.isAir(var13[0] + var15, var8, var13[1] + var7)) {
                            var14 = false;
                        }
                    }
                }
            }

            if (var14 && Math.abs(var13[1] - 146) > 5) {
                var12.add(var13);
            }
        }

        return var12;
    }

    public static Ambience ambience(Schematic var0) {
        ArrayList var1 = new ArrayList();
        var1.add(zone(100, 2, 190, 60, "BLOCK_BELL_USE", 0.4F, 0.5F, 0.01));
        var1.add(zone(100, 2, 190, 30, "BLOCK_WOODEN_DOOR_CLOSE", 0.4F, 0.7F, 0.03));
        var1.add(zone(205, 2, 170, 25, "BLOCK_WOODEN_DOOR_CLOSE", 0.4F, 0.7F, 0.03));
        var1.add(zone(100, 2, 70, 30, "BLOCK_WOODEN_DOOR_CLOSE", 0.4F, 0.7F, 0.03));
        var1.add(zone(212, 2, 240, 18, "ENTITY_SLIME_JUMP", 0.5F, 0.5F, 0.06));
        var1.add(zone(170, 2, 330, 55, "ENTITY_FROG_AMBIENT", 0.5F, 0.6F, 0.08));
        var1.add(zone(170, 2, 330, 45, "BLOCK_WATER_AMBIENT", 0.4F, 0.6F, 0.08));
        var1.add(zone(150, 2, 220, 260, "ITEM_ELYTRA_FLYING", 0.15F, 0.5F, 0.04));
        var1.add(zone(86, 7, 152, 12, "ENTITY_VEX_AMBIENT", 0.3F, 0.5F, 0.03));
        var1.add(zone(99, 2, 200, 25, "BLOCK_BEACON_AMBIENT", 0.2F, 0.7F, 0.05));
        ArrayList var2 = new ArrayList();
        var2.add(new MapLight(new BlockPos(BEACON[0], BEACON[1], BEACON[2]), "blink"));
        Iterator var3 = List.of("A", "B", "C", "P").iterator();

        while(var3.hasNext()) {
            String var4 = (String)var3.next();
            FloorPlan var5 = plan(var4);
            Iterator var6 = var5.cells('L').iterator();

            while(var6.hasNext()) {
                FloorPlan.Cell var7 = (FloorPlan.Cell)var6.next();
                var2.add(new MapLight(new BlockPos(var7.x(), 1 + var5.lampDy(var7.floor()), var7.z()), "flicker"));
            }
        }

        var3 = streetLamps(var0).iterator();

        while(var3.hasNext()) {
            int[] var8 = (int[])var3.next();
            var2.add(new MapLight(new BlockPos(var8[0], 7, var8[1]), "flicker"));
        }

        return new Ambience(var1, var2, PHRASES);
    }

    private static SoundZone zone(int var0, int var1, int var2, int var3, String var4, float var5, float var6, double var7) {
        return new SoundZone(new BlockPos(var0, var1, var2), var3, var4, var5, var6, var7);
    }

    public static record Room(String building, int floor, String kind, int x1, int z1, int x2, int z2, String sign, int doorX, int doorZ) {
        public Room(String building, int floor, String kind, int x1, int z1, int x2, int z2, String sign, int doorX, int doorZ) {
            this.building = building;
            this.floor = floor;
            this.kind = kind;
            this.x1 = x1;
            this.z1 = z1;
            this.x2 = x2;
            this.z2 = z2;
            this.sign = sign;
            this.doorX = doorX;
            this.doorZ = doorZ;
        }

        public boolean contains(int var1, int var2) {
            return var1 > this.x1 && var1 < this.x2 && var2 > this.z1 && var2 < this.z2;
        }

        public String building() {
            return this.building;
        }

        public int floor() {
            return this.floor;
        }

        public String kind() {
            return this.kind;
        }

        public int x1() {
            return this.x1;
        }

        public int z1() {
            return this.z1;
        }

        public int x2() {
            return this.x2;
        }

        public int z2() {
            return this.z2;
        }

        public String sign() {
            return this.sign;
        }

        public int doorX() {
            return this.doorX;
        }

        public int doorZ() {
            return this.doorZ;
        }
    }
}
