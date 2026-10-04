package dev.two.slender.game;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Hud {
    private final BossBar bar;
    private final Set<UUID> viewers;

    public Hud() {
        this.bar = BossBar.bossBar((Component)Component.empty(), 0.0F, Color.RED, Overlay.PROGRESS);
        this.viewers = new HashSet();
    }

    public void show(Player var1) {
        var1.showBossBar(this.bar);
        this.viewers.add(var1.getUniqueId());
    }

    public void hide(Player var1) {
        var1.hideBossBar(this.bar);
        this.viewers.remove(var1.getUniqueId());
    }

    public void update(Component var1, float var2) {
        this.bar.name(var1);
        this.bar.progress(Math.max(0.0F, Math.min(1.0F, var2)));
    }

    public void hideAll() {
        Iterator var1 = (new ArrayList(this.viewers)).iterator();

        while(var1.hasNext()) {
            UUID var2 = (UUID)var1.next();
            Player var3 = Bukkit.getPlayer(var2);
            if (var3 != null) {
                var3.hideBossBar(this.bar);
            }
        }

        this.viewers.clear();
    }

    public static void title(Player var0, Component var1, Component var2, int var3, int var4, int var5) {
        var0.showTitle((Title)Title.title(var1, var2, Times.times(Duration.ofMillis((long)var3 * 50L), Duration.ofMillis((long)var4 * 50L), Duration.ofMillis((long)var5 * 50L))));
    }
}
