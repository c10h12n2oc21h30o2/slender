package dev.two.slender.map;

import dev.two.slender.map.schem.Schematic;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.data.BlockData;

public final class Builder {
    private final BlockQueue q;
    public final int ox;
    public final int oy;
    public final int oz;
    public final Random rnd;
    private final Map<String, BlockData> byString = new HashMap();
    private final Map<Material, BlockData> byMaterial = new EnumMap(Material.class);

    public Builder(BlockQueue var1, int var2, int var3, int var4, Random var5) {
        this.q = var1;
        this.ox = var2;
        this.oy = var3;
        this.oz = var4;
        this.rnd = var5;
    }

    public Builder shifted(int var1, int var2, int var3) {
        return new Builder(this.q, this.ox + var1, this.oy + var2, this.oz + var3, this.rnd);
    }

    public void paste(Schematic var1, int var2, int var3, int var4) {
        BlockData[] var5 = new BlockData[var1.palette().size()];

        for(int var6 = 0; var6 < var5.length; ++var6) {
            if (!var1.paletteIsAir(var6)) {
                var5[var6] = this.data((String)var1.palette().get(var6));
            }
        }

        this.q.add(BlockQueue.schematicJob(var1, var5, this.ox + var2, this.oy + var3, this.oz + var4), (long)var1.solidCount());
    }

    /** Aplica los extras de un schematic pegado en (dx, dy, dz) relativo a este builder. Un fallo se avisa y se sigue. */
    public void extras(dev.two.slender.map.schem.SchematicExtras e, int dx, int dy, int dz) {
        int bx = this.ox + dx;
        int by = this.oy + dy;
        int bz = this.oz + dz;
        for (dev.two.slender.map.schem.SchematicExtras.SignText s : e.signs()) {
            this.q.add(BlockQueue.worldJob(w -> safely("cartel", () -> ExtrasApplier.sign(w, bx, by, bz, s))), 1L);
        }
        for (dev.two.slender.map.schem.SchematicExtras.BannerData b : e.banners()) {
            this.q.add(BlockQueue.worldJob(w -> safely("estandarte", () -> ExtrasApplier.banner(w, bx, by, bz, b))), 1L);
        }
        for (dev.two.slender.map.schem.SchematicExtras.HeadData h : e.heads()) {
            this.q.add(BlockQueue.worldJob(w -> safely("cabeza", () -> ExtrasApplier.head(w, bx, by, bz, h))), 1L);
        }
        for (dev.two.slender.map.schem.SchematicExtras.PaintingData p : e.paintings()) {
            this.q.add(BlockQueue.worldJob(w -> safely("cuadro", () -> ExtrasApplier.painting(w, bx, by, bz, p))), 1L);
        }
    }

    /** Bloque de adorno sin colisión (BlockDisplay) en (x, y, z), con una etiqueta de scoreboard. Bedrock no lo ve. */
    public void display(int x, int y, int z, String blockState, String tag) {
        org.bukkit.block.data.BlockData d = this.data(blockState);
        int ax = this.ox + x;
        int ay = this.oy + y;
        int az = this.oz + z;
        this.q.add(BlockQueue.worldJob(w -> safely("bloque decorativo", () -> w.spawn(new org.bukkit.Location(w, ax, ay, az),
                org.bukkit.entity.BlockDisplay.class, e -> {
                    e.setBlock(d);
                    e.addScoreboardTag(tag);
                    e.setPersistent(true);
                }))), 1L);
    }

    private static void safely(String what, Runnable r) {
        try {
            r.run();
        } catch (RuntimeException ex) {
            LOG.warning("No se pudo poner un " + what + ": " + ex.getMessage());
        }
    }

    public BlockData data(String var1) {
        String var2 = var1.contains(":") ? var1 : "minecraft:" + var1;
        return (BlockData)this.byString.computeIfAbsent(var2, s -> SafeBlockData.resolve(s, Bukkit::createBlockData,
                Material.STONE.createBlockData(), WARNED, msg -> LOG.warning(msg)));
    }

    private static final java.util.Set<String> WARNED = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger("Slender");

    public BlockData data(Material var1) {
        return (BlockData)this.byMaterial.computeIfAbsent(var1, Material::createBlockData);
    }

    public void set(int var1, int var2, int var3, BlockData var4) {
        this.q.set(this.ox + var1, this.oy + var2, this.oz + var3, var4);
    }

    public void set(int var1, int var2, int var3, Material var4) {
        this.set(var1, var2, var3, this.data(var4));
    }

    public void set(int var1, int var2, int var3, String var4) {
        this.set(var1, var2, var3, this.data(var4));
    }

    public void fill(int var1, int var2, int var3, int var4, int var5, int var6, BlockData var7) {
        for(int var8 = Math.min(var1, var4); var8 <= Math.max(var1, var4); ++var8) {
            for(int var9 = Math.min(var2, var5); var9 <= Math.max(var2, var5); ++var9) {
                for(int var10 = Math.min(var3, var6); var10 <= Math.max(var3, var6); ++var10) {
                    this.set(var8, var9, var10, var7);
                }
            }
        }

    }

    public void fill(int var1, int var2, int var3, int var4, int var5, int var6, Material var7) {
        this.fill(var1, var2, var3, var4, var5, var6, this.data(var7));
    }

    public void fill(int var1, int var2, int var3, int var4, int var5, int var6, String var7) {
        this.fill(var1, var2, var3, var4, var5, var6, this.data(var7));
    }

    public void air(int var1, int var2, int var3, int var4, int var5, int var6) {
        this.fill(var1, var2, var3, var4, var5, var6, Material.AIR);
    }

    public void walls(int var1, int var2, int var3, int var4, int var5, int var6, Material var7) {
        int var8 = Math.min(var1, var4);
        int var9 = Math.max(var1, var4);
        int var10 = Math.min(var3, var6);
        int var11 = Math.max(var3, var6);

        for(int var12 = Math.min(var2, var5); var12 <= Math.max(var2, var5); ++var12) {
            int var13;
            for(var13 = var8; var13 <= var9; ++var13) {
                this.set(var13, var12, var10, var7);
                this.set(var13, var12, var11, var7);
            }

            for(var13 = var10; var13 <= var11; ++var13) {
                this.set(var8, var12, var13, var7);
                this.set(var9, var12, var13, var7);
            }
        }

    }

    public void wallsMixed(int var1, int var2, int var3, int var4, int var5, int var6, Material... var7) {
        int var8 = Math.min(var1, var4);
        int var9 = Math.max(var1, var4);
        int var10 = Math.min(var3, var6);
        int var11 = Math.max(var3, var6);

        for(int var12 = Math.min(var2, var5); var12 <= Math.max(var2, var5); ++var12) {
            int var13;
            for(var13 = var8; var13 <= var9; ++var13) {
                this.set(var13, var12, var10, (Material)this.pick(var7));
                this.set(var13, var12, var11, (Material)this.pick(var7));
            }

            for(var13 = var10; var13 <= var11; ++var13) {
                this.set(var8, var12, var13, (Material)this.pick(var7));
                this.set(var9, var12, var13, (Material)this.pick(var7));
            }
        }

    }

    public void room(int var1, int var2, int var3, int var4, int var5, int var6, Material var7, Material var8, Material var9) {
        this.fill(var1, var2, var3, var4, var2, var6, var8);
        this.fill(var1, var5, var3, var4, var5, var6, var9);
        this.walls(var1, var2 + 1, var3, var4, var5 - 1, var6, var7);
        this.air(Math.min(var1, var4) + 1, var2 + 1, Math.min(var3, var6) + 1, Math.max(var1, var4) - 1, var5 - 1, Math.max(var3, var6) - 1);
    }

    public void cylinder(int var1, int var2, int var3, double var4, int var6, Material var7, boolean var8) {
        int var9 = (int)Math.ceil(var4);

        for(int var10 = -var9; var10 <= var9; ++var10) {
            for(int var11 = -var9; var11 <= var9; ++var11) {
                double var12 = Math.sqrt((double)(var10 * var10 + var11 * var11));
                if (!(var12 > var4 + 0.3) && (!var8 || !(var12 < var4 - 0.7))) {
                    for(int var14 = 0; var14 < var6; ++var14) {
                        this.set(var1 + var10, var2 + var14, var3 + var11, var7);
                    }
                }
            }
        }

    }

    public void disc(int var1, int var2, int var3, double var4, Material var6) {
        this.cylinder(var1, var2, var3, var4, 1, var6, false);
    }

    public void blob(int var1, int var2, int var3, double var4, Material... var6) {
        int var7 = (int)Math.ceil(var4) + 1;

        for(int var8 = -var7; var8 <= var7; ++var8) {
            for(int var9 = -var7; var9 <= var7; ++var9) {
                for(int var10 = -var7; var10 <= var7; ++var10) {
                    double var11 = Math.sqrt((double)(var8 * var8) + (double)(var9 * var9) * 1.6 + (double)(var10 * var10)) + this.rnd.nextDouble() * 0.9;
                    if (var11 <= var4 && var2 + var9 >= 1) {
                        this.set(var1 + var8, var2 + var9, var3 + var10, (Material)this.pick(var6));
                    }
                }
            }
        }

    }

    public void path(int var1, int var2, int var3, int var4, int var5, Material... var6) {
        double var7 = (double)(var3 - var1);
        double var9 = (double)(var4 - var2);
        int var11 = (int)Math.max(Math.abs(var7), Math.abs(var9));
        double var12 = 0.0;

        for(int var14 = 0; var14 <= var11; ++var14) {
            double var15 = var11 == 0 ? 0.0 : (double)var14 / (double)var11;
            var12 = Math.max(-2.5, Math.min(2.5, var12 + (this.rnd.nextDouble() - 0.5) * 0.6));
            int var17 = (int)Math.round((double)var1 + var7 * var15 + (Math.abs(var9) > Math.abs(var7) ? var12 : 0.0));
            int var18 = (int)Math.round((double)var2 + var9 * var15 + (Math.abs(var7) >= Math.abs(var9) ? var12 : 0.0));

            for(int var19 = -var5 / 2; var19 <= var5 / 2; ++var19) {
                for(int var20 = -var5 / 2; var20 <= var5 / 2; ++var20) {
                    this.set(var17 + var19, 0, var18 + var20, (Material)((Material)this.pick(var6)));
                }
            }
        }

    }

    public void tree(int var1, int var2, int var3, TreeType var4) {
        this.q.add(BlockQueue.treeJob(this.ox + var1, this.oy + var2, this.oz + var3, var4, this.rnd.nextLong()), 60L);
    }

    public void clear(int var1, int var2, int var3, int var4, int var5, int var6) {
        long var7 = (long)(Math.abs(var4 - var1) + 1) * (long)(Math.abs(var5 - var2) + 1) * (long)(Math.abs(var6 - var3) + 1);
        this.q.add(BlockQueue.clearJob(this.ox + Math.min(var1, var4), this.oy + Math.min(var2, var5), this.oz + Math.min(var3, var6), this.ox + Math.max(var1, var4), this.oy + Math.max(var2, var5), this.oz + Math.max(var3, var6)), var7 / 64L);
    }

    public void door(int var1, int var2, int var3, String var4, String var5) {
        this.set(var1, var2, var3, var4 + "[facing=" + var5 + ",half=lower,hinge=left,open=false]");
        this.set(var1, var2 + 1, var3, var4 + "[facing=" + var5 + ",half=upper,hinge=left,open=false]");
    }

    public void stairs(int var1, int var2, int var3, String var4, String var5) {
        this.set(var1, var2, var3, var4 + "[facing=" + var5 + ",half=bottom,shape=straight]");
    }

    public Loc lobbyRoom(int var1, int var2, int var3) {
        this.room(var1 - 4, var2, var3 - 4, var1 + 4, var2 + 5, var3 + 4, Material.DARK_OAK_PLANKS, Material.SPRUCE_PLANKS, Material.DARK_OAK_PLANKS);
        int[][] var4 = new int[][]{{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            int[] var7 = var4[var6];
            this.set(var1 + var7[0], var2 + 1, var3 + var7[1], "lantern[hanging=false]");
        }

        this.set(var1, var2 + 4, var3, "lantern[hanging=true]");
        this.fill(var1 - 1, var2 + 1, var3 + 3, var1 + 1, var2 + 1, var3 + 3, Material.SPRUCE_SLAB);
        return this.loc((double)var1, (double)(var2 + 1), (double)var3, 180.0F);
    }

    public void sign(int var1, int var2, int var3, String var4, DyeColor var5, String... var6) {
        this.q.add(BlockQueue.signJob(this.ox + var1, this.oy + var2, this.oz + var3, this.data("dark_oak_wall_sign[facing=" + var4 + "]"), List.of(var6), var5), 1L);
    }

    public BlockPos abs(int var1, int var2, int var3) {
        return new BlockPos(this.ox + var1, this.oy + var2, this.oz + var3);
    }

    public Loc loc(double var1, double var3, double var5, float var7) {
        return new Loc((double)this.ox + var1 + 0.5, (double)this.oy + var3, (double)this.oz + var5 + 0.5, var7);
    }

    public PageSpot page(int var1, int var2, int var3, String var4, String var5) {
        return new PageSpot(this.abs(var1, var2, var3), var4, var5);
    }

    public PageSpot pageOnWall(int var1, int var2, int var3, String var4, String var5) {
        byte var6 = 0;
        byte var7 = 0;
        switch (var4) {
            case "NORTH" -> var7 = -1;
            case "SOUTH" -> var7 = 1;
            case "EAST" -> var6 = 1;
            case "WEST" -> var6 = -1;
            default -> throw new IllegalArgumentException("facing no válido: " + var4);
        }

        this.set(var1 + var6, var2, var3 + var7, Material.AIR);
        return this.page(var1 + var6, var2, var3 + var7, var4, var5);
    }

    public boolean chance(double var1) {
        return this.rnd.nextDouble() < var1;
    }

    public int range(int var1, int var2) {
        return var1 + this.rnd.nextInt(var2 - var1 + 1);
    }

    @SafeVarargs
    public final <T> T pick(T... var1) {
        return var1[this.rnd.nextInt(var1.length)];
    }
}
