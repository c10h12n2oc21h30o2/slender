// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import java.util.Collection;
import java.util.List;

record Note(String place, int dx, int dy, int dz, String facing, List<String> lines, String support) {
    public Note(final String place, final int dx, final int dy, final int dz, final String facing, final List<String> lines, final String support) {
        final List<Object> copy = (List<Object>)List.copyOf((Collection<? extends String>)lines);
        this.place = place;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.facing = facing;
        this.lines = (List<String>)copy;
        this.support = support;
    }
}
