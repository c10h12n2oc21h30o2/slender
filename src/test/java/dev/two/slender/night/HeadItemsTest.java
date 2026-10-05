package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HeadItemsTest {
    @Test
    void nameAndKey() {
        assertEquals("Cabeza de Ana", HeadItems.displayName("Ana"));
        assertEquals("head", HeadItems.KEY);
        assertEquals("cromo", HeadItems.CARD_KEY);
        assertEquals("Cromo de rana de chocolate", HeadItems.CARD_NAME);
    }
}
