package com.greatorator.tolkienmobs.handler.interfaces;

import java.util.Optional;

import com.greatorator.tolkienmobs.block.custom.entity.ChameleonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface IChameleon {
	public static BlockState getChameleonStateOrDefault(BlockState state, BlockGetter level, BlockPos pos) {
		if (level instanceof LevelReader reader)
			return getChameleonBlockState(reader, Optional.ofNullable(state), pos).orElse(state);
		else
			return state;
	}

	public static Optional<BlockState> getChameleonBlockState(LevelReader level, Optional<BlockState> state, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ChameleonBlockEntity be)
			return getChameleonBlockState((LevelReader) level.holderLookup(Registries.BLOCK), be.selectBestAdjacentBlock(level, pos), pos);

		return Optional.empty();
	}
}