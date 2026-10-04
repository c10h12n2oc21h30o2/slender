// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

public final class Signposts
{
    private Signposts() {
    }
    
    public static String arrow(final String s, final int n, final int n2) {
        int n3 = -1;
        switch (s.hashCode()) {
            case 74469605: {
                if (s.equals("NORTH")) {
                    n3 = 0;
                    break;
                }
                break;
            }
            case 79090093: {
                if (s.equals("SOUTH")) {
                    n3 = 1;
                    break;
                }
                break;
            }
            case 2120701: {
                if (s.equals("EAST")) {
                    n3 = 2;
                    break;
                }
                break;
            }
            case 2660783: {
                if (s.equals("WEST")) {
                    n3 = 3;
                    break;
                }
                break;
            }
        }
        int n4 = 0;
        int n5 = 0;
        switch (n3) {
            case 0: {
                n4 = 0;
                n5 = 1;
                break;
            }
            case 1: {
                n4 = 0;
                n5 = -1;
                break;
            }
            case 2: {
                n4 = -1;
                n5 = 0;
                break;
            }
            case 3: {
                n4 = 1;
                n5 = 0;
                break;
            }
            default: {
                throw new IllegalArgumentException("Cara no v\u00e1lida: " + s);
            }
        }
        final double degrees = Math.toDegrees(Math.atan2(n * -n5 + n2 * n4, n * n4 + n2 * n5));
        if (Math.abs(degrees) <= 45.0) {
            return "^";
        }
        if (Math.abs(degrees) >= 135.0) {
            return "v";
        }
        return (degrees > 0.0) ? ">" : "<";
    }
    
    public static String line(final String s, final String s2, final int n, final int n2) {
        final String arrow = arrow(s, n, n2);
        return arrow.equals(">") ? (s2 + " >") : (arrow + " " + s2);
    }
}
