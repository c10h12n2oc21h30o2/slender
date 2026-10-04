package dev.two.slender.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class MapCatalog {
    private MapCatalog() {
    }

    public static List<String> ordered(List<String> var0, Collection<String> var1) {
        ArrayList var2 = new ArrayList(var0);
        Stream var10000 = var1.stream().filter((var1x) -> {
            return !var0.contains(var1x);
        }).sorted();
        Objects.requireNonNull(var2);
        var10000.forEach(var2::add);
        return var2;
    }
}
