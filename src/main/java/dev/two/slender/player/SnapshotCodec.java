package dev.two.slender.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SnapshotCodec {
    private SnapshotCodec() {
    }

    public static Map<String, Object> toMap(PlayerSnapshot var0) {
        LinkedHashMap var1 = new LinkedHashMap();
        var1.put("items", var0.itemsBase64());
        var1.put("level", var0.level());
        var1.put("exp", (double)var0.exp());
        var1.put("health", var0.health());
        var1.put("food", var0.food());
        var1.put("saturation", (double)var0.saturation());
        var1.put("game-mode", var0.gameMode());
        var1.put("walk-speed", (double)var0.walkSpeed());
        var1.put("fly-speed", (double)var0.flySpeed());
        var1.put("allow-flight", var0.allowFlight());
        var1.put("effects", new ArrayList(var0.effects()));
        var1.put("location", var0.location());
        var1.put("view-distance", var0.viewDistance());
        var1.put("send-view-distance", var0.sendViewDistance());
        return var1;
    }

    public static PlayerSnapshot fromMap(Map<String, ?> var0) {
        Object var2 = var0.get("effects");
        List<String> effects = var2 instanceof List<?> var1 ? var1.stream().map(Object::toString).toList() : List.of();
        return new PlayerSnapshot(String.valueOf(var0.get("items")), num(var0, "level", 0).intValue(),
                num(var0, "exp", 0).floatValue(), num(var0, "health", 20).doubleValue(), num(var0, "food", 20).intValue(),
                num(var0, "saturation", 5).floatValue(),
                var0.get("game-mode") == null ? "SURVIVAL" : String.valueOf(var0.get("game-mode")),
                num(var0, "walk-speed", 0.2).floatValue(), num(var0, "fly-speed", 0.1).floatValue(),
                Boolean.TRUE.equals(var0.get("allow-flight")), effects, String.valueOf(var0.get("location")),
                num(var0, "view-distance", -1).intValue(), num(var0, "send-view-distance", -1).intValue());
    }

    private static Number num(Map<String, ?> var0, String var1, Number var2) {
        Object var4 = var0.get(var1);
        Number var10000;
        if (var4 instanceof Number var3) {
            var10000 = var3;
        } else {
            var10000 = var2;
        }

        return var10000;
    }
}
