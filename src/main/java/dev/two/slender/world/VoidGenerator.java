package dev.two.slender.world;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public final class VoidGenerator extends ChunkGenerator {
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

    public BiomeProvider getDefaultBiomeProvider(WorldInfo var1) {
        return new BiomeProvider() {

            public Biome getBiome(WorldInfo var1, int var2, int var3, int var4) {
                return Biome.PALE_GARDEN;
            }

            public List<Biome> getBiomes(WorldInfo var1) {
                return List.of(Biome.PALE_GARDEN);
            }
        };
    }

    public Location getFixedSpawnLocation(World var1, Random var2) {
        return new Location(var1, 0.5, 65.0, 0.5);
    }
}
