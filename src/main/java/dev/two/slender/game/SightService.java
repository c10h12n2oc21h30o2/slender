// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import java.util.Iterator;
import java.util.Collection;
import dev.two.slender.logic.SightMath;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.FluidCollisionMode;
import dev.two.slender.logic.Vec3;
import org.bukkit.Location;
import dev.two.slender.config.SlenderConfig;

public final class SightService
{
    private final SlenderConfig cfg;
    
    public SightService(final SlenderConfig cfg) {
        this.cfg = cfg;
    }
    
    public static Vec3 vec(final Location location) {
        return new Vec3(location.getX(), location.getY(), location.getZ());
    }
    
    public static Vec3 dir(final Location location) {
        return Vec3.fromYawPitch(location.getYaw(), location.getPitch());
    }
    
    public boolean lineOfSight(final Location location, final Location location2) {
        if (location.getWorld() != location2.getWorld()) {
            return false;
        }
        final Vector subtract = location2.toVector().subtract(location.toVector());
        final double length = subtract.length();
        return length < 0.05 || location.getWorld().rayTraceBlocks(location, subtract.normalize(), length, FluidCollisionMode.NEVER, true) == null;
    }
    
    public boolean looksAt(final Player player, final double n, final double n2, final Location... array) {
        final Location eyeLocation = player.getEyeLocation();
        final Vec3 vec = vec(eyeLocation);
        final Vec3 dir = dir(eyeLocation);
        for (final Location location : array) {
            if (location != null) {
                if (location.getWorld() == eyeLocation.getWorld()) {
                    if (SightMath.inCone(vec, dir, vec(location), n, n2) && this.lineOfSight(eyeLocation, location)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean observedByAny(final Collection<Player> collection, final Location... array) {
        final Iterator<Player> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (this.looksAt(iterator.next(), this.cfg.observeHalfAngle, this.cfg.observeMaxDistance, array)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean staticLook(final Player player, final Location... array) {
        return this.looksAt(player, this.cfg.staticLookHalfAngle, this.cfg.staticLookMaxDistance, array);
    }
}
