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
    static final int DX = -191;
    static final int DY = -34;
    static final int DZ = -206;
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

    @Override
    public ArenaMap build(Builder builder, String world, long seed) {
        Builder b = builder.shifted(DX, DY, DZ);
        b.paste(HogwartsMaps.schematic(), 0, 0, 0);
        b.extras(HogwartsMaps.extras(), 0, 0, 0);
        for (HogwartsSections.Seal c : this.s.seal()) {
            b.set(c.pos().x(), c.pos().y(), c.pos().z(), c.state());
        }
        for (HogwartsSections.LightBlock l : this.s.lightBlocks()) {
            b.set(l.pos().x(), l.pos().y(), l.pos().z(), l.state());
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
        List<PageSpot> pages = new ArrayList<>();
        for (PageSpot p : this.s.pages()) {
            pages.add(b.pageOnWall(p.pos().x(), p.pos().y(), p.pos().z(), p.facing(), p.label()));
        }
        List<BlockPos> batteries = this.s.batteries().stream().map(p -> b.abs(p.x(), p.y(), p.z())).toList();
        List<BlockPos> gate = this.s.gate().stream().map(p -> b.abs(p.x(), p.y(), p.z())).toList();
        List<Loc> spawns = this.s.spawns().stream()
                .map(l -> new Loc(b.ox + l.x(), b.oy + l.y(), b.oz + l.z(), l.yaw())).toList();
        int[] bd = this.s.bounds();
        return new ArenaMap(this.id(), this.displayName(), world, builder.abs(0, 0, 0),
                b.abs(bd[0], this.s.fallY(), bd[1]), b.abs(bd[2], 250, bd[3]), spawns.get(0), spawns, pages, batteries, gate,
                this.s.gateBlock(), b.abs(this.s.exitMin().x(), this.s.exitMin().y(), this.s.exitMin().z()),
                b.abs(this.s.exitMax().x(), this.s.exitMax().y(), this.s.exitMax().z()), seed,
                this.s.ambience().offset(b.ox, b.oy, b.oz));
    }
}
