// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;

public final class ChunkArea
{
    private ChunkArea() {
    }
    
    public static List<int[]> chunks(final int n, final int n2, final int n3, final int n4) {
        final ArrayList list = new ArrayList();
        for (int i = Math.min(n, n3) >> 4; i <= Math.max(n, n3) >> 4; ++i) {
            for (int j = Math.min(n2, n4) >> 4; j <= Math.max(n2, n4) >> 4; ++j) {
                list.add(new int[] { i, j });
            }
        }
        return list;
    }
}
