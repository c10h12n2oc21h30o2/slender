package dev.two.slender.game;

import dev.two.slender.config.Msg;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class Items {
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

    public Items(Plugin var1, Msg var2) {
        this.key = new NamespacedKey(var1, "item");
        this.msg = var2;
    }

    public ItemStack make(String var1) {
        Material var10000;
        switch (var1) {
            case "flashlight" -> var10000 = Material.BLAZE_ROD;
            case "teleport" -> var10000 = Material.ENDER_EYE;
            case "whisper" -> var10000 = Material.ECHO_SHARD;
            case "apparition" -> var10000 = Material.PHANTOM_MEMBRANE;
            case "blackout" -> var10000 = Material.BLACK_DYE;
            case "page" -> var10000 = Material.PAPER;
            case "battery" -> var10000 = Material.COPPER_INGOT;
            case "hub-menu" -> var10000 = Material.COMPASS;
            case "hub-exit" -> var10000 = Material.DARK_OAK_DOOR;
            default -> throw new IllegalArgumentException("objeto desconocido: " + var1);
        }

        Material var2 = var10000;
        ItemStack var3 = new ItemStack(var2);
        var3.editMeta((var2x) -> {
            var2x.displayName(this.msg.get("item-" + var1).decoration(TextDecoration.ITALIC, false));
            var2x.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, var1);
        });
        return var3;
    }

    public String idOf(ItemStack var1) {
        return var1 != null && !var1.getType().isAir() && var1.hasItemMeta() ? (String)var1.getItemMeta().getPersistentDataContainer().get(this.key, PersistentDataType.STRING) : null;
    }
}
