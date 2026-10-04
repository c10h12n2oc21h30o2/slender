// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.ArrayList;
import java.util.List;

public final class MapValidator
{
    private MapValidator() {
    }
    
    public static List<Problem> problems(final ArenaMap arenaMap, final int i) {
        final ArrayList list = new ArrayList();
        if (arenaMap.spawns().isEmpty()) {
            list.add(new Problem("check-no-spawns", "0", "1"));
        }
        if (arenaMap.pageSpots().size() < i) {
            list.add(new Problem("check-few-pages", String.valueOf(arenaMap.pageSpots().size()), String.valueOf(i)));
        }
        if (!arenaMap.hasExit()) {
            list.add(new Problem("check-no-exit", "", ""));
        }
        final long count = arenaMap.spawns().stream().filter(loc -> !arenaMap2.inBounds(loc.x(), loc.z()) || loc.y() < arenaMap2.fallY()).count();
        if (count > 0L) {
            list.add(new Problem("check-spawn-outside", String.valueOf(count), ""));
        }
        final long count2 = arenaMap.pageSpots().stream().filter(pageSpot -> !arenaMap3.inBounds(pageSpot.pos().x() + 0.5, pageSpot.pos().z() + 0.5) || pageSpot.pos().y() < arenaMap3.fallY()).count();
        if (count2 > 0L) {
            list.add(new Problem("check-page-outside", String.valueOf(count2), ""));
        }
        if (arenaMap.hasExit() && !touchesBounds(arenaMap)) {
            list.add(new Problem("check-exit-outside", "", ""));
        }
        return list;
    }
    
    private static boolean touchesBounds(final ArenaMap arenaMap) {
        return arenaMap.exitMax().x() >= arenaMap.min().x() - 1 && arenaMap.exitMin().x() <= arenaMap.max().x() + 1 && arenaMap.exitMax().z() >= arenaMap.min().z() - 1 && arenaMap.exitMin().z() <= arenaMap.max().z() + 1;
    }
    
    public static boolean playable(final ArenaMap arenaMap, final int n) {
        return problems(arenaMap, n).isEmpty();
    }
    
    record Problem(String key, String count, String need) {}
}
