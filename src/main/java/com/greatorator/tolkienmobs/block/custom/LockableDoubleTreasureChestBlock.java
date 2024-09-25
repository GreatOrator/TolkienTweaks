package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.TolkienBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockableDoubleTreasureChestBlock extends TolkienBlock {
    public static final MapCodec<LockableDoubleTreasureChestBlock> CODEC = simpleCodec(LockableDoubleTreasureChestBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    protected static final VoxelShape LOCKABLE_SHAPE_N = Block.box(-7.0D, 0.0D, 1.0D, 23.0D, 15.0D, 15.0D);
    protected static final VoxelShape LOCKABLE_SHAPE_S = Block.box(1.0D, 0.0D, -7.0D, 15.0D, 15.0D, 23.0D);
    protected static final VoxelShape LOCKABLE_SHAPE_E = Block.box(1.0D, 0.0D, -7.0D, 15.0D, 15.0D, 23.0D);
    protected static final VoxelShape LOCKABLE_SHAPE_W = Block.box(-7.0D, 0.0D, 1.0D, 23.0D, 15.0D, 15.0D);

    public LockableDoubleTreasureChestBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE).setValue(OPEN, Boolean.FALSE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        switch((Direction)state.getValue(FACING)) {
            default:
            case NORTH:
                return LOCKABLE_SHAPE_N;
            case SOUTH:
                return LOCKABLE_SHAPE_W;
            case EAST:
                return LOCKABLE_SHAPE_E;
            case WEST:
                return LOCKABLE_SHAPE_S;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, OPEN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighbor, LevelAccessor world, BlockPos pos, BlockPos offset) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = world.getFluidState(pos);
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
