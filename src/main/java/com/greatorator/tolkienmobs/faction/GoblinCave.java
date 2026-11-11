package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * The goblin cave page 27 hunts Ulmug in, cut into the hill behind a pinned door.
 * <p>
 * A cave is the one ground in the campaign that is not a template, so it is not placed: it is taken out
 * of the hill the world already made. The door is where a scout stood - {@code /tmpin place goblins} -
 * and everything else is measured from it. Which way the hill rises is read rather than given, so the
 * same pin in the same world always cuts the same cave and the cave always goes into the hill instead of
 * out of it.
 * <p>
 * Two kinds of room, on purpose. The hall is squared: it is the worked part, cut and floored by whoever
 * holds the pass. The chambers are not - they are built out of overlapping lobes, so their walls have
 * shoulders and recesses rather than one clean sweep, they swell and pinch round the compass and dome
 * over, and then rock is left standing in them: rubble on the floor, stalactites hanging and stalagmites
 * standing wherever the cell hash puts them. Two chambers flank the hall and a third, larger, sits
 * behind its far wall. A goblin hall is a room somebody made and the caves behind it are not. The sines
 * and the hash are the shape; they are the same ones {@code tools/cave_section.py} draws with, so the
 * drawing and the cut agree.
 * <p>
 * The one rule the plan keeps is that the cave never opens where nobody cut a mouth: every cell is held
 * {@link #ROOF} blocks under the ground above it, except the way in's own first few blocks, which are cut
 * through the surface because that is what a mouth is.
 */
public final class GoblinCave {
    /** The pin the door is read from. A place name, so /tmpin, /tmtp and the catalogue all know it. */
    public static final String PIN = "goblins";

    private static final int DROP = 40;           // how far the way in falls, from the door's ground
    private static final int RAMP = DROP * 2;     // one block down in two, so twice as far as it falls
    private static final int HALL_BACK = RAMP + 6;        // where the hall starts, past the door
    private static final int HALL_FRONT = HALL_BACK + 18; // and where it ends
    private static final int HALL_SIDE = 8;       // half its width
    private static final int HALL_HEIGHT = 13;    // the hall's own headroom
    private static final int LANDING_HEIGHT = 5;  // the landing's
    private static final int LANDING_SIDE = 3;    // half the landing's width
    private static final int PASSAGE_NEAR = HALL_SIDE + 1;   // the way out of the hall to each flank
    private static final int PASSAGE_FAR = HALL_SIDE + 13;
    private static final int PASSAGE_HEIGHT = 7;
    private static final int FLANK_PASS_BACK = HALL_BACK + 10;   // where that passage leaves the hall
    private static final int FLANK_PASS_FRONT = HALL_BACK + 22;  // and where it arrives
    private static final int BACK_PASS_BACK = HALL_FRONT - 2;     // where the hall's far wall opens
    private static final int BACK_PASS_FRONT = 152;               // and where the back chamber holds it
    private static final int BACK_PASS_SIDE = 3;
    private static final int CHAMBER_TALL = 20;   // the tallest any lobe of a chamber gets
    private static final int FAR = 220;           // the plan's own far wall
    private static final int FAR_SIDE = 60;       // and how wide it reaches

    /** One lobe of a chamber: where it sits in the plan, how far it reaches, how tall it is. */
    private record Lobe(int a, int s, int ra, int rs, int height) {
    }

    /**
     * The chamber either side of the hall, as lobes rather than an ellipse: four overlapping ones, so
     * the outline has shoulders and recesses instead of one clean sweep. Mirrored, so this is one side.
     */
    private static final Lobe[] FLANK = {
            new Lobe(112, 28, 20, 18, 16),
            new Lobe(136, 36, 12, 11, 12),
            new Lobe(96, 40, 11, 10, 11),
            new Lobe(118, 46, 8, 8, 10),
    };

    /** The chamber behind the hall's far wall, larger than either flank. */
    private static final Lobe[] BACK = {
            new Lobe(172, 0, 26, 30, 20),
            new Lobe(196, -18, 15, 16, 15),
            new Lobe(200, 20, 14, 15, 14),
            new Lobe(150, 13, 13, 13, 13),
    };
    private static final int ROOF = 3;            // rock kept between the cave and the sky above it
    private static final int OPEN = 4;            // the way in's own blocks, cut through the surface
    private static final int OUTSIDE = 1;         // blocks cut in front of the door, so it is not walled in
    private static final int MOUTH_SIDE = 2;      // half the mouth's width, and the way in's
    private static final int MOUTH_HEIGHT = 7;    // and how tall both are
    private static final int SPUR_TOP = 16;       // of 256: how often a column of cavern keeps a stalactite
    private static final int SPUR_LOW = 14;       // and how often a stalagmite
    private static final int SPUR_LONG = 5;       // the longest of either, in blocks
    private static final double COARSE = 0.16;    // how far a wall wanders, as a share of its reach
    private static final double FINE = 0.06;      // and how much of that is per two blocks

    /** What one cut came to, for the command to say. */
    public record Result(BlockPos door, Direction into, int floor, int ceiling, int cells, int dropped) {
    }

    private GoblinCave() {
    }

    /**
     * Cuts the cave behind this world's goblin pin. Nothing is cut when the pin is missing, or when the
     * ground under it has not been made yet: a cave is taken out of a hill, so there has to be one.
     */
    @Nullable
    public static Result cut(ServerLevel level) {
        BlockPos pin = Pins.at(level, PIN);
        if (pin == null || !TolkienDimensions.ARDA.equals(level.dimension())) return null;
        Map<Long, Integer> ground = new HashMap<>();
        Integer surface = ground(level, pin.getX(), pin.getZ(), ground);
        if (surface == null) return null;

        int doorX = pin.getX();
        int doorZ = pin.getZ();
        Direction into = steepest(level, doorX, doorZ, surface, ground);
        int stepX = into.getStepX();
        int stepZ = into.getStepZ();
        int acrossX = -stepZ;
        int acrossZ = stepX;

        // Every chunk the cave touches is asked for first: a block written to a chunk nobody is holding
        // is a block written to nothing.
        reach(level, doorX, doorZ, stepX, stepZ, acrossX, acrossZ);

        int floor = surface - DROP;
        int cells = 0;
        int dropped = 0;
        for (int a = -OUTSIDE; a <= FAR; a++) {
            for (int s = -FAR_SIDE; s <= FAR_SIDE; s++) {
                int x = doorX + stepX * a + acrossX * s;
                int z = doorZ + stepZ * a + acrossZ * s;
                Integer top = ground(level, x, z, ground);
                if (top == null) continue;
                Spur spur = spur(a, s, floor);
                for (int y = floor; y <= surface + MOUTH_HEIGHT - 1; y++) {
                    if (!carve(a, s, y, surface, floor, spur)) continue;
                    // The mouth's own few blocks are the one place the cave is meant to open: they are cut
                    // through the surface, which is what makes a mouth a mouth rather than a buried room.
                    if (a > OPEN && y > top - ROOF) {
                        dropped++;
                        continue;
                    }
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(at);
                    if (state.isAir() || state.getFluidState().is(Fluids.WATER)) continue;
                    level.setBlock(at, Blocks.CAVE_AIR.defaultBlockState(), 2);
                    cells++;
                }
            }
        }
        return new Result(pin, into, floor, floor + HALL_HEIGHT - 1, cells, dropped);
    }

    /** Whether the plan puts cave air at one door-relative cell, before the hill is consulted. */
    private static boolean carve(int a, int s, int y, int doorFloor, int floor, Spur spur) {
        int sideways = Math.abs(s);
        if (a >= 0 && a <= 2 && sideways <= MOUTH_SIDE
                && y >= doorFloor && y <= doorFloor + MOUTH_HEIGHT - 1) {
            return true;
        }
        if (a >= 1 && a <= RAMP && sideways <= MOUTH_SIDE) {
            int low = floor + DROP - (a - 1) * DROP / (RAMP - 1);
            if (y >= low && y <= low + MOUTH_HEIGHT - 1) return true;
        }
        if (a >= RAMP - 2 && a <= HALL_BACK && sideways <= LANDING_SIDE
                && y >= floor && y <= floor + LANDING_HEIGHT - 1) {
            return true;
        }
        if (a >= HALL_BACK && a <= HALL_FRONT && sideways <= HALL_SIDE
                && y >= floor && y <= floor + HALL_HEIGHT - 1) {
            return true;
        }
        // The way out of the hall to each flank chamber.
        if (a >= FLANK_PASS_BACK && a <= FLANK_PASS_FRONT && sideways >= PASSAGE_NEAR
                && sideways <= PASSAGE_FAR && y >= floor && y <= floor + PASSAGE_HEIGHT - 1) {
            return true;
        }
        // And on through the hall's far wall to the chamber behind it.
        if (a >= BACK_PASS_BACK && a <= BACK_PASS_FRONT && sideways <= BACK_PASS_SIDE
                && y >= floor && y <= floor + PASSAGE_HEIGHT - 1) {
            return true;
        }
        if (!cavern(a, s, y, floor)) return false;
        // A stalactite or a stalagmite is rock the cut leaves standing rather than a block it places:
        // the hillside's own material comes down from the ceiling and up off the floor.
        return spur == null || !spur.covers(y);
    }

    /** A chamber is several overlapping lobes, so no room is one ellipse with a dome on it. */
    private static boolean cavern(int a, int s, int y, int floor) {
        if (y < floor || y >= floor + CHAMBER_TALL) return false;
        // Rubble on the floor, so a cavern's bottom is not a poured slab.
        if (y == floor && hash(a >> 1, s >> 1, 505) < 40) return false;
        for (Lobe lobe : FLANK) {
            if (swell(a, s, y, floor, lobe.a(), lobe.s(), lobe.ra(), lobe.rs(), lobe.height())) return true;
            if (swell(a, s, y, floor, lobe.a(), -lobe.s(), lobe.ra(), lobe.rs(), lobe.height())) return true;
        }
        for (Lobe lobe : BACK) {
            if (swell(a, s, y, floor, lobe.a(), lobe.s(), lobe.ra(), lobe.rs(), lobe.height())) return true;
        }
        return false;
    }

    /**
     * One lobe: radii pinched near its ceiling, swelling round the compass by three sine terms, then
     * worn off true by the cell hash. The terms are {@code tools/cave_section.py}'s too.
     */
    private static boolean swell(int a, int s, int y, int floor, int centreA, int centreS,
                                 int reachA, int reachS, int height) {
        if (y < floor || y >= floor + height) return false;
        // Nothing this far outside a lobe can be inside it however far its wall wanders, so it is turned
        // away before the trigonometry: the plan is wide and asks this of every cell in it.
        if (Math.abs(a - centreA) > reachA * 1.8 + 1 || Math.abs(s - centreS) > reachS * 1.8 + 1) {
            return false;
        }
        double share = (y - floor) / (double) (height - 1);
        double shrink = share <= 0.4 ? 1.0 : 1.0 - 0.5 * ((share - 0.4) / 0.6);
        double da = a - centreA;
        double ds = s - centreS;
        double angle = Math.atan2(ds, da);
        shrink *= 1.0 + 0.26 * Math.sin(2.0 * angle + 2.1)
                + 0.17 * Math.sin(3.0 * angle + 0.6)
                + 0.09 * Math.sin(5.0 * angle - 1.3);
        double ra = reachA * shrink;
        double rs = reachS * shrink;
        if (ra <= 0 || rs <= 0) return false;
        // The wall wanders: one wobble over four blocks and a finer one over two, so it is rock rather
        // than a swept surface. The hash is the same number in the drawing tool, so the shapes agree.
        double wander = (hash(a >> 2, s >> 2, y >> 2) - 128) / 128.0 * COARSE
                + (hash(a >> 1, s >> 1, y >> 1) - 128) / 128.0 * FINE;
        return da * da / (ra * ra) + ds * ds / (rs * rs) <= 1.0 + wander;
    }

    /**
     * A deterministic 0..255 from a cell. Integer arithmetic only and no state, because
     * {@code tools/cave_section.py} has to compute the same number: the drawing is only evidence of the
     * cut while both sides agree, so a mismatch here is a silent lie in the plan.
     */
    private static int hash(int a, int s, int y) {
        int h = a * 0x27D4EB2D ^ s * 0x165667B1 ^ y * 0x9E3779B1;
        h ^= h >>> 15;
        h *= 0x85EBCA6B;
        h ^= h >>> 13;
        h *= 0xC2B2AE35;
        h ^= h >>> 16;
        return h & 0xFF;
    }

    /** What one column of a cavern keeps: how far a stalactite hangs, how far a stalagmite stands. */
    private record Spur(int low, int high, int hang, int stand) {
        boolean covers(int y) {
            return (hang > 0 && y >= high - hang) || (stand > 0 && y < low + stand);
        }
    }

    /**
     * The spur this column keeps, or null for most columns. Only rock is taken out, so a stalactite is
     * ceiling left hanging and a stalagmite is floor left standing - no material to pick, and nothing to
     * place. Both are chosen off the column's own hash, so the same cave grows the same spurs.
     */
    @Nullable
    private static Spur spur(int a, int s, int floor) {
        int hang = hash(a >> 1, s >> 1, 101) < SPUR_TOP
                ? 1 + hash(a >> 1, s >> 1, 202) % SPUR_LONG : 0;
        int stand = hash(a >> 1, s >> 1, 303) < SPUR_LOW
                ? 1 + hash(a >> 1, s >> 1, 404) % SPUR_LONG : 0;
        if (hang == 0 && stand == 0) return null;
        int low = floor;
        while (low < floor + CHAMBER_TALL && !cavern(a, s, low, floor)) low++;
        int high = low;
        while (high < floor + CHAMBER_TALL && cavern(a, s, high, floor)) high++;
        return high > low ? new Spur(low, high, hang, stand) : null;
    }

    /** The way into the hill: the compass direction whose ground rises most over the next stretch. */
    private static Direction steepest(ServerLevel level, int x, int z, int surface, Map<Long, Integer> cache) {
        Direction best = Direction.SOUTH;
        double steepest = Double.NEGATIVE_INFINITY;
        for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            double sum = 0;
            int count = 0;
            for (int a = 6; a <= 26; a += 4) {
                for (int s = -4; s <= 4; s += 4) {
                    int px = x + direction.getStepX() * a - direction.getStepZ() * s;
                    int pz = z + direction.getStepZ() * a + direction.getStepX() * s;
                    Integer top = ground(level, px, pz, cache);
                    if (top == null) continue;
                    sum += top - surface;
                    count++;
                }
            }
            if (count == 0) continue;
            double mean = sum / count;
            if (mean > steepest) {
                steepest = mean;
                best = direction;
            }
        }
        return best;
    }

    /** Asks for every chunk the cut reaches, so the ground under it exists before it is measured. */
    private static void reach(ServerLevel level, int x, int z, int stepX, int stepZ, int acrossX, int acrossZ) {
        int lowX = Integer.MAX_VALUE;
        int lowZ = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        int highZ = Integer.MIN_VALUE;
        for (int a : new int[]{-OUTSIDE, FAR}) {
            for (int s : new int[]{-FAR_SIDE, FAR_SIDE}) {
                int px = x + stepX * a + acrossX * s;
                int pz = z + stepZ * a + acrossZ * s;
                lowX = Math.min(lowX, px);
                lowZ = Math.min(lowZ, pz);
                highX = Math.max(highX, px);
                highZ = Math.max(highZ, pz);
            }
        }
        for (int cx = lowX >> 4; cx <= highX >> 4; cx++) {
            for (int cz = lowZ >> 4; cz <= highZ >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    /** The top solid block of a column, cached: the cut asks for the same columns many times. */
    @Nullable
    private static Integer ground(ServerLevel level, int x, int z, Map<Long, Integer> cache) {
        long key = ((long) x << 32) ^ (z & 0xFFFFFFFFL);
        Integer known = cache.get(key);
        if (known != null) return known;
        for (int y = level.getMaxY() - 1; y > level.getMinY(); y--) {
            BlockState state = level.getBlockState(new BlockPos(x, y, z));
            if (state.isAir() || state.getFluidState().is(Fluids.WATER)) continue;
            cache.put(key, y);
            return y;
        }
        return null;
    }
}
