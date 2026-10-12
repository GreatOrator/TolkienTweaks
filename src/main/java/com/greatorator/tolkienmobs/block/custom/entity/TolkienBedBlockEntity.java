package com.greatorator.tolkienmobs.block.custom.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TolkienBedBlockEntity extends BlockEntity implements Nameable {

  private DyeColor color;
  @Nullable
  protected Component name;

  public TolkienBedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos,
                               BlockState state) {
    super(blockEntityType, pos, state);
  }

  public TolkienBedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state,
                               DyeColor colorIn) {
    this(blockEntityType, pos, state);
    this.setColor(colorIn);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  public DyeColor getColor() {

    if (this.color == null) {
      this.color = ((BedBlock) this.getBlockState().getBlock()).getColor();
    }
    return this.color;
  }

  public void setColor(DyeColor color) {
    this.color = color;
  }

  @Override
  public boolean hasCustomName() {
    return this.name != null;
  }

  @Nullable
  @Override
  public Component getCustomName() {
    return this.name;
  }

  public void setName(@Nonnull Component name) {
    this.name = name;
  }
}
