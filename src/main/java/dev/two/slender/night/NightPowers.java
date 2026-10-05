package dev.two.slender.night;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.game.Director;
import dev.two.slender.game.Game;
import dev.two.slender.game.Hud;
import dev.two.slender.game.SightService;
import dev.two.slender.game.SlenderBody;
import dev.two.slender.game.SurvivorState;
import dev.two.slender.logic.DirectorMath;
import dev.two.slender.logic.Vec3;
import dev.two.slender.map.SpotRules;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/** Las 6 habilidades nuevas de Slender en LA NOCHE ENTERA (spec §6). Usa los modelos puros del paquete. */
public final class NightPowers {
    public static final String TAG = "slender_night";
    private final NightPlan plan;
    private final NightSettings s;
    private final SlenderConfig cfg;
    private final int phase;
    private final HeadLedger heads;
    private final Function<UUID, Optional<Item>> headEntity;
    private final AbilityClock clock = new AbilityClock();
    private final SprintEar ear = new SprintEar();
    private final Map<UUID, Double> exposed = new HashMap<>();
    private final Map<UUID, Double> grabbing = new HashMap<>();
    private Vec3 lastBody;
    private Mannequin mimic;
    private UUID mimicTarget;
    private double mimicUntil;
    private UUID chaseTarget;
    private double chaseUntil;
    private boolean wasObserved;

    public NightPowers(NightPlan plan, NightSettings s, SlenderConfig cfg, int phase, HeadLedger heads,
                       Function<UUID, Optional<Item>> headEntity) {
        this.plan = plan;
        this.s = s;
        this.cfg = cfg;
        this.phase = phase;
        this.heads = heads;
        this.headEntity = headEntity;
    }

    public static double chaseStep(double speed, double dtSeconds) {
        return speed * dtSeconds;
    }

    /** Sitio para el falso compañero: a [min, max] del objetivo, fuera de su vista (60°…300° respecto a view). */
    public static Vec3 mimicSpot(Vec3 target, Vec3 view, double min, double max, Random r) {
        Vec3 h = view.horizontal();
        double base = h.length() < 1e-6 ? r.nextDouble() * Math.PI * 2 : Math.atan2(h.z(), h.x());
        double off = Math.PI / 3 + r.nextDouble() * (4 * Math.PI / 3);
        double dist = min + r.nextDouble() * (max - min);
        return new Vec3(target.x() + Math.cos(base + off) * dist, target.y(), target.z() + Math.sin(base + off) * dist);
    }

    private boolean has(Ability a) {
        return this.plan.abilities(this.phase).contains(a);
    }

    private double freq(Ability a) {
        return this.plan.frequency(a, this.phase);
    }

    /** Cada segundo: luces, cabezas, fogonazo, empezar a suplantar, fin de la suplantación. */
    public void second(Game g) {
        double now = g.tick() / 20.0;
        List<Player> alive = g.aliveSurvivors();
        SlenderBody body = g.body();
        Random r = g.random();
        boolean bodyAlive = body != null && body.alive();
        if (this.has(Ability.LIGHTS) && bodyAlive) {
            Vec3 b = SightService.vec(body.location());
            if (this.lastBody == null || b.distance(this.lastBody) > 5) {
                double secs = this.s.lights().minSec() + r.nextDouble() * (this.s.lights().maxSec() - this.s.lights().minSec());
                g.lightsOut(body.location(), this.s.lights().radius(), g.tick() + (long) (secs * 20));
            }
            this.lastBody = b;
        } else if (!bodyAlive) {
            this.lastBody = null;
        }
        if (this.has(Ability.HEADS)) {
            this.huntHeads(g, alive, body, bodyAlive);
        }
        if (this.has(Ability.FLASH) && bodyAlive) {
            for (Player p : alive) {
                if (g.sight().staticLook(p, body.center(), body.head())) {
                    continue;
                }
                if (this.clock.ready(Ability.FLASH, p.getUniqueId(), now)
                        && AbilityClock.roll(this.s.flash().chance(), this.freq(Ability.FLASH), 1.0, r.nextDouble())) {
                    this.flash(g, p);
                    this.clock.used(Ability.FLASH, p.getUniqueId(), now, this.s.flash().cooldown());
                }
            }
        }
        if (this.mimic != null && now >= this.mimicUntil) {
            this.endMimic(g, now);
        }
        if (this.has(Ability.MIMIC) && this.mimic == null && alive.size() >= 2 && this.clock.locked() == null
                && this.clock.ready(Ability.MIMIC, null, now)
                && AbilityClock.roll(this.s.mimic().chance(), this.freq(Ability.MIMIC), 1.0, r.nextDouble())) {
            this.startMimic(g, alive, now);
        }
    }

    /** Cada 2 ticks: oír correr, pila al mirarlo, suplantación y persecución en curso. */
    public void tick(Game g) {
        double now = g.tick() / 20.0;
        List<Player> alive = g.aliveSurvivors();
        SlenderBody body = g.body();
        Director d = g.director();
        boolean bodyAlive = body != null && body.alive();
        if (this.has(Ability.SPRINT) && d != null) {
            for (Player p : alive) {
                if (this.ear.update(p.getUniqueId(), p.isSprinting(), 0.1, this.plan.sprintThreshold(this.phase))) {
                    d.lurePlayer(p.getUniqueId(), this.s.sprint().distance());
                    g.msg().send(p, "noche-te-oigo");
                }
            }
        }
        if (this.has(Ability.LIGHTS) && bodyAlive) {
            double extra = (this.s.lights().lookDrain() - 1.0) * this.cfg.batteryDrainPerSecond * 0.1;
            for (Player p : alive) {
                if (g.flashlights().isOn(p.getUniqueId()) && g.sight().staticLook(p, body.center(), body.head())) {
                    g.flashlights().drain(p.getUniqueId(), extra);
                }
            }
        }
        if (this.mimic != null) {
            this.tickMimic(g, alive, now);
        }
        if (this.has(Ability.CHASE) && d != null) {
            this.tickChase(g, d, alive, body, bodyAlive, now);
        }
    }

    private void huntHeads(Game g, List<Player> alive, SlenderBody body, boolean bodyAlive) {
        Director d = g.director();
        for (UUID owner : this.heads.onGround()) {
            Optional<Item> e = this.headEntity.apply(owner);
            if (e.isEmpty() || !e.get().isValid()) {
                continue;
            }
            Location at = e.get().getLocation();
            boolean guarded = alive.stream().anyMatch(p -> p.getWorld().equals(at.getWorld())
                    && p.getLocation().distance(at) <= this.s.heads().radius());
            if (guarded) {
                this.exposed.remove(owner);
                this.grabbing.remove(owner);
                continue;
            }
            double secs = this.exposed.merge(owner, 1.0, Double::sum);
            if (secs >= this.s.heads().seconds() && d != null && !d.suspended() && !d.observed(alive)
                    && (!bodyAlive || body.location().distance(at) > 3)) {
                d.lureTo(at);
            }
            if (bodyAlive && body.location().getWorld().equals(at.getWorld()) && body.location().distance(at) <= 3) {
                double grab = this.grabbing.merge(owner, 1.0, Double::sum);
                if (grab >= this.s.heads().grabSeconds()) {
                    e.get().remove();
                    this.heads.stolen(owner);
                    this.exposed.remove(owner);
                    this.grabbing.remove(owner);
                    String name = Optional.ofNullable(Bukkit.getOfflinePlayer(owner).getName()).orElse("?");
                    for (Player p : g.online()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.HOSTILE, 0.7F, 0.5F);
                        g.msg().send(p, "noche-cabeza-robada", "player", name);
                    }
                }
            }
        }
    }

    private void flash(Game g, Player p) {
        Component bar = Component.text("█".repeat(24), NamedTextColor.WHITE);
        Hud.title(p, bar, bar, 0, 6, 30);
        int ticks = (int) (this.s.flash().seconds() * 20);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ticks, 0, false, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 1, false, false, false));
        p.playSound(p.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.HOSTILE, 1.0F, 2.0F);
        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 0.8F, 2.0F);
        SurvivorState st = g.stateOf(p.getUniqueId());
        if (st != null) {
            st.stat.bump(this.s.flash().staticBump());
        }
    }

    private void startMimic(Game g, List<Player> alive, double now) {
        Random r = g.random();
        Director d = g.director();
        if (d == null || !this.clock.lock(Ability.MIMIC)) {
            return;
        }
        Player target = alive.stream().filter(p -> p.getUniqueId().equals(d.target())).findFirst()
                .orElse(alive.get(r.nextInt(alive.size())));
        List<Player> others = new ArrayList<>(alive);
        others.remove(target);
        Player face = others.get(r.nextInt(others.size()));
        World w = target.getWorld();
        Location spot = null;
        for (int i = 0; i < 10 && spot == null; i++) {
            Vec3 v = mimicSpot(SightService.vec(target.getLocation()), SightService.dir(target.getEyeLocation()),
                    this.s.mimic().minDist(), this.s.mimic().maxDist(), r);
            Location gnd = SlenderBody.ground(new Location(w, v.x(), v.y(), v.z()), 6, 8);
            if (gnd != null && SpotRules.allows(g.map().id(), gnd.getBlockX(), gnd.getBlockY(), gnd.getBlockZ())) {
                spot = gnd;
            }
        }
        if (spot == null) {
            this.clock.unlock(Ability.MIMIC);
            this.clock.used(Ability.MIMIC, null, now, 10);
            return;
        }
        d.suspend(true);
        if (g.body() != null) {
            g.body().remove();
        }
        Location at = spot;
        at.setDirection(target.getLocation().toVector().subtract(at.toVector()));
        this.mimic = w.spawn(at, Mannequin.class, m -> {
            m.setProfile(ResolvableProfile.resolvableProfile(face.getPlayerProfile()));
            m.customName(Component.text(face.getName()));
            m.setCustomNameVisible(true);
            m.setInvulnerable(true);
            m.setPersistent(false);
            m.setSilent(true);
            m.getScoreboardTags().add(TAG);
        });
        this.mimicTarget = target.getUniqueId();
        this.mimicUntil = now + this.s.mimic().duration();
    }

    private void tickMimic(Game g, List<Player> alive, double now) {
        if (!this.mimic.isValid()) {
            this.endMimic(g, now);
            return;
        }
        Location m = this.mimic.getLocation();
        for (Player p : alive) {
            if (p.getWorld().equals(m.getWorld()) && p.getLocation().distance(m) <= this.s.mimic().reveal()) {
                SlenderBody body = g.body();
                if (body != null) {
                    body.spawn(m, true);
                    body.moveTo(m, p.getLocation());
                }
                if (g.scares() != null) {
                    g.scares().jumpscare(p);
                }
                SurvivorState st = g.stateOf(p.getUniqueId());
                if (st != null) {
                    st.stat.bump(this.s.mimic().staticBump());
                }
                this.endMimic(g, now);
                return;
            }
        }
        Player target = Bukkit.getPlayer(this.mimicTarget);
        if (target != null && target.getWorld().equals(m.getWorld())) {
            Vec3 next = DirectorMath.stepTowards(SightService.vec(m), SightService.vec(target.getLocation()), 0.1);
            Location gnd = SlenderBody.ground(new Location(m.getWorld(), next.x(), m.getY(), next.z()), 1, 2);
            if (gnd != null && g.sight().lineOfSight(m.clone().add(0, 1, 0), gnd.clone().add(0, 1, 0))) {
                gnd.setDirection(target.getLocation().toVector().subtract(gnd.toVector()));
                this.mimic.teleport(gnd);
            }
        }
    }

    private void endMimic(Game g, double now) {
        if (this.mimic != null) {
            this.mimic.remove();
            this.mimic = null;
        }
        this.mimicTarget = null;
        if (g.director() != null) {
            g.director().suspend(false);
        }
        this.clock.unlock(Ability.MIMIC);
        this.clock.used(Ability.MIMIC, null, now, this.s.mimic().cooldown());
    }

    private void tickChase(Game g, Director d, List<Player> alive, SlenderBody body, boolean bodyAlive, double now) {
        if (this.chaseTarget != null) {
            Player t = Bukkit.getPlayer(this.chaseTarget);
            boolean targetOk = t != null && alive.contains(t);
            if (!targetOk || !bodyAlive || now >= this.chaseUntil) {
                this.endChase(g, d, now);
                return;
            }
            Location from = body.location();
            Location last = null;
            for (Vec3 v : DirectorMath.subSteps(SightService.vec(from), SightService.vec(t.getLocation()),
                    chaseStep(this.s.chase().speed(), 0.1), 0.5)) {
                Location gnd = SlenderBody.ground(new Location(from.getWorld(), v.x(), from.getY(), v.z()), 1, 2);
                if (gnd == null) {
                    break;
                }
                Location step = new Location(from.getWorld(), v.x(), gnd.getY(), v.z());
                if (!g.sight().lineOfSight(from.clone().add(0, 1, 0), step.clone().add(0, 1, 0))) {
                    break;
                }
                from = step;
                last = step;
            }
            if (last != null) {
                body.moveTo(last, t.getLocation());
            }
            if (body.location().distance(t.getLocation()) < this.cfg.catchDistance
                    && g.sight().lineOfSight(body.head(), t.getEyeLocation())) {
                g.kill(t, "caught");
                this.endChase(g, d, now);
            }
            return;
        }
        if (!bodyAlive || this.clock.locked() != null) {
            this.wasObserved = false;
            return;
        }
        Player target = alive.stream().filter(p -> p.getUniqueId().equals(d.target())).findFirst().orElse(null);
        boolean observed = target != null && target.getWorld().equals(body.location().getWorld())
                && target.getLocation().distance(body.location()) <= this.s.chase().radius()
                && g.sight().observedByAny(List.of(target), body.center(), body.head());
        if (observed && !this.wasObserved && this.clock.ready(Ability.CHASE, null, now)
                && g.random().nextDouble() < this.s.chase().chance() * this.freq(Ability.CHASE)
                && this.clock.lock(Ability.CHASE)) {
            this.chaseTarget = target.getUniqueId();
            this.chaseUntil = now + this.s.chase().duration();
            d.suspend(true);
        }
        this.wasObserved = observed;
    }

    private void endChase(Game g, Director d, double now) {
        this.chaseTarget = null;
        if (g.body() != null && g.body().alive()) {
            g.body().remove();
        }
        d.suspend(false);
        this.clock.unlock(Ability.CHASE);
        this.clock.used(Ability.CHASE, null, now, this.s.chase().cooldown());
        this.wasObserved = false;
    }

    public void cleanup(Game g) {
        if (this.mimic != null) {
            this.mimic.remove();
            this.mimic = null;
        }
        this.chaseTarget = null;
        this.exposed.clear();
        this.grabbing.clear();
    }
}
