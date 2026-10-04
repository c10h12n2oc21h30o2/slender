package dev.two.slender.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PhraseDeckTest {
    private static final List<String> TEN = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");

    /** 8 páginas con 10 frases: ninguna se repite en la partida. */
    @Test
    void noRepeatsUntilAllUsed() {
        for (int seed = 0; seed < 200; seed++) {
            PhraseDeck d = new PhraseDeck(TEN, new Random(seed));
            Set<String> seen = new HashSet<>();
            for (int i = 0; i < TEN.size(); i++) {
                seen.add(d.next());
            }
            assertEquals(TEN.size(), seen.size(), "seed " + seed);
        }
    }

    /** Si hay más páginas que frases, al barajar de nuevo no sale dos veces seguidas la misma. */
    @Test
    void noBackToBackAcrossReshuffles() {
        for (int seed = 0; seed < 200; seed++) {
            PhraseDeck d = new PhraseDeck(List.of("x", "y", "z"), new Random(seed));
            String prev = d.next();
            for (int i = 0; i < 30; i++) {
                String cur = d.next();
                assertNotEquals(prev, cur, "seed " + seed);
                prev = cur;
            }
        }
    }

    @Test
    void singlePhraseStillWorks() {
        PhraseDeck d = new PhraseDeck(List.of("SIEMPRE MIRA"), new Random(1));
        assertEquals("SIEMPRE MIRA", d.next());
        assertEquals("SIEMPRE MIRA", d.next());
    }
}
