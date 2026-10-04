package dev.two.slender.map.builders.forest;

import dev.two.slender.logic.Heightmap;
import dev.two.slender.logic.Noise;
import dev.two.slender.map.Ambience;
import dev.two.slender.map.ArenaMap;
import dev.two.slender.map.Builder;
import dev.two.slender.map.Loc;
import dev.two.slender.map.MapBuilder;
import dev.two.slender.map.gen.Decor;
import dev.two.slender.map.gen.TerrainPainter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public final class ForestBuilder implements MapBuilder {
    private static final int R = 80;

    public String id() {
        return "bosque";
    }

    public String displayName() {
        return "Bosque";
    }

    public Material icon() {
        return Material.SPRUCE_SAPLING;
    }

    public int radius() {
        return 96;
    }

    public ArenaMap build(Builder var1, String var2, long var3) {
        Heightmap var5 = ForestLayout.terrain(var3);
        Noise var6 = new Noise(var3 ^ 24301L);
        TerrainPainter.paint(var1, var5, (var3x, var4, var5x) -> {
            return ForestNature.surface(var1, var5, var6, var3x, var4);
        });
        ForestNature.plant(var1, var5, var6);
        List var7 = Decor.border(var1, var5, 80);
        Decor.exitRoad(var1, 80);
        ForestPlaces.build(var1, var5);
        Decor.posts(var1, var5, ForestLayout.POSTS, ForestLayout::place, 80);
        Decor.board(var1, ForestLayout.PLACES, "BOSQUE DE", "VALDEMORA");
        Decor.notes(var1, var5, ForestLayout.NOTES, ForestLayout::place);
        List var8 = Decor.pages(var1, var5, ForestLayout.PAGES, ForestLayout::place);
        ArrayList var9 = new ArrayList();
        int[][] var10 = ForestLayout.BATTERIES;
        int var11 = var10.length;

        int var12;
        for(var12 = 0; var12 < var11; ++var12) {
            int[] var13 = var10[var12];
            var9.add(var1.abs(var13[0], var5.at(var13[0], var13[1]) + 1, var13[1]));
        }

        ArrayList var15 = new ArrayList();
        int[][] var16 = ForestLayout.SPAWNS;
        var12 = var16.length;

        for(int var18 = 0; var18 < var12; ++var18) {
            int[] var14 = var16[var18];
            var15.add(var1.loc((double)var14[0], (double)(var5.at(var14[0], var14[1]) + 1), (double)var14[1], 180.0F));
        }

        Ambience var17 = ForestLayout.ambience(var5).offset(var1.ox, var1.oy, var1.oz);
        return new ArenaMap(this.id(), this.displayName(), var2, var1.abs(0, 0, 0), var1.abs(-80, -8, -80), var1.abs(80, 45, 90), (Loc)var15.get(0), var15, var8, var9, var7, "minecraft:iron_bars[east=true,west=true]", var1.abs(-3, 1, 83), var1.abs(3, 4, 89), var3, var17);
    }
}
