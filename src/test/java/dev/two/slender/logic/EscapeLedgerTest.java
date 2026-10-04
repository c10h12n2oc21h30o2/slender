package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EscapeLedgerTest {
    @Test
    void anEscapeCountsForTheWholeRoundEvenIfThePlayerLeavesAfterwards() {
        EscapeLedger l = new EscapeLedger();
        assertFalse(l.anyEscaped());
        UUID a = UUID.randomUUID();
        l.escaped(a);
        // No hay forma de "des-escapar": salir de la partida no borra la huida.
        assertTrue(l.anyEscaped());
        assertTrue(l.hasEscaped(a));
        assertEquals(1, l.count());
    }

    @Test
    void clearStartsANewRound() {
        EscapeLedger l = new EscapeLedger();
        l.escaped(UUID.randomUUID());
        l.clear();
        assertFalse(l.anyEscaped());
        assertEquals(0, l.count());
    }
}
