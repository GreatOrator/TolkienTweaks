package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The ground and the road the city needs, done by the world rather than by an operator.
 * <p>
 * A city that has been stood - generated, or laid band by band - is still a city with a mountain against its walls
 * and no way down to the river. Those two passes exist as commands ({@code /tmcity drape}, {@code /tmroad build})
 * and are fine for an operator looking at the build, but a packed world has to arrive at them by itself: nobody is
 * going to type a command in a survival game, and the road is the difference between a valley and a valley with a
 * bridge.
 * <p>
 * Neither pass can be run in one tick. The drape walks more than a hundred thousand columns of ring and the road
 * grades and lays a corridor across the Bruinen; doing either whole is the stall that the city's own laying was cut
 * into bands to avoid. So this is a job: it reads the cut's outline a few pieces at a time, dresses the ring a couple
 * of chunks at a time, and only then calls the road, which is bounded enough to take in one piece. Two chunks of ring
 * per tick is some thirty seconds of background work in all, and no tick in it is longer than the game already pays
 * for a chunk being made.
 * <p>
 * It begins when the city is whole and not before, and only while somebody is near enough to see it: a city nobody
 * is looking at is not dressed, both because the ground is not worth changing unseen and because a ring of a
 * thousand chunks loaded for no player is the idle cost this is meant to keep off a server. A corner is written down when the first band is planted, and
 * dressing the ground round a city that is one band high would cut the mountain down to a roofline that is not there
 * yet - so the city has to say it is finished, which a generated city does the moment its start exists and a planted
 * one does when its last band is down.
 * <p>
 * What it has done is kept in {@code _dressed.txt} beside the cut's folder, by dimension and corner, so a world is not
 * dressed again on every start. A corner that moves - a city re-cut from a new corner - is dressed again as its own.
 */
@EventBusSubscriber(modid = MODID)
public final class Dressing {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dressing.class);

    /** How often the world is asked whether it has a city that wants dressing, in ticks. */
    private static final int CHECK_EVERY = 20;
    /**
     * How close a player has to be to the city for the world to work on it, in blocks from its footprint.
     * <p>
     * Far enough to cover the ring and the bridge a player standing in the valley can see, near enough that no
     * pass ever runs over chunks the game is not already keeping for somebody. It is the difference between a
     * world that dresses its valley and a server that quietly generates a hundred thousand columns for nobody.
     */
    private static final int WORK_REACH = 320;
    /** How many times a road that cannot be built is tried before it is left for the next visit to the valley. */
    private static final int ROAD_ATTEMPTS = 3;
    /** How many pieces of the cut one tick reads while the outline is being taken. */
    private static final int PIECES_PER_TICK = 4;
    /** How many chunks of ring one tick dresses. */
    private static final int CHUNKS_PER_TICK = 2;
    /**
     * How far the country has to stand over a build's own floor before the drape cuts it away, in blocks.
     * <p>
     * The drape never cut outside a deck at all, because cutting the ground round a face the build leaves open is a
     * trench where the country was. Over a village that rule reads the other way round: the Shire's village is at the
     * foot of its own country, so the ground along its south edge stood eleven blocks over its paving with a raw cliff
     * between them - not a trench but a wall the town never asked for. Three is the line between the two: a groove of a
     * block or two round a city's open faces is left alone, and a bank the height of a storey is dressed.
     */
    private static final int BANK_BLOCKS = 3;
    /**
     * How far under the top of a column the drape will look for the ground it stands on.
     * <p>
     * A crown is ten or twelve blocks deep at most, so forty is more than any tree needs and still short of the air
     * over a cave: a column whose ground is further under its top than this has a hole in it rather than a join, and
     * is left exactly as it stands.
     */
    private static final int COLUMN_DEPTH = 40;
    /** What has been dressed in this world, beside the cut's folder: dimension, corner, drape, road. */
    private static final String MARKER = "_dressed.txt";

    /** The job in flight, or null when there is nothing to do or it is done. */
    private static Job job;
    /**
     * A corner whose drape and road are both done, so the idle path stops asking.
     * <p>
     * Without this the tick would keep reading the cut's table and the marker every second for the rest of the
     * session to be told what it already knows. A corner that moves - a city re-cut from a new one - is not this
     * key, so it is dressed as its own.
     */
    private static String finished;

    private Dressing() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        ServerLevel level = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (level == null) return;
        // A city the world has not raised for itself is raised from its pin as a player comes to the valley, the same
        // way the camps are: a piece at a time, on this clock, and only while somebody is near enough to see it.
        CityCommands.rouse(level);
        if (job == null) {
            // Looking for a city is a table read, so it happens once a second; working on one happens every tick,
            // because a step is what the whole job is made of.
            if (event.getServer().getTickCount() % CHECK_EVERY != 0) return;
            job = start(level);
            if (job == null) return;
        }
        // A city with nobody near it waits where it is, and resumes the moment someone walks back into the valley.
        if (!watched(level, job.box)) return;
        if (!job.step(level)) {
            finished = job.key;
            job = null;
        }
    }

    /**
     * The drape, run to the end in one call, for the hand that asked for it.
     * <p>
     * The same job as the background one, stepped until the ring is done: an operator looking at a city wants an
     * answer, not a pass that finishes next minute, and the pieces of the cut are already read for them by the time
     * the command is typed.
     */
    static CityCommands.Drape drape(ServerLevel level, Path folder, List<CityCommands.Part> parts,
                                    BlockPos origin, CityCommands.Box box) {
        return drape(level, folder, Pieces.ofCity(folder), parts, origin, box, false, CityCommands.DECK_LIFT);
    }

    /**
     * The same drape for a cut that is not the city's: the pieces come from wherever the caller says.
     * <p>
     * The city's own drape reads its outline through {@link CityCommands#raw}, which knows the city's folder and the
     * cut in the jar. A build cut the same way and laid by `/tmcut` is a different folder, so the reading is the one
     * thing the two cannot share; everything after it - the outline, the ring, the water left alone, the filling of
     * the build's holes - is the same pass, and is meant to be, because two drape rules for two builds is how a
     * country ends up with two different ideas about where a wall stops.
     */
    static CityCommands.Drape drape(ServerLevel level, Path folder, Pieces pieces, List<CityCommands.Part> parts,
                                    BlockPos origin, CityCommands.Box box) {
        return drape(level, folder, pieces, parts, origin, box, false, CityCommands.DECK_LIFT);
    }

    /**
     * The same drape with the build's own trees and its own deck line.
     * <p>
     * The outline is the highest block the cut puts in each column, and a village's highest block is a canopy: the
     * ring would then be filled up to the leaves, which stands a wall of stone where the ground beside a tree belongs,
     * one spike per tree. {@code trees} says the cut's trees are not its shape - leaves and logs are skipped while the
     * outline is taken - so the ring meets the ground under the crown. The city keeps the reading it has always had:
     * its own stone is its shape, and nothing in its cut is a tree.
     * <p>
     * {@code deck} is how high the build's floor stands over the corner it was planted on. The city's is two, and the
     * number was written as a constant of the city's - which reads aright for the city and puts a village whose floor
     * is sixteen up into a trench the height of its own walls. A cut that says what its floor is gets its own answer;
     * one that says nothing gets the city's.
     */
    static CityCommands.Drape drape(ServerLevel level, Path folder, Pieces pieces, List<CityCommands.Part> parts,
                                    BlockPos origin, CityCommands.Box box, boolean trees, int deck) {
        Job whole = new Job("", folder, pieces, parts, origin, box, false, false, trees, deck);
        while (whole.piece < parts.size()) whole.takePieces();
        whole.grow();
        while (whole.chunk < whole.ring.size()) whole.dressChunks(level);
        return new CityCommands.Drape(whole.cut, whole.filled, whole.open, whole.skipped, whole.orphaned,
                whole.inside);
    }

    /**
     * The same pass as a job the caller steps itself, a tick at a time.
     * <p>
     * A world that raises its own village on the first start cannot hold the whole drape in one call: the ring is
     * seven hundred blocks across and dressing it in a single tick is a stall at the very moment a player is waiting
     * for the world. The city's own job already knows how to stop between pieces, between rings and between chunks,
     * so a build with no city is given the same job rather than a second copy of it.
     */
    static Job begin(ServerLevel level, Path folder, Pieces pieces, List<CityCommands.Part> parts,
                     BlockPos origin, CityCommands.Box box, boolean trees, int deck) {
        return new Job("", folder, pieces, parts, origin, box, false, true, trees, deck);
    }

    /** Where a job reads its pieces' NBT from: the city's cut, or any other cut a hand names. */
    interface Pieces {
        CompoundTag raw(String name) throws IOException;

        static Pieces ofCity(Path folder) {
            return name -> CityCommands.raw(folder, name);
        }
    }

    /** A job for a city this world has stood and not yet dressed, or null when there is nothing to do. */
    private static Job start(ServerLevel level) {
        BlockPos corner = CityCommands.stoodCity(level);
        if (corner == null) return null;
        String key = level.dimension().identifier() + "@" + corner.toShortString();
        if (key.equals(finished)) return null;
        Path folder = CityCommands.folder(level);
        try {
            List<CityCommands.Part> parts = CityCommands.parts(folder);
            if (parts.isEmpty()) return null;
            CityCommands.Box box = CityCommands.Box.of(parts, corner);
            // Nobody near the valley means the game is not making its chunks, and a pass that loads them itself is
            // exactly the idle work this whole gate exists to avoid.
            if (!watched(level, box)) return null;
            boolean[] done = dressed(folder, level, corner);
            // The road's completion is the road's own record, not a flag kept beside the cut. A marker written after a
            // pass that failed said the crossing was down when it was not, and the world believed it for good; the
            // pass writes its own note, so it can only say what actually happened. A build with no route has no road
            // to lay, which is the only other way to be finished.
            Route route = Roads.forGround(box.lowX(), box.lowZ(), box.highX(), box.highZ());
            boolean roadDown = route == null || RoadCommands.down(level, route);
            if (route != null && !roadDown) {
                LOGGER.info("Rivendell: the crossing is not down, so a road is planned for it");
            }
            if (done[0] && roadDown) {
                finished = key;
                return null;
            }
            return new Job(key, folder, Pieces.ofCity(folder), parts, corner, box, done[0], roadDown, false,
                    CityCommands.DECK_LIFT);
        } catch (IOException unreadable) {
            LOGGER.warn("Rivendell: the cut could not be read, so the ground is not dressed: {}",
                    unreadable.getMessage());
            return null;
        }
    }

    /**
     * One city's dressing, in the order it has to happen: the cut's outline, taken a piece at a time; the ring of
     * ground outside the walls, dressed a chunk at a time; and the road, which is one pass because a road is a road
     * from end to end and not a thing to leave half graded.
     */
    static final class Job {
        /** This job's city, as dimension and corner: what {@code _dressed.txt} is keyed on. */
        private final String key;
        private final Path folder;
        private final Pieces pieces;
        /** Whether the cut's trees are to be left out of its outline: true for a build with a canopy over its edge. */
        private final boolean trees;
        private final List<CityCommands.Part> parts;
        private final BlockPos origin;
        private final CityCommands.Box box;
        /**
         * How high this build's own floor stands over its planted corner: a deck is a floor tile, and a roofline is
         * not.
         * <p>
         * It was a constant of the city's, and a build whose floor is not two blocks over its corner reads every
         * column outside its walls as a roofline and cuts the country down to it. Hobbiton's floor is sixteen over
         * its base, so the whole village would have been dressed to the tops of its walls. The cut says what its own
         * floor is; the city's number is only the answer when a cut says nothing.
         */
        private final int deck;
        private final int wide;
        private final int deep;
        /** The build's own height in every column of its footprint, and where its water stands on top of that. */
        private final int[] outline;
        private final int[] watered;
        private boolean[] channel;
        /** The chunks of the ring, worked out once so the pass is a cursor rather than a nested loop. */
        private final List<int[]> ring = new ArrayList<>();
        private int piece;
        private int chunk;
        private boolean grown;
        private boolean dressed;
        private boolean road;
        /** The road being laid, once the ground is dressed. */
        private RoadCommands.RoadJob laying;
        /** How many times the road has failed to build, so a pass that cannot run is not retried every tick for ever. */
        private int failures;
        private int cut;
        private int filled;
        private int open;
        /** Columns looked at outside the walls and left exactly as they stand, so a dressed ring can be counted. */
        private int skipped;
        /** Of those, the ones whose ground is gone and whose tree was taken off instead of being walked past. */
        private int orphaned;
        /** Columns inside the walls where the country was standing over the cut and was taken off it. */
        private int inside;

        private Job(String key, Path folder, Pieces pieces, List<CityCommands.Part> parts, BlockPos origin,
                    CityCommands.Box box, boolean dressed, boolean road, boolean trees, int deck) {
            this.deck = deck;
            this.key = key;
            this.folder = folder;
            this.pieces = pieces;
            this.trees = trees;
            this.parts = parts;
            this.origin = origin;
            this.box = box;
            this.dressed = dressed;
            this.road = road;
            this.wide = box.highX() - box.lowX() + 1;
            this.deep = box.highZ() - box.lowZ() + 1;
            this.outline = new int[wide * deep];
            Arrays.fill(this.outline, Integer.MIN_VALUE);
            this.watered = new int[wide * deep];
            Arrays.fill(this.watered, Integer.MIN_VALUE);
            int reach = CityCommands.DRAPE_REACH;
            for (int chunkX = (box.lowX() - reach) >> 4; chunkX <= (box.highX() + reach) >> 4; chunkX++) {
                for (int chunkZ = (box.lowZ() - reach) >> 4; chunkZ <= (box.highZ() + reach) >> 4; chunkZ++) {
                    ring.add(new int[]{chunkX, chunkZ});
                }
            }
        }

        /** One step of the job; false when there is nothing left to do. */
        boolean step(ServerLevel level) {
            if (!dressed) {
                if (piece < parts.size()) {
                    takePieces();
                    return true;
                }
                if (!grown) {
                    grow();
                    return true;
                }
                if (chunk < ring.size()) {
                    dressChunks(level);
                    return true;
                }
                dressed = true;
                LOGGER.info("Rivendell: the ground is dressed - {} columns cut down, {} filled up, {} left for the "
                        + "water, {} left alone, {} cleared of a tree standing on nothing, {} inside the walls cut "
                        + "down to the cut",
                        cut, filled, open, skipped, orphaned, inside);
                write(level, true, road);
            }
            if (!road) {
                apply(level);
                if (!road) return failures < ROAD_ATTEMPTS;
                write(level, true, true);
            }
            return false;
        }

        /** The next pieces of the cut, read into the outline: where the build's own top stands in each column. */
        private void takePieces() {
            int upto = Math.min(parts.size(), piece + PIECES_PER_TICK);
            for (; piece < upto; piece++) {
                CityCommands.Part part = parts.get(piece);
                CompoundTag tag;
                try {
                    tag = pieces.raw(part.name());
                } catch (IOException unreadable) {
                    // A piece that cannot be read is not a reason to dress the rest of the ring to a hole.
                    LOGGER.warn("Rivendell: {} could not be read for the outline", part.name());
                    continue;
                }
                ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
                String[] names = new String[palette.size()];
                for (int i = 0; i < palette.size(); i++) {
                    names[i] = palette.getCompoundOrEmpty(i).getStringOr("Name", "");
                }
                ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
                for (int i = 0; i < blocks.size(); i++) {
                    CompoundTag block = blocks.getCompoundOrEmpty(i);
                    int state = block.getIntOr("state", 0);
                    if (state < 0 || state >= names.length) continue;
                    String name = names[state];
                    if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
                    if (trees && tree(name)) continue;
                    ListTag pos = block.getList("pos").orElseGet(ListTag::new);
                    if (pos.size() < 3) continue;
                    int gx = origin.getX() + part.dx() + pos.getIntOr(0, 0) - box.lowX();
                    int gz = origin.getZ() + part.dz() + pos.getIntOr(2, 0) - box.lowZ();
                    if (gx < 0 || gx >= wide || gz < 0 || gz >= deep) continue;
                    int at = gx + gz * wide;
                    int y = origin.getY() + part.dy() + pos.getIntOr(1, 0);
                    if (y > outline[at]) outline[at] = y;
                    if (name.contains("water")) watered[at] = Math.max(watered[at], y);
                }
            }
        }

        /** Whether a block is part of a tree rather than part of the ground: leaves, a trunk, a fungus stem. */
        private static boolean tree(String name) {
            return name.endsWith("_leaves") || name.contains("leaves_") || name.endsWith("_log")
                    || name.contains("log_") || name.contains("_wood") || name.contains("wood_")
                    || name.contains("_stem") || name.contains("_hyphae");
        }

        /**
         * Whether a block is the ground a column stands on, rather than the tree standing on it.
         * <p>
         * The leaves of this mod are in {@code #minecraft:leaves}, but its logs and wood are in no tag, so the tag is
         * not enough: a trunk of the mod's own wood would read as ground and be cut only to the top of the trunk.
         */
        private static boolean ground(BlockState state) {
            if (!state.isSolid() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)) return false;
            return !tree(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
        }

        /**
         * Fills the build's holes, and marks its water.
         * <p>
         * A wall with a doorway in it is still a wall: a column the build leaves empty takes the height of the
         * columns beside it, so the drape follows the shape of the city rather than its holes. A column is a
         * watercourse only where the water is what stands on top of it - a channel the river can run in - and not
         * where water is buried in the build, since the spring's aqueduct is under a roof at 239.
         */
        private void grow() {
            grown = true;
            int[] top = outline;
            for (int pass = 0; pass < 2; pass++) {
                int[] next = top.clone();
                for (int gz = 0; gz < deep; gz++) {
                    for (int gx = 0; gx < wide; gx++) {
                        int at = gx + gz * wide;
                        if (top[at] != Integer.MIN_VALUE) continue;
                        int best = Integer.MIN_VALUE;
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                int nx = gx + dx;
                                int nz = gz + dz;
                                if (nx < 0 || nx >= wide || nz < 0 || nz >= deep) continue;
                                best = Math.max(best, top[nx + nz * wide]);
                            }
                        }
                        next[at] = best;
                    }
                }
                top = next;
            }
            channel = new boolean[wide * deep];
            for (int i = 0; i < channel.length; i++) {
                channel[i] = top[i] != Integer.MIN_VALUE && watered[i] == top[i];
            }
        }

        /** Dresses the next chunks of the ring, opening each one first: a block written into a closed chunk is lost. */
        private void dressChunks(ServerLevel level) {
            int reach = CityCommands.DRAPE_REACH;
            int upto = Math.min(ring.size(), chunk + CHUNKS_PER_TICK);
            for (; chunk < upto; chunk++) {
                int[] at = ring.get(chunk);
                level.getChunk(at[0], at[1]);
                int fromX = Math.max(at[0] << 4, box.lowX() - reach);
                int toX = Math.min((at[0] << 4) + 15, box.highX() + reach);
                int fromZ = Math.max(at[1] << 4, box.lowZ() - reach);
                int toZ = Math.min((at[1] << 4) + 15, box.highZ() + reach);
                for (int x = fromX; x <= toX; x++) {
                    for (int z = fromZ; z <= toZ; z++) {
                        dressColumn(level, x, z);
                    }
                }
            }
        }

        /**
         * One column outside the walls, carried to the build's own outline.
         * <p>
         * Capped at the nearest outline column where the wall is, easing to what the ground already stands at
         * {@code DRAPE_REACH} out, so the join at the wall is the build's shape and the join in the country is the
         * country's. Water at the build's own edge is left alone where it is the outflow, banked where it is a spring
         * held up in the build, and cut with the ground it lies on everywhere else.
         */
        private void dressColumn(ServerLevel level, int x, int z) {
            if (x >= box.lowX() && x <= box.highX() && z >= box.lowZ() && z <= box.highZ()) {
                dressInside(level, x, z);
                return;
            }
            int gx = Mth.clamp(x, box.lowX(), box.highX()) - box.lowX();
            int gz = Mth.clamp(z, box.lowZ(), box.highZ()) - box.lowZ();
            int at = gx + gz * wide;
            int wall = outline[at];
            if (wall == Integer.MIN_VALUE) {
                // No piece of the cut at this column of the wall: there is no shape to join to, so it is left.
                skipped++;
                return;
            }
            // The deck line is the build's own floor, read from the corner the city was planted at: its deck
            // tiles lie on the two blocks over it, whatever height the world has room for.
            boolean banked = wall <= origin.getY() + deck;
            // A spring at the wall is not a deck: the column is held for its water, but the ground over that
            // water is still ground the drape is for.
            boolean spring = false;
            if (banked) {
                // A deck is a floor tile rather than a roofline, so the ground outside meets the rock standing over
                // that deck inside the box and is never cut down to the deck itself.
                wall = Math.max(wall, level.getHeight(Heightmap.Types.MOTION_BLOCKING,
                        gx + box.lowX(), gz + box.lowZ()) - 1);
            }
            if (channel[at]) {
                if (watered[at] - (box.lowY() - 1) <= CityCommands.OUTLET_LIFT) {
                    // The city's outflow: the branch takes the water out at the floor, so the mouth stays open - and
                    // the ground in front of it is taken out with it. Left standing, the country's own bank is a bar
                    // across the mouth, because every column of the ring in front of a mouth clamps to the mouth's
                    // own column and so read as water and was left alone: the water left the build and stopped
                    // against four to six blocks of land eight blocks out.
                    openMouth(level, x, z, watered[at]);
                    open++;
                    return;
                }
                spring = true;
                banked = true;
            }
            int surface = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z),
                    level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)) - 1;
            if (surface <= level.getMinY()) {
                skipped++;
                return;
            }
            // A tree is not ground, and the top of its crown is not where its ground is. The height that counts
            // leaves out answers where the column really stands; read from the leaves' own top, a column under a
            // crown that hung over air - which every big crown does - stepped down into that air and took it for its
            // skin, so the fill began five or ten blocks over the ground and laid its rock in the empty space the
            // crown had been in. That is the plate of bare stone standing in the air above a dressed tree, one per
            // crown, all round the ring.
            //
            // A height still answers for less than the whole column. A height that counts leaves out answers with the
            // highest thing that is not a leaf, which under a bough reaching out over air is the bough itself, one
            // block over nothing: read there, a column had its crown taken off - the leaves over the bough stand
            // above it and are cleared first - then found air under the bough, was taken for a column with no ground,
            // and was left exactly as it stood. That is the glade where the drape took the trees out and left the
            // ground empty, one per crown along a join. So the heights only say where to start looking, and the column
            // is read down from the top of whatever stands, through air, leaves and wood, to the first ground.
            int floor = Math.max(level.getMinY(), surface - COLUMN_DEPTH);
            int top = surface;
            BlockState skin = level.getBlockState(new BlockPos(x, top, z));
            while (top > floor && !ground(skin)) {
                skin = level.getBlockState(new BlockPos(x, --top, z));
            }
            if (!ground(skin)) {
                // The ground this column stood on is not merely under a bough any more: it is gone, and further under
                // the column than the search is allowed. That is a build's own carve - Rivendell's cut took the hill
                // out from under its oaks, and their crowns hung ninety-four blocks over the floor while the pass
                // walked past them for ever. There is no ground to join to, so nothing is filled and nothing is
                // joined; the tree standing in the column comes off, which is all that is left of it.
                if (orphan(level, x, z, surface, floor) > 0) orphaned++;
                else skipped++;
                return;
            }
            for (int y = surface; y > top; y--) {
                level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
            }
            if (!skin.getFluidState().isEmpty()) {
                // Water out in the ring is a pool lying on the drawn ground, so the pass works down to the ground
                // under it rather than leaving a pillar of ground standing wherever a puddle reached the top.
                while (top > level.getMinY() + 1
                        && !level.getBlockState(new BlockPos(x, top, z)).getFluidState().isEmpty()) {
                    top--;
                }
                skin = level.getBlockState(new BlockPos(x, top, z));
                if (!skin.getFluidState().isEmpty()) {
                    skipped++;
                    return;
                }
            }
            // A column with nothing solid under it is not one the drape can carry to a cap: a fill built there is
            // rock with air beneath it, and a cut would take the ground out from under whatever does stand. It is
            // left exactly as it is - the same answer the ring gives a column the build leaves open.
            if (!skin.isSolid()) {
                skipped++;
                return;
            }
            int outX = Math.max(0, Math.max(box.lowX() - x, x - box.highX()));
            int outZ = Math.max(0, Math.max(box.lowZ() - z, z - box.highZ()));
            double share = Math.min(1.0D, Math.hypot(outX, outZ) / (double) CityCommands.DRAPE_REACH);
            double ease = share * share * (3.0D - 2.0D * share);
            int cap = (int) Math.round(wall + (top - wall) * ease);
            // A deck is not a roofline, so the ground outside it is not cut - the trench that opened round every
            // face the build leaves open is why. A spring is a different column: its pool is the build's own top
            // there, so the cut stops at the pool's surface and no further. The mountain standing over the water
            // comes off - one chunk of crown left hanging over a spring while every other face was dressed - and
            // the water stays where it is.
            //
            // A bank is the third case, and the one the village found. Where the country stands well over the
            // build's own floor, leaving it alone is not a trench but a cliff: eleven blocks of raw ground along the
            // Shire's south edge with the village's own paving at its foot. So a column standing BANK_BLOCKS or more
            // over the floor is cut to the same join every other column is cut to - the build's own ground at the
            // wall, easing back into the country over the reach - and the shallow grooves round a city's open faces
            // are left exactly as they were.
            int down = spring ? Math.max(cap, watered[at]) : cap;
            boolean bank = banked && !spring && top - (origin.getY() + deck) >= BANK_BLOCKS;
            if (top > down && (!banked || spring || bank)) {
                for (int y = surface; y > down; y--) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, down, z), skin, 2);
                cut++;
            } else if (top < cap) {
                for (int y = top + 1; y < cap; y++) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, cap, z), skin, 2);
                filled++;
            }
        }

        /**
         * One column inside the walls, where the country is still standing over the cut's own ground.
         * <p>
         * The ring is the ring, and the ground inside the footprint is the build's: the pieces were laid, so what
         * stands in a column is the city. That holds for every column the cut fills to its own top. It does not hold
         * for a cut that carves a valley, whose floors are left under the hill that was never taken off them - which
         * is a mountain standing raw inside the city beside a ring dressed down to the skyline. So a column inside
         * the walls whose country stands {@link #BANK_BLOCKS} or more over the top of the cut in it has the country
         * taken off down to that top: the cut's own top course is the city's and stays, nothing below it is touched,
         * so no wall, floor or roof of the build can be cut - and what stood over the city was never part of it. The
         * ring's shallow grooves are left as grooves here too, and the water is left where it is.
         */
        private void dressInside(ServerLevel level, int x, int z) {
            int at = (x - box.lowX()) + (z - box.lowZ()) * wide;
            int roof = outline[at];
            if (roof == Integer.MIN_VALUE) {
                // A column the cut leaves empty is a hole in the city, not ground the country is standing over.
                skipped++;
                return;
            }
            int surface = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z),
                    level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)) - 1;
            if (surface <= level.getMinY() || surface - roof < BANK_BLOCKS) return;
            if (!level.getBlockState(new BlockPos(x, surface, z)).getFluidState().isEmpty()) return;
            for (int y = surface; y > roof; y--) {
                level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
            }
            inside++;
        }

        /**
         * A column whose ground is gone: the tree standing in it comes off, and nothing is put in its place.
         * <p>
         * The ground search stops {@link #COLUMN_DEPTH} blocks under the top of a column - deeper than any canopy and
         * shallower than the air over a cave - and a column that found nothing in that depth was left exactly as it
         * stood, on the reading that a column with a hole in it has no join to make. The reading holds for a cave and
         * fails for a build's own carve: a crown over ground a cut took out is not a hole in the country, it is a
         * tree with nothing under it, and leaving it is a shoulder dressed to the skyline with a garden hanging over
         * it. Nothing is filled and nothing is joined here - rock built in the air is rock over a hole - so the
         * column is left as the carve left it, less the tree.
         *
         * @return how many blocks of tree came off, which is nought when the column was not standing in a tree
         */
        private int orphan(ServerLevel level, int x, int z, int surface, int floor) {
            int taken = 0;
            for (int y = surface; y >= floor; y--) {
                BlockState state = level.getBlockState(new BlockPos(x, y, z));
                if (state.isAir()) continue;
                if (!state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS)
                        && !tree(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString())) {
                    // Something that is not a tree: not this pass's to take from a column it cannot join.
                    break;
                }
                level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                taken++;
            }
            return taken;
        }

        /**
         * Takes the ground out of the way of a mouth and lays the water back over it, leaving a bed under it.
         * <p>
         * Only the ring is cut, and only where the ground stands over the bed: a column that is already the drawn
         * river, or already under the water, is left exactly as it is, so the join is the drawing's own channel
         * meeting the build's rather than a trench dug through the river to reach it. What is taken out is filled with
         * water up to the level the mouth carries it out at, so the build's water and the drawing's meet as one sheet
         * rather than along the dry strip a bare cut leaves. A tree in the way goes with the ground, as it does
         * everywhere else the drape cuts.
         *
         * @return whether anything was taken out
         */
        private boolean openMouth(ServerLevel level, int x, int z, int water) {
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
            if (top <= level.getMinY()) return false;
            BlockState skin = level.getBlockState(new BlockPos(x, top, z));
            while (top > level.getMinY() && (skin.is(BlockTags.LEAVES) || skin.is(BlockTags.LOGS))) {
                skin = level.getBlockState(new BlockPos(x, --top, z));
            }
            if (top <= level.getMinY() || !skin.getFluidState().isEmpty() || !skin.isSolid()) return false;
            int bed = water - CityCommands.MOUTH_DEPTH;
            if (top <= bed) return false;
            for (int y = top; y > bed; y--) {
                level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
            }
            level.setBlock(new BlockPos(x, bed, z), skin, 2);
            // A bed is not a gap. The cut stopped at the bed, with the mouth's water standing in the build behind it and
            // the drawing's channel in front, so the two sheets met along a dry strip three blocks deep: the city's
            // water had nowhere to go but over ground. Filled to the level the mouth carries it out at - no higher, so
            // the fill can never stand as a wall of water - the strip is the channel's own water and the join is a join.
            for (int y = bed + 1; y <= water; y++) {
                level.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState(), 2);
            }
            return true;
        }

        /** One step of the road: the hand's own pass, taken a stage at a time so no tick carries it whole. */
        private void apply(ServerLevel level) {
            try {
                if (laying == null) {
                    Route route = Roads.forGround(box.lowX(), box.lowZ(), box.highX(), box.highZ());
                    if (route == null) {
                        // A build nobody has walked a road for gets none: a road laid to another city's numbers
                        // is worse than ground with no way down it.
                        road = true;
                        LOGGER.info("Rivendell: no road is walked for this build, so the ground is all it gets");
                        return;
                    }
                    if (RoadCommands.down(level, route)) {
                        road = true;
                        LOGGER.info("Rivendell: the road is already down, so there is nothing left to build");
                        return;
                    }
                    laying = RoadCommands.plan(route);
                }
                if (laying.step(level)) return;
                RoadCommands.Road laid = laying.summary();
                road = true;
                LOGGER.info("Rivendell: the road is down - bridge of {} pieces, approach {} blocks, {} columns laid",
                        laid.pieces(), laid.along(), laid.graded()[0] + laid.far()[0]);
            } catch (IOException unreadable) {
                // Not recorded as laid. A failed pass used to set this flag anyway, and the job then wrote the marker
                // saying the road was down - which is how a world came to have a crossing nobody had built and no
                // plan to build one. It is retried a few times and then left for the next visit.
                laying = null;
                if (++failures < ROAD_ATTEMPTS) {
                    LOGGER.warn("Rivendell: the road could not be built, trying again: {}", unreadable.getMessage());
                    return;
                }
                LOGGER.warn("Rivendell: the road could not be built: {} - it is not recorded as laid",
                        unreadable.getMessage());
            }
        }

        /** What this world has had dressed, written so a restart does not do it again. */
        private void write(ServerLevel level, boolean drapeDone, boolean roadDone) {
            try {
                Files.createDirectories(folder);
                Files.writeString(folder.resolve(MARKER),
                        level.dimension().identifier() + " " + origin.getX() + " " + origin.getY() + " "
                                + origin.getZ() + " " + (drapeDone ? 1 : 0) + " " + (roadDone ? 1 : 0));
            } catch (IOException unwritable) {
                LOGGER.warn("Rivendell: what was dressed could not be written: {}", unwritable.getMessage());
            }
        }
    }

    /**
     * Whether anyone is near enough to the city for the world to work on it.
     * <p>
     * Measured from the footprint rather than from a corner: the city is five hundred blocks across, so a corner
     * would call a player standing at the far wall a long way away and a player out in the ring next door. The ring
     * and the bridge are both inside the reach, which is all a pass that writes ground in the city's own country
     * needs - a player who can see the valley is a player whose chunks the game is already keeping.
     */
    private static boolean watched(ServerLevel level, CityCommands.Box box) {
        for (ServerPlayer player : level.players()) {
            BlockPos at = player.blockPosition();
            int dx = Math.max(0, Math.max(box.lowX() - at.getX(), at.getX() - box.highX()));
            int dz = Math.max(0, Math.max(box.lowZ() - at.getZ(), at.getZ() - box.highZ()));
            if (Math.hypot(dx, dz) <= WORK_REACH) return true;
        }
        return false;
    }

    /** Whether this corner of this dimension has had its drape and its road, as {drape, road}. */
    private static boolean[] dressed(Path folder, ServerLevel level, BlockPos corner) {
        boolean[] none = {false, false};
        Path marker = folder.resolve(MARKER);
        if (!Files.isRegularFile(marker)) return none;
        try {
            String[] fields = Files.readString(marker).trim().split("\\s+");
            if (fields.length < 6) return none;
            if (!fields[0].equals(level.dimension().identifier().toString())
                    || Integer.parseInt(fields[1]) != corner.getX()
                    || Integer.parseInt(fields[2]) != corner.getY()
                    || Integer.parseInt(fields[3]) != corner.getZ()) {
                return none;
            }
            return new boolean[]{"1".equals(fields[4]), "1".equals(fields[5])};
        } catch (IOException | NumberFormatException unreadable) {
            return none;
        }
    }
}
