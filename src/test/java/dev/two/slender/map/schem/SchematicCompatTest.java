package dev.two.slender.map.schem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** El schematic que genera tools/hogwarts lo tiene que leer el lector del plugin. */
class SchematicCompatTest {
    @Test
    void readsHogwartsSchematic() {
        Schematic s = SchematicReader.resource("maps/hogwarts.schem");
        assertEquals(383, s.width());
        assertEquals(255, s.height());
        assertEquals(413, s.length());
        // pared de barrera del lago (clean.contain_lake): cara x = 0, y 0..30
        assertEquals("minecraft:barrier", s.state(0, 15, 200));
        assertEquals("minecraft:bedrock", s.state(183, 0, 194));
    }
}
