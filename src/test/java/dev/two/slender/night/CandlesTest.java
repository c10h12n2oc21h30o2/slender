package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CandlesTest {
    @Test
    void wipeSpendsUntilNone() {
        Candles c = new Candles(2, 4);
        assertTrue(c.onWipe());
        assertEquals(1, c.count());
        assertTrue(c.onWipe());
        assertEquals(0, c.count());
        assertFalse(c.onWipe());
        assertEquals(0, c.count());
    }

    @Test
    void gainCapsAtMax() {
        Candles c = new Candles(3, 4);
        assertTrue(c.gain());
        assertFalse(c.gain());
        assertEquals(4, c.count());
    }

    @Test
    void spendForLostNeedsACandle() {
        Candles c = new Candles(1, 4);
        assertTrue(c.spendForLost());
        assertFalse(c.spendForLost());
        assertEquals(0, c.count());
    }
}
