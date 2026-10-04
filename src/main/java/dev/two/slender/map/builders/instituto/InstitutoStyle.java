package dev.two.slender.map.builders.instituto;

import dev.two.slender.map.Builder;
import dev.two.slender.map.gen.FloorPlan;
import dev.two.slender.map.gen.PlanStyle;
import java.util.Iterator;
import java.util.List;

final class InstitutoStyle implements PlanStyle {
    private final Builder b;
    private final String building;

    InstitutoStyle(Builder var1, String var2) {
        this.b = var1;
        this.building = var2;
    }

    static String stripe(String var0) {
        String var10000;
        switch (var0) {
            case "A" -> var10000 = "light_blue_terracotta";
            case "B" -> var10000 = "lime_terracotta";
            case "C" -> var10000 = "orange_terracotta";
            default -> var10000 = null;
        }

        return var10000;
    }

    static String floorFor(String var0, String var1) {
        if (var1 == null) {
            return var0.equals("P") ? "spruce_planks" : "polished_andesite";
        } else {
            String var10000;
            switch (var1) {
                case "aula":
                case "biblioteca":
                case "profes":
                case "despacho":
                case "musica":
                case "plastica":
                case "info":
                case "salon":
                case "conserje":
                    var10000 = "oak_planks";
                    break;
                case "banos":
                case "vestuario":
                case "cocina":
                    var10000 = "white_concrete";
                    break;
                case "comedor":
                    var10000 = "smooth_stone";
                    break;
                default:
                    var10000 = "light_gray_concrete";
            }

            return var10000;
        }
    }

    static String kindAt(String var0, int var1, int var2, int var3) {
        Iterator var4 = InstitutoLayout.ROOMS.iterator();

        InstitutoLayout.Room var5;
        do {
            if (!var4.hasNext()) {
                return null;
            }

            var5 = (InstitutoLayout.Room)var4.next();
        } while(!var5.building().equals(var0) || var5.floor() != var1 || !var5.contains(var2, var3));

        return var5.kind();
    }

    public String wall(FloorPlan.Cell var1, boolean var2) {
        return (String)this.b.pick("white_concrete", "white_concrete", "white_concrete", "light_gray_concrete");
    }

    public String stripe(FloorPlan.Cell var1) {
        return stripe(this.building);
    }

    public String floor(FloorPlan.Cell var1) {
        return floorFor(this.building, kindAt(this.building, var1.floor(), var1.x(), var1.z()));
    }

    public String roof(int var1, int var2) {
        return "light_gray_concrete";
    }

    public String door() {
        return "birch_door";
    }

    public String stairs() {
        return "polished_andesite_stairs";
    }

    public List<String> furniture(char var1) {
        List var10000;
        switch (var1) {
            case 'A':
                var10000 = PlanStyle.stack("anvil[facing=north]");
                break;
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'd':
            case 'i':
            case 'l':
            case 'o':
            case 'q':
            case 's':
            case 'v':
            case 'x':
            default:
                var10000 = List.of();
                break;
            case 'G':
                var10000 = PlanStyle.stack("smooth_quartz", "smooth_quartz_stairs[facing=west,half=bottom,shape=straight]");
                break;
            case 'P':
                var10000 = PlanStyle.stack("white_concrete", "white_concrete");
                break;
            case 'Q':
                var10000 = PlanStyle.stack("air", "white_concrete");
                break;
            case 'a':
                var10000 = PlanStyle.stack("crafting_table");
                break;
            case 'b':
                var10000 = PlanStyle.stack("bookshelf", "bookshelf", "bookshelf");
                break;
            case 'c':
                var10000 = PlanStyle.stack("smoker[facing=south]");
                break;
            case 'e':
                var10000 = PlanStyle.stack("spruce_planks");
                break;
            case 'f':
                var10000 = PlanStyle.stack("polished_andesite");
                break;
            case 'g':
                var10000 = PlanStyle.stack("smooth_quartz_stairs[facing=west,half=bottom,shape=straight]");
                break;
            case 'h':
                var10000 = PlanStyle.stack("birch_stairs[facing=south,half=bottom,shape=straight]");
                break;
            case 'j':
                var10000 = PlanStyle.stack("spruce_slab[type=bottom]");
                break;
            case 'k':
                var10000 = PlanStyle.stack("dark_oak_planks", "black_concrete", "black_concrete");
                break;
            case 'm':
                var10000 = PlanStyle.stack("oak_fence", "oak_pressure_plate");
                break;
            case 'n':
                var10000 = PlanStyle.stack("note_block");
                break;
            case 'p':
                var10000 = PlanStyle.stack("birch_slab[type=top]");
                break;
            case 'r':
                var10000 = PlanStyle.stack("white_concrete", "brewing_stand");
                break;
            case 't':
                var10000 = PlanStyle.stack("blue_terracotta", "blue_terracotta");
                break;
            case 'u':
                var10000 = PlanStyle.stack("birch_slab[type=top]", "black_stained_glass_pane");
                break;
            case 'w':
                var10000 = PlanStyle.stack("cauldron");
                break;
            case 'y':
                var10000 = PlanStyle.stack("barrel[facing=up]", "barrel[facing=up]");
                break;
            case 'z':
                var10000 = PlanStyle.stack("loom[facing=south]");
        }

        return var10000;
    }
}
