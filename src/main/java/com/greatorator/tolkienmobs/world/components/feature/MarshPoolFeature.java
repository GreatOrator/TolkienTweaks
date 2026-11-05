package com.greatorator.tolkienmobs.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A marsh pool: a shallow sheet of standing water dug into the flat ground of a fen.
 * <p>
 * The drawing puts water in the Wetwang, where the Anduin's channels run through it, but the Dead Marshes
 * and the Midgewater are painted as dry country - and a marsh with no water in it is a meadow. The pool is
 * therefore dug: the ground at the site's own level is cut down a block per ring from the rim to the middle
 * and filled to the surface, so what stands is a pool with the fen's own ground round it rather than a hole.
 * <p>
 * The water is laid at one level, and only over columns whose ground is no higher than that level, so the
 * hummocks between the pools stay dry - the bodies are laid only in water, and a pool that flooded the
 * whole fen would leave nowhere for the bank to be. A column whose floor stands on a cave is left alone,
 * because a pool opened into a cavern drains itself the first time the chunk ticks.
 * <p>
 * The pool is kept inside the chunk being decorated. The bodies are laid by a later feature in the same
 * chunk, so a pool that reached into a neighbour would be water the bodies could not see, and the marsh
 * would be dug but empty.
 */
public class MarshPoolFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MIN_RADIUS = 3;
    private static final int MAX_RADIUS = 8;
    /** The deepest the middle of a pool stands, in blocks; the rim is always one. */
    private static final int MAX_DEPTH = 3;
    /** The most of a chunk's width a pool may take, leaving it room to stay in its own chunk. */
    private static final int MAX_MARGIN = 7;

    public MarshPoolFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int radius = MIN_RADIUS + random.nextInt(MAX_RADIUS - MIN_RADIUS + 1);
        int margin = radius + 1;
        if (margin > MAX_MARGIN) {
            return false;
        }
        int localX = Mth.clamp(origin.getX() & 15, margin, 15 - margin);
        int localZ = Mth.clamp(origin.getZ() & 15, margin, 15 - margin);
        int centreX = origin.getX() - (origin.getX() & 15) + localX;
        int centreZ = origin.getZ() - (origin.getZ() & 15) + localZ;
        int surface = origin.getY() - 1;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean flooded = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double reach = Math.sqrt(dx * dx + dz * dz) / (double) radius;
                if (reach > 1.0D) {
                    continue;
                }
                int x = centreX + dx;
                int z = centreZ + dz;
                int depth = 1 + (int) Math.round((1.0D - reach) * (MAX_DEPTH - 1));
                int ground = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
                // A hummock above the water line is an island in the pool; ground that drops away further
                // than the pool is deep belongs to whatever made it and is left as it is.
                if (ground > surface || ground < surface - depth) {
                    continue;
                }
                int floor = surface - depth;
                cursor.set(x, floor, z);
                if (!level.getBlockState(cursor).isSolidRender()) {
                    continue;
                }
                for (int y = floor + 1; y <= surface; y++) {
                    cursor.set(x, y, z);
                    level.setBlock(cursor, Blocks.WATER.defaultBlockState(), 2);
                }
                flooded = true;
            }
        }
        return flooded;
    }
}
