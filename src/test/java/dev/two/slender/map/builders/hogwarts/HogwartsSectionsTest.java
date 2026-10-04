package dev.two.slender.map.builders.hogwarts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class HogwartsSectionsTest {
    @Test
    void readsSeven() {
        List<HogwartsSections.Section> s = HogwartsSections.resource();
        assertEquals(List.of("hw_mazmorras", "hw_comedor", "hw_ala_este", "hw_patio", "hw_aulas", "hw_torres", "hw_terrenos"),
                s.stream().map(HogwartsSections.Section::id).toList());
        for (int i = 0; i < s.size(); i++) {
            HogwartsSections.Section x = s.get(i);
            assertEquals(i + 1, x.order());
            assertTrue(x.pages().size() >= 12 && x.pages().size() <= 16, x.id());
            assertTrue(x.spawns().size() >= 3, x.id());
            assertFalse(x.gate().isEmpty(), x.id());
            assertFalse(x.seal().isEmpty(), x.id());
            assertEquals(10, x.ambience().phrases().size(), x.id());
        }
    }
}
