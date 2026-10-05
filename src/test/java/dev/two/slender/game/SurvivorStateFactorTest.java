package dev.two.slender.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.two.slender.config.SlenderConfig;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class SurvivorStateFactorTest {
    /** En la noche la estática sube un 8 % por fase: con factor 2 sube el doble mirando a Slender. */
    @Test
    void staticFactorScalesBuildUp() {
        var in = getClass().getClassLoader().getResourceAsStream("config.yml");
        SlenderConfig cfg = new SlenderConfig(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
        SurvivorState normal = new SurvivorState(UUID.randomUUID(), cfg);
        SurvivorState night = new SurvivorState(UUID.randomUUID(), cfg, 2.0);
        normal.stat.update(0.1, true, 30);
        night.stat.update(0.1, true, 30);
        assertEquals(2.0 * normal.stat.value(), night.stat.value(), 1e-9);
    }
}
