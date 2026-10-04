package dev.two.slender.map;

import org.bukkit.Material;

public interface MapBuilder {
    String id();

    String displayName();

    Material icon();

    int radius();

    ArenaMap build(Builder var1, String var2, long var3);
}
