package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Arda, as the Middle-earth maps draw it.
 *
 * <p>This is a hand-drawn picture of the world rather than a climate grid. The picture is painted
 * once, at class load, into a grid of countries - one country per 512-block cell - from a list of
 * rectangles and lines that name the same places the maps do: Forodwaith across the top, Ered Luin
 * down the western seaboard, the Misty Mountains through the middle, Mirkwood and the Long Lake,
 * the Mark and Ered Nimrais, Mordor in its ring of ash, and Harad at the southern edge. Every
 * country carries the biome that belongs there, the height its ground stands at and how rough that
 * ground is, so one lookup answers both what a column is and how high it stands.
 *
 * <p>Where a country ends is not a cell boundary. Two things soften them: the heights are
 * interpolated between neighbouring cells with a smoothstep, which is what turns a drawn coast into
 * a shore and a drawn wall into a slope, and the lookups are warped by the world's own noise, so a
 * straight line drawn on paper comes out as an organic border in world. Both the biome source and
 * the generator read this class, and both are handed the same noise, so a sea is water and the
 * ground beside it is the country the map draws there.
 *
 * <p>Three things are worked out rather than drawn: the sea's biome is banded by latitude (frozen
 * ocean in the north, temperate in the middle, warm in the south), the shore is read off the ground
 * rather than the cell, and the named rivers - the Anduin, Brandywine, Greyflood, Isen, Celduin,
 * Forest River and Entwash - are cut where the reference mask draws them.
 *
 * <p>A column's biome follows its ground, not the cell it was drawn in. Because the heights are
 * blended, a cell's own height only holds at its own edge and a coast is a slope: the waterline
 * wanders half a cell either way. So the band a few blocks either side of the waterline is the
 * shore, wherever it falls, and a sea cell whose middle stands dry above that band is not a sea - it
 * is named for the land country the ground belongs to, which is what keeps a bay's far end, or a
 * one-cell sea drawn between two countries, from reading as an ocean the player is standing on top
 * of. Everything else keeps the biome of the country it was drawn in, so a country's own grass and
 * rock still reach its coasts.
 */
public final class ArdaMap {
    /** Blocks per map cell: the world is 48 cells wide and 36 deep, so 24,576 by 18,432 blocks. */
    public static final int CELL = 512;
    /** The height the world's water stands at, from the overworld's own noise settings. */
    public static final int SEA_LEVEL = 63;
    /** How far either side of the waterline a column's ground may stand and still be the shore. */
    private static final int BEACH_HEIGHT = 3;

    static final int COLUMNS = 48;
    static final int ROWS = 36;

    /** How many quarter-cells a cell is cut into, and how many patches the patch grid has. */
    static final int PATCHES_PER_CELL = 4;
    static final int PATCH_COLUMNS = COLUMNS * PATCHES_PER_CELL;
    static final int PATCH_ROWS = ROWS * PATCHES_PER_CELL;

    /**
     * The cell the Shire sits in. Its centre is block (0, 0), so the hobbits' country is where a
     * player who builds at the overworld's origin arrives, and every other country's place on the
     * map is measured from there. Block coordinates pass through the portal unchanged, so this one
     * number is what lines Arda up with the overworld.
     */
    static final int ORIGIN_COLUMN = 10;
    static final int ORIGIN_ROW = 20;

    /** How far the drawn borders wander, in blocks. Bigger is more organic and less faithful. */
    private static final double WARP = 192.0D;

    /**
     * Whether the city's own ground - its valley floor, its massif, the shoulder and the branch cut out of
     * them - is laid over the drawing.
     * <p>
     * Off, the masks alone make the world: the drawn ground, the drawn rivers and lakes, and the drawn
     * mountains, with nothing invented for a build. That is the ground a place is to be sited against, since
     * a build can stand where the terrain suits it and the terrain cannot be bent round every build. The
     * city's own ground is kept rather than deleted: it is what will be laid at whatever site is chosen for
     * it, and the numbers below are its shape.
     */
    private static final boolean PLANTED = false;

    /**
     * The valley the map overrides rather than draws: its middle, the half-widths of its floor, and the
     * height of that floor.
     * <p>
     * A cell of the drawing is 512 blocks and the city that stands here is 554 across, so this ground
     * cannot be a country of the 48 by 36 grid: it is cut out of cells the drawing already fills and
     * blended back into them at its rim. Its middle is the middle of that city - the corner it is
     * planted from, plus half of its 554 by 512 footprint - so a city planted at 4352, -3833 stands in
     * the middle of the floor rather than against one of its walls. The floor is a few blocks smaller
     * than the footprint on every side - 544 by 502 inside a 554 by 512 city - so the mountain's foot
     * begins inside the build and the build embeds itself in it. There is no apron of flat ground in
     * front of the walls for the join to be dressed across, because there is no join: the lowest band of
     * the city is laid into the rock, and the stone that was there shows wherever the build has nothing
     * of its own.
     */
    private static final int VALLEY_X = 4628;
    private static final int VALLEY_Z = -3577;
    private static final int VALLEY_HALF_X = 272;
    private static final int VALLEY_HALF_Z = 251;

    /**
     * The height the rock stands at where the massif meets the build: the crown of the city itself.
     * <p>
     * The climb used to begin at the floor, a few blocks inside the walls, so the ground the build met
     * was 240 blocks under its own roof and the mountain did not rise to the city's height until 140
     * blocks out - the city stood on a pan in the middle of a bowl, and the join was a cliff of the
     * build's own making rather than a hillside. The rock is level with the crown where it meets the
     * build now, all the way round: the ground outside the walls is a continuation of the ground inside
     * them, and the mountain grows out of the roofline instead of out of a field below it. Inside the
     * footprint the floor is still the floor, because the build is what stands over it; the crown is
     * what shows wherever the build has nothing of its own, which is why the corner the mountain stood
     * in still reads as it did.
     */
    private static final int PLANTED_CROWN = 318;
    private static final int PLANTED_FLOOR = 74;

    /**
     * The valley's own heights, and how far they have been raised to fit the world they are in.
     * <p>
     * The city and the rock it was cut into were planted at 74 and 318 in a world whose sky was 319. A
     * world made since Arda was raised to 512 is a hundred and twenty-eight blocks taller, so the same
     * build can stand that much higher with its crown still under the sky - which is what
     * {@link #liftTo} does, and it does nothing at all where there is no room for it.
     */
    private static volatile int VALLEY_FLOOR = PLANTED_FLOOR;
    private static volatile int VALLEY_CROWN = PLANTED_CROWN;
    private static volatile int VALLEY_LIFT;

    /**
     * How far the ground climbs out of the valley floor before it is mountain, and how far the mountain
     * takes to come back down into the country beyond it, in blocks.
     * <p>
     * The valley is a vale in a massif rather than a pan beside a hillside: the floor is smaller than the
     * city, so the mountain's foot starts inside the build rather than a lawn away from it, and its foot is
     * the city's own crown, so the climb begins where the build ends rather than a hundred and forty blocks
     * out from it, and 175 blocks of it puts the ridge over the roofs rather than across a field from them.
     */
    private static final int VALLEY_RIM = 175;
    private static final int VALLEY_FALL = 340;
    /**
     * How far past the massif's foot the country is brought down to the valley's own level, in blocks.
     * <p>
     * A hundred and seventy-five blocks of mountain, then its fall, then nine
     * hundred more of country settling to the vale's floor: without the last of those the massif ends in
     * a wall of six blocks where the heightmap happens to be, and a valley that the map puts a hundred
     * blocks above the city is a pit with the whole country standing on its rim.
     */
    private static final int VALLEY_VALE = 900;

    /**
     * The height the mountains round the valley reach, and how far from that they are allowed to wander.
     * <p>
     * The ridge tops out at the city's own crown, 318, because that is as high as this world can stand: a save
     * made before Arda was raised is 384 blocks tall, its ceiling is 319, and a summit drawn above it is cut
     * off flat. The mask's white means the same height, so the ranges and the build reach the same sky. The
     * ridge's distance still wanders - the drawn noise swings it in and out over the scale below - and with
     * the crest swing at nothing its top is the crown itself, level with the roofs the massif shuts in.
     */
    private static volatile int VALLEY_CREST = VALLEY_CROWN;
    private static final int CREST_SWING = 0;
    private static final int CREST_SCALE = 420;
    private static final double RIDGE_SWING = 0.35D;
    private static final int RIDGE_SCALE = 320;

    /**
     * How far the mountain's flank is broken up, in blocks of height, and over what distance that broken
     * ground fades in from the floor.
     * <p>
     * A flank whose height is a function of distance alone draws the same contour at every level, and
     * contours two blocks apart are terraces. Twelve blocks of the map's own noise makes spurs and gullies
     * of them instead. The fade in keeps the floor flat where the city stands; the fade out matters as
     * much, because a rib still twelve blocks high where the climb meets the ridge leaves a cliff running
     * the whole length of the ridge - which is a face the terrain was never asked for.
     */
    private static final int CLIMB_ROUGH = 12;
    private static final int CLIMB_SCALE = 88;
    private static final int CLIMB_EASE = 28;

    /**
     * The city's north-east corner: the rectangle the build itself leaves empty, the height the rock stood
     * at in it, and how far outside the walls that face runs before the massif's foot takes over.
     * <p>
     * The gap is read off the cut rather than guessed: it has no piece at all in x 4797..4905 by z
     * -3833..-3774 above the lowest band, and those are the two positions the build marks as its corners.
     */
    private static final int SHOULDER_WEST = 4797;
    private static final int SHOULDER_EAST = 4905;
    private static final int SHOULDER_NORTH = -3833;
    private static final int SHOULDER_SOUTH = -3774;
    private static volatile int SHOULDER_TOP = VALLEY_CROWN;
    private static final int SHOULDER_RUN = 110;

    /**
     * The Bruinen: where the city's own water leaves the walls, the water it feeds, and the course between.
     * <p>
     * The mouth is read off the build rather than guessed. Its 584 pieces carry 302,049 water blocks, and the
     * water stands against the west wall over x 4352..4354, z -3536..-3525 with its surface at 82 - seven
     * blocks above the valley's floor. The east wall's only water is an aqueduct at y 239, which is a spring
     * inside the city, not an outflow. So the branch leaves west, falls the eight blocks to the vale, and runs
     * round the valley's south side to the drawn Bruinen: the nearest drawn water to the mouth is at 4996,
     * -3308, 681 blocks off, and the straight line to it runs through the city.
     * <p>
     * The river is cut as its own channel rather than by the maps' own river lines - which flatten every
     * column they touch to a level 63 and met the massif's rock two hundred and thirty blocks over the water.
     * Its water is read off the ground as the course goes rather than set at either end, so it falls where the
     * ground falls and never runs uphill.
     */
    private static final int BRUINEN_OUTLET_X = 4352;
    private static final int BRUINEN_OUTLET_Z = -3530;
    /**
     * The branch's water at the build's own mouth: the city carries its water out at {@link #PLANTED_FLOOR}
     * plus eight, seven blocks above the valley's floor, and this is that level, not the floor's. An
     * initialiser two blocks under the floor left the branch ten blocks below the build's own water, so the
     * water at the city's mouth stood over a channel it could not run into.
     */
    private static volatile int BRUINEN_WATER = PLANTED_FLOOR + 8;
    private static final int BRUINEN_BED = 7;
    /** How much of the channel's width the ford takes away, 0 at the outlet and 0.55 at the ford. */
    private static final double BRUINEN_FORD_NARROW = 0.30D;
    /** Blocks of water the branch keeps under it where it comes to the ford, however shallow it gets. */
    private static final double BRUINEN_FORD_DEPTH = 2.0D;
    /** How much of the branch's depth the last stretch to the ford takes away, 0.55 at most. */
    private static final double BRUINEN_FORD_SHALLOWING = 0.55D;
    /**
     * How far out the bed looks for a drawn river's bank, in blocks: a channel wider than this counts as
     * this wide, so a river drawn as a bay is not a canyon.
     */
    private static final int RIVER_REACH = 24;
    /**
     * The deepest a drawn river's bed goes, in blocks, however wide the mask paints it.
     * <p>
     * The reference paints its rivers as bands thirty blocks and more across, and a bed as deep as the
     * band is wide cuts a canyon where the map draws a river - and undercuts the city's outlet where the
     * branch meets the drawn Bruinen. Sixteen is deep enough to be a real channel at any of those widths.
     */
    private static final int RIVER_DEPTH_MAX = 11;

    /**
     * How far from a drawn river or lake its bank is raised, and how many of those blocks the raise falls
     * away over.
     * <p>
     * The drawing's water is laid at the height of the ground it is drawn on, and the ground beside it is
     * whatever the country answers: a channel cut into a slope has a low side, and water finds it in a
     * tick or two and floods the country to the river's height. The same trouble in a hand-painted world is
     * cured by levelling the corridor round a road, and this is the river's version of it - the ground
     * within the reach stands at least one block over the water, flat while it is walked and falling away
     * over the last few blocks so the bank meets the country's own ground instead of ending in a step.
     */
    private static final int BANK_REACH = 12;
    private static final int BANK_FALL = 4;
    /**
     * How far the channel runs either side of the course, read off the build's own water: twelve blocks
     * across, five to the left of the middle block and seven to the right.
     */
    private static final int BRUINEN_LEFT = 5;
    private static final int BRUINEN_RIGHT = 7;
    private static final int BRUINEN_FORD_X = 4995;
    private static final int BRUINEN_FORD_Z = -3311;
    private static final double BRUINEN_BANK = 1.2D;
    private static final double BRUINEN_FORD_SHALLOW = 0.92D;
    /** Steps the channel's water is read at along its course, from the outlet to the ford. */
    private static final int BRUINEN_STEPS = 256;
    /**
     * How fast the ground climbs away from water, in blocks of height per block out.
     * <p>
     * The massif stands at the city's own crown where its floor ends, which is right behind the build's walls
     * and hidden by them - but a river at the floor's level at the foot of that crown is a slot with two
     * hundred blocks of rock either side of it, not a river. Six tenths of a block of rise per block out is a
     * mountainside's own grade, about thirty degrees, and it is what the rock falls away at beside any water.
     */
    private static final double MOUNTAIN_SLOPE = 0.6D;
    /**
     * How far the crown's own surface rolls below the crown, in blocks.
     * <p>
     * A summit standing at exactly one height is a table: every contour on it is the crown's own outline, and
     * the mountain's edge reads as a cut however softly the flank is graded. A few blocks of roll over the
     * same distance as the ridge's wander costs the world no height and stops the top being a plane.
     */
    private static final double CROWN_ROLL = 3.0D;
    /** How far out a column looks for a drawn river whose ground it has to give way to, in blocks. */
    private static final int WATER_REACH = 64;
    /**
     * How far the branch wanders off its own course, and how many turns it makes on the way, in blocks and in
     * half turns. The wander falls to nothing at each corner, so the water is one channel rather than a
     * channel and a step wherever the course turns.
     * <p>
     * A river drawn as one straight cut from a wall to another river is a canal: the water leaves the city,
     * runs dead straight for a hundred blocks and arrives. These two numbers give it a course that leaves
     * the outlet and the join exactly where they are - the wander is zero at both ends - and bends between
     * them, which is also what makes the gorge read as the valley a river cuts rather than as a slot.
     */
    private static final double BRUINEN_SNAKE = 18.0D;
    private static final double BRUINEN_TURNS = 1.5D;
    /**
     * The course the branch runs, in blocks: the city's mouth, out west of the wall, south round the valley's
     * own side, east below the city, and in to the drawn Bruinen.
     * <p>
     * A straight line from the mouth to the nearest drawn water runs through the city, and the drawn river
     * passes the valley's east side while the city's water leaves on its west, so the water has to go round.
     * The legs keep a corridor's width clear of the walls, so the corridor the valley is cut with does not eat
     * the city's foot.
     */
    private static final int[] COURSE_X = {BRUINEN_OUTLET_X, 4272, 4272, 4952, BRUINEN_FORD_X};
    private static final int[] COURSE_Z = {BRUINEN_OUTLET_Z, -3530, -3256, -3256, BRUINEN_FORD_Z};
    /** How long each leg is, where each leg starts along the course, and the whole course. */
    private static final double[] COURSE_LEG = new double[COURSE_X.length - 1];
    private static final double[] COURSE_START = new double[COURSE_X.length - 1];
    private static final double COURSE_LENGTH;

    static {
        double total = 0.0D;
        for (int leg = 0; leg < COURSE_LEG.length; leg++) {
            COURSE_START[leg] = total;
            COURSE_LEG[leg] = Math.hypot(COURSE_X[leg + 1] - COURSE_X[leg], COURSE_Z[leg + 1] - COURSE_Z[leg]);
            total += COURSE_LEG[leg];
        }
        COURSE_LENGTH = total;
    }

    /** How far off the course's own line its middle stands at a share of the way along it. */
    private static double wander(double share) {
        return BRUINEN_SNAKE * Math.sin(share * BRUINEN_TURNS * Math.PI) * Math.sin(Math.PI * share);
    }

    /** Which leg of the course a share of the way along it falls on. */
    private static int courseLeg(double share) {
        double along = Mth.clamp(share, 0.0D, 1.0D) * COURSE_LENGTH;
        int leg = 0;
        while (leg < COURSE_LEG.length - 1 && along >= COURSE_START[leg] + COURSE_LEG[leg]) {
            leg++;
        }
        return leg;
    }

    /** How far into its own leg a share of the way along the course falls, from 0 to 1. */
    private static double courseInto(int leg, double share) {
        return Mth.clamp((Mth.clamp(share, 0.0D, 1.0D) * COURSE_LENGTH - COURSE_START[leg]) / COURSE_LEG[leg],
                0.0D, 1.0D);
    }

    /** The direction the course runs on a leg: x first. */
    private static double courseDirX(int leg) {
        return (COURSE_X[leg + 1] - COURSE_X[leg]) / COURSE_LEG[leg];
    }

    /** The same direction, z. */
    private static double courseDirZ(int leg) {
        return (COURSE_Z[leg + 1] - COURSE_Z[leg]) / COURSE_LEG[leg];
    }

    /** The course's middle at a share of the way along it: x first. */
    private static double courseX(double share) {
        int leg = courseLeg(share);
        double into = courseInto(leg, share);
        return COURSE_X[leg] + into * (COURSE_X[leg + 1] - COURSE_X[leg])
                - courseDirZ(leg) * wander(share) * Math.sin(Math.PI * into);
    }

    /** The same middle, z. */
    private static double courseZ(double share) {
        int leg = courseLeg(share);
        double into = courseInto(leg, share);
        return COURSE_Z[leg] + into * (COURSE_Z[leg + 1] - COURSE_Z[leg])
                + courseDirX(leg) * wander(share) * Math.sin(Math.PI * into);
    }

    /**
     * Where a column lies of the course: the share of the way along it, how far across at the nearest point,
     * how far off the course that is, the direction the water runs there, and whether the column is behind
     * the mouth or past the join.
     */
    private static double[] project(double blockX, double blockZ) {
        double near = Double.MAX_VALUE;
        double share = 0.0D;
        double side = 0.0D;
        double dirX = 1.0D;
        double dirZ = 0.0D;
        boolean before = false;
        boolean after = false;
        for (int leg = 0; leg < COURSE_LEG.length; leg++) {
            double alongX = COURSE_X[leg + 1] - COURSE_X[leg];
            double alongZ = COURSE_Z[leg + 1] - COURSE_Z[leg];
            double at = ((blockX - COURSE_X[leg]) * alongX + (blockZ - COURSE_Z[leg]) * alongZ)
                    / (alongX * alongX + alongZ * alongZ);
            double into = Mth.clamp(at, 0.0D, 1.0D);
            double footX = COURSE_X[leg] + into * alongX;
            double footZ = COURSE_Z[leg] + into * alongZ;
            double away = Math.hypot(blockX - footX, blockZ - footZ);
            if (away < near) {
                near = away;
                share = (COURSE_START[leg] + into * COURSE_LEG[leg]) / COURSE_LENGTH;
                dirX = courseDirX(leg);
                dirZ = courseDirZ(leg);
                side = (blockX - footX) * -dirZ + (blockZ - footZ) * dirX;
                before = leg == 0 && at < 0.0D;
                after = leg == COURSE_LEG.length - 1 && at > 1.0D;
            }
        }
        return new double[] {share, side, near, dirX, dirZ, before ? 1.0D : 0.0D, after ? 1.0D : 0.0D};
    }

    /** The course's water, read once; and the lock that keeps two chunks from reading it together. */
    private static volatile int[] bruinenProfile;
    private static final Object BRUINEN_LOCK = new Object();

    /** How deep a lake's bed lies under its water. */
    private static final int LAKE_DEPTH = 6;
    /**
     * How far a column drawn as lake may stand above its sheet's level and still be flooded. Ground higher
     * than this is shore: it is left as land rather than cut away, which is what keeps a sea from ending in
     * a cliff. The number is reported at load beside each sheet's spread, so it can be judged against real
     * ground rather than guessed.
     */
    private static final int LAKE_SHORE = 4;
    /**
     * How far a shore may be cut down to meet its sheet. Ground standing higher than this over the water it
     * faces keeps its own face: a lake at the bottom of its basin is meant to end against the hill, and
     * lowering a hill to a ramp is what put a cone round every sheet instead of a shore.
     */
    private static final int SHORE_CUT = 6;

    /** The highest the drawing may raise the ground, leaving the world's ceiling room above it. */
    private static final int MOUNTAIN_CEILING = 420;

    /**
     * How far a ridge's crests and gullies stand either side of the mask's own climb, in blocks.
     * <p>
     * The mask's brightness is blurred, and a blurred brightness read linearly is a cone: every drawn
     * range came out a smooth hill with nothing on it but its own gradient. This is the roughness put
     * back - crests, spurs and the gullies between them - scaled by how much rock the mask puts at the
     * column, so a range is ragged where the drawing is dark and a plain beside it is untouched.
     */
    private static final int CRAG = 42;

    private static final Country[][] GRID = draw();

    /**
     * A country smaller than a cell, painted on the quarter-cell grid.
     * <p>
     * A cell is 512 blocks, and some countries on the poster are smaller than that: painted as whole cells
     * a haven becomes a county, and painted not at all it stays its neighbour. A patch answers for the
     * country of the column it covers at 128 blocks, and every column between the patches still reads off
     * the 48 by 36 grid, so the valley, the places and the canon cells keep their coordinates.
     */
    private record Patch(Country country, int fromColumn, int fromRow, int toColumn, int toRow) {
    }

    private static final Patch[] PATCHES = {
            // The corsairs' haven and its dry hills, on the warm coast below Belfalas.
            new Patch(Country.UMBAR, 100, 136, 119, 143),
            // The vineyards of Dorwinion, at the Sea of Rhun's north-west corner.
            new Patch(Country.DORWINION, 150, 34, 159, 43),
            // The three countries south of the Shire, on the land the masks keep above water there: rows 22
            // and 23 are ground and the row below them is the sea.
            new Patch(Country.MINHIRIATH, 20, 88, 31, 95),
            new Patch(Country.CARDOLAN, 32, 88, 47, 95),
            new Patch(Country.ENEDWAITH, 52, 88, 67, 95)
    };

    /** The patch country at a column of the drawing, or null where the cells answer. */
    private static Country patchAt(double mapX, double mapZ) {
        int column = (int) Math.floor(mapX * PATCHES_PER_CELL);
        int row = (int) Math.floor(mapZ * PATCHES_PER_CELL);
        for (Patch patch : PATCHES) {
            if (column >= patch.fromColumn() && column <= patch.toColumn()
                    && row >= patch.fromRow() && row <= patch.toRow()) {
                return patch.country();
            }
        }
        return null;
    }

    private ArdaMap() {
    }

    /**
     * The named lakes, by the span the lake mask draws each of them in. The drawing is the authority: a
     * column of water inside one of these spans is that lake, and every other sheet the mask draws is a
     * lake without a name of its own.
     */
    private static final int[] EVENDIM_SPAN = {248, 768, -5176, -4600};
    private static final int[] RHUN_SPAN = {16336, 18808, -1400, 1168};
    private static final int[] NURNEN_SPAN = {14304, 16608, 6488, 7648};
    private static final int[] LONG_LAKE_SPAN = {11424, 11560, -5120, -4696};

    /**
     * The biome a column of open water reads as.
     * <p>
     * The sample only says that a column is water, because rivers and lakes are laid the same way; which
     * water it is has to be asked of the mask. A lake column is the lake whose drawn span holds it, and a
     * lake the drawing does not name stays the generic one; everything else is the generic river.
     */
    public static ResourceKey<Biome> waterBiome(int blockX, int blockZ) {
        if (!ArdaMasks.lake(blockX, blockZ)) {
            return switch (ArdaMasks.riverName(blockX, blockZ)) {
                case 1 -> TolkienBiomes.RIVER_ANDUIN;
                case 2 -> TolkienBiomes.RIVER_CELDUIN;
                case 3 -> TolkienBiomes.RIVER_GWATHLO;
                case 4 -> TolkienBiomes.RIVER_LUNE;
                default -> TolkienBiomes.RIVER;
            };
        }
        if (within(blockX, blockZ, EVENDIM_SPAN)) {
            return TolkienBiomes.LAKE_EVENDIM;
        }
        if (within(blockX, blockZ, RHUN_SPAN)) {
            return TolkienBiomes.SEA_OF_RHUN;
        }
        if (within(blockX, blockZ, NURNEN_SPAN)) {
            return TolkienBiomes.SEA_OF_NURNEN;
        }
        if (within(blockX, blockZ, LONG_LAKE_SPAN)) {
            return TolkienBiomes.LONG_LAKE;
        }
        return TolkienBiomes.LAKE;
    }

    private static boolean within(int blockX, int blockZ, int[] span) {
        return blockX >= span[0] && blockX <= span[1] && blockZ >= span[2] && blockZ <= span[3];
    }

    /**
     * What is at a column: the country whose cell it falls in, the height its rock stands at,
     * whether a river runs there, whether its ground is the water's edge, and - for ground standing
     * dry in a cell the map drew as sea - the land country that ground belongs to.
     */
    public record Sample(Country country, int height, boolean river, boolean shore, Country upland,
            int water) {
        /** The same column with water standing at the world's own level, which is almost every column. */
        public Sample(Country country, int height, boolean river, boolean shore, Country upland) {
            this(country, height, river, shore, upland, SEA_LEVEL);
        }

        /** The biome this column reads as, which is the river's or the shore's where those apply. */
        public ResourceKey<Biome> biome() {
            if (this.river) {
                return Biomes.RIVER;
            }
            if (this.shore) {
                return Biomes.BEACH;
            }
            // A sea cell is a sea wherever its water reaches; only ground the blend has lifted clear
            // of the waterline is land, and it belongs to the country it is nearest.
            if (!this.country.land() && this.height > SEA_LEVEL + BEACH_HEIGHT) {
                return this.upland.biome();
            }
            return this.country.biome();
        }
    }

    /**
     * One country of the map. {@code height} is the top of its rock before the surface rules dress
     * it and before any water is laid on top, and {@code relief} is how far the ground is allowed to
     * wander about that height - a field is nearly flat, a range is not.
     */
    public enum Country {
        // The seas. Which of the three a column is depends on its latitude, not on the drawing.
        FROZEN_SEA(Biomes.DEEP_FROZEN_OCEAN, 36, 5, false),
        TEMPERATE_SEA(Biomes.DEEP_OCEAN, 38, 5, false),
        WARM_SEA(Biomes.WARM_OCEAN, 40, 5, false),

        // The north and the western seaboard.
        FORODWAITH(Biomes.FROZEN_OCEAN, 70, 8, true),
        // The Ice Bay of Forochel and the snowfield round it, with the slopes that climb out of it
        // toward Ered Luin and Angmar. A low frozen coast is what the bay is; the slopes behind it
        // are hills rather than peaks.
        FOROCHEL(TolkienBiomes.FOROCHEL, 68, 5, true),
        FOROCHEL_SLOPES(TolkienBiomes.FOROCHEL_SLOPES, 104, 18, true),
        LINDON(TolkienBiomes.LINDON, 68, 5, true),
        BLUE_MOUNTAINS(TolkienBiomes.BLUE_MOUNTAINS, 150, 30, true),
        ERED_MITHRIN(TolkienBiomes.ERED_MITHRIN, 145, 28, true),
        IRON_HILLS(TolkienBiomes.IRON_HILLS, 145, 28, true),

        // The north-west: Arnor's lost realm and the cold moors beyond it.
        ERIADOR(TolkienBiomes.ERIADOR, 70, 5, true),
        ANGMAR(TolkienBiomes.ANGMAR, 88, 14, true),
        RHUDAUR(TolkienBiomes.RHUDAUR, 86, 13, true),
        SHIRE(TolkienBiomes.SHIRE, 68, 4, true),
        OLD_FOREST(TolkienBiomes.OLD_FOREST, 74, 8, true),
        BARROW_DOWNS(TolkienBiomes.BARROW_DOWNS, 88, 14, true),
        DUNLAND(TolkienBiomes.DUNLAND, 92, 16, true),

        // The Misty Mountains and the countries either side of them.
        HITHAEGLIR(TolkienBiomes.HITHAEGLIR, 175, 34, true),
        FIRIEN(TolkienBiomes.FIRIEN, 76, 8, true),
        // The Trollshaws, west of the mountains and south of Rhudaur: higher and rougher than Eregion
        // below them, which is what makes them a hill country rather than more of the same wood.
        TROLLSHAWS(TolkienBiomes.TROLLSHAWS, 84, 16, true),
        /**
         * Imladris, the hidden valley: a floor of its own cut by hand into the Trollshaws' western edge,
         * because the city that stands in it is 554 blocks across and no cell of a 512-block drawing can
         * be its own. Its height is the floor it is drawn at and its relief two blocks, which is what
         * lets a city sit in it instead of on a hillside - see {@link #valley}.
         */
        IMLADRIS(TolkienBiomes.IMLADRIS, 74, 2, true),
        LORINAND(TolkienBiomes.LORINAND, 74, 6, true),
        FANGORN(TolkienBiomes.FANGORN, 78, 10, true),

        // Rhovanion, Mirkwood and the east.
        MIRKWOOD(TolkienBiomes.MIRKWOOD, 76, 8, true),
        MIRKWOOD_SPOOKY(TolkienBiomes.MIRKWOOD_SPOOKY, 74, 8, true),
        WILD_WOOD(TolkienBiomes.WILD_WOOD, 74, 8, true),
        GLADDEN(TolkienBiomes.GLADDEN, 68, 5, true),
        RHUN(TolkienBiomes.RHUN, 70, 7, true),

        // The south: the Mark, the white wall, Gondor and the enemy's country.
        ROHAN(TolkienBiomes.ROHAN, 72, 6, true),
        WHITE_MOUNTAINS(TolkienBiomes.WHITE_MOUNTAINS, 165, 32, true),
        GONDOR(TolkienBiomes.GONDOR, 70, 5, true),
        MARSHES(TolkienBiomes.MARSHES, 64, 3, true),
        DEAD_MARSHES(TolkienBiomes.DEAD_MARSHES, 62, 2, true),
        DAGORLAD(TolkienBiomes.DAGORLAD, 70, 6, true),
        ITHILIEN(TolkienBiomes.ITHILIEN, 74, 8, true),
        MORDOR(TolkienBiomes.MORDOR, 74, 10, true),
        ASH_MOUNTAINS(TolkienBiomes.ASH_MOUNTAINS, 150, 30, true),
        NURN(TolkienBiomes.NURN, 61, 4, true),
        HARAD(TolkienBiomes.HARADWAITH, 72, 8, true),

        // The rest of the poster: the north-east's Dale below Erebor, Eregion west of the mountains, the
        // cold Ettenmoors, the western cape's dark wood, the Brown Lands and the Emyn Muil, the warm coast
        // of Belfalas, Khand beyond Mordor's eastern wall, and the empty east canon leaves unpeopled.
        DALE(TolkienBiomes.DALE, 72, 8, true),
        EREGION(TolkienBiomes.EREGION, 82, 14, true),
        ETTENMOORS(TolkienBiomes.ETTENMOORS, 96, 18, true),
        ERYN_VORN(TolkienBiomes.ERYN_VORN, 72, 10, true),
        BROWN_LANDS(TolkienBiomes.BROWN_LANDS, 72, 6, true),
        EMYN_MUIL(TolkienBiomes.EMYN_MUIL, 96, 22, true),
        BELFALAS(TolkienBiomes.BELFALAS, 68, 5, true),
        KHAND(TolkienBiomes.KHAND, 74, 8, true),
        // The countries smaller than a cell, painted on the quarter-cell grid.
        UMBAR(TolkienBiomes.UMBAR, 70, 8, true),
        DORWINION(TolkienBiomes.DORWINION, 70, 6, true),
        MINHIRIATH(TolkienBiomes.MINHIRIATH, 70, 6, true),
        CARDOLAN(TolkienBiomes.CARDOLAN, 76, 10, true),
        ENEDWAITH(TolkienBiomes.ENEDWAITH, 74, 8, true),
        EASTERN_WASTE(TolkienBiomes.EASTERN_WASTE, 76, 12, true);

        private final ResourceKey<Biome> biome;
        private final int height;
        private final int relief;
        private final boolean land;

        Country(ResourceKey<Biome> biome, int height, int relief, boolean land) {
            this.biome = biome;
            this.height = height;
            this.relief = relief;
            this.land = land;
        }

        public ResourceKey<Biome> biome() {
            return this.biome;
        }

        public int height() {
            return this.height;
        }

        /** How far the ground is allowed to wander about {@link #height()}. */
        public int relief() {
            return this.relief;
        }

        public boolean land() {
            return this.land;
        }
    }

    /**
     * What stands at a column of the world. The warp is the world's own noise, so it is the caller's
     * to hand in: the biome source reads it from the climate sampler it is given and the generator
     * from the same sampler in its random state.
     */
    public static Sample sample(int blockX, int blockZ, DensityFunction warpX, DensityFunction warpZ) {
        return read(blockX, blockZ, warp(warpX, blockX, blockZ, 1.0D), warp(warpZ, blockX, blockZ, 1.0D));
    }

    /**
     * What stands at a column with no warp applied: the drawing as it is drawn.
     *
     * <p>The warp needs the world's own noise, which only a generator has been handed. A survey of
     * ground the world has not generated therefore reads the drawing unwarped, and the build checks
     * the ground it actually has before it lays anything down (see {@link ArdaSurvey}).
     */
    public static Sample sample(int blockX, int blockZ) {
        return read(blockX, blockZ, 0.0D, 0.0D);
    }

    /** The drawing at a column, shifted by whatever warp the caller has already read. */
    private static Sample read(int blockX, int blockZ, double shiftX, double shiftZ) {
        Sample drawn = readDrawn(blockX, blockZ, shiftX, shiftZ);
        // The Bruinen the city carries is cut into whatever the drawing and the valley answer, because it
        // runs through both: out of the valley's own floor, through the massif's flank, and into the country
        // beyond its ridge. With the city's ground not laid there is no branch to cut.
        return PLANTED ? bruinen(blockX, blockZ, drawn) : drawn;
    }

    /** The drawing at a column, before the river the build carries is cut into it. */
    private static Sample readDrawn(int blockX, int blockZ, double shiftX, double shiftZ) {
        double mapX = (blockX + shiftX) / (double) CELL + ORIGIN_COLUMN;
        double mapZ = (blockZ + shiftZ) / (double) CELL + ORIGIN_ROW;

        Ground ground = groundAt(mapX, mapZ, blockX, blockZ);

        // The valley is laid over the drawing rather than read out of it, and before the rivers:
        // nothing the maps draw as a river runs through it. It is measured in unwarped blocks on
        // purpose - the warp is there to make drawn borders organic, and a floor a city is planted on
        // by hand must not wander a hundred blocks off the middle the city was planted around.
        // A drawn river is not buried - not by the massif and not by the floor either. The Bruinen the
        // map draws runs past the valley's east side, inside the floor the city stands on, and the branch
        // out of the city has to meet it in the open: the drawing answers before the rock and before the
        // floor, so the river cuts its own channel through both and the fork is water, not a notch.
        Sample drawn = water(blockX, blockZ, ground);
        if (drawn != null) {
            return drawn;
        }
        if (PLANTED) {
            Sample valley = valley(blockX, blockZ, ground);
            if (valley != null) {
                return valley;
            }
        }
        // Water the drawing lays stays in the bed the drawing gives it: beside a drawn river or lake the
        // ground is raised to hold it, because a channel whose bank stands under the water it carries is
        // not a channel at all - the water runs out at the first tick and the country is flooded instead.
        int bank = bank(blockX, blockZ, ground.height());
        int height = Math.max(ground.height(), bank);

        // Land beside a drawn lake comes down to it as well: the bank only ever holds water in, so a shore
        // that stands eight blocks over its own sheet kept a wall where the drawing had a hill. The grade
        // never undercuts what the bank needs, and never reaches under the sea.
        if (nearLake(blockX, blockZ)) {
            int grade = shoreGrade(blockX, blockZ);

            if (grade < Integer.MAX_VALUE && height - grade <= SHORE_CUT) {
                height = Math.max(bank, grade);
            }
        }
        return new Sample(ground.country(), height, false, ground.shore(), ground.upland());
    }

    /** How far a column lies beyond the floor's rectangle, in blocks: nothing on the floor itself. */
    private static double beyond(double blockX, double blockZ) {
        double beyondX = Math.abs(blockX - VALLEY_X) - VALLEY_HALF_X;
        double beyondZ = Math.abs(blockZ - VALLEY_Z) - VALLEY_HALF_Z;
        return Math.hypot(Math.max(0.0D, beyondX), Math.max(0.0D, beyondZ));
    }

    /**
     * The water the drawing puts at a column, or null where it draws none.
     * <p>
     * The masks answer where a river or a lake is, and the top of the water is the ground the drawing gives
     * the column: the drawing's rivers and lakes are already drawn at their own level, so water follows the
     * land down instead of standing at a level of its own. Averaging the ground over the country round the
     * column was tried and put water on top of the land wherever the drawing dips - a lake sheet three or
     * four blocks over its own shore. The bed is cut under that surface, and a drawn river's bed slopes from
     * its middle out to its banks, because one depth for a sixteen-block-wide drawing puts a cliff at both.
     */
    private static Sample water(int blockX, int blockZ, Ground ground) {
        if (ArdaMasks.lake(blockX, blockZ)) {
            // One level for the whole sheet. Laid at the ground the drawing gives each column, a lake
            // inherits every rise and fall under it and comes out as a staircase of water - the sheet the
            // player walks down like a waterfall. A lake is a level surface, so the level is measured once
            // per sheet and the bed under it is cut to it.
            int surface = lakeLevel(blockX, blockZ, ground.height());
            int sheet = ArdaMasks.lakeId(blockX, blockZ);

            // A column drawn as lake that stands well above the sheet is a headland, not water: cutting it
            // down to the bed is what put a wall of stone at the edge of Nurnen. It stays land - but it walks
            // down to the water rather than standing over it, so a rise inside the sheet ends in a shore.
            if (sheet > 0 && ground.height() > surface + LAKE_SHORE) {
                int grade = shoreGrade(blockX, blockZ);
                int height = grade < Integer.MAX_VALUE && ground.height() - grade <= SHORE_CUT
                        ? grade
                        : ground.height();

                return new Sample(ground.country(), height, false, ground.shore(), ground.upland());
            }

            // The bed is graded by how much of the column the sheet covers, so the water goes from wading
            // depth at the mask's edge to the full depth inside it over the sheet's own edge instead of
            // stopping at a flat shelf and a wall. A bed cut to two values - shallow at the rim, deep in the
            // middle - is what showed through the water as terraces.
            double cover = Math.max(0.0D, Math.min(1.0D, ArdaMasks.lakeCoverage(blockX, blockZ)));
            int depth = 1 + (int) Math.round(cover * (LAKE_DEPTH - 1));
            int bed = Math.min(ground.height(), surface - depth);
            return new Sample(ground.country(), bed, true, false, ground.upland(), surface);
        }
        if (ArdaMasks.river(blockX, blockZ)) {
            // Near a coast the ground's grade falls under the sea; held there, the last stretch of a
            // river meets the sea instead of digging a trench beside it.
            int surface = Math.max(SEA_LEVEL, ground.height());
            // As deep as it is wide, within what a channel can be without becoming a canyon - and never
            // deeper than the column stands from its own bank, so the water shallows out to its edge
            // instead of ending in a wall. A bed cut flat from bank to bank makes the river a square hole
            // in the ground, because every column of it holds the same depth of water right up to the
            // land; a bed that rises a block per block to each bank is the riverside a person wades in.
            int deep = ArdaMasks.riverDepth(blockX, blockZ,
                    Math.min(drawnRiverWidth(blockX, blockZ), RIVER_DEPTH_MAX));
            return new Sample(ground.country(), Math.min(ground.height(), surface - deep), true,
                    false, ground.upland(), surface);
        }
        return null;
    }

    /**
     * The hidden valley, or null for ground the drawing answers for.
     * <p>
     * The floor is flat at {@link #VALLEY_FLOOR} and is the valley's own country. Round it the mountain's
     * rock stands at {@link #VALLEY_CROWN} - the crown of the build - and climbs from there, measured
     * outward from the floor's own rectangle so its foot is the walls themselves, with rounded corners and
     * a ridge that wanders and changes height - because a massif whose foot follows four straight sides at
     * one distance and tops out at one fixed summit is a box with a pyramid in it.
     * Beyond the ridge the ground is handed back to the drawn country over {@link #VALLEY_FALL}.
     * <p>
     * The rock stands at the crown from the floor's own edge outward, which is what lays the lowest band
     * of the build into the mountain instead of setting it down beside it.
     */
    /**
     * The water level of a lake sheet, or the drawn ground where the sheet is one the bake does not know.
     * <p>
     * The level is the lowest drawn ground over the sheet, sampled at 128 blocks, so the water sits in the basin the drawing gives it.
     */
    private static int lakeLevel(int blockX, int blockZ, int fallback) {
        int id = ArdaMasks.lakeId(blockX, blockZ);
        int[] levels = LakeLevels.LEVELS;

        if (id > 0 && id < levels.length && levels[id] > 0) {
            return levels[id];
        }
        return Math.max(SEA_LEVEL, fallback);
    }

    /**
     * The height the ground beside a column has to stand at to keep the water beside it in its bed, or
     * {@link Integer#MIN_VALUE} where no drawn water stands within {@link #BANK_REACH}.
     * <p>
     * Read outward in eight directions until a column of the river or lake mask is met, and from that
     * column's water surface - the drawing lays water at the height of its own ground, never below the sea
     * - comes the level the bank has to reach. Where two rivers run close the higher of them sets the
     * bank, because the lower one's water is already inside the higher one's reach.
     */
    private static int bank(int blockX, int blockZ, int height) {
        // Ground under the waterline is the sea's own bed, not a bank: lifting it would put a wall of
        // land across the mouth of every river that reaches the coast.
        if (height < SEA_LEVEL) {
            return Integer.MIN_VALUE;
        }
        int need = Integer.MIN_VALUE;
        for (int step = 0; step < 8; step++) {
            double dirX = Math.cos(step * Math.PI / 4.0D);
            double dirZ = Math.sin(step * Math.PI / 4.0D);
            for (int reach = 2; reach <= BANK_REACH; reach += 2) {
                int px = blockX + (int) Math.round(dirX * reach);
                int pz = blockZ + (int) Math.round(dirZ * reach);
                if (!ArdaMasks.river(px, pz) && !ArdaMasks.lake(px, pz)) {
                    continue;
                }
                int drawn = groundAt(px / (double) CELL + ORIGIN_COLUMN,
                        pz / (double) CELL + ORIGIN_ROW, px, pz).height();
                // A lake's bank has to reach its one level, not the ground of the column the mask happens
                // to hold there, or the bank is as stepped as the sheet used to be.
                int surface = ArdaMasks.lake(px, pz) ? lakeLevel(px, pz, drawn) : Math.max(SEA_LEVEL, drawn);
                need = Math.max(need, surface + 1 - Math.max(0, reach - (BANK_REACH - BANK_FALL)));
                break;
            }
        }
        return need;
    }

    /** Whether drawn lake water lies within a dozen blocks of a column, tested cheaply before the scan. */
    private static boolean nearLake(int blockX, int blockZ) {
        for (int step = 0; step < 8; step++) {
            int px = blockX + (int) Math.round(Math.cos(step * Math.PI / 4.0D) * BANK_REACH);
            int pz = blockZ + (int) Math.round(Math.sin(step * Math.PI / 4.0D) * BANK_REACH);
            if (ArdaMasks.lake(px, pz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The highest a column of lake shore may stand and still meet its sheet as a shore, or
     * {@link Integer#MAX_VALUE} where no lake water lies within {@link #BANK_REACH}.
     * <p>
     * A lake is one level, so its shore cannot be graded against the land's own slope: the number comes from
     * the water instead, one block up where the water is met and one more for every block further out. That
     * turns the wall a drawn headland used to end in into the beach its sheet should have had.
     */
    private static int shoreGrade(int blockX, int blockZ) {
        // Sixteen directions, one block at a time. Eight rays read outward count a diagonal step as one
        // rather than one and a half, and a ramp drawn on that is a square ring round the water however
        // smooth the drawing under it is.
        for (int reach = 1; reach <= BANK_REACH; reach++) {
            for (int step = 0; step < 16; step++) {
                double angle = step * Math.PI / 8.0D;
                int px = blockX + (int) Math.round(Math.cos(angle) * reach);
                int pz = blockZ + (int) Math.round(Math.sin(angle) * reach);

                if (!ArdaMasks.lake(px, pz)) {
                    continue;
                }
                int drawn = groundAt(px / (double) CELL + ORIGIN_COLUMN,
                        pz / (double) CELL + ORIGIN_ROW, px, pz).height();

                // A neighbour that is itself a headland is land, not the sheet: the shore is the water.
                if (drawn > lakeLevel(px, pz, drawn) + LAKE_SHORE) {
                    continue;
                }
                return lakeLevel(px, pz, drawn) + 1 + Math.max(0, reach - 2);
            }
        }
        return Integer.MAX_VALUE;
    }

    /**
     * One water level per drawn lake sheet, measured once from the ground the sheet lies on.
     * <p>
     * The map is sampled at 128 blocks rather than column by column, so a whole world's lakes are measured
     * in one pass at a cost a chunk's worth of generation would not notice. The level is the lowest ground the sheet is drawn over, not its mean: ground standing over it is land, and a level taken from higher up cuts the sheet into a dam.
     */
    private static final class LakeLevels {
        private static final int[] LEVELS = measure();

        private static int[] measure() {
            byte[] sheets = ArdaMasks.lakeSheets();
            int width = ArdaMasks.maskWidth();
            int height = ArdaMasks.maskHeight();
            int blocks = ArdaMasks.maskBlocks();
            int west = ArdaMasks.maskWest();
            int north = ArdaMasks.maskNorth();
            int highest = 0;

            for (byte sheet : sheets) {
                highest = Math.max(highest, sheet & 0xFF);
            }

            int[] lows = new int[highest + 1];
            java.util.Arrays.fill(lows, Integer.MAX_VALUE);
            int[] counts = new int[highest + 1];
            for (int row = 0; row < height; row += 16) {
                for (int column = 0; column < width; column += 16) {
                    int id = sheets[row * width + column] & 0xFF;

                    if (id == 0) {
                        continue;
                    }

                    int x = west + column * blocks;
                    int z = north + row * blocks;
                    lows[id] = Math.min(lows[id], groundAt(x / (double) CELL + ORIGIN_COLUMN,
                            z / (double) CELL + ORIGIN_ROW, x, z).height());
                    counts[id]++;
                }
            }

            int[] levels = new int[highest + 1];
            for (int id = 1; id <= highest; id++) {
                if (counts[id] > 0) {
                    levels[id] = Math.max(SEA_LEVEL, lows[id]);
                }
            }
            // How far the drawn ground rises over each sheet's level: the number the shore band is tuned
            // by. A sea whose ground climbs far over its own level is a sea on a hillside, and this says so
            // once, at load, rather than leaving it to be found as a cliff.
            int[] risen = new int[highest + 1];
            int[] within = new int[highest + 1];
            int[] seen = new int[highest + 1];
            for (int row = 0; row < height; row += 16) {
                for (int column = 0; column < width; column += 16) {
                    int id = sheets[row * width + column] & 0xFF;

                    if (id == 0) {
                        continue;
                    }

                    int x = west + column * blocks;
                    int z = north + row * blocks;
                    int rise = groundAt(x / (double) CELL + ORIGIN_COLUMN, z / (double) CELL + ORIGIN_ROW, x, z).height()
                            - levels[id];
                    risen[id] = Math.max(risen[id], rise);
                    seen[id]++;

                    if (rise <= LAKE_SHORE) {
                        within[id]++;
                    }
                }
            }
            for (int id = 1; id <= highest; id++) {
                if (seen[id] > 0) {
                    com.mojang.logging.LogUtils.getLogger().info(
                            "Arda lake sheet {}: level {}, drawn ground rises to +{} over it, {}/{} samples ({}%) within {} blocks",
                            id, levels[id], risen[id], within[id], seen[id],
                            Math.round(100.0D * within[id] / seen[id]), LAKE_SHORE);
                }
            }
            return levels;
        }
    }

    private static Sample valley(double blockX, double blockZ, Ground ground) {
        // A distance from the floor's rectangle, not one from each of its four sides: the difference is
        // at the corners, where this rounds the mountain's foot instead of squaring it off.
        double beyond = beyond(blockX, blockZ);
        int x = (int) Math.floor(blockX);
        int z = (int) Math.floor(blockZ);
        // The ridge is not a ring at one height: it wanders in and out, and its summits rise and fall
        // along it, which is the whole difference between a range of mountains and the rim of a bowl.
        double ridge = VALLEY_RIM * (1.0D + RIDGE_SWING * (fractal(x, z, RIDGE_SCALE, 31) - 0.5D) * 2.0D);
        double crest = VALLEY_CREST + CREST_SWING * (fractal(x, z, CREST_SCALE, 43) - 0.5D) * 2.0D;
        // The crown rolls a little below its own height, so the summit is a surface and not a plane.
        double crown = VALLEY_CROWN - CROWN_ROLL * fractal(x, z, RIDGE_SCALE, 59);
        if (beyond > ridge + VALLEY_FALL) {
            return null;
        }
        int height;
        Country country = ground.country();
        if (beyond <= 0.0D) {
            country = Country.IMLADRIS;
            height = VALLEY_FLOOR;
        } else if (beyond <= ridge) {
            // Ribs and gullies, faded in from the floor: without them the flank is a contour map of the
            // city's own footprint, and a contour every couple of blocks is a terrace.
            double ribs = (fractal(x, z, CLIMB_SCALE, 17) - 0.5D) * 2.0D * CLIMB_ROUGH
                    * Math.min(1.0D, beyond / CLIMB_EASE)
                    * Math.min(1.0D, (ridge - beyond) / CLIMB_EASE);
            // The rock stands at the crown where it meets the build and climbs from there to the ridge:
            // the ground outside the walls is a continuation of the ground inside them, and the mountain
            // grows out of the roofline instead of out of a field below it.
            height = (int) Math.round(crown + (crest - crown) * climb(beyond / ridge) + ribs);
        } else {
            double fell = Math.min(1.0D, (beyond - ridge) / (double) VALLEY_FALL);
            height = (int) Math.round(crest + (ground.height() - crest) * smooth(fell));
        }
        // Water at the foot of the crown is what turns a face into a wall: the ground falls away from any
        // water it stands beside, so the massif opens into a valley wherever the rivers reach it.
        return new Sample(country, giveWay(x, z, height), false, false, ground.upland());
    }

    /**
     * The height the massif round the valley reaches at its highest, which is the room a world must
     * have for it.
     * <p>
     * A dimension's height is fixed when its world is made, so a save copied from one made before Arda
     * was raised keeps the old ceiling, and every summit drawn above it is cut off flat. The generator
     * reads this to say so once, rather than leaving it to look like a range that stops in the sky.
     */
    /**
     * Raises the valley, the massif and the Bruinen together to stand as high as the world allows.
     * <p>
     * The sky is the highest block the world will hold, so the build's crown - the rock's crown with it -
     * is set one block under that and everything else moves up by the same amount: the floor from 74,
     * the crown from 318, the outlet's water from 82, and the shoulder with them. Where a world has no
     * room - a save made before Arda was raised is 384 blocks tall - the lift is nothing, and the ground
     * stays exactly where that save was made with.
     */
    static synchronized void liftTo(int sky) {
        int lift = Math.max(0, sky - 1 - PLANTED_CROWN);
        if (lift == VALLEY_LIFT) {
            return;
        }
        VALLEY_LIFT = lift;
        VALLEY_FLOOR = PLANTED_FLOOR + lift;
        VALLEY_CROWN = PLANTED_CROWN + lift;
        VALLEY_CREST = VALLEY_CROWN;
        SHOULDER_TOP = VALLEY_CROWN;
        BRUINEN_WATER = PLANTED_FLOOR + 8 + lift;
        bruinenProfile = null;
    }

    /** How far the valley has been raised above where the city was planted, in blocks. */
    static int valleyLift() {
        return VALLEY_LIFT;
    }

    static int massifTop() {
        return VALLEY_CREST + CREST_SWING;
    }

    /**
     * The height the valley's floor stands at: where a city in it is planted from, whatever the climb
     * beside its walls has done.
     */
    public static int valleyFloor() {
        return VALLEY_FLOOR;
    }

    /**
     * How much of the way up the mountain the ground has climbed at a share of the distance to the ridge.
     * <p>
     * Steepest at the foot and rounded at the top: the ground starts climbing a lawn's width out from the
     * city's walls, gains most of its height in the first half of the climb, and eases into the ridge so
     * the top is a shoulder rather than a point.
     */
    private static double climb(double share) {
        double t = Math.min(1.0D, share);
        return 1.0D - Math.pow(1.0D - t, 1.2D);
    }

    /**
     * The water's own height along the Bruinen's course, read off the ground and kept.
     * <p>
     * Water does not run uphill, so the surface steps down the course and never back up: read forward from
     * the outlet, it holds the build's own level at {@link #BRUINEN_WATER} until the ground falls under it
     * and takes the ground from there. Nothing is set at the ford - the river arrives at grade if the
     * ground has come down to it, and the cut carries it the rest of the way if it has not.
     * <p>
     * Read once and kept, because a column of the gorge would otherwise walk the whole course.
     */
    private static int bruinenWater(double share) {
        int[] profile = bruinenProfile;
        if (profile == null) {
            synchronized (BRUINEN_LOCK) {
                profile = bruinenProfile;
                if (profile == null) {
                    profile = readBruinenProfile();
                    bruinenProfile = profile;
                }
            }
        }
        return profile[Mth.clamp((int) Math.round(share * BRUINEN_STEPS), 0, BRUINEN_STEPS)];
    }

    private static int[] readBruinenProfile() {
        int[] profile = new int[BRUINEN_STEPS + 1];
        int lowest = BRUINEN_WATER;
        profile[0] = lowest;
        for (int step = 1; step <= BRUINEN_STEPS; step++) {
            double share = step / (double) BRUINEN_STEPS;
            int x = (int) Math.round(courseX(share));
            int z = (int) Math.round(courseZ(share));
            lowest = Math.min(lowest,
                    groundAt(x / (double) CELL + ORIGIN_COLUMN, z / (double) CELL + ORIGIN_ROW, x, z).height());
            profile[step] = lowest;
        }
        return profile;
    }

    /**
     * The Bruinen's gorge at a column, or the ground unchanged where the gorge does not reach.
     * <p>
     * The channel is cut into whatever stands there rather than drawn over it: the massif's flank and the
     * shoulder where the valley answers, the drawn country beyond the ridge where the valley has handed the
     * ground back, so the same cut opens the front of the valley and runs out to the ford. Its water falls
     * along the course, its bed lies {@link #BRUINEN_BED} under that, and its walls climb from the water at
     * {@link #BRUINEN_BANK} blocks for each block out. The climb stops where the natural ground is lower than
     * the wall, which is what leaves the gorge an open mouth rather than a trench with a lid on it.
     * <p>
     * The cut begins at the outlet rather than a few blocks past it, because the build's own channel is there
     * and the city plants its lowest band over the floor; and it shallows over the last stretch, because the
     * ford is where the river is waded rather than a place a channel can end in a wall.
     */
    private static Sample bruinen(double blockX, double blockZ, Sample ground) {
        double[] at = project(blockX, blockZ);
        if (at[5] > 0.5D || at[6] > 0.5D) {
            // Behind the mouth is the city's own water; past the join is the Bruinen's.
            return ground;
        }
        double share = at[0];
        // Measured from the course's own middle rather than from the legs between its points, so the channel
        // and the ground round it bend with the river.
        double offsetX = blockX - courseX(share);
        double offsetZ = blockZ - courseZ(share);
        // Measured across the course rather than as a radius: the river is twelve blocks wide and is not
        // the same width either side of it, so the bank is found by which way the column lies of the flow.
        double side = offsetX * -at[4] + offsetZ * at[3];
        double level = bruinenWater(share);
        double shallow = smooth(Mth.clamp((share - BRUINEN_FORD_SHALLOW) / (1.0D - BRUINEN_FORD_SHALLOW),
                0.0D, 1.0D));
        // The channel narrows as it comes to the ford, so the water ends in a crossing a person can wade
        // rather than in a square of water with a straight edge.
        double bank = 1.0D - BRUINEN_FORD_NARROW * shallow;
        double left = BRUINEN_LEFT * bank;
        double right = BRUINEN_RIGHT * bank;
        double out = side > 0.0D ? side - right : -left - side;
        int height;
        if (out <= 0.0D) {
            // The bed slopes from the middle of the channel up to the water's edge instead of lying flat
            // with a wall at the bank. A flat bed under a river is a square of water cut into the ground:
            // every block of it is the same seven under the surface, so the edge is a cliff and the
            // shallow water a person wades at is nowhere.
            double reach = Math.min(1.0D, Math.abs(side) / Math.max(1.0D, side > 0.0D ? right : left));
            double deep = 1.0D - reach * reach;
            // A branch that shallows to nothing stops being a river before it reaches the water it feeds,
            // so the ford keeps two blocks under it however far into the shallow it has come, and the
            // slope down to it is gentler than the bank's.
            double under = Math.max(BRUINEN_FORD_DEPTH,
                    BRUINEN_BED * deep * (1.0D - shallow * BRUINEN_FORD_SHALLOWING));
            height = (int) Math.round(level - under);
        } else {
            int x = (int) Math.floor(blockX);
            int z = (int) Math.floor(blockZ);
            // The same ribs and gullies the massif's flank has, faded in from the water: a wall whose height
            // is a function of distance alone draws the same contour at every level, and contours two blocks
            // apart are terraces.
            double ribs = (fractal(x, z, CLIMB_SCALE, 23) - 0.5D) * 2.0D * CLIMB_ROUGH
                    * Math.min(1.0D, out / CLIMB_EASE);
            double wall = Math.max(level, level + out * BRUINEN_BANK + ribs);
            height = (int) Math.round(Math.min(ground.height(), wall));
        }
        // Only the channel carries the river's own level: the walls stand above it, and the valley floor
        // beside the mouth stands under it, so a floor given this level as its water would be flooded to
        // the height of the river thirty blocks away from it.
        int water = out <= 0.0D ? (int) Math.round(level) : SEA_LEVEL;
        return new Sample(ground.country(), height, height < level, false, ground.upland(), water);
    }


    /**
     * The massif's own height, given way to the water it stands beside, in blocks.
     * <p>
     * The rock stands at the crown where the city's floor ends - which the build's own walls hide - but the
     * drawn Bruinen runs past the valley's east side at the floor's level, and the branch out of the city
     * meets it there: rock two hundred and forty blocks high beside water at seventy-four is not a valley a
     * river runs through, it is a slot cut in a plateau. So the ground climbs away from any water at
     * {@link #MOUNTAIN_SLOPE}, and both rivers open the massif into a valley instead of a trench.
     * <p>
     * The nearest water is the branch's own course, measured exactly, or the nearest drawn river column found
     * in eight directions: the walk out to it is the bank's run, and its surface is the level the ground has
     * to come down to. Nothing is done where the column already stands no higher than that.
     */
    private static int giveWay(int blockX, int blockZ, int height) {
        // The course is measured where it runs and off its own ends, because its ends are water too: the
        // city's own water at the mouth and the Bruinen's at the join. Reading a column beyond an end as
        // having no water at all closed the massif over the river's own mouth, and the flat crown then
        // stood at 318 three blocks from water at 82 - the wall this cone was written to remove.
        double[] at = project(blockX, blockZ);
        double run = at[2] - BRUINEN_RIGHT;
        int level = bruinenWater(at[0]);
        boolean drawn = false;
        for (int step = 0; step < 8; step++) {
            double dirX = Math.cos(step * Math.PI / 4.0D);
            double dirZ = Math.sin(step * Math.PI / 4.0D);
            for (int reach = 4; reach <= WATER_REACH; reach += 4) {
                int px = blockX + (int) Math.round(dirX * reach);
                int pz = blockZ + (int) Math.round(dirZ * reach);
                if (!ArdaMasks.river(px, pz)) {
                    continue;
                }
                double away = reach - 2.0D;
                if (away < run) {
                    drawn = true;
                    run = away;
                    level = Math.max(SEA_LEVEL, groundAt(px / (double) CELL + ORIGIN_COLUMN,
                            pz / (double) CELL + ORIGIN_ROW, px, pz).height());
                }
                break;
            }
        }
        // The course's own distance is exact however far a column stands from it, so the rock can give way to
        // the river the whole way up its bank. A drawn river is only found within the reach above, and a cone
        // cut off at that reach would leave the step it was cut to remove - so the drawn river's own banks
        // stay as they are until its distance can be read as far.
        if (drawn && run >= WATER_REACH) {
            return height;
        }
        double limit = level + Math.max(0.0D, run) * MOUNTAIN_SLOPE;
        return limit >= height ? height : (int) Math.round(limit);
    }

    /**
     * Moria's roof, in blocks, and the rectangle of the range it lies under.
     * <p>
     * The halls are not a country of the drawing: the surface over them is the Misty Mountains, which is
     * what the poster draws, and the map has no cell at a depth. So the biome source asks this instead -
     * rock under these columns, below this height - and the surface keeps its own biome and its own ground.
     * The rectangle is the stretch of the range above the Dimrill Dale, west of Lorien's north.
     */
    private static final int MORIA_ROOF = 40;
    private static final int MORIA_X0 = 5120;
    private static final int MORIA_X1 = 6144;
    private static final int MORIA_Z0 = -4096;
    private static final int MORIA_Z1 = -2304;

    static int moriaRoof() {
        return MORIA_ROOF;
    }

    /** Whether a column stands over Moria's halls. */
    static boolean underMoria(int blockX, int blockZ) {
        return blockX >= MORIA_X0 && blockX <= MORIA_X1 && blockZ >= MORIA_Z0 && blockZ <= MORIA_Z1;
    }

    /**
     * The room the halls are hollowed into: its floor, and the rectangle of it under the range.
     * <p>
     * A rectangle of rock under a mountain is not a place. The biome was given to the rock below the roof
     * and the rock stayed rock, so the halls could only be found on the debug screen at a height a player
     * cannot stand at - which is what a country that is all stone looks like. This is a chamber cut out of
     * that rock: the ground the halls will be built on, and somewhere to stand while they are looked at.
     * It is narrower than the rock the biome covers, so the mountain keeps its feet.
     */
    private static final int MORIA_FLOOR = 14;
    private static final int HALL_X0 = 5376;
    private static final int HALL_X1 = 5888;
    private static final int HALL_Z0 = -3712;
    private static final int HALL_Z1 = -2688;

    static int moriaFloor() {
        return MORIA_FLOOR;
    }

    /** Whether a block is inside the hollowed halls, between their floor and the roof of the rock. */
    public static boolean moriaHalls(int blockX, int y, int blockZ) {
        return y > MORIA_FLOOR && y < MORIA_ROOF
                && blockX >= HALL_X0 && blockX <= HALL_X1 && blockZ >= HALL_Z0 && blockZ <= HALL_Z1;
    }

    /** Whether a cell of the map exists at all, since a search may walk off its edge. */
    static boolean inside(int column, int row) {
        return column >= 0 && column < COLUMNS && row >= 0 && row < ROWS;
    }

    /**
     * How wide the drawn channel is at a column, in blocks.
     * <p>
     * The mask is a drawing, not a distance field, so the bank is found by walking out in blocks until
     * the drawing stops being water; the narrowest of the four walks is across the channel, because along
     * it the water runs on. A river as deep as it is wide is the shape water cuts, and walking in blocks
     * keeps the bed rising a block per block to each bank instead of stepping.
     */
    private static int drawnRiverWidth(double blockX, double blockZ) {
        return ArdaMasks.channelWidth((int) Math.floor(blockX), (int) Math.floor(blockZ));
    }

    /**
     * The country at the middle of one cell, read off the ground rather than off the drawing.
     * <p>
     * {@link #cellCountry} answers what the 48 by 36 grid paints in a cell, and it is the wrong answer
     * for the valley: Imladris is laid over cells the drawing gives to the Trollshaws, so it has no cell
     * of its own and a survey looking for it would be refused every cell in the map. This asks the same
     * column the generator will shape, which is the valley's own answer where the valley is.
     */
    static ArdaMap.Country countryAt(int column, int row) {
        BlockPos centre = centreOf(column, row);
        return sample(centre.getX(), centre.getZ()).country();
    }

    /** The country drawn in one cell, which is the whole of what a survey needs to begin. */
    static ArdaMap.Country cellCountry(int column, int row) {
        return GRID[Mth.clamp(row, 0, ROWS - 1)][Mth.clamp(column, 0, COLUMNS - 1)];
    }

    /**
     * The middle of a country's drawn ground, as a point in the world.
     * <p>
     * The maps put a name in the middle of a country rather than at a corner of it, and the middle of
     * the cells a country was drawn in is the nearest thing this drawing has to that. It is where a
     * landmark the books place is looked for - Weathertop is Amon Sul, a hill in the heart of Eriador -
     * so where the landmark stands stops depending on where in the world the page was taken.
     */
    public static BlockPos anchor(Country country) {
        // The valley is not one of the grid's cells, so it has no middle to average: its own numbers
        // are its middle, and a name the maps put in it is surveyed from there.
        if (country == Country.IMLADRIS) {
            return new BlockPos(VALLEY_X, 0, VALLEY_Z);
        }
        long columns = 0;
        long rows = 0;
        int cells = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (GRID[row][column] != country) continue;
                columns += column;
                rows += row;
                cells++;
            }
        }
        // A country the drawing does not contain has no middle; the origin is the honest answer, and
        // the survey's own country test refuses it a moment later.
        if (cells == 0) {
            // A country painted on the patch grid has no cell of its own: its middle is the middle of the
            // patches it was drawn in, read back into blocks at the patch scale.
            long patchColumns = 0;
            long patchRows = 0;
            int count = 0;
            for (Patch patch : PATCHES) {
                if (patch.country() != country) continue;
                patchColumns += patch.fromColumn() + patch.toColumn() + 1;
                patchRows += patch.fromRow() + patch.toRow() + 1;
                count++;
            }
            if (count == 0) return BlockPos.ZERO;
            int size = CELL / PATCHES_PER_CELL;
            return new BlockPos((int) Math.round((patchColumns / (2.0D * count) - ORIGIN_COLUMN * PATCHES_PER_CELL) * size), 0,
                    (int) Math.round((patchRows / (2.0D * count) - ORIGIN_ROW * PATCHES_PER_CELL) * size));
        }
        int column = (int) Math.round((double) columns / cells);
        int row = (int) Math.round((double) rows / cells);
        return new BlockPos((column - ORIGIN_COLUMN) * CELL + CELL / 2, 0,
                (row - ORIGIN_ROW) * CELL + CELL / 2);
    }

    /**
     * The middle of one cell of the drawing, as a point in the world.
     * <p>
     * {@link #anchor} answers where a country is - the middle of all the cells it was drawn in - which
     * is the right answer for a country and the wrong one for a place: three names in a country of six
     * cells all survey from that one point and all come back in the cell it lands in. A name the maps
     * pinpoint asks for its own cell instead, and this is how that cell is named in blocks.
     */
    public static BlockPos centreOf(int column, int row) {
        return new BlockPos((column - ORIGIN_COLUMN) * CELL + CELL / 2, 0,
                (row - ORIGIN_ROW) * CELL + CELL / 2);
    }

    /**
     * The drawn ground at a block coordinate, read off the four cells around it.
     *
     * <p>The height of the rock there is the four cells' heights blended with a smoothstep, so a
     * cell is flat at its own edge and slopes where two countries meet, plus the ground's own
     * roughness. The blend also answers what the water is doing: whether a drawn sea is within reach
     * of the column at all, and - of the land countries the blend reads - which one the ground is
     * most nearly a part of.
     */
    private static Ground groundAt(double mapX, double mapZ, int blockX, int blockZ) {
        int column = Mth.clamp((int) Math.floor(mapX), 0, COLUMNS - 1);
        int row = Mth.clamp((int) Math.floor(mapZ), 0, ROWS - 1);
        double fractionX = Mth.clamp(mapX - Math.floor(mapX), 0.0D, 1.0D);
        double fractionZ = Mth.clamp(mapZ - Math.floor(mapZ), 0.0D, 1.0D);

        Country northWest = GRID[row][column];
        Country northEast = GRID[row][Math.min(column + 1, COLUMNS - 1)];
        Country southWest = GRID[Math.min(row + 1, ROWS - 1)][column];
        Country southEast = GRID[Math.min(row + 1, ROWS - 1)][Math.min(column + 1, COLUMNS - 1)];

        double x = smooth(fractionX);
        double z = smooth(fractionZ);
        double top = northWest.height() + (northEast.height() - northWest.height()) * x;
        double bottom = southWest.height() + (southEast.height() - southWest.height()) * x;
        int drawn = Mth.clamp((int) Math.round(top + (bottom - top) * z), -60, 300);

        // The four cells again, this time for what they cover more than how high they stand: the
        // heaviest land country beside a column is the one its dry ground belongs to.
        Country[] cells = {northWest, northEast, southWest, southEast};
        double[] weights = {(1.0D - x) * (1.0D - z), x * (1.0D - z), (1.0D - x) * z, x * z};
        Country upland = northWest;
        double heaviest = -1.0D;
        boolean seaInReach = false;
        for (int index = 0; index < cells.length; index++) {
            if (!cells[index].land()) {
                seaInReach = true;
            } else if (weights[index] > heaviest) {
                heaviest = weights[index];
                upland = cells[index];
            }
        }

        // Three octaves of the map's own hash noise: enough to break a drawn flat into country
        // without moving the country itself.
        Country cover = patchAt(mapX, mapZ);
        if (cover == null) cover = northWest;
        int relief = cover.relief();
        double rough = fractal(blockX, blockZ, 192, 1) + 0.5D * fractal(blockX, blockZ, 96, 2)
                + 0.25D * fractal(blockX, blockZ, 48, 3);
        // The ground is the reference heightmap where it has land, and the drawing where it has sea:
        // an ocean column is the drawing's, and the map's own zero would put it at the sea's height.
        int height = groundHeight(blockX, blockZ, drawn, relief, rough);
        boolean shore = seaInReach && Math.abs(drawn - SEA_LEVEL) <= BEACH_HEIGHT;
        return new Ground(cover, height, drawn, shore, upland);
    }

    /**
     * The ground at a column: the reference heightmap where it has land, the drawing where it has sea.
     * <p>
     * The heightmap is read from the sea's own height up to the crown, and the mountain mask then raises
     * the ground toward the crown by its brightness, pinning its whitest pixels there: the ranges stand
     * where the mask draws them, and a summit is the city's own crown, so drawn rock and built rock reach
     * the same sky. That climb alone is a cone, because the brightness it is read from is blurred - so the
     * ridges are broken by {@link #ragged} before they are answered. A column the map calls sea keeps the
     * drawing's answer, because the map's zero is the sea's height and a bed at that height is a dry ocean.
     */
    private static int groundHeight(int blockX, int blockZ, int drawn, int relief, double rough) {
        int mapped = ArdaMasks.ground(blockX, blockZ);
        if (mapped != ArdaMasks.NO_GROUND) {
            return ragged(blockX, blockZ, ArdaMasks.lift(blockX, blockZ, vale(blockX, blockZ,
                    SEA_LEVEL + (int) Math.round(mapped / 65535.0D * (VALLEY_CROWN - SEA_LEVEL))),
                    VALLEY_CROWN));
        }
        return ragged(blockX, blockZ, ArdaMasks.lift(blockX, blockZ,
                Mth.clamp((int) Math.round(drawn + relief * (rough / 1.75D - 0.5D) * 2.0D), -60,
                        MOUNTAIN_CEILING), VALLEY_CROWN));
    }

    /**
     * A lifted column broken into ridge and gully by the map's own noise, so a range has crests and spurs
     * instead of the smooth cone its blurred brightness gradient makes.
     * <p>
     * The noise is folded - one less the absolute value of it, which is what turns a wandering value into a
     * line of sharp crossings - so its crests are ridges rather than dunes, and it is read at two scales: the
     * range's, a hundred blocks across, and the crag's, thirty. The amplitude follows the square of the
     * mask's brightness, so the ground near a range's foot moves barely a block while its flanks break into
     * several; a plain with no rock on it is left exactly as it was drawn.
     */
    private static int ragged(int blockX, int blockZ, int height) {
        double rock = ArdaMasks.rock(blockX, blockZ);
        if (rock <= 0.05D) {
            return height;
        }
        double crest = 1.0D - Math.abs(fractal(blockX, blockZ, 128, 11));
        crest += 0.5D * (1.0D - Math.abs(fractal(blockX, blockZ, 32, 12)));
        double shift = (crest / 1.5D - 0.5D) * CRAG * rock * rock;
        return Mth.clamp(height + (int) Math.round(shift), -60, MOUNTAIN_CEILING);
    }

    /**
     * The country brought down, or up, to meet the valley the city stands in.
     * <p>
     * Past the massif's own foot the heightmap is blended toward the height the city's water sits at, over
     * {@link #VALLEY_VALE} blocks, so the mountain rises out of a vale instead of out of a wall and the river
     * leaving it has ground to run on rather than a slot cut through a plateau. It works either way: where
     * the map is above the city the country comes down to it, and where the city has been raised the
     * country rises to meet it, so a world of either height has a valley that belongs to it.
     */
    private static int vale(double blockX, double blockZ, int height) {
        double beyond = beyond(blockX, blockZ);
        double share = (beyond - (VALLEY_RIM + VALLEY_FALL)) / (double) VALLEY_VALE;
        if (share >= 1.0D) {
            return height;
        }
        double weight = 1.0D - smooth(Mth.clamp(share, 0.0D, 1.0D));
        // The vale's floor is the city's own floor. It used to be eight blocks above it, which put the
        // country's lowest ground higher than the valley's water: a river could not leave the valley,
        // because the water sitting in the floor was already below everything it had to reach.
        int floor = VALLEY_FLOOR;
        return (int) Math.round(floor * weight + height * (1.0D - weight));
    }

    /** The drawn ground at a column, before it is turned into a {@link Sample}. */
    private record Ground(Country country, int height, int drawn, boolean shore, Country upland) {
    }

    private static double smooth(double value) {
        return value * value * (3.0D - 2.0D * value);
    }

    /** The warp for one axis, read from the climate sampler's own noise. */
    private static double warp(DensityFunction noise, int blockX, int blockZ, double scale) {
        return noise.compute(new DensityFunction.SinglePointContext(blockX, 0, blockZ)) * WARP * scale;
    }

    /** Value noise in [0, 1] at a block position, on a lattice of the given size. */
    private static double fractal(int blockX, int blockZ, int size, int salt) {
        int cellX = Math.floorDiv(blockX, size);
        int cellZ = Math.floorDiv(blockZ, size);
        double fractionX = smooth(Math.floorMod(blockX, size) / (double) size);
        double fractionZ = smooth(Math.floorMod(blockZ, size) / (double) size);

        double northWest = hash(cellX, cellZ, salt);
        double northEast = hash(cellX + 1, cellZ, salt);
        double southWest = hash(cellX, cellZ + 1, salt);
        double southEast = hash(cellX + 1, cellZ + 1, salt);

        double north = northWest + (northEast - northWest) * fractionX;
        double south = southWest + (southEast - southWest) * fractionX;
        return north + (south - north) * fractionZ;
    }

    private static double hash(int x, int z, int salt) {
        long value = x * 374761393L + z * 668265263L + salt * 1442695040888963407L;
        value = (value ^ (value >> 13)) * 1274126177L;
        value ^= value >> 16;
        return (value & 0xFFFFFFL) / (double) 0xFFFFFFL;
    }

    // ---------------------------------------------------------------------------------------------
    // The drawing itself.
    // ---------------------------------------------------------------------------------------------

    /**
     * Paints the map. The world begins as open sea and the land is laid over it in the order the
     * maps draw it: the north first, then the western seaboard, the Misty Mountains and their
     * neighbours, Rhovanion and Mirkwood, and last the south - the Mark, the white wall, Gondor,
     * Mordor and Harad. The seas that cut into the land come after the land they cut into, and the
     * sea's own biome is banded by latitude once everything else is down.
     */
    private static Country[][] draw() {
        Country[][] map = new Country[ROWS][COLUMNS];
        for (Country[] row : map) {
            java.util.Arrays.fill(row, Country.TEMPERATE_SEA);
        }

        // The far north, and the empty coast between Lindon and the Grey Mountains.
        fill(map, Country.FORODWAITH, 4, 0, 47, 1);
        fill(map, Country.FORODWAITH, 2, 2, 47, 4);
        fill(map, Country.FORODWAITH, 6, 2, 16, 5);

        // The western seaboard: Lindon behind Ered Luin, and the range itself running north to
        // south, which is what makes the north-west a wall rather than a shore.
        fill(map, Country.LINDON, 1, 5, 2, 18);
        fill(map, Country.BLUE_MOUNTAINS, 3, 4, 5, 21);
        fill(map, Country.ERIADOR, 6, 6, 16, 18);

        // Forochel, whose Ice Bay is the north-west coast the maps draw above Lindon. It takes the
        // northern end of the seaboard and the north end of Ered Luin, leaving the range running
        // from the slopes south to the Gulf, which is what makes the coast a shore there and a wall
        // further down. Its slopes are the ground between the snowfield and the mountains behind.
        fill(map, Country.FOROCHEL, 1, 4, 3, 8);
        fill(map, Country.FOROCHEL_SLOPES, 4, 4, 6, 8);

        // The cold north-west: Angmar and the Ettenmoors above Rhudaur, all the barrow country.
        fill(map, Country.ANGMAR, 17, 4, 21, 7);
        fill(map, Country.RHUDAUR, 15, 8, 19, 12);

        // The Shire, the Old Forest and the Barrow Downs, with Bree-land east of them, and the
        // wild hills - Cardolan, Minhiriath, Enedwaith and Dunland - south of that.
        fill(map, Country.SHIRE, 8, 19, 11, 21);
        fill(map, Country.ERIADOR, 12, 17, 16, 21);
        fill(map, Country.OLD_FOREST, 11, 22, 12, 22);
        fill(map, Country.DUNLAND, 12, 23, 20, 26);
        fill(map, Country.DUNLAND, 20, 23, 23, 25);
        // The Downs are painted after Dunland, not before it: both Dunland fills reach back over the two
        // cells the Downs stand on, and painted first the barrows had no cells left at all - a country
        // with a biome, a structure and a name, and nowhere in the world to be. Dunland gives those two
        // cells up to them.
        fill(map, Country.BARROW_DOWNS, 12, 23, 13, 23);

        // The Misty Mountains, the greatest wall of the west, from Mount Gundabad to the Gap of
        // Rohan; Eregion and the Trollshaws on their western side and Lorien, Rivendell and the
        // Vales on the eastern one.
        line(map, Country.HITHAEGLIR, 2, new double[]{20.5, 21.0, 21.0, 22.0, 22.5},
                new double[]{3.0, 7.0, 11.0, 15.0, 19.0});
        // The Trollshaws take the northern end of the country drawn as Firien - the ground under
        // Rhudaur and against the mountains - leaving Eregion and the holly country south of it.
        // Firien is painted round them rather than over them: a fill that started at row 12 covered
        // the three rows the Trollshaws had just taken, so the country ended up with no cells at all
        // and every place the books put in it had nowhere to be surveyed. The mountains are drawn
        // before either, so a Trollshaw column that is really mountainside keeps its rock.
        // The hills run from Rhudaur's edge down to the mountains' feet: three cells wide and five
        // deep, which is the room chapter one needs rather than the two-by-three the maps' scale would
        // give them anyway - the valley, Elladan's camp, Barachen's fire, the rider's ruin, the camp
        // the errands pitch and the three crags the stone asks in each want ground of their own.
        fill(map, Country.FIRIEN, 18, 17, 19, 18);
        fill(map, Country.TROLLSHAWS, 17, 12, 19, 16);
        fill(map, Country.LORINAND, 21, 13, 21, 14);
        fill(map, Country.LORINAND, 23, 15, 24, 18);
        fill(map, Country.GLADDEN, 25, 6, 26, 20);
        fill(map, Country.FANGORN, 24, 19, 26, 21);

        // Mirkwood, with the East Bight cut into its eastern side and Dol Guldur at its southern
        // corner, and the Wild Wood laid below it between the Anduin and the Brown Lands.
        fill(map, Country.MIRKWOOD, 27, 5, 33, 18);
        fill(map, Country.GLADDEN, 31, 11, 33, 15);
        fill(map, Country.MIRKWOOD_SPOOKY, 30, 16, 32, 18);
        fill(map, Country.RHUN, 34, 5, 39, 20);
        fill(map, Country.WILD_WOOD, 28, 19, 35, 22);
        fill(map, Country.WILD_WOOD, 27, 16, 29, 18);

        // The north-east: Ered Mithrin, the Iron Hills, Erebor and the Long Lake, and Rhun's plains
        // falling away to the far east of the map.
        fill(map, Country.ERED_MITHRIN, 24, 5, 33, 6);
        fill(map, Country.IRON_HILLS, 35, 6, 41, 7);
        fill(map, Country.IRON_HILLS, 35, 11, 36, 12);
        fill(map, Country.RHUN, 40, 5, 43, 22);
        // The empty east: canon leaves the land beyond Rhun unpeopled, and the maps draw it as waste. It
        // was painted as the Haradwaith's desert, which put dunes along the northern cold-lands as well;
        // it is its own country now, and still nobody lives there.
        fill(map, Country.EASTERN_WASTE, 44, 4, 47, 35);

        // The Mark, the white wall across the middle of the south, and Gondor below it.
        fill(map, Country.ROHAN, 23, 20, 29, 24);
        fill(map, Country.WHITE_MOUNTAINS, 11, 25, 34, 26);
        fill(map, Country.GONDOR, 16, 27, 30, 30);

        // The Dead Marshes, the Brown Lands and the Dagorlad, north of Mordor's wall.
        fill(map, Country.MARSHES, 27, 22, 28, 24);
        fill(map, Country.DAGORLAD, 30, 23, 34, 25);
        fill(map, Country.DAGORLAD, 31, 21, 36, 22);
        // The Dead Marshes lie between the Emyn Muil and the Dagorlad, east of the Wetwang, and the
        // Midgewater is the fen east of Bree on the East Road. Both hold water of their own, so they are
        // painted after the countries round them rather than under them.
        fill(map, Country.DEAD_MARSHES, 29, 22, 30, 24);
        fill(map, Country.MARSHES, 13, 18, 14, 18);

        // Mordor: Gorgoroth inside its ring, Nurn and the Sea of Nurnen below it, and the ring
        // itself - Ered Lithui across the north and Ephel Duth down the west.
        fill(map, Country.ASH_MOUNTAINS, 34, 21, 45, 23);
        fill(map, Country.ASH_MOUNTAINS, 34, 24, 36, 28);
        fill(map, Country.MORDOR, 37, 24, 44, 26);
        fill(map, Country.NURN, 37, 27, 43, 29);

        // Ithilien, between the Anduin and Mordor's western wall, and the Haradwaith: Umbar on the
        // coast, South Gondor above it and the lands of the south to the map's edge.
        fill(map, Country.ITHILIEN, 30, 25, 33, 29);
        fill(map, Country.HARAD, 22, 31, 47, 35);
        fill(map, Country.HARAD, 26, 30, 33, 31);
        fill(map, Country.HARAD, 34, 31, 43, 33);

        // The rest of the poster: Dale on the Long Lake below Erebor, Eregion between the Trollshaws and
        // the mountains, the Ettenmoors above Rhudaur, Eryn Vorn on the western cape, the Brown Lands and
        // the Emyn Muil north and west of the Dagorlad, Belfalas on the southern coast, and Khand east of
        // Mordor's wall. Each is its own ground and its own biome, so a place can be asked for by country.
        fill(map, Country.DALE, 34, 5, 36, 7);
        fill(map, Country.EREGION, 17, 15, 20, 16);
        fill(map, Country.ETTENMOORS, 16, 7, 18, 9);
        fill(map, Country.ERYN_VORN, 5, 19, 7, 21);
        fill(map, Country.BROWN_LANDS, 31, 21, 33, 22);
        // Emyn Muil is the rock north of the marsh. Drawn over rows 23-24 it took the Dead Marshes'
        // whole rectangle with it - the two fills were the same cells - and the marsh generated as Emyn
        // Muil from edge to edge. The row north of the marsh is all of Emyn Muil this map can hold.
        fill(map, Country.EMYN_MUIL, 29, 22, 30, 22);
        fill(map, Country.BELFALAS, 22, 31, 27, 33);
        fill(map, Country.KHAND, 43, 28, 46, 31);

        bandSeas(map);
        return map;
    }

    /**
     * The sea's biome by latitude: the farther from the map's middle, the colder or hotter the
     * water. The bands are drawn on the map's own rows, so the north Atlantic beside Forodwaith is
     * frozen ocean and the water off the Haradwaith coast is warm.
     */
    private static void bandSeas(Country[][] map) {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                Country country = map[row][column];
                if (country != Country.TEMPERATE_SEA) {
                    continue;
                }
                if (row <= 8) {
                    map[row][column] = Country.FROZEN_SEA;
                } else if (row >= 27 && !inlandSea(column, row)) {
                    map[row][column] = Country.WARM_SEA;
                }
            }
        }
    }

    /** The Sea of Rhun and the Sea of Nurnen lie inland, so they are not banded by latitude. */
    private static boolean inlandSea(int column, int row) {
        return (column >= 39 && column <= 43 && row >= 11 && row <= 16)
                || (column >= 38 && column <= 42 && row >= 26 && row <= 29);
    }

    private static void fill(Country[][] map, Country country, int fromColumn, int fromRow, int toColumn, int toRow) {
        for (int row = Math.max(0, fromRow); row <= Math.min(ROWS - 1, toRow); row++) {
            for (int column = Math.max(0, fromColumn); column <= Math.min(COLUMNS - 1, toColumn); column++) {
                map[row][column] = country;
            }
        }
    }

    /** Paints a country along a line of cells, wide enough to be a range rather than a thread. */
    private static void line(Country[][] map, Country country, int halfWidth, double[] columns, double[] rows) {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (distanceTo(column + 0.5D, row + 0.5D, columns, rows) <= halfWidth) {
                    map[row][column] = country;
                }
            }
        }
    }

    private static double distanceTo(double column, double row, double[] columns, double[] rows) {
        double closest = Double.MAX_VALUE;
        for (int point = 0; point < columns.length - 1; point++) {
            double dx = columns[point + 1] - columns[point];
            double dz = rows[point + 1] - rows[point];
            double lengthSquared = dx * dx + dz * dz;
            double along = lengthSquared == 0.0D ? 0.0D
                    : Mth.clamp(((column - columns[point]) * dx + (row - rows[point]) * dz) / lengthSquared, 0.0D, 1.0D);
            double offsetX = column - (columns[point] + along * dx);
            double offsetZ = row - (rows[point] + along * dz);
            closest = Math.min(closest, Math.sqrt(offsetX * offsetX + offsetZ * offsetZ));
        }
        return closest;
    }
}
