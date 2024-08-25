package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TolkienHangingSignBlockEntity extends SignBlockEntity {
    public TolkienHangingSignBlockEntity(BlockPos pos, BlockState state) {
        super(TolkienBlocks.TOLKIEN_HANGING_SIGN_ENTITY.get(),pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return TolkienBlocks.TOLKIEN_HANGING_SIGN_ENTITY.get();
    }
}
