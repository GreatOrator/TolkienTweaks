package com.greatorator.tolkienmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;

public class TolkienSaplingBlock extends SaplingBlock {
    private final Block block;

    public TolkienSaplingBlock(TreeGrower treeGrower, Properties properties, Block block) {
        super(treeGrower, properties);
        this.block = block;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.is(block);
    }
}