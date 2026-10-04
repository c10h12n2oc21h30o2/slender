// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.HashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.LongPredicate;
import java.util.List;

public final class LightDiff
{
    private LightDiff() {
    }
    
    public static long pack(final int n, final int n2, final int n3) {
        return (long)(n & 0x3FFFFFF) << 38 | (long)(n3 & 0x3FFFFFF) << 12 | (long)(n2 & 0xFFF);
    }
    
    public static int unpackX(final long n) {
        return (int)(n >> 38);
    }
    
    public static int unpackY(final long n) {
        return (int)(n << 52 >> 52);
    }
    
    public static int unpackZ(final long n) {
        return (int)(n << 26 >> 38);
    }
    
    public static OptionalLong firstFree(final List<Long> list, final LongPredicate longPredicate, final Set<Long> set) {
        for (final long longValue : list) {
            if (!set.contains(longValue) && longPredicate.test(longValue)) {
                return OptionalLong.of(longValue);
            }
        }
        return OptionalLong.empty();
    }
    
    public static Map<Long, Integer> withoutReserved(final Map<Long, Integer> m, final Set<Long> set) {
        final HashMap hashMap = new HashMap((Map<? extends K, ? extends V>)m);
        hashMap.keySet().removeAll(set);
        return hashMap;
    }
    
    public static Result diff(final Map<Long, Integer> map, final Map<Long, Integer> map2) {
        final HashSet toRestore = new HashSet();
        for (final Long n : map.keySet()) {
            if (!map2.containsKey(n)) {
                toRestore.add(n);
            }
        }
        final HashMap toLight = new HashMap();
        for (final Map.Entry entry : map2.entrySet()) {
            if (!((Integer)entry.getValue()).equals(map.get(entry.getKey()))) {
                toLight.put(entry.getKey(), entry.getValue());
            }
        }
        return new Result(toRestore, toLight);
    }
    
    record Result(Set<Long> toRestore, Map<Long, Integer> toLight) {}
}
