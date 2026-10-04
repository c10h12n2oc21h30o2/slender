// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import dev.two.slender.logic.Vec3;
import dev.two.slender.logic.SightMath;
import java.util.Iterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import java.util.Random;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.SoundCategory;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.Collection;
import org.bukkit.Location;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import org.bukkit.entity.Player;
import java.util.function.BiConsumer;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.Plugin;

public final class SlenderPlayerController
{
    private static final String GRAB = "grab";
    private static final String[][] LABELS;
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final Items items;
    private final SightService sight;
    private final SlenderBody body;
    private final Flashlights flashlights;
    private final ScareScheduler scares;
    private final ArenaMap map;
    private final BiConsumer<Player, String> killer;
    private final Map<String, Long> readyAt;
    private final Concealment concealment;
    private UUID slender;
    private boolean frozen;
    
    public SlenderPlayerController(final Plugin plugin, final SlenderConfig cfg, final Msg msg, final Items items, final SightService sight, final SlenderBody body, final Flashlights flashlights, final ScareScheduler scares, final ArenaMap map, final BiConsumer<Player, String> killer) {
        this.readyAt = new HashMap<String, Long>();
        this.plugin = plugin;
        this.cfg = cfg;
        this.msg = msg;
        this.items = items;
        this.sight = sight;
        this.body = body;
        this.flashlights = flashlights;
        this.scares = scares;
        this.map = map;
        this.killer = killer;
        this.concealment = new Concealment(plugin);
    }
    
    public void setup(final Player player, final Location location, final Collection<Player> collection) {
        this.slender = player.getUniqueId();
        player.teleport(location);
        this.concealment.hide(player, collection);
        this.body.spawn(location, true);
        this.body.hideFrom(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false, false));
        player.getInventory().setItem(0, this.items.make("teleport"));
        player.getInventory().setItem(1, this.items.make("whisper"));
        player.getInventory().setItem(2, this.items.make("apparition"));
        player.getInventory().setItem(3, this.items.make("blackout"));
        player.setWalkSpeed(0.22f);
    }
    
    public boolean isSlender(final UUID uuid) {
        return uuid.equals(this.slender);
    }
    
    public void revealTo(final Player player) {
        final Player player2 = (this.slender == null) ? null : Bukkit.getPlayer(this.slender);
        if (player2 != null) {
            this.concealment.revealTo(player, player2);
        }
    }
    
    public UUID slender() {
        return this.slender;
    }
    
    public void tick(final Player player, final List<Player> list, final int n, final long n2) {
        final Location location = player.getLocation();
        location.setPitch(0.0f);
        this.body.moveTo(location, null);
        final boolean observedByAny = this.sight.observedByAny(list, this.body.center(), this.body.head());
        if (observedByAny != this.frozen) {
            this.frozen = observedByAny;
            player.setWalkSpeed(observedByAny ? 0.0f : 0.22f);
            final AttributeInstance attribute = player.getAttribute(Attribute.JUMP_STRENGTH);
            if (attribute != null) {
                attribute.setBaseValue(observedByAny ? 0.0 : 0.42);
            }
            if (observedByAny) {
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 0.4f, 1.6f);
            }
        }
        if (n2 % 20L == 0L) {
            player.sendActionBar(this.actionBar(player, list, n2));
        }
    }
    
    private Component actionBar(final Player player, final List<Player> list, final long n) {
        final Player nearest = this.nearest(player, list, Double.MAX_VALUE);
        final String s = (nearest == null) ? "-" : (Math.round(nearest.getLocation().distance(player.getLocation())) + " m");
        final StringBuilder sb = new StringBuilder();
        final String[][] labels = SlenderPlayerController.LABELS;
        for (int length = labels.length, i = 0; i < length; ++i) {
            final String[] array = labels[i];
            final long n2 = this.readyAt.getOrDefault(array[0], 0L) - n;
            sb.append((n2 > 0L) ? ("<red>" + array[1] + " " + (n2 + 19L) / 20L + "s</red> ") : ("<green>" + array[1] + "</green> "));
        }
        return this.msg.get("actionbar-slender", "state", this.frozen ? this.msg.raw("frozen") : "", "distance", s, "cooldowns", sb.toString().trim());
    }
    
    private double cooldownSeconds(final String s) {
        return switch (s) {
            case "teleport" -> this.cfg.asimTeleportCooldown;
            case "whisper" -> this.cfg.asimWhisperCooldown;
            case "apparition" -> this.cfg.asimApparitionCooldown;
            case "blackout" -> this.cfg.asimBlackoutCooldown;
            default -> this.cfg.asimGrabCooldown;
        };
    }
    
    private void spend(final String s, final int n, final long n2) {
        this.readyAt.put(s, n2 + Math.round(this.cooldownSeconds(s) * Math.max(0.3, 1.0 - this.cfg.asimCooldownReductionPerPage * n) * 20.0));
    }
    
    public void use(final Player player, final String key, final List<Player> list, final int n, final long n2, final Random random) {
        if (this.frozen) {
            player.sendActionBar(this.msg.parse("<dark_red>" + this.msg.raw("frozen") + "</dark_red>"));
            return;
        }
        final long n3 = this.readyAt.getOrDefault(key, 0L) - n2;
        if (n3 > 0L) {
            player.sendActionBar(this.msg.get("cooldown", "seconds", String.valueOf((n3 + 19L) / 20L)));
            return;
        }
        if (switch (key) {
            case "teleport" -> this.teleport(player, list);
            case "whisper" -> this.whisper(player, list, random);
            case "apparition" -> this.apparition(player, list, n2, random);
            case "blackout" -> this.blackout(player, list, n2);
            default -> false;
        }) {
            this.spend(key, n, n2);
        }
    }
    
    private boolean teleport(final Player player, final List<Player> list) {
        final RayTraceResult rayTraceBlocks = player.rayTraceBlocks(this.cfg.asimTeleportRange);
        if (rayTraceBlocks == null || rayTraceBlocks.getHitBlock() == null) {
            this.msg.send((CommandSender)player, "teleport-fail", new String[0]);
            return false;
        }
        final BlockFace hitBlockFace = rayTraceBlocks.getHitBlockFace();
        final Location ground = SlenderBody.ground(rayTraceBlocks.getHitBlock().getRelative((hitBlockFace == null) ? BlockFace.UP : hitBlockFace).getLocation().add(0.5, 0.0, 0.5), 2, 4);
        if (ground == null || !this.map.inRoam(ground.getX(), ground.getZ())) {
            this.msg.send((CommandSender)player, "teleport-fail", new String[0]);
            return false;
        }
        final double height = this.body.height();
        if (this.sight.observedByAny(list, ground.clone().add(0.0, height * 0.55, 0.0), ground.clone().add(0.0, height * 0.9, 0.0))) {
            this.msg.send((CommandSender)player, "teleport-fail", new String[0]);
            return false;
        }
        ground.setYaw(player.getLocation().getYaw());
        player.teleport(ground);
        this.body.moveTo(ground, null);
        return true;
    }
    
    private Player nearest(final Player player, final List<Player> list, final double n) {
        Player player2 = null;
        double n2 = n;
        for (final Player player3 : list) {
            if (player3.getWorld() != player.getWorld()) {
                continue;
            }
            final double distance = player3.getLocation().distance(player.getLocation());
            if (distance > n2) {
                continue;
            }
            n2 = distance;
            player2 = player3;
        }
        return player2;
    }
    
    private boolean whisper(final Player player, final List<Player> list, final Random random) {
        final Player nearest = this.nearest(player, list, this.cfg.asimWhisperRange);
        if (nearest == null) {
            this.msg.send((CommandSender)player, "no-target", new String[0]);
            return false;
        }
        this.scares.whisper(nearest, random);
        this.scares.footsteps(nearest, random);
        return true;
    }
    
    private boolean apparition(final Player player, final List<Player> list, final long n, final Random random) {
        final Location eyeLocation = player.getEyeLocation();
        final Vec3 vec = SightService.vec(eyeLocation);
        final Vec3 dir = SightService.dir(eyeLocation);
        Player nearest = null;
        double n2 = 15.0;
        for (final Player player2 : list) {
            if (player2.getWorld() == player.getWorld()) {
                if (player2.getLocation().distance(player.getLocation()) > 40.0) {
                    continue;
                }
                final double angleBetweenDeg = SightMath.angleBetweenDeg(dir, SightService.vec(player2.getEyeLocation()).sub(vec));
                if (angleBetweenDeg >= n2) {
                    continue;
                }
                n2 = angleBetweenDeg;
                nearest = player2;
            }
        }
        if (nearest == null) {
            nearest = this.nearest(player, list, 40.0);
        }
        if (nearest == null || !this.scares.apparition(nearest, n, random)) {
            this.msg.send((CommandSender)player, "no-target", new String[0]);
            return false;
        }
        return true;
    }
    
    private boolean blackout(final Player player, final List<Player> list, final long n) {
        boolean b = false;
        for (final Player player2 : list) {
            if (player2.getWorld() == player.getWorld()) {
                if (player2.getLocation().distance(player.getLocation()) > this.cfg.asimBlackoutRadius) {
                    continue;
                }
                this.flashlights.disable(player2.getUniqueId(), n + this.cfg.asimBlackoutTicks);
                player2.playSound(player2.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.8f, 0.6f);
                b = true;
            }
        }
        if (!b) {
            this.msg.send((CommandSender)player, "no-target", new String[0]);
        }
        return b;
    }
    
    public boolean tryGrab(final Player player, final Player player2, final int n, final long n2) {
        if (this.frozen || this.readyAt.getOrDefault("grab", 0L) > n2) {
            return false;
        }
        if (player.getWorld() != player2.getWorld() || player.getLocation().distance(player2.getLocation()) > this.cfg.asimGrabDistance) {
            return false;
        }
        this.spend("grab", n, n2);
        this.killer.accept(player2, "grabbed");
        return true;
    }
    
    public void cleanup(final Player player, final Collection<Player> collection) {
        if (player != null) {
            this.concealment.reveal(player);
            player.setWalkSpeed(0.2f);
            final AttributeInstance attribute = player.getAttribute(Attribute.JUMP_STRENGTH);
            if (attribute != null) {
                attribute.setBaseValue(0.42);
            }
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
        this.body.remove();
        this.slender = null;
        this.frozen = false;
        this.readyAt.clear();
    }
    
    static {
        LABELS = new String[][] { { "teleport", "TP" }, { "whisper", "SUS" }, { "apparition", "APA" }, { "blackout", "APG" }, { "grab", "AGR" } };
    }
}
