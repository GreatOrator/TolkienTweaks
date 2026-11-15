package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * The dwarven keep behind the Gundabad gate: the hold the pass was shaped for, stood up at the end of the gate's
 * hall rather than waiting for a page or a command.
 * <p>
 * The keep is one template, {@link Sites#GUNDABAD} - a hundred and twenty-two wide, twenty-six tall and a hundred and
 * eighty-three deep, its rooms on one floor over the rock it was cut in. It is a single file rather than an assembly,
 * so placing it is placing the world's own build: every cell the file holds goes down, the rock with it, and the air
 * inside it with it. Nothing is dressed afterwards, because there is nothing to dress - the build is the hold.
 * <p>
 * What {@link ArdaMap#keepPlan()} gives is the corner it was measured to, not a corner somebody walked: the keep is
 * underground, so the only point of it that could be stood on is the doorway's floor, and the owner marked that with an
 * ammolite on the chamber's floor. {@link #DOOR_X}, {@link #DOOR_FLOOR} and {@link #DOOR_FACE} are the template's own
 * coordinates of that doorway, and the plan is the mark worked backwards through them - so the doorway's middle lands
 * on the mark's x, its floor on the mark's course, and the wall on the mark's plane, with the body running the rest of
 * the template's depth into the mountain behind it.
 * <p>
 * The order matters. {@code GundabadGate} carries its chamber north to the keep's wall and clears a door through where
 * that wall will stand, so the gate is laid first and the keep laid over it: the keep's own wall is then written whole,
 * with its own doorway the only way in, and the clearing the hall made is air the keep's rooms already hold. Laid the
 * other way round the hall cuts a passage as wide as the chamber through the keep's wall, and the hold has no door.
 * <p>
 * Once the wall is whole the doorway's mouth is dressed - {@link #clearMouth} - because the ground it opens onto is
 * the chamber's, not the build's: the chamber's floor stands a course higher than the hold's own and the mountain's
 * rock comes down over the eastern half of the opening, so the wall alone would leave a five-wide doorway reading as a
 * one-block slot. The dressing is a box around the door and is safe on every pass.
 */
public final class GundabadKeep {
    /**
     * The doorway the plan is written around: the far one of the south wall, which is the one an ammolite was put at
     * the foot of. The template's doorways are cut at local x 13 to 17, 43 to 47, 74 to 78 and 104 to 108 - five wide
     * and seven tall, from course fourteen up - and the far one is taken so the keep's body runs away from the way in
     * rather than straddling it.
     */
    private static final int DOOR_X = 106;

    /** The course that doorway's floor is the top of: the fourteen courses of deepslate under the rooms. */
    private static final int DOOR_FLOOR = 13;

    /** The south wall's own course, which is the last course of the template's depth. */
    private static final int DOOR_FACE = ArdaMap.KEEP_DEEP - 1;

    /** The doorway itself: five wide and seven tall, its floor on {@link #DOOR_FLOOR}'s top course. */
    private static final int DOOR_WIDE = 5;
    private static final int DOOR_TALL = 7;

    /**
     * How far the mouth is dressed: four columns past the doorway on either hand and eight courses out from the wall.
     * <p>
     * Measured off the world the hold is laid in. South of the wall the chamber's ground stands a course high - course
     * 190 is solid across the passage's whole width - so at the height a player walks, the doorway read as a one-block
     * slot at its middle column with the ground covering the other four; and east of the doorway the mountain's rock
     * comes in from 196 up, so the five-wide opening had a roof of stone over half of it. Eight courses out is where
     * that ground ends and the passage is already open.
     */
    private static final int MOUTH_SIDE = 4;
    private static final int MOUTH_REACH = 8;

    /**
     * Stands the keep at its plan if it is not already there: every world is drawn with the pass, and the pass was
     * shaped for the hold.
     *
     * @return how many blocks the template holds, or nil if a keep already stood at the plan or the build could not be
     *         laid
     */
    public static int autospawn(MinecraftServer server) {
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return 0;
        BlockPos plan = ArdaMap.keepPlan();
        // The hold's own blocks, not a material: this asked whether the doorway's floor was deepslate, and Arda's rock
        // under the mountain is deepslate as well, so the answer was "already standing" in every world and the keep was
        // never laid at all. The box is opened first because a build nobody has visited cannot be read, and a hold that
        // cannot be read must not be answered as missing - laying over one is how a template's air eats a courtyard.
        load(level, plan, ArdaMap.KEEP_WIDE, ArdaMap.KEEP_DEEP);
        // The chamber out of the gate is carried to the hold's own wall on every pass, because that wall's place is the
        // keep's number rather than the gate's: a world laid against the older plan had the hall stopped twelve blocks
        // short of where the wall now stands, and a doorway opening into that rock is a doorway with no way in. It only
        // writes air, so a hall already at the wall costs nothing and a hold that moves takes its hall with it.
        GundabadGate.extendHall(level, ArdaMap.gatePlan(), Rotation.NONE);
        if (Sites.standsAt(level, Sites.GUNDABAD, plan)) {
            // A hold already standing still has its mouth dressed: the ground in front of a doorway is not part of
            // the build, and a doorway the chamber's floor stands a course across is a slot rather than a way out.
            clearMouth(level, plan);
            return 0;
        }
        int laid = place(level, plan);
        if (laid > 0) clearMouth(level, plan);
        return laid;
    }

    /**
     * Lays the keep with its north-west bottom corner at {@code plan}, unturned.
     *
     * @return how many blocks the template holds, or nil if the template is missing or its box is not loaded
     */
    public static int place(ServerLevel level, BlockPos plan) {
        StructureTemplate template = level.getStructureManager()
                .get(Identifier.parse(Sites.GUNDABAD)).orElse(null);
        if (template == null) return 0;
        Vec3i size = template.getSize();
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) return 0;
        load(level, plan, size.getX(), size.getZ());
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.NONE);
        template.placeInWorld(level, plan, plan, settings, level.getRandom(), 2);
        return size.getX() * size.getY() * size.getZ();
    }

    /**
     * Takes the ground out of the doorway's mouth: the chamber the hold opens into stands a course higher than the
     * hold's own floor, and the rock east of the doorway comes down over half the opening, so a hold whose wall is
     * whole is still a hold nobody can walk into.
     * <p>
     * What is cleared is a box, not a shape: the doorway's five columns and four more either side, eight courses out
     * from the wall, from the floor's own course up to the top of the opening - the height a player's head passes
     * through. The floor course itself is never touched, so the apron comes out flush with the hold's floor rather
     * than as a trench, and the hold's own wall is south of every cell this looks at. It is safe to run on every pass:
     * what it clears is air the next time, and a hold whose door was re-cut by a later gate is dressed again.
     *
     * @return how many blocks were taken out of the mouth
     */
    public static int clearMouth(ServerLevel level, BlockPos plan) {
        int face = plan.getZ() + DOOR_FACE;
        int lowX = plan.getX() + DOOR_X - DOOR_WIDE / 2 - MOUTH_SIDE;
        int wide = DOOR_WIDE + MOUTH_SIDE * 2;
        int floor = plan.getY() + DOOR_FLOOR;
        load(level, new BlockPos(lowX, 0, face + 1), wide, MOUTH_REACH);
        int cleared = 0;
        for (int z = face + 1; z <= face + MOUTH_REACH; z++) {
            for (int x = lowX; x < lowX + wide; x++) {
                for (int y = floor + 1; y <= floor + DOOR_TALL; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    if (level.getBlockState(at).isAir()) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    /**
     * Asks for every chunk a box touches, so a hold laid where a world keeps nothing loaded still has ground under it.
     * <p>
     * The keep's box begins at chunk x 341, z -444 - further north and west than a world keeps loaded around its
     * spawn - so on a first start every one of its chunks is unloaded, and a hold asked for with a {@code hasChunkAt}
     * test answered no and was silently never laid.
     */
    private static void load(ServerLevel level, BlockPos low, int wide, int deep) {
        for (int cx = low.getX() >> 4; cx <= (low.getX() + wide - 1) >> 4; cx++) {
            for (int cz = low.getZ() >> 4; cz <= (low.getZ() + deep - 1) >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    private GundabadKeep() {
    }
}
