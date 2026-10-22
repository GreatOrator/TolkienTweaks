package com.greatorator.tolkienmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TolkienHardenedLeavesBlock extends Block {
	private Block leafBlock;
	public TolkienHardenedLeavesBlock(Properties properties, Block leafBlock) {
		super(properties);
		this.leafBlock = leafBlock;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader reader, BlockPos pos, BlockState state, boolean includeData, Player player) {
		return new ItemStack(leafBlock);
	}
}
