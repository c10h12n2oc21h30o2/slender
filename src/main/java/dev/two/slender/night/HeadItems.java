package dev.two.slender.night;

import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

/** Cabezas de los caídos y cromos de ranas de chocolate (objetos de LA NOCHE ENTERA). */
public final class HeadItems {
    public static final String KEY = "head";
    public static final String CARD_KEY = "cromo";
    public static final String CARD_NAME = "Cromo de rana de chocolate";
    private static final NamespacedKey HEAD = new NamespacedKey("slender", KEY);
    private static final NamespacedKey CARD = new NamespacedKey("slender", CARD_KEY);

    private HeadItems() {
    }

    public static String displayName(String player) {
        return "Cabeza de " + player;
    }

    public static ItemStack make(Player owner) {
        ItemStack it = new ItemStack(Material.PLAYER_HEAD);
        it.editMeta(SkullMeta.class, m -> {
            m.setPlayerProfile(owner.getPlayerProfile());
            m.displayName(Component.text(displayName(owner.getName()), NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
            m.getPersistentDataContainer().set(HEAD, PersistentDataType.STRING, owner.getUniqueId().toString());
        });
        return it;
    }

    public static Optional<UUID> ownerOf(ItemStack it) {
        if (it == null || !it.hasItemMeta()) {
            return Optional.empty();
        }
        String s = it.getItemMeta().getPersistentDataContainer().get(HEAD, PersistentDataType.STRING);
        return s == null ? Optional.empty() : Optional.of(UUID.fromString(s));
    }

    public static ItemStack card() {
        ItemStack it = new ItemStack(Material.PAPER);
        it.editMeta(m -> {
            m.displayName(Component.text(CARD_NAME, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
            m.getPersistentDataContainer().set(CARD, PersistentDataType.BYTE, (byte) 1);
        });
        return it;
    }

    public static boolean isCard(ItemStack it) {
        return it != null && it.hasItemMeta() && it.getItemMeta().getPersistentDataContainer().has(CARD, PersistentDataType.BYTE);
    }

    /** Una cabeza o un cromo (etiqueta de la noche) que intenta coger alguien que no juega la fase: no. */
    public static boolean blockedForOutsider(java.util.Set<String> entityTags) {
        return entityTags.contains(NightPowers.TAG);
    }
}
