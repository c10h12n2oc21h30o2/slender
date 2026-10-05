package dev.two.slender.night;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** NightState ↔ mapa plano para yml (claves sin puntos, como MapStore). */
public final class NightCodec {
    private NightCodec() {
    }

    public static Map<String, Object> toMap(NightState s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.id);
        List<String> members = new ArrayList<>();
        s.members.forEach((id, name) -> members.add(id + ";" + name));
        m.put("miembros", members);
        m.put("fase", s.phase);
        m.put("velas", s.candles);
        m.put("mision-tipo", s.mission.type().name());
        m.put("mision-fases", new ArrayList<>(s.mission.phases()));
        m.put("mision-encontradas", new ArrayList<>(s.mission.foundIn()));
        m.put("mision-fallida", s.mission.failed());
        m.put("mision-premiada", s.missionRewarded);
        m.put("snitch-fase", s.snitchPhase);
        m.put("snitch-atrapada", s.snitchCaught);
        List<String> lost = new ArrayList<>();
        s.lost.forEach((id, phase) -> lost.add(id + ";" + phase));
        m.put("perdidos", lost);
        m.put("creada", s.created);
        m.put("actualizada", s.updated);
        return m;
    }

    public static NightState fromMap(Map<String, ?> m) {
        if (!(m.get("id") instanceof String id) || !(m.get("miembros") instanceof List<?> members)) {
            throw new IllegalArgumentException("Noche guardada sin id o sin miembros");
        }
        NightState s = new NightState();
        s.id = id;
        for (Object o : members) {
            String[] p = o.toString().split(";", 2);
            s.members.put(UUID.fromString(p[0]), p.length > 1 ? p[1] : "?");
        }
        s.phase = num(m, "fase").intValue();
        s.candles = num(m, "velas").intValue();
        Set<Integer> found = new LinkedHashSet<>(ints(m.get("mision-encontradas")));
        s.mission = MissionModel.restore(MissionType.valueOf(String.valueOf(m.get("mision-tipo"))), ints(m.get("mision-fases")),
                found, Boolean.TRUE.equals(m.get("mision-fallida")));
        s.missionRewarded = Boolean.TRUE.equals(m.get("mision-premiada"));
        s.snitchPhase = num(m, "snitch-fase").intValue();
        s.snitchCaught = Boolean.TRUE.equals(m.get("snitch-atrapada"));
        if (m.get("perdidos") instanceof List<?> lost) {
            for (Object o : lost) {
                String[] p = o.toString().split(";", 2);
                s.lost.put(UUID.fromString(p[0]), Integer.parseInt(p[1]));
            }
        }
        s.created = num(m, "creada").longValue();
        s.updated = num(m, "actualizada").longValue();
        return s;
    }

    private static Number num(Map<String, ?> m, String k) {
        if (m.get(k) instanceof Number n) {
            return n;
        }
        throw new IllegalArgumentException("Noche guardada sin " + k);
    }

    private static List<Integer> ints(Object o) {
        List<Integer> out = new ArrayList<>();
        if (o instanceof List<?> l) {
            for (Object x : l) {
                out.add(((Number) x).intValue());
            }
        }
        return out;
    }
}
