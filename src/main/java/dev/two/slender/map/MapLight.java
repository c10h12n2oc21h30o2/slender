// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

record MapLight(BlockPos pos, String pattern) {
    public static final String BLINK = "blink";
    public static final String FLICKER = "flicker";
    
    public MapLight {
        if (!"blink".equals(pattern) && !"flicker".equals(pattern)) {
            throw new IllegalArgumentException("Patr\u00f3n de luz inv\u00e1lido: " + pattern);
        }
    }
    
    public boolean litAt(final long n, final double n2) {
        return "blink".equals(this.pattern) ? (n / 20L % 2L == 0L) : (n2 >= 0.3);
    }
    
    public MapLight offset(final int n, final int n2, final int n3) {
        return new MapLight(this.pos.offset(n, n2, n3), this.pattern);
    }
    
    public String serialize() {
        return this.pos.serialize() + "|" + this.pattern;
    }
    
    public static MapLight parse(final String s) {
        final String[] split = s.split("\\|");
        if (split.length != 2) {
            throw new IllegalArgumentException("Luz inv\u00e1lida: " + s);
        }
        return new MapLight(BlockPos.parse(split[0]), split[1]);
    }
}
