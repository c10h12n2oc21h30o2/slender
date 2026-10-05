package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

/** Pendiente 3: las cabezas y los cromos solo los coge quien juega la fase. */
class NightItemsTest {
    @Test
    void outsidersCannotPickNightItems() {
        assertTrue(HeadItems.blockedForOutsider(Set.of(NightPowers.TAG)));
        assertTrue(HeadItems.blockedForOutsider(Set.of("otra", NightPowers.TAG)));
        assertFalse(HeadItems.blockedForOutsider(Set.of("slender_battery")));
        assertFalse(HeadItems.blockedForOutsider(Set.of()));
    }
}
