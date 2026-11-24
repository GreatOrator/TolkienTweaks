package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The block each build is walked to: the point a caller means when they ask for a build by name.
 * <p>
 * A pin is where a build is planted - the corner of a template, the low corner of a cut - and a corner is the right
 * answer for a hand laying the thing and the wrong answer for a player being sent to it: an arrow at a corner points
 * at a wall, and a jump lands outside the door. The owner walked every build in the test world and stood on the block
 * they would arrive at - the doorway, the gate, the edge of the camp - and those blocks are written here, once, read by
 * the two things that point somebody at a build: {@code /tmtp}, which puts the caller on the block, and the page's own
 * needle, which shows the way to it.
 * <p>
 * A mark is not a pin, and it never moves a build. A pin seats a template on the ground; a mark only says where a
 * traveller is set down - so a world that already stands its camps where it stands them gets the corrected doorway in
 * front of the same camp, and nothing is raised a second time to gain it.
 * <p>
 * The keys are the ones the commands already ask with: a place's catalogue id where the name is the catalogue's, and a
 * structure's template id where the name is a build's. A name typed as a build's own last word -
 * {@code troll_cave} for {@code tolkienmobs:key_structures/troll_cave} - is matched the way a pin's name is, so the
 * word that finds the build finds its mark.
 */
public final class Marks {
    /**
     * The block the owner stood on at each build, keyed as the command asks.
     * <p>
     * The y is a player's own feet, which is what a jump uses as given. Every number was a block somebody walked to,
     * so the only way one is moved by hand is by walking the new one - except that the drawing under them was re-based
     * on 24 November 2026 (black at y 0, white at 318, water at 25), where a block is no longer where it was: each one
     * was re-read as the drawn ground of its own column, one over it, and the columns are the ones that were walked.
     * Rivendell's is untouched because the city is off, and Gundabad's is the gate's own ground rather than the
     * country's, which did not move.
     */
    private static final Map<String, BlockPos> VISITED = new LinkedHashMap<>(Map.ofEntries(
            // The valley, at the last block of the city's own path out of the south gate: the road end /tmtp rivendell
            // stands a caller on, rather than the low corner the city was cut from.
            Map.entry("rivendell", new BlockPos(5200, 135, -3034)),
            // The Shire's village, on the ground inside it rather than at the middle of the footprint its cut was taken
            // in: a board's distance and a jump are both measured from where a player can stand.
            Map.entry("hobbiton", new BlockPos(-150, 67, -382)),
            // Thorenhad, which is Elladan's camp: the place's name and the build's name are the same ground, and the
            // walk the chapter makes is to the camp's own door.
            Map.entry("thorenhad", new BlockPos(4647, 117, -3315)),
            Map.entry(Sites.ELLADAN_CAMP, new BlockPos(4647, 117, -3315)),
            // Barachen's fire: the camp's doorway, which the place's name is announced at as well.
            Map.entry("barachen", new BlockPos(4819, 120, -2887)),
            Map.entry(Sites.BARACHENS_CAMP, new BlockPos(4819, 120, -2887)),
            // The ruin the black horse lies in, at the wall a page sends a player to.
            Map.entry(Sites.WOOD_RUIN, new BlockPos(4787, 121, -3059)),
            // The stone's three crags and Matuna's lair, each at the point the chapter's roads arrive on rather than at
            // the corner of the hill's box: an arrow to a crag is an arrow to its way in.
            Map.entry(Sites.TROLL_CAVE, new BlockPos(4515, 115, -2446)),
            Map.entry(Sites.TROLL_CAVE_2, new BlockPos(4624, 117, -2480)),
            Map.entry(Sites.TROLL_CAVE_3, new BlockPos(4562, 116, -2398)),
            Map.entry(Sites.TROLL_CAVE_4, new BlockPos(4684, 122, -3050)),
            // Glóin's camp at the east mouth of the pass: its doorway, which the catalogue's name for the camp and the
            // camp's own template both answer with.
            Map.entry("gloin", new BlockPos(8229, 180, -3654)),
            Map.entry(Sites.GLOIN_CAMP, new BlockPos(8229, 180, -3654)),
            // The goblin fires of the Pinnath Fenui: the way in, which the page's name for them is read at.
            Map.entry("goblins", new BlockPos(5605, 145, -3622)),
            // Gundabad: the hold's own gate ground rather than the corner of its 122 by 183 plan, so a jump arrives at
            // the way in and a board measures to the door.
            Map.entry("gundabad", new BlockPos(5578, 190, -6833)),
            Map.entry(Sites.GUNDABAD, new BlockPos(5578, 190, -6833))
    ));

    private Marks() {
    }

    /**
     * The block walked on a place or a build, or null when nobody has marked one - which is every name but the
     * fifteen below, and the answer that leaves the pin and the drawing to speak for the rest.
     */
    @Nullable
    public static BlockPos at(@Nullable String name) {
        if (name == null || name.isEmpty()) return null;
        BlockPos exact = VISITED.get(name);
        if (exact != null) return exact;
        for (Map.Entry<String, BlockPos> row : VISITED.entrySet()) {
            if (tail(row.getKey()).equals(name)) return row.getValue();
        }
        return null;
    }

    /** A key's last word, as {@code /tmtp} reads a build: {@code tolkienmobs:key_structures/troll_cave} is its own. */
    private static String tail(String key) {
        int cut = Math.max(key.lastIndexOf('/'), key.lastIndexOf(':'));
        return cut < 0 ? key : key.substring(cut + 1);
    }
}
