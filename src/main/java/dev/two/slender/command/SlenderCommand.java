// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.Hub;
import dev.two.slender.world.WorldSetup;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.Optional;
import java.util.Iterator;
import dev.two.slender.config.Msg;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Collection;
import dev.two.slender.player.SkinCapture;
import org.bukkit.Location;
import dev.two.slender.map.Loc;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.game.Game;
import dev.two.slender.logic.JoinRules;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import dev.two.slender.SlenderPlugin;
import java.util.List;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public final class SlenderCommand implements CommandExecutor, TabCompleter
{
    private static final List<String> PLAYER_SUBS;
    private static final List<String> ADMIN_SUBS;
    private static final String HUB = "vestibulo";
    private final SlenderPlugin plugin;
    private final EditorCommand editor;
    
    public SlenderCommand(final SlenderPlugin plugin) {
        this.plugin = plugin;
        this.editor = new EditorCommand(plugin);
    }
    
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] array) {
        final Msg msg = this.plugin.msg();
        if (array.length == 0) {
            if (commandSender instanceof final Player player) {
                if (this.plugin.hub().inside(player.getUniqueId())) {
                    this.plugin.menu().openMaps(player);
                }
                else {
                    this.plugin.hub().enter(player);
                }
                return true;
            }
            msg.send(commandSender, "usage", new String[0]);
            return true;
        }
        else {
            final String lowerCase = array[0].toLowerCase(Locale.ROOT);
            if (SlenderCommand.ADMIN_SUBS.contains(lowerCase) && !commandSender.hasPermission("slender.admin")) {
                msg.send(commandSender, "no-permission", new String[0]);
                return true;
            }
            final String s2 = lowerCase;
            switch (s2) {
                case "join": {
                    final Player player2 = this.player(commandSender);
                    if (player2 == null) {
                        return true;
                    }
                    if (array.length < 2) {
                        msg.send((CommandSender)player2, "usage", new String[0]);
                        return true;
                    }
                    final String lowerCase2 = array[1].toLowerCase(Locale.ROOT);
                    if (!this.known(lowerCase2)) {
                        msg.send((CommandSender)player2, "unknown-map", "map", lowerCase2, "maps", this.mapList());
                        return true;
                    }
                    this.plugin.hub().join(player2, lowerCase2, (array.length >= 3) ? JoinRules.normalizeMode(array[2]) : null);
                    break;
                }
                case "leave": {
                    final Player player3 = this.player(commandSender);
                    if (player3 == null) {
                        return true;
                    }
                    if (this.plugin.games().gameOf(player3.getUniqueId()).isPresent()) {
                        this.plugin.games().leave(player3);
                    }
                    else if (this.plugin.hub().inside(player3.getUniqueId())) {
                        this.plugin.hub().exit(player3);
                    }
                    else {
                        msg.send((CommandSender)player3, "not-in-game", new String[0]);
                    }
                    break;
                }
                case "list": {
                    msg.send(commandSender, "list-header", new String[0]);
                    final Iterator<String> iterator = this.plugin.catalog().iterator();
                    while (iterator.hasNext()) {
                        commandSender.sendMessage(this.plugin.menu().statusLine(iterator.next()));
                    }
                    break;
                }
                case "build": {
                    this.build(commandSender, array);
                    break;
                }
                case "start": {
                    final String mapArg = this.mapArg(commandSender, array);
                    if (mapArg == null) {
                        return true;
                    }
                    final Optional<Game> game = this.plugin.games().game(mapArg);
                    if (game.isPresent() && game.get().forceStart()) {
                        msg.send(commandSender, "start-forced", "map", mapArg);
                    }
                    else {
                        msg.send(commandSender, "not-enough-players", "min", "1");
                    }
                    break;
                }
                case "stop": {
                    final String mapArg2 = this.mapArg(commandSender, array);
                    if (mapArg2 == null) {
                        return true;
                    }
                    this.plugin.games().stop(mapArg2);
                    msg.send(commandSender, "stop-done", "map", mapArg2);
                    break;
                }
                case "tp": {
                    final Player player4 = this.player(commandSender);
                    final String mapArg3 = this.mapArg(commandSender, array);
                    if (player4 == null || mapArg3 == null) {
                        return true;
                    }
                    final ArenaMap arenaMap = this.plugin.maps().get(mapArg3);
                    final World world = this.plugin.world();
                    if (arenaMap == null || world == null) {
                        msg.send((CommandSender)player4, "map-not-built", "map", mapArg3);
                        return true;
                    }
                    final Loc loc = arenaMap.spawns().isEmpty() ? arenaMap.lobby() : arenaMap.spawns().get(0);
                    player4.teleport(new Location(world, loc.x(), loc.y(), loc.z(), loc.yaw(), 0.0f));
                    msg.send((CommandSender)player4, "tp-done", "map", arenaMap.displayName());
                    break;
                }
                case "reload": {
                    this.plugin.reloadAll();
                    msg.send(commandSender, "reload-done", new String[0]);
                    break;
                }
                case "skin": {
                    if (array.length >= 2) {
                        this.fetchSkin(commandSender, array[1]);
                        return true;
                    }
                    final Player player5 = this.player(commandSender);
                    if (player5 == null) {
                        return true;
                    }
                    final Optional<ProfileProperty> signedTextures = SkinCapture.signedTextures(player5.getPlayerProfile().getProperties());
                    if (signedTextures.isEmpty()) {
                        msg.send((CommandSender)player5, "skin-missing", new String[0]);
                        return true;
                    }
                    this.applySkin(commandSender, signedTextures.get());
                    break;
                }
                case "map": {
                    this.editor.map(commandSender, array);
                    break;
                }
                case "hub": {
                    this.editor.hub(commandSender, array);
                    break;
                }
                default: {
                    msg.send(commandSender, "usage", new String[0]);
                    break;
                }
            }
            return true;
        }
    }
    
    private void fetchSkin(final CommandSender commandSender, final String s2) {
        this.plugin.msg().send(commandSender, "skin-fetching", "name", s2);
        Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            Bukkit.createProfile(s2);
            try {
                playerProfile2.complete(true, true);
            }
            catch (final RuntimeException ex) {}
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                final boolean b;
                final Object o = b ? SkinCapture.signedTextures(playerProfile2.getProperties()) : Optional.empty();
                if (optional.isEmpty()) {
                    this.plugin.msg().send(commandSender2, "skin-not-found", "name", s3);
                }
                else {
                    this.applySkin(commandSender2, optional.get());
                }
            });
        });
    }
    
    private void applySkin(final CommandSender commandSender, final ProfileProperty profileProperty) {
        this.plugin.getConfig().set("slender.body", (Object)"mannequin");
        this.plugin.getConfig().set("slender.skin.player", (Object)"");
        this.plugin.getConfig().set("slender.skin.value", (Object)profileProperty.getValue());
        this.plugin.getConfig().set("slender.skin.signature", (Object)profileProperty.getSignature());
        this.plugin.saveConfig();
        this.plugin.reloadAll();
        this.plugin.msg().send(commandSender, "skin-saved", new String[0]);
    }
    
    private void build(final CommandSender commandSender, final String[] array) {
        final Msg msg = this.plugin.msg();
        if (array.length < 2) {
            msg.send(commandSender, "usage", new String[0]);
            return;
        }
        if (this.plugin.builds().isBuilding()) {
            msg.send(commandSender, "build-busy", new String[0]);
            return;
        }
        final String lowerCase = array[1].toLowerCase(Locale.ROOT);
        final ArrayList list = new ArrayList();
        if (lowerCase.equals("all")) {
            this.plugin.builds().builders().forEach(mapBuilder -> list2.add(mapBuilder.id()));
            list.add("vestibulo");
        }
        else {
            list.add(lowerCase);
        }
        for (final String s : list) {
            if (s.equals("vestibulo")) {
                if (this.plugin.hub().size() > 0) {
                    msg.send(commandSender, "hub-busy", new String[0]);
                    return;
                }
                continue;
            }
            else {
                if (this.plugin.builds().builder(s).isEmpty()) {
                    msg.send(commandSender, "unknown-map", "map", s, "maps", this.mapList());
                    return;
                }
                final Optional<Game> game = this.plugin.games().game(s);
                if (game.isPresent() && game.get().size() > 0) {
                    msg.send(commandSender, "build-game-running", "map", s);
                    return;
                }
                continue;
            }
        }
        World world = this.plugin.world();
        if (world == null) {
            world = WorldSetup.ensure((Plugin)this.plugin, this.plugin.cfg().world);
        }
        this.chain(commandSender, world, list, 0);
    }
    
    private void chain(final CommandSender commandSender, final World world, final List<String> list, final int n) {
        if (n >= list.size()) {
            return;
        }
        final String s = list.get(n);
        final Runnable runnable = () -> this.chain(commandSender2, world2, list2, n2 + 1);
        if (s.equals("vestibulo")) {
            this.plugin.builds().buildHub(world, commandSender, hub -> this.plugin.hub().setHub(hub.withDoorsFrom(this.plugin.hub().hub().orElse(null))), runnable);
            return;
        }
        this.plugin.games().drop(s);
        this.plugin.builds().build(s, world, commandSender, runnable);
    }
    
    private boolean known(final String s) {
        return this.plugin.maps().containsKey(s) || this.plugin.builds().builder(s).isPresent();
    }
    
    private String mapArg(final CommandSender commandSender, final String[] array) {
        if (array.length < 2) {
            this.plugin.msg().send(commandSender, "usage", new String[0]);
            return null;
        }
        final String lowerCase = array[1].toLowerCase(Locale.ROOT);
        if (!this.known(lowerCase)) {
            this.plugin.msg().send(commandSender, "unknown-map", "map", lowerCase, "maps", this.mapList());
            return null;
        }
        return lowerCase;
    }
    
    private Player player(final CommandSender commandSender) {
        if (commandSender instanceof final Player player) {
            return player;
        }
        this.plugin.msg().send(commandSender, "players-only", new String[0]);
        return null;
    }
    
    private String mapList() {
        return String.join(", ", this.plugin.catalog());
    }
    
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] array2) {
        final ArrayList list = new ArrayList();
        final String lowerCase = array2[0].toLowerCase(Locale.ROOT);
        if (array2.length == 1) {
            list.addAll(SlenderCommand.PLAYER_SUBS);
            if (commandSender.hasPermission("slender.admin")) {
                list.addAll(SlenderCommand.ADMIN_SUBS);
            }
        }
        else if (commandSender.hasPermission("slender.admin") && (lowerCase.equals("map") || lowerCase.equals("hub"))) {
            list.addAll(this.editor.complete(array2));
        }
        else if (array2.length == 2 && lowerCase.equals("build")) {
            this.plugin.builds().builders().forEach(mapBuilder -> list2.add(mapBuilder.id()));
            list.add("vestibulo");
            list.add("all");
        }
        else if (array2.length == 2 && List.of("join", "start", "stop", "tp").contains(lowerCase)) {
            list.addAll(this.plugin.catalog());
        }
        else if (array2.length == 3 && lowerCase.equals("join")) {
            list.addAll(List.of("coop", "asim"));
        }
        return list.stream().filter(s2 -> {
            array2[array2.length - 1].toLowerCase(Locale.ROOT);
            return s2.startsWith(prefix);
        }).toList();
    }
    
    static {
        PLAYER_SUBS = List.of("join", "leave", "list");
        ADMIN_SUBS = List.of("build", "start", "stop", "tp", "reload", "skin", "map", "hub");
    }
}
