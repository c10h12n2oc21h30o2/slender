package dev.two.slender.map;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ArenaMap(String id, String displayName, String world, BlockPos origin, BlockPos min, BlockPos max, Loc lobby, List<Loc> spawns, List<PageSpot> pageSpots, List<BlockPos> batterySpots, List<BlockPos> gate, String gateBlock, BlockPos exitMin, BlockPos exitMax, long seed, Ambience ambience) {
    public static final BlockPos UNSET = new BlockPos(0, -9999, 0);

    public ArenaMap(String id, String displayName, String world, BlockPos origin, BlockPos min, BlockPos max, Loc lobby, List<Loc> spawns, List<PageSpot> pageSpots, List<BlockPos> batterySpots, List<BlockPos> gate, String gateBlock, BlockPos exitMin, BlockPos exitMax, long seed, Ambience ambience) {
        spawns = List.copyOf(spawns);
        pageSpots = List.copyOf(pageSpots);
        batterySpots = List.copyOf(batterySpots);
        gate = List.copyOf(gate);
        if (ambience == null) {
            ambience = Ambience.NONE;
        }

        this.id = id;
        this.displayName = displayName;
        this.world = world;
        this.origin = origin;
        this.min = min;
        this.max = max;
        this.lobby = lobby;
        this.spawns = spawns;
        this.pageSpots = pageSpots;
        this.batterySpots = batterySpots;
        this.gate = gate;
        this.gateBlock = gateBlock;
        this.exitMin = exitMin;
        this.exitMax = exitMax;
        this.seed = seed;
        this.ambience = ambience;
    }

    public ArenaMap(String var1, String var2, String var3, BlockPos var4, BlockPos var5, BlockPos var6, Loc var7, List<Loc> var8, List<PageSpot> var9, List<BlockPos> var10, List<BlockPos> var11, String var12, BlockPos var13, BlockPos var14, long var15) {
        this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, Ambience.NONE);
    }

    public ArenaMap withAmbience(Ambience var1) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, var1);
    }

    public static ArenaMap draft(String var0, String var1, String var2, BlockPos var3) {
        return new ArenaMap(var0, var1, var2, var3, var3.offset(-60, -20, -60), var3.offset(60, 60, 60), new Loc((double)var3.x() + 0.5, (double)var3.y(), (double)var3.z() + 0.5, 0.0F), List.of(), List.of(), List.of(), List.of(), "minecraft:air", UNSET, UNSET, 0L);
    }

    public int fallY() {
        return Math.min(this.min.y(), this.origin.y() - 12);
    }

    public Zone roamArea() {
        int var1 = this.min.x() + 2;
        int var2 = this.min.z() + 2;
        int var3 = this.max.x() - 2;
        int var4 = this.max.z() - 2;
        if (!this.gate.isEmpty()) {
            int var5 = ((BlockPos)this.gate.get(0)).x();
            int var6 = ((BlockPos)this.gate.get(0)).z();
            boolean var7 = this.gate.stream().allMatch((var1x) -> {
                return var1x.z() == var6;
            });
            boolean var8 = this.gate.stream().allMatch((var1x) -> {
                return var1x.x() == var5;
            });
            int var9 = Math.min(this.max.z() - var6, var6 - this.min.z());
            int var10 = Math.min(this.max.x() - var5, var5 - this.min.x());
            if (var7 && (!var8 || var9 <= var10) && var9 <= 12) {
                if (var6 * 2 > this.min.z() + this.max.z()) {
                    var4 = Math.min(var4, var6 - 1);
                } else {
                    var2 = Math.max(var2, var6 + 1);
                }
            } else if (var8 && var10 <= 12) {
                if (var5 * 2 > this.min.x() + this.max.x()) {
                    var3 = Math.min(var3, var5 - 1);
                } else {
                    var1 = Math.max(var1, var5 + 1);
                }
            }
        }

        return new Zone(new BlockPos(var1, this.min.y(), var2), new BlockPos(var3, this.max.y(), var4));
    }

    public boolean inRoam(double var1, double var3) {
        Zone var5 = this.roamArea();
        return var1 >= (double)var5.min().x() && var1 < (double)(var5.max().x() + 1) && var3 >= (double)var5.min().z() && var3 < (double)(var5.max().z() + 1);
    }

    public boolean hasExit() {
        return !this.exitMin.equals(UNSET);
    }

    public Zone bounds() {
        return new Zone(this.min, this.max);
    }

    public double farthestPageFromExit() {
        if (!this.hasExit()) {
            return 0.0;
        } else {
            int var1 = Math.min(this.exitMin.x(), this.exitMax.x());
            int var2 = Math.max(this.exitMin.x(), this.exitMax.x());
            int var3 = Math.min(this.exitMin.z(), this.exitMax.z());
            int var4 = Math.max(this.exitMin.z(), this.exitMax.z());
            double var5 = 0.0;

            int var9;
            int var10;
            for(Iterator var7 = this.pageSpots.iterator(); var7.hasNext(); var5 = Math.max(var5, Math.hypot((double)var9, (double)var10))) {
                PageSpot var8 = (PageSpot)var7.next();
                var9 = Math.max(0, Math.max(var1 - var8.pos().x(), var8.pos().x() - var2));
                var10 = Math.max(0, Math.max(var3 - var8.pos().z(), var8.pos().z() - var4));
            }

            return var5;
        }
    }

    public ArenaMap withDisplayName(String var1) {
        return new ArenaMap(this.id, var1, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }

    public ArenaMap withSpawns(List<Loc> var1) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, var1, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }

    public ArenaMap withPages(List<PageSpot> var1) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, var1, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }

    public ArenaMap withBatteries(List<BlockPos> var1) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, var1, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }

    public ArenaMap withGate(List<BlockPos> var1, String var2) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, var1, var2, this.exitMin, this.exitMax, this.seed, this.ambience);
    }

    public ArenaMap withExit(Zone var1) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, var1.min(), var1.max(), this.seed, this.ambience);
    }

    public ArenaMap withBounds(Zone var1) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, var1.min(), var1.max(), this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }

    public boolean inBounds(double var1, double var3) {
        return var1 >= (double)(this.min.x() - 1) && var1 <= (double)(this.max.x() + 1) && var3 >= (double)(this.min.z() - 1) && var3 <= (double)(this.max.z() + 1);
    }

    public boolean inExit(double var1, double var3, double var5) {
        if (!this.hasExit()) {
            return false;
        } else {
            return var1 >= (double)this.exitMin.x() && var1 <= (double)(this.exitMax.x() + 1) && var3 >= (double)this.exitMin.y() && var3 <= (double)(this.exitMax.y() + 1) && var5 >= (double)this.exitMin.z() && var5 <= (double)(this.exitMax.z() + 1);
        }
    }

    public Map<String, Object> toMap() {
        LinkedHashMap var1 = new LinkedHashMap();
        var1.put("id", this.id);
        var1.put("name", this.displayName);
        var1.put("world", this.world);
        var1.put("origin", this.origin.serialize());
        var1.put("min", this.min.serialize());
        var1.put("max", this.max.serialize());
        var1.put("lobby", this.lobby.serialize());
        var1.put("spawns", this.spawns.stream().map(Loc::serialize).toList());
        var1.put("pages", this.pageSpots.stream().map(PageSpot::serialize).toList());
        var1.put("batteries", this.batterySpots.stream().map(BlockPos::serialize).toList());
        var1.put("gate", this.gate.stream().map(BlockPos::serialize).toList());
        var1.put("gate-block", this.gateBlock);
        var1.put("exit-min", this.exitMin.serialize());
        var1.put("exit-max", this.exitMax.serialize());
        var1.put("seed", this.seed);
        this.ambience.writeTo(var1);
        return var1;
    }

    public static ArenaMap fromMap(Map<String, ?> var0) {
        return new ArenaMap(str(var0, "id"), str(var0, "name"), str(var0, "world"), BlockPos.parse(str(var0, "origin")), BlockPos.parse(str(var0, "min")), BlockPos.parse(str(var0, "max")), Loc.parse(str(var0, "lobby")), list(var0, "spawns").stream().map(Loc::parse).toList(), list(var0, "pages").stream().map(PageSpot::parse).toList(), list(var0, "batteries").stream().map(BlockPos::parse).toList(), list(var0, "gate").stream().map(BlockPos::parse).toList(), str(var0, "gate-block"), BlockPos.parse(str(var0, "exit-min")), BlockPos.parse(str(var0, "exit-max")), ((Number)var0.get("seed")).longValue(), Ambience.readFrom(var0));
    }

    private static String str(Map<String, ?> var0, String var1) {
        Object var2 = var0.get(var1);
        if (var2 == null) {
            throw new IllegalArgumentException("Falta '" + var1 + "' en el mapa");
        } else {
            return var2.toString();
        }
    }

    private static List<String> list(Map<String, ?> var0, String var1) {
        Object var2 = var0.get(var1);
        if (var2 instanceof List var3) {
            return var3.stream().map(Object::toString).toList();
        } else {
            return List.of();
        }
    }

    public String id() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public String world() {
        return this.world;
    }

    public BlockPos origin() {
        return this.origin;
    }

    public BlockPos min() {
        return this.min;
    }

    public BlockPos max() {
        return this.max;
    }

    public Loc lobby() {
        return this.lobby;
    }

    public List<Loc> spawns() {
        return this.spawns;
    }

    public List<PageSpot> pageSpots() {
        return this.pageSpots;
    }

    public List<BlockPos> batterySpots() {
        return this.batterySpots;
    }

    public List<BlockPos> gate() {
        return this.gate;
    }

    public String gateBlock() {
        return this.gateBlock;
    }

    public BlockPos exitMin() {
        return this.exitMin;
    }

    public BlockPos exitMax() {
        return this.exitMax;
    }

    public long seed() {
        return this.seed;
    }

    public Ambience ambience() {
        return this.ambience;
    }
}
