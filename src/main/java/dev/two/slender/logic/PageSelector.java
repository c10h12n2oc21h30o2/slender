package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class PageSelector {
    private PageSelector() {
    }

    public static <T> List<T> choose(List<T> var0, int var1, Random var2) {
        ArrayList var3 = new ArrayList(var0);
        Collections.shuffle(var3, var2);
        return List.copyOf(var3.subList(0, Math.min(var1, var3.size())));
    }
}
