package dev.two.slender.command;

import dev.two.slender.SlenderPlugin;
import dev.two.slender.config.Msg;
import dev.two.slender.game.Game;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.EditOps;
import dev.two.slender.map.Hub;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapIds;
import dev.two.slender.map.MapValidator;
import dev.two.slender.map.PageSpot;
import dev.two.slender.map.Zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;

public final class EditorCommand {
    static final List<String> ACTIONS = List.of("spawn", "page", "battery", "gate", "exit", "bounds", "door", "name", "check", "show", "delete");
    private final SlenderPlugin plugin;
    private final Map<String, BlockPos> pos1 = new HashMap();

    public EditorCommand(SlenderPlugin var1) {
        this.plugin = var1;
    }

    private Msg msg() {
        return this.plugin.msg();
    }

    private Player editor(CommandSender var1) {
        if (var1 instanceof Player var2) {
            if (!var2.getWorld().getName().equals(this.plugin.cfg().world)) {
                this.msg().send(var2, "editor-wrong-world", "world", this.plugin.cfg().world);
                return null;
            } else {
                return var2;
            }
        } else {
            this.msg().send(var1, "players-only");
            return null;
        }
    }

    private static BlockPos feet(Location var0) {
        return new BlockPos(var0.getBlockX(), var0.getBlockY(), var0.getBlockZ());
    }

    private static String join(String[] var0, int var1) {
        return String.join(" ", (CharSequence[])Arrays.copyOfRange(var0, var1, var0.length)).trim();
    }

    private ArenaMap save(ArenaMap var1) {
        this.plugin.maps().put(var1.id(), var1);
        this.plugin.mapStore().save(var1);
        this.plugin.games().drop(var1.id());
        return var1;
    }

    public void map(CommandSender var1, String[] var2) {
        Player var3 = this.editor(var1);
        if (var3 != null) {
            if (var2.length < 2) {
                this.msg().send(var3, "editor-usage");
            } else {
                String var4 = var2[1].toLowerCase(Locale.ROOT);
                if (var4.equals("list")) {
                    this.msg().send(var3, "list-header");
                    Iterator var17 = this.plugin.catalog().iterator();

                    while(var17.hasNext()) {
                        String var18 = (String)var17.next();
                        var3.sendMessage((Component)this.plugin.menu().statusLine(var18));
                    }

                } else if (var4.equals("create")) {
                    this.create(var3, var2);
                } else {
                    ArenaMap var5 = (ArenaMap)this.plugin.maps().get(var4);
                    if (var5 == null) {
                        this.msg().send(var3, "unknown-map", "map", var4, "maps", String.join(", ", this.plugin.catalog()));
                    } else if (var2.length < 3) {
                        this.msg().send(var3, "editor-usage");
                    } else {
                        Optional var6 = this.plugin.games().game(var4);
                        if (var6.isPresent() && ((Game)var6.get()).size() > 0) {
                            this.msg().send(var3, "map-busy", "map", var5.displayName());
                        } else {
                            String var7 = var2[2].toLowerCase(Locale.ROOT);
                            String var8 = var2.length >= 4 ? var2[3].toLowerCase(Locale.ROOT) : "";
                            Location var9 = var3.getLocation();
                            ArenaMap var20;
                            int var21;
                            ArenaMap var22;
                            switch (var7) {
                                case "spawn":
                                    if (var8.equals("add")) {
                                        var20 = this.save(var5.withSpawns(EditOps.plus(var5.spawns(), new Loc(var9.getX(), var9.getY(), var9.getZ(), var9.getYaw()))));
                                        this.msg().send(var3, "editor-spawn-added", "count", String.valueOf(var20.spawns().size()));
                                    } else if (var8.equals("clear")) {
                                        this.save(var5.withSpawns(List.of()));
                                        this.msg().send(var3, "editor-spawns-cleared");
                                    } else {
                                        this.msg().send(var3, "editor-usage");
                                    }
                                    break;
                                case "page":
                                    if (var8.equals("add")) {
                                        this.pageAdd(var3, var5, var2.length >= 5 ? join(var2, 4) : "");
                                    } else if (var8.equals("remove")) {
                                        var21 = EditOps.nearest(var5.pageSpots().stream().map(PageSpot::pos).toList(), var9.getX(), var9.getY(), var9.getZ(), 4.0);
                                        if (var21 < 0) {
                                            this.msg().send(var3, "editor-none-near");
                                            return;
                                        }

                                        var22 = this.save(var5.withPages(EditOps.minus(var5.pageSpots(), var21)));
                                        this.msg().send(var3, "editor-page-removed", "count", String.valueOf(var22.pageSpots().size()));
                                    } else {
                                        this.msg().send(var3, "editor-usage");
                                    }
                                    break;
                                case "battery":
                                    if (var8.equals("add")) {
                                        var20 = this.save(var5.withBatteries(EditOps.plus(var5.batterySpots(), feet(var9))));
                                        this.msg().send(var3, "editor-battery-added", "count", String.valueOf(var20.batterySpots().size()));
                                    } else if (var8.equals("remove")) {
                                        var21 = EditOps.nearest(var5.batterySpots(), var9.getX(), var9.getY(), var9.getZ(), 3.0);
                                        if (var21 < 0) {
                                            this.msg().send(var3, "editor-none-near");
                                            return;
                                        }

                                        var22 = this.save(var5.withBatteries(EditOps.minus(var5.batterySpots(), var21)));
                                        this.msg().send(var3, "editor-battery-removed", "count", String.valueOf(var22.batterySpots().size()));
                                    } else {
                                        this.msg().send(var3, "editor-usage");
                                    }
                                    break;
                                case "gate":
                                    if (var8.equals("add")) {
                                        Block var19 = var3.getTargetBlockExact(6);
                                        if (var19 == null || var19.getType().isAir()) {
                                            this.msg().send(var3, "editor-no-target");
                                            return;
                                        }

                                        BlockPos var13 = new BlockPos(var19.getX(), var19.getY(), var19.getZ());
                                        String var14 = var5.gate().isEmpty() ? var19.getBlockData().getAsString() : var5.gateBlock();
                                        List var15 = var5.gate().contains(var13) ? var5.gate() : EditOps.plus(var5.gate(), var13);
                                        ArenaMap var16 = this.save(var5.withGate(var15, var14));
                                        this.msg().send(var3, "editor-gate-added", "count", String.valueOf(var16.gate().size()), "block", var14);
                                    } else if (var8.equals("clear")) {
                                        this.save(var5.withGate(List.of(), "minecraft:air"));
                                        this.msg().send(var3, "editor-gate-cleared");
                                    } else {
                                        this.msg().send(var3, "editor-usage");
                                    }
                                    break;
                                case "exit":
                                case "bounds":
                                case "door":
                                    this.corner(var3, var5, var7, var8, feet(var9));
                                    break;
                                case "name":
                                    String var12 = var2.length >= 4 ? join(var2, 3) : "";
                                    if (var12.isEmpty()) {
                                        this.msg().send(var3, "editor-usage");
                                        return;
                                    }

                                    this.save(var5.withDisplayName(var12));
                                    this.msg().send(var3, "editor-name-set", "name", var12);
                                    break;
                                case "check":
                                    this.check(var3, var5);
                                    break;
                                case "show":
                                    this.show(var3, var5);
                                    break;
                                case "delete":
                                    if (!var8.equals("confirm")) {
                                        this.msg().send(var3, "editor-delete-confirm", "map", var5.id());
                                        return;
                                    }

                                    this.plugin.games().drop(var5.id());
                                    this.plugin.maps().remove(var5.id());
                                    this.plugin.mapStore().delete(var5.id());
                                    this.plugin.hub().hub().filter((var1x) -> {
                                        return var1x.doors().containsKey(var5.id());
                                    }).ifPresent((var2x) -> {
                                        this.plugin.hub().setHub(var2x.withoutDoor(var5.id()));
                                    });
                                    this.msg().send(var3, "editor-deleted", "map", var5.id());
                                    break;
                                default:
                                    this.msg().send(var3, "editor-usage");
                            }

                        }
                    }
                }
            }
        }
    }

    private void create(Player var1, String[] var2) {
        if (var2.length < 4) {
            this.msg().send(var1, "editor-usage");
        } else {
            String var3 = var2[2].toLowerCase(Locale.ROOT);
            if (!MapIds.valid(var3)) {
                this.msg().send(var1, "editor-bad-id");
            } else if (!this.plugin.maps().containsKey(var3) && !this.plugin.builds().builder(var3).isPresent()) {
                this.save(ArenaMap.draft(var3, join(var2, 3), var1.getWorld().getName(), feet(var1.getLocation())));
                this.msg().send(var1, "editor-created", "map", var3);
            } else {
                this.msg().send(var1, "editor-exists", "map", var3);
            }
        }
    }

    private void pageAdd(Player var1, ArenaMap var2, String var3) {
        RayTraceResult var4 = var1.rayTraceBlocks(6.0);
        Block var5 = var4 == null ? null : var4.getHitBlock();
        if (var5 != null && var4.getHitBlockFace() != null && EditOps.wallFace(var4.getHitBlockFace().name())) {
            String var6 = var4.getHitBlockFace().name();
            if (!var5.getRelative(var4.getHitBlockFace()).getType().isAir()) {
                this.msg().send(var1, "editor-page-blocked");
            } else {
                BlockPos var7 = EditOps.front(new BlockPos(var5.getX(), var5.getY(), var5.getZ()), var6);
                String var8 = var3.isEmpty() ? "página " + (var2.pageSpots().size() + 1) : var3;
                ArenaMap var9 = this.save(var2.withPages(EditOps.plus(var2.pageSpots(), new PageSpot(var7, var6, var8))));
                this.msg().send(var1, "editor-page-added", "label", var8, "count", String.valueOf(var9.pageSpots().size()));
            }
        } else {
            this.msg().send(var1, "editor-page-face");
        }
    }

    private void corner(Player var1, ArenaMap var2, String var3, String var4, BlockPos var5) {
        String var10000 = String.valueOf(var1.getUniqueId());
        String var6 = var10000 + ":" + var2.id() + ":" + var3;
        if (var4.equals("pos1")) {
            this.pos1.put(var6, var5);
            this.msg().send(var1, "editor-pos1");
        } else if (!var4.equals("pos2")) {
            this.msg().send(var1, "editor-usage");
        } else {
            BlockPos var7 = (BlockPos)this.pos1.get(var6);
            if (var7 == null) {
                this.msg().send(var1, "editor-need-pos1");
            } else {
                Zone var8 = Zone.of(var7, var5);
                switch (var3) {
                    case "exit":
                        this.save(var2.withExit(var8.tall(3)));
                        this.msg().send(var1, "editor-exit-set");
                        break;
                    case "bounds":
                        this.save(var2.withBounds(var8));
                        this.msg().send(var1, "editor-bounds-set");
                        break;
                    default:
                        Optional var11 = this.plugin.hub().hub();
                        if (var11.isEmpty()) {
                            this.msg().send(var1, "hub-missing");
                            return;
                        }

                        this.plugin.hub().setHub(((Hub)var11.get()).withDoor(var2.id(), var8.tall(3)));
                        this.msg().send(var1, "editor-door-set", "map", var2.displayName());
                }

                this.pos1.remove(var6);
            }
        }
    }

    private void check(Player var1, ArenaMap var2) {
        List var3 = MapValidator.problems(var2, this.plugin.cfg().pages);
        if (var3.isEmpty()) {
            this.msg().send(var1, "editor-check-ok", "map", var2.displayName());
        } else {
            this.msg().send(var1, "editor-check-header", "map", var2.displayName());
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
                MapValidator.Problem var5 = (MapValidator.Problem)var4.next();
                var1.sendMessage((Component)this.msg().get(var5.key(), "count", var5.count(), "need", var5.need()));
            }

        }
    }

    private void show(Player var1, ArenaMap var2) {
        this.msg().send(var1, "editor-show");
        Zone var3 = (Zone)this.plugin.hub().hub().map((var1x) -> {
            return (Zone)var1x.doors().get(var2.id());
        }).orElse(null);
        int[] var4 = new int[]{0};
        BukkitTask[] var5 = new BukkitTask[1];
        var5[0] = Bukkit.getScheduler().runTaskTimer(this.plugin, (Runnable)(() -> {
            if (var1.isOnline() && ++var4[0] <= 20) {
                Iterator var5x = var2.spawns().iterator();

                while(var5x.hasNext()) {
                    Loc var6 = (Loc)var5x.next();
                    dot(var1, Particle.HAPPY_VILLAGER, var6.x(), var6.y() + 0.3, var6.z(), 6);
                }

                var5x = var2.pageSpots().iterator();

                while(var5x.hasNext()) {
                    PageSpot var7 = (PageSpot)var5x.next();
                    dot(var1, Particle.END_ROD, (double)var7.pos().x() + 0.5, (double)var7.pos().y() + 0.5, (double)var7.pos().z() + 0.5, 2);
                }

                var5x = var2.batterySpots().iterator();

                BlockPos var8;
                while(var5x.hasNext()) {
                    var8 = (BlockPos)var5x.next();
                    dot(var1, Particle.WAX_ON, (double)var8.x() + 0.5, (double)var8.y() + 0.3, (double)var8.z() + 0.5, 4);
                }

                var5x = var2.gate().iterator();

                while(var5x.hasNext()) {
                    var8 = (BlockPos)var5x.next();
                    dot(var1, Particle.FLAME, (double)var8.x() + 0.5, (double)var8.y() + 0.5, (double)var8.z() + 0.5, 3);
                }

                if (var2.hasExit()) {
                    outline(var1, new Zone(var2.exitMin(), var2.exitMax()));
                }

                if (var3 != null) {
                    outline(var1, var3);
                }

                border(var1, var2.bounds());
            } else {
                var5[0].cancel();
            }
        }), 0L, 10L);
    }

    private static void dot(Player var0, Particle var1, double var2, double var4, double var6, int var8) {
        Location var9 = var0.getLocation();
        if (!(Math.abs(var2 - var9.getX()) > 48.0) && !(Math.abs(var6 - var9.getZ()) > 48.0)) {
            var0.spawnParticle(var1, var2, var4, var6, var8, 0.15, 0.15, 0.15, 0.0);
        }
    }

    private static void outline(Player var0, Zone var1) {
        int var2 = var1.min().x();
        int var3 = var1.max().x() + 1;
        int var4 = var1.min().z();
        int var5 = var1.max().z() + 1;
        if ((long)(var3 - var2) * (long)(var5 - var4) <= 4000L) {
            for(int var6 = var2; var6 <= var3; ++var6) {
                for(int var7 = var4; var7 <= var5; ++var7) {
                    if (var6 == var2 || var6 == var3 || var7 == var4 || var7 == var5) {
                        dot(var0, Particle.SOUL_FIRE_FLAME, (double)var6, (double)var1.min().y() + 0.1, (double)var7, 1);
                        dot(var0, Particle.SOUL_FIRE_FLAME, (double)var6, (double)var1.max().y() + 0.9, (double)var7, 1);
                    }
                }
            }

        }
    }

    private static void border(Player var0, Zone var1) {
        double var2 = var0.getLocation().getY() + 1.2;

        int var4;
        for(var4 = var1.min().x(); var4 <= var1.max().x() + 1; var4 += 2) {
            dot(var0, Particle.CRIT, (double)var4, var2, (double)var1.min().z(), 1);
            dot(var0, Particle.CRIT, (double)var4, var2, (double)(var1.max().z() + 1), 1);
        }

        for(var4 = var1.min().z(); var4 <= var1.max().z() + 1; var4 += 2) {
            dot(var0, Particle.CRIT, (double)var1.min().x(), var2, (double)var4, 1);
            dot(var0, Particle.CRIT, (double)(var1.max().x() + 1), var2, (double)var4, 1);
        }

    }

    public void hub(CommandSender var1, String[] var2) {
        Player var3 = this.editor(var1);
        if (var3 != null) {
            String var4 = var2.length >= 2 ? var2[1].toLowerCase(Locale.ROOT) : "";
            Location var5 = var3.getLocation();
            switch (var4) {
                case "set":
                    Loc var12 = new Loc(var5.getX(), var5.getY(), var5.getZ(), var5.getYaw());
                    Hub var13 = (Hub)this.plugin.hub().hub().map((var1x) -> {
                        return var1x.withSpawn(var12);
                    }).orElse(new Hub(var3.getWorld().getName(), var12, (Zone)null, Map.of()));
                    this.plugin.hub().setHub(var13);
                    this.msg().send(var3, "hub-set");
                    break;
                case "exit":
                    Optional var8 = this.plugin.hub().hub();
                    if (var8.isEmpty()) {
                        this.msg().send(var3, "hub-missing");
                        return;
                    }

                    String var9 = var2.length >= 3 ? var2[2].toLowerCase(Locale.ROOT) : "";
                    String var10 = String.valueOf(var3.getUniqueId()) + ":hub:exit";
                    if (var9.equals("pos1")) {
                        this.pos1.put(var10, feet(var5));
                        this.msg().send(var3, "editor-pos1");
                    } else if (var9.equals("pos2")) {
                        BlockPos var11 = (BlockPos)this.pos1.remove(var10);
                        if (var11 == null) {
                            this.msg().send(var3, "editor-need-pos1");
                            return;
                        }

                        this.plugin.hub().setHub(((Hub)var8.get()).withExit(Zone.of(var11, feet(var5)).tall(3)));
                        this.msg().send(var3, "hub-exit-set");
                    } else {
                        this.msg().send(var3, "editor-usage");
                    }
                    break;
                default:
                    this.msg().send(var3, "editor-usage");
            }

        }
    }

    public List<String> complete(String[] var1) {
        ArrayList var2 = new ArrayList();
        if (var1[0].equalsIgnoreCase("hub")) {
            if (var1.length == 2) {
                var2.addAll(List.of("set", "exit"));
            } else if (var1.length == 3 && var1[1].equalsIgnoreCase("exit")) {
                var2.addAll(List.of("pos1", "pos2"));
            }

            return var2;
        } else {
            if (var1.length == 2) {
                var2.add("list");
                var2.add("create");
                var2.addAll(this.plugin.maps().keySet());
            } else if (var1.length == 3 && this.plugin.maps().containsKey(var1[1].toLowerCase(Locale.ROOT))) {
                var2.addAll(ACTIONS);
            } else if (var1.length == 4) {
                List var10001;
                switch (var1[2].toLowerCase(Locale.ROOT)) {
                    case "spawn":
                    case "gate":
                        var10001 = List.of("add", "clear");
                        break;
                    case "page":
                    case "battery":
                        var10001 = List.of("add", "remove");
                        break;
                    case "exit":
                    case "bounds":
                    case "door":
                        var10001 = List.of("pos1", "pos2");
                        break;
                    case "delete":
                        var10001 = List.of("confirm");
                        break;
                    default:
                        var10001 = List.of();
                }

                var2.addAll(var10001);
            }

            return var2;
        }
    }
}
