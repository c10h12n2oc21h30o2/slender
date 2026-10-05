package dev.two.slender.night;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Cabezas de la fase: quién está muerto, qué cabezas hay en el suelo, quién lleva cuáles (spec §7). Sin Bukkit. */
public final class HeadLedger {
    private final Set<UUID> alive = new LinkedHashSet<>();
    private final Set<UUID> dead = new LinkedHashSet<>();
    private final Set<UUID> escaped = new LinkedHashSet<>();
    private final Set<UUID> onGround = new LinkedHashSet<>();
    private final Map<UUID, UUID> carrierOf = new LinkedHashMap<>();
    private final Set<UUID> rescued = new LinkedHashSet<>();

    public HeadLedger(Collection<UUID> alive) {
        this.alive.addAll(alive);
    }

    /** Muere (o se va): cae su cabeza y las que llevaba. Devuelve los dueños de las cabezas que caen. */
    public List<UUID> died(UUID who) {
        List<UUID> out = new ArrayList<>();
        if (!this.alive.remove(who)) {
            return out;
        }
        this.dead.add(who);
        out.add(who);
        this.onGround.add(who);
        for (UUID owner : this.carriedBy(who)) {
            this.carrierOf.remove(owner);
            this.onGround.add(owner);
            out.add(owner);
        }
        return out;
    }

    public boolean pickUp(UUID owner, UUID carrier) {
        if (!this.alive.contains(carrier) || !this.onGround.contains(owner)) {
            return false;
        }
        this.onGround.remove(owner);
        this.carrierOf.put(owner, carrier);
        return true;
    }

    public void revived(UUID owner) {
        this.carrierOf.remove(owner);
        this.onGround.remove(owner);
        this.dead.remove(owner);
        this.alive.add(owner);
    }

    /** Escapa: los dueños de las cabezas que lleva quedan rescatados. */
    public List<UUID> escaped(UUID carrier) {
        this.alive.remove(carrier);
        this.escaped.add(carrier);
        List<UUID> out = new ArrayList<>(this.carriedBy(carrier));
        for (UUID owner : out) {
            this.carrierOf.remove(owner);
        }
        this.rescued.addAll(out);
        return out;
    }

    public void stolen(UUID owner) {
        this.onGround.remove(owner);
        this.carrierOf.remove(owner);
    }

    public Set<UUID> onGround() {
        return Set.copyOf(this.onGround);
    }

    public Set<UUID> carriedBy(UUID carrier) {
        Set<UUID> out = new LinkedHashSet<>();
        this.carrierOf.forEach((owner, c) -> {
            if (c.equals(carrier)) {
                out.add(owner);
            }
        });
        return out;
    }

    public boolean alive(UUID id) {
        return this.alive.contains(id);
    }

    public Set<UUID> rescued() {
        return Set.copyOf(this.rescued);
    }

    /** Muertos al acabar que ni revivieron ni fueron rescatados (cabeza abandonada o robada). */
    public Set<UUID> lostAtEnd() {
        Set<UUID> out = new LinkedHashSet<>(this.dead);
        out.removeAll(this.rescued);
        return out;
    }
}
