package com.greatorator.tolkienmobs.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.handler.TolkienBlocksHelper;
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

public class WallMushroomBrownBlock extends BushBlock  {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final EnumMap<Direction, VoxelShape> BOUNDING_SHAPES = Maps.newEnumMap(ImmutableMap.of(

    Direction.NORTH, Stream.of(
                    Block.box(9, 14, 12, 14, 15, 16),
                    Block.box(2, 9, 12, 7, 10, 16),
                    Block.box(8, 3, 12, 13, 4, 16),
                    Block.box(8, 11, 14, 11, 12, 16),
                    Block.box(3, 13, 14, 6, 14, 16),
                    Block.box(6, 6, 14, 9, 7, 16),
                    Block.box(12, 12, 14, 14, 13, 16),
                    Block.box(10, 5, 14, 12, 6, 16),
                    Block.box(3, 4, 14, 5, 5, 16),
                    Block.box(12, 8, 14, 15, 9, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.SOUTH, Stream.of(
                    Block.box(2, 14, 0, 7, 15, 4),
                    Block.box(9, 9, 0, 14, 10, 4),
                    Block.box(3, 3, 0, 8, 4, 4),
                    Block.box(5, 11, 0, 8, 12, 2),
                    Block.box(10, 13, 0, 13, 14, 2),
                    Block.box(7, 6, 0, 10, 7, 2),
                    Block.box(2, 12, 0, 4, 13, 2),
                    Block.box(4, 5, 0, 6, 6, 2),
                    Block.box(11, 4, 0, 13, 5, 2),
                    Block.box(1, 8, 0, 4, 9, 2)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.EAST, Stream.of(
                    Block.box(0, 14, 9, 4, 15, 14),
                    Block.box(0, 9, 2, 4, 10, 7),
                    Block.box(0, 3, 8, 4, 4, 13),
                    Block.box(0, 11, 8, 2, 12, 11),
                    Block.box(0, 13, 3, 2, 14, 6),
                    Block.box(0, 6, 6, 2, 7, 9),
                    Block.box(0, 12, 12, 2, 13, 14),
                    Block.box(0, 5, 10, 2, 6, 12),
                    Block.box(0, 4, 3, 2, 5, 5),
                    Block.box(0, 8, 12, 2, 9, 15)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
    Direction.WEST, Stream.of(
                    Block.box(12, 14, 2, 16, 15, 7),
                    Block.box(12, 9, 9, 16, 10, 14),
                    Block.box(12, 3, 3, 16, 4, 8),
                    Block.box(14, 11, 5, 16, 12, 8),
                    Block.box(14, 13, 10, 16, 14, 13),
                    Block.box(14, 6, 7, 16, 7, 10),
                    Block.box(14, 12, 2, 16, 13, 4),
                    Block.box(14, 5, 4, 16, 6, 6),
                    Block.box(14, 4, 11, 16, 5, 13),
                    Block.box(14, 8, 1, 16, 9, 4)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()));
    protected static final MapCodec<WallMushroomBrownBlock> CODEC = simpleCodec(WallMushroomBrownBlock::new);

    public WallMushroomBrownBlock(Properties properties) {
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
        Direction direction = (Direction) state.getValue(FACING);
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
