// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

public enum Mode
{
    COOP("coop"), 
    ASIM("asim");
    
    private final String id;
    
    private Mode(final String id) {
        this.id = id;
    }
    
    public String id() {
        return this.id;
    }
    
    public static Mode of(final String anObject) {
        return "asim".equals(anObject) ? Mode.ASIM : Mode.COOP;
    }
}
