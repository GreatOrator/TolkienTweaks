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

public class WallMushroomRedBlock extends BushBlock  {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(

    Direction.NORTH, Stream.of(
                    Block.box(4, 6, 13, 9, 9, 16),
                    Block.box(5, 5, 13.1, 8, 10, 16),
                    Block.box(6, 6, 12.1, 7, 9, 13),
                    Block.box(5, 7, 12, 8, 8, 13),
                    Block.box(11, 7, 14, 15, 9, 16),
                    Block.box(12, 6, 14, 14, 10, 16),
                    Block.box(12, 7, 13, 14, 9, 14),
                    Block.box(7, 13, 15, 10, 14, 16),
                    Block.box(8, 12, 15, 9, 15, 16),
                    Block.box(8, 13, 14, 9, 14, 15)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.SOUTH, Stream.of(
                    Block.box(7, 6, 0, 12, 9, 3),
                    Block.box(8, 5, 0, 11, 10, 2.9),
                    Block.box(9, 6, 3, 10, 9, 3.9),
                    Block.box(8, 7, 3, 11, 8, 4),
                    Block.box(1, 7, 0, 5, 9, 2),
                    Block.box(2, 6, 0, 4, 10, 2),
                    Block.box(2, 7, 2, 4, 9, 3),
                    Block.box(6, 13, 0, 9, 14, 1),
                    Block.box(7, 12, 0, 8, 15, 1),
                    Block.box(7, 13, 1, 8, 14, 2)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.EAST, Stream.of(
                    Block.box(0, 6, 4, 3, 9, 9),
                    Block.box(0, 5, 5, 2.9000000000000004, 10, 8),
                    Block.box(3, 6, 6, 3.9000000000000004, 9, 7),
                    Block.box(3, 7, 5, 4, 8, 8),
                    Block.box(0, 7, 11, 2, 9, 15),
                    Block.box(0, 6, 12, 2, 10, 14),
                    Block.box(2, 7, 12, 3, 9, 14),
                    Block.box(0, 13, 7, 1, 14, 10),
                    Block.box(0, 12, 8, 1, 15, 9),
                    Block.box(1, 13, 8, 2, 14, 9)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.WEST, Stream.of(
                    Block.box(13, 6, 7, 16, 9, 12),
                    Block.box(13.1, 5, 8, 16, 10, 11),
                    Block.box(12.1, 6, 9, 13, 9, 10),
                    Block.box(12, 7, 8, 13, 8, 11),
                    Block.box(14, 7, 1, 16, 9, 5),
                    Block.box(14, 6, 2, 16, 10, 4),
                    Block.box(13, 7, 2, 14, 9, 4),
                    Block.box(15, 13, 6, 16, 14, 9),
                    Block.box(15, 12, 7, 16, 15, 8),
                    Block.box(14, 13, 7, 15, 14, 8)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()));
    protected static final MapCodec<WallMushroomRedBlock> CODEC = simpleCodec(WallMushroomRedBlock::new);

    public WallMushroomRedBlock(Properties properties) {
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
