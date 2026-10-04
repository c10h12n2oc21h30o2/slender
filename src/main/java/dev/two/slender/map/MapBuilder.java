// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import org.bukkit.Material;

public interface MapBuilder
{
    String id();
    
    String displayName();
    
    Material icon();
    
    int radius();
    
    ArenaMap build(final Builder p0, final String p1, final long p2);
}
