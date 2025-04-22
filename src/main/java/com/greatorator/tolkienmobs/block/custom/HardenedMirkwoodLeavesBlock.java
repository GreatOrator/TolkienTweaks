package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class HardenedMirkwoodLeavesBlock extends Block {

	public HardenedMirkwoodLeavesBlock(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult result, LevelReader reader, BlockPos pos, Player player) {
		return new ItemStack(TolkienBlocks.LEAVES_MIRKWOOD.get());
	}
}
