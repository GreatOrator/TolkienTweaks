package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RubbleFeature extends Feature<NoneFeatureConfiguration> {
    public RubbleFeature(Codec<NoneFeatureConfiguration> configIn) {
        super(configIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos blockpos = context.origin();
        RandomSource randomsource = context.random();
        WorldGenLevel worldgenlevel;
        for(worldgenlevel = context.level(); worldgenlevel.isEmptyBlock(blockpos) && blockpos.getY() > worldgenlevel.getMinBuildHeight() + 2; blockpos = blockpos.below()) {
        }

        if (!worldgenlevel.getBlockState(blockpos).is(Blocks.GRASS_BLOCK)) {
            return false;
        } else {
            int i = randomsource.nextInt(4) + 7;
            int j = i / 4 + randomsource.nextInt(2);
            if (j > 1 && randomsource.nextInt(60) == 0) {
                blockpos = blockpos.above(10 + randomsource.nextInt(30));
            }

            for (int j1 = 0; j1 < 75; ++j1) {
                blockpos = blockpos.above(randomsource.nextInt(4));
                BlockPos randomPos = context.origin().offset(context.random().nextInt(8), context.random().nextInt(4), context.random().nextInt(8));
                if(context.random().nextInt(10) == 0) {
                    if (worldgenlevel.isEmptyBlock(randomPos)) {
                        Block block;
                        int chance = context.random().nextInt(31);
                        if (chance < 10) {
                            block = TolkienBlocks.COBBLED_DARK_STONE.get();
                        } else if (chance > 10 && chance < 20) {
                            block = Blocks.MOSSY_COBBLESTONE;
                        } else if (chance > 20 && chance < 30) {
                            block = Blocks.GRANITE;
                        } else {
                            block = TolkienBlocks.CRACKED_DARK_STONE_BRICKS.get();
                        }
                        worldgenlevel.setBlock(randomPos, block.defaultBlockState(), 3);
                    }
                }
            }
            return true;
        }
    }
}