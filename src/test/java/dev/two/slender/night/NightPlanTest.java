package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class NightPlanTest {
    private final NightPlan plan = new NightPlan(NightSettings.defaults());

    @Test
    void orderOfTheStory() {
        assertEquals(List.of("hw_terrenos", "hw_comedor", "hw_mazmorras", "hw_ala_este", "hw_patio", "hw_aulas", "hw_torres"),
                NightPlan.ORDER);
        assertEquals("hw_terrenos", NightPlan.sectionOf(1));
        assertEquals("hw_torres", NightPlan.sectionOf(7));
    }

    /** Tabla de la spec §5.2 (14 sitios por sección). */
    @Test
    void pagesTable() {
        int[][] want = {
            {2, 3, 4, 4, 5, 5, 6},      // 1 jugador
            {3, 4, 5, 5, 6, 7, 8},      // 2–3
            {4, 5, 6, 6, 8, 9, 10},     // 4–5
            {5, 6, 8, 8, 9, 11, 12},    // 6+
        };
        int[] players = {1, 2, 4, 6};
        for (int row = 0; row < 4; row++) {
            for (int t = 1; t <= 7; t++) {
                assertEquals(want[row][t - 1], plan.pages(t, players[row], 14), "n=" + players[row] + " t=" + t);
            }
        }
        assertEquals(plan.pages(4, 2, 14), plan.pages(4, 3, 14));
        assertEquals(plan.pages(4, 6, 14), plan.pages(4, 9, 14));
        assertEquals(9, plan.pages(7, 8, 9));
    }

    @Test
    void timeAndPower() {
        assertEquals(360, plan.timeSeconds(3));
        assertEquals(660, plan.timeSeconds(8));
        assertEquals(0.0, plan.aggression(1, 0, 3, 0, false), 1e-9);
        assertEquals(11.0, plan.aggression(7, 3, 3, 0, false), 1e-9);
        assertEquals(11.8, plan.aggression(7, 0, 8, 0, true), 1e-9);
        assertEquals(6.0, plan.aggression(3, 3, 6, 95, false), 1e-9);
        assertEquals(1.48, plan.staticFactor(7), 1e-9);
        assertEquals(1.0, plan.staticFactor(1), 1e-9);
        assertTrue(plan.dormantAllowed(1));
        assertFalse(plan.dormantAllowed(2));
    }

    @Test
    void abilityCalendar() {
        assertTrue(plan.abilities(1).isEmpty());
        assertEquals(EnumSet.of(Ability.LIGHTS), plan.abilities(2));
        assertEquals(EnumSet.allOf(Ability.class), plan.abilities(7));
        assertEquals(1.0, plan.frequency(Ability.LIGHTS, 2), 1e-9);
        assertEquals(1.5, plan.frequency(Ability.LIGHTS, 4), 1e-9);
        assertEquals(0.0, plan.frequency(Ability.CHASE, 6), 1e-9);
        assertEquals(6.0, plan.sprintThreshold(3), 1e-9);
        assertEquals(4.0, plan.sprintThreshold(7), 1e-9);
    }
}
