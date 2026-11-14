package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.block.custom.MilestoneBlock;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

/**
 * Fast travel, put where the campaign needs it rather than where a player happens to carry a milestone.
 * <p>
 * The pack already had the stone and the asking: a milestone remembers itself, lists itself to whoever
 * has touched it, and charges for the walk. What it did not have was any reason to be at Rivendell. A
 * chapter that sends a stranger four thousand blocks up the road and then asks them to walk back for
 * every errand needs a way back that is not the same road again.
 * <p>
 * So a stone comes with the ground rather than with the page: every building the campaign lays stands one
 * beside its door ({@code Sites.build}), in the same spot in every world, and the valley - which is
 * another hand's city rather than a template of ours - carries one on a pin of its own
 * ({@code Sites.STANDING}). Nothing plants a stone when a page changes hands any more: the hall's stone
 * used to follow the giver's room, and two pages naming one camp left two stones at two spots, neither
 * of them the door. A stone is not a gift either - a milestone only lists itself to a player who has
 * stood at it - so the chapter's answer to "how do I get back" is a stone already waiting at the place.
 * <p>
 * A stone is named for the place it stands at, because the name is what the travel list shows, and the
 * campaign's business with it is a place the player is meant to go back to and not a stranger's stone.
 */
public final class Stones {
    /** The radius within which an existing stone counts as already standing there. */
    private static final int ALREADY = 24;

    private Stones() {
    }

    /** Whether a stone of this name already stands within a short walk of the spot. */
    private static boolean alreadyStood(Level level, String name, BlockPos around) {
        return MilestoneHandler.standsNear(level, name, around, ALREADY);
    }

    /**
     * Stands one of the campaign's stones on ground it was given, if the ground will take it and no stone of
     * the same name already stands a short walk away.
     * <p>
     * This is a building's stone, laid once when the building is laid rather than whenever a page is taken, so
     * it is a fact of the world: the same spot in every world, and the same stone for every player who walks
     * up to it.
     * <p>
     * The spot is where the thing belongs, so a scout's marker standing there is replaced rather than left
     * beside the stone: a block of ammolite is how a location is marked on the ground, and a marker is a
     * placeholder rather than scenery.
     */
    public static void stone(ServerLevel level, BlockPos spot, String name, @Nullable ServerPlayer player) {
        if (name.isEmpty() || !level.hasChunkAt(spot)) return;
        BlockState ground = level.getBlockState(spot);
        boolean marked = ground.is(TolkienBlocks.BLOCK_AMMOLITE.get());
        if (!marked && !ground.isAir()) return;
        if (!marked && !level.getBlockState(spot.below()).isSolid()) return;
        if (alreadyStood(level, name, spot)) return;
        BlockState state = TolkienBlocks.MILESTONE_BLOCK.get().defaultBlockState()
                .setValue(MilestoneBlock.FACING, Direction.SOUTH);
        level.setBlockAndUpdate(spot, state);
        if (!(level.getBlockEntity(spot) instanceof MilestoneBlockEntity stone)) return;
        MilestoneSettings settings = stone.getMilestoneSettings();
        stone.setMilestoneSettings(new MilestoneSettings(settings.distance, settings.dimension, name,
                settings.uuid == null || settings.uuid.isEmpty()
                        ? UUID.randomUUID().toString() : settings.uuid));
        stone.setChanged();
        level.sendBlockUpdated(spot, state, state, 3);
        MilestoneHandler.updateMilestone(stone, player);
    }
}
