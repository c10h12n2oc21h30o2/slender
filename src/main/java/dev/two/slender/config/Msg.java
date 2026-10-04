// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.config;

import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import java.util.HashMap;
import org.bukkit.configuration.ConfigurationSection;
import net.kyori.adventure.text.minimessage.MiniMessage;
import java.util.Map;

public final class Msg
{
    private final Map<String, String> raw;
    private final MiniMessage mm;
    private final ConfigurationSection messages;
    
    public Msg(final ConfigurationSection messages) {
        this.raw = new HashMap<String, String>();
        this.mm = MiniMessage.miniMessage();
        this.messages = messages;
    }
    
    public String raw(final String key) {
        return this.raw.computeIfAbsent(key, s -> {
            final String s2 = (this.messages == null) ? null : this.messages.getString(s);
            return (s2 == null) ? ("<red>" + s + "</red>") : s2;
        });
    }
    
    public String fill(final String s, final String... array) {
        String s2 = this.raw(s);
        for (int n = 0; n + 1 < array.length; n += 2) {
            s2 = s2.replace("{" + array[n], (CharSequence)array[n + 1]);
        }
        return s2;
    }
    
    public Component get(final String s, final String... array) {
        return this.mm.deserialize((Object)this.fill(s, array));
    }
    
    public Component parse(final String s) {
        return this.mm.deserialize((Object)s);
    }
    
    public Component prefixed(final String s, final String... array) {
        return this.mm.deserialize((Object)this.raw("prefix")).append(this.get(s, array));
    }
    
    public void send(final CommandSender commandSender, final String s, final String... array) {
        commandSender.sendMessage(this.prefixed(s, array));
    }
}
