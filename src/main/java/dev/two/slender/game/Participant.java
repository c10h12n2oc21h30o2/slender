// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import java.util.UUID;

public final class Participant
{
    public final UUID id;
    public final String name;
    public Role role;
    public SurvivorState state;
    
    public Participant(final UUID id, final String name) {
        this.role = Role.SURVIVOR;
        this.id = id;
        this.name = name;
    }
}
