package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PlacardBlockEntity extends TolkienBlockEntity {
    public PlacardBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    public PlacardBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), pos, state);
    }
}
