package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienEntityBlock;
import com.greatorator.tolkienmobs.block.custom.entity.AncientWatcherDeadStatueBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.util.MathUtility;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AncientWatcherDeadStatueBlock extends TolkienEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<AncientWatcherDeadStatueBlock> CODEC = simpleCodec(AncientWatcherDeadStatueBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private long nextAbilityUse = 0L;
    private final static long coolDown = 15000L;

    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public AncientWatcherDeadStatueBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighbor, LevelAccessor world, BlockPos pos, BlockPos offset) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = world.getFluidState(pos);
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        long time = System.currentTimeMillis();

        if (!level.isClientSide()) {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                if (level.getBlockEntity(blockPos) instanceof AncientWatcherDeadStatueBlockEntity) {
                    int rand = MathUtility.getRandomInteger(100, 1);
                    Random random = new Random();

                    double randomXOffset = 0.3 + (0.6 - 0.3) * random.nextDouble();
                    double randomZOffset = 0.3 + (0.6 - 0.3) * random.nextDouble();

                    double newX = blockPos.getX() + randomXOffset;
                    double newY = blockPos.getY() - 0.25;
                    double newZ = blockPos.getZ() + randomZOffset;

                    ItemEntity dropActiveHeart = new ItemEntity(level, newX, newY, newZ, new ItemStack(TolkienItems.ITEM_WATCHERHEART.get(), 1));
                    ItemEntity dropCrackedHeart = new ItemEntity(level, newX, newY, newZ, new ItemStack(TolkienItems.ITEM_WATCHERHEART_CRACKED.get(), 1));

                    if (time > nextAbilityUse) {
                        nextAbilityUse = time + coolDown;
                        if (rand <= 10) {
                            level.addFreshEntity(dropActiveHeart);
                        } else {
                            level.addFreshEntity(dropCrackedHeart);
                        }
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    /** Entity Stuff */
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AncientWatcherDeadStatueBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof AncientWatcherDeadStatueBlockEntity tile) {
                    tile.tickClient();
                }
            };
        }
        return (lvl, pos, blockState, t) -> {
            if (t instanceof AncientWatcherDeadStatueBlockEntity tile) {
                tile.tickServer();
            }
        };
    }
}
