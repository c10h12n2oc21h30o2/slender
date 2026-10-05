package dev.two.slender.night;

import dev.two.slender.map.BlockPos;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/** Elegir al azar una celda de suelo que cumpla una condición. Puro. */
public final class NightSpots {
    private NightSpots() {
    }

    public static Optional<BlockPos> pick(int count, IntFunction<BlockPos> cell, Predicate<BlockPos> ok, Random r, int tries) {
        if (count <= 0) {
            return Optional.empty();
        }
        for (int i = 0; i < tries; i++) {
            BlockPos c = cell.apply(r.nextInt(count));
            if (ok.test(c)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
