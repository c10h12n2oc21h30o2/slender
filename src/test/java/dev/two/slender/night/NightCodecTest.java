package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NightCodecTest {
    private static NightState full() {
        UUID a = UUID.fromString("00000000-0000-0000-0000-00000000000a");
        UUID b = UUID.fromString("00000000-0000-0000-0000-00000000000b");
        UUID c = UUID.fromString("00000000-0000-0000-0000-00000000000c");
        NightState s = NightState.create(a, "Ana", new Random(5));
        s.members.put(b, "Bea");
        s.members.put(c, "Cris");
        s.phase = 4;
        s.candles = 3;
        s.mission = MissionModel.restore(MissionType.BRASEROS, List.of(2, 3, 5, 7), Set.of(2, 3), false);
        s.missionRewarded = false;
        s.snitchPhase = 6;
        s.snitchCaught = true;
        s.lost.put(b, 3);
        s.lost.put(c, 3);
        s.updated = 1234L;
        return s;
    }

    @Test
    void roundTrip() {
        NightState s = full();
        NightState t = NightCodec.fromMap(NightCodec.toMap(s));
        assertEquals(s.id, t.id);
        assertEquals(s.members, t.members);
        assertEquals(4, t.phase);
        assertEquals(3, t.candles);
        assertEquals(MissionType.BRASEROS, t.mission.type());
        assertEquals(List.of(2, 3, 5, 7), t.mission.phases());
        assertEquals(Set.of(2, 3), t.mission.foundIn());
        assertEquals(6, t.snitchPhase);
        assertTrue(t.snitchCaught);
        assertEquals(s.lost, t.lost);
        assertEquals(s.created, t.created);
        assertEquals(1234L, t.updated);
    }

    @Test
    void createPicksMissionAndSnitchPhase() {
        for (int seed = 0; seed < 50; seed++) {
            NightState s = NightState.create(UUID.randomUUID(), "x", new Random(seed));
            assertEquals(1, s.phase);
            assertEquals(1, s.members.size());
            assertTrue(s.snitchPhase >= 3 && s.snitchPhase <= 7);
        }
    }

    @Test
    void emptyMapThrows() {
        assertThrows(IllegalArgumentException.class, () -> NightCodec.fromMap(Map.of()));
    }

    @Test
    void storeLatestForMemberAndIgnoresCorrupt(@TempDir Path dir) throws Exception {
        NightStore store = new NightStore(dir.toFile(), Logger.getAnonymousLogger());
        NightState old = full();
        old.updated = 100;
        store.save(old);
        NightState newer = NightState.create(UUID.fromString("00000000-0000-0000-0000-00000000000a"), "Ana", new Random(9));
        newer.updated = 200;
        store.save(newer);
        Files.writeString(dir.resolve("roto.yml"), "esto: [no es: un estado");
        assertEquals(newer.id, store.latestFor(UUID.fromString("00000000-0000-0000-0000-00000000000a")).orElseThrow().id);
        assertEquals(old.id, store.latestFor(UUID.fromString("00000000-0000-0000-0000-00000000000b")).orElseThrow().id);
        store.delete(newer.id);
        assertTrue(store.load(newer.id).isEmpty());
        assertTrue(new File(dir.toFile(), old.id + ".yml").exists());
    }
}
