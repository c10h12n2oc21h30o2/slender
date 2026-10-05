package dev.two.slender.night;

/** Velas de la noche: reintentos de fase (spec §8). */
public final class Candles {
    private final int max;
    private int count;

    public Candles(int count, int max) {
        this.count = count;
        this.max = max;
    }

    public int count() {
        return this.count;
    }

    /** Han caído todos: true = se gasta una y se repite la fase; false = no quedan, se acaba la noche. */
    public boolean onWipe() {
        if (this.count <= 0) {
            return false;
        }
        this.count--;
        return true;
    }

    public boolean spendForLost() {
        return this.onWipe();
    }

    /** +1 vela (misión o Snitch) sin pasar del máximo. */
    public boolean gain() {
        if (this.count >= this.max) {
            return false;
        }
        this.count++;
        return true;
    }
}
