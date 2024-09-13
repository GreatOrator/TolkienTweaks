package com.greatorator.tolkienmobs.block;

import com.greatorator.tolkienmobs.block.custom.entity.TolkienHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TolkienHangingWallSignBlock extends WallHangingSignBlock {
    public TolkienHangingWallSignBlock(WoodType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TolkienHangingSignBlockEntity(pos, state);
    }
}