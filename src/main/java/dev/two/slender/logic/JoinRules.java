package dev.two.slender.logic;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;

public final class JoinRules {
    private JoinRules() {
    }

    public static Result check(String var0, String var1, boolean var2, int var3, int var4) {
        if (!var2) {
            return JoinRules.Result.RUNNING;
        } else if (var3 >= var4) {
            return JoinRules.Result.FULL;
        } else {
            return var0 != null && var1 != null && !var0.equals(var1) ? JoinRules.Result.OK_MODE_FORCED : JoinRules.Result.OK;
        }
    }

    public static String resolveMode(String var0, String var1) {
        if (var0 != null) {
            return var0;
        } else {
            return var1 == null ? "coop" : var1;
        }
    }

    public static String normalizeMode(String var0) {
        if (var0 == null) {
            return null;
        } else {
            String var1 = Normalizer.normalize(var0, Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
            if (var1.startsWith("coop")) {
                return "coop";
            } else {
                return !var1.startsWith("asim") && !var1.startsWith("asym") ? null : "asim";
            }
        }
    }

    public static enum Result {
        OK,
        OK_MODE_FORCED,
        RUNNING,
        FULL;
    }
}
