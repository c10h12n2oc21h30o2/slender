// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.schem;

import java.util.Collection;
import java.util.List;

public final class Schematic
{
    private final int width;
    private final int height;
    private final int length;
    private final List<String> palette;
    private final int[] cells;
    private final boolean[] air;
    
    public Schematic(final int width, final int height, final int length, final List<String> coll, final int[] cells) {
        if (width <= 0 || height <= 0 || length <= 0) {
            throw new IllegalArgumentException("Medidas no v\u00e1lidas");
        }
        if (cells.length != width * height * length) {
            throw new IllegalArgumentException("Faltan bloques: " + cells.length);
        }
        this.width = width;
        this.height = height;
        this.length = length;
        this.palette = List.copyOf((Collection<? extends String>)coll);
        this.cells = cells;
        this.air = new boolean[coll.size()];
        for (int i = 0; i < this.air.length; ++i) {
            this.air[i] = isAirState((String)coll.get(i));
        }
        for (final int n : cells) {
            if (n < 0 || n >= coll.size()) {
                throw new IllegalArgumentException("\u00cdndice de paleta fuera de rango: " + n);
            }
        }
    }
    
    public static boolean isAirState(final String s) {
        final String s2 = s.contains("[") ? s.substring(0, s.indexOf(91)) : s;
        return s2.equals("minecraft:air") || s2.equals("minecraft:cave_air") || s2.equals("minecraft:void_air");
    }
    
    public int width() {
        return this.width;
    }
    
    public int height() {
        return this.height;
    }
    
    public int length() {
        return this.length;
    }
    
    public List<String> palette() {
        return this.palette;
    }
    
    public int size() {
        return this.cells.length;
    }
    
    public boolean inside(final int n, final int n2, final int n3) {
        return n >= 0 && n2 >= 0 && n3 >= 0 && n < this.width && n2 < this.height && n3 < this.length;
    }
    
    public int index(final int n, final int n2, final int n3) {
        return n + n3 * this.width + n2 * this.width * this.length;
    }
    
    public int paletteAt(final int n) {
        return this.cells[n];
    }
    
    public String state(final int n, final int n2, final int n3) {
        return this.inside(n, n2, n3) ? this.palette.get(this.cells[this.index(n, n2, n3)]) : "minecraft:air";
    }
    
    public boolean isAir(final int n, final int n2, final int n3) {
        return !this.inside(n, n2, n3) || this.air[this.cells[this.index(n, n2, n3)]];
    }
    
    public boolean paletteIsAir(final int n) {
        return this.air[n];
    }
    
    public int solidCount() {
        int n = 0;
        final int[] cells = this.cells;
        for (int length = cells.length, i = 0; i < length; ++i) {
            if (!this.air[cells[i]]) {
                ++n;
            }
        }
        return n;
    }
}
