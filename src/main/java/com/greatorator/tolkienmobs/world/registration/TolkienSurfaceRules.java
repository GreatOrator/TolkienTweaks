package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.data.worldgen.SurfaceRuleData;
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
	private static final SurfaceRules.RuleSource MOUNTAIN_STONE = makeStateRule(TolkienBlocks.MOUNTAIN_STONE.get());

	private static SurfaceRules.RuleSource makeStateRule(Block block) {
		return SurfaceRules.state(block.defaultBlockState());
	}

	public static SurfaceRules.RuleSource tolkienSurface() {
        // TerraBlender selects this rule for our namespace; keep it out of caves
        // and leave bedrock/deepslate to the vanilla fallback rules.
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), materials()),
                // The Blue Mountains' rock is the biome's stone, not a surface dressing, so its
                // rules sit outside the guard above as well: a shaft sunk into the mountain stays
                // in mountain stone all the way down to the deepslate, and a floor that the
                // preliminary surface guessed too low still gets its grass. The White Mountains
                // are treated the same way, since their snow cap and their stone are one surface.
                blueMountainsFloor(),
                blueMountainsRock(),
                whiteMountainsFloor(),
                whiteMountainsRock());
	}

    /**
     * Arda's own surface, laid without the preliminary-surface guard.
     *
     * <p>That guard asks how high the noise's own density says the ground is, which is a fair guess
     * in the overworld and meaningless in Arda: Arda keeps the overworld's noise settings but cuts
     * its terrain to {@link ArdaMap}, so a drawn plain under a noise mountain sits far below the
     * height the guard is measured against. Every block of such a column failed the guard, every
     * material rule was skipped with it, and the ground came out as the stone the generator wrote.
     * Arda therefore lays the same materials unguarded - the drawn ground is the surface, and the
     * generator leaves no cave inside a column for them to dress by mistake - and follows them with
     * the vanilla rule, which is what a TerraBlender dimension falls back to anyway: it gives the
     * vanilla biomes of the map (its seas, shores and rivers) their sand and gravel, and it supplies
     * the bedrock floor and the deepslate that the materials leave alone.
     */
    public static SurfaceRules.RuleSource ardaSurface() {
        return SurfaceRules.sequence(
                materials(),
                blueMountainsFloor(),
                blueMountainsRock(),
                whiteMountainsFloor(),
                whiteMountainsRock(),
                SurfaceRuleData.overworld());
    }

    /** Every material the mod's biomes wear, without the guard the overworld needs around them. */
    private static SurfaceRules.RuleSource materials() {
        return SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.NURN), nurnSurface()),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.MORDOR), DARK_STONE),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.HARADWAITH), SAND),
                                ironHillsSurface(), hithaeglirSurface(), whiteMountainsFloor(), makeTolkienWorldRules())),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.NURN, TolkienBiomes.MORDOR, TolkienBiomes.ASH_MOUNTAINS), DARK_STONE),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.HARADWAITH), SANDSTONE),
                                SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.HITHAEGLIR), PACKED_ICE))),
                        blueMountainsFloor(),
                        blueMountainsRock(),
                        whiteMountainsRock(),
                        overworldLikeFloor());
    }

	/**
	 * Nurn's floor is dark stone, with the surface noise that shapes the badlands breaking it up into
	 * patches of cobbled dark stone. Grass only grows near water: along the low ground that sits a
	 * few blocks above the water table, which is where the flooded lowland's shores and islands are,
	 * and around a lake, which lays its own meadow out to a chunk from its water at whatever height
	 * it sits. Only ground with no water over it is coloured, so a lake or a spring never gets a
	 * stone lip placed over its own surface.
	 */
	private static SurfaceRules.RuleSource nurnSurface() {
		return SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(NURN_SHORE_HEIGHT), 0)), GRASS_BLOCK),
				SurfaceRules.ifTrue(surfaceNoiseAbove(NURN_COBBLE_NOISE), COBBLED_DARK_STONE),
				DARK_STONE));
	}

	/**
	 * Nurn's floor is grass below this height and dark stone above it. The water table is sea level,
	 * so everything here stands at most a few blocks above water: the low shores and islands of the
	 * flooded lowland. A lake above that height lays its own grassland around itself instead.
	 */
	private static final int NURN_SHORE_HEIGHT = 68;

	/**
	 * The surface noise value above which Nurn's floor is cobbled dark stone instead of plain dark
	 * stone. It is texture only; both are dry ground.
	 */
	private static final double NURN_COBBLE_NOISE = 1.2D;

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

	/**
	 * The Blue Mountains stand on the mod's own mountain stone instead of vanilla stone: grass on the
	 * floor, mountain stone in every block beneath it. The surface system only ever rewrites a block
	 * that is still the generator's default - stone - so the rock rule can simply claim the whole
	 * column between it and the deepslate layer, which the dimension still supplies.
	 *
	 * <p>The two rules are separate because the rock rule would otherwise claim the floor as well and
	 * leave the biome bare stone, the way Mordor is. The floor rule comes first so the floor keeps its
	 * grass; everything under it - including the dirt band the rest of the world gets - is rock.
	 * Both are called inside and outside the preliminary-surface guard, so the colour of the floor
	 * does not depend on how well that height estimate guessed the mountain's feet.
	 */
	private static SurfaceRules.RuleSource blueMountainsFloor() {
		SurfaceRules.RuleSource grassGrows = SurfaceRules.ifTrue(
				SurfaceRules.waterBlockCheck(-1, 0),
				SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(-4), 1), GRASS_BLOCK));
		SurfaceRules.RuleSource floor = SurfaceRules.sequence(grassGrows, DIRT);
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.BLUE_MOUNTAINS),
				SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, floor));
	}    private static SurfaceRules.RuleSource blueMountainsRock() {
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.BLUE_MOUNTAINS), MOUNTAIN_STONE);
	}

	/**
	 * Ered Nimrais is the one range the mod paints white: snow on the level ground, bare stone on the
	 * steep faces and under the snow, and the range's own stone beneath both, so the wall reads as
	 * limestone under snow rather than as the Misty Mountains' ice.
	 */
	private static SurfaceRules.RuleSource whiteMountainsFloor() {
		SurfaceRules.RuleSource floor = SurfaceRules.sequence(
				SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(-1, 0), SurfaceRules.sequence(
						SurfaceRules.ifTrue(SurfaceRules.steep(), STONE),
						SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SURFACE, -0.35, 0.2), STONE),
						SNOW_BLOCK)),
				GRAVEL);
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.WHITE_MOUNTAINS),
				SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, floor));
	}

	private static SurfaceRules.RuleSource whiteMountainsRock() {
		return SurfaceRules.ifTrue(SurfaceRules.isBiome(TolkienBiomes.WHITE_MOUNTAINS), MOUNTAIN_STONE);
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
