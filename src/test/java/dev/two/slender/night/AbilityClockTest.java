package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AbilityClockTest {
    @Test
    void globalLockOnlyMimicOrChase() {
        AbilityClock c = new AbilityClock();
        assertTrue(c.lock(Ability.MIMIC));
        assertFalse(c.lock(Ability.CHASE));
        assertEquals(Ability.MIMIC, c.locked());
        c.unlock(Ability.MIMIC);
        assertNull(c.locked());
        assertTrue(c.lock(Ability.CHASE));
        assertFalse(c.lock(Ability.LIGHTS));
    }

    @Test
    void cooldownsPerPlayerAndGlobal() {
        AbilityClock c = new AbilityClock();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        assertTrue(c.ready(Ability.FLASH, a, 0));
        c.used(Ability.FLASH, a, 0, 90);
        assertFalse(c.ready(Ability.FLASH, a, 89));
        assertTrue(c.ready(Ability.FLASH, b, 10));
        assertTrue(c.ready(Ability.FLASH, a, 90));
        c.used(Ability.MIMIC, null, 5, 120);
        assertFalse(c.ready(Ability.MIMIC, null, 100));
    }

    @Test
    void rollScalesWithFrequency() {
        assertTrue(AbilityClock.roll(0.02, 1.5, 1.0, 0.029));
        assertFalse(AbilityClock.roll(0.02, 1.5, 1.0, 0.031));
        assertFalse(AbilityClock.roll(0.02, 0.0, 1.0, 0.0));
    }
}
