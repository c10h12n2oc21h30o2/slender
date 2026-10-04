// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

public final class EscapeLedger
{
    private final Set<UUID> escaped;
    
    public EscapeLedger() {
        this.escaped = new HashSet<UUID>();
    }
    
    public void escaped(final UUID uuid) {
        this.escaped.add(uuid);
    }
    
    public boolean hasEscaped(final UUID uuid) {
        return this.escaped.contains(uuid);
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
