package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.BlockPos;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Test;

class NightSpotsTest {
    @Test
    void onlyReturnsCellsThatPass() {
        Random r = new Random(3);
        for (int i = 0; i < 50; i++) {
            Optional<BlockPos> p = NightSpots.pick(100, k -> new BlockPos(k, 0, 0), c -> c.x() % 10 == 7, r, 200);
            assertTrue(p.isPresent());
            assertEquals(7, p.get().x() % 10);
        }
    }

    @Test
    void emptyWhenNothingPasses() {
        assertTrue(NightSpots.pick(100, k -> new BlockPos(k, 0, 0), c -> false, new Random(1), 200).isEmpty());
        assertTrue(NightSpots.pick(0, k -> new BlockPos(k, 0, 0), c -> true, new Random(1), 200).isEmpty());
    }
}
