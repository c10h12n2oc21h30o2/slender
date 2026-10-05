package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Solo los vivos de la fase hacen avanzar la misión (revisión final, hallazgo 3); el premio es una vela, una vez. */
class MissionRulesTest {
    private final UUID alive = UUID.randomUUID();
    private final UUID dead = UUID.randomUUID();

    private NightState norrisIn(int phase) {
        NightState s = NightState.create(this.alive, "Ana", new Random(1));
        s.mission = MissionModel.restore(MissionType.NORRIS, List.of(phase), Set.of(), false);
        return s;
    }

    @Test
    void deadSpectatorsOrEscapedDoNotCount() {
        NightState s = this.norrisIn(3);
        s.phase = 3;
        HeadLedger h = new HeadLedger(List.of(this.alive, this.dead));
        h.died(this.dead);
        assertEquals(MissionRules.Found.IGNORED, MissionRules.find(s, h, this.dead, 4));
        assertEquals(MissionRules.Found.IGNORED, MissionRules.find(s, h, UUID.randomUUID(), 4));
        assertEquals(0, s.mission.progress());
        assertEquals(2, s.candles);
    }

    @Test
    void aliveFindsAndIsRewardedOnce() {
        NightState s = this.norrisIn(3);
        s.phase = 3;
        HeadLedger h = new HeadLedger(List.of(this.alive));
        assertEquals(MissionRules.Found.REWARDED, MissionRules.find(s, h, this.alive, 4));
        assertTrue(s.missionRewarded);
        assertEquals(3, s.candles);
        assertEquals(MissionRules.Found.FOUND, MissionRules.find(s, h, this.alive, 4));
        assertEquals(3, s.candles);
    }

    @Test
    void rewardIsCappedAtMax() {
        NightState s = this.norrisIn(2);
        s.phase = 2;
        s.candles = 4;
        HeadLedger h = new HeadLedger(List.of(this.alive));
        assertEquals(MissionRules.Found.REWARDED, MissionRules.find(s, h, this.alive, 4));
        assertEquals(4, s.candles);
        assertFalse(s.mission.failed());
    }

    /** Pendiente 2: al acabar una fase ganada se sabe si la misión acaba de fallar, para avisar a los jugadores. */
    @Test
    void endPhaseSaysWhenTheMissionJustFailed() {
        NightState s = this.norrisIn(3);
        s.phase = 3;
        assertTrue(MissionRules.endPhase(s));
        assertTrue(s.mission.failed());
        s.phase = 4;
        assertFalse(MissionRules.endPhase(s), "ya estaba fallida");
        NightState t = this.norrisIn(5);
        t.phase = 3;
        assertFalse(MissionRules.endPhase(t), "en esta fase no estaba");
    }
}
