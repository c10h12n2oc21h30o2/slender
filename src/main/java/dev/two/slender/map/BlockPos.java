// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

record BlockPos(int x, int y, int z) {
    public BlockPos offset(final int n, final int n2, final int n3) {
        return new BlockPos(this.x + n, this.y + n2, this.z + n3);
    }
    
    public String serialize() {
        return this.x + "," + this.y + "," + this.z;
    }
    
    public static BlockPos parse(final String s) {
        final String[] split = s.split(",");
        if (split.length != 3) {
            throw new IllegalArgumentException("BlockPos inv\u00e1lido: " + s);
        }
        try {
            return new BlockPos(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()));
        }
        catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("BlockPos inv\u00e1lido: " + s, (Throwable)cause);
        }
    }
}
