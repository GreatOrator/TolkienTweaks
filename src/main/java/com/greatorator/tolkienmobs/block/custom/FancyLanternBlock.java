package com.greatorator.tolkienmobs.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class FancyLanternBlock extends Block implements SimpleWaterloggedBlock  {
    public static final MapCodec<FancyLanternBlock> CODEC = simpleCodec(FancyLanternBlock::new);
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape NON_HANGING_SHAPE = Shapes.or(
            Block.box(5.299999999999999, 0, 5.299999999999999, 10.7, 1, 10.7),
            Block.box(5, 1, 5, 11, 2, 11),
            Block.box(6.499999999999998, 1.4999999999999996, 6.499999999999998, 9.499999999999998, 2.4999999999999996, 9.499999999999998),
            Block.box(5.299999999999999, 2, 5.299999999999999, 10.700000000000001, 8, 10.700000000000001),
            Block.box(5, 8, 5, 11, 9, 11),
            Block.box(5.299999999999999, 9, 5.299999999999999, 10.7, 10, 10.7),
            Block.box(6.299999999999999, 10, 6.299999999999999, 9.7, 11, 9.7)
    );
    private static final VoxelShape HANGING_SHAPE = Shapes.or(
            Block.box(5.299999999999999, 0, 5.299999999999999, 10.7, 1, 10.7),
            Block.box(5, 1, 5, 11, 2, 11),
            Block.box(6.499999999999998, 1.4999999999999996, 6.499999999999998, 9.499999999999998, 2.4999999999999996, 9.499999999999998),
            Block.box(5.299999999999999, 2, 5.299999999999999, 10.700000000000001, 8, 10.700000000000001),
            Block.box(5, 8, 5, 11, 9, 11),
            Block.box(5.299999999999999, 9, 5.299999999999999, 10.7, 10, 10.7),
            Block.box(6.299999999999999, 10, 6.299999999999999, 9.7, 11, 9.7),
            Block.box(6.299999999999999, 15, 6.299999999999999, 9.7, 15.999999999999996, 9.7),
            Block.box(7, 11, 8, 9, 15, 8),
            Block.box(8, 11, 7, 8, 15, 9)
    );

    public FancyLanternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, Boolean.valueOf(false)).setValue(LIT, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        boolean currentState = state.getValue(LIT);
        boolean newState = !currentState;
        level.setBlockAndUpdate(pos, state.setValue(LIT, newState));

        return InteractionResult.SUCCESS;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return p_50763_ -> p_50763_.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        for (Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                BlockState blockstate = this.defaultBlockState().setValue(HANGING, Boolean.valueOf(direction == Direction.UP));
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                    return blockstate.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
                }
            }
        }

        return null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? HANGING_SHAPE : NON_HANGING_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HANGING, WATERLOGGED, LIT);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    protected static Direction getConnectedDirection(BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}