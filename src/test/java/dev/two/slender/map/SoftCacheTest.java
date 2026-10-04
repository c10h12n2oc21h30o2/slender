package dev.two.slender.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.builders.hogwarts.HogwartsMaps;
import dev.two.slender.map.schem.Schematic;
import dev.two.slender.map.schem.SchematicExtras;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SoftCacheTest {
    @Test
    void loadsOnceAndReloadsAfterTheGcTakesIt() {
        AtomicInteger loads = new AtomicInteger();
        SoftCache<Object> c = new SoftCache<>(() -> {
            loads.incrementAndGet();
            return new Object();
        });
        Object a = c.get();
        assertSame(a, c.get());
        assertEquals(1, loads.get());
        c.forget();
        c.get();
        assertEquals(2, loads.get());
    }

    /** El castillo leído son ~160 MB: no puede quedarse en memoria para siempre después de construir. */
    @Test
    void hogwartsDoesNotPinTheSchematic() {
        for (Field f : HogwartsMaps.class.getDeclaredFields()) {
            boolean big = f.getType() == Schematic.class || f.getType() == SchematicExtras.class;
            assertTrue(!(Modifier.isStatic(f.getModifiers()) && big), "referencia fija: " + f.getName());
        }
        assertTrue(Arrays.stream(HogwartsMaps.class.getDeclaredFields()).anyMatch(f -> f.getType() == SoftCache.class));
    }
}
