// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.player;

import java.util.Arrays;

public enum RestoreMode
{
    FULL, 
    STATE_ONLY;
    
    public static RestoreMode after(final Object[] a, final Object[] a2) {
        return (a == null || Arrays.equals(a, a2)) ? RestoreMode.FULL : RestoreMode.STATE_ONLY;
    }
}
