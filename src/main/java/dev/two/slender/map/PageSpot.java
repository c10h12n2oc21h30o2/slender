// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

record PageSpot(BlockPos pos, String facing, String label) {
    public String serialize() {
        return this.pos.serialize() + "|" + this.facing + "|" + this.label;
    }
    
    public static PageSpot parse(final String s) {
        final String[] split = s.split("\\|", 3);
        if (split.length != 3) {
            throw new IllegalArgumentException("PageSpot inv\u00e1lido: " + s);
        }
        return new PageSpot(BlockPos.parse(split[0]), split[1], split[2]);
    }
}
