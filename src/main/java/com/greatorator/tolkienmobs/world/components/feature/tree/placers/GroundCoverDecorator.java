package com.greatorator.tolkienmobs.world.components.feature.tree.placers;

import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

/**
 * Recolours the soil under a tree - the podzol carpet of Fangorn and the Old Forest.
 * <p>
 * 26.1's own {@code AlterGroundDecorator} writes at the top of a short scan above the lowest trunk
 * block whatever its provider hands back, and a plain provider always hands back a state, so the
 * carpet ended up in the air a block or two above the grass with the whole tree standing on it. This
 * decorator scans down instead and only ever replaces the highest solid block of a column, and only
 * when that block is soil: a trunk log, a leaf or a stone face stops the scan untouched.
 */
public class GroundCoverDecorator extends TreeDecorator {
    public static final MapCodec<GroundCoverDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("provider").forGetter(o -> o.provider)
    ).apply(instance, GroundCoverDecorator::new));

    private final BlockStateProvider provider;

    public GroundCoverDecorator(BlockStateProvider provider) {
        this.provider = provider;
    }

    @Override
    protected TreeDecoratorType<GroundCoverDecorator> type() {
        return TolkienFeatureModifiers.GROUND_COVER.get();
    }

    @Override
    public void place(Context context) {
        List<BlockPos> base = TreeFeature.getLowestTrunkOrRootOfTree(context);
        if (base.isEmpty()) {
            return;
        }
        int minY = base.getFirst().getY();
        base.stream().filter(pos -> pos.getY() == minY).forEach(pos -> {
            placeCircle(context, pos.west().north());
            placeCircle(context, pos.east(2).north());
            placeCircle(context, pos.west().south(2));
            placeCircle(context, pos.east(2).south(2));

            // A ragged edge, so the carpet does not read as a tidy square.
            for (int i = 0; i < 5; i++) {
                int placement = context.random().nextInt(64);
                int x = placement % 8;
                int z = placement / 8;
                if (x == 0 || x == 7 || z == 0 || z == 7) {
                    placeCircle(context, pos.offset(-3 + x, 0, -3 + z));
                }
            }
        });
    }

    private void placeCircle(Context context, BlockPos centre) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) != 2 || Math.abs(z) != 2) {
                    placeBlockAt(context, centre.offset(x, 0, z));
                }
            }
        }
    }

    private void placeBlockAt(Context context, BlockPos pos) {
        for (int dy = 2; dy >= -4; dy--) {
            BlockPos cursor = pos.above(dy);
            BlockState state = context.level().getBlockState(cursor);
            // Grass, flowers and a snow layer sit above the soil and are no reason to stop looking.
            if (state.isAir() || state.canBeReplaced()) {
                continue;
            }
            if (state.is(BlockTags.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM)) {
                context.setBlock(cursor, this.provider.getState(context.level(), context.random(), cursor));
            }
            return;
        }
    }
}
