package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.world.components.chunkgenerators.TerrainColumn;
import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.function.Consumer;

public class BiomeMaker extends BiomeHelper{

    public static List<TerrainColumn> makeBiomeList(HolderGetter<Biome> biomeRegistry, Holder<Biome> undergroundBiome) {
        return List.of(
                biomeColumnToBedrock(0.1D, 1.25D, 0.5D, biomeRegistry, TolkienBiomes.MIRKWOOD),
                biomeColumnToBedrock(0.1D, 1.25D, 0.5D, biomeRegistry, TolkienBiomes.MIRKWOOD_SPOOKY),
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
                biomeColumnWithUnderground(5.5D, 1.75D, 1.0D, biomeRegistry, TolkienBiomes.IRON_HILLS, undergroundBiome)

//                biomeColumnWithUnderground(-0.1D, 0.001D, 1.35D, biomeRegistry, TolkienBiomes.STREAM, undergroundBiome),
//                biomeColumnWithUnderground(-2.2D, 2.0D, 1.0D, biomeRegistry, TolkienBiomes.LAKE, undergroundBiome)
                );
    }

    public static Biome makeBiomeMirkwood(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        mirkwoodGen(placedFeatureGetter, carverGetter);
        mirkwoodSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .waterFogColor(5541815)
                        .fogColor(5988193)
                        .foliageColorOverride(738353)
                        .grassColorOverride(738353)
                        .skyColor(getSkyColorWithTemperatureModifier(0.6F))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(mirkwoodSpawning().build())
                .generationSettings(mirkwoodGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeMirkwoodSpooky(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        mirkwoodSpookyGen(placedFeatureGetter, carverGetter);
        mirkwoodSpookySpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .waterFogColor(5541815)
                        .fogColor(5988193)
                        .foliageColorOverride(738353)
                        .grassColorOverride(738353)
                        .skyColor(getSkyColorWithTemperatureModifier(0.6F))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(mirkwoodSpookySpawning().build())
                .generationSettings(mirkwoodSpookyGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeIronHills(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        ironHillsGen(placedFeatureGetter, carverGetter);
        ironHillsSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.2F)
                .downfall(0.3F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(10518688)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
                        .skyColor(getSkyColorWithTemperatureModifier(0.2F))
                        .ambientLoopSound(TolkienSounds.MUSIC_DISC_ALLTHATGLITTERS)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(ironHillsSpawning().build())
                .generationSettings(ironHillsGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeFangorn(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        fangornForestGen(placedFeatureGetter, carverGetter);
        fangornForestSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.3F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x00D2FB)
                        .fogColor(0x548FB7)
                        .foliageColorOverride(0x4EAD4E)
                        .grassColorOverride(0x4EAD4E)
                        .skyColor(getSkyColorWithTemperatureModifier(0.8F))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(fangornForestSpawning().build())
                .generationSettings(fangornForestGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeHithaelgir(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        hithaeglirGen(placedFeatureGetter, carverGetter);
        hithaeglirSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.3F)
                .downfall(0.2F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(10518688)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
                        .skyColor(getSkyColorWithTemperatureModifier(0.3F))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_MOOD)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(hithaeglirSpawning().build())
                .generationSettings(hithaeglirGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeGladden(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        gladdenGen(placedFeatureGetter, carverGetter);
        gladdenSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(329011)
                        .waterFogColor(10518688)
                        .fogColor(12638463)
                        .foliageColorOverride(14596231)
                        .grassColorOverride(14596231)
                        .skyColor(getSkyColorWithTemperatureModifier(0.8F))
                        .ambientLoopSound(TolkienSounds.MEDIEVAL_CITY)
                        .build())
                .mobSpawnSettings(gladdenSpawning().build())
                .generationSettings(gladdenGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeDagorland(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        dagorladGen(placedFeatureGetter, carverGetter);
        dagorladSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.4F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(329011)
                        .waterFogColor(10518688)
                        .fogColor(12638463)
                        .foliageColorOverride(14596231)
                        .grassColorOverride(14596231)
                        .skyColor(getSkyColorWithTemperatureModifier(0.8F))
                        .ambientLoopSound(TolkienSounds.MEDIEVAL_CITY)
                        .build())
                .mobSpawnSettings(dagorladSpawning().build())
                .generationSettings(dagorladGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeHaradwaith(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        haradwaithGen(placedFeatureGetter, carverGetter);
        haradwaithSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .skyColor(getSkyColorWithTemperatureModifier(2.0F))
                        .ambientLoopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
                        .build())
                .mobSpawnSettings(haradwaithSpawning().build())
                .generationSettings(haradwaithGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeFirien(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        firienGen(placedFeatureGetter, carverGetter);
        firienSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(10518688)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
                        .skyColor(getSkyColorWithTemperatureModifier(0.7F))
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.LEBETHRON_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(firienSpawning().build())
                .generationSettings(firienGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeShire(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        shireGen(placedFeatureGetter, carverGetter);
        shireSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .waterFogColor(329011)
                        .fogColor(10518688)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
                        .skyColor(getSkyColorWithTemperatureModifier(0.8F))
                        .backgroundMusic(Musics.createGameMusic(TolkienSounds.MUSIC_DISC_CONCERNINGHOBBITS))
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.CULUMALDA_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(shireSpawning().build())
                .generationSettings(shireGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeOldForest(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        oldForestForestGen(placedFeatureGetter, carverGetter);
        oldForestForestSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(54011)
                        .waterFogColor(5541815)
                        .fogColor(5988193)
                        .foliageColorOverride(5163086)
                        .grassColorOverride(5163086)
                        .skyColor(getSkyColorWithTemperatureModifier(0.8F))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP))
                        .ambientLoopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(oldForestForestSpawning().build())
                .generationSettings(oldForestForestGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeBarrowDowns(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        barrowGen(placedFeatureGetter, carverGetter);
        barrowSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.25F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(808080)
                        .waterFogColor(5541815)
                        .fogColor(11119017)
                        .foliageColorOverride(14481884)
                        .grassColorOverride(14481884)
                        .skyColor(getSkyColorWithTemperatureModifier(0.25F))
                        .ambientLoopSound(TolkienSounds.PATH_OF_UNDEAD)
                        .build())
                .mobSpawnSettings(barrowSpawning().build())
                .generationSettings(barrowGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeLorinad(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        lorinadGen(placedFeatureGetter, carverGetter);
        lorinadSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.6F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(54011)
                        .waterFogColor(5541815)
                        .fogColor(5988193)
                        .foliageColorOverride(738353)
                        .grassColorOverride(738353)
                        .skyColor(getSkyColorWithTemperatureModifier(0.6F))
                        .backgroundMusic(Musics.createGameMusic(TolkienSounds.MUSIC_DISC_LIGHTOFLOTHLORIEN))
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), 0.001f))
                        .ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.MALLORN_LEAVES.get(), 0.00625F))
                        .build())
                .mobSpawnSettings(lorinadSpawning().build())
                .generationSettings(lorinadGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeMordor(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        mordorGen(placedFeatureGetter, carverGetter);
        mordorSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .foliageColorOverride(2329)
                        .grassColorOverride(2329)
                        .skyColor(getSkyColorWithTemperatureModifier(2.0F))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.00625F))
                        .build())
                .mobSpawnSettings(mordorSpawning().build())
                .generationSettings(mordorGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeAshMountain(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        mordorGen(placedFeatureGetter, carverGetter);
        mordorSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .waterFogColor(329011)
                        .fogColor(12638463)
                        .foliageColorOverride(2329)
                        .grassColorOverride(2329)
                        .skyColor(getSkyColorWithTemperatureModifier(2.0F))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.00625F))
                        .build())
                .mobSpawnSettings(mordorSpawning().build())
                .generationSettings(mordorGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeMarshes(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        marshesGen(placedFeatureGetter, carverGetter);
        marshesSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.8F)
                .downfall(0.8F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(14745540)
                        .waterFogColor(329011)
                        .fogColor(5988193)
                        .foliageColorOverride(6316071)
                        .grassColorOverride(6316071)
                        .skyColor(getSkyColorWithTemperatureModifier(0.8F))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.00625F))
                        .build())
                .mobSpawnSettings(marshesSpawning().build())
                .generationSettings(marshesGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeStream(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        streamsAndLakes(placedFeatureGetter, carverGetter, false);
        defaultMobSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.5F)
                .downfall(0.1F)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .fogColor(0xC0FFD8)
                        .skyColor(getSkyColorWithTemperatureModifier(0.5F))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(defaultMobSpawning().build())
                .generationSettings(streamsAndLakes(placedFeatureGetter, carverGetter, false).build())
                .build();
    }

    public static Biome makeBiomeLake(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        streamsAndLakes(placedFeatureGetter, carverGetter, true);
        defaultMobSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.66F)
                .downfall(1)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .fogColor(0xC0FFD8)
                        .skyColor(getSkyColorWithTemperatureModifier(0.66F))
                        .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(defaultMobSpawning().build())
                .generationSettings(streamsAndLakes(placedFeatureGetter, carverGetter, true).build())
                .build();
    }

    public static Biome makeBiomeUnderground(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        undergroundGen(placedFeatureGetter, carverGetter);
        defaultMobSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.0F)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .fogColor(0xC0FFD8)
                        .skyColor(getSkyColorWithTemperatureModifier(0.66F))
                        .backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES))
                        .build())
                .mobSpawnSettings(defaultMobSpawning().build())
                .generationSettings(undergroundGen(placedFeatureGetter, carverGetter).build())
                .build();
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