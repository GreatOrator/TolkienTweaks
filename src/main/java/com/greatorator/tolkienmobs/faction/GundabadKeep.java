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
        // The doorway's own floor: the deepslate the hold's floor is cut from. The mark a builder stands on is that
        // same cell, so a keep that is already standing answers with its own block and nothing is laid over it.
        if (level.getBlockState(plan.offset(DOOR_X, DOOR_FLOOR, DOOR_FACE)).is(Blocks.DEEPSLATE)) {
            return 0;
        }
        return place(level, plan);
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
        // The box sits deeper into the mountain than a world's spawn chunks reach - its south wall is fifty-three
        // blocks past the back of the gate and its west side a further twenty-five - so on the first start of a world
        // every one of its chunks is unloaded, and a keep asked for with a hasChunkAt test answered no and was never
        // laid. The chunks are asked for by hand instead, which loads them and draws them if they are new ground.
        for (int cx = plan.getX() >> 4; cx <= (plan.getX() + size.getX() - 1) >> 4; cx++) {
            for (int cz = plan.getZ() >> 4; cz <= (plan.getZ() + size.getZ() - 1) >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(Rotation.NONE);
        template.placeInWorld(level, plan, plan, settings, level.getRandom(), 2);
        return size.getX() * size.getY() * size.getZ();
    }

    private GundabadKeep() {
    }
}
