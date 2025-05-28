package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeMaker extends BiomeHelper{

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
}