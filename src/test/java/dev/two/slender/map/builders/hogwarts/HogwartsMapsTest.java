package dev.two.slender.map.builders.hogwarts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.command.SlenderCommand;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.MapIds;
import dev.two.slender.map.Zone;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class HogwartsMapsTest {
    record Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        boolean overlaps(Box o) {
            return x1 <= o.x2 && o.x1 <= x2 && y1 <= o.y2 && o.y1 <= y2 && z1 <= o.z2 && o.z1 <= z2;
        }
    }

    static Box abs(BlockPos origin, Zone z) {
        return new Box(origin.x() + z.min().x(), origin.y() + z.min().y(), origin.z() + z.min().z(),
                origin.x() + z.max().x(), origin.y() + z.max().y(), origin.z() + z.max().z());
    }

    @Test
    void originsAndBoxesDontOverlap() {
        List<MapBuilder> hw = HogwartsMaps.all();
        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < hw.size(); i++) {
            boxes.add(abs(hw.get(i).origin(4 + i), hw.get(i).clearBox()));
        }
        for (int i = 0; i < boxes.size(); i++) {
            for (int j = i + 1; j < boxes.size(); j++) {
                assertFalse(boxes.get(i).overlaps(boxes.get(j)), hw.get(i).id() + " pisa " + hw.get(j).id());
            }
            for (int k = 0; k < 4; k++) {   // bosque, sanatorio, maizal, instituto (radio ≤ 232)
                Box old = new Box(k * 2000 - 232, 52, -232, k * 2000 + 232, 124, 232);
                assertFalse(boxes.get(i).overlaps(old), hw.get(i).id() + " pisa un mapa viejo");
            }
            Box hub = new Box(-2041, 52, -41, -1959, 124, 41);
            assertFalse(boxes.get(i).overlaps(hub), hw.get(i).id() + " pisa la estación");
        }
        assertEquals(new BlockPos(0, 64, 6000), hw.get(0).origin(4));
        assertEquals(new BlockPos(6000, 64, 6000), hw.get(6).origin(10));
    }

    @Test
    void idsValidAndReserved() {
        for (String id : HogwartsMaps.IDS) {
            assertTrue(MapIds.valid(id), id);
        }
        assertFalse(MapIds.valid("hogwarts"));
    }

    @Test
    void buildAliasExpands() {
        List<String> all = new ArrayList<>(List.of("bosque", "sanatorio", "maizal", "instituto"));
        all.addAll(HogwartsMaps.IDS);
        assertEquals(HogwartsMaps.IDS, SlenderCommand.expandBuildTargets("hogwarts", all));
        List<String> expectAll = new ArrayList<>(all);
        expectAll.add("vestibulo");
        assertEquals(expectAll, SlenderCommand.expandBuildTargets("all", all));
        assertEquals(List.of("bosque"), SlenderCommand.expandBuildTargets("bosque", all));
    }
}
