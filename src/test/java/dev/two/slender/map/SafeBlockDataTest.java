package dev.two.slender.map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SafeBlockDataTest {
    @Test
    void fallbackAndWarnOnce() {
        Set<String> warned = new HashSet<>();
        List<String> log = new ArrayList<>();
        java.util.function.Function<String, String> parser = s -> {
            if (s.equals("x")) throw new IllegalArgumentException("no");
            return "ok:" + s;
        };
        assertEquals("piedra", SafeBlockData.resolve("x", parser, "piedra", warned, log::add));
        assertEquals("piedra", SafeBlockData.resolve("x", parser, "piedra", warned, log::add));
        assertEquals("ok:y", SafeBlockData.resolve("y", parser, "piedra", warned, log::add));
        assertEquals(1, log.size());
        assertEquals("Bloque no válido en 26.3, uso piedra: x", log.get(0));
    }
}
