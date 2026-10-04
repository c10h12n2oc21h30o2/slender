package dev.two.slender.map;

import java.util.ArrayList;
import java.util.List;

public final class MapValidator {
    private MapValidator() {
    }

    public static List<Problem> problems(ArenaMap var0, int var1) {
        ArrayList var2 = new ArrayList();
        if (var0.spawns().isEmpty()) {
            var2.add(new Problem("check-no-spawns", "0", "1"));
        }

        if (var0.pageSpots().size() < var1) {
            var2.add(new Problem("check-few-pages", String.valueOf(var0.pageSpots().size()), String.valueOf(var1)));
        }

        if (!var0.hasExit()) {
            var2.add(new Problem("check-no-exit", "", ""));
        }

        long var3 = var0.spawns().stream().filter((var1x) -> {
            return !var0.inBounds(var1x.x(), var1x.z()) || var1x.y() < (double)var0.fallY();
        }).count();
        if (var3 > 0L) {
            var2.add(new Problem("check-spawn-outside", String.valueOf(var3), ""));
        }

        long var5 = var0.pageSpots().stream().filter((var1x) -> {
            return !var0.inBounds((double)var1x.pos().x() + 0.5, (double)var1x.pos().z() + 0.5) || var1x.pos().y() < var0.fallY();
        }).count();
        if (var5 > 0L) {
            var2.add(new Problem("check-page-outside", String.valueOf(var5), ""));
        }

        if (var0.hasExit() && !touchesBounds(var0)) {
            var2.add(new Problem("check-exit-outside", "", ""));
        }

        return var2;
    }

    private static boolean touchesBounds(ArenaMap var0) {
        return var0.exitMax().x() >= var0.min().x() - 1 && var0.exitMin().x() <= var0.max().x() + 1 && var0.exitMax().z() >= var0.min().z() - 1 && var0.exitMin().z() <= var0.max().z() + 1;
    }

    public static boolean playable(ArenaMap var0, int var1) {
        return problems(var0, var1).isEmpty();
    }

    public static record Problem(String key, String count, String need) {
        public Problem(String key, String count, String need) {
            this.key = key;
            this.count = count;
            this.need = need;
        }

        public String key() {
            return this.key;
        }

        public String count() {
            return this.count;
        }

        public String need() {
            return this.need;
        }
    }
}
