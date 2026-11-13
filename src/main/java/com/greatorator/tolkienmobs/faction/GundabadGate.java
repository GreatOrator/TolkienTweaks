package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
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
