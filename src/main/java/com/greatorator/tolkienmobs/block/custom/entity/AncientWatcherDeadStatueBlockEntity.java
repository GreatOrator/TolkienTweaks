package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AncientWatcherDeadStatueBlockEntity extends TolkienBlockEntity {
    public AncientWatcherDeadStatueBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.ANCIENT_WATCHER_DEAD_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void tickClient() {
    }

    public void tickServer() {

    }
}
