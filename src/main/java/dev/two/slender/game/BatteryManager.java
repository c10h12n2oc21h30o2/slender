// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import java.util.Iterator;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import dev.two.slender.map.BlockPos;
import java.util.List;
import org.bukkit.World;
import java.util.HashMap;
import org.bukkit.entity.Item;
import java.util.UUID;
import java.util.Map;

public final class BatteryManager
{
    public static final String TAG = "slender_battery";
    private final Items items;
    private final Map<UUID, Item> spawned;
    
    public BatteryManager(final Items items) {
        this.spawned = new HashMap<UUID, Item>();
        this.items = items;
    }
    
    public void spawn(final World world, final List<BlockPos> list) {
        for (final BlockPos blockPos : list) {
            final Item dropItem = world.dropItem(new Location(world, blockPos.x() + 0.5, blockPos.y() + 0.2, blockPos.z() + 0.5), this.items.make("battery"), item -> {
                item.setUnlimitedLifetime((boolean)(1 != 0));
                item.setCanMobPickup((boolean)(0 != 0));
                item.setPickupDelay(0);
                item.setWillAge((boolean)(0 != 0));
                item.setPersistent((boolean)(0 != 0));
                item.setVelocity(new Vector());
                item.getScoreboardTags().add("slender_battery");
                return;
            });
            this.spawned.put(dropItem.getUniqueId(), dropItem);
        }
    }
    
    public boolean isBattery(final Item item) {
        return this.spawned.containsKey(item.getUniqueId());
    }
    
    public void remove(final Item item) {
        this.spawned.remove(item.getUniqueId());
        item.remove();
    }
    
    public void clear() {
        for (final Item item : this.spawned.values()) {
            if (item.isValid()) {
                item.remove();
            }
        }
        this.spawned.clear();
    }
}
