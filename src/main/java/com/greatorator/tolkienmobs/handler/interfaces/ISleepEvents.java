package com.greatorator.tolkienmobs.handler.interfaces;

import com.greatorator.tolkienmobs.handler.capability.ISleepData;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

public interface ISleepEvents {

  Player.BedSleepingProblem getSleepResult(ServerPlayer player, BlockPos pos);

  Either<Player.BedSleepingProblem, Unit> getSleepResult(ServerPlayer player, BlockPos pos,
                                                         Either<Player.BedSleepingProblem, Unit> vanillaResult);

  boolean isAwakeTime(Player player, BlockPos pos);

  Optional<? extends ISleepData> getSleepData(Player player);

  void sendAutoSleepPacket(ServerPlayer player, BlockPos pos);

  void sendPlaceBagPacket(ServerPlayer serverPlayer, UseOnContext context);
}
