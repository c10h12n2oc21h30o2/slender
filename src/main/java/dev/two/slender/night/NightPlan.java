package dev.two.slender.night;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Reglas puras de las 7 fases: páginas, tiempo, poder de Slender y calendario de habilidades (spec §5). */
public final class NightPlan {
    public static final List<String> ORDER = List.of("hw_terrenos", "hw_comedor", "hw_mazmorras", "hw_ala_este", "hw_patio",
            "hw_aulas", "hw_torres");
    private final NightSettings s;

    public NightPlan(NightSettings s) {
        this.s = s;
    }

    public NightSettings settings() {
        return this.s;
    }

    /** id de la sección de la fase (1…7). */
    public static String sectionOf(int phase) {
        return ORDER.get(phase - 1);
    }

    public int pages(int phase, int players, int spots) {
        double factor = this.s.playerFactor().floorEntry(Math.max(1, players)).getValue();
        int p = (int) Math.floor(this.s.pagesBase().get(phase - 1) * factor + 0.5);
        return Math.max(this.s.pagesMin(), Math.min(p, Math.min(this.s.pagesMax(), spots)));
    }

    public int timeSeconds(int pages) {
        return this.s.timeBase() + this.s.timePerPage() * pages;
    }

    public double aggression(int phase, int collected, int total, double secsSincePage, boolean escape) {
        if (escape) {
            return 10.0 + this.s.escapePerPhase() * (phase - 1);
        }
        double progress = total <= 0 ? 0 : 8.0 * collected / total;
        return Math.min(8.0, progress + Math.floor(secsSincePage / 90.0)) + this.s.aggressionPerPhase() * (phase - 1);
    }

    public double staticFactor(int phase) {
        return 1.0 + this.s.staticPerPhase() * (phase - 1);
    }

    public boolean dormantAllowed(int phase) {
        return phase == 1;
    }

    public Set<Ability> abilities(int phase) {
        EnumSet<Ability> out = EnumSet.noneOf(Ability.class);
        for (Ability a : Ability.values()) {
            if (phase >= a.unlock()) {
                out.add(a);
            }
        }
        return out;
    }

    /** Multiplicador de frecuencia de una habilidad en la fase (0 si aún no se ha estrenado). */
    public double frequency(Ability a, int phase) {
        return phase < a.unlock() ? 0.0 : 1.0 + this.s.frequencyPerPhase() * (phase - a.unlock());
    }

    /** Segundos corriendo seguidos para que Slender te oiga. */
    public double sprintThreshold(int phase) {
        return this.s.sprint().threshold() - this.s.sprint().dropPerPhase() * (phase - Ability.SPRINT.unlock());
    }
}
