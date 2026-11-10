package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.IntBinaryOperator;

/**
 * The staging ground at the north end of the high pass road: a disc of level ground that a cutscene is played on.
 * <p>
 * It is drawn the way a generated road is drawn - a pure function of the column, answered while the ground is made -
 * and not laid by a command, so it stands in every world that raises this ground and nothing has to be built twice.
 * There is one of these, and it is a place rather than a catalogue: the numbers below are the block a scout marked,
 * 5117, -3409, and the thirteen blocks around it.
 * <p>
 * The shape is a clearing rather than a built plaza. Inside the radius the columns take one height - the drawn ground
 * under the middle of the disc - so the ground is level across the whole of it, and over the last four blocks of the
 * rim that level is eased back into the country's own, so the disc meets the hillside instead of standing on it as a
 * ring of cliff. The floor is the wild mix the roads are laid of, earthier in the middle where a crowd would stand and
 * grass wearing through at the edges, chosen per block from a hash so the scatter does not clot.
 */
public final class Plaza {
    /** The block the scout marked: the middle of the disc, on the vale's floor north of the road's last pin. */
    private static final int CENTRE_X = 5117;
    private static final int CENTRE_Z = -3409;

    /** How far the level ground reaches, in blocks: thirteen, so the disc is twenty-seven across. */
    private static final int RADIUS = 13;

    /**
     * How many blocks of the rim the country's own ground is eased back in over.
     * <p>
     * Four, because the ground around the disc is not all one height: the vale's floor is flat but the wooded bank to
     * the east rises six or seven blocks within the radius. Cut square, that bank would be a cliff standing in the
     * clearing's edge; eased, it is the clearing running up into the trees.
     */
    private static final int RIM = 4;

    /** The trodden middle, in blocks: the part of the floor that is packed earth rather than grass wearing through. */
    private static final int INNER = 6;

    private Plaza() {
    }

    /** Whether the disc reaches a column, which is the test every other part of this asks first. */
    public static boolean reaches(int x, int z) {
        return away(x, z) <= RADIUS;
    }

    private static double away(int x, int z) {
        return Math.hypot(x - CENTRE_X, z - CENTRE_Z);
    }

    /**
     * The height the disc stands at in a column, or null where it does not reach it.
     * <p>
     * The level is the drawn ground under the middle, read through the caller's own ground function so that the disc,
     * the road beside it and the heightmaps all answer from one map. Inside the rim that is the height outright; across
     * the rim it is the country's ground again, blended on a smoothstep so the join has no lip at either end.
     */
    public static Integer heightAt(int x, int z, IntBinaryOperator ground) {
        double away = away(x, z);
        if (away > RADIUS) return null;
        int level = ground.applyAsInt(CENTRE_X, CENTRE_Z);
        if (away <= RADIUS - RIM) return level;
        double t = (away - (RADIUS - RIM)) / RIM;
        double eased = t * t * (3.0D - 2.0D * t);
        return (int) Math.round(level + (ground.applyAsInt(x, z) - level) * eased);
    }

    /**
     * The block the floor is made of, or null outside the disc.
     * <p>
     * The middle is the trodden part: coarse dirt, path and gravel, with grass kept to one column in eight because a
     * clearing that people stand in wears through. The rest is the country's own mix, grass winning as it goes out, so
     * the edge of the disc reads as the edge of a clearing rather than as the edge of a paved thing.
     */
    public static BlockState surface(int x, int z) {
        double away = away(x, z);
        if (away > RADIUS) return null;
        int roll = Math.floorMod(hash(x, z), 100);
        if (away <= INNER) {
            if (roll < 38) return Blocks.COARSE_DIRT.defaultBlockState();
            if (roll < 64) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 86) return Blocks.GRAVEL.defaultBlockState();
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
        if (roll < 52) return Blocks.GRASS_BLOCK.defaultBlockState();
        if (roll < 72) return Blocks.DIRT_PATH.defaultBlockState();
        if (roll < 88) return Blocks.COARSE_DIRT.defaultBlockState();
        return Blocks.GRAVEL.defaultBlockState();
    }

    /**
     * Takes the wood out of the clearing when its chunk loads.
     * <p>
     * The disc is drawn before the features run, so a wood planted over it arrives standing in the middle of the
     * staging ground - the same reason a road's corridor is cleared on load rather than during generation. A clearing
     * is open to the sky, so everything that grows over the floor goes, trunks included: a tree cut back to its crown
     * would be the one thing left standing where the scene is played. Only cover is taken, and only above the floor,
     * so the ground the disc was cut to is untouched.
     *
     * @return how many blocks were taken out
     */
    public static int clear(LevelAccessor level, ChunkAccess chunk) {
        int cleared = 0;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int minY = level.getMinY();
        int maxY = level.getMaxY() - 1;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockX = minX + x;
                int blockZ = minZ + z;
                if (!reaches(blockX, blockZ)) continue;

                int floor = minY - 1;
                for (int y = maxY; y >= minY; y--) {
                    BlockState state = level.getBlockState(at.set(blockX, y, blockZ));
                    if (state.isAir() || RoadPaint.cover(state)) continue;
                    floor = y;
                    break;
                }
                if (floor < minY) continue;
                for (int y = floor + 1; y <= maxY; y++) {
                    if (!RoadPaint.cover(level.getBlockState(at.set(blockX, y, blockZ)))) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    /** A stable hash of one column, so the floor's scatter is the same block whichever chunk or run asks. */
    private static int hash(int x, int z) {
        int value = x * 374761393 + z * 668265263;
        value = (value ^ (value >>> 13)) * 1274126177;
        return value ^ (value >>> 16);
    }
}
