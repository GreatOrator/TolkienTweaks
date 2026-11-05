package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
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
 * So a pin is a location written down twice: once in this table, as the block it stands on, and once
 * in the world, when a page is taken or a name is first wanted. The table is read before the drawing
 * is, by both of the things that place ground - {@link Places} for a name, {@link Sites} for a
 * building - so a pinned location is exact in every world, and the survey is left to the laying out of
 * the footprint, which still has to sit on ground the world actually made.
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
public final class Pins {
    /**
     * Where each hand-scouted location stands, keyed by whatever the code asks with: a place's id as
     * the catalogue writes it ({@code "rivendell"}, {@code "thorenhad"}), or a structure's template id
     * as a page names it ({@code "tolkienmobs:key_structures/wild_tent"}).
     * <p>
     * Empty is the normal state and a working one: a location with no pin is surveyed from the drawing
     * exactly as it was before this table existed, so the campaign can be walked with none of these
     * written down.
     */
    private static final Map<String, BlockPos> POINTS = new LinkedHashMap<>(Map.ofEntries(
            // Examples of the shape, one of each kind. Delete the slashes to pin one.
            // "thorenhad", new BlockPos(4864, 157, -3328),
            // "tolkienmobs:key_structures/wild_tent", new BlockPos(4864, 157, -3340)
    ));

    private Pins() {
    }

    /**
     * The block a location was pinned to, or null when it has not been scouted yet - which is the
     * answer for every location on a fresh checkout, and leaves the drawing to speak.
     */
    @Nullable
    public static BlockPos at(@Nullable String key) {
        return key == null ? null : POINTS.get(key);
    }

    /** How many locations have been pinned by hand. */
    public static int count() {
        return POINTS.size();
    }
}
