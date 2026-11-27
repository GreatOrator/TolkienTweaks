package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
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
     * <p>
     * It is measured over the build's own top at the wall rather than over its deck. The deck is a floor tile, and a
     * village cut into the side of its valley draws edges well under it: the Shire's own north edge stands at 81 with
     * the deck at 86, and the country beside it at 85 - four blocks of raw face at every column of that side, which
     * the deck's rule read as a groove because 85 is under 86, and left standing. Three over the build's own top is a
     * face whatever the build's top is.
     */
    private static final int BANK_BLOCKS = 3;

    /**
     * How wide a bank over the cut is graded back from the cut's own edge, in blocks, and how deep a bank the
     * grading reaches.
     * <p>
     * The inside cut takes the country off a column down to the cut's own top, which is a face wherever the cut
     * steps. Measured on the Shire's village, 99% of neighbouring cut columns differ by a block or none, so the cut
     * itself is not terraced - but a bank of three to eight blocks beside a field plate reads as a straight drop at
     * the plate's edge, which is the square the owner photographed. Four blocks is how wide a hobbit bank is at its
     * steepest, and eight is the deepest bank the inside of a village carries: a column with more than that over it
     * is the country outside the build, which the ring and its own join answer, not this.
     */
    private static final int APRON_WIDTH = 4;
    private static final int APRON_CAP = 8;

    /**
     * How far inside the build's own ground the country is graded up to it, in blocks, and the tallest edge it is
     * graded against.
     * <p>
     * The inside of a village is not all cut. The pieces are forty-eight-block tiles and a village's fields stop short
     * of the tile's edge, so a quarter of the footprint is country the tile was cut over - and the clear leaves that
     * country exactly where the land put it while the field beside it stands four blocks over. That is the plate with a
     * straight drop at its edge the owner photographed: one column wide, all round every field. Three to six blocks of
     * bank is how a hobbit field meets the land it is cut into, so the country is filled up to the plate over this many
     * blocks - following the country's own top, so the foot of the bank wanders with the land and not with the tile -
     * and the country beyond is left alone. Four, with the wander on top, is a bank of four to seven blocks.
     * <p>
     * The rise is capped because a build's own hill stands seventeen blocks over the country on the Shire's west side,
     * and a fill laid against it buries its foot rather than joining it. That edge is the build's and is counted as left
     * alone, not dressed; only the short edges a field or a lane makes are graded.
     */
    private static final int SKIRT_REACH = 4;
    private static final int SKIRT_RISE = 6;

    /**
     * How close to the build's own edge a pool is drained, in blocks, when it is not the river through the city.
     * <p>
     * The drape never dresses a column standing in water, which is right for the drawing's river and wrong for the
     * puddles the drawing leaves at a build's own edge: the ring could not cut or fill beside them, so the join
     * stopped dead at a source a block wide and the wall stood beside it - the stalls the owner photographed on
     * 26 November. Three blocks is the width of the shoulder the ring lays, so a pool further in than that is the
     * drawing's water and stays. A pool within three blocks of a column the cut itself carries water in is the
     * river's own and is left as well.
     */
    private static final int EDGE_WATER = 3;

    /** How far in from the build's own edge the mountain's crest stands, in blocks. */
    private static final int HEM_INSET = 4;
    /** How far out from the build the mountain runs before it is the country again, in blocks. */
    private static final int HEM_REACH = 60;
    /** How far the crest stands over the build's own top, in blocks, before {@link #HEM_DROP} is taken off it. */
    private static final int HEM_CREST = 3;
    /** How far the whole hill is lowered, crest and foot together: the owner's "lower it about 3 blocks". */
    private static final int HEM_DROP = 3;
    /**
     * How far into the cut's own tiles the build's own edge is looked for, in blocks.
     * <p>
     * The cut is laid in forty-eight-block tiles and the build does not fill the last tile of a row: the village's own
     * east edge stands twenty-five blocks short of its tile's edge, and its south edge twelve. A ring column clamps to
     * the tile's edge, which is air, so a ring that only asked the clamped column left the whole of those sides of the
     * country exactly as it stood - the two- and five-block steps the owner photographed on 26 November. The nearest
     * filled column is looked for along the row and then along the column, and never further than a tile and a half,
     * so the search cannot cross a hollow in the build and join to something else.
     */
    private static final int HEM_HELD = 64;
    /** How far the build's edge must stand over its own deck before a mountain is planted over it. */
    private static final int HEM_TALL = 4;

    /**
     * How deep the country keeps its own dirt under its grass, and so how deep a fill keeps it too.
     * <p>
     * The drawn ground shows three to four blocks of dirt over its stone wherever a riser is cut (dirt 83 to 85 under
     * the grass at 86 the whole length of the village's ring), and that band is what makes a one-block terrace read as
     * ground. A fill laid in stone to the surface showed every step of its shoulder as a grey plate: the ring is 24,417
     * fills, and from the water the whole apron read as a quarry cut rather than a hillside.
     */
    private static final int SOIL_BLOCKS = 3;

    /**
     * What the water map answers for a column with no water within the ring, in blocks.
     * <p>
     * Past any cap, so a column that took this number is left at its own shape: the shore rule can only lower a fill,
     * never raise one.
     */
    private static final int NO_WATER = 1024;
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
        Job whole = new Job("", folder, pieces, parts, origin, box, false, false, trees, deck,
                CityCommands.DRAPE_REACH);
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
                     BlockPos origin, CityCommands.Box box, boolean trees, int deck, int reach) {
        return new Job("", folder, pieces, parts, origin, box, false, true, trees, deck, reach);
    }

    /**
     * Puts a build's own ground back where something has worn it away.
     * <p>
     * The drape lays a build once and never rewrites the cut's own blocks, which is what keeps a wall, a floor and a
     * roof whole inside its walls. Nothing in the drape wears them either - what does is the build's own folk walking
     * over its grass, which vanilla turns to dirt under a body's feet, so a village a fortnight old has a trail of bare
     * dirt through it that reads as ground the drape skipped. This is the repair, and it is asked for by a hand: the
     * cut is read for its own grass and nothing but a worn column of it is written.
     * <p>
     * The whole cut is asked, not a circle round the caller. The wear is the village's own folk walking, and they walk
     * it end to end: the village in `Generation Redux - 3` holds 5,450 columns of it, and a hand stood where the
     * screenshots are taken is within sixty-four blocks of four of them, because the other 5,446 are across the green
     * and no nearer the hand than they are each other. Reading the cut's grass is what the pass costs - its grid is 551
     * by 468 columns, and every one of them is read before a single block is written - so the radius never bought a
     * cheaper pass, only a smaller one.
     *
     * @return how many columns took their grass back
     */
    static int repair(ServerLevel level, Path folder, Pieces pieces, List<CityCommands.Part> parts,
                      BlockPos origin, CityCommands.Box box, boolean trees, int deck) {
        Job job = new Job("", folder, pieces, parts, origin, box, false, false, trees, deck,
                CityCommands.DRAPE_REACH);
        while (job.piece < parts.size()) job.takePieces();
        job.grow();
        return job.heal(level);
    }

    /**
     * Whether a cut's palette name is the grass a build's feet wear.
     * <p>
     * A palette name is the block's own key, written by the same `getKey` every piece in the jar was cut with, so
     * the test is a string's and needs no registry read: what a repair puts back is grass and nothing else.
     */
    private static boolean grass(String name) {
        return name.equals("minecraft:grass_block") || name.equals("grass_block");
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
                    CityCommands.DECK_LIFT, CityCommands.DRAPE_REACH);
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
        /**
         * How far outside the footprint this job dresses, in blocks.
         * <p>
         * The ring is the ground outside the build, and sixty blocks of it is what a city's valley needs. A village
         * whose ring is switched off still wants its own rim graded - the banks at the foot of its fields are inside
         * its footprint - so the same job runs with a reach of nought: the same columns walked, the same inside pass,
         * and not one block outside the build read or written.
         */
        private final int reach;
        /** The build's own height in every column of its footprint, and where its water stands on top of that. */
        private final int[] outline;
        /**
         * The build's own height in every column of its footprint with its trees counted, which {@link #outline} leaves
         * out.
         * <p>
         * The ring reads a column without its canopy, because a ring filled up to the leaves stands a wall of stone
         * beside every tree. The ground inside the walls asks the other question: there, what the build draws is the
         * build, and a village's party tree is in its file as its walls are. Read against the tree-less outline, a drawn
         * tree stood five blocks over the course of grass it grows in and read as the country standing over the village,
         * so the inside cut took it off at the grass - the party tree came up as sawn-off poles with its lanterns hanging
         * in the air where the crown had been. This is the same outline with the trees left in, and it is what the
         * inside cut is asked about.
         */
        private final int[] whole;
        private final int[] watered;
        /** How high the cut's own grass stands in every column - the one block a repair puts back. */
        private final int[] crownY;
        private boolean[] channel;
        /** The chunks of the ring, worked out once so the pass is a cursor rather than a nested loop. */
        private final List<int[]> ring = new ArrayList<>();
        private int piece;
        private int chunk;
        private boolean grown;
        private boolean dressed;
        /**
         * How far every column of the ring stands from water, and the level that water is at.
         * <p>
         * Water is the one thing the ring never dresses, so a fill that ran to a shoreline stopped dead at it and
         * stood as a face - a fill of twenty blocks beside the river in the world made on 26 November. The distance is
         * read once for the whole ring, before the ring is dressed, so a column can be carried down into the water it
         * is beside as a shore.
         */
        private int[] shore = new int[0];
        private int[] shoreLevel = new int[0];
        private int shoreLowX;
        private int shoreLowZ;
        private int shoreWide;
        private int shoreDeep;
        /** How far the water has been read, in the ring's own chunks, and whether its distances are worked out. */
        private int shoreChunk;
        private boolean shoreReady;
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
        /** Columns inside the footprint where the country was graded up to the foot of the build's own ground. */
        private int skirted;
        /** Columns of the build's own outer blocks carried under the mountain planted round its high points. */
        private int covered;
        /** Columns of the ring the mountain raised or cut to its own flank. */
        private int mounted;

        private Job(String key, Path folder, Pieces pieces, List<CityCommands.Part> parts, BlockPos origin,
                    CityCommands.Box box, boolean dressed, boolean road, boolean trees, int deck, int reach) {
            this.deck = deck;
            this.reach = reach;
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
            this.whole = new int[wide * deep];
            Arrays.fill(this.whole, Integer.MIN_VALUE);
            this.watered = new int[wide * deep];
            Arrays.fill(this.watered, Integer.MIN_VALUE);
            this.crownY = new int[wide * deep];
            Arrays.fill(this.crownY, Integer.MIN_VALUE);
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
                if (!shoreReady) {
                    mapWater(level);
                    return true;
                }
                if (chunk < ring.size()) {
                    dressChunks(level);
                    return true;
                }
                dressed = true;
                LOGGER.info("Rivendell: the ground is dressed - {} columns cut down, {} filled up, {} left for the "
                        + "water, {} left alone, {} cleared of a tree standing on nothing, {} inside the walls cut "
                        + "down to the cut, {} of the build's own edge banked up, {} of the build carried under the "
                        + "mountain, {} of the ring on its flank",
                        cut, filled, open, skipped, orphaned, inside, skirted, covered, mounted);
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
                    boolean drawn = trees && tree(name);
                    ListTag pos = block.getList("pos").orElseGet(ListTag::new);
                    if (pos.size() < 3) continue;
                    int gx = origin.getX() + part.dx() + pos.getIntOr(0, 0) - box.lowX();
                    int gz = origin.getZ() + part.dz() + pos.getIntOr(2, 0) - box.lowZ();
                    if (gx < 0 || gx >= wide || gz < 0 || gz >= deep) continue;
                    int at = gx + gz * wide;
                    int y = origin.getY() + part.dy() + pos.getIntOr(1, 0);
                    // Read before the trees are skipped: a drawn tree is the build's own height inside its walls, and
                    // the inside cut has to know it stands there or it takes the canopy for the country over the village.
                    if (y > whole[at]) whole[at] = y;
                    if (y >= crownY[at] && grass(name)) crownY[at] = y;
                    if (drawn) continue;
                    if (y > outline[at]) outline[at] = y;
                    if (name.contains("water")) watered[at] = Math.max(watered[at], y);
                }
            }
        }

        /** Whether a top is soil, so a fill under it keeps the country's own band of dirt over its stone. */
        private static boolean soft(BlockState state) {
            return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT)
                    || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM) || state.is(Blocks.ROOTED_DIRT);
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
            holes(outline);
            holes(whole);
            channel = new boolean[wide * deep];
            for (int i = 0; i < channel.length; i++) {
                channel[i] = outline[i] != Integer.MIN_VALUE && watered[i] == outline[i];
            }
        }

        /**
         * Fills a column's own holes from the columns beside them: a wall with a doorway in it is still a wall, and a
         * ring capped at the gap has a pit at every gate.
         * <p>
         * Two passes, because a hole two columns wide is filled from the ring the first pass makes around it. Both
         * outlines are filled - the ring's, which leaves trees out, and the whole one - so each answers for a doorway
         * and neither invents a height for a column the build never reaches.
         */
        private void holes(int[] top) {
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
                System.arraycopy(next, 0, top, 0, top.length);
            }
        }

        /** Dresses the next chunks of the ring, opening each one first: a block written into a closed chunk is lost. */
        private void dressChunks(ServerLevel level) {
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
         * Reads the water across the ring, a couple of chunks a step, and works out the distance to it.
         * <p>
         * Read before the ring is dressed and from the ground as the world had it, so a fill cannot be mistaken for
         * water and the distance cannot depend on the order the chunks are walked in. The distance is then two sweeps
         * of the ring - forward, then back - each column taking the nearest water's distance and level from the eight
         * beside it, which is all the shore rule needs and is paid once for a pass that is already minutes long.
         * <p>
         * Only the water's own top is asked for: a column whose top is a fluid is a water column, and its level is
         * that top. A lily or a leaf pile over the water is not water and is left to the column's own height, since
         * the fill's business is the water's surface and not what floats on it.
         */
        private void mapWater(ServerLevel level) {
            if (shore.length == 0) {
                this.shoreLowX = box.lowX() - reach;
                this.shoreLowZ = box.lowZ() - reach;
                this.shoreWide = box.highX() + reach - shoreLowX + 1;
                this.shoreDeep = box.highZ() + reach - shoreLowZ + 1;
                this.shore = new int[shoreWide * shoreDeep];
                this.shoreLevel = new int[shoreWide * shoreDeep];
                Arrays.fill(shore, NO_WATER);
            }
            int upto = Math.min(ring.size(), shoreChunk + CHUNKS_PER_TICK * 2);
            for (; shoreChunk < upto; shoreChunk++) {
                int[] at = ring.get(shoreChunk);
                level.getChunk(at[0], at[1]);
                int fromX = Math.max(at[0] << 4, shoreLowX);
                int toX = Math.min((at[0] << 4) + 15, shoreLowX + shoreWide - 1);
                int fromZ = Math.max(at[1] << 4, shoreLowZ);
                int toZ = Math.min((at[1] << 4) + 15, shoreLowZ + shoreDeep - 1);
                for (int x = fromX; x <= toX; x++) {
                    for (int z = fromZ; z <= toZ; z++) {
                        int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
                        if (top <= level.getMinY()) continue;
                        if (level.getBlockState(new BlockPos(x, top, z)).getFluidState().isEmpty()) continue;
                        int index = (x - shoreLowX) + (z - shoreLowZ) * shoreWide;
                        shore[index] = 0;
                        shoreLevel[index] = top;
                    }
                }
            }
            if (shoreChunk < ring.size()) return;
            for (int pass = 0; pass < 2; pass++) {
                for (int n = 0; n < shore.length; n++) {
                    int index = pass == 0 ? n : shore.length - 1 - n;
                    int gx = index % shoreWide;
                    int gz = index / shoreWide;
                    int best = shore[index];
                    int bestLevel = shoreLevel[index];
                    for (int ox = -1; ox <= 1; ox++) {
                        int nx = gx + ox;
                        if (nx < 0 || nx >= shoreWide) continue;
                        for (int oz = -1; oz <= 1; oz++) {
                            int nz = gz + oz;
                            if (nz < 0 || nz >= shoreDeep) continue;
                            int beside = nx + nz * shoreWide;
                            int far = shore[beside];
                            if (far == NO_WATER) continue;
                            if (far + 1 < best) {
                                best = far + 1;
                                bestLevel = shoreLevel[beside];
                            }
                        }
                    }
                    shore[index] = best;
                    shoreLevel[index] = bestLevel;
                }
            }
            shoreReady = true;
            LOGGER.info("Rivendell: the water is mapped over the ring - {} columns of it, so the ring is dressed"
                    + " as a shore", countWater());
        }

        /** How many columns of the ring stand in water, which is what the shore rule is worked from. */
        private int countWater() {
            int wet = 0;
            for (int at : shore) if (at == 0) wet++;
            return wet;
        }

        /** How far a column of the ring stands from the nearest water, or {@link #NO_WATER} when there is none. */
        private int shore(int x, int z) {
            int gx = x - shoreLowX;
            int gz = z - shoreLowZ;
            if (gx < 0 || gz < 0 || gx >= shoreWide || gz >= shoreDeep) return NO_WATER;
            return shore[gx + gz * shoreWide];
        }

        /** The level the nearest water stands at, which is nought when there is none. */
        private int shoreLevel(int x, int z) {
            int gx = x - shoreLowX;
            int gz = z - shoreLowZ;
            if (gx < 0 || gz < 0 || gx >= shoreWide || gz >= shoreDeep) return 0;
            return shoreLevel[gx + gz * shoreWide];
        }

        /**
         * One column outside the walls, carried to the build's own outline.
         * <p>
         * Capped at the nearest outline column where the wall is, easing to what the ground already stands at
         * {@code DRAPE_REACH} out, so the join at the wall is the build's shape and the join in the country is the
         * country's. Water at the build's own edge is left alone where it is the outflow, banked where it is a spring
         * held up in the build, and a column standing in the drawing's own water is not dressed at all.
         */
        private void dressColumn(ServerLevel level, int x, int z) {
            if (x >= box.lowX() && x <= box.highX() && z >= box.lowZ() && z <= box.highZ()) {
                dressInside(level, x, z);
                return;
            }
            join(level, x, z);
        }

        /**
         * The build's own edge column nearest a column of the ring, as {x, z}, or null when the cut fills none of
         * that row or column.
         * <p>
         * The cut is a row of forty-eight-block tiles, and the edge of a tile is not the edge of the build: the
         * village's own east edge stands twenty-five blocks inside the tile it was cut from, and its south edge
         * twelve. The ring clamped every column outside the footprint to the edge of a tile, which there is air, so
         * the whole of those sides of the ring was left exactly as it stood - the country's own two- and five-block
         * steps standing where the join should be, which is the lip the owner photographed round the build on 26
         * November, and the flat field with a stone wall across it that they photographed beside it. So the wall is
         * read from the nearest column the cut actually fills, along the row first and then along the column: the
         * join is measured from the build a player can see, not from the tile the build was cut in.
         */
        private int[] held(int x, int z) {
            int cx = Mth.clamp(x, box.lowX(), box.highX());
            int cz = Mth.clamp(z, box.lowZ(), box.highZ());
            if (outline[(cx - box.lowX()) + (cz - box.lowZ()) * wide] != Integer.MIN_VALUE) {
                return new int[]{cx, cz};
            }
            for (int step = 1; step <= HEM_HELD; step++) {
                for (int way = -1; way <= 1; way += 2) {
                    int nx = cx + way * step;
                    if (nx >= box.lowX() && nx <= box.highX()
                            && outline[(nx - box.lowX()) + (cz - box.lowZ()) * wide] != Integer.MIN_VALUE) {
                        return new int[]{nx, cz};
                    }
                }
                for (int way = -1; way <= 1; way += 2) {
                    int nz = cz + way * step;
                    if (nz >= box.lowZ() && nz <= box.highZ()
                            && outline[(cx - box.lowX()) + (nz - box.lowZ()) * wide] != Integer.MIN_VALUE) {
                        return new int[]{cx, nz};
                    }
                }
            }
            return null;
        }

        /** One column of the ring, carried to the shape of the nearest column of the build's own edge. */
        private void join(ServerLevel level, int x, int z) {
            int[] held = held(x, z);
            if (held == null) {
                // No piece of the build in this row or column: there is no shape to join to, so it is left.
                skipped++;
                return;
            }
            int hx = held[0];
            int hz = held[1];
            int gx = hx - box.lowX();
            int gz = hz - box.lowZ();
            int at = gx + gz * wide;
            int wall = outline[at];
            int outX = Math.abs(x - hx);
            int outZ = Math.abs(z - hz);
            // Whether the shape is this column's own wall rather than one looked for past an empty tile column. A
            // fallback wall is the build's shape but not the column's, and the hill is only ever planted over the
            // build itself: water at a column that had to look past its own wall is the drawing's and stays.
            boolean own = hx == Mth.clamp(x, box.lowX(), box.highX()) && hz == Mth.clamp(z, box.lowZ(), box.highZ());
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
            int surface = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z),
                    level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)) - 1;
            if (surface <= level.getMinY()) {
                skipped++;
                return;
            }
            if (channel[at]) {
                // An outflow is water the build holds up over the country in front of it. A pond at the build's own
                // edge is not: its water already stands at the level the ground beside it stands at, and it leaves
                // where it has always left. Every column of the Shire's pond is within OUTLET_LIFT of the box's base -
                // the village's floor is sixteen over it and the pond nine - so the pond answered as the city's
                // outflow along the whole of its own edge, and the drape cut a mouth at every column of it: a channel
                // three deep, filled to the pond's level, running out across the country. That is the water the owner
                // photographed threading through the village's fields as a set of channels on 26 November. The test is
                // the water standing over the country in front of it, which is what a held-up spring is and what a
                // pond is not.
                if (watered[at] > surface && watered[at] - (box.lowY() - 1) <= CityCommands.OUTLET_LIFT) {
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
            // A column standing in the drawing's water is the drawing's, and dressing it carves the river. A height that
            // counts fluids answers with the water's own surface, so the ground search below walked under the sheet to
            // the bed, and the column was then cut and filled against the build's outline like any land: where the ring
            // crossed a river the water came out down to the cap, the bed was laid back over the hole and the bank was
            // filled with stone - a trench walled with stone running through the river, three blocks deep. So a column
            // with water as its top, or in the block over its top, is left exactly as it stands; `Drape` has always kept
            // one so (`wet`), and the join at a river is the river. A spring the build itself holds up is the build's
            // water and is not this case: that column is dressed, and the mountain over its pool comes off above it.
            //
            // The spring's exemption is gone, because a spring is not always the build's water. The ring reads its
            // shape from the nearest wall column, and where the drawing's river runs along the footprint's edge that
            // column's water is the river's own: every ring column beside it answered `channel`, with the wall's water
            // ten blocks over the village floor - past `OUTLET_LIFT` - so it read as a spring and was dressed anyway,
            // cut down to the pool's level and capped with the ground beneath it. That is the water taken out of the
            // river and a bank laid over the hole, which is the trench, surviving the guard the trench was meant to
            // meet. The build's pool needs no exemption: it is inside the walls, and this pass never cuts inside as
            // ring. A column outside that is itself water is the drawing's water whoever holds it up.
            if (water(level, x, surface, z)) {
                int bed = stray(x, z) ? drain(level, x, z, surface) : -1;
                if (bed > 0) {
                    surface = bed;
                } else if (!own || !mountain(outX, outZ, at)) {
                    // No mountain here: the water is the drawing's and the column is left exactly as it stands. A
                    // mountain is another matter - the owner's own answer is that it takes that stretch of river with
                    // it - so the column is carried on to the fill, which lays ground over the sheet.
                    skipped++;
                    return;
                }
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
            double share = Math.min(1.0D, Math.hypot(outX, outZ) / (double) CityCommands.DRAPE_REACH);
            // A shoulder is a slope, and a slope is straight. Easing the join - the smoothstep this used to be - held
            // the ring at the build's own height for the first four or five blocks out and then dropped the rest in the
            // middle of the reach: a flat plate of fill at the build's height with a cliff at its outer edge, which is
            // the "little bit of raised terrain" the owner photographed on 26 November beside the Shire's own hill, and
            // the squares before it - a straight build edge makes a straight plate and a corner makes a square one. The
            // same twenty blocks read this way is the same join with nothing to stand on and nothing to fall off: one
            // block down for every one or two out, all the way to the country.
            int ridge = mound(outX, outZ, at, top);
            boolean onFlank = ridge != Integer.MIN_VALUE && ridge != top;
            // On the mountain's own flank the mountain's line is the join, not a floor under the ring's: it is the same
            // shape rising to the crest and the same shape cutting the country down to it.
            int cap = onFlank ? ridge : (int) Math.round(wall + (top - wall) * share);
            if (onFlank) mounted++;
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
            // A fill is a shoulder and a shoulder is a slope: it never stands more than one block over its own
            // distance from water. Without this the ease runs out at the shoreline - water is not dressed, so nothing
            // carries the ground down to it - and the shoulder reads as a quarry face standing beside a pond.
            if (top < cap) {
                int to = shore(x, z);
                if (to > 0) {
                    int line = shoreLevel(x, z) + to - 1;
                    if (line <= top) {
                        // The shore line is at or under the ground the column already stands on, so the bank there is
                        // the country's own and a fill over it would be the face again. Left exactly as it stands.
                        skipped++;
                        return;
                    }
                    cap = Math.min(cap, line);
                }
            }
            int down = spring ? Math.max(cap, watered[at]) : cap;
            // A join that takes or lays a single block is a lip and not a join. The falloff lands a block over the
            // country somewhere along every seam - at the build's own edge, and at the foot of the mountain where its
            // reach meets the ground - and a one-block rim round the dressed ring is what the owner photographed as a
            // lip on 26 November. The column is left exactly as it stands, so the join begins where it is two blocks
            // and ends a step short of the country: a step of two is a shoulder, a step of one is a lip to walk over.
            if (Math.abs(down - top) == 1) {
                skipped++;
                return;
            }
            // A bank is measured against the build's own ground at the wall, not against its deck. The deck is the
            // village's floor, and the country along its north edge stood four blocks over the edge the village
            // actually draws there - under the deck, so the deck's rule called it a groove and left a four-block
            // face round the whole side. Three over the build's own top is a face wherever the build's top is.
            boolean bank = !spring && top - wall >= BANK_BLOCKS;
            if (top > down && (!banked || spring || bank)) {
                // A cut beside water is the water's too. The sheet itself is left exactly as it stands, so a column
                // cut below the level beside it leaves the drawing's water standing on a face - the walls of water
                // the owner photographed on 26 November, where the ring went through a pool instead of round it -
                // and the cut makes a bowl beside the lake that reads as a hole dug round the build. It is not only
                // the three blocks beside the sheet: water spreads, so any column the ring cuts under the level of
                // the nearest water leaves a way in for it, and the three-block guard was thirteen blocks too short
                // for the lakes on the village's north and west sides. The cut stops at the water's own surface
                // wherever water is in the ring, and a column the water already holds up is left as it stands.
                int beside = shore(x, z);
                if (beside > 0) {
                    down = Math.max(down, shoreLevel(x, z));
                }
                if (top <= down) {
                    skipped++;
                    return;
                }
                for (int y = surface; y > down; y--) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, down, z), skin, 2);
                cut++;
            } else if (top < cap) {
                // A shoulder is ground, so its face is the country's own shape: the same band of dirt under the grass
                // that every drawn riser shows, and stone only under that. Rock to the surface made a fill read as a
                // quarry face however gently it was sloped, because every one-block step of it exposed stone.
                int soil = soft(skin) ? Math.min(SOIL_BLOCKS, cap - 1 - top) : 0;
                for (int y = top + 1; y < cap; y++) {
                    level.setBlock(new BlockPos(x, y, z),
                            y > cap - 1 - soil ? Blocks.DIRT.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, cap, z), skin, 2);
                filled++;
            }
        }

        /**
         * Whether a column of the ring is carried under the mountain: the edge it joins stands tall enough to be
         * worth burying, and the column is inside the mountain's own reach. The city's outflow is not exempt - the
         * owner's answer is that the hill takes it, so the river ends under the mountain rather than in a walled pond.
         */
        private boolean mountain(int outX, int outZ, int at) {
            if (!tall(at)) return false;
            return Math.hypot(outX, outZ) < HEM_REACH;
        }

        /** Whether the build's own edge stands tall here: the high points a mountain is planted round. */
        private boolean tall(int at) {
            return outline[at] != Integer.MIN_VALUE && outline[at] - (origin.getY() + deck) >= HEM_TALL;
        }

        /** The nearest column of the build's own edge to a column inside it, as an index into the outline. */
        private int edge(int x, int z) {
            int nx = x;
            int nz = z;
            int edgeX = Math.min(x - box.lowX(), box.highX() - x);
            int edgeZ = Math.min(z - box.lowZ(), box.highZ() - z);
            if (edgeX < edgeZ) {
                nx = x - box.lowX() <= box.highX() - x ? box.lowX() : box.highX();
            } else {
                nz = z - box.lowZ() <= box.highZ() - z ? box.lowZ() : box.highZ();
            }
            return (nx - box.lowX()) + (nz - box.lowZ()) * wide;
        }

        /**
         * The mountain's own top over a column inside the crest, or nought where no mountain is planted there.
         * <p>
         * The crest stands {@link #HEM_INSET} blocks inside the build's own edge and {@link #HEM_CREST} over the top of
         * the build there, so the tall outer wall is carried under the hill rather than left standing as the face the
         * owner photographed on 26 November. Only where the edge stands {@link #HEM_TALL} blocks over the deck: a
         * garden, a field or a low front is the village's and is not buried.
         */
        private int crest(int x, int z, int at) {
            int edgeX = Math.min(x - box.lowX(), box.highX() - x);
            int edgeZ = Math.min(z - box.lowZ(), box.highZ() - z);
            if (Math.min(edgeX, edgeZ) > HEM_INSET) return 0;
            if (channel[at]) return 0;
            int eat = edge(x, z);
            if (!tall(eat)) return 0;
            return outline[eat] + HEM_CREST - HEM_DROP;
        }

        /**
         * The mountain's own top over a column of the ring, or {@link Integer#MIN_VALUE} where none is planted.
         * <p>
         * The crest is the same height the crest inside the edge is, so the hill runs through the build's own edge
         * without a step, and it falls to the country the column already stands at over {@link #HEM_REACH} blocks: a
         * smoothstep, so the foot of the mountain is flat where it meets the ground and the mountain is the country's
         * own slope and not a plate of fill with a face at its end.
         */
        private int mound(int outX, int outZ, int at, int top) {
            if (!tall(at)) return Integer.MIN_VALUE;
            double out = Math.hypot(outX, outZ);
            if (out >= HEM_REACH) return Integer.MIN_VALUE;
            int crest = outline[at] + HEM_CREST - HEM_DROP;
            double t = out / (double) HEM_REACH;
            double ease = t * t * (3.0D - 2.0D * t);
            int line = top + (int) Math.round((crest - top) * (1.0D - ease));
            // Sign matters: where the country already stands over the crest the same line is below its own ground, and
            // the ring's cut takes the difference off. A column that only ever answered with a fill could not do that,
            // and the country beside the build stayed a wall the hill had no way to reach - the cuts the owner found at
            // 856,118 and the corner where the drape looked like it was not reaching at all.
            return line;
        }

        /**
         * Whether a pool at this column is a stray one, standing within {@link #EDGE_WATER} blocks of the build's own
         * edge and away from the river the cut carries through the city.
         * <p>
         * The distance is measured to the box, so a column three blocks inside the edge and one three blocks outside it
         * are both asked. A pool within the same distance of a column the cut fills with water is the river's own - the
         * mouth and its approach - and is left exactly as it stands: draining that would take the river out of its bed
         * to tidy a bank.
         */
        private boolean stray(int x, int z) {
            if (channel == null) return false;
            int away = Math.max(Math.max(box.lowX() - x, x - box.highX()),
                    Math.max(box.lowZ() - z, z - box.highZ()));
            if (Math.abs(away) > EDGE_WATER) return false;
            int gx = x - box.lowX();
            int gz = z - box.lowZ();
            for (int dz = -EDGE_WATER; dz <= EDGE_WATER; dz++) {
                for (int dx = -EDGE_WATER; dx <= EDGE_WATER; dx++) {
                    int nx = gx + dx;
                    int nz = gz + dz;
                    if (nx < 0 || nx >= wide || nz < 0 || nz >= deep) continue;
                    if (channel[nx + nz * wide]) return false;
                }
            }
            return true;
        }

        /**
         * Takes a stray pool out of a column: the sheet over its ground, and the water in it, become dirt, and the
         * column answers with the height of the bed it stands on.
         * <p>
         * What is left is ground, so the drape's own join then cuts or fills the column as it would any other: the pool
         * is not hidden under a fill but replaced by the shoulder the join was always meant to have there. Nothing over
         * the column is touched, so a build's own blocks are not read as bed.
         *
         * @return the bed's height, or nought when the column holds no ground under its water
         */
        private int drain(ServerLevel level, int x, int z, int surface) {
            int floor = Math.max(level.getMinY(), surface - COLUMN_DEPTH);
            for (int y = surface + 1; y <= surface + 2; y++) {
                if (!level.getBlockState(new BlockPos(x, y, z)).getFluidState().isEmpty()) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                }
            }
            int bed = 0;
            for (int y = surface; y > floor; y--) {
                BlockState state = level.getBlockState(new BlockPos(x, y, z));
                if (state.getFluidState().isEmpty()) {
                    if (state.isAir()) continue;
                    bed = y;
                    break;
                }
                level.setBlock(new BlockPos(x, y, z), Blocks.DIRT.defaultBlockState(), 2);
                if (bed == 0) bed = y;
            }
            return bed;
        }

        /**
         * Whether a column's top, or the block over it, is a fluid: the drawing's water, which the drape never dresses.
         * <p>
         * Both blocks, because the answer must not depend on whether the height map counted the fluid: a column under a
         * sheet has water for its top when it does, and water in the block over its ground when it does not.
         */
        private static boolean water(ServerLevel level, int x, int surface, int z) {
            return !level.getBlockState(new BlockPos(x, surface, z)).getFluidState().isEmpty()
                    || !level.getBlockState(new BlockPos(x, surface + 1, z)).getFluidState().isEmpty();
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
         * ring's shallow grooves are left as grooves here too, and the water is left where it is. A bank of
         * {@link #APRON_CAP} blocks or less is graded back to the cut over {@link #APRON_WIDTH} rather than left as a
         * face - see {@link #apron} - so a field plate's edge is a slope and not a wall.
         */
        private void dressInside(ServerLevel level, int x, int z) {
            int gx = x - box.lowX();
            int gz = z - box.lowZ();
            int at = gx + gz * wide;
            // The build's own top here with its trees counted: inside the walls a drawn tree is the village, and the
            // ring's outline - which leaves canopies out so the ring does not stand a wall beside every tree - would
            // read a party tree's crown as the country standing five blocks over the village.
            int roof = whole[at];
            if (roof == Integer.MIN_VALUE) {
                // A column the cut's tiles leave empty is not the city at all: it is the country the tile was cut
                // over. With a ring round the build it is dressed by the ring's own join, read from the nearest
                // column of the build; with no ring it gets the bank at the foot of the build's own ground instead,
                // because the join eases that ground over sixty blocks and inside the footprint that is a plate of
                // fill where the country should still be the country.
                if (reach > 0) join(level, x, z);
                else skirt(level, x, z);
                return;
            }
            // The mountain is the ring's outermost ring, and a village with no ring round it is the cut's own blocks
            // and nothing written over them: a bare build that had its outer four columns carried under a hill the
            // ring never planted would be the mountain arriving without the mountain.
            int buried = reach > 0 ? crest(x, z, at) : 0;
            if (buried > roof) {
                // The mountain is planted over the build's own outer four blocks, and the column's country - which the
                // cut below would have taken off down to the roofline - is the mountain's own flank here.
                for (int y = roof + 1; y < buried; y++) {
                    level.setBlock(new BlockPos(x, y, z),
                            y > buried - 1 - SOIL_BLOCKS ? Blocks.DIRT.defaultBlockState()
                                    : Blocks.STONE.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, buried, z), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                covered++;
                return;
            }
            // The country is measured against the build's own ground - the outline, with its trees left out - and not
            // against the top of a drawn tree. A crown stands ten or twelve blocks over the ground it grows in, and
            // every column under one held its country at the crown's own height, so the country stayed twelve blocks
            // over a village floor graded beside it: the standing wall the owner photographed on 26 November with the
            // trees still on top of it, one wall per grove. Where the cut draws no ground under a crown - a tree
            // reaching out past the build's own edge - the roof is the answer, as it always was.
            int ground = outline[at];
            int base = ground == Integer.MIN_VALUE ? roof : ground;
            // The country is measured by its ground and not by the tree standing on it. A crown is the highest block
            // in a column, so a height that counts leaves answers with the crown - and a column under a canopy then
            // measured its tree's height over the cut's own ground, which is a bank past APRON_CAP, and was cut
            // straight down to that ground. That is a pit in the shape of the crown, with the country beside it left
            // standing in the shape of everything between the crowns: the chunks with dirt and stone pillars shaped
            // like the trees above them that the owner photographed on 26 November. The column's own ground is read
            // here instead, down through whatever stands on it, and the clear below passes over leaves and wood so a
            // crown is left on the ground that stays under it.
            int sky = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z),
                    level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z)) - 1;
            if (sky > level.getMinY()
                    && !level.getBlockState(new BlockPos(x, sky, z)).getFluidState().isEmpty()) {
                // Water over the column is still water: a stray pool is drained to its bed, as it always was.
                int bed = stray(x, z) ? drain(level, x, z, sky) : -1;
                if (bed < 0) return;
            }
            int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
            if (surface <= level.getMinY()) return;
            int floor = Math.max(level.getMinY(), surface - COLUMN_DEPTH);
            BlockState skin = level.getBlockState(new BlockPos(x, surface, z));
            while (surface > floor && !ground(skin)) {
                skin = level.getBlockState(new BlockPos(x, --surface, z));
            }
            if (!ground(skin)) return;
            // The band at the cut's own edge is the clear's, and it is graded there: a bank of the country left
            // standing at the footprint's line and rolled down into the build over CutCommands.RIM_REACH. Cutting that
            // bank a second time here would take it off at the apron's four blocks and put the face back exactly where
            // the clear had taken it out - the join would be the ring's rule fighting the cut's.
            if (CutCommands.rimDistance(gx, gz, wide, deep) < CutCommands.RIM_REACH) return;
            if (surface - base < BANK_BLOCKS) return;
            int down = apron(level, x, z, base, surface);
            if (down >= surface) return;
            // What comes off is the country, and the village's own trees stay: a column can be country all the way up
            // under a crown, and clearing it from the top would take the crown with it - which is how a party tree came
            // up as sawn-off poles. Leaves and wood are passed over and the country between them is taken out.
            for (int y = surface; y > down; y--) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(pos);
                if (state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)
                        || tree(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString())) continue;
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
            // A graded cut is a hillside rather than a face, so its top course is the country's own grass - the same
            // block the ring lays on a fill - and not the bare dirt the cut opened.
            if (down > base && level.getBlockState(new BlockPos(x, down, z)).isSolid()) {
                level.setBlock(new BlockPos(x, down, z), soft(skin) ? skin : Blocks.GRASS_BLOCK.defaultBlockState(), 2);
            }
            inside++;
        }

        /**
         * One column inside the footprint the cut draws nothing in, at the foot of the build's own ground: the country
         * graded up to that ground over a few blocks, so the plate's edge is a bank rather than a wall one column wide.
         * <p>
         * The bank follows the country's own top rather than a plane laid through the box, so its foot wanders with the
         * land; its reach wanders a column at a time as well, so no two neighbouring columns end their bank at the same
         * distance out. The top course is the country's own skin and the fill under it is the country's own band of
         * dirt, which is what makes a one-block step read as ground rather than as a wall built of stone.
         * <p>
         * Left exactly as it stands: a column with no build within the bank's reach, an edge taller than
         * {@link #SKIRT_RISE} (the build's own hill, which a fill would bury), a one- or two-block step, and water.
         */
        private void skirt(ServerLevel level, int x, int z) {
            int[] near = held(x, z);
            if (near == null) {
                skipped++;
                return;
            }
            int out = Math.max(Math.abs(x - near[0]), Math.abs(z - near[1]));
            int reach = SKIRT_REACH + CutCommands.wander(x, z);
            if (out <= 0 || out > reach) {
                skipped++;
                return;
            }
            int at = (near[0] - box.lowX()) + (near[1] - box.lowZ()) * wide;
            int wall = outline[at];
            if (wall == Integer.MIN_VALUE) {
                skipped++;
                return;
            }
            int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
            if (surface <= level.getMinY()) {
                skipped++;
                return;
            }
            if (!level.getBlockState(new BlockPos(x, surface, z)).getFluidState().isEmpty()) {
                // Water is left exactly where it stands, as the ring leaves it: a bank laid over a sheet fills it.
                skipped++;
                return;
            }
            int floor = Math.max(level.getMinY(), surface - COLUMN_DEPTH);
            int top = surface;
            BlockState skin = level.getBlockState(new BlockPos(x, top, z));
            while (top > floor && !ground(skin)) {
                skin = level.getBlockState(new BlockPos(x, --top, z));
            }
            if (!ground(skin)) {
                skipped++;
                return;
            }
            // A bank is laid against a field, a lane or a gravel bed and never against a wall: the top of the build's
            // column has to be ground, or the fill buries the foot of the very thing the join is for. The same test
            // the apron makes, for the same reason.
            if (!earth(level.getBlockState(new BlockPos(near[0], wall, near[1])))) {
                skipped++;
                return;
            }
            int rise = wall - top;
            if (rise < BANK_BLOCKS || rise > SKIRT_RISE) {
                skipped++;
                return;
            }
            int cap = Math.min(wall, top + (int) Math.round(rise * (reach - out) / (double) (reach - 1)));
            if (cap <= top) {
                skipped++;
                return;
            }
            int soil = soft(skin) ? Math.min(SOIL_BLOCKS, cap - top) : 0;
            for (int y = top + 1; y < cap; y++) {
                level.setBlock(new BlockPos(x, y, z),
                        y > cap - 1 - soil ? Blocks.DIRT.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
            }
            level.setBlock(new BlockPos(x, cap, z), skin, 2);
            skirted++;
        }

        /** The eight ways out from a column in unit steps, so a bank running diagonally to its plate is measured too. */
        private static final int[][] WAYS = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        /**
         * How far down the country is cut in a column: the cut's own top, unless the column is the edge of a bank that
         * can be graded.
         * <p>
         * A bank of {@link #APRON_CAP} blocks or less standing over the cut's own ground is cut in quarters of its
         * height rather than all at once - one quarter at the first block out from the plate, all of it at the
         * {@link #APRON_WIDTH}th - so the join it leaves is a slope and not a face. A taller bank is the country
         * outside the build and is cut exactly as it was, since the ring's own join is what dresses that, and a column
         * whose top course is a wall, a roof or a beam is refused the grading, because a slope laid against one buries
         * its foot.
         *
         * @return the block the column is cut down to, which is the country's own top when nothing is cut
         */
        private int apron(ServerLevel level, int x, int z, int roof, int surface) {
            int tall = surface - roof;
            if (tall > APRON_CAP) return roof;
            if (!earth(level.getBlockState(new BlockPos(x, roof, z)))) return roof;
            int out = shoulder(level, x, z, roof);
            if (out <= 0) return roof;
            return Math.min(surface, roof + (tall * out + APRON_WIDTH - 1) / APRON_WIDTH);
        }

        /**
         * How many blocks out the cut's own shoulder stands, or nought when there is none within the apron.
         * <p>
         * A shoulder is a column the cut has not left the country standing over - its own country lies within a bank
         * of its top - which is the plate a bank beside it falls to. The nearest shoulder in the eight ways is the
         * distance the grading is measured from, so a column deep inside a filled bank finds none and is cut as it
         * always was.
         */
        private int shoulder(ServerLevel level, int x, int z, int roof) {
            for (int ring = 1; ring <= APRON_WIDTH; ring++) {
                for (int[] way : WAYS) {
                    int nx = x + way[0] * ring;
                    int nz = z + way[1] * ring;
                    int gx = nx - box.lowX();
                    int gz = nz - box.lowZ();
                    if (gx < 0 || gx >= wide || gz < 0 || gz >= deep) continue;
                    int there = whole[gx + gz * wide];
                    if (there == Integer.MIN_VALUE) continue;
                    int top = Math.max(level.getHeight(Heightmap.Types.MOTION_BLOCKING, nx, nz),
                            level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, nx, nz)) - 1;
                    if (top - there < BANK_BLOCKS) return ring;
                }
            }
            return 0;
        }

        /**
         * Whether a top is the country's own ground rather than the build's, so a bank may be graded down from it.
         * <p>
         * Soil and the rock and sand under it: a grass field, a path laid in the cut, a gravel bed or a stone shoulder
         * are all ground a slope can meet. A plank, a beam, a slab or a wall is not, and a bank left against one buries
         * the foot of it.
         */
        private static boolean earth(BlockState state) {
            return soft(state) || state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)
                    || state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY)
                    || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.MUD) || state.is(Blocks.PACKED_MUD)
                    || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.DIRT_PATH) || state.is(Blocks.FARMLAND);
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

        /**
         * Puts the cut's own grass back on every column of the cut that has been worn to plain dirt.
         * <p>
         * Only the one case, and it is the case a build's own feet make: the cut draws grass on the column's top, the
         * world holds plain dirt at that same height, and nothing solid or wet stands over it. A floor laid at the
         * cut's height is another block and is left, a garden the cut draws as dirt is not grass and is left, and a
         * hole under the cut is a hole - the drape's own rule is that it never fills over the build.
         *
         * @return how many columns took their grass back
         */
        private int heal(ServerLevel level) {
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            int healed = 0;
            for (int gz = 0; gz < deep; gz++) {
                for (int gx = 0; gx < wide; gx++) {
                    int x = box.lowX() + gx;
                    int z = box.lowZ() + gz;
                    int at = gx + gz * wide;
                    int top = crownY[at];
                    if (top == Integer.MIN_VALUE) continue;
                    if (!level.isLoaded(cursor.set(x, top, z))) continue;
                    BlockState worn = level.getBlockState(cursor);
                    if (!worn.is(Blocks.DIRT) && !worn.is(Blocks.COARSE_DIRT)) continue;
                    BlockState over = level.getBlockState(cursor.set(x, top + 1, z));
                    if (over.isSolid() || !over.getFluidState().isEmpty()) continue;
                    level.setBlock(cursor.set(x, top, z), Blocks.GRASS_BLOCK.defaultBlockState(),
                            Block.UPDATE_CLIENTS);
                    healed++;
                }
            }
            return healed;
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
