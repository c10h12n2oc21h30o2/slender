package dev.two.slender.map.builders.hogwarts;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapLight;
import dev.two.slender.map.PageSpot;
import dev.two.slender.map.SoundZone;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Las 7 secciones de Hogwarts (maps/hogwarts/secciones.json, lo genera tools/hogwarts). Coordenadas del schematic. */
public final class HogwartsSections {
    public static final String RESOURCE = "maps/hogwarts/secciones.json";

    private HogwartsSections() {
    }

    public record Seal(BlockPos pos, String state) {
    }

    public record LightBlock(BlockPos pos, String state) {
    }

    public record BoardPlace(String name, int x, int z) {
    }

    public record Board(BlockPos pos, List<String> title, List<BoardPlace> places) {
    }

    public record NoteData(BlockPos pos, String facing, List<String> lines, String support) {
    }

    /** pages: pos es el bloque de PARED; el PageSpot real lo da Builder.pageOnWall. */
    public record Section(String id, String name, int order, String icon, int[] bounds, int fallY, List<Loc> spawns,
                          List<PageSpot> pages, List<BlockPos> batteries, List<BlockPos> gate, String gateBlock,
                          BlockPos exitMin, BlockPos exitMax, List<Seal> seal, Board board, List<NoteData> notes,
                          List<LightBlock> lightBlocks, Ambience ambience, int story) {
    }

    private static List<Section> cache;

    public static synchronized List<Section> resource() {
        if (cache == null) {
            try (InputStream in = HogwartsSections.class.getClassLoader().getResourceAsStream(RESOURCE)) {
                if (in == null) {
                    throw new IllegalStateException("No está en el jar: " + RESOURCE);
                }
                cache = read(in);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return cache;
    }

    public static List<Section> read(InputStream in) throws IOException {
        try (Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            List<Section> out = new ArrayList<>();
            for (JsonElement e : root.getAsJsonArray("sections")) {
                out.add(section(e.getAsJsonObject()));
            }
            return List.copyOf(out);
        }
    }

    private static Section section(JsonObject o) {
        JsonArray b = o.getAsJsonArray("bounds");
        int[] bounds = {b.get(0).getAsInt(), b.get(1).getAsInt(), b.get(2).getAsInt(), b.get(3).getAsInt()};
        List<Loc> spawns = strings(o, "spawns").stream().map(Loc::parse).toList();
        List<PageSpot> pages = strings(o, "pages").stream().map(PageSpot::parse).toList();
        List<BlockPos> batteries = strings(o, "batteries").stream().map(BlockPos::parse).toList();
        List<BlockPos> gate = strings(o, "gate").stream().map(BlockPos::parse).toList();
        JsonArray ex = o.getAsJsonArray("exit");
        List<Seal> seal = new ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("seal")) {
            JsonArray a = e.getAsJsonArray();
            seal.add(new Seal(new BlockPos(a.get(0).getAsInt(), a.get(1).getAsInt(), a.get(2).getAsInt()), a.get(3).getAsString()));
        }
        Board board = null;
        if (o.has("board") && o.get("board").isJsonObject()) {
            JsonObject bo = o.getAsJsonObject("board");
            List<BoardPlace> places = new ArrayList<>();
            for (JsonElement e : bo.getAsJsonArray("places")) {
                JsonArray a = e.getAsJsonArray();
                places.add(new BoardPlace(a.get(0).getAsString(), a.get(1).getAsInt(), a.get(2).getAsInt()));
            }
            board = new Board(BlockPos.parse(bo.get("pos").getAsString()), strings(bo, "title"), List.copyOf(places));
        }
        List<NoteData> notes = new ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("notes")) {
            JsonObject n = e.getAsJsonObject();
            notes.add(new NoteData(BlockPos.parse(n.get("pos").getAsString()), n.get("facing").getAsString(), strings(n, "lines"),
                    n.get("support").getAsString()));
        }
        List<LightBlock> lightBlocks = new ArrayList<>();
        for (JsonElement e : o.getAsJsonArray("lightBlocks")) {
            JsonArray a = e.getAsJsonArray();
            lightBlocks.add(new LightBlock(BlockPos.parse(a.get(0).getAsString()), a.get(1).getAsString()));
        }
        Ambience amb = new Ambience(strings(o, "sounds").stream().map(SoundZone::parse).toList(),
                strings(o, "lights").stream().map(MapLight::parse).toList(), strings(o, "phrases"));
        return new Section(o.get("id").getAsString(), o.get("name").getAsString(), o.get("order").getAsInt(),
                o.get("icon").getAsString(), bounds, o.get("fallY").getAsInt(), spawns, pages, batteries, gate,
                o.get("gateBlock").getAsString(), BlockPos.parse(ex.get(0).getAsString()), BlockPos.parse(ex.get(1).getAsString()),
                List.copyOf(seal), board, List.copyOf(notes), List.copyOf(lightBlocks), amb,
                o.has("story") ? o.get("story").getAsInt() : o.get("order").getAsInt());
    }

    private static List<String> strings(JsonObject o, String key) {
        List<String> out = new ArrayList<>();
        if (o.has(key)) {
            for (JsonElement e : o.getAsJsonArray(key)) {
                out.add(e.getAsString());
            }
        }
        return List.copyOf(out);
    }
}
