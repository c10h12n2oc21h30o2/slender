package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StaticModelTest {
    private StaticModel model() { return new StaticModel(18, 25, 6, 10); }

    private void run(StaticModel m, double seconds, boolean looking, double dist) {
        for (int i = 0; i < Math.round(seconds / 0.1); i++) m.update(0.1, looking, dist);
    }

    @Test
    void staringAtCloseRangeKillsInAFewSeconds() {
        StaticModel m = model();
        run(m, 2.0, true, 8);
        assertFalse(m.lethal());
        run(m, 2.0, true, 8);
        assertTrue(m.lethal());
        assertEquals(4, m.level());
    }

    @Test
    void decaysWhenNotLookingAndNeverGoesNegative() {
        StaticModel m = model();
        m.bump(50);
        run(m, 2.0, false, 30);
        assertEquals(30, m.value(), 1e-6);
        run(m, 10.0, false, 30);
        assertEquals(0, m.value(), 1e-6);
    }

    @Test
    void proximityRaisesStaticWithoutLooking() {
        StaticModel m = model();
        run(m, 1.0, false, 3);
        assertEquals(12.5, m.value(), 1e-6);
    }

    @Test
    void clampedAtHundredAndLevels() {
        StaticModel m = model();
        m.bump(500);
        assertEquals(100, m.value(), 1e-6);
        m.reset();
        m.bump(20); assertEquals(1, m.level());
        m.bump(25); assertEquals(2, m.level());
        m.bump(25); assertEquals(3, m.level());
    }

    @Test
    void distanceFactorIsBounded() {
        assertEquals(1.8, StaticModel.distanceFactor(0), 1e-9);
        assertEquals(0.4, StaticModel.distanceFactor(200), 1e-9);
        assertEquals(1.6, StaticModel.distanceFactor(8), 1e-9);
    }
}
