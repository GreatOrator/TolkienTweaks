package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.faction.MercenaryContracts;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The notice board mercenary work is posted on and handed back to.
 * <p>
 * Its model is bigger than the block it stands in. The frame reaches five pixels outside the west
 * face and five past the east one, a third of a block past the north, and a little over one block
 * above the ground, so the block is registered {@code noOcclusion} - the faces of a neighbour beside
 * it are still drawn - and its shape is the model's own outline. That outline reaching outside the
 * cell is the part the game needs: a plain cube would be traced only in the middle block, and the
 * rest of the board would be scenery no click could reach and no player could bump into.
 */
public class NoticeBoardBlock extends Block {
    public static final MapCodec<NoticeBoardBlock> CODEC = simpleCodec(NoticeBoardBlock::new);

    /**
     * One box per element of {@code models/block/notice_board.json}, in the order Blockbench lists
     * them, each one already turned by its element's own rotation. Re-exporting the model with
     * different proportions means redrawing this list from it.
     */
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(-4, 0, 10, -2, 2, 12),
            Block.box(18, 0, 10, 20, 2, 12),
            Block.box(-3.7, 2, 10.3, -2.3, 18, 11.7),
            Block.box(18.3, 2, 10.3, 19.7, 18, 11.7),
            Block.box(-3.6, 12.7, 10.3, -2.4, 17.7, 11.5),
            Block.box(-4.6, 15.26, 6.92, -3.4, 19.65, 11.3),
            Block.box(19.3, 15.26, 6.92, 20.5, 19.65, 11.3),
            Block.box(18.3, 12.7, 10.3, 19.5, 17.7, 11.5),
            Block.box(-3.6, 12.2, 10.35, -2.4, 16.58, 14.74),
            Block.box(-4.6, 15.06, 10.6, -3.4, 19.45, 14.98),
            Block.box(19.4, 14.96, 10.6, 20.6, 19.35, 14.98),
            Block.box(18.4, 12.4, 10.45, 19.6, 16.78, 14.84),
            Block.box(-2.7, 3, 10.4, 19.3, 4.2, 11.6),
            Block.box(-2.7, 16.5, 10.4, 19.3, 17.7, 11.6),
            Block.box(-5, 17.6, 9.8, 21, 20, 12.2),
            Block.box(-4.8, 14.93, 6.28, 20.8, 19.95, 11.3),
            Block.box(-4.8, 14.9, 14.8, 20.8, 20.8, 16),
            Block.box(-5, 14.17, 13.39, 21, 17, 16.21),
            Block.box(-5, 14.27, 5.79, 21, 17.1, 8.61),
            Block.box(-3.2, 4, 10.5, 18.8, 17, 11.5));

    public NoticeBoardBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
