package dev.two.slender.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class EscapeLedger {
    private final Set<UUID> escaped = new HashSet();

    public void escaped(UUID var1) {
        this.escaped.add(var1);
    }

    public boolean hasEscaped(UUID var1) {
        return this.escaped.contains(var1);
    }

    public boolean anyEscaped() {
        return !this.escaped.isEmpty();
    }

    public int count() {
        return this.escaped.size();
    }

    public void clear() {
        this.escaped.clear();
    }
}
