package dev.two.slender.map;

public record Zone(BlockPos min, BlockPos max) {
    public Zone(BlockPos min, BlockPos max) {
        this.min = min;
        this.max = max;
    }

    public static Zone of(BlockPos var0, BlockPos var1) {
        return new Zone(new BlockPos(Math.min(var0.x(), var1.x()), Math.min(var0.y(), var1.y()), Math.min(var0.z(), var1.z())), new BlockPos(Math.max(var0.x(), var1.x()), Math.max(var0.y(), var1.y()), Math.max(var0.z(), var1.z())));
    }

    public boolean contains(double var1, double var3, double var5) {
        return var1 >= (double)this.min.x() && var1 < (double)(this.max.x() + 1) && var3 >= (double)this.min.y() && var3 < (double)(this.max.y() + 1) && var5 >= (double)this.min.z() && var5 < (double)(this.max.z() + 1);
    }

    public Zone tall(int var1) {
        return new Zone(this.min, new BlockPos(this.max.x(), Math.max(this.max.y(), this.min.y() + var1 - 1), this.max.z()));
    }

    public String serialize() {
        String var10000 = this.min.serialize();
        return var10000 + ";" + this.max.serialize();
    }

    public static Zone parse(String var0) {
        String[] var1 = var0.split(";");
        if (var1.length != 2) {
            throw new IllegalArgumentException("Zona inválida: " + var0);
        } else {
            return of(BlockPos.parse(var1[0]), BlockPos.parse(var1[1]));
        }
    }

    public BlockPos min() {
        return this.min;
    }

    public BlockPos max() {
        return this.max;
    }
}
