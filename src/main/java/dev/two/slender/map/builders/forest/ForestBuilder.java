// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.forest;

import dev.two.slender.map.PageSpot;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.BlockPos;
import java.util.List;
import dev.two.slender.map.Loc;
import java.util.ArrayList;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.TerrainPainter;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Builder;
import org.bukkit.Material;
import dev.two.slender.map.MapBuilder;

public final class ForestBuilder implements MapBuilder
{
    private static final int R = 80;
    
    @Override
    public String id() {
        return "bosque";
    }
    
    @Override
    public String displayName() {
        return "Bosque";
    }
    
    @Override
    public Material icon() {
        return Material.SPRUCE_SAPLING;
    }
    
    @Override
    public int radius() {
        return 96;
    }
    
    @Override
    public ArenaMap build(final Builder builder, final String world, final long seed) {
        final Heightmap terrain = ForestLayout.terrain(seed);
        final Noise noise = new Noise(seed ^ 0x5EEDL);
        TerrainPainter.paint(builder, terrain, (n4, n5, p5) -> ForestNature.surface(builder2, heightmap, noise2, n4, n5));
        ForestNature.plant(builder, terrain, noise);
        final List<BlockPos> border = Decor.border(builder, terrain, 80);
        Decor.exitRoad(builder, 80);
        ForestPlaces.build(builder, terrain);
        Decor.posts(builder, terrain, ForestLayout.POSTS, ForestLayout::place, 80);
        Decor.board(builder, ForestLayout.PLACES, "BOSQUE DE", "VALDEMORA");
        Decor.notes(builder, terrain, ForestLayout.NOTES, ForestLayout::place);
        final List<PageSpot> pages = Decor.pages(builder, terrain, ForestLayout.PAGES, ForestLayout::place);
        final ArrayList batterySpots = new ArrayList();
        for (final int[] array : ForestLayout.BATTERIES) {
            batterySpots.add(builder.abs(array[0], terrain.at(array[0], array[1]) + 1, array[1]));
        }
        final ArrayList spawns = new ArrayList();
        for (final int[] array2 : ForestLayout.SPAWNS) {
            spawns.add(builder.loc(array2[0], terrain.at(array2[0], array2[1]) + 1, array2[1], 180.0f));
        }
        return new ArenaMap(this.id(), this.displayName(), world, builder.abs(0, 0, 0), builder.abs(-80, -8, -80), builder.abs(80, 45, 90), (Loc)spawns.get(0), spawns, pages, batterySpots, border, "minecraft:iron_bars[east=true,west=true]", builder.abs(-3, 1, 83), builder.abs(3, 4, 89), seed, ForestLayout.ambience(terrain).offset(builder.ox, builder.oy, builder.oz));
    }
}
