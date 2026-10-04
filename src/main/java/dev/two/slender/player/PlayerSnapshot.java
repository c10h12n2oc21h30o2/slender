// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.player;

import java.util.Collection;
import java.util.List;

record PlayerSnapshot(String itemsBase64, int level, float exp, double health, int food, float saturation, String gameMode, float walkSpeed, float flySpeed, boolean allowFlight, List<String> effects, String location, int viewDistance, int sendViewDistance) {
    public PlayerSnapshot(final String itemsBase64, final int level, final float exp, final double health, final int food, final float saturation, final String gameMode, final float walkSpeed, final float flySpeed, final boolean allowFlight, final List<String> effects, final String location, final int viewDistance, final int sendViewDistance) {
        final List<Object> copy = (List<Object>)List.copyOf((Collection<? extends String>)effects);
        this.itemsBase64 = itemsBase64;
        this.level = level;
        this.exp = exp;
        this.health = health;
        this.food = food;
        this.saturation = saturation;
        this.gameMode = gameMode;
        this.walkSpeed = walkSpeed;
        this.flySpeed = flySpeed;
        this.allowFlight = allowFlight;
        this.effects = (List<String>)copy;
        this.location = location;
        this.viewDistance = viewDistance;
        this.sendViewDistance = sendViewDistance;
    }
}
