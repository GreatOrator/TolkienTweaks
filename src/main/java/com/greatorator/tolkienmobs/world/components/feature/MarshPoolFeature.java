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

import java.util.Arrays;

/**
 * A marsh pool: a shallow sheet of standing water lying in the fen's own hollows.
 * <p>
 * The drawing puts water in the Wetwang, where the Anduin's channels run through it, but the Dead Marshes
 * and the Midgewater are painted as dry country - and a marsh with no water in it is a meadow. A pool is
 * therefore dug into the lowest ground of its site: the water line is the lowest ground anywhere in the
 * disc, every column that stands at or near that line is cut down to it and flooded, and anything higher is
 * left standing as the fen's bank.
 * <p>
 * The water line has to come from the ground rather than from the feature's own origin. Taking it from the
 * origin column put a sheet at that one column's height across a fen that rises and falls by a couple of
 * blocks, which is water standing above its own shore - and the columns above the line were skipped, so the
 * sheet was left studded with islands of the ground it had refused to cut. Reading the low ground first,
 * and cutting the one or two blocks that stand over it, makes the pool a pool.
 * <p>
 * The height is read with {@link Heightmap.Types#OCEAN_FLOOR_WG}, the map the placement itself uses, so a
 * column is measured by its terrain and not by anything standing on it: a sheet laid from a surface map
 * that counts leaves and grass is a sheet laid at the height of the fen's plants.
 * <p>
 * The pool is kept inside the chunk being decorated. The bodies are laid by a later feature in the same
 * chunk, and the trees and grass are placed after it, so a pool that reached into a neighbour would be
 * water the bodies could not see and vegetation the marsh would have to be dug around.
 */
public class MarshPoolFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MIN_RADIUS = 3;
    private static final int MAX_RADIUS = 8;
    /** The deepest the middle of a pool stands, in blocks; the rim is always one. */
    private static final int MAX_DEPTH = 3;
    /** The most of a chunk's width a pool may take, leaving it room to stay in its own chunk. */
    private static final int MAX_MARGIN = 7;
    /**
     * How far above the water line the ground inside the pool may stand and still be cut down to it. This
     * is the fen's own undulation, not a hillside: a column higher than this is the bank and is left alone.
     */
    private static final int MAX_LIP = 3;

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

        int[] grounds = new int[(2 * radius + 1) * (2 * radius + 1)];
        int found = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }
                grounds[found++] = groundAt(level, centreX + dx, centreZ + dz);
            }
        }
        if (found == 0) {
            return false;
        }
        // The middle of the fen's own ground, not its lowest dip. A line taken from the lowest column sits
        // under the rest of the disc and leaves a pool nobody can see, and one taken from a single column
        // is a pond dug on a slope; the marsh is uneven enough that its middle is the honest line.
        Arrays.sort(grounds, 0, found);
        int line = grounds[found / 2];
        if (line <= level.getMinY() + 2) {
            return false;
        }

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
                int ground = groundAt(level, x, z);
                // Ground well below the line is a channel or a pit of its own, and ground above the lip is
                // the fen's bank; neither is part of this pool.
                if (ground > line + MAX_LIP || ground < line - MAX_DEPTH - 4) {
                    continue;
                }
                int depth = 1 + (int) Math.round((1.0D - reach) * (MAX_DEPTH - 1));
                int floor = line - depth;
                cursor.set(x, floor, z);
                if (!level.getBlockState(cursor).isSolidRender()) {
                    continue;
                }
                // Cut the lip first, so nothing of the fen stands over the water, then lay the water from
                // the bed up to the line.
                for (int y = line + 1; y <= ground; y++) {
                    cursor.set(x, y, z);
                    level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 2);
                }
                for (int y = floor + 1; y <= line; y++) {
                    cursor.set(x, y, z);
                    level.setBlock(cursor, Blocks.WATER.defaultBlockState(), 2);
                }
                flooded = true;
            }
        }
        return flooded;
    }

    /** The top of a column's terrain, at the height the placement itself reads. */
    private static int groundAt(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
    }
}
