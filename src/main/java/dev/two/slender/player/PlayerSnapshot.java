package dev.two.slender.player;

import java.util.List;

public record PlayerSnapshot(String itemsBase64, int level, float exp, double health, int food, float saturation, String gameMode, float walkSpeed, float flySpeed, boolean allowFlight, List<String> effects, String location, int viewDistance, int sendViewDistance) {
    public PlayerSnapshot(String itemsBase64, int level, float exp, double health, int food, float saturation, String gameMode, float walkSpeed, float flySpeed, boolean allowFlight, List<String> effects, String location, int viewDistance, int sendViewDistance) {
        effects = List.copyOf(effects);
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
        this.effects = effects;
        this.location = location;
        this.viewDistance = viewDistance;
        this.sendViewDistance = sendViewDistance;
    }

    public String itemsBase64() {
        return this.itemsBase64;
    }

    public int level() {
        return this.level;
    }

    public float exp() {
        return this.exp;
    }

    public double health() {
        return this.health;
    }

    public int food() {
        return this.food;
    }

    public float saturation() {
        return this.saturation;
    }

    public String gameMode() {
        return this.gameMode;
    }

    public float walkSpeed() {
        return this.walkSpeed;
    }

    public float flySpeed() {
        return this.flySpeed;
    }

    public boolean allowFlight() {
        return this.allowFlight;
    }

    public List<String> effects() {
        return this.effects;
    }

    public String location() {
        return this.location;
    }

    public int viewDistance() {
        return this.viewDistance;
    }

    public int sendViewDistance() {
        return this.sendViewDistance;
    }
}
