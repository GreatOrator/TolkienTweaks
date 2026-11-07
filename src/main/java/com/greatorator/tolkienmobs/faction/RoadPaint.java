package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.IdentityHashMap;
import java.util.function.IntBinaryOperator;
import java.util.List;
import java.util.Map;

/**
 * Roads the world generates, painted into the ground as its chunks are made.
 * <p>
 * The campaign's own roads are laid by a pass, {@code /tmroad}, because their alignment is a search over ground that
 * exists: a shelf cut into a hillside needs the hill. A road that stands in every world cannot search for anything,
 * since it has to answer for a chunk that does not exist yet - so it is a pure function of x and z instead, exactly
 * the way the drawn rivers are: a line of waypoints, each carrying the height a scout walked it at, and a half-width.
 * <p>
 * The heights are what makes it a graded road rather than a painted one. Every column of the corridor takes the height
 * of the nearest point of the line, so the strip is level across its width; between two waypoints the height is a cubic
 * through the heights walked at them, so the road is a ramp along its length that curves through a bend instead of
 * kinking at it. The generator cuts the ground above that height and fills the ground below it, which is the shelf: a
 * cutting where the hill stands over the line and an embankment where it falls away, both answered per chunk from
 * nothing but the column's coordinates, and both held within {@link #MAX_CUT} and {@link #MAX_FILL} of the ground the
 * column already has.
 * <p>
 * One line per road, and each line is where sections are written down as they are walked: a scout's markers become
 * waypoints here, and the road is in every world from then on without a command being run in any of them. A line's
 * waypoints can be heights walked at them, or places with {@link #GROUND} in the height's place - in which case the
 * grade is read from the ground the world actually has, which is the form a road baked between two markers takes. The
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

    /** How far a generated road may cut into the ground it crosses, in blocks. */
    public static final int MAX_CUT = 4;
    /** How far it may stand above that ground. */
    public static final int MAX_FILL = 4;

    /**
     * A waypoint's y when the line follows the ground rather than carrying a height walked at it.
     * <p>
     * A baked line is a place, not a measurement: the alignment is what a walk found, and the height is read back out
     * of the ground of whichever world raises it. That is what lets a road outlive a change of terrain code or a
     * different seed - the places still mean what the scout meant, and the ground says the rest. A line's waypoints
     * are either all heights or all places, and the first one's y says which.
     */
    public static final int GROUND = 0;

    /**
     * How far along a ground line either side of a point is read when its height is taken: eight blocks, so a boulder
     * or a one-block dip is averaged out of the road rather than built into it.
     */
    private static final int GROUND_READ = 8;

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

        /** Whether the waypoints are places on the map, their heights to be read from the ground of the world. */
        public boolean followsGround() {
            return waypoints.getFirst()[1] == GROUND;
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
            // Elladan's camp's road end to the block the valley road ends on, walked along the contours of
            // the scouted ground rather than over them. Every waypoint is a place rather than a height, so each
            // world grades the road from its own ground.
            new Line("Elladan's camp to the valley road", 3,
                    List.of(new int[]{4648, 0, -3314}, new int[]{4654, 0, -3308}, new int[]{4654, 0, -3293},
                            new int[]{4666, 0, -3294}, new int[]{4682, 0, -3288}, new int[]{4698, 0, -3289},
                            new int[]{4711, 0, -3276}, new int[]{4723, 0, -3264}, new int[]{4736, 0, -3263},
                            new int[]{4751, 0, -3271}, new int[]{4762, 0, -3266}, new int[]{4769, 0, -3251},
                            new int[]{4785, 0, -3246}, new int[]{4795, 0, -3236}, new int[]{4809, 0, -3223},
                            new int[]{4821, 0, -3211}, new int[]{4833, 0, -3198}, new int[]{4849, 0, -3193},
                            new int[]{4864, 0, -3184}, new int[]{4867, 0, -3168}, new int[]{4879, 0, -3156},
                            new int[]{4895, 0, -3150}, new int[]{4899, 0, -3132}, new int[]{4899, 0, -3112},
                            new int[]{4903, 0, -3093}, new int[]{4911, 0, -3076}, new int[]{4911, 0, -3075},
                            new int[]{5006, 0, -3075}))
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
     * because between two waypoints the height is a cubic through the heights walked at them, eased at each so the
     * profile curves through a bend rather than kinking there, and bounded from the ground it crosses by
     * {@link #MAX_CUT} and {@link #MAX_FILL}. There is no search and no heightmap read: a chunk cannot see the ground it would have to compare itself with, and a pass that
     * guessed from the ground it could see would put the step back into the road.
     */
    public static Integer heightAt(int x, int z, IntBinaryOperator ground) {
        Station on = nearest(x, z, VERGE);
        return on == null ? null : on.height(ground);
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
                Profile grade = profile(line);
                double walked = grade.along()[index - 1]
                        + along * (grade.along()[index] - grade.along()[index - 1]);
                best = new Station(line, away, walked);
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

    /** Where a column stands on its road: the line, its distance from the centre, and how far along it it stands. */
    private record Station(Line line, double away, double walked) {
        /** The road's height there: the grade its waypoints carry, or the ground under a line of places. */
        int height(IntBinaryOperator ground) {
            return line.followsGround() ? along(line, walked, ground) : profile(line).heightAt(walked);
        }
    }

    /**
     * A line's grade read by distance along it: the heights it was walked at, and the slope it passes through each.
     * <p>
     * The heights between waypoints are a cubic through them rather than the straight line between two, because a road
     * that changes direction while it is falling would otherwise have a corner in its profile at the waypoint - and a
     * corner in a grade is a step to walk up. The slope at a waypoint is the two legs either side averaged and held to
     * a third of the shallower of them, and levelled where the road passes a crest or a hollow: that is the shape a
     * cubic cannot overshoot with, so easing a bend never digs for a hollow nobody walked.
     */
    private record Profile(double[] along, double[] height, double[] slope) {
        int heightAt(double distance) {
            int last = height.length - 1;
            if (distance <= along[0]) return (int) Math.round(height[0]);
            if (distance >= along[last]) return (int) Math.round(height[last]);
            int leg = 0;
            while (leg < last - 1 && distance > along[leg + 1]) leg++;
            double run = along[leg + 1] - along[leg];
            if (run <= 0.0D) return (int) Math.round(height[leg + 1]);
            double turned = (distance - along[leg]) / run;
            double square = turned * turned;
            double cube = square * turned;
            double value = (2.0D * cube - 3.0D * square + 1.0D) * height[leg]
                    + (cube - 2.0D * square + turned) * run * slope[leg]
                    + (3.0D * square - 2.0D * cube) * height[leg + 1]
                    + (cube - square) * run * slope[leg + 1];
            return (int) Math.round(value);
        }
    }

    /** The grade of each line, read once: a line is a constant, so its profile is the same in every chunk. */
    private static final Map<Line, Profile> PROFILES = new IdentityHashMap<>();

    private static Profile profile(Line line) {
        Profile known = PROFILES.get(line);
        if (known != null) return known;
        List<int[]> points = line.waypoints();
        int legs = points.size() - 1;
        double[] along = new double[points.size()];
        double[] height = new double[points.size()];
        double[] rate = new double[legs];
        for (int index = 0; index < points.size(); index++) {
            int[] point = points.get(index);
            height[index] = point[1];
            if (index > 0) {
                int[] before = points.get(index - 1);
                along[index] = along[index - 1] + Math.hypot(point[0] - before[0], point[2] - before[2]);
            }
        }
        for (int leg = 0; leg < legs; leg++) {
            double run = along[leg + 1] - along[leg];
            rate[leg] = run == 0.0D ? 0.0D : (height[leg + 1] - height[leg]) / run;
        }
        double[] slope = new double[points.size()];
        slope[0] = rate[0];
        slope[legs] = rate[legs - 1];
        for (int index = 1; index < legs; index++) {
            slope[index] = ease(rate[index - 1], rate[index]);
        }
        Profile made = new Profile(along, height, slope);
        PROFILES.put(line, made);
        return made;
    }

    /** The slope a waypoint is passed through: its legs averaged, level at a crest, and never an overshoot. */
    private static double ease(double before, double after) {
        if (before * after <= 0.0D) return 0.0D;
        double average = (before + after) / 2.0D;
        double limit = 3.0D * Math.min(Math.abs(before), Math.abs(after));
        return Math.clamp(average, -limit, limit);
    }

    /**
     * The height of a ground line at a distance along it: the ground at that point and eight blocks either side,
     * averaged, so the road takes the shape of the hillside and not the shape of one column.
     * <p>
     * The ground is asked for by coordinate alone, and every caller asks in the same way, so the answer is the same in
     * every chunk: a road is still a function of x and z, it simply reads the draught of the world it is in rather than
     * a height someone walked in another one.
     */
    private static int along(Line line, double walked, IntBinaryOperator ground) {
        int total = 0;
        for (int step = -GROUND_READ; step <= GROUND_READ; step += GROUND_READ) {
            int[] spot = spot(line, walked + step);
            total += ground.applyAsInt(spot[0], spot[1]);
        }
        return (int) Math.round(total / 3.0D);
    }

    /** The column a distance along a line falls on, rounded, so a ground line can be read between its waypoints. */
    private static int[] spot(Line line, double distance) {
        double[] along = profile(line).along();
        List<int[]> points = line.waypoints();
        int last = along.length - 1;
        double clamped = Math.clamp(distance, along[0], along[last]);
        int leg = 0;
        while (leg < last - 1 && clamped > along[leg + 1]) leg++;
        double run = along[leg + 1] - along[leg];
        double turned = run <= 0.0D ? 0.0D : (clamped - along[leg]) / run;
        int[] from = points.get(leg);
        int[] to = points.get(leg + 1);
        return new int[]{
                (int) Math.round(from[0] + (to[0] - from[0]) * turned),
                (int) Math.round(from[2] + (to[2] - from[2]) * turned)};
    }
}
