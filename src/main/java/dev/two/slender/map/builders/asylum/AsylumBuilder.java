package dev.two.slender.map.builders.asylum;

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

public final class AsylumBuilder implements MapBuilder {
    public String id() {
        return "sanatorio";
    }

    public String displayName() {
        return "Sanatorio";
    }

    public Material icon() {
        return Material.IRON_BARS;
    }

    public int radius() {
        return 92;
    }

    public ArenaMap build(Builder var1, String var2, long var3) {
        Heightmap var5 = AsylumLayout.terrain(var3);
        Noise var6 = new Noise(var3 ^ 24301L);
        TerrainPainter.paint(var1, var5, (var3x, var4, var5x) -> {
            return AsylumNature.surface(var1, var5, var6, var3x, var4);
        });
        AsylumNature.plant(var1, var5, var6);
        List var7 = Decor.border(var1, var5, 76);
        Decor.exitRoad(var1, 76);
        AsylumPlaces.grounds(var1);
        PlanPainter.paint(var1, AsylumLayout.PLAN, 8, new AsylumStyle(var1));
        AsylumPlaces.tower(var1);
        Decor.posts(var1, var5, AsylumLayout.POSTS, AsylumLayout::place, 76);
        Decor.board(var1, AsylumLayout.PLACES, "SANATORIO DE", "VALDEMORA");
        Decor.notes(var1, var5, AsylumLayout.NOTES, AsylumLayout::place);
        Decor.notes(var1, var5, AsylumLayout.SIGNS, AsylumLayout::place);
        List var8 = Decor.pages(var1, var5, AsylumLayout.PAGES, AsylumLayout::place);
        ArrayList var9 = new ArrayList();
        int[][] var10 = AsylumLayout.BATTERIES_OUT;
        int var11 = var10.length;

        int var12;
        int[] var13;
        for(var12 = 0; var12 < var11; ++var12) {
            var13 = var10[var12];
            var9.add(var1.abs(var13[0], var5.at(var13[0], var13[1]) + 1, var13[1]));
        }

        var10 = AsylumLayout.BATTERIES_IN;
        var11 = var10.length;

        for(var12 = 0; var12 < var11; ++var12) {
            var13 = var10[var12];
            var9.add(var1.abs(var13[1], 8 + var13[0] * 5 + 1, var13[2]));
        }

        ArrayList var15 = new ArrayList();
        int[][] var16 = AsylumLayout.SPAWNS;
        var12 = var16.length;

        for(int var18 = 0; var18 < var12; ++var18) {
            int[] var14 = var16[var18];
            var15.add(var1.loc((double)var14[0], (double)(var5.at(var14[0], var14[1]) + 1), (double)var14[1], 180.0F));
        }

        Ambience var17 = AsylumLayout.ambience(var5).offset(var1.ox, var1.oy, var1.oz);
        return new ArenaMap(this.id(), this.displayName(), var2, var1.abs(0, 0, 0), var1.abs(-76, -8, -76), var1.abs(76, 45, 86), (Loc)var15.get(0), var15, var8, var9, var7, "minecraft:iron_bars[east=true,west=true]", var1.abs(-3, 1, 79), var1.abs(3, 4, 85), var3, var17);
    }
}
