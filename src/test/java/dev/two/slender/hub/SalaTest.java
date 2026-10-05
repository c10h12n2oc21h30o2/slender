package dev.two.slender.hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapIds;
import dev.two.slender.map.Zone;
import java.util.List;
import org.junit.jupiter.api.Test;

class SalaTest {
    private static boolean overlap(Zone a, Zone b) {
        return a.min().x() <= b.max().x() && b.min().x() <= a.max().x()
                && a.min().y() <= b.max().y() && b.min().y() <= a.max().y()
                && a.min().z() <= b.max().z() && b.min().z() <= a.max().z();
    }

    private static boolean inside(Zone outer, Zone in) {
        return outer.min().x() <= in.min().x() && in.max().x() <= outer.max().x()
                && outer.min().y() <= in.min().y() && in.max().y() <= outer.max().y()
                && outer.min().z() <= in.min().z() && in.max().z() <= outer.max().z();
    }

    @Test
    void boxIsTheAgreedOne() {
        assertEquals(Zone.of(new BlockPos(-7, 0, 24), new BlockPos(7, 7, 38)), Sala.box());
    }

    @Test
    void zonesInsideBoxApartAndWithinStationRadius() {
        List<Zone> zones = List.of(Sala.readyCircle(), Sala.candleShrine(), Sala.exitDoor());
        for (int i = 0; i < zones.size(); i++) {
            Zone z = zones.get(i);
            assertTrue(inside(Sala.box(), z), "fuera de la sala: " + z);
            assertTrue(Math.abs(z.min().x()) <= HubBuilder.RADIUS && Math.abs(z.max().x()) <= HubBuilder.RADIUS);
            assertTrue(Math.abs(z.min().z()) <= HubBuilder.RADIUS && Math.abs(z.max().z()) <= HubBuilder.RADIUS);
            for (int j = i + 1; j < zones.size(); j++) {
                assertFalse(overlap(z, zones.get(j)), "zonas solapadas: " + z + " / " + zones.get(j));
            }
        }
    }

    @Test
    void salaClearOfArchesAndStationWalls() {
        Zone arches = Zone.of(new BlockPos(-35, -100, 17), new BlockPos(35, 100, 20));
        Zone stationWall = Zone.of(new BlockPos(-35, -100, 20), new BlockPos(35, 100, 21));
        assertFalse(overlap(Sala.box(), arches));
        assertFalse(overlap(Sala.box(), stationWall));
        assertTrue(Sala.box().max().z() <= HubBuilder.RADIUS);
    }

    @Test
    void spawnInsideAndOutOfEveryZone() {
        Loc s = Sala.spawn(new BlockPos(0, 0, 0));
        Zone interior = Zone.of(new BlockPos(-6, 1, 25), new BlockPos(6, 6, 37));
        assertTrue(interior.contains(s.x(), s.y(), s.z()), "spawn fuera: " + s);
        for (Zone z : List.of(Sala.readyCircle(), Sala.candleShrine(), Sala.exitDoor())) {
            assertFalse(z.contains(s.x(), s.y(), s.z()), "spawn dentro de " + z);
        }
        Loc moved = Sala.spawn(new BlockPos(-2000, 64, 0));
        assertEquals(s.x() - 2000, moved.x(), 1e-9);
        assertEquals(s.y() + 64, moved.y(), 1e-9);
    }

    @Test
    void eightBoardSignsOnTheNorthWall() {
        List<BlockPos> signs = Sala.boardSigns();
        assertEquals(8, signs.size());
        assertEquals(8, signs.stream().distinct().count());
        assertTrue(signs.stream().allMatch(p -> p.z() == 25));
        // de arriba abajo y de izquierda a derecha (mirando al norte, la izquierda es −x)
        assertTrue(signs.get(0).y() > signs.get(7).y());
        assertTrue(signs.get(0).x() < signs.get(1).x());
        for (BlockPos p : signs) {
            assertTrue(Sala.box().contains(p.x(), p.y(), p.z()));
        }
    }

    @Test
    void absoluteAddsTheStationOrigin() {
        Zone z = Sala.absolute(Sala.exitDoor());
        assertEquals(Sala.exitDoor().min().x() + HubBuilder.ORIGIN.x(), z.min().x());
        assertEquals(Sala.exitDoor().max().z() + HubBuilder.ORIGIN.z(), z.max().z());
    }

    @Test
    void nocheIsReserved() {
        assertFalse(MapIds.valid("noche"));
    }
}
