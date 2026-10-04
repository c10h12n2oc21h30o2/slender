// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.listener;

import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.HumanEntity;
import dev.two.slender.ui.MenuHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Iterator;
import org.bukkit.entity.Entity;
import dev.two.slender.game.Game;
import java.util.Optional;
import org.bukkit.entity.Player;
import dev.two.slender.SlenderPlugin;
import java.util.Set;
import org.bukkit.event.Listener;

public final class GameListener implements Listener
{
    private static final Set<String> TAGS;
    private final SlenderPlugin plugin;
    
    public GameListener(final SlenderPlugin plugin) {
        this.plugin = plugin;
    }
    
    private Optional<Game> game(final Player player) {
        return this.plugin.games().gameOf(player.getUniqueId());
    }
    
    private boolean inHub(final Player player) {
        return this.plugin.hub().inside(player.getUniqueId());
    }
    
    private boolean guarded(final Player player) {
        return this.game(player).isPresent() || this.inHub(player);
    }
    
    private static boolean tagged(final Entity entity) {
        final Iterator iterator = entity.getScoreboardTags().iterator();
        while (iterator.hasNext()) {
            if (GameListener.TAGS.contains(iterator.next())) {
                return true;
            }
        }
        return false;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(final PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getHand() != EquipmentSlot.HAND || playerInteractEvent.getAction() == Action.PHYSICAL) {
            return;
        }
        final Player player = playerInteractEvent.getPlayer();
        final String id = this.plugin.items().idOf(playerInteractEvent.getItem());
        if (id == null) {
            return;
        }
        final boolean b = playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK;
        if ("hub-menu".equals(id) || "hub-exit".equals(id)) {
            playerInteractEvent.setCancelled(true);
            if (!this.inHub(player)) {
                return;
            }
            if ("hub-menu".equals(id)) {
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.menu().openMaps(player2));
            }
            else if (b) {
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.hub().exit(player3));
            }
        }
        else {
            final Optional<Game> game = this.game(player);
            if (game.isEmpty()) {
                return;
            }
            if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && playerInteractEvent.getClickedBlock() != null && ProtectionListener.isOpenable(playerInteractEvent.getClickedBlock().getType())) {
                return;
            }
            playerInteractEvent.setCancelled(true);
            if (!b) {
                return;
            }
            if ("flashlight".equals(id)) {
                game.get().toggleFlashlight(player);
            }
            else {
                game.get().useAbility(player, id);
            }
        }
    }
    
    @EventHandler
    public void onDrop(final PlayerDropItemEvent playerDropItemEvent) {
        if (!this.guarded(playerDropItemEvent.getPlayer())) {
            return;
        }
        playerDropItemEvent.setCancelled(true);
        final Optional<Game> game = this.game(playerDropItemEvent.getPlayer());
        if (game.isPresent() && "flashlight".equals(this.plugin.items().idOf(playerDropItemEvent.getItemDrop().getItemStack()))) {
            game.get().toggleFlashlight(playerDropItemEvent.getPlayer());
        }
    }
    
    @EventHandler
    public void onSwap(final PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
        if (this.guarded(playerSwapHandItemsEvent.getPlayer())) {
            playerSwapHandItemsEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent inventoryClickEvent) {
        final HumanEntity whoClicked = inventoryClickEvent.getWhoClicked();
        if (!(whoClicked instanceof Player)) {
            return;
        }
        final Player player = (Player)whoClicked;
        final InventoryHolder holder = inventoryClickEvent.getInventory().getHolder();
        if (holder instanceof final MenuHolder menuHolder) {
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getClickedInventory() == inventoryClickEvent.getInventory()) {
                this.plugin.menu().click(player, menuHolder, inventoryClickEvent.getSlot());
            }
            return;
        }
        if (this.guarded(player)) {
            inventoryClickEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent inventoryDragEvent) {
        if (inventoryDragEvent.getInventory().getHolder() instanceof MenuHolder) {
            inventoryDragEvent.setCancelled(true);
            return;
        }
        final HumanEntity whoClicked = inventoryDragEvent.getWhoClicked();
        if (whoClicked instanceof Player && this.guarded((Player)whoClicked)) {
            inventoryDragEvent.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityInteract(final PlayerInteractEntityEvent playerInteractEntityEvent) {
        if (playerInteractEntityEvent.getHand() != EquipmentSlot.HAND) {
            if (tagged(playerInteractEntityEvent.getRightClicked())) {
                playerInteractEntityEvent.setCancelled(true);
            }
            return;
        }
        final Entity rightClicked = playerInteractEntityEvent.getRightClicked();
        final Optional<Game> game = this.game(playerInteractEntityEvent.getPlayer());
        if (tagged(rightClicked) || (game.isPresent() && rightClicked instanceof ItemFrame)) {
            playerInteractEntityEvent.setCancelled(true);
        }
        if (game.isPresent() && rightClicked instanceof ItemFrame) {
            game.get().interactFrame(playerInteractEntityEvent.getPlayer(), rightClicked);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPreAttack(final PrePlayerAttackEntityEvent prePlayerAttackEntityEvent) {
        final Entity attacked = prePlayerAttackEntityEvent.getAttacked();
        if (tagged(attacked)) {
            prePlayerAttackEntityEvent.setCancelled(true);
        }
        final Optional<Game> game = this.game(prePlayerAttackEntityEvent.getPlayer());
        if (game.isEmpty()) {
            return;
        }
        prePlayerAttackEntityEvent.setCancelled(true);
        if (attacked instanceof ItemFrame) {
            game.get().interactFrame(prePlayerAttackEntityEvent.getPlayer(), attacked);
        }
        else if (attacked instanceof final Player player) {
            game.get().slenderHit(prePlayerAttackEntityEvent.getPlayer(), player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageBy(final EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (tagged(entityDamageByEntityEvent.getEntity())) {
            entityDamageByEntityEvent.setCancelled(true);
        }
        final Entity damager = entityDamageByEntityEvent.getDamager();
        if (damager instanceof Player && this.guarded((Player)damager)) {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent entityDamageEvent) {
        if (tagged(entityDamageEvent.getEntity())) {
            entityDamageEvent.setCancelled(true);
            return;
        }
        final Entity entity = entityDamageEvent.getEntity();
        if (entity instanceof Player && this.guarded((Player)entity)) {
            entityDamageEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onHangingBreak(final HangingBreakEvent hangingBreakEvent) {
        if (hangingBreakEvent.getEntity().getWorld().getName().equals(this.plugin.cfg().world)) {
            hangingBreakEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPickup(final PlayerAttemptPickupItemEvent playerAttemptPickupItemEvent) {
        if (!playerAttemptPickupItemEvent.getItem().getScoreboardTags().contains("slender_battery")) {
            return;
        }
        playerAttemptPickupItemEvent.setCancelled(true);
        this.game(playerAttemptPickupItemEvent.getPlayer()).ifPresent(game -> game.pickupBattery(playerAttemptPickupItemEvent2.getPlayer(), item));
    }
    
    @EventHandler
    public void onFood(final FoodLevelChangeEvent foodLevelChangeEvent) {
        final HumanEntity entity = foodLevelChangeEvent.getEntity();
        if (entity instanceof Player && this.guarded((Player)entity)) {
            foodLevelChangeEvent.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent playerQuitEvent) {
        this.plugin.games().quit(playerQuitEvent.getPlayer());
        this.plugin.hub().quit(playerQuitEvent.getPlayer());
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent playerJoinEvent) {
        if (!this.plugin.snapshots().has(playerJoinEvent.getPlayer().getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            if (player.isOnline() && this.plugin.games().gameOf(player.getUniqueId()).isEmpty()) {
                this.plugin.snapshots().restore(player, true);
            }
        }, 5L);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldLeaving(final PlayerChangedWorldEvent playerChangedWorldEvent) {
        final String world = this.plugin.cfg().world;
        if (playerChangedWorldEvent.getFrom().getName().equals(world) && !playerChangedWorldEvent.getPlayer().getWorld().getName().equals(world)) {
            this.plugin.hub().noteLeaving(playerChangedWorldEvent.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(final PlayerChangedWorldEvent playerChangedWorldEvent) {
        if (playerChangedWorldEvent.getPlayer().getWorld().getName().equals(this.plugin.cfg().world)) {
            return;
        }
        this.plugin.games().worldChanged(playerChangedWorldEvent.getPlayer());
        this.plugin.hub().leftWorld(playerChangedWorldEvent.getPlayer());
    }
    
    static {
        TAGS = Set.of("slender_page", "slender_fake_page", "slender_body", "slender_battery");
    }
}
