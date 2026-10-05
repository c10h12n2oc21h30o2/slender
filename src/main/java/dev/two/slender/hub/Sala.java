package dev.two.slender.hub;

import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Loc;
import dev.two.slender.map.Zone;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * Sala de los Menesteres de LA NOCHE ENTERA (spec §9): detrás del muro sur de la estación, en su caja
 * (x −7…7, y 0…7, z 24…38, relativa a {@link HubBuilder#ORIGIN}), dentro del radio que se limpia al construir la estación.
 * Sin aberturas: se entra y se sale por teletransporte. Muro sur doble: la puerta de salida es un hueco con hormigón
 * negro detrás, como los arcos.
 */
public final class Sala {
    /** Interior: x −6…6, y 1…6, z 25…36; muro sur en z = 37 (con la puerta) y z = 38 (hormigón detrás). */
    private static final int X = 6;
    private static final int Z1 = 24;
    private static final int Z2 = 38;
    private static final int TOP = 7;
    /** Centro del círculo de listos. */
    private static final int RING_Z = 30;
    private static final Material[] WALL = {Material.STONE_BRICKS, Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS,
        Material.MOSSY_STONE_BRICKS};

    private Sala() {
    }

    public static Zone box() {
        return Zone.of(new BlockPos(-X - 1, 0, Z1), new BlockPos(X + 1, TOP, Z2));
    }

    /** Dentro del anillo de farolillos de alma (5×5). */
    public static Zone readyCircle() {
        return Zone.of(new BlockPos(-2, 1, RING_Z - 2), new BlockPos(2, 3, RING_Z + 2));
    }

    /** Delante del altar de la vela, junto a la pared oeste. */
    public static Zone candleShrine() {
        return Zone.of(new BlockPos(-5, 1, RING_Z - 1), new BlockPos(-4, 3, RING_Z + 1));
    }

    /** El hueco de la puerta de salida (1×2) en el muro sur. */
    public static Zone exitDoor() {
        return Zone.of(new BlockPos(0, 1, Z2 - 1), new BlockPos(0, 2, Z2 - 1));
    }

    /** Donde aparecen los jugadores: entre el círculo y la puerta, mirando al tablón (norte). */
    public static Loc spawn(BlockPos origin) {
        return new Loc(origin.x() + 0.5, origin.y() + 1, origin.z() + 35.5, 180.0F);
    }

    /** Los 8 carteles del tablón, en la pared norte: de arriba abajo y de izquierda a derecha (oeste → este). */
    public static List<BlockPos> boardSigns() {
        List<BlockPos> out = new ArrayList<>();
        for (int y = 4; y >= 3; y--) {
            for (int x = -2; x <= 1; x++) {
                out.add(new BlockPos(x, y, Z1 + 1));
            }
        }
        return out;
    }

    /** Una zona relativa de la sala, en coordenadas del mundo. */
    public static Zone absolute(Zone rel) {
        BlockPos o = HubBuilder.ORIGIN;
        return Zone.of(new BlockPos(rel.min().x() + o.x(), rel.min().y() + o.y(), rel.min().z() + o.z()),
                new BlockPos(rel.max().x() + o.x(), rel.max().y() + o.y(), rel.max().z() + o.z()));
    }

    public static void build(Builder b) {
        shell(b);
        board(b);
        ring(b);
        shrine(b);
        fireplace(b);
        exit(b);
        candles(b);
    }

    /** Suelo de madera oscura y andesita, paredes de ladrillo con estanterías y techo de losas. */
    private static void shell(Builder b) {
        b.fill(-X - 1, -1, Z1, X + 1, -1, Z2, Material.STONE);
        for (int x = -X - 1; x <= X + 1; x++) {
            for (int z = Z1; z <= Z2; z++) {
                b.set(x, 0, z, (x + z & 1) == 0 ? Material.DARK_OAK_PLANKS : Material.POLISHED_ANDESITE);
                b.set(x, TOP, z, "dark_oak_slab[type=top]");
            }
        }
        for (int y = 1; y < TOP; y++) {
            for (int x = -X - 1; x <= X + 1; x++) {
                b.set(x, y, Z1, b.pick(WALL));
                b.set(x, y, Z2 - 1, b.pick(WALL));
                b.set(x, y, Z2, b.pick(WALL));
            }
            for (int z = Z1; z <= Z2; z++) {
                b.set(-X - 1, y, z, b.pick(WALL));
                b.set(X + 1, y, z, b.pick(WALL));
            }
        }
        b.air(-X, 1, Z1 + 1, X, TOP - 1, Z2 - 2);
        // estanterías en las paredes este y oeste (menos junto a la chimenea y el altar) y en el muro sur
        for (int z = Z1 + 1; z <= Z2 - 2; z++) {
            boolean nearFeature = Math.abs(z - RING_Z) <= 2;
            for (int y = 1; y <= 4; y++) {
                if (!nearFeature) {
                    b.set(-X - 1, y, z, Material.BOOKSHELF);
                    b.set(X + 1, y, z, Material.BOOKSHELF);
                }
            }
        }
        for (int x = -X; x <= X; x++) {
            if (Math.abs(x) >= 2) {
                for (int y = 1; y <= 4; y++) {
                    b.set(x, y, Z2 - 1, Material.BOOKSHELF);
                }
            }
        }
        b.fill(-X, 5, Z1 + 1, -4, 5, Z1 + 1, "dark_oak_slab[type=top]");
        b.fill(3, 5, Z1 + 1, X, 5, Z1 + 1, "dark_oak_slab[type=top]");
        for (int x = -X; x <= X; x++) {
            b.set(x, 5, Z2 - 2, "dark_oak_slab[type=top]");
        }
        for (int x : new int[]{-4, 4}) {
            for (int z : new int[]{28, 33}) {
                b.set(x, 6, z, "soul_lantern[hanging=true]");
            }
        }
    }

    /** Tablón de 4×2 carteles con marco de madera; el plugin escribe en ellos. */
    private static void board(Builder b) {
        b.fill(-3, 2, Z1, 2, 5, Z1, Material.DARK_OAK_PLANKS);
        for (BlockPos p : boardSigns()) {
            b.sign(p.x(), p.y(), p.z(), "south", DyeColor.WHITE, "", "", "", "");
        }
        b.sign(-1, 5, Z1 + 1, "south", DyeColor.YELLOW, "", "SALA DE LOS", "MENESTERES", "");
    }

    /** Anillo de farolillos de alma alrededor del círculo de listos. */
    private static void ring(Builder b) {
        for (int x = -2; x <= 2; x++) {
            for (int z = RING_Z - 2; z <= RING_Z + 2; z++) {
                b.set(x, 0, z, Material.POLISHED_BLACKSTONE);
            }
        }
        b.set(0, 0, RING_Z, Material.CHISELED_POLISHED_BLACKSTONE);
        int[][] lanterns = {{-3, RING_Z - 3}, {0, RING_Z - 3}, {3, RING_Z - 3}, {-3, RING_Z}, {3, RING_Z},
            {-3, RING_Z + 3}, {0, RING_Z + 3}, {3, RING_Z + 3}};
        for (int[] l : lanterns) {
            b.set(l[0], 1, l[1], "soul_lantern[hanging=false]");
        }
    }

    /** Altar de la vela: ladrillo cincelado con una vela encendida, contra la pared oeste. */
    private static void shrine(Builder b) {
        b.set(-X, 1, RING_Z, Material.CHISELED_STONE_BRICKS);
        b.set(-X, 2, RING_Z, "candle[candles=1,lit=true]");
        b.set(-X, 1, RING_Z - 1, "stone_brick_wall");
        b.set(-X, 1, RING_Z + 1, "stone_brick_wall");
        b.set(-X, 2, RING_Z - 1, "white_candle[candles=2,lit=true]");
        b.set(-X, 2, RING_Z + 1, "white_candle[candles=2,lit=true]");
        b.sign(-X, 3, RING_Z, "east", DyeColor.YELLOW, "UNA VELA", "por un", "perdido", "");
    }

    /** Chimenea en la pared este: fuego tras unos barrotes. */
    private static void fireplace(Builder b) {
        for (int z : new int[]{RING_Z - 1, RING_Z + 1}) {
            b.fill(X - 1, 1, z, X, 4, z, Material.BRICKS);
        }
        b.fill(X - 1, 3, RING_Z, X, 4, RING_Z, Material.BRICKS);
        b.set(X, 1, RING_Z, "campfire[lit=true,facing=west]");
        b.set(X, 2, RING_Z, Material.AIR);
        b.set(X - 1, 1, RING_Z, "iron_bars[north=true,south=true]");
        b.set(X - 1, 2, RING_Z, "iron_bars[north=true,south=true]");
        b.fill(X - 1, 5, RING_Z - 1, X - 1, 5, RING_Z + 1, "brick_slab[type=bottom]");
        b.set(X - 1, 6, RING_Z - 1, "candle[candles=3,lit=true]");
        b.set(X - 1, 6, RING_Z + 1, "candle[candles=2,lit=true]");
    }

    /** Puerta de salida: hueco de 1×2 en el muro sur con hormigón negro detrás. */
    private static void exit(Builder b) {
        b.fill(-1, 1, Z2 - 1, 1, 3, Z2 - 1, Material.POLISHED_DEEPSLATE);
        b.air(0, 1, Z2 - 1, 0, 2, Z2 - 1);
        b.fill(0, 1, Z2, 0, 2, Z2, Material.BLACK_CONCRETE);
        b.sign(0, 4, Z2 - 2, "north", DyeColor.RED, "", "SALIDA", "a la estación", "");
    }

    /** Muchas velas: por el suelo junto a las paredes y sobre las estanterías. */
    private static void candles(Builder b) {
        String[] kinds = {"candle", "white_candle", "candle", "light_gray_candle"};
        for (int i = 0; i < 26; i++) {
            int x;
            int z;
            if (b.chance(0.5)) {
                x = b.chance(0.5) ? -X : X;
                z = b.range(Z1 + 1, Z2 - 2);
                if (Math.abs(z - RING_Z) <= 2) {
                    continue;
                }
            } else {
                x = b.range(-X, X);
                z = b.chance(0.5) ? Z1 + 1 : Z2 - 2;
                if (Math.abs(x) <= 1) {
                    continue;
                }
            }
            b.set(x, 1, z, b.pick(kinds) + "[candles=" + b.range(1, 4) + ",lit=true]");
        }
        for (int x : new int[]{-6, -4, 3, 5}) {
            b.set(x, 6, Z1 + 1, "candle[candles=" + b.range(2, 4) + ",lit=true]");
        }
    }
}
