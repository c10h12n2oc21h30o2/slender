// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.gen;

import org.bukkit.Material;
import java.util.Iterator;
import java.util.List;
import dev.two.slender.map.Builder;

public final class PlanPainter
{
    private static final String WALLISH = "#WdD%";
    private static final String SHELL = " %E";
    
    private PlanPainter() {
    }
    
    public static void paint(final Builder builder, final FloorPlan floorPlan, final int n, final PlanStyle planStyle) {
        final List<FloorPlan.Stair> stairs = floorPlan.stairs();
        for (int i = floorPlan.lowest(); i <= floorPlan.highest(); ++i) {
            for (int j = floorPlan.minZ(); j <= floorPlan.maxZ(); ++j) {
                for (int k = floorPlan.minX(); k <= floorPlan.maxX(); ++k) {
                    final FloorPlan.Cell cell = new FloorPlan.Cell(i, k, j);
                    if (floorPlan.at(cell) != ' ') {
                        cell(builder, floorPlan, stairs, n, planStyle, cell);
                    }
                }
            }
        }
        final int storey = floorPlan.storey();
        for (int l = floorPlan.lowest(); l <= floorPlan.highest(); ++l) {
            for (int minZ = floorPlan.minZ(); minZ <= floorPlan.maxZ(); ++minZ) {
                for (int minX = floorPlan.minX(); minX <= floorPlan.maxX(); ++minX) {
                    final char at = floorPlan.at(l, minX, minZ);
                    if (at != ' ' && at != 'x' && floorPlan.at(l + 1, minX, minZ) == ' ') {
                        builder.set(minX, n + (l + 1) * storey, minZ, planStyle.roof(minX, minZ));
                    }
                }
            }
        }
        lamps(builder, floorPlan, n);
    }
    
    public static void paintInside(final Builder builder, final FloorPlan floorPlan, final int n, final PlanStyle planStyle) {
        final List<FloorPlan.Stair> stairs = floorPlan.stairs();
        for (int i = floorPlan.lowest(); i <= floorPlan.highest(); ++i) {
            for (int j = floorPlan.minZ(); j <= floorPlan.maxZ(); ++j) {
                for (int k = floorPlan.minX(); k <= floorPlan.maxX(); ++k) {
                    final FloorPlan.Cell cell = new FloorPlan.Cell(i, k, j);
                    if (" %E".indexOf(floorPlan.at(cell)) < 0) {
                        cell(builder, floorPlan, stairs, n, planStyle, cell);
                    }
                }
            }
        }
        lamps(builder, floorPlan, n);
    }
    
    private static void lamps(final Builder builder, final FloorPlan floorPlan, final int n) {
        for (final FloorPlan.Cell cell : floorPlan.cells('L')) {
            builder.set(cell.x(), n + floorPlan.lampDy(cell.floor()), cell.z(), "redstone_lamp[lit=true]");
        }
    }
    
    private static void cell(final Builder builder, final FloorPlan floorPlan, final List<FloorPlan.Stair> list, final int n, final PlanStyle planStyle, final FloorPlan.Cell cell) {
        final int storey = floorPlan.storey();
        final int x = cell.x();
        final int z = cell.z();
        final int n2 = n + cell.floor() * storey;
        final char at = floorPlan.at(cell);
        if (at != 'o') {
            builder.set(x, n2, z, planStyle.floor(cell));
        }
        for (int i = 1; i < storey; ++i) {
            builder.set(x, n2 + i, z, Material.AIR);
        }
        final boolean outer = outer(floorPlan, cell);
        switch (at) {
            case 35: {
                for (int j = 1; j < storey; ++j) {
                    builder.set(x, n2 + j, z, planStyle.wall(cell, outer));
                }
                final String s = outer ? null : planStyle.stripe(cell);
                if (s != null) {
                    builder.set(x, n2 + 2, z, s);
                }
                break;
            }
            case 87: {
                final String s2 = "glass_pane[" + (alongX(floorPlan, cell) ? "east=true,west=true" : "north=true,south=true");
                for (int k = 1; k < storey; ++k) {
                    if (k != 2 && k != 3) {
                        builder.set(x, n2 + k, z, planStyle.wall(cell, outer));
                    }
                    else if (!builder.chance(0.35)) {
                        builder.set(x, n2 + k, z, s2);
                    }
                }
                break;
            }
            case 68: {
                for (int l = 3; l < storey; ++l) {
                    builder.set(x, n2 + l, z, planStyle.wall(cell, outer));
                }
                break;
            }
            case 100: {
                builder.door(x, n2 + 1, z, planStyle.door(), doorFacing(floorPlan, cell));
                for (int n3 = 3; n3 < storey; ++n3) {
                    builder.set(x, n2 + n3, z, planStyle.wall(cell, outer));
                }
                break;
            }
            case 126: {
                builder.set(x, n2 + 1, z, Material.WATER);
                break;
            }
            case 120: {
                if (builder.chance(0.7)) {
                    builder.set(x, n2 + 1, z, builder.pick("cobblestone", "gravel", "andesite", "cobblestone_slab[type=bottom]"));
                    break;
                }
                break;
            }
            case 46:
            case 69:
            case 76:
            case 111: {
                break;
            }
            default: {
                final FloorPlan.Stair stair = stairAt(list, cell);
                if (stair != null) {
                    final int index = stair.index(x, z);
                    for (int n4 = 1; n4 <= index; ++n4) {
                        builder.set(x, n2 + n4, z, planStyle.wall(cell, false));
                    }
                    builder.set(x, n2 + 1 + index, z, planStyle.stairs() + "[facing=" + facing(stair) + ",half=bottom,shape=straight]");
                    for (int n5 = index + 2; n5 <= storey; ++n5) {
                        builder.set(x, n2 + n5, z, Material.AIR);
                    }
                    break;
                }
                final List<String> furniture = planStyle.furniture(at);
                for (int n6 = 0; n6 < furniture.size() && n6 < storey - 1; ++n6) {
                    builder.set(x, n2 + 1 + n6, z, (String)furniture.get(n6));
                }
                break;
            }
        }
    }
    
    static boolean outer(final FloorPlan floorPlan, final FloorPlan.Cell cell) {
        return floorPlan.at(cell.floor(), cell.x() + 1, cell.z()) == ' ' || floorPlan.at(cell.floor(), cell.x() - 1, cell.z()) == ' ' || floorPlan.at(cell.floor(), cell.x(), cell.z() + 1) == ' ' || floorPlan.at(cell.floor(), cell.x(), cell.z() - 1) == ' ';
    }
    
    static boolean alongX(final FloorPlan floorPlan, final FloorPlan.Cell cell) {
        return "#WdD%".indexOf(floorPlan.at(cell.floor(), cell.x() - 1, cell.z())) >= 0 || "#WdD%".indexOf(floorPlan.at(cell.floor(), cell.x() + 1, cell.z())) >= 0;
    }
    
    static String doorFacing(final FloorPlan floorPlan, final FloorPlan.Cell cell) {
        return alongX(floorPlan, cell) ? "south" : "east";
    }
    
    static String facing(final FloorPlan.Stair stair) {
        if (stair.dx() == 1) {
            return "east";
        }
        if (stair.dx() == -1) {
            return "west";
        }
        return (stair.dz() == -1) ? "north" : "south";
    }
    
    private static FloorPlan.Stair stairAt(final List<FloorPlan.Stair> list, final FloorPlan.Cell cell) {
        for (final FloorPlan.Stair stair : list) {
            if (stair.floor() == cell.floor() && stair.contains(cell.x(), cell.z())) {
                return stair;
            }
        }
        return null;
    }
}
