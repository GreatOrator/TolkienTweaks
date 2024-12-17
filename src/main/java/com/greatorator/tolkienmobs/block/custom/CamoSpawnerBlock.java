package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.custom.entity.CamoSpawnerBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class CamoSpawnerBlock extends ChameleonBlock<CamoSpawnerBlockEntity> {
    public static final MapCodec<CamoSpawnerBlock> CODEC = simpleCodec(CamoSpawnerBlock::new);

    public CamoSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    // Entity Stuff
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CamoSpawnerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(
                blockEntityType, TolkienBlocks.CAMO_SPAWNER_BLOCK_ENTITY.get(), level.isClientSide ? CamoSpawnerBlockEntity::clientTick : CamoSpawnerBlockEntity::serverTick
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Spawner.appendHoverText(stack, tooltipComponents, "SpawnData");
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && Screen.hasShiftDown() && player.isCreative()) {
            if (level.getBlockEntity(blockPos) instanceof CamoSpawnerBlockEntity blockEntity) {
                player.openMenu(blockEntity, blockPos);
            }
        }
        return InteractionResult.SUCCESS;
    }
}