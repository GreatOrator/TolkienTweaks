package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import org.jetbrains.annotations.NotNull;

public class TolkienSurfaceRules {
	private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
	private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
	private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
	private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
	private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
	private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
	private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
	private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
	private static final SurfaceRules.RuleSource SNOW = makeStateRule(Blocks.SNOW_BLOCK);
	private static final SurfaceRules.RuleSource COBBLED_DARK_STONE = makeStateRule(TolkienBlocks.COBBLED_DARK_STONE.get());
	private static final SurfaceRules.RuleSource CRACKED_DARK_STONE = makeStateRule(TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get());
	private static final SurfaceRules.RuleSource DARK_STONE = makeStateRule(TolkienBlocks.DARK_STONE.get());

	private static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	public static SurfaceRules.RuleSource tolkienSurface() {
		SurfaceRules.RuleSource bedrockLayer = SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK);

		return SurfaceRules.sequence(
			bedrockLayer,
//			highlandsSurface(),
			mordorSurface(),
//			snowyForestSurface(),
//			glacierSurface(),
			overworldLikeFloor()
		);
	}

//	@NotNull
//	private static SurfaceRules.RuleSource highlandsSurface() {
//		// Make sure it's not a block under the water level
//		SurfaceRules.RuleSource podzolFloor = SurfaceRules.sequence(
//			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), PODZOL),
//			DIRT
//		);
//
//		//highlands has a noise-based mixture of podzol and coarse dirt
//		SurfaceRules.RuleSource highlandsSoil = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
//			//mix coarse dirt and podzol with noise
//			SurfaceRules.ifTrue(surfaceNoiseAbove(2.25D), COARSE_DIRT),
//			SurfaceRules.ifTrue(surfaceNoiseAbove(-2.25D), podzolFloor)
//		));
//
//		//check if we're in the highlands
//		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TFBiomes.HIGHLANDS), highlandsSoil);
//	}

	@NotNull
	private static SurfaceRules.RuleSource mordorSurface() {
		SurfaceRules.RuleSource mordorTerrain = SurfaceRules.sequence(
			SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, COBBLED_DARK_STONE),
			SurfaceRules.ifTrue(
				SurfaceRules.waterBlockCheck(0, 0),
				SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, CRACKED_DARK_STONE)
			),
				DARK_STONE
		);

		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MORDOR, TolkienBiomes.ASH_MOUNTAINS), mordorTerrain);
	}

//	@NotNull
//	private static SurfaceRules.RuleSource snowyForestSurface() {
//		// Make sure it's not a block under the water level
//		SurfaceRules.RuleSource snowFloor = SurfaceRules.sequence(
//			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SNOW),
//			DIRT
//		);
//
//		//snowy forest is all snow on the top layers
//		SurfaceRules.RuleSource snowySoil = SurfaceRules.sequence(
//			SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, snowFloor),
//			SurfaceRules.ifTrue(
//				SurfaceRules.waterStartCheck(-6, -1),
//				// This used to be dirt, but was changed to snow under the first block in later versions. Should this be kept to snow?
//				SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)
//			)
//		);
//
//		//check if we're in the snowy forest
//		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TFBiomes.SNOWY_FOREST), snowySoil);
//	}

//	@NotNull
//	private static SurfaceRules.RuleSource glacierSurface() {
//		//glacier has gravel for a few layers, then stone. All blanketed under 30+ blocks of ice
//		SurfaceRules.RuleSource surfaceUnderPermafrost = SurfaceRules.sequence(
//			//surface and under is gravel
//			SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, GRAVEL),
//			SurfaceRules.ifTrue(
//				SurfaceRules.waterStartCheck(-6, -1),
//				SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, GRAVEL)
//			)
//		);
//
//		//check if we're in the glacier biome
//		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TFBiomes.GLACIER), surfaceUnderPermafrost);
//	}

	@NotNull
	private static SurfaceRules.RuleSource overworldLikeFloor() {
		//lakes and rivers get sand
		SurfaceRules.RuleSource riverLakeBeds = SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.LAKE, TolkienBiomes.STREAM), SurfaceRules.sequence(
			SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE),
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK),
			SAND
		));

		//make sure the swamps always get grass, they had weird stone patches sometimes
		SurfaceRules.RuleSource swampBeds = SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MARSHES), SurfaceRules.sequence(
			SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), GRASS_BLOCK),
			DIRT
		));

		//check if we're above ground, so hollow hills dont have grassy floors
		SurfaceRules.RuleSource grassAboveSeaLevel = SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1), GRASS_BLOCK);
		//make everything else grass
		SurfaceRules.RuleSource grassSurface = SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), grassAboveSeaLevel);

		//if we're around the area hollow hill floors are, check if we're underwater. If so place some dirt.
		//This fixes streams having weird stone patches
		SurfaceRules.RuleSource underwaterSurface = SurfaceRules.ifTrue(
			SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1)),
			SurfaceRules.ifTrue(
				SurfaceRules.not(SurfaceRules.waterBlockCheck(-1, 0)),
				DIRT
			)
		);

		// Twilight Forest's surface is based off the normal overworld surface
		SurfaceRules.RuleSource onFloor = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
			riverLakeBeds,
			swampBeds,
			grassSurface,
			underwaterSurface
		));

		//dirt goes under the grass of course!
		SurfaceRules.RuleSource underFloor = SurfaceRules.ifTrue(
			SurfaceRules.waterStartCheck(-6, -1),
			//check if we're above ground, so hollow hills dont have dirt floors
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
