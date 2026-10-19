package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

/** Adds a Tolkien region alongside the vanilla region, retaining vanilla climate and terrain shapes. */
public class TolkienOverworldRegion extends Region {
    public TolkienOverworldRegion() {
        super(TolkienMobsMain.resLoc("overworld"), RegionType.OVERWORLD, 2);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addModifiedVanillaOverworldBiomes(mapper, builder -> {
            builder.replaceBiome(Biomes.DARK_FOREST, TolkienBiomes.MIRKWOOD);
            builder.replaceBiome(Biomes.OLD_GROWTH_SPRUCE_TAIGA, TolkienBiomes.MIRKWOOD_SPOOKY);
            builder.replaceBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS, TolkienBiomes.IRON_HILLS);
            // The windswept forest point is the last free windswept climate, and its temperate
            // hills give the dwarven range of the Blue Mountains somewhere to stand.
            builder.replaceBiome(Biomes.WINDSWEPT_FOREST, TolkienBiomes.BLUE_MOUNTAINS);
            builder.replaceBiome(Biomes.FROZEN_PEAKS, TolkienBiomes.HITHAEGLIR);
            builder.replaceBiome(Biomes.OLD_GROWTH_PINE_TAIGA, TolkienBiomes.FANGORN);
            builder.replaceBiome(Biomes.ERODED_BADLANDS, TolkienBiomes.MORDOR);
            builder.replaceBiome(Biomes.BADLANDS, TolkienBiomes.NURN);
            builder.replaceBiome(Biomes.WOODED_BADLANDS, TolkienBiomes.NURN);
            builder.replaceBiome(Biomes.JAGGED_PEAKS, TolkienBiomes.ASH_MOUNTAINS);
            builder.replaceBiome(Biomes.SWAMP, TolkienBiomes.MARSHES);
            builder.replaceBiome(Biomes.BIRCH_FOREST, TolkienBiomes.LORINAND);
            builder.replaceBiome(Biomes.WINDSWEPT_HILLS, TolkienBiomes.BARROW_DOWNS);
            builder.replaceBiome(Biomes.FOREST, TolkienBiomes.OLD_FOREST);
            builder.replaceBiome(Biomes.PLAINS, TolkienBiomes.SHIRE);
            builder.replaceBiome(Biomes.FLOWER_FOREST, TolkienBiomes.FIRIEN);
            builder.replaceBiome(Biomes.DESERT, TolkienBiomes.HARADWAITH);
            builder.replaceBiome(Biomes.SAVANNA_PLATEAU, TolkienBiomes.DAGORLAD);
            builder.replaceBiome(Biomes.SUNFLOWER_PLAINS, TolkienBiomes.GLADDEN);
            builder.replaceBiome(Biomes.SNOWY_PLAINS, TolkienBiomes.FOROCHEL);
            builder.replaceBiome(Biomes.SNOWY_SLOPES, TolkienBiomes.FOROCHEL_SLOPES);
        });
    }
}
