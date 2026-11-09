package com.greatorator.tolkienmobs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A reed: sugar cane's plant with the mod's own model.
 * <p>
 * What a reed needs of the world is what sugar cane needs of it - a bank, water beside it, and room to stand three
 * high - and none of that is worth writing twice. The block is the plant, the growth and the survival test; the
 * shape and the texture are the mod's own, so a stand of reeds in the Gladden reads as reeds.
 * <p>
 * One rule is the fen's and not the cane field's. Sugar cane wants water level with its own root, which is true of a
 * river bank and almost never of a marsh, where the pools sit a block or two below the tussocks between them. A reed
 * therefore takes its root on any wet-footed ground - dirt, sand, mud, clay, gravel - with water at its own level or
 * within a few blocks below it, which is what lets a stand of reeds find the Gladden's banks at all.
 */
public class TolkienReedBlock extends SugarCaneBlock {
    public TolkienReedBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        boolean wetFooted = below.is(BlockTags.DIRT) || below.is(BlockTags.SAND)
                || below.is(Blocks.MUD) || below.is(Blocks.MUDDY_MANGROVE_ROOTS)
                || below.is(Blocks.CLAY) || below.is(Blocks.GRAVEL);
        if (!wetFooted) {
            return false;
        }
        BlockPos root = pos.below();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int depth = 0; depth <= 3; depth++) {
                if (level.getFluidState(root.relative(direction).below(depth)).is(FluidTags.WATER)) {
                    return true;
                }
            }
        }
        return false;
    }
}
