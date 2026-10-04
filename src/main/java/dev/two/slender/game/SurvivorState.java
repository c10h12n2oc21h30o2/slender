package dev.two.slender.game;

import dev.two.slender.config.SlenderConfig;
import dev.two.slender.logic.StaminaModel;
import dev.two.slender.logic.StaticModel;
import java.util.UUID;

public final class SurvivorState {
    public final UUID id;
    public final StaticModel stat;
    public final StaminaModel stamina;
    public int pages;
    public boolean alive = true;
    public boolean escaped;
    public long nextHeartbeatTick;

    public SurvivorState(UUID var1, SlenderConfig var2) {
        this.id = var1;
        this.stat = new StaticModel(var2.staticLookRate, var2.staticProximityRate, var2.staticProximityRadius, var2.staticDecayRate);
        this.stamina = new StaminaModel(var2.staminaDrain, var2.staminaRegen, var2.staminaRecoverAt);
    }

    public boolean active() {
        return this.alive && !this.escaped;
    }
}
