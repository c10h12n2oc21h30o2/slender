// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class HubFog
{
    private HubFog() {
    }
    
    public static int viewDistance(final int a, final int b) {
        if (b <= 0) {
            return a;
        }
        final int max = Math.max(2, b);
        return (a <= 1) ? max : Math.min(a, max);
    }
    
    public static boolean onPlatform(final double a, final double a2) {
        final int n = (int)Math.floor(a);
        final int n2 = (int)Math.floor(a2);
        return n >= -30 && n <= 30 && n2 >= -2 && n2 <= 7;
    }
    
    public static boolean onTracks(final double a, final double a2) {
        final int n = (int)Math.floor(a);
        final int n2 = (int)Math.floor(a2);
        return n >= -35 && n <= 35 && n2 >= 8 && n2 <= 16;
    }
    
    public static List<double[]> mist(final double n, final double n2, final int n3, final double n4, final Random random) {
        final ArrayList list = new ArrayList();
        for (int n5 = 0; n5 < n3 * 3 && list.size() < n3; ++n5) {
            final double n6 = random.nextDouble() * 2.0 * 3.141592653589793;
            final double n7 = n4 * Math.sqrt(random.nextDouble());
            final double n8 = n + Math.cos(n6) * n7;
            final double n9 = n2 + Math.sin(n6) * n7;
            if (onPlatform(n8, n9)) {
                list.add(new double[] { n8, 2.0 + random.nextDouble() * 0.6, n9 });
            }
            else if (onTracks(n8, n9)) {
                list.add(new double[] { n8, 1.0 + random.nextDouble() * 0.6, n9 });
            }
        }
        return list;
    }
}
