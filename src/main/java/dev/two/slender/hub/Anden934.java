package dev.two.slender.hub;

import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Zone;
import dev.two.slender.map.builders.hogwarts.HogwartsMaps;
import dev.two.slender.map.builders.hogwarts.HogwartsSections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * Andén 9¾ en el patio sin usar de la estación (x 0…34, z −24…−4, relativo a su origen).
 * El andén 1 se alarga hasta un muro (z = −6) con un tramo de ladrillo: su hueco central son block_display
 * (Java ve ladrillos y lo cruza, Bedrock ve un hueco). Detrás, a la misma altura, el Expreso de Hogwarts con una
 * puerta por sección y una 8.ª para LA NOCHE ENTERA.
 */
public final class Anden934 {
    /** Etiqueta de scoreboard de los block_display del hueco. */
    public static final String TAG = "slender_hub";
    /** y del suelo del andén 1 y del 9¾ (se pisa a y = 2). */
    static final int FLOOR_Y = 1;
    static final int WALL_Z = -6;
    static final int GAP_X = 16;
    /** Fachada sur del tren; las puertas se abren en esta fila. */
    static final int TRAIN_Z = -18;
    /** x de las puertas 1–7, en el orden de HogwartsMaps.IDS. */
    private static final int[] DOOR_X = {3, 6, 9, 14, 17, 20, 24};
    /** La 8.ª puerta: LA NOCHE ENTERA. */
    private static final int NIGHT_X = 26;
    /** Id de la zona de la 8.ª puerta (reservado en MapIds). */
    public static final String NIGHT_DOOR = "noche";
    /** Vagones (x de inicio y fin) y enganches entre ellos; la locomotora va de 28 a 34. */
    private static final int[][] CARRIAGES = {{1, 10}, {12, 21}, {23, 27}};
    private static final int[] COUPLINGS = {11, 22};
    private static final String[] FLOOR = {"smooth_stone", "smooth_stone", "smooth_stone", "polished_andesite", "andesite",
        "cracked_stone_bricks"};
    private static final String[] WALL = {"stone_bricks", "stone_bricks", "cracked_stone_bricks", "mossy_stone_bricks"};

    private Anden934() {
    }

    /** Zona de cada puerta (3×3×1, metida en el hueco de la puerta), relativa al origen de la estación. */
    public static LinkedHashMap<String, Zone> doors() {
        LinkedHashMap<String, Zone> out = new LinkedHashMap<>();
        List<String> ids = HogwartsMaps.IDS;
        int y = FLOOR_Y + 2;
        for (int i = 0; i < ids.size(); i++) {
            int x = DOOR_X[i];
            out.put(ids.get(i), Zone.of(new BlockPos(x - 1, y, TRAIN_Z), new BlockPos(x + 1, y + 2, TRAIN_Z)));
        }
        return out;
    }

    /**
     * Zona de la 8.ª puerta, relativa a la estación. Solo x 26…27: la 7.ª puerta (x 24) ocupa hasta x 25 y su
     * hueco está en el mismo vagón.
     */
    public static Zone nightDoor() {
        int y = FLOOR_Y + 2;
        return Zone.of(new BlockPos(NIGHT_X, y, TRAIN_Z), new BlockPos(NIGHT_X + 1, y + 2, TRAIN_Z));
    }

    /** Las 9 celdas del hueco del muro (3 de ancho × 3 de alto, desde el suelo del andén), relativas a la estación. */
    public static List<BlockPos> gapCells() {
        List<BlockPos> out = new ArrayList<>();
        for (int x = GAP_X; x < GAP_X + 3; x++) {
            for (int y = FLOOR_Y + 1; y <= FLOOR_Y + 3; y++) {
                out.add(new BlockPos(x, y, WALL_Z));
            }
        }
        return out;
    }

    /** Cartel de puerta: "- n -" y el nombre en líneas de ≤15 (una palabra larga se parte con guion). */
    public static List<String> doorSign(int n, String name) {
        List<String> lines = new ArrayList<>();
        lines.add("- " + n + " -");
        StringBuilder cur = new StringBuilder();
        for (String w : name.split(" ")) {
            while (w.length() > 15) {
                if (!cur.isEmpty()) {
                    lines.add(cur.toString());
                    cur.setLength(0);
                }
                lines.add(w.substring(0, 10) + "-");
                w = w.substring(10);
            }
            if (cur.isEmpty()) {
                cur.append(w);
            } else if (cur.length() + 1 + w.length() <= 15) {
                cur.append(' ').append(w);
            } else {
                lines.add(cur.toString());
                cur.setLength(0);
                cur.append(w);
            }
        }
        if (!cur.isEmpty()) {
            lines.add(cur.toString());
        }
        while (lines.size() < 4) {
            lines.add("");
        }
        return List.copyOf(lines.subList(0, 4));
    }

    /** Construye el andén (después de yard(), que deja árboles secos por aquí) y devuelve las puertas en absoluto. */
    public static Map<String, Zone> build(Builder b) {
        b.air(-5, 1, -24, 34, 12, -3);
        platformOne(b);
        walls(b);
        magicWall(b);
        // todo lo de detrás del muro se construye sobre el suelo del andén: en h, el suelo es y = 0
        Builder h = b.shifted(0, FLOOR_Y, 0);
        platform(h);
        train(h);
        Map<String, String> names = new LinkedHashMap<>();
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            names.put(s.id(), s.name());
        }
        List<String> ids = HogwartsMaps.IDS;
        for (int i = 0; i < ids.size(); i++) {
            int x = DOOR_X[i];
            h.set(x, 1, TRAIN_Z + 1, "stone_brick_slab[type=bottom]");
            List<String> l = doorSign(i + 1, names.getOrDefault(ids.get(i), ids.get(i)));
            h.sign(x, 4, TRAIN_Z + 1, "south", DyeColor.YELLOW, l.toArray(String[]::new));
        }
        h.set(NIGHT_X, 1, TRAIN_Z + 1, "stone_brick_slab[type=bottom]");
        h.sign(NIGHT_X, 4, TRAIN_Z + 1, "south", DyeColor.RED, "LA NOCHE", "ENTERA", "", "8.ª puerta");
        h.sign(1, 3, -9, "east", DyeColor.WHITE, "Hogwarts por", "Gabbel", "v1.1.0", "planetminecraft");
        LinkedHashMap<String, Zone> out = new LinkedHashMap<>();
        LinkedHashMap<String, Zone> rel = doors();
        rel.put(NIGHT_DOOR, nightDoor());
        rel.forEach((id, z) -> out.put(id, Zone.of(b.abs(z.min().x(), z.min().y(), z.min().z()),
                b.abs(z.max().x(), z.max().y(), z.max().z()))));
        return out;
    }

    /** El andén 1 sigue hasta el muro (z −5…−3) en vez de acabar en la valla: se llega al 9¾ sin bajar. */
    private static void platformOne(Builder b) {
        for (int x = -5; x <= 34; x++) {
            for (int z = -5; z <= -3; z++) {
                b.set(x, 0, z, Material.STONE);
                b.set(x, FLOOR_Y, z, b.pick(FLOOR));
            }
        }
        for (int x : new int[]{4, 30}) {
            b.fill(x, 2, -4, x, 4, -4, "dark_oak_fence");
            b.set(x, 5, -4, "lantern[hanging=false]");
        }
        b.sign(8, 4, WALL_Z + 1, "south", DyeColor.WHITE, "", "ANDÉN 9", "", "");
        b.sign(26, 4, WALL_Z + 1, "south", DyeColor.WHITE, "", "ANDÉN 10", "", "");
    }

    /**
     * Muro del andén (z = −6, de x −5 a 34) y cierre oeste (x = 0): el 9¾ y el patio de detrás quedan cerrados;
     * al norte y al este están los muros de la estación.
     */
    private static void walls(Builder b) {
        for (int x = -5; x <= 34; x++) {
            for (int y = 1; y <= 8; y++) {
                b.set(x, y, WALL_Z, b.pick(WALL));
            }
        }
        for (int z = -24; z < WALL_Z; z++) {
            for (int y = 1; y <= 8; y++) {
                b.set(0, y, z, b.pick(WALL));
            }
        }
    }

    private static void magicWall(Builder b) {
        int top = FLOOR_Y + 6;
        b.fill(13, 1, WALL_Z, 21, top, WALL_Z, Material.BRICKS);
        b.fill(12, 1, WALL_Z, 12, top + 1, WALL_Z, Material.STONE_BRICKS);
        b.fill(22, 1, WALL_Z, 22, top + 1, WALL_Z, Material.STONE_BRICKS);
        b.fill(13, top + 1, WALL_Z, 21, top + 1, WALL_Z, "stone_brick_slab[type=bottom]");
        for (BlockPos p : gapCells()) {
            b.set(p.x(), p.y(), p.z(), Material.AIR);
            b.display(p.x(), p.y(), p.z(), "minecraft:bricks", TAG);
        }
        b.sign(GAP_X + 1, FLOOR_Y + 4, WALL_Z + 1, "south", DyeColor.WHITE, "", "9¾", "", "");
        b.sign(GAP_X + 1, FLOOR_Y + 5, WALL_Z - 1, "north", DyeColor.YELLOW, "ANDÉN 9¾", "HOGWARTS", "", "");
    }

    /** Suelo del 9¾ (en h el suelo es y = 0), vía, farolas, bancos y equipaje. */
    private static void platform(Builder h) {
        for (int x = 1; x <= 34; x++) {
            for (int z = -24; z <= -7; z++) {
                String floor;
                if (z <= TRAIN_Z) {
                    floor = "gravel";
                } else if (z == TRAIN_Z + 1) {
                    floor = "smooth_sandstone";
                } else {
                    floor = h.pick(FLOOR);
                }
                h.set(x, -1, z, Material.STONE);
                h.set(x, 0, z, floor);
            }
            h.set(x, 1, -20, "rail[shape=east_west]");
        }
        for (int x : new int[]{5, 12, 25, 31}) {
            h.fill(x, 1, -12, x, 3, -12, "dark_oak_fence");
            h.set(x, 4, -12, "lantern[hanging=false]");
        }
        for (int[] bench : new int[][]{{2, 5}, {26, 29}}) {
            for (int x = bench[0]; x <= bench[1]; x++) {
                h.stairs(x, 1, WALL_Z - 1, "dark_oak_stairs", "south");
            }
        }
        // equipaje: baúles y la jaula de la lechuza
        h.set(31, 1, -8, "barrel[facing=up]");
        h.set(32, 1, -8, "chest[facing=south]");
        h.set(31, 2, -8, "barrel[facing=north]");
        h.set(33, 1, -9, "chest[facing=west]");
        h.set(33, 1, -8, "iron_bars");
        h.set(33, 2, -8, "lantern[hanging=false]");
        h.set(9, 1, -8, "chest[facing=south]");
        h.set(9, 2, -8, "barrel[facing=up]");
    }

    private static void train(Builder h) {
        for (int[] c : CARRIAGES) {
            carriage(h, c[0], c[1]);
        }
        for (int x : COUPLINGS) {
            // fuelle entre vagones; delante queda un hueco sin salida
            h.fill(x, 2, -21, x, 4, -19, Material.BLACK_WOOL);
            h.fill(x, 5, -21, x, 5, -19, "polished_blackstone_slab[type=bottom]");
        }
        locomotive(h);
    }

    private static boolean isDoor(int x) {
        if (x == NIGHT_X) {
            return true;
        }
        for (int d : DOOR_X) {
            if (d == x) {
                return true;
            }
        }
        return false;
    }

    /** Vagón granate con ventanas encendidas, techo negro curvo e interior con asientos y faroles. */
    private static void carriage(Builder h, int x1, int x2) {
        // bastidor y ruedas
        h.fill(x1, 1, -22, x2, 1, TRAIN_Z, Material.POLISHED_BLACKSTONE);
        for (int x = x1; x <= x2; x++) {
            boolean wheel = x == x1 + 1 || x == x1 + 2 || x == x2 - 2 || x == x2 - 1;
            if (!wheel) {
                h.set(x, 1, TRAIN_Z, "polished_blackstone_slab[type=top]");
                h.set(x, 1, -22, "polished_blackstone_slab[type=top]");
            } else {
                h.set(x, 1, TRAIN_Z, Material.BLACK_CONCRETE);
                h.set(x, 1, -22, Material.BLACK_CONCRETE);
            }
        }
        // caja: zócalo oscuro, ventanas, franja de arriba
        h.fill(x1, 2, -22, x2, 5, TRAIN_Z, Material.RED_CONCRETE);
        h.fill(x1, 2, -22, x2, 2, -22, Material.RED_TERRACOTTA);
        h.fill(x1, 2, TRAIN_Z, x2, 2, TRAIN_Z, Material.RED_TERRACOTTA);
        h.air(x1 + 1, 2, -21, x2 - 1, 5, -19);
        for (int x = x1 + 1; x < x2; x++) {
            h.fill(x, 3, -22, x, 4, -22, "glass_pane[east=true,west=true]");
            if (isDoor(x)) {
                h.air(x, 2, TRAIN_Z, x, 3, TRAIN_Z);
            } else {
                h.fill(x, 3, TRAIN_Z, x, 4, TRAIN_Z, "glass_pane[east=true,west=true]");
            }
        }
        // interior: alfombra, asientos contra la pared norte y faroles colgados
        for (int x = x1 + 1; x < x2; x++) {
            h.fill(x, 2, -20, x, 2, -19, "red_carpet");
            h.stairs(x, 2, -21, "dark_oak_stairs", "north");
        }
        for (int x = x1 + 2; x < x2; x += 3) {
            h.set(x, 5, -20, "lantern[hanging=true]");
        }
        // techo negro curvo
        h.fill(x1, 6, -21, x2, 6, -19, Material.POLISHED_BLACKSTONE);
        for (int x = x1; x <= x2; x++) {
            h.stairs(x, 6, TRAIN_Z, "polished_blackstone_stairs", "north");
            h.stairs(x, 6, -22, "polished_blackstone_stairs", "south");
            h.stairs(x, 7, -19, "polished_blackstone_stairs", "north");
            h.stairs(x, 7, -21, "polished_blackstone_stairs", "south");
            h.set(x, 7, -20, "polished_blackstone_slab[type=bottom]");
        }
    }

    /** Locomotora granate: cabina con luz del hogar, caldera, caja de humos negra, chimenea con humo y cúpula de cobre. */
    private static void locomotive(Builder h) {
        h.fill(28, 1, -22, 34, 1, TRAIN_Z, Material.POLISHED_BLACKSTONE);
        // ruedas motrices granates a los dos lados
        h.fill(31, 1, TRAIN_Z, 33, 1, TRAIN_Z, Material.RED_CONCRETE);
        h.fill(31, 1, -22, 33, 1, -22, Material.RED_CONCRETE);
        // cabina
        h.fill(28, 2, -22, 30, 5, TRAIN_Z, Material.RED_CONCRETE);
        h.fill(28, 2, -22, 30, 2, TRAIN_Z, Material.RED_TERRACOTTA);
        h.fill(28, 5, -22, 30, 5, TRAIN_Z, Material.BLACK_CONCRETE);
        h.air(29, 2, -21, 29, 4, -19);
        h.set(29, 2, -20, "lantern[hanging=false]");
        h.set(29, 4, TRAIN_Z, "orange_stained_glass_pane[east=true,west=true]");
        h.set(29, 4, -22, "orange_stained_glass_pane[east=true,west=true]");
        h.fill(28, 6, -22, 31, 6, TRAIN_Z, "polished_blackstone_slab[type=bottom]");
        h.sign(29, 3, TRAIN_Z + 1, "south", DyeColor.YELLOW, "EXPRESO DE", "HOGWARTS", "", "5972");
        // caldera con aros negros y estribos
        h.fill(31, 2, -21, 33, 4, -19, Material.RED_CONCRETE);
        h.fill(32, 2, -21, 32, 4, -19, Material.BLACK_CONCRETE);
        h.fill(31, 5, -20, 33, 5, -20, "red_nether_brick_slab[type=bottom]");
        h.fill(31, 2, TRAIN_Z, 34, 2, TRAIN_Z, "polished_blackstone_slab[type=bottom]");
        h.fill(31, 2, -22, 34, 2, -22, "polished_blackstone_slab[type=bottom]");
        // caja de humos y chimenea con humo
        h.fill(34, 2, -21, 34, 4, -19, Material.BLACK_CONCRETE);
        h.set(34, 5, -20, Material.BLACK_CONCRETE);
        h.set(34, 6, -20, Material.BLACK_CONCRETE);
        h.set(34, 7, -20, "campfire[lit=true,signal_fire=true]");
        // cúpula y silbato
        h.set(32, 5, -20, "waxed_copper_block");
        h.set(31, 5, -20, "lightning_rod[facing=up]");
    }
}
