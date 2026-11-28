package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The shoulder a build carries out into its country, declared rather than coded.
 * <p>
 * Three passes have been doing this job by hand: the city's and the village's ring ({@link Dressing}, reached over
 * {@code CityCommands.DRAPE_REACH}), a camp's sweep ({@link Sites#sweep}, a ring of twelve), and a crag's apron
 * ({@link Sites#apron}, a ramp out to a hundred and twenty-eight). The rule they all follow is one rule - cap the
 * ground just outside the walls at the build's own shape there, ease back to what the country already stands at by the
 * far edge of the ring, and never lay anything in the air a crown hangs over - and it was written out three times with
 * three different sets of numbers, which is how a camp ends up with an edge that reads as a wall while the town beside
 * it reads as ground.
 * <p>
 * This is that rule in one place, taking a {@link Declaration}: the build's box, its {@link Outline}, how far out it
 * dresses ({@code reach}), how high its floor stands over the box's own low corner ({@code deck}), whether its cut's
 * trees are not its shape ({@code trees}), whether the ring is allowed to fill as well as cut ({@code fill}), and the
 * version of the rules it was declared under. A build is added to the drape by writing one declaration, not by writing
 * a pass.
 * <p>
 * What it is <em>not</em> is a replacement for the town pass: the city and the village keep their own job (it steps a
 * piece and a chunk at a time, carries water out of a mouth, banks a spring and knows its deck), and are untouched by
 * this. A camp's sweep is now this pass with a camp's declaration - {@code reach} twelve, no deck, no fill - and comes
 * out exactly as it did, which is the point of doing it as one commit: nothing about a town or a camp changes, and the
 * next build to want a drape writes a declaration instead of a fourth copy.
 */
public final class Drape {
    private static final Logger LOGGER = LoggerFactory.getLogger(Drape.class);

    /** How far out a camp's ring dresses, in blocks: {@link Sites#sweep}'s own number, now its declaration's. */
    public static final int CAMP_REACH = 12;

    /**
     * How many columns of footprint a ring may dress before it is given the shallower edge, in columns.
     * <p>
     * A build the size of a keep has more wall than a ring of twelve can walk in a session, and its ground is a
     * mountain's rather than a camp's, so its ring is cut to {@link #NARROW} blocks and the {@link Sites#apron} is what
     * carries the height away. A camp is never near this.
     */
    private static final int WIDE = 2048;

    /** How far out a footprint past {@link #WIDE} dresses. */
    private static final int NARROW = 4;

    private Drape() {
    }

    /**
     * The box a build occupies, in the world: its low corner and the far corner of its footprint.
     * <p>
     * The height is the box's own low corner rather than the ground it was seated on, because {@code deck} is measured
     * against it - a build whose floor stands over the block it was planted on says so in its declaration, and one
     * whose floor is its low course says nothing.
     */
    public record Box(int lowX, int lowY, int lowZ, int highX, int highZ) {
        public static Box of(BlockPos corner, Vec3i size) {
            return new Box(corner.getX(), corner.getY(), corner.getZ(),
                    corner.getX() + size.getX() - 1, corner.getZ() + size.getZ() - 1);
        }

        /** How many columns of footprint it is across. */
        public int wide() {
            return highX - lowX + 1;
        }

        /** How many columns of footprint it is deep. */
        public int deep() {
            return highZ - lowZ + 1;
        }
    }

    /**
     * A build's own shape as its file draws it: the highest and lowest block in every column of its footprint, from its
     * own low corner, the blocks themselves, and where its water stands.
     * <p>
     * {@code roof} and {@code floor} answer {@link Integer#MIN_VALUE} where the build stands nothing at all in that
     * column, which a doorway and a courtyard both do: the pass fills those holes from the columns beside them before
     * the ring is walked ({@link #open}), so a wall with a gate in it is still a wall for the ground's sake.
     * {@code own} is the set of the build's own blocks, keyed against its low corner as {@link #offset} writes them, so
     * the ground inside the walls can be cut down to the floor without cutting what the build stands there.
     * {@code watered} is the height the build's own water stands at in a column, or {@code Integer.MIN_VALUE}.
     */
    public record Outline(int wide, int deep, int[] roof, int[] floor, Set<Long> own, int[] watered) {
    }

    /**
     * Where an outline is read from: a cut folder of pieces, or one structure template's file.
     * <p>
     * This is the one thing the passes could not share before. A cut is laid piece by piece from the build's own folder
     * and a template is one file, so each reading knew its own format and each pass was written against one of them. An
     * outline is the same shape either way - per column, top and bottom - so the reading is the only difference, and it
     * is the thing behind this interface.
     */
    public interface Source {
        Outline read(ServerLevel level) throws IOException;
    }

    /**
     * The template source: one structure file, read into an outline from its own corner.
     * <p>
     * Read from the file rather than from the world, and read before any placement at all: the ground is what the build
     * has to be carried to, so the numbers wanted are the ones the file draws, not what happens to be standing now. A
     * hill left over a roof inside the footprint would otherwise be its own answer.
     * <p>
     * {@code trees} says the build's own trees are not its shape - leaves and wood are skipped while the outline is
     * taken - so a ring meets the ground under a crown instead of being filled up to the leaves, one spike per tree. A
     * camp passes false, which is the reading its sweep has always had; a build with trees in its file and a drape it
     * wants to look like ground passes true.
     *
     * @return the outline, or null when the file cannot be read or holds nothing
     */
    public static Outline outline(ServerLevel level, String structure, Vec3i size, boolean trees) {
        Identifier id = Identifier.tryParse(structure);
        if (id == null) return null;
        Identifier file = Identifier.fromNamespaceAndPath(id.getNamespace(), "structure/" + id.getPath() + ".nbt");
        CompoundTag tag;
        try (InputStream stream = level.getServer().getResourceManager().getResourceOrThrow(file).open()) {
            tag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        } catch (IOException | RuntimeException unreadable) {
            LOGGER.warn("Could not read {} to sweep the ground over it", file, unreadable);
            return null;
        }
        ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
        if (palette.isEmpty()) return null;
        String[] names = new String[palette.size()];
        for (int index = 0; index < palette.size(); index++) {
            names[index] = palette.getCompoundOrEmpty(index).getStringOr("Name", "");
        }
        int wide = size.getX();
        int deep = size.getZ();
        int[] top = new int[wide * deep];
        int[] floor = new int[wide * deep];
        int[] watered = new int[wide * deep];
        Arrays.fill(top, Integer.MIN_VALUE);
        Arrays.fill(floor, Integer.MIN_VALUE);
        Arrays.fill(watered, Integer.MIN_VALUE);
        Set<Long> own = new HashSet<>();
        ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
        for (int index = 0; index < blocks.size(); index++) {
            CompoundTag block = blocks.getCompoundOrEmpty(index);
            int state = block.getIntOr("state", -1);
            if (state < 0 || state >= names.length) continue;
            String name = names[state];
            if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
            if (trees && tree(name)) continue;
            ListTag pos = block.getList("pos").orElseGet(ListTag::new);
            if (pos.size() < 3) continue;
            int x = pos.getIntOr(0, 0);
            int y = pos.getIntOr(1, 0);
            int z = pos.getIntOr(2, 0);
            if (x < 0 || x >= wide || z < 0 || z >= deep) continue;
            int at = x + z * wide;
            top[at] = Math.max(top[at], y);
            floor[at] = floor[at] == Integer.MIN_VALUE ? y : Math.min(floor[at], y);
            if (name.contains("water")) watered[at] = Math.max(watered[at], y);
            own.add(offset(x, y, z));
        }
        return new Outline(wide, deep, top, floor, own, watered);
    }

    /**
     * The cut source: the pieces of a build's own cut folder, read into an outline the way the city's ring reads them.
     * <p>
     * The pieces are named relative to the cut and stand at their own offsets from the corner the build was planted
     * on, so a column of the outline is the highest block any piece puts in that world column, and the lowest is the
     * floor. A piece that cannot be read is skipped rather than taken as a hole, and water is recorded where the cut
     * puts it, so a ring can leave a channel alone.
     * <p>
     * Nothing declares a cut today - the towns keep their own pass, which reads its pieces a tick at a time - so this
     * is the half of the source that is written and not yet used. It takes a {@code deck} of nought: the {@code own}
     * keys it writes are measured from the box's low corner.
     */
    public static Source cut(Dressing.Pieces pieces, List<CityCommands.Part> parts, BlockPos origin, Box box,
                             boolean trees) {
        return level -> {
            int wide = box.wide();
            int deep = box.deep();
            int[] top = new int[wide * deep];
            int[] floor = new int[wide * deep];
            int[] watered = new int[wide * deep];
            Arrays.fill(top, Integer.MIN_VALUE);
            Arrays.fill(floor, Integer.MIN_VALUE);
            Arrays.fill(watered, Integer.MIN_VALUE);
            Set<Long> own = new HashSet<>();
            for (CityCommands.Part part : parts) {
                CompoundTag tag;
                try {
                    tag = pieces.raw(part.name());
                } catch (IOException unreadable) {
                    LOGGER.warn("Could not read {} for an outline", part.name());
                    continue;
                }
                ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
                String[] names = new String[palette.size()];
                for (int index = 0; index < palette.size(); index++) {
                    names[index] = palette.getCompoundOrEmpty(index).getStringOr("Name", "");
                }
                ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
                for (int index = 0; index < blocks.size(); index++) {
                    CompoundTag block = blocks.getCompoundOrEmpty(index);
                    int state = block.getIntOr("state", 0);
                    if (state < 0 || state >= names.length) continue;
                    String name = names[state];
                    if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
                    if (trees && tree(name)) continue;
                    ListTag pos = block.getList("pos").orElseGet(ListTag::new);
                    if (pos.size() < 3) continue;
                    int x = origin.getX() + part.dx() + pos.getIntOr(0, 0) - box.lowX();
                    int z = origin.getZ() + part.dz() + pos.getIntOr(2, 0) - box.lowZ();
                    if (x < 0 || x >= wide || z < 0 || z >= deep) continue;
                    int at = x + z * wide;
                    int y = origin.getY() + part.dy() + pos.getIntOr(1, 0);
                    top[at] = Math.max(top[at], y);
                    floor[at] = floor[at] == Integer.MIN_VALUE ? y : Math.min(floor[at], y);
                    if (name.contains("water")) watered[at] = Math.max(watered[at], y);
                    own.add(offset(x, y - box.lowY(), z));
                }
            }
            return new Outline(wide, deep, top, floor, own, watered);
        };
    }

    /**
     * One build's drape, as its declaration reads.
     * <p>
     * {@code build} names the piece of work and {@code group} the family it belongs to - a hold's gate and the hold
     * itself are two declarations under one group - so a rule change can be asked of a family rather than of each part,
     * and so two builds that share a country share a version. {@code reach} is how far out the ring dresses, in blocks;
     * {@code deck} is how high the build's floor stands over its box's low corner, so the ring joins the floor and not
     * the ground the box was planted on; {@code trees} says the build's trees are not its shape; {@code fill} says the
     * ring may build the ground up as well as cut it down; and {@code version} is the rules this build was declared
     * under, which a build that keeps a note records so a changed rule reaches a world that already holds the build.
     * <p>
     * The layout is a record rather than a builder because a declaration is meant to be read at the call site: a camp
     * is one line, and a hold's two halves are two lines that name themselves.
     */
    public record Declaration(String build, String group, Box box, int reach, int deck, boolean trees, boolean fill,
                               int version, Outline outline) {
    }

    /** The version of the rules a declaration made by {@link #declare} was written under, for a build that keeps a note. */
    public static final int RULES = 1;

    /**
     * How far under the top of a column the ring will look for the ground it stands on, in blocks.
     * <p>
     * A crown is ten or twelve blocks deep at most, so forty is more than any tree needs and still short of the air
     * over a cave: a column whose ground is further under its top than this is a hole rather than a column the ring
     * can carry, and is left exactly as it stands.
     */
    private static final int COLUMN_DEPTH = 40;

    /**
     * How far out any build's ring dresses when nobody says otherwise, in blocks.
     * <p>
     * The camp's own number, which is the smallest ring that reads as ground rather than as a plinth: the wall is met
     * by its own height at the wall, and the country is reached a dozen blocks out. It is enough for a house, a hall
     * and a tower; a build whose rim is taller than a house's passes its own reach. A footprint wider than {@link #WIDE}
     * columns loses this number to {@link #NARROW} whichever way it is declared, so a castle still cannot spend itself
     * on its own shoulder.
     */
    public static final int DEFAULT_REACH = 12;

    /**
     * Any build's shoulder, declared from its own file.
     * <p>
     * The ordinary rules without a number written by hand: the outline is the template's own columns read with its
     * trees discounted, the ring is {@link #DEFAULT_REACH} out, the build stands on its own low corner (a deck of
     * nought), and the ring both takes ground down and builds it up. A house, a hall, a tower, a ruin and a camp all
     * take this declaration as it stands; the ones that do not - a floor standing over its corner, a ring that may
     * only cut - pass their own numbers, which is why {@link Declaration} is still the way a build says what it is.
     *
     * @return the declaration, or null when the file cannot be read or holds nothing
     */
    @Nullable
    public static Declaration declare(ServerLevel level, String structure, BlockPos corner, Vec3i size) {
        return declare(level, structure, corner, size, DEFAULT_REACH);
    }

    /** The same declaration with a ring of the given reach, for a build whose rim is taller than a house's. */
    @Nullable
    public static Declaration declare(ServerLevel level, String structure, BlockPos corner, Vec3i size, int reach) {
        if (size.getX() <= 0 || size.getZ() <= 0) return null;
        Outline outline = outline(level, structure, size, true);
        if (outline == null) return null;
        return new Declaration(structure, structure, Box.of(corner, size), reach, 0, true, true, RULES, outline);
    }

    /** Reads a build's file and dresses its ring in one call, on the rules {@link #declare} writes down. */
    public static Counts dress(ServerLevel level, String structure, BlockPos corner, Vec3i size) {
        Declaration drape = declare(level, structure, corner, size);
        return drape == null ? new Counts(0, 0, 0) : dress(level, drape);
    }

    /** What one pass came to, in columns: the ring's ground cut down, its dips filled, and the ground taken inside. */
    public record Counts(int cut, int filled, int inside) {
    }

    /**
     * Dresses one build's ring: every column outside its walls, carried to its own shape there and eased back to the
     * country by the far edge of the ring.
     * <p>
     * The columns inside the walls are the build's own ground and are cut to its floor, never to its roof: a ruin seated
     * on the highest column of a bank has a hill's worth of ground standing inside its own walls, and what it takes out
     * is what the build does not stand. A column outside is capped at the nearest wall column's own height and eased by
     * a smoothstep to what the country already stands at by the far edge, so the join at the wall is the build's shape
     * and the join in the country is the country's.
     * <p>
     * Water is left exactly as it was, and a column standing in water's own bed is not dressed at all: a spring is the
     * ground's, not the drape's. A column whose crown hangs over air is read down to the first ground - a big crown
     * always hangs over something - so nothing is ever laid in the air under a tree.
     * <p>
     * A column is measured on the ground it stands on, not on the top of the tree growing in it, which is what
     * {@link #standing} reads. The cap is eased from the build's own shape to that height, so a height taken from a
     * crown carried the ring up into the canopy: the fill rose through the leaves, and its top course - the block the
     * column's skin was, a leaf where a leaf was the skin - came out as a flat plate of leaves at the crown's own
     * level, with the ground beside it stepped into terraces shaped like the tree. That is the ground the owner
     * photographed on 28 November 2026 and asked to stop: the country was being shaped as if the trees were part of
     * the land. So a column is measured on its ground, and a column a tree stands over is left exactly as it stands:
     * a shoulder is laid on ground and never through a crown, which is the rule the village's own bank follows.
     */
    public static Counts dress(ServerLevel level, Declaration drape) {
        Box box = drape.box();
        Outline outline = drape.outline();
        int wide = outline.wide();
        int deep = outline.deep();
        if (wide <= 0 || deep <= 0) return new Counts(0, 0, 0);
        int[] top = outline.roof();
        int[] floor = outline.floor();
        open(top, wide, deep);
        open(floor, wide, deep);
        int ring = wide * deep > WIDE ? NARROW : drape.reach();
        int base = box.lowY() + drape.deck();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int cut = 0;
        int filled = 0;
        int inside = 0;
        for (int dx = -ring; dx < wide + ring; dx++) {
            for (int dz = -ring; dz < deep + ring; dz++) {
                int out = Math.max(0, Math.max(Math.max(-dx, dx - wide + 1), Math.max(-dz, dz - deep + 1)));
                if (out > ring) continue;
                int nearX = Math.max(0, Math.min(wide - 1, dx));
                int nearZ = Math.max(0, Math.min(deep - 1, dz));
                int roof = top[nearX + nearZ * wide];
                if (roof == Integer.MIN_VALUE) continue;
                int x = box.lowX() + dx;
                int z = box.lowZ() + dz;
                Footing foot = footing(level, x, z);
                if (foot.ground() == Integer.MIN_VALUE) continue;
                int surface = foot.ground();
                if (wet(level, x, surface + 1, z)) continue;
                if (out == 0) {
                    // Inside the walls the ground comes down to the build's own floor rather than to its roof, and
                    // nothing the build stands is what is cut: a keep's height is the cap, so a cut at the roofline
                    // leaves the ground filling the ruin to the top of it and the whole thing reads as a mound of dirt.
                    // The cut is read from the top of the column, crown and all, as it always was: a country tree
                    // inside the walls comes off with the ground it stood in, so nothing is left hanging over a cut.
                    int bottom = floor[nearX + nearZ * wide];
                    if (bottom == Integer.MIN_VALUE) continue;
                    inside += inside(level, cursor, base, nearX, nearZ, x, z, crown(level, x, z), base + bottom,
                            outline.own());
                    continue;
                }
                // A tree's column of the ring is the tree's: a bank laid through a crown comes up as grass on the
                // leaves, and one laid under it leaves a pit round the trunk. Anything growing on the ground that is
                // not a tree - a fern, a tuft - is passed over by the read itself and does not stop the join.
                if (foot.tree()) continue;
                double share = Math.min(1.0D, out / (double) ring);
                double ease = share * share * (3.0D - 2.0D * share);
                int cap = (int) Math.round(base + roof + (surface - base - roof) * ease);
                if (surface > cap) {
                    cut += column(level, cursor, x, z, surface, cap);
                } else if (surface < cap && drape.fill()) {
                    filled += lift(level, cursor, x, z, surface, cap);
                }
            }
        }
        return new Counts(cut, filled, inside);
    }

    /**
     * Dresses a build that is cut into rock rather than seated on ground: the ring around it is filled with the rock
     * the build was cut out of, so its walls meet stone instead of a trench of air.
     * <p>
     * The surface ring asks the heightmap where the country stands, and underground that answer belongs to the sky,
     * forty blocks over the roof. What a buried build wants is the other half of the same rule: nothing above its roof
     * and nothing below its floor, and between the two, the columns outside its walls filled with the stone they are
     * cut into. That is what a capture's air takes away - a template writes the air it carries, so a hall cut out of a
     * mountain clears a box of rock around itself and stands in it with straight sides - and this is what puts the rock
     * back. It is the specific case the one surface rule cannot answer, kept beside it rather than bending it: a
     * declaration's box, outline, reach and deck mean the same thing to both.
     * <p>
     * The stone is the country's own, read from the first solid block above the build's roof in that column, so a
     * chamber's shoulder is the mountain it was cut from and not a wall of one chosen stone. A column with sky or water
     * over its roof is left alone (that column is a surface column and belongs to {@link #dress}), the build's own
     * blocks are never replaced, and nothing between the walls is touched at all.
     * <p>
     * Water in the ring is left where it is, the same rule the surface ring follows.
     *
     * @return what came to, in columns: {@code filled} is every column the rock was carried into
     */
    public static Counts enclose(ServerLevel level, Declaration drape) {
        Box box = drape.box();
        Outline outline = drape.outline();
        int wide = outline.wide();
        int deep = outline.deep();
        if (wide <= 0 || deep <= 0) return new Counts(0, 0, 0);
        int[] top = outline.roof();
        int[] floor = outline.floor();
        open(top, wide, deep);
        open(floor, wide, deep);
        int ring = wide * deep > WIDE ? NARROW : drape.reach();
        int base = box.lowY() + drape.deck();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int filled = 0;
        for (int dx = -ring; dx < wide + ring; dx++) {
            for (int dz = -ring; dz < deep + ring; dz++) {
                int out = Math.max(0, Math.max(Math.max(-dx, dx - wide + 1), Math.max(-dz, dz - deep + 1)));
                if (out == 0 || out > ring) continue;
                int nearX = Math.max(0, Math.min(wide - 1, dx));
                int nearZ = Math.max(0, Math.min(deep - 1, dz));
                int roof = top[nearX + nearZ * wide];
                int bottom = floor[nearX + nearZ * wide];
                if (roof == Integer.MIN_VALUE || bottom == Integer.MIN_VALUE) continue;
                int x = box.lowX() + dx;
                int z = box.lowZ() + dz;
                cursor.set(x, base + roof + 1, z);
                BlockState rock = level.getBlockState(cursor);
                if (!rock.isSolid() || !rock.getFluidState().isEmpty()) continue;
                for (int y = base + bottom; y <= base + roof; y++) {
                    if (outline.own().contains(offset(dx, y - base, dz))) continue;
                    cursor.set(x, y, z);
                    BlockState state = level.getBlockState(cursor);
                    if (!state.getFluidState().isEmpty() || !state.canBeReplaced()) continue;
                    level.setBlock(cursor, rock, Block.UPDATE_CLIENTS);
                    filled++;
                }
            }
        }
        return new Counts(0, filled, 0);
    }

    /**
     * The ground a column stands on, read down through whatever grows on it, and whether a tree stands over it.
     * <p>
     * A height answers with the highest thing in the column, and a height without leaves still answers with a trunk.
     * Read from either, a tree in the ring was the country's own height there, and the ring was carried up to its wood
     * or its crown - the plate of leaves and the terraced ground beside it that the owner photographed on
     * 28 November 2026. The read walks down from the top of the column, through leaves, wood and the air a bough
     * reaches over, and stops at the first block that is solid or holds a fluid: the country's own ground, or the
     * water standing on it, which the ring leaves alone exactly as it always did. A tree's own wood is solid and is
     * walked through as well, which is why it is tested first. {@code tree} says the walk passed any, so the ring can
     * leave that column as the tree has it.
     */
    private record Footing(int ground, boolean tree) {
        static final Footing NONE = new Footing(Integer.MIN_VALUE, false);
    }

    /** The ground a column stands on and what it found there, or {@link Footing#NONE} where there is no column. */
    private static Footing footing(ServerLevel level, int x, int z) {
        int top = crown(level, x, z);
        if (top <= level.getMinY()) return Footing.NONE;
        int floor = Math.max(level.getMinY(), top - COLUMN_DEPTH);
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        BlockState state = level.getBlockState(at.set(x, top, z));
        boolean wood = false;
        while (top > floor && (tree(state) || (!state.isSolid() && state.getFluidState().isEmpty()))) {
            wood |= tree(state);
            state = level.getBlockState(at.set(x, --top, z));
        }
        return new Footing(top, wood);
    }

    /** The highest thing in a column, leaves and all, which is where a read down to the ground begins. */
    private static int crown(ServerLevel level, int x, int z) {
        return Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z),
                level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)) - 1;
    }

    /**
     * Whether a block is part of a tree rather than part of the ground: leaves, a trunk, a fungus stem.
     * <p>
     * The tags carry this mod's leaves - the hardened ones included - but its logs and wood are in no tag, so the
     * name is read as well. The same reading the city's pass uses, because a shoulder and a ring are one rule.
     */
    private static boolean tree(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)
                || tree(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
    }

    /**
     * One column of the ring: everything standing above the cap goes, and the column gets its ground back.
     * <p>
     * The skin is the block the column had, so a shoulder keeps the country's own stone, sand or grass; where that
     * block was a tree, a fluid or nothing solid, the column is capped with the ground beneath it, or with grass where
     * that ground was grass - a crown of leaves laid flat at a roofline is not a shoulder, it is a lid.
     *
     * @return how many blocks came off the column, which is nought when it already stood at or under the cap
     */
    static int column(ServerLevel level, BlockPos.MutableBlockPos cursor, int x, int z, int surface, int cap) {
        if (surface <= cap) return 0;
        BlockState skin = level.getBlockState(cursor.set(x, surface, z));
        boolean keep = skin.isSolid() && !skin.is(BlockTags.LOGS) && !skin.is(BlockTags.LEAVES)
                && skin.getFluidState().isEmpty();
        int taken = 0;
        for (int y = surface; y > cap; y--) {
            cursor.set(x, y, z);
            if (level.getBlockState(cursor).isAir()) continue;
            level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            taken++;
        }
        if (keep) {
            level.setBlock(cursor.set(x, cap, z), skin, Block.UPDATE_CLIENTS);
            return taken;
        }
        cursor.set(x, cap - 1, z);
        BlockState below = level.getBlockState(cursor);
        if (!below.isSolid()) return taken;
        boolean grassy = below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.DIRT) || below.is(Blocks.PODZOL)
                || below.is(Blocks.MYCELIUM) || below.is(Blocks.COARSE_DIRT);
        level.setBlock(cursor.set(x, cap, z),
                grassy ? Blocks.GRASS_BLOCK.defaultBlockState() : below, Block.UPDATE_CLIENTS);
        return taken;
    }

    /**
     * One column of a ring that fills: the ground is built up to the cap and capped with what stood on it.
     * <p>
     * Only a build that declares {@code fill} ever reaches this - a camp's ring is a shoulder cut into a hillside and
     * declares nothing of the kind - so it is here for the builds that want a shelf made level as well as a hill taken
     * down, the way the city's own ring does both.
     */
    private static int lift(ServerLevel level, BlockPos.MutableBlockPos cursor, int x, int z, int surface, int cap) {
        BlockState skin = level.getBlockState(cursor.set(x, surface, z));
        BlockState body = skin.isSolid() ? skin : Blocks.STONE.defaultBlockState();
        for (int y = surface + 1; y < cap; y++) {
            level.setBlock(cursor.set(x, y, z), Blocks.STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        level.setBlock(cursor.set(x, cap, z), body, Block.UPDATE_CLIENTS);
        return 1;
    }

    /**
     * One column of the sweep inside a build: the ground comes down to the build's own floor and no further, and the
     * build itself is never what is cut - the walls, the keep, the part of the craft the ground is standing in.
     * <p>
     * Water is left where it is, the same rule the ring follows: a chamber the ground has filled with a spring is not
     * a chamber to bail out on the way past.
     */
    private static int inside(ServerLevel level, BlockPos.MutableBlockPos cursor, int base, int relX, int relZ,
                              int x, int z, int surface, int floor, Set<Long> own) {
        int taken = 0;
        for (int y = surface; y > floor; y--) {
            if (own.contains(offset(relX, y - base, relZ))) continue;
            cursor.set(x, y, z);
            BlockState state = level.getBlockState(cursor);
            if (state.isAir() || !state.getFluidState().isEmpty()) continue;
            level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            taken++;
        }
        return taken;
    }

    /**
     * Fills the build's own holes from the columns beside them, so a doorway is still a wall for the ground's sake.
     * <p>
     * Two passes, because a hole two columns wide is filled from the ring the first pass makes around it: a gate is a
     * gap in a wall, and a ring capped at the gap is a ring with a pit in it at every gate in the build.
     */
    private static void open(int[] top, int wide, int deep) {
        for (int pass = 0; pass < 2; pass++) {
            int[] next = top.clone();
            for (int z = 0; z < deep; z++) {
                for (int x = 0; x < wide; x++) {
                    int at = x + z * wide;
                    if (top[at] != Integer.MIN_VALUE) continue;
                    int best = Integer.MIN_VALUE;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            int nx = x + dx;
                            int nz = z + dz;
                            if (nx < 0 || nx >= wide || nz < 0 || nz >= deep) continue;
                            best = Math.max(best, top[nx + nz * wide]);
                        }
                    }
                    next[at] = best;
                }
            }
            System.arraycopy(next, 0, top, 0, top.length);
        }
    }

    /** Whether the block under a column's skin is water, which is a column the drape leaves alone. */
    private static boolean wet(ServerLevel level, int x, int ground, int z) {
        return !level.getBlockState(new BlockPos(x, ground - 1, z)).getFluidState().isEmpty();
    }

    /** The key of one of a build's own blocks, for the test of what the inside cut must not take. */
    private static long offset(int x, int y, int z) {
        return ((long) (x + 512) << 42) | ((long) (z + 512) << 21) | (long) (y + 4096);
    }

    /**
     * Whether a block's name is a tree rather than part of the ground: leaves, a trunk, a fungus stem.
     * <p>
     * The same reading the city's pass uses, copied because the two are separate passes until the city moves onto a
     * declaration: this mod's leaves are in {@code #minecraft:leaves}, but its logs and wood are in no tag, so the tags
     * alone would leave a trunk read as shape.
     */
    private static boolean tree(String name) {
        return name.endsWith("_leaves") || name.contains("leaves_") || name.endsWith("_log")
                || name.contains("log_") || name.contains("_wood") || name.contains("wood_")
                || name.contains("_stem") || name.contains("_hyphae");
    }
}
