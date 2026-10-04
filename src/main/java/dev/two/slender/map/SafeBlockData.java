package dev.two.slender.map;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/** Estados de bloque que el servidor no conoce: en vez de reventar la construcción, se pone el de reserva y se avisa. */
public final class SafeBlockData {
    private SafeBlockData() {
    }

    public static <T> T resolve(String state, Function<String, T> parser, T fallback, Set<String> warned, Consumer<String> log) {
        try {
            return parser.apply(state);
        } catch (IllegalArgumentException e) {
            if (warned.add(state)) {
                log.accept("Bloque no válido en 26.3, uso piedra: " + state);
            }
            return fallback;
        }
    }
}
