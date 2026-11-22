package com.greatorator.tolkienmobs.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * <p>
 * Which way the hall was laid is this class's own record, kept in a file of its own, and not the world's blocks. A
 * guard that reads the template back out of the world cannot tell a hall standing the wrong way round from a hall
 * somebody has been living in: the file does not carry every cell of its box, and one block a player had moved was
 * enough for the strict reading to answer "not standing" - after which the next start took the whole box out and laid
 * the hall again over their work. The record is written when the hall is laid and read before anything is touched, so
 * a hall this world laid the right way round is left alone whatever has happened to it since.
 */
public final class TownHalls extends SavedData {
    private static final Logger LOGGER = LoggerFactory.getLogger(TownHalls.class);
    /** The prefix a hall pin is kept under, so a pin can be read back as a town's hall. */
    private static final String KEY = "hall:";
    /** The file this class's own record is kept in, beside the pins and the places. */
    private static final String SAVE_DATA_NAME = "halls";

    /**
     * Which way a hall is turned on its pin: half round, so the front faces the way it was pinned for.
     * <p>
     * The hall is one build with one front, and the owner walked it in the Shire and found the notice board at the end
     * away from the path. Half a turn about the box's own middle leaves the pin, the floor and the footprint exactly
     * where they were - only the front moves - so the same pin now stands the hall the right way round.
     */
    private static final Rotation TURNED = Rotation.CLOCKWISE_180;

    /**
     * Which way each town's hall was laid in this world, by town: the pin it was laid at, and the rotation it was laid
     * with, as the rotation's own name.
     * <p>
     * The pin is part of the record because a town's hall can be pinned somewhere else. A hall recorded turned at the
     * block it stands on says nothing about a hall pinned in a new place, which is a hall still to be laid - so a pin
     * that has moved is read as no record, and the new ground is laid like ground that has never held a hall.
     */
    private static final Map<String, Laid> LAID = new LinkedHashMap<>();
    /** What {@link #instance} answers with when there is no server to hold a file. */
    private static final TownHalls NO_SERVER = new TownHalls(false);

    private static final Codec<TownHalls> CODEC = Laid.CODEC.listOf().xmap(TownHalls::from, TownHalls::toList);

    public static final SavedDataType<TownHalls> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), TownHalls::new, CODEC);

    /**
     * A world with no hall file starts with nothing laid. These maps are static, so a player who leaves one world and
     * opens another in the same session would otherwise carry the first world's halls into the second - the same trap
     * the places file had to clear.
     */
    private TownHalls() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private TownHalls(boolean fresh) {
        super();
        if (fresh) LAID.clear();
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
     * The three towns that raise themselves answer here: the Shire's village and Bree from their notes, written when
     * each pass finishes, and the valley from its marker, whose band count reaches the cut's own when the last band is
     * down. Each pass lays, clears and dresses the ground its hall is pinned in, so a hall stood there first would be
     * taken away with the country it was meant to stand on. A town with no build of its own has nothing to wait for, so
     * a hall pinned in one is stood straight away.
     */
    public static boolean waits(ServerLevel level, String town) {
        if ("hobbiton".equals(town)) return !Hobbiton.complete(level);
        if ("bree".equals(town)) return !Bree.complete(level);
        if ("rivendell".equals(town)) return !CityCommands.complete(level);
        return false;
    }

    /**
     * Stands a town's hall at its pin, when this world has one and the town is finished.
     * <p>
     * The pin is the block the builder stood in, so the hall's own ground course is laid in the block they stood on:
     * the floor comes up into that block rather than sitting a course above it, and the builder keeps their footing on
     * the hall's floor. It is laid turned half round ({@link #TURNED}), and a hall this world is already recorded as
     * having laid that way is left alone.
     *
     * @return how many blocks the template holds, or nil when there is no pin, no town, the town is not finished yet or
     *         the hall already stands the right way round
     */
    public static int raise(ServerLevel level, String town) {
        BlockPos pin = at(level, town);
        if (pin == null || waits(level, town)) return 0;
        return stand(level, town, pin);
    }

    /**
     * Lays the town's hall turned half round on the pin, unless this world already has it that way round.
     * <p>
     * A half turn about the box's own middle keeps the pin, the floor and the footprint where they are, so the box is
     * placed from its far corner and the template is read back turned to match. Which way this world's hall was laid is
     * written down ({@link #LAID}) and asked first: a hall recorded turned is returned from without a block being
     * touched, so a player who has moved something inside the hall, or taken a wall out of it, is not answered by
     * having it swept and laid again over their work.
     * <p>
     * A hall this world has no record of is read out of the world: one standing turned is written down and left as it
     * is, which is how a world made before the record existed comes in without anything being turned over. Only a hall
     * the record says was laid the other way - or one whose blocks say so - is taken out and laid again. Taking it out
     * matters: the file does not carry every cell of its box, so a turned lay over an unturned hall would leave the old
     * walls standing where the file says nothing. Its own bottom course is complete, so the floor comes back with the
     * lay.
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
        BlockPos far = corner.offset(size.getX() - 1, 0, size.getZ() - 1);
        TownHalls store = instance(level);
        Laid laid = LAID.get(town);
        if (laid != null && laid.at().equals(pin) && TURNED.name().equals(laid.rotation())) return 0;
        // No record at this pin, so the way the hall already stands is read off its own blocks: a world made before the
        // record existed, or a hall pinned somewhere new. One standing turned is noted and left, rather than laid over.
        boolean recorded = laid != null && laid.at().equals(pin);
        if (!recorded && orientation(level, template, corner, far) == TURNED) {
            note(store, town, pin, TURNED);
            LOGGER.info("{}'s hall already stands at {} turned half round on the pin; noted, and left as it is",
                    town, corner.toShortString());
            return 0;
        }
        sweep(level, corner, size);
        build.placeInWorld(level, far, far,
                new StructurePlaceSettings().setRotation(TURNED), level.getRandom(), 2);
        note(store, town, pin, TURNED);
        LOGGER.info("{}'s hall stands at {} - {} blocks of {}, turned half round on the pin",
                town, corner.toShortString(), size.getX() * size.getY() * size.getZ(), template);
        return size.getX() * size.getY() * size.getZ();
    }

    /**
     * Which way a hall already standing at the pin was laid, read out of the world's own blocks.
     * <p>
     * The two orientations are compared by a majority of the template's sampled blocks - turned wins if it answers,
     * since a hall laid turned is the one that is not to be touched - and null is the answer when neither of them
     * stands, which is ground that has never held a hall. A majority rather than every block is the point: the
     * question is which way a hall stands, not whether anybody has been inside it, and one changed block is not an
     * orientation.
     */
    @Nullable
    private static Rotation orientation(ServerLevel level, String template, BlockPos low, BlockPos far) {
        if (Sites.standsAt(level, template, far, TURNED)) return TURNED;
        if (Sites.standsAt(level, template, low)) return Rotation.NONE;
        return null;
    }

    /** Writes down which way this world's hall was laid at its pin, and marks the file so it is kept. */
    private static void note(TownHalls store, String town, BlockPos pin, Rotation rotation) {
        LAID.put(town, new Laid(town, pin, rotation.name()));
        store.setDirty();
    }

    /**
     * Forgets which way this world's hall was laid, so a hall pinned again is laid again.
     * <p>
     * The record belongs to the pin, so it goes with it: a player who takes a town's hall out of the world and pins it
     * back is asking for a hall, not for the file to answer that one already stands.
     */
    public static void forget(ServerLevel level, String town) {
        TownHalls store = instance(level);
        if (LAID.remove(town) == null) return;
        store.setDirty();
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

    // ---- plumbing ----

    private static TownHalls instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private static List<Laid> toList(TownHalls store) {
        List<Laid> rows = new ArrayList<>(LAID.size());
        for (Map.Entry<String, Laid> row : LAID.entrySet()) rows.add(row.getValue());
        return rows;
    }

    private static TownHalls from(List<Laid> rows) {
        LAID.clear();
        for (Laid row : rows) LAID.put(row.town(), row);
        // Read without clearing: the map was just filled from the file.
        return new TownHalls(false);
    }

    /** One town's hall as this world's file holds it: the town, the pin it was laid at, and how it was turned. */
    private record Laid(String town, BlockPos at, String rotation) {
        static final Codec<Laid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("town").forGetter(Laid::town),
                BlockPos.CODEC.fieldOf("at").forGetter(Laid::at),
                Codec.STRING.fieldOf("rotation").forGetter(Laid::rotation)
        ).apply(instance, Laid::new));
    }
}
