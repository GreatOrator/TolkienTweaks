package com.greatorator.tolkienmobs.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TolkienBlockItem extends BlockItem {
    public TolkienBlockItem(Block block) {
        super(block, new Item.Properties());
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, @Nonnull BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 26);
    }
}
