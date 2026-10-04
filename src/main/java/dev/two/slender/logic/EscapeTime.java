// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class EscapeTime
{
    public static final double PACE = 2.7;
    
    private EscapeTime() {
    }
    
    public static int seconds(final int a, final double n) {
        return Math.max(a, (int)Math.ceil(n / 2.7));
    }
}
