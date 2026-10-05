package dev.two.slender.night;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class NightSettingsTest {
    private static YamlConfiguration jarConfig() {
        var in = NightSettingsTest.class.getClassLoader().getResourceAsStream("config.yml");
        assertNotNull(in);
        return YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    @Test
    void defaultsFromSpec() {
        NightSettings d = NightSettings.defaults();
        assertEquals(List.of(3, 4, 5, 5, 6, 7, 8), d.pagesBase());
        assertEquals(0.75, d.playerFactor().floorEntry(1).getValue());
        assertEquals(1.0, d.playerFactor().floorEntry(3).getValue());
        assertEquals(1.25, d.playerFactor().floorEntry(5).getValue());
        assertEquals(1.5, d.playerFactor().floorEntry(9).getValue());
        assertEquals(2, d.pagesMin());
        assertEquals(12, d.pagesMax());
        assertEquals(180, d.timeBase());
        assertEquals(60, d.timePerPage());
        assertEquals(2, d.candlesStart());
        assertEquals(4, d.candlesMax());
        assertEquals(8.0, d.altar().seconds());
        assertEquals(3.5, d.chase().speed());
        assertEquals(1.2, d.snitch().catchDist());
    }

    @Test
    void missingKeysFallBackToDefaults() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("noche.velas-inicio", 3);
        y.set("noche.caldero.segundos", 5);
        NightSettings s = NightSettings.from(y.getConfigurationSection("noche"));
        assertEquals(3, s.candlesStart());
        assertEquals(5.0, s.altar().seconds());
        assertEquals(60.0, s.altar().cooldown());
        assertEquals(NightSettings.defaults().pagesBase(), s.pagesBase());
        assertEquals(NightSettings.defaults(), NightSettings.from(null));
    }

    @Test
    void jarConfigMatchesDefaults() {
        assertEquals(NightSettings.defaults(), NightSettings.from(jarConfig().getConfigurationSection("noche")));
    }

    @Test
    void nightMessagesExist() {
        YamlConfiguration y = jarConfig();
        List<String> keys = List.of("noche-premisa", "noche-diario-1", "noche-diario-2", "noche-diario-3", "noche-diario-4",
                "noche-diario-5", "noche-diario-6", "noche-diario-7", "noche-final-bueno", "noche-final-malo",
                "noche-fase-titulo", "noche-menu-titulo", "noche-menu-nueva", "noche-menu-continuar", "noche-menu-unirse",
                "noche-ocupada", "noche-sin-hogwarts", "noche-esperando", "noche-listos", "noche-vela-gastada",
                "noche-velas", "noche-fase-ganada", "noche-fase-perdida", "noche-te-oigo", "noche-cabeza-robada",
                "noche-cabeza", "noche-ritual", "noche-revivido", "noche-mision-norris", "noche-mision-cromos",
                "noche-mision-braseros", "noche-mision-hecha", "noche-mision-fallida", "noche-snitch", "noche-perdido",
                "noche-estado");
        for (String k : keys) {
            assertNotNull(y.getString("messages." + k), "falta messages." + k);
        }
    }

    /** Pendiente 6: un factor-jugadores mal escrito no rompe el arranque y siempre hay factor para 1 jugador. */
    @Test
    void badPlayerFactorKeysAreSkipped() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("factor-jugadores.dos", 1.0);
        y.set("factor-jugadores.3", 1.2);
        y.set("max-miembros", 6);
        NightSettings s = NightSettings.from(y);
        org.junit.jupiter.api.Assertions.assertFalse(s.playerFactor().containsKey(2));
        org.junit.jupiter.api.Assertions.assertEquals(1.2, s.playerFactor().floorEntry(1).getValue(), 1e-9);
        org.junit.jupiter.api.Assertions.assertEquals(6, s.maxMembers());
        org.junit.jupiter.api.Assertions.assertEquals(8, NightSettings.defaults().maxMembers());
        YamlConfiguration bad = new YamlConfiguration();
        bad.set("factor-jugadores.uno", 2.0);
        org.junit.jupiter.api.Assertions.assertEquals(0.75, NightSettings.from(bad).playerFactor().floorEntry(1).getValue(), 1e-9);
    }
}
