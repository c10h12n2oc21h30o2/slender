package dev.two.slender.game;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Cómo acabó una fase: quién escapó, quién murió, quién jugaba; aborted = se cortó sin terminar (se fueron todos…). */
public record PhaseResult(Game.Winner winner, boolean aborted, Set<UUID> escaped, Set<UUID> dead, List<UUID> participants) {
    /** Una fase no cuenta si no llegó a acabar o si la paró alguien (/slender stop, build, editor, recarga). */
    public static boolean aborted(boolean reachedEnd, boolean stopped) {
        return !reachedEnd || stopped;
    }
}
