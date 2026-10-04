package dev.two.slender.logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;

public final class IsolationTracker {
    private final int minPlayersExclusive;
    private final double radius;
    private final double threshold;
    private final Map<UUID, Double> seconds = new HashMap();

    public IsolationTracker(int var1, double var2, double var4) {
        this.minPlayersExclusive = var1;
        this.radius = var2;
        this.threshold = var4;
    }

    public void update(Map<UUID, Vec3> var1, double var2) {
        this.seconds.keySet().retainAll(var1.keySet());
        boolean var4 = var1.size() > this.minPlayersExclusive;
        Iterator var5 = var1.entrySet().iterator();

        while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            double var7 = Double.MAX_VALUE;
            Iterator var9 = var1.entrySet().iterator();

            while(var9.hasNext()) {
                Map.Entry var10 = (Map.Entry)var9.next();
                if (!((UUID)var10.getKey()).equals(var6.getKey())) {
                    var7 = Math.min(var7, ((Vec3)var6.getValue()).distance((Vec3)var10.getValue()));
                }
            }

            double var11 = (Double)this.seconds.getOrDefault(var6.getKey(), 0.0);
            this.seconds.put((UUID)var6.getKey(), var4 && var7 > this.radius ? var11 + var2 : Math.max(0.0, var11 - 2.0 * var2));
        }

    }

    public double seconds(UUID var1) {
        return (Double)this.seconds.getOrDefault(var1, 0.0);
    }

    public boolean isIsolated(UUID var1) {
        return this.seconds(var1) >= this.threshold;
    }

    public Optional<UUID> priorityTarget() {
        return this.seconds.entrySet().stream().filter((var1) -> {
            return (Double)var1.getValue() >= this.threshold;
        }).max(Entry.comparingByValue()).map(Map.Entry::getKey);
    }
}
