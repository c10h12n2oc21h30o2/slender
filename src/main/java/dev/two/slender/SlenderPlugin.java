package dev.two.slender;

import dev.two.slender.command.SlenderCommand;
import dev.two.slender.config.ConfigMigration;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import dev.two.slender.game.GameManager;
import dev.two.slender.game.Items;
import dev.two.slender.hub.HubManager;
import dev.two.slender.listener.GameListener;
import dev.two.slender.listener.ProtectionListener;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.BuildManager;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.MapCatalog;
import dev.two.slender.map.MapStore;
import dev.two.slender.map.builders.asylum.AsylumBuilder;
import dev.two.slender.map.builders.cornfield.CornfieldBuilder;
import dev.two.slender.map.builders.forest.ForestBuilder;
import dev.two.slender.map.builders.instituto.InstitutoBuilder;
import dev.two.slender.player.SnapshotStore;
import dev.two.slender.ui.MenuGui;
import dev.two.slender.world.VoidGenerator;
import dev.two.slender.world.WorldSetup;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class SlenderPlugin extends JavaPlugin {
    private SlenderConfig cfg;
    private Msg msg;
    private Items items;
    private SnapshotStore snapshots;
    private MapStore mapStore;
    private final Map<String, ArenaMap> maps = new LinkedHashMap();
    private BuildManager builds;
    private GameManager games;
    private MenuGui menu;
    private HubManager hub;

    public void onEnable() {
        this.saveDefaultConfig();
        this.snapshots = new SnapshotStore(new File(this.getDataFolder(), "snapshots"), this.getLogger());
        this.mapStore = new MapStore(new File(this.getDataFolder(), "maps"), this.getLogger());
        this.loadAll();
        this.getServer().getPluginManager().registerEvents(new GameListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        SlenderCommand var1 = new SlenderCommand(this);
        PluginCommand var2 = (PluginCommand)Objects.requireNonNull(this.getCommand("slender"));
        var2.setExecutor(var1);
        var2.setTabCompleter(var1);
        this.getServer().getScheduler().runTask(this, (Runnable)(() -> {
            WorldSetup.ensure(this, this.cfg.world);
            for (Player online : this.getServer().getOnlinePlayers()) {
                if (this.snapshots.has(online.getUniqueId())) {
                    this.snapshots.restore(online, true);
                }
            }

        }));
    }

    private void loadAll() {
        this.reloadConfig();
        if (ConfigMigration.migrate(this.getConfig())) {
            this.getLogger().info("config.yml actualizado a la versión 3.");
        }

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.cfg = new SlenderConfig(this.getConfig());
        this.msg = new Msg(this.getConfig().getConfigurationSection("messages"));
        this.items = new Items(this, this.msg);
        this.maps.clear();
        this.maps.putAll(this.mapStore.loadAll());
        this.builds = new BuildManager(this, this.cfg, this.msg, this.mapStore, this.maps, java.util.stream.Stream.concat(java.util.stream.Stream.<MapBuilder>of(new ForestBuilder(), new AsylumBuilder(), new CornfieldBuilder(), new InstitutoBuilder()), dev.two.slender.map.builders.hogwarts.HogwartsMaps.safely(dev.two.slender.map.builders.hogwarts.HogwartsMaps::all, this.getLogger()).stream()).toList());
        this.hub = new HubManager(this, this.mapStore);
        this.games = new GameManager(this, this.cfg, this.msg, this.items, this.hub, this.maps, () -> {
            return Bukkit.getWorld(this.cfg.world);
        });
        this.menu = new MenuGui(this);
        this.hub.start();
    }

    public void reloadAll() {
        this.games.shutdown();
        this.hub.shutdown();
        this.builds.cancel();
        this.loadAll();
        WorldSetup.ensure(this, this.cfg.world);
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

    public ChunkGenerator getDefaultWorldGenerator(String var1, String var2) {
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
        return MapCatalog.ordered(this.builds.builders().stream().map(MapBuilder::id).toList(), this.maps.keySet());
    }

    public World world() {
        return Bukkit.getWorld(this.cfg.world);
    }
}
