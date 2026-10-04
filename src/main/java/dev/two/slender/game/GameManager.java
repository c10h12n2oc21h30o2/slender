package dev.two.slender.game;

import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.JoinRules;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.MapValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class GameManager {
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final Items items;
    private final HubAccess hub;
    private final Map<String, ArenaMap> maps;
    private final Supplier<World> world;
    private final Map<String, Game> games = new HashMap();
    private final Map<UUID, Game> byPlayer = new HashMap();

    public GameManager(Plugin var1, SlenderConfig var2, Msg var3, Items var4, HubAccess var5, Map<String, ArenaMap> var6, Supplier<World> var7) {
        this.plugin = var1;
        this.cfg = var2;
        this.msg = var3;
        this.items = var4;
        this.hub = var5;
        this.maps = var6;
        this.world = var7;
    }

    public Optional<Game> gameOf(UUID var1) {
        return Optional.ofNullable((Game)this.byPlayer.get(var1));
    }

    public Optional<Game> game(String var1) {
        return Optional.ofNullable((Game)this.games.get(var1));
    }

    public String modeName(String var1) {
        return this.msg.raw("asim".equals(var1) ? "mode-asim" : "mode-coop");
    }

    public void join(Player var1, String var2, String var3) {
        UUID var4 = var1.getUniqueId();
        Game var5 = (Game)this.byPlayer.get(var4);
        if (var5 == null || !var5.map().id().equals(var2) && var5.acceptingPlayers()) {
            ArenaMap var6 = (ArenaMap)this.maps.get(var2);
            World var7 = (World)this.world.get();
            if (var6 != null && var7 != null) {
                if (!MapValidator.playable(var6, this.cfg.pages)) {
                    this.msg.send(var1, "map-incomplete", "map", var6.displayName(), "id", var2);
                } else {
                    Game var8 = (Game)this.games.computeIfAbsent(var2, (var3x) -> {
                        Plugin var10002 = this.plugin;
                        SlenderConfig var10003 = this.cfg;
                        Msg var10004 = this.msg;
                        Items var10005 = this.items;
                        HubAccess var10006 = this.hub;
                        Map var10009 = this.byPlayer;
                        Objects.requireNonNull(var10009);
                        return new Game(var10002, var10003, var10004, var10005, var10006, var6, var7, var10009::remove);
                    });
                    String var9 = var8.size() != 0 && var8.mode() != null ? var8.mode().id() : null;
                    String var10 = JoinRules.resolveMode(var9, var3);
                    switch (JoinRules.check(var9, var3, var8.acceptingPlayers(), var8.size(), this.cfg.maxPlayers(var10))) {
                        case RUNNING:
                            this.msg.send(var1, "game-running", "map", var6.displayName());
                            return;
                        case FULL:
                            this.msg.send(var1, "game-full", "map", var6.displayName());
                            return;
                        case OK_MODE_FORCED:
                            this.msg.send(var1, "mode-forced", "mode", this.modeName(var10));
                        case OK:
                        default:
                            if (var5 != null) {
                                var5.leave(var4, LeaveReason.COMMAND);
                            }

                            this.byPlayer.put(var4, var8);
                            var8.join(var1, Mode.of(var10));
                            this.msg.send(var1, "joined", "map", var6.displayName(), "mode", this.modeName(var10));
                    }
                }
            } else {
                this.msg.send(var1, "map-not-built", "map", var2);
            }
        } else {
            this.msg.send(var1, "already-in-game");
        }
    }

    public void leave(Player var1) {
        Game var2 = (Game)this.byPlayer.get(var1.getUniqueId());
        if (var2 == null) {
            this.msg.send(var1, "not-in-game");
        } else {
            var2.leave(var1.getUniqueId(), LeaveReason.COMMAND);
            this.msg.send(var1, "left");
        }
    }

    public void quit(Player var1) {
        Game var2 = (Game)this.byPlayer.get(var1.getUniqueId());
        if (var2 != null) {
            var2.leave(var1.getUniqueId(), LeaveReason.QUIT);
        }

    }

    public void worldChanged(Player var1) {
        Game var2 = (Game)this.byPlayer.get(var1.getUniqueId());
        if (var2 != null) {
            var2.leave(var1.getUniqueId(), LeaveReason.WORLD_CHANGE);
        }

    }

    public void stop(String var1) {
        Game var2 = (Game)this.games.get(var1);
        if (var2 != null) {
            var2.stop();
        }

    }

    public void drop(String var1) {
        Game var2 = (Game)this.games.remove(var1);
        if (var2 != null) {
            var2.stop();
        }

    }

    public void shutdown() {
        Iterator var1 = (new ArrayList(this.games.values())).iterator();

        while(var1.hasNext()) {
            Game var2 = (Game)var1.next();
            var2.stop();
        }

        this.games.clear();
        this.byPlayer.clear();
    }
}
