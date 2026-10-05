package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.logic.Vec3;
import java.util.Random;
import org.junit.jupiter.api.Test;

class NightPowersMathTest {
    @Test
    void chaseStepIsSpeedTimesDt() {
        assertEquals(0.35, NightPowers.chaseStep(3.5, 0.1), 1e-9);
    }

    /** El falso compañero aparece a 15–30 bloques, detrás o a un lado de hacia donde mira el objetivo. */
    @Test
    void mimicSpotDistanceAndOutOfView() {
        Vec3 target = new Vec3(100, 64, 100);
        Vec3 view = new Vec3(0, 0, 1);
        for (int seed = 0; seed < 200; seed++) {
            Vec3 p = NightPowers.mimicSpot(target, view, 15, 30, new Random(seed));
            Vec3 d = p.sub(target).horizontal();
            assertTrue(d.length() >= 15 - 1e-9 && d.length() <= 30 + 1e-9, "seed " + seed + " " + d.length());
            assertTrue(d.normalize().dot(view) <= 0.5 + 1e-9, "seed " + seed);
        }
    }
}
