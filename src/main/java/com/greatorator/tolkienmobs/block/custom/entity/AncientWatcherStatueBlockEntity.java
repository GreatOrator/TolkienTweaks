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

public class AncientWatcherStatueBlockEntity extends TolkienBlockEntity {
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    public AncientWatcherStatueBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TolkienBlocks.ANCIENT_WATCHER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void tickClient() {
    }

    public void tickServer() {
        BlockPos blockPos = this.worldPosition;
        Player player = Objects.requireNonNull(this.getLevel()).getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 16D, true);
        long time = System.currentTimeMillis();

        if (player != null) {
            if (!player.getInventory().contains(TolkienItems.RING_OF_HOPE.toStack())) {
                if (time > nextAbilityUse) {
                    nextAbilityUse = time + coolDown;
                    player.sendSystemMessage(Component.translatable("tolkienmobs.msg.ancient_watcher.warn").withStyle(ChatFormatting.RED));
                }
                player.addEffect(new MobEffectInstance(TolkienMobEffects.WATCHER_FEAR, 10, 0));
            }
            if (time > nextAbilityUse) {
                nextAbilityUse = time + coolDown;
                player.sendSystemMessage(Component.translatable("tolkienmobs.msg.ancient_watcher.safe").withStyle(ChatFormatting.GOLD));
            }
        }
    }
}
