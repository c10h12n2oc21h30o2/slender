// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.command;

import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import dev.two.slender.map.MapValidator;
import dev.two.slender.map.Hub;
import dev.two.slender.map.Zone;
import org.bukkit.util.RayTraceResult;
import dev.two.slender.map.MapIds;
import org.bukkit.block.Block;
import java.util.Optional;
import java.util.Iterator;
import java.util.function.Function;
import dev.two.slender.map.PageSpot;
import dev.two.slender.map.EditOps;
import dev.two.slender.map.Loc;
import dev.two.slender.game.Game;
import java.util.Locale;
import dev.two.slender.map.ArenaMap;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import dev.two.slender.config.Msg;
import java.util.HashMap;
import dev.two.slender.map.BlockPos;
import java.util.Map;
import dev.two.slender.SlenderPlugin;
import java.util.List;

public final class EditorCommand
{
    static final List<String> ACTIONS;
    private final SlenderPlugin plugin;
    private final Map<String, BlockPos> pos1;
    
    public EditorCommand(final SlenderPlugin plugin) {
        this.pos1 = new HashMap<String, BlockPos>();
        this.plugin = plugin;
    }
    
    private Msg msg() {
        return this.plugin.msg();
    }
    
    private Player editor(final CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            this.msg().send(commandSender, "players-only", new String[0]);
            return null;
        }
        final Player player = (Player)commandSender;
        if (!player.getWorld().getName().equals(this.plugin.cfg().world)) {
            this.msg().send((CommandSender)player, "editor-wrong-world", "world", this.plugin.cfg().world);
            return null;
        }
        return player;
    }
    
    private static BlockPos feet(final Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    private static String join(final String[] original, final int from) {
        return String.join(" ", (CharSequence[])Arrays.copyOfRange(original, from, original.length)).trim();
    }
    
    private ArenaMap save(final ArenaMap arenaMap) {
        this.plugin.maps().put(arenaMap.id(), arenaMap);
        this.plugin.mapStore().save(arenaMap);
        this.plugin.games().drop(arenaMap.id());
        return arenaMap;
    }
    
    public void map(final CommandSender commandSender, final String[] array) {
        final Player editor = this.editor(commandSender);
        if (editor == null) {
            return;
        }
        if (array.length < 2) {
            this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
            return;
        }
        final String lowerCase = array[1].toLowerCase(Locale.ROOT);
        if (lowerCase.equals("list")) {
            this.msg().send((CommandSender)editor, "list-header", new String[0]);
            final Iterator<String> iterator = this.plugin.catalog().iterator();
            while (iterator.hasNext()) {
                editor.sendMessage(this.plugin.menu().statusLine(iterator.next()));
            }
            return;
        }
        if (lowerCase.equals("create")) {
            this.create(editor, array);
            return;
        }
        final ArenaMap arenaMap = this.plugin.maps().get(lowerCase);
        if (arenaMap == null) {
            this.msg().send((CommandSender)editor, "unknown-map", "map", lowerCase, "maps", String.join(", ", this.plugin.catalog()));
            return;
        }
        if (array.length < 3) {
            this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
            return;
        }
        final Optional<Game> game = this.plugin.games().game(lowerCase);
        if (game.isPresent() && game.get().size() > 0) {
            this.msg().send((CommandSender)editor, "map-busy", "map", arenaMap.displayName());
            return;
        }
        final String lowerCase2 = array[2].toLowerCase(Locale.ROOT);
        final String s = (array.length >= 4) ? array[3].toLowerCase(Locale.ROOT) : "";
        final Location location = editor.getLocation();
        final String s2 = lowerCase2;
        switch (s2) {
            case "spawn": {
                if (s.equals("add")) {
                    this.msg().send((CommandSender)editor, "editor-spawn-added", "count", String.valueOf(this.save(arenaMap.withSpawns(EditOps.plus(arenaMap.spawns(), new Loc(location.getX(), location.getY(), location.getZ(), location.getYaw())))).spawns().size()));
                    break;
                }
                if (s.equals("clear")) {
                    this.save(arenaMap.withSpawns(List.of()));
                    this.msg().send((CommandSender)editor, "editor-spawns-cleared", new String[0]);
                    break;
                }
                this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                break;
            }
            case "page": {
                if (s.equals("add")) {
                    this.pageAdd(editor, arenaMap, (array.length >= 5) ? join(array, 4) : "");
                    break;
                }
                if (!s.equals("remove")) {
                    this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                    break;
                }
                final int nearest = EditOps.nearest(arenaMap.pageSpots().stream().map((Function<? super Object, ? extends BlockPos>)PageSpot::pos).toList(), location.getX(), location.getY(), location.getZ(), 4.0);
                if (nearest < 0) {
                    this.msg().send((CommandSender)editor, "editor-none-near", new String[0]);
                    return;
                }
                this.msg().send((CommandSender)editor, "editor-page-removed", "count", String.valueOf(this.save(arenaMap.withPages(EditOps.minus(arenaMap.pageSpots(), nearest))).pageSpots().size()));
                break;
            }
            case "battery": {
                if (s.equals("add")) {
                    this.msg().send((CommandSender)editor, "editor-battery-added", "count", String.valueOf(this.save(arenaMap.withBatteries(EditOps.plus(arenaMap.batterySpots(), feet(location)))).batterySpots().size()));
                    break;
                }
                if (!s.equals("remove")) {
                    this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                    break;
                }
                final int nearest2 = EditOps.nearest(arenaMap.batterySpots(), location.getX(), location.getY(), location.getZ(), 3.0);
                if (nearest2 < 0) {
                    this.msg().send((CommandSender)editor, "editor-none-near", new String[0]);
                    return;
                }
                this.msg().send((CommandSender)editor, "editor-battery-removed", "count", String.valueOf(this.save(arenaMap.withBatteries(EditOps.minus(arenaMap.batterySpots(), nearest2))).batterySpots().size()));
                break;
            }
            case "gate": {
                if (s.equals("add")) {
                    final Block targetBlockExact = editor.getTargetBlockExact(6);
                    if (targetBlockExact == null || targetBlockExact.getType().isAir()) {
                        this.msg().send((CommandSender)editor, "editor-no-target", new String[0]);
                        return;
                    }
                    final BlockPos blockPos = new BlockPos(targetBlockExact.getX(), targetBlockExact.getY(), targetBlockExact.getZ());
                    final String s3 = arenaMap.gate().isEmpty() ? targetBlockExact.getBlockData().getAsString() : arenaMap.gateBlock();
                    this.msg().send((CommandSender)editor, "editor-gate-added", "count", String.valueOf(this.save(arenaMap.withGate(arenaMap.gate().contains(blockPos) ? arenaMap.gate() : EditOps.plus(arenaMap.gate(), blockPos), s3)).gate().size()), "block", s3);
                    break;
                }
                else {
                    if (s.equals("clear")) {
                        this.save(arenaMap.withGate(List.of(), "minecraft:air"));
                        this.msg().send((CommandSender)editor, "editor-gate-cleared", new String[0]);
                        break;
                    }
                    this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                    break;
                }
                break;
            }
            case "exit":
            case "bounds":
            case "door": {
                this.corner(editor, arenaMap, lowerCase2, s, feet(location));
                break;
            }
            case "name": {
                final String s4 = (array.length >= 4) ? join(array, 3) : "";
                if (s4.isEmpty()) {
                    this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                    return;
                }
                this.save(arenaMap.withDisplayName(s4));
                this.msg().send((CommandSender)editor, "editor-name-set", "name", s4);
                break;
            }
            case "check": {
                this.check(editor, arenaMap);
                break;
            }
            case "show": {
                this.show(editor, arenaMap);
                break;
            }
            case "delete": {
                if (!s.equals("confirm")) {
                    this.msg().send((CommandSender)editor, "editor-delete-confirm", "map", arenaMap.id());
                    return;
                }
                this.plugin.games().drop(arenaMap.id());
                this.plugin.maps().remove(arenaMap.id());
                this.plugin.mapStore().delete(arenaMap.id());
                this.plugin.hub().hub().filter(hub -> hub.doors().containsKey(arenaMap2.id())).ifPresent(hub2 -> this.plugin.hub().setHub(hub2.withoutDoor(arenaMap3.id())));
                this.msg().send((CommandSender)editor, "editor-deleted", "map", arenaMap.id());
                break;
            }
            default: {
                this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                break;
            }
        }
    }
    
    private void create(final Player player, final String[] array) {
        if (array.length < 4) {
            this.msg().send((CommandSender)player, "editor-usage", new String[0]);
            return;
        }
        final String lowerCase = array[2].toLowerCase(Locale.ROOT);
        if (!MapIds.valid(lowerCase)) {
            this.msg().send((CommandSender)player, "editor-bad-id", new String[0]);
            return;
        }
        if (this.plugin.maps().containsKey(lowerCase) || this.plugin.builds().builder(lowerCase).isPresent()) {
            this.msg().send((CommandSender)player, "editor-exists", "map", lowerCase);
            return;
        }
        this.save(ArenaMap.draft(lowerCase, join(array, 3), player.getWorld().getName(), feet(player.getLocation())));
        this.msg().send((CommandSender)player, "editor-created", "map", lowerCase);
    }
    
    private void pageAdd(final Player player, final ArenaMap arenaMap, final String s) {
        final RayTraceResult rayTraceBlocks = player.rayTraceBlocks(6.0);
        final Block block = (rayTraceBlocks == null) ? null : rayTraceBlocks.getHitBlock();
        if (block == null || rayTraceBlocks.getHitBlockFace() == null || !EditOps.wallFace(rayTraceBlocks.getHitBlockFace().name())) {
            this.msg().send((CommandSender)player, "editor-page-face", new String[0]);
            return;
        }
        final String name = rayTraceBlocks.getHitBlockFace().name();
        if (!block.getRelative(rayTraceBlocks.getHitBlockFace()).getType().isAir()) {
            this.msg().send((CommandSender)player, "editor-page-blocked", new String[0]);
            return;
        }
        final BlockPos front = EditOps.front(new BlockPos(block.getX(), block.getY(), block.getZ()), name);
        final String label = s.isEmpty() ? ("p\u00e1gina " + (arenaMap.pageSpots().size() + 1)) : s;
        this.msg().send((CommandSender)player, "editor-page-added", "label", label, "count", String.valueOf(this.save(arenaMap.withPages(EditOps.plus(arenaMap.pageSpots(), new PageSpot(front, name, label)))).pageSpots().size()));
    }
    
    private void corner(final Player player, final ArenaMap arenaMap, final String s, final String s2, final BlockPos blockPos) {
        final String s3 = String.valueOf(player.getUniqueId()) + ":" + arenaMap.id() + ":" + s;
        if (s2.equals("pos1")) {
            this.pos1.put(s3, blockPos);
            this.msg().send((CommandSender)player, "editor-pos1", new String[0]);
            return;
        }
        if (!s2.equals("pos2")) {
            this.msg().send((CommandSender)player, "editor-usage", new String[0]);
            return;
        }
        final BlockPos blockPos2 = this.pos1.get(s3);
        if (blockPos2 == null) {
            this.msg().send((CommandSender)player, "editor-need-pos1", new String[0]);
            return;
        }
        final Zone of = Zone.of(blockPos2, blockPos);
        switch (s) {
            case "exit": {
                this.save(arenaMap.withExit(of.tall(3)));
                this.msg().send((CommandSender)player, "editor-exit-set", new String[0]);
                break;
            }
            case "bounds": {
                this.save(arenaMap.withBounds(of));
                this.msg().send((CommandSender)player, "editor-bounds-set", new String[0]);
                break;
            }
            default: {
                final Optional<Hub> hub = this.plugin.hub().hub();
                if (hub.isEmpty()) {
                    this.msg().send((CommandSender)player, "hub-missing", new String[0]);
                    return;
                }
                this.plugin.hub().setHub(hub.get().withDoor(arenaMap.id(), of.tall(3)));
                this.msg().send((CommandSender)player, "editor-door-set", "map", arenaMap.displayName());
                break;
            }
        }
        this.pos1.remove(s3);
    }
    
    private void check(final Player player, final ArenaMap arenaMap) {
        final List<MapValidator.Problem> problems = MapValidator.problems(arenaMap, this.plugin.cfg().pages);
        if (problems.isEmpty()) {
            this.msg().send((CommandSender)player, "editor-check-ok", "map", arenaMap.displayName());
            return;
        }
        this.msg().send((CommandSender)player, "editor-check-header", "map", arenaMap.displayName());
        for (final MapValidator.Problem problem : problems) {
            player.sendMessage(this.msg().get(problem.key(), "count", problem.count(), "need", problem.need()));
        }
    }
    
    private void show(final Player player, final ArenaMap arenaMap) {
        this.msg().send((CommandSender)player, "editor-show", new String[0]);
        final Zone zone = this.plugin.hub().hub().map(hub -> hub.doors().get(arenaMap2.id())).orElse(null);
        final int[] array = { 0 };
        (new BukkitTask[] { null })[0] = Bukkit.getScheduler().runTaskTimer((Plugin)this.plugin, () -> {
            if (!player2.isOnline() || ++array2[0] > 20) {
                array3[0].cancel();
            }
            else {
                arenaMap3.spawns().iterator();
                final Iterator iterator;
                while (iterator.hasNext()) {
                    final Loc loc = iterator.next();
                    dot(player2, Particle.HAPPY_VILLAGER, loc.x(), loc.y() + 0.3, loc.z(), 6);
                }
                arenaMap3.pageSpots().iterator();
                final Iterator iterator2;
                while (iterator2.hasNext()) {
                    final PageSpot pageSpot = iterator2.next();
                    dot(player2, Particle.END_ROD, pageSpot.pos().x() + 0.5, pageSpot.pos().y() + 0.5, pageSpot.pos().z() + 0.5, 2);
                }
                arenaMap3.batterySpots().iterator();
                final Iterator iterator3;
                while (iterator3.hasNext()) {
                    final BlockPos blockPos = iterator3.next();
                    dot(player2, Particle.WAX_ON, blockPos.x() + 0.5, blockPos.y() + 0.3, blockPos.z() + 0.5, 4);
                }
                arenaMap3.gate().iterator();
                final Iterator iterator4;
                while (iterator4.hasNext()) {
                    final BlockPos blockPos2 = iterator4.next();
                    dot(player2, Particle.FLAME, blockPos2.x() + 0.5, blockPos2.y() + 0.5, blockPos2.z() + 0.5, 3);
                }
                if (arenaMap3.hasExit()) {
                    outline(player2, new Zone(arenaMap3.exitMin(), arenaMap3.exitMax()));
                }
                if (zone2 != null) {
                    outline(player2, zone2);
                }
                border(player2, arenaMap3.bounds());
            }
        }, 0L, 10L);
    }
    
    private static void dot(final Player player, final Particle particle, final double n, final double n2, final double n3, final int n4) {
        final Location location = player.getLocation();
        if (Math.abs(n - location.getX()) > 48.0 || Math.abs(n3 - location.getZ()) > 48.0) {
            return;
        }
        player.spawnParticle(particle, n, n2, n3, n4, 0.15, 0.15, 0.15, 0.0);
    }
    
    private static void outline(final Player player, final Zone zone) {
        final int x = zone.min().x();
        final int n = zone.max().x() + 1;
        final int z = zone.min().z();
        final int n2 = zone.max().z() + 1;
        if ((n - x) * (long)(n2 - z) > 4000L) {
            return;
        }
        for (int i = x; i <= n; ++i) {
            for (int j = z; j <= n2; ++j) {
                if (i == x || i == n || j == z || j == n2) {
                    dot(player, Particle.SOUL_FIRE_FLAME, i, zone.min().y() + 0.1, j, 1);
                    dot(player, Particle.SOUL_FIRE_FLAME, i, zone.max().y() + 0.9, j, 1);
                }
            }
        }
    }
    
    private static void border(final Player player, final Zone zone) {
        final double n = player.getLocation().getY() + 1.2;
        for (int i = zone.min().x(); i <= zone.max().x() + 1; i += 2) {
            dot(player, Particle.CRIT, i, n, zone.min().z(), 1);
            dot(player, Particle.CRIT, i, n, zone.max().z() + 1, 1);
        }
        for (int j = zone.min().z(); j <= zone.max().z() + 1; j += 2) {
            dot(player, Particle.CRIT, zone.min().x(), n, j, 1);
            dot(player, Particle.CRIT, zone.max().x() + 1, n, j, 1);
        }
    }
    
    public void hub(final CommandSender commandSender, final String[] array) {
        final Player editor = this.editor(commandSender);
        if (editor == null) {
            return;
        }
        final String s = (array.length >= 2) ? array[1].toLowerCase(Locale.ROOT) : "";
        final Location location = editor.getLocation();
        final String s2 = s;
        switch (s2) {
            case "set": {
                this.plugin.hub().setHub(this.plugin.hub().hub().map(hub2 -> hub2.withSpawn(loc)).orElse(new Hub(editor.getWorld().getName(), new Loc(location.getX(), location.getY(), location.getZ(), location.getYaw()), null, Map.of())));
                this.msg().send((CommandSender)editor, "hub-set", new String[0]);
                break;
            }
            case "exit": {
                final Optional<Hub> hub = this.plugin.hub().hub();
                if (hub.isEmpty()) {
                    this.msg().send((CommandSender)editor, "hub-missing", new String[0]);
                    return;
                }
                final String s3 = (array.length >= 3) ? array[2].toLowerCase(Locale.ROOT) : "";
                final String s4 = String.valueOf(editor.getUniqueId()) + ":hub:exit";
                if (s3.equals("pos1")) {
                    this.pos1.put(s4, feet(location));
                    this.msg().send((CommandSender)editor, "editor-pos1", new String[0]);
                }
                else if (s3.equals("pos2")) {
                    final BlockPos blockPos = this.pos1.remove(s4);
                    if (blockPos == null) {
                        this.msg().send((CommandSender)editor, "editor-need-pos1", new String[0]);
                        return;
                    }
                    this.plugin.hub().setHub(hub.get().withExit(Zone.of(blockPos, feet(location)).tall(3)));
                    this.msg().send((CommandSender)editor, "hub-exit-set", new String[0]);
                }
                else {
                    this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                }
                break;
            }
            default: {
                this.msg().send((CommandSender)editor, "editor-usage", new String[0]);
                break;
            }
        }
    }
    
    public List<String> complete(final String[] array) {
        final ArrayList list = new ArrayList();
        if (array[0].equalsIgnoreCase("hub")) {
            if (array.length == 2) {
                list.addAll(List.of("set", "exit"));
            }
            else if (array.length == 3 && array[1].equalsIgnoreCase("exit")) {
                list.addAll(List.of("pos1", "pos2"));
            }
            return list;
        }
        if (array.length == 2) {
            list.add("list");
            list.add("create");
            list.addAll(this.plugin.maps().keySet());
        }
        else if (array.length == 3 && this.plugin.maps().containsKey(array[1].toLowerCase(Locale.ROOT))) {
            list.addAll(EditorCommand.ACTIONS);
        }
        else if (array.length == 4) {
            final ArrayList list2 = list;
            final String lowerCase = array[2].toLowerCase(Locale.ROOT);
            list2.addAll((Collection)switch (lowerCase) {
                case "spawn",  "gate" -> List.of("add", "clear");
                case "page",  "battery" -> List.of("add", "remove");
                case "exit",  "bounds",  "door" -> List.of("pos1", "pos2");
                case "delete" -> List.of("confirm");
                default -> List.of();
            });
        }
        return list;
    }
    
    static {
        ACTIONS = List.of(new String[] { "spawn", "page", "battery", "gate", "exit", "bounds", "door", "name", "check", "show", "delete" });
    }
}
