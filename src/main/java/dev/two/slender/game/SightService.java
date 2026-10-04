package dev.two.slender.game;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.SightMath;
import dev.two.slender.logic.Vec3;
import java.util.Collection;
import java.util.Iterator;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class SightService {
    private final SlenderConfig cfg;

    public SightService(SlenderConfig var1) {
        this.cfg = var1;
    }

    public static Vec3 vec(Location var0) {
        return new Vec3(var0.getX(), var0.getY(), var0.getZ());
    }

    public static Vec3 dir(Location var0) {
        return Vec3.fromYawPitch(var0.getYaw(), var0.getPitch());
    }

    public boolean lineOfSight(Location var1, Location var2) {
        if (var1.getWorld() != var2.getWorld()) {
            return false;
        } else {
            Vector var3 = var2.toVector().subtract(var1.toVector());
            double var4 = var3.length();
            if (var4 < 0.05) {
                return true;
            } else {
                return var1.getWorld().rayTraceBlocks(var1, var3.normalize(), var4, FluidCollisionMode.NEVER, true) == null;
            }
        }
    }

    public boolean looksAt(Player var1, double var2, double var4, Location... var6) {
        Location var7 = var1.getEyeLocation();
        Vec3 var8 = vec(var7);
        Vec3 var9 = dir(var7);
        Location[] var10 = var6;
        int var11 = var6.length;

        for(int var12 = 0; var12 < var11; ++var12) {
            Location var13 = var10[var12];
            if (var13 != null && var13.getWorld() == var7.getWorld() && SightMath.inCone(var8, var9, vec(var13), var2, var4) && this.lineOfSight(var7, var13)) {
                return true;
            }
        }

        return false;
    }

    public boolean observedByAny(Collection<Player> var1, Location... var2) {
        Iterator var3 = var1.iterator();

        Player var4;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            var4 = (Player)var3.next();
        } while(!this.looksAt(var4, this.cfg.observeHalfAngle, this.cfg.observeMaxDistance, var2));

        return true;
    }

    public boolean staticLook(Player var1, Location... var2) {
        return this.looksAt(var1, this.cfg.staticLookHalfAngle, this.cfg.staticLookMaxDistance, var2);
    }
}
