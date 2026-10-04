// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.function.Function;
import java.util.Comparator;
import java.util.Optional;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public final class IsolationTracker
{
    private final int minPlayersExclusive;
    private final double radius;
    private final double threshold;
    private final Map<UUID, Double> seconds;
    
    public IsolationTracker(final int minPlayersExclusive, final double radius, final double threshold) {
        this.seconds = new HashMap<UUID, Double>();
        this.minPlayersExclusive = minPlayersExclusive;
        this.radius = radius;
        this.threshold = threshold;
    }
    
    public void update(final Map<UUID, Vec3> map, final double n) {
        this.seconds.keySet().retainAll(map.keySet());
        final boolean b = map.size() > this.minPlayersExclusive;
        for (final Map.Entry entry : map.entrySet()) {
            double min = Double.MAX_VALUE;
            for (final Map.Entry entry2 : map.entrySet()) {
                if (((UUID)entry2.getKey()).equals(entry.getKey())) {
                    continue;
                }
                min = Math.min(min, ((Vec3)entry.getValue()).distance((Vec3)entry2.getValue()));
            }
            final double doubleValue = this.seconds.getOrDefault(entry.getKey(), 0.0);
            this.seconds.put((UUID)entry.getKey(), (b && min > this.radius) ? (doubleValue + n) : Math.max(0.0, doubleValue - 2.0 * n));
        }
    }
    
    public double seconds(final UUID key) {
        return this.seconds.getOrDefault(key, 0.0);
    }
    
    public boolean isIsolated(final UUID uuid) {
        return this.seconds(uuid) >= this.threshold;
    }
    
    public Optional<UUID> priorityTarget() {
        return this.seconds.entrySet().stream().filter(entry -> entry.getValue() >= this.threshold).max((Comparator<? super Object>)Map.Entry.comparingByValue()).map((Function<? super Object, ? extends UUID>)Map.Entry::getKey);
    }
}
