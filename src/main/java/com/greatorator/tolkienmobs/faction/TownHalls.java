package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

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
 */
public final class TownHalls {
    /** The prefix a hall pin is kept under, so a pin can be read back as a town's hall. */
    private static final String KEY = "hall:";

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
}
