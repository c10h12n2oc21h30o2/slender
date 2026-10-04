package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SurvivorModelsTest {
    @Test
    void sprintingEightSecondsExhaustsThenNeedsThirtyPercent() {
        StaminaModel s = new StaminaModel(12.5, 8, 30);
        for (int i = 0; i < 80; i++) s.update(0.1, true);
        assertFalse(s.canSprint());
        for (int i = 0; i < 30; i++) s.update(0.1, false);
        assertFalse(s.canSprint(), "24 % no basta");
        for (int i = 0; i < 10; i++) s.update(0.1, false);
        assertTrue(s.canSprint());
    }

    @Test
    void sprintingWhileExhaustedDoesNotDrainBelowZero() {
        StaminaModel s = new StaminaModel(12.5, 8, 30);
        for (int i = 0; i < 200; i++) s.update(0.1, true);
        assertTrue(s.value() >= 0);
        assertEquals(s.value() / 100.0, s.fraction(), 1e-9);
    }

    @Test
    void batteryLastsAbout150SecondsAndChargesCapped() {
        BatteryModel b = new BatteryModel(1 / 1.5);
        b.drain(149);
        assertFalse(b.empty());
        b.drain(2);
        assertTrue(b.empty());
        assertEquals(0, b.level(), 1e-9);
        b.charge(35);
        assertEquals(35, b.level(), 1e-9);
        b.charge(200);
        assertEquals(100, b.level(), 1e-9);
    }

    @Test
    void batteryLowBelowTwenty() {
        BatteryModel b = new BatteryModel(1);
        b.set(20);
        assertFalse(b.low());
        b.drain(0.5);
        assertTrue(b.low());
    }
}
