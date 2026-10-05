package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class HeadLedgerTest {
    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();
    private final UUID c = UUID.randomUUID();

    private HeadLedger ledger() {
        return new HeadLedger(List.of(this.a, this.b, this.c));
    }

    @Test
    void diedDropsOwnAndCarried() {
        HeadLedger h = ledger();
        assertEquals(List.of(this.b), h.died(this.b));
        assertTrue(h.pickUp(this.b, this.a));
        assertEquals(Set.of(this.b), h.carriedBy(this.a));
        List<UUID> dropped = h.died(this.a);
        assertEquals(Set.of(this.a, this.b), Set.copyOf(dropped));
        assertEquals(Set.of(this.a, this.b), h.onGround());
        assertTrue(h.carriedBy(this.a).isEmpty());
    }

    /** Review Focus 5: solo los vivos de la fase cogen cabezas, y solo las que están en el suelo. */
    @Test
    void onlyAliveCanPickUp() {
        HeadLedger h = ledger();
        h.died(this.b);
        h.died(this.c);
        assertFalse(h.pickUp(this.b, this.c), "un muerto no coge");
        assertFalse(h.pickUp(this.a, this.c), "no hay cabeza de un vivo");
        h.escaped(this.a);
        assertFalse(h.pickUp(this.b, this.a), "un escapado no coge");
        assertFalse(h.pickUp(this.b, UUID.randomUUID()), "uno de fuera no coge");
    }

    @Test
    void escapeRescues() {
        HeadLedger h = ledger();
        h.died(this.b);
        h.pickUp(this.b, this.a);
        assertEquals(List.of(this.b), h.escaped(this.a));
        assertTrue(h.rescued().contains(this.b));
        assertFalse(h.lostAtEnd().contains(this.b));
    }

    @Test
    void stolenIsLost() {
        HeadLedger h = ledger();
        h.died(this.b);
        h.stolen(this.b);
        assertTrue(h.lostAtEnd().contains(this.b));
        assertFalse(h.onGround().contains(this.b));
        assertFalse(h.pickUp(this.b, this.a));
    }

    @Test
    void reviveThenDieAgain() {
        HeadLedger h = ledger();
        h.died(this.b);
        h.pickUp(this.b, this.a);
        h.revived(this.b);
        assertTrue(h.alive(this.b));
        assertTrue(h.carriedBy(this.a).isEmpty());
        assertFalse(h.lostAtEnd().contains(this.b));
        h.died(this.b);
        assertTrue(h.onGround().contains(this.b));
        assertTrue(h.lostAtEnd().contains(this.b), "sin recoger al acabar, perdido");
    }
}
