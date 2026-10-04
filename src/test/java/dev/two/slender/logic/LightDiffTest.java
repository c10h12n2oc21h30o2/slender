package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LightDiffTest {
    @Test
    void packRoundTripsNegativeCoordinates() {
        int[][] pts = {{0, 0, 0}, {-1, -64, -1}, {4000, 319, -30000}, {-2000, 65, 123}};
        for (int[] p : pts) {
            long k = LightDiff.pack(p[0], p[1], p[2]);
            assertEquals(p[0], LightDiff.unpackX(k));
            assertEquals(p[1], LightDiff.unpackY(k));
            assertEquals(p[2], LightDiff.unpackZ(k));
        }
    }

    @Test
    void movingLightRestoresOldAndLightsNew() {
        long a = LightDiff.pack(1, 65, 1), b = LightDiff.pack(2, 65, 1), head = LightDiff.pack(0, 66, 0);
        LightDiff.Result r = LightDiff.diff(Map.of(a, 15, head, 8), Map.of(b, 15, head, 8));
        assertEquals(Set.of(a), r.toRestore());
        assertEquals(Map.of(b, 15), r.toLight());
    }

    @Test
    void turningOffRestoresEverything() {
        long a = LightDiff.pack(1, 65, 1), head = LightDiff.pack(0, 66, 0);
        LightDiff.Result r = LightDiff.diff(Map.of(a, 15, head, 8), Map.of());
        assertEquals(Set.of(a, head), r.toRestore());
        assertTrue(r.toLight().isEmpty());
    }

    @Test
    void levelChangeIsResentAndNoChangeIsEmpty() {
        long a = LightDiff.pack(1, 65, 1);
        assertEquals(Map.of(a, 11), LightDiff.diff(Map.of(a, 15), Map.of(a, 11)).toLight());
        LightDiff.Result same = LightDiff.diff(Map.of(a, 15), Map.of(a, 15));
        assertTrue(same.toLight().isEmpty() && same.toRestore().isEmpty());
    }
}
