package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.world.components.chunkgenerators.TerrainColumn;
import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

import java.util.List;
import java.util.function.Consumer;

public class BiomeMaker extends BiomeHelper{

    public static List<TerrainColumn> makeBiomeList(HolderGetter<Biome> biomeRegistry, Holder<Biome> undergroundBiome) {
        return List.of(
                biomeColumnToBedrock(0.1D, 1.25D, 0.5D, biomeRegistry, TolkienBiomes.MIRKWOOD),
                biomeColumnWithUnderground(-0.8D, 3.9D, 1.0D, biomeRegistry, TolkienBiomes.FANGORN, undergroundBiome),
                biomeColumnWithUnderground(0.05D, 1.15D, 1.0D, biomeRegistry, TolkienBiomes.MORDOR, undergroundBiome),
                biomeColumnWithUnderground(5.5D, 1.75D, 1.15D, biomeRegistry, TolkienBiomes.ASH_MOUNTAINS, undergroundBiome),
                biomeColumnToBedrock(-0.6D, 1.7D, 1.0D, biomeRegistry, TolkienBiomes.MARSHES),
                biomeColumnToBedrock(0.125D, 0.05D, 1.0D, biomeRegistry, TolkienBiomes.LORINAND),
                biomeColumnToBedrock(0.2D, 0.2D, 1.0D, biomeRegistry, TolkienBiomes.BARROW_DOWNS),
                biomeColumnToBedrock(0.125D, 0.05D, 1.0D, biomeRegistry, TolkienBiomes.OLD_FOREST),
                biomeColumnToBedrock(0.125D, 0.05D, 1.0D, biomeRegistry, TolkienBiomes.SHIRE),
                biomeColumnToBedrock(-0.5D, 0.25D, 1.0D, biomeRegistry, TolkienBiomes.FIRIEN),
                biomeColumnWithUnderground(5.5D, 1.75D, 1.0D, biomeRegistry, TolkienBiomes.HITHAEGLIR, undergroundBiome),
                biomeColumnToBedrock(0.125D, 0.05D, 1.0D, biomeRegistry, TolkienBiomes.HARADWAITH),
                biomeColumnToBedrock(0.125D, 0.05D, 1.0D, biomeRegistry, TolkienBiomes.DAGORLAD),
                biomeColumnToBedrock(0.125D, 0.05D, 1.0D, biomeRegistry, TolkienBiomes.GLADDEN),
                biomeColumnWithUnderground(5.5D, 1.75D, 1.0D, biomeRegistry, TolkienBiomes.IRON_HILLS, undergroundBiome),

                biomeColumnWithUnderground(-0.1D, 0.001D, 1.35D, biomeRegistry, TolkienBiomes.STREAM, undergroundBiome),
                biomeColumnWithUnderground(-2.2D, 2.0D, 1.0D, biomeRegistry, TolkienBiomes.LAKE, undergroundBiome)
                );
    }

    public static Biome.BiomeBuilder makeBiomeIronHills(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.2F)
                .downfall(0.3F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeGladden(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeDagorland(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeHaradwaith(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeHithaelgir(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.2F)
                .downfall(0.3F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeFirien(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeShire(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeOldForest(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeBarrowDowns(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.25F)
                .downfall(0.8F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeLorinad(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeMirkwood(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeFangorn(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeMordor(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0.0F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    public static Biome.BiomeBuilder makeBiomeMarshes(BiomeSpecialEffects.Builder biomeAmbience, MobSpawnSettings.Builder mobSpawnInfo, BiomeGenerationSettings.Builder biomeGenerationSettings) {
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.9F)
                .specialEffects(biomeAmbience.build())
                .mobSpawnSettings(mobSpawnInfo.build())
                .generationSettings(biomeGenerationSettings.build())
                .temperatureAdjustment(Biome.TemperatureModifier.NONE);
    }

    private static TerrainColumn biomeColumnWithUnderground(double noiseDepth, double noiseScale, double weight, HolderGetter<Biome> biomeRegistry, ResourceKey<Biome> key, Holder<Biome> undergroundBiome) {
        Holder.Reference<Biome> biomeHolder = biomeRegistry.getOrThrow(key);

        biomeHolder.bindKey(key);

        return makeColumn(DensityFunctions.constant(noiseDepth), DensityFunctions.constant(noiseScale), DensityFunctions.constant(weight), biomeHolder, treeMap -> {
            // This will put the transition boundary around Y-8
            treeMap.put(Math.min(noiseDepth - 1, -1), biomeHolder);
            treeMap.put(Math.min(noiseDepth - 3, -3), undergroundBiome);
        });
    }

    private static TerrainColumn biomeColumnToBedrock(double noiseDepth, double noiseScale, double weight, HolderGetter<Biome> biomeRegistry, ResourceKey<Biome> key) {
        Holder.Reference<Biome> biomeHolder = biomeRegistry.getOrThrow(key);

        biomeHolder.bindKey(key);

        return makeColumn(DensityFunctions.constant(noiseDepth), DensityFunctions.constant(noiseScale), DensityFunctions.constant(weight), biomeHolder, treeMap -> treeMap.put(0, biomeHolder));
    }

    private static TerrainColumn makeColumn(DensityFunction noiseDepth, DensityFunction noiseScale, DensityFunction noiseWeight, Holder<Biome> biomeHolder, Consumer<Double2ObjectSortedMap<Holder<Biome>>> layerBuilder) {
        return new TerrainColumn(biomeHolder, Util.make(new Double2ObjectAVLTreeMap<>(), layerBuilder), noiseDepth, noiseScale, noiseWeight);
    }
}