// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public final class EditOps
{
    private EditOps() {
    }
    
    public static int nearest(final List<BlockPos> list, final double n, final double n2, final double n3, final double n4) {
        int n5 = -1;
        double n6 = n4 * n4;
        for (int i = 0; i < list.size(); ++i) {
            final BlockPos blockPos = list.get(i);
            final double n7 = blockPos.x() + 0.5 - n;
            final double n8 = blockPos.y() + 0.5 - n2;
            final double n9 = blockPos.z() + 0.5 - n3;
            final double n10 = n7 * n7 + n8 * n8 + n9 * n9;
            if (n10 <= n6) {
                n6 = n10;
                n5 = i;
            }
        }
        return n5;
    }
    
    public static boolean wallFace(final String s) {
        return switch (s) {
            case "NORTH",  "SOUTH",  "EAST",  "WEST" -> true;
            default -> false;
        };
    }
    
    public static BlockPos front(final BlockPos blockPos, final String s) {
        return switch (s) {
            case "NORTH" -> blockPos.offset(0, 0, -1);
            case "SOUTH" -> blockPos.offset(0, 0, 1);
            case "EAST" -> blockPos.offset(1, 0, 0);
            case "WEST" -> blockPos.offset(-1, 0, 0);
            default -> throw new IllegalArgumentException("Cara no v\u00e1lida: " + s);
        };
    }
    
    public static <T> List<T> plus(final List<T> c, final T t) {
        final ArrayList coll = new ArrayList((Collection<? extends E>)c);
        coll.add(t);
        return (List<T>)List.copyOf((Collection<?>)coll);
    }
    
    public static <T> List<T> minus(final List<T> c, final int n) {
        final ArrayList coll = new ArrayList((Collection<? extends E>)c);
        coll.remove(n);
        return (List<T>)List.copyOf((Collection<?>)coll);
    }
}
