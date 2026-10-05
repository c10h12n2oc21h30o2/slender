package dev.two.slender.command;

import dev.two.slender.map.MapBuilder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.two.slender.SlenderPlugin;
import dev.two.slender.config.Msg;
import dev.two.slender.game.Game;
import dev.two.slender.logic.JoinRules;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Hub;
import dev.two.slender.map.Loc;
import dev.two.slender.player.SkinCapture;
import dev.two.slender.world.WorldSetup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class SlenderCommand implements CommandExecutor, TabCompleter {
    private static final List<String> PLAYER_SUBS = List.of("join", "leave", "list");
    private static final List<String> ADMIN_SUBS = List.of("build", "start", "stop", "tp", "reload", "skin", "map", "hub", "noche");
    private static final String HUB = "vestibulo";
    private final SlenderPlugin plugin;
    private final EditorCommand editor;

    public SlenderCommand(SlenderPlugin var1) {
        this.plugin = var1;
        this.editor = new EditorCommand(var1);
    }

    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
        Msg var5 = this.plugin.msg();
        if (var4.length == 0) {
            if (var1 instanceof Player) {
                Player var14 = (Player)var1;
                if (this.plugin.hub().inside(var14.getUniqueId())) {
                    this.plugin.menu().openMaps(var14);
                } else {
                    this.plugin.hub().enter(var14);
                }

                return true;
            } else {
                var5.send(var1, "usage");
                return true;
            }
        } else {
            String var6 = var4[0].toLowerCase(Locale.ROOT);
            if (ADMIN_SUBS.contains(var6) && !var1.hasPermission("slender.admin")) {
                var5.send(var1, "no-permission");
                return true;
            } else {
                Player var9;
                Optional var10;
                String var15;
                String var17;
                switch (var6) {
                    case "join":
                        var9 = this.player(var1);
                        if (var9 == null) {
                            return true;
                        }

                        if (var4.length < 2) {
                            var5.send(var9, "usage");
                            return true;
                        }

                        var17 = var4[1].toLowerCase(Locale.ROOT);
                        if (!this.known(var17)) {
                            var5.send(var9, "unknown-map", "map", var17, "maps", this.mapList());
                            return true;
                        }

                        this.plugin.hub().join(var9, var17, var4.length >= 3 ? JoinRules.normalizeMode(var4[2]) : null);
                        break;
                    case "leave":
                        var9 = this.player(var1);
                        if (var9 == null) {
                            return true;
                        }

                        if (this.plugin.games().gameOf(var9.getUniqueId()).isPresent()) {
                            this.plugin.games().leave(var9);
                        } else if (this.plugin.hub().inside(var9.getUniqueId())) {
                            this.plugin.hub().exit(var9);
                        } else {
                            var5.send(var9, "not-in-game");
                        }
                        break;
                    case "list":
                        var5.send(var1, "list-header");
                        Iterator var16 = this.plugin.catalog().iterator();

                        while(var16.hasNext()) {
                            var17 = (String)var16.next();
                            var1.sendMessage(this.plugin.menu().statusLine(var17));
                        }

                        return true;
                    case "build":
                        this.build(var1, var4);
                        break;
                    case "start":
                        var15 = this.mapArg(var1, var4);
                        if (var15 == null) {
                            return true;
                        }

                        var10 = this.plugin.games().game(var15);
                        if (var10.isPresent() && ((Game)var10.get()).forceStart()) {
                            var5.send(var1, "start-forced", "map", var15);
                        } else {
                            var5.send(var1, "not-enough-players", "min", "1");
                        }
                        break;
                    case "stop":
                        var15 = this.mapArg(var1, var4);
                        if (var15 == null) {
                            return true;
                        }

                        this.plugin.games().stop(var15);
                        var5.send(var1, "stop-done", "map", var15);
                        break;
                    case "tp":
                        var9 = this.player(var1);
                        var17 = this.mapArg(var1, var4);
                        if (var9 == null || var17 == null) {
                            return true;
                        }

                        ArenaMap var11 = (ArenaMap)this.plugin.maps().get(var17);
                        World var12 = this.plugin.world();
                        if (var11 == null || var12 == null) {
                            var5.send(var9, "map-not-built", "map", var17);
                            return true;
                        }

                        Loc var13 = var11.spawns().isEmpty() ? var11.lobby() : (Loc)var11.spawns().get(0);
                        var9.teleport(new Location(var12, var13.x(), var13.y(), var13.z(), var13.yaw(), 0.0F));
                        var5.send(var9, "tp-done", "map", var11.displayName());
                        break;
                    case "reload":
                        this.plugin.reloadAll();
                        var5.send(var1, "reload-done");
                        break;
                    case "skin":
                        if (var4.length >= 2) {
                            this.fetchSkin(var1, var4[1]);
                            return true;
                        }

                        var9 = this.player(var1);
                        if (var9 == null) {
                            return true;
                        }

                        var10 = SkinCapture.signedTextures(var9.getPlayerProfile().getProperties());
                        if (var10.isEmpty()) {
                            var5.send(var9, "skin-missing");
                            return true;
                        }

                        this.applySkin(var1, (ProfileProperty)var10.get());
                        break;
                    case "map":
                        this.editor.map(var1, var4);
                        break;
                    case "hub":
                        this.editor.hub(var1, var4);
                        break;
                    case "noche":
                        this.night(var1, var4);
                        break;
                    default:
                        var5.send(var1, "usage");
                }

                return true;
            }
        }
    }

    /** /slender noche estado | terminar | fase N */
    private void night(CommandSender s, String[] a) {
        dev.two.slender.night.NightManager n = this.plugin.night();
        String sub = a.length >= 2 ? a[1].toLowerCase(Locale.ROOT) : "";
        switch (sub) {
            case "estado" -> {
                String st = n.status();
                if (st == null) {
                    this.plugin.msg().send(s, "noche-ninguna");
                } else {
                    s.sendMessage(this.plugin.msg().parse(st));
                }
            }
            case "terminar" -> {
                if (n.active().isEmpty()) {
                    this.plugin.msg().send(s, "noche-ninguna");
                } else {
                    n.terminate();
                    this.plugin.msg().send(s, "noche-terminada");
                }
            }
            case "fase" -> {
                int phase;
                try {
                    phase = a.length >= 3 ? Integer.parseInt(a[2]) : -1;
                } catch (NumberFormatException e) {
                    phase = -1;
                }
                if (n.setPhase(phase)) {
                    this.plugin.msg().send(s, "noche-fase-puesta", "phase", String.valueOf(phase));
                } else if (n.active().isEmpty()) {
                    this.plugin.msg().send(s, "noche-ninguna");
                } else {
                    this.plugin.msg().send(s, "noche-uso");
                }
            }
            default -> this.plugin.msg().send(s, "noche-uso");
        }
    }

    private void fetchSkin(CommandSender var1, String var2) {
        this.plugin.msg().send(var1, "skin-fetching", "name", var2);
        PlayerProfile var3 = Bukkit.createProfile(var2);
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, (Runnable)(() -> {
            boolean var4;
            try {
                var4 = var3.complete(true, true);
            } catch (RuntimeException var6) {
                var4 = false;
            }

            Optional var5 = var4 ? SkinCapture.signedTextures(var3.getProperties()) : Optional.empty();
            Bukkit.getScheduler().runTask(this.plugin, (Runnable)(() -> {
                if (var5.isEmpty()) {
                    this.plugin.msg().send(var1, "skin-not-found", "name", var2);
                } else {
                    this.applySkin(var1, (ProfileProperty)var5.get());
                }

            }));
        }));
    }

    private void applySkin(CommandSender var1, ProfileProperty var2) {
        this.plugin.getConfig().set("slender.body", "mannequin");
        this.plugin.getConfig().set("slender.skin.player", "");
        this.plugin.getConfig().set("slender.skin.value", var2.getValue());
        this.plugin.getConfig().set("slender.skin.signature", var2.getSignature());
        this.plugin.saveConfig();
        this.plugin.reloadAll();
        this.plugin.msg().send(var1, "skin-saved");
    }

    private void build(CommandSender var1, String[] var2) {
        Msg var3 = this.plugin.msg();
        if (var2.length < 2) {
            var3.send(var1, "usage");
        } else if (this.plugin.builds().isBuilding()) {
            var3.send(var1, "build-busy");
        } else {
            String var4 = var2[1].toLowerCase(Locale.ROOT);
            List<String> var5 = expandBuildTargets(var4, this.plugin.builds().builders().stream().map(MapBuilder::id).toList());

            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
                String var7 = (String)var6.next();
                if (var7.equals("vestibulo")) {
                    if (this.plugin.hub().size() > 0) {
                        var3.send(var1, "hub-busy");
                        return;
                    }
                } else {
                    if (this.plugin.builds().builder(var7).isEmpty()) {
                        var3.send(var1, "unknown-map", "map", var7, "maps", this.mapList());
                        return;
                    }

                    Optional var8 = this.plugin.games().game(var7);
                    if (var8.isPresent() && ((Game)var8.get()).size() > 0) {
                        var3.send(var1, "build-game-running", "map", var7);
                        return;
                    }
                }
            }

            World var9 = this.plugin.world();
            if (var9 == null) {
                var9 = WorldSetup.ensure(this.plugin, this.plugin.cfg().world);
            }

            this.chain(var1, var9, var5, 0);
        }
    }

    private void chain(CommandSender var1, World var2, List<String> var3, int var4) {
        if (var4 < var3.size()) {
            String var5 = (String)var3.get(var4);
            Runnable var6 = () -> {
                this.chain(var1, var2, var3, var4 + 1);
            };
            if (var5.equals("vestibulo")) {
                this.plugin.builds().buildHub(var2, var1, (var1x) -> {
                    this.plugin.hub().setHub(var1x.withDoorsFrom((Hub)this.plugin.hub().hub().orElse(null)));
                }, var6);
            } else {
                this.plugin.games().drop(var5);
                this.plugin.builds().build(var5, var2, var1, var6);
            }
        }
    }

    private boolean known(String var1) {
        return this.plugin.maps().containsKey(var1) || this.plugin.builds().builder(var1).isPresent();
    }

    private String mapArg(CommandSender var1, String[] var2) {
        if (var2.length < 2) {
            this.plugin.msg().send(var1, "usage");
            return null;
        } else {
            String var3 = var2[1].toLowerCase(Locale.ROOT);
            if (!this.known(var3)) {
                this.plugin.msg().send(var1, "unknown-map", "map", var3, "maps", this.mapList());
                return null;
            } else {
                return var3;
            }
        }
    }

    private Player player(CommandSender var1) {
        if (var1 instanceof Player var2) {
            return var2;
        } else {
            this.plugin.msg().send(var1, "players-only");
            return null;
        }
    }

    private String mapList() {
        return String.join(", ", this.plugin.catalog());
    }

    public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
        ArrayList<String> var5 = new ArrayList<>();
        String var6 = var4[0].toLowerCase(Locale.ROOT);
        if (var4.length == 1) {
            var5.addAll(PLAYER_SUBS);
            if (var1.hasPermission("slender.admin")) {
                var5.addAll(ADMIN_SUBS);
            }
        } else if (!var1.hasPermission("slender.admin") || !var6.equals("map") && !var6.equals("hub")) {
            if (var4.length == 2 && var6.equals("build")) {
                this.plugin.builds().builders().forEach((var1x) -> {
                    var5.add(var1x.id());
                });
                var5.add("vestibulo");
                var5.add("all");
                var5.add("hogwarts");
            } else if (var4.length == 2 && List.of("join", "start", "stop", "tp").contains(var6)) {
                var5.addAll(this.plugin.catalog());
            } else if (var4.length == 3 && var6.equals("join")) {
                var5.addAll(List.of("coop", "asim"));
            } else if (var4.length == 2 && var6.equals("noche") && var1.hasPermission("slender.admin")) {
                var5.addAll(List.of("estado", "terminar", "fase"));
            } else if (var4.length == 3 && var6.equals("noche") && var4[1].equalsIgnoreCase("fase")) {
                for (int i = 1; i <= 7; i++) {
                    var5.add(String.valueOf(i));
                }
            }
        } else {
            var5.addAll(this.editor.complete(var4));
        }

        String var7 = var4[var4.length - 1].toLowerCase(Locale.ROOT);
        return var5.stream().filter((var1x) -> {
            return var1x.startsWith(var7);
        }).toList();
    }

    /** Qué mapas construye `/slender build <arg>`: "all" = todos más la estación; "hogwarts" = sus 7 secciones. */
    public static List<String> expandBuildTargets(String arg, List<String> allIds) {
        List<String> out = new ArrayList<>();
        if (arg.equals("all")) {
            out.addAll(allIds);
            out.add("vestibulo");
        } else if (arg.equals("hogwarts")) {
            out.addAll(dev.two.slender.map.builders.hogwarts.HogwartsMaps.IDS);
        } else {
            out.add(arg);
        }
        return out;
    }
}
