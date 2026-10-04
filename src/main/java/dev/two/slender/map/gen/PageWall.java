package dev.two.slender.map.gen;

public record PageWall(String place, int dx, int dy, int dz, String facing, String label, String material) {
    public PageWall(String place, int dx, int dy, int dz, String facing, String label, String material) {
        this.place = place;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.facing = facing;
        this.label = label;
        this.material = material;
    }

    public String place() {
        return this.place;
    }

    public int dx() {
        return this.dx;
    }

    public int dy() {
        return this.dy;
    }

    public int dz() {
        return this.dz;
    }

    public String facing() {
        return this.facing;
    }

    public String label() {
        return this.label;
    }

    public String material() {
        return this.material;
    }
}
