// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender;

import org.bukkit.World;
import java.util.Collection;
import dev.two.slender.map.MapCatalog;
import java.util.function.Function;
import dev.two.slender.world.VoidGenerator;
import org.bukkit.generator.ChunkGenerator;
import dev.two.slender.game.HubAccess;
import org.bukkit.Bukkit;
import dev.two.slender.map.MapBuilder;
import java.util.List;
import dev.two.slender.map.builders.instituto.InstitutoBuilder;
import dev.two.slender.map.builders.cornfield.CornfieldBuilder;
import dev.two.slender.map.builders.asylum.AsylumBuilder;
import dev.two.slender.map.builders.forest.ForestBuilder;
import org.bukkit.configuration.ConfigurationSection;
import dev.two.slender.config.ConfigMigration;
import java.util.Iterator;
import org.bukkit.entity.Player;
import dev.two.slender.world.WorldSetup;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;
import java.util.Objects;
import org.bukkit.command.PluginCommand;
import dev.two.slender.command.SlenderCommand;
import dev.two.slender.listener.ProtectionListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import dev.two.slender.listener.GameListener;
import java.io.File;
import java.util.LinkedHashMap;
import dev.two.slender.hub.HubManager;
import dev.two.slender.ui.MenuGui;
import dev.two.slender.game.GameManager;
import dev.two.slender.map.BuildManager;
import dev.two.slender.map.ArenaMap;
import java.util.Map;
import dev.two.slender.map.MapStore;
import dev.two.slender.player.SnapshotStore;
import dev.two.slender.game.Items;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlenderPlugin extends JavaPlugin
{
    private SlenderConfig cfg;
    private Msg msg;
    private Items items;
    private SnapshotStore snapshots;
    private MapStore mapStore;
    private final Map<String, ArenaMap> maps;
    private BuildManager builds;
    private GameManager games;
    private MenuGui menu;
    private HubManager hub;
    
    public SlenderPlugin() {
        this.maps = new LinkedHashMap<String, ArenaMap>();
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        this.snapshots = new SnapshotStore(new File(this.getDataFolder(), "snapshots"), this.getLogger());
        this.mapStore = new MapStore(new File(this.getDataFolder(), "maps"), this.getLogger());
        this.loadAll();
        this.getServer().getPluginManager().registerEvents((Listener)new GameListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ProtectionListener(this), (Plugin)this);
        final SlenderCommand slenderCommand = new SlenderCommand(this);
        final PluginCommand pluginCommand = Objects.requireNonNull(this.getCommand("slender"));
        pluginCommand.setExecutor((CommandExecutor)slenderCommand);
        pluginCommand.setTabCompleter((TabCompleter)slenderCommand);
        this.getServer().getScheduler().runTask((Plugin)this, () -> {
            WorldSetup.ensure((Plugin)this, this.cfg.world);
            this.getServer().getOnlinePlayers().iterator();
            final Iterator iterator;
            while (iterator.hasNext()) {
                final Player player = iterator.next();
                if (this.snapshots.has(player.getUniqueId())) {
                    this.snapshots.restore(player, true);
                }
            }
        });
    }
    
    private void loadAll() {
        this.reloadConfig();
        if (ConfigMigration.migrate((ConfigurationSection)this.getConfig())) {
            this.getLogger().info("config.yml actualizado a la versi\u00f3n 3.");
        }
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.cfg = new SlenderConfig(this.getConfig());
        this.msg = new Msg(this.getConfig().getConfigurationSection("messages"));
        this.items = new Items((Plugin)this, this.msg);
        this.maps.clear();
        this.maps.putAll(this.mapStore.loadAll());
        this.builds = new BuildManager((Plugin)this, this.cfg, this.msg, this.mapStore, this.maps, List.of(new ForestBuilder(), new AsylumBuilder(), new CornfieldBuilder(), new InstitutoBuilder()));
        this.hub = new HubManager(this, this.mapStore);
        this.games = new GameManager((Plugin)this, this.cfg, this.msg, this.items, this.hub, this.maps, () -> Bukkit.getWorld(this.cfg.world));
        this.menu = new MenuGui(this);
        this.hub.start();
    }
    
    public void reloadAll() {
        this.games.shutdown();
        this.hub.shutdown();
        this.builds.cancel();
        this.loadAll();
        WorldSetup.ensure((Plugin)this, this.cfg.world);
    }
    
    public void onDisable() {
        if (this.games != null) {
            this.games.shutdown();
        }
        if (this.hub != null) {
            this.hub.shutdown();
        }
        if (this.builds != null) {
            this.builds.cancel();
        }
    }
    
    public ChunkGenerator getDefaultWorldGenerator(final String s, final String s2) {
        return new VoidGenerator();
    }
    
    public SlenderConfig cfg() {
        return this.cfg;
    }
    
    public Msg msg() {
        return this.msg;
    }
    
    public Items items() {
        return this.items;
    }
    
    public SnapshotStore snapshots() {
        return this.snapshots;
    }
    
    public BuildManager builds() {
        return this.builds;
    }
    
    public GameManager games() {
        return this.games;
    }
    
    public Map<String, ArenaMap> maps() {
        return this.maps;
    }
    
    public MenuGui menu() {
        return this.menu;
    }
    
    public HubManager hub() {
        return this.hub;
    }
    
    public MapStore mapStore() {
        return this.mapStore;
    }
    
    public List<String> catalog() {
        return MapCatalog.ordered(this.builds.builders().stream().map((Function<? super MapBuilder, ? extends String>)MapBuilder::id).toList(), this.maps.keySet());
    }
    
    public World world() {
        return Bukkit.getWorld(this.cfg.world);
    }
}
