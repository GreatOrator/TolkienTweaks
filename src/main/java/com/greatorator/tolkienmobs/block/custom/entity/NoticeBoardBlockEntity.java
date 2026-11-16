package com.greatorator.tolkienmobs.block.custom.entity;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The notice board's block entity, which holds nothing at all.
 * <p>
 * A board keeps no state of its own: the paper it deals in lives in the player's pack, so what one board
 * has to say to one player is not what it has to say to another, and no blockstate could say it - a
 * blockstate is the same for everyone looking at the block. The entity exists only so the block can have
 * a renderer, because a mark hung over the board has to face whoever is reading it and be drawn from the
 * reader's own pack. It therefore carries no data, no ticker and no packets, and a board standing in a
 * world from before this was added is given one the next time its chunk is loaded.
 */
public class NoticeBoardBlockEntity extends TolkienBlockEntity {

    public NoticeBoardBlockEntity(BlockPos pos, BlockState state) {
        super(TolkienBlocks.NOTICE_BOARD_BLOCK_ENTITY.get(), pos, state);
    }
}
