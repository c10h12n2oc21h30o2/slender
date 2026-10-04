package dev.two.slender.ui;

import dev.two.slender.SlenderPlugin;
import dev.two.slender.config.Msg;
import dev.two.slender.game.Game;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.MapValidator;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class MenuGui {
    /** 6 filas: caben 28 mapas (4 filas de 7 sin tocar los bordes); con Hogwarts ya son 11 de serie. */
    static final int SIZE = 54;
    static final int[] MAP_SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43};
    private final SlenderPlugin plugin;

    public MenuGui(SlenderPlugin var1) {
        this.plugin = var1;
    }

    public void openMaps(Player var1) {
        Msg var2 = this.plugin.msg();
        MenuHolder var3 = new MenuHolder(MenuHolder.Type.MAPS, (String)null);
        Inventory var4 = Bukkit.createInventory(var3, SIZE, (Component)var2.get("menu-title"));
        var3.setInventory(var4);
        List var5 = this.plugin.catalog();

        for(int var6 = 0; var6 < var5.size() && var6 < MAP_SLOTS.length; ++var6) {
            String var7 = (String)var5.get(var6);
            Material var8 = (Material)this.plugin.builds().builder(var7).map(MapBuilder::icon).orElse(Material.FILLED_MAP);
            var4.setItem(MAP_SLOTS[var6], this.icon(var8, "<white>" + this.nameOf(var7) + "</white>", this.statusLine(var7)));
        }

        var1.openInventory(var4);
    }

    public String nameOf(String var1) {
        ArenaMap var2 = (ArenaMap)this.plugin.maps().get(var1);
        return var2 != null ? var2.displayName() : (String)this.plugin.builds().builder(var1).map(MapBuilder::displayName).orElse(var1);
    }

    public Component statusLine(String var1) {
        Msg var2 = this.plugin.msg();
        ArenaMap var3 = (ArenaMap)this.plugin.maps().get(var1);
        Optional<Game> var4 = this.plugin.games().game(var1);
        String var5 = var3 == null ? "state-not-built" : (!MapValidator.playable(var3, this.plugin.cfg().pages) ? "state-incomplete" : (var4.isPresent() && !((Game)var4.get()).acceptingPlayers() ? "state-running" : "state-waiting"));
        int var6 = (Integer)var4.map(Game::size).orElse(0);
        String var7 = var4.isPresent() && ((Game)var4.get()).mode() != null ? ((Game)var4.get()).mode().id() : "coop";
        return var2.get("list-line", "map", this.nameOf(var1), "state", var2.raw(var5), "count", String.valueOf(var6), "max", String.valueOf(this.plugin.cfg().maxPlayers(var7)), "mode", var6 == 0 ? "-" : this.plugin.games().modeName(var7));
    }

    public void openModes(Player var1, String var2) {
        Msg var3 = this.plugin.msg();
        MenuHolder var4 = new MenuHolder(MenuHolder.Type.MODES, var2);
        Inventory var5 = Bukkit.createInventory(var4, 27, (Component)var3.get("mode-title"));
        var4.setInventory(var5);
        var5.setItem(11, this.icon(Material.PAPER, "<white>" + var3.raw("mode-coop") + "</white>", (Component)null));
        var5.setItem(15, this.icon(Material.ENDER_EYE, "<dark_red>" + var3.raw("mode-asim") + "</dark_red>", (Component)null));
        var1.openInventory(var5);
    }

    private ItemStack icon(Material var1, String var2, Component var3) {
        ItemStack var4 = new ItemStack(var1);
        var4.editMeta((var3x) -> {
            var3x.displayName(this.plugin.msg().parse(var2).decoration(TextDecoration.ITALIC, false));
            if (var3 != null) {
                var3x.lore(List.of(var3.decoration(TextDecoration.ITALIC, false)));
            }

        });
        return var4;
    }

    public void click(Player var1, MenuHolder var2, int var3) {
        if (var2.type() != MenuHolder.Type.MAPS) {
            String var8 = var3 == 11 ? "coop" : (var3 == 15 ? "asim" : null);
            if (var8 != null) {
                this.later(var1, () -> {
                    this.plugin.hub().join(var1, var2.mapId(), var8);
                });
            }

        } else {
            List var4 = this.plugin.catalog();

            for(int var5 = 0; var5 < MAP_SLOTS.length && var5 < var4.size(); ++var5) {
                if (MAP_SLOTS[var5] == var3) {
                    String var6 = (String)var4.get(var5);
                    Optional var7 = this.plugin.games().game(var6);
                    if (var7.isPresent() && ((Game)var7.get()).size() > 0) {
                        this.later(var1, () -> {
                            this.plugin.hub().join(var1, var6, (String)null);
                        });
                    } else {
                        Bukkit.getScheduler().runTask(this.plugin, (Runnable)(() -> {
                            this.openModes(var1, var6);
                        }));
                    }
                }
            }

        }
    }

    private void later(Player var1, Runnable var2) {
        Bukkit.getScheduler().runTask(this.plugin, (Runnable)(() -> {
            var1.closeInventory();
            var2.run();
        }));
    }
}
