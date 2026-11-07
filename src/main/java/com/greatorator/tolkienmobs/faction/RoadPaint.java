package com.greatorator.tolkienmobs.faction;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Roads the world generates, painted into the ground as its chunks are made.
 * <p>
 * The campaign's own roads are laid by a pass, {@code /tmroad}, because their alignment is a search over ground that
 * exists: a shelf cut into a hillside needs the hill. A road that stands in every world cannot search for anything,
 * since it has to answer for a chunk that does not exist yet - so it is a pure function of x and z instead, exactly
 * the way the drawn rivers are: a line of waypoints, a half-width, and a per-column mix that a chunk border cannot
 * split. Nothing is cut or filled, so a generated road never has to know what stands beside it; it paints the column
 * it owns and leaves the ground's own height alone.
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

    private RoadPaint() {
    }

    /**
     * One road as generation draws it: the waypoints a surveyor walked, and how wide the strip is either side of the
     * line between them.
     * <p>
     * A waypoint is {@code {x, z}} in blocks. Legs are straight, because a straight leg is the only shape a chunk can
     * be asked about without knowing the ground of the chunk next door; a road that has to bend is given a waypoint at
     * the bend, which is the same thing a surveyor writes in a notebook.
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
            // A road is written as a line of blocks between the markers that establish it:
            // new Line("camp to the ruin", 3, List.of(new int[]{4642, -3315}, new int[]{4787, -3058}))
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

    /** The road running through a column, or null where none does. The nearest line wins where two overlap. */
    public static Line lineAt(int x, int z) {
        Line best = null;
        double nearest = Double.MAX_VALUE;
        for (Line line : LINES) {
            double away = distance(line, x, z);
            if (away > line.half() || away >= nearest) continue;
            nearest = away;
            best = line;
        }
        return best;
    }

    /** Whether a column is close enough to either end of its road to be paved rather than wild. */
    private static boolean approach(Line line, int x, int z) {
        List<int[]> points = line.waypoints();
        for (int[] end : List.of(points.getFirst(), points.getLast())) {
            if (Math.hypot(x - end[0], z - end[1]) <= APPROACH) return true;
        }
        return false;
    }

    /**
     * How far a column stands from a line's centre, in blocks: the nearest point of the nearest leg.
     * <p>
     * The legs are grid lines between waypoints, so the distance is the perpendicular to a segment rather than to a
     * ray - a road's width is measured from the road, not from the corner it started at.
     */
    private static double distance(Line line, int x, int z) {
        List<int[]> points = line.waypoints();
        double best = Double.MAX_VALUE;
        for (int index = 1; index < points.size(); index++) {
            int[] from = points.get(index - 1);
            int[] to = points.get(index);
            double dx = to[0] - from[0];
            double dz = to[1] - from[1];
            double length = dx * dx + dz * dz;
            double along = length == 0.0D ? 0.0D
                    : Math.clamp(((x - from[0]) * dx + (z - from[1]) * dz) / length, 0.0D, 1.0D);
            best = Math.min(best, Math.hypot(x - (from[0] + dx * along), z - (from[1] + dz * along)));
        }
        return best;
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

    /** A stable hash of one column, so a block's surface does not depend on which chunk or which run asked. */
    private static int hash(int x, int z) {
        int value = x * 374761393 + z * 668265263;
        value = (value ^ (value >>> 13)) * 1274126177;
        return value ^ (value >>> 16);
    }
}
