package dev.two.slender.map.builders.cornfield;

import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.gen.PlanStyle;
import java.util.List;

final class FarmStyles {
    private FarmStyles() {
    }

    static final class Barn implements PlanStyle {
        private final Builder b;

        Barn(Builder var1) {
            this.b = var1;
        }

        public String wall(FloorPlan.Cell var1, boolean var2) {
            return (String)this.b.pick("red_terracotta", "red_terracotta", "red_terracotta", "brown_terracotta");
        }

        public String stripe(FloorPlan.Cell var1) {
            return null;
        }

        public String floor(FloorPlan.Cell var1) {
            return var1.floor() > 0 ? "spruce_planks" : (String)this.b.pick("coarse_dirt", "coarse_dirt", "packed_mud", "spruce_planks");
        }

        public String roof(int var1, int var2) {
            return "dark_oak_planks";
        }

        public String door() {
            return "spruce_door";
        }

        public String stairs() {
            return "spruce_stairs";
        }

        public List<String> furniture(char var1) {
            List var10000;
            switch (var1) {
                case 'c' -> var10000 = PlanStyle.stack("barrel[facing=up]");
                case 'f' -> var10000 = PlanStyle.stack("oak_fence");
                case 'h' -> var10000 = PlanStyle.stack("hay_block[axis=y]", "hay_block[axis=y]");
                case 'w' -> var10000 = PlanStyle.stack("cauldron");
                default -> var10000 = List.of();
            }

            return var10000;
        }
    }

    static final class House implements PlanStyle {
        private final Builder b;

        House(Builder var1) {
            this.b = var1;
        }

        public String wall(FloorPlan.Cell var1, boolean var2) {
            return var2 ? (String)this.b.pick("spruce_planks", "spruce_planks", "spruce_planks", "stripped_spruce_log[axis=y]") : (String)this.b.pick("birch_planks", "birch_planks", "stripped_birch_log[axis=y]");
        }

        public String stripe(FloorPlan.Cell var1) {
            return null;
        }

        public String floor(FloorPlan.Cell var1) {
            return var1.floor() == 0 ? "oak_planks" : "spruce_planks";
        }

        public String roof(int var1, int var2) {
            return "spruce_planks";
        }

        public String door() {
            return "spruce_door";
        }

        public String stairs() {
            return "oak_stairs";
        }

        public List<String> furniture(char var1) {
            List var10000;
            switch (var1) {
                case 'b':
                    var10000 = PlanStyle.stack("white_wool");
                    break;
                case 'c':
                case 'u':
                    var10000 = PlanStyle.stack("cauldron");
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
                case 'p':
                case 'q':
                default:
                    var10000 = List.of();
                    break;
                case 'k':
                    var10000 = PlanStyle.stack("furnace[facing=east]");
                    break;
                case 'm':
                    var10000 = PlanStyle.stack("smooth_stone_slab[type=top]");
                    break;
                case 'r':
                    var10000 = PlanStyle.stack("bookshelf", "bookshelf");
                    break;
                case 's':
                    var10000 = PlanStyle.stack("spruce_stairs[facing=north,half=bottom,shape=straight]");
                    break;
                case 't':
                    var10000 = PlanStyle.stack("oak_fence", "oak_pressure_plate");
            }

            return var10000;
        }
    }
}
