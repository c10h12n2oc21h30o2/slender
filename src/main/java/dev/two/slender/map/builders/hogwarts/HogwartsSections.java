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
                          List<LightBlock> lightBlocks, Ambience ambience, int story, int[] floor, List<Integer> gateWet) {
        /** ¿Es (x, y, z) del schem suelo alcanzable? (o la celda de encima, por las losas). Sin datos: sí. */
        public boolean isFloor(int x, int y, int z, int width, int length) {
            if (this.floor.length == 0) {
                return true;
            }
            if (x < 0 || z < 0 || y < 1 || x >= width || z >= length) {
                return false;
            }
            int i = x + z * width + y * width * length;
            return java.util.Arrays.binarySearch(this.floor, i) >= 0
                    || java.util.Arrays.binarySearch(this.floor, i - width * length) >= 0;
        }
    }

    private static List<Section> cache;
    /** Ancho (x) y largo (z) del schematic, para los índices de 'floor'. */
    public static final int WIDTH = 383;
    public static final int LENGTH = 413;

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
                o.has("story") ? o.get("story").getAsInt() : o.get("order").getAsInt(),
                o.has("floor") ? decodeCells(o.get("floor").getAsString()) : new int[0], ints(o, "gateWet"));
    }

    /** Índices ordenados en diferencias varint y base64 (content.encode_cells de tools/hogwarts). */
    static int[] decodeCells(String text) {
        byte[] b = java.util.Base64.getDecoder().decode(text);
        int[] out = new int[b.length];
        int n = 0;
        int cur = 0;
        int acc = 0;
        int shift = 0;
        for (byte x : b) {
            acc |= (x & 0x7F) << shift;
            if ((x & 0x80) != 0) {
                shift += 7;
                continue;
            }
            cur += acc;
            out[n++] = cur;
            acc = 0;
            shift = 0;
        }
        return java.util.Arrays.copyOf(out, n);
    }

    private static List<Integer> ints(JsonObject o, String key) {
        List<Integer> out = new ArrayList<>();
        if (o.has(key)) {
            for (JsonElement e : o.getAsJsonArray(key)) {
                out.add(e.getAsInt());
            }
        }
        return List.copyOf(out);
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
