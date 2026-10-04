package dev.two.slender.map.gen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class FloorPlan {
    private static final String WALK = ".LDd~E";
    private static final String ARROWS = "><^v";
    private final int originX;
    private final int originZ;
    private final int lowest;
    private final int storey;
    private final List<List<String>> floors;

    public FloorPlan(int var1, int var2, int var3, int var4, List<List<String>> var5) {
        this.originX = var1;
        this.originZ = var2;
        this.lowest = var3;
        this.storey = var4;
        this.floors = var5.stream().map(List::copyOf).toList();
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

    public int lampDy(int var1) {
        return var1 * this.storey + this.storey - 1;
    }

    public int minX() {
        return this.originX;
    }

    public int minZ() {
        return this.originZ;
    }

    public int maxX() {
        return this.originX + this.floors.stream().flatMap(Collection::stream).mapToInt(String::length).max().orElse(1) - 1;
    }

    public int maxZ() {
        return this.originZ + this.floors.stream().mapToInt(List::size).max().orElse(1) - 1;
    }

    public char at(int var1, int var2, int var3) {
        if (var1 >= this.lowest && var1 <= this.highest()) {
            List var4 = (List)this.floors.get(var1 - this.lowest);
            int var5 = var3 - this.originZ;
            int var6 = var2 - this.originX;
            return var5 >= 0 && var5 < var4.size() && var6 >= 0 && var6 < ((String)var4.get(var5)).length() ? ((String)var4.get(var5)).charAt(var6) : ' ';
        } else {
            return ' ';
        }
    }

    public char at(Cell var1) {
        return this.at(var1.floor(), var1.x(), var1.z());
    }

    public boolean walkable(Cell var1) {
        return ".LDd~E".indexOf(this.at(var1)) >= 0;
    }

    public List<Cell> cells(char var1) {
        ArrayList var2 = new ArrayList();

        for(int var3 = this.lowest; var3 <= this.highest(); ++var3) {
            for(int var4 = this.minZ(); var4 <= this.maxZ(); ++var4) {
                for(int var5 = this.minX(); var5 <= this.maxX(); ++var5) {
                    if (this.at(var3, var5, var4) == var1) {
                        var2.add(new Cell(var3, var5, var4));
                    }
                }
            }
        }

        return var2;
    }

    private static int[] dir(char var0) {
        int[] var10000;
        switch (var0) {
            case '<' -> var10000 = new int[]{-1, 0};
            case '>' -> var10000 = new int[]{1, 0};
            case '^' -> var10000 = new int[]{0, -1};
            case 'v' -> var10000 = new int[]{0, 1};
            default -> var10000 = null;
        }

        return var10000;
    }

    public List<Stair> stairs() {
        ArrayList var1 = new ArrayList();

        for(int var2 = this.lowest; var2 <= this.highest(); ++var2) {
            for(int var3 = this.minZ(); var3 <= this.maxZ(); ++var3) {
                for(int var4 = this.minX(); var4 <= this.maxX(); ++var4) {
                    char var5 = this.at(var2, var4, var3);
                    int[] var6 = dir(var5);
                    if (var6 != null && this.at(var2, var4 - var6[0], var3 - var6[1]) != var5) {
                        int var7;
                        for(var7 = 0; this.at(var2, var4 + var6[0] * var7, var3 + var6[1] * var7) == var5; ++var7) {
                        }

                        var1.add(new Stair(var2, var4, var3, var6[0], var6[1], var7));
                    }
                }
            }
        }

        return var1;
    }

    public Set<Cell> reachable(Cell var1) {
        List var2 = this.stairs();
        HashSet var3 = new HashSet();
        ArrayDeque var4 = new ArrayDeque();
        if (!this.walkable(var1)) {
            return var3;
        } else {
            var3.add(var1);
            var4.add(var1);

            while(!var4.isEmpty()) {
                Cell var5 = (Cell)var4.poll();
                ArrayList var6 = new ArrayList();
                int[][] var7 = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    int[] var10 = var7[var9];
                    var6.add(new Cell(var5.floor(), var5.x() + var10[0], var5.z() + var10[1]));
                }

                Iterator var11 = var2.iterator();

                while(var11.hasNext()) {
                    Stair var12 = (Stair)var11.next();
                    if (var12.below().equals(var5)) {
                        var6.add(var12.above());
                    }

                    if (var12.above().equals(var5)) {
                        var6.add(var12.below());
                    }
                }

                var11 = var6.iterator();

                while(var11.hasNext()) {
                    Cell var13 = (Cell)var11.next();
                    if (this.walkable(var13) && var3.add(var13)) {
                        var4.add(var13);
                    }
                }
            }

            return var3;
        }
    }

    public List<String> problems() {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.stairs().iterator();

        while(var2.hasNext()) {
            Stair var3 = (Stair)var2.next();
            String var4 = "escalera " + String.valueOf(var3);
            if (var3.length() != this.storey) {
                var1.add(var4 + ": tiene " + var3.length() + " peldaños y la planta mide " + this.storey);
            }

            if (var3.floor() >= this.highest()) {
                var1.add(var4 + ": no hay planta encima");
            }

            for(int var5 = 0; var5 < var3.length(); ++var5) {
                if (this.at(var3.floor() + 1, var3.x() + var3.dx() * var5, var3.z() + var3.dz() * var5) != 'o') {
                    var1.add(var4 + ": falta el hueco 'o' encima del peldaño " + var5);
                }
            }

            if (!this.walkable(var3.below())) {
                var1.add(var4 + ": no se puede llegar al primer peldaño");
            }

            if (!this.walkable(var3.above())) {
                var1.add(var4 + ": no hay suelo al final");
            }
        }

        var2 = this.cells('o').iterator();

        while(var2.hasNext()) {
            Cell var7 = (Cell)var2.next();
            boolean var8 = false;
            Iterator var9 = this.stairs().iterator();

            while(var9.hasNext()) {
                Stair var6 = (Stair)var9.next();
                if (var6.floor() == var7.floor() - 1 && var6.contains(var7.x(), var7.z())) {
                    var8 = true;
                }
            }

            if (!var8) {
                var1.add("hueco 'o' sin escalera debajo en " + String.valueOf(var7));
            }
        }

        return var1;
    }

    public static record Cell(int floor, int x, int z) {
        public Cell(int floor, int x, int z) {
            this.floor = floor;
            this.x = x;
            this.z = z;
        }

        public int floor() {
            return this.floor;
        }

        public int x() {
            return this.x;
        }

        public int z() {
            return this.z;
        }
    }

    public static record Stair(int floor, int x, int z, int dx, int dz, int length) {
        public Stair(int floor, int x, int z, int dx, int dz, int length) {
            this.floor = floor;
            this.x = x;
            this.z = z;
            this.dx = dx;
            this.dz = dz;
            this.length = length;
        }

        public Cell below() {
            return new Cell(this.floor, this.x - this.dx, this.z - this.dz);
        }

        public Cell above() {
            return new Cell(this.floor + 1, this.x + this.dx * this.length, this.z + this.dz * this.length);
        }

        public boolean contains(int var1, int var2) {
            return this.index(var1, var2) >= 0;
        }

        public int index(int var1, int var2) {
            for(int var3 = 0; var3 < this.length; ++var3) {
                if (this.x + this.dx * var3 == var1 && this.z + this.dz * var3 == var2) {
                    return var3;
                }
            }

            return -1;
        }

        public int floor() {
            return this.floor;
        }

        public int x() {
            return this.x;
        }

        public int z() {
            return this.z;
        }

        public int dx() {
            return this.dx;
        }

        public int dz() {
            return this.dz;
        }

        public int length() {
            return this.length;
        }
    }
}
