package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Keeps a generated road walkable: clears its corridor when the chunk holding it loads.
 * <p>
 * A road is painted while the ground is dressed, and trees and grass are placed after that, so a strip through wood
 * arrives as a line of road blocks with a forest standing in it. There is no place in the generation pass to answer
 * for that - the features of one chunk spill into its neighbours, and which chunk decorates last depends on the order
 * the world happens to load them. The load of a finished chunk is the first moment the road and everything planted
 * over it are both there, so the corridor is cleared here instead: leaves, logs, vines and replaceable plants above the
 * road's own surface, and the road's block written back where a trunk had taken it.
 * <p>
 * This runs on every load of a chunk with a road in it, and it is safe to run again: a corridor already clear has
 * nothing to take away and writes nothing. It only answers for Arda, since a road's waypoints are Arda's blocks and a
 * world with the same coordinates is not the world the road was walked in.
 * <p>
 * The load of a chunk is also where a graded road begins. A road used to be planned only when the build it left from
 * was standing and dressed, so the crossing out of Rivendell led away across the ford and stopped there until a player
 * happened to find Elladan's camp - the road existed in two halves, and the second began wherever the second build
 * was. A chunk that a road passes through now starts that road, whichever end of it the player comes in at, and the
 * pass is carried on by the tick below for as long as the ground its head stands on is loaded: a road resumed by
 * walking away, and resumed again by coming back, with nothing to type and no build to find first.
 * <p>
 * The plaza is cleared here too, for the same reason a road's corridor is: it is drawn before the wood grows.
 * <p>
 * A city's crossing is not begun here. A bridge behind a build is the city's own business - it needs the span
 * assembled and the city's ground ready for its abutments - so that crossing stays with the dressing. A crossing on a
 * named road has no city to wait for, and starts from the ground it crosses like every other section.
 */
@EventBusSubscriber(modid = TolkienMobsMain.MODID)
public final class RoadCorridor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoadCorridor.class);

    private RoadCorridor() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(TolkienDimensions.ARDA)) return;
        if (!RoadPaint.LINES.isEmpty()) RoadPaint.clear(level, event.getChunk());
        Plaza.clear(level, event.getChunk());
        start(level, event.getChunk());
    }

    /** Starts every dry road this chunk lies on, which is how a road begins wherever it is first met. */
    private static void start(ServerLevel level, LevelChunk chunk) {
        int lowX = chunk.getPos().getMinBlockX();
        int lowZ = chunk.getPos().getMinBlockZ();
        for (Route route : Roads.ROUTES) {
            if (!mayBegin(level, route)) continue;
            if (!touches(route, lowX, lowZ)) continue;
            RoadCommands.pass(level, route, lowX + 8, lowZ + 8);
        }
    }

    /**
     * Whether the world may begin this section from the ground it crosses.
     * <p>
     * A bridge behind a build is the build's own business, so a crossing is only begun here when it belongs to a named
     * road. A road planned at a build this session is begun by the command that planned it and carried on by the note
     * that command leaves: a plan with no note is a road waiting to be asked for, and laying it because a chunk of its
     * ground came into view is how a road planned twice was laid twice - the old forest road planned in the mountains
     * stood beside its own first attempt, section for section. A road the code names is not a plan; it is part of the
     * world like any other, and the ground it crosses begins it.
     */
    private static boolean mayBegin(ServerLevel level, Route route) {
        if (route instanceof Route.Crossing && !Roads.road(route)) return false;
        // A road of a build's own waits for the build: a city is laid a band at a time and its ground is dressed after
        // the last of them, so a road begun before that grades country the dressing then changes under it. The ground
        // is asked again every second by {@link #look}, so the road begins by itself the moment the build is finished.
        if (Roads.waits(level, route)) return false;
        return !Roads.planned(route) || RoadCommands.begun(level, route);
    }

    /**
     * Whether a chunk lies close enough to a road to have begun it.
     * <p>
     * The test is against the blocks the road is recorded as passing through - its own ends and the bends walked on
     * the ground - rather than the line a search will eventually take, because the search needs the ground that this
     * very chunk is bringing in. A wide margin covers the wander a search makes between two recorded points, and the
     * cost of being early is only a pass that waits for its own head to be loaded.
     */
    private static boolean touches(Route route, int lowX, int lowZ) {
        // The chunk's own middle, held to the road: a chunk is begun by the road passing near it.
        return near(route, lowX + 8, lowZ + 8, TOUCH);
    }

    /** Whether a point stands within a distance of the blocks a road is recorded as passing through. */
    private static boolean near(Route route, int x, int z, int reach) {
        List<int[]> marks = new java.util.ArrayList<>();
        marks.add(new int[]{route.pathX(), route.pathZ()});
        for (BlockPos bend : Roads.bends(route)) marks.add(new int[]{bend.getX(), bend.getZ()});
        if (route instanceof Route.Riverless dry) marks.add(new int[]{dry.farX(), dry.farZ()});
        if (route instanceof Route.Crossing span) marks.add(new int[]{span.farX(), span.farZ()});
        for (int index = 1; index < marks.size(); index++) {
            int[] from = marks.get(index - 1);
            int[] to = marks.get(index);
            double dx = to[0] - from[0];
            double dz = to[1] - from[1];
            double length = dx * dx + dz * dz;
            double along = length == 0.0D ? 0.0D
                    : Math.clamp(((x - from[0]) * dx + (z - from[1]) * dz) / length, 0.0D, 1.0D);
            double away = Math.hypot(x - (from[0] + dx * along), z - (from[1] + dz * along));
            if (away <= reach) return true;
        }
        return false;
    }

    /**
     * How far from the blocks a road is recorded as passing through a chunk still counts as having begun it: two
     * chunks either way, which is the wander a search can take between two recorded points and still be the road.
     */
    private static final int TOUCH = 32;

    /**
     * Carries every road in hand along while the ground under its head is loaded.
     * <p>
     * A block written into a chunk nobody has made is quietly dropped, so a pass only steps while its head's chunk is
     * loaded - which is the same rule the world's own buildings are raised under, and the reason a road fills in as
     * the player walks it. A pass whose head is not loaded waits where it stands, and the ground is the thing that
     * starts it again.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        look(event);
        for (RoadCommands.RoadJob job : List.copyOf(RoadCommands.passes())) {
            Route route = job.route();
            if (!(event.getServer().getLevel(TolkienDimensions.ARDA) instanceof ServerLevel level)) continue;
            int[] head = job.head();
            if (!level.hasChunkAt(new BlockPos(head[0], level.getMinY(), head[1]))) continue;
            boolean more;
            try {
                more = job.step(level);
            } catch (IOException unreadable) {
                // A pass that cannot run is not retried on every tick; the road's own note is what it resumes from. Said
                // once per tick at most, because a silent pass is a road that never appears and a player who cannot tell
                // why.
                LOGGER.warn("Road {}: a step could not run: {}", route.id(), unreadable.toString());
                continue;
            }
            if (!more) RoadCommands.stop(job);
        }
    }

    /**
     * Begins the roads the players are standing beside.
     * <p>
     * The load of a chunk is only ever the first look at it. A chunk that was brought in before the player came - by a
     * structure being raised, by a city being built, or by a save made in another session - has no second load to begin
     * a road with, and a road whose ground is loaded already has nothing left to start it at all: the world sat at
     * Rivendell with the high pass legs six hundred blocks away and nothing in the log to say why. So the players are
     * looked at instead. Once a second, every road a player stands within {@link #NEAR} of is begun from where they are,
     * which is the same rule the chunk load has - the ground a road crosses begins it - with the ground being wherever
     * a player has got to. A road already down owes nothing and is not begun again; a road that refused its ground is
     * not asked again this run.
     */
    private static void look(ServerTickEvent.Post event) {
        if (++ticks % NEAR_EVERY != 0) return;
        if (!(event.getServer().getLevel(TolkienDimensions.ARDA) instanceof ServerLevel level)) return;
        for (ServerPlayer player : level.players()) {
            BlockPos at = player.blockPosition();
            for (Route route : Roads.ROUTES) {
                if (!mayBegin(level, route)) continue;
                if (!near(route, at.getX(), at.getZ(), NEAR)) continue;
                RoadCommands.pass(level, route, at.getX(), at.getZ());
            }
        }
    }

    /** How near a player stands to a road's recorded line for the ground to begin it, in blocks. */
    private static final int NEAR = 32;

    /** How often the players are looked at, in ticks: once a second, which is nearer than a walker outruns a road's start. */
    private static final int NEAR_EVERY = 20;

    /** Ticks since the server started, so the players are looked at once a second rather than every tick. */
    private static int ticks;
}
