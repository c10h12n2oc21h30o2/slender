package dev.two.slender.map.builders.cornfield;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.PlanPainter;
import dev.two.slender.map.gen.TerrainPainter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public final class CornfieldBuilder implements MapBuilder {
    public String id() {
        return "maizal";
    }

    public String displayName() {
        return "Maizal";
    }

    public Material icon() {
        return Material.HAY_BLOCK;
    }

    public int radius() {
        return 92;
    }

    public ArenaMap build(Builder var1, String var2, long var3) {
        Heightmap var5 = CornfieldLayout.terrain(var3);
        boolean[][] var6 = CornfieldLayout.maze(var3);
        Noise var7 = new Noise(var3 ^ 24301L);
        List var8 = CornfieldLayout.poles(var5);
        TerrainPainter.paint(var1, var5, (var3x, var4, var5x) -> {
            return CornfieldNature.surface(var1, var5, var6, var3x, var4);
        });
        CornfieldNature.corn(var1, var5, var6);
        CornfieldNature.plant(var1, var5, var7, var8);
        List var9 = Decor.border(var1, var5, 76);
        Decor.exitRoad(var1, 76);
        CornfieldPlaces.build(var1, var5, var8);
        int var10 = var5.at(CornfieldLayout.place("casa").x(), CornfieldLayout.place("casa").z());
        int var11 = var5.at(CornfieldLayout.place("granero").x(), CornfieldLayout.place("granero").z());
        PlanPainter.paint(var1, CornfieldLayout.CASA, var10, new FarmStyles.House(var1));
        PlanPainter.paint(var1, CornfieldLayout.GRANERO, var11, new FarmStyles.Barn(var1));
        CornfieldPlaces.roofs(var1, var5);
        Decor.posts(var1, var5, CornfieldLayout.POSTS, CornfieldLayout::place, 76);
        Decor.board(var1, CornfieldLayout.PLACES, "GRANJA", "ROLDÁN");
        ArrayList var12 = new ArrayList(CornfieldLayout.NOTES);
        var12.add(CornfieldLayout.HOUSE_SIGN);
        Decor.notes(var1, var5, var12, CornfieldLayout::place);
        List var13 = Decor.pages(var1, var5, CornfieldLayout.PAGES, CornfieldLayout::place);
        ArrayList var14 = new ArrayList();
        int[][] var15 = CornfieldLayout.BATTERIES_OUT;
        int var16 = var15.length;

        int var17;
        int[] var18;
        for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            var14.add(var1.abs(var18[0], var5.at(var18[0], var18[1]) + 1, var18[1]));
        }

        var15 = CornfieldLayout.BATTERIES_CASA;
        var16 = var15.length;

        for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            var14.add(var1.abs(var18[1], var10 + var18[0] * CornfieldLayout.CASA.storey() + 1, var18[2]));
        }

        var15 = CornfieldLayout.BATTERIES_GRANERO;
        var16 = var15.length;

        for(var17 = 0; var17 < var16; ++var17) {
            var18 = var15[var17];
            var14.add(var1.abs(var18[1], var11 + var18[0] * CornfieldLayout.GRANERO.storey() + 1, var18[2]));
        }

        ArrayList var20 = new ArrayList();
        int[][] var21 = CornfieldLayout.SPAWNS;
        var17 = var21.length;

        for(int var23 = 0; var23 < var17; ++var23) {
            int[] var19 = var21[var23];
            var20.add(var1.loc((double)var19[0], (double)(var5.at(var19[0], var19[1]) + 1), (double)var19[1], 180.0F));
        }

        Ambience var22 = CornfieldLayout.ambience(var5).offset(var1.ox, var1.oy, var1.oz);
        return new ArenaMap(this.id(), this.displayName(), var2, var1.abs(0, 0, 0), var1.abs(-76, -8, -76), var1.abs(76, 45, 86), (Loc)var20.get(0), var20, var13, var14, var9, "minecraft:iron_bars[east=true,west=true]", var1.abs(-3, 1, 79), var1.abs(3, 4, 85), var3, var22);
    }
}
