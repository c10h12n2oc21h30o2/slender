// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.world;

import org.bukkit.Location;
import java.util.Random;
import org.bukkit.World;
import java.util.List;
import org.bukkit.block.Biome;
import java.util.Objects;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.bukkit.generator.ChunkGenerator;

public final class VoidGenerator extends ChunkGenerator
{
    public boolean shouldGenerateNoise() {
        return false;
    }
    
    public boolean shouldGenerateSurface() {
        return false;
    }
    
    public boolean shouldGenerateBedrock() {
        return false;
    }
    
    public boolean shouldGenerateCaves() {
        return false;
    }
    
    public boolean shouldGenerateDecorations() {
        return false;
    }
    
    public boolean shouldGenerateMobs() {
        return false;
    }
    
    public boolean shouldGenerateStructures() {
        return false;
    }
    
    public BiomeProvider getDefaultBiomeProvider(final WorldInfo worldInfo) {
        return new BiomeProvider(this) {
            public Biome getBiome(final WorldInfo worldInfo, final int n, final int n2, final int n3) {
                return Biome.PALE_GARDEN;
            }
            
            public List<Biome> getBiomes(final WorldInfo worldInfo) {
                return List.of(Biome.PALE_GARDEN);
            }
        };
    }
    
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return new Location(world, 0.5, 65.0, 0.5);
    }
}
