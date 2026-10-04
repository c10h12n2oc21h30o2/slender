package dev.two.slender.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DoorTracker {
    private final Map<UUID, String> current = new HashMap();

    public String update(UUID var1, String var2) {
        if (var2 == null) {
            this.current.remove(var1);
            return null;
        } else {
            String var3 = (String)this.current.put(var1, var2);
            return var2.equals(var3) ? null : var2;
        }
    }

    public void forget(UUID var1) {
        this.current.remove(var1);
    }
}
