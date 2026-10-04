package dev.two.slender.map.builders.hogwarts;

import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.PageSpot;
import dev.two.slender.map.Zone;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.Place;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/** Una sección de Hogwarts: su propia copia del castillo, sellada con derrumbes, con su contenido. */
public final class HogwartsBuilder implements MapBuilder {
    /** Desplazamiento del schematic respecto al origen: y_mundo = y_original + 30. */
    public static final int DX = -191;
    public static final int DY = -34;
    public static final int DZ = -206;
    private final HogwartsSections.Section s;

    public HogwartsBuilder(HogwartsSections.Section s) {
        this.s = s;
    }

    public HogwartsSections.Section section() {
        return this.s;
    }

    @Override
    public String id() {
        return this.s.id();
    }

    @Override
    public String displayName() {
        return "Hogwarts: " + this.s.name();
    }

    @Override
    public Material icon() {
        Material m = Material.matchMaterial(this.s.icon());
        return m != null ? m : Material.FILLED_MAP;
    }

    @Override
    public int radius() {
        return 210;
    }

    @Override
    public BlockPos origin(int index) {
        return new BlockPos(1000 * (this.s.order() - 1), 64, 6000);
    }

    @Override
    public Zone clearBox() {
        return Zone.of(new BlockPos(-192, -34, -207), new BlockPos(192, 221, 207));
    }

    /** ¿Puede aparecer Slender en (x, y, z) del mundo? Solo en suelo de la sección al que llegan los jugadores. */
    public boolean slenderCanStand(int wx, int wy, int wz) {
        BlockPos o = this.origin(0);
        return this.s.isFloor(wx - o.x() - DX, wy - o.y() - DY, wz - o.z() - DZ, HogwartsSections.WIDTH, HogwartsSections.LENGTH);
    }

    /** Un bloque que se pone después de pegar el castillo (coordenadas del schem). */
    public record Placement(BlockPos pos, String state) {
    }

    /**
     * Lo que se pone sobre el castillo pegado, en orden: derrumbes, velas y la verja cerrada (la última, que nada la
     * pise). Game solo pone la verja al acabar una partida, así que hay que ponerla al construir.
     */
    public List<Placement> placements() {
        List<Placement> out = new ArrayList<>();
        for (HogwartsSections.Seal c : this.s.seal()) {
            out.add(new Placement(c.pos(), c.state()));
        }
        for (HogwartsSections.LightBlock l : this.s.lightBlocks()) {
            out.add(new Placement(l.pos(), l.state()));
        }
        for (BlockPos g : this.s.gate()) {
            out.add(new Placement(g, this.s.gateBlock()));
        }
        return out;
    }

    /** Celda de delante de la pared de una página (donde va el marco). */
    static BlockPos front(BlockPos wall, String facing) {
        return switch (facing) {
            case "NORTH" -> new BlockPos(wall.x(), wall.y(), wall.z() - 1);
            case "SOUTH" -> new BlockPos(wall.x(), wall.y(), wall.z() + 1);
            case "EAST" -> new BlockPos(wall.x() + 1, wall.y(), wall.z());
            case "WEST" -> new BlockPos(wall.x() - 1, wall.y(), wall.z());
            default -> throw new IllegalArgumentException("facing no válido: " + facing);
        };
    }

    /** El mapa de esta sección con el schem pegado en (ox, oy, oz) del mundo. Sin mundo: se puede probar. */
    ArenaMap arena(String world, long seed, BlockPos buildOrigin, int ox, int oy, int oz) {
        List<PageSpot> pages = new ArrayList<>();
        for (PageSpot p : this.s.pages()) {
            BlockPos f = front(p.pos(), p.facing());
            pages.add(new PageSpot(new BlockPos(ox + f.x(), oy + f.y(), oz + f.z()), p.facing(), p.label()));
        }
        List<BlockPos> batteries = this.s.batteries().stream().map(p -> new BlockPos(ox + p.x(), oy + p.y(), oz + p.z())).toList();
        List<BlockPos> gate = this.s.gate().stream().map(p -> new BlockPos(ox + p.x(), oy + p.y(), oz + p.z())).toList();
        List<Loc> spawns = this.s.spawns().stream().map(l -> new Loc(ox + l.x(), oy + l.y(), oz + l.z(), l.yaw())).toList();
        int[] bd = this.s.bounds();
        BlockPos em = this.s.exitMin();
        BlockPos eM = this.s.exitMax();
        // ArenaMap.fallY() = min(min.y, origin.y - 12) y origin solo se usa para eso: con esta y sale el fallY de la
        // sección (si no, 52 en todas y quien cae de un piso alto no vuelve a un spawn)
        int fall = oy + this.s.fallY();
        BlockPos origin = new BlockPos(buildOrigin.x(), fall + 12, buildOrigin.z());
        return new ArenaMap(this.id(), this.displayName(), world, origin,
                new BlockPos(ox + bd[0], oy + this.s.fallY(), oz + bd[1]), new BlockPos(ox + bd[2], oy + 250, oz + bd[3]),
                spawns.get(0), spawns, pages, batteries, gate, this.s.gateBlock(),
                new BlockPos(ox + em.x(), oy + em.y(), oz + em.z()), new BlockPos(ox + eM.x(), oy + eM.y(), oz + eM.z()), seed,
                this.s.ambience().offset(ox, oy, oz));
    }

    @Override
    public ArenaMap build(Builder builder, String world, long seed) {
        Builder b = builder.shifted(DX, DY, DZ);
        b.paste(HogwartsMaps.schematic(), 0, 0, 0);
        b.extras(HogwartsMaps.extras(), 0, 0, 0);
        for (Placement p : this.placements()) {
            b.set(p.pos().x(), p.pos().y(), p.pos().z(), p.state());
        }
        HogwartsSections.Board board = this.s.board();
        if (board != null) {
            List<Place> places = new ArrayList<>();
            for (HogwartsSections.BoardPlace p : board.places()) {
                places.add(new Place(p.name(), p.name(), p.x(), p.z(), 0));
            }
            Decor.board(b, board.pos().x(), board.pos().y(), board.pos().z(), places, board.title().get(0), board.title().get(1));
        }
        for (HogwartsSections.NoteData n : this.s.notes()) {
            Decor.note(b, n.pos().x(), n.pos().y(), n.pos().z(), n.facing(), n.lines(), n.support());
        }
        for (PageSpot p : this.s.pages()) {
            BlockPos f = front(p.pos(), p.facing());
            b.set(f.x(), f.y(), f.z(), Material.AIR);
        }
        return this.arena(world, seed, builder.abs(0, 0, 0), b.ox, b.oy, b.oz);
    }
}
