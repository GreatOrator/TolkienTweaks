package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienCaveCarvers;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.world.TolkienPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.placement.AquaticPlacements;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public abstract class BiomeHelper {
	public static int getSkyColorWithTemperatureModifier(float temp) {
		float lvt_1_1_ = temp / 3.0F;
		lvt_1_1_ = Mth.clamp(lvt_1_1_, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}

		// Mob Spawning
	public static MobSpawnSettings.Builder lorinadSpawning() {

			return new MobSpawnSettings.Builder()
					.addSpawn(MobCategory.MISC, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_ELVES.get(), 35, 1, 2))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 10, 1, 4))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 1, 4))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 1, 4))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 10, 1, 4))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 10, 1, 4))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 10, 1, 4))
					.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 10, 1, 4))
					.addMobCharge(TolkienEntities.ENTITY_TTM_ELVES.get(), 0.6, 0.15)
					.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
					.addMobCharge(EntityType.PIG, 0.6, 0.15)
					.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
					.addMobCharge(EntityType.COW, 0.6, 0.15)
					.addMobCharge(EntityType.HORSE, 0.6, 0.15)
					.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
					.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder ironHillsSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HARADRIM.get(), 45, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), 25, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 15, 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HARADRIM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder gladdenSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MISC, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HUMAN.get(), 45, 1, 5))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 10, 1, 2))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 10, 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HUMAN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder dagorladSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 35, 1, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 25, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 35, 1, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 25, 1, 4))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 25, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 15, 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder haradwaithSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HARADRIM.get(), 45, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), 25, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 15, 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HARADRIM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder hithaeglirSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TROLL.get(), 35, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 45, 1, 5))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_DUERGAR.get(), 20, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_TROLL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_DUERGAR.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder firienSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MISC, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_ELVES.get(), 35, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HURON.get(), 45, 1, 5))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 20, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 10, 1, 2))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 10, 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_ELVES.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_HURON.get(), 0.6, 0.15)
				.addMobCharge(EntityType.WOLF, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder shireSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MISC, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HOBBIT.get(), 45, 1, 5))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 10, 1, 2))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 10, 1, 4))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 10, 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HOBBIT.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder oldForestForestSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HURON.get(), 45, 1, 5))
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 20, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 10, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 10, 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HURON.get(), 0.6, 0.15)
				.addMobCharge(EntityType.WOLF, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder barrowSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BARROW.get(), 45, 1, 5))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 20, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_BARROW.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder mirkwoodSpookySpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 15, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 15, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 55, 1, 4))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.30)
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.30)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 0.6, 0.02)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.30);
	}

	public static MobSpawnSettings.Builder mirkwoodSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 20, 1, 5))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 25, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 25, 1, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 35, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder fangornForestSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TREEENT.get(), 25, 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_TREEENT.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder mordorSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 20, 1, 5))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 25, 1, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 25, 1, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 35, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder marshesSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 25, 1, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), 25, 1, 2))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 35, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 10, 1, 4))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 20, 1, 5))
				.addMobCharge(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

		// Biome Settings
	public static BiomeGenerationSettings.Builder lorinadGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_ELANOR_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MALLORN_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_WOOD_ROOTS_SPREAD);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.AMMOLITE_ORE_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MALLORN_FALLEN_LEAVES);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder ironHillsGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MITHRIL_ORE_PLACED_KEY);

		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addMountainTrees(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addForestGrass(biome);
		BiomeDefaultFeatures.addDefaultExtraVegetation(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addExtraEmeralds(biome);
		BiomeDefaultFeatures.addFerns(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addCommonBerryBushes(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder gladdenGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);

		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addForestGrass(biome);
		BiomeDefaultFeatures.addBadlandExtraVegetation(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addExtraEmeralds(biome);
		BiomeDefaultFeatures.addFerns(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addCommonBerryBushes(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder dagorladGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
		biome.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);

		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultGrass(biome);
		BiomeDefaultFeatures.addBadlandExtraVegetation(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder haradwaithGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		BiomeDefaultFeatures.addDesertExtraDecoration(biome);
		BiomeDefaultFeatures.addDesertVegetation(biome);
		BiomeDefaultFeatures.addDesertExtraVegetation(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder hithaeglirGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MITHRIL_ORE_PLACED_KEY);

		BiomeDefaultFeatures.addMountainTrees(biome);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder firienGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder shireGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder oldForestForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FOREST_FLOWERS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder barrowGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder mirkwoodSpookyGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

		return new BiomeGenerationSettings.Builder(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_MIRKWOOD_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LEAVES)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_WEBS)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);
	}

	public static BiomeGenerationSettings.Builder mirkwoodGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new BiomeGenerationSettings.Builder(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_MIRKWOOD_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LEAVES);
	}

	public static BiomeGenerationSettings.Builder fangornForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new BiomeGenerationSettings.Builder(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LEAVES);
	}

	public static BiomeGenerationSettings.Builder mordorGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new BiomeGenerationSettings.Builder(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_BRAMBLES_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_STONE_SPIKE)
				.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE)
				.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MORGULIRON_ORE_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_ANDESITE)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_DIORITE)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_GRANITE);
	}

	public static BiomeGenerationSettings.Builder marshesGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new BiomeGenerationSettings.Builder(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_SWAMPMILKWEED_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
	}

	public static MobSpawnSettings.Builder defaultMobSpawning() {
		MobSpawnSettings.Builder spawnInfo = new MobSpawnSettings.Builder();

		spawnInfo.creatureGenerationProbability(0.15f);

		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 12, 4, 4));
		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 10, 4, 4));
		spawnInfo.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
		spawnInfo.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 15, 4, 8));
		spawnInfo.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 10, 2, 4));
		spawnInfo.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 10, 1, 2));

		return spawnInfo;
	}

	public static MobSpawnSettings.Builder undergroundMobSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SPIDER, 10, 2, 3))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 10, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 10, 1, 1))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 1, 1, 1))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SLIME, 10, 2, 4))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 1, 1, 2))
				.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 10, 1, 3))
				.addSpawn(MobCategory.AMBIENT, new MobSpawnSettings.SpawnerData(EntityType.BAT, 10, 1, 1))
				.addMobCharge(EntityType.SPIDER, 0.2D, 0.15D)
				.addMobCharge(EntityType.ZOMBIE, 0.2D, 0.15D)
				.addMobCharge(EntityType.SKELETON, 0.3D, 0.15D)
				.addMobCharge(EntityType.CREEPER, 0.35D, 0.15D)
				.addMobCharge(EntityType.SLIME, 0.2D, 0.15D)
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.2D, 0.15D)
				.addMobCharge(EntityType.ENDERMAN, 0.4D, 0.15D);
				}

	public static BiomeGenerationSettings.Builder undergroundGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new BiomeGenerationSettings.Builder(featureGetter, carverGetter);

		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		withWoodRoots(biome);
		addCaves(biome);
		addSmallStoneClusters(biome);
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

	public static void withWoodRoots(BiomeGenerationSettings.Builder biome) {
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_WOOD_ROOTS_SPREAD);
	}

	public static void addSmallStoneClusters(BiomeGenerationSettings.Builder biome) {
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_ANDESITE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_DIORITE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_GRANITE);
	}

	public static void addCaves(BiomeGenerationSettings.Builder biome) {
		biome.addCarver(GenerationStep.Carving.AIR, TolkienCaveCarvers.CAVES_CONFIGURED);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, TolkienPlacedFeatures.PLACED_PLANT_ROOTS);
		addLegacyOres(biome);
	}

	public static void addLegacyOres(BiomeGenerationSettings.Builder biome) {
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_COAL_ORE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_IRON_ORE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_GOLD_ORE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_REDSTONE_ORE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_DIAMOND_ORE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_LAPIS_ORE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_COPPER_ORE);
	}
}
