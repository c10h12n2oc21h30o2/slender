// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import net.kyori.adventure.title.Title;
import java.time.Duration;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import java.util.HashSet;
import net.kyori.adventure.text.Component;
import java.util.UUID;
import java.util.Set;
import net.kyori.adventure.bossbar.BossBar;

public final class Hud
{
    private final BossBar bar;
    private final Set<UUID> viewers;
    
    public Hud() {
        this.bar = BossBar.bossBar((Component)Component.empty(), 0.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        this.viewers = new HashSet<UUID>();
    }
    
    public void show(final Player player) {
        player.showBossBar(this.bar);
        this.viewers.add(player.getUniqueId());
    }
    
    public void hide(final Player player) {
        player.hideBossBar(this.bar);
        this.viewers.remove(player.getUniqueId());
    }
    
    public void update(final Component component, final float b) {
        this.bar.name(component);
        this.bar.progress(Math.max(0.0f, Math.min(1.0f, b)));
    }
    
    public void hideAll() {
        final Iterator iterator = new ArrayList(this.viewers).iterator();
        while (iterator.hasNext()) {
            final Player player = Bukkit.getPlayer((UUID)iterator.next());
            if (player != null) {
                player.hideBossBar(this.bar);
            }
        }
        this.viewers.clear();
    }
    
    public static void title(final Player player, final Component component, final Component component2, final int n, final int n2, final int n3) {
        player.showTitle(Title.title(component, component2, Title.Times.times(Duration.ofMillis(n * 50L), Duration.ofMillis(n2 * 50L), Duration.ofMillis(n3 * 50L))));
    }
}
