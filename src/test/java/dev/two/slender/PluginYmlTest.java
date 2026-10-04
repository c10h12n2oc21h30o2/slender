package dev.two.slender;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class PluginYmlTest {
    @Test
    void descriptorPointsAtRealMainClass() throws Exception {
        var in = getClass().getClassLoader().getResourceAsStream("plugin.yml");
        assertNotNull(in);
        YamlConfiguration y = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        assertEquals("Slender", y.getString("name"));
        assertEquals("26.3", y.getString("api-version"));
        assertEquals("dev.two.slender.SlenderPlugin", y.getString("main"));
        assertNotNull(Class.forName(y.getString("main"), false, getClass().getClassLoader()));
        assertNotNull(y.getConfigurationSection("commands.slender"));
        assertEquals("op", y.getString("permissions.slender.admin.default"));
        assertTrue(y.getStringList("loadbefore").contains("Multiverse-Core"));
    }
}
