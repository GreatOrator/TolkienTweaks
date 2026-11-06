package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
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
 * It begins when the city is whole and not before. A corner is written down when the first band is planted, and
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
    /** How many pieces of the cut one tick reads while the outline is being taken. */
    private static final int PIECES_PER_TICK = 4;
    /** How many chunks of ring one tick dresses. */
    private static final int CHUNKS_PER_TICK = 2;
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
        if (job == null) {
            // Looking for a city is a table read, so it happens once a second; working on one happens every tick,
            // because a step is what the whole job is made of.
            if (event.getServer().getTickCount() % CHECK_EVERY != 0) return;
            job = start(level);
            if (job == null) return;
        }
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
        Job whole = new Job("", folder, parts, origin, box, false, false);
        while (whole.piece < parts.size()) whole.takePieces();
        whole.grow();
        while (whole.chunk < whole.ring.size()) whole.dressChunks(level);
        return new CityCommands.Drape(whole.cut, whole.filled, whole.open);
    }

    /** A job for a city this world has stood and not yet dressed, or null when there is nothing to do. */
    private static Job start(ServerLevel level) {
        BlockPos corner = CityCommands.stoodCity(level);
        if (corner == null) return null;
        String key = level.dimension().identifier() + "@" + corner.toShortString();
        if (key.equals(finished)) return null;
        Path folder = CityCommands.folder();
        try {
            List<CityCommands.Part> parts = CityCommands.parts(folder);
            if (parts.isEmpty()) return null;
            boolean[] done = dressed(folder, level, corner);
            if (done[0] && done[1]) {
                finished = key;
                return null;
            }
            return new Job(key, folder, parts, corner, CityCommands.Box.of(parts, corner), done[0], done[1]);
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
    private static final class Job {
        /** This job's city, as dimension and corner: what {@code _dressed.txt} is keyed on. */
        private final String key;
        private final Path folder;
        private final List<CityCommands.Part> parts;
        private final BlockPos origin;
        private final CityCommands.Box box;
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
        private int cut;
        private int filled;
        private int open;

        private Job(String key, Path folder, List<CityCommands.Part> parts, BlockPos origin,
                    CityCommands.Box box, boolean dressed, boolean road) {
            this.key = key;
            this.folder = folder;
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
        private boolean step(ServerLevel level) {
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
                LOGGER.info("Rivendell: the ground is dressed - {} columns cut down, {} filled up, {} left for the water",
                        cut, filled, open);
                write(level, true, road);
            }
            if (!road) {
                apply(level);
                if (!road) return true;
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
                    tag = CityCommands.raw(folder, part.name());
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
            if (x >= box.lowX() && x <= box.highX() && z >= box.lowZ() && z <= box.highZ()) return;
            int gx = Mth.clamp(x, box.lowX(), box.highX()) - box.lowX();
            int gz = Mth.clamp(z, box.lowZ(), box.highZ()) - box.lowZ();
            int at = gx + gz * wide;
            int wall = outline[at];
            if (wall == Integer.MIN_VALUE) return;
            boolean banked = wall <= CityCommands.DECK_TOP;
            if (banked) {
                // A deck is a floor tile rather than a roofline, so the ground outside meets the rock standing over
                // that deck inside the box and is never cut down to the deck itself.
                wall = Math.max(wall, level.getHeight(Heightmap.Types.MOTION_BLOCKING,
                        gx + box.lowX(), gz + box.lowZ()) - 1);
            }
            if (channel[at]) {
                if (watered[at] - (box.lowY() - 1) <= CityCommands.OUTLET_LIFT) {
                    // The city's outflow: the branch takes the water out at the floor, so the mouth stays open.
                    open++;
                    return;
                }
                banked = true;
            }
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
            if (top <= level.getMinY()) return;
            int surface = top;
            BlockState skin = level.getBlockState(new BlockPos(x, top, z));
            if (!skin.getFluidState().isEmpty()) {
                // Water out in the ring is a pool lying on the drawn ground, so the pass works down to the ground
                // under it rather than leaving a pillar of ground standing wherever a puddle reached the top.
                while (top > level.getMinY() + 1
                        && !level.getBlockState(new BlockPos(x, top, z)).getFluidState().isEmpty()) {
                    top--;
                }
                skin = level.getBlockState(new BlockPos(x, top, z));
                if (!skin.getFluidState().isEmpty()) return;
            }
            int outX = Math.max(0, Math.max(box.lowX() - x, x - box.highX()));
            int outZ = Math.max(0, Math.max(box.lowZ() - z, z - box.highZ()));
            double share = Math.min(1.0D, Math.hypot(outX, outZ) / (double) CityCommands.DRAPE_REACH);
            double ease = share * share * (3.0D - 2.0D * share);
            int cap = (int) Math.round(wall + (top - wall) * ease);
            if (top > cap && !banked) {
                for (int y = surface; y > cap; y--) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, cap, z), skin, 2);
                cut++;
            } else if (top < cap) {
                for (int y = top + 1; y < cap; y++) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState(), 2);
                }
                level.setBlock(new BlockPos(x, cap, z),
                        skin.isSolid() ? skin : Blocks.STONE.defaultBlockState(), 2);
                filled++;
            }
        }

        /** One step of the road: the hand's own pass, taken a stage at a time so no tick carries it whole. */
        private void apply(ServerLevel level) {
            try {
                if (laying == null) laying = RoadCommands.plan();
                if (laying.step(level)) return;
                RoadCommands.Road laid = laying.summary();
                road = true;
                LOGGER.info("Rivendell: the road is down - bridge of {} pieces, approach {} blocks, {} columns laid",
                        laid.pieces(), laid.along(), laid.graded()[0] + laid.far()[0]);
            } catch (IOException unreadable) {
                road = true;
                LOGGER.warn("Rivendell: the road could not be built: {}", unreadable.getMessage());
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
