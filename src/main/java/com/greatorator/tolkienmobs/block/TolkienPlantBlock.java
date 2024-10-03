package com.greatorator.tolkienmobs.block;

import com.greatorator.tolkienmobs.datagen.tags.TolkienBlockTagProvider;
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class TolkienPlantBlock extends BushBlock {
    protected TolkienPlantBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static boolean canPlaceRootAt(BlockGetter reader, BlockPos pos) {
        return reader.getBlockState(pos.above()).is(TolkienTags.Blocks.PLANTS_HANG_ON);
    }
}