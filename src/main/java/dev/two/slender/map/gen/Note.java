package dev.two.slender.map.gen;

import java.util.List;

public record Note(String place, int dx, int dy, int dz, String facing, List<String> lines, String support) {
    public Note(String place, int dx, int dy, int dz, String facing, List<String> lines, String support) {
        lines = List.copyOf(lines);
        this.place = place;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.facing = facing;
        this.lines = lines;
        this.support = support;
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

    public List<String> lines() {
        return this.lines;
    }

    public String support() {
        return this.support;
    }
}
