package dev.two.slender.night;

import java.util.Set;
import java.util.UUID;

/** Resultado de una fase para las reglas: anulada, ganada, rescatados y perdidos en ella. */
public record PhaseOutcome(boolean aborted, boolean won, Set<UUID> rescued, Set<UUID> lostNow) {
}
