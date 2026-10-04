package dev.two.slender.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class SnapshotStore {
    private final File dir;
    private final Logger log;

    public SnapshotStore(File var1, Logger var2) {
        this.dir = var1;
        this.log = var2;
        if (!var1.exists() && !var1.mkdirs()) {
            var2.warning("No se pudo crear " + String.valueOf(var1));
        }

    }

    public PlayerSnapshot capture(Player var1) {
        ItemStack[] var2 = var1.getInventory().getContents();
        ItemStack[] var3 = new ItemStack[var2.length];

        for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = var2[var4] == null ? ItemStack.empty() : var2[var4];
        }

        String var8 = Base64.getEncoder().encodeToString(ItemStack.serializeItemsAsBytes(var3));
        ArrayList var5 = new ArrayList();
        Iterator var6 = var1.getActivePotionEffects().iterator();

        while(var6.hasNext()) {
            PotionEffect var7 = (PotionEffect)var6.next();
            String var10001 = String.valueOf(var7.getType().getKey());
            var5.add(var10001 + ";" + var7.getDuration() + ";" + var7.getAmplifier() + ";" + var7.isAmbient() + ";" + var7.hasParticles() + ";" + var7.hasIcon());
        }

        Location var9 = var1.getLocation();
        String var10000 = var9.getWorld().getName();
        String var10 = var10000 + "," + var9.getX() + "," + var9.getY() + "," + var9.getZ() + "," + var9.getYaw() + "," + var9.getPitch();
        return new PlayerSnapshot(var8, var1.getLevel(), var1.getExp(), var1.getHealth(), var1.getFoodLevel(), var1.getSaturation(), var1.getGameMode().name(), var1.getWalkSpeed(), var1.getFlySpeed(), var1.getAllowFlight(), var5, var10, var1.getViewDistance(), var1.getSendViewDistance());
    }

    public Location location(PlayerSnapshot var1) {
        String[] var2 = var1.location().split(",");
        if (var2.length != 6) {
            return null;
        } else {
            World var3 = Bukkit.getWorld(var2[0]);
            return var3 == null ? null : new Location(var3, Double.parseDouble(var2[1]), Double.parseDouble(var2[2]), Double.parseDouble(var2[3]), Float.parseFloat(var2[4]), Float.parseFloat(var2[5]));
        }
    }

    public void apply(Player var1, PlayerSnapshot var2) {
        resetForGame(var1);

        try {
            ItemStack[] var3 = ItemStack.deserializeItemsFromBytes(Base64.getDecoder().decode(var2.itemsBase64()));
            ItemStack[] var4 = (ItemStack[])Arrays.copyOf(var3, var1.getInventory().getSize());
            var1.getInventory().setContents(var4);
        } catch (RuntimeException var8) {
            Logger var10000 = this.log;
            String var10001 = var1.getName();
            var10000.severe("No se pudo restaurar el inventario de " + var10001 + ": " + var8.getMessage());
        }

        Iterator var9 = var2.effects().iterator();

        while(var9.hasNext()) {
            String var11 = (String)var9.next();
            String[] var5 = var11.split(";");
            if (var5.length == 6) {
                NamespacedKey var6 = NamespacedKey.fromString(var5[0]);
                PotionEffectType var7 = var6 == null ? null : (PotionEffectType)Registry.EFFECT.get(var6);
                if (var7 != null) {
                    var1.addPotionEffect(new PotionEffect(var7, Integer.parseInt(var5[1]), Integer.parseInt(var5[2]), Boolean.parseBoolean(var5[3]), Boolean.parseBoolean(var5[4]), Boolean.parseBoolean(var5[5])));
                }
            }
        }

        var1.setLevel(var2.level());
        var1.setExp(Math.max(0.0F, Math.min(0.9999F, var2.exp())));
        AttributeInstance var10 = var1.getAttribute(Attribute.MAX_HEALTH);
        double var12 = var10 == null ? 20.0 : var10.getValue();
        var1.setHealth(Math.max(1.0, Math.min(var12, var2.health())));
        var1.setFoodLevel(var2.food());
        var1.setSaturation(var2.saturation());
        applyState(var1, var2);
    }

    public static void applyState(Player var0, PlayerSnapshot var1) {
        try {
            var0.setGameMode(GameMode.valueOf(var1.gameMode()));
        } catch (IllegalArgumentException var3) {
            var0.setGameMode(GameMode.SURVIVAL);
        }

        var0.setWalkSpeed(var1.walkSpeed());
        var0.setFlySpeed(var1.flySpeed());
        var0.setAllowFlight(var1.allowFlight());
        applyView(var0, var1);
    }

    public static void applyView(Player var0, PlayerSnapshot var1) {
        if (var1.viewDistance() > 1) {
            var0.setViewDistance(var1.viewDistance());
        }

        if (var1.sendViewDistance() > 1) {
            var0.setSendViewDistance(var1.sendViewDistance());
        }

    }

    public boolean restoreState(Player var1) {
        Optional var2 = this.load(var1.getUniqueId());
        if (var2.isEmpty()) {
            return false;
        } else {
            applyState(var1, (PlayerSnapshot)var2.get());
            this.delete(var1.getUniqueId());
            return true;
        }
    }

    public boolean restore(Player var1, boolean var2) {
        Optional var3 = this.load(var1.getUniqueId());
        if (var3.isEmpty()) {
            return false;
        } else {
            if (var2) {
                Location var4 = this.location((PlayerSnapshot)var3.get());
                if (var4 == null) {
                    var4 = ((World)Bukkit.getWorlds().get(0)).getSpawnLocation();
                }

                var1.teleport(var4);
            }

            this.apply(var1, (PlayerSnapshot)var3.get());
            this.delete(var1.getUniqueId());
            return true;
        }
    }

    public static void resetForGame(Player var0) {
        var0.getInventory().clear();
        Iterator var1 = var0.getActivePotionEffects().iterator();

        while(var1.hasNext()) {
            PotionEffect var2 = (PotionEffect)var1.next();
            var0.removePotionEffect(var2.getType());
        }

        AttributeInstance var3 = var0.getAttribute(Attribute.MAX_HEALTH);
        var0.setHealth(var3 == null ? 20.0 : var3.getValue());
        var0.setFoodLevel(20);
        var0.setSaturation(20.0F);
        var0.setFireTicks(0);
        var0.setLevel(0);
        var0.setExp(0.0F);
        var0.setGameMode(GameMode.ADVENTURE);
        var0.setWalkSpeed(0.2F);
        var0.setFlySpeed(0.1F);
        var0.setFlying(false);
        var0.setAllowFlight(false);
        AttributeInstance var4 = var0.getAttribute(Attribute.JUMP_STRENGTH);
        if (var4 != null) {
            var4.setBaseValue(0.42);
        }

    }

    public void save(UUID var1, PlayerSnapshot var2) {
        YamlConfiguration var3 = new YamlConfiguration();
        Map<String, Object> var10000 = SnapshotCodec.toMap(var2);
        var10000.forEach(var3::set);

        try {
            var3.save(this.file(var1));
        } catch (IOException var5) {
            Logger var6 = this.log;
            String var10001 = String.valueOf(var1);
            var6.severe("No se pudo guardar la instantánea de " + var10001 + ": " + var5.getMessage());
        }

    }

    public Optional<PlayerSnapshot> load(UUID var1) {
        File var2 = this.file(var1);
        if (!var2.exists()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(SnapshotCodec.fromMap(YamlConfiguration.loadConfiguration(var2).getValues(false)));
            } catch (RuntimeException var4) {
                Logger var10000 = this.log;
                String var10001 = var2.getName();
                var10000.severe("Instantánea corrupta " + var10001 + ": " + var4.getMessage());
                return Optional.empty();
            }
        }
    }

    public boolean has(UUID var1) {
        return this.file(var1).exists();
    }

    public void delete(UUID var1) {
        File var2 = this.file(var1);
        if (var2.exists() && !var2.delete()) {
            this.log.warning("No se pudo borrar " + String.valueOf(var2));
        }

    }

    private File file(UUID var1) {
        return new File(this.dir, String.valueOf(var1) + ".yml");
    }
}
