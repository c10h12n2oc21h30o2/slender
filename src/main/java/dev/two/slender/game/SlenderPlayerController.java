package dev.two.slender.game;

import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.SightMath;
import dev.two.slender.logic.Vec3;
import dev.two.slender.map.ArenaMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public final class SlenderPlayerController {
    private static final String GRAB = "grab";
    private static final String[][] LABELS = new String[][]{{"teleport", "TP"}, {"whisper", "SUS"}, {"apparition", "APA"}, {"blackout", "APG"}, {"grab", "AGR"}};
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
    private final Map<String, Long> readyAt = new HashMap();
    private final Concealment concealment;
    private UUID slender;
    private boolean frozen;

    public SlenderPlayerController(Plugin var1, SlenderConfig var2, Msg var3, Items var4, SightService var5, SlenderBody var6, Flashlights var7, ScareScheduler var8, ArenaMap var9, BiConsumer<Player, String> var10) {
        this.plugin = var1;
        this.cfg = var2;
        this.msg = var3;
        this.items = var4;
        this.sight = var5;
        this.body = var6;
        this.flashlights = var7;
        this.scares = var8;
        this.map = var9;
        this.killer = var10;
        this.concealment = new Concealment(var1);
    }

    public void setup(Player var1, Location var2, Collection<Player> var3) {
        this.slender = var1.getUniqueId();
        var1.teleport(var2);
        this.concealment.hide(var1, var3);
        this.body.spawn(var2, true);
        this.body.hideFrom(var1);
        var1.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false, false));
        var1.getInventory().setItem(0, this.items.make("teleport"));
        var1.getInventory().setItem(1, this.items.make("whisper"));
        var1.getInventory().setItem(2, this.items.make("apparition"));
        var1.getInventory().setItem(3, this.items.make("blackout"));
        var1.setWalkSpeed(0.22F);
    }

    public boolean isSlender(UUID var1) {
        return var1.equals(this.slender);
    }

    public void revealTo(Player var1) {
        Player var2 = this.slender == null ? null : Bukkit.getPlayer(this.slender);
        if (var2 != null) {
            this.concealment.revealTo(var1, var2);
        }

    }

    public UUID slender() {
        return this.slender;
    }

    public void tick(Player var1, List<Player> var2, int var3, long var4) {
        Location var6 = var1.getLocation();
        var6.setPitch(0.0F);
        this.body.moveTo(var6, (Location)null);
        boolean var7 = this.sight.observedByAny(var2, this.body.center(), this.body.head());
        if (var7 != this.frozen) {
            this.frozen = var7;
            var1.setWalkSpeed(var7 ? 0.0F : 0.22F);
            AttributeInstance var8 = var1.getAttribute(Attribute.JUMP_STRENGTH);
            if (var8 != null) {
                var8.setBaseValue(var7 ? 0.0 : 0.42);
            }

            if (var7) {
                var1.playSound(var1.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 0.4F, 1.6F);
            }
        }

        if (var4 % 20L == 0L) {
            var1.sendActionBar((Component)this.actionBar(var1, var2, var4));
        }

    }

    private Component actionBar(Player var1, List<Player> var2, long var3) {
        Player var5 = this.nearest(var1, var2, Double.MAX_VALUE);
        String var6 = var5 == null ? "-" : Math.round(var5.getLocation().distance(var1.getLocation())) + " m";
        StringBuilder var7 = new StringBuilder();
        String[][] var8 = LABELS;
        int var9 = var8.length;

        for(int var10 = 0; var10 < var9; ++var10) {
            String[] var11 = var8[var10];
            long var12 = (Long)this.readyAt.getOrDefault(var11[0], 0L) - var3;
            var7.append(var12 > 0L ? "<red>" + var11[1] + " " + (var12 + 19L) / 20L + "s</red> " : "<green>" + var11[1] + "</green> ");
        }

        return this.msg.get("actionbar-slender", "state", this.frozen ? this.msg.raw("frozen") : "", "distance", var6, "cooldowns", var7.toString().trim());
    }

    private double cooldownSeconds(String var1) {
        double var10000;
        switch (var1) {
            case "teleport" -> var10000 = this.cfg.asimTeleportCooldown;
            case "whisper" -> var10000 = this.cfg.asimWhisperCooldown;
            case "apparition" -> var10000 = this.cfg.asimApparitionCooldown;
            case "blackout" -> var10000 = this.cfg.asimBlackoutCooldown;
            default -> var10000 = this.cfg.asimGrabCooldown;
        }

        return var10000;
    }

    private void spend(String var1, int var2, long var3) {
        double var5 = Math.max(0.3, 1.0 - this.cfg.asimCooldownReductionPerPage * (double)var2);
        this.readyAt.put(var1, var3 + Math.round(this.cooldownSeconds(var1) * var5 * 20.0));
    }

    public void use(Player var1, String var2, List<Player> var3, int var4, long var5, Random var7) {
        if (this.frozen) {
            var1.sendActionBar((Component)this.msg.parse("<dark_red>" + this.msg.raw("frozen") + "</dark_red>"));
        } else {
            long var8 = (Long)this.readyAt.getOrDefault(var2, 0L) - var5;
            if (var8 > 0L) {
                var1.sendActionBar((Component)this.msg.get("cooldown", "seconds", String.valueOf((var8 + 19L) / 20L)));
            } else {
                boolean var10000;
                switch (var2) {
                    case "teleport" -> var10000 = this.teleport(var1, var3);
                    case "whisper" -> var10000 = this.whisper(var1, var3, var7);
                    case "apparition" -> var10000 = this.apparition(var1, var3, var5, var7);
                    case "blackout" -> var10000 = this.blackout(var1, var3, var5);
                    default -> var10000 = false;
                }

                boolean var10 = var10000;
                if (var10) {
                    this.spend(var2, var4, var5);
                }

            }
        }
    }

    private boolean teleport(Player var1, List<Player> var2) {
        RayTraceResult var3 = var1.rayTraceBlocks(this.cfg.asimTeleportRange);
        if (var3 != null && var3.getHitBlock() != null) {
            BlockFace var4 = var3.getHitBlockFace();
            Block var5 = var3.getHitBlock().getRelative(var4 == null ? BlockFace.UP : var4);
            Location var6 = SlenderBody.ground(var5.getLocation().add(0.5, 0.0, 0.5), 2, 4);
            if (var6 != null && this.map.inRoam(var6.getX(), var6.getZ())) {
                double var7 = this.body.height();
                if (this.sight.observedByAny(var2, var6.clone().add(0.0, var7 * 0.55, 0.0), var6.clone().add(0.0, var7 * 0.9, 0.0))) {
                    this.msg.send(var1, "teleport-fail");
                    return false;
                } else {
                    var6.setYaw(var1.getLocation().getYaw());
                    var1.teleport(var6);
                    this.body.moveTo(var6, (Location)null);
                    return true;
                }
            } else {
                this.msg.send(var1, "teleport-fail");
                return false;
            }
        } else {
            this.msg.send(var1, "teleport-fail");
            return false;
        }
    }

    private Player nearest(Player var1, List<Player> var2, double var3) {
        Player var5 = null;
        double var6 = var3;
        Iterator var8 = var2.iterator();

        while(var8.hasNext()) {
            Player var9 = (Player)var8.next();
            if (var9.getWorld() == var1.getWorld()) {
                double var10 = var9.getLocation().distance(var1.getLocation());
                if (var10 <= var6) {
                    var6 = var10;
                    var5 = var9;
                }
            }
        }

        return var5;
    }

    private boolean whisper(Player var1, List<Player> var2, Random var3) {
        Player var4 = this.nearest(var1, var2, this.cfg.asimWhisperRange);
        if (var4 == null) {
            this.msg.send(var1, "no-target");
            return false;
        } else {
            this.scares.whisper(var4, var3);
            this.scares.footsteps(var4, var3);
            return true;
        }
    }

    private boolean apparition(Player var1, List<Player> var2, long var3, Random var5) {
        Location var6 = var1.getEyeLocation();
        Vec3 var7 = SightService.vec(var6);
        Vec3 var8 = SightService.dir(var6);
        Player var9 = null;
        double var10 = 15.0;
        Iterator var12 = var2.iterator();

        while(var12.hasNext()) {
            Player var13 = (Player)var12.next();
            if (var13.getWorld() == var1.getWorld() && !(var13.getLocation().distance(var1.getLocation()) > 40.0)) {
                double var14 = SightMath.angleBetweenDeg(var8, SightService.vec(var13.getEyeLocation()).sub(var7));
                if (var14 < var10) {
                    var10 = var14;
                    var9 = var13;
                }
            }
        }

        if (var9 == null) {
            var9 = this.nearest(var1, var2, 40.0);
        }

        if (var9 != null && this.scares.apparition(var9, var3, var5)) {
            return true;
        } else {
            this.msg.send(var1, "no-target");
            return false;
        }
    }

    private boolean blackout(Player var1, List<Player> var2, long var3) {
        boolean var5 = false;
        Iterator var6 = var2.iterator();

        while(var6.hasNext()) {
            Player var7 = (Player)var6.next();
            if (var7.getWorld() == var1.getWorld() && !(var7.getLocation().distance(var1.getLocation()) > this.cfg.asimBlackoutRadius)) {
                this.flashlights.disable(var7.getUniqueId(), var3 + (long)this.cfg.asimBlackoutTicks);
                var7.playSound(var7.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.8F, 0.6F);
                var5 = true;
            }
        }

        if (!var5) {
            this.msg.send(var1, "no-target");
        }

        return var5;
    }

    public boolean tryGrab(Player var1, Player var2, int var3, long var4) {
        if (!this.frozen && (Long)this.readyAt.getOrDefault("grab", 0L) <= var4) {
            if (var1.getWorld() == var2.getWorld() && !(var1.getLocation().distance(var2.getLocation()) > this.cfg.asimGrabDistance)) {
                this.spend("grab", var3, var4);
                this.killer.accept(var2, "grabbed");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void cleanup(Player var1, Collection<Player> var2) {
        if (var1 != null) {
            this.concealment.reveal(var1);
            var1.setWalkSpeed(0.2F);
            AttributeInstance var3 = var1.getAttribute(Attribute.JUMP_STRENGTH);
            if (var3 != null) {
                var3.setBaseValue(0.42);
            }

            var1.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        this.body.remove();
        this.slender = null;
        this.frozen = false;
        this.readyAt.clear();
    }
}
