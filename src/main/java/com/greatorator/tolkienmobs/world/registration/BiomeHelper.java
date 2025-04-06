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

	public static BiomeGenerationSettings.Builder fangornForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FANGORNOAK_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LOG);

		addCaves(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder mordorGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.DEADWOOD_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_ROCKPILE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STONE_SPIKE);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MORGULIRON_ORE_PLACED_KEY);

		addCaves(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder twilightForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
//		addForestVegetationAlt(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_VANILLA_TF_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LARGE_TWILIGHT_OAK_TREE);
//		addCanopyTrees(biome);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MEGA_CANOPY_TREE);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder denseForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
//
//		addForestVegetationAlt(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DENSE_CANOPY_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_VANILLA_TF_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_OAK_BUSH_DENSE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DEFAULT_FALLEN_LOGS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FOREST_MEGA_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MEGA_CANOPY_TREE);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder fireflyForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);

//		addForestVegetationAlt(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FIREFLY_FOREST_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_VANILLA_TF_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LARGE_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LAMPPOST_PLACER);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MUSHGLOOM_CLUSTER);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DEFAULT_FALLEN_LOGS);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder clearingGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
//		addForestVegetation(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_FOREST_FLOWERS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DEFAULT_FALLEN_LOGS);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder oakSavannaGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
//
//		addForestVegetation(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_SAVANNAH_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LARGE_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DEFAULT_FALLEN_LOGS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_SAVANNAH_MEGA_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder enchantedForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
//
//		addForestVegetation(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_ENCHANTED_FOREST_TREES);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FIDDLEHEAD);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES);
//
//		addCanopyTrees(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder spookyForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MAYAPPLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LARGE_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DEAD_CANOPY_TREE);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_PUMPKIN_LAMPPOST);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TF_OAK_FALLEN_LOG);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_CANOPY_FALLEN_LOG);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_WEBS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FALLEN_LEAVES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
//
//		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TFPlacedFeatures.PLACED_GRAVEYARD);

		return biome;
	}

	public static BiomeGenerationSettings.Builder mushroomForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
//
//		addForestVegetationAlt(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MYCELIUM_BLOB);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_VANILLA_TF_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LARGE_TWILIGHT_OAK_TREE);
//		addCanopyMushrooms(biome, false);
//		addCanopyTrees(biome);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MEGA_CANOPY_TREE);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder denseMushroomForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE);
//
//		addForestVegetationAlt(biome);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MYCELIUM_BLOB);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_VANILLA_TF_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TWILIGHT_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_LARGE_TWILIGHT_OAK_TREE);
//		addCanopyMushrooms(biome, true);
//		addCanopyTrees(biome);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MEGA_CANOPY_TREE);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_WATER);

		return biome;
	}

	public static BiomeGenerationSettings.Builder thornlandsGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

//		commonFeaturesWithoutBuildings(biome);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_THORNS);

		return biome;
	}

	public static BiomeGenerationSettings.Builder highlandsGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_HIGHLANDS_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, MiscOverworldPlacements.FOREST_ROCK);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_SPARSE_MUSHGLOOMS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_SPRUCE_FALLEN_LOG);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_FERNS);
//
//		addHighlandCaves(biome);
//		addSmallStoneClusters(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder highlandsUndergroundGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

//		addHighlandCaves(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder streamsAndLakes(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter, boolean isLake) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, isLake ? AquaticPlacements.SEAGRASS_DEEP : AquaticPlacements.SEAGRASS_NORMAL);

		BiomeDefaultFeatures.addDefaultSeagrass(biome);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);

		addLegacyOres(biome);
		addSmallStoneClusters(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder swampGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
//
//		commonFeatures(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MANGROVE_TREE);
//		addSwampTrees(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MANGROVE_FALLEN_LOG);
//
//		lilypads(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder fireSwampGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = defaultGenSettingBuilder(featureGetter, carverGetter);

//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_TAIGA_2);
//
//		commonFeaturesWithoutBuildings(biome);
//		addSwampTrees(biome);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FIRE_JET);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_SMOKER);
//		biome.addFeature(GenerationStep.Decoration.LAKES, TFPlacedFeatures.PLACED_LAKE_LAVA);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.VINES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP);


		return biome;
	}

	public static BiomeGenerationSettings.Builder darkForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

//		addDarkForestVegetation(biome);
//		addCaves(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder darkForestCenterGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		return darkForestGen(featureGetter, carverGetter);
	}

	public static BiomeGenerationSettings.Builder glacierGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

//		BiomeDefaultFeatures.addSurfaceFreezing(biome);
//
//		addCaves(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder undergroundGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

//		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
//		BiomeDefaultFeatures.addSurfaceFreezing(biome);
//		withWoodRoots(biome);
//		addCaves(biome);
//		addSmallStoneClusters(biome);

		return biome;
	}

	public static void withWoodRoots(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_WOOD_ROOTS_SPREAD);
	}

	public static void commonFeatures(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TFPlacedFeatures.PLACED_DRUID_HUT);
//		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TFPlacedFeatures.PLACED_WELL_PLACER);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_GROVE_RUINS);
//		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TFPlacedFeatures.PLACED_FOUNDATION);
//
//		commonFeaturesWithoutBuildings(biome);
	}

	public static void commonFeaturesWithoutBuildings(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_STONE_CIRCLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_OUTSIDE_STALAGMITE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MONOLITH);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_HOLLOW_STUMP);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_HOLLOW_LOG);
	}

	public static void lilypads(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_HUGE_LILY_PAD);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_HUGE_WATER_LILY);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY);
	}

	public static void addForestVegetation(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MAYAPPLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FLOWER_PLACER);
	}

	public static void addForestVegetationAlt(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MAYAPPLE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_FLOWER_PLACER_ALT);
	}

	public static void addDarkForestVegetation(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_FOREST_TREE_MIX);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_FOREST_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARKWOOD_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_GRASS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_FERNS);  //  undefined opinion
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_DEAD_BUSHES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_PUMPKINS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_MUSHGLOOMS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_BROWN_MUSHROOMS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DARK_RED_MUSHROOMS);
	}

	//Canopies, trees, and anything resembling a forest thing
	public static void addCanopyTrees(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_CANOPY_TREES);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_DEFAULT_FALLEN_LOGS);
	}

	public static void addCanopyMushrooms(BiomeGenerationSettings.Builder biome, boolean dense) {
//		BiomeDefaultFeatures.addDefaultMushrooms(biome); // Add small mushrooms
//		//Same config as DefaultBiomeFeatures.withMushroomBiomeVegetation, we just use our custom large mushrooms instead
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_TAIGA);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_TAIGA);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_VANILLA_TF_BIG_MUSH);
//
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, dense ? TFPlacedFeatures.PLACED_CANOPY_MUSHROOMS_DENSE : TFPlacedFeatures.PLACED_CANOPY_MUSHROOMS_SPARSE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_MUSHGLOOM_CLUSTER);
	}

	public static void addSwampTrees(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_SWAMPY_OAK_TREE);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_OAK_BUSH);
	}

	public static void addSmallStoneClusters(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_SMALL_ANDESITE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_SMALL_DIORITE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_SMALL_GRANITE);
	}

	public static BiomeSpecialEffects.Builder ashParticles(BiomeSpecialEffects.Builder builder) {
		builder.ambientParticle(new AmbientParticleSettings(ParticleTypes.ASH, 0.05f));
		return builder;
	}

	public static BiomeSpecialEffects.Builder lightningbugParticles(BiomeSpecialEffects.Builder builder) {
		builder.ambientParticle(new AmbientParticleSettings(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), 0.00025f));
		return builder;
	}

	//Caves!
	public static void addCaves(BiomeGenerationSettings.Builder biome) {
//		biome.addCarver(GenerationStep.Carving.AIR, TFCaveCarvers.TFCAVES_CONFIGURED);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TFPlacedFeatures.PLACED_PLANT_ROOTS);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TFPlacedFeatures.PLACED_TORCH_BERRIES);
//		//biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TFPlacedFeatures.PLACED_VANILLA_ROOTS);
//		addLegacyOres(biome);
	}

	public static void addHighlandCaves(BiomeGenerationSettings.Builder biome) {
//		biome.addCarver(GenerationStep.Carving.AIR, TFCaveCarvers.HIGHLANDCAVES_CONFIGURED);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TFPlacedFeatures.PLACED_TROLL_ROOTS);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TFPlacedFeatures.PLACED_TROLL_MUSHGLOOMS);
//		addLegacyOres(biome);
	}

	public static void addLegacyOres(BiomeGenerationSettings.Builder biome) {
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_COAL_ORE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_IRON_ORE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_GOLD_ORE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_REDSTONE_ORE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_DIAMOND_ORE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_LAPIS_ORE);
//		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TFPlacedFeatures.PLACED_LEGACY_COPPER_ORE);
	}

	//Special mob spawns
	public static MobSpawnSettings.Builder penguinSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.creatureGenerationProbability(0.15f);
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.PENGUIN.get(), 10, 2, 4));

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder darkForestSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.creatureGenerationProbability(0.05f);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 2, 1, 2)).addMobCharge(EntityType.ENDERMAN, 0.75D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 5, 1, 2)).addMobCharge(EntityType.ZOMBIE, 0.7D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 5, 1, 2)).addMobCharge(EntityType.SKELETON, 0.8D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.MIST_WOLF.get(), 5, 1, 1)).addMobCharge(TFEntities.MIST_WOLF.get(), 0.75D, 0.2D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.SKELETON_DRUID.get(), 5, 1, 1)).addMobCharge(TFEntities.SKELETON_DRUID.get(), 0.8D, 0.2D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.KING_SPIDER.get(), 1, 1, 1)).addMobCharge(TFEntities.KING_SPIDER.get(), 0.85D, 0.25D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.KOBOLD.get(), 10, 1, 3)).addMobCharge(TFEntities.KOBOLD.get(), 0.7D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITCH, 2, 1, 1)).addMobCharge(EntityType.WITCH, 0.75D, 0.15D);

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder ravenSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.creatureGenerationProbability(0.3f);
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.RAVEN.get(), 10, 4, 4));

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder swampSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 10, 1, 2)).addMobCharge(EntityType.ZOMBIE, 0.7D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.MOSQUITO_SWARM.get(), 10, 1, 1)).addMobCharge(TFEntities.MOSQUITO_SWARM.get(), 0.7D, 0.15D);

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder spookSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 50, 1, 2)).addMobCharge(EntityType.SPIDER, 0.75D, 0.25D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 20, 1, 2)).addMobCharge(EntityType.SKELETON, 0.85D, 0.25D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.SKELETON_DRUID.get(), 5, 1, 1)).addMobCharge(TFEntities.SKELETON_DRUID.get(), 0.95D, 0.25D);
//		spawnInfo.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 20, 2, 4));

		return spawnInfo;
	}

	public static BiomeGenerationSettings.Builder defaultGenSettingBuilder(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

//		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
//		BiomeDefaultFeatures.addSavannaGrass(biome);
//		BiomeDefaultFeatures.addDefaultGrass(biome);
//		BiomeDefaultFeatures.addSavannaExtraGrass(biome);
//		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE);
//		BiomeDefaultFeatures.addSurfaceFreezing(biome);
//		withWoodRoots(biome);
//		addCaves(biome);
//		addSmallStoneClusters(biome);

		return biome;
	}

	public static MobSpawnSettings.Builder defaultMobSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.creatureGenerationProbability(0.15f);
//
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.BIGHORN_SHEEP.get(), 12, 4, 4));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.BOAR.get(), 10, 4, 4));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.DEER.get(), 15, 4, 5));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.TINY_BIRD.get(), 15, 4, 8));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.SQUIRREL.get(), 10, 2, 4));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.DWARF_RABBIT.get(), 10, 4, 5));
//		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TFEntities.RAVEN.get(), 10, 1, 2));

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder undergroundMobSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 10, 2, 3)).addMobCharge(EntityType.SPIDER, 0.2D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 10, 1, 2)).addMobCharge(EntityType.ZOMBIE, 0.2D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 10, 1, 1)).addMobCharge(EntityType.SKELETON, 0.3D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 1, 1, 1)).addMobCharge(EntityType.CREEPER, 0.35D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 10, 2, 4)).addMobCharge(EntityType.SLIME, 0.2D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 1, 2)).addMobCharge(EntityType.ENDERMAN, 0.4D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TFEntities.KOBOLD.get(), 10, 1, 3)).addMobCharge(TFEntities.KOBOLD.get(), 0.2D, 0.15D);
//		spawnInfo.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 10, 1, 1));

		return spawnInfo;
	}
}
