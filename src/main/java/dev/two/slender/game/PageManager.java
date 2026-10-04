package dev.two.slender.game;

import dev.two.slender.logic.LightDiff;
import dev.two.slender.map.PageSpot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class PageManager {
    public static final String TAG = "slender_page";
    public static final String FAKE_TAG = "slender_fake_page";
    private final Plugin plugin;
    private final Items items;
    private final Map<UUID, PageSpot> spots = new HashMap();
    private final Map<UUID, ItemFrame> frames = new HashMap();
    private final Map<UUID, UUID> fakeViewer = new HashMap();

    public PageManager(Plugin var1, Items var2) {
        this.plugin = var1;
        this.items = var2;
    }

    public void spawnAll(World var1, List<PageSpot> var2) {
        Iterator var3 = var2.iterator();

        while(var3.hasNext()) {
            PageSpot var4 = (PageSpot)var3.next();
            ItemFrame var5 = this.spawn(var1, var4, true);
            if (var5 == null) {
                Logger var10000 = this.plugin.getLogger();
                String var10001 = var4.label();
                var10000.warning("No se pudo colgar la página en " + var10001 + " " + var4.pos().serialize());
            } else {
                this.spots.put(var5.getUniqueId(), var4);
                this.frames.put(var5.getUniqueId(), var5);
            }
        }

    }

    private ItemFrame spawn(World var1, PageSpot var2, boolean var3) {
        Location var4 = new Location(var1, (double)var2.pos().x() + 0.5, (double)var2.pos().y() + 0.5, (double)var2.pos().z() + 0.5);
        BlockFace var5 = BlockFace.valueOf(var2.facing());

        try {
            return (ItemFrame)var1.spawn(var4, ItemFrame.class, (var3x) -> {
                var3x.setFacingDirection(var5, true);
                var3x.setItem(this.items.make("page"), false);
                var3x.setFixed(true);
                var3x.setInvulnerable(true);
                var3x.setPersistent(false);
                var3x.setVisibleByDefault(var3);
                var3x.getScoreboardTags().add(var3 ? "slender_page" : "slender_fake_page");
            });
        } catch (RuntimeException var7) {
            return null;
        }
    }

    public boolean isPage(Entity var1) {
        return this.spots.containsKey(var1.getUniqueId());
    }

    public void collect(Entity var1) {
        this.spots.remove(var1.getUniqueId());
        this.frames.remove(var1.getUniqueId());
        var1.remove();
    }

    public int remaining() {
        return this.spots.size();
    }

    public Set<Long> framePositions() {
        HashSet var1 = new HashSet();
        Iterator var2 = this.frames.values().iterator();

        while(var2.hasNext()) {
            ItemFrame var3 = (ItemFrame)var2.next();
            if (var3.isValid()) {
                Location var4 = var3.getLocation();
                var1.add(LightDiff.pack(var4.getBlockX(), var4.getBlockY(), var4.getBlockZ()));
            }
        }

        return var1;
    }

    public Set<PageSpot> activeSpots() {
        return new HashSet(this.spots.values());
    }

    public ItemFrame spawnFake(World var1, PageSpot var2, Player var3) {
        ItemFrame var4 = this.spawn(var1, var2, false);
        if (var4 == null) {
            return null;
        } else {
            var3.showEntity(this.plugin, var4);
            this.frames.put(var4.getUniqueId(), var4);
            this.fakeViewer.put(var4.getUniqueId(), var3.getUniqueId());
            return var4;
        }
    }

    public boolean isFake(Entity var1) {
        return this.fakeViewer.containsKey(var1.getUniqueId());
    }

    public boolean hasFake(UUID var1) {
        return this.fakeViewer.containsValue(var1);
    }

    public void removeFake(Entity var1) {
        this.fakeViewer.remove(var1.getUniqueId());
        this.frames.remove(var1.getUniqueId());
        var1.remove();
    }

    public void clear() {
        Iterator var1 = this.frames.values().iterator();

        while(var1.hasNext()) {
            ItemFrame var2 = (ItemFrame)var1.next();
            if (var2.isValid()) {
                var2.remove();
            }
        }

        this.frames.clear();
        this.spots.clear();
        this.fakeViewer.clear();
    }
}
