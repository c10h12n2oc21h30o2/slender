// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.config;

import java.util.Collection;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

public final class SlenderConfig
{
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
    
    public SlenderConfig(final FileConfiguration fileConfiguration) {
        this.world = fileConfiguration.getString("world", "slender");
        this.buildMaxBlocksPerTick = fileConfiguration.getInt("build.max-blocks-per-tick", 2000);
        this.buildMaxMsPerTick = fileConfiguration.getInt("build.max-ms-per-tick", 15);
        this.pages = fileConfiguration.getInt("game.pages", 8);
        this.countdownSeconds = fileConfiguration.getInt("game.countdown-seconds", 20);
        this.fullCountdownSeconds = fileConfiguration.getInt("game.full-countdown-seconds", 5);
        this.escapeSeconds = fileConfiguration.getInt("game.escape-seconds", 60);
        this.endSeconds = fileConfiguration.getInt("game.end-seconds", 10);
        this.batteriesPerGame = fileConfiguration.getInt("game.batteries-per-game", 6);
        this.coopMin = fileConfiguration.getInt("game.coop.min-players", 1);
        this.coopMax = fileConfiguration.getInt("game.coop.max-players", 8);
        this.coopTime = fileConfiguration.getInt("game.coop.time-limit-seconds", 900);
        this.asimMin = fileConfiguration.getInt("game.asim.min-players", 3);
        this.asimMax = fileConfiguration.getInt("game.asim.max-players", 9);
        this.asimTime = fileConfiguration.getInt("game.asim.time-limit-seconds", 600);
        this.flashlightRange = fileConfiguration.getDouble("flashlight.range", 14.0);
        this.batteryDrainPerSecond = fileConfiguration.getDouble("flashlight.battery-drain-per-second", 0.6667);
        this.batteryCharge = fileConfiguration.getDouble("flashlight.battery-charge", 35.0);
        this.staticLookRate = fileConfiguration.getDouble("static.look-rate", 18.0);
        this.staticLookHalfAngle = fileConfiguration.getDouble("static.look-half-angle", 30.0);
        this.staticLookMaxDistance = fileConfiguration.getDouble("static.look-max-distance", 48.0);
        this.staticProximityRate = fileConfiguration.getDouble("static.proximity-rate", 25.0);
        this.staticProximityRadius = fileConfiguration.getDouble("static.proximity-radius", 6.0);
        this.staticDecayRate = fileConfiguration.getDouble("static.decay-rate", 10.0);
        this.staminaDrain = fileConfiguration.getDouble("stamina.drain-per-second", 12.5);
        this.staminaRegen = fileConfiguration.getDouble("stamina.regen-per-second", 8.0);
        this.staminaRecoverAt = fileConfiguration.getDouble("stamina.recover-at", 30.0);
        this.slenderBody = fileConfiguration.getString("slender.body", "mannequin");
        this.slenderScale = fileConfiguration.getDouble("slender.scale", 1.45);
        this.skinPlayer = fileConfiguration.getString("slender.skin.player", "");
        this.skinValue = fileConfiguration.getString("slender.skin.value", "");
        this.skinSignature = fileConfiguration.getString("slender.skin.signature", "");
        this.observeHalfAngle = fileConfiguration.getDouble("slender.observe-half-angle", 55.0);
        this.observeMaxDistance = fileConfiguration.getDouble("slender.observe-max-distance", 64.0);
        this.catchDistance = fileConfiguration.getDouble("slender.catch-distance", 1.5);
        this.isolationMinPlayersExclusive = fileConfiguration.getInt("isolation.min-players-exclusive", 4);
        this.isolationRadius = fileConfiguration.getDouble("isolation.radius", 25.0);
        this.isolationSeconds = fileConfiguration.getDouble("isolation.seconds", 40.0);
        this.asimTeleportRange = fileConfiguration.getDouble("asim.teleport-range", 40.0);
        this.asimTeleportCooldown = fileConfiguration.getDouble("asim.teleport-cooldown", 10.0);
        this.asimWhisperRange = fileConfiguration.getDouble("asim.whisper-range", 30.0);
        this.asimWhisperCooldown = fileConfiguration.getDouble("asim.whisper-cooldown", 15.0);
        this.asimApparitionCooldown = fileConfiguration.getDouble("asim.apparition-cooldown", 25.0);
        this.asimBlackoutCooldown = fileConfiguration.getDouble("asim.blackout-cooldown", 40.0);
        this.asimBlackoutRadius = fileConfiguration.getDouble("asim.blackout-radius", 20.0);
        this.asimBlackoutTicks = fileConfiguration.getInt("asim.blackout-ticks", 80);
        this.asimGrabDistance = fileConfiguration.getDouble("asim.grab-distance", 2.5);
        this.asimGrabCooldown = fileConfiguration.getDouble("asim.grab-cooldown", 3.0);
        this.asimCooldownReductionPerPage = fileConfiguration.getDouble("asim.cooldown-reduction-per-page", 0.05);
        this.hubFog = fileConfiguration.getInt("vestibulo.niebla", 2);
        this.hubMist = fileConfiguration.getBoolean("vestibulo.bruma", true);
        final List stringList = fileConfiguration.getStringList("page-phrases");
        this.pagePhrases = (List<String>)(stringList.isEmpty() ? List.of("SIEMPRE MIRA") : List.copyOf((Collection<?>)stringList));
    }
    
    public int minPlayers(final String anObject) {
        return "asim".equals(anObject) ? this.asimMin : this.coopMin;
    }
    
    public int maxPlayers(final String anObject) {
        return "asim".equals(anObject) ? this.asimMax : this.coopMax;
    }
    
    public int timeLimit(final String anObject) {
        return "asim".equals(anObject) ? this.asimTime : this.coopTime;
    }
}
