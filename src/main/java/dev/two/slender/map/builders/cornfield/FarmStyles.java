// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.builders.cornfield;

import java.util.List;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.PlanStyle;

final class FarmStyles
{
    private FarmStyles() {
    }
    
    static final class House implements PlanStyle
    {
        private final Builder b;
        
        House(final Builder b) {
            this.b = b;
        }
        
        @Override
        public String wall(final FloorPlan.Cell cell, final boolean b) {
            if (b) {
                return this.b.pick("spruce_planks", "spruce_planks", "spruce_planks", "stripped_spruce_log[axis=y]");
            }
            return this.b.pick("birch_planks", "birch_planks", "stripped_birch_log[axis=y]");
        }
        
        @Override
        public String stripe(final FloorPlan.Cell cell) {
            return null;
        }
        
        @Override
        public String floor(final FloorPlan.Cell cell) {
            return (cell.floor() == 0) ? "oak_planks" : "spruce_planks";
        }
        
        @Override
        public String roof(final int n, final int n2) {
            return "spruce_planks";
        }
        
        @Override
        public String door() {
            return "spruce_door";
        }
        
        @Override
        public String stairs() {
            return "oak_stairs";
        }
        
        @Override
        public List<String> furniture(final char c) {
            return switch (c) {
                case 'k' -> PlanStyle.stack("furnace[facing=east]");
                case 'm' -> PlanStyle.stack("smooth_stone_slab[type=top]");
                case 't' -> PlanStyle.stack("oak_fence", "oak_pressure_plate");
                case 's' -> PlanStyle.stack("spruce_stairs[facing=north,half=bottom,shape=straight]");
                case 'c',  'u' -> PlanStyle.stack("cauldron");
                case 'r' -> PlanStyle.stack("bookshelf", "bookshelf");
                case 'b' -> PlanStyle.stack("white_wool");
                default -> List.of();
            };
        }
    }
    
    static final class Barn implements PlanStyle
    {
        private final Builder b;
        
        Barn(final Builder b) {
            this.b = b;
        }
        
        @Override
        public String wall(final FloorPlan.Cell cell, final boolean b) {
            return this.b.pick("red_terracotta", "red_terracotta", "red_terracotta", "brown_terracotta");
        }
        
        @Override
        public String stripe(final FloorPlan.Cell cell) {
            return null;
        }
        
        @Override
        public String floor(final FloorPlan.Cell cell) {
            if (cell.floor() > 0) {
                return "spruce_planks";
            }
            return this.b.pick("coarse_dirt", "coarse_dirt", "packed_mud", "spruce_planks");
        }
        
        @Override
        public String roof(final int n, final int n2) {
            return "dark_oak_planks";
        }
        
        @Override
        public String door() {
            return "spruce_door";
        }
        
        @Override
        public String stairs() {
            return "spruce_stairs";
        }
        
        @Override
        public List<String> furniture(final char c) {
            return switch (c) {
                case 'h' -> PlanStyle.stack("hay_block[axis=y]", "hay_block[axis=y]");
                case 'f' -> PlanStyle.stack("oak_fence");
                case 'w' -> PlanStyle.stack("cauldron");
                case 'c' -> PlanStyle.stack("barrel[facing=up]");
                default -> List.of();
            };
        }
    }
}
