package dev.two.slender.map;

public record PageSpot(BlockPos pos, String facing, String label) {
    public PageSpot(BlockPos pos, String facing, String label) {
        this.pos = pos;
        this.facing = facing;
        this.label = label;
    }

    public String serialize() {
        String var10000 = this.pos.serialize();
        return var10000 + "|" + this.facing + "|" + this.label;
    }

    public static PageSpot parse(String var0) {
        String[] var1 = var0.split("\\|", 3);
        if (var1.length != 3) {
            throw new IllegalArgumentException("PageSpot inválido: " + var0);
        } else {
            return new PageSpot(BlockPos.parse(var1[0]), var1[1], var1[2]);
        }
    }

    public BlockPos pos() {
        return this.pos;
    }

    public String facing() {
        return this.facing;
    }

    public String label() {
        return this.label;
    }
}
