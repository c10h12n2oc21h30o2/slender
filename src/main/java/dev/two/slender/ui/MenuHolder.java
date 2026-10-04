// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.ui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class MenuHolder implements InventoryHolder
{
    private final Type type;
    private final String mapId;
    private Inventory inventory;
    
    public MenuHolder(final Type type, final String mapId) {
        this.type = type;
        this.mapId = mapId;
    }
    
    public Type type() {
        return this.type;
    }
    
    public String mapId() {
        return this.mapId;
    }
    
    void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
    
    public enum Type
    {
        MAPS, 
        MODES;
    }
}
