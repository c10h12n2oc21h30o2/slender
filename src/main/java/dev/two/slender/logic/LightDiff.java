package dev.two.slender.logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.LongPredicate;

public final class LightDiff {
    private LightDiff() {
    }

    public static long pack(int var0, int var1, int var2) {
        return (long)(var0 & 67108863) << 38 | (long)(var2 & 67108863) << 12 | (long)(var1 & 4095);
    }

    public static int unpackX(long var0) {
        return (int)(var0 >> 38);
    }

    public static int unpackY(long var0) {
        return (int)(var0 << 52 >> 52);
    }

    public static int unpackZ(long var0) {
        return (int)(var0 << 26 >> 38);
    }

    public static OptionalLong firstFree(List<Long> var0, LongPredicate var1, Set<Long> var2) {
        Iterator var3 = var0.iterator();

        long var4;
        do {
            if (!var3.hasNext()) {
                return OptionalLong.empty();
            }

            var4 = (Long)var3.next();
        } while(var2.contains(var4) || !var1.test(var4));

        return OptionalLong.of(var4);
    }

    public static Map<Long, Integer> withoutReserved(Map<Long, Integer> var0, Set<Long> var1) {
        HashMap var2 = new HashMap(var0);
        var2.keySet().removeAll(var1);
        return var2;
    }

    public static Result diff(Map<Long, Integer> var0, Map<Long, Integer> var1) {
        HashSet var2 = new HashSet();
        Iterator var3 = var0.keySet().iterator();

        while(var3.hasNext()) {
            Long var4 = (Long)var3.next();
            if (!var1.containsKey(var4)) {
                var2.add(var4);
            }
        }

        HashMap var6 = new HashMap();
        Iterator var7 = var1.entrySet().iterator();

        while(var7.hasNext()) {
            Map.Entry var5 = (Map.Entry)var7.next();
            if (!((Integer)var5.getValue()).equals(var0.get(var5.getKey()))) {
                var6.put((Long)var5.getKey(), (Integer)var5.getValue());
            }
        }

        return new Result(var2, var6);
    }

    public static record Result(Set<Long> toRestore, Map<Long, Integer> toLight) {
        public Result(Set<Long> toRestore, Map<Long, Integer> toLight) {
            this.toRestore = toRestore;
            this.toLight = toLight;
        }

        public Set<Long> toRestore() {
            return this.toRestore;
        }

        public Map<Long, Integer> toLight() {
            return this.toLight;
        }
    }
}
