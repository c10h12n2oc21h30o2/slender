package dev.two.slender.config;

import org.bukkit.configuration.ConfigurationSection;

public final class ConfigMigration {
    public static final int VERSION = 3;

    private ConfigMigration() {
    }

    public static boolean migrate(ConfigurationSection var0) {
        int var1 = var0.getInt("config-version", 1);
        if (var1 >= 3) {
            return false;
        } else {
            if (var1 < 2) {
                if (was(var0, "flashlight.battery-drain-per-second", 0.6667)) {
                    var0.set("flashlight.battery-drain-per-second", 0.2);
                }

                if (was(var0, "flashlight.battery-charge", 35.0)) {
                    var0.set("flashlight.battery-charge", 40);
                }
            }

            if (var1 < 3) {
                if (!var0.contains("vestibulo.niebla", true)) {
                    var0.set("vestibulo.niebla", 2);
                }

                if (!var0.contains("vestibulo.bruma", true)) {
                    var0.set("vestibulo.bruma", true);
                }
            }

            var0.set("config-version", 3);
            return true;
        }
    }

    private static boolean was(ConfigurationSection var0, String var1, double var2) {
        Object var5 = var0.get(var1, (Object)null);
        boolean var10000;
        if (var5 instanceof Number var4) {
            if (Math.abs(var4.doubleValue() - var2) < 0.001) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }
}
