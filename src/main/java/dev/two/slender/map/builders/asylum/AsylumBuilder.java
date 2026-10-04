// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.asylum;

import dev.two.slender.map.PageSpot;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.BlockPos;
import java.util.List;
import dev.two.slender.map.Loc;
import java.util.ArrayList;
import dev.two.slender.map.gen.PlanStyle;
import dev.two.slender.map.gen.PlanPainter;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.TerrainPainter;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Builder;
import org.bukkit.Material;
import dev.two.slender.map.MapBuilder;

public final class AsylumBuilder implements MapBuilder
{
    @Override
    public String id() {
        return "sanatorio";
    }
    
    @Override
    public String displayName() {
        return "Sanatorio";
    }
    
    @Override
    public Material icon() {
        return Material.IRON_BARS;
    }
    
    @Override
    public int radius() {
        return 92;
    }
    
    @Override
    public ArenaMap build(final Builder builder, final String world, final long seed) {
        final Heightmap terrain = AsylumLayout.terrain(seed);
        final Noise noise = new Noise(seed ^ 0x5EEDL);
        TerrainPainter.paint(builder, terrain, (n4, n5, p5) -> AsylumNature.surface(builder2, heightmap, noise2, n4, n5));
        AsylumNature.plant(builder, terrain, noise);
        final List<BlockPos> border = Decor.border(builder, terrain, 76);
        Decor.exitRoad(builder, 76);
        AsylumPlaces.grounds(builder);
        PlanPainter.paint(builder, AsylumLayout.PLAN, 8, new AsylumStyle(builder));
        AsylumPlaces.tower(builder);
        Decor.posts(builder, terrain, AsylumLayout.POSTS, AsylumLayout::place, 76);
        Decor.board(builder, AsylumLayout.PLACES, "SANATORIO DE", "VALDEMORA");
        Decor.notes(builder, terrain, AsylumLayout.NOTES, AsylumLayout::place);
        Decor.notes(builder, terrain, AsylumLayout.SIGNS, AsylumLayout::place);
        final List<PageSpot> pages = Decor.pages(builder, terrain, AsylumLayout.PAGES, AsylumLayout::place);
        final ArrayList batterySpots = new ArrayList();
        for (final int[] array : AsylumLayout.BATTERIES_OUT) {
            batterySpots.add(builder.abs(array[0], terrain.at(array[0], array[1]) + 1, array[1]));
        }
        for (final int[] array2 : AsylumLayout.BATTERIES_IN) {
            batterySpots.add(builder.abs(array2[1], 8 + array2[0] * 5 + 1, array2[2]));
        }
        final ArrayList spawns = new ArrayList();
        for (final int[] array3 : AsylumLayout.SPAWNS) {
            spawns.add(builder.loc(array3[0], terrain.at(array3[0], array3[1]) + 1, array3[1], 180.0f));
        }
        return new ArenaMap(this.id(), this.displayName(), world, builder.abs(0, 0, 0), builder.abs(-76, -8, -76), builder.abs(76, 45, 86), (Loc)spawns.get(0), spawns, pages, batterySpots, border, "minecraft:iron_bars[east=true,west=true]", builder.abs(-3, 1, 79), builder.abs(3, 4, 85), seed, AsylumLayout.ambience(terrain).offset(builder.ox, builder.oy, builder.oz));
    }
}
