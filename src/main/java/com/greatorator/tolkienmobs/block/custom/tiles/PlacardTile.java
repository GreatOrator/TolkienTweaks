package com.greatorator.tolkienmobs.block.custom.tiles;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PlacardTile extends BlockEntity {

    public PlacardTile(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.PLACARD_TILE.get(), blockPos, blockState);
    }
    public PlacardTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(TolkienBlocks.PLACARD_TILE.get(), pos, state);
    }
}