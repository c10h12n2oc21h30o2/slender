package dev.two.slender.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class MenuGuiTest {
    /** Los 4 mapas de siempre + las 7 secciones de Hogwarts + sitio para los del editor. */
    @Test
    void menuFitsAllBuiltInMapsAndEditorOnes() {
        int[] slots = MenuGui.MAP_SLOTS;
        assertTrue(slots.length >= 11 + 7, "solo caben " + slots.length);
        assertEquals(0, MenuGui.SIZE % 9);
        assertTrue(MenuGui.SIZE <= 54);
        assertEquals(slots.length, Arrays.stream(slots).distinct().count());
        assertTrue(Arrays.stream(slots).allMatch(s -> s >= 0 && s < MenuGui.SIZE));
        // sin tocar los bordes, como el menú de antes
        assertTrue(Arrays.stream(slots).allMatch(s -> s % 9 != 0 && s % 9 != 8 && s >= 9 && s < MenuGui.SIZE - 9));
    }
}
