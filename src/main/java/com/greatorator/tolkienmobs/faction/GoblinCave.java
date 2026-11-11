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
 * holds the pass. The caverns either side are not - a flat floor, a wall that swells and pinches round
 * the compass, a ceiling that domes over, and an alcove or two off each - because a goblin hall is a room
 * somebody made and the caves behind it are not. The sine terms below are the shape; they are the same
 * ones {@code tools/cave_section.py} draws with, so the drawing and the cut agree.
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
    private static final int HALL_FRONT = HALL_BACK + 16; // and where it ends
    private static final int HALL_SIDE = 7;       // half its width
    private static final int HALL_HEIGHT = 10;    // the hall's own headroom
    private static final int LANDING_HEIGHT = 5;  // the landing's
    private static final int LANDING_SIDE = 3;    // half the landing's width
    private static final int CHAMBER_AT = 94;     // the caverns' middle, down the way in
    private static final int CHAMBER_SIDE = 24;   // and how far out to either side
    private static final int CHAMBER_RA = 15;     // their reach along the way in
    private static final int CHAMBER_RS = 12;     // and across
    private static final int CHAMBER_HEIGHT = 13;
    private static final int PASSAGE_NEAR = HALL_SIDE + 1;  // the way out of the hall to each cavern
    private static final int PASSAGE_FAR = 14;
    private static final int PASSAGE_HEIGHT = 5;
    private static final int FAR = CHAMBER_AT + CHAMBER_RA + 6;           // the plan's own far wall
    private static final int FAR_SIDE = CHAMBER_SIDE + CHAMBER_RS + 6;    // and how wide it reaches
    private static final int ROOF = 3;            // rock kept between the cave and the sky above it
    private static final int OPEN = 4;            // the way in's own blocks, cut through the surface
    private static final int OUTSIDE = 1;         // blocks cut in front of the door, so it is not walled in

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
                for (int y = floor; y <= surface + 3; y++) {
                    if (!carve(a, s, y, surface, floor)) continue;
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
    private static boolean carve(int a, int s, int y, int doorFloor, int floor) {
        int sideways = Math.abs(s);
        if (a >= 0 && a <= 2 && sideways <= 1 && y >= doorFloor && y <= doorFloor + 3) return true;
        if (a >= 1 && a <= RAMP && sideways <= 1) {
            int low = floor + DROP - (a - 1) * DROP / (RAMP - 1);
            if (y >= low && y <= low + 3) return true;
        }
        if (a >= RAMP - 2 && a <= HALL_BACK && sideways <= LANDING_SIDE
                && y >= floor && y <= floor + LANDING_HEIGHT - 1) {
            return true;
        }
        if (a >= HALL_BACK && a <= HALL_FRONT && sideways <= HALL_SIDE
                && y >= floor && y <= floor + HALL_HEIGHT - 1) {
            return true;
        }
        if (a >= CHAMBER_AT - 2 && a <= CHAMBER_AT + 2 && sideways >= PASSAGE_NEAR && sideways <= PASSAGE_FAR
                && y >= floor && y <= floor + PASSAGE_HEIGHT - 1) {
            return true;
        }
        return cavern(a, s, y, floor);
    }

    /** A cavern: a flat floor, a wall that swells and pinches, a ceiling that domes over. */
    private static boolean cavern(int a, int s, int y, int floor) {
        if (y < floor || y >= floor + CHAMBER_HEIGHT) return false;
        for (int sign : new int[]{-1, 1}) {
            int centreS = CHAMBER_SIDE * sign;
            if (swell(a, s, y, floor, CHAMBER_AT, centreS, CHAMBER_RA, CHAMBER_RS,
                    CHAMBER_HEIGHT, true)) {
                return true;
            }
            // An alcove or two, so the room is not one swept shape.
            if (swell(a, s, y, floor, CHAMBER_AT + 8, centreS + 7, 7, 6, 9, false)) return true;
            if (swell(a, s, y, floor, CHAMBER_AT - 9, centreS - 3, 6, 5, 9, false)) return true;
        }
        return false;
    }

    /**
     * One rounded lump of a cavern. With a share given, its wall swells and pinches round the compass;
     * without one - an alcove - it is a plain dome, so an alcove reads as a pocket in the wall.
     */
    private static boolean swell(int a, int s, int y, int floor, double centreA, double centreS,
                                 double reachA, double reachS, int height, boolean lumpy) {
        if (y < floor || y >= floor + height) return false;
        double share = (y - floor) / (double) (height - 1);
        double flat = lumpy ? 0.4 : 0.5;
        double pinch = lumpy ? 0.5 : 0.55;
        double shrink = share <= flat ? 1.0 : 1.0 - pinch * ((share - flat) / (1.0 - flat));
        double da = a - centreA;
        double ds = s - centreS;
        if (lumpy) {
            double angle = Math.atan2(ds, da);
            shrink *= 1.0 + 0.20 * Math.sin(3.0 * angle + 0.6) + 0.11 * Math.sin(5.0 * angle - 1.3);
        }
        double ra = reachA * shrink;
        double rs = reachS * shrink;
        if (ra <= 0 || rs <= 0) return false;
        return da * da / (ra * ra) + ds * ds / (rs * rs) <= 1.0;
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
