package dev.two.slender.map.gen;

import dev.two.slender.logic.Heightmap;
import java.util.List;

public record Post(int x, int z, String facing, List<String> targets) {
    public Post(int x, int z, String facing, List<String> targets) {
        targets = List.copyOf(targets);
        this.x = x;
        this.z = z;
        this.facing = facing;
        this.targets = targets;
    }

    public int base(Heightmap var1) {
        byte var2 = 0;
        byte var3 = 0;
        switch (this.facing) {
            case "NORTH" -> var3 = -1;
            case "SOUTH" -> var3 = 1;
            case "EAST" -> var2 = 1;
            default -> var2 = -1;
        }

        int var6 = Math.max(var1.at(this.x + var2, this.z + var3), var1.at(this.x + 2 * var2, this.z + 2 * var3));
        return Math.max(var1.at(this.x, this.z), var6 - 1);
    }

    public int x() {
        return this.x;
    }

    public int z() {
        return this.z;
    }

    public String facing() {
        return this.facing;
    }

    public List<String> targets() {
        return this.targets;
    }
}
