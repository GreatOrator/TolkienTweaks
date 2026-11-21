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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The dwarven keep behind the Gundabad gate: the hold the pass was shaped for, stood up at the end of the gate's
 * hall rather than waiting for a page or a command.
 * <p>
 * The keep is one template, {@link Sites#GUNDABAD} - a hundred and twenty-two wide, twenty-six tall and a hundred and
 * eighty-three deep, its rooms on one floor over the rock it was cut in. It is a single file rather than an assembly,
 * so placing it is placing the world's own build: every cell the file holds goes down, the rock with it, and the air
 * inside it with it. Nothing is dressed afterwards, because there is nothing to dress - the build is the hold.
 * <p>
 * What {@link ArdaMap#keepPlan()} gives is the corner the box is written at, not a corner somebody walked: the keep is
 * underground, so it is a plan rather than a survey. The x is the entrance's number rather than the box's. The south
 * wall's four five-wide openings stand on a thirty-block beat, so a box centred on the chamber's axis put none of them
 * in the chamber - the passage ended at wall - and twelve east brings the 43..47 opening to world 5571..5575, inside
 * the pass's 5563..5591. That is the door the hold is entered by, and {@link #DOOR_X} names it for the mouth.
 * {@link #DOOR_FLOOR} and {@link #DOOR_FACE} are the template's own course and plane of that wall.
 * <p>
 * The way in is the keep's own door. The gate carries its chamber north to the keep's wall and stops there; the wall is
 * the template's, and the hold is entered through the opening in it. The gate no longer cuts a passage as wide as the
 * chamber through that wall, because it would take the doorway with it - a hold entered through a hole the width of the
 * hall has no door at all.
 * <p>
 * The doorway's mouth is still dressed - {@link #clearMouth} - because the ground it opens onto is the chamber's, not
 * the build's, and the chamber's ground can stand a course higher than the hold's own, which leaves a five-wide doorway
 * reading as a one-block slot. The dressing is a box around the door and is safe on every pass.
 * <p>
 * A world that already holds a hold keeps it. The plan moved twelve east on 21 November, and a world laid before that
 * has its keep at the older x: laying this build's box over it would put a second hold in the rock, 110 of its 122
 * columns over the first, with the old south wall standing through the new rooms. So the older plans are asked too -
 * {@link ArdaMap#keepOlderPlans()} - and a hold found on any of them is the world's hold. The shift east is what a
 * world made after 21 November gets; it is not a migration.
 */
public final class GundabadKeep {
    private static final Logger LOGGER = LoggerFactory.getLogger(GundabadKeep.class);
    /**
     * The doorway the hold is entered by: the second of the south wall's four, which is the one the box's shift puts in
     * the chamber. The template's doorways are cut at local x 13 to 17, 43 to 47, 74 to 78 and 104 to 108 - five wide
     * and seven tall, from course fourteen up - and with the plan at 5528 this one stands at world 5571..5575, inside
     * the pass's 5563..5591 rather than twenty-nine blocks east of it.
     */
    private static final int DOOR_X = 45;

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
     * @return how many blocks the template holds, or nil if a keep already stood at this plan or on one of the older
     *         ones the world may hold, or the build could not be laid
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
        // A world that already holds a hold keeps the one it has, wherever the plan stood when it was laid. The x moved
        // twelve east on 21 November, so a world made before that holds its keep at 5516 and this build's plan is
        // ground it has never seen - and a second hold laid twelve blocks over the first is two south walls through one
        // set of rooms. The older plans are recognised rather than migrated: a hold is 122 by 183 of rock, and the world
        // that holds the older one is the world that has something in it.
        for (BlockPos older : ArdaMap.keepOlderPlans()) {
            load(level, older, ArdaMap.KEEP_WIDE, ArdaMap.KEEP_DEEP);
            if (!Sites.standsAt(level, Sites.GUNDABAD, older)) continue;
            clearMouth(level, older);
            LOGGER.info("The keep already stands at the older plan {}; this world keeps the hold it has - the shift"
                    + " east is for a world made after it", older.toShortString());
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
