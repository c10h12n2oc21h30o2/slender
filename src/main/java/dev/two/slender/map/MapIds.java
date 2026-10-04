// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.Set;
import java.util.regex.Pattern;

public final class MapIds
{
    private static final Pattern SLUG;
    private static final Set<String> RESERVED;
    
    private MapIds() {
    }
    
    public static boolean valid(final String input) {
        return input != null && MapIds.SLUG.matcher(input).matches() && !MapIds.RESERVED.contains(input);
    }
    
    static {
        SLUG = Pattern.compile("[a-z0-9_]{2,20}");
        RESERVED = Set.of("all", "vestibulo", "list", "create");
    }
}
