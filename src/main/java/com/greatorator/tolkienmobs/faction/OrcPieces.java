package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * can judge: the piece then runs out from it by half its own width and depth. Three of the marks are not middles but
 * edges - a ram's front edge, marked by the pair of blocks a ten-wide front is centred between, and a farm's back - so
 * a piece also carries a **push**: how far its footprint is moved off the mark once it has been centred on it. A piece also carries a **drop** - how far
 * below its mark its own base course sits - because the marks were not all made the same way. A tower's mark was placed
 * in the air over the courtyard floor, so its base sits one course below it, on the floor; a hut's mark was placed in
 * the plaza's own surface course, so its base sits on the mark, flush with the ground the builder stood on.
 * <p>
 * A piece may also be **turned**. A tower is four walls and reads the same any way round, but a hut has one opening in
 * it: a single column two courses high in the template's **south face**, with its stairs cut into the base below and a
 * lintel over it. The turn is written beside the mark rather than worked out at the laying, so what goes up is what was
 * judged when the marks were read - the three huts east of the hall are laid **unturned**, so their openings look south
 * at the gate's own front, and the one west of the hall is turned a quarter anticlockwise, which turns that opening
 * from south to east, at the plaza's middle.
 */
public final class OrcPieces {
    /**
     * One piece: the template, the mark it was read from, which way it is turned, how far its base sits below, and how
     * far its footprint is pushed off the mark. Most marks are the middle of the footprint and the push is nothing; a
     * mark taken along an edge of a build - a ram's front edge, a farm's back - needs the push to say where the rest of
     * the build went, and it is written here as it was judged rather than worked out at the laying.
     */
    private record Piece(String structure, BlockPos mark, Rotation rotation, int drop, int shiftX, int shiftZ) {
        /** A piece whose mark is the middle of its footprint, which is how all but three of these marks were made. */
        Piece(String structure, BlockPos mark, Rotation rotation, int drop) {
            this(structure, mark, rotation, drop, 0, 0);
        }
    }

    /** An arrangement laid by an earlier table and still standing on the ground. */
    private record Former(String structure, BlockPos mark, Rotation rotation, int drop, int shiftX, int shiftZ) {
        Former(String structure, BlockPos mark, Rotation rotation, int drop) {
            this(structure, mark, rotation, drop, 0, 0);
        }
    }

    /**
     * The pieces and their marks, read out of `run/saves/Mod Test` by scanning Arda for `tolkienmobs:block_ammolite`
     * (no template in the pack carries one, so every ammolite block in the world is a mark).
     */
    private static final List<Piece> PIECES = List.of(
            // The near rank of towers: marks in the gate's own footprint, in the air over the courtyard floor at 188.
            new Piece(Sites.ORC_TOWER, new BlockPos(5539, 188, -6831), Rotation.NONE, 1),
            new Piece(Sites.ORC_TOWER, new BlockPos(5557, 188, -6831), Rotation.NONE, 1),
            new Piece(Sites.ORC_TOWER, new BlockPos(5598, 188, -6830), Rotation.NONE, 1),
            new Piece(Sites.ORC_TOWER, new BlockPos(5628, 188, -6830), Rotation.NONE, 1),
            // The far rank: two either side of the passage's mouth, on ground one course lower at 187.
            new Piece(Sites.ORC_TOWER, new BlockPos(5539, 187, -6882), Rotation.NONE, 1),
            new Piece(Sites.ORC_TOWER, new BlockPos(5558, 187, -6882), Rotation.NONE, 1),
            new Piece(Sites.ORC_TOWER, new BlockPos(5598, 187, -6882), Rotation.NONE, 1),
            new Piece(Sites.ORC_TOWER, new BlockPos(5628, 187, -6882), Rotation.NONE, 1),
            // The huts on the plaza: four bodies 13 wide in the strip the mountain leaves open either side of the hall,
            // one west of it and three east, 14 apart so each stands as its own building. Their marks are in the plaza's
            // surface course, so they sit flush with it (drop 0). The three east of the hall look south, at the gate's
            // front; the one west of it looks east, at the plaza's middle.
            new Piece(Sites.ORC_HUT, new BlockPos(5548, 187, -6899), Rotation.COUNTERCLOCKWISE_90, 1),
            new Piece(Sites.ORC_HUT, new BlockPos(5599, 187, -6899), Rotation.NONE, 0),
            new Piece(Sites.ORC_HUT, new BlockPos(5613, 187, -6899), Rotation.NONE, 0),
            new Piece(Sites.ORC_HUT, new BlockPos(5627, 187, -6899), Rotation.NONE, 0),
            // The lava pit. Its one mark is a single block in the courtyard floor, buried at 186 under the stone lip at
            // 189, and it is the middle of the pit's 22 by 22 footprint, so it needs no push. A pit is a hole: the rim
            // sits level with the low ground round it, which puts the base course at 172 and the lava at the bottom of
            // that, seventeen courses under the lip - and the piece's own 6140 cells of air carve the whole 22 by 15
            // box out of the ground, the bank to the south of it as well.
            new Piece(Sites.ORC_LAVA_PIT, new BlockPos(5578, 186, -6813), Rotation.NONE, 14),
            // The two battle rams at the gate. Each pair of marks is the middle of a ram's front edge, so the ram runs
            // back from it: 18 deep, pushed 9 south of the mark so that edge sits at -6829, a course clear of the gate
            // wall at -6830 - and the head, the cobblestone tip at the template's high-z end, turned round to face the
            // gate. Their bases sit on the courtyard floor at 188, a course under the marks.
            new Piece(Sites.ORC_RAM, new BlockPos(5610, 189, -6830), Rotation.CLOCKWISE_180, 1, 0, 9),
            new Piece(Sites.ORC_RAM, new BlockPos(5617, 189, -6830), Rotation.CLOCKWISE_180, 1, 0, 9),
            // The farm. Its mark is the middle of its back edge - the back of the house, whose red wool roof is the
            // template's low-z half - rather than the middle of its footprint, so it is pushed its own half-depth, 14,
            // north: 28 deep from -6860 up to -6887, its gated cobblestone wall, the template's high-z end, looking at
            // the huts five courses away.
            new Piece(Sites.ORC_FARM, new BlockPos(5614, 189, -6860), Rotation.CLOCKWISE_180, 1, 0, -14));

    /**
     * Arrangements these pieces were laid in before, and which still stand: the three huts east of the hall went up a
     * course low and facing west, before the builder marked their doorways and judged them. A piece that is not whole
     * is taken down from these first, so the new arrangement replaces the old one instead of being laid over it and
     * leaving the old walls as debris beside the new.
     */
    private static final List<Former> FORMER = List.of(
            new Former(Sites.ORC_HUT, new BlockPos(5599, 187, -6899), Rotation.CLOCKWISE_90, 1),
            new Former(Sites.ORC_HUT, new BlockPos(5613, 187, -6899), Rotation.CLOCKWISE_90, 1),
            new Former(Sites.ORC_HUT, new BlockPos(5627, 187, -6899), Rotation.CLOCKWISE_90, 1));

    /** The template's own blocks, as offsets and block names, read once per structure and kept. */
    private record Cell(int x, int y, int z, String name) {
    }

    private static final Map<String, List<Cell>> CELLS = new HashMap<>();

    private OrcPieces() {
    }

    /**
     * Raises every marked piece that is not already standing, and says how many went up.
     * <p>
     * A piece already there is left alone, so asking twice raises nothing twice: the check is {@link Sites#wholeAt},
     * which reads the template's own blocks at the corner it would be laid from, turned the way the piece is turned. It
     * is the strict reading on purpose - a piece cut in half by something else laid over its footprint is not standing,
     * and is laid whole again rather than left as the half it was left. A piece that has moved - turned, lifted, or put
     * on a mark it was not on before - fails that check too, and its earlier arrangement is taken off the ground first.
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
            StructureTemplate template = template(level, piece.structure());
            if (template == null) continue;
            BlockPos anchor = anchor(template, piece.mark(), piece.rotation(), piece.drop(),
                    piece.shiftX(), piece.shiftZ());
            open(level, anchor, template, piece.rotation());
            if (Sites.wholeAt(level, piece.structure(), anchor, piece.rotation())) continue;
            takeDown(level, piece);
            template.placeInWorld(level, anchor, anchor,
                    new StructurePlaceSettings().setRotation(piece.rotation()), level.getRandom(), 2);
            laid++;
        }
        return laid;
    }

    /**
     * Takes a piece back off the ground wherever it was laid before, so it can go up somewhere else: every cell that
     * still holds one of the piece's own blocks becomes air, and anything the world has built over or changed is left
     * alone. Both the arrangements in {@link #FORMER} and the current one are cleared, which is what lets a piece that
     * has only been partly knocked about be laid whole again.
     */
    private static int takeDown(ServerLevel level, Piece piece) {
        int floor = piece.mark().getY() - piece.drop();
        int removed = 0;
        for (Former former : FORMER) {
            if (!former.mark().equals(piece.mark())) continue;
            removed += clear(level, former.structure(), former.mark(), former.rotation(), former.drop(),
                    former.shiftX(), former.shiftZ(), floor);
        }
        removed += clear(level, piece.structure(), piece.mark(), piece.rotation(), piece.drop(),
                piece.shiftX(), piece.shiftZ(), floor);
        return removed;
    }

    /**
     * Removes one arrangement of a piece: its own blocks only, where the world still holds them unchanged, and only from
     * {@code floor} up. The floor is the base of the arrangement that is going up in its place, so the old piece's own
     * base course is left in the ground - a hut lifted a course would otherwise have its old pad taken out from under it
     * and stand over a hole.
     */
    private static int clear(ServerLevel level, String structure, BlockPos mark, Rotation rotation, int drop,
                             int shiftX, int shiftZ, int floor) {
        StructureTemplate template = template(level, structure);
        Identifier id = Identifier.tryParse(structure);
        if (template == null || id == null) return 0;
        BlockPos anchor = anchor(template, mark, rotation, drop, shiftX, shiftZ);
        open(level, anchor, template, rotation);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int removed = 0;
        for (Cell cell : cellsOf(level, id)) {
            int[] at = Sites.turnedSample(rotation, cell.x(), cell.z());
            int y = anchor.getY() + cell.y();
            if (y < floor) continue;
            cursor.set(anchor.getX() + at[0], y, anchor.getZ() + at[1]);
            if (!nameOf(level.getBlockState(cursor)).equals(cell.name())) continue;
            level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 2);
            removed++;
        }
        return removed;
    }

    private static String nameOf(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    /** The template's own blocks, as offsets and block names, read once per structure out of the file it is loaded from. */
    private static List<Cell> cellsOf(ServerLevel level, Identifier id) {
        String key = id.toString();
        List<Cell> known = CELLS.get(key);
        if (known != null) return known;
        List<Cell> cells = readCells(level, id);
        CELLS.put(key, cells);
        return cells;
    }

    private static List<Cell> readCells(ServerLevel level, Identifier id) {
        Identifier file = Identifier.fromNamespaceAndPath(id.getNamespace(), "structure/" + id.getPath() + ".nbt");
        CompoundTag tag;
        try (InputStream stream = level.getServer().getResourceManager().getResourceOrThrow(file).open()) {
            tag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        } catch (IOException | RuntimeException unreadable) {
            return List.of();
        }
        ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
        ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
        if (palette.isEmpty() || blocks.isEmpty()) return List.of();
        String[] names = new String[palette.size()];
        for (int index = 0; index < palette.size(); index++) {
            names[index] = palette.getCompoundOrEmpty(index).getStringOr("Name", "");
        }
        List<Cell> cells = new ArrayList<>();
        for (int index = 0; index < blocks.size(); index++) {
            CompoundTag block = blocks.getCompoundOrEmpty(index);
            int state = block.getIntOr("state", -1);
            if (state < 0 || state >= names.length) continue;
            String name = names[state];
            if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
            ListTag pos = block.getList("pos").orElseGet(ListTag::new);
            if (pos.size() < 3) continue;
            cells.add(new Cell(pos.getIntOr(0, 0), pos.getIntOr(1, 0), pos.getIntOr(2, 0), name));
        }
        return cells;
    }

    /**
     * The corner a piece is laid from: half its own footprint out from the mark in the turn it will be laid in, its base
     * course the piece's own drop below the mark, and the piece's own push further out where the mark was taken along an
     * edge of the build rather than its middle.
     */
    private static BlockPos anchor(StructureTemplate template, BlockPos mark, Rotation rotation, int drop,
                                   int shiftX, int shiftZ) {
        BoundingBox box = template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), BlockPos.ZERO);
        int across = box.maxX() - box.minX() + 1;
        int deep = box.maxZ() - box.minZ() + 1;
        BlockPos origin = mark.offset(-(across - 1) / 2 + shiftX, -drop, -(deep - 1) / 2 + shiftZ);
        return origin.offset(-box.minX(), -box.minY(), -box.minZ());
    }

    private static StructureTemplate template(ServerLevel level, String structure) {
        Identifier id = Identifier.tryParse(structure);
        if (id == null) return null;
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) return null;
        if (template.getSize().getX() <= 0 || template.getSize().getZ() <= 0) return null;
        return template;
    }

    /** Every chunk a piece touches, opened before it is asked about or laid, the way the gate opens its own. */
    private static void open(ServerLevel level, BlockPos at, StructureTemplate template, Rotation rotation) {
        BoundingBox box = template.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), BlockPos.ZERO);
        int wide = box.maxX() - box.minX() + 1;
        int deep = box.maxZ() - box.minZ() + 1;
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
