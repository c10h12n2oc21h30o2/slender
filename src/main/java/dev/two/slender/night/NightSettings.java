package dev.two.slender.night;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.bukkit.configuration.ConfigurationSection;

/** Números de LA NOCHE ENTERA (config.yml → noche:). Lo que falte toma el valor por defecto de la spec. */
public record NightSettings(List<Integer> pagesBase, NavigableMap<Integer, Double> playerFactor, int pagesMin, int pagesMax,
                            int timeBase, int timePerPage, double aggressionPerPhase, double escapePerPhase,
                            double staticPerPhase, int candlesStart, int candlesMax, double frequencyPerPhase, Lights lights,
                            Sprint sprint, Heads heads, Flash flash, Mimic mimic, Chase chase, Altar altar, Snitch snitch) {

    public record Lights(double radius, double minSec, double maxSec, double lookDrain) {
    }

    public record Sprint(double threshold, double dropPerPhase, double distance) {
    }

    public record Heads(double radius, double seconds, double grabSeconds) {
    }

    public record Flash(double cooldown, double chance, double seconds, double staticBump) {
    }

    public record Mimic(double cooldown, double chance, double minDist, double maxDist, double reveal, double duration,
                        double staticBump) {
    }

    public record Chase(double radius, double chance, double speed, double duration, double cooldown) {
    }

    public record Altar(double seconds, double radius, double cooldown, double battery) {
    }

    public record Snitch(double everyMin, double everyMax, double duration, double speed, double flee, double catchDist) {
    }

    public static NightSettings defaults() {
        return from(null);
    }

    public static NightSettings from(ConfigurationSection c) {
        Reader r = new Reader(c);
        TreeMap<Integer, Double> factor = new TreeMap<>();
        ConfigurationSection f = c == null ? null : c.getConfigurationSection("factor-jugadores");
        if (f != null) {
            for (String k : f.getKeys(false)) {
                factor.put(Integer.parseInt(k.trim()), f.getDouble(k));
            }
        }
        if (factor.isEmpty()) {
            factor.put(1, 0.75);
            factor.put(2, 1.0);
            factor.put(4, 1.25);
            factor.put(6, 1.5);
        }
        List<Integer> base = c == null ? List.of() : c.getIntegerList("paginas-base");
        if (base.size() != 7) {
            base = List.of(3, 4, 5, 5, 6, 7, 8);
        }
        return new NightSettings(List.copyOf(new ArrayList<>(base)), java.util.Collections.unmodifiableNavigableMap(factor),
                r.i("paginas-min", 2), r.i("paginas-max", 12), r.i("tiempo-base", 180), r.i("tiempo-por-pagina", 60),
                r.d("agresividad-por-fase", 0.5), r.d("huida-por-fase", 0.3), r.d("estatica-por-fase", 0.08),
                r.i("velas-inicio", 2), r.i("velas-max", 4), r.d("frecuencia-por-fase", 0.25),
                new Lights(r.d("luces.radio", 12), r.d("luces.min", 30), r.d("luces.max", 60), r.d("luces.pila-mirando", 6.0)),
                new Sprint(r.d("correr.umbral", 6.0), r.d("correr.baja-por-fase", 0.5), r.d("correr.distancia", 0.6)),
                new Heads(r.d("cabezas.radio", 8), r.d("cabezas.segundos", 20), r.d("cabezas.coger", 2)),
                new Flash(r.d("fogonazo.enfriamiento", 90), r.d("fogonazo.prob", 0.02), r.d("fogonazo.segundos", 3),
                        r.d("fogonazo.estatica", 15)),
                new Mimic(r.d("suplantar.enfriamiento", 120), r.d("suplantar.prob", 0.03), r.d("suplantar.min", 15),
                        r.d("suplantar.max", 30), r.d("suplantar.revela", 4), r.d("suplantar.dura", 30),
                        r.d("suplantar.estatica", 30)),
                new Chase(r.d("perseguir.radio", 20), r.d("perseguir.prob", 0.35), r.d("perseguir.velocidad", 3.5),
                        r.d("perseguir.dura", 8), r.d("perseguir.enfriamiento", 30)),
                new Altar(r.d("caldero.segundos", 8), r.d("caldero.radio", 3), r.d("caldero.enfriamiento", 60),
                        r.d("caldero.pila", 50)),
                new Snitch(r.d("snitch.cada-min", 120), r.d("snitch.cada-max", 240), r.d("snitch.dura", 45),
                        r.d("snitch.velocidad", 5.0), r.d("snitch.huye", 6), r.d("snitch.atrapa", 1.2)));
    }

    private record Reader(ConfigurationSection c) {
        int i(String k, int def) {
            return this.c == null ? def : this.c.getInt(k, def);
        }

        double d(String k, double def) {
            return this.c == null ? def : this.c.getDouble(k, def);
        }
    }
}
