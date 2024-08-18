package com.greatorator.tolkienmobs.block.custom;
//
//import javax.annotation.Nonnull;
//
//import com.greatorator.tolkienmobs.TolkienMobsConfig;
//import com.greatorator.tolkienmobs.block.TolkienBedBlock;
//import com.greatorator.tolkienmobs.block.custom.entity.SleepingBagBlockEntity;
//import com.greatorator.tolkienmobs.block.custom.entity.TolkienBedEntity;
//import com.greatorator.tolkienmobs.handler.TolkienRegistry;
//import com.greatorator.tolkienmobs.init.TolkienBlocks;
//import com.greatorator.tolkienmobs.init.TolkienEntities;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.item.DyeColor;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.SoundType;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.material.MapColor;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.VoxelShape;
//
//public class SleepingBagBlock extends TolkienBedBlock {
//
//  private static final VoxelShape SLEEPING_BAG_SHAPE = Block
//      .box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
//  private final DyeColor color;
//
//  public SleepingBagBlock(DyeColor color) {
//    super(BedType.SLEEPING_BAG, color,
//        Block.Properties.of().ignitedByLava().mapColor(MapColor.WOOL).sound(SoundType.WOOL)
//            .strength(0.1F));
//    this.color = color;
//  }
//
//  @Nonnull
//  @Override
//  public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn,
//                             @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
//    return SLEEPING_BAG_SHAPE;
//  }
//
//  @Override
//  public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
//    return new SleepingBagBlockEntity(pos, state, this.color);
//  }
//
//  @Override
//  protected boolean canRest() {
//    return TolkienMobsConfig.sleepingBagsStopPhantoms;
//  }
//
//  @Override
//  public BlockEntityType<? extends TolkienBedEntity> getBlockEntityType() {
//    return TolkienBlocks.SLEEPING_BAG_ENTITY.get();
//  }
//}
