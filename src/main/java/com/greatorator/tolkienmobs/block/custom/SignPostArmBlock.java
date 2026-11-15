package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.custom.entity.SignPostArmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * The board that hangs off a post and says where a road goes. It is four pixels high and sits in one
 * of four vertical steps of its cell, so boards stack tightly up the post the way a real fingerpost
 * does; each one carries its own destination, so a post can point four ways from one height.
 * <p>
 * The model is drawn pointing south, so an arm whose {@code facing} is south is an arm whose post
 * stands to its north: the post is always on the side the arm does not point.
 */
public class SignPostArmBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    /** Which four pixel step of the cell the board's underside sits on. */
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);

    public SignPostArmBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(LEVEL, 0));
    }

    /**
     * The board as the model draws it: one pixel thin, four high, reaching back eight pixels into the post's
     * own cell so the two meet, and out 12.5 pixels past this one. That the inner end lies outside this
     * block's space is the point - the post is two pixels wide in a cell of its own, and a board that stopped
     * at the cell boundary would hang in the air beside it.
     */
    public static VoxelShape shape(Direction facing, int level) {
        double low = level * 4.0D;
        double high = low + 4.0D;
        return switch (facing) {
            case NORTH -> Block.box(7.0D, low, 3.5D, 8.0D, high, 24.0D);
            case EAST -> Block.box(-8.0D, low, 7.0D, 12.5D, high, 8.0D);
            case WEST -> Block.box(3.5D, low, 7.0D, 24.0D, high, 8.0D);
            default -> Block.box(7.0D, low, -8.0D, 8.0D, high, 12.5D);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(LEVEL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape(state.getValue(FACING), state.getValue(LEVEL));
    }

    /** Whether a post - a shaft or a cap - is what this block is. */
    public static boolean isPost(BlockState state) {
        return state.getBlock() instanceof SignPostShaftBlock || state.getBlock() instanceof SignPostCapBlock;
    }

    /**
     * Which way an arm in this cell has to point, read from the post beside it: the board faces the way its
     * post does not, so the flat end always sits against the shaft and the arrow out. Null when no post
     * touches the cell, which is the only reason an arm cannot go there.
     */
    @Nullable
    public static Direction facingFromPost(LevelReader level, BlockPos pos) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (isPost(level.getBlockState(pos.relative(side)))) return side.getOpposite();
        }
        return null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isPost(level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return this.canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        // The post decides which way the board points, not the click: whatever face was hit and whichever
        // way the player happens to stand, the flat end goes to the shaft and the arrow out along the road.
        Direction facing = facingFromPost(context.getLevel(), context.getClickedPos());
        if (facing == null) return null;

        double height = context.getClickLocation().y - context.getClickedPos().getY();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(LEVEL, Mth.clamp((int) (height * 4.0D), 0, 3));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof SignPostArmBlockEntity arm)) return InteractionResult.PASS;
        arm.stepDestination(player.isShiftKeyDown() ? -1 : 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SignPostArmBlockEntity(pos, state);
    }
}
