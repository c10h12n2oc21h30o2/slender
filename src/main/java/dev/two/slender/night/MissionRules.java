package dev.two.slender.night;

import java.util.UUID;

/** Encontrar el objeto de la misión en una fase (spec §8). Puro. */
public final class MissionRules {
    public enum Found { IGNORED, FOUND, REWARDED }

    private MissionRules() {
    }

    /** Al acabar una fase ganada: true si la misión acaba de quedar imposible (para avisar). */
    public static boolean endPhase(NightState s) {
        boolean was = s.mission.failed();
        s.mission.phaseEnded(s.phase);
        return !was && s.mission.failed();
    }

    /** Solo cuenta un vivo de la fase; al cumplirse la misión da una vela (sin pasar del máximo), una sola vez. */
    public static Found find(NightState s, HeadLedger heads, UUID who, int candlesMax) {
        if (!heads.alive(who)) {
            return Found.IGNORED;
        }
        s.mission.found(s.phase);
        if (s.mission.done() && !s.missionRewarded) {
            s.missionRewarded = true;
            Candles c = new Candles(s.candles, candlesMax);
            c.gain();
            s.candles = c.count();
            return Found.REWARDED;
        }
        return Found.FOUND;
    }
}
