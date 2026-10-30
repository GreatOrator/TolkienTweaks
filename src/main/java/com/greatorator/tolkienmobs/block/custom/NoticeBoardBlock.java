package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.faction.MercenaryContracts;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The notice board mercenary work is posted on and handed back to.
 * <p>
 * Its model is bigger than the block it stands in: the frame reaches five pixels outside the west
 * face and three past the east one, a third of a block past the north, and a little over one block
 * above the ground. The block is therefore registered {@code noOcclusion}, and its shape is the
 * model's own outline rather than a cube - a cube would be traced only in the middle block, leaving
 * the overhang unclickable and walkable.
 * <p>
 * Because the board turns to face whoever places it, there is one outline per direction. The model
 * is drawn facing south, so south is the unrotated case and the other three are the same boxes
 * turned a quarter at a time, matching the y rotations data generation writes into the blockstate.
 */
public class NoticeBoardBlock extends Block {
    public static final MapCodec<NoticeBoardBlock> CODEC = simpleCodec(NoticeBoardBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    /**
     * One box per element of {@code models/block/notice_board.json}, as drawn: each element already
     * turned by its own rotation, in the orientation the model is authored in (facing south).
     * Re-exporting the model with different proportions means redrawing this list from it.
     */
    private static final double[][] BOXES = {
            {-4, 0, 10, -2, 2, 12},
            {18, 0, 10, 20, 2, 12},
            {-3.7, 2, 10.3, -2.3, 18, 11.7},
            {18.3, 2, 10.3, 19.7, 18, 11.7},
            {-3.6, 12.7, 10.3, -2.4, 17.7, 11.5},
            {-4.6, 15.26, 6.92, -3.4, 19.65, 11.3},
            {19.3, 15.26, 6.92, 20.5, 19.65, 11.3},
            {18.3, 12.7, 10.3, 19.5, 17.7, 11.5},
            {-3.6, 12.2, 10.35, -2.4, 16.58, 14.74},
            {-4.6, 15.06, 10.6, -3.4, 19.45, 14.98},
            {19.4, 14.96, 10.6, 20.6, 19.35, 14.98},
            {18.4, 12.4, 10.45, 19.6, 16.78, 14.84},
            {-2.7, 3, 10.4, 19.3, 4.2, 11.6},
            {-2.7, 16.5, 10.4, 19.3, 17.7, 11.6},
            {-5, 17.6, 9.8, 21, 20, 12.2},
            {-4.8, 14.93, 6.28, 20.8, 19.95, 11.3},
            {-4.8, 14.9, 14.8, 20.8, 20.8, 16},
            {-5, 14.17, 13.39, 21, 17, 16.21},
            {-5, 14.27, 5.79, 21, 17.1, 8.61},
            {-3.2, 4, 10.5, 18.8, 17, 11.5},
    };

    private static final VoxelShape SHAPE_SOUTH = outline(0);
    private static final VoxelShape SHAPE_WEST = outline(90);
    private static final VoxelShape SHAPE_NORTH = outline(180);
    private static final VoxelShape SHAPE_EAST = outline(270);

    public NoticeBoardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
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
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case NORTH -> SHAPE_NORTH;
            default -> SHAPE_SOUTH;
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos,
                                           CollisionContext context) {
        return getShape(state, getter, pos, context);
    }

    /**
     * Turns every box by the given number of degrees about the middle of the block, which is the same
     * quarter turn the blockstate applies to the model.
     */
    private static VoxelShape outline(int degrees) {
        VoxelShape shape = Shapes.empty();
        for (double[] box : BOXES) {
            double x1 = box[0], y1 = box[1], z1 = box[2], x2 = box[3], y2 = box[4], z2 = box[5];
            double ax1, az1, ax2, az2;
            switch (degrees) {
                case 90 -> {
                    ax1 = 16 - z2;
                    az1 = x1;
                    ax2 = 16 - z1;
                    az2 = x2;
                }
                case 180 -> {
                    ax1 = 16 - x2;
                    az1 = 16 - z2;
                    ax2 = 16 - x1;
                    az2 = 16 - z1;
                }
                case 270 -> {
                    ax1 = z1;
                    az1 = 16 - x2;
                    ax2 = z2;
                    az2 = 16 - x1;
                }
                default -> {
                    ax1 = x1;
                    az1 = z1;
                    ax2 = x2;
                    az2 = z2;
                }
            }
            shape = Shapes.or(shape, Block.box(ax1, y1, az1, ax2, y2, az2));
        }
        return shape.optimize();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            MercenaryContracts.useBoard(serverPlayer);
        }
        return InteractionResult.CONSUME;
    }
}
