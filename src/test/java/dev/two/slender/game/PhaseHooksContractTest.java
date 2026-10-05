package dev.two.slender.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PhaseHooksContractTest {
    /** Solo 3 métodos obligatorios; el resto no hace nada por defecto (partida normal). */
    @Test
    void onlyThreeRequired() {
        PhaseHooks h = new PhaseHooks() {
            @Override
            public int pages(int spots) {
                return 3;
            }

            @Override
            public int timeSeconds(int pages) {
                return 360;
            }

            @Override
            public double aggression(int collected, int total, double secsSincePage, boolean escape) {
                return 1.0;
            }
        };
        assertEquals(3, h.pages(14));
        assertTrue(h.dormantAllowed());
        assertEquals(1.0, h.staticFactor());
        assertFalse(h.startsAsSpectator(UUID.randomUUID()));
        assertFalse(h.onInteractBlock(null, null, null));
        assertEquals(PhaseHooks.Pickup.IGNORE, h.onPickup(null, null, null));
        assertFalse(h.onInteractEntity(null, null, null));
    }

    @Test
    void resultKeepsSets() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        PhaseResult r = new PhaseResult(Game.Winner.SURVIVORS, false, Set.of(a), Set.of(b), List.of(a, b));
        assertEquals(Set.of(a), r.escaped());
        assertEquals(Set.of(b), r.dead());
        assertEquals(List.of(a, b), r.participants());
        assertFalse(r.aborted());
    }
}
