package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;

public final class ChunkArea {
    private ChunkArea() {
    }

    public static List<int[]> chunks(int var0, int var1, int var2, int var3) {
        ArrayList var4 = new ArrayList();

        for(int var5 = Math.min(var0, var2) >> 4; var5 <= Math.max(var0, var2) >> 4; ++var5) {
            for(int var6 = Math.min(var1, var3) >> 4; var6 <= Math.max(var1, var3) >> 4; ++var6) {
                var4.add(new int[]{var5, var6});
            }
        }

        return var4;
    }
}
