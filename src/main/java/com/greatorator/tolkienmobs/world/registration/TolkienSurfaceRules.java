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
	private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
	private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
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
	private static final SurfaceRules.RuleSource SMOOTH_DARK_STONE = makeStateRule(TolkienBlocks.SMOOTH_DARK_STONE.get());
	private static final SurfaceRules.RuleSource DARK_STONE = makeStateRule(TolkienBlocks.DARK_STONE.get());

	private static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	public static SurfaceRules.RuleSource tolkienSurface() {
		return SurfaceRules.sequence(
				makeTolkienWorldRules());
	}

	private static SurfaceRules.RuleSource makeTolkienWorldRules() {
		SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
		SurfaceRules.ConditionSource sixBelowWater = SurfaceRules.waterStartCheck(-6, -1);
		SurfaceRules.ConditionSource isAbove62 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
		SurfaceRules.ConditionSource isAbove63 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
		SurfaceRules.RuleSource mordorSurface = SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, DARK_STONE),
				COBBLED_DARK_STONE
		);
		SurfaceRules.RuleSource darkStoneLining = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, DARK_STONE), GRASS_BLOCK);

		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(
						sixBelowWater,
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(
										SurfaceRules.UNDER_FLOOR,
										SurfaceRules.sequence(
												SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MORDOR), darkStoneLining)
										)
								),
								SurfaceRules.ifTrue(
										SurfaceRules.DEEP_UNDER_FLOOR,
										SurfaceRules.sequence(
												SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MORDOR), DARK_STONE)
										)
								)
						)
				),
				SurfaceRules.ifTrue(
						SurfaceRules.ON_FLOOR,
						SurfaceRules.sequence(
								SurfaceRules.ifTrue(
										// Underwater lining
										SurfaceRules.not(isAtOrAboveWaterLevel),
										SurfaceRules.sequence(
												SurfaceRules.ifTrue(
														SurfaceRules.isBiome(TolkienBiomes.ASH_MOUNTAINS),
														SurfaceRules.ifTrue(surfaceNoiseAbove(2.7D), SMOOTH_DARK_STONE)
												)
										)
								)
						)
				),
				SurfaceRules.ifTrue(
						SurfaceRules.isBiome(TolkienBiomes.ASH_MOUNTAINS),
						SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(3.7D), GRASS_BLOCK),
								SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(2.6D), DARK_STONE),
										SurfaceRules.sequence(SurfaceRules.ifTrue(surfaceNoiseAbove(1.5D), SMOOTH_DARK_STONE),
												mordorSurface
										)
								)
						)
				)
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
}
