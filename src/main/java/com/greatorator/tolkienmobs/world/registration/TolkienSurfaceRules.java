package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import org.jetbrains.annotations.NotNull;

public class TolkienSurfaceRules {
	private static final SurfaceRules.RuleSource STONE = makeifTrueRule(SurfaceRules.ON_FLOOR, makeStateRule(Blocks.STONE));
	private static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.sequence(makeifTrueRule(SurfaceRules.ON_FLOOR, makeStateRule(Blocks.GRAVEL)), makeifTrueRule(SurfaceRules.UNDER_FLOOR, makeStateRule(Blocks.GRAVEL)));
	private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
	private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
	private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
	private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
	private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
	private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
	private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
	private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
	private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
	private static final SurfaceRules.RuleSource COBBLED_DARK_STONE = makeStateRule(TolkienBlocks.COBBLED_DARK_STONE.get());
	private static final SurfaceRules.RuleSource CRACKED_DARK_STONE = makeStateRule(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get());
	private static final SurfaceRules.RuleSource DARK_STONE = makeStateRule(TolkienBlocks.DARK_STONE.get());

	public static SurfaceRules.RuleSource tolkienSurface() {
		SurfaceRules.RuleSource bedrockLayer = SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK);

		return SurfaceRules.sequence(
			bedrockLayer,
			MORDOR_SURFACE,
			ASH_MOUNTAINS_SURFACE,
			hithaeglirSurface(),
			haradwaithSurface(),
			ironHillsSurface(),
			overworldLikeFloor()
		);
	}

	private static SurfaceRules.RuleSource ironHillsSurface() {
		SurfaceRules.ConditionSource waterBlocks = SurfaceRules.waterBlockCheck(0, 0);
		SurfaceRules.ConditionSource mountainType = SurfaceRules.steep();
		SurfaceRules.RuleSource snowySoil = SurfaceRules.sequence(
				SurfaceRules.ifTrue(
						SurfaceRules.isBiome(TolkienBiomes.IRON_HILLS),
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(mountainType, STONE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, -0.5, 0.2), STONE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, -0.0625, 0.025), STONE),
								SurfaceRules.ifTrue(waterBlocks, STONE)
						)
				),
				SurfaceRules.ifTrue(
						SurfaceRules.isBiome(TolkienBiomes.IRON_HILLS),
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(mountainType, STONE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0, 0.2), STONE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, 0.0, 0.025), STONE),
								SurfaceRules.ifTrue(waterBlocks, STONE)
						)
				),
				SurfaceRules.ifTrue(
						SurfaceRules.ON_FLOOR,
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.IRON_HILLS), STONE)
						)
				));
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.IRON_HILLS), snowySoil);
	}

	private static SurfaceRules.RuleSource haradwaithSurface() {
		SurfaceRules.RuleSource desertSoil = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
		SurfaceRules.ConditionSource desertType = SurfaceRules.isBiome(TolkienBiomes.HARADWAITH);

        return SurfaceRules.sequence(
				SurfaceRules.ifTrue(desertType, desertSoil)
		);
	}

	private static final SurfaceRules.RuleSource MORDOR_SURFACE = biomeAbovePreliminarySurface(TolkienBiomes.MORDOR, SurfaceRules.sequence(
			makeifTrueRule(SurfaceRuleData.surfaceNoiseAbove(1.75D),
					SurfaceRules.sequence(
							makeifTrueRule(SurfaceRules.ON_FLOOR, GRASS_BLOCK),
							makeifTrueRule(SurfaceRules.UNDER_FLOOR, DARK_STONE)
					)),
			makeifTrueRule(SurfaceRuleData.surfaceNoiseAbove(-0.95D), DARK_STONE),
			GRASS_BLOCK
	));

	private static final SurfaceRules.RuleSource ASH_MOUNTAINS_SURFACE = biomeAbovePreliminarySurface(TolkienBiomes.ASH_MOUNTAINS, SurfaceRules.sequence(
			makeifTrueRule(SurfaceRuleData.surfaceNoiseAbove(1.75D),
					SurfaceRules.sequence(
							makeifTrueRule(SurfaceRules.ON_FLOOR, GRASS_BLOCK),
							makeifTrueRule(SurfaceRules.UNDER_FLOOR, DARK_STONE)
					)),
			makeifTrueRule(SurfaceRuleData.surfaceNoiseAbove(-0.95D), DARK_STONE),
			GRASS_BLOCK
	));

	@NotNull
	private static SurfaceRules.RuleSource mordorSurface() {
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MORDOR, TolkienBiomes.ASH_MOUNTAINS), SurfaceRules.ifTrue(surfaceNoiseAbove(1.0), DARK_STONE));
	}

	@NotNull
	private static SurfaceRules.RuleSource hithaeglirSurface() {
		SurfaceRules.ConditionSource waterBlocks = SurfaceRules.waterBlockCheck(0, 0);
		SurfaceRules.ConditionSource mountainType = SurfaceRules.steep();
		SurfaceRules.RuleSource snowySoil = SurfaceRules.sequence(
				SurfaceRules.ifTrue(
						SurfaceRules.isBiome(TolkienBiomes.HITHAEGLIR),
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(mountainType, PACKED_ICE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), ICE),
								SurfaceRules.ifTrue(waterBlocks, SNOW_BLOCK)
						)
				),
				SurfaceRules.ifTrue(
						SurfaceRules.isBiome(TolkienBiomes.HITHAEGLIR),
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(mountainType, PACKED_ICE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE),
								SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), ICE),
								SurfaceRules.ifTrue(waterBlocks, SNOW_BLOCK)
						)
				),
				SurfaceRules.ifTrue(
						SurfaceRules.ON_FLOOR,
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), STONE)
						)
				));
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.HITHAEGLIR), snowySoil);
	}

	@NotNull
	private static SurfaceRules.RuleSource overworldLikeFloor() {
		SurfaceRules.RuleSource swampBeds = SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MARSHES), SurfaceRules.sequence(
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK),
			DIRT
		));

		SurfaceRules.RuleSource grassAboveSeaLevel = SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1), GRASS_BLOCK);
		SurfaceRules.RuleSource grassSurface = SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), grassAboveSeaLevel);

		SurfaceRules.RuleSource underwaterSurface = SurfaceRules.ifTrue(
			SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1)),
			SurfaceRules.ifTrue(
				SurfaceRules.not(SurfaceRules.waterBlockCheck(-1, 0)),
				DIRT
			)
		);

		SurfaceRules.RuleSource onFloor = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
			swampBeds,
			grassSurface,
			underwaterSurface
		));

		SurfaceRules.RuleSource underFloor = SurfaceRules.ifTrue(
			SurfaceRules.waterStartCheck(-6, -1),
			SurfaceRules.ifTrue(
				SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1),
				SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
			)
		);

		return SurfaceRules.sequence(onFloor, underFloor);
	}

	private static SurfaceRules.ConditionSource surfaceNoiseAbove(double p_194809_) {
		return SurfaceRules.noiseCondition(Noises.SURFACE, p_194809_ / 8.25D, Double.MAX_VALUE);
	}

	private static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	private static SurfaceRules.RuleSource makeifTrueRule(ResourceKey<Biome> biome, SurfaceRules.RuleSource rule) {
		return makeifTrueRule(SurfaceRules.isBiome(biome), rule);
	}

	private static <B extends Block> SurfaceRules.RuleSource makeifTrueRule(SurfaceRules.ConditionSource conditionSource, B block) {
		return makeifTrueRule(conditionSource, makeStateRule(block));
	}

	private static SurfaceRules.RuleSource makeifTrueRule(SurfaceRules.ConditionSource ifTrue, SurfaceRules.RuleSource thenRun) {
		return SurfaceRules.ifTrue(ifTrue, thenRun);
	}

	private static SurfaceRules.RuleSource abovePreliminarySurface(SurfaceRules.RuleSource rule) {
		return makeifTrueRule(SurfaceRules.abovePreliminarySurface(), rule);
	}

	private static SurfaceRules.RuleSource biomeAbovePreliminarySurface(ResourceKey<Biome> biome, SurfaceRules.RuleSource rule) {
		return makeifTrueRule(biome, abovePreliminarySurface(rule));
	}
}
