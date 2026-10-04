// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

record Place(String id, String name, int x, int z, int pad, int y) {
    public static final int AUTO = Integer.MIN_VALUE;
    
    public Place(final String id, final String name, final int x, final int z, final int pad) {
        this(id, name, x, z, pad, Integer.MIN_VALUE);
    }
}
