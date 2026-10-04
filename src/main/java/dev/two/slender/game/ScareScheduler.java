package dev.two.slender.game;

import dev.two.slender.config.Msg;
import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.Vec3;
import dev.two.slender.map.PageSpot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public final class ScareScheduler {
    private static final Sound[] WHISPERS;
    private final Plugin plugin;
    private final SlenderConfig cfg;
    private final Msg msg;
    private final SightService sight;
    private final Flashlights flashlights;
    private final PageManager pages;
    private final Function<UUID, SurvivorState> states;
    private final Supplier<List<PageSpot>> freeSpots;
    private final List<Apparition> apparitions = new ArrayList();
    private final List<SlenderBody> jumpscares = new ArrayList();
    private final List<BukkitTask> tasks = new ArrayList();

    public ScareScheduler(Plugin var1, SlenderConfig var2, Msg var3, SightService var4, Flashlights var5, PageManager var6, Function<UUID, SurvivorState> var7, Supplier<List<PageSpot>> var8) {
        this.plugin = var1;
        this.cfg = var2;
        this.msg = var3;
        this.sight = var4;
        this.flashlights = var5;
        this.pages = var6;
        this.states = var7;
        this.freeSpots = var8;
    }

    public void tick(List<Player> var1, double var2, long var4, Random var6) {
        this.tasks.removeIf((var0) -> {
            return var0.isCancelled() || !Bukkit.getScheduler().isQueued(var0.getTaskId());
        });
        Iterator var7 = var1.iterator();

        while(true) {
            while(true) {
                Player var8;
                do {
                    if (!var7.hasNext()) {
                        return;
                    }

                    var8 = (Player)var7.next();
                } while(var6.nextDouble() >= 0.02 + 0.01 * var2);

                int var9 = var6.nextInt(100);
                if (var9 < 30) {
                    this.footsteps(var8, var6);
                } else if (var9 < 50) {
                    this.branch(var8, var6);
                } else if (var9 < 70) {
                    this.whisper(var8, var6);
                } else if (var9 < 78) {
                    this.scream(var8, var6);
                } else if (var9 < 90 && var2 >= 1.0) {
                    this.apparition(var8, var4, var6);
                } else if (var9 < 95 && var2 >= 2.0) {
                    this.fakePage(var8);
                } else {
                    this.flashlights.flicker(var8.getUniqueId(), 10);
                }
            }
        }
    }

    private static Vector back(Player var0) {
        Vec3 var1 = SightService.dir(var0.getEyeLocation()).horizontal().normalize();
        if (var1.length() < 1.0E-6) {
            var1 = new Vec3(0.0, 0.0, 1.0);
        }

        return new Vector(-var1.x(), 0.0, -var1.z());
    }

    private static Sound stepSound(Player var0) {
        Block var1 = var0.getLocation().subtract(0.0, 0.2, 0.0).getBlock();

        try {
            return var1.getBlockData().getSoundGroup().getStepSound();
        } catch (RuntimeException var3) {
            return Sound.BLOCK_GRASS_STEP;
        }
    }

    public void footsteps(Player var1, Random var2) {
        Vector var3 = back(var1);
        Sound var4 = stepSound(var1);

        for(int var5 = 0; var5 < 4; ++var5) {
            double var6 = (double)(5 - var5);
            this.tasks.add(Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                if (var1.isOnline()) {
                    var1.playSound(var1.getLocation().add(var3.clone().multiply(var6)), var4, SoundCategory.HOSTILE, 0.9F, 0.8F);
                }
            }, (long)var5 * 10L));
        }

    }

    public void branch(Player var1, Random var2) {
        var1.playSound(Atmosphere.around(var1, var2, 8.0 + var2.nextDouble() * 4.0), Sound.BLOCK_WOOD_BREAK, SoundCategory.HOSTILE, 1.0F, 0.7F);
    }

    public void whisper(Player var1, Random var2) {
        Location var3 = var1.getLocation().add(back(var1).multiply(1.5)).add(0.0, 1.6, 0.0);
        var1.playSound(var3, WHISPERS[var2.nextInt(WHISPERS.length)], SoundCategory.HOSTILE, 0.6F, 0.5F);
    }

    public void scream(Player var1, Random var2) {
        var1.playSound(Atmosphere.around(var1, var2, 30.0), Sound.ENTITY_GHAST_SCREAM, SoundCategory.HOSTILE, 0.35F, 0.5F);
    }

    public boolean apparition(Player var1, long var2, Random var4) {
        Location var5 = var1.getEyeLocation();
        Vec3 var6 = SightService.dir(var5).horizontal().normalize();
        if (var6.length() < 1.0E-6) {
            var6 = new Vec3(0.0, 0.0, 1.0);
        }

        for(int var7 = 0; var7 < 8; ++var7) {
            double var8 = (35.0 + var4.nextDouble() * 25.0) * (double)(var4.nextBoolean() ? 1 : -1);
            Vec3 var10 = var6.rotateYaw(var8).mul(15.0 + var4.nextDouble() * 10.0);
            Location var11 = SlenderBody.ground(var1.getLocation().add(var10.x(), 0.0, var10.z()), 4, 6);
            if (var11 != null && this.sight.lineOfSight(var5, var11.clone().add(0.0, 1.5, 0.0))) {
                SlenderBody var12 = new SlenderBody(this.plugin, this.cfg);
                var12.spawn(var11, false);
                var12.moveTo(var11, var1.getLocation());
                var12.showTo(var1);
                this.apparitions.add(new Apparition(var12, var1.getUniqueId(), var2 + 60L));
                return true;
            }
        }

        return false;
    }

    public void tickApparitions(long var1) {
        Iterator var3 = this.apparitions.iterator();

        while(true) {
            while(var3.hasNext()) {
                Apparition var4 = (Apparition)var3.next();
                Player var5 = Bukkit.getPlayer(var4.viewer());
                if (var5 != null && var1 < var4.expire() && var4.body().alive()) {
                    if (this.sight.looksAt(var5, 20.0, 64.0, var4.body().center(), var4.body().head())) {
                        SurvivorState var6 = (SurvivorState)this.states.apply(var5.getUniqueId());
                        if (var6 != null) {
                            var6.stat.bump(8.0);
                        }

                        var5.playSound(var5.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.5F, 0.5F);
                        var4.body().remove();
                        var3.remove();
                    }
                } else {
                    var4.body().remove();
                    var3.remove();
                }
            }

            return;
        }
    }

    public void fakePage(Player var1) {
        if (!this.pages.hasFake(var1.getUniqueId())) {
            PageSpot var2 = null;
            double var3 = 30.0;
            Iterator var5 = ((List)this.freeSpots.get()).iterator();

            while(var5.hasNext()) {
                PageSpot var6 = (PageSpot)var5.next();
                double var7 = var1.getLocation().distance(new Location(var1.getWorld(), (double)var6.pos().x() + 0.5, (double)var6.pos().y(), (double)var6.pos().z() + 0.5));
                if (var7 < var3 && var7 > 4.0) {
                    var3 = var7;
                    var2 = var6;
                }
            }

            if (var2 != null) {
                this.pages.spawnFake(var1.getWorld(), var2, var1);
            }

        }
    }

    public boolean onFakePage(Player var1, Entity var2, Random var3) {
        if (!this.pages.isFake(var2)) {
            return false;
        } else {
            this.pages.removeFake(var2);
            var1.sendActionBar((Component)this.msg.get("fake-page"));
            this.whisper(var1, var3);
            SurvivorState var4 = (SurvivorState)this.states.apply(var1.getUniqueId());
            if (var4 != null) {
                var4.stat.bump(25.0);
            }

            return true;
        }
    }

    public void jumpscare(Player var1) {
        Location var2 = var1.getEyeLocation();
        Vec3 var3 = SightService.dir(var2).horizontal().normalize();
        if (var3.length() < 1.0E-6) {
            var3 = new Vec3(0.0, 0.0, 1.0);
        }

        Location var4 = var1.getLocation().add(var3.x() * 1.3, 0.0, var3.z() * 1.3);
        SlenderBody var5 = new SlenderBody(this.plugin, this.cfg);
        var5.spawn(var4, false);
        var5.moveTo(var4, var1.getLocation());
        var5.showTo(var1);
        this.jumpscares.add(var5);
        var1.setRotation(Vec3.yawOf(var3), -25.0F);
        var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.HOSTILE, 1.0F, 0.6F);
        var1.playSound(var1.getLocation(), Sound.ENTITY_GHAST_HURT, SoundCategory.HOSTILE, 1.0F, 0.5F);
        var1.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60, 0, false, false, false));
        List var10000 = this.tasks;
        BukkitScheduler var10001 = Bukkit.getScheduler();
        Plugin var10002 = this.plugin;
        Objects.requireNonNull(var5);
        var10000.add(var10001.runTaskLater(var10002, var5::remove, 30L));
    }

    public void clear() {
        Iterator var1 = this.apparitions.iterator();

        while(var1.hasNext()) {
            Apparition var2 = (Apparition)var1.next();
            var2.body().remove();
        }

        this.apparitions.clear();
        var1 = this.jumpscares.iterator();

        while(var1.hasNext()) {
            SlenderBody var3 = (SlenderBody)var1.next();
            var3.remove();
        }

        this.jumpscares.clear();
        var1 = this.tasks.iterator();

        while(var1.hasNext()) {
            BukkitTask var4 = (BukkitTask)var1.next();
            var4.cancel();
        }

        this.tasks.clear();
    }

    static {
        WHISPERS = new Sound[]{Sound.ENTITY_VEX_AMBIENT, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, Sound.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, Sound.ENTITY_VEX_AMBIENT};
    }

    private static record Apparition(SlenderBody body, UUID viewer, long expire) {
        private Apparition(SlenderBody body, UUID viewer, long expire) {
            this.body = body;
            this.viewer = viewer;
            this.expire = expire;
        }

        public SlenderBody body() {
            return this.body;
        }

        public UUID viewer() {
            return this.viewer;
        }

        public long expire() {
            return this.expire;
        }
    }
}
