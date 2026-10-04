// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.Map;
import org.bukkit.plugin.Plugin;

public final class Concealment
{
    private final Plugin plugin;
    private final Map<UUID, Player> hiddenFrom;
    
    public Concealment(final Plugin plugin) {
        this.hiddenFrom = new HashMap<UUID, Player>();
        this.plugin = plugin;
    }
    
    public void hide(final Player player, final Collection<Player> collection) {
        for (final Player player2 : collection) {
            player2.hidePlayer(this.plugin, player);
            this.hiddenFrom.put(player2.getUniqueId(), player2);
        }
    }
    
    public void revealTo(final Player player, final Player player2) {
        if (this.hiddenFrom.remove(player.getUniqueId()) != null && player.isOnline()) {
            player.showPlayer(this.plugin, player2);
        }
    }
    
    public void reveal(final Player player) {
        for (final Player player2 : this.hiddenFrom.values()) {
            if (player2.isOnline()) {
                player2.showPlayer(this.plugin, player);
            }
        }
        this.hiddenFrom.clear();
    }
}
