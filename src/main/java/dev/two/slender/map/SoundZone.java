package dev.two.slender.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.DoubleSupplier;

public record SoundZone(BlockPos center, int radius, String sound, float volume, float pitch, double chance) {
    public SoundZone(BlockPos center, int radius, String sound, float volume, float pitch, double chance) {
        this.center = center;
        this.radius = radius;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.chance = chance;
    }

    public boolean contains(double var1, double var3) {
        double var5 = var1 - ((double)this.center.x() + 0.5);
        double var7 = var3 - ((double)this.center.z() + 0.5);
        return var5 * var5 + var7 * var7 <= (double)this.radius * (double)this.radius;
    }

    public SoundZone offset(int var1, int var2, int var3) {
        return new SoundZone(this.center.offset(var1, var2, var3), this.radius, this.sound, this.volume, this.pitch, this.chance);
    }

    public String serialize() {
        String var10000 = this.center.serialize();
        return var10000 + "|" + this.radius + "|" + this.sound + "|" + this.volume + "|" + this.pitch + "|" + this.chance;
    }

    public static SoundZone parse(String var0) {
        String[] var1 = var0.split("\\|");
        if (var1.length != 6) {
            throw new IllegalArgumentException("Zona de sonido inválida: " + var0);
        } else {
            return new SoundZone(BlockPos.parse(var1[0]), Integer.parseInt(var1[1]), var1[2], Float.parseFloat(var1[3]), Float.parseFloat(var1[4]), Double.parseDouble(var1[5]));
        }
    }

    public static List<SoundZone> due(List<SoundZone> var0, double var1, double var3, DoubleSupplier var5) {
        ArrayList var6 = new ArrayList();
        Iterator var7 = var0.iterator();

        while(var7.hasNext()) {
            SoundZone var8 = (SoundZone)var7.next();
            if (var8.contains(var1, var3) && var5.getAsDouble() < var8.chance()) {
                var6.add(var8);
            }
        }

        return var6;
    }

    public BlockPos center() {
        return this.center;
    }

    public int radius() {
        return this.radius;
    }

    public String sound() {
        return this.sound;
    }

    public float volume() {
        return this.volume;
    }

    public float pitch() {
        return this.pitch;
    }

    public double chance() {
        return this.chance;
    }
}
