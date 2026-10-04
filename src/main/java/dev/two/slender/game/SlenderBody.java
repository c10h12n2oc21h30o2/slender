package dev.two.slender.game;

import com.destroystokyo.paper.profile.ProfileProperty;
import dev.two.slender.config.SlenderConfig;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public final class SlenderBody {
    public static final String TAG = "slender_body";
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private LivingEntity entity;

    public SlenderBody(Plugin var1, SlenderConfig var2) {
        this.plugin = var1;
        this.cfg = var2;
    }

    public LivingEntity spawn(Location var1, boolean var2) {
        this.remove();
        World var3 = var1.getWorld();
        if (this.useMannequin()) {
            try {
                this.entity = (LivingEntity)var3.spawn(var1, Mannequin.class, (var2x) -> {
                    this.common(var2x, var2);
                    var2x.setImmovable(true);
                    var2x.setDescription(Component.empty());
                    var2x.setProfile(this.profile());
                });
            } catch (RuntimeException var5) {
                this.plugin.getLogger().warning("No se pudo crear el Mannequin, uso Enderman: " + var5.getMessage());
                this.entity = null;
            }
        }

        if (this.entity == null) {
            this.entity = (LivingEntity)var3.spawn(var1, Enderman.class, (var2x) -> {
                this.common(var2x, var2);
                var2x.setAI(false);
                var2x.setCarriedBlock((BlockData)null);
            });
        }

        AttributeInstance var4 = this.entity.getAttribute(Attribute.SCALE);
        if (var4 != null) {
            var4.setBaseValue(this.entity instanceof Mannequin ? this.cfg.slenderScale : 0.95);
        }

        return this.entity;
    }

    private void common(LivingEntity var1, boolean var2) {
        var1.setVisibleByDefault(var2);
        var1.setInvulnerable(true);
        var1.setSilent(true);
        var1.setPersistent(false);
        var1.setGravity(false);
        var1.setCollidable(false);
        var1.setRemoveWhenFarAway(false);
        var1.setCustomNameVisible(false);
        var1.getScoreboardTags().add("slender_body");
    }

    private boolean useMannequin() {
        return !"enderman".equalsIgnoreCase(this.cfg.slenderBody) && (!this.cfg.skinPlayer.isBlank() || !this.cfg.skinValue.isBlank());
    }

    private ResolvableProfile profile() {
        if (!this.cfg.skinValue.isBlank()) {
            ProfileProperty var1 = this.cfg.skinSignature.isBlank() ? new ProfileProperty("textures", this.cfg.skinValue) : new ProfileProperty("textures", this.cfg.skinValue, this.cfg.skinSignature);
            return (ResolvableProfile)ResolvableProfile.resolvableProfile().uuid(UUID.nameUUIDFromBytes("slender".getBytes(StandardCharsets.UTF_8))).name("Slender").addProperty(var1).build();
        } else {
            return (ResolvableProfile)ResolvableProfile.resolvableProfile().name(this.cfg.skinPlayer).build();
        }
    }

    public boolean alive() {
        return this.entity != null && this.entity.isValid();
    }

    public LivingEntity entity() {
        return this.entity;
    }

    public Location location() {
        return this.entity.getLocation();
    }

    public double height() {
        return this.entity.getHeight();
    }

    public Location center() {
        return this.location().add(0.0, this.height() * 0.55, 0.0);
    }

    public Location head() {
        return this.location().add(0.0, this.height() * 0.9, 0.0);
    }

    public void moveTo(Location var1, Location var2) {
        if (this.alive()) {
            Location var3 = var1.clone();
            if (var2 != null) {
                Vector var4 = var2.toVector().subtract(var3.toVector());
                if (var4.lengthSquared() > 1.0E-6) {
                    var3.setDirection(var4);
                }
            }

            var3.setPitch(0.0F);
            this.entity.teleport(var3);
        }
    }

    public void showTo(Player var1) {
        if (this.alive()) {
            var1.showEntity(this.plugin, this.entity);
        }

    }

    public void hideFrom(Player var1) {
        if (this.alive()) {
            var1.hideEntity(this.plugin, this.entity);
        }

    }

    public void remove() {
        if (this.entity != null) {
            this.entity.remove();
        }

        this.entity = null;
    }

    public static Location ground(Location var0, int var1, int var2) {
        World var3 = var0.getWorld();
        int var4 = var0.getBlockX();
        int var5 = var0.getBlockZ();
        int[] var6 = scanOrder(var0.getBlockY(), var1, var2);
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = var6[var8];
            Block var10 = var3.getBlockAt(var4, var9, var5);
            Block var11 = var10.getRelative(0, 1, 0);
            if (var10.isPassable() && !var10.isLiquid() && var11.isPassable() && !var11.isLiquid() && var10.getRelative(0, -1, 0).isSolid()) {
                return new Location(var3, (double)var4 + 0.5, (double)var9, (double)var5 + 0.5, var0.getYaw(), 0.0F);
            }
        }

        return null;
    }

    static int[] scanOrder(int var0, int var1, int var2) {
        int[] var3 = new int[var1 + var2 + 1];
        int var4 = 0;
        var3[var4++] = var0;

        for(int var5 = 1; var5 <= Math.max(var1, var2); ++var5) {
            if (var5 <= var2) {
                var3[var4++] = var0 - var5;
            }

            if (var5 <= var1) {
                var3[var4++] = var0 + var5;
            }
        }

        return var3;
    }
}
