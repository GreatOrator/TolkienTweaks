package com.greatorator.tolkienmobs.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A summit thrown up above the existing ground: a steep, irregular cone of stone that raises the
 * Misty Mountains into the range they are meant to be.
 * <p>
 * The overworld's height comes from vanilla's noise, and a biome cannot change that, so the only
 * lever a biome has is its own features. This one is placed rarely and builds high - a nine-block
 * base rising thirty to sixty blocks - so a valley between two peaks keeps its shape while the
 * mountains along it grow cliffs. Nothing is placed on a column that is not already open air, so a
 * peak never eats into anything already built.
 * <p>
 * Snow is placed here rather than left to the biome's frost, because the peak runs in the last
 * generation step. The frost still passes over it afterwards and puts a layer on the ice-cold stone
 * that is left below the cap.
 */
public class MountainPeakFeature extends Feature<NoneFeatureConfiguration> {

    public MountainPeakFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState snow = Blocks.SNOW_BLOCK.defaultBlockState();
        int height = 30 + random.nextInt(31);         // 30 to 60 blocks
        int baseRadius = 6 + random.nextInt(5);       // 6 to 10 blocks
        int snowLine = (int) (height * 0.55F);
        int placed = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        // Four layers below the surface seat the foot into a slope instead of leaving it hanging
        // off one side. They are as wide as the base, so the peak grows out of the mountain.
        int lowest = -4;
        int widest = baseRadius + 3;

        for (int y = lowest; y < height; y++) {
            int radius;
            if (y < 0) {
                radius = widest;
            } else {
                float remaining = 1.0F - (float) y / height;
                // A squared falloff gives a steep summit and a broad foot, rather than a needle.
                radius = Mth.ceil(baseRadius * remaining * remaining);
            }
            int radiusSquared = radius * radius;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radiusSquared) {
                        continue;
                    }
                    // Break the edge up, so the cone reads as rock rather than as a pyramid.
                    if (y >= 0 && dx * dx + dz * dz == radiusSquared && random.nextBoolean()) {
                        continue;
                    }
                    cursor.setWithOffset(origin, dx, y, dz);
                    if (level.isOutsideBuildHeight(cursor)) {
                        continue;
                    }
                    BlockState current = level.getBlockState(cursor);
                    if (!current.isAir() && !current.canBeReplaced()) {
                        continue;
                    }
                    // The snow line wanders a block or two, so the cap is not a painted band.
                    boolean cap = y >= snowLine + ((dx * 7 + dz * 13) & 3) - 1;
                    level.setBlock(cursor, cap ? snow : stone, 2);
                    placed++;
                }
            }
        }

        return placed > 0;
    }
}
