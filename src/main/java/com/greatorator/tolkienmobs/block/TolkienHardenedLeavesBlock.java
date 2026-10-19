package com.greatorator.tolkienmobs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class TolkienHardenedLeavesBlock extends Block {
	private Block leafBlock;
	public TolkienHardenedLeavesBlock(Properties properties, Block leafBlock) {
		super(properties);
		this.leafBlock = leafBlock;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult result, LevelReader reader, BlockPos pos, Player player) {
		return new ItemStack(leafBlock);
	}
}
