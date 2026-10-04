package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class MazeGeneratorTest {
    @Test
    void perfectMazeHasTreeShapeAndClosedBorder() {
        boolean[][] g = MazeGenerator.generate(10, 8, 0, new Random(1));
        assertEquals(21, g.length);
        assertEquals(17, g[0].length);
        int cells = 80;
        assertEquals(cells + (cells - 1), MazeGenerator.openCount(g));
        for (int x = 0; x < g.length; x++) { assertFalse(g[x][0]); assertFalse(g[x][g[0].length - 1]); }
        for (int z = 0; z < g[0].length; z++) { assertFalse(g[0][z]); assertFalse(g[g.length - 1][z]); }
        for (int cx = 0; cx < 10; cx++) for (int cz = 0; cz < 8; cz++) assertTrue(g[2 * cx + 1][2 * cz + 1]);
        assertTrue(MazeGenerator.isConnected(g));
    }

    @Test
    void braidingAddsLoopsButStaysConnected() {
        boolean[][] perfect = MazeGenerator.generate(12, 12, 0, new Random(9));
        boolean[][] braided = MazeGenerator.generate(12, 12, 0.5, new Random(9));
        assertTrue(MazeGenerator.openCount(braided) > MazeGenerator.openCount(perfect));
        assertTrue(MazeGenerator.isConnected(braided));
    }

    @Test
    void carvingAClearingKeepsConnectivity() {
        boolean[][] g = MazeGenerator.generate(15, 15, 0.1, new Random(3));
        MazeGenerator.carve(g, 10, 10, 16, 16);
        for (int x = 10; x <= 16; x++) for (int z = 10; z <= 16; z++) assertTrue(g[x][z]);
        assertTrue(MazeGenerator.isConnected(g));
    }

    @Test
    void carveIsClampedToGrid() {
        boolean[][] g = MazeGenerator.generate(3, 3, 0, new Random(3));
        MazeGenerator.carve(g, -5, -5, 100, 100);
        assertEquals(25, MazeGenerator.openCount(g), "solo el interior 5x5");
    }
}
