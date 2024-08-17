package com.greatorator.tolkienmobs.world.tree.placers;

import com.google.common.collect.Lists;
import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.greatorator.tolkienmobs.util.FeatureLogic;
import com.greatorator.tolkienmobs.util.VoxelBresenhamIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BranchingLargeTrunkPlacer extends TrunkPlacer {
	public static final MapCodec<BranchingLargeTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> trunkPlacerParts(instance).and(instance.group(
		Codec.intRange(0, 24).fieldOf("branch_start_offset_down").forGetter(o -> o.branchDownwardOffset),
		BranchesConfig.CODEC.fieldOf("branch_config").forGetter(o -> o.branchesConfig),
		Codec.BOOL.fieldOf("perpendicular_branches").forGetter(o -> o.perpendicularBranches)
	)).apply(instance, BranchingLargeTrunkPlacer::new));

	private final int branchDownwardOffset;
	private final BranchesConfig branchesConfig;
	private final boolean perpendicularBranches;

	public BranchingLargeTrunkPlacer(int baseHeight, int randomHeightA, int randomHeightB, int branchDownwardOffset, BranchesConfig branchesConfig, boolean perpendicularBranches) {
		super(baseHeight, randomHeightA, randomHeightB);
		this.branchDownwardOffset = branchDownwardOffset;
		this.branchesConfig = branchesConfig;
		this.perpendicularBranches = perpendicularBranches;
	}

	@Override
	protected TrunkPlacerType<BranchingLargeTrunkPlacer> type() {
		return TolkienFeatureModifiers.TRUNK_LARGE_BRANCHING.get();
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader worldReader, BiConsumer<BlockPos, BlockState> worldPlacer, RandomSource random, int height, BlockPos startPos, TreeConfiguration treeConfig) {
		List<FoliagePlacer.FoliageAttachment> leafAttachments = Lists.newArrayList();
		List<BlockPos> rootBlocks = Lists.newArrayList();

		setDirtAt(worldReader, worldPlacer, random, startPos.below(), treeConfig);
		setDirtAt(worldReader, worldPlacer, random, startPos.below().east(), treeConfig);
		setDirtAt(worldReader, worldPlacer, random, startPos.below().south(), treeConfig);
		setDirtAt(worldReader, worldPlacer, random, startPos.below().south().east(), treeConfig);
		BlockPos.MutableBlockPos mutableBoundingBox = new BlockPos.MutableBlockPos();
		Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
		int lvt_11_1_ = height - random.nextInt(4);
		int lvt_12_1_ = 2 - random.nextInt(3);
		int lvt_13_1_ = startPos.getX();
		int lvt_14_1_ = startPos.getY();
		int lvt_15_1_ = startPos.getZ();
		int lvt_16_1_ = lvt_13_1_;
		int lvt_17_1_ = lvt_15_1_;

		int lvt_19_2_;
		int lvt_20_2_;
		for(lvt_19_2_ = 0; lvt_19_2_ < height; ++lvt_19_2_) {
			if (lvt_19_2_ >= lvt_11_1_ && lvt_12_1_ > 0) {
				lvt_16_1_ += direction.getStepX();
				lvt_17_1_ += direction.getStepZ();
				--lvt_12_1_;
			}

			lvt_20_2_ = lvt_14_1_ + lvt_19_2_;
			BlockPos lvt_21_1_ = new BlockPos(lvt_16_1_, lvt_20_2_, lvt_17_1_);
			if (TreeFeature.isAirOrLeaves(worldReader, lvt_21_1_)) {
				placeLog(worldReader, worldPlacer, random, lvt_21_1_, treeConfig);
				placeLog(worldReader, worldPlacer, random, lvt_21_1_.east(), treeConfig);
				placeLog(worldReader, worldPlacer, random, lvt_21_1_.south(), treeConfig);
				placeLog(worldReader, worldPlacer, random, lvt_21_1_.east().south(), treeConfig);
			}
		}

		leafAttachments.add(new FoliagePlacer.FoliageAttachment(startPos.above(height), 0, false));
		rootBlocks.add(new BlockPos(startPos));

		int numBranches = branchesConfig.branchCount() + random.nextInt(branchesConfig.randomAddBranches() + 1);
		float offset = random.nextFloat();
		for (int b = 0; b < numBranches; b++) {
			buildBranch(worldReader, worldPlacer, startPos, leafAttachments, height - branchDownwardOffset + b, branchesConfig.length(), branchesConfig.spacingYaw() * b + offset, branchesConfig.downwardsPitch(), random, mutableBoundingBox, treeConfig, perpendicularBranches);
		}

		return leafAttachments;	}

	private void buildBranch(LevelSimulatedReader worldReader, BiConsumer<BlockPos, BlockState> worldPlacer, BlockPos pos, List<FoliagePlacer.FoliageAttachment> leafBlocks, int height, double length, double angle, double tilt, RandomSource treeRNG, BlockPos.MutableBlockPos mbb, TreeConfiguration config, boolean perpendicularBranches) {
		BlockPos src = pos.above(height);
		BlockPos dest = FeatureLogic.translate(src, length, angle, tilt);

		if (perpendicularBranches) {
			drawBresenhamBranch(worldReader, worldPlacer, treeRNG, src, new BlockPos(dest.getX(), src.getY(), dest.getZ()));

			int max = Math.max(src.getY(), dest.getY());

			for (int i = Math.min(src.getY(), dest.getY()); i < max + 1; i++) {
				placeWood(worldReader, worldPlacer, treeRNG, new BlockPos(dest.getX(), i, dest.getZ()));
			}
		} else {
			drawBresenhamBranch(worldReader, worldPlacer, treeRNG, src, dest);
		}

		placeWood(worldReader, worldPlacer, treeRNG, dest.east());
		placeWood(worldReader, worldPlacer, treeRNG, dest.west());
		placeWood(worldReader, worldPlacer, treeRNG, dest.south());
		placeWood(worldReader, worldPlacer, treeRNG, dest.north());

		leafBlocks.add(new FoliagePlacer.FoliageAttachment(dest, 0, false));
	}

	/**
	 * Draws a line from {x1, y1, z1} to {x2, y2, z2}
	 * This takes all variables for setting Branch
	 */
	private void drawBresenhamBranch(LevelSimulatedReader worldReader, BiConsumer<BlockPos, BlockState> worldPlacer, RandomSource random, BlockPos from, BlockPos to) {
		for (BlockPos pixel : new VoxelBresenhamIterator(from, to)) placeWood(worldReader, worldPlacer, random, pixel);
	}

	@SuppressWarnings("UnusedReturnValue")
	protected boolean placeWood(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos) {
		return this.placeWood(level, blockSetter, random, pos, Function.identity());
	}

	protected boolean placeWood(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, Function<BlockState, BlockState> propertySetter) {
		if (this.validTreePos(level, pos)) {
			blockSetter.accept(pos, propertySetter.apply(this.branchesConfig.branchProvider().getState(random, pos)));
			return true;
		} else return false;
	}
}
