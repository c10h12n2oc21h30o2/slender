// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.config;

import org.bukkit.configuration.ConfigurationSection;

public final class ConfigMigration
{
    public static final int VERSION = 3;
    
    private ConfigMigration() {
    }
    
    public static boolean migrate(final ConfigurationSection configurationSection) {
        final int int1 = configurationSection.getInt("config-version", 1);
        if (int1 >= 3) {
            return false;
        }
        if (int1 < 2) {
            if (was(configurationSection, "flashlight.battery-drain-per-second", 0.6667)) {
                configurationSection.set("flashlight.battery-drain-per-second", (Object)0.2);
            }
            if (was(configurationSection, "flashlight.battery-charge", 35.0)) {
                configurationSection.set("flashlight.battery-charge", (Object)40);
            }
        }
        if (int1 < 3) {
            if (!configurationSection.contains("vestibulo.niebla", true)) {
                configurationSection.set("vestibulo.niebla", (Object)2);
            }
            if (!configurationSection.contains("vestibulo.bruma", true)) {
                configurationSection.set("vestibulo.bruma", (Object)true);
            }
        }
        configurationSection.set("config-version", (Object)3);
        return true;
    }
    
    private static boolean was(final ConfigurationSection configurationSection, final String s, final double n) {
        final Object value = configurationSection.get(s, (Object)null);
        return value instanceof Number && Math.abs(((Number)value).doubleValue() - n) < 0.001;
    }
}
