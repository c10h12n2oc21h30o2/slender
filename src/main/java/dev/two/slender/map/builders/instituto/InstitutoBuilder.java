package dev.two.slender.map.builders.instituto;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.PlanPainter;
import dev.two.slender.map.schem.Schematic;
import dev.two.slender.map.schem.SchematicReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;

public final class InstitutoBuilder implements MapBuilder {
    public String id() {
        return "instituto";
    }

    public String displayName() {
        return "Instituto";
    }

    public Material icon() {
        return Material.BELL;
    }

    public int radius() {
        return 232;
    }

    public ArenaMap build(Builder var1, String var2, long var3) {
        Builder var5 = var1.shifted(-200, -1, -218);
        Schematic var6 = SchematicReader.resource("maps/instituto.schem");
        var5.paste(var6, 0, 0, 0);
        Iterator var7 = List.of("A", "B", "C", "P").iterator();

        while(var7.hasNext()) {
            String var8 = (String)var7.next();
            PlanPainter.paintInside(var5, InstitutoLayout.plan(var8), 1, new InstitutoStyle(var5, var8));
        }

        List var18 = InstitutoLayout.streetLamps(var6);
        InstitutoPlaces.outside(var5, var18);
        List var19 = InstitutoPlaces.gate(var5);
        Heightmap var9 = InstitutoLayout.ground();
        Decor.posts(var5, var9, InstitutoLayout.POSTS, InstitutoLayout::place, 19, 432);
        Decor.board(var5, InstitutoLayout.BOARD[0], 1, InstitutoLayout.BOARD[1], InstitutoLayout.PLACES, "IES LAGOA", "DE ANTELA");
        ArrayList var10 = new ArrayList(InstitutoLayout.NOTES);
        var10.addAll(InstitutoLayout.signs());
        Decor.notes(var5, var9, var10, InstitutoLayout::place);
        List var11 = Decor.pages(var5, var9, InstitutoLayout.PAGES, InstitutoLayout::place);
        ArrayList var12 = new ArrayList();
        int[][] var13 = InstitutoLayout.BATTERIES_OUT;
        int var14 = var13.length;

        int var15;
        int[] var16;
        for(var15 = 0; var15 < var14; ++var15) {
            var16 = var13[var15];
            var12.add(var5.abs(var16[0], 2, var16[1]));
        }

        var13 = InstitutoLayout.BATTERIES_IN;
        var14 = var13.length;

        for(var15 = 0; var15 < var14; ++var15) {
            var16 = var13[var15];
            var12.add(var5.abs(var16[1], 1 + var16[0] * 5 + 1, var16[2]));
        }

        ArrayList var20 = new ArrayList();
        int[][] var21 = InstitutoLayout.SPAWNS;
        var15 = var21.length;

        for(int var23 = 0; var23 < var15; ++var23) {
            int[] var17 = var21[var23];
            var20.add(var5.loc((double)var17[0], 2.0, (double)var17[1], 0.0F));
        }

        Ambience var22 = InstitutoLayout.ambience(var6).offset(var5.ox, var5.oy, var5.oz);
        return new ArenaMap(this.id(), this.displayName(), var2, var1.abs(0, 0, 0), var5.abs(InstitutoLayout.BOUNDS[0], -6, InstitutoLayout.BOUNDS[1]), var5.abs(InstitutoLayout.BOUNDS[2], 40, InstitutoLayout.BOUNDS[3]), (Loc)var20.get(0), var20, var11, var12, var19, "minecraft:red_concrete", var5.abs(InstitutoLayout.EXIT[0], 2, InstitutoLayout.EXIT[1]), var5.abs(InstitutoLayout.EXIT[2], 5, InstitutoLayout.EXIT[3]), var3, var22);
    }
}
