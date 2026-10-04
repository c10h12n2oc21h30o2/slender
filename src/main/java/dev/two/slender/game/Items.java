// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import dev.two.slender.config.Msg;
import org.bukkit.NamespacedKey;

public final class Items
{
    public static final String FLASHLIGHT = "flashlight";
    public static final String TELEPORT = "teleport";
    public static final String WHISPER = "whisper";
    public static final String APPARITION = "apparition";
    public static final String BLACKOUT = "blackout";
    public static final String PAGE = "page";
    public static final String BATTERY = "battery";
    public static final String HUB_MENU = "hub-menu";
    public static final String HUB_EXIT = "hub-exit";
    private final NamespacedKey key;
    private final Msg msg;
    
    public Items(final Plugin plugin, final Msg msg) {
        this.key = new NamespacedKey(plugin, "item");
        this.msg = msg;
    }
    
    public ItemStack make(final String s) {
        final ItemStack itemStack = new ItemStack(switch (s) {
            case "flashlight" -> Material.BLAZE_ROD;
            case "teleport" -> Material.ENDER_EYE;
            case "whisper" -> Material.ECHO_SHARD;
            case "apparition" -> Material.PHANTOM_MEMBRANE;
            case "blackout" -> Material.BLACK_DYE;
            case "page" -> Material.PAPER;
            case "battery" -> Material.COPPER_INGOT;
            case "hub-menu" -> Material.COMPASS;
            case "hub-exit" -> Material.DARK_OAK_DOOR;
            default -> throw new IllegalArgumentException("objeto desconocido: " + s);
        });
        itemStack.editMeta(itemMeta -> {
            itemMeta.displayName(this.msg.get("item-" + s2, new String[0]).decoration(TextDecoration.ITALIC, (boolean)(0 != 0)));
            itemMeta.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, (Object)s2);
            return;
        });
        return itemStack;
    }
    
    public String idOf(final ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir() || !itemStack.hasItemMeta()) {
            return null;
        }
        return (String)itemStack.getItemMeta().getPersistentDataContainer().get(this.key, PersistentDataType.STRING);
    }
}
