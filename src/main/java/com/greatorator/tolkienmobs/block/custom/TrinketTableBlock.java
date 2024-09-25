package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableEntity;
import com.greatorator.tolkienmobs.containers.TrinketTableContainer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;

public class TrinketTableBlock extends TolkienBlock implements EntityBlock {
    public static final MapCodec<TrinketTableBlock> CODEC = simpleCodec(TrinketTableBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    protected static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(3, 0, 3, 13, 1, 13),
            Block.box(6, 1, 6, 10, 9, 10),
            Block.box(2, 7.9, 2, 14, 9.9, 14),
            Block.box(2.1, 9.9, 2.200000000000003, 13.9, 15.9, 4.200000000000003),
            Block.box(2.2, 9, 3.8000000000000007, 3.2, 12, 13.8),
            Block.box(12.8, 9, 3.8000000000000007, 13.8, 12, 13.8),
            Block.box(5.5, 9, 4.500000000000002, 10.5, 12, 9.500000000000002),
            Block.box(5.75, 11.5, 4.8000000000000025, 10.25, 12.5, 9.200000000000001),
            Block.box(9.7, 9.799999999999999, 10.300000000000006, 12.7, 11.799999999999999, 13.299999999999999),
            Block.box(3.3, 9.8, 10.299999999999999, 6.3, 11.8, 13.3),
            Block.box(6.5, 9.8, 10.300000000000002, 9.5, 11.8, 13.299999999999999),
            Block.box(7, 13.1, 4.300000000000001, 9, 15.1, 4.300000000000001),
            Block.box(5.4, 6.299999999999999, 4.9, 10.6, 8.299999999999999, 10.9)
    );
    protected static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(3, 0, 3, 13, 1, 13),
            Block.box(6, 1, 6, 10, 9, 10),
            Block.box(2, 7.9, 2, 14, 9.9, 14),
            Block.box(2.1, 9.9, 11.799999999999997, 13.9, 15.9, 13.799999999999997),
            Block.box(2.2, 9, 2.2, 3.2, 12, 12.2),
            Block.box(12.8, 9, 2.2, 13.8, 12, 12.2),
            Block.box(5.5, 9, 6.499999999999998, 10.5, 12, 11.499999999999998),
            Block.box(5.75, 11.5, 6.799999999999999, 10.25, 12.5, 11.199999999999998),
            Block.box(9.7, 9.799999999999999, 2.7000000000000015, 12.7, 11.799999999999999, 5.699999999999994),
            Block.box(3.3, 9.8, 2.6999999999999997, 6.3, 11.8, 5.700000000000001),
            Block.box(6.5, 9.8, 2.7000000000000006, 9.5, 11.8, 5.6999999999999975),
            Block.box(7, 13.1, 11.7, 9, 15.1, 11.7),
            Block.box(5.4, 6.299999999999999, 5.1, 10.6, 8.299999999999999, 11.1)
    );
    protected static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(3, 0, 3, 13, 1, 13),
            Block.box(6, 1, 6, 10, 9, 10),
            Block.box(2, 7.9, 2, 14, 9.9, 14),
            Block.box(11.799999999999997, 9.9, 2.0999999999999996, 13.799999999999997, 15.9, 13.9),
            Block.box(2.1999999999999993, 9, 2.2, 12.2, 12, 3.2),
            Block.box(2.1999999999999993, 9, 12.8, 12.2, 12, 13.8),
            Block.box(6.499999999999998, 9, 5.5, 11.499999999999998, 12, 10.5),
            Block.box(6.799999999999999, 11.5, 5.75, 11.199999999999998, 12.5, 10.25),
            Block.box(2.700000000000001, 9.799999999999999, 9.7, 5.699999999999994, 11.799999999999999, 12.7),
            Block.box(2.6999999999999993, 9.8, 3.3, 5.700000000000001, 11.8, 6.3),
            Block.box(2.700000000000001, 9.8, 6.5, 5.6999999999999975, 11.8, 9.5),
            Block.box(11.7, 13.1, 7, 11.7, 15.1, 9),
            Block.box(5.1, 6.299999999999999, 5.4, 11.1, 8.299999999999999, 10.6)
    );
    protected static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(3, 0, 3, 13, 1, 13),
            Block.box(6, 1, 6, 10, 9, 10),
            Block.box(2, 7.9, 2, 14, 9.9, 14),
            Block.box(2.200000000000003, 9.9, 2.0999999999999996, 4.200000000000003, 15.9, 13.9),
            Block.box(3.8000000000000007, 9, 2.2, 13.8, 12, 3.2),
            Block.box(3.8000000000000007, 9, 12.8, 13.8, 12, 13.8),
            Block.box(4.500000000000002, 9, 5.5, 9.500000000000002, 12, 10.5),
            Block.box(4.8000000000000025, 11.5, 5.75, 9.200000000000001, 12.5, 10.25),
            Block.box(10.300000000000006, 9.799999999999999, 9.7, 13.299999999999999, 11.799999999999999, 12.7),
            Block.box(10.299999999999999, 9.8, 3.3, 13.3, 11.8, 6.3),
            Block.box(10.300000000000002, 9.8, 6.5, 13.299999999999999, 11.8, 9.5),
            Block.box(4.300000000000001, 13.1, 7, 4.300000000000001, 15.1, 9),
            Block.box(4.9, 6.299999999999999, 5.4, 10.9, 8.299999999999999, 10.6)
    );

    public TrinketTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        switch((Direction)state.getValue(FACING)) {
            default:
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING).getOpposite()));
    }

    /** Entity Stuff */
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (worldIn.getBlockEntity(pPos) instanceof TrinketTableEntity blockEntity) {
                if (worldIn instanceof ServerLevel serverLevel) {
                    blockEntity.drops(serverLevel);
                    Block.popResource(worldIn, pPos, new ItemStack(TolkienBlocks.TRINKET_TABLE));
                }
            }
        }

        super.onRemove(state, worldIn, pPos, newState, isMoving);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(blockPos) instanceof TrinketTableEntity trinketTable)
            {
                player.openMenu(trinketTable, blockPos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TrinketTableEntity(blockPos, blockState);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
//        if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
//            if (pState.getValue(IcariaBlockStateProperties.KETTLE) == Kettle.ACTIVE) {
//                pLevel.addParticle(IcariaParticleTypes.STEAM.get(), pPos.getX() + this.getX(pState) + pRandom.nextDouble() / 8.0D * (pRandom.nextBoolean() ? 1 : -1), pPos.getY() + 0.875D, pPos.getZ() + this.getZ(pState) + pRandom.nextDouble() / 8.0D * (pRandom.nextBoolean() ? 1 : -1), 0.0D, 0.0D, 0.0D);
//                pLevel.addParticle(ParticleTypes.SMALL_FLAME, pPos.getX() + this.getX(pState) + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1), pPos.getY() + 0.125D, pPos.getZ() + this.getZ(pState) + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1), 0.0D, 0.0D, 0.0D);
//                if (IcariaConfig.KETTLE_SOUNDS.get() && pRandom.nextDouble() < 0.1D) {
//                    pLevel.playLocalSound(pPos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
//                }
//            } else if (pState.getValue(IcariaBlockStateProperties.KETTLE) == Kettle.BREWING) {
//                pLevel.addParticle(IcariaParticleTypes.BUBBLE.get(), pPos.getX() + this.getX(pState) + pRandom.nextDouble() / 8.0D * (pRandom.nextBoolean() ? 1 : -1), pPos.getY() + 0.75D, pPos.getZ() + this.getZ(pState) + pRandom.nextDouble() / 8.0D * (pRandom.nextBoolean() ? 1 : -1), 0.0D, 0.0D, 0.0D);
//                pLevel.addParticle(IcariaParticleTypes.STEAM.get(), pPos.getX() + this.getX(pState) + pRandom.nextDouble() / 8.0D * (pRandom.nextBoolean() ? 1 : -1), pPos.getY() + 0.875D, pPos.getZ() + this.getZ(pState) + pRandom.nextDouble() / 8.0D * (pRandom.nextBoolean() ? 1 : -1), 0.0D, 0.0D, 0.0D);
//                pLevel.addParticle(ParticleTypes.SMALL_FLAME, pPos.getX() + this.getX(pState) + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1), pPos.getY() + 0.125D, pPos.getZ() + this.getZ(pState) + pRandom.nextDouble() / 4.0D * (pRandom.nextBoolean() ? 1 : -1), 0.0D, 0.0D, 0.0D);
//                if (IcariaConfig.KETTLE_SOUNDS.get() && pRandom.nextDouble() < 0.1D) {
//                    pLevel.playLocalSound(pPos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
//                } else if (IcariaConfig.KETTLE_SOUNDS.get() && pRandom.nextDouble() > 0.9D) {
//                    pLevel.playLocalSound(pPos, IcariaSoundEvents.KETTLE_CONCOCT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
//                }
//            }
//        }
    }
}
