package dev.two.slender.night;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/** Noches guardadas en plugins/Slender/noches/<id>.yml. Un fichero roto se ignora con aviso. */
public final class NightStore {
    private final File dir;
    private final Logger log;

    public NightStore(File dir, Logger log) {
        this.dir = dir;
        this.log = log;
        if (!dir.exists() && !dir.mkdirs()) {
            log.warning("No se pudo crear " + dir);
        }
    }

    public void save(NightState s) {
        YamlConfiguration y = new YamlConfiguration();
        NightCodec.toMap(s).forEach(y::set);
        try {
            y.save(new File(this.dir, s.id + ".yml"));
        } catch (IOException e) {
            this.log.severe("No se pudo guardar la noche " + s.id + ": " + e.getMessage());
        }
    }

    public Optional<NightState> load(String id) {
        return this.read(new File(this.dir, id + ".yml"));
    }

    /** La noche guardada más reciente en la que está ese jugador. */
    public Optional<NightState> latestFor(UUID member) {
        File[] files = this.dir.listFiles((d, n) -> n.endsWith(".yml"));
        if (files == null) {
            return Optional.empty();
        }
        Optional<NightState> best = Optional.empty();
        for (File f : files) {
            Optional<NightState> s = this.read(f);
            if (s.isPresent() && s.get().members.containsKey(member)
                    && best.map(b -> s.get().updated > b.updated).orElse(true)) {
                best = s;
            }
        }
        return best;
    }

    /** Apunta un miembro nuevo en la noche guardada sin tocar nada más (unirse a mitad de fase). */
    public boolean addMember(String id, UUID member, String name) {
        Optional<NightState> s = this.load(id);
        if (s.isEmpty()) {
            return false;
        }
        s.get().members.putIfAbsent(member, name);
        this.save(s.get());
        return true;
    }

    private File worksFile() {
        return new File(this.dir, "obras.txt");
    }

    /** Lo que ha puesto la fase en curso (mundo y "x y z|estado" por línea), por si el servidor se cae. */
    public void saveWorks(String world, List<String> lines) {
        List<String> all = new ArrayList<>();
        all.add(world);
        all.addAll(lines);
        try {
            Files.write(this.worksFile().toPath(), all, StandardCharsets.UTF_8);
        } catch (IOException e) {
            this.log.warning("No se pudo apuntar " + this.worksFile() + ": " + e.getMessage());
        }
    }

    public Optional<Map.Entry<String, List<String>>> loadWorks() {
        File f = this.worksFile();
        if (!f.isFile()) {
            return Optional.empty();
        }
        try {
            List<String> all = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            if (all.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(Map.entry(all.get(0), List.copyOf(all.subList(1, all.size()))));
        } catch (IOException e) {
            this.log.warning("No se pudo leer " + f + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    public void clearWorks() {
        File f = this.worksFile();
        if (f.exists() && !f.delete()) {
            this.log.warning("No se pudo borrar " + f);
        }
    }

    public void delete(String id) {
        File f = new File(this.dir, id + ".yml");
        if (f.exists() && !f.delete()) {
            this.log.warning("No se pudo borrar " + f);
        }
    }

    private Optional<NightState> read(File f) {
        if (!f.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(NightCodec.fromMap(YamlConfiguration.loadConfiguration(f).getValues(false)));
        } catch (RuntimeException e) {
            this.log.warning("Noche guardada ignorada (" + f.getName() + "): " + e.getMessage());
            return Optional.empty();
        }
    }
}
