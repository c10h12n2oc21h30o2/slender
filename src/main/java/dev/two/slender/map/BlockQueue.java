// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import org.bukkit.block.BlockState;
import org.bukkit.block.sign.SignSide;
import net.kyori.adventure.text.Component;
import org.bukkit.block.sign.Side;
import org.bukkit.block.Sign;
import org.bukkit.DyeColor;
import java.util.List;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.Location;
import java.util.Random;
import org.bukkit.TreeType;
import dev.two.slender.map.schem.Schematic;
import org.bukkit.block.Block;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import java.util.ArrayDeque;

public final class BlockQueue
{
    private static final int CHUNK = 4096;
    private final ArrayDeque<Job> jobs;
    private Placement current;
    private long total;
    private long done;
    
    public BlockQueue() {
        this.jobs = new ArrayDeque<Job>();
    }
    
    public void set(final int n, final int n2, final int n3, final BlockData blockData) {
        if (this.current == null || this.current.size == 4096) {
            this.current = new Placement();
            this.jobs.add(this.current);
        }
        this.current.add(n, n2, n3, blockData);
        ++this.total;
    }
    
    public void add(final Job e, final long n) {
        this.current = null;
        this.jobs.add(e);
        this.total += n;
    }
    
    public boolean tick(final World world, final int n, final long n2) {
        final Budget budget = new Budget(System.nanoTime() + n2, n);
        while (!this.jobs.isEmpty() && !budget.spent()) {
            if (this.jobs.peek().run(world, budget)) {
                this.jobs.poll();
            }
        }
        this.done += budget.used;
        return this.jobs.isEmpty();
    }
    
    public double progress() {
        return (this.total == 0L) ? 1.0 : Math.min(1.0, this.done / (double)this.total);
    }
    
    public static Job clearJob(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return new Job() {
            int x = n;
            int y = n2;
            int z = n3;
            boolean entitiesCleared;
            int checks;
            
            @Override
            public boolean run(final World world, final Budget budget) {
                if (!this.entitiesCleared) {
                    for (final Entity entity : world.getNearbyEntities(new BoundingBox((double)n, (double)n2, (double)n3, (double)(n4 + 1), (double)(n5 + 1), (double)(n6 + 1)))) {
                        if (!(entity instanceof Player)) {
                            entity.remove();
                        }
                    }
                    this.entitiesCleared = true;
                }
                while (!budget.spent()) {
                    final Block block = world.getBlockAt(this.x, this.y, this.z);
                    if (!block.getType().isAir()) {
                        block.setType(Material.AIR, false);
                        budget.use(1);
                    }
                    if (++this.checks % 64 == 0) {
                        budget.use(1);
                    }
                    if (++this.y > n5) {
                        this.y = n2;
                        if (++this.z <= n6) {
                            continue;
                        }
                        this.z = n3;
                        if (++this.x > n4) {
                            return true;
                        }
                        continue;
                    }
                }
                return false;
            }
        };
    }
    
    public static Job schematicJob(final Schematic schematic, final BlockData[] array, final int n, final int n2, final int n3) {
        return new Job() {
            int i;
            int skipped;
            final int w = schematic.width();
            final int layer = schematic.width() * schematic.length();
            final int n = schematic.size();
            
            @Override
            public boolean run(final World world, final Budget budget) {
                while (this.i < this.n && !budget.spent()) {
                    final BlockData blockData = array[schematic.paletteAt(this.i)];
                    if (blockData != null) {
                        final int n = this.i / this.layer;
                        final int n2 = this.i - n * this.layer;
                        final int n3 = n2 / this.w;
                        world.getBlockAt(n + (n2 - n3 * this.w), n2 + n, n3 + n3).setBlockData(blockData, false);
                        budget.use(1);
                    }
                    else if (++this.skipped % 512 == 0) {
                        budget.use(1);
                    }
                    ++this.i;
                }
                return this.i >= this.n;
            }
        };
    }
    
    public static Job treeJob(final int n, final int n2, final int n3, final TreeType treeType, final long n4) {
        return (world2, budget2) -> {
            budget2.use(60);
            final Random random = new Random(seed);
            if (world2.generateTree(new Location(world2, (double)n5, (double)n6, (double)n7), random, treeType2)) {
                return true;
            }
            else {
                final int n8 = 6 + random.nextInt(4);
                Material.SPRUCE_LOG.createBlockData();
                Material.SPRUCE_LEAVES.createBlockData(blockData3 -> ((Leaves)blockData3).setPersistent(true));
                int i = 0;
                while (i < n8) {
                    final BlockData blockData;
                    world2.getBlockAt(n5, n6 + i, n7).setBlockData(blockData, false);
                    ++i;
                }
                int j = 0;
                final BlockData blockData2;
                while (j <= n8) {
                    Math.max(0, (n8 - j) / 2);
                    int k = 0;
                    final int n9;
                    while (k <= n9) {
                        int l = 0;
                        while (l <= n9) {
                            if ((k != 0 || l != 0) && Math.abs(k) + Math.abs(l) <= n9 + 1) {
                                world2.getBlockAt(n5 + k, n6 + j, n7 + l);
                                final Block block;
                                if (block.getType().isAir()) {
                                    block.setBlockData(blockData2, false);
                                }
                            }
                            ++l;
                        }
                        ++k;
                    }
                    ++j;
                }
                world2.getBlockAt(n5, n6 + n8, n7).setBlockData(blockData2, false);
                return true;
            }
        };
    }
    
    public static Job signJob(final int n, final int n2, final int n3, final BlockData blockData, final List<String> list, final DyeColor dyeColor) {
        return (world2, budget2) -> {
            budget2.use(1);
            world2.getBlockAt(n4, n5, n6);
            final Block block;
            block.setBlockData(blockData2, false);
            block.getState();
            final Sign sign;
            if (sign instanceof Sign) {
                final Sign sign2 = sign;
                sign2.getSide(Side.FRONT);
                int n7 = 0;
                final SignSide signSide;
                while (n7 < 4 && n7 < list2.size()) {
                    signSide.line(n7, (Component)Component.text((String)list2.get(n7)));
                    ++n7;
                }
                signSide.setColor(color);
                signSide.setGlowingText(true);
                sign2.setWaxed(true);
                sign2.update(true, false);
            }
            return true;
        };
    }
    
    public static final class Budget
    {
        private final long deadline;
        private final int maxBlocks;
        private int used;
        
        Budget(final long deadline, final int maxBlocks) {
            this.deadline = deadline;
            this.maxBlocks = maxBlocks;
        }
        
        public void use(final int n) {
            this.used += n;
        }
        
        public boolean spent() {
            return this.used >= this.maxBlocks || System.nanoTime() >= this.deadline;
        }
    }
    
    private static final class Placement implements Job
    {
        final int[] xs;
        final int[] ys;
        final int[] zs;
        final BlockData[] data;
        int size;
        int idx;
        
        private Placement() {
            this.xs = new int[4096];
            this.ys = new int[4096];
            this.zs = new int[4096];
            this.data = new BlockData[4096];
        }
        
        void add(final int n, final int n2, final int n3, final BlockData blockData) {
            this.xs[this.size] = n;
            this.ys[this.size] = n2;
            this.zs[this.size] = n3;
            this.data[this.size] = blockData;
            ++this.size;
        }
        
        @Override
        public boolean run(final World world, final Budget budget) {
            while (this.idx < this.size && !budget.spent()) {
                world.getBlockAt(this.xs[this.idx], this.ys[this.idx], this.zs[this.idx]).setBlockData(this.data[this.idx], false);
                ++this.idx;
                budget.use(1);
            }
            return this.idx >= this.size;
        }
    }
    
    public interface Job
    {
        boolean run(final World p0, final Budget p1);
    }
}
