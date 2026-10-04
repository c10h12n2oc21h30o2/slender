// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import dev.two.slender.logic.Heightmap;
import java.util.Collection;
import java.util.List;

record Post(int x, int z, String facing, List<String> targets) {
    public Post(final int x, final int z, final String facing, final List<String> targets) {
        final List<Object> copy = (List<Object>)List.copyOf((Collection<? extends String>)targets);
        this.x = x;
        this.z = z;
        this.facing = facing;
        this.targets = (List<String>)copy;
    }
    
    public int base(final Heightmap heightmap) {
        int n = 0;
        int n2 = 0;
        final String facing = this.facing;
        switch (facing) {
            case "NORTH": {
                n2 = -1;
                break;
            }
            case "SOUTH": {
                n2 = 1;
                break;
            }
            case "EAST": {
                n = 1;
                break;
            }
            default: {
                n = -1;
                break;
            }
        }
        return Math.max(heightmap.at(this.x, this.z), Math.max(heightmap.at(this.x + n, this.z + n2), heightmap.at(this.x + 2 * n, this.z + 2 * n2)) - 1);
    }
}
