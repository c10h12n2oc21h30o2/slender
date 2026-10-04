package dev.two.slender.map;

import dev.two.slender.map.schem.Schematic;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public final class BlockQueue {
    private static final int CHUNK = 4096;
    private final ArrayDeque<Job> jobs = new ArrayDeque();
    private Placement current;
    private long total;
    private long done;

    public void set(int var1, int var2, int var3, BlockData var4) {
        if (this.current == null || this.current.size == 4096) {
            this.current = new Placement();
            this.jobs.add(this.current);
        }

        this.current.add(var1, var2, var3, var4);
        ++this.total;
    }

    public void add(Job var1, long var2) {
        this.current = null;
        this.jobs.add(var1);
        this.total += var2;
    }

    public boolean tick(World var1, int var2, long var3) {
        Budget var5 = new Budget(System.nanoTime() + var3, var2);

        while(!this.jobs.isEmpty() && !var5.spent()) {
            Job var6 = (Job)this.jobs.peek();
            if (var6.run(var1, var5)) {
                this.jobs.poll();
            }
        }

        this.done += (long)var5.used;
        return this.jobs.isEmpty();
    }

    public double progress() {
        return this.total == 0L ? 1.0 : Math.min(1.0, (double)this.done / (double)this.total);
    }

    /** Trabajo de una sola vez sobre el mundo (carteles, estandartes, cuadros…), después de los bloques encolados antes. */
    public static Job worldJob(java.util.function.Consumer<World> action) {
        return (world, budget) -> {
            budget.use(1);
            action.accept(world);
            return true;
        };
    }

    public static Job clearJob(final int var0, final int var1, final int var2, final int var3, final int var4, final int var5) {
        return new Job() {
            int x = var0;
            int y = var1;
            int z = var2;
            boolean entitiesCleared;
            int checks;

            public boolean run(World var1x, Budget var2x) {
                if (!this.entitiesCleared) {
                    Iterator var3x = var1x.getNearbyEntities(new BoundingBox((double)var0, (double)var1, (double)var2, (double)(var3 + 1), (double)(var4 + 1), (double)(var5 + 1))).iterator();

                    while(var3x.hasNext()) {
                        Entity var4x = (Entity)var3x.next();
                        if (!(var4x instanceof Player)) {
                            var4x.remove();
                        }
                    }

                    this.entitiesCleared = true;
                }

                while(!var2x.spent()) {
                    Block var5x = var1x.getBlockAt(this.x, this.y, this.z);
                    if (!var5x.getType().isAir()) {
                        var5x.setType(Material.AIR, false);
                        var2x.use(1);
                    }

                    if (++this.checks % 64 == 0) {
                        var2x.use(1);
                    }

                    if (++this.y > var4) {
                        this.y = var1;
                        if (++this.z > var5) {
                            this.z = var2;
                            if (++this.x > var3) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        };
    }

    public static Job schematicJob(final Schematic var0, final BlockData[] var1, final int var2, final int var3, final int var4) {
        return new Job() {
            int i;
            int skipped;
            final int w = var0.width();
            final int layer = var0.width() * var0.length();
            final int n = var0.size();

            public boolean run(World var1x, Budget var2x) {
                for(; this.i < this.n && !var2x.spent(); ++this.i) {
                    BlockData var3x = var1[var0.paletteAt(this.i)];
                    if (var3x != null) {
                        int var4x = this.i / this.layer;
                        int var5 = this.i - var4x * this.layer;
                        int var6 = var5 / this.w;
                        int var7 = var5 - var6 * this.w;
                        var1x.getBlockAt(var2 + var7, var3 + var4x, var4 + var6).setBlockData(var3x, false);
                        var2x.use(1);
                    } else if (++this.skipped % 512 == 0) {
                        var2x.use(1);
                    }
                }

                return this.i >= this.n;
            }
        };
    }

    public static Job treeJob(int var0, int var1, int var2, TreeType var3, long var4) {
        return (var6, var7) -> {
            var7.use(60);
            Random var8 = new Random(var4);
            if (var6.generateTree(new Location(var6, (double)var0, (double)var1, (double)var2), var8, var3)) {
                return true;
            } else {
                int var9 = 6 + var8.nextInt(4);
                BlockData var10 = Material.SPRUCE_LOG.createBlockData();
                BlockData var11 = Material.SPRUCE_LEAVES.createBlockData((var0x) -> {
                    ((Leaves)var0x).setPersistent(true);
                });

                int var12;
                for(var12 = 0; var12 < var9; ++var12) {
                    var6.getBlockAt(var0, var1 + var12, var2).setBlockData(var10, false);
                }

                for(var12 = 3; var12 <= var9; ++var12) {
                    int var13 = Math.max(0, (var9 - var12) / 2);

                    for(int var14 = -var13; var14 <= var13; ++var14) {
                        for(int var15 = -var13; var15 <= var13; ++var15) {
                            if ((var14 != 0 || var15 != 0) && Math.abs(var14) + Math.abs(var15) <= var13 + 1) {
                                Block var16 = var6.getBlockAt(var0 + var14, var1 + var12, var2 + var15);
                                if (var16.getType().isAir()) {
                                    var16.setBlockData(var11, false);
                                }
                            }
                        }
                    }
                }

                var6.getBlockAt(var0, var1 + var9, var2).setBlockData(var11, false);
                return true;
            }
        };
    }

    public static Job signJob(int var0, int var1, int var2, BlockData var3, List<String> var4, DyeColor var5) {
        return (var6, var7) -> {
            var7.use(1);
            Block var8 = var6.getBlockAt(var0, var1, var2);
            var8.setBlockData(var3, false);
            BlockState var10 = var8.getState();
            if (var10 instanceof Sign var9) {
                SignSide var12 = var9.getSide(Side.FRONT);

                for(int var11 = 0; var11 < 4 && var11 < var4.size(); ++var11) {
                    var12.line(var11, Component.text((String)var4.get(var11)));
                }

                var12.setColor(var5);
                var12.setGlowingText(true);
                var9.setWaxed(true);
                var9.update(true, false);
            }

            return true;
        };
    }

    private static final class Placement implements Job {
        final int[] xs = new int[4096];
        final int[] ys = new int[4096];
        final int[] zs = new int[4096];
        final BlockData[] data = new BlockData[4096];
        int size;
        int idx;

        void add(int var1, int var2, int var3, BlockData var4) {
            this.xs[this.size] = var1;
            this.ys[this.size] = var2;
            this.zs[this.size] = var3;
            this.data[this.size] = var4;
            ++this.size;
        }

        public boolean run(World var1, Budget var2) {
            while(this.idx < this.size && !var2.spent()) {
                var1.getBlockAt(this.xs[this.idx], this.ys[this.idx], this.zs[this.idx]).setBlockData(this.data[this.idx], false);
                ++this.idx;
                var2.use(1);
            }

            return this.idx >= this.size;
        }
    }

    public static final class Budget {
        private final long deadline;
        private final int maxBlocks;
        private int used;

        Budget(long var1, int var3) {
            this.deadline = var1;
            this.maxBlocks = var3;
        }

        public void use(int var1) {
            this.used += var1;
        }

        public boolean spent() {
            return this.used >= this.maxBlocks || System.nanoTime() >= this.deadline;
        }
    }

    public interface Job {
        boolean run(World var1, Budget var2);
    }
}
