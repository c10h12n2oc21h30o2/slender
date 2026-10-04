// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.Optional;
import org.bukkit.configuration.MemorySection;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.io.File;

public final class MapStore
{
    private final File dir;
    private final Logger log;
    
    public MapStore(final File file, final Logger log) {
        this.dir = file;
        this.log = log;
        if (!file.exists() && !file.mkdirs()) {
            log.warning("No se pudo crear " + String.valueOf(file));
        }
    }
    
    public Map<String, ArenaMap> loadAll() {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        final File[] listFiles = this.dir.listFiles((p0, s) -> s.endsWith(".yml"));
        if (listFiles == null) {
            return linkedHashMap;
        }
        final File[] array = listFiles;
        for (int length = array.length, i = 0; i < length; ++i) {
            final File file = array[i];
            try {
                final ArenaMap fromMap = ArenaMap.fromMap(YamlConfiguration.loadConfiguration(file).getValues(false));
                linkedHashMap.put(fromMap.id(), fromMap);
            }
            catch (final RuntimeException ex) {
                this.log.warning("Mapa ignorado (" + file.getName() + "): " + ex.getMessage());
            }
        }
        return linkedHashMap;
    }
    
    public void save(final ArenaMap arenaMap) {
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        final Map<String, Object> map = arenaMap.toMap();
        final MemorySection obj = (MemorySection)yamlConfiguration;
        Objects.requireNonNull((YamlConfiguration)obj);
        map.forEach(obj::set);
        try {
            yamlConfiguration.save(new File(this.dir, arenaMap.id() + ".yml"));
        }
        catch (final IOException ex) {
            this.log.severe("No se pudo guardar el mapa " + arenaMap.id() + ": " + ex.getMessage());
        }
    }
    
    public void delete(final String s) {
        final File obj = new File(this.dir, s + ".yml");
        if (obj.exists() && !obj.delete()) {
            this.log.warning("No se pudo borrar " + String.valueOf(obj));
        }
    }
    
    private File hubFile() {
        return new File(this.dir.getParentFile(), "hub.yml");
    }
    
    public Optional<Hub> loadHub() {
        final File hubFile = this.hubFile();
        if (!hubFile.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Hub.fromMap(YamlConfiguration.loadConfiguration(hubFile).getValues(false)));
        }
        catch (final RuntimeException ex) {
            this.log.warning("hub.yml ignorado: " + ex.getMessage());
            return Optional.empty();
        }
    }
    
    public void saveHub(final Hub hub) {
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        final Map<String, Object> map = hub.toMap();
        final MemorySection obj = (MemorySection)yamlConfiguration;
        Objects.requireNonNull((YamlConfiguration)obj);
        map.forEach(obj::set);
        try {
            yamlConfiguration.save(this.hubFile());
        }
        catch (final IOException ex) {
            this.log.severe("No se pudo guardar hub.yml: " + ex.getMessage());
        }
    }
}
