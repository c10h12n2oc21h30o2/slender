// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.asylum;

import java.util.List;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.PlanStyle;

final class AsylumStyle implements PlanStyle
{
    private final Builder b;
    
    AsylumStyle(final Builder b) {
        this.b = b;
    }
    
    static String ward(final int n, final int n2, final int n3) {
        if (n < 0) {
            return null;
        }
        if (n2 >= 10) {
            return "blue_terracotta";
        }
        if (n2 <= -10) {
            return "red_terracotta";
        }
        if (n3 <= -9) {
            return "green_terracotta";
        }
        return null;
    }
    
    static boolean chapel(final int n, final int a, final int n2) {
        return n == 1 && n2 >= 3 && Math.abs(a) <= 8;
    }
    
    @Override
    public String wall(final FloorPlan.Cell cell, final boolean b) {
        if (cell.floor() < 0) {
            return this.b.pick("deepslate_bricks", "deepslate_bricks", "cracked_deepslate_bricks", "cobbled_deepslate");
        }
        if (b) {
            return this.b.pick("stone_bricks", "stone_bricks", "stone_bricks", "cracked_stone_bricks", "mossy_stone_bricks");
        }
        return this.b.pick("white_terracotta", "white_terracotta", "white_terracotta", "light_gray_terracotta", "white_concrete");
    }
    
    @Override
    public String stripe(final FloorPlan.Cell cell) {
        return ward(cell.floor(), cell.x(), cell.z());
    }
    
    @Override
    public String floor(final FloorPlan.Cell cell) {
        if (cell.floor() < 0) {
            return this.b.pick("stone", "cobblestone", "andesite", "stone");
        }
        if (chapel(cell.floor(), cell.x(), cell.z())) {
            return "dark_oak_planks";
        }
        final boolean b = Math.floorMod(cell.x() + cell.z(), 2) == 0;
        if (this.b.chance(0.08)) {
            return "cracked_stone_bricks";
        }
        return b ? "light_gray_terracotta" : "white_terracotta";
    }
    
    @Override
    public String roof(final int n, final int n2) {
        return this.b.chance(0.9) ? "deepslate_tiles" : "cracked_deepslate_tiles";
    }
    
    @Override
    public String door() {
        return "dark_oak_door";
    }
    
    @Override
    public String stairs() {
        return "stone_brick_stairs";
    }
    
    @Override
    public List<String> furniture(final char c) {
        return switch (c) {
            case 'b' -> PlanStyle.stack("white_wool");
            case 't' -> PlanStyle.stack("oak_fence", "oak_pressure_plate");
            case 'r' -> PlanStyle.stack("polished_andesite");
            case 'k' -> PlanStyle.stack("blast_furnace[facing=south]");
            case 'm' -> PlanStyle.stack("smooth_stone", "smooth_stone");
            case 'c' -> PlanStyle.stack("barrel[facing=up]", "barrel[facing=up]");
            case 'p' -> PlanStyle.stack("dark_oak_stairs[facing=north,half=bottom,shape=straight]");
            case 'a' -> PlanStyle.stack("polished_andesite", "candle[candles=3,lit=false]");
            default -> List.of();
        };
    }
}
