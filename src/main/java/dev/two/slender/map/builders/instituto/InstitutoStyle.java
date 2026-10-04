// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.instituto;

import java.util.List;
import dev.two.slender.map.gen.FloorPlan;
import java.util.Iterator;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.PlanStyle;

final class InstitutoStyle implements PlanStyle
{
    private final Builder b;
    private final String building;
    
    InstitutoStyle(final Builder b, final String building) {
        this.b = b;
        this.building = building;
    }
    
    static String stripe(final String s) {
        return switch (s) {
            case "A" -> "light_blue_terracotta";
            case "B" -> "lime_terracotta";
            case "C" -> "orange_terracotta";
            default -> null;
        };
    }
    
    static String floorFor(final String s, final String s2) {
        if (s2 == null) {
            return s.equals("P") ? "spruce_planks" : "polished_andesite";
        }
        return switch (s2) {
            case "aula",  "biblioteca",  "profes",  "despacho",  
                 "musica",  "plastica",  "info",  "salon",  
                 "conserje" -> "oak_planks";
            case "banos",  "vestuario",  "cocina" -> "white_concrete";
            case "comedor" -> "smooth_stone";
            default -> "light_gray_concrete";
        };
    }
    
    static String kindAt(final String anObject, final int n, final int n2, final int n3) {
        for (final InstitutoLayout.Room room : InstitutoLayout.ROOMS) {
            if (room.building().equals(anObject) && room.floor() == n && room.contains(n2, n3)) {
                return room.kind();
            }
        }
        return null;
    }
    
    @Override
    public String wall(final FloorPlan.Cell cell, final boolean b) {
        return this.b.pick("white_concrete", "white_concrete", "white_concrete", "light_gray_concrete");
    }
    
    @Override
    public String stripe(final FloorPlan.Cell cell) {
        return stripe(this.building);
    }
    
    @Override
    public String floor(final FloorPlan.Cell cell) {
        return floorFor(this.building, kindAt(this.building, cell.floor(), cell.x(), cell.z()));
    }
    
    @Override
    public String roof(final int n, final int n2) {
        return "light_gray_concrete";
    }
    
    @Override
    public String door() {
        return "birch_door";
    }
    
    @Override
    public String stairs() {
        return "polished_andesite_stairs";
    }
    
    @Override
    public List<String> furniture(final char c) {
        return switch (c) {
            case 'k' -> PlanStyle.stack("dark_oak_planks", "black_concrete", "black_concrete");
            case 'm' -> PlanStyle.stack("oak_fence", "oak_pressure_plate");
            case 'p' -> PlanStyle.stack("birch_slab[type=top]");
            case 'h' -> PlanStyle.stack("birch_stairs[facing=south,half=bottom,shape=straight]");
            case 'b' -> PlanStyle.stack("bookshelf", "bookshelf", "bookshelf");
            case 'r' -> PlanStyle.stack("white_concrete", "brewing_stand");
            case 'c' -> PlanStyle.stack("smoker[facing=south]");
            case 'f' -> PlanStyle.stack("polished_andesite");
            case 'w' -> PlanStyle.stack("cauldron");
            case 't' -> PlanStyle.stack("blue_terracotta", "blue_terracotta");
            case 'u' -> PlanStyle.stack("birch_slab[type=top]", "black_stained_glass_pane");
            case 'a' -> PlanStyle.stack("crafting_table");
            case 'A' -> PlanStyle.stack("anvil[facing=north]");
            case 'n' -> PlanStyle.stack("note_block");
            case 'z' -> PlanStyle.stack("loom[facing=south]");
            case 'y' -> PlanStyle.stack("barrel[facing=up]", "barrel[facing=up]");
            case 'e' -> PlanStyle.stack("spruce_planks");
            case 'j' -> PlanStyle.stack("spruce_slab[type=bottom]");
            case 'P' -> PlanStyle.stack("white_concrete", "white_concrete");
            case 'Q' -> PlanStyle.stack("air", "white_concrete");
            case 'g' -> PlanStyle.stack("smooth_quartz_stairs[facing=west,half=bottom,shape=straight]");
            case 'G' -> PlanStyle.stack("smooth_quartz", "smooth_quartz_stairs[facing=west,half=bottom,shape=straight]");
            default -> List.of();
        };
    }
}
