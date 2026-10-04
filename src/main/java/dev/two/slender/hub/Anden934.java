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
 * Se entra cruzando un muro de ladrillo: el hueco central son block_display (Java ve ladrillos,
 * Bedrock ve un hueco). Detrás, el Expreso con una puerta por sección y una 8.ª tapiada.
 */
public final class Anden934 {
    /** Etiqueta de scoreboard de los block_display del hueco. */
    public static final String TAG = "slender_hub";
    static final int WALL_Z = -6;
    static final int GAP_X = 16;
    /** Fachada sur del tren; las puertas se abren en esta fila. */
    static final int TRAIN_Z = -18;
    /** x de las puertas 1–7, en el orden de HogwartsMaps.IDS. */
    private static final int[] DOOR_X = {3, 6, 9, 14, 17, 20, 24};
    /** La 8.ª puerta, tapiada: el modo historia ("la noche entera"). */
    private static final int SEALED_X = 26;
    /** Vagones (x de inicio y fin) y enganches entre ellos. */
    private static final int[][] CARRIAGES = {{1, 10}, {12, 21}, {23, 27}};
    private static final int[] COUPLINGS = {11, 22};
    private static final String[] FLOOR = {"stone_bricks", "stone_bricks", "polished_andesite", "cracked_stone_bricks"};
    private static final String[] YARD_WALL = {"stone_bricks", "stone_bricks", "cracked_stone_bricks", "mossy_stone_bricks"};

    private Anden934() {
    }

    /** Zona de cada puerta (3×3×1, metida en el hueco de la puerta), relativa al origen de la estación. */
    public static LinkedHashMap<String, Zone> doors() {
        LinkedHashMap<String, Zone> out = new LinkedHashMap<>();
        List<String> ids = HogwartsMaps.IDS;
        for (int i = 0; i < ids.size(); i++) {
            int x = DOOR_X[i];
            out.put(ids.get(i), Zone.of(new BlockPos(x - 1, 2, TRAIN_Z), new BlockPos(x + 1, 4, TRAIN_Z)));
        }
        return out;
    }

    /** Las 9 celdas del hueco del muro (3 de ancho × 3 de alto), relativas a la estación. */
    public static List<BlockPos> gapCells() {
        List<BlockPos> out = new ArrayList<>();
        for (int x = GAP_X; x < GAP_X + 3; x++) {
            for (int y = 1; y <= 3; y++) {
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
        b.air(0, 1, -24, 34, 10, -4);
        enclosure(b);
        magicWall(b);
        platform(b);
        train(b);
        Map<String, String> names = new LinkedHashMap<>();
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            names.put(s.id(), s.name());
        }
        List<String> ids = HogwartsMaps.IDS;
        for (int i = 0; i < ids.size(); i++) {
            int x = DOOR_X[i];
            b.set(x, 1, TRAIN_Z + 1, "stone_brick_slab[type=bottom]");
            List<String> l = doorSign(i + 1, names.getOrDefault(ids.get(i), ids.get(i)));
            b.sign(x, 4, TRAIN_Z + 1, "south", DyeColor.YELLOW, l.toArray(String[]::new));
        }
        b.fill(SEALED_X, 2, TRAIN_Z, SEALED_X, 3, TRAIN_Z, "bricks");
        b.sign(SEALED_X, 4, TRAIN_Z + 1, "south", DyeColor.RED, "LA NOCHE", "ENTERA", "", "próximamente");
        b.sign(1, 3, -9, "east", DyeColor.WHITE, "Hogwarts por", "Gabbel", "v1.1.0", "planetminecraft");
        LinkedHashMap<String, Zone> out = new LinkedHashMap<>();
        doors().forEach((id, z) -> out.put(id, Zone.of(b.abs(z.min().x(), z.min().y(), z.min().z()),
                b.abs(z.max().x(), z.max().y(), z.max().z()))));
        return out;
    }

    /** Cierra el andén por el sur (z = −6) y el oeste (x = 0); al norte y al este están los muros de la estación. */
    private static void enclosure(Builder b) {
        for (int x = 0; x <= 34; x++) {
            for (int y = 1; y <= 7; y++) {
                b.set(x, y, WALL_Z, b.pick(YARD_WALL));
            }
        }
        for (int z = -24; z < WALL_Z; z++) {
            for (int y = 1; y <= 7; y++) {
                b.set(0, y, z, b.pick(YARD_WALL));
            }
        }
        // hueco en la valla del andén 1, con escalón, frente al muro
        b.air(GAP_X, 1, -3, GAP_X + 2, 2, -3);
        for (int x = GAP_X; x < GAP_X + 3; x++) {
            b.stairs(x, 1, -3, "stone_brick_stairs", "south");
        }
    }

    private static void magicWall(Builder b) {
        b.fill(13, 1, WALL_Z, 21, 6, WALL_Z, Material.BRICKS);
        b.fill(12, 1, WALL_Z, 12, 7, WALL_Z, Material.STONE_BRICKS);
        b.fill(22, 1, WALL_Z, 22, 7, WALL_Z, Material.STONE_BRICKS);
        b.fill(13, 7, WALL_Z, 21, 7, WALL_Z, "stone_brick_slab[type=bottom]");
        for (BlockPos p : gapCells()) {
            b.set(p.x(), p.y(), p.z(), Material.AIR);
            b.display(p.x(), p.y(), p.z(), "minecraft:bricks", TAG);
        }
        b.sign(GAP_X + 1, 5, WALL_Z + 1, "south", DyeColor.WHITE, "", "9¾", "", "");
        b.sign(GAP_X + 1, 5, WALL_Z - 1, "north", DyeColor.WHITE, "ANDÉN 9¾", "HOGWARTS", "", "");
    }

    private static void platform(Builder b) {
        for (int x = 1; x <= 34; x++) {
            for (int z = -24; z <= -7; z++) {
                String floor;
                if (z <= TRAIN_Z) {
                    floor = "gravel";
                } else if (z == TRAIN_Z + 1) {
                    floor = "smooth_sandstone";
                } else {
                    floor = b.pick(FLOOR);
                }
                b.set(x, 0, z, floor);
            }
            b.set(x, 1, -20, "rail[shape=east_west]");
        }
        for (int x : new int[]{5, 12, 25}) {
            b.fill(x, 1, -12, x, 3, -12, "dark_oak_fence");
            b.set(x, 4, -12, "soul_lantern[hanging=false]");
        }
        // equipaje
        b.set(31, 1, -8, "barrel[facing=up]");
        b.set(32, 1, -8, "chest[facing=south]");
        b.set(31, 2, -8, "barrel[facing=north]");
        b.set(33, 1, -9, "barrel[facing=up]");
    }

    private static void train(Builder b) {
        for (int[] c : CARRIAGES) {
            carriage(b, c[0], c[1]);
        }
        for (int x : COUPLINGS) {
            b.set(x, 2, -20, "iron_chain[axis=x]");
            // que no se pueda pasar por el enganche al otro lado del tren
            b.fill(x, 1, TRAIN_Z, x, 5, TRAIN_Z, Material.BARRIER);
        }
        locomotive(b);
    }

    private static boolean isDoor(int x) {
        if (x == SEALED_X) {
            return true;
        }
        for (int d : DOOR_X) {
            if (d == x) {
                return true;
            }
        }
        return false;
    }

    private static void carriage(Builder b, int x1, int x2) {
        b.fill(x1, 1, -22, x2, 1, TRAIN_Z, Material.POLISHED_BLACKSTONE);
        b.fill(x1, 2, -22, x2, 4, TRAIN_Z, Material.RED_CONCRETE);
        b.fill(x1, 5, -22, x2, 5, TRAIN_Z, Material.RED_TERRACOTTA);
        b.fill(x1, 6, -22, x2, 6, TRAIN_Z, "blackstone_slab[type=bottom]");
        for (int x = x1 + 1; x < x2; x++) {
            if (isDoor(x)) {
                b.air(x, 2, TRAIN_Z, x, 3, TRAIN_Z);
                b.fill(x, 2, TRAIN_Z - 1, x, 3, TRAIN_Z - 1, Material.BLACK_CONCRETE);
            } else {
                b.set(x, 3, TRAIN_Z, Material.BLACK_STAINED_GLASS);
                b.set(x, 3, -22, Material.BLACK_STAINED_GLASS);
            }
        }
    }

    private static void locomotive(Builder b) {
        b.fill(28, 1, -22, 34, 1, TRAIN_Z, Material.POLISHED_BLACKSTONE);
        // cabina
        b.fill(28, 2, -22, 30, 5, TRAIN_Z, Material.BLACK_CONCRETE);
        b.set(29, 4, TRAIN_Z, Material.ORANGE_STAINED_GLASS);
        b.set(29, 4, -22, Material.ORANGE_STAINED_GLASS);
        b.fill(27, 6, -22, 31, 6, TRAIN_Z, "blackstone_slab[type=bottom]");
        b.sign(29, 3, TRAIN_Z + 1, "south", DyeColor.YELLOW, "EXPRESO DE", "HOGWARTS", "", "");
        // caldera
        b.fill(31, 2, -21, 34, 4, -19, Material.BLACK_CONCRETE);
        b.fill(32, 2, -21, 32, 4, -19, Material.RED_CONCRETE);
        b.fill(34, 2, -22, 34, 2, TRAIN_Z, Material.RED_CONCRETE);
        // chimenea con humo
        b.set(33, 5, -20, Material.BLACK_CONCRETE);
        b.set(33, 6, -20, "campfire[lit=true,signal_fire=true]");
        b.set(31, 5, -20, Material.BLACK_CONCRETE);
    }
}
