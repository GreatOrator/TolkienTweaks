package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.Optional;

/**
 * 26.1 keeps only the colours in {@link BiomeSpecialEffects}: the fog and sky colours, the ambient
 * particles and sounds and the background music are environment attributes on the biome itself, so
 * each one is set on the builder instead of on the effects.
 */
public class BiomeMaker extends BiomeHelper{

    /** A loop sound is one field of {@link AmbientSounds}; the mood-only settings have their own constant. */
    private static AmbientSounds ambientLoop(Holder<SoundEvent> loop) {
        return new AmbientSounds(Optional.of(loop), Optional.empty(), List.of());
    }

    public static Biome makeBiomeMirkwood(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        mirkwoodGen(placedFeatureGetter, carverGetter);
        mirkwoodSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.8F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.6F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(5988193))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(5541815))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(SoundEvents.AMBIENT_WARPED_FOREST_LOOP))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .foliageColorOverride(738353)
                        .grassColorOverride(738353)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.6F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(5988193))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(5541815))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(SoundEvents.AMBIENT_WARPED_FOREST_LOOP))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .foliageColorOverride(738353)
                        .grassColorOverride(738353)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.2F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(10518688))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(TolkienSounds.MUSIC_DISC_ALLTHATGLITTERS))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
                        .build())
                .mobSpawnSettings(ironHillsSpawning().build())
                .generationSettings(ironHillsGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    /**
     * The Blue Mountains follow the Iron Hills' shape without their northern cold: temperate, with
     * the same teal (#59B6BD) for the grass and the water. The water fog is that same teal, darkened
     * so it reads as depth rather than as the surface itself.
     */
    public static Biome makeBiomeBlueMountains(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        blueMountainsGen(placedFeatureGetter, carverGetter);
        blueMountainsSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.7F)
                .downfall(0.6F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.7F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(0x3E8A90))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(TolkienSounds.MUSIC_DISC_ALLTHATGLITTERS))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x59B6BD)
                        .grassColorOverride(0x59B6BD)
                        .build())
                .mobSpawnSettings(blueMountainsSpawning().build())
                .generationSettings(blueMountainsGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeFangorn(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        fangornForestGen(placedFeatureGetter, carverGetter);
        fangornForestSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.8F)
                .downfall(0.3F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.8F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(0x548FB7))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(0x00D2FB))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x3F76E4)
                        .foliageColorOverride(0x4EAD4E)
                        .grassColorOverride(0x4EAD4E)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.3F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(10518688))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(SoundEvents.AMBIENT_WARPED_FOREST_MOOD))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.8F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(10518688))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(TolkienSounds.MEDIEVAL_CITY))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(329011)
                        .foliageColorOverride(14596231)
                        .grassColorOverride(14596231)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.8F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(10518688))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(TolkienSounds.MEDIEVAL_CITY))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(329011)
                        .foliageColorOverride(14596231)
                        .grassColorOverride(14596231)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(2.0F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.7F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(10518688))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.LEBETHRON_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.8F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(10518688))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(TolkienSounds.MUSIC_DISC_CONCERNINGHOBBITS)))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.CULUMALDA_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4159204)
                        .foliageColorOverride(2292007)
                        .grassColorOverride(2292007)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.8F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(5988193))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(5541815))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS,
                        new AmbientSounds(Optional.of(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP), Optional.of(AmbientMoodSettings.LEGACY_CAVE_SETTINGS), List.of()))
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(54011)
                        .foliageColorOverride(5163086)
                        .grassColorOverride(5163086)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.25F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(11119017))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(5541815))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, ambientLoop(TolkienSounds.PATH_OF_UNDEAD))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(808080)
                        .foliageColorOverride(14481884)
                        .grassColorOverride(14481884)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.6F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(5988193))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(5541815))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(TolkienSounds.MUSIC_DISC_LIGHTOFLOTHLORIEN)))
                // 26.1 takes a list here; the old single-valued builder kept only the mallorn leaves,
                // so the second particle is the one that used to survive.
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(TolkienParticleTypes.MALLORN_LEAVES.get(), 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(54011)
                        .foliageColorOverride(738353)
                        .grassColorOverride(738353)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(2.0F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .foliageColorOverride(2329)
                        .grassColorOverride(2329)
                        .build())
                .mobSpawnSettings(mordorSpawning().build())
                .generationSettings(mordorGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeNurn(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        nurnGen(placedFeatureGetter, carverGetter);
        nurnSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(1.0F)
                .downfall(1.0F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(1.0F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                // The sea of Nurnen is fed by the ash of Mordor, so the water fog is not blue.
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(0x5A4D33))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0x9A8455)
                        .foliageColorOverride(0x8A9A4F)
                        .grassColorOverride(0x9BAA5F)
                        .build())
                .mobSpawnSettings(nurnSpawning().build())
                .generationSettings(nurnGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeAshMountain(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        mordorGen(placedFeatureGetter, carverGetter);
        mordorSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(2.0F)
                .downfall(0)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(2.0F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(0)
                        .foliageColorOverride(2329)
                        .grassColorOverride(2329)
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
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(0.8F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(5988193))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.ASH, 0.00625F))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(14745540)
                        .foliageColorOverride(6316071)
                        .grassColorOverride(6316071)
                        .build())
                .mobSpawnSettings(marshesSpawning().build())
                .generationSettings(marshesGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeForochel(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        forochelGen(placedFeatureGetter, carverGetter);
        forochelSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(-0.5F)
                .downfall(0.5F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(-0.5F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4020182)
                        .build())
                .mobSpawnSettings(forochelSpawning().build())
                .generationSettings(forochelGen(placedFeatureGetter, carverGetter).build())
                .build();
    }

    public static Biome makeBiomeForochelSlopes(HolderGetter<PlacedFeature> placedFeatureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        forochelSlopesGen(placedFeatureGetter, carverGetter);
        forochelSlopesSpawning();
        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(-0.7F)
                .downfall(0.4F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, ARGB.opaque(getSkyColorWithTemperatureModifier(-0.7F)))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, ARGB.opaque(12638463))
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, ARGB.opaque(329011))
                .specialEffects((new BiomeSpecialEffects.Builder())
                        .waterColor(4020182)
                        .build())
                .mobSpawnSettings(forochelSlopesSpawning().build())
                .generationSettings(forochelSlopesGen(placedFeatureGetter, carverGetter).build())
                .build();
    }
}
