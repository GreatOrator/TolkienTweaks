package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Where each town's mustering hall stands.
 * <p>
 * A hall is no longer scattered by the world generator. There is one per alliance and a dozen towns to come, so a
 * hall thrown across a country lands wherever the spread finds room and then has to be explained: the player meets a
 * hall with nobody in it, in a field, before the town it belongs to exists. A hall belongs to a town, and a town is a
 * build somebody placed - so the hall is a pin in the town's own ground, kept beside the other pins and read by
 * whatever raises the town.
 * <p>
 * Three things are joined here, and each already existed: the town is the catalogue's (a board may point at it), the
 * alliance is the town's own (the ink its name is written in is whose country it is), and the pin is {@link Pins}'.
 * The key is the town's rather than the template's because two towns of the same alliance have two halls of one
 * template, and a key that named the template alone would let the second town overwrite the first.
 * <p>
 * Standing the hall is this class's own job, and the plain half of the same idea: the pin says where, the town's
 * alliance says which build, and the town's own pass says when. The when is the part that matters - a town lays its
 * cut, clears the footprint out of the country and dresses the ring round it, so a hall stood there first would be
 * taken away by the ground it was meant to stand on. So a hall waits for its town to finish, and a world whose town
 * already stands gets its hall on the next start.
 */
public final class TownHalls {
    private static final Logger LOGGER = LoggerFactory.getLogger(TownHalls.class);
    /** The prefix a hall pin is kept under, so a pin can be read back as a town's hall. */
    private static final String KEY = "hall:";

    /**
     * Which way a hall is turned on its pin: half round, so the front faces the way it was pinned for.
     * <p>
     * The hall is one build with one front, and the owner walked it in the Shire and found the notice board at the end
     * away from the path. Half a turn about the box's own middle leaves the pin, the floor and the footprint exactly
     * where they were - only the front moves - so the same pin now stands the hall the right way round.
     */
    private static final Rotation TURNED = Rotation.CLOCKWISE_180;

    private TownHalls() {
    }

    /** The pin a town's hall is kept under. */
    public static String key(String town) {
        return KEY + town;
    }

    /** The town a pin key names, or null when the key is not a hall's. */
    public static @Nullable String townOf(String key) {
        return key.startsWith(KEY) ? key.substring(KEY.length()) : null;
    }

    /**
     * The template a town's hall is built from: its alliance's own hall, cut from the one build the three are made of.
     * A town the catalogue gives no country has no hall, since there would be nobody's hall to stand there.
     */
    @Nullable
    public static String template(String town) {
        Places.Name name = Places.known(town);
        if (name == null || name.alliance() == null) return null;
        return "tolkienmobs:faction_hall_" + name.alliance().id();
    }

    /** Where that town's hall has been pinned in this world, or null when nobody has stood in the spot yet. */
    @Nullable
    public static BlockPos at(@Nullable Level level, String town) {
        return Pins.at(level, key(town));
    }

    /**
     * Whether the town's own build is still to come, so that a hall at its pin would be laid over.
     * <p>
     * Both towns that raise themselves answer here: the Shire's village from its note, written when its pass finishes,
     * and the valley from its marker, whose band count reaches the cut's own when the last band is down. A town with no
     * build of its own has nothing to wait for, so a hall pinned in one is stood straight away.
     */
    public static boolean waits(ServerLevel level, String town) {
        if ("hobbiton".equals(town)) return !Hobbiton.complete(level);
        if ("rivendell".equals(town)) return !CityCommands.complete(level);
        return false;
    }

    /**
     * Stands a town's hall at its pin, when this world has one and the town is finished.
     * <p>
     * The pin is the block the builder stood in, so the hall's own ground course is laid in the block they stood on:
     * the floor comes up into that block rather than sitting a course above it, and the builder keeps their footing on
     * the hall's floor. It is laid turned half round ({@link #TURNED}) and left alone once it stands, since the guard
     * reads the template's own blocks back out of the world.
     *
     * @return how many blocks the template holds, or nil when there is no pin, no town, the town is not finished yet or
     *         the hall already stands
     */
    public static int raise(ServerLevel level, String town) {
        BlockPos pin = at(level, town);
        if (pin == null || waits(level, town)) return 0;
        return stand(level, town, pin);
    }

    /**
     * Lays the town's hall turned half round on the pin, holding to the template's own blocks as the guard.
     * <p>
     * A half turn about the box's own middle keeps the pin, the floor and the footprint where they are, so the box is
     * placed from its far corner and the template is read back turned to match. A hall standing the other way is taken
     * out first: the file does not carry every cell of its box, so a turned lay over an unturned hall would leave the
     * old walls standing where the file says nothing. Its own bottom course is complete, so the floor comes back with
     * the lay.
     */
    private static int stand(ServerLevel level, String town, BlockPos pin) {
        String template = template(town);
        if (template == null) return 0;
        Identifier id = Identifier.tryParse(template);
        if (id == null) return 0;
        StructureTemplate build = level.getStructureManager().get(id).orElse(null);
        if (build == null) return 0;
        Vec3i size = build.getSize();
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) return 0;
        BlockPos corner = pin.below();
        load(level, corner, size.getX(), size.getZ());
        BlockPos at = corner.offset(size.getX() - 1, 0, size.getZ() - 1);
        if (Sites.wholeAt(level, template, at, TURNED)) return 0;
        sweep(level, corner, size);
        build.placeInWorld(level, at, at,
                new StructurePlaceSettings().setRotation(TURNED), level.getRandom(), 2);
        LOGGER.info("{}'s hall stands at {} - {} blocks of {}, turned half round on the pin",
                town, corner.toShortString(), size.getX() * size.getY() * size.getZ(), template);
        return size.getX() * size.getY() * size.getZ();
    }

    /** Takes the hall's own box out, so a hall laid again is a hall and not the old one with a second in it. */
    private static int sweep(ServerLevel level, BlockPos low, Vec3i size) {
        int cleared = 0;
        for (BlockPos at : BlockPos.betweenClosed(low,
                low.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1))) {
            if (level.getBlockState(at).isAir()) continue;
            level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
            cleared++;
        }
        return cleared;
    }

    /** Asks for every chunk the hall's box touches, so a hall pinned in country nobody has visited has ground. */
    private static void load(ServerLevel level, BlockPos low, int wide, int deep) {
        for (int cx = low.getX() >> 4; cx <= (low.getX() + wide - 1) >> 4; cx++) {
            for (int cz = low.getZ() >> 4; cz <= (low.getZ() + deep - 1) >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }
}
