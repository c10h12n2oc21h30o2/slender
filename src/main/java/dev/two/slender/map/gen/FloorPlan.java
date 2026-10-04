// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.Collection;
import java.util.function.Function;
import java.util.List;

public final class FloorPlan
{
    private static final String WALK = ".LDd~E";
    private static final String ARROWS = "><^v";
    private final int originX;
    private final int originZ;
    private final int lowest;
    private final int storey;
    private final List<List<String>> floors;
    
    public FloorPlan(final int originX, final int originZ, final int lowest, final int storey, final List<List<String>> list) {
        this.originX = originX;
        this.originZ = originZ;
        this.lowest = lowest;
        this.storey = storey;
        this.floors = list.stream().map((Function<? super Object, ? extends List<String>>)List::copyOf).toList();
    }
    
    public int lowest() {
        return this.lowest;
    }
    
    public int highest() {
        return this.lowest + this.floors.size() - 1;
    }
    
    public int storey() {
        return this.storey;
    }
    
    public int lampDy(final int n) {
        return n * this.storey + this.storey - 1;
    }
    
    public int minX() {
        return this.originX;
    }
    
    public int minZ() {
        return this.originZ;
    }
    
    public int maxX() {
        return this.originX + this.floors.stream().flatMap((Function<? super Object, ? extends Stream<?>>)Collection::stream).mapToInt(String::length).max().orElse(1) - 1;
    }
    
    public int maxZ() {
        return this.originZ + this.floors.stream().mapToInt(List::size).max().orElse(1) - 1;
    }
    
    public char at(final int n, final int n2, final int n3) {
        if (n < this.lowest || n > this.highest()) {
            return ' ';
        }
        final List list = this.floors.get(n - this.lowest);
        final int n4 = n3 - this.originZ;
        final int index = n2 - this.originX;
        if (n4 < 0 || n4 >= list.size() || index < 0 || index >= ((String)list.get(n4)).length()) {
            return ' ';
        }
        return ((String)list.get(n4)).charAt(index);
    }
    
    public char at(final Cell cell) {
        return this.at(cell.floor(), cell.x(), cell.z());
    }
    
    public boolean walkable(final Cell cell) {
        return ".LDd~E".indexOf(this.at(cell)) >= 0;
    }
    
    public List<Cell> cells(final char c) {
        final ArrayList list = new ArrayList();
        for (int i = this.lowest; i <= this.highest(); ++i) {
            for (int j = this.minZ(); j <= this.maxZ(); ++j) {
                for (int k = this.minX(); k <= this.maxX(); ++k) {
                    if (this.at(i, k, j) == c) {
                        list.add(new Cell(i, k, j));
                    }
                }
            }
        }
        return list;
    }
    
    private static int[] dir(final char c) {
        return switch (c) {
            case '>' -> new int[] { 1, 0 };
            case '<' -> new int[] { -1, 0 };
            case '^' -> new int[] { 0, -1 };
            case 'v' -> new int[] { 0, 1 };
            default -> null;
        };
    }
    
    public List<Stair> stairs() {
        final ArrayList list = new ArrayList();
        for (int i = this.lowest; i <= this.highest(); ++i) {
            for (int j = this.minZ(); j <= this.maxZ(); ++j) {
                for (int k = this.minX(); k <= this.maxX(); ++k) {
                    final char at = this.at(i, k, j);
                    final int[] dir = dir(at);
                    if (dir != null) {
                        if (this.at(i, k - dir[0], j - dir[1]) != at) {
                            int length;
                            for (length = 0; this.at(i, k + dir[0] * length, j + dir[1] * length) == at; ++length) {}
                            list.add(new Stair(i, k, j, dir[0], dir[1], length));
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public Set<Cell> reachable(final Cell cell) {
        final List<Stair> stairs = this.stairs();
        final HashSet set = new HashSet();
        final ArrayDeque arrayDeque = new ArrayDeque();
        if (!this.walkable(cell)) {
            return set;
        }
        set.add(cell);
        arrayDeque.add(cell);
        while (!arrayDeque.isEmpty()) {
            final Cell cell2 = (Cell)arrayDeque.poll();
            final ArrayList list = new ArrayList();
            for (final int[] array2 : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }) {
                list.add(new Cell(cell2.floor(), cell2.x() + array2[0], cell2.z() + array2[1]));
            }
            for (final Stair stair : stairs) {
                if (stair.below().equals(cell2)) {
                    list.add(stair.above());
                }
                if (stair.above().equals(cell2)) {
                    list.add(stair.below());
                }
            }
            for (final Cell cell3 : list) {
                if (this.walkable(cell3) && set.add(cell3)) {
                    arrayDeque.add(cell3);
                }
            }
        }
        return set;
    }
    
    public List<String> problems() {
        final ArrayList list = new ArrayList();
        for (Stair obj : this.stairs()) {
            final String s = "escalera " + String.valueOf(obj);
            if (obj.length() != this.storey) {
                list.add(s + ": tiene " + obj.length() + " pelda\u00f1os y la planta mide " + this.storey);
            }
            if (obj.floor() >= this.highest()) {
                list.add(s + ": no hay planta encima");
            }
            for (int i = 0; i < obj.length(); ++i) {
                if (this.at(obj.floor() + 1, obj.x() + obj.dx() * i, obj.z() + obj.dz() * i) != 'o') {
                    list.add(s + ": falta el hueco 'o' encima del pelda\u00f1o " + i);
                }
            }
            if (!this.walkable(obj.below())) {
                list.add(s + ": no se puede llegar al primer pelda\u00f1o");
            }
            if (!this.walkable(obj.above())) {
                list.add(s + ": no hay suelo al final");
            }
        }
        for (Cell obj2 : this.cells('o')) {
            boolean b = false;
            for (final Stair stair : this.stairs()) {
                if (stair.floor() == obj2.floor() - 1 && stair.contains(obj2.x(), obj2.z())) {
                    b = true;
                }
            }
            if (!b) {
                list.add("hueco 'o' sin escalera debajo en " + String.valueOf(obj2));
            }
        }
        return list;
    }
    
    record Cell(int floor, int x, int z) {}
    
    record Stair(int floor, int x, int z, int dx, int dz, int length) {
        public Cell below() {
            return new Cell(this.floor, this.x - this.dx, this.z - this.dz);
        }
        
        public Cell above() {
            return new Cell(this.floor + 1, this.x + this.dx * this.length, this.z + this.dz * this.length);
        }
        
        public boolean contains(final int n, final int n2) {
            return this.index(n, n2) >= 0;
        }
        
        public int index(final int n, final int n2) {
            for (int i = 0; i < this.length; ++i) {
                if (this.x + this.dx * i == n && this.z + this.dz * i == n2) {
                    return i;
                }
            }
            return -1;
        }
    }
}
