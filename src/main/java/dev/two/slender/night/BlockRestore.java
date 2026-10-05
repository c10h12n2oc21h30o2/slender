package dev.two.slender.night;

import dev.two.slender.map.BlockPos;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lo que había en cada bloque que pone una fase, para dejarlo igual al acabar. Puro. */
public final class BlockRestore {
    private final LinkedHashMap<BlockPos, String> original = new LinkedHashMap<>();

    /** Antes de poner algo en {@code p}: guarda lo que había (si ya se guardó, se queda lo primero). */
    public void record(BlockPos p, String blockData) {
        this.original.putIfAbsent(p, blockData);
    }

    /** Qué poner en cada bloque al acabar, al revés de como se pusieron. */
    public List<Map.Entry<BlockPos, String>> restoreOrder() {
        List<Map.Entry<BlockPos, String>> out = new ArrayList<>();
        this.original.forEach((p, d) -> out.add(Map.entry(p, d)));
        Collections.reverse(out);
        return out;
    }

    /** Una línea por bloque, "x y z|estado", en el orden en que se pusieron. */
    public List<String> serialize() {
        List<String> out = new ArrayList<>();
        this.original.forEach((p, d) -> out.add(p.x() + " " + p.y() + " " + p.z() + "|" + d));
        return out;
    }

    /** Lo contrario de serialize(); las líneas que no se entienden se saltan. */
    public static BlockRestore parse(List<String> lines) {
        BlockRestore r = new BlockRestore();
        for (String l : lines) {
            int bar = l.indexOf('|');
            if (bar < 0) {
                continue;
            }
            String[] xyz = l.substring(0, bar).trim().split(" ");
            try {
                r.record(new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])),
                        l.substring(bar + 1));
            } catch (RuntimeException e) {
                // línea rota: se ignora
            }
        }
        return r;
    }

    public boolean isEmpty() {
        return this.original.isEmpty();
    }

    public void clear() {
        this.original.clear();
    }
}
