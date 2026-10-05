package dev.two.slender.night;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/** Progreso guardado de una noche (spec §4, §8, §9). Campos públicos y mutables, como SurvivorState. */
public final class NightState {
    public String id;
    public final LinkedHashMap<UUID, String> members = new LinkedHashMap<>();
    public int phase = 1;
    public int candles = 2;
    public MissionModel mission;
    public boolean missionRewarded;
    public int snitchPhase;
    public boolean snitchCaught;
    /** Perdidos: dueño → fase en la que se perdió (se saltan la siguiente). */
    public final Map<UUID, Integer> lost = new LinkedHashMap<>();
    public long created;
    public long updated;

    public static NightState create(UUID first, String name, Random r) {
        NightState s = new NightState();
        s.id = new UUID(r.nextLong(), r.nextLong()).toString().substring(0, 8);
        s.members.put(first, name);
        s.mission = MissionModel.create(MissionType.values()[r.nextInt(MissionType.values().length)], r);
        s.snitchPhase = 3 + r.nextInt(5);
        s.created = System.currentTimeMillis();
        s.updated = s.created;
        return s;
    }
}
