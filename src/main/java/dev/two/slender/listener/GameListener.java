package dev.two.slender.listener;

import dev.two.slender.SlenderPlugin;
import dev.two.slender.game.Game;
import dev.two.slender.game.PhaseHooks;
import dev.two.slender.ui.MenuHolder;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

public final class GameListener implements Listener {
    private static final Set<String> TAGS = Set.of("slender_page", "slender_fake_page", "slender_body", "slender_battery");
    private final SlenderPlugin plugin;

    public GameListener(SlenderPlugin var1) {
        this.plugin = var1;
    }

    private Optional<Game> game(Player var1) {
        return this.plugin.games().gameOf(var1.getUniqueId());
    }

    private boolean inHub(Player var1) {
        return this.plugin.hub().inside(var1.getUniqueId());
    }

    private boolean guarded(Player var1) {
        return this.game(var1).isPresent() || this.inHub(var1);
    }

    private static boolean tagged(Entity var0) {
        Iterator var1 = var0.getScoreboardTags().iterator();

        String var2;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            var2 = (String)var1.next();
        } while(!TAGS.contains(var2));

        return true;
    }

    @EventHandler(
        priority = EventPriority.HIGH
    )
    public void onInteract(PlayerInteractEvent var1) {
        if (var1.getHand() == EquipmentSlot.HAND && var1.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<Game> night = this.game(var1.getPlayer());
            if (night.isPresent() && night.get().interactBlock(var1.getPlayer(), var1.getClickedBlock())) {
                var1.setCancelled(true);
                return;
            }
        }
        if (var1.getHand() == EquipmentSlot.HAND && var1.getAction() != Action.PHYSICAL) {
            Player var2 = var1.getPlayer();
            String var3 = this.plugin.items().idOf(var1.getItem());
            if (var3 != null) {
                boolean var4 = var1.getAction() == Action.RIGHT_CLICK_AIR || var1.getAction() == Action.RIGHT_CLICK_BLOCK;
                if (!"hub-menu".equals(var3) && !"hub-exit".equals(var3)) {
                    Optional var5 = this.game(var2);
                    if (!var5.isEmpty()) {
                        if (var1.getAction() != Action.RIGHT_CLICK_BLOCK || var1.getClickedBlock() == null || !ProtectionListener.isOpenable(var1.getClickedBlock().getType())) {
                            var1.setCancelled(true);
                            if (var4) {
                                if ("flashlight".equals(var3)) {
                                    ((Game)var5.get()).toggleFlashlight(var2);
                                } else {
                                    ((Game)var5.get()).useAbility(var2, var3);
                                }

                            }
                        }
                    }
                } else {
                    var1.setCancelled(true);
                    if (this.inHub(var2)) {
                        if ("hub-menu".equals(var3)) {
                            Bukkit.getScheduler().runTask(this.plugin, (Runnable)(() -> {
                                this.plugin.menu().openMaps(var2);
                            }));
                        } else if (var4) {
                            Bukkit.getScheduler().runTask(this.plugin, (Runnable)(() -> {
                                this.plugin.hub().exit(var2);
                            }));
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent var1) {
        if (this.guarded(var1.getPlayer())) {
            var1.setCancelled(true);
            Optional var2 = this.game(var1.getPlayer());
            if (var2.isPresent() && "flashlight".equals(this.plugin.items().idOf(var1.getItemDrop().getItemStack()))) {
                ((Game)var2.get()).toggleFlashlight(var1.getPlayer());
            }

        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent var1) {
        if (this.guarded(var1.getPlayer())) {
            var1.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent var1) {
        HumanEntity var3 = var1.getWhoClicked();
        if (var3 instanceof Player var2) {
            InventoryHolder var4 = var1.getInventory().getHolder();
            if (var4 instanceof MenuHolder var5) {
                var1.setCancelled(true);
                if (var1.getClickedInventory() == var1.getInventory()) {
                    this.plugin.menu().click(var2, var5, var1.getSlot());
                }

            } else {
                if (this.guarded(var2)) {
                    var1.setCancelled(true);
                }

            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent var1) {
        if (var1.getInventory().getHolder() instanceof MenuHolder) {
            var1.setCancelled(true);
        } else {
            HumanEntity var3 = var1.getWhoClicked();
            if (var3 instanceof Player) {
                Player var2 = (Player)var3;
                if (this.guarded(var2)) {
                    var1.setCancelled(true);
                }
            }

        }
    }

    @EventHandler(
        priority = EventPriority.HIGH
    )
    public void onEntityInteract(PlayerInteractEntityEvent var1) {
        if (var1.getHand() != EquipmentSlot.HAND) {
            if (tagged(var1.getRightClicked())) {
                var1.setCancelled(true);
            }

        } else {
            Entity var2 = var1.getRightClicked();
            Optional var3 = this.game(var1.getPlayer());
            if (var3.isPresent() && ((Game)var3.get()).interactEntity(var1.getPlayer(), var2)) {
                var1.setCancelled(true);
                return;
            }
            if (tagged(var2) || var3.isPresent() && var2 instanceof ItemFrame) {
                var1.setCancelled(true);
            }

            if (var3.isPresent() && var2 instanceof ItemFrame) {
                ((Game)var3.get()).interactFrame(var1.getPlayer(), var2);
            }

        }
    }

    @EventHandler(
        priority = EventPriority.HIGH
    )
    public void onPreAttack(PrePlayerAttackEntityEvent var1) {
        Entity var2 = var1.getAttacked();
        if (tagged(var2)) {
            var1.setCancelled(true);
        }

        Optional var3 = this.game(var1.getPlayer());
        if (!var3.isEmpty()) {
            var1.setCancelled(true);
            if (var2 instanceof ItemFrame) {
                ((Game)var3.get()).interactFrame(var1.getPlayer(), var2);
            } else if (var2 instanceof Player) {
                Player var4 = (Player)var2;
                ((Game)var3.get()).slenderHit(var1.getPlayer(), var4);
            }

        }
    }

    @EventHandler(
        priority = EventPriority.HIGH
    )
    public void onDamageBy(EntityDamageByEntityEvent var1) {
        if (tagged(var1.getEntity())) {
            var1.setCancelled(true);
        }

        Entity var3 = var1.getDamager();
        if (var3 instanceof Player var2) {
            if (this.guarded(var2)) {
                var1.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onDamage(EntityDamageEvent var1) {
        if (tagged(var1.getEntity())) {
            var1.setCancelled(true);
        } else {
            Entity var3 = var1.getEntity();
            if (var3 instanceof Player) {
                Player var2 = (Player)var3;
                if (this.guarded(var2)) {
                    var1.setCancelled(true);
                }
            }

        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent var1) {
        if (var1.getEntity().getWorld().getName().equals(this.plugin.cfg().world)) {
            var1.setCancelled(true);
        }

    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent var1) {
        Item var2 = var1.getItem();
        Optional<Game> night = this.game(var1.getPlayer());
        if (night.isPresent()) {
            PhaseHooks.Pickup v = night.get().pickup(var1.getPlayer(), var2);
            if (v == PhaseHooks.Pickup.ALLOW) {
                return;
            }
            if (v == PhaseHooks.Pickup.DENY) {
                var1.setCancelled(true);
                return;
            }
        }
        if (var2.getScoreboardTags().contains("slender_battery")) {
            var1.setCancelled(true);
            this.game(var1.getPlayer()).ifPresent((var2x) -> {
                var2x.pickupBattery(var1.getPlayer(), var2);
            });
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent var1) {
        HumanEntity var3 = var1.getEntity();
        if (var3 instanceof Player var2) {
            if (this.guarded(var2)) {
                var1.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent var1) {
        this.plugin.games().quit(var1.getPlayer());
        this.plugin.hub().quit(var1.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent var1) {
        Player var2 = var1.getPlayer();
        if (this.plugin.snapshots().has(var2.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(this.plugin, (Runnable)(() -> {
                if (var2.isOnline() && this.plugin.games().gameOf(var2.getUniqueId()).isEmpty()) {
                    this.plugin.snapshots().restore(var2, true);
                }

            }), 5L);
        }
    }

    @EventHandler(
        priority = EventPriority.LOWEST
    )
    public void onWorldLeaving(PlayerChangedWorldEvent var1) {
        String var2 = this.plugin.cfg().world;
        if (var1.getFrom().getName().equals(var2) && !var1.getPlayer().getWorld().getName().equals(var2)) {
            this.plugin.hub().noteLeaving(var1.getPlayer());
        }

    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    public void onWorldChange(PlayerChangedWorldEvent var1) {
        if (!var1.getPlayer().getWorld().getName().equals(this.plugin.cfg().world)) {
            this.plugin.games().worldChanged(var1.getPlayer());
            this.plugin.hub().leftWorld(var1.getPlayer());
        }
    }
}
