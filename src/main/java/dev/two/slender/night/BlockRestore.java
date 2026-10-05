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

    public void clear() {
        this.original.clear();
    }
}
