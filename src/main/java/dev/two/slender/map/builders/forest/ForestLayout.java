package dev.two.slender.map.builders.forest;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.SoundZone;
import dev.two.slender.map.gen.Note;
import dev.two.slender.map.gen.PageWall;
import dev.two.slender.map.gen.Place;
import dev.two.slender.map.gen.Post;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ForestLayout {
    public static final int R = 80;
    public static final int TERRAIN = 92;
    public static final int WATER = -1;
    public static final int TOWER_TOP = 31;
    public static final List<Place> PLACES = List.of(new Place("torre", "Torre", 0, -8, 8, 7), new Place("cabana", "Cabaña", -40, -25, 8), new Place("campamento", "Campamento", -28, 22, 7), new Place("capilla", "Capilla", -20, -68, 8), new Place("piedras", "Piedras", -16, -36, 6), new Place("caravana", "Caravana", 45, -68, 6), new Place("caza", "Puesto caza", 70, -42, 4), new Place("refugio", "Refugio", 47, -12, 6), new Place("coche", "Accidente", 54, 24, 4), new Place("pozo", "Pozo", 11, 26, 4), new Place("tunel", "Túnel", -72, 0, 6), new Place("embarcadero", "Embarcadero", 37, 63, 4, 0));
    public static final int[][] LOOP = new int[][]{{0, 52}, {-30, 45}, {-55, 20}, {-58, -20}, {-35, -50}, {0, -60}, {35, -52}, {58, -25}, {60, 10}, {40, 35}, {0, 52}};
    public static final int[][][] SPURS = new int[][][]{{{0, 80}, {0, 52}}, {{0, 52}, {-3, 30}, {0, 0}}, {{0, -16}, {-2, -40}, {0, -60}}, {{-2, -36}, {-10, -36}}, {{-58, -20}, {-48, -24}}, {{-41, 33}, {-32, 25}}, {{-35, -50}, {-26, -62}}, {{35, -52}, {42, -62}}, {{58, -28}, {66, -38}}, {{59, -10}, {53, -12}}, {{-2, 26}, {7, 26}}, {{-57, 0}, {-66, 0}}, {{20, 44}, {30, 58}, {34, 62}}};
    public static final int[][] STREAM = new int[][]{{25, -92}, {20, -72}, {28, -50}, {22, -28}, {30, -5}, {24, 15}, {32, 32}, {42, 46}, {50, 54}};
    public static final int[] LAKE = new int[]{55, 58, 13, 9};
    public static final int[][] SPAWNS = new int[][]{{-6, 70}, {-3, 72}, {0, 70}, {3, 72}, {6, 70}, {-4, 67}, {0, 66}, {4, 67}};
    public static final int[][] BATTERIES = new int[][]{{-4, 58}, {6, -14}, {-35, -20}, {-22, 26}, {-14, -60}, {-10, -32}, {40, -63}, {67, -38}, {42, -10}, {50, 20}, {14, 23}, {-66, 3}, {33, 60}, {30, -34}, {-52, 28}, {3, -42}};
    public static final int[] CABIN_LAMP = new int[]{2, 4, 0};
    public static final int[] CAR_LAMP = new int[]{2, 1, -1};
    public static final List<Post> POSTS = List.of(new Post(4, 55, "SOUTH", List.of("torre", "campamento", "embarcadero", "coche")), new Post(-4, -20, "SOUTH", List.of("capilla", "piedras", "caravana", "cabana")), new Post(-60, -3, "EAST", List.of("tunel", "cabana", "campamento", "salida")), new Post(5, -55, "SOUTH", List.of("capilla", "caravana", "caza", "torre")), new Post(54, -17, "EAST", List.of("refugio", "caza", "coche", "caravana")), new Post(-31, -47, "SOUTH", List.of("capilla", "cabana", "piedras", "tunel")));
    public static final List<PageWall> PAGES = List.of(new PageWall("torre", 6, 2, 0, "EAST", "Caseta de la radio", "andesite"), new PageWall("torre", 4, 2, -2, "SOUTH", "Mesa de la radio", "andesite"), new PageWall("cabana", 4, 2, 1, "EAST", "Pared de la cabaña", "spruce_planks"), new PageWall("cabana", -2, 2, -3, "SOUTH", "Dormitorio", "spruce_planks"), new PageWall("campamento", -5, 2, 3, "EAST", "Barriles del campamento", "barrel[facing=up]"), new PageWall("capilla", 0, 2, -6, "SOUTH", "Altar de la capilla", "stone_bricks"), new PageWall("capilla", 3, 2, -2, "EAST", "Muro de la capilla", "stone_bricks"), new PageWall("piedras", 0, 2, -5, "SOUTH", "Círculo de piedras", "mossy_cobblestone"), new PageWall("caravana", -4, 3, 0, "WEST", "Caravana", "white_terracotta"), new PageWall("caza", 0, 2, 1, "NORTH", "Bajo el puesto de caza", "spruce_planks"), new PageWall("pozo", 3, 2, 0, "WEST", "Junto al pozo", "spruce_planks"), new PageWall("embarcadero", -2, 2, 1, "SOUTH", "Caseta del embarcadero", "spruce_planks"), new PageWall("refugio", 3, 2, 0, "WEST", "Dentro del refugio", "cobblestone"), new PageWall("refugio", 0, 2, -2, "NORTH", "Muro del refugio", "cobblestone"), new PageWall("coche", 3, 2, 0, "SOUTH", "Árbol del accidente", "oak_log"), new PageWall("tunel", -1, 3, 3, "EAST", "Boca del túnel", "stone_bricks"));
    public static final List<Note> NOTES = List.of(new Note("torre", 1, 3, 1, "WEST", List.of("Grabación 14:", "estática y una", "voz que dice", "mi nombre."), "andesite"), new Note("cabana", 3, 2, -2, "SOUTH", List.of("Turno invierno", "Í. Varela", "No salir de", "noche."), "spruce_planks"), new Note("campamento", -5, 1, 4, "SOUTH", List.of("Día 2: algo", "rodea el", "campamento", "toda la noche."), "barrel[facing=up]"), new Note("capilla", -2, 2, -1, "EAST", List.of("Aquí rezaban", "por los que se", "perdían en el", "bosque."), "stone_bricks"), new Note("piedras", 0, 2, 4, "NORTH", List.of("Si lo miras,", "te mira.", "", "Í.V."), "mossy_cobblestone"), new Note("caravana", 5, 3, 0, "EAST", List.of("Se fueron", "con prisa.", "Dejaron la", "cena servida."), "white_terracotta"), new Note("caza", 0, 3, 0, "NORTH", List.of("Lo vi desde", "aquí arriba.", "No se movía.", "Yo tampoco."), "spruce_planks"), new Note("pozo", 3, 1, 1, "SOUTH", List.of("Se oyen voces", "dentro del", "pozo.", ""), "spruce_planks"), new Note("embarcadero", 1, 2, 0, "EAST", List.of("La barca", "nunca volvió", "a la orilla.", ""), "spruce_planks"), new Note("refugio", -1, 2, 1, "NORTH", List.of("Aquí dormí", "dos noches.", "Seguía ahí", "fuera."), "cobblestone"), new Note("coche", 3, 2, -1, "NORTH", List.of("El coche de", "mi padre.", "Nadie salió", "de dentro."), "oak_log"), new Note("tunel", 0, 2, -3, "EAST", List.of("El tren de", "las 02:13", "no para aquí.", "Ya no."), "stone_bricks"));
    public static final List<String> PHRASES = List.of("SOLO HAY ESTÁTICA", "ME LLAMA POR MI NOMBRE", "NO TIENE CARA", "LOS ÁRBOLES SE MUEVEN", "LO VI EN EL PUENTE", "NO VUELVAS A LA CABAÑA", "EL TREN NO PARA AQUÍ", "SI LO MIRAS, TE MIRA", "LA TORRE SIGUE EMITIENDO", "NO APAGUES LA LINTERNA", "ESTÁ DETRÁS DE TI", "DÍA 40: SIGO AQUÍ");

    private ForestLayout() {
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
        Heightmap var2 = new Heightmap(92, -1);
        var2.hills(new Noise(var0), 52.0, 3, 3.0, 9.0, -2, 12);
        var2.channel(STREAM, 1, 2);
        var2.lake(LAKE[0], LAKE[1], LAKE[2], LAKE[3], 3);
        Iterator var3 = PLACES.iterator();

        while(var3.hasNext()) {
            Place var4 = (Place)var3.next();
            var2.flatten(var4.x(), var4.z(), var4.pad(), var4.y() == Integer.MIN_VALUE ? var2.at(var4.x(), var4.z()) : var4.y(), 4);
        }

        var2.flattenRect(-9, 60, 9, 92, 0, 6);
        var2.road(LOOP, 2);
        int[][][] var7 = SPURS;
        int var8 = var7.length;

        for(int var5 = 0; var5 < var8; ++var5) {
            int[][] var6 = var7[var5];
            var2.road(var6, 1);
        }

        var2.clampSlope();
        return var2;
    }

    public static Biome biome(int var0, int var1, Noise var2) {
        double var3 = var2.value((double)var0, (double)var1, 18.0) * 10.0;
        if (Math.hypot((double)(var0 - LAKE[0]), (double)(var1 - LAKE[1])) < 24.0 + var3) {
            return ForestLayout.Biome.SWAMP;
        } else if ((double)var1 < -32.0 + var3) {
            return ForestLayout.Biome.PINE;
        } else if ((double)var0 < -36.0 + var3) {
            return ForestLayout.Biome.BURNT;
        } else {
            return (double)var0 > 34.0 + var3 && var1 < 30 ? ForestLayout.Biome.DEAD : ForestLayout.Biome.MIXED;
        }
    }

    public static Ambience ambience(Heightmap var0) {
        ArrayList var1 = new ArrayList();
        int[][] var2 = STREAM;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int[] var5 = var2[var4];
            if (Math.abs(var5[1]) <= 80) {
                var1.add(zone(var0, var5[0], var5[1], 7, "BLOCK_WATER_AMBIENT", 0.7F, 1.0F, 0.6));
            }
        }

        var1.add(zone(var0, LAKE[0], LAKE[1], 18, "BLOCK_WATER_AMBIENT", 0.6F, 0.9F, 0.4));
        var1.add(zone(var0, LAKE[0], LAKE[1], 26, "ENTITY_FROG_AMBIENT", 0.5F, 0.8F, 0.15));
        var1.add(zoneAt(var0, "torre", 14, "BLOCK_BEACON_AMBIENT", 0.8F, 0.6F, 0.5));
        var1.add(zoneAt(var0, "torre", 14, "BLOCK_SCULK_SENSOR_CLICKING", 0.6F, 0.5F, 0.15));
        var1.add(zoneAt(var0, "cabana", 10, "BLOCK_WOOD_STEP", 0.6F, 0.6F, 0.2));
        var1.add(zoneAt(var0, "cabana", 10, "BLOCK_WOODEN_DOOR_OPEN", 0.5F, 0.7F, 0.04));
        var1.add(zoneAt(var0, "capilla", 14, "BLOCK_BELL_RESONATE", 0.5F, 0.5F, 0.04));
        var1.add(zoneAt(var0, "campamento", 9, "BLOCK_CAMPFIRE_CRACKLE", 0.7F, 0.8F, 0.35));
        var1.add(zoneAt(var0, "tunel", 12, "ENTITY_MINECART_RIDING", 0.5F, 0.5F, 0.05));
        var1.add(zoneAt(var0, "caravana", 9, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.4F, 0.6F, 0.04));
        var1.add(zone(var0, 0, -62, 34, "ENTITY_WOLF_GROWL", 0.35F, 0.6F, 0.03));
        var1.add(zone(var0, 0, -62, 34, "ENTITY_LIGHTNING_BOLT_THUNDER", 0.2F, 0.6F, 0.01));
        var1.add(zone(var0, -62, -5, 30, "BLOCK_FIRE_AMBIENT", 0.4F, 0.6F, 0.1));
        var1.add(zone(var0, 60, -30, 30, "ENTITY_FOX_SCREECH", 0.4F, 0.6F, 0.03));
        var1.add(zone(var0, 60, -30, 30, "BLOCK_PALE_HANGING_MOSS_IDLE", 0.5F, 1.0F, 0.1));
        Place var6 = place("torre");
        Place var7 = place("cabana");
        Place var8 = place("coche");
        List var9 = List.of(new MapLight(new BlockPos(var6.x(), var0.at(var6.x(), var6.z()) + 31, var6.z()), "blink"), new MapLight(new BlockPos(var7.x() + CABIN_LAMP[0], var0.at(var7.x(), var7.z()) + CABIN_LAMP[1], var7.z() + CABIN_LAMP[2]), "flicker"), new MapLight(new BlockPos(var8.x() + CAR_LAMP[0], var0.at(var8.x(), var8.z()) + CAR_LAMP[1], var8.z() + CAR_LAMP[2]), "flicker"));
        return new Ambience(var1, var9, PHRASES);
    }

    private static SoundZone zone(Heightmap var0, int var1, int var2, int var3, String var4, float var5, float var6, double var7) {
        return new SoundZone(new BlockPos(var1, var0.at(var1, var2) + 1, var2), var3, var4, var5, var6, var7);
    }

    private static SoundZone zoneAt(Heightmap var0, String var1, int var2, String var3, float var4, float var5, double var6) {
        Place var8 = place(var1);
        return zone(var0, var8.x(), var8.z(), var2, var3, var4, var5, var6);
    }

    public static enum Biome {
        PINE,
        BURNT,
        DEAD,
        SWAMP,
        MIXED;
    }
}
