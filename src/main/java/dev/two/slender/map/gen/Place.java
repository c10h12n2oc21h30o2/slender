package dev.two.slender.map.gen;

public record Place(String id, String name, int x, int z, int pad, int y) {
    public static final int AUTO = Integer.MIN_VALUE;

    public Place(String var1, String var2, int var3, int var4, int var5) {
        this(var1, var2, var3, var4, var5, Integer.MIN_VALUE);
    }

    public Place(String id, String name, int x, int z, int pad, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.z = z;
        this.pad = pad;
        this.y = y;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public int x() {
        return this.x;
    }

    public int z() {
        return this.z;
    }

    public int pad() {
        return this.pad;
    }

    public int y() {
        return this.y;
    }
}
