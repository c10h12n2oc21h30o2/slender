package dev.two.slender.map.builders.hogwarts;

import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.schem.Schematic;
import dev.two.slender.map.schem.SchematicExtras;
import dev.two.slender.map.schem.SchematicReader;
import java.util.List;

/** Los 7 mapas de Hogwarts y los recursos que comparten (se leen una sola vez). */
public final class HogwartsMaps {
    public static final List<String> IDS = List.of("hw_mazmorras", "hw_comedor", "hw_ala_este", "hw_patio", "hw_aulas",
            "hw_torres", "hw_terrenos");
    private static Schematic schematic;
    private static SchematicExtras extras;

    private HogwartsMaps() {
    }

    public static List<MapBuilder> all() {
        return HogwartsSections.resource().stream().<MapBuilder>map(HogwartsBuilder::new).toList();
    }

    static synchronized Schematic schematic() {
        if (schematic == null) {
            schematic = SchematicReader.resource("maps/hogwarts.schem");
        }
        return schematic;
    }

    static synchronized SchematicExtras extras() {
        if (extras == null) {
            extras = SchematicExtras.resource("maps/hogwarts/extra.json.gz");
        }
        return extras;
    }
}
