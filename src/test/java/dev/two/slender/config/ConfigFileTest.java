package dev.two.slender.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ConfigFileTest {
    private YamlConfiguration load() {
        var in = getClass().getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(in, "config.yml en el classpath");
        return YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    @Test
    void defaultsMatchSpec() {
        SlenderConfig c = new SlenderConfig(load());
        assertEquals("slender", c.world);
        assertEquals(8, c.pages);
        assertEquals(1, c.minPlayers("coop"));
        assertEquals(8, c.maxPlayers("coop"));
        assertEquals(3, c.minPlayers("asim"));
        assertEquals(9, c.maxPlayers("asim"));
        assertEquals(900, c.timeLimit("coop"));
        assertEquals(600, c.timeLimit("asim"));
        assertEquals(60, c.escapeSeconds);
        assertEquals(2000, c.buildMaxBlocksPerTick);
        assertEquals(15, c.buildMaxMsPerTick);
        assertTrue(c.pagePhrases.size() >= 8);
    }

    @Test
    void everyMessageKeyUsedInCodeExists() {
        YamlConfiguration y = load();
        List<String> keys = List.of("prefix", "no-permission", "players-only", "unknown-map", "map-not-built",
            "already-in-game", "not-in-game", "game-running", "game-full", "mode-forced", "joined", "left",
            "player-joined", "player-left", "countdown", "countdown-cancelled", "start-coop", "start-asim-survivor",
            "start-asim-slender", "page-collected", "page-title", "escape-start", "escaped", "died",
            "win-survivors", "win-slender", "lose-coop", "time-up", "build-start", "build-progress", "build-done",
            "build-busy", "build-game-running", "reload-done", "list-header", "list-line", "stop-done",
            "bossbar-waiting", "bossbar-countdown", "bossbar-playing", "bossbar-escape", "actionbar-survivor",
            "actionbar-slender", "frozen", "cooldown", "teleport-fail", "no-target", "fake-page", "menu-title",
            "mode-title", "state-waiting", "state-running", "state-not-built", "battery-picked", "battery-dead",
            "item-flashlight", "item-teleport", "item-whisper", "item-apparition", "item-blackout", "item-page",
            "item-battery", "mode-coop", "mode-asim", "slender-left", "spectating", "usage", "tp-done", "start-forced",
            "not-enough-players", "static-glitch", "result-escaped", "result-dead");
        for (String k : keys) assertNotNull(y.getString("messages." + k), "falta messages." + k);
    }
}
