// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.instituto;

import dev.two.slender.map.PageSpot;
import dev.two.slender.logic.Heightmap;
import java.util.Iterator;
import dev.two.slender.map.schem.Schematic;
import dev.two.slender.map.BlockPos;
import dev.two.slender.map.Loc;
import dev.two.slender.map.gen.Note;
import java.util.Collection;
import java.util.ArrayList;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.PlanStyle;
import dev.two.slender.map.gen.PlanPainter;
import java.util.List;
import dev.two.slender.map.schem.SchematicReader;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Builder;
import org.bukkit.Material;
import dev.two.slender.map.MapBuilder;

public final class InstitutoBuilder implements MapBuilder
{
    @Override
    public String id() {
        return "instituto";
    }
    
    @Override
    public String displayName() {
        return "Instituto";
    }
    
    @Override
    public Material icon() {
        return Material.BELL;
    }
    
    @Override
    public int radius() {
        return 232;
    }
    
    @Override
    public ArenaMap build(final Builder builder, final String world, final long seed) {
        final Builder shifted = builder.shifted(-200, -1, -218);
        final Schematic resource = SchematicReader.resource("maps/instituto.schem");
        shifted.paste(resource, 0, 0, 0);
        for (final String s : List.of("A", "B", "C", "P")) {
            PlanPainter.paintInside(shifted, InstitutoLayout.plan(s), 1, new InstitutoStyle(shifted, s));
        }
        InstitutoPlaces.outside(shifted, InstitutoLayout.streetLamps(resource));
        final List<BlockPos> gate = InstitutoPlaces.gate(shifted);
        final Heightmap ground = InstitutoLayout.ground();
        Decor.posts(shifted, ground, InstitutoLayout.POSTS, InstitutoLayout::place, 19, 432);
        Decor.board(shifted, InstitutoLayout.BOARD[0], 1, InstitutoLayout.BOARD[1], InstitutoLayout.PLACES, "IES LAGOA", "DE ANTELA");
        final ArrayList list = new ArrayList((Collection<? extends E>)InstitutoLayout.NOTES);
        list.addAll(InstitutoLayout.signs());
        Decor.notes(shifted, ground, list, InstitutoLayout::place);
        final List<PageSpot> pages = Decor.pages(shifted, ground, InstitutoLayout.PAGES, InstitutoLayout::place);
        final ArrayList batterySpots = new ArrayList();
        for (final int[] array : InstitutoLayout.BATTERIES_OUT) {
            batterySpots.add(shifted.abs(array[0], 2, array[1]));
        }
        for (final int[] array2 : InstitutoLayout.BATTERIES_IN) {
            batterySpots.add(shifted.abs(array2[1], 1 + array2[0] * 5 + 1, array2[2]));
        }
        final ArrayList spawns = new ArrayList();
        for (final int[] array3 : InstitutoLayout.SPAWNS) {
            spawns.add(shifted.loc(array3[0], 2.0, array3[1], 0.0f));
        }
        return new ArenaMap(this.id(), this.displayName(), world, builder.abs(0, 0, 0), shifted.abs(InstitutoLayout.BOUNDS[0], -6, InstitutoLayout.BOUNDS[1]), shifted.abs(InstitutoLayout.BOUNDS[2], 40, InstitutoLayout.BOUNDS[3]), (Loc)spawns.get(0), spawns, pages, batterySpots, gate, "minecraft:red_concrete", shifted.abs(InstitutoLayout.EXIT[0], 2, InstitutoLayout.EXIT[1]), shifted.abs(InstitutoLayout.EXIT[2], 5, InstitutoLayout.EXIT[3]), seed, InstitutoLayout.ambience(resource).offset(shifted.ox, shifted.oy, shifted.oz));
    }
}
