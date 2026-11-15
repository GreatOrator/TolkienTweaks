package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.block.custom.entity.SignPostArmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Two boards on one post in the one direction, for a place that sends a traveller to two destinations down the
 * same road. They are drawn as one board above another, each with its own arrow and its own lettering, and each
 * with a destination of its own in the block entity.
 * <p>
 * A cell holds one block, so a post that needs two boards in the same direction needs one block that draws both:
 * this is that block. Together they are eight pixels, which leaves two places they can sit in a cell, the lower
 * half or the upper, so {@code level} is 0 or 2 and never anything else.
 */
public class SignPostDoubleArmBlock extends SignPostArmBlock {
    /** The pair sitting on the floor of the cell, boards at 0-4 and 4-8. */
    public static final int LOWER = 0;
    /** The pair hanging in the top half, boards at 8-12 and 12-16, flush under a cap. */
    public static final int UPPER = 2;

    public SignPostDoubleArmBlock(Properties properties) {
        super(properties);
    }

    /** Both boards as the models draw them: two four pixel arms, one above the other, sharing the same reach. */
    public static VoxelShape shape(Direction facing, int level) {
        double low = level * 4.0D;
        double high = low + 8.0D;
        return switch (facing) {
            case NORTH -> Block.box(7.0D, low, 3.5D, 8.0D, high, 24.0D);
            case EAST -> Block.box(-8.0D, low, 7.0D, 12.5D, high, 8.0D);
            case WEST -> Block.box(3.5D, low, 7.0D, 24.0D, high, 8.0D);
            default -> Block.box(7.0D, low, -8.0D, 8.0D, high, 12.5D);
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape(state.getValue(FACING), state.getValue(LEVEL));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = facingFromPost(context.getLevel(), context.getClickedPos());
        if (facing == null) return null;

        // Eight pixels of board fit the cell twice over, so the height clicked only decides which half it takes.
        double height = context.getClickLocation().y - context.getClickedPos().getY();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(LEVEL, height < 0.5D ? LOWER : UPPER);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof SignPostArmBlockEntity arm)) return InteractionResult.PASS;

        // A plain click steps the lower board on, a crouching one the upper, so the two can be set one at a time.
        arm.stepDestination(player.isShiftKeyDown() ? 1 : 0, 1);
        return InteractionResult.SUCCESS;
    }
}
