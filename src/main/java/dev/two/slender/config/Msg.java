package dev.two.slender.config;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public final class Msg {
    private final Map<String, String> raw = new HashMap();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final ConfigurationSection messages;

    public Msg(ConfigurationSection var1) {
        this.messages = var1;
    }

    public String raw(String var1) {
        return (String)this.raw.computeIfAbsent(var1, (var1x) -> {
            String var2 = this.messages == null ? null : this.messages.getString(var1x);
            return var2 == null ? "<red>" + var1x + "</red>" : var2;
        });
    }

    public String fill(String var1, String... var2) {
        String var3 = this.raw(var1);

        for(int var4 = 0; var4 + 1 < var2.length; var4 += 2) {
            var3 = var3.replace("{" + var2[var4] + "}", var2[var4 + 1]);
        }

        return var3;
    }

    public Component get(String var1, String... var2) {
        return this.mm.deserialize(this.fill(var1, var2));
    }

    public Component parse(String var1) {
        return this.mm.deserialize(var1);
    }

    public Component prefixed(String var1, String... var2) {
        return this.mm.deserialize(this.raw("prefix")).append(this.get(var1, var2));
    }

    public void send(CommandSender var1, String var2, String... var3) {
        var1.sendMessage(this.prefixed(var2, var3));
    }
}
