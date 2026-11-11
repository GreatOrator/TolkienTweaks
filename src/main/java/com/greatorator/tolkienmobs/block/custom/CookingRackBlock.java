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
 * The model is drawn six pixels lower than the block it stands in, so it sits down over the fire it
 * is placed above rather than floating at the block's floor: its poles run from fifteen pixels below
 * the block's floor to nine above its roof, where they meet at the rope lashing, and it is meant to
 * stand with the fire in the space beneath it and its own legs reaching down around the flame. Six is
 * as far as it can drop: a model element may not reach below -16 and the poles already hang at -9,
 * so a full half block puts the model out of bounds and it does not load at all. It
 * therefore asks for no support: what it stands on is a campfire, and a rack that popped off the
 * moment the fire went out or the ground under it was dug away would be no use. It is registered
 * {@code noOcclusion} so the flame, its sparks and its smoke come up through the bars, and
 * {@code noCollision} because the grate and cage hang in mid-air - a collision shape there would make
 * the whole block a wall and fence the fire off from whoever wants to reach it.
 * <p>
 * The outline is a coarse reading of {@code models/block/cooking_rack.json} as it is now drawn, so it
 * reaches six pixels below the block's floor with the model: the rack is picked and broken where it
 * is actually drawn, not where the block's own cube is. Re-exporting the model with different
 * proportions means redrawing this list from it.
 */
public class CookingRackBlock extends Block {
    public static final MapCodec<CookingRackBlock> CODEC = simpleCodec(CookingRackBlock::new);

    /**
     * One box per part of the model, in the model's own pixels: the three poles where they cross this
     * block, the hanging grate, the cage above it, and the three hooks. The poles lean, so each is
     * recorded as the slab its crossing sweeps out rather than as a box around the whole pole.
     */
    private static final double[][] BOXES = {
            {7.25, -6, 0, 8.75, 10, 5.2},      // north pole, and the lashing above
            {7.25, -6, 11.4, 8.75, 10, 16},    // south pole
            {0, -6, 7.25, 5.0, 10, 8.75},      // west pole
            {4.27, 1.27, 3.79, 11.5, 1.71, 12.21},     // the grate the pot sits on
            {2.32, 5.46, 2.32, 9.55, 7.47, 13.69},     // the cage of bars above it
            {7.0, 0.85, 10.3, 9.0, 6.1, 12.1},         // hooks, hanging from the cage
            {7.0, 0.85, 3.97, 9.0, 6.1, 5.75},
            {4.0, 0.85, 7.0, 5.8, 6.1, 9.0},
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
