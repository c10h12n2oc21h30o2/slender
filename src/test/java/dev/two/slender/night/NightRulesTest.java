package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NightRulesTest {
    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();

    private NightState state(int phase, int candles) {
        NightState s = NightState.create(this.a, "Ana", new Random(1));
        s.members.put(this.b, "Bea");
        s.phase = phase;
        s.candles = candles;
        return s;
    }

    /** Review Focus 2: si se van todos a mitad de fase no se gasta vela ni se cambia nada. */
    @Test
    void annulledKeepsState() {
        NightState s = state(3, 1);
        s.lost.put(this.b, 2);
        assertEquals(NightRules.Next.ANNULLED, NightRules.afterPhase(s, new PhaseOutcome(true, false, Set.of(), Set.of()), 4));
        assertEquals(3, s.phase);
        assertEquals(1, s.candles);
        assertEquals(Map.of(this.b, 2), s.lost);
    }

    @Test
    void winAdvancesAndRecordsLost() {
        NightState s = state(3, 1);
        s.lost.put(this.a, 2);
        assertEquals(NightRules.Next.NEXT_PHASE,
                NightRules.afterPhase(s, new PhaseOutcome(false, true, Set.of(), Set.of(this.b)), 4));
        assertEquals(4, s.phase);
        assertEquals(Map.of(this.b, 3), s.lost, "los de antes ya cumplieron");
    }

    @Test
    void winningPhaseSevenWinsTheNight() {
        NightState s = state(7, 0);
        assertEquals(NightRules.Next.NIGHT_WON, NightRules.afterPhase(s, new PhaseOutcome(false, true, Set.of(), Set.of()), 4));
    }

    @Test
    void wipeSpendsCandleOrLosesNight() {
        NightState s = state(5, 1);
        s.lost.put(this.b, 4);
        assertEquals(NightRules.Next.REPEAT, NightRules.afterPhase(s, new PhaseOutcome(false, false, Set.of(), Set.of()), 4));
        assertEquals(5, s.phase);
        assertEquals(0, s.candles);
        assertTrue(s.lost.isEmpty(), "al repetir vuelven todos");
        assertEquals(NightRules.Next.NIGHT_LOST, NightRules.afterPhase(s, new PhaseOutcome(false, false, Set.of(), Set.of()), 4));
    }

    @Test
    void spendCandleForLost() {
        NightState s = state(4, 1);
        s.lost.put(this.b, 3);
        assertTrue(NightRules.spendForLost(s, this.b));
        assertTrue(s.lost.isEmpty());
        assertEquals(0, s.candles);
        s.lost.put(this.b, 3);
        assertFalse(NightRules.spendForLost(s, this.b));
        assertFalse(NightRules.spendForLost(s, this.a), "no está perdido");
    }
}
