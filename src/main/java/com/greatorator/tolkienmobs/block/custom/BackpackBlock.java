package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienEntityBlock;
import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.BackpackBlockContainer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class BackpackBlock extends TolkienEntityBlock {
    public static final MapCodec<BackpackBlock> CODEC = simpleCodec(BackpackBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    protected static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 5.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 12.0D);
    protected static final VoxelShape SHAPE_EAST = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_WEST = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 14.0D, 15.0D);
    protected static final VoxelShape SHAPE_COMMON = Block.box(1.0D, 0.0D, 1.0D, 15.0, 14.0D, 15.0D);

    public BackpackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
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
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BackpackBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    /** Creative mode skips loot tables, so drop a backpack carrying the contents ourselves (like shulker boxes). */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative() && level.getBlockEntity(pos) instanceof BackpackBlockEntity blockEntity && blockEntity.hasContents()) {
            ItemStack stack = new ItemStack(this);
            stack.applyComponents(blockEntity.collectComponents());
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(blockPos) instanceof BackpackBlockEntity blockEntity) {
                player.openMenu(blockEntity, buf -> {
                    buf.writeByte(BackpackBlockContainer.SOURCE_BLOCK);
                    buf.writeBlockPos(blockPos);
                });
            }
        }
        return InteractionResult.SUCCESS;
    }

        // Fluid Handling
    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (level.isClientSide()) return InteractionResult.TRY_WITH_EMPTY_HAND;
        ResourceHandler<FluidResource> itemTank = ItemAccess.forPlayerInteraction(player, hand).getCapability(Capabilities.Fluid.ITEM);
        if (itemTank == null) return InteractionResult.TRY_WITH_EMPTY_HAND;
        ResourceHandler<FluidResource> blockTank = level.getCapability(Capabilities.Fluid.BLOCK, blockPos, blockHitResult.getDirection());
        if (blockTank == null) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (transfer(blockTank, itemTank) > 0) {
            level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        if (transfer(itemTank, blockTank) > 0) {
            level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    /** Moves fluid between the held container and the pack's tank, committing only if something moved. */
    private static int transfer(ResourceHandler<FluidResource> source, ResourceHandler<FluidResource> sink) {
        try (Transaction transaction = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(source, sink, resource -> true, Integer.MAX_VALUE, transaction);
            if (moved > 0) {
                transaction.commit();
            }
            return moved;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof BackpackBlockEntity tile) {
                    tile.tickClient();
                }
            };
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof BackpackBlockEntity tile) {
                tile.tickServer();
            }
        };
    }
}