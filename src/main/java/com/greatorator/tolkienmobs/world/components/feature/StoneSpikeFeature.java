package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.Nullable;

/**
 * The spires of dark stone that stand on Mordor's floor.
 * <p>
 * The spike grows out of the ground it stands on: air, snow and the plants a column carries are built
 * through, and the rock or soil under them is built against. It used to write only into ground blocks,
 * which is all a spike that reaches above the soil never touches, so an attempt on open ground spent
 * itself replacing nothing and the biome came out without a single one.
 */
public class StoneSpikeFeature extends Feature<NoneFeatureConfiguration> {
    /** How far below the column's surface the ground the spike stands on is looked for. */
    private static final int GROUND_DEPTH = 8;

    public StoneSpikeFeature(Codec<NoneFeatureConfiguration> configIn) {
        super(configIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();

        BlockPos ground = groundUnder(level, ctx.origin());
        if (ground == null) {
            return false;
        }

        // One attempt in sixty raises a needle instead of a lump: tall enough to be seen from the far
        // side of a plain, and narrow, because a cone of that height would be a mountain.
        boolean needle = random.nextInt(60) == 0;
        int height = needle ? 20 + random.nextInt(21) : random.nextInt(4) + 7;
        int width = needle ? 1 : height / 4 + random.nextInt(2);

        BlockState stone = TolkienBlocks.DARK_STONE.get().defaultBlockState();
        boolean placed = false;
        for (int y = 0; y < height; y++) {
            float radiusHere = (1.0F - (float) y / (float) height) * (float) width;
            int radius = Mth.ceil(radiusHere);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    float edgeX = (float) Mth.abs(dx) - 0.25F;
                    float edgeZ = (float) Mth.abs(dz) - 0.25F;
                    // A round cross-section, its corners thinned out so the spike reads as rock.
                    if ((dx != 0 || dz != 0) && edgeX * edgeX + edgeZ * edgeZ > radiusHere * radiusHere) {
                        continue;
                    }
                    if (radius > 1 && (Math.abs(dx) == radius || Math.abs(dz) == radius) && random.nextFloat() > 0.75F) {
                        continue;
                    }
                    BlockPos pos = ground.above(y).offset(dx, 0, dz);
                    if (pos.getY() > level.getMaxY() || !canBuildThrough(level, pos)) {
                        continue;
                    }
                    this.setBlock(level, pos, stone);
                    placed = true;
                }
            }
        }
        return placed;
    }

    /** The rock or soil the spike stands on, below the plants, snow or water the column carries. */
    @Nullable
    private static BlockPos groundUnder(WorldGenLevel level, BlockPos from) {
        BlockPos pos = from;
        for (int step = 0; step < GROUND_DEPTH && pos.getY() > level.getMinY(); step++, pos = pos.below()) {
            if (GroundRules.isBareGround(level.getBlockState(pos))) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Air, whatever a plant would grow over, and the fluid of a lake. The spike stops at anything with
     * a shape of its own, but a lake is only fluid: Mordor's lakes sit in its floor, so a spike built
     * from the rock under one used to skip the lake and hang in the air above it - and refusing such a
     * site instead would leave the biome's lowlands, where the lakes are, without a single spike. It
     * fills the fluid it passes through now, so its foot plugs into the lake's floor and the spire
     * stands in the water or lava.
     */
    private static boolean canBuildThrough(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // A fluid block itself, not a shape that merely holds fluid, so a waterlogged fence is rock
        // the spike stops at rather than something it eats.
        return state.isAir() || state.canBeReplaced() || state.liquid();
    }
}
