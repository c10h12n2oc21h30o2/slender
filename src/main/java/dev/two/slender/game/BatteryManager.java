package dev.two.slender.game;

import dev.two.slender.map.BlockPos;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

public final class BatteryManager {
    public static final String TAG = "slender_battery";
    private final Items items;
    private final Map<UUID, Item> spawned = new HashMap();

    public BatteryManager(Items var1) {
        this.items = var1;
    }

    public void spawn(World var1, List<BlockPos> var2) {
        Iterator var3 = var2.iterator();

        while(var3.hasNext()) {
            BlockPos var4 = (BlockPos)var3.next();
            Item var5 = var1.dropItem(new Location(var1, (double)var4.x() + 0.5, (double)var4.y() + 0.2, (double)var4.z() + 0.5), this.items.make("battery"), (var0) -> {
                var0.setUnlimitedLifetime(true);
                var0.setCanMobPickup(false);
                var0.setPickupDelay(0);
                var0.setWillAge(false);
                var0.setPersistent(false);
                var0.setVelocity(new Vector());
                var0.getScoreboardTags().add("slender_battery");
            });
            this.spawned.put(var5.getUniqueId(), var5);
        }

    }

    public boolean isBattery(Item var1) {
        return this.spawned.containsKey(var1.getUniqueId());
    }

    public void remove(Item var1) {
        this.spawned.remove(var1.getUniqueId());
        var1.remove();
    }

    public void clear() {
        Iterator var1 = this.spawned.values().iterator();

        while(var1.hasNext()) {
            Item var2 = (Item)var1.next();
            if (var2.isValid()) {
                var2.remove();
            }
        }

        this.spawned.clear();
    }
}
