package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * The Gundabad gate: the gatehouse that stands in the mountain pass before the fortress.
 * <p>
 * The build came out of a map rather than out of this pack, so it is cut rather than designed: a scout stood at
 * its four floor corners and at the top of its arch, and {@code tools/gate_capture.py} read the world between
 * those markers. It keeps every cell the box holds: the gate's own blocks, the air inside it - the passage, the
 * rooms and the hollow behind the wall - and the map's rock around it. The rock is the build, not a wrapper: the
 * gate was dug and dressed into a mountainside, so its skirts and retaining walls are that mountain, and a cut
 * that dropped the stone left the gate standing on nothing. The sky above each wing comes with it too, as air
 * as tall as the gate; {@code --trim-sky} cuts that back to the building's own top instead.
 * <p>
 * The gate is 160 wide, 81 tall and 48 deep: four 48-cubes across, two up and one in, eight files laid by the
 * offsets below the way the valley's own pieces are laid. Its front face is the box's north side, so
 * {@link #place} takes the point the gate should <em>look</em> at and turns the parts to face it.
 * <p>
 * The gate was cut a second way while the two answers were being judged - a passage arched out of the hillside
 * rather than a build set into it - and the build won: it lays fast enough that nothing is gained from the cut,
 * and the cut could carry none of the hand-dressed detail. The cut has been taken out of the pack, and this is
 * the gate.
 */
public final class GundabadGate {
    /** The gate's own box, in blocks, as it was cut: x across the facade, y up, z into the hill. */
    public static final int WIDE = 160;
    public static final int TALL = 81;
    public static final int DEEP = 48;

    /** The middle of the facade's foot: the point a caller places the gate by. */
    private static final int MIDDLE = WIDE / 2;

    private static final String FOLDER = "tolkienmobs:key_structures/gundabad_gate_";

    /**
     * One 48-cube of the gate: the file it was cut into and the corner of the gate's own box it stands at.
     * <p>
     * The sizes are read from the files rather than written here - the last column is sixteen wide and the top
     * course thirty-three tall - so a gate cut again with a course more or less still lays without a table to
     * edit.
     */
    private record Piece(String name, int x, int y, int z) {
    }

    private static final List<Piece> PIECES = List.of(
            new Piece("0_0_0", 0, 0, 0),
            new Piece("1_0_0", 48, 0, 0),
            new Piece("2_0_0", 96, 0, 0),
            new Piece("3_0_0", 144, 0, 0),
            new Piece("0_1_0", 0, 48, 0),
            new Piece("1_1_0", 48, 48, 0),
            new Piece("2_1_0", 96, 48, 0),
            new Piece("3_1_0", 144, 48, 0));

    private GundabadGate() {
    }

    /** The quarter turn that puts the gate's own north face towards the way it is asked to look. */
    public static Rotation rotationFor(Direction looks) {
        return switch (looks) {
            case NORTH -> Rotation.NONE;
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            default -> Rotation.COUNTERCLOCKWISE_90;
        };
    }

    /**
     * Lays the gate so the middle of its facade stands at {@code front} and its face looks along
     * {@code looks}.
     *
     * @return how many of the eight files were found and laid; a file missing from the pack is skipped
     */
    /** Whether the seam is dressed as part of the placing, or left bare for the ground to be judged on its own. */
    public static final boolean DRESS_ON_PLACEMENT = true;

    /** How far into the rock the hall is carried, and how far north it will walk looking for the rock. */
    private static final int HALL_INTO_ROCK = 3;
    private static final int HALL_REACH = 96;

    /** The hall's own rock: two courses to a wall, three to a roof, which is what the room's frame carries. */
    private static final int HALL_WALL = 2;
    private static final int HALL_ROOF = 3;

    /** How tall the chamber is, in courses: y 190 to 226, measured off the rear face. Read, never grown. */
    private static final int HALL_HEIGHT = 37;

    /**
     * How far north of the gate the plaza runs before the mountain: the hall starts there, not at the gate, so the
     * courtyard between the two stays open to the sky and the box only exists where it is against the rock.
     */
    private static final int HALL_PLAZA = 32;

    /** How far out from the footprint the floor is bedded into the ground beside it, in blocks. */
    private static final int FLOOR_REACH = 6;

    /** The floor stands two courses: `base` and the one above it, so the ground beside it beds to that top. */
    private static final int FLOOR_COURSE = 1;

    public static int place(ServerLevel level, BlockPos front, Rotation rotation) {
        BlockPos base = base(front, rotation);
        int laid = 0;
        for (Piece piece : PIECES) {
            StructureTemplate template = level.getStructureManager()
                    .get(Identifier.parse(FOLDER + piece.name())).orElse(null);
            if (template == null) continue;
            Vec3i size = template.getSize();
            if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) continue;
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
            BlockPos at = base.offset(turnedX(piece, size, rotation), piece.y(),
                    turnedZ(piece, size, rotation));
            open(level, at, turnedWidth(size, rotation), turnedDepth(size, rotation));
            // The piece's own box is not anchored at its corner once it is turned, so the corner this wants is
            // the minimum of the turned box rather than the position handed to the placing.
            BoundingBox box = template.getBoundingBox(settings, BlockPos.ZERO);
            BlockPos anchor = at.offset(-box.minX(), -box.minY(), -box.minZ());
            template.placeInWorld(level, anchor, anchor, settings, level.getRandom(), 2);
            laid++;
        }
        return laid;
    }

    /**
     * Takes out the skirt a drape laid across the chamber's opening - the strip behind the gate, out to the dressing's
     * reach, from the floor's top up to the top of the build there. Worlds laid before the drape learned to keep off the
     * back need it once, so the chamber leads out into the hall instead of into a wall of its own ground.
     *
     * @return how many blocks were taken out
     */
    public static int undressBack(ServerLevel level, BlockPos front, Rotation rotation) {
        BlockPos base = base(front, rotation);
        boolean turned = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        int wide = turned ? DEEP : WIDE;
        int deep = turned ? WIDE : DEEP;
        open(level, base.offset(0, 0, -DRAPE_REACH), wide, DRAPE_REACH);

        // Only the width the chamber itself spans, and only up to the gate's own top in each of those columns: the wings
        // beyond it have ground of their own standing high behind them, and that ground is not this to take away.
        int lowX = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        for (int y = 1; y < TALL - 1; y++) {
            int run = -1;
            for (int x = 0; x < wide; x++) {
                if (!level.getBlockState(base.offset(x, y, 0)).isAir()) {
                    if (run > 0 && x - run >= 8) {
                        lowX = Math.min(lowX, run);
                        highX = Math.max(highX, x - 1);
                    }
                    run = -1;
                } else if (run < 0) {
                    run = x;
                }
            }
        }
        if (lowX > highX) return 0;

        int cleared = 0;
        for (int x = lowX; x <= highX; x++) {
            int top = base.getY();
            for (int y = TALL - 1; y >= 0; y--) {
                if (!level.getBlockState(base.offset(x, y, 0)).isAir()) {
                    top = base.getY() + y;
                    break;
                }
            }
            for (int z = -DRAPE_REACH; z < 0; z++) {
                for (int y = base.getY() + 1; y <= top; y++) {
                    BlockPos at = base.offset(x, y, z);
                    if (!level.hasChunkAt(at)) continue;
                    if (level.getBlockState(at).isAir()) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    /**
     * Beds the ground beside the gate up to the floor's own top.
     * <p>
     * The floor stands two courses, so anywhere the ground beside the footprint sits a course or two below it there is a
     * lip at the edge of the build - the last thing between the gate and the country it was set into. This walks the ring
     * around the footprint, and fills the ground up to the floor's top with the stone it already has, capped with the
     * surface that was there, so the floor runs out into the ground instead of stopping above it.
     * <p>
     * It only ever adds, and only where the ground is below the floor: where the rock already stands at or above it, as
     * it does at the ends, this leaves the rock exactly as it is.
     *
     * @return how many blocks were added
     */
    public static int dressFloor(ServerLevel level, BlockPos front, Rotation rotation) {
        BlockPos base = base(front, rotation);
        boolean turned = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        int wide = turned ? DEEP : WIDE;
        int deep = turned ? WIDE : DEEP;
        open(level, base.offset(-FLOOR_REACH, 0, -FLOOR_REACH), wide + FLOOR_REACH * 2, deep + FLOOR_REACH * 2);

        int want = base.getY() + FLOOR_COURSE;
        int dressed = 0;
        for (int x = -FLOOR_REACH; x < wide + FLOOR_REACH; x++) {
            for (int z = -FLOOR_REACH; z < deep + FLOOR_REACH; z++) {
                int outX = x < 0 ? -x : (x >= wide ? x - wide + 1 : 0);
                int outZ = z < 0 ? -z : (z >= deep ? z - deep + 1 : 0);
                int out = Math.max(outX, outZ);
                if (out == 0) continue;
                BlockPos column = base.offset(x, 0, z);
                if (!level.hasChunkAt(column)) continue;
                int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ()) - 1;
                if (ground >= want) continue;
                BlockState cap = level.getBlockState(column.atY(ground));
                for (int y = ground + 1; y < want; y++) {
                    level.setBlock(column.atY(y), Blocks.STONE.defaultBlockState(), 2);
                    dressed++;
                }
                if (!cap.isAir() && !cap.canBeReplaced()) {
                    level.setBlock(column.atY(want), cap, 2);
                    dressed++;
                }
            }
        }
        return dressed;
    }

    /**
     * Carries the chamber at the back of the gate north into the mountain: the room the three rear passages open into is
     * read back off the world, and that room - its floor, its two side walls and its roof at its own ceiling - is carried
     * north until the rock stands over the roof, and three blocks further into it.
     * <p>
     * Nothing is ever taken away. Every block is written only where the cell is air, so the mountain keeps its own rock,
     * the ground keeps its own shape, and the hall simply closes onto the mountain where the mountain arrives - which is
     * what makes it read, from outside, as a way cut through a mass of rock rather than a tunnel laid in the open.
     *
     * @return how many blocks the hall added
     */
    public static int extendHall(ServerLevel level, BlockPos front, Rotation rotation) {
        if (rotation != Rotation.NONE) return 0;
        BlockPos base = base(front, rotation);
        int wide = WIDE;
        open(level, base.offset(-4, 0, -HALL_REACH), wide + 8, DEEP + HALL_REACH + 4);

        // The room: its floor is the lowest row of the rear face that air and rock close between, and its height is the
        // chamber's own thirty-seven courses - the rear face is open above that, so there is no ceiling up there to stop
        // at and growing upward only ever found the daylight between the towers.
        int lowY = -1;
        for (int y = 1; y < TALL - 1 && lowY < 0; y++) {
            int run = -1;
            for (int x = 0; x < wide; x++) {
                if (!level.getBlockState(base.offset(x, y, 0)).isAir()) {
                    if (run > 0 && x - run >= 8) {
                        lowY = y;
                        break;
                    }
                    run = -1;
                } else if (run < 0) {
                    run = x;
                }
            }
        }
        if (lowY < 0) return 0;
        int highY = Math.min(TALL - 2, lowY + HALL_HEIGHT - 1);

        // Its width: every closed run in those rows, taken together, which is the room the three passages open into.
        int lowX = Integer.MAX_VALUE;
        int highX = Integer.MIN_VALUE;
        for (int y = lowY; y <= highY; y++) {
            int run = -1;
            for (int x = 0; x < wide; x++) {
                if (!level.getBlockState(base.offset(x, y, 0)).isAir()) {
                    if (run > 0 && x - run >= 8) {
                        lowX = Math.min(lowX, run);
                        highX = Math.max(highX, x - 1);
                    }
                    run = -1;
                } else if (run < 0) {
                    run = x;
                }
            }
        }
        if (lowX > highX) return 0;

        int floorTop = base.getY() + lowY - 1;
        int roofY = base.getY() + highY + 1;
        int added = 0;

        // The hall itself: its floor and its roof, from the plaza's north edge to the rock that stands over the roof.
        int first = base.getZ() - HALL_PLAZA;
        for (int x = lowX; x <= highX; x++) {
            int end = hallEnd(level, base, x, roofY);
            if (end >= first) continue;
            for (int z = first; z >= end; z--) {
                for (int y = base.getY(); y <= floorTop; y++) {
                    added += lay(level, base.getX() + x, y, z);
                }
                int top = roofY + HALL_ROOF - 1 + rough(x, z, 1);
                for (int y = roofY; y <= top; y++) {
                    added += lay(level, base.getX() + x, y, z);
                }
            }
        }

        // Its two walls, outside the room, following the room's own end at the nearest column, each course of the
        // thickness a course lower than the last so the outside reads as a bank of rock rather than a wall.
        for (int side = 0; side < 2; side++) {
            int ref = side == 0 ? lowX : highX;
            int end = hallEnd(level, base, ref, roofY);
            if (end >= first) continue;
            for (int w = 0; w < HALL_WALL; w++) {
                int x = side == 0 ? lowX - 1 - w : highX + 1 + w;
                for (int z = first; z >= end; z--) {
                    int top = roofY + HALL_ROOF - 1 + rough(x, z, 2) - w;
                    for (int y = base.getY(); y <= top; y++) {
                        added += lay(level, base.getX() + x, y, z);
                    }
                }
            }
        }
        return added;
    }

    /** How far north a column of the hall runs: until the rock stands over the roof, and three blocks further in. */
    private static int hallEnd(ServerLevel level, BlockPos base, int x, int roofY) {
        for (int z = base.getZ() - 1; z > base.getZ() - HALL_REACH; z--) {
            if (level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base.getX() + x, z) - 1 >= roofY) {
                return z - HALL_INTO_ROCK;
            }
        }
        return base.getZ() - HALL_REACH;
    }

    /** How many courses this column of the hall carries above its nominal top: the roughening that stops it reading flat. */
    private static int rough(int x, int z, int salt) {
        int h = x * 73856093 ^ z * 19349663 ^ salt * 83492791;
        h ^= h >>> 13;
        return Math.floorMod(h, 3);
    }

    /** Puts the gate's own rock into a cell, and only where that cell is air: the mountain is never cut into. */
    private static int lay(ServerLevel level, int x, int y, int z) {
        BlockPos at = new BlockPos(x, y, z);
        if (!level.hasChunkAt(at)) return 0;
        if (!level.getBlockState(at).isAir()) return 0;
        level.setBlock(at, rock(x, y, z), 2);
        return 1;
    }

    /**
     * The rock the gate is cut out of, in the proportions the capture holds it: stone for the bulk, cobblestone and
     * mossy cobblestone for the rest, so a hall laid in it does not read as one block repeated.
     */
    private static BlockState rock(int x, int y, int z) {
        int pick = Math.floorMod(x * 31 + z * 17 + y * 7, 10);
        if (pick < 5) return Blocks.STONE.defaultBlockState();
        if (pick < 8) return Blocks.COBBLESTONE.defaultBlockState();
        return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
    }

    /** How far out from the gate's own edge the seam is dressed, in blocks, and how fast that skirt falls away. */
    private static final int DRAPE_REACH = 6;
    private static final int DRAPE_FALL = 2;

    /**
     * Dresses the seam where the gate meets the ground it was set into.
     * <p>
     * The gate is rock - forty-eight blocks of the mountain it was cut out of - so the ground beside it has to
     * arrive at the gate's own edge height, or the build stands in a trench of its own making. The pass is drawn to
     * that height already, but the pass is measured off the map and the gate is a build: a block out here and a
     * course there leave the seam open. This walks the ring around the footprint, reads the top of the gate at the
     * nearest column of it, and fills the ground up to that with the stone it already has, capped with the surface
     * that was there, so the rock the build carries runs on into the rock the world made.
     * <p>
     * It only ever adds. The mountain over the gate is the mountain, and a drape that cut would lower the very
     * walls the pass was shaped for.
     *
     * @return how many blocks of ground were added
     */
    public static int drape(ServerLevel level, BlockPos front, Rotation rotation) {
        BlockPos base = base(front, rotation);
        boolean turned = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        int wide = turned ? DEEP : WIDE;
        int deep = turned ? WIDE : DEEP;
        open(level, base.offset(-DRAPE_REACH, 0, -DRAPE_REACH), wide + DRAPE_REACH * 2, deep + DRAPE_REACH * 2);

        // The top of the gate at every column of its own plan, read back off the world rather than guessed at.
        int[] tops = new int[wide * deep];
        for (int x = 0; x < wide; x++) {
            for (int z = 0; z < deep; z++) {
                int found = Integer.MIN_VALUE;
                for (int y = TALL - 1; y >= 0; y--) {
                    if (!level.getBlockState(base.offset(x, y, z)).isAir()) {
                        found = y;
                        break;
                    }
                }
                tops[x * deep + z] = found;
            }
        }

        int dressed = 0;
        for (int x = -DRAPE_REACH; x < wide + DRAPE_REACH; x++) {
            for (int z = -DRAPE_REACH; z < deep + DRAPE_REACH; z++) {
                // Never behind the gate. The back is where the chamber opens and the hall runs north out of it, and a skirt
                // of ground laid across that opening closes the one thing the build is there to lead into.
                if (z < 0) continue;
                int outX = x < 0 ? -x : (x >= wide ? x - wide + 1 : 0);
                int outZ = z < 0 ? -z : (z >= deep ? z - deep + 1 : 0);
                int out = Math.max(outX, outZ);
                if (out == 0) continue;
                int edge = tops[Math.min(wide - 1, Math.max(0, x)) * deep + Math.min(deep - 1, Math.max(0, z))];
                if (edge == Integer.MIN_VALUE) continue;
                int want = base.getY() + edge - DRAPE_FALL * (out - 1);
                BlockPos column = base.offset(x, 0, z);
                if (!level.hasChunkAt(column)) continue;
                int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ()) - 1;
                if (ground >= want) continue;
                BlockState cap = level.getBlockState(column.atY(ground));
                for (int y = ground + 1; y < want; y++) {
                    level.setBlock(column.atY(y), Blocks.STONE.defaultBlockState(), 2);
                    dressed++;
                }
                if (!cap.isAir() && !cap.canBeReplaced()) {
                    level.setBlock(column.atY(want), cap, 2);
                    dressed++;
                }
            }
        }
        return dressed;
    }

    /**
     * Takes the gate down again, so a spot tried and judged can be tried somewhere else: the whole box the parts
     * stand in is cleared to air.
     *
     * @return how many blocks were taken out
     */
    public static int clear(ServerLevel level, BlockPos front, Rotation rotation) {
        BlockPos base = base(front, rotation);
        boolean turned = rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90;
        int wide = turned ? DEEP : WIDE;
        int deep = turned ? WIDE : DEEP;
        int cleared = 0;
        for (int x = 0; x < wide; x++) {
            for (int z = 0; z < deep; z++) {
                BlockPos column = base.offset(x, 0, z);
                if (!level.hasChunkAt(column)) continue;
                for (int y = 0; y < TALL; y++) {
                    BlockPos at = column.above(y);
                    if (level.getBlockState(at).isAir()) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    /** The gate's own origin, from the point its facade's middle should stand at. */
    private static BlockPos base(BlockPos front, Rotation rotation) {
        int[] turned = turn(MIDDLE, 0, rotation);
        return front.offset(-turned[0], 0, -turned[1]);
    }

    /** Where a point of the gate's own plan lands once the gate is turned. */
    private static int[] turn(int x, int z, Rotation rotation) {
        return switch (rotation) {
            case NONE -> new int[]{x, z};
            case CLOCKWISE_90 -> new int[]{DEEP - 1 - z, x};
            case CLOCKWISE_180 -> new int[]{WIDE - 1 - x, DEEP - 1 - z};
            case COUNTERCLOCKWISE_90 -> new int[]{z, WIDE - 1 - x};
        };
    }

    private static int turnedX(Piece piece, Vec3i size, Rotation rotation) {
        int[] low = turn(piece.x(), piece.z(), rotation);
        int[] high = turn(piece.x() + size.getX() - 1, piece.z() + size.getZ() - 1, rotation);
        return Math.min(low[0], high[0]);
    }

    private static int turnedZ(Piece piece, Vec3i size, Rotation rotation) {
        int[] low = turn(piece.x(), piece.z(), rotation);
        int[] high = turn(piece.x() + size.getX() - 1, piece.z() + size.getZ() - 1, rotation);
        return Math.min(low[1], high[1]);
    }

    private static int turnedWidth(Vec3i size, Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                ? size.getZ() : size.getX();
    }

    private static int turnedDepth(Vec3i size, Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                ? size.getX() : size.getZ();
    }

    /** Every chunk a piece touches, opened before it is laid: a block written into a closed chunk is lost. */
    private static void open(ServerLevel level, BlockPos at, int wide, int deep) {
        for (int dx = 0; dx < wide; dx += 8) {
            for (int dz = 0; dz < deep; dz += 8) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
    }

    /** The pieces this pack does not carry, for a command that has to say what it could not find. */
    public static List<String> missing(ServerLevel level) {
        List<String> missing = new ArrayList<>();
        for (Piece piece : PIECES) {
            Identifier id = Identifier.parse(FOLDER + piece.name());
            if (level.getStructureManager().get(id).isEmpty()) missing.add(id.toString());
        }
        return missing;
    }
}
