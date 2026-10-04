package dev.two.slender.map;

import org.bukkit.Material;

public interface MapBuilder {
    String id();

    String displayName();

    Material icon();

    int radius();

    ArenaMap build(Builder var1, String var2, long var3);

    /** Origen del mapa en el mundo slender. Por defecto, uno cada 2000 bloques en x (como hasta la 1.7). */
    default BlockPos origin(int index) {
        return new BlockPos(index * 2000, 64, 0);
    }

    /** Caja que se limpia antes de construir, relativa al origen. Por defecto, el cubo de siempre. */
    default Zone clearBox() {
        int r = this.radius();
        return Zone.of(new BlockPos(-r, -12, -r), new BlockPos(r, 60, r));
    }
}
