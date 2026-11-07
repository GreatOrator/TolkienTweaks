package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;

/**
 * Roads the world generates, painted into the ground as its chunks are made.
 * <p>
 * The campaign's own roads are laid by a pass, {@code /tmroad}, because their alignment is a search over ground that
 * exists: a shelf cut into a hillside needs the hill. A road that stands in every world cannot search for anything,
 * since it has to answer for a chunk that does not exist yet - so it is a pure function of x and z instead, exactly
 * the way the drawn rivers are: a line of waypoints, each carrying the height a scout walked it at, and a half-width.
 * <p>
 * The heights are what makes it a graded road rather than a painted one. Every column of the corridor takes the height
 * of the nearest point of the line, so the strip is level across its width; between two waypoints the height is the
 * straight line between the heights walked at them, so the road is a ramp along its length. The generator cuts the
 * ground above that height and fills the ground below it, which is the shelf: a cutting where the hill stands over the
 * line and an embankment where it falls away, both answered per chunk from nothing but the column's coordinates.
 * <p>
 * One line per road, and each line is where sections are written down as they are walked: a scout's markers become
 * waypoints here, and the road is in every world from then on without a command being run in any of them. The
 * surface is the one {@code docs/arda-roads.md} settled - a wild strip of gravel, coarse dirt, grass and path, and
 * a paved approach in the last stretch before a waypoint the road ends at - chosen per block from a hash of the
 * column, so the same column gets the same block whichever chunk asks for it.
 */
public final class RoadPaint {
    /** How many blocks before a road's end the paving starts, where a road enters a place rather than open country. */
    private static final int APPROACH = 24;

    /**
     * How far past the pavement a road's verge is cleared.
     * <p>
     * A tree one block off the edge would otherwise keep its trunk and be cut back to bare legs wherever its crown
     * overhung the road, which reads as a pole beside the pavement rather than a road through wood. Two blocks is
     * enough to take the trunks that would stand on the verge without felling the trees behind it.
     */
    private static final int VERGE = 2;

    private RoadPaint() {
    }

    /**
     * One road as generation draws it: the waypoints a surveyor walked, and how wide the strip is either side of the
     * line between them.
     * <p>
     * A waypoint is {@code {x, y, z}} in blocks, where y is the height of the ground the scout stood on - the marker's
     * own height, which is the height the road passes at there. Legs are straight, because a straight leg is the only
     * shape a chunk can be asked about without knowing the ground of the chunk next door; a road that has to bend is
     * given a waypoint at the bend, and one that has to change height is given a waypoint where it changes, since the
     * grade between two waypoints is the line between their heights.
     */
    public record Line(String id, int half, List<int[]> waypoints) {
        public Line {
            if (waypoints.size() < 2) throw new IllegalArgumentException("a road needs two waypoints: " + id);
            if (half < 1) throw new IllegalArgumentException("a road needs a width: " + id);
        }
    }

    /**
     * The roads generation paints. Empty is the normal state and a working one - the world simply has no road in it,
     * which is where Arda stood before any of these were walked.
     * <p>
     * A line is added here once its ground is settled: the waypoints are the markers a scout put down, and the half
     * is the width that stretch asks for (three of wild road, two where it enters a settlement, one for a spur).
     */
    public static final List<Line> LINES = List.of(
            // A road is written as its markers, each one's own height included:
            // new Line("camp to the ruin", 3, List.of(new int[]{4642, 96, -3315}, new int[]{4787, 79, -3058}))
    );

    /**
     * The block a generated road column is surfaced with, or null when no road runs through it.
     * <p>
     * This is asked once per column by the surface pass, so it has to be cheap and it has to answer the same thing
     * from any chunk: the only state it reads is the line table above and the column's own coordinates.
     */
    public static BlockState at(int x, int z) {
        Line on = lineAt(x, z);
        if (on == null) return null;
        return surface(x, z, approach(on, x, z));
    }

    /**
     * The height a road's corridor stands at in a column, or null where no road reaches it.
     * <p>
     * The ground under the road is cut and filled to this. The corridor is level across its width, because every column
     * takes the height of the nearest point of the line rather than its own ground, and a ramp along its length,
     * because between two waypoints the height is the straight line between the heights walked at them. There is no
     * search and no heightmap read: a chunk cannot see the ground it would have to compare itself with, and a pass that
     * guessed from the ground it could see would put the step back into the road.
     */
    public static Integer heightAt(int x, int z) {
        Station on = nearest(x, z, VERGE);
        return on == null ? null : on.height();
    }

    /** The paved road a column belongs to, or null where no pavement runs through it. */
    public static Line lineAt(int x, int z) {
        Station on = nearest(x, z, 0);
        return on == null ? null : on.line();
    }

    /** The road whose corridor takes a column in: the pavement and the verge either side of it. */
    private static Line clearingAt(int x, int z) {
        Station on = nearest(x, z, VERGE);
        return on == null ? null : on.line();
    }

    /**
     * A column's place on the nearest road: the line, how far it stands from the centre, and the height the road
     * passes at there.
     * <p>
     * The legs are segments between waypoints, so the distance is measured to the segment rather than to a ray - a
     * road's width is measured from the road, not from the corner it started at - and the height is read at the same
     * point, so a column beside a bend takes the height of the bend it stands beside.
     */
    private static Station nearest(int x, int z, int slack) {
        Station best = null;
        for (Line line : LINES) {
            double reach = line.half() + slack;
            List<int[]> points = line.waypoints();
            for (int index = 1; index < points.size(); index++) {
                int[] from = points.get(index - 1);
                int[] to = points.get(index);
                double dx = to[0] - from[0];
                double dz = to[2] - from[2];
                double length = dx * dx + dz * dz;
                double along = length == 0.0D ? 0.0D
                        : Math.clamp(((x - from[0]) * dx + (z - from[2]) * dz) / length, 0.0D, 1.0D);
                double away = Math.hypot(x - (from[0] + dx * along), z - (from[2] + dz * along));
                if (away > reach) continue;
                if (best != null && away >= best.away()) continue;
                best = new Station(line, away, (int) Math.round(from[1] + (to[1] - from[1]) * along));
            }
        }
        return best;
    }

    /** Whether a column is close enough to either end of its road to be paved rather than wild. */
    private static boolean approach(Line line, int x, int z) {
        List<int[]> points = line.waypoints();
        for (int[] end : List.of(points.getFirst(), points.getLast())) {
            if (Math.hypot(x - end[0], z - end[2]) <= APPROACH) return true;
        }
        return false;
    }

    /**
     * The per-column mix of a strip, from a hash of the column: never a blob, and never a different answer at a chunk
     * border. The paved mix is the wild one with the loose ground replaced by built masonry, which is the swap the
     * mod's villages already make for their own streets.
     */
    private static BlockState surface(int x, int z, boolean paved) {
        int roll = Math.floorMod(hash(x, z), 100);
        if (paved) {
            if (roll < 50) return Blocks.COBBLESTONE.defaultBlockState();
            if (roll < 77) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 87) return Blocks.COARSE_DIRT.defaultBlockState();
            if (roll < 95) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
        if (roll < 40) return Blocks.GRAVEL.defaultBlockState();
        if (roll < 65) return Blocks.COARSE_DIRT.defaultBlockState();
        if (roll < 85) return Blocks.GRASS_BLOCK.defaultBlockState();
        return Blocks.DIRT_PATH.defaultBlockState();
    }

    /**
     * Clears a generated road of what the passes after the ground stood in it: trunks, leaves, vines and the plants
     * that grew between the pavers.
     * <p>
     * The road is painted before the features run - trees and grass are placed after the surface is dressed - so a
     * strip through wood would otherwise be a line of road blocks with a forest growing out of them. Nothing here
     * knows about trees: a road column is walked from the top down, every leaf, log, vine and replaceable plant is
     * cover rather than ground, and the first block that is not cover is the surface the road is laid on. The cover is
     * then taken away and the road's own block written back where a trunk had taken its place.
     * <p>
     * It is run over the whole column rather than a corridor of sixteen blocks, because a trunk cut off at that height
     * leaves its crown hanging over the road with nothing holding it up. Water is left alone: a column the map floods
     * has no road in it, and its own cover is the river's business.
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
                if (clearingAt(blockX, blockZ) == null) continue;

                int ground = minY - 1;
                for (int y = maxY; y >= minY; y--) {
                    BlockState state = level.getBlockState(at.set(blockX, y, blockZ));
                    // Sky is not ground either: a column that is nothing but air has no road in it, and reading the
                    // topmost air block as the surface would leave the road's own cover standing over nothing.
                    if (state.isAir() || cover(state)) continue;
                    ground = y;
                    break;
                }
                if (ground < minY) continue;
                if (!level.getFluidState(at.set(blockX, ground, blockZ)).isEmpty()) continue;

                for (int y = ground + 1; y <= maxY; y++) {
                    if (!cover(level.getBlockState(at.set(blockX, y, blockZ)))) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    cleared++;
                }

                BlockState surface = at(blockX, blockZ);
                if (surface == null || level.getBlockState(at.set(blockX, ground, blockZ)).is(surface.getBlock())) continue;
                level.setBlock(at, surface, 2);
            }
        }
        return cleared;
    }

    /** What a road's corridor takes away: what a tree or a patch of ground cover put there, never the ground. */
    private static boolean cover(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)
                || state.is(BlockTags.REPLACEABLE_BY_TREES) || state.is(Blocks.VINE)
                || state.is(Blocks.SNOW);
    }

    /** A stable hash of one column, so a block's surface does not depend on which chunk or which run asked. */
    private static int hash(int x, int z) {
        int value = x * 374761393 + z * 668265263;
        value = (value ^ (value >>> 13)) * 1274126177;
        return value ^ (value >>> 16);
    }

    /** Where a column stands on its road: the line, its distance from the centre, and the road's height there. */
    private record Station(Line line, double away, int height) {
    }
}
