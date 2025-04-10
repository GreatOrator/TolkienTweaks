package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienEntityBlock;
import com.greatorator.tolkienmobs.block.custom.entity.TrinketTableBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

import javax.annotation.Nullable;

public class TrinketTableBlock extends TolkienEntityBlock {
    public static final Component TextComponent = Component.translatable("screen.tolkienmobs.block.tolkienmobs.trinket_table");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<TrinketTableBlock> CODEC = simpleCodec(TrinketTableBlock::new);
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
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /* Facing */
    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING).getOpposite()));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        switch(state.getValue(FACING)) {
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

    /* Entity Stuff */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TrinketTableBlockEntity(blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (worldIn.getBlockEntity(pPos) instanceof TrinketTableBlockEntity blockEntity) {
                blockEntity.drops();
            }
        }

        super.onRemove(state, worldIn, pPos, newState, isMoving);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult hit) {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(blockPos) instanceof TrinketTableBlockEntity trinketTable)
            {
                player.openMenu(trinketTable, blockPos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, TolkienBlocks.TRINKET_TABLE_BLOCK_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pState.getValue(LIT)) {
            return;
        }
        double xPos = (double)pPos.getX() + 0.5;
        double yPos = pPos.getY();
        double zPos = (double)pPos.getZ() + 0.5;
        if (pRandom.nextDouble() < 0.15) {
            pLevel.playLocalSound(xPos, yPos, zPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }

        Direction direction = pState.getValue(FACING);
        Direction.Axis axis = direction.getAxis();

        double defaultOffset = pRandom.nextDouble() * 0.6 - 0.3;
        double xOffsets = axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : defaultOffset;
        double yOffset = pRandom.nextDouble() * 6.0 / 8.0;
        double zOffset = axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : defaultOffset;

        pLevel.addParticle(ParticleTypes.SMOKE, xPos + xOffsets, yPos + yOffset, zPos + zOffset, 0.0, 0.0, 0.0);

        if(pLevel.getBlockEntity(pPos) instanceof TrinketTableBlockEntity blockEntity && !blockEntity.itemHandler.getStackInSlot(1).isEmpty()) {
            pLevel.addParticle(new ItemParticleOption(ParticleTypes.ITEM, blockEntity.itemHandler.getStackInSlot(1)),
                    xPos + xOffsets, yPos + yOffset, zPos + zOffset, 0.0, 0.0, 0.0);
        }
    }
}
