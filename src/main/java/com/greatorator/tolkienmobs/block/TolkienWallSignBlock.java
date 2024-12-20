package com.greatorator.tolkienmobs.block;

import com.greatorator.tolkienmobs.block.custom.entity.TolkienSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TolkienWallSignBlock extends WallSignBlock {
    public TolkienWallSignBlock(WoodType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TolkienSignBlockEntity(pos, state);
    }
}
