package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Pendiente 1: unirse a mitad de fase guarda al nuevo miembro, pero no lo que va pasando en la fase. */
class NightStoreTest {
    @Test
    void addMemberTouchesOnlyTheSavedMembers(@TempDir Path dir) {
        NightStore store = new NightStore(dir.toFile(), Logger.getAnonymousLogger());
        NightState s = NightState.create(UUID.randomUUID(), "Ana", new Random(4));
        s.phase = 3;
        s.candles = 2;
        store.save(s);
        // durante la fase, en memoria: la Snitch da una vela y se cumple algo de la misión
        s.candles = 3;
        s.snitchCaught = true;
        UUID bea = UUID.randomUUID();
        assertTrue(store.addMember(s.id, bea, "Bea"));
        NightState saved = store.load(s.id).orElseThrow();
        assertEquals("Bea", saved.members.get(bea));
        assertEquals(2, saved.candles);
        assertFalse(saved.snitchCaught);
        assertEquals(3, saved.phase);
        assertFalse(store.addMember("no-existe", bea, "Bea"));
    }

    /** Pendiente 4: lo que ha puesto la fase en curso se apunta en disco hasta que se deshace. */
    @Test
    void worksJournalRoundTrip(@TempDir Path dir) {
        NightStore store = new NightStore(dir.toFile(), Logger.getAnonymousLogger());
        assertTrue(store.loadWorks().isEmpty());
        store.saveWorks("slender", java.util.List.of("1 2 3|minecraft:air", "1 3 3|minecraft:ladder[facing=east]"));
        var w = store.loadWorks().orElseThrow();
        assertEquals("slender", w.getKey());
        assertEquals(java.util.List.of("1 2 3|minecraft:air", "1 3 3|minecraft:ladder[facing=east]"), w.getValue());
        assertTrue(store.latestFor(UUID.randomUUID()).isEmpty(), "no es una noche");
        store.clearWorks();
        assertTrue(store.loadWorks().isEmpty());
    }
}
