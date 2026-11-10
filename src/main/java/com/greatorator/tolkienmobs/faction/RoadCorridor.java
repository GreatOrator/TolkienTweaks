package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
 * A city's crossing is not begun here. A bridge behind a build is the city's own business - it needs the span
 * assembled and the city's ground ready for its abutments - so that crossing stays with the dressing. A crossing on a
 * named road has no city to wait for, and starts from the ground it crosses like every other section.
 */
@EventBusSubscriber(modid = TolkienMobsMain.MODID)
public final class RoadCorridor {
    private RoadCorridor() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(TolkienDimensions.ARDA)) return;
        if (!RoadPaint.LINES.isEmpty()) RoadPaint.clear(level, event.getChunk());
        start(level, event.getChunk());
    }

    /** Starts every dry road this chunk lies on, which is how a road begins wherever it is first met. */
    private static void start(ServerLevel level, LevelChunk chunk) {
        int lowX = chunk.getPos().getMinBlockX();
        int lowZ = chunk.getPos().getMinBlockZ();
        for (Route route : Roads.ROUTES) {
            if (route instanceof Route.Crossing && !Roads.road(route)) continue;
            if (!touches(route, lowX, lowZ)) continue;
            RoadCommands.pass(level, route, lowX + 8, lowZ + 8);
        }
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
            // The chunk's own middle, held to the segment: a chunk is begun by the road passing near it.
            int midX = lowX + 8;
            int midZ = lowZ + 8;
            double along = length == 0.0D ? 0.0D
                    : Math.clamp(((midX - from[0]) * dx + (midZ - from[1]) * dz) / length, 0.0D, 1.0D);
            double away = Math.hypot(midX - (from[0] + dx * along), midZ - (from[1] + dz * along));
            if (away <= TOUCH) return true;
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
        for (RoadCommands.RoadJob job : List.copyOf(RoadCommands.passes())) {
            Route route = job.route();
            if (!(event.getServer().getLevel(TolkienDimensions.ARDA) instanceof ServerLevel level)) continue;
            int[] head = job.head();
            if (!level.hasChunkAt(new BlockPos(head[0], level.getMinY(), head[1]))) continue;
            boolean more;
            try {
                more = job.step(level);
            } catch (IOException unreadable) {
                // A pass that cannot run is not retried on every tick; the road's own note is what it resumes from.
                continue;
            }
            if (!more) RoadCommands.stop(job);
        }
    }
}
