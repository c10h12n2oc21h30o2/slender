// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.TreeType;
import java.util.function.Function;
import org.bukkit.Bukkit;
import dev.two.slender.map.schem.Schematic;
import java.util.EnumMap;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import java.util.Map;
import java.util.Random;

public final class Builder
{
    private final BlockQueue q;
    public final int ox;
    public final int oy;
    public final int oz;
    public final Random rnd;
    private final Map<String, BlockData> byString;
    private final Map<Material, BlockData> byMaterial;
    
    public Builder(final BlockQueue q, final int ox, final int oy, final int oz, final Random rnd) {
        this.byString = new HashMap<String, BlockData>();
        this.byMaterial = new EnumMap<Material, BlockData>(Material.class);
        this.q = q;
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.rnd = rnd;
    }
    
    public Builder shifted(final int n, final int n2, final int n3) {
        return new Builder(this.q, this.ox + n, this.oy + n2, this.oz + n3, this.rnd);
    }
    
    public void paste(final Schematic schematic, final int n, final int n2, final int n3) {
        final BlockData[] array = new BlockData[schematic.palette().size()];
        for (int i = 0; i < array.length; ++i) {
            if (!schematic.paletteIsAir(i)) {
                array[i] = this.data(schematic.palette().get(i));
            }
        }
        this.q.add(BlockQueue.schematicJob(schematic, array, this.ox + n, this.oy + n2, this.oz + n3), schematic.solidCount());
    }
    
    public BlockData data(final String s) {
        return this.byString.computeIfAbsent(s.contains(":") ? s : ("minecraft:" + s), (Function<? super String, ? extends BlockData>)Bukkit::createBlockData);
    }
    
    public BlockData data(final Material key) {
        return this.byMaterial.computeIfAbsent(key, Material::createBlockData);
    }
    
    public void set(final int n, final int n2, final int n3, final BlockData blockData) {
        this.q.set(this.ox + n, this.oy + n2, this.oz + n3, blockData);
    }
    
    public void set(final int n, final int n2, final int n3, final Material material) {
        this.set(n, n2, n3, this.data(material));
    }
    
    public void set(final int n, final int n2, final int n3, final String s) {
        this.set(n, n2, n3, this.data(s));
    }
    
    public void fill(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final BlockData blockData) {
        for (int i = Math.min(n, n4); i <= Math.max(n, n4); ++i) {
            for (int j = Math.min(n2, n5); j <= Math.max(n2, n5); ++j) {
                for (int k = Math.min(n3, n6); k <= Math.max(n3, n6); ++k) {
                    this.set(i, j, k, blockData);
                }
            }
        }
    }
    
    public void fill(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Material material) {
        this.fill(n, n2, n3, n4, n5, n6, this.data(material));
    }
    
    public void fill(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final String s) {
        this.fill(n, n2, n3, n4, n5, n6, this.data(s));
    }
    
    public void air(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.fill(n, n2, n3, n4, n5, n6, Material.AIR);
    }
    
    public void walls(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Material material) {
        final int min = Math.min(n, n4);
        final int max = Math.max(n, n4);
        final int min2 = Math.min(n3, n6);
        final int max2 = Math.max(n3, n6);
        for (int i = Math.min(n2, n5); i <= Math.max(n2, n5); ++i) {
            for (int j = min; j <= max; ++j) {
                this.set(j, i, min2, material);
                this.set(j, i, max2, material);
            }
            for (int k = min2; k <= max2; ++k) {
                this.set(min, i, k, material);
                this.set(max, i, k, material);
            }
        }
    }
    
    public void wallsMixed(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Material... array) {
        final int min = Math.min(n, n4);
        final int max = Math.max(n, n4);
        final int min2 = Math.min(n3, n6);
        final int max2 = Math.max(n3, n6);
        for (int i = Math.min(n2, n5); i <= Math.max(n2, n5); ++i) {
            for (int j = min; j <= max; ++j) {
                this.set(j, i, min2, this.pick(array));
                this.set(j, i, max2, this.pick(array));
            }
            for (int k = min2; k <= max2; ++k) {
                this.set(min, i, k, this.pick(array));
                this.set(max, i, k, this.pick(array));
            }
        }
    }
    
    public void room(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Material material, final Material material2, final Material material3) {
        this.fill(n, n2, n3, n4, n2, n6, material2);
        this.fill(n, n5, n3, n4, n5, n6, material3);
        this.walls(n, n2 + 1, n3, n4, n5 - 1, n6, material);
        this.air(Math.min(n, n4) + 1, n2 + 1, Math.min(n3, n6) + 1, Math.max(n, n4) - 1, n5 - 1, Math.max(n3, n6) - 1);
    }
    
    public void cylinder(final int n, final int n2, final int n3, final double a, final int n4, final Material material, final boolean b) {
        for (int n5 = (int)Math.ceil(a), i = -n5; i <= n5; ++i) {
            for (int j = -n5; j <= n5; ++j) {
                final double sqrt = Math.sqrt(i * i + j * j);
                if (sqrt <= a + 0.3) {
                    if (!b || sqrt >= a - 0.7) {
                        for (int k = 0; k < n4; ++k) {
                            this.set(n + i, n2 + k, n3 + j, material);
                        }
                    }
                }
            }
        }
    }
    
    public void disc(final int n, final int n2, final int n3, final double n4, final Material material) {
        this.cylinder(n, n2, n3, n4, 1, material, false);
    }
    
    public void blob(final int n, final int n2, final int n3, final double a, final Material... array) {
        for (int n4 = (int)Math.ceil(a) + 1, i = -n4; i <= n4; ++i) {
            for (int j = -n4; j <= n4; ++j) {
                for (int k = -n4; k <= n4; ++k) {
                    if (Math.sqrt(i * i + j * j * 1.6 + k * k) + this.rnd.nextDouble() * 0.9 <= a && n2 + j >= 1) {
                        this.set(n + i, n2 + j, n3 + k, this.pick(array));
                    }
                }
            }
        }
    }
    
    public void path(final int n, final int n2, final int n3, final int n4, final int n5, final Material... array) {
        final double a = n3 - n;
        final double a2 = n4 - n2;
        final int n6 = (int)Math.max(Math.abs(a), Math.abs(a2));
        double max = 0.0;
        for (int i = 0; i <= n6; ++i) {
            final double n7 = (n6 == 0) ? 0.0 : (i / (double)n6);
            max = Math.max(-2.5, Math.min(2.5, max + (this.rnd.nextDouble() - 0.5) * 0.6));
            final int n8 = (int)Math.round(n + a * n7 + ((Math.abs(a2) > Math.abs(a)) ? max : 0.0));
            final int n9 = (int)Math.round(n2 + a2 * n7 + ((Math.abs(a) >= Math.abs(a2)) ? max : 0.0));
            for (int j = -n5 / 2; j <= n5 / 2; ++j) {
                for (int k = -n5 / 2; k <= n5 / 2; ++k) {
                    this.set(n8 + j, 0, n9 + k, this.pick(array));
                }
            }
        }
    }
    
    public void tree(final int n, final int n2, final int n3, final TreeType treeType) {
        this.q.add(BlockQueue.treeJob(this.ox + n, this.oy + n2, this.oz + n3, treeType, this.rnd.nextLong()), 60L);
    }
    
    public void clear(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.q.add(BlockQueue.clearJob(this.ox + Math.min(n, n4), this.oy + Math.min(n2, n5), this.oz + Math.min(n3, n6), this.ox + Math.max(n, n4), this.oy + Math.max(n2, n5), this.oz + Math.max(n3, n6)), (Math.abs(n4 - n) + 1) * (long)(Math.abs(n5 - n2) + 1) * (Math.abs(n6 - n3) + 1) / 64L);
    }
    
    public void door(final int n, final int n2, final int n3, final String s, final String s2) {
        this.set(n, n2, n3, s + "[facing=" + s2 + ",half=lower,hinge=left,open=false]");
        this.set(n, n2 + 1, n3, s + "[facing=" + s2 + ",half=upper,hinge=left,open=false]");
    }
    
    public void stairs(final int n, final int n2, final int n3, final String s, final String s2) {
        this.set(n, n2, n3, s + "[facing=" + s2 + ",half=bottom,shape=straight]");
    }
    
    public Loc lobbyRoom(final int n, final int n2, final int n3) {
        this.room(n - 4, n2, n3 - 4, n + 4, n2 + 5, n3 + 4, Material.DARK_OAK_PLANKS, Material.SPRUCE_PLANKS, Material.DARK_OAK_PLANKS);
        for (final int[] array2 : new int[][] { { -3, -3 }, { 3, -3 }, { -3, 3 }, { 3, 3 } }) {
            this.set(n + array2[0], n2 + 1, n3 + array2[1], "lantern[hanging=false]");
        }
        this.set(n, n2 + 4, n3, "lantern[hanging=true]");
        this.fill(n - 1, n2 + 1, n3 + 3, n + 1, n2 + 1, n3 + 3, Material.SPRUCE_SLAB);
        return this.loc(n, n2 + 1, n3, 180.0f);
    }
    
    public void sign(final int n, final int n2, final int n3, final String s, final DyeColor dyeColor, final String... elements) {
        this.q.add(BlockQueue.signJob(this.ox + n, this.oy + n2, this.oz + n3, this.data("dark_oak_wall_sign[facing=" + s), List.of(elements), dyeColor), 1L);
    }
    
    public BlockPos abs(final int n, final int n2, final int n3) {
        return new BlockPos(this.ox + n, this.oy + n2, this.oz + n3);
    }
    
    public Loc loc(final double n, final double n2, final double n3, final float yaw) {
        return new Loc(this.ox + n + 0.5, this.oy + n2, this.oz + n3 + 0.5, yaw);
    }
    
    public PageSpot page(final int n, final int n2, final int n3, final String facing, final String label) {
        return new PageSpot(this.abs(n, n2, n3), facing, label);
    }
    
    public PageSpot pageOnWall(final int n, final int n2, final int n3, final String s, final String s2) {
        int n4 = 0;
        int n5 = 0;
        switch (s) {
            case "NORTH": {
                n5 = -1;
                break;
            }
            case "SOUTH": {
                n5 = 1;
                break;
            }
            case "EAST": {
                n4 = 1;
                break;
            }
            case "WEST": {
                n4 = -1;
                break;
            }
            default: {
                throw new IllegalArgumentException("facing no v\u00e1lido: " + s);
            }
        }
        this.set(n + n4, n2, n3 + n5, Material.AIR);
        return this.page(n + n4, n2, n3 + n5, s, s2);
    }
    
    public boolean chance(final double n) {
        return this.rnd.nextDouble() < n;
    }
    
    public int range(final int n, final int n2) {
        return n + this.rnd.nextInt(n2 - n + 1);
    }
    
    @SafeVarargs
    public final <T> T pick(final T... array) {
        return array[this.rnd.nextInt(array.length)];
    }
}
