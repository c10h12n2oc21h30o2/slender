package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.logic.Vec3;
import java.util.Random;
import org.junit.jupiter.api.Test;

class SnitchMotionTest {
    private static final SnitchMotion.Cells FLAT = (x, y, z) -> y == 0;

    @Test
    void fleesFromNearbyPlayer() {
        for (int seed = 0; seed < 50; seed++) {
            SnitchMotion s = new SnitchMotion(new Vec3(0.5, 1.0, 0.5), new Random(seed));
            Vec3 player = new Vec3(3.5, 1.0, 0.5);
            double before = s.pos().distance(player);
            for (int i = 0; i < 10; i++) {
                s.step(0.1, player, 6, 5.0, FLAT);
            }
            assertTrue(s.pos().distance(player) > before, "seed " + seed);
        }
    }

    @Test
    void staysOnValidCells() {
        SnitchMotion.Cells upToFive = (x, y, z) -> y == 0 && x <= 5;
        for (int seed = 0; seed < 50; seed++) {
            SnitchMotion s = new SnitchMotion(new Vec3(2.5, 1.0, 0.5), new Random(seed));
            for (int i = 0; i < 400; i++) {
                s.step(0.1, null, 6, 5.0, upToFive);
                assertTrue(s.pos().x() < 6.0, "seed " + seed + " x=" + s.pos().x());
            }
        }
    }

    @Test
    void caughtWithinDistance() {
        SnitchMotion s = new SnitchMotion(new Vec3(0, 1, 0), new Random(1));
        assertTrue(s.caught(new Vec3(1.1, 1, 0), 1.2));
        assertFalse(s.caught(new Vec3(1.3, 1, 0), 1.2));
    }
}
