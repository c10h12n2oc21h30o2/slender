package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NightMenuTest {
    private static final String NEW = "noche-menu-nueva";
    private static final String CONTINUE = "noche-menu-continuar";
    private static final String JOIN = "noche-menu-unirse";

    @Test
    void options() {
        NightState saved = NightState.create(UUID.randomUUID(), "Ana", new Random(1));
        assertEquals(List.of(NEW), NightMenu.options(false, false, Optional.empty()));
        assertEquals(List.of(NEW, CONTINUE), NightMenu.options(false, true, Optional.of(saved)));
        assertEquals(List.of(JOIN), NightMenu.options(true, false, Optional.empty()));
        assertEquals(List.of(JOIN), NightMenu.options(true, true, Optional.of(saved)));
    }

    @Test
    void slotsAreCentred() {
        assertArrayEquals(new int[]{13}, NightMenu.slots(1));
        assertArrayEquals(new int[]{11, 15}, NightMenu.slots(2));
    }

    @Test
    void wrapKeepsSignWidth() {
        assertEquals(List.of("Las barcas han", "vuelto vacías."), NightMenu.wrap("Las barcas han vuelto vacías.", 15));
        assertEquals(List.of("Transforma-", "ciones"), NightMenu.wrap("Transformaciones", 15));
        for (String l : NightMenu.wrap("Ahí abajo el eco devuelve pasos que no son míos. Norris no quiere bajar.", 15)) {
            assertTrue(l.length() <= 15, l);
        }
    }

    @Test
    void boardHasEightSignsOfFourLines() {
        List<String> b = NightMenu.board(3, "Mazmorras", 2, 4, "Encontrad a la Señora Norris", "0/1",
                "Ahí abajo el eco devuelve pasos que no son míos. Norris no quiere bajar.");
        assertEquals(32, b.size());
        assertTrue(b.stream().allMatch(l -> l.length() <= 15), b.toString());
        assertEquals("FASE 3", b.get(0));
        assertEquals("Mazmorras", b.get(1));
        assertEquals("VELAS", b.get(4));
        assertEquals("2 / 4", b.get(5));
        assertEquals("MISIÓN", b.get(8));
        assertEquals("0/1", b.get(12 + 1));
        assertEquals("DIARIO", b.get(16));
    }
}
