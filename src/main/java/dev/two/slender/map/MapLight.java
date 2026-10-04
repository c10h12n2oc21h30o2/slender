package dev.two.slender.map;

public record MapLight(BlockPos pos, String pattern) {
    public static final String BLINK = "blink";
    public static final String FLICKER = "flicker";

    public MapLight(BlockPos pos, String pattern) {
        if (!"blink".equals(pattern) && !"flicker".equals(pattern)) {
            throw new IllegalArgumentException("Patrón de luz inválido: " + pattern);
        } else {
            this.pos = pos;
            this.pattern = pattern;
        }
    }

    public boolean litAt(long var1, double var3) {
        return "blink".equals(this.pattern) ? var1 / 20L % 2L == 0L : var3 >= 0.3;
    }

    public MapLight offset(int var1, int var2, int var3) {
        return new MapLight(this.pos.offset(var1, var2, var3), this.pattern);
    }

    public String serialize() {
        String var10000 = this.pos.serialize();
        return var10000 + "|" + this.pattern;
    }

    public static MapLight parse(String var0) {
        String[] var1 = var0.split("\\|");
        if (var1.length != 2) {
            throw new IllegalArgumentException("Luz inválida: " + var0);
        } else {
            return new MapLight(BlockPos.parse(var1[0]), var1[1]);
        }
    }

    public BlockPos pos() {
        return this.pos;
    }

    public String pattern() {
        return this.pattern;
    }
}
