package com.greatorator.tolkienmobs.block;

import com.greatorator.tolkienmobs.block.custom.entity.TolkienSignEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class TolkienSignBlock extends StandingSignBlock {
    public TolkienSignBlock(WoodType type, Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TolkienSignEntity(pos, state);
    }
}