// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

record Zone(BlockPos min, BlockPos max) {
    public static Zone of(final BlockPos blockPos, final BlockPos blockPos2) {
        return new Zone(new BlockPos(Math.min(blockPos.x(), blockPos2.x()), Math.min(blockPos.y(), blockPos2.y()), Math.min(blockPos.z(), blockPos2.z())), new BlockPos(Math.max(blockPos.x(), blockPos2.x()), Math.max(blockPos.y(), blockPos2.y()), Math.max(blockPos.z(), blockPos2.z())));
    }
    
    public boolean contains(final double n, final double n2, final double n3) {
        return n >= this.min.x() && n < this.max.x() + 1 && n2 >= this.min.y() && n2 < this.max.y() + 1 && n3 >= this.min.z() && n3 < this.max.z() + 1;
    }
    
    public Zone tall(final int n) {
        return new Zone(this.min, new BlockPos(this.max.x(), Math.max(this.max.y(), this.min.y() + n - 1), this.max.z()));
    }
    
    public String serialize() {
        return this.min.serialize() + ";" + this.max.serialize();
    }
    
    public static Zone parse(final String s) {
        final String[] split = s.split(";");
        if (split.length != 2) {
            throw new IllegalArgumentException("Zona inv\u00e1lida: " + s);
        }
        return of(BlockPos.parse(split[0]), BlockPos.parse(split[1]));
    }
}
