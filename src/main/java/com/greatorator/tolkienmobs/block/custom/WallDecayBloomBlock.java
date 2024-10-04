package com.greatorator.tolkienmobs.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.util.TolkienBlocksHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.stream.Stream;

public class WallDecayBloomBlock extends BushBlock  {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(

    Direction.SOUTH, Stream.of(
            Block.box(4, 9, 0, 9, 12, 3),
            Block.box(5, 8, 0, 8, 13, 2.9),
            Block.box(6, 9, 3, 7, 12, 3.9),
            Block.box(5, 10, 3, 8, 11, 4),
            Block.box(11, 9, 0, 15, 11, 2),
            Block.box(12, 8, 0, 14, 12, 2),
            Block.box(12, 9, 2, 14, 11, 3),
            Block.box(7, 4, 0, 10, 5, 1),
            Block.box(8, 3, 0, 9, 6, 1),
            Block.box(8, 4, 1, 9, 5, 2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.NORTH, Stream.of(
            Block.box(7, 9, 13, 12, 12, 16),
            Block.box(8, 8, 13.1, 11, 13, 16),
            Block.box(9, 9, 12.1, 10, 12, 13),
            Block.box(8, 10, 12, 11, 11, 13),
            Block.box(1, 9, 14, 5, 11, 16),
            Block.box(2, 8, 14, 4, 12, 16),
            Block.box(2, 9, 13, 4, 11, 14),
            Block.box(6, 4, 15, 9, 5, 16),
            Block.box(7, 3, 15, 8, 6, 16),
            Block.box(7, 4, 14, 8, 5, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.WEST, Stream.of(
            Block.box(13, 9, 4, 16, 12, 9),
            Block.box(13.1, 8, 5, 16, 13, 8),
            Block.box(12.1, 9, 6, 13, 12, 7),
            Block.box(12, 10, 5, 13, 11, 8),
            Block.box(14, 9, 11, 16, 11, 15),
            Block.box(14, 8, 12, 16, 12, 14),
            Block.box(13, 9, 12, 14, 11, 14),
            Block.box(15, 4, 7, 16, 5, 10),
            Block.box(15, 3, 8, 16, 6, 9),
            Block.box(14, 4, 8, 15, 5, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.EAST, Stream.of(
            Block.box(0, 9, 7, 3, 12, 12),
            Block.box(0, 8, 8, 2.9000000000000004, 13, 11),
            Block.box(3, 9, 9, 3.9000000000000004, 12, 10),
            Block.box(3, 10, 8, 4, 11, 11),
            Block.box(0, 9, 1, 2, 11, 5),
            Block.box(0, 8, 2, 2, 12, 4),
            Block.box(2, 9, 2, 3, 11, 4),
            Block.box(0, 4, 6, 1, 5, 9),
            Block.box(0, 3, 7, 1, 6, 8),
            Block.box(1, 4, 7, 2, 5, 8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()));
    protected static final MapCodec<WallDecayBloomBlock> CODEC = simpleCodec(WallDecayBloomBlock::new);

    public WallDecayBloomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Vec3 vec3 = state.getOffset(worldIn, pos);
        return BOUNDING_SHAPES.get(state.getValue(FACING)).move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isFaceSturdy(world, blockPos, direction);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (canSurvive(state, world, pos))
            return state;
        else
            return Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return TolkienBlocksHelper.rotateHorizontal(state, rotation, FACING);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return TolkienBlocksHelper.mirrorHorizontal(state, mirror, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = this.defaultBlockState();
        LevelReader worldView = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Direction[] directions = context.getNearestLookingDirections();
        for (int i = 0; i < directions.length; ++i) {
            Direction direction = directions[i];
            if (direction.getAxis().isHorizontal()) {
                Direction direction2 = direction.getOpposite();
                blockState = blockState.setValue(FACING, direction2);
                if (blockState.canSurvive(worldView, blockPos)) {
                    return blockState;
                }
            }
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockReader, BlockPos blockPos) {
        return blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT) || blockState.is(Blocks.COARSE_DIRT) || blockState.is(Blocks.PODZOL) || blockState.is(Blocks.FARMLAND) || blockState.is(Blocks.STONE) || blockState.is(Blocks.COBBLESTONE);
    }
}
