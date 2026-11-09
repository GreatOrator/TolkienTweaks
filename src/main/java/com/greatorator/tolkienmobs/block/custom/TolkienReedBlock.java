package com.greatorator.tolkienmobs.block.custom;

import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A reed: sugar cane's plant with the mod's own model.
 * <p>
 * What a reed needs of the world is what sugar cane needs of it - a bank, water beside it, and room to stand three
 * high - and none of that is worth writing twice. The block is the plant, the growth and the survival test; the
 * shape and the texture are the mod's own, so a stand of reeds in the Gladden reads as reeds.
 */
public class TolkienReedBlock extends SugarCaneBlock {
    public TolkienReedBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
