package dev.two.slender.config;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public final class SlenderConfig {
    public final String world;
    public final int buildMaxBlocksPerTick;
    public final int buildMaxMsPerTick;
    public final int pages;
    public final int countdownSeconds;
    public final int fullCountdownSeconds;
    public final int escapeSeconds;
    public final int endSeconds;
    public final int batteriesPerGame;
    private final int coopMin;
    private final int coopMax;
    private final int coopTime;
    private final int asimMin;
    private final int asimMax;
    private final int asimTime;
    public final double flashlightRange;
    public final double batteryDrainPerSecond;
    public final double batteryCharge;
    public final double staticLookRate;
    public final double staticLookHalfAngle;
    public final double staticLookMaxDistance;
    public final double staticProximityRate;
    public final double staticProximityRadius;
    public final double staticDecayRate;
    public final double staminaDrain;
    public final double staminaRegen;
    public final double staminaRecoverAt;
    public final String slenderBody;
    public final String skinPlayer;
    public final String skinValue;
    public final String skinSignature;
    public final double slenderScale;
    public final double observeHalfAngle;
    public final double observeMaxDistance;
    public final double catchDistance;
    public final int isolationMinPlayersExclusive;
    public final double isolationRadius;
    public final double isolationSeconds;
    public final double asimTeleportRange;
    public final double asimTeleportCooldown;
    public final double asimWhisperRange;
    public final double asimWhisperCooldown;
    public final double asimApparitionCooldown;
    public final double asimBlackoutCooldown;
    public final double asimBlackoutRadius;
    public final double asimGrabDistance;
    public final double asimGrabCooldown;
    public final double asimCooldownReductionPerPage;
    public final int asimBlackoutTicks;
    public final List<String> pagePhrases;
    public final int hubFog;
    public final boolean hubMist;

    public SlenderConfig(FileConfiguration var1) {
        this.world = var1.getString("world", "slender");
        this.buildMaxBlocksPerTick = var1.getInt("build.max-blocks-per-tick", 2000);
        this.buildMaxMsPerTick = var1.getInt("build.max-ms-per-tick", 15);
        this.pages = var1.getInt("game.pages", 8);
        this.countdownSeconds = var1.getInt("game.countdown-seconds", 20);
        this.fullCountdownSeconds = var1.getInt("game.full-countdown-seconds", 5);
        this.escapeSeconds = var1.getInt("game.escape-seconds", 60);
        this.endSeconds = var1.getInt("game.end-seconds", 10);
        this.batteriesPerGame = var1.getInt("game.batteries-per-game", 6);
        this.coopMin = var1.getInt("game.coop.min-players", 1);
        this.coopMax = var1.getInt("game.coop.max-players", 8);
        this.coopTime = var1.getInt("game.coop.time-limit-seconds", 900);
        this.asimMin = var1.getInt("game.asim.min-players", 3);
        this.asimMax = var1.getInt("game.asim.max-players", 9);
        this.asimTime = var1.getInt("game.asim.time-limit-seconds", 600);
        this.flashlightRange = var1.getDouble("flashlight.range", 14.0);
        this.batteryDrainPerSecond = var1.getDouble("flashlight.battery-drain-per-second", 0.6667);
        this.batteryCharge = var1.getDouble("flashlight.battery-charge", 35.0);
        this.staticLookRate = var1.getDouble("static.look-rate", 18.0);
        this.staticLookHalfAngle = var1.getDouble("static.look-half-angle", 30.0);
        this.staticLookMaxDistance = var1.getDouble("static.look-max-distance", 48.0);
        this.staticProximityRate = var1.getDouble("static.proximity-rate", 25.0);
        this.staticProximityRadius = var1.getDouble("static.proximity-radius", 6.0);
        this.staticDecayRate = var1.getDouble("static.decay-rate", 10.0);
        this.staminaDrain = var1.getDouble("stamina.drain-per-second", 12.5);
        this.staminaRegen = var1.getDouble("stamina.regen-per-second", 8.0);
        this.staminaRecoverAt = var1.getDouble("stamina.recover-at", 30.0);
        this.slenderBody = var1.getString("slender.body", "mannequin");
        this.slenderScale = var1.getDouble("slender.scale", 1.45);
        this.skinPlayer = var1.getString("slender.skin.player", "");
        this.skinValue = var1.getString("slender.skin.value", "");
        this.skinSignature = var1.getString("slender.skin.signature", "");
        this.observeHalfAngle = var1.getDouble("slender.observe-half-angle", 55.0);
        this.observeMaxDistance = var1.getDouble("slender.observe-max-distance", 64.0);
        this.catchDistance = var1.getDouble("slender.catch-distance", 1.5);
        this.isolationMinPlayersExclusive = var1.getInt("isolation.min-players-exclusive", 4);
        this.isolationRadius = var1.getDouble("isolation.radius", 25.0);
        this.isolationSeconds = var1.getDouble("isolation.seconds", 40.0);
        this.asimTeleportRange = var1.getDouble("asim.teleport-range", 40.0);
        this.asimTeleportCooldown = var1.getDouble("asim.teleport-cooldown", 10.0);
        this.asimWhisperRange = var1.getDouble("asim.whisper-range", 30.0);
        this.asimWhisperCooldown = var1.getDouble("asim.whisper-cooldown", 15.0);
        this.asimApparitionCooldown = var1.getDouble("asim.apparition-cooldown", 25.0);
        this.asimBlackoutCooldown = var1.getDouble("asim.blackout-cooldown", 40.0);
        this.asimBlackoutRadius = var1.getDouble("asim.blackout-radius", 20.0);
        this.asimBlackoutTicks = var1.getInt("asim.blackout-ticks", 80);
        this.asimGrabDistance = var1.getDouble("asim.grab-distance", 2.5);
        this.asimGrabCooldown = var1.getDouble("asim.grab-cooldown", 3.0);
        this.asimCooldownReductionPerPage = var1.getDouble("asim.cooldown-reduction-per-page", 0.05);
        this.hubFog = var1.getInt("vestibulo.niebla", 2);
        this.hubMist = var1.getBoolean("vestibulo.bruma", true);
        List var2 = var1.getStringList("page-phrases");
        this.pagePhrases = var2.isEmpty() ? List.of("SIEMPRE MIRA") : List.copyOf(var2);
    }

    public int minPlayers(String var1) {
        return "asim".equals(var1) ? this.asimMin : this.coopMin;
    }

    public int maxPlayers(String var1) {
        return "asim".equals(var1) ? this.asimMax : this.coopMax;
    }

    public int timeLimit(String var1) {
        return "asim".equals(var1) ? this.asimTime : this.coopTime;
    }
}
