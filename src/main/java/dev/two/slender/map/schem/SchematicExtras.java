package dev.two.slender.map.schem;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.two.slender.map.BlockPos;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/** Lo que acompaña a un schematic y no son bloques: textos de carteles, estandartes, cabezas y cuadros.
 *  Coordenadas relativas al schematic. Lo genera tools/hogwarts (extras.py). */
public record SchematicExtras(List<SignText> signs, List<BannerData> banners, List<HeadData> heads,
                              List<PaintingData> paintings) {

    public record SignText(BlockPos pos, List<String> lines, String color, boolean glow) {
    }

    public record BannerData(BlockPos pos, List<String[]> patterns) {
    }

    public record HeadData(BlockPos pos, String texture) {
    }

    public record PaintingData(BlockPos pos, String facing, String variant) {
    }

    public static final SchematicExtras NONE = new SchematicExtras(List.of(), List.of(), List.of(), List.of());

    public static SchematicExtras read(InputStream gzip) throws IOException {
        try (Reader r = new InputStreamReader(new GZIPInputStream(gzip), StandardCharsets.UTF_8)) {
            JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
            List<SignText> signs = new ArrayList<>();
            for (JsonElement e : arr(o, "signs")) {
                JsonObject s = e.getAsJsonObject();
                List<String> lines = new ArrayList<>();
                for (JsonElement l : s.getAsJsonArray("lines")) {
                    lines.add(l.getAsString());
                }
                signs.add(new SignText(pos(s), lines, s.has("color") ? s.get("color").getAsString() : "black",
                        s.has("glow") && s.get("glow").getAsBoolean()));
            }
            List<BannerData> banners = new ArrayList<>();
            for (JsonElement e : arr(o, "banners")) {
                JsonObject b = e.getAsJsonObject();
                List<String[]> pats = new ArrayList<>();
                for (JsonElement p : b.getAsJsonArray("patterns")) {
                    JsonArray pa = p.getAsJsonArray();
                    pats.add(new String[]{pa.get(0).getAsString(), pa.get(1).getAsString()});
                }
                banners.add(new BannerData(pos(b), pats));
            }
            List<HeadData> heads = new ArrayList<>();
            for (JsonElement e : arr(o, "heads")) {
                JsonObject h = e.getAsJsonObject();
                heads.add(new HeadData(pos(h), h.get("texture").getAsString()));
            }
            List<PaintingData> paintings = new ArrayList<>();
            for (JsonElement e : arr(o, "paintings")) {
                JsonObject p = e.getAsJsonObject();
                paintings.add(new PaintingData(pos(p), p.get("facing").getAsString(), p.get("variant").getAsString()));
            }
            return new SchematicExtras(List.copyOf(signs), List.copyOf(banners), List.copyOf(heads), List.copyOf(paintings));
        }
    }

    public static SchematicExtras resource(String name) {
        try (InputStream in = SchematicExtras.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("No está en el jar: " + name);
            }
            return read(in);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + name, e);
        }
    }

    private static JsonArray arr(JsonObject o, String key) {
        return o.has(key) ? o.getAsJsonArray(key) : new JsonArray();
    }

    private static BlockPos pos(JsonObject o) {
        JsonArray p = o.getAsJsonArray("pos");
        return new BlockPos(p.get(0).getAsInt(), p.get(1).getAsInt(), p.get(2).getAsInt());
    }
}
