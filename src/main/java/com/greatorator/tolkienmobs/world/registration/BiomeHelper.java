package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.greatorator.tolkienmobs.world.TolkienPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public abstract class BiomeHelper {
	public static int getSkyColorWithTemperatureModifier(float temp) {
		float lvt_1_1_ = temp / 3.0F;
		lvt_1_1_ = Mth.clamp(lvt_1_1_, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}

	public static BiomeSpecialEffects.Builder forestParticles(BiomeSpecialEffects.Builder builder) {
		builder.ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), 0.001f));
		return builder;
	}

	public static BiomeSpecialEffects.Builder desolateParticles(BiomeSpecialEffects.Builder builder) {
		builder.ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.00625F));
		return builder;
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

	public static BiomeSpecialEffects.Builder fangornAmbientBuilder() {
		return new BiomeSpecialEffects.Builder()
				.fogColor(0x548FB7)
				.waterColor(0x3F76E4)
				.waterFogColor(0x00D2FB)
				.skyColor(getSkyColorWithTemperatureModifier(0.8F))
				.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);
	}

	public static BiomeSpecialEffects.Builder mordorAmbientBuilder() {
		return new BiomeSpecialEffects.Builder()
				.fogColor(12638463)
				.waterColor(0)
				.waterFogColor(329011)
				.skyColor(getSkyColorWithTemperatureModifier(2.0F))
				.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);
	}

	public static BiomeSpecialEffects.Builder marshesAmbientBuilder() {
		return new BiomeSpecialEffects.Builder()
				.fogColor(5988193)
				.waterColor(14745540)
				.waterFogColor(2302743)
				.skyColor(getSkyColorWithTemperatureModifier(0.8F))
				.backgroundMusic(Musics.createGameMusic(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP))
				.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS);
	}

	public static MobSpawnSettings.Builder fangornForestSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TREEENT.get(), 1, 1, 1)).addMobCharge(TolkienEntities.ENTITY_TTM_TREEENT.get(), 0.6D, 0.15D);

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder mordorSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 5, 1, 1)).addMobCharge(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 0.6D, 0.15D);

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder marshesSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 5, 1, 1)).addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6D, 0.15D);
		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), 15, 1, 2)).addMobCharge(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), 0.6D, 0.15D);
		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 25, 1, 2)).addMobCharge(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 0.6D, 0.15D);
		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 15, 1, 2)).addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6D, 0.15D);
		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 4)).addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6D, 0.15D);

		return spawnInfo;
	}

	public static BiomeGenerationSettings.Builder fangornForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LEAVES);

		return biome;
	}

	public static BiomeGenerationSettings.Builder mordorGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_BRAMBLES_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_ROCKPILE);
		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_STONE_SPIKE);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MORGULIRON_ORE_PLACED_KEY);

		return biome;
	}

	public static BiomeGenerationSettings.Builder marshesGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SWAMP);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_SWAMPMILKWEED_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_SWAMP);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_SWAMP);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);

		return biome;
	}
}
