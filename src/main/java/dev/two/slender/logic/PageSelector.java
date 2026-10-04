// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public final class PageSelector
{
    private PageSelector() {
    }
    
    public static <T> List<T> choose(final List<T> c, final int a, final Random rnd) {
        final ArrayList list = new ArrayList((Collection<? extends E>)c);
        Collections.shuffle(list, rnd);
        return (List<T>)List.copyOf((Collection<?>)list.subList(0, Math.min(a, list.size())));
    }
}
