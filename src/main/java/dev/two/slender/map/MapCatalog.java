// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MapCatalog
{
    private MapCatalog() {
    }
    
    public static List<String> ordered(final List<String> c, final Collection<String> collection) {
        final ArrayList list = new ArrayList((Collection<? extends E>)c);
        final Stream<String> sorted = collection.stream().filter(s -> !list2.contains(s)).sorted();
        final ArrayList obj = list;
        Objects.requireNonNull(obj);
        sorted.forEach(obj::add);
        return list;
    }
}
