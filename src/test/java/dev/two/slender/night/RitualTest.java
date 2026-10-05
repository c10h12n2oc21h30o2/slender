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

    /** Pendientes 7 y 8: el caldero dice por qué no empieza (espera, ocupado, el dueño ya no está). */
    @Test
    void tryStartExplainsWhy() {
        Ritual r = new Ritual(8, 3, 60);
        java.util.UUID c = java.util.UUID.randomUUID();
        java.util.UUID o = java.util.UUID.randomUUID();
        org.junit.jupiter.api.Assertions.assertEquals(Ritual.Start.OWNER_GONE, r.tryStart(c, o, 0, false));
        org.junit.jupiter.api.Assertions.assertFalse(r.running());
        org.junit.jupiter.api.Assertions.assertEquals(Ritual.Start.STARTED, r.tryStart(c, o, 0, true));
        org.junit.jupiter.api.Assertions.assertEquals(Ritual.Start.BUSY, r.tryStart(c, o, 1, true));
        org.junit.jupiter.api.Assertions.assertEquals(Ritual.Status.DONE, r.tick(8, 1, true));
        org.junit.jupiter.api.Assertions.assertEquals(Ritual.Start.COOLDOWN, r.tryStart(c, o, 20, true));
        org.junit.jupiter.api.Assertions.assertEquals(48, r.cooldownLeft(20), 1e-9);
        org.junit.jupiter.api.Assertions.assertEquals(0, r.cooldownLeft(80), 1e-9);
        org.junit.jupiter.api.Assertions.assertEquals(Ritual.Start.STARTED, r.tryStart(c, o, 68, true));
    }
}
