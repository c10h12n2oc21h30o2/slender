// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.player;

import java.util.function.Function;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SnapshotCodec
{
    private SnapshotCodec() {
    }
    
    public static Map<String, Object> toMap(final PlayerSnapshot playerSnapshot) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("items", playerSnapshot.itemsBase64());
        linkedHashMap.put("level", playerSnapshot.level());
        linkedHashMap.put("exp", (double)playerSnapshot.exp());
        linkedHashMap.put("health", playerSnapshot.health());
        linkedHashMap.put("food", playerSnapshot.food());
        linkedHashMap.put("saturation", (double)playerSnapshot.saturation());
        linkedHashMap.put("game-mode", playerSnapshot.gameMode());
        linkedHashMap.put("walk-speed", (double)playerSnapshot.walkSpeed());
        linkedHashMap.put("fly-speed", (double)playerSnapshot.flySpeed());
        linkedHashMap.put("allow-flight", playerSnapshot.allowFlight());
        linkedHashMap.put("effects", new ArrayList(playerSnapshot.effects()));
        linkedHashMap.put("location", playerSnapshot.location());
        linkedHashMap.put("view-distance", playerSnapshot.viewDistance());
        linkedHashMap.put("send-view-distance", playerSnapshot.sendViewDistance());
        return linkedHashMap;
    }
    
    public static PlayerSnapshot fromMap(final Map<String, ?> map) {
        final String value = String.valueOf(map.get("items"));
        final int intValue = num(map, "level", 0).intValue();
        final float floatValue = num(map, "exp", 0).floatValue();
        final double doubleValue = num(map, "health", 20).doubleValue();
        final int intValue2 = num(map, "food", 20).intValue();
        final float floatValue2 = num(map, "saturation", 5).floatValue();
        final String gameMode = (map.get("game-mode") == null) ? "SURVIVAL" : String.valueOf(map.get("game-mode"));
        final float floatValue3 = num(map, "walk-speed", 0.2).floatValue();
        final float floatValue4 = num(map, "fly-speed", 0.1).floatValue();
        final boolean equals = Boolean.TRUE.equals(map.get("allow-flight"));
        final Object value2 = map.get("effects");
        return new PlayerSnapshot(value, intValue, floatValue, doubleValue, intValue2, floatValue2, gameMode, floatValue3, floatValue4, equals, (value2 instanceof List) ? ((List)value2).stream().map(Object::toString).toList() : List.of(), String.valueOf(map.get("location")), num(map, "view-distance", -1).intValue(), num(map, "send-view-distance", -1).intValue());
    }
    
    private static Number num(final Map<String, ?> map, final String s, final Number n) {
        final Object value = map.get(s);
        return (Number)((value instanceof Number) ? value : n);
    }
}
