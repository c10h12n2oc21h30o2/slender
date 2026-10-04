package dev.two.slender.player;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SnapshotCodecTest {
    private PlayerSnapshot sample() {
        return new PlayerSnapshot("AAEC", 30, 0.25f, 17.5, 14, 3.5f, "SURVIVAL", 0.2f, 0.1f, true,
            List.of("minecraft:speed;600;1;false;true;true"), "world,10.5,70.0,-3.5,90.0,10.0", 10, 8);
    }

    @Test
    void roundTripPlainMap() {
        assertEquals(sample(), SnapshotCodec.fromMap(SnapshotCodec.toMap(sample())));
    }

    @Test
    void roundTripThroughYamlText() {
        YamlConfiguration y = new YamlConfiguration();
        SnapshotCodec.toMap(sample()).forEach(y::set);
        YamlConfiguration back = new YamlConfiguration();
        assertDoesNotThrow(() -> back.loadFromString(y.saveToString()));
        assertEquals(sample(), SnapshotCodec.fromMap(back.getValues(false)));
    }

    @Test
    void missingOptionalFieldsUseDefaults() {
        var m = SnapshotCodec.toMap(sample());
        m.remove("effects");
        m.remove("view-distance");
        m.remove("send-view-distance");
        PlayerSnapshot s = SnapshotCodec.fromMap(m);
        assertEquals(List.of(), s.effects());
        assertEquals(-1, s.viewDistance());
    }
}
