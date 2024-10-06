package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TolkienSignBlockEntity extends SignBlockEntity {
    public TolkienSignBlockEntity(BlockPos pos, BlockState state) {
        super(TolkienBlocks.TOLKIEN_SIGN_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TolkienBlocks.TOLKIEN_SIGN_BLOCK_ENTITY.get();
    }
}
