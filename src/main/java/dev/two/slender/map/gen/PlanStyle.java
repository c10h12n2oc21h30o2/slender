// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import java.util.List;

public interface PlanStyle
{
    String wall(final FloorPlan.Cell p0, final boolean p1);
    
    String stripe(final FloorPlan.Cell p0);
    
    String floor(final FloorPlan.Cell p0);
    
    String roof(final int p0, final int p1);
    
    String door();
    
    String stairs();
    
    List<String> furniture(final char p0);
    
    default List<String> stack(final String... elements) {
        return List.of(elements);
    }
}
