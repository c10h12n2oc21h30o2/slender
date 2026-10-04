// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.Objects;
import java.util.Iterator;
import java.util.Optional;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

record Hub(String world, Loc spawn, Zone exit, Map<String, Zone> doors) {
    public Hub(final String world, final Loc spawn, final Zone exit, final Map<String, Zone> doors) {
        final Map<Object, Object> unmodifiableMap = (Map<Object, Object>)Collections.unmodifiableMap((Map<? extends String, ? extends Zone>)new LinkedHashMap<String, Zone>(doors));
        this.world = world;
        this.spawn = spawn;
        this.exit = exit;
        this.doors = (Map<String, Zone>)unmodifiableMap;
    }
    
    public Optional<String> doorAt(final double n, final double n2, final double n3) {
        for (final Map.Entry entry : this.doors.entrySet()) {
            if (((Zone)entry.getValue()).contains(n, n2, n3)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }
    
    public boolean inExit(final double n, final double n2, final double n3) {
        return this.exit != null && this.exit.contains(n, n2, n3);
    }
    
    public Hub withSpawn(final Loc spawn) {
        return new Hub(this.world, spawn, this.exit, this.doors);
    }
    
    public Hub withExit(final Zone exit) {
        return new Hub(this.world, this.spawn, exit, this.doors);
    }
    
    public Hub withDoor(final String s, final Zone zone) {
        final LinkedHashMap doors = new LinkedHashMap((Map<? extends K, ? extends V>)this.doors);
        doors.put(s, zone);
        return new Hub(this.world, this.spawn, this.exit, doors);
    }
    
    public Hub withoutDoor(final String s) {
        final LinkedHashMap doors = new LinkedHashMap((Map<? extends K, ? extends V>)this.doors);
        doors.remove(s);
        return new Hub(this.world, this.spawn, this.exit, doors);
    }
    
    public Hub withDoorsFrom(final Hub hub) {
        if (hub == null) {
            return this;
        }
        final LinkedHashMap doors = new LinkedHashMap((Map<? extends K, ? extends V>)this.doors);
        final Map<String, Zone> doors2 = hub.doors();
        final LinkedHashMap obj = doors;
        Objects.requireNonNull(obj);
        doors2.forEach(obj::putIfAbsent);
        return new Hub(this.world, this.spawn, this.exit, doors);
    }
    
    public Map<String, Object> toMap() {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("world", this.world);
        linkedHashMap.put("spawn", this.spawn.serialize());
        if (this.exit != null) {
            linkedHashMap.put("exit", this.exit.serialize());
        }
        final ArrayList list = new ArrayList();
        this.doors.forEach((s, zone) -> list2.add(s + "|" + zone.serialize()));
        linkedHashMap.put("doors", list);
        return linkedHashMap;
    }
    
    public static Hub fromMap(final Map<String, ?> map) {
        final Object value = map.get("world");
        final Object value2 = map.get("spawn");
        final Object value3 = map.get("exit");
        final Object value4 = map.get("doors");
        if (value == null || value2 == null) {
            throw new IllegalArgumentException("hub.yml incompleto");
        }
        final LinkedHashMap doors = new LinkedHashMap();
        if (value4 instanceof final List list) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                final String[] split = iterator.next().toString().split("\\|", 2);
                if (split.length == 2) {
                    doors.put(split[0], Zone.parse(split[1]));
                }
            }
        }
        return new Hub(value.toString(), Loc.parse(value2.toString()), (value3 == null) ? null : Zone.parse(value3.toString()), doors);
    }
}
