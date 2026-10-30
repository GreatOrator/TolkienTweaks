package com.greatorator.tolkienmobs.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The workbench the factions' goods are made and mended on.
 * <p>
 * Its model is drawn two blocks wide and nearly two blocks tall, and it says so outright: an element
 * may sit anywhere inside 32 pixels of the block, so unlike the notice board the drawing needs
 * neither a bake-time scale nor a wrapper. The cost is that half of the bench stands in the
 * neighbouring block, which is why the block is registered {@code noOcclusion} and why its outline
 * follows the drawing rather than a cube - a cube would leave the overhanging half walkable.
 * <p>
 * The boxes below are a coarse reading of {@code models/block/workbench.json}: the tabletop, the two
 * leg frames, the hutch along the back and the shelf across it. That model holds 107 elements, and
 * the tools laid out on the bench are not cut out separately. Re-exporting the model with different
 * proportions means redrawing this list from it.
 * <p>
 * The hutch is drawn along the south edge, so the model as authored is a bench whose working side
 * faces north, and data generation turns it with an offset of 180: a player placing it stands on the
 * side they can work from, with the hutch behind the bench.
 */
public class WorkbenchBlock extends Block {
    public static final MapCodec<WorkbenchBlock> CODEC = simpleCodec(WorkbenchBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    /** One box per mass of the model, in the model's own pixels, drawn facing north. */
    private static final double[][] BOXES = {
            {-7.9, 12, -0.4, 24.1, 16, 17.6},     // tabletop, two blocks across
            {-6.6, 0, 1.0, -4.7, 12, 16.2},       // west leg frame
            {20.9, 0, 1.0, 22.8, 12, 16.2},       // east leg frame
            {-5.5, 16, 13.5, 23.1, 28, 17.7},     // hutch along the back
            {13.0, 23.5, 11.6, 22.6, 24.0, 17.7}, // shelf across the hutch
    };

    private static final VoxelShape SHAPE_NORTH = outline(0);
    private static final VoxelShape SHAPE_EAST = outline(90);
    private static final VoxelShape SHAPE_SOUTH = outline(180);
    private static final VoxelShape SHAPE_WEST = outline(270);

    public WorkbenchBlock(Properties properties) {
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
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos,
                                           CollisionContext context) {
        return getShape(state, getter, pos, context);
    }

    /**
     * Turns every box by the number of degrees data generation writes into the blockstate for that
     * facing, about the middle of the block.
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
}
