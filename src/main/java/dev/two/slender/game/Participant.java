package dev.two.slender.game;

import java.util.UUID;

public final class Participant {
    public final UUID id;
    public final String name;
    public Role role;
    public SurvivorState state;

    public Participant(UUID var1, String var2) {
        this.role = Role.SURVIVOR;
        this.id = var1;
        this.name = var2;
    }
}
