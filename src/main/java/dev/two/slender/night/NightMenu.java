package dev.two.slender.night;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Textos del menú de la 8.ª puerta y del tablón de la sala. Puro. */
public final class NightMenu {
    public static final String NEW = "noche-menu-nueva";
    public static final String CONTINUE = "noche-menu-continuar";
    public static final String JOIN = "noche-menu-unirse";
    /** Ancho de una línea de cartel. */
    public static final int SIGN_WIDTH = 15;

    private NightMenu() {
    }

    /**
     * Opciones del menú, en orden. Con una noche activa solo se puede unir (una sola noche a la vez; si es la suya,
     * unirse le devuelve a ella). Sin noche activa: nueva y, si tiene una guardada, continuar.
     */
    public static List<String> options(boolean activeExists, boolean isMember, Optional<NightState> saved) {
        if (activeExists) {
            return List.of(JOIN);
        }
        return saved.isPresent() ? List.of(NEW, CONTINUE) : List.of(NEW);
    }

    /** Huecos de un menú de 3 filas para n opciones, centrados en la fila del medio. */
    public static int[] slots(int n) {
        return switch (n) {
            case 1 -> new int[]{13};
            case 2 -> new int[]{11, 15};
            default -> new int[]{11, 13, 15};
        };
    }

    /** Parte un texto en líneas de como mucho {@code width}; una palabra más larga se corta con guion. */
    public static List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int cut = Math.max(1, width - 5);
        for (String w : text.trim().split("\\s+")) {
            if (w.isEmpty()) {
                continue;
            }
            while (w.length() > width) {
                if (!cur.isEmpty()) {
                    lines.add(cur.toString());
                    cur.setLength(0);
                }
                lines.add(w.substring(0, cut) + "-");
                w = w.substring(cut);
            }
            if (cur.isEmpty()) {
                cur.append(w);
            } else if (cur.length() + 1 + w.length() <= width) {
                cur.append(' ').append(w);
            } else {
                lines.add(cur.toString());
                cur.setLength(0);
                cur.append(w);
            }
        }
        if (!cur.isEmpty()) {
            lines.add(cur.toString());
        }
        return lines;
    }

    /**
     * Las 32 líneas del tablón (8 carteles × 4, en el orden de {@code Sala.boardSigns()}): fase y sección, velas,
     * misión, su progreso y, en la fila de abajo, la siguiente entrada del diario de Filch.
     */
    public static List<String> board(int phase, String sectionName, int candles, int candlesMax, String missionName,
                                     String missionState, String diary) {
        List<String> out = new ArrayList<>();
        sign(out, "FASE " + phase, wrap(sectionName, SIGN_WIDTH));
        sign(out, "VELAS", List.of(candles + " / " + candlesMax));
        sign(out, "MISIÓN", wrap(missionName, SIGN_WIDTH));
        sign(out, "PROGRESO", List.of(missionState));
        List<String> d = new ArrayList<>();
        d.add("DIARIO");
        d.addAll(wrap(diary, SIGN_WIDTH));
        for (int i = 0; i < 16; i++) {
            out.add(i < d.size() ? d.get(i) : "");
        }
        return out;
    }

    private static void sign(List<String> out, String head, List<String> body) {
        out.add(head);
        for (int i = 0; i < 3; i++) {
            out.add(i < body.size() ? body.get(i) : "");
        }
    }
}
