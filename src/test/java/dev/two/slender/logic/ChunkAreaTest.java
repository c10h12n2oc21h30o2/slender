package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChunkAreaTest {
    @Test
    void forestBoundsIncludingExitRoadCoverEveryChunk() {
        List<int[]> cs = ChunkArea.chunks(-80, -80, 80, 90);
        assertEquals(11 * 11, cs.size(), "x -5..5, z -5..5 (90 >> 4 = 5)");
        assertTrue(cs.stream().anyMatch(c -> c[0] == -5 && c[1] == -5), "esquina noroeste");
        assertTrue(cs.stream().anyMatch(c -> c[0] == 5 && c[1] == 5), "camino de salida");
    }

    @Test
    void negativeCoordinatesRoundDownToTheirChunk() {
        List<int[]> cs = ChunkArea.chunks(-1, -1, -1, -1);
        assertEquals(1, cs.size());
        assertArrayEquals(new int[]{-1, -1}, cs.get(0));
    }

    @Test
    void secondMapOriginIsOffset() {
        List<int[]> cs = ChunkArea.chunks(2000 - 70, -70, 2000 + 70, 80);
        assertEquals(120, cs.get(0)[0], "1930 >> 4");
        assertEquals(10 * 11, cs.size(), "x 120..129, z -5..5");
    }
}
