package dev.two.slender.map.builders.asylum;

import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.gen.PlanStyle;
import java.util.List;

final class AsylumStyle implements PlanStyle {
    private final Builder b;

    AsylumStyle(Builder var1) {
        this.b = var1;
    }

    static String ward(int var0, int var1, int var2) {
        if (var0 < 0) {
            return null;
        } else if (var1 >= 10) {
            return "blue_terracotta";
        } else if (var1 <= -10) {
            return "red_terracotta";
        } else {
            return var2 <= -9 ? "green_terracotta" : null;
        }
    }

    static boolean chapel(int var0, int var1, int var2) {
        return var0 == 1 && var2 >= 3 && Math.abs(var1) <= 8;
    }

    public String wall(FloorPlan.Cell var1, boolean var2) {
        if (var1.floor() < 0) {
            return (String)this.b.pick("deepslate_bricks", "deepslate_bricks", "cracked_deepslate_bricks", "cobbled_deepslate");
        } else {
            return var2 ? (String)this.b.pick("stone_bricks", "stone_bricks", "stone_bricks", "cracked_stone_bricks", "mossy_stone_bricks") : (String)this.b.pick("white_terracotta", "white_terracotta", "white_terracotta", "light_gray_terracotta", "white_concrete");
        }
    }

    public String stripe(FloorPlan.Cell var1) {
        return ward(var1.floor(), var1.x(), var1.z());
    }

    public String floor(FloorPlan.Cell var1) {
        if (var1.floor() < 0) {
            return (String)this.b.pick("stone", "cobblestone", "andesite", "stone");
        } else if (chapel(var1.floor(), var1.x(), var1.z())) {
            return "dark_oak_planks";
        } else {
            boolean var2 = Math.floorMod(var1.x() + var1.z(), 2) == 0;
            if (this.b.chance(0.08)) {
                return "cracked_stone_bricks";
            } else {
                return var2 ? "light_gray_terracotta" : "white_terracotta";
            }
        }
    }

    public String roof(int var1, int var2) {
        return this.b.chance(0.9) ? "deepslate_tiles" : "cracked_deepslate_tiles";
    }

    public String door() {
        return "dark_oak_door";
    }

    public String stairs() {
        return "stone_brick_stairs";
    }

    public List<String> furniture(char var1) {
        List var10000;
        switch (var1) {
            case 'a':
                var10000 = PlanStyle.stack("polished_andesite", "candle[candles=3,lit=false]");
                break;
            case 'b':
                var10000 = PlanStyle.stack("white_wool");
                break;
            case 'c':
                var10000 = PlanStyle.stack("barrel[facing=up]", "barrel[facing=up]");
                break;
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'l':
            case 'n':
            case 'o':
            case 'q':
            case 's':
            default:
                var10000 = List.of();
                break;
            case 'k':
                var10000 = PlanStyle.stack("blast_furnace[facing=south]");
                break;
            case 'm':
                var10000 = PlanStyle.stack("smooth_stone", "smooth_stone");
                break;
            case 'p':
                var10000 = PlanStyle.stack("dark_oak_stairs[facing=north,half=bottom,shape=straight]");
                break;
            case 'r':
                var10000 = PlanStyle.stack("polished_andesite");
                break;
            case 't':
                var10000 = PlanStyle.stack("oak_fence", "oak_pressure_plate");
        }

        return var10000;
    }
}
