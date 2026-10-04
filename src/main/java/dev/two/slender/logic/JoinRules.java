// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.Locale;
import java.text.Normalizer;

public final class JoinRules
{
    private JoinRules() {
    }
    
    public static Result check(final String s, final String anObject, final boolean b, final int n, final int n2) {
        if (!b) {
            return Result.RUNNING;
        }
        if (n >= n2) {
            return Result.FULL;
        }
        if (s != null && anObject != null && !s.equals(anObject)) {
            return Result.OK_MODE_FORCED;
        }
        return Result.OK;
    }
    
    public static String resolveMode(final String s, final String s2) {
        if (s != null) {
            return s;
        }
        return (s2 == null) ? "coop" : s2;
    }
    
    public static String normalizeMode(final String src) {
        if (src == null) {
            return null;
        }
        final String lowerCase = Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        if (lowerCase.startsWith("coop")) {
            return "coop";
        }
        if (lowerCase.startsWith("asim") || lowerCase.startsWith("asym")) {
            return "asim";
        }
        return null;
    }
    
    public enum Result
    {
        OK, 
        OK_MODE_FORCED, 
        RUNNING, 
        FULL;
    }
}
