package dev.two.slender.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record Hub(String world, Loc spawn, Zone exit, Map<String, Zone> doors) {
    public Hub(String world, Loc spawn, Zone exit, Map<String, Zone> doors) {
        doors = Collections.unmodifiableMap(new LinkedHashMap(doors));
        this.world = world;
        this.spawn = spawn;
        this.exit = exit;
        this.doors = doors;
    }

    public Optional<String> doorAt(double var1, double var3, double var5) {
        Iterator var7 = this.doors.entrySet().iterator();

        Map.Entry var8;
        do {
            if (!var7.hasNext()) {
                return Optional.empty();
            }

            var8 = (Map.Entry)var7.next();
        } while(!((Zone)var8.getValue()).contains(var1, var3, var5));

        return Optional.of((String)var8.getKey());
    }

    public boolean inExit(double var1, double var3, double var5) {
        return this.exit != null && this.exit.contains(var1, var3, var5);
    }

    public Hub withSpawn(Loc var1) {
        return new Hub(this.world, var1, this.exit, this.doors);
    }

    public Hub withExit(Zone var1) {
        return new Hub(this.world, this.spawn, var1, this.doors);
    }

    public Hub withDoor(String var1, Zone var2) {
        LinkedHashMap var3 = new LinkedHashMap(this.doors);
        var3.put(var1, var2);
        return new Hub(this.world, this.spawn, this.exit, var3);
    }

    public Hub withoutDoor(String var1) {
        LinkedHashMap var2 = new LinkedHashMap(this.doors);
        var2.remove(var1);
        return new Hub(this.world, this.spawn, this.exit, var2);
    }

    public Hub withDoorsFrom(Hub var1) {
        if (var1 == null) {
            return this;
        } else {
            LinkedHashMap var2 = new LinkedHashMap(this.doors);
            Map var10000 = var1.doors();
            Objects.requireNonNull(var2);
            var10000.forEach(var2::putIfAbsent);
            return new Hub(this.world, this.spawn, this.exit, var2);
        }
    }

    public Map<String, Object> toMap() {
        LinkedHashMap var1 = new LinkedHashMap();
        var1.put("world", this.world);
        var1.put("spawn", this.spawn.serialize());
        if (this.exit != null) {
            var1.put("exit", this.exit.serialize());
        }

        ArrayList var2 = new ArrayList();
        this.doors.forEach((var1x, var2x) -> {
            var2.add(var1x + "|" + var2x.serialize());
        });
        var1.put("doors", var2);
        return var1;
    }

    public static Hub fromMap(Map<String, ?> var0) {
        Object var1 = var0.get("world");
        Object var2 = var0.get("spawn");
        Object var3 = var0.get("exit");
        Object var4 = var0.get("doors");
        if (var1 != null && var2 != null) {
            LinkedHashMap var5 = new LinkedHashMap();
            if (var4 instanceof List) {
                List var6 = (List)var4;
                Iterator var7 = var6.iterator();

                while(var7.hasNext()) {
                    Object var8 = var7.next();
                    String[] var9 = var8.toString().split("\\|", 2);
                    if (var9.length == 2) {
                        var5.put(var9[0], Zone.parse(var9[1]));
                    }
                }
            }

            return new Hub(var1.toString(), Loc.parse(var2.toString()), var3 == null ? null : Zone.parse(var3.toString()), var5);
        } else {
            throw new IllegalArgumentException("hub.yml incompleto");
        }
    }

    public String world() {
        return this.world;
    }

    public Loc spawn() {
        return this.spawn;
    }

    public Zone exit() {
        return this.exit;
    }

    public Map<String, Zone> doors() {
        return this.doors;
    }
}
