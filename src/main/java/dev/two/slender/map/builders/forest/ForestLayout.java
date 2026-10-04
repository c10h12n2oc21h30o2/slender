// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.forest;

import dev.two.slender.map.SoundZone;
import dev.two.slender.map.MapLight;
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
import java.util.List;

public final class ForestLayout
{
    public static final int R = 80;
    public static final int TERRAIN = 92;
    public static final int WATER = -1;
    public static final int TOWER_TOP = 31;
    public static final List<Place> PLACES;
    public static final int[][] LOOP;
    public static final int[][][] SPURS;
    public static final int[][] STREAM;
    public static final int[] LAKE;
    public static final int[][] SPAWNS;
    public static final int[][] BATTERIES;
    public static final int[] CABIN_LAMP;
    public static final int[] CAR_LAMP;
    public static final List<Post> POSTS;
    public static final List<PageWall> PAGES;
    public static final List<Note> NOTES;
    public static final List<String> PHRASES;
    
    private ForestLayout() {
    }
    
    public static Place place(final String anObject) {
        for (final Place place : ForestLayout.PLACES) {
            if (place.id().equals(anObject)) {
                return place;
            }
        }
        throw new IllegalArgumentException("Lugar desconocido: " + anObject);
    }
    
    public static Heightmap terrain(final long n) {
        final Heightmap heightmap = new Heightmap(92, -1);
        heightmap.hills(new Noise(n), 52.0, 3, 3.0, 9.0, -2, 12);
        heightmap.channel(ForestLayout.STREAM, 1, 2);
        heightmap.lake(ForestLayout.LAKE[0], ForestLayout.LAKE[1], ForestLayout.LAKE[2], ForestLayout.LAKE[3], 3);
        for (final Place place : ForestLayout.PLACES) {
            heightmap.flatten(place.x(), place.z(), place.pad(), (place.y() == Integer.MIN_VALUE) ? heightmap.at(place.x(), place.z()) : place.y(), 4);
        }
        heightmap.flattenRect(-9, 60, 9, 92, 0, 6);
        heightmap.road(ForestLayout.LOOP, 2);
        final int[][][] spurs = ForestLayout.SPURS;
        for (int length = spurs.length, i = 0; i < length; ++i) {
            heightmap.road(spurs[i], 1);
        }
        heightmap.clampSlope();
        return heightmap;
    }
    
    public static Biome biome(final int n, final int n2, final Noise noise) {
        final double n3 = noise.value(n, n2, 18.0) * 10.0;
        if (Math.hypot(n - ForestLayout.LAKE[0], n2 - ForestLayout.LAKE[1]) < 24.0 + n3) {
            return Biome.SWAMP;
        }
        if (n2 < -32.0 + n3) {
            return Biome.PINE;
        }
        if (n < -36.0 + n3) {
            return Biome.BURNT;
        }
        if (n > 34.0 + n3 && n2 < 30) {
            return Biome.DEAD;
        }
        return Biome.MIXED;
    }
    
    public static Ambience ambience(final Heightmap heightmap) {
        final ArrayList sounds = new ArrayList();
        for (final int[] array : ForestLayout.STREAM) {
            if (Math.abs(array[1]) <= 80) {
                sounds.add(zone(heightmap, array[0], array[1], 7, "BLOCK_WATER_AMBIENT", 0.7f, 1.0f, 0.6));
            }
        }
        sounds.add(zone(heightmap, ForestLayout.LAKE[0], ForestLayout.LAKE[1], 18, "BLOCK_WATER_AMBIENT", 0.6f, 0.9f, 0.4));
        sounds.add(zone(heightmap, ForestLayout.LAKE[0], ForestLayout.LAKE[1], 26, "ENTITY_FROG_AMBIENT", 0.5f, 0.8f, 0.15));
        sounds.add(zoneAt(heightmap, "torre", 14, "BLOCK_BEACON_AMBIENT", 0.8f, 0.6f, 0.5));
        sounds.add(zoneAt(heightmap, "torre", 14, "BLOCK_SCULK_SENSOR_CLICKING", 0.6f, 0.5f, 0.15));
        sounds.add(zoneAt(heightmap, "cabana", 10, "BLOCK_WOOD_STEP", 0.6f, 0.6f, 0.2));
        sounds.add(zoneAt(heightmap, "cabana", 10, "BLOCK_WOODEN_DOOR_OPEN", 0.5f, 0.7f, 0.04));
        sounds.add(zoneAt(heightmap, "capilla", 14, "BLOCK_BELL_RESONATE", 0.5f, 0.5f, 0.04));
        sounds.add(zoneAt(heightmap, "campamento", 9, "BLOCK_CAMPFIRE_CRACKLE", 0.7f, 0.8f, 0.35));
        sounds.add(zoneAt(heightmap, "tunel", 12, "ENTITY_MINECART_RIDING", 0.5f, 0.5f, 0.05));
        sounds.add(zoneAt(heightmap, "caravana", 9, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.4f, 0.6f, 0.04));
        sounds.add(zone(heightmap, 0, -62, 34, "ENTITY_WOLF_GROWL", 0.35f, 0.6f, 0.03));
        sounds.add(zone(heightmap, 0, -62, 34, "ENTITY_LIGHTNING_BOLT_THUNDER", 0.2f, 0.6f, 0.01));
        sounds.add(zone(heightmap, -62, -5, 30, "BLOCK_FIRE_AMBIENT", 0.4f, 0.6f, 0.1));
        sounds.add(zone(heightmap, 60, -30, 30, "ENTITY_FOX_SCREECH", 0.4f, 0.6f, 0.03));
        sounds.add(zone(heightmap, 60, -30, 30, "BLOCK_PALE_HANGING_MOSS_IDLE", 0.5f, 1.0f, 0.1));
        final Place place = place("torre");
        final Place place2 = place("cabana");
        final Place place3 = place("coche");
        return new Ambience(sounds, List.of(new MapLight(new BlockPos(place.x(), heightmap.at(place.x(), place.z()) + 31, place.z()), "blink"), new MapLight(new BlockPos(place2.x() + ForestLayout.CABIN_LAMP[0], heightmap.at(place2.x(), place2.z()) + ForestLayout.CABIN_LAMP[1], place2.z() + ForestLayout.CABIN_LAMP[2]), "flicker"), new MapLight(new BlockPos(place3.x() + ForestLayout.CAR_LAMP[0], heightmap.at(place3.x(), place3.z()) + ForestLayout.CAR_LAMP[1], place3.z() + ForestLayout.CAR_LAMP[2]), "flicker")), ForestLayout.PHRASES);
    }
    
    private static SoundZone zone(final Heightmap heightmap, final int x, final int z, final int radius, final String sound, final float volume, final float pitch, final double chance) {
        return new SoundZone(new BlockPos(x, heightmap.at(x, z) + 1, z), radius, sound, volume, pitch, chance);
    }
    
    private static SoundZone zoneAt(final Heightmap heightmap, final String s, final int n, final String s2, final float n2, final float n3, final double n4) {
        final Place place = place(s);
        return zone(heightmap, place.x(), place.z(), n, s2, n2, n3, n4);
    }
    
    static {
        PLACES = List.of(new Place[] { new Place("torre", "Torre", 0, -8, 8, 7), new Place("cabana", "Caba\u00f1a", -40, -25, 8), new Place("campamento", "Campamento", -28, 22, 7), new Place("capilla", "Capilla", -20, -68, 8), new Place("piedras", "Piedras", -16, -36, 6), new Place("caravana", "Caravana", 45, -68, 6), new Place("caza", "Puesto caza", 70, -42, 4), new Place("refugio", "Refugio", 47, -12, 6), new Place("coche", "Accidente", 54, 24, 4), new Place("pozo", "Pozo", 11, 26, 4), new Place("tunel", "T\u00fanel", -72, 0, 6), new Place("embarcadero", "Embarcadero", 37, 63, 4, 0) });
        LOOP = new int[][] { { 0, 52 }, { -30, 45 }, { -55, 20 }, { -58, -20 }, { -35, -50 }, { 0, -60 }, { 35, -52 }, { 58, -25 }, { 60, 10 }, { 40, 35 }, { 0, 52 } };
        SPURS = new int[][][] { { { 0, 80 }, { 0, 52 } }, { { 0, 52 }, { -3, 30 }, { 0, 0 } }, { { 0, -16 }, { -2, -40 }, { 0, -60 } }, { { -2, -36 }, { -10, -36 } }, { { -58, -20 }, { -48, -24 } }, { { -41, 33 }, { -32, 25 } }, { { -35, -50 }, { -26, -62 } }, { { 35, -52 }, { 42, -62 } }, { { 58, -28 }, { 66, -38 } }, { { 59, -10 }, { 53, -12 } }, { { -2, 26 }, { 7, 26 } }, { { -57, 0 }, { -66, 0 } }, { { 20, 44 }, { 30, 58 }, { 34, 62 } } };
        STREAM = new int[][] { { 25, -92 }, { 20, -72 }, { 28, -50 }, { 22, -28 }, { 30, -5 }, { 24, 15 }, { 32, 32 }, { 42, 46 }, { 50, 54 } };
        LAKE = new int[] { 55, 58, 13, 9 };
        SPAWNS = new int[][] { { -6, 70 }, { -3, 72 }, { 0, 70 }, { 3, 72 }, { 6, 70 }, { -4, 67 }, { 0, 66 }, { 4, 67 } };
        BATTERIES = new int[][] { { -4, 58 }, { 6, -14 }, { -35, -20 }, { -22, 26 }, { -14, -60 }, { -10, -32 }, { 40, -63 }, { 67, -38 }, { 42, -10 }, { 50, 20 }, { 14, 23 }, { -66, 3 }, { 33, 60 }, { 30, -34 }, { -52, 28 }, { 3, -42 } };
        CABIN_LAMP = new int[] { 2, 4, 0 };
        CAR_LAMP = new int[] { 2, 1, -1 };
        POSTS = List.of(new Post(4, 55, "SOUTH", List.of("torre", "campamento", "embarcadero", "coche")), new Post(-4, -20, "SOUTH", List.of("capilla", "piedras", "caravana", "cabana")), new Post(-60, -3, "EAST", List.of("tunel", "cabana", "campamento", "salida")), new Post(5, -55, "SOUTH", List.of("capilla", "caravana", "caza", "torre")), new Post(54, -17, "EAST", List.of("refugio", "caza", "coche", "caravana")), new Post(-31, -47, "SOUTH", List.of("capilla", "cabana", "piedras", "tunel")));
        PAGES = List.of(new PageWall[] { new PageWall("torre", 6, 2, 0, "EAST", "Caseta de la radio", "andesite"), new PageWall("torre", 4, 2, -2, "SOUTH", "Mesa de la radio", "andesite"), new PageWall("cabana", 4, 2, 1, "EAST", "Pared de la caba\u00f1a", "spruce_planks"), new PageWall("cabana", -2, 2, -3, "SOUTH", "Dormitorio", "spruce_planks"), new PageWall("campamento", -5, 2, 3, "EAST", "Barriles del campamento", "barrel[facing=up]"), new PageWall("capilla", 0, 2, -6, "SOUTH", "Altar de la capilla", "stone_bricks"), new PageWall("capilla", 3, 2, -2, "EAST", "Muro de la capilla", "stone_bricks"), new PageWall("piedras", 0, 2, -5, "SOUTH", "C\u00edrculo de piedras", "mossy_cobblestone"), new PageWall("caravana", -4, 3, 0, "WEST", "Caravana", "white_terracotta"), new PageWall("caza", 0, 2, 1, "NORTH", "Bajo el puesto de caza", "spruce_planks"), new PageWall("pozo", 3, 2, 0, "WEST", "Junto al pozo", "spruce_planks"), new PageWall("embarcadero", -2, 2, 1, "SOUTH", "Caseta del embarcadero", "spruce_planks"), new PageWall("refugio", 3, 2, 0, "WEST", "Dentro del refugio", "cobblestone"), new PageWall("refugio", 0, 2, -2, "NORTH", "Muro del refugio", "cobblestone"), new PageWall("coche", 3, 2, 0, "SOUTH", "\u00c1rbol del accidente", "oak_log"), new PageWall("tunel", -1, 3, 3, "EAST", "Boca del t\u00fanel", "stone_bricks") });
        NOTES = List.of(new Note[] { new Note("torre", 1, 3, 1, "WEST", List.of("Grabaci\u00f3n 14:", "est\u00e1tica y una", "voz que dice", "mi nombre."), "andesite"), new Note("cabana", 3, 2, -2, "SOUTH", List.of("Turno invierno", "\u00cd. Varela", "No salir de", "noche."), "spruce_planks"), new Note("campamento", -5, 1, 4, "SOUTH", List.of("D\u00eda 2: algo", "rodea el", "campamento", "toda la noche."), "barrel[facing=up]"), new Note("capilla", -2, 2, -1, "EAST", List.of("Aqu\u00ed rezaban", "por los que se", "perd\u00edan en el", "bosque."), "stone_bricks"), new Note("piedras", 0, 2, 4, "NORTH", List.of("Si lo miras,", "te mira.", "", "\u00cd.V."), "mossy_cobblestone"), new Note("caravana", 5, 3, 0, "EAST", List.of("Se fueron", "con prisa.", "Dejaron la", "cena servida."), "white_terracotta"), new Note("caza", 0, 3, 0, "NORTH", List.of("Lo vi desde", "aqu\u00ed arriba.", "No se mov\u00eda.", "Yo tampoco."), "spruce_planks"), new Note("pozo", 3, 1, 1, "SOUTH", List.of("Se oyen voces", "dentro del", "pozo.", ""), "spruce_planks"), new Note("embarcadero", 1, 2, 0, "EAST", List.of("La barca", "nunca volvi\u00f3", "a la orilla.", ""), "spruce_planks"), new Note("refugio", -1, 2, 1, "NORTH", List.of("Aqu\u00ed dorm\u00ed", "dos noches.", "Segu\u00eda ah\u00ed", "fuera."), "cobblestone"), new Note("coche", 3, 2, -1, "NORTH", List.of("El coche de", "mi padre.", "Nadie sali\u00f3", "de dentro."), "oak_log"), new Note("tunel", 0, 2, -3, "EAST", List.of("El tren de", "las 02:13", "no para aqu\u00ed.", "Ya no."), "stone_bricks") });
        PHRASES = List.of(new String[] { "SOLO HAY EST\u00c1TICA", "ME LLAMA POR MI NOMBRE", "NO TIENE CARA", "LOS \u00c1RBOLES SE MUEVEN", "LO VI EN EL PUENTE", "NO VUELVAS A LA CABA\u00d1A", "EL TREN NO PARA AQU\u00cd", "SI LO MIRAS, TE MIRA", "LA TORRE SIGUE EMITIENDO", "NO APAGUES LA LINTERNA", "EST\u00c1 DETR\u00c1S DE TI", "D\u00cdA 40: SIGO AQU\u00cd" });
    }
    
    public enum Biome
    {
        PINE, 
        BURNT, 
        DEAD, 
        SWAMP, 
        MIXED;
    }
}
