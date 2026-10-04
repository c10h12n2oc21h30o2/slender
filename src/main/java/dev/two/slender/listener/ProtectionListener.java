package dev.two.slender.listener;

import dev.two.slender.SlenderPlugin;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ProtectionListener implements Listener {
    private final SlenderPlugin plugin;

    public ProtectionListener(SlenderPlugin var1) {
        this.plugin = var1;
    }

    private boolean guarded(World var1) {
        return var1.getName().equals(this.plugin.cfg().world);
    }

    private boolean bypass(Player var1) {
        return var1.hasPermission("slender.admin") && var1.getGameMode() == GameMode.CREATIVE;
    }

    public static boolean isOpenable(Material var0) {
        return Tag.DOORS.isTagged(var0) && var0 != Material.IRON_DOOR || Tag.TRAPDOORS.isTagged(var0) && var0 != Material.IRON_TRAPDOOR;
    }

    @EventHandler(
        ignoreCancelled = true
    )
    public void onBreak(BlockBreakEvent var1) {
        if (this.guarded(var1.getBlock().getWorld()) && !this.bypass(var1.getPlayer())) {
            var1.setCancelled(true);
        }

    }

    @EventHandler(
        ignoreCancelled = true
    )
    public void onPlace(BlockPlaceEvent var1) {
        if (this.guarded(var1.getBlock().getWorld()) && !this.bypass(var1.getPlayer())) {
            var1.setCancelled(true);
        }

    }

    @EventHandler(
        ignoreCancelled = true
    )
    public void onBucket(PlayerBucketEmptyEvent var1) {
        if (this.guarded(var1.getBlock().getWorld()) && !this.bypass(var1.getPlayer())) {
            var1.setCancelled(true);
        }

    }

    @EventHandler(
        ignoreCancelled = true
    )
    public void onBucketFill(PlayerBucketFillEvent var1) {
        if (this.guarded(var1.getBlock().getWorld()) && !this.bypass(var1.getPlayer())) {
            var1.setCancelled(true);
        }

    }

    @EventHandler(
        priority = EventPriority.LOW
    )
    public void onInteract(PlayerInteractEvent var1) {
        if (this.guarded(var1.getPlayer().getWorld()) && !this.bypass(var1.getPlayer())) {
            Block var2 = var1.getClickedBlock();
            if (var2 != null) {
                if (var1.getAction() == Action.PHYSICAL) {
                    if (var2.getType() == Material.FARMLAND) {
                        var1.setCancelled(true);
                    }

                } else if (var1.getAction() != Action.RIGHT_CLICK_BLOCK || !isOpenable(var2.getType())) {
                    var1.setUseInteractedBlock(Result.DENY);
                }
            }
        }
    }

    @EventHandler(
        ignoreCancelled = true
    )
    public void onEntityChange(EntityChangeBlockEvent var1) {
        if (this.guarded(var1.getBlock().getWorld())) {
            var1.setCancelled(true);
        }

    }
}
