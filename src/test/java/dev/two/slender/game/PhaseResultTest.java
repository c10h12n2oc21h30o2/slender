package dev.two.slender.game;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Una fase parada por un admin (/slender stop, build, editor) no cuenta (revisión final, hallazgo 4). */
class PhaseResultTest {
    @Test
    void stoppedOrUnfinishedIsAborted() {
        assertFalse(PhaseResult.aborted(true, false));
        assertTrue(PhaseResult.aborted(false, false));
        assertTrue(PhaseResult.aborted(true, true));
        assertTrue(PhaseResult.aborted(false, true));
    }
}
