// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.listener;

import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Keyed;
import org.bukkit.Tag;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.World;
import dev.two.slender.SlenderPlugin;
import org.bukkit.event.Listener;

public final class ProtectionListener implements Listener
{
    private final SlenderPlugin plugin;
    
    public ProtectionListener(final SlenderPlugin plugin) {
        this.plugin = plugin;
    }
    
    private boolean guarded(final World world) {
        return world.getName().equals(this.plugin.cfg().world);
    }
    
    private boolean bypass(final Player player) {
        return player.hasPermission("slender.admin") && player.getGameMode() == GameMode.CREATIVE;
    }
    
    public static boolean isOpenable(final Material material) {
        return (Tag.DOORS.isTagged((Keyed)material) && material != Material.IRON_DOOR) || (Tag.TRAPDOORS.isTagged((Keyed)material) && material != Material.IRON_TRAPDOOR);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent blockBreakEvent) {
        if (this.guarded(blockBreakEvent.getBlock().getWorld()) && !this.bypass(blockBreakEvent.getPlayer())) {
            blockBreakEvent.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlace(final BlockPlaceEvent blockPlaceEvent) {
        if (this.guarded(blockPlaceEvent.getBlock().getWorld()) && !this.bypass(blockPlaceEvent.getPlayer())) {
            blockPlaceEvent.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBucket(final PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        if (this.guarded(playerBucketEmptyEvent.getBlock().getWorld()) && !this.bypass(playerBucketEmptyEvent.getPlayer())) {
            playerBucketEmptyEvent.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(final PlayerBucketFillEvent playerBucketFillEvent) {
        if (this.guarded(playerBucketFillEvent.getBlock().getWorld()) && !this.bypass(playerBucketFillEvent.getPlayer())) {
            playerBucketFillEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(final PlayerInteractEvent playerInteractEvent) {
        if (!this.guarded(playerInteractEvent.getPlayer().getWorld()) || this.bypass(playerInteractEvent.getPlayer())) {
            return;
        }
        final Block clickedBlock = playerInteractEvent.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (playerInteractEvent.getAction() == Action.PHYSICAL) {
            if (clickedBlock.getType() == Material.FARMLAND) {
                playerInteractEvent.setCancelled(true);
            }
            return;
        }
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && isOpenable(clickedBlock.getType())) {
            return;
        }
        playerInteractEvent.setUseInteractedBlock(Event.Result.DENY);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityChange(final EntityChangeBlockEvent entityChangeBlockEvent) {
        if (this.guarded(entityChangeBlockEvent.getBlock().getWorld())) {
            entityChangeBlockEvent.setCancelled(true);
        }
    }
}
