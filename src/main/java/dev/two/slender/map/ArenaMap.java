// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.function.Function;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;

record ArenaMap(String id, String displayName, String world, BlockPos origin, BlockPos min, BlockPos max, Loc lobby, List<Loc> spawns, List<PageSpot> pageSpots, List<BlockPos> batterySpots, List<BlockPos> gate, String gateBlock, BlockPos exitMin, BlockPos exitMax, long seed, Ambience ambience) {
    public static final BlockPos UNSET;
    
    public ArenaMap(final String id, final String displayName, final String world, final BlockPos origin, final BlockPos min, final BlockPos max, final Loc lobby, final List<Loc> spawns, final List<PageSpot> pageSpots, final List<BlockPos> batterySpots, final List<BlockPos> gate, final String gateBlock, final BlockPos exitMin, final BlockPos exitMax, final long seed, Ambience ambience) {
        final List<Object> copy = (List<Object>)List.copyOf((Collection<? extends Loc>)spawns);
        final List<Object> copy2 = (List<Object>)List.copyOf((Collection<? extends PageSpot>)pageSpots);
        final List<Object> copy3 = (List<Object>)List.copyOf((Collection<? extends BlockPos>)batterySpots);
        final List<Object> copy4 = (List<Object>)List.copyOf((Collection<? extends BlockPos>)gate);
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
        this.spawns = (List<Loc>)copy;
        this.pageSpots = (List<PageSpot>)copy2;
        this.batterySpots = (List<BlockPos>)copy3;
        this.gate = (List<BlockPos>)copy4;
        this.gateBlock = gateBlock;
        this.exitMin = exitMin;
        this.exitMax = exitMax;
        this.seed = seed;
        this.ambience = ambience;
    }
    
    public ArenaMap(final String id, final String displayName, final String world, final BlockPos origin, final BlockPos min, final BlockPos max, final Loc lobby, final List<Loc> spawns, final List<PageSpot> pageSpots, final List<BlockPos> batterySpots, final List<BlockPos> gate, final String gateBlock, final BlockPos exitMin, final BlockPos exitMax, final long seed) {
        this(id, displayName, world, origin, min, max, lobby, spawns, pageSpots, batterySpots, gate, gateBlock, exitMin, exitMax, seed, Ambience.NONE);
    }
    
    public ArenaMap withAmbience(final Ambience ambience) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, ambience);
    }
    
    public static ArenaMap draft(final String s, final String s2, final String s3, final BlockPos blockPos) {
        return new ArenaMap(s, s2, s3, blockPos, blockPos.offset(-60, -20, -60), blockPos.offset(60, 60, 60), new Loc(blockPos.x() + 0.5, blockPos.y(), blockPos.z() + 0.5, 0.0f), List.of(), List.of(), List.of(), List.of(), "minecraft:air", ArenaMap.UNSET, ArenaMap.UNSET, 0L);
    }
    
    public int fallY() {
        return Math.min(this.min.y(), this.origin.y() - 12);
    }
    
    public Zone roamArea() {
        int max = this.min.x() + 2;
        int max2 = this.min.z() + 2;
        int min = this.max.x() - 2;
        int min2 = this.max.z() - 2;
        if (!this.gate.isEmpty()) {
            final int x = this.gate.get(0).x();
            final int z = this.gate.get(0).z();
            final boolean allMatch = this.gate.stream().allMatch(blockPos -> blockPos.z() == n);
            final boolean allMatch2 = this.gate.stream().allMatch(blockPos2 -> blockPos2.x() == n2);
            final int min3 = Math.min(this.max.z() - z, z - this.min.z());
            final int min4 = Math.min(this.max.x() - x, x - this.min.x());
            if (allMatch && (!allMatch2 || min3 <= min4) && min3 <= 12) {
                if (z * 2 > this.min.z() + this.max.z()) {
                    min2 = Math.min(min2, z - 1);
                }
                else {
                    max2 = Math.max(max2, z + 1);
                }
            }
            else if (allMatch2 && min4 <= 12) {
                if (x * 2 > this.min.x() + this.max.x()) {
                    min = Math.min(min, x - 1);
                }
                else {
                    max = Math.max(max, x + 1);
                }
            }
        }
        return new Zone(new BlockPos(max, this.min.y(), max2), new BlockPos(min, this.max.y(), min2));
    }
    
    public boolean inRoam(final double n, final double n2) {
        final Zone roamArea = this.roamArea();
        return n >= roamArea.min().x() && n < roamArea.max().x() + 1 && n2 >= roamArea.min().z() && n2 < roamArea.max().z() + 1;
    }
    
    public boolean hasExit() {
        return !this.exitMin.equals(ArenaMap.UNSET);
    }
    
    public Zone bounds() {
        return new Zone(this.min, this.max);
    }
    
    public double farthestPageFromExit() {
        if (!this.hasExit()) {
            return 0.0;
        }
        final int min = Math.min(this.exitMin.x(), this.exitMax.x());
        final int max = Math.max(this.exitMin.x(), this.exitMax.x());
        final int min2 = Math.min(this.exitMin.z(), this.exitMax.z());
        final int max2 = Math.max(this.exitMin.z(), this.exitMax.z());
        double max3 = 0.0;
        for (final PageSpot pageSpot : this.pageSpots) {
            max3 = Math.max(max3, Math.hypot(Math.max(0, Math.max(min - pageSpot.pos().x(), pageSpot.pos().x() - max)), Math.max(0, Math.max(min2 - pageSpot.pos().z(), pageSpot.pos().z() - max2))));
        }
        return max3;
    }
    
    public ArenaMap withDisplayName(final String displayName) {
        return new ArenaMap(this.id, displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }
    
    public ArenaMap withSpawns(final List<Loc> spawns) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }
    
    public ArenaMap withPages(final List<PageSpot> pageSpots) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }
    
    public ArenaMap withBatteries(final List<BlockPos> batterySpots) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }
    
    public ArenaMap withGate(final List<BlockPos> gate, final String gateBlock) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, gate, gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }
    
    public ArenaMap withExit(final Zone zone) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, this.min, this.max, this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, zone.min(), zone.max(), this.seed, this.ambience);
    }
    
    public ArenaMap withBounds(final Zone zone) {
        return new ArenaMap(this.id, this.displayName, this.world, this.origin, zone.min(), zone.max(), this.lobby, this.spawns, this.pageSpots, this.batterySpots, this.gate, this.gateBlock, this.exitMin, this.exitMax, this.seed, this.ambience);
    }
    
    public boolean inBounds(final double n, final double n2) {
        return n >= this.min.x() - 1 && n <= this.max.x() + 1 && n2 >= this.min.z() - 1 && n2 <= this.max.z() + 1;
    }
    
    public boolean inExit(final double n, final double n2, final double n3) {
        return this.hasExit() && n >= this.exitMin.x() && n <= this.exitMax.x() + 1 && n2 >= this.exitMin.y() && n2 <= this.exitMax.y() + 1 && n3 >= this.exitMin.z() && n3 <= this.exitMax.z() + 1;
    }
    
    public Map<String, Object> toMap() {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("id", this.id);
        linkedHashMap.put("name", this.displayName);
        linkedHashMap.put("world", this.world);
        linkedHashMap.put("origin", this.origin.serialize());
        linkedHashMap.put("min", this.min.serialize());
        linkedHashMap.put("max", this.max.serialize());
        linkedHashMap.put("lobby", this.lobby.serialize());
        linkedHashMap.put("spawns", this.spawns.stream().map((Function<? super Object, ?>)Loc::serialize).toList());
        linkedHashMap.put("pages", this.pageSpots.stream().map((Function<? super Object, ?>)PageSpot::serialize).toList());
        linkedHashMap.put("batteries", this.batterySpots.stream().map((Function<? super Object, ?>)BlockPos::serialize).toList());
        linkedHashMap.put("gate", this.gate.stream().map((Function<? super Object, ?>)BlockPos::serialize).toList());
        linkedHashMap.put("gate-block", this.gateBlock);
        linkedHashMap.put("exit-min", this.exitMin.serialize());
        linkedHashMap.put("exit-max", this.exitMax.serialize());
        linkedHashMap.put("seed", this.seed);
        this.ambience.writeTo(linkedHashMap);
        return linkedHashMap;
    }
    
    public static ArenaMap fromMap(final Map<String, ?> map) {
        return new ArenaMap(str(map, "id"), str(map, "name"), str(map, "world"), BlockPos.parse(str(map, "origin")), BlockPos.parse(str(map, "min")), BlockPos.parse(str(map, "max")), Loc.parse(str(map, "lobby")), list(map, "spawns").stream().map((Function<? super Object, ? extends Loc>)Loc::parse).toList(), list(map, "pages").stream().map((Function<? super Object, ? extends PageSpot>)PageSpot::parse).toList(), list(map, "batteries").stream().map((Function<? super Object, ? extends BlockPos>)BlockPos::parse).toList(), list(map, "gate").stream().map((Function<? super Object, ? extends BlockPos>)BlockPos::parse).toList(), str(map, "gate-block"), BlockPos.parse(str(map, "exit-min")), BlockPos.parse(str(map, "exit-max")), ((Number)map.get("seed")).longValue(), Ambience.readFrom(map));
    }
    
    private static String str(final Map<String, ?> map, final String s) {
        final Object value = map.get(s);
        if (value == null) {
            throw new IllegalArgumentException("Falta '" + s + "' en el mapa");
        }
        return value.toString();
    }
    
    private static List<String> list(final Map<String, ?> map, final String s) {
        final Object value = map.get(s);
        if (value instanceof final List list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
    
    static {
        UNSET = new BlockPos(0, -9999, 0);
    }
}
