// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.StaminaModel;
import dev.two.slender.logic.StaticModel;
import java.util.UUID;

public final class SurvivorState
{
    public final UUID id;
    public final StaticModel stat;
    public final StaminaModel stamina;
    public int pages;
    public boolean alive;
    public boolean escaped;
    public long nextHeartbeatTick;
    
    public SurvivorState(final UUID id, final SlenderConfig slenderConfig) {
        this.alive = true;
        this.id = id;
        this.stat = new StaticModel(slenderConfig.staticLookRate, slenderConfig.staticProximityRate, slenderConfig.staticProximityRadius, slenderConfig.staticDecayRate);
        this.stamina = new StaminaModel(slenderConfig.staminaDrain, slenderConfig.staminaRegen, slenderConfig.staminaRecoverAt);
    }
    
    public boolean active() {
        return this.alive && !this.escaped;
    }
}
