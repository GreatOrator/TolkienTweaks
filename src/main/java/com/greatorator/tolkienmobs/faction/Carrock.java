package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

/**
 * The Carrock: the rock the owner shaped in the Anduin, cut and shipped as a build the world can ask for by name.
 * <p>
 * It is not a template but a cut - three pieces and a {@code carrock.parts} table, in the jar at
 * {@code /data/tolkienmobs/structure/carrock/}, in the shape {@link CutCommands} already reads. That is what makes it
 * ready to place before anybody has said where: {@code /tmcut table carrock} and {@code /tmcut place carrock <corner>}
 * work in any world off the jar alone, and the same folder is what a raise or a generator would read when one is
 * chosen.
 * <p>
 * <b>The anchor is the cut's own low corner</b> - its north-west bottom block, the corner the table measures its
 * offsets from. The table starts at 0, 0, 0, so the block a placement names is that corner, and the build runs
 * {@link #WIDE} east, {@link #HEIGHT} up and {@link #DEEP} south of it. A pin taken for the Carrock is that same
 * corner, which is why the spot still to be handed over is one coordinate and not a box.
 * <p>
 * <b>How it comes to stand is still open.</b> Nothing here raises or generates anything: whether the world plants it
 * as worldgen or stands it at a pin the way {@link TownHalls} stands a hall, both would ask for it by this name. Until
 * that is decided the cut is complete and is laid by hand with {@code /tmcut}. When the spot is given, the entry is one
 * line - a {@link Pins} entry under {@link #NAME} for a raise, or the generated structure's own placement for a
 * generator - and neither needs the build cutting again.
 * <p>
 * <b>What was cut.</b> Boxed by four ammolite markers in the Arda save's overworld, all at y 36 in the river: 16694 and
 * 16735 across, 5627 and 5711 south. The markers sat on the water's surface, so the cut starts under them at the rock's
 * own base course, and it carries stone, dirt, gravel, sand, clay, grass, a dirt path and the spruce standing on it,
 * with the water of the river and the markers themselves left out: air, water and ammolite are all absent from the
 * pieces.
 * <p>
 * <b>And what was taken back off it.</b> That first cut carried the ground the rock stands on with it - the riverbed and
 * the clipped banks, a plate no more than six blocks deep that spread over the whole box and came out of the water at
 * the south end as three tongues of bare dirt. Standing at the pin it read as terrain nobody had built, so every column
 * whose highest block stood at or under the plate's top (y 5) was cut away: 2,860 blocks in 1,189 columns, nothing
 * above them touched and the rock's own foot left whole. The pieces hold 36,079 blocks in ten kinds, and the only
 * columns that do not start at the base are fourteen leaf columns of the spruce leaning off the west side.
 */
public final class Carrock {
    /** The name the build is asked for by: its folder in the jar, and what a pin or a page would call it. */
    public static final String NAME = "carrock";

    /** How much ground the cut covers: east, up, south, from its low corner. */
    public static final int WIDE = 42;
    public static final int HEIGHT = 57;
    public static final int DEEP = 85;

    /**
     * The low corner: where a placement's coordinate goes, and where the cut's own corner stood when it was taken. The
     * owner's pin is the same corner in another world - see {@link Pins}, where {@link #NAME} stands at 8307, 187, -4165.
     */
    public static final BlockPos CORNER = new BlockPos(16694, 34, 5627);

    private Carrock() {
    }
}
