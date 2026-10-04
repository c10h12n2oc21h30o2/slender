package dev.two.slender.map;

public record BlockPos(int x, int y, int z) {
    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos offset(int var1, int var2, int var3) {
        return new BlockPos(this.x + var1, this.y + var2, this.z + var3);
    }

    public String serialize() {
        return this.x + "," + this.y + "," + this.z;
    }

    public static BlockPos parse(String var0) {
        String[] var1 = var0.split(",");
        if (var1.length != 3) {
            throw new IllegalArgumentException("BlockPos inválido: " + var0);
        } else {
            try {
                return new BlockPos(Integer.parseInt(var1[0].trim()), Integer.parseInt(var1[1].trim()), Integer.parseInt(var1[2].trim()));
            } catch (NumberFormatException var3) {
                throw new IllegalArgumentException("BlockPos inválido: " + var0, var3);
            }
        }
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }
}
