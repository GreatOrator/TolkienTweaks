package com.greatorator.tolkienmobs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The foot of a sign post: a broad stone plinth with the first course of the post standing on it.
 * Both are exactly one block high, so a shaft sits on the plinth flush.
 * <p>
 * The post is scenery, not a thing to be moved, so it is as hard as bedrock and drops nothing; the
 * whole point of planting one is that it stays planted.
 */
public class SignPostPlinthBlock extends Block {
    public static final VoxelShape SHAPE = Shapes.or(
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D),
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D),
            Block.box(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D)
    );

    public SignPostPlinthBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }
}
