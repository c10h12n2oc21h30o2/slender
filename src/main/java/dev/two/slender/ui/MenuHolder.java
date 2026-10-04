package dev.two.slender.ui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class MenuHolder implements InventoryHolder {
    private final Type type;
    private final String mapId;
    private Inventory inventory;

    public MenuHolder(Type var1, String var2) {
        this.type = var1;
        this.mapId = var2;
    }

    public Type type() {
        return this.type;
    }

    public String mapId() {
        return this.mapId;
    }

    void setInventory(Inventory var1) {
        this.inventory = var1;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public static enum Type {
        MAPS,
        MODES;
    }
}
