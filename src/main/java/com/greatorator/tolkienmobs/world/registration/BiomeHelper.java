package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.world.components.placements.TolkienPlacedFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.Carvers;
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
	/**
	 * One country's own people in its spawn list.
	 *
	 * <p>26.1 cannot spawn a {@link MobCategory#MISC} mob out of biome data: its
	 * {@code NaturalSpawner} leaves the misc list out of the categories it rolls and refuses a MISC
	 * entry when it does meet one, and {@link MobSpawnSettings.SpawnerData} quietly replaces such a
	 * mob with a pig as it is built. That is what turned every NPC in these lists into
	 * {@code minecraft:pig}. Arda's people are registered {@link MobCategory#CREATURE} for it, and
	 * this refuses to write the biome data if one is ever named against a category that cannot
	 * spawn, rather than letting a country fill with pigs again.
	 */
	private static MobSpawnSettings.SpawnerData npc(EntityType<?> type, int min, int max) {
		if (type.getCategory() != MobCategory.CREATURE) {
			throw new IllegalStateException("NPC " + EntityType.getKey(type) + " is a "
					+ type.getCategory().getName() + " mob, and 26.1 spawns none of those from biome data");
		}
		return new MobSpawnSettings.SpawnerData(type, min, max);
	}

	public static MobSpawnSettings.Builder lorinadSpawning() {

			return new MobSpawnSettings.Builder()
					.addSpawn(MobCategory.CREATURE, 35, npc(TolkienEntities.ENTITY_TTM_ELVES.get(), 1, 2))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 4))
					.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
					.addMobCharge(TolkienEntities.ENTITY_TTM_ELVES.get(), 0.6, 0.15)
					.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
					.addMobCharge(EntityType.PIG, 0.6, 0.15)
					.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
					.addMobCharge(EntityType.COW, 0.6, 0.15)
					.addMobCharge(EntityType.HORSE, 0.6, 0.15)
					.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
					.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15);
	}

	/**
	 * The Iron Hills are the Lonely Mountain's own country: Dale, Esgaroth and the dwarf-city under
	 * Erebor stand in them, so the hills carry dwarves and their flocks - the battle goats of the
	 * mod's own making, one in a hundred of which comes with a dwarf already in the saddle, in place
	 * of vanilla's shepherds' sheep. They are tagged a heartland, which means this list names no
	 * monster at all - it used to hand the hills to the Haradrim.
	 */
	public static MobSpawnSettings.Builder ironHillsSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_DWARF.get(), 1, 3))
				.addSpawn(MobCategory.CREATURE, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOAT.get(), 1, 3))
				.addSpawn(MobCategory.AMBIENT, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_DWARF.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOAT.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	/**
	 * The Blue Mountains are a heartland - Belegost and Nogrod stand in them, and the dwarven
	 * village brings the range's own people - so the slopes carry only the creatures that live
	 * there, the dwarves' battle goats and the crebain. They used to hand the range to the goblins
	 * of the deep.
	 */
	public static MobSpawnSettings.Builder blueMountainsSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOAT.get(), 1, 3))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 1, 3))
				.addSpawn(MobCategory.AMBIENT, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOAT.get(), 0.6, 0.15)
				.addMobCharge(EntityType.RABBIT, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder gladdenSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_HUMAN.get(), 1, 5))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
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
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 1, 4))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 1, 4))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6, 0.15);
	}

	/**
	 * Harad. The southrons' own country, so it carries their traders as well as the mumakil the
	 * Haradrim ride. The trader is one entry of five against the beasts, which is a meeting every
	 * sixty-odd chunks of the country rather than a fixture of it, and a trader is only ever placed
	 * as the country is made - see {@code TolkienEventBusEvents} - so he is met in the field rather
	 * than gathered around whoever stands still in it.
	 */
	public static MobSpawnSettings.Builder haradwaithSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 45, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HARADRIM.get(), 1, 4))
				.addSpawn(MobCategory.CREATURE, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), 1, 2))
				.addSpawn(MobCategory.CREATURE, 5, npc(TolkienEntities.ENTITY_TTM_SOUTHRON.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HARADRIM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder hithaeglirSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TROLL.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 45, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 5))
				.addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_DUERGAR.get(), 1, 2))
				.addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_TROLL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_DUERGAR.get(), 0.6, 0.15)
				.addMobCharge(EntityType.GOAT, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder firienSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 35, npc(TolkienEntities.ENTITY_TTM_ELVES.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 45, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HURON.get(), 1, 5))
				.addSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
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
				.addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_HOBBIT.get(), 1, 5))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
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
				.addSpawn(MobCategory.MONSTER, 45, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_HURON.get(), 1, 5))
				.addSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HURON.get(), 0.6, 0.15)
				.addMobCharge(EntityType.WOLF, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder barrowSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 45, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BARROW.get(), 1, 5))
				.addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_BARROW.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder mirkwoodSpookySpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 55, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.30)
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.30)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 0.6, 0.02)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.30);
	}

	public static MobSpawnSettings.Builder mirkwoodSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 5))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 1, 3))
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	public static MobSpawnSettings.Builder fangornForestSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TREEENT.get(), 1, 1))
				.addMobCharge(TolkienEntities.ENTITY_TTM_TREEENT.get(), 0.6, 0.15);
	}

	/**
	 * Mordor, the orcs' own country, which is where their traders walk - and the Ash Mountains with
	 * it, since they share this list. The trader is the country's only creature, so what keeps him a
	 * rare meeting rather than one at every turn is Mordor's own
	 * generation rate: an entry this bare is otherwise rolled into a tenth of the country's chunks,
	 * and a hundredth is a trader every thousand blocks or so. A trader is placed only as a country
	 * is made, never by the tick pass, so that rate is the whole story. Nurn, inside the same ring,
	 * carries the same trader among its farmers - see {@link #nurnSpawning()}.
	 */
	public static MobSpawnSettings.Builder mordorSpawning() {

        return new MobSpawnSettings.Builder()
				.creatureGenerationProbability(0.01F)
				.addSpawn(MobCategory.CREATURE, 1, npc(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 5))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 1, 3))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 1, 4))
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 4))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}    /** Nurn is worked ground inside Mordor, so the same wardens patrol it, and its farms are where
     * the dark tower's own traders come to deal - the trader is one entry of nine in its creature
     * list, a meeting every ninety chunks or so of farmland. */
    public static MobSpawnSettings.Builder nurnSpawning() {

        return new MobSpawnSettings.Builder()
                .addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 4))
                .addSpawn(MobCategory.MONSTER, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 1, 3))
                .addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 1, 4))
                .addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 1, 2))
                .addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 4))
                .addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 3))
                .addSpawn(MobCategory.CREATURE, 8, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 3))
                .addSpawn(MobCategory.CREATURE, 2, npc(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), 1, 2))
                .addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_MORDORORC.get(), 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_URUKHAI.get(), 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15)
                .addMobCharge(EntityType.SHEEP, 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15);
    }

    public static MobSpawnSettings.Builder marshesSpawning() {

        return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 1, 3))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SWARM.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 20, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 5))
				.addMobCharge(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SWARM.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

    public static MobSpawnSettings.Builder forochelSpawning() {

        return new MobSpawnSettings.Builder()
                .addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_HUMAN.get(), 1, 5))
                .addSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 2))
                .addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.RABBIT, 2, 4))
                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3))
                .addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
                .addMobCharge(TolkienEntities.ENTITY_TTM_HUMAN.get(), 0.6, 0.15)
                .addMobCharge(EntityType.POLAR_BEAR, 0.6, 0.15)
                .addMobCharge(EntityType.RABBIT, 0.6, 0.15)
                .addMobCharge(EntityType.GOAT, 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15);
    }

    public static MobSpawnSettings.Builder forochelSlopesSpawning() {

        return new MobSpawnSettings.Builder()
                .addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TROLL.get(), 1, 2))
                .addSpawn(MobCategory.MONSTER, 45, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 5))
                .addSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.POLAR_BEAR, 1, 2))
                .addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 3))
                .addMobCharge(TolkienEntities.ENTITY_TTM_TROLL.get(), 0.6, 0.15)
                .addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
                .addMobCharge(EntityType.POLAR_BEAR, 0.6, 0.15)
                .addMobCharge(EntityType.GOAT, 0.6, 0.15);
    }

	/**
	 * The Wild Wood's spawn list. The wood is the enemy's country: its orcs hold it, its spiders nest
	 * in it and the vale's brigands hide in it, and it is tagged hostile, so it keeps them whatever
	 * else is placed there. Wolves, crebain and squirrels are the only quiet company in it.
	 */
	public static MobSpawnSettings.Builder wildWoodSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 1, 4))
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 1, 3))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 1, 3))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 3))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_GOBLIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.15)
				.addMobCharge(EntityType.WOLF, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15);
	}

	/**
	 * The Mark. Rohan is a heartland - Edoras, Aldburg and the Hornburg stand in it - so its riders,
	 * herds and horses are the whole list and no monster is named.
	 */
	public static MobSpawnSettings.Builder rohanSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_HUMAN.get(), 1, 5))
				.addSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 2, 6))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HUMAN.get(), 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15);
	}

	/**
	 * Ered Nimrais. Nothing is settled above the Mark's valleys, so the white wall carries the wild
	 * things that live on stone: trolls out of the deeps, wargs on the passes, goats and wolves.
	 */
	public static MobSpawnSettings.Builder whiteMountainsSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_TROLL.get(), 1, 2))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 1, 3))
				.addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.GOAT, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 2))
				.addSpawn(MobCategory.AMBIENT, 15, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 3))
				.addMobCharge(TolkienEntities.ENTITY_TTM_TROLL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
				.addMobCharge(EntityType.GOAT, 0.6, 0.15)
				.addMobCharge(EntityType.WOLF, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15);
	}

	/**
	 * Dunland and the worn hills: Enedwaith, Minhiriath, Cardolan and the Weather Hills. Wild
	 * country, so the hillmen's brigands and their wargs keep it, among sheep and cattle.
	 */
	public static MobSpawnSettings.Builder dunlandSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.MONSTER, 35, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 1, 4))
				.addSpawn(MobCategory.MONSTER, 25, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_WARG.get(), 1, 3))
				.addSpawn(MobCategory.CREATURE, 20, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 1, 3))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_BRIGAND.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_WARG.get(), 0.6, 0.15)
				.addMobCharge(EntityType.WOLF, 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_CREBAIN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15);
	}

	/**
	 * Lindon, the green land behind the Grey Havens. The elves' own country and a heartland, so the
	 * list is elves and their herds.
	 */
	public static MobSpawnSettings.Builder lindonSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_ELVES.get(), 1, 3))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_ELVES.get(), 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15);
	}

	/**
	 * The fields of Gondor: Minas Tirith, Pelargir, Dol Amroth, Linhir and the rest stand in them,
	 * so the kingdom is a heartland and its people and herds are the whole list.
	 */
	public static MobSpawnSettings.Builder gondorSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 45, npc(TolkienEntities.ENTITY_TTM_HUMAN.get(), 1, 5))
				.addSpawn(MobCategory.CREATURE, 15, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.DONKEY, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HUMAN.get(), 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.DONKEY, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15);
	}

	/**
	 * Eriador: Bree-land and the lost realm of Arnor, whose towns - Bree, Archet, Combe, Staddle,
	 * Annuminas and Fornost - stand in it. A heartland, so hobbits, men and their herds, no monster.
	 */
	public static MobSpawnSettings.Builder eriadorSpawning() {

		return new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, 25, npc(TolkienEntities.ENTITY_TTM_HUMAN.get(), 1, 5))
				.addSpawn(MobCategory.CREATURE, 25, npc(TolkienEntities.ENTITY_TTM_HOBBIT.get(), 1, 5))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.PIG, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.CHICKEN, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.COW, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(EntityType.HORSE, 1, 4))
				.addSpawn(MobCategory.CREATURE, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_AUROCH.get(), 1, 4))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_THRUSH.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 1, 2))
				.addSpawn(MobCategory.AMBIENT, 10, new MobSpawnSettings.SpawnerData(TolkienEntities.ENTITY_TTM_FROG.get(), 1, 2))
				.addMobCharge(TolkienEntities.ENTITY_TTM_HUMAN.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_HOBBIT.get(), 0.6, 0.15)
				.addMobCharge(EntityType.SHEEP, 0.6, 0.15)
				.addMobCharge(EntityType.PIG, 0.6, 0.15)
				.addMobCharge(EntityType.CHICKEN, 0.6, 0.15)
				.addMobCharge(EntityType.COW, 0.6, 0.15)
				.addMobCharge(EntityType.HORSE, 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_AUROCH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_THRUSH.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), 0.6, 0.15)
				.addMobCharge(TolkienEntities.ENTITY_TTM_FROG.get(), 0.6, 0.15);
	}

		// Biome Settings
	public static BiomeGenerationSettings.Builder lorinadGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

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
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MITHRIL_ORE_PLACED_KEY);

		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addMountainTrees(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addForestGrass(biome);
		BiomeDefaultFeatures.addDefaultExtraVegetation(biome, true);
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

	/**
	 * The Blue Mountains are the Iron Hills' generation with the mountain's own stone underfoot; the
	 * block itself is laid by {@code TolkienSurfaceRules.blueMountainsSurface}.
	 */
	public static BiomeGenerationSettings.Builder blueMountainsGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MOUNTAIN_STONE_PLACED_KEY);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MITHRIL_ORE_PLACED_KEY);

		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addMountainTrees(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addForestGrass(biome);
		BiomeDefaultFeatures.addDefaultExtraVegetation(biome, true);
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
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);
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
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_BADLANDS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);

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
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		BiomeDefaultFeatures.addDesertExtraDecoration(biome);
		BiomeDefaultFeatures.addDesertVegetation(biome);
		BiomeDefaultFeatures.addDesertExtraVegetation(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder hithaeglirGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DWARVEN_MAPLE_FALLEN_LOG);        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MITHRIL_ORE_PLACED_KEY);

        BiomeDefaultFeatures.addMountainTrees(biome);
        // Summits go in above the trees but before the freeze, so the snow lands on them and none
        // of them is planted with a spruce.
        biome.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TolkienPlacedFeatures.PLACED_MOUNTAIN_PEAK);
        BiomeDefaultFeatures.addSurfaceFreezing(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder firienGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.DARK_FOREST_VEGETATION);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder shireGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder oldForestForestGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FOREST_FLOWERS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder barrowGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_ROCKPILE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);

		addLegacyOres(biome);

		return biome;
	}

	public static BiomeGenerationSettings.Builder mirkwoodSpookyGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

		return new TolkienBiomeGenerationSettings(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_MIRKWOOD_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LEAVES)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS)
				// Webs hang off the mirkwood canopy, and the trees reach this biome through the
				// add_tree_mirkwood biome modifier, which appends them to the end of the vegetal
				// decoration step. Running webs in the last step is what guarantees the canopy is
				// standing before they go looking for it.
				.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TolkienPlacedFeatures.PLACED_WEBS);
	}

	public static BiomeGenerationSettings.Builder mirkwoodGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new TolkienBiomeGenerationSettings(featureGetter, carverGetter)
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

        return new TolkienBiomeGenerationSettings(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_FANGORN_FALLEN_LEAVES);
	}

	public static BiomeGenerationSettings.Builder mordorGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new TolkienBiomeGenerationSettings(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_JUNGLE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_BRAMBLES_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_ROCKPILE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STONE_SPIKE)
				.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.LAKE_LAVA_SURFACE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MORGULIRON_ORE_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_ANDESITE)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_DIORITE)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_SMALL_GRANITE);
	}    /**
     * Nurn's land, without a trace of the vanilla lava. The vanilla springs and lava lakes are
     * deliberately left out - the aquifer still puts lava in the deepest bedrock layers, which is
     * the dimension's own floor rather than a biome feature - and water is the only fluid added.
     */
    public static BiomeGenerationSettings.Builder nurnGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

        biome.addFeature(GenerationStep.Decoration.LAKES, TolkienPlacedFeatures.PLACED_NURN_LAKE);
        biome.addFeature(GenerationStep.Decoration.FLUID_SPRINGS, MiscOverworldPlacements.SPRING_WATER);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG);
        biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MORGULIRON_ORE_PLACED_KEY);

        biome.addCarver(Carvers.CAVE);
        biome.addCarver(Carvers.CAVE_EXTRA_UNDERGROUND);
        biome.addCarver(Carvers.CANYON);

        BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
        BiomeDefaultFeatures.addDefaultMushrooms(biome);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
        addLegacyOres(biome);

        return biome;
    }

    public static BiomeGenerationSettings.Builder marshesGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {

        return new TolkienBiomeGenerationSettings(featureGetter, carverGetter)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.TREES_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.FLOWER_SWAMPMILKWEED_PLACED_KEY)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_DEAD_BUSH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MUD_SPLATTER)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_SUGAR_CANE_SWAMP)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_WATERLILY)
				.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, MiscOverworldPlacements.DISK_CLAY);
	}

    public static BiomeGenerationSettings.Builder forochelGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_PUMPKIN);

        BiomeDefaultFeatures.addDefaultOres(biome);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
        BiomeDefaultFeatures.addDefaultMushrooms(biome);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
        BiomeDefaultFeatures.addSurfaceFreezing(biome);
        addLegacyOres(biome);

        return biome;
    }

    public static BiomeGenerationSettings.Builder forochelSlopesGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
        BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
        biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);

        BiomeDefaultFeatures.addDefaultOres(biome);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
        BiomeDefaultFeatures.addDefaultMushrooms(biome);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
        BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
        BiomeDefaultFeatures.addSurfaceFreezing(biome);
        BiomeDefaultFeatures.addMountainTrees(biome);
        addLegacyOres(biome);

        return biome;
    }

	/**
	 * The Wild Wood's ground: forest floor, ferns and flowers under the wood's own beeches, with the
	 * fallen timber and stumps of an unworked wood. The trees themselves arrive through the
	 * add_tree_mirkwood biome modifier.
	 */
	public static BiomeGenerationSettings.Builder wildWoodGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FOREST_FLOWERS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_MIRKWOOD_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.PLACED_WOOD_ROOTS_SPREAD);

		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		addLegacyOres(biome);

		return biome;
	}

	/**
	 * The Mark: open grass with the plain's oaks and flowers, a settled land's orchards, and the
	 * springs and ores any grass country gets.
	 */
	public static BiomeGenerationSettings.Builder rohanGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_PLAIN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		addLegacyOres(biome);

		return biome;
	}

	/**
	 * Ered Nimrais, the white wall. Bare stone with snow on it, spruce below the snow line - the
	 * Misty Mountains' generation with the ice softened to snow, and the mod's own rock underfoot.
	 */
	public static BiomeGenerationSettings.Builder whiteMountainsGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, TolkienPlacedFeatures.MOUNTAIN_STONE_PLACED_KEY);
		// Summits go in above the trees but before the freeze, so the snow lands on them.
		biome.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, TolkienPlacedFeatures.PLACED_MOUNTAIN_PEAK);

		BiomeDefaultFeatures.addMountainTrees(biome);
		BiomeDefaultFeatures.addSurfaceFreezing(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		addLegacyOres(biome);

		return biome;
	}

	/**
	 * The worn hills of Dunland: grass and fern over stone, with the rubble and the boulders the
	 * hill country is strewn with, and the stumps of wood the hillmen cut.
	 */
	public static BiomeGenerationSettings.Builder dunlandGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_ROCKPILE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_RANDOM_RUBBLE);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_STUMPS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_DEADWOOD_FALLEN_LOG);

		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addDefaultSprings(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		addLegacyOres(biome);

		return biome;
	}

	/** Lindon: the Shire's green ground, kept as the elves' own country rather than the hobbits'. */
	public static BiomeGenerationSettings.Builder lindonGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		addLegacyOres(biome);

		return biome;
	}

	/** Gondor's fields: the same green ground as Lindon, with the kingdom's white flowers over it. */
	public static BiomeGenerationSettings.Builder gondorGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		addLegacyOres(biome);

		return biome;
	}

	/** Eriador: the Shire's ground again, for Bree-land and the lost realm of Arnor beside it. */
	public static BiomeGenerationSettings.Builder eriadorGen(HolderGetter<PlacedFeature> featureGetter, HolderGetter<ConfiguredWorldCarver<?>> carverGetter) {
		BiomeGenerationSettings.Builder biome = new TolkienBiomeGenerationSettings(featureGetter, carverGetter);

		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_GRASS_FOREST);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.PATCH_LARGE_FERN);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.FLOWER_PLAINS);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.BROWN_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, VegetationPlacements.RED_MUSHROOM_OLD_GROWTH);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LEAVES);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_CULUMALDA_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, TolkienPlacedFeatures.PLACED_LEBETHRON_FALLEN_LOG);
		biome.addFeature(GenerationStep.Decoration.LAKES, MiscOverworldPlacements.SPRING_WATER);

		BiomeDefaultFeatures.addPlainVegetation(biome);
		BiomeDefaultFeatures.addDefaultOres(biome);
		BiomeDefaultFeatures.addDefaultSoftDisks(biome);
		BiomeDefaultFeatures.addDefaultMonsterRoom(biome);
		BiomeDefaultFeatures.addDefaultMushrooms(biome);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(biome);
		BiomeDefaultFeatures.addDefaultCarversAndLakes(biome);
		BiomeDefaultFeatures.addDefaultFlowers(biome);
		addLegacyOres(biome);

		return biome;
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
