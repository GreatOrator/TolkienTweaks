package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

/**
 * The orc pieces standing where somebody walked out the spot and marked it.
 * <p>
 * Every other building the pack raises is asked where it belongs - a country's middle, a page's survey, a pin under a
 * template's name - and that is the right answer for a world nobody has looked at. These are the other case: a builder
 * has stood on the ground, decided the piece belongs on that block, and left a block of ammolite in it. The ammolite is
 * the record; this table is that record written down, so the piece can be raised again in this world or any other
 * without anybody measuring it twice.
 * <p>
 * A mark is the **middle of the footprint** rather than a corner, because that is what a person standing in a doorway
 * can judge: the piece then runs out from it by half its own width and depth, and its base course sits one block below
 * the mark - the mark stands in the first course of air, so the base takes the ground under it, which is the same rule
 * the orc pins already follow.
 * <p>
 * The towers come in two ranks of four. The near rank is the marks inside the gate's own footprint (x 5496 to 5655,
 * z -6877 to -6830), on the courtyard floor at 188. The far rank stands five blocks north of the gate's back face, at
 * z -6882, on ground one course lower: two towers west of the passage's mouth and two east of it, closing the same
 * building from behind.
 */
public final class OrcPieces {
    /** One piece: the template, and the ammolite block that marks the middle of its footprint. */
    private record Piece(String structure, BlockPos mark) {
    }

    /** How far below its mark a piece's own base course sits: the mark is in the first course of air over the ground. */
    private static final int MARK_DROP = 1;

    /**
     * The pieces and their marks, read out of `run/saves/Mod Test` by scanning Arda for `tolkienmobs:block_ammolite`
     * (no template in the pack carries one, so every ammolite block in the world is a mark).
     */
    private static final List<Piece> PIECES = List.of(
            // the near rank: marks in the gate's own footprint, on the courtyard floor at 188
            new Piece(Sites.ORC_TOWER, new BlockPos(5539, 188, -6831)),
            new Piece(Sites.ORC_TOWER, new BlockPos(5557, 188, -6831)),
            new Piece(Sites.ORC_TOWER, new BlockPos(5598, 188, -6830)),
            new Piece(Sites.ORC_TOWER, new BlockPos(5628, 188, -6830)),
            // the far rank: two either side of the passage's mouth, on ground one course lower at 187
            new Piece(Sites.ORC_TOWER, new BlockPos(5539, 187, -6882)),
            new Piece(Sites.ORC_TOWER, new BlockPos(5558, 187, -6882)),
            new Piece(Sites.ORC_TOWER, new BlockPos(5598, 187, -6882)),
            new Piece(Sites.ORC_TOWER, new BlockPos(5628, 187, -6882)));

    private OrcPieces() {
    }

    /**
     * Raises every marked piece that is not already standing, and says how many went up.
     * <p>
     * A piece already there is left alone, so asking twice raises nothing twice: the check is {@link Sites#wholeAt},
     * which reads the template's own blocks at the corner it would be laid from. It is the strict reading on purpose -
     * a piece cut in half by something else laid over its footprint is not standing, and is laid whole again rather
     * than left as the half it was left.
     *
     * @return how many pieces were laid, which is 0 once they are all standing
     */
    public static int raise(ServerLevel level) {
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return 0;
        int laid = 0;
        for (Piece piece : PIECES) {
            Identifier id = Identifier.tryParse(piece.structure());
            if (id == null) continue;
            StructureTemplate template = level.getStructureManager().get(id).orElse(null);
            if (template == null) continue;
            Vec3i size = template.getSize();
            if (size.getX() <= 0 || size.getZ() <= 0) continue;
            BlockPos corner = corner(piece, size);
            if (!level.hasChunkAt(corner) || !level.hasChunkAt(corner.offset(size.getX() - 1, 0, size.getZ() - 1))) {
                continue;
            }
            if (Sites.wholeAt(level, piece.structure(), corner)) continue;
            template.placeInWorld(level, corner, corner, new StructurePlaceSettings(), level.getRandom(), 2);
            laid++;
        }
        return laid;
    }

    /** The corner a marked piece is laid from: half its own footprint out from the mark, and its base below it. */
    private static BlockPos corner(Piece piece, Vec3i size) {
        return piece.mark().offset(-(size.getX() - 1) / 2, -MARK_DROP, -(size.getZ() - 1) / 2);
    }

    /** How many pieces are marked, for a command that wants to say so before it raises anything. */
    public static int marked() {
        return PIECES.size();
    }
}
