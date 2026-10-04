package dev.two.slender.map;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

public final class MapStore {
    private final File dir;
    private final Logger log;

    public MapStore(File var1, Logger var2) {
        this.dir = var1;
        this.log = var2;
        if (!var1.exists() && !var1.mkdirs()) {
            var2.warning("No se pudo crear " + String.valueOf(var1));
        }

    }

    public Map<String, ArenaMap> loadAll() {
        LinkedHashMap var1 = new LinkedHashMap();
        File[] var2 = this.dir.listFiles((var0, var1x) -> {
            return var1x.endsWith(".yml");
        });
        if (var2 == null) {
            return var1;
        } else {
            File[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File var6 = var3[var5];

                try {
                    YamlConfiguration var7 = YamlConfiguration.loadConfiguration(var6);
                    ArenaMap var8 = ArenaMap.fromMap(var7.getValues(false));
                    var1.put(var8.id(), var8);
                } catch (RuntimeException var9) {
                    Logger var10000 = this.log;
                    String var10001 = var6.getName();
                    var10000.warning("Mapa ignorado (" + var10001 + "): " + var9.getMessage());
                }
            }

            return var1;
        }
    }

    public void save(ArenaMap var1) {
        YamlConfiguration var2 = new YamlConfiguration();
        Map<String, Object> var10000 = var1.toMap();
        var10000.forEach(var2::set);

        try {
            var2.save(new File(this.dir, var1.id() + ".yml"));
        } catch (IOException var4) {
            Logger var5 = this.log;
            String var10001 = var1.id();
            var5.severe("No se pudo guardar el mapa " + var10001 + ": " + var4.getMessage());
        }

    }

    public void delete(String var1) {
        File var2 = new File(this.dir, var1 + ".yml");
        if (var2.exists() && !var2.delete()) {
            this.log.warning("No se pudo borrar " + String.valueOf(var2));
        }

    }

    private File hubFile() {
        return new File(this.dir.getParentFile(), "hub.yml");
    }

    public Optional<Hub> loadHub() {
        File var1 = this.hubFile();
        if (!var1.exists()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(Hub.fromMap(YamlConfiguration.loadConfiguration(var1).getValues(false)));
            } catch (RuntimeException var3) {
                this.log.warning("hub.yml ignorado: " + var3.getMessage());
                return Optional.empty();
            }
        }
    }

    public void saveHub(Hub var1) {
        YamlConfiguration var2 = new YamlConfiguration();
        Map<String, Object> var10000 = var1.toMap();
        var10000.forEach(var2::set);

        try {
            var2.save(this.hubFile());
        } catch (IOException var4) {
            this.log.severe("No se pudo guardar hub.yml: " + var4.getMessage());
        }

    }
}
