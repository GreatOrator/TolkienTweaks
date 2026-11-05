package com.greatorator.tolkienmobs.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ground that was walked before it was named: the block a location stands on, chosen in a world whose
 * hills have already been made rather than computed from the drawing.
 * <p>
 * Everything else in this package places ground by asking the map: a country's middle, a cell's
 * middle, then the nearest column inside that search that will take a footprint. That is an answer to
 * "somewhere in this part of the country", and it is the right answer for a world nobody has looked
 * at yet. It is not an answer to "here": a scout who has flown the country, found the gap between the
 * crags and the dry bench above the water, and written the numbers down has already done the work the
 * survey was approximating, and the survey should not be asked to guess it back.
 * <p>
 * So a pin is a location written down twice. Once in the <em>world</em>, by {@code /tmpin}: somebody
 * standing on the spot says this is where the camp goes, and the placement paths obey it for that
 * world from then on. Once in {@link #SOURCE}, for every world: a pin scouted once and worth keeping
 * goes in the table in this file, so the same camp stands in the same place in a world nobody has
 * walked. The command prints that line, so pinning is a copy rather than a transcription.
 * <p>
 * The table is read before the drawing is, by both of the things that place ground - {@link Places}
 * for a name, {@link Sites} for a building - so a pinned location is exact, and the survey is left to
 * the laying out of the footprint, which still has to sit on ground the world actually made.
 * <p>
 * A pin is also what lets a name stand where the drawing paints nothing: the valley is the case that
 * matters, since no column of the map is sampled as IMLADRIS while the city is only planned. Pinned by
 * hand, it is ground like any other, and the pages that need it stop waiting on a drawing that has not
 * caught up.
 * <p>
 * The y is the surface the scout stood on. It is not used to seat a building - the laying measures the
 * ground under the footprint and seats on that - so a y a block or two out is not an error, only a
 * note of where the scout was standing.
 */
public final class Pins extends SavedData {
    private static final String SAVE_DATA_NAME = "pins";

    /**
     * The pins that hold for every world, keyed by whatever the code asks with: a place's id as the
     * catalogue writes it ({@code "rivendell"}, {@code "thorenhad"}), or a structure's template id as
     * a page names it ({@code "tolkienmobs:key_structures/wild_tent"}).
     * <p>
     * Empty is the normal state and a working one: a location with no pin is surveyed from the drawing
     * exactly as it was before any of this existed, so the campaign can be walked with an empty table.
     * Lines added here are printed by {@code /tmpin}, which is the intended way to get them.
     */
    private static final Map<String, BlockPos> SOURCE = new LinkedHashMap<>(Map.ofEntries(
            // One of each kind, as the shape a line takes. Delete the slashes to keep one.
            // "thorenhad", new BlockPos(4864, 157, -3328),
            // "tolkienmobs:key_structures/wild_tent", new BlockPos(4864, 157, -3340)
    ));

    /**
     * The pins this world has been told about, by key. A world pin wins over a source pin: the ground
     * of the world in front of the scout is the ground that matters there, and a pack update that moves
     * a source pin should not drag a world's own choice out from under it.
     */
    private static final Map<String, BlockPos> PINNED = new LinkedHashMap<>();
    /** What {@link #instance} answers with when there is no server to hold a file. */
    private static final Pins NO_SERVER = new Pins(false);

    /** One location as a world's file holds it: the key the code asks with, and the block. */
    public record Pin(String key, BlockPos at) {
        static final Codec<Pin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("key").forGetter(Pin::key),
                BlockPos.CODEC.fieldOf("at").forGetter(Pin::at)
        ).apply(instance, Pin::new));
    }

    public static final Codec<Pins> CODEC = Pin.CODEC.listOf().xmap(Pins::from, Pins::toList);

    public static final SavedDataType<Pins> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Pins::new, CODEC);

    /** A world with no pins file starts with nothing pinned by hand, and forgets the last world's. */
    private Pins() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Pins(boolean fresh) {
        super();
        if (fresh) PINNED.clear();
    }

    /**
     * The block a location was pinned to, or null when it has not been scouted yet - which is the
     * answer for every location on a fresh checkout, and leaves the drawing to speak.
     * <p>
     * The world is read first and the file second, so a spot chosen in this world is not overruled by
     * one written into the build.
     */
    @Nullable
    public static BlockPos at(@Nullable Level level, @Nullable String key) {
        if (key == null) return null;
        if (level != null) {
            instance(level);
            BlockPos here = PINNED.get(key);
            if (here != null) return here;
        }
        return SOURCE.get(key);
    }

    /** Pins a location in this world, replacing one of the same key, and answers where it went. */
    public static BlockPos set(Level level, String key, BlockPos at) {
        Pins store = instance(level);
        PINNED.put(key, at);
        store.setDirty();
        return at;
    }

    /** Forgets this world's pin for a key, and answers whether there was one to forget. */
    public static boolean remove(Level level, String key) {
        Pins store = instance(level);
        if (PINNED.remove(key) == null) return false;
        store.setDirty();
        return true;
    }

    /** Every pin this world has been told about, in the order they were given. */
    public static List<Pin> pinned(Level level) {
        instance(level);
        List<Pin> pins = new ArrayList<>(PINNED.size());
        for (Map.Entry<String, BlockPos> row : PINNED.entrySet()) {
            pins.add(new Pin(row.getKey(), row.getValue()));
        }
        return pins;
    }

    /** How many pins hold for every world, which is what a pack ships with. */
    public static int sourced() {
        return SOURCE.size();
    }

    /** The keys a pack already pins, for a command that lists what is set without being asked. */
    public static List<String> sourceKeys() {
        return List.copyOf(SOURCE.keySet());
    }

    // ---- plumbing ----

    private static Pins instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private static List<Pin> toList(Pins store) {
        List<Pin> pins = new ArrayList<>(PINNED.size());
        for (Map.Entry<String, BlockPos> row : PINNED.entrySet()) {
            pins.add(new Pin(row.getKey(), row.getValue()));
        }
        return pins;
    }

    private static Pins from(List<Pin> pins) {
        PINNED.clear();
        for (Pin pin : pins) {
            PINNED.put(pin.key(), pin.at());
        }
        // Read without clearing: the map was just filled from the file.
        return new Pins(false);
    }
}
