package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class AncientWatcherDeadStatueBlockEntity extends TolkienBlockEntity {
    public AncientWatcherDeadStatueBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.ANCIENT_WATCHER_DEAD_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void tickClient() {
    }

    public void tickServer() {

    }
}
