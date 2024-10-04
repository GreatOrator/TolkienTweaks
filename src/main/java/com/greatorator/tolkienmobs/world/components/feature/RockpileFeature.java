package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class RockpileFeature extends Feature<BlockStateConfiguration> {
    public RockpileFeature(Codec<BlockStateConfiguration> config) {
        super(config);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> config) {
        BlockPos blockpos = config.origin();
        WorldGenLevel worldgenlevel = config.level();
        RandomSource randomsource = config.random();
        BlockStateConfiguration blockstateconfiguration;
        for (blockstateconfiguration = config.config(); blockpos.getY() > worldgenlevel.getMinBuildHeight() + 3; blockpos = blockpos.below()) {
            if (!worldgenlevel.isEmptyBlock(blockpos.below())) {
                BlockState blockstate = worldgenlevel.getBlockState(blockpos.below());
                if (isGrassOrDirt(worldgenlevel, blockpos) || isStone(blockstate)) {
                    break;
                }
            }
        }

        if (blockpos.getY() <= worldgenlevel.getMinBuildHeight() + 3) {
            return false;
        } else {
            for (int l = 0; l < 3; l++) {
                int i = randomsource.nextInt(2);
                int j = randomsource.nextInt(2);
                int k = randomsource.nextInt(2);
                float f = (float)(i + j + k) * 0.333F + 0.5F;

                for (BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, -j, -k), blockpos.offset(i, j, k))) {
                    if (blockpos1.distSqr(blockpos) <= (double)(f * f)) {
                        worldgenlevel.setBlock(blockpos1, blockstateconfiguration.state, 3);
                    }
                }

                blockpos = blockpos.offset(-1 + randomsource.nextInt(2), -randomsource.nextInt(2), -1 + randomsource.nextInt(2));
            }

            return true;
        }
    }


    protected static boolean isStone(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD);
    }

    public static boolean isGrassOrDirt(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos, Feature::isDirt);
    }
}