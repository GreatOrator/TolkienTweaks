package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienEntityBlock;
import com.greatorator.tolkienmobs.block.custom.entity.PiggyBankBlockEntity;
import com.greatorator.tolkienmobs.containers.CoinPouchContainer;
import com.greatorator.tolkienmobs.containers.handlers.CoinPouchItemStackHandler;
import com.greatorator.tolkienmobs.item.custom.CoinPouchItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;

public class PiggyBankBlock extends TolkienEntityBlock {
    public static final MapCodec<PiggyBankBlock> CODEC = simpleCodec(PiggyBankBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FULL = BooleanProperty.create("full");

    protected static final VoxelShape SHAPE_NORTH = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_EAST = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_WEST = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SHAPE_COMMON = Block.box(1.0, 0.0D, 1.0, 15.0, 14.0, 15.0);

    public PiggyBankBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FULL, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        switch(state.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FULL);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    /* Entity Stuff */
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PiggyBankBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    // 26.1 removed Block#onRemove; the block entity drops its slots itself in
    // BlockEntity#preRemoveSideEffects.

    /**
     * A loaded coin pouch tips its coins into the bank instead of opening either menu, which is how a
     * pouch was meant to bank a haul of coins. An empty pouch, or anything else in hand, still opens
     * the bank as before.
     */
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof CoinPouchItem) || CoinPouchItem.getCoinCount(stack) <= 0) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PiggyBankBlockEntity bank) {
            if (depositCoins(bank, stack)) {
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.3F, 0.9F);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /** Moves every coin the pouch holds into the bank, leaving whatever the bank could not take. */
    private static boolean depositCoins(PiggyBankBlockEntity bank, ItemStack pouchStack) {
        CoinPouchItemStackHandler pouch = new CoinPouchItemStackHandler(CoinPouchContainer.SLOTS, pouchStack);
        boolean moved = false;
        for (int slot = 0; slot < pouch.size(); slot++) {
            ItemStack coins = ItemUtil.getStack(pouch, slot);
            if (coins.isEmpty()) continue;
            ItemStack leftover = ItemUtil.insertItemReturnRemaining(bank.itemHandler, coins.copy(), false, null);
            if (leftover.getCount() == coins.getCount()) continue;
            pouch.set(slot, leftover.isEmpty() ? ItemResource.EMPTY : ItemResource.of(leftover), leftover.getCount());
            moved = true;
        }
        if (moved) {
            bank.setChanged();
        }
        return moved;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(blockPos) instanceof PiggyBankBlockEntity blockEntity) {
                player.openMenu(blockEntity, blockPos);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
