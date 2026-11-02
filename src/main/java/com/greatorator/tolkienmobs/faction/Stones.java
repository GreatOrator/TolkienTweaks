package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.block.custom.MilestoneBlock;
import com.greatorator.tolkienmobs.block.custom.entity.MilestoneBlockEntity;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.util.block.MilestoneSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
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
    /** How far into the room a body stands in the search reads, and how many blocks of it it counts. */
    private static final int ROOM_REACH = 20;
    private static final int ROOM_LIMIT = 3000;

    private Stones() {
    }

    /**
     * Plants the two stones a taken page earns: one where it sends the player, one where they took it.
     * Called when a page enters the book rather than when it is settled, so the stone is standing on the
     * ground before the player first walks up to it.
     */
    public static void plant(ServerPlayer player, Quest quest) {
        if (!quest.story()) return;
        // A stone is a landmark, and a page of talk is a meeting: the stone a page earns is planted on
        // ground it sends the player to walk, while a conversation already has a body standing at the end
        // of it. Planting one beside every speaker put stones in fountains and camps, and the walk back
        // from a finished page is the book's to answer now.
        Waypoint.Mark ground = Waypoint.ground(player, quest);
        if (ground != null && !quest.talks()) plantAt(player, words(ground.label()), ground.pos());
        String hall = quest.giver().isEmpty() ? "" : Quest.label(quest.giver()).getString();
        if (!hall.isEmpty() && player.level() instanceof ServerLevel level) {
            // The hall's stone belongs in the middle of the hall, not wherever the herald happened to
            // be standing when the page changed hands: a herald drifts about his own floor, and a stone
            // that follows him is a stone that ends up in a corner.
            BlockPos middle = roomCentre(level, quest.origin());
            // A stone belongs where a building stands: a room found is a room stoned, and a giver met in
            // the open is not - a stone at every camp the line passes is a map full of them, and the walk
            // back over open ground is what the finished page's Return is for.
            if (middle != null) plantAt(player, hall, middle);
        }
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

    /**
     * The middle of the room a body is standing in, read as the average of the floor they could walk
     * across without leaving it.
     * <p>
     * This is a hall's own answer to where its middle is: the building knows it, and nothing else in the
     * campaign does - the herald's position is where he stood, and a template's corner is not written
     * down anywhere a page can read. The walk is horizontal and stays on the storey it started on, so a
     * stair is not followed up into the rafters, and it is bounded by a reach that keeps an open door
     * from pulling the middle of the room out into the street.
     */
    /**
     * Names the stones a template has already laid, and answers how many it found.
     * <p>
     * A building knows where its own middle is, so a milestone written into the piece that stands there
     * carries its coordinate with it: the piece is laid, and this reads the footprint back for anything
     * that is a stone and gives it the name of the place. That is what keeps a stone's position out of a
     * coordinate written down in this class - the number here would be wrong the moment a piece was moved,
     * and a template with no stone in it simply names nothing.
     * <p>
     * A stone that already carries a name keeps it: this runs on the visit that lays a building, and a
     * place somebody has since renamed must not be written over by a later visit.
     */
    public static int nameLaid(ServerLevel level, BlockPos corner, Vec3i size, String name,
                               ServerPlayer player) {
        if (name.isEmpty() || size.getX() <= 0 || size.getZ() <= 0) return 0;
        int named = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx <= size.getX(); dx++) {
            for (int dy = 0; dy <= size.getY(); dy++) {
                for (int dz = 0; dz <= size.getZ(); dz++) {
                    cursor.set(corner.getX() + dx, corner.getY() + dy, corner.getZ() + dz);
                    if (!level.hasChunkAt(cursor)) continue;
                    if (!(level.getBlockEntity(cursor) instanceof MilestoneBlockEntity stone)) continue;
                    MilestoneSettings settings = stone.getMilestoneSettings();
                    boolean fresh = settings.name == null || settings.name.isEmpty()
                            || settings.name.equals("Enter Name");
                    stone.setMilestoneSettings(new MilestoneSettings(settings.distance, settings.dimension,
                            fresh ? name : settings.name,
                            settings.uuid == null || settings.uuid.isEmpty()
                                    ? UUID.randomUUID().toString() : settings.uuid));
                    stone.setChanged();
                    BlockState state = level.getBlockState(cursor);
                    level.sendBlockUpdated(cursor, state, state, 3);
                    MilestoneHandler.updateMilestone(stone, player);
                    if (fresh) named++;
                }
            }
        }
        return named;
    }

    @Nullable
    private static BlockPos roomCentre(ServerLevel level, BlockPos from) {
        Set<BlockPos> floor = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(from);
        long sumX = 0L;
        long sumZ = 0L;
        double reach = (double) ROOM_REACH * ROOM_REACH;
        while (!queue.isEmpty() && floor.size() < ROOM_LIMIT) {
            BlockPos at = queue.poll();
            if (!floor.add(at)) continue;
            sumX += at.getX();
            sumZ += at.getZ();
            for (Direction side : Direction.Plane.HORIZONTAL) {
                BlockPos next = at.relative(side);
                if (floor.contains(next) || next.distSqr(from) > reach) continue;
                if (!level.hasChunkAt(next)) continue;
                if (!level.getBlockState(next).isAir()) continue;
                // A block with nothing under it is not this room's floor, so the walk does not drift
                // out of a doorway and down a hill.
                if (level.getBlockState(next.below()).isAir()) continue;
                queue.add(next);
            }
        }
        if (floor.size() < 9) return null;
        return new BlockPos((int) (sumX / floor.size()), from.getY(), (int) (sumZ / floor.size()));
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
                        // A spot a body is standing in is not free ground. The hall's stone is asked
                        // for at the herald's own feet, which is air over a floor and would otherwise
                        // be the first answer - a stone inside the man who just handed the page over.
                        if (!level.getEntities((Entity) null, new AABB(cursor),
                                seen -> !seen.isSpectator()).isEmpty()) continue;
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
