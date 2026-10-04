package dev.two.slender.player;

import java.util.Arrays;

public enum RestoreMode {
    FULL,
    STATE_ONLY;

    public static RestoreMode after(Object[] var0, Object[] var1) {
        return var0 != null && !Arrays.equals(var0, var1) ? STATE_ONLY : FULL;
    }
}
