package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The coal gate at the mouth of the hall: the lattice the owner built across the way in, and the Key-stone that lifts it.
 * <p>
 * The door is not a wall of coal but a screen of it - seven across and ten courses up at z -6856, in a checker the owner
 * laid by hand: every odd course solid, every even course coal at the odd columns, and the bottom course closed with
 * dark oak stairs set top-half and facing north so the lattice reads as ironwork rather than as a wall. Its holes are a
 * single block wide in both directions, which is why it stops a player and not just the eye.
 * <p>
 * Written down rather than cut out of the world, because a lattice read back out of the world once it is half open would
 * remember only the half that is left. The shape is the owner's four marks at the corners of it - (5575, 190) and (5581,
 * 199) - and the two patterns between them, so it can be lifted and put back for ever without having kept anything.
 * <p>
 * Lifting is the bottom course going first, one course at a time: the screen shrinks up into the lintel over it and the
 * way in opens from the floor, which is what a portcullis does. Nothing is stored between sessions - the state is read
 * off the door itself the first time this is asked in a world, so a door left half open by a stopped server is finished
 * opening or shutting rather than left as it was found.
 */
@EventBusSubscriber(modid = MODID)
public final class GundabadDoor {
    /** The door's own box: seven columns, ten courses, one deep. */
    private static final int LOW_X = 5575;
    private static final int HIGH_X = 5581;
    private static final int FLOOR_Y = 190;
    private static final int TOP_Y = 199;
    private static final int Z = -6856;

    /** How many courses stand: the height the screen has to lift through. */
    private static final int COURSES = TOP_Y - FLOOR_Y + 1;

    /** How close the Key-stone has to be to the middle of the door before the mountain answers it. */
    private static final int REACH = 12;

    /** How many ticks a course takes, so the screen lifts at a speed the eye can follow: a course a tenth of a second. */
    private static final int STEP = 2;

    /** Courses already taken out of the bottom, nought for a shut door and {@link #COURSES} for an open one. */
    private static int lifted;
    /** Whether the world's own state has been read into {@link #lifted}, which happens on the first ask of a session. */
    private static boolean read;

    /** The coal the screen is made of, and the stairs closing its bottom course. */
    private static final BlockState COAL = Blocks.COAL_BLOCK.defaultBlockState();

    private static BlockState stair() {
        return Blocks.DARK_OAK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, Direction.NORTH)
                .setValue(StairBlock.HALF, Half.TOP);
    }

    private GundabadDoor() {
    }

    /**
     * The door's own rule, once every {@link #STEP} ticks: it lifts while somebody within {@link #REACH} blocks is
     * carrying the Key-stone, and shuts again once nobody is - never on somebody standing in the opening, which is the
     * one way a gate could take a player's legs out from under them.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % STEP != 0) return;
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return;
        if (!level.hasChunkAt(new BlockPos(LOW_X, FLOOR_Y, Z))) return;
        if (!read) {
            lifted = coursesMissing(level);
            read = true;
        }
        BlockPos middle = new BlockPos((LOW_X + HIGH_X) / 2, FLOOR_Y, Z);
        boolean asked = false;
        boolean standing = false;
        for (ServerPlayer player : level.players()) {
            if (middle.distToCenterSqr(player.position()) > (double) REACH * REACH) continue;
            if (carriesStone(player)) asked = true;
            if (blocking(player)) standing = true;
        }
        if (asked) {
            if (lifted < COURSES) setCourse(level, lifted++, false);
            return;
        }
        if (standing || lifted <= 0) return;
        setCourse(level, --lifted, true);
    }

    /** The Key-stone, anywhere in the pack: the stone is the lock, so a hand that has it has the way in. */
    private static boolean carriesStone(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(TolkienItems.ITEM_KEYSTONE.get())) return true;
        }
        return false;
    }

    /** Whether this player is standing in the opening, so the door waits rather than shutting on them. */
    private static boolean blocking(ServerPlayer player) {
        AABB opening = new AABB(LOW_X, FLOOR_Y, Z, HIGH_X + 1.0D, TOP_Y + 1.0D, Z + 1.0D);
        return opening.intersects(player.getBoundingBox());
    }

    /** How many of the bottom courses are already air, which is how open a door found in a world is. */
    private static int coursesMissing(ServerLevel level) {
        int missing = 0;
        for (int course = 0; course < COURSES; course++) {
            if (!holdsCoal(level, course)) missing++;
            else break;
        }
        return missing;
    }

    /**
     * Whether the course at this height still has its screen in it, asked of the first column - which is coal on every
     * course of the lattice, where the middle ones are air on half of them.
     */
    private static boolean holdsCoal(ServerLevel level, int course) {
        BlockState now = level.getBlockState(new BlockPos(LOW_X, FLOOR_Y + course, Z));
        return now.is(COAL.getBlock()) || now.getBlock() instanceof StairBlock;
    }

    /**
     * Puts one course of the screen back or takes it out: the bottom course is the stairs, every odd course above it is
     * solid, and every even one is coal at the odd columns, which is the lattice the owner laid.
     */
    private static void setCourse(ServerLevel level, int course, boolean shut) {
        int y = FLOOR_Y + course;
        for (int x = LOW_X; x <= HIGH_X; x++) {
            boolean odd = ((x - LOW_X) & 1) == 0;
            BlockState wanted;
            if (course == 0) {
                wanted = odd ? stair() : null;
            } else if ((course & 1) == 1) {
                wanted = COAL;
            } else {
                wanted = odd ? COAL : null;
            }
            if (!shut) wanted = null;
            BlockPos at = new BlockPos(x, y, Z);
            if (wanted == null) {
                if (!level.getBlockState(at).isAir()) {
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            } else {
                level.setBlock(at, wanted, Block.UPDATE_CLIENTS);
            }
        }
    }
}
