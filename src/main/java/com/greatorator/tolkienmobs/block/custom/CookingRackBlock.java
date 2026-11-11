package com.greatorator.tolkienmobs.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A tripod cooking rack: three poles lashed together above the block, a grate and a cage of bars hung
 * between them, and three hooks hanging down to pot height.
 * <p>
 * The model is taller than the block it stands in. The poles are drawn from nine pixels below the
 * block's floor to fifteen past its roof, where they meet at the rope lashing, so the rack is meant to
 * stand with the fire in the space beneath it and its own legs reaching down around the flame. It
 * therefore asks for no support: what it stands on is a campfire, and a rack that popped off the
 * moment the fire went out or the ground under it was dug away would be no use. It is registered
 * {@code noOcclusion} so the flame, its sparks and its smoke come up through the bars, and
 * {@code noCollision} because the grate and cage hang in mid-air - a collision shape there would make
 * the whole block a wall and fence the fire off from whoever wants to reach it.
 * <p>
 * The outline is a coarse reading of {@code models/block/cooking_rack.json}, clipped to the block's
 * own volume, so the legs reach into the block below and the lashing above only as far as its faces.
 * Re-exporting the model with different proportions means redrawing this list from it.
 */
public class CookingRackBlock extends Block {
    public static final MapCodec<CookingRackBlock> CODEC = simpleCodec(CookingRackBlock::new);

    /**
     * One box per part of the model, in the model's own pixels: the three poles where they cross this
     * block, the hanging grate, the cage above it, and the three hooks. The poles lean, so each is
     * recorded as the slab its crossing sweeps out rather than as a box around the whole pole.
     */
    private static final double[][] BOXES = {
            {7.25, 0, 0, 8.75, 16, 5.2},       // north pole, front edge at the floor to the lashing above
            {7.25, 0, 11.4, 8.75, 16, 16},     // south pole
            {0, 0, 7.25, 5.0, 16, 8.75},       // west pole
            {4.27, 7.27, 3.79, 11.5, 7.71, 12.21},   // the grate the pot sits on
            {2.32, 11.46, 2.32, 9.55, 13.47, 13.69}, // the cage of bars above it
            {7.0, 6.85, 10.3, 9.0, 12.1, 12.1},      // hooks, hanging from the cage
            {7.0, 6.85, 3.97, 9.0, 12.1, 5.75},
            {4.0, 6.85, 7.0, 5.8, 12.1, 9.0},
    };

    private static final VoxelShape SHAPE = outline();

    public CookingRackBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static VoxelShape outline() {
        VoxelShape shape = Shapes.empty();
        for (double[] box : BOXES) {
            shape = Shapes.or(shape, Block.box(box[0], box[1], box[2], box[3], box[4], box[5]));
        }
        return shape.optimize();
    }
}
