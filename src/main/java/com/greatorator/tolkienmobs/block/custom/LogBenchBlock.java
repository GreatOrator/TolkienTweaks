package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.entity.SeatEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * A log bench somebody can sit down on.
 * <p>
 * Right-clicking it puts the player on a {@link SeatEntity} at the height of the bench's own seat -
 * seven pixels up, which is where the model's seat planks are drawn - rather than on the block, so the
 * sitter's feet land on the bench instead of the ground beside it. A second right-click finds the seat
 * already standing there and rides that one, so a bench only ever has one seat in it.
 * <p>
 * Sneaking is left alone: a player who crouches is trying to work on the bench or place against it, so
 * the click goes through to whatever they were doing.
 * <p>
 * The model is drawn along the block with its back on the north edge and a leg reaching five pixels
 * past the back, which is why the block is registered {@code noOcclusion}; its outline stays a cube,
 * since the overhang is small enough not to be walked into and the seat is on top of it either way.
 */
public class LogBenchBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<LogBenchBlock> CODEC = simpleCodec(LogBenchBlock::new);

    /** The top of the seat planks in the model, in a block's own units. */
    private static final double SEAT_HEIGHT = 7.0 / 16.0;

    public LogBenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            List<? extends SeatEntity> seats = level.getEntities(TolkienEntities.ENTITY_TTM_SEAT.get(),
                    new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                    seat -> true);
            SeatEntity seat = seats.isEmpty() ? null : seats.getFirst();
            if (seat == null) {
                seat = TolkienEntities.ENTITY_TTM_SEAT.get().spawn((ServerLevel) level, pos, EntitySpawnReason.TRIGGERED);
                if (seat == null) {
                    return InteractionResult.PASS;
                }
                seat.setPos(pos.getX() + 0.5, pos.getY() + SEAT_HEIGHT, pos.getZ() + 0.5);
            }
            player.startRiding(seat);
        }
        return InteractionResult.SUCCESS;
    }
}
