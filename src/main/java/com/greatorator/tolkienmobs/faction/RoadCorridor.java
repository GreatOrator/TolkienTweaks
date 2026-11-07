package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

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
 */
@EventBusSubscriber(modid = TolkienMobsMain.MODID)
public final class RoadCorridor {
    private RoadCorridor() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (RoadPaint.LINES.isEmpty()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(TolkienDimensions.ARDA)) return;
        RoadPaint.clear(level, event.getChunk());
    }
}
