package com.greatorator.tolkienmobs.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The little squared crown on top of the post, two pixels of it sitting on the shaft's own top. It
 * is the only part of the post that does not fill its block, and it goes nowhere but on a shaft.
 */
public class SignPostCapBlock extends Block {
    public static final VoxelShape SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 2.0D, 9.5D);

    public SignPostCapBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof SignPostShaftBlock;
    }
}
