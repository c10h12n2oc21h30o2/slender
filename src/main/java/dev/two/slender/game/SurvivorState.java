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
        this(var1, var2, 1.0);
    }

    /** staticFactor multiplica lo rápido que sube la estática (LA NOCHE ENTERA la sube por fase). */
    public SurvivorState(UUID var1, SlenderConfig var2, double staticFactor) {
        this.id = var1;
        this.stat = new StaticModel(var2.staticLookRate * staticFactor, var2.staticProximityRate * staticFactor, var2.staticProximityRadius, var2.staticDecayRate);
        this.stamina = new StaminaModel(var2.staminaDrain, var2.staminaRegen, var2.staminaRecoverAt);
    }

    public boolean active() {
        return this.alive && !this.escaped;
    }

    /** Pasa a espectador 30 ticks después de morir salvo que lo hayan revivido entretanto (caldero). */
    public static boolean becomesSpectator(SurvivorState st) {
        return st == null || !st.alive;
    }
}
