package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class RitualTest {
    private final UUID carrier = UUID.randomUUID();
    private final UUID owner = UUID.randomUUID();

    @Test
    void completesAfterSecondsThenCoolsDown() {
        Ritual r = new Ritual(8, 3, 60);
        assertEquals(Ritual.Status.IDLE, r.tick(0, 0, true));
        assertTrue(r.start(this.carrier, this.owner, 10));
        assertFalse(r.start(this.carrier, this.owner, 11), "ya hay uno en curso");
        assertEquals(Ritual.Status.RUNNING, r.tick(14, 1, true));
        assertEquals(0.5, r.progress(14), 1e-9);
        assertEquals(Ritual.Status.DONE, r.tick(18, 1, true));
        assertEquals(this.owner, r.owner());
        assertFalse(r.start(this.carrier, this.owner, 77), "enfriándose");
        assertTrue(r.start(this.carrier, this.owner, 78));
    }

    @Test
    void cancelledByDistanceOrDeath() {
        Ritual r = new Ritual(8, 3, 60);
        r.start(this.carrier, this.owner, 0);
        assertEquals(Ritual.Status.CANCELLED, r.tick(2, 3.5, true));
        assertTrue(r.start(this.carrier, this.owner, 3), "cortar no enfría");
        assertEquals(Ritual.Status.CANCELLED, r.tick(4, 1, false));
    }
}
