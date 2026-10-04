package dev.two.slender.map.builders.hogwarts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapValidator;
import dev.two.slender.map.PageSpot;
import java.util.List;
import org.junit.jupiter.api.Test;

class HogwartsBuilderTest {
    private static ArenaMap arena(HogwartsSections.Section s) {
        HogwartsBuilder b = new HogwartsBuilder(s);
        BlockPos o = b.origin(0);
        return b.arena("slender", 1L, o, o.x() + HogwartsBuilder.DX, o.y() + HogwartsBuilder.DY, o.z() + HogwartsBuilder.DZ);
    }

    /** Game solo pone la verja al acabar una partida: si no se pone al construir, la primera partida sale abierta. */
    @Test
    void gateClosedAtBuild() {
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            List<HogwartsBuilder.Placement> ps = new HogwartsBuilder(s).placements();
            for (BlockPos g : s.gate()) {
                boolean closed = ps.contains(new HogwartsBuilder.Placement(g, s.gateBlock()))
                        || ps.contains(new HogwartsBuilder.Placement(g, s.gateBlock() + "[waterlogged=true]"));
                assertTrue(closed, s.id() + " verja abierta en " + g);
            }
            // la verja va después de los derrumbes y las velas: nada la pisa
            int lastGate = -1;
            for (int i = 0; i < ps.size(); i++) {
                if (s.gate().contains(ps.get(i).pos())) {
                    lastGate = i;
                }
            }
            assertEquals(ps.size() - 1, lastGate, s.id());
        }
    }

    /** fallY de la sección, no el 52 de siempre: quien cae desde un piso alto vuelve a un spawn. */
    @Test
    void fallYIsTheSectionFloor() {
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            ArenaMap m = arena(s);
            int oy = new HogwartsBuilder(s).origin(0).y() + HogwartsBuilder.DY;
            assertEquals(oy + s.fallY(), m.fallY(), s.id());
            assertTrue(MapValidator.playable(m, 8), s.id() + " " + MapValidator.problems(m, 8));
        }
    }

    /** Lo que devuelve arena() coincide con lo que pone build(): páginas en la celda de delante de la pared. */
    @Test
    void pagesInFrontOfTheirWall() {
        HogwartsSections.Section s = HogwartsSections.resource().get(0);
        ArenaMap m = arena(s);
        BlockPos o = new HogwartsBuilder(s).origin(0);
        int ox = o.x() + HogwartsBuilder.DX;
        int oy = o.y() + HogwartsBuilder.DY;
        int oz = o.z() + HogwartsBuilder.DZ;
        for (int i = 0; i < s.pages().size(); i++) {
            PageSpot wall = s.pages().get(i);
            PageSpot p = m.pageSpots().get(i);
            int dx = p.pos().x() - (ox + wall.pos().x());
            int dz = p.pos().z() - (oz + wall.pos().z());
            assertEquals(1, Math.abs(dx) + Math.abs(dz), wall.toString());
            assertEquals(oy + wall.pos().y(), p.pos().y());
            assertEquals(wall.facing(), p.facing());
        }
        Loc sp = m.spawns().get(0);
        assertEquals(ox + s.spawns().get(0).x(), sp.x(), 1e-9);
    }

    /** Las rejas en agua (Mazmorras, Terrenos) van anegadas: si no, secan el túnel y al abrir queda una bolsa de aire. */
    @Test
    void wetGateCellsAreWaterlogged() {
        int wet = 0;
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            List<HogwartsBuilder.Placement> ps = new HogwartsBuilder(s).placements();
            for (int i = 0; i < s.gate().size(); i++) {
                String want = s.gateWet().contains(i) ? s.gateBlock() + "[waterlogged=true]" : s.gateBlock();
                assertTrue(ps.contains(new HogwartsBuilder.Placement(s.gate().get(i), want)), s.id() + " " + i);
            }
            wet += s.gateWet().size();
        }
        assertEquals(7, wet);
    }

    /** Un secciones.json roto no tumba el plugin entero: se avisa y se sigue sin Hogwarts. */
    @Test
    void brokenResourceOnlyDropsHogwarts() {
        java.util.List<String> logged = new java.util.ArrayList<>();
        java.util.logging.Logger log = java.util.logging.Logger.getAnonymousLogger();
        log.setUseParentHandlers(false);
        log.addHandler(new java.util.logging.Handler() {
            @Override public void publish(java.util.logging.LogRecord r) { logged.add(r.getMessage()); }
            @Override public void flush() { }
            @Override public void close() { }
        });
        List<dev.two.slender.map.MapBuilder> out = HogwartsMaps.safely(() -> {
            throw new IllegalStateException("json roto");
        }, log);
        assertTrue(out.isEmpty());
        assertTrue(logged.stream().anyMatch(m -> m.contains("json roto")), logged.toString());
        assertEquals(7, HogwartsMaps.safely(HogwartsMaps::all, log).size());
    }
}
