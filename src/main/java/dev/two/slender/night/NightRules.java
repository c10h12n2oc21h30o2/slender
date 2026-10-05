package dev.two.slender.night;

import java.util.UUID;

/** Qué pasa con la noche después de una fase (spec §4.5, §7, §8). Puro: muta el estado y dice qué toca. */
public final class NightRules {
    public enum Next { NEXT_PHASE, REPEAT, NIGHT_WON, NIGHT_LOST, ANNULLED }

    private NightRules() {
    }

    public static Next afterPhase(NightState s, PhaseOutcome o, int candlesMax) {
        if (o.aborted()) {
            return Next.ANNULLED;
        }
        s.updated = System.currentTimeMillis();
        if (o.won()) {
            s.lost.clear();
            for (UUID id : o.lostNow()) {
                s.lost.put(id, s.phase);
            }
            if (s.phase >= 7) {
                return Next.NIGHT_WON;
            }
            s.phase++;
            return Next.NEXT_PHASE;
        }
        Candles c = new Candles(s.candles, candlesMax);
        if (c.onWipe()) {
            s.candles = c.count();
            s.lost.clear();
            return Next.REPEAT;
        }
        return Next.NIGHT_LOST;
    }

    /** Unirse: quien ya es miembro siempre; uno nuevo solo si caben (spec: de 1 a 8 jugadores). */
    public static boolean canJoin(NightState s, UUID who, int max) {
        return s.members.containsKey(who) || s.members.size() < max;
    }

    /** En la sala: gastar una vela para que un perdido vuelva ya. */
    public static boolean spendForLost(NightState s, UUID who) {
        if (!s.lost.containsKey(who) || s.candles <= 0) {
            return false;
        }
        s.candles--;
        s.lost.remove(who);
        return true;
    }
}
