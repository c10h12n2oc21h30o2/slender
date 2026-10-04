// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.cornfield;

import dev.two.slender.map.PageSpot;
import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.gen.Note;
import java.util.List;
import java.util.Collection;
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

public final class CornfieldBuilder implements MapBuilder
{
    @Override
    public String id() {
        return "maizal";
    }
    
    @Override
    public String displayName() {
        return "Maizal";
    }
    
    @Override
    public Material icon() {
        return Material.HAY_BLOCK;
    }
    
    @Override
    public int radius() {
        return 92;
    }
    
    @Override
    public ArenaMap build(final Builder builder, final String world, final long seed) {
        final Heightmap terrain = CornfieldLayout.terrain(seed);
        final boolean[][] maze = CornfieldLayout.maze(seed);
        final Noise noise = new Noise(seed ^ 0x5EEDL);
        final List<int[]> poles = CornfieldLayout.poles(terrain);
        TerrainPainter.paint(builder, terrain, (n4, n5, p5) -> CornfieldNature.surface(builder2, heightmap, array5, n4, n5));
        CornfieldNature.corn(builder, terrain, maze);
        CornfieldNature.plant(builder, terrain, noise, poles);
        final List<BlockPos> border = Decor.border(builder, terrain, 76);
        Decor.exitRoad(builder, 76);
        CornfieldPlaces.build(builder, terrain, poles);
        final int at = terrain.at(CornfieldLayout.place("casa").x(), CornfieldLayout.place("casa").z());
        final int at2 = terrain.at(CornfieldLayout.place("granero").x(), CornfieldLayout.place("granero").z());
        PlanPainter.paint(builder, CornfieldLayout.CASA, at, new FarmStyles.House(builder));
        PlanPainter.paint(builder, CornfieldLayout.GRANERO, at2, new FarmStyles.Barn(builder));
        CornfieldPlaces.roofs(builder, terrain);
        Decor.posts(builder, terrain, CornfieldLayout.POSTS, CornfieldLayout::place, 76);
        Decor.board(builder, CornfieldLayout.PLACES, "GRANJA", "ROLD\u00c1N");
        final ArrayList list = new ArrayList((Collection<? extends E>)CornfieldLayout.NOTES);
        list.add(CornfieldLayout.HOUSE_SIGN);
        Decor.notes(builder, terrain, list, CornfieldLayout::place);
        final List<PageSpot> pages = Decor.pages(builder, terrain, CornfieldLayout.PAGES, CornfieldLayout::place);
        final ArrayList batterySpots = new ArrayList();
        for (final int[] array : CornfieldLayout.BATTERIES_OUT) {
            batterySpots.add(builder.abs(array[0], terrain.at(array[0], array[1]) + 1, array[1]));
        }
        for (final int[] array2 : CornfieldLayout.BATTERIES_CASA) {
            batterySpots.add(builder.abs(array2[1], at + array2[0] * CornfieldLayout.CASA.storey() + 1, array2[2]));
        }
        for (final int[] array3 : CornfieldLayout.BATTERIES_GRANERO) {
            batterySpots.add(builder.abs(array3[1], at2 + array3[0] * CornfieldLayout.GRANERO.storey() + 1, array3[2]));
        }
        final ArrayList spawns = new ArrayList();
        for (final int[] array4 : CornfieldLayout.SPAWNS) {
            spawns.add(builder.loc(array4[0], terrain.at(array4[0], array4[1]) + 1, array4[1], 180.0f));
        }
        return new ArenaMap(this.id(), this.displayName(), world, builder.abs(0, 0, 0), builder.abs(-76, -8, -76), builder.abs(76, 45, 86), (Loc)spawns.get(0), spawns, pages, batterySpots, border, "minecraft:iron_bars[east=true,west=true]", builder.abs(-3, 1, 79), builder.abs(3, 4, 85), seed, CornfieldLayout.ambience(terrain).offset(builder.ox, builder.oy, builder.oz));
    }
}
