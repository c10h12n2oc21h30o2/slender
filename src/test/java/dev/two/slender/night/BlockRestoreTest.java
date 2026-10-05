package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.BlockPos;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Lo que pone una fase en el castillo se deshace dejando lo que había, no aire (revisión final, hallazgo 1). */
class BlockRestoreTest {
    @Test
    void restoresWhatWasThereInReverseOrder() {
        BlockRestore r = new BlockRestore();
        BlockPos feet = new BlockPos(1, 2, 3);
        BlockPos head = new BlockPos(1, 3, 3);
        r.record(feet, "minecraft:oak_stairs[facing=north]");
        r.record(head, "minecraft:air");
        assertEquals(List.of(Map.entry(head, "minecraft:air"), Map.entry(feet, "minecraft:oak_stairs[facing=north]")),
                r.restoreOrder());
    }

    @Test
    void secondPlacementKeepsTheFirstOriginal() {
        BlockRestore r = new BlockRestore();
        BlockPos p = new BlockPos(0, 0, 0);
        r.record(p, "minecraft:ladder[facing=east]");
        r.record(p, "minecraft:red_wool");
        assertEquals(List.of(Map.entry(p, "minecraft:ladder[facing=east]")), r.restoreOrder());
        r.clear();
        assertTrue(r.restoreOrder().isEmpty());
    }
}
