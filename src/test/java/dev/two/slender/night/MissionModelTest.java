package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MissionModelTest {
    @Test
    void norrisFailsIfItsPhaseEndsWithoutFinding() {
        MissionModel m = MissionModel.create(MissionType.NORRIS, new Random(3));
        assertEquals(1, m.phases().size());
        int p = m.phases().get(0);
        assertTrue(p >= 2 && p <= 6);
        assertTrue(m.appearsIn(p));
        assertFalse(m.appearsIn(p == 2 ? 3 : 2));
        m.phaseEnded(p);
        assertTrue(m.failed());
        assertFalse(m.done());
        assertFalse(m.appearsIn(p), "fallida: ya no sale");
    }

    @Test
    void norrisDoneWhenFound() {
        MissionModel m = MissionModel.create(MissionType.NORRIS, new Random(4));
        int p = m.phases().get(0);
        m.found(p);
        m.phaseEnded(p);
        assertTrue(m.done());
        assertEquals(1, m.progress());
        assertEquals(1, m.goal());
    }

    @Test
    void cromosNeedFiveOfSeven() {
        MissionModel m = MissionModel.create(MissionType.CROMOS, new Random(1));
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), m.phases());
        assertEquals(5, m.goal());
        for (int t = 1; t <= 5; t++) {
            m.found(t);
            m.phaseEnded(t);
        }
        assertTrue(m.done());
        m.found(5);
        assertEquals(5, m.progress(), "un cromo por fase");
    }

    @Test
    void cromosFailWhenNoLongerReachable() {
        MissionModel m = MissionModel.create(MissionType.CROMOS, new Random(1));
        for (int t = 1; t <= 6; t++) {
            if (t <= 3) {
                m.found(t);
            }
            m.phaseEnded(t);
        }
        assertTrue(m.failed(), "3 cromos y solo queda la fase 7");
    }

    @Test
    void braserosInFourDistinctPhasesFrom2To7() {
        for (int seed = 0; seed < 100; seed++) {
            MissionModel m = MissionModel.create(MissionType.BRASEROS, new Random(seed));
            Set<Integer> ph = new HashSet<>(m.phases());
            assertEquals(4, ph.size());
            assertTrue(ph.stream().allMatch(t -> t >= 2 && t <= 7));
            assertEquals(4, m.goal());
        }
    }

    @Test
    void restoreKeepsProgress() {
        MissionModel m = MissionModel.restore(MissionType.BRASEROS, List.of(2, 3, 5, 7), Set.of(2, 3), false);
        assertEquals(2, m.progress());
        assertTrue(m.appearsIn(5));
        assertFalse(m.appearsIn(2), "ya encendido");
    }
}
