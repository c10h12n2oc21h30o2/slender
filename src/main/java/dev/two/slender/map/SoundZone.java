// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.function.DoubleSupplier;
import java.util.List;

record SoundZone(BlockPos center, int radius, String sound, float volume, float pitch, double chance) {
    public boolean contains(final double n, final double n2) {
        final double n3 = n - (this.center.x() + 0.5);
        final double n4 = n2 - (this.center.z() + 0.5);
        return n3 * n3 + n4 * n4 <= this.radius * (double)this.radius;
    }
    
    public SoundZone offset(final int n, final int n2, final int n3) {
        return new SoundZone(this.center.offset(n, n2, n3), this.radius, this.sound, this.volume, this.pitch, this.chance);
    }
    
    public String serialize() {
        return this.center.serialize() + "|" + this.radius + "|" + this.sound + "|" + this.volume + "|" + this.pitch + "|" + this.chance;
    }
    
    public static SoundZone parse(final String s) {
        final String[] split = s.split("\\|");
        if (split.length != 6) {
            throw new IllegalArgumentException("Zona de sonido inv\u00e1lida: " + s);
        }
        return new SoundZone(BlockPos.parse(split[0]), Integer.parseInt(split[1]), split[2], Float.parseFloat(split[3]), Float.parseFloat(split[4]), Double.parseDouble(split[5]));
    }
    
    public static List<SoundZone> due(final List<SoundZone> list, final double n, final double n2, final DoubleSupplier doubleSupplier) {
        final ArrayList list2 = new ArrayList();
        for (final SoundZone soundZone : list) {
            if (soundZone.contains(n, n2) && doubleSupplier.getAsDouble() < soundZone.chance()) {
                list2.add(soundZone);
            }
        }
        return list2;
    }
}
