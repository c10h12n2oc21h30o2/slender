package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Cazar cabezas (spec §6): 20 s expuesta, Slender aparece al lado y tarda 2 s en llevársela. */
class HeadHuntTest {
    private final UUID a = UUID.randomUUID();

    private HeadHunt hunt() {
        return new HeadHunt(20, 2, 3);
    }

    @Test
    void bodyNextToAFreshHeadDoesNotStealIt() {
        HeadHunt h = this.hunt();
        for (int s = 0; s < 19; s++) {
            assertEquals(HeadHunt.Action.NONE, h.step(this.a, false, 1.0, 1.0), "segundo " + s);
        }
    }

    @Test
    void afterExposureItLuresThenStealsInTwoSeconds() {
        HeadHunt h = this.hunt();
        for (int s = 0; s < 19; s++) {
            h.step(this.a, false, 50, 1.0);
        }
        assertEquals(HeadHunt.Action.LURE, h.step(this.a, false, 50, 1.0));
        assertEquals(HeadHunt.Action.NONE, h.step(this.a, false, 2.0, 1.0));
        assertEquals(HeadHunt.Action.STEAL, h.step(this.a, false, 2.0, 1.0));
    }

    @Test
    void walkingAwayOrAGuardResetsTheGrab() {
        HeadHunt h = this.hunt();
        for (int s = 0; s < 20; s++) {
            h.step(this.a, false, 50, 1.0);
        }
        assertEquals(HeadHunt.Action.NONE, h.step(this.a, false, 2.0, 1.0));
        assertEquals(HeadHunt.Action.LURE, h.step(this.a, false, 9.0, 1.0));
        assertEquals(HeadHunt.Action.NONE, h.step(this.a, false, 2.0, 1.0));
        assertEquals(HeadHunt.Action.NONE, h.step(this.a, true, 2.0, 1.0));
        assertEquals(HeadHunt.Action.NONE, h.step(this.a, false, 2.0, 1.0), "la guardia reinicia los 20 s");
    }

    @Test
    void noBodyMeansInfiniteDistance() {
        HeadHunt h = this.hunt();
        for (int s = 0; s < 19; s++) {
            h.step(this.a, false, Double.POSITIVE_INFINITY, 1.0);
        }
        assertEquals(HeadHunt.Action.LURE, h.step(this.a, false, Double.POSITIVE_INFINITY, 1.0));
    }
}
