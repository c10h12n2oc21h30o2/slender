// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Mannequin;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.Plugin;

public final class SlenderBody
{
    public static final String TAG = "slender_body";
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private LivingEntity entity;
    
    public SlenderBody(final Plugin plugin, final SlenderConfig cfg) {
        this.plugin = plugin;
        this.cfg = cfg;
    }
    
    public LivingEntity spawn(final Location location, final boolean b) {
        this.remove();
        final World world = location.getWorld();
        if (this.useMannequin()) {
            try {
                this.entity = (LivingEntity)world.spawn(location, (Class)Mannequin.class, mannequin -> {
                    this.common((LivingEntity)mannequin, b2);
                    mannequin.setImmovable((boolean)(1 != 0));
                    mannequin.setDescription((Component)Component.empty());
                    mannequin.setProfile(this.profile());
                    return;
                });
            }
            catch (final RuntimeException ex) {
                this.plugin.getLogger().warning("No se pudo crear el Mannequin, uso Enderman: " + ex.getMessage());
                this.entity = null;
            }
        }
        if (this.entity == null) {
            this.entity = (LivingEntity)world.spawn(location, (Class)Enderman.class, enderman -> {
                this.common((LivingEntity)enderman, b3);
                enderman.setAI((boolean)(0 != 0));
                enderman.setCarriedBlock((BlockData)null);
                return;
            });
        }
        final AttributeInstance attribute = this.entity.getAttribute(Attribute.SCALE);
        if (attribute != null) {
            attribute.setBaseValue((this.entity instanceof Mannequin) ? this.cfg.slenderScale : 0.95);
        }
        return this.entity;
    }
    
    private void common(final LivingEntity livingEntity, final boolean visibleByDefault) {
        livingEntity.setVisibleByDefault(visibleByDefault);
        livingEntity.setInvulnerable(true);
        livingEntity.setSilent(true);
        livingEntity.setPersistent(false);
        livingEntity.setGravity(false);
        livingEntity.setCollidable(false);
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setCustomNameVisible(false);
        livingEntity.getScoreboardTags().add("slender_body");
    }
    
    private boolean useMannequin() {
        return !"enderman".equalsIgnoreCase(this.cfg.slenderBody) && (!this.cfg.skinPlayer.isBlank() || !this.cfg.skinValue.isBlank());
    }
    
    private ResolvableProfile profile() {
        if (!this.cfg.skinValue.isBlank()) {
            return (ResolvableProfile)ResolvableProfile.resolvableProfile().uuid(UUID.nameUUIDFromBytes("slender".getBytes(StandardCharsets.UTF_8))).name("Slender").addProperty(this.cfg.skinSignature.isBlank() ? new ProfileProperty("textures", this.cfg.skinValue) : new ProfileProperty("textures", this.cfg.skinValue, this.cfg.skinSignature)).build();
        }
        return (ResolvableProfile)ResolvableProfile.resolvableProfile().name(this.cfg.skinPlayer).build();
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
    
    public void moveTo(final Location location, final Location location2) {
        if (!this.alive()) {
            return;
        }
        final Location clone = location.clone();
        if (location2 != null) {
            final Vector subtract = location2.toVector().subtract(clone.toVector());
            if (subtract.lengthSquared() > 1.0E-6) {
                clone.setDirection(subtract);
            }
        }
        clone.setPitch(0.0f);
        this.entity.teleport(clone);
    }
    
    public void showTo(final Player player) {
        if (this.alive()) {
            player.showEntity(this.plugin, (Entity)this.entity);
        }
    }
    
    public void hideFrom(final Player player) {
        if (this.alive()) {
            player.hideEntity(this.plugin, (Entity)this.entity);
        }
    }
    
    public void remove() {
        if (this.entity != null) {
            this.entity.remove();
        }
        this.entity = null;
    }
    
    public static Location ground(final Location location, final int n, final int n2) {
        final World world = location.getWorld();
        final int blockX = location.getBlockX();
        final int blockZ = location.getBlockZ();
        for (final int n3 : scanOrder(location.getBlockY(), n, n2)) {
            final Block block = world.getBlockAt(blockX, n3, blockZ);
            final Block relative = block.getRelative(0, 1, 0);
            if (block.isPassable() && !block.isLiquid() && relative.isPassable() && !relative.isLiquid() && block.getRelative(0, -1, 0).isSolid()) {
                return new Location(world, blockX + 0.5, (double)n3, blockZ + 0.5, location.getYaw(), 0.0f);
            }
        }
        return null;
    }
    
    static int[] scanOrder(final int n, final int a, final int b) {
        final int[] array = new int[a + b + 1];
        int n2 = 0;
        array[n2++] = n;
        for (int i = 1; i <= Math.max(a, b); ++i) {
            if (i <= b) {
                array[n2++] = n - i;
            }
            if (i <= a) {
                array[n2++] = n + i;
            }
        }
        return array;
    }
}
