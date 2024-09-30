package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PlacardEntity extends BlockEntity {
    public PlacardEntity(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    public PlacardEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), pos, state);
    }
}
