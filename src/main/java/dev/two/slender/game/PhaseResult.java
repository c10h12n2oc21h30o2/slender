package dev.two.slender.game;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Cómo acabó una fase: quién escapó, quién murió, quién jugaba; aborted = se cortó sin terminar (se fueron todos…). */
public record PhaseResult(Game.Winner winner, boolean aborted, Set<UUID> escaped, Set<UUID> dead, List<UUID> participants) {
}
