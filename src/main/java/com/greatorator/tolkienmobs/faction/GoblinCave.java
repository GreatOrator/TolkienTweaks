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
 * The shape is the one {@code tools/cave_section.py} draws, and the two must agree: a mouth at the door,
 * a ramp of one in two down into the hill, a landing that runs into the hall's own back wall, a hall ten
 * blocks tall with a chamber out to either side. Every cell is held {@link #ROOF} blocks under the ground
 * above it, so the mouth is the one place the cave opens - except the mouth's own first few blocks, which
 * are cut through the surface because that is what a mouth is.
 */
public final class GoblinCave {
    /** The pin the door is read from. A place name, so /tmpin, /tmtp and the catalogue all know it. */
    public static final String PIN = "goblins";

    private static final int RAMP = 16;          // blocks the way in runs before the landing
    private static final int DROP = 8;           // blocks it falls over that run, so one in two
    private static final int HALL_BACK = 22;     // where the hall starts, past the door
    private static final int HALL_FRONT = 38;    // and where it ends
    private static final int HALL_SIDE = 7;      // half its width
    private static final int HALL_UP = 1;        // its ceiling, against the door's own ground
    private static final int CHAMBER_NEAR = 12;  // how far out the chambers start
    private static final int CHAMBER_FAR = 20;   // and how far they reach
    private static final int CHAMBER_BACK = 2;   // past the hall's wall
    private static final int CHAMBER_FRONT = 11;
    private static final int CHAMBER_UP = -3;    // their ceiling, against the door's own ground
    private static final int ROOF = 3;           // rock kept between the cave and the sky above it
    private static final int OPEN = 4;           // the way in's own blocks, cut through the surface
    private static final int OUTSIDE = 1;        // blocks cut in front of the door, so it is not walled in

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
        for (int a = -OUTSIDE; a <= HALL_FRONT; a++) {
            for (int s = -CHAMBER_FAR - 1; s <= CHAMBER_FAR + 1; s++) {
                int[] want = want(a, s, floor);
                if (want == null) continue;
                int x = doorX + stepX * a + acrossX * s;
                int z = doorZ + stepZ * a + acrossZ * s;
                Integer top = ground(level, x, z, ground);
                if (top == null) continue;
                // The mouth's own few blocks are the one place the cave is meant to open: they are cut
                // through the surface, which is what makes a mouth a mouth rather than a buried room.
                int ceiling = a <= OPEN ? want[1] : Math.min(want[1], top - ROOF);
                if (ceiling < want[0]) {
                    dropped += want[1] - want[0] + 1;
                    continue;
                }
                dropped += want[1] - ceiling;
                for (int y = want[0]; y <= ceiling; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(at);
                    if (state.isAir() || state.getFluidState().is(Fluids.WATER)) continue;
                    level.setBlock(at, Blocks.CAVE_AIR.defaultBlockState(), 2);
                    cells++;
                }
            }
        }
        return new Result(pin, into, floor, floor + DROP + HALL_UP, cells, dropped);
    }

    /** The plan's own void at one door-relative cell: {@code {bottom, top}}, or null for rock. */
    @Nullable
    private static int[] want(int a, int s, int floor) {
        int sideways = Math.abs(s);
        if (a <= 2 && sideways <= 1) return new int[]{floor + DROP, floor + DROP + 3};
        if (a >= 1 && a <= RAMP && sideways <= 1) {
            int low = rampFloor(a, floor);
            return new int[]{low, low + 3};
        }
        if (a >= RAMP - 2 && a <= HALL_BACK && sideways <= 3) return new int[]{floor, floor + 4};
        if (a >= HALL_BACK && a <= HALL_FRONT && sideways <= HALL_SIDE) {
            return new int[]{floor, floor + DROP + HALL_UP};
        }
        if (a >= HALL_BACK + 4 && a <= HALL_BACK + 6 && sideways <= CHAMBER_FAR) {
            return new int[]{floor, floor + 4};
        }
        if (a >= HALL_BACK + CHAMBER_BACK && a <= HALL_BACK + CHAMBER_FRONT
                && sideways >= CHAMBER_NEAR && sideways <= CHAMBER_FAR) {
            return new int[]{floor, floor + DROP + CHAMBER_UP};
        }
        return null;
    }

    /** The way in falls from the door's own ground to the floor, one block in two. */
    private static int rampFloor(int a, int floor) {
        return floor + DROP - (a - 1) * DROP / (RAMP - 1);
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
        for (int a : new int[]{-OUTSIDE, HALL_FRONT}) {
            for (int s : new int[]{-CHAMBER_FAR - 1, CHAMBER_FAR + 1}) {
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
