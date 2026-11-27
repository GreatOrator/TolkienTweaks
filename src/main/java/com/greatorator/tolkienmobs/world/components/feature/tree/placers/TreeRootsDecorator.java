package com.greatorator.tolkienmobs.world.components.feature.tree.placers;

import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;
import java.util.Optional;

/**
 * Lays a tree's roots out over the ground it stands on: strands out from the foot of the trunk, each following the
 * ground it meets, so a mallorn or a mirkwood oak is joined to its country by roots rather than planted in it.
 * <p>
 * It used to hand its strands to {@code FeaturePlacers.traceRoot}, which walks a line from one block <em>below</em> the
 * lowest trunk block and writes only where {@code FeatureLogic.canRootGrowIn} answers true - air with a solid
 * neighbour, or a replaceable block. The first position of every strand is the ground the tree was just planted on,
 * which is grass, dirt or stone, and none of those three is either, so every strand stopped at its first block and no
 * root was ever placed. The exposed path was no better: it is reached only through the constructor that names an
 * exposed-root provider, which {@code TolkienDecorators.LIVING_ROOTS} does not use, and the line it walks is through
 * open air beside a slope rather than along the ground.
 * <p>
 * So the strands are traced here instead, along the ground. Each one leaves the foot of the trunk on its own bearing,
 * steps out one column at a time, finds the top of that column within the step a root will climb or the drop it will
 * follow, and lays its root in the air above that ground. It stops at water, at stone that is not soil, at a column
 * whose top is already occupied - another trunk, a leaf, a wall - and where the ground falls away further than
 * {@link #DROP}, so a root never crosses a cliff and never floats.
 */
public class TreeRootsDecorator extends TreeDecorator {
	private static final SimpleStateProvider EMPTY = BlockStateProvider.simple(Blocks.AIR.defaultBlockState());

	/** How far a strand will follow the ground down in one step. It will climb one. */
	private static final int DROP = 2;

	/** One strand in five ends early, so the roots of one tree do not all reach the same way out. */
	private static final int RAGGED = 5;

	public static final MapCodec<TreeRootsDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.intRange(0, 16).fieldOf("base_strand_count").forGetter(o -> o.strands),
		Codec.intRange(0, 16).fieldOf("additional_random_strands").forGetter(o -> o.addExtraStrands),
		Codec.intRange(0, 32).fieldOf("root_length").forGetter(o -> o.length),
		Codec.INT.fieldOf("y_offset").forGetter(o -> o.yOffset),
		BlockStateProvider.CODEC.optionalFieldOf("exposed_roots_provider").forGetter(o -> Optional.ofNullable(o.surfaceBlock != EMPTY ? o.surfaceBlock : null)),
		BlockStateProvider.CODEC.fieldOf("ground_roots_provider").forGetter(o -> o.rootBlock),
		Codec.INT.fieldOf("rootPenetrability").forGetter(o -> o.rootPenetrability)
	).apply(instance, TreeRootsDecorator::new));

	private final int strands;
	private final int addExtraStrands;
	private final int length;
	private final int yOffset;
	private final BlockStateProvider surfaceBlock;
	private final BlockStateProvider rootBlock;

	private final boolean hasSurfaceRoots;
	private final int rootPenetrability;

	private TreeRootsDecorator(int count, int addExtraStrands, int length, int yOffset, Optional<BlockStateProvider> surfaceBlock, BlockStateProvider rootBlock, int rootPenetrability) {
		this.strands = count;
		this.addExtraStrands = addExtraStrands;
		this.length = length;
		this.yOffset = yOffset;
		this.rootBlock = rootBlock;
		this.rootPenetrability = rootPenetrability;
		this.hasSurfaceRoots = surfaceBlock.isPresent();

		if (this.hasSurfaceRoots) {
			this.surfaceBlock = surfaceBlock.get();
		} else {
			this.surfaceBlock = EMPTY;
		}
	}

	public TreeRootsDecorator(int count, int addExtraStrands, int length, BlockStateProvider rootBlock, int rootPenetrability) {
		this.strands = count;
		this.addExtraStrands = addExtraStrands;
		this.length = length;
		this.yOffset = 0;
		this.rootBlock = rootBlock;
		this.rootPenetrability = rootPenetrability;
		this.hasSurfaceRoots = false;
		this.surfaceBlock = EMPTY;
	}

	public TreeRootsDecorator(int count, int addExtraStrands, int length, int yOffset, BlockStateProvider surfaceBlock, BlockStateProvider rootBlock, int rootPenetrability) {
		this.strands = count;
		this.addExtraStrands = addExtraStrands;
		this.length = length;
		this.yOffset = yOffset;
		this.rootBlock = rootBlock;
		this.rootPenetrability = rootPenetrability;
		this.hasSurfaceRoots = true;
		this.surfaceBlock = surfaceBlock;
	}

	@Override
	protected TreeDecoratorType<TreeRootsDecorator> type() {
		return TolkienFeatureModifiers.TREE_ROOTS.get();
	}

	@Override
	public void place(Context context) {
		// The foot of the trunk, not the first log in the list: a decorator is handed the logs in no particular order,
		// and 26.1 keeps the helper that answers the lowest trunk block for exactly this.
		List<BlockPos> feet = TreeFeature.getLowestTrunkOrRootOfTree(context);
		if (feet.isEmpty())
			return;

		int strands = this.strands + context.random().nextInt(this.addExtraStrands + 1);
		if (strands <= 0)
			return;

		// One strand per bearing, the first turned by a random share, so two trees of the same shape do not grow
		// the same roots.
		BlockPos foot = feet.getFirst().above(this.yOffset);
		double turn = context.random().nextDouble();
		for (int strand = 0; strand < strands; strand++) {
			this.strand(context, foot, turn + (double) strand / strands);
		}
	}

	/** One root, from the foot of the trunk outward along one bearing, for as far as the ground allows. */
	private void strand(Context context, BlockPos foot, double bearing) {
		double angle = bearing * 2.0D * Math.PI;
		int dx = (int) Math.round(Math.sin(angle));
		int dz = (int) Math.round(Math.cos(angle));
		if (dx == 0 && dz == 0) dz = 1;

		BlockPos last = foot;
		for (int step = 1; step <= this.length; step++) {
			BlockPos ground = groundUnder(context, foot.getX() + dx * step, foot.getZ() + dz * step, last.getY());
			if (ground == null)
				return;

			BlockPos root = ground.above();
			context.setBlock(root, this.provider().getState(context.level(), context.random(), root));
			last = root;

			if (context.random().nextInt(RAGGED) == 0)
				return;
		}
	}

	/**
	 * The top of the ground in one column, as near the last root's height as the ground allows.
	 * <p>
	 * Only the earth a root may lie on answers: soil, stone, sand, moss, snow and gravel. Water, a trunk, a leaf, a
	 * wall, and the top of a column more than one block above or {@link #DROP} below the last root all answer null,
	 * which ends the strand.
	 */
	private static BlockPos groundUnder(Context context, int x, int z, int nearY) {
		for (int y = nearY; y >= nearY - DROP; y--) {
			BlockPos at = new BlockPos(x, y, z);
			BlockState state = context.level().getBlockState(at);
			if (state.isAir() || state.canBeReplaced())
				continue;
			if (!earth(state))
				return null;

			// Root only where the block over the ground is free: a flower or a tuft of grass is no reason to stop,
			// but a trunk, a leaf, a wall or water is.
			BlockState above = context.level().getBlockState(at.above());
			if (!above.isAir() && !above.canBeReplaced())
				return null;
			if (!above.getFluidState().isEmpty())
				return null;
			return at;
		}
		return null;
	}

	/** Whether a block is one a root may lie on: earth, rock and the loose ground of a shore. */
	private static boolean earth(BlockState state) {
		return state.is(BlockTags.DIRT) || state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.SAND)
			|| state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.GRAVEL);
	}

	/** The block a root is laid with: the exposed-root provider when a build names one, the ground provider otherwise. */
	private BlockStateProvider provider() {
		return this.hasSurfaceRoots ? this.surfaceBlock : this.rootBlock;
	}
}
