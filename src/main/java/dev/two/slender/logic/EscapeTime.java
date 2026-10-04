package dev.two.slender.logic;

public final class EscapeTime {
    public static final double PACE = 2.7;

    private EscapeTime() {
    }

    public static int seconds(int var0, double var1) {
        return Math.max(var0, (int)Math.ceil(var1 / 2.7));
    }
}
