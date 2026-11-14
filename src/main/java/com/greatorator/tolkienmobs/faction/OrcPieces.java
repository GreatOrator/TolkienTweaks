package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
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
 * A piece may also be **turned**. A tower is four walls and reads the same any way round, but a hut has one opening in
 * it, and the huts round the plaza are placed so those openings look at the middle of it. The turn is written beside the
 * mark rather than worked out at the laying, so what goes up is what was judged when the marks were read: the template's
 * opening is in its **south face** (a single column at x 10, two courses high, with its stairs in the base below and a
 * lintel over it), and the plaza's middle is west of every hut here, so each is turned a quarter clockwise, which turns
 * that opening from south to west.
 */
public final class OrcPieces {
    /** One piece: the template, the ammolite block that marks the middle of its footprint, and which way it is turned. */
    private record Piece(String structure, BlockPos mark, Rotation rotation) {
    }

    /** How far below its mark a piece's own base course sits: the mark is in the first course of air over the ground. */
    private static final int MARK_DROP = 1;

    /**
     * The pieces and their marks, read out of `run/saves/Mod Test` by scanning Arda for `tolkienmobs:block_ammolite`
     * (no template in the pack carries one, so every ammolite block in the world is a mark).
     */
    private static final List<Piece> PIECES = List.of(
            // The near rank of towers: marks in the gate's own footprint, on the courtyard floor at 188.
            new Piece(Sites.ORC_TOWER, new BlockPos(5539, 188, -6831), Rotation.NONE),
            new Piece(Sites.ORC_TOWER, new BlockPos(5557, 188, -6831), Rotation.NONE),
            new Piece(Sites.ORC_TOWER, new BlockPos(5598, 188, -6830), Rotation.NONE),
            new Piece(Sites.ORC_TOWER, new BlockPos(5628, 188, -6830), Rotation.NONE),
            // The far rank: two either side of the passage's mouth, on ground one course lower at 187.
            new Piece(Sites.ORC_TOWER, new BlockPos(5539, 187, -6882), Rotation.NONE),
            new Piece(Sites.ORC_TOWER, new BlockPos(5558, 187, -6882), Rotation.NONE),
            new Piece(Sites.ORC_TOWER, new BlockPos(5598, 187, -6882), Rotation.NONE),
            new Piece(Sites.ORC_TOWER, new BlockPos(5628, 187, -6882), Rotation.NONE),
            // The huts on the plaza, each turned a quarter clockwise so its opening looks west at the plaza's middle.
            new Piece(Sites.ORC_HUT, new BlockPos(5604, 187, -6899), Rotation.CLOCKWISE_90),
            new Piece(Sites.ORC_HUT, new BlockPos(5612, 187, -6892), Rotation.CLOCKWISE_90),
            new Piece(Sites.ORC_HUT, new BlockPos(5615, 187, -6905), Rotation.CLOCKWISE_90),
            new Piece(Sites.ORC_HUT, new BlockPos(5627, 187, -6903), Rotation.CLOCKWISE_90));

    private OrcPieces() {
    }

    /**
     * Raises every marked piece that is not already standing, and says how many went up.
     * <p>
     * A piece already there is left alone, so asking twice raises nothing twice: the check is {@link Sites#wholeAt},
     * which reads the template's own blocks at the corner it would be laid from, turned the way the piece is turned. It
     * is the strict reading on purpose - a piece cut in half by something else laid over its footprint is not standing,
     * and is laid whole again rather than left as the half it was left.
     * <p>
     * The chunks under a piece are opened before it is asked about: this runs as the server comes up, and a chunk that
     * is not loaded reads as air, which would both hide a piece that stands and lose one that is laid into it.
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
            if (template.getSize().getX() <= 0 || template.getSize().getZ() <= 0) continue;
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(piece.rotation());
            // A turned piece fills a different box, so the box is asked of the template in the turn it will be laid in.
            BoundingBox box = template.getBoundingBox(settings, BlockPos.ZERO);
            int across = box.maxX() - box.minX() + 1;
            int deep = box.maxZ() - box.minZ() + 1;
            BlockPos origin = piece.mark().offset(-(across - 1) / 2, -MARK_DROP, -(deep - 1) / 2);
            BlockPos anchor = origin.offset(-box.minX(), -box.minY(), -box.minZ());
            open(level, anchor, across, deep);
            if (Sites.wholeAt(level, piece.structure(), anchor, piece.rotation())) continue;
            template.placeInWorld(level, anchor, anchor, settings, level.getRandom(), 2);
            laid++;
        }
        return laid;
    }

    /** Every chunk a piece touches, opened before it is asked about or laid, the way the gate opens its own. */
    private static void open(ServerLevel level, BlockPos at, int wide, int deep) {
        for (int dx = 0; dx < wide; dx += 8) {
            for (int dz = 0; dz < deep; dz += 8) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
    }

    /** How many pieces are marked, for a command that wants to say so before it raises anything. */
    public static int marked() {
        return PIECES.size();
    }
}
