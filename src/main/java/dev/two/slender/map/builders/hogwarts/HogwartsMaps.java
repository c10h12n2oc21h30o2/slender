package dev.two.slender.map.builders.hogwarts;

import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.SoftCache;
import dev.two.slender.map.SpotRules;
import dev.two.slender.map.schem.Schematic;
import dev.two.slender.map.schem.SchematicExtras;
import dev.two.slender.map.schem.SchematicReader;
import java.util.List;

/** Los 7 mapas de Hogwarts y los recursos que comparten (se leen una sola vez). */
public final class HogwartsMaps {
    public static final List<String> IDS = List.of("hw_mazmorras", "hw_comedor", "hw_ala_este", "hw_patio", "hw_aulas",
            "hw_torres", "hw_terrenos");
    /** ~160 MB una vez leído: se guarda mientras haya memoria (las 7 secciones se construyen seguidas) y luego se suelta. */
    private static final SoftCache<Schematic> SCHEMATIC = new SoftCache<>(() -> SchematicReader.resource("maps/hogwarts.schem"));
    private static final SoftCache<SchematicExtras> EXTRAS = new SoftCache<>(() -> SchematicExtras.resource("maps/hogwarts/extra.json.gz"));

    private HogwartsMaps() {
    }

    /** all() sin tumbar el plugin: si el recurso está roto, se avisa y se sigue sin Hogwarts. */
    public static List<MapBuilder> safely(java.util.function.Supplier<List<MapBuilder>> all, java.util.logging.Logger log) {
        try {
            return all.get();
        } catch (RuntimeException e) {
            log.severe("Hogwarts no se carga (" + e.getMessage() + "); el resto de mapas sigue.");
            return List.of();
        }
    }

    /** Los 7 mapas; registra también dónde puede aparecer Slender en cada uno. */
    public static List<MapBuilder> all() {
        List<MapBuilder> out = new java.util.ArrayList<>();
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            HogwartsBuilder b = new HogwartsBuilder(s);
            SpotRules.register(s.id(), b::slenderCanStand);
            out.add(b);
        }
        return List.copyOf(out);
    }

    static Schematic schematic() {
        return SCHEMATIC.get();
    }

    static SchematicExtras extras() {
        return EXTRAS.get();
    }
}
