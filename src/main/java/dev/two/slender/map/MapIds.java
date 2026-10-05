package dev.two.slender.map;

import java.util.Set;
import java.util.regex.Pattern;

public final class MapIds {
    private static final Pattern SLUG = Pattern.compile("[a-z0-9_]{2,20}");
    private static final Set<String> RESERVED = Set.of("all", "vestibulo", "list", "create", "hogwarts", "noche");

    private MapIds() {
    }

    public static boolean valid(String var0) {
        return var0 != null && SLUG.matcher(var0).matches() && !RESERVED.contains(var0);
    }
}
