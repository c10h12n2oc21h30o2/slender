package dev.two.slender.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/** Frases de página como un mazo: salen todas antes de repetir, y al barajar otra vez no repite la última. */
public final class PhraseDeck {
    private final List<String> source;
    private final Random random;
    private final Deque<String> bag = new ArrayDeque<>();
    private String last;

    public PhraseDeck(List<String> source, Random random) {
        this.source = List.copyOf(source);
        this.random = random;
    }

    public String next() {
        if (this.bag.isEmpty()) {
            List<String> shuffled = new ArrayList<>(this.source);
            Collections.shuffle(shuffled, this.random);
            if (shuffled.size() > 1 && shuffled.get(0).equals(this.last)) {
                Collections.swap(shuffled, 0, shuffled.size() - 1);
            }
            this.bag.addAll(shuffled);
        }
        this.last = this.bag.poll();
        return this.last;
    }
}
