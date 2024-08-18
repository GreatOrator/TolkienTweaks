package com.greatorator.tolkienmobs.block.custom.entity;
//
//import com.greatorator.tolkienmobs.init.TolkienBlocks;
//import com.greatorator.tolkienmobs.init.TolkienEntities;
//import javax.annotation.Nonnull;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.DyeColor;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class SleepingBagBlockEntity extends TolkienBedEntity {
//
//  public SleepingBagBlockEntity(BlockPos pos, BlockState state) {
//    super(TolkienBlocks.SLEEPING_BAG_BLOCK_ENTITY.get(), pos, state);
//  }
//
//  public SleepingBagBlockEntity(BlockPos pos, BlockState state, DyeColor colorIn) {
//    super(TolkienBlocks.SLEEPING_BAG_BLOCK_ENTITY.get(), pos, state, colorIn);
//  }
//
//  @Nonnull
//  @Override
//  public Component getName() {
//    return this.name != null ? this.name : Component.translatable(
//        "block.tolkienmobs.sleeping_bag_" + this.getColor().getSerializedName());
//  }
//}
