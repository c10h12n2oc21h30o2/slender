package dev.two.slender.map.gen;

import java.util.List;

public interface PlanStyle {
    String wall(FloorPlan.Cell var1, boolean var2);

    String stripe(FloorPlan.Cell var1);

    String floor(FloorPlan.Cell var1);

    String roof(int var1, int var2);

    String door();

    String stairs();

    List<String> furniture(char var1);

    static List<String> stack(String... var0) {
        return List.of(var0);
    }
}
