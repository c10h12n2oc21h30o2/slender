package dev.two.slender.map.schem;

import java.util.List;

public final class Schematic {
    private final int width;
    private final int height;
    private final int length;
    private final List<String> palette;
    private final int[] cells;
    private final boolean[] air;

    public Schematic(int var1, int var2, int var3, List<String> var4, int[] var5) {
        if (var1 > 0 && var2 > 0 && var3 > 0) {
            if (var5.length != var1 * var2 * var3) {
                throw new IllegalArgumentException("Faltan bloques: " + var5.length);
            } else {
                this.width = var1;
                this.height = var2;
                this.length = var3;
                this.palette = List.copyOf(var4);
                this.cells = var5;
                this.air = new boolean[var4.size()];

                for(int var6 = 0; var6 < this.air.length; ++var6) {
                    this.air[var6] = isAirState((String)var4.get(var6));
                }

                int[] var10 = var5;
                int var7 = var5.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    int var9 = var10[var8];
                    if (var9 < 0 || var9 >= var4.size()) {
                        throw new IllegalArgumentException("Índice de paleta fuera de rango: " + var9);
                    }
                }

            }
        } else {
            throw new IllegalArgumentException("Medidas no válidas");
        }
    }

    public static boolean isAirState(String var0) {
        String var1 = var0.contains("[") ? var0.substring(0, var0.indexOf(91)) : var0;
        return var1.equals("minecraft:air") || var1.equals("minecraft:cave_air") || var1.equals("minecraft:void_air");
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int length() {
        return this.length;
    }

    public List<String> palette() {
        return this.palette;
    }

    public int size() {
        return this.cells.length;
    }

    public boolean inside(int var1, int var2, int var3) {
        return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.width && var2 < this.height && var3 < this.length;
    }

    public int index(int var1, int var2, int var3) {
        return var1 + var3 * this.width + var2 * this.width * this.length;
    }

    public int paletteAt(int var1) {
        return this.cells[var1];
    }

    public String state(int var1, int var2, int var3) {
        return this.inside(var1, var2, var3) ? (String)this.palette.get(this.cells[this.index(var1, var2, var3)]) : "minecraft:air";
    }

    public boolean isAir(int var1, int var2, int var3) {
        return !this.inside(var1, var2, var3) || this.air[this.cells[this.index(var1, var2, var3)]];
    }

    public boolean paletteIsAir(int var1) {
        return this.air[var1];
    }

    public int solidCount() {
        int var1 = 0;
        int[] var2 = this.cells;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            if (!this.air[var5]) {
                ++var1;
            }
        }

        return var1;
    }
}
