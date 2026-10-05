package dev.two.slender.night;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/** Progreso de la misión de la noche. Sin Bukkit: qué fases tienen objeto, qué se ha encontrado, cumplida o fallida. */
public final class MissionModel {
    private final MissionType type;
    private final List<Integer> phases;
    private final Set<Integer> foundIn = new TreeSet<>();
    private boolean failed;

    private MissionModel(MissionType type, List<Integer> phases) {
        this.type = type;
        this.phases = List.copyOf(phases);
    }

    public static MissionModel create(MissionType t, Random r) {
        List<Integer> phases = switch (t) {
            case NORRIS -> List.of(2 + r.nextInt(5));
            case CROMOS -> List.of(1, 2, 3, 4, 5, 6, 7);
            case BRASEROS -> {
                List<Integer> all = new ArrayList<>(List.of(2, 3, 4, 5, 6, 7));
                Collections.shuffle(all, r);
                List<Integer> four = new ArrayList<>(all.subList(0, 4));
                Collections.sort(four);
                yield four;
            }
        };
        return new MissionModel(t, phases);
    }

    public static MissionModel restore(MissionType t, List<Integer> phases, Set<Integer> foundIn, boolean failed) {
        MissionModel m = new MissionModel(t, phases);
        m.foundIn.addAll(foundIn);
        m.failed = failed;
        return m;
    }

    public MissionType type() {
        return this.type;
    }

    public List<Integer> phases() {
        return this.phases;
    }

    public Set<Integer> foundIn() {
        return new LinkedHashSet<>(this.foundIn);
    }

    public int goal() {
        return switch (this.type) {
            case NORRIS -> 1;
            case CROMOS -> 5;
            case BRASEROS -> 4;
        };
    }

    public int progress() {
        return this.foundIn.size();
    }

    public boolean done() {
        return !this.failed && this.progress() >= this.goal();
    }

    public boolean failed() {
        return this.failed;
    }

    /** ¿Hay objeto de la misión en esta fase? */
    public boolean appearsIn(int phase) {
        return !this.failed && !this.done() && this.phases.contains(phase) && !this.foundIn.contains(phase);
    }

    public void found(int phase) {
        if (!this.failed && this.phases.contains(phase)) {
            this.foundIn.add(phase);
        }
    }

    public void phaseEnded(int phase) {
        if (this.failed || this.done()) {
            return;
        }
        switch (this.type) {
            case NORRIS, BRASEROS -> {
                if (this.phases.contains(phase) && !this.foundIn.contains(phase)) {
                    this.failed = true;
                }
            }
            case CROMOS -> {
                long remaining = this.phases.stream().filter(p -> p > phase).count();
                if (this.progress() + remaining < this.goal()) {
                    this.failed = true;
                }
            }
        }
    }
}
