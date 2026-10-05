package dev.two.slender.night;

import java.io.File;
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
