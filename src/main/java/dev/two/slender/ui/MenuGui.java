// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.ui;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;
import dev.two.slender.game.Game;
import dev.two.slender.map.MapValidator;
import net.kyori.adventure.text.Component;
import dev.two.slender.map.ArenaMap;
import java.util.List;
import org.bukkit.inventory.Inventory;
import dev.two.slender.config.Msg;
import java.util.function.Function;
import dev.two.slender.map.MapBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import dev.two.slender.SlenderPlugin;

public final class MenuGui
{
    private static final int[] MAP_SLOTS;
    private final SlenderPlugin plugin;
    
    public MenuGui(final SlenderPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void openMaps(final Player player) {
        final Msg msg = this.plugin.msg();
        final MenuHolder menuHolder = new MenuHolder(MenuHolder.Type.MAPS, null);
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)menuHolder, 27, msg.get("menu-title", new String[0]));
        menuHolder.setInventory(inventory);
        final List<String> catalog = this.plugin.catalog();
        for (int n = 0; n < catalog.size() && n < MenuGui.MAP_SLOTS.length; ++n) {
            final String s = catalog.get(n);
            inventory.setItem(MenuGui.MAP_SLOTS[n], this.icon((Material)this.plugin.builds().builder(s).map((Function<? super MapBuilder, ?>)MapBuilder::icon).orElse(Material.FILLED_MAP), "<white>" + this.nameOf(s) + "</white>", this.statusLine(s)));
        }
        player.openInventory(inventory);
    }
    
    public String nameOf(final String other) {
        final ArenaMap arenaMap = this.plugin.maps().get(other);
        if (arenaMap != null) {
            return arenaMap.displayName();
        }
        return this.plugin.builds().builder(other).map((Function<? super MapBuilder, ? extends String>)MapBuilder::displayName).orElse(other);
    }
    
    public Component statusLine(final String s) {
        final Msg msg = this.plugin.msg();
        final ArenaMap arenaMap = this.plugin.maps().get(s);
        final Optional<Game> game = this.plugin.games().game(s);
        final String s2 = (arenaMap == null) ? "state-not-built" : (MapValidator.playable(arenaMap, this.plugin.cfg().pages) ? ((game.isPresent() && !game.get().acceptingPlayers()) ? "state-running" : "state-waiting") : "state-incomplete");
        final int intValue = game.map((Function<? super Object, ? extends Integer>)Game::size).orElse(0);
        final String s3 = (game.isPresent() && game.get().mode() != null) ? game.get().mode().id() : "coop";
        return msg.get("list-line", "map", this.nameOf(s), "state", msg.raw(s2), "count", String.valueOf(intValue), "max", String.valueOf(this.plugin.cfg().maxPlayers(s3)), "mode", (intValue == 0) ? "-" : this.plugin.games().modeName(s3));
    }
    
    public void openModes(final Player player, final String s) {
        final Msg msg = this.plugin.msg();
        final MenuHolder menuHolder = new MenuHolder(MenuHolder.Type.MODES, s);
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)menuHolder, 27, msg.get("mode-title", new String[0]));
        menuHolder.setInventory(inventory);
        inventory.setItem(11, this.icon(Material.PAPER, "<white>" + msg.raw("mode-coop") + "</white>", (Component)null));
        inventory.setItem(15, this.icon(Material.ENDER_EYE, "<dark_red>" + msg.raw("mode-asim") + "</dark_red>", (Component)null));
        player.openInventory(inventory);
    }
    
    private ItemStack icon(final Material material, final String s, final Component component) {
        final ItemStack itemStack = new ItemStack(material);
        itemStack.editMeta(itemMeta -> {
            itemMeta.displayName(this.plugin.msg().parse(s2).decoration(TextDecoration.ITALIC, (boolean)(0 != 0)));
            if (component2 != null) {
                itemMeta.lore((List)List.of(component2.decoration(TextDecoration.ITALIC, (boolean)(0 != 0))));
            }
            return;
        });
        return itemStack;
    }
    
    public void click(final Player player, final MenuHolder menuHolder, final int n) {
        if (menuHolder.type() == MenuHolder.Type.MAPS) {
            final List<String> catalog = this.plugin.catalog();
            for (int n2 = 0; n2 < MenuGui.MAP_SLOTS.length && n2 < catalog.size(); ++n2) {
                if (MenuGui.MAP_SLOTS[n2] == n) {
                    final Optional<Game> game = this.plugin.games().game(catalog.get(n2));
                    if (game.isPresent() && game.get().size() > 0) {
                        this.later(player, () -> this.plugin.hub().join(player2, s, null));
                    }
                    else {
                        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.openModes(player3, s2));
                    }
                }
            }
            return;
        }
        if (((n == 11) ? "coop" : ((n == 15) ? "asim" : null)) != null) {
            this.later(player, () -> this.plugin.hub().join(player4, menuHolder2.mapId(), s3));
        }
    }
    
    private void later(final Player player, final Runnable runnable) {
        Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
            player2.closeInventory();
            runnable2.run();
        });
    }
    
    static {
        MAP_SLOTS = new int[] { 10, 11, 12, 13, 14, 15, 16 };
    }
}
