package dev.two.slender.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class Concealment {
    private final Plugin plugin;
    private final Map<UUID, Player> hiddenFrom = new HashMap();

    public Concealment(Plugin var1) {
        this.plugin = var1;
    }

    public void hide(Player var1, Collection<Player> var2) {
        Iterator var3 = var2.iterator();

        while(var3.hasNext()) {
            Player var4 = (Player)var3.next();
            var4.hidePlayer(this.plugin, var1);
            this.hiddenFrom.put(var4.getUniqueId(), var4);
        }

    }

    public void revealTo(Player var1, Player var2) {
        if (this.hiddenFrom.remove(var1.getUniqueId()) != null && var1.isOnline()) {
            var1.showPlayer(this.plugin, var2);
        }

    }

    public void reveal(Player var1) {
        Iterator var2 = this.hiddenFrom.values().iterator();

        while(var2.hasNext()) {
            Player var3 = (Player)var2.next();
            if (var3.isOnline()) {
                var3.showPlayer(this.plugin, var1);
            }
        }

        this.hiddenFrom.clear();
    }
}
