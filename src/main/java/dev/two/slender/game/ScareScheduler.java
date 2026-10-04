// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.scheduler.BukkitScheduler;
import java.util.Objects;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import dev.two.slender.logic.Vec3;
import org.bukkit.util.Vector;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.util.Random;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.scheduler.BukkitTask;
import dev.two.slender.map.PageSpot;
import java.util.List;
import java.util.function.Supplier;
import java.util.UUID;
import java.util.function.Function;
import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import org.bukkit.plugin.Plugin;
import org.bukkit.Sound;

public final class ScareScheduler
{
    private static final Sound[] WHISPERS;
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final SightService sight;
    private final Flashlights flashlights;
    private final PageManager pages;
    private final Function<UUID, SurvivorState> states;
    private final Supplier<List<PageSpot>> freeSpots;
    private final List<Apparition> apparitions;
    private final List<SlenderBody> jumpscares;
    private final List<BukkitTask> tasks;
    
    public ScareScheduler(final Plugin plugin, final SlenderConfig cfg, final Msg msg, final SightService sight, final Flashlights flashlights, final PageManager pages, final Function<UUID, SurvivorState> states, final Supplier<List<PageSpot>> freeSpots) {
        this.apparitions = new ArrayList<Apparition>();
        this.jumpscares = new ArrayList<SlenderBody>();
        this.tasks = new ArrayList<BukkitTask>();
        this.plugin = plugin;
        this.cfg = cfg;
        this.msg = msg;
        this.sight = sight;
        this.flashlights = flashlights;
        this.pages = pages;
        this.states = states;
        this.freeSpots = freeSpots;
    }
    
    public void tick(final List<Player> list, final double n, final long n2, final Random random) {
        this.tasks.removeIf(bukkitTask -> bukkitTask.isCancelled() || !Bukkit.getScheduler().isQueued(bukkitTask.getTaskId()));
        for (final Player player : list) {
            if (random.nextDouble() >= 0.02 + 0.01 * n) {
                continue;
            }
            final int nextInt = random.nextInt(100);
            if (nextInt < 30) {
                this.footsteps(player, random);
            }
            else if (nextInt < 50) {
                this.branch(player, random);
            }
            else if (nextInt < 70) {
                this.whisper(player, random);
            }
            else if (nextInt < 78) {
                this.scream(player, random);
            }
            else if (nextInt < 90 && n >= 1.0) {
                this.apparition(player, n2, random);
            }
            else if (nextInt < 95 && n >= 2.0) {
                this.fakePage(player);
            }
            else {
                this.flashlights.flicker(player.getUniqueId(), 10);
            }
        }
    }
    
    private static Vector back(final Player player) {
        Vec3 normalize = SightService.dir(player.getEyeLocation()).horizontal().normalize();
        if (normalize.length() < 1.0E-6) {
            normalize = new Vec3(0.0, 0.0, 1.0);
        }
        return new Vector(-normalize.x(), 0.0, -normalize.z());
    }
    
    private static Sound stepSound(final Player player) {
        final Block block = player.getLocation().subtract(0.0, 0.2, 0.0).getBlock();
        try {
            return block.getBlockData().getSoundGroup().getStepSound();
        }
        catch (final RuntimeException ex) {
            return Sound.BLOCK_GRASS_STEP;
        }
    }
    
    public void footsteps(final Player player, final Random random) {
        back(player);
        stepSound(player);
        for (int n2 = 0; n2 < 4; ++n2) {
            this.tasks.add(Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                final int n2;
                final Object o = 5 - n2;
                if (!player2.isOnline()) {
                    return;
                }
                else {
                    player2.playSound(player2.getLocation().add(vector.clone().multiply(n)), sound, SoundCategory.HOSTILE, 0.9f, 0.8f);
                    return;
                }
            }, n2 * 10L));
        }
    }
    
    public void branch(final Player player, final Random random) {
        player.playSound(Atmosphere.around(player, random, 8.0 + random.nextDouble() * 4.0), Sound.BLOCK_WOOD_BREAK, SoundCategory.HOSTILE, 1.0f, 0.7f);
    }
    
    public void whisper(final Player player, final Random random) {
        player.playSound(player.getLocation().add(back(player).multiply(1.5)).add(0.0, 1.6, 0.0), ScareScheduler.WHISPERS[random.nextInt(ScareScheduler.WHISPERS.length)], SoundCategory.HOSTILE, 0.6f, 0.5f);
    }
    
    public void scream(final Player player, final Random random) {
        player.playSound(Atmosphere.around(player, random, 30.0), Sound.ENTITY_GHAST_SCREAM, SoundCategory.HOSTILE, 0.35f, 0.5f);
    }
    
    public boolean apparition(final Player player, final long n, final Random random) {
        final Location eyeLocation = player.getEyeLocation();
        Vec3 normalize = SightService.dir(eyeLocation).horizontal().normalize();
        if (normalize.length() < 1.0E-6) {
            normalize = new Vec3(0.0, 0.0, 1.0);
        }
        for (int i = 0; i < 8; ++i) {
            final Vec3 mul = normalize.rotateYaw((35.0 + random.nextDouble() * 25.0) * (random.nextBoolean() ? 1 : -1)).mul(15.0 + random.nextDouble() * 10.0);
            final Location ground = SlenderBody.ground(player.getLocation().add(mul.x(), 0.0, mul.z()), 4, 6);
            if (ground != null && this.sight.lineOfSight(eyeLocation, ground.clone().add(0.0, 1.5, 0.0))) {
                final SlenderBody body = new SlenderBody(this.plugin, this.cfg);
                body.spawn(ground, false);
                body.moveTo(ground, player.getLocation());
                body.showTo(player);
                this.apparitions.add(new Apparition(body, player.getUniqueId(), n + 60L));
                return true;
            }
        }
        return false;
    }
    
    public void tickApparitions(final long n) {
        final Iterator<Apparition> iterator = this.apparitions.iterator();
        while (iterator.hasNext()) {
            final Apparition apparition = iterator.next();
            final Player player = Bukkit.getPlayer(apparition.viewer());
            if (player == null || n >= apparition.expire() || !apparition.body().alive()) {
                apparition.body().remove();
                iterator.remove();
            }
            else {
                if (!this.sight.looksAt(player, 20.0, 64.0, apparition.body().center(), apparition.body().head())) {
                    continue;
                }
                final SurvivorState survivorState = this.states.apply(player.getUniqueId());
                if (survivorState != null) {
                    survivorState.stat.bump(8.0);
                }
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.5f, 0.5f);
                apparition.body().remove();
                iterator.remove();
            }
        }
    }
    
    public void fakePage(final Player player) {
        if (this.pages.hasFake(player.getUniqueId())) {
            return;
        }
        PageSpot pageSpot = null;
        double n = 30.0;
        for (final PageSpot pageSpot2 : this.freeSpots.get()) {
            final double distance = player.getLocation().distance(new Location(player.getWorld(), pageSpot2.pos().x() + 0.5, (double)pageSpot2.pos().y(), pageSpot2.pos().z() + 0.5));
            if (distance < n && distance > 4.0) {
                n = distance;
                pageSpot = pageSpot2;
            }
        }
        if (pageSpot != null) {
            this.pages.spawnFake(player.getWorld(), pageSpot, player);
        }
    }
    
    public boolean onFakePage(final Player player, final Entity entity, final Random random) {
        if (!this.pages.isFake(entity)) {
            return false;
        }
        this.pages.removeFake(entity);
        player.sendActionBar(this.msg.get("fake-page", new String[0]));
        this.whisper(player, random);
        final SurvivorState survivorState = this.states.apply(player.getUniqueId());
        if (survivorState != null) {
            survivorState.stat.bump(25.0);
        }
        return true;
    }
    
    public void jumpscare(final Player player) {
        Vec3 normalize = SightService.dir(player.getEyeLocation()).horizontal().normalize();
        if (normalize.length() < 1.0E-6) {
            normalize = new Vec3(0.0, 0.0, 1.0);
        }
        final Location add = player.getLocation().add(normalize.x() * 1.3, 0.0, normalize.z() * 1.3);
        final SlenderBody slenderBody = new SlenderBody(this.plugin, this.cfg);
        slenderBody.spawn(add, false);
        slenderBody.moveTo(add, player.getLocation());
        slenderBody.showTo(player);
        this.jumpscares.add(slenderBody);
        player.setRotation(Vec3.yawOf(normalize), -25.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.HOSTILE, 1.0f, 0.6f);
        player.playSound(player.getLocation(), Sound.ENTITY_GHAST_HURT, SoundCategory.HOSTILE, 1.0f, 0.5f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60, 0, false, false, false));
        final List<BukkitTask> tasks = this.tasks;
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final Plugin plugin = this.plugin;
        final SlenderBody obj = slenderBody;
        Objects.requireNonNull(obj);
        tasks.add(scheduler.runTaskLater(plugin, obj::remove, 30L));
    }
    
    public void clear() {
        final Iterator<Apparition> iterator = this.apparitions.iterator();
        while (iterator.hasNext()) {
            iterator.next().body().remove();
        }
        this.apparitions.clear();
        final Iterator<SlenderBody> iterator2 = this.jumpscares.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().remove();
        }
        this.jumpscares.clear();
        final Iterator<BukkitTask> iterator3 = this.tasks.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().cancel();
        }
        this.tasks.clear();
    }
    
    static {
        WHISPERS = new Sound[] { Sound.ENTITY_VEX_AMBIENT, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, Sound.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, Sound.ENTITY_VEX_AMBIENT };
    }
    
    record Apparition(SlenderBody body, UUID viewer, long expire) {}
}
