package dev.two.slender.map.schem;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.BlockPos;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.Test;

class SchematicExtrasTest {
    static byte[] gz(String json) throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        try (GZIPOutputStream g = new GZIPOutputStream(bo)) {
            g.write(json.getBytes(StandardCharsets.UTF_8));
        }
        return bo.toByteArray();
    }

    @Test
    void readsAllKinds() throws Exception {
        String json = "{\"signs\":[{\"pos\":[1,2,3],\"lines\":[\"\",\"Biblioteca\",\"\",\"\"],\"color\":\"black\",\"glow\":false}],"
                + "\"banners\":[{\"pos\":[4,5,6],\"patterns\":[[\"minecraft:stripe_top\",\"red\"]]}],"
                + "\"heads\":[{\"pos\":[7,8,9],\"texture\":\"abc\"}],"
                + "\"paintings\":[{\"pos\":[10,11,12],\"facing\":\"NORTH\",\"variant\":\"minecraft:kebab\"}]}";
        SchematicExtras e = SchematicExtras.read(new ByteArrayInputStream(gz(json)));
        assertEquals(new BlockPos(1, 2, 3), e.signs().get(0).pos());
        assertEquals(List.of("", "Biblioteca", "", ""), e.signs().get(0).lines());
        assertEquals("black", e.signs().get(0).color());
        assertArrayEquals(new String[]{"minecraft:stripe_top", "red"}, e.banners().get(0).patterns().get(0));
        assertEquals("abc", e.heads().get(0).texture());
        assertEquals(new BlockPos(10, 11, 12), e.paintings().get(0).pos());
        assertEquals("NORTH", e.paintings().get(0).facing());
        assertEquals("minecraft:kebab", e.paintings().get(0).variant());
    }

    @Test
    void resourceExists() {
        SchematicExtras e = SchematicExtras.resource("maps/hogwarts/extra.json.gz");
        assertTrue(e.signs().size() >= 698);
        assertTrue(e.banners().size() >= 1900);
        assertTrue(e.paintings().size() >= 185);
    }
}
