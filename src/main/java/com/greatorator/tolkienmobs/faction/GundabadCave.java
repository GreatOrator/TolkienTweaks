package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * The cave east of the gate: the second ammolite the owner laid, cut back into the mountain.
 * <p>
 * The mark at {@link ArdaMap#gundabadCave()} stands on the shelf where the slope begins - the ground is flat at 184 to
 * its west and the rock climbs from 186 at the mark to 214 seven blocks east of it - so the way in is a mouth two
 * across and two up, and the room is behind it, under rock deep enough to keep it a cave rather than a trench. Nothing
 * is built on the way in or on the walls: the mountain's own stone is the cave, the way the goblin crags are their
 * hills, and what is cut is only the air.
 * <p>
 * Three things about it are written down rather than measured, because a cave read off the surface would move with
 * every ridge the world's own noise put there:
 * <ul>
 *   <li>the floor is the mouth's own ground, so the step in from the shelf is flush;</li>
 *   <li>the room is five courses clear at its lowest, and the ceiling over it is ragged by up to two more - the same
 *       uneven cut the gate's rock and the goblin halls carry, so a hollow does not read as a corridor with a lid;</li>
 *   <li>the dais is three by three, one course up, of the holds' own brick, on the middle of the floor.</li>
 * </ul>
 * The room is seven across and nine deep with the mouth's tunnel in front of it, which is inside one chunk: forty-three
 * blocks across the whole cut, nothing over the chunk line.
 */
public final class GundabadCave {
    /** The mouth: two across and two up, the least that is a doorway rather than a drain. */
    private static final int MOUTH_WIDE = 2;
    private static final int MOUTH_TALL = 2;

    /**
     * How deep the tunnel through the slope's lip runs before the room opens: six blocks.
     * <p>
     * Measured, not chosen. The shelf's foot runs diagonally - the rock begins at x 5693 on the mark's own line and at
     * x 5697 five blocks south of it - so a room starting three blocks in broke out of the hillside along its southern
     * corner, where the surface is 186 and the room's ceiling is 193. Six blocks in, the shallowest column the room
     * covers has 199 of rock over a ceiling that tops at 193, so the hollow is buried all the way round.
     */
    private static final int TUNNEL = 6;

    /** The room: seven across the mouth's own line and nine back into the mountain. */
    private static final int ROOM_WIDE = 7;
    private static final int ROOM_DEEP = 9;

    /** How many courses are clear over the floor, before the ceiling's own raggedness adds nought to two. */
    private static final int CLEAR = 5;

    /** The dais: three by three, one course over the floor, on the room's middle. */
    private static final int DAIS = 3;

    /**
     * Stands the cave if the mountain does not already hold it.
     * <p>
     * The test is the room's own first cell rather than the mouth's: the mouth's cell answers air in the world as the
     * owner left it - the marker stands on the shelf with the sky over it - so only the rock behind it says whether the
     * cave has been cut.
     *
     * @return how many blocks were taken out, or nil if the cave already stood
     */
    public static int autospawn(MinecraftServer server) {
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return 0;
        BlockPos plan = ArdaMap.gundabadCave();
        BlockPos room = plan.offset(TUNNEL, 1, 0);
        level.getChunk(room.getX() >> 4, room.getZ() >> 4);
        if (level.getBlockState(room).isAir()) return 0;
        return carve(level);
    }

    /**
     * Cuts the cave: the mouth and its tunnel first, then the room with the hillside left ragged over it, then the dais
     * and the marker's own removal.
     *
     * @return how many blocks were taken out
     */
    public static int carve(ServerLevel level) {
        BlockPos plan = ArdaMap.gundabadCave();
        int mouthX = plan.getX();
        int roomX = mouthX + TUNNEL;
        int middleZ = plan.getZ();
        int lowZ = middleZ - ROOM_WIDE / 2;
        int floor = plan.getY();
        load(level, new BlockPos(mouthX, 0, lowZ), TUNNEL + ROOM_DEEP, ROOM_WIDE);

        int cleared = 0;
        // The mouth and the tunnel behind it: two across, two up, cut through the lip of the shelf.
        for (int x = mouthX; x < roomX; x++) {
            for (int z = middleZ - MOUTH_WIDE / 2; z < middleZ - MOUTH_WIDE / 2 + MOUTH_WIDE; z++) {
                for (int y = floor + 1; y <= floor + MOUTH_TALL; y++) {
                    cleared += clear(level, new BlockPos(x, y, z));
                }
            }
        }
        // The room, its ceiling taken a course or two higher column by column so the hollow has a roof and not a lid.
        for (int x = roomX; x < roomX + ROOM_DEEP; x++) {
            for (int z = lowZ; z < lowZ + ROOM_WIDE; z++) {
                int top = floor + CLEAR + rough(x, z);
                for (int y = floor + 1; y <= top; y++) {
                    cleared += clear(level, new BlockPos(x, y, z));
                }
            }
        }
        // The dais, on the middle of the room's floor, one course up.
        int daisX = roomX + ROOM_DEEP / 2;
        for (int dx = -(DAIS / 2); dx <= DAIS / 2; dx++) {
            for (int dz = -(DAIS / 2); dz <= DAIS / 2; dz++) {
                level.setBlock(new BlockPos(daisX + dx, floor + 1, middleZ + dz),
                        TolkienBlocks.ANGAINOR_BRICKS.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
        // The marker is the mouth's own floor once the cave is cut, so it is taken up rather than left in the way.
        if (level.getBlockState(plan).is(TolkienBlocks.BLOCK_AMMOLITE.get())) {
            level.setBlock(plan, Blocks.STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        return cleared;
    }

    /** How many courses the ceiling stands higher at this column: nought, one or two, by the same hash the gate's rock uses. */
    private static int rough(int x, int z) {
        int h = x * 73856093 ^ z * 19349663 ^ 83492791;
        h ^= h >>> 13;
        return Math.floorMod(h, 3);
    }

    /** Takes one cell out, and answers nil where it was already air. */
    private static int clear(ServerLevel level, BlockPos at) {
        if (level.getBlockState(at).isAir()) return 0;
        level.setBlock(at, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        return 1;
    }

    /** Asks for every chunk a box touches before anything is read out of it or written into it. */
    private static void load(ServerLevel level, BlockPos low, int wide, int deep) {
        for (int cx = low.getX() >> 4; cx <= (low.getX() + wide - 1) >> 4; cx++) {
            for (int cz = low.getZ() >> 4; cz <= (low.getZ() + deep - 1) >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    private GundabadCave() {
    }
}
