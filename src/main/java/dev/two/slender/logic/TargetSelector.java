// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.Iterator;
import java.util.UUID;
import java.util.Optional;
import java.util.Random;
import java.util.List;

public final class TargetSelector
{
    private TargetSelector() {
    }
    
    public static double weight(final Candidate candidate) {
        final double n = 1.0 + 0.2 * candidate.pages() + (candidate.flashlightOn() ? 0.5 : 0.0);
        return candidate.isolated() ? (n * 5.0) : n;
    }
    
    public static Optional<UUID> pick(final List<Candidate> list, final Random random) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        double n = 0.0;
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            n += weight((Candidate)iterator.next());
        }
        double n2 = random.nextDouble() * n;
        for (final Candidate candidate : list) {
            n2 -= weight(candidate);
            if (n2 < 0.0) {
                return Optional.of(candidate.id());
            }
        }
        return Optional.of(list.get(list.size() - 1).id());
    }
    
    public static boolean shouldRetarget(final UUID obj, final boolean b, final double n, final Optional<UUID> optional) {
        return obj == null || !b || (optional.isPresent() && !optional.get().equals(obj)) || n >= 20.0;
    }
    
    record Candidate(UUID id, int pages, boolean flashlightOn, boolean isolated) {}
}
