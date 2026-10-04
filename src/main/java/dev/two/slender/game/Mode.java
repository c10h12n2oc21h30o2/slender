package dev.two.slender.game;

public enum Mode {
    COOP("coop"),
    ASIM("asim");

    private final String id;

    private Mode(String var3) {
        this.id = var3;
    }

    public String id() {
        return this.id;
    }

    public static Mode of(String var0) {
        return "asim".equals(var0) ? ASIM : COOP;
    }
}
