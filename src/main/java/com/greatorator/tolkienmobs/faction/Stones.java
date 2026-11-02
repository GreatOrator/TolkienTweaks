package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.block.custom.MilestoneBlock;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Fast travel, put where the campaign needs it rather than where a player happens to carry a milestone.
 * <p>
 * The pack already had the stone and the asking: a milestone remembers itself, lists itself to whoever
 * has touched it, and charges for the walk. What it did not have was any reason to be at Rivendell. A
 * chapter that sends a stranger four thousand blocks up the road and then asks them to walk back for
 * every errand needs a way back that is not the same road again.
 * <p>
 * So a stone is planted in two places whenever a page is taken: on the ground the page names, and where
 * the page was handed over. Neither is a gift - a milestone only lists itself to a player who has stood
 * at it - so the chapter's answer to "how do I get back" is a stone waiting where they arrive the first
 * time, not a free jump to ground they have never seen.
 * <p>
 * A stone is named for the place it stands at, because the name is what the travel list shows, and the
 * campaign's business with it is a place the player is meant to go back to and not a stranger's stone.
 */
public final class Stones {
    /** How far from the named ground a stone is allowed to stand when the ground itself is taken. */
    private static final int REACH = 12;
    /** The radius within which an existing stone counts as already standing there. */
    private static final int ALREADY = 24;

    private Stones() {
    }

    /**
     * Plants the two stones a taken page earns: one where it sends the player, one where they took it.
     * Called when a page enters the book rather than when it is settled, so the stone is standing on the
     * ground before the player first walks up to it.
     */
    public static void plant(ServerPlayer player, Quest quest) {
        if (!quest.story()) return;
        Waypoint.Mark ground = Waypoint.ground(player, quest);
        if (ground != null) plantAt(player, words(ground.label()), ground.pos());
        String hall = quest.giver().isEmpty() ? "" : Quest.label(quest.giver()).getString();
        if (!hall.isEmpty()) plantAt(player, hall, quest.origin());
    }

    /** A name as a stone shows it: a key if it is one, and the words themselves if they are a name. */
    private static String words(String label) {
        return label.indexOf('.') < 0 ? label : Component.translatable(label).getString();
    }

    /** Plants one stone near a point, unless one already stands there. */
    private static void plantAt(ServerPlayer player, String name, BlockPos around) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (name.isEmpty() || alreadyStood(player, name, around)) return;
        BlockPos spot = freeGround(level, around);
        if (spot == null) return;
        BlockState state = TolkienBlocks.MILESTONE_BLOCK.get().defaultBlockState()
                .setValue(MilestoneBlock.FACING, Direction.SOUTH);
        level.setBlockAndUpdate(spot, state);
        if (!(level.getBlockEntity(spot) instanceof MilestoneBlockEntity stone)) return;
        // The stone is named before anybody reads it, so the travel list says where it is rather than
        // "Enter Name" - and the name is written into the world's record as well, since that is the copy
        // every list is drawn from.
        MilestoneSettings settings = stone.getMilestoneSettings();
        stone.setMilestoneSettings(new MilestoneSettings(settings.distance, settings.dimension, name,
                settings.uuid == null || settings.uuid.isEmpty()
                        ? UUID.randomUUID().toString() : settings.uuid));
        stone.setChanged();
        level.sendBlockUpdated(spot, state, state, 3);
        MilestoneHandler.updateMilestone(stone, player);
    }

    /** Whether a stone of this name already stands within a short walk of the spot. */
    private static boolean alreadyStood(ServerPlayer player, String name, BlockPos around) {
        return MilestoneHandler.standsNear(player.level(), name, around, ALREADY);
    }

    /**
     * The nearest block of open air with something under it, so a stone stands on the ground rather than
     * in a wall: the ground a page names is a place's middle, which may be a courtyard, a fire, or the
     * inside of a hall.
     */
    @Nullable
    private static BlockPos freeGround(ServerLevel level, BlockPos around) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int ring = 0; ring <= REACH; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
                    for (int dy = 2; dy >= -3; dy--) {
                        cursor.set(around.getX() + dx, around.getY() + dy, around.getZ() + dz);
                        if (!level.hasChunkAt(cursor)) continue;
                        if (!level.getBlockState(cursor).isAir()) continue;
                        if (level.getBlockState(cursor.above()).isAir()
                                && !level.getBlockState(cursor.below()).isAir()) {
                            return cursor.immutable();
                        }
                    }
                }
            }
        }
        return null;
    }
}
