package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SprintEarTest {
    @Test
    void firesOnceAfterContinuousSprint() {
        SprintEar ear = new SprintEar();
        UUID id = UUID.randomUUID();
        int fired = 0;
        for (int i = 0; i < 20; i++) {
            if (ear.update(id, true, 0.5, 6)) {
                fired++;
            }
        }
        assertEquals(1, fired);
        assertEquals(10.0, ear.seconds(id), 1e-9);
    }

    @Test
    void stoppingResets() {
        SprintEar ear = new SprintEar();
        UUID id = UUID.randomUUID();
        for (int i = 0; i < 11; i++) {
            ear.update(id, true, 0.5, 6);
        }
        assertFalse(ear.update(id, false, 0.5, 6));
        assertEquals(0.0, ear.seconds(id), 1e-9);
        for (int i = 0; i < 11; i++) {
            assertFalse(ear.update(id, true, 0.5, 6));
        }
    }
}
