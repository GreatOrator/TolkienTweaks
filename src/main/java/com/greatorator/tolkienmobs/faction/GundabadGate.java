package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
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
 * {@link #cut} is the other answer to the same question, kept for the comparison: the same gate as ground the
 * world cuts rather than a build it places - a passage arched out of the hillside, its mouth dressed from the
 * stone that was there. It cannot carry the hand-built detail, but it costs no templates and it always fits the
 * ground it is cut into, so which of the two is wanted is a decision to make with both standing.
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

    /** The mouth a cut makes: twenty-four across, twelve courses of jamb and eight of arch. */
    private static final int CUT_HALF = 12;
    private static final int CUT_JAMB = 12;
    private static final int CUT_RISE = 8;
    private static final int CUT_APRON = 6;

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

    /**
     * The gate as a cut rather than a build: a passage arched out of the hillside behind {@code door}, its mouth
     * dressed from the stone that was there.
     * <p>
     * The shape is the gate's own, reduced to what a cut can carry: twenty-four across, a straight jamb of twelve
     * courses and an arch rising eight over it, running forty-eight blocks in - the gate's own depth, so the two
     * answers are the same length. The mouth's ring is dressed in cobblestone and its wet twin, the two cobbles
     * the build is made of, and the crown is closed with a keystone of obsidian, so the portal reads as worked
     * stone without a leaf of the template's detail. The floor is cobblestone from six blocks out in front of
     * the door to the end of the passage, so the way in is walked rather than stepped up.
     *
     * @return what was cut, or null when there is no horizontal way in to cut along
     */
    @Nullable
    public static Cut cut(ServerLevel level, BlockPos door, Direction into) {
        if (into.getAxis().isVertical()) return null;
        int floor = door.getY();
        int carved = 0;
        int dressed = 0;
        for (int a = 0; a < DEEP; a++) {
            for (int s = -CUT_HALF; s < CUT_HALF; s++) {
                int head = head(s);
                for (int y = floor; y <= floor + head; y++) {
                    BlockPos at = along(door, into, a, s, y);
                    if (!level.hasChunkAt(at) || level.getBlockState(at).isAir()) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    carved++;
                }
            }
        }
        // Its floor and the apron in front of the door, laid once across the whole mouth rather than per course.
        for (int a = -CUT_APRON; a < DEEP; a++) {
            for (int s = -CUT_HALF; s < CUT_HALF; s++) {
                BlockPos bed = along(door, into, a, s, floor - 1);
                if (!level.hasChunkAt(bed) || !level.getBlockState(bed).isAir()) continue;
                level.setBlock(bed, Blocks.COBBLESTONE.defaultBlockState(), 2);
                carved++;
            }
        }
        // The mouth's ring: the stone the cut left standing on the arch's own outline, for the first two courses
        // in, dressed in the gate's cobbles. The outline is read in the passage's own coordinates, so nothing has
        // to be turned for a gate that faces east or west.
        for (int a = 0; a < 2; a++) {
            for (int s = -CUT_HALF - 2; s < CUT_HALF + 2; s++) {
                for (int up = -1; up <= CUT_JAMB + CUT_RISE + 2; up++) {
                    if (inside(s, up)) continue;
                    if (!touchesMouth(s, up)) continue;
                    BlockPos at = along(door, into, a, s, floor + up);
                    if (!level.hasChunkAt(at) || level.getBlockState(at).isAir()) continue;
                    level.setBlock(at, ring(at), 2);
                    dressed++;
                }
            }
        }
        BlockPos crown = along(door, into, 0, 0, floor + CUT_JAMB + CUT_RISE);
        if (level.hasChunkAt(crown)) {
            level.setBlock(crown, Blocks.OBSIDIAN.defaultBlockState(), 2);
            dressed++;
        }
        return new Cut(door, into, carved, dressed, DEEP, CUT_HALF * 2, CUT_JAMB + CUT_RISE + 1);
    }

    /** What a cut took out and dressed, for the command to report. */
    public record Cut(BlockPos door, Direction into, int carved, int dressed, int depth, int wide, int high) {
    }

    /** The top of the passage at an offset across it: the jamb, plus whatever the arch has risen there. */
    private static int head(int s) {
        double t = (double) s / (double) CUT_HALF;
        return CUT_JAMB + (int) Math.round(CUT_RISE * Math.sqrt(Math.max(0.0D, 1.0D - t * t)));
    }

    /** Whether a mouth-relative cell is inside the arch, before the hill is consulted. */
    private static boolean inside(int s, int up) {
        return s >= -CUT_HALF && s < CUT_HALF && up >= 0 && up <= head(s);
    }

    /** Whether a cell sits on the arch's own outline: one step outside the opening, beside stone it keeps. */
    private static boolean touchesMouth(int s, int up) {
        return inside(s - 1, up) || inside(s + 1, up) || inside(s, up - 1) || inside(s, up + 1);
    }

    /** The gate's two cobbles, alternating off the cell's own hash so the ring reads as weathered stone. */
    private static BlockState ring(BlockPos at) {
        return (at.asLong() >>> 61 & 1L) == 0L ? Blocks.COBBLESTONE.defaultBlockState()
                : Blocks.MOSSY_COBBLESTONE.defaultBlockState();
    }

    /** A cell of the passage: from the door, along the way in, across it, and above its floor. */
    private static BlockPos along(BlockPos door, Direction into, int a, int s, int y) {
        return door.relative(into, a).relative(into.getClockWise(), s).atY(y);
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
