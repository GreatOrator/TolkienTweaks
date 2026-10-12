package com.greatorator.tolkienmobs.handler.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITicker {
  void serverTick(Level level, BlockPos pos, BlockState state);

  void clientTick(Level level, BlockPos pos, BlockState state);
}