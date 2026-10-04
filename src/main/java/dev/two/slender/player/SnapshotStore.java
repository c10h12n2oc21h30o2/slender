// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.player;

import org.bukkit.configuration.MemorySection;
import java.util.Map;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.UUID;
import java.util.Optional;
import org.bukkit.GameMode;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.Attribute;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.NamespacedKey;
import java.util.Arrays;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.Iterator;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import java.util.ArrayList;
import java.util.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.logging.Logger;
import java.io.File;

public final class SnapshotStore
{
    private final File dir;
    private final Logger log;
    
    public SnapshotStore(final File file, final Logger log) {
        this.dir = file;
        this.log = log;
        if (!file.exists() && !file.mkdirs()) {
            log.warning("No se pudo crear " + String.valueOf(file));
        }
    }
    
    public PlayerSnapshot capture(final Player player) {
        final ItemStack[] contents = player.getInventory().getContents();
        final ItemStack[] array = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; ++i) {
            array[i] = ((contents[i] == null) ? ItemStack.empty() : contents[i]);
        }
        final String encodeToString = Base64.getEncoder().encodeToString(ItemStack.serializeItemsAsBytes(array));
        final ArrayList effects = new ArrayList();
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            effects.add(String.valueOf(potionEffect.getType().getKey()) + ";" + potionEffect.getDuration() + ";" + potionEffect.getAmplifier() + ";" + potionEffect.isAmbient() + ";" + potionEffect.hasParticles() + ";" + potionEffect.hasIcon());
        }
        final Location location = player.getLocation();
        return new PlayerSnapshot(encodeToString, player.getLevel(), player.getExp(), player.getHealth(), player.getFoodLevel(), player.getSaturation(), player.getGameMode().name(), player.getWalkSpeed(), player.getFlySpeed(), player.getAllowFlight(), (List<String>)effects, location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch(), player.getViewDistance(), player.getSendViewDistance());
    }
    
    public Location location(final PlayerSnapshot playerSnapshot) {
        final String[] split = playerSnapshot.location().split(",");
        if (split.length != 6) {
            return null;
        }
        final World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            return null;
        }
        return new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
    
    public void apply(final Player player, final PlayerSnapshot playerSnapshot) {
        resetForGame(player);
        try {
            player.getInventory().setContents((ItemStack[])Arrays.copyOf(ItemStack.deserializeItemsFromBytes(Base64.getDecoder().decode(playerSnapshot.itemsBase64())), player.getInventory().getSize()));
        }
        catch (final RuntimeException ex) {
            this.log.severe("No se pudo restaurar el inventario de " + player.getName() + ": " + ex.getMessage());
        }
        final Iterator<String> iterator = playerSnapshot.effects().iterator();
        while (iterator.hasNext()) {
            final String[] split = iterator.next().split(";");
            if (split.length != 6) {
                continue;
            }
            final NamespacedKey fromString = NamespacedKey.fromString(split[0]);
            final PotionEffectType potionEffectType = (fromString == null) ? null : ((PotionEffectType)Registry.EFFECT.get(fromString));
            if (potionEffectType == null) {
                continue;
            }
            player.addPotionEffect(new PotionEffect(potionEffectType, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Boolean.parseBoolean(split[3]), Boolean.parseBoolean(split[4]), Boolean.parseBoolean(split[5])));
        }
        player.setLevel(playerSnapshot.level());
        player.setExp(Math.max(0.0f, Math.min(0.9999f, playerSnapshot.exp())));
        final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        player.setHealth(Math.max(1.0, Math.min((attribute == null) ? 20.0 : attribute.getValue(), playerSnapshot.health())));
        player.setFoodLevel(playerSnapshot.food());
        player.setSaturation(playerSnapshot.saturation());
        applyState(player, playerSnapshot);
    }
    
    public static void applyState(final Player player, final PlayerSnapshot playerSnapshot) {
        try {
            player.setGameMode(GameMode.valueOf(playerSnapshot.gameMode()));
        }
        catch (final IllegalArgumentException ex) {
            player.setGameMode(GameMode.SURVIVAL);
        }
        player.setWalkSpeed(playerSnapshot.walkSpeed());
        player.setFlySpeed(playerSnapshot.flySpeed());
        player.setAllowFlight(playerSnapshot.allowFlight());
        applyView(player, playerSnapshot);
    }
    
    public static void applyView(final Player player, final PlayerSnapshot playerSnapshot) {
        if (playerSnapshot.viewDistance() > 1) {
            player.setViewDistance(playerSnapshot.viewDistance());
        }
        if (playerSnapshot.sendViewDistance() > 1) {
            player.setSendViewDistance(playerSnapshot.sendViewDistance());
        }
    }
    
    public boolean restoreState(final Player player) {
        final Optional<PlayerSnapshot> load = this.load(player.getUniqueId());
        if (load.isEmpty()) {
            return false;
        }
        applyState(player, load.get());
        this.delete(player.getUniqueId());
        return true;
    }
    
    public boolean restore(final Player player, final boolean b) {
        final Optional<PlayerSnapshot> load = this.load(player.getUniqueId());
        if (load.isEmpty()) {
            return false;
        }
        if (b) {
            Location location = this.location(load.get());
            if (location == null) {
                location = Bukkit.getWorlds().get(0).getSpawnLocation();
            }
            player.teleport(location);
        }
        this.apply(player, load.get());
        this.delete(player.getUniqueId());
        return true;
    }
    
    public static void resetForGame(final Player player) {
        player.getInventory().clear();
        final Iterator iterator = player.getActivePotionEffects().iterator();
        while (iterator.hasNext()) {
            player.removePotionEffect(((PotionEffect)iterator.next()).getType());
        }
        final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        player.setHealth((attribute == null) ? 20.0 : attribute.getValue());
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.setFireTicks(0);
        player.setLevel(0);
        player.setExp(0.0f);
        player.setGameMode(GameMode.ADVENTURE);
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setFlying(false);
        player.setAllowFlight(false);
        final AttributeInstance attribute2 = player.getAttribute(Attribute.JUMP_STRENGTH);
        if (attribute2 != null) {
            attribute2.setBaseValue(0.42);
        }
    }
    
    public void save(final UUID obj, final PlayerSnapshot playerSnapshot) {
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        final Map<String, Object> map = SnapshotCodec.toMap(playerSnapshot);
        final MemorySection obj2 = (MemorySection)yamlConfiguration;
        Objects.requireNonNull((YamlConfiguration)obj2);
        map.forEach(obj2::set);
        try {
            yamlConfiguration.save(this.file(obj));
        }
        catch (final IOException ex) {
            this.log.severe("No se pudo guardar la instant\u00e1nea de " + String.valueOf(obj) + ": " + ex.getMessage());
        }
    }
    
    public Optional<PlayerSnapshot> load(final UUID uuid) {
        final File file = this.file(uuid);
        if (!file.exists()) {
            return Optional.empty();
        }
        try {
            return Optional.of(SnapshotCodec.fromMap(YamlConfiguration.loadConfiguration(file).getValues(false)));
        }
        catch (final RuntimeException ex) {
            this.log.severe("Instant\u00e1nea corrupta " + file.getName() + ": " + ex.getMessage());
            return Optional.empty();
        }
    }
    
    public boolean has(final UUID uuid) {
        return this.file(uuid).exists();
    }
    
    public void delete(final UUID uuid) {
        final File file = this.file(uuid);
        if (file.exists() && !file.delete()) {
            this.log.warning("No se pudo borrar " + String.valueOf(file));
        }
    }
    
    private File file(final UUID obj) {
        return new File(this.dir, String.valueOf(obj) + ".yml");
    }
}
