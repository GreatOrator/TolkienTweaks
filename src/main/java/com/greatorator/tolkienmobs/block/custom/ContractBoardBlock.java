package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.faction.MercenaryContracts;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The board mercenary work is posted on and handed back to.
 * <p>
 * This is a placeholder standing in for the real notice board: an ordinary full block, drawn with a
 * vanilla texture, with no screen and no model of its own. What it does is the whole loop, though -
 * right-clicking it pays a finished contract or posts a fresh one - so the feature can be played and
 * tested before the board has art, a menu and the three offers a day it is meant to show.
 */
public class ContractBoardBlock extends Block {
    public static final MapCodec<ContractBoardBlock> CODEC = simpleCodec(ContractBoardBlock::new);

    public ContractBoardBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            MercenaryContracts.useBoard(serverPlayer);
        }
        return InteractionResult.CONSUME;
    }
}
