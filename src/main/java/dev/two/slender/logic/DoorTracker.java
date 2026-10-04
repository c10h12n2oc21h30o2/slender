// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public final class DoorTracker
{
    private final Map<UUID, String> current;
    
    public DoorTracker() {
        this.current = new HashMap<UUID, String>();
    }
    
    public String update(final UUID uuid, final String s) {
        if (s == null) {
            this.current.remove(uuid);
            return null;
        }
        return s.equals(this.current.put(uuid, s)) ? null : s;
    }
    
    public void forget(final UUID uuid) {
        this.current.remove(uuid);
    }
}
