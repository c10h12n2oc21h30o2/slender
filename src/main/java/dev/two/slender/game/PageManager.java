// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.game;

import org.bukkit.entity.Player;
import java.util.Collection;
import dev.two.slender.logic.LightDiff;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import java.util.Iterator;
import java.util.List;
import org.bukkit.World;
import java.util.HashMap;
import org.bukkit.entity.ItemFrame;
import dev.two.slender.map.PageSpot;
import java.util.UUID;
import java.util.Map;
import org.bukkit.plugin.Plugin;

public final class PageManager
{
    public static final String TAG = "slender_page";
    public static final String FAKE_TAG = "slender_fake_page";
    private final Plugin plugin;
    private final Items items;
    private final Map<UUID, PageSpot> spots;
    private final Map<UUID, ItemFrame> frames;
    private final Map<UUID, UUID> fakeViewer;
    
    public PageManager(final Plugin plugin, final Items items) {
        this.spots = new HashMap<UUID, PageSpot>();
        this.frames = new HashMap<UUID, ItemFrame>();
        this.fakeViewer = new HashMap<UUID, UUID>();
        this.plugin = plugin;
        this.items = items;
    }
    
    public void spawnAll(final World world, final List<PageSpot> list) {
        for (PageSpot pageSpot : list) {
            final ItemFrame spawn = this.spawn(world, pageSpot, true);
            if (spawn == null) {
                this.plugin.getLogger().warning("No se pudo colgar la p\u00e1gina en " + pageSpot.label() + " " + pageSpot.pos().serialize());
            }
            else {
                this.spots.put(spawn.getUniqueId(), pageSpot);
                this.frames.put(spawn.getUniqueId(), spawn);
            }
        }
    }
    
    private ItemFrame spawn(final World world, final PageSpot pageSpot, final boolean b) {
        final Location location = new Location(world, pageSpot.pos().x() + 0.5, pageSpot.pos().y() + 0.5, pageSpot.pos().z() + 0.5);
        BlockFace.valueOf(pageSpot.facing());
        try {
            return (ItemFrame)world.spawn(location, (Class)ItemFrame.class, itemFrame -> {
                itemFrame.setFacingDirection(blockFace, (boolean)(1 != 0));
                itemFrame.setItem(this.items.make("page"), (boolean)(0 != 0));
                itemFrame.setFixed((boolean)(1 != 0));
                itemFrame.setInvulnerable((boolean)(1 != 0));
                itemFrame.setPersistent((boolean)(0 != 0));
                itemFrame.setVisibleByDefault(visibleByDefault);
                itemFrame.getScoreboardTags().add(visibleByDefault ? "slender_page" : "slender_fake_page");
            });
        }
        catch (final RuntimeException ex) {
            return null;
        }
    }
    
    public boolean isPage(final Entity entity) {
        return this.spots.containsKey(entity.getUniqueId());
    }
    
    public void collect(final Entity entity) {
        this.spots.remove(entity.getUniqueId());
        this.frames.remove(entity.getUniqueId());
        entity.remove();
    }
    
    public int remaining() {
        return this.spots.size();
    }
    
    public Set<Long> framePositions() {
        final HashSet set = new HashSet();
        for (final ItemFrame itemFrame : this.frames.values()) {
            if (!itemFrame.isValid()) {
                continue;
            }
            final Location location = itemFrame.getLocation();
            set.add(LightDiff.pack(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        }
        return set;
    }
    
    public Set<PageSpot> activeSpots() {
        return new HashSet<PageSpot>(this.spots.values());
    }
    
    public ItemFrame spawnFake(final World world, final PageSpot pageSpot, final Player player) {
        final ItemFrame spawn = this.spawn(world, pageSpot, false);
        if (spawn == null) {
            return null;
        }
        player.showEntity(this.plugin, (Entity)spawn);
        this.frames.put(spawn.getUniqueId(), spawn);
        this.fakeViewer.put(spawn.getUniqueId(), player.getUniqueId());
        return spawn;
    }
    
    public boolean isFake(final Entity entity) {
        return this.fakeViewer.containsKey(entity.getUniqueId());
    }
    
    public boolean hasFake(final UUID uuid) {
        return this.fakeViewer.containsValue(uuid);
    }
    
    public void removeFake(final Entity entity) {
        this.fakeViewer.remove(entity.getUniqueId());
        this.frames.remove(entity.getUniqueId());
        entity.remove();
    }
    
    public void clear() {
        for (final ItemFrame itemFrame : this.frames.values()) {
            if (itemFrame.isValid()) {
                itemFrame.remove();
            }
        }
        this.frames.clear();
        this.spots.clear();
        this.fakeViewer.clear();
    }
}
