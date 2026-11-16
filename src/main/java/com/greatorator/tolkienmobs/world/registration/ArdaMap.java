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
    private static final boolean PLANTED = true;

    /**
     * Whether the river the city carries is cut out of that ground.
     * <p>
     * The massif and the floor are laid; the branch from the mouth to the drawn Bruinen is not cut yet, so
     * the rock stands where that channel will go. That is the one piece of the city's ground still to draw,
     * which is why it is a switch of its own rather than part of {@link #PLANTED}.
     */
    private static final boolean BRANCH = true;

    /**
     * The valley the map overrides rather than draws: its middle, the half-widths of its floor, and the
     * height of that floor.
     * <p>
     * A cell of the drawing is 512 blocks and the city that stands here is 554 across, so this ground
     * cannot be a country of the 48 by 36 grid: it is cut out of cells the drawing already fills and
     * blended back into them at its rim. Its middle is the middle of that city - the corner it is
     * planted from, plus half of its 554 by 512 footprint - so a city planted at 5200, -3370 stands in
     * the middle of the floor rather than against one of its walls. The floor is a few blocks smaller
     * is the footprint itself - 554 by 512 - so the rock's foot begins at the first block outside the
     * walls rather than four blocks inside them. A strip of crown left standing inside the build is a wall
     * of its own at every face the build leaves open, and that strip is what read as a four-block wall at
     * the river's mouth and again at the spring. There is no apron of flat ground in front of the walls
     * for the join to be dressed across, because there is no join: the lowest band of the city is laid on
     * the floor, and what the ground does outside the walls is the drape's to shape.
     */
    private static final int CITY_WEST = 5200;
    private static final int CITY_NORTH = -3370;
    private static final int VALLEY_X = CITY_WEST + 276;
    private static final int VALLEY_Z = CITY_NORTH + 256;
    private static final int VALLEY_HALF_X = 277;
    private static final int VALLEY_HALF_Z = 256;

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
     * out from it, and 96 blocks of it keeps the range at the roofline out past the walls instead of a
     * field away from them.
     */
    private static final int VALLEY_RIM = 96;
    private static final int VALLEY_FALL = 200;
    /**
     * How far past the massif's foot the country is brought down to the valley's own level, in blocks.
     * <p>
     * Ninety-six blocks of mountain, then its fall, then a hundred and twenty-eight more of country
     * settling to the vale's floor. The settling used to run nine hundred blocks, which is not a vale:
     * it flattened the whole southern approach of the Misty Mountains to the city's own floor, so the
     * range stood at seventy-four where the drawing puts it at a hundred and sixty, and the drawn river
     * ran in a slot under a plain that called itself river. A short apron still keeps the massif from
     * ending in a wall, and hands the country back to the drawing while it is still a mountain.
     */
    private static final int VALLEY_VALE = 128;
    /**
     * How far inside the end of the massif's fall its opening to the water is eased back out, in blocks.
     * <p>
     * The fall hands the ground back to the drawing at one fixed distance from the valley's floor, and the
     * opening the water gets is only cut inside that. Cut to the seam, as it was, the ground inside stood
     * where the water's curve put it and the ground outside at the drawing's own height, and the difference
     * between them stood as a face along the seam - the straight-edged step in the bank beside the river,
     * which the save showed once at each side of the vale. The last of the fall takes the cut back out, so
     * the seam is a join in the ground rather than an edge in it.
     */
    private static final int GIVE_EDGE = 96;

    /**
     * The height the mountains round the valley reach, and how far from that they are allowed to wander.
     * <p>
     * The ridge tops out at the city's own crown, 318, because that is as high as this world can stand: a save
     * made before Arda was raised is 384 blocks tall, its ceiling is 319, and a summit drawn above it is cut
     * off flat. The mask's white means the same height, so the ranges and the build reach the same sky. The
     * ridge's distance does not wander - it is the shelf's own square edge, all the way round - and with
     * the crest swing at nothing its top is the crown itself, level with the roofs the massif shuts in.
     */
    private static volatile int VALLEY_CREST = VALLEY_CROWN;
    private static final int CREST_SWING = 0;
    private static final int CREST_SCALE = 420;
    /**
     * How far the ridge's own distance wanders off the shelf's edge, as a share of it.
     * <p>
     * Nothing at all: the rock is to stand at the crown in a square, its inner face at the build's own
     * outside edge, so the shelf's edge is four straight runs rather than a ring that swings in and out.
     * The swing is left here, read at nothing, because it is what would make a range of it again.
     */
    private static final double RIDGE_SWING = 0.0D;
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
     * The gap is read off the cut rather than guessed: it has no piece at all in x 5645..5753 by z
     * -3370..-3311 above the lowest band, and those are the two positions the build marks as its corners.
     */
    private static final int SHOULDER_WEST = CITY_WEST + 445;
    private static final int SHOULDER_EAST = CITY_WEST + 553;
    private static final int SHOULDER_NORTH = CITY_NORTH;
    private static final int SHOULDER_SOUTH = CITY_NORTH + 59;
    private static volatile int SHOULDER_TOP = VALLEY_CROWN;
    private static final int SHOULDER_RUN = 110;

    /**
     * The Bruinen: where the city's own water leaves the walls, the water it feeds, and the course between.
     * <p>
     * The mouth is read off the build rather than guessed. Its 584 pieces carry 302,049 water blocks, and the
     * water stands against the west wall over x 5200..5201, z -3072..-3063 with its surface at 82 - seven
     * blocks above the valley's floor. The east wall's only water is an aqueduct at y 239, which is a spring
     * inside the city, not an outflow. So the branch leaves west, falls the eight blocks to the vale, and runs
     * round the valley's south side to the drawn Bruinen: the nearest drawn water to the mouth is at 5054,
     * -3086, 147 blocks west, and the straight line to it runs through the city.
     * <p>
     * The river is cut as its own channel rather than by the maps' own river lines - which flatten every
     * column they touch to a level 63 and met the massif's rock two hundred and thirty blocks over the water.
     * Its water is read off the ground as the course goes rather than set at either end, so it falls where the
     * ground falls and never runs uphill.
     */
    private static final int BRUINEN_OUTLET_X = CITY_WEST;
    private static final int BRUINEN_OUTLET_Z = CITY_NORTH + 303;
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
     * How far out a river column looks for the lowest water of its own channel, in blocks: wider than the
     * painted band is across, so the whole channel answers with one level.
     * <p>
     * The drawing's own terracing and the mask bake's pixel edges are a few blocks across, and a river that
     * takes each column's height as its surface turns them into pools that spill one into the next. Twelve
     * blocks levelled a step between those pixels, but it is read inside one bank rather than across the
     * channel: the masks paint their rivers as bands thirty blocks wide and more, and the surface a column
     * standing in one takes is that column's own blend of the vale's apron and the drawn country - a blend that
     * falls over the channel's width. North of Rivendell that came out as a bowl: fifteen blocks of fall
     * across forty of channel, the river lying in a stair of pools with its bed terraced under each one and
     * the terraces showing through the water. Twenty-four reaches across the widest band, so a channel answers
     * with one level across it, and the only fall its water has is the one along its course.
     */
    private static final int RIVER_LEVEL_REACH = 24;

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
    private static final int BRUINEN_FORD_X = 5054;
    private static final int BRUINEN_FORD_Z = -3086;
    private static final double BRUINEN_FORD_SHALLOW = 0.92D;
    /** Steps the channel's water is read at along its course, from the outlet to the ford. */
    private static final int BRUINEN_STEPS = 256;
    /**
     * How far along the course the branch falls one block. Twelve put the city's water four blocks over the
     * river it feeds on a course of a hundred and fifty, and spent the fall in the first third of it - a
     * cascade every twelve blocks, which the save at the ford showed as a run of stepped plates. Twenty-four
     * is the rate the dale holds the drawn river at, so the branch and the river it joins are one gradient,
     * and the same fall is carried down the whole course instead of into the crossing.
     */
    private static final int BRUINEN_GRADE = 24;
    /**
     * How far out from water the ground climbs back to the city's crown, in blocks.
     * <p>
     * The climb is one curve, not a grade: the ground leaves the water almost level, takes most of its
     * height across the middle of the mouth and eases into the crown at the run's end, so the mountainside
     * has no edge in it at either end. The run is the shelf's own width, so the rock a river opens is the
     * rock the shelf would have stood at anyway.
     * <p>
     * Two grades were drawn and both were wrong in the same place. Six tenths, a mountainside's own, does
     * not reach the crown for 390 blocks, which laid the cone across the whole south flank: the walls stood
     * at 255 and 272 and the peaks read two hundred blocks out. Six, a gorge wall's, puts the crown back
     * within forty blocks - but at the water's own bank, so the river ran in a slot, and where the slot's
     * open ground stopped the rock stood at 240 four blocks from ground at 100: the wall the save showed
     * just outside the west wall. That is a seam rather than a slope. The branch's own bank opened the
     * ground at one and a fifth blocks per block and the cone cut it at six, and the line where one gave
     * way to the other is a face two hundred blocks high. One curve for both ends the seam.
     */
    /**
     * The run over which the ground climbs from drawn water to the crown. Ninety-six put the massif's west
     * flank at 319 within a hundred blocks of the Bruinen at 74, which reads across the river as a wall; a
     * hundred and sixty spread the same climb over nearly twice the ground and still took more than two
     * blocks of height per block of run through the middle of the curve - sixty-six degrees, which is the
     * striped face the save showed closing the ford, and the terraces where the cell grid met it. Three
     * hundred and twenty halves that again: no part of the climb is steeper than a block and a fifth, which
     * is a mountainside rather than a wall.
     */
    private static final int MOUTH_RUN = 320;
    /**
     * The dale's own numbers, in blocks: how gently its water may fall, how far over the vale's floor the
     * sheet stands, and how far along the water a fall is followed.
     * <p>
     * The country the masks draw round the vale stands at 155 to 175 and the vale's water at 76, so a
     * branch coming into the river has its own height to lose. It goes at one block of fall per
     * {@link #DALE_GRADE} of run - a slope water can be followed down - and the drawing takes the water
     * back wherever the ground it lays is lower.
     * <p>
     * Eight was steep. A branch entering the dale a hundred blocks over the river lost all of that within
     * eight hundred blocks of the junction, so the water came down the last stretch of the branch like a
     * chute into the crossing while the river it joined stayed at the vale's water for a thousand blocks
     * either side. Sixteen gave it twice the branch. Twenty-four gives it more: the drawing holds a branch
     * at the vale's own level for a long stretch before the mountains take it up, and the climb that follows
     * is three or four hundred blocks of range, so the fall is spread as thinly as a river's own.
     * <p>
     * The fall hands itself back to the drawing over the last {@link #DALE_EASE} blocks of the reach instead
     * of stopping at its end. Stopped, the water stood at the last block of fall and then, one column
     * further out, at the drawing - and the drawing on a branch climbing into the range stands above any
     * fall that has run out of reach, so the branch ended at the field's own edge in a step of twenty blocks
     * or more: the strip of sheer ground the save showed across the water at the top of a branch.
     */
    private static final int DALE_GRADE = 24;
    /**
     * How far inside the end of the dale's reach the fall hands the water back to the drawing, in blocks.
     */
    private static final int DALE_EASE = 320;
    private static final int DALE_DEPTH = 2;
    private static final int DALE_REACH = 2000;
    /**
     * How far out from the dale's river the ground comes down to its water, in blocks: far enough that the
     * bank can climb to the tallest ground it cuts before the field ends.
     * <p>
     * The reach is read off the massif rather than picked. Its crown stands two hundred and forty-two
     * blocks over the vale's water, so a bank that climbs two blocks per block of run needs a hundred and
     * twenty-one blocks of run to reach it; the field is set at twice that, so the ground between the crest
     * and the field's own edge has room to be graded rather than stepped. Ninety-six was short even at one
     * block per block: the flank kept its own height to the end of the field and stood there as a face.
     */
    private static final int DALE_BANK = 256;
    /**
     * How many blocks the bank rises for each block of run out from the water: two, so the ground beside the
     * river climbs to the drawn country inside the width of one hillside instead of spanning it.
     * <p>
     * One was the drawn country's own slope, and it left the cut so wide that the river read as the floor of
     * a bowl: seventy-five blocks of drop over seventy-five blocks of tread either side, which is a long walk
     * and a very visible rim where the drawing starts again. Three is a chute - the save showed it as a slot
     * with the water at the bottom of a wall on either side, and the massif's flanks terraced where the cell
     * grid met a slope that steep. Two is the middle: the bank reaches the drawn ground in one flank's width
     * and the water's own edge still reads as the foot of a slope rather than the edge of a basin. The reach
     * above is read for this slope, so the bank still climbs to the crown.
     */
    private static final int DALE_RISE = 2;
    /**
     * How much roll the bank's own surface has, in blocks, and how far apart its rises and hollows stand.
     * <p>
     * A bank cut to one curve is a cone: every contour on it is parallel to the river, and it reads as a cut
     * however shallowly it is graded. A few blocks of roll, read against the cut rather than the ground,
     * breaks those contours into spurs and gullies where the cut is deep and dies out where it is shallow,
     * so the foot of the slope - and the water's own edge - stays the foot of the slope.
     */
    private static final int DALE_ROLL = 4;
    private static final int DALE_ROLL_SCALE = 48;
    static {
        ArdaMasks.buildDale(BRUINEN_FORD_X, BRUINEN_FORD_Z, DALE_REACH);
    }
    /**
     * How far the crown's own surface rolls below the crown, in blocks.
     * <p>
     * A summit standing at exactly one height is a table: every contour on it is the crown's own outline, and
     * the mountain's edge reads as a cut however softly the flank is graded. A few blocks of roll over the
     * same distance as the ridge's wander costs the world no height and stops the top being a plane.
     */
    private static final double CROWN_ROLL = 3.0D;
    /**
     * How far out a column looks for a drawn river whose ground it has to give way to, in blocks: longer
     * than {@link #MOUTH_RUN}, so the climb is finished before the walk is and the ground past the walk
     * is the crown rather than the step the walk would otherwise leave.
     */
    /**
     * How far the walk for drawn water reaches. It must pass {@link #MOUTH_RUN}, or the climb would be cut off
     * short of the crown and end in the step the climb was written to remove.
     */
    private static final int WATER_REACH = 340;
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
    private static final int[] COURSE_X = {BRUINEN_OUTLET_X, CITY_WEST - 70, BRUINEN_FORD_X};
    private static final int[] COURSE_Z = {BRUINEN_OUTLET_Z, -3078, BRUINEN_FORD_Z};
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

    /** Hobbiton's pin: the low corner of the cut, which is the corner `/tmcut place hobbiton` is given. */
    public static final int HOBBITON_X = -700;
    public static final int HOBBITON_Y = 87;
    public static final int HOBBITON_Z = -512;
    /** How wide and how deep the village's footprint is, from the player's own map: 551 by 468. */
    public static final int HOBBITON_WIDE = 551;
    public static final int HOBBITON_DEEP = 468;
    /** How high the village's own water stands over the pin: its pond's surface, nine over the floor. */
    public static final int HOBBITON_WATER_LIFT = 9;
    /** How high the village's ground stands over the pin: its grass line, sixteen over the floor. */
    public static final int HOBBITON_DECK_LIFT = 16;
    /** How many blocks of run the village's branch loses a block of height over. */
    private static final int HOBBITON_GRADE = 40;
    /**
     * How far from a carried course a column still belongs to it, in blocks: the channel's own half width of
     * twenty-eight at its widest, the mask's edge, and enough margin to cover the columns the narrowing leaves at the
     * bank.
     */
    private static final int HOBBITON_REACH = 64;
    /** The course the village's branch was drawn along, bend for bend: the corridor has to find its own columns. */
    private static final int[] HOBBITON_XS = {-206, -230, -270, -320, -380, -450, -520, -590, -652, -712, -770};
    private static final int[] HOBBITON_ZS = {-45, -6, 40, 80, 112, 140, 165, 190, 212, 232, 246};

    /** The corner the village's cut is laid on: the low corner of the footprint it was cut in. */
    public static BlockPos hobbitonPlan() {
        return new BlockPos(HOBBITON_X, HOBBITON_Y, HOBBITON_Z);
    }

    /** The middle of the village's footprint at its own ground: where a name for it is written. */
    public static BlockPos hobbitonCentre() {
        return new BlockPos(HOBBITON_X + HOBBITON_WIDE / 2, HOBBITON_Y + HOBBITON_DECK_LIFT,
                HOBBITON_Z + HOBBITON_DEEP / 2);
    }

    /**
     * A drawn channel whose level is a placed build's rather than the drawing's.
     * <p>
     * A drawn river normally stands at the height of the ground it is drawn on, and needs nothing: its water is the
     * country's. A channel that is leaving a build is the other case. Hobbiton's village stands at (-700, 87, -512)
     * with its water nine over that base, so the river in it runs at 96. Its west arm comes out level with the drawn
     * channel - 96 against the channel's 95 - and needs nothing. Its south arm comes out into country the drawing
     * stands at a hundred and five to a hundred and thirteen, so water the mask draws there would be laid at the
     * ground and would stand thirteen blocks over the village's own: the arm would end against its own wall, under a
     * river running past it.
     * <p>
     * So a carried channel's level is read from the build and not from the ground: it leaves the build at
     * {@code water} and falls one block in every {@link #grade} of run, arriving at the Y the mask draws at
     * (-770, 246) at 80 - a block under the drawn river's own 81, which the level's own reach turns into a step at
     * the confluence rather than a wall. Everything else the drawn-water path already does follows from that one
     * number: the bed is cut under the surface, so the branch is a channel thirteen deep at the wall that opens out to
     * two at the fork, and the ground beside it is graded against the water that is really there rather than against
     * the hillside above it.
     * <p>
     * The level is the build's own rather than a number of the Shire's: a second build with a channel out of it is an
     * entry in {@link #CHANNELS} with its own pin, its own water lift and its own grade, and nothing in this rule has
     * to be copied to say so.
     */
    private record Channel(String place, int water, int grade, int reach, int[] alongX, int[] alongZ,
                           double[] legs, double[] starts) {
        /** A channel with its legs measured: how long each is, and where each begins along the course. */
        static Channel of(String place, int water, int grade, int reach, int[] alongX, int[] alongZ) {
            double[] legs = new double[alongX.length - 1];
            double[] starts = new double[alongX.length - 1];
            double total = 0.0D;
            for (int leg = 0; leg < legs.length; leg++) {
                starts[leg] = total;
                legs[leg] = Math.hypot(alongX[leg + 1] - alongX[leg], alongZ[leg + 1] - alongZ[leg]);
                total += legs[leg];
            }
            return new Channel(place, water, grade, reach, alongX, alongZ, legs, starts);
        }

        /**
         * The level this channel stands at over a column, or {@link Integer#MAX_VALUE} off its course.
         * <p>
         * Read from the build's water and the run along the channel rather than from the drawing, which is the whole
         * point of it: the drawing holds the hillside over this channel, and the water in it is a gorge's.
         */
        int water(int blockX, int blockZ) {
            double near = Double.MAX_VALUE;
            double run = 0.0D;
            for (int leg = 0; leg < this.legs.length; leg++) {
                double alongX = this.alongX[leg + 1] - this.alongX[leg];
                double alongZ = this.alongZ[leg + 1] - this.alongZ[leg];
                double at = ((blockX - this.alongX[leg]) * alongX + (blockZ - this.alongZ[leg]) * alongZ)
                        / (alongX * alongX + alongZ * alongZ);
                double into = Mth.clamp(at, 0.0D, 1.0D);
                double footX = this.alongX[leg] + into * alongX;
                double footZ = this.alongZ[leg] + into * alongZ;
                double away = Math.hypot(blockX - footX, blockZ - footZ);
                if (away < near) {
                    near = away;
                    run = this.starts[leg] + into * this.legs[leg];
                }
            }
            if (near > this.reach) {
                return Integer.MAX_VALUE;
            }
            return Math.max(SEA_LEVEL, this.water - (int) Math.round(run / this.grade));
        }
    }

    /** The channels this world carries out of a build: one entry per placed build that has one. */
    private static final Channel[] CHANNELS = {
            Channel.of("hobbiton", HOBBITON_Y + HOBBITON_WATER_LIFT, HOBBITON_GRADE, HOBBITON_REACH,
                    HOBBITON_XS, HOBBITON_ZS)
    };

    /** The lowest level a placed build's channel stands at over a column, or {@link Integer#MAX_VALUE} off them. */
    private static int carriedWater(int blockX, int blockZ) {
        int water = Integer.MAX_VALUE;
        for (Channel channel : CHANNELS) {
            water = Math.min(water, channel.water(blockX, blockZ));
        }
        return water;
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

    /**
     * The Gundabad gate's site, and the notch the pass ends in for it.
     * <p>
     * The gate is the one build in this pack whose shape the world is drawn to know: it is a gatehouse cut out of
     * a mountainside - a hundred and sixty wide, forty-eight deep and eighty-one tall, its facade on the south
     * face and its back in the rock - so the ground it stands on can be measured to it instead of the other way
     * round. The box below is that measurement, taken off the gate laid in the map, and it is the one place the
     * gate can go: the pass is cut to the box, so a gate laid anywhere else has a valley shaped for a different
     * gate. {@link #gateFront()} is the point that names it.
     * <p>
     * The massif's own drawing is a plateau three hundred blocks up with thirty blocks of movement on it and no
     * face anywhere a gate eighty-one blocks tall could be set into, which is what the first trial in the map
     * found. The country is drawn as a pass instead: a floor at a hundred and eighty-seven under the gate,
     * widening south from the gate's own width into a courtyard, and falling to a hundred and seventy-six where
     * it opens into the Hithaeglir's own low ground, so the walk up to the fortress is a climb the drawing
     * already asked for rather than a shelf. The massif stands over it on three sides: the rock behind the gate
     * is the drawn plateau climbing out of the floor, and the walls either side come down to the gate's own edges
     * at the height of its wings, so the mountain the gate carries is the mountain the world grows.
     */
    /** The gate's box, in blocks: west and east across the facade, north at its back, south at its face. */
    public static final int GATE_WEST = 5496;
    public static final int GATE_EAST = 5655;
    public static final int GATE_NORTH = -6877;
    public static final int GATE_SOUTH = -6830;
    /** The gate's floor: the course its own file starts at, and the pass's floor where it stands. */
    public static final int GATE_FLOOR = 187;

    /** The middle of the gate's facade, which is where a caller places it by. */
    /**
     * The point the gate's plan is anchored by: the middle of the row its own plan starts at.
     * <p>
     * {@code GundabadGate.place} takes the point that the middle of the gate's plan lands on after it is turned, and
     * with the gate unturned that point is the middle of the box's north row - so a gate laid here with
     * {@link Rotation#NONE} fills the box at x 5496 to 5655, z -6877 to -6830, facade south. The same box it always
     * fills: what changed is which way round the build sits in it.
     */
    public static BlockPos gatePlan() {
        return new BlockPos(PASS_X, GATE_FLOOR, GATE_NORTH);
    }

    /**
     * The dwarven keep the pass was shaped for: the gundabad template's own north-west bottom corner, laid with no
     * rotation, with its south wall on the plane the owner marked.
     * <p>
     * The keep is underground by design - a hall cut into the mountain with the pass's own crown standing over it at
     * 267 - so it is written down here as a plan rather than surveyed: its corner is inside solid rock and nobody can
     * stand on it to pin it. What <em>can</em> be stood on is the mark the owner walked: an ammolite on the chamber's
     * floor at (5577, 189, -6918), at the foot of the keep's own doorway, and the box below is that mark read back
     * through the template.
     * <p>
     * The template carries the hold's floor as the rock under it - fourteen courses of deepslate whose top course is
     * the floor a player stands on, with the rooms over it, the doorways cut from course fourteen. So the mark's
     * course is that top course, local y 13, and the base is 176: the doorway's floor lands on 189 and the rooms fill
     * the chamber's own section, 190 to 200, to the block. The doorway used is the far one of the south wall - local
     * x 104 to 108, its middle 106 - so with the box at x 5471 its middle lands on the mark's 5577; and the south
     * wall's own course, local z 182, lands on the mark's z -6918. The body runs the rest of its hundred and eighty-
     * three courses west and north into the mountain, and its doorway is the way out of the gate's chamber.
     * <p>
     * The hall out of the gate is carried north to {@link #keepWall()} and its doorway is cut the wall's depth beyond
     * that, so the gate is laid first and the keep laid over it: the keep's own wall and its doorway then stand whole,
     * and the cut behind them is air the keep's rooms already hold.
     */
    public static final int KEEP_X = 5516;
    public static final int KEEP_Z = -7112;
    public static final int KEEP_WIDE = 122;
    public static final int KEEP_TALL = 26;
    public static final int KEEP_DEEP = 183;

    /**
     * The keep's own bottom course: the template's y 0.
     * <p>
     * The chamber's floor top is 189 and the template's floor is its three bottom courses, so the corner is 187 - the one
     * number that puts the hold's floor on the hall's, its interiors both starting at 190. It read 176, the first plan,
     * which stood the hold eleven blocks under the passage it opens off and did not match the box the keep was walked
     * for: x 5516…5637 and z -7112…-6930, with the mountain's shallowest cover over its south wall.
     */
    public static final int KEEP_FLOOR = 187;

    /**
     * The keep's throne, from the plan's own corner: thirty along, seventeen up, seventy-six back. The owner marked the
     * seat itself - (5501, 192, -7024) is the slab the captain sits over - and this is that mark in the template's own
     * coordinates, so moving the keep moves the seat with it.
     */
    public static BlockPos keepThrone() {
        return keepPlan().offset(30, 17, 76);
    }

    /** Where the wraith stands to keep the captain's company: four paces down the carpet, facing the same way. */
    public static BlockPos keepThroneWatch() {
        return keepPlan().offset(30, 17, 79);
    }

    /** The keep's pin: the template's north-west bottom corner, laid with no rotation. */
    public static BlockPos keepPlan() {
        return new BlockPos(KEEP_X, KEEP_FLOOR, KEEP_Z);
    }

    /**
     * The Gundabad travel stone: the ammolite the owner set into the gate's own facade, at (5584, 190, -6830), which is
     * where the hold's stone is asked to stand. It is the gate's rather than the keep's because the facade is what a
     * player walks up to - the hold itself is a doorway in a chamber - and {@code Stones} replaces a marker standing on
     * its spot rather than leaving it beside the stone.
     */
    public static BlockPos gundabadStone() {
        return new BlockPos(5584, 190, -6830);
    }

    /**
     * The cave east of the gate: the second ammolite the owner laid, at (5693, 186, -6676), standing on the shelf where
     * the mountain begins to climb. It is the mouth's own floor course - the ground the marker stands on - and
     * {@link com.greatorator.tolkienmobs.faction.GundabadCave} cuts the way in facing west and the room back east into
     * the rock.
     */
    public static BlockPos gundabadCave() {
        return new BlockPos(5693, 186, -6676);
    }

    /** The south wall's own course, which is the last course of the passage out of the gate. */
    public static int keepWall() {
        return KEEP_Z + KEEP_DEEP - 1;
    }

    public static BlockPos gateFront() {
        return new BlockPos((GATE_WEST + GATE_EAST) / 2, GATE_FLOOR, GATE_SOUTH);
    }

    /** The middle of the pass, a block east of the gate's own middle so the gate's box sits inside it. */
    private static final int PASS_X = 5576;
    /** The back of the gate: the pass ends here, and the plateau stands over it. */
    private static final int PASS_HEAD_Z = GATE_NORTH;
    /** Where the valley's floor meets the ground the drawing already put low. */
    private static final int PASS_MOUTH_Z = -6350;
    /** Half the pass's width at the gate: eighty, which is half the gate, so the walls stand at its edges. */
    private static final int PASS_HEAD_HALF = 80;
    /** The same at the mouth, where it has opened into the courtyard and the low ground beyond it. */
    private static final int PASS_MOUTH_HALF = 380;
    /** The floor at the gate, and at the mouth, so the way up is walked rather than climbed. */
    private static final int PASS_HEAD_FLOOR = GATE_FLOOR;
    private static final int PASS_MOUTH_FLOOR = 176;
    /**
     * The height the ground stands at the walls' own foot beside the gate: the top of the gate's wings, eighty
     * above its floor. A wall that starts at the floor and climbs leaves the gate standing in a trench of its own
     * making - the rock the build carries on its back would show as a face. This is the height it continues from.
     */
    private static final int PASS_EDGE_TOP = 267;
    /** How far south of the gate the walls keep that height before they open into the courtyard. */
    private static final int PASS_GATE_KEEP = 80;
    /** ... and how long they take to open from there, so the courtyard is a court and not a funnel. */
    private static final int PASS_GATE_EASE = 420;
    /**
     * How fast the ground climbs away from the build, in blocks of height per block of distance: two and a half.
     * <p>
     * A stair of two or three blocks for each step out, so the gate's ends are into the rock within about thirty
     * blocks of the footprint and the gate reads as nestled into the pass. The climb is capped by the country's own
     * drawn surface - {@code Math.min(drawn, floor + distance * PASS_STEP)} - so past the cap the mountain is the
     * drawing's and slopes away as it always did. Nothing is stood up above the drawn massif, so there is no crest to
     * end in a face.
     */
    private static final double PASS_STEP = 2.5D;
    /**
     * How far the mountain's foot stands above the floor at the build's own ends, in blocks: zero.
     * <p>
     * At zero the ground at the footprint's edge is the floor and the climb runs its full length out of it - floor
     * 187, the roof's 267 by about thirty-two blocks, the drawn massif by about forty-five - which is the slope the
     * owner called right from every side.
     * <p>
     * It was sixty for a while, to bring the foot in twenty-four blocks' worth of climb. That works on paper and is
     * wrong in the world: the lift is a step at the footprint's edge, so the sides began with a sixty-block face and
     * only twenty blocks of slope were left before the drawn plateau's three hundred, and the side mountains read as
     * flat from the ground while the back - where the lift is zero because it is shared by distance - kept its slope.
     * If the foot is to come in again, it has to come in as length of climb, not as height: shortening this slope is
     * what makes a wall of it.
     */
    private static final int PASS_SINK = 0;
    /**
     * How far inboard of the footprint the mountain's foot starts on each side, in blocks: thirty-nine on the west and
     * twenty-four on the east.
     * <p>
     * The foot is still the floor - 187 - but it begins that many blocks <em>inside</em> the box, so the ground comes
     * down into the footprint and the build is laid into the mountain rather than stood against it. The two sides are
     * different because the drawn massif does not reach the gate equally on both: bringing the west in thirteen blocks
     * further than the east puts the same flank of rock against both ends.
     */
    private static final int PASS_FOOT_WEST = 39;
    private static final int PASS_FOOT_EAST = 24;
    /**
     * How far the mountain's own surface is broken up, in blocks, and at what scales: the crag a range is broken
     * by, and the crests that wander along the pass.
     * <p>
     * Held off the build's own edge, so the gate still meets the ground exactly where its ends sink in.
     */
    private static final int PASS_CRAG = 14;
    private static final int PASS_CRAG_SCALE = 28;
    private static final int PASS_SPUR = 18;
    private static final int PASS_SPUR_SCALE = 96;
    /** The same once the pass has opened into the courtyard, where a hillside is a hillside. */
    private static final int PASS_SIDE_RUN = 150;
    /**
     * The plaza behind the gate, and how far past it the mountain climbs.
     * <p>
     * The gate stands in a pass and is the only way into the keep behind it, so what is behind it is not rock against
     * its back: it is a floor an army can stand on - level to the block for thirty-two blocks north of the box's back
     * - and then the mountain, which climbs to the same peak the sides do and falls away into the country beyond that
     * crest. The plaza is the pass's own width, so the mountain closes round it on three sides and the gate is the
     * one way in.
     */
    private static final int PASS_PLAZA = 32;
    private static final int PASS_BACK_RISE = 96;
    /** How far north of the back the shaping reaches: the plaza, and the climb out of it to the drawn ground. */
    private static final int PASS_BACK_REACH = PASS_PLAZA + 200;
    /** How far the mouth blends back into the open ground it opens onto. */
    private static final int PASS_MOUTH_RUN = 250;
    /** How much the floor rolls either way, in blocks, so a valley is ground rather than a table. */
    private static final int PASS_ROLL = 2;
    /** The scale of the floor's own roll, in blocks. */
    private static final int PASS_ROLL_SCALE = 128;

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
        /**
         * Mount Gundabad: the massif at the range's northern end, where the Misty Mountains, the Grey
         * Mountains and Angmar's peaks come together. It stands higher than the range it belongs to and
         * broader at the foot, which is what three peaks over one massif are - and it is painted after the
         * mountains' line, so it takes that line's own cells rather than standing beside them.
         */
        GUNDABAD(TolkienBiomes.GUNDABAD, 195, 32, true),
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

    /**
     * What the drawing and the grading make of one column, for a hand that wants the numbers instead of the map.
     * <p>
     * Read straight out of the classes the generator uses rather than re-derived: the height the column stands at,
     * the level its water is laid at, whether it is river at all, and the ceiling the river's valley holds it to. The
     * last is the one worth watching - the valley's rule is a rate, a reach and a depth, and a rate read wrong is a
     * hillside cut wrong over hundreds of blocks, which no screenshot argues about as clearly as four numbers do.
     */
    public record Column(int x, int z, int ground, int water, boolean river, int ceiling) {
        /** How many blocks the valley takes off this column: nothing when it stands under its own ceiling. */
        public int taken() {
            return this.ceiling == Integer.MAX_VALUE ? 0 : Math.max(0, this.ground - this.ceiling);
        }
    }

    /** One column of the drawing and its grading. */
    public static Column column(int blockX, int blockZ) {
        Sample sample = sample(blockX, blockZ);
        return new Column(blockX, blockZ, sample.height(), sample.water(), sample.river(),
                riverCeiling(blockX, blockZ));
    }

    /** The drawing at a column, shifted by whatever warp the caller has already read. */
    private static Sample read(int blockX, int blockZ, double shiftX, double shiftZ) {
        Sample drawn = readDrawn(blockX, blockZ, shiftX, shiftZ);
        // The Bruinen the city carries is cut into whatever the drawing and the valley answer, because it
        // runs through both: out of the valley's own floor, through the massif's flank, and into the country
        // beyond its ridge. With the city's ground not laid there is no branch to cut.
        return PLANTED && BRANCH ? bruinen(blockX, blockZ, drawn) : drawn;
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
                // The valley answers before the bank does, and the valley's own floor is far under the
                // drawn river that crosses its edge: the water kept its own surface while the ground
                // beside it stayed at the floor, and the vale had a wall of water twenty blocks tall
                // along it. The bank reaches the valley's ground the same way it reaches the country's.
                int need = bank(blockX, blockZ, valley.height());
                if (need > valley.height()) {
                    return new Sample(valley.country(), Math.min(VALLEY_CROWN, need), false,
                            valley.shore(), valley.upland(), valley.water());
                }
                // The massif's outward fall is the dale's ground as much as the drawn country's: the Bruinen
                // crosses that band on its way past the vale, and it was the one stretch the bank never
                // reached - the crown standing at its own height over water a hundred and eighty blocks
                // under it, down the whole western side of the range and its north face, with the drawing's
                // channel cut through the middle of it. The cut is kept off the floor, the shoulder and the
                // crown, past the rim, because that ground is the city's own and the build stands on it.
                if (beyond(blockX, blockZ) > VALLEY_RIM) {
                    int dale = daleBank(blockX, blockZ, valley.height());
                    if (dale != Integer.MIN_VALUE && dale < valley.height()) {
                        return new Sample(valley.country(), dale, false, valley.shore(),
                                valley.upland(), valley.water());
                    }
                }
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
        // The dale's ground comes down to the river it holds, so the river is not left in a slot where the
        // water has been taken out from under the drawing.
        int dale = daleBank(blockX, blockZ, height);
        if (dale != Integer.MIN_VALUE) {
            height = Math.min(height, dale);
        }
        // And the same for every drawn river the dale does not own. A channel whose water stands well under the
        // country beside it is a slot until something brings the hillside down to meet it, and that is what this is:
        // the valley's own flank, read off the drawing's water rather than off the country.
        int river = riverBank(blockX, blockZ, height);
        if (river != Integer.MAX_VALUE) {
            height = Math.min(height, river);
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
     * The masks answer where a river or a lake is, and the top of a lake's water is the ground the drawing
     * gives the column: the drawing's lakes are already drawn at their own level, so water follows the land
     * down instead of standing at a level of its own. Averaging the ground over the country round the
     * column was tried and put water on top of the land wherever the drawing dips - a lake sheet three or
     * four blocks over its own shore. The bed is cut under that surface, and a drawn river's bed slopes from
     * its middle out to its banks, because one depth for a sixteen-block-wide drawing puts a cliff at both.
     * A drawn river's water stands at the channel's own level rather than at each column's: see
     * {@link #riverLevel}.
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
            // The surface is the channel's own level as the drawing gives it, and the low of that channel
            // within a short reach: the ground here carries the range's lift and the crag, which are rock
            // and which step at the mask bake's own pixel edges, so a surface taken from the ground stood
            // the river up on the range's skirt and made one pool per step, each spilling into the next.
            // Near a coast the grade falls under the sea; held there, the last stretch of a river meets the
            // sea instead of digging a trench beside it.
            int surface = riverLevel(blockX, blockZ);
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
     * The level the drawing gives a column's channel, before the range's lift and the crag are read into it.
     * <p>
     * The mask's brightness stands the ground up toward the crown, which is what makes a drawn range as tall as
     * the built rock beside it - and a channel is water, not rock. Read into the river, the lift stood the
     * Bruinen up on the skirt of the range it runs past: the stretch by Rivendell read twenty blocks over the
     * drawing wherever the mask had any brightness and at the drawing's own level where it had none, which is
     * the dip into the vale at the city and the climb back out of it either side. A river's water, the bank
     * that holds it and the ground that gives way to it are all the channel's own level, so all three read
     * here. {@link Integer#MIN_VALUE} where the heightmap has no land at the column.
     */
    private static int channelAt(int blockX, int blockZ) {
        int mapped = ArdaMasks.ground(blockX, blockZ);
        if (mapped == ArdaMasks.NO_GROUND) {
            return Integer.MIN_VALUE;
        }
        return vale(blockX, blockZ,
                SEA_LEVEL + (int) Math.round(mapped / 65535.0D * (VALLEY_CROWN - SEA_LEVEL)));
    }

    /**
     * The height the ground beside any drawn river may stand at, one cell per mask cell, or null until it is built.
     */
    private static volatile char[] riverBanks;
    /** How much the valley's wall climbs per cell ring at each cell, in blocks: steep where the cut is deep. */
    private static volatile byte[] riverRises;
    /** How far out from a drawn river its valley is cut, in blocks. */
    private static final int RIVER_BANK = 192;
    /**
     * How many blocks the valley's wall climbs for each cell ring it is grown out, at the shallowest and the deepest:
     * a cell is eight blocks of run, so three is a rise of a third of a block a block and sixteen is a gorge's two.
     * <p>
     * One rate cannot do both jobs. At the Shire's half a block per block a valley fifty blocks deep would need a
     * hundred blocks of run and a hill two hundred blocks over its river - which is what a drawn river crossing a
     * range comes to, the mask lifting the ground and the channel keeping the drawing's level - would need four
     * hundred, so the reach would either leave a wall standing at its own edge or run a quarter of a cell into every
     * river in the world. The rate is read off the depth at the water instead: the country over the channel, divided
     * by the reach, held between these two.
     */
    private static final int RIVER_STEEP_MIN = 3;
    /** How far out the country is sampled to find the depth a valley has to climb to, in blocks. */
    private static final int RIVER_SAMPLE = 96;
    private static final int RIVER_STEEP_MAX = 16;
    /** How much roll the valley's wall has, in blocks, and how far in from its foot that roll fades. */
    private static final int RIVER_ROLL = 2;
    private static final int RIVER_ROLL_SCALE = 48;

    /**
     * How far the country stands over a river's water, read round the channel rather than at it, in blocks.
     * <p>
     * At the channel the answer is nothing: the water is laid at the ground it is drawn on, so the ground under it is
     * its own level however high the country about it stands. What the valley has to climb to is the ground a
     * {@link #RIVER_SAMPLE} blocks out, which is sampled in eight directions and answered as the tallest.
     */
    private static int riverDepth(int blockX, int blockZ, int water) {
        int deepest = 0;
        for (int step = 0; step < 8; step++) {
            double dirX = Math.cos(step * Math.PI / 4.0D);
            double dirZ = Math.sin(step * Math.PI / 4.0D);
            for (int reach = RIVER_SAMPLE; reach >= RIVER_SAMPLE / 2; reach -= RIVER_SAMPLE / 2) {
                int px = blockX + (int) Math.round(dirX * reach);
                int pz = blockZ + (int) Math.round(dirZ * reach);
                int mapped = ArdaMasks.ground(px, pz);
                if (mapped == ArdaMasks.NO_GROUND) {
                    continue;
                }
                deepest = Math.max(deepest, ArdaMasks.lift(px, pz, mapped, VALLEY_CROWN) - water);
            }
        }
        return deepest;
    }

    /**
     * Builds the valley every drawn river is cut through: how high the ground beside it may stand, grown out from the
     * water one cell at a time.
     * <p>
     * This is the dale's own bank taken over the whole world, and for the same reason. A drawn river stands at the
     * height of the ground it is drawn on, so its banks are the country and there is nothing to grade; but wherever
     * the channel's level is not the ground's - the Shire's branch, whose water is read from the village above it, and
     * every river the mountain mask lifts the ground over - the bed is cut under a surface the hillside stands well
     * over, and a bed cut under such a surface is a slot: sheer walls with flat country on both lips. Grown from the
     * water, the ground beside it climbs to meet the country instead, and the wall's rate is read per channel from how
     * far the country stands over that water.
     * <p>
     * The dale's water is left to the dale. Its bank is graded over {@link #DALE_BANK} blocks at {@link #DALE_RISE},
     * which is a steeper, longer valley than this pass makes, and seeding both would grade the same ground twice and
     * leave the Bruinen in a trench neither rule asked for.
     */
    private static void buildRiverBanks() {
        int width = ArdaMasks.maskWidth();
        int height = ArdaMasks.maskHeight();
        int blocks = Math.max(1, ArdaMasks.maskBlocks());
        int west = ArdaMasks.maskWest();
        int north = ArdaMasks.maskNorth();
        if (width <= 0 || height <= 0) {
            riverBanks = new char[0];
            riverRises = new byte[0];
            return;
        }
        char[] banks = new char[width * height];
        byte[] rises = new byte[width * height];
        int[] queue = new int[1 << 14];
        int tail = 0;
        for (int row = 0; row < height; row++) {
            int z = north + row * blocks;
            for (int column = 0; column < width; column++) {
                int x = west + column * blocks;
                if (!ArdaMasks.riverCell(column, row)) {
                    continue;
                }
                if (ArdaMasks.daleRun(x, z) >= 0) {
                    continue;
                }
                int mapped = ArdaMasks.ground(x, z);
                if (mapped == ArdaMasks.NO_GROUND) {
                    continue;
                }
                int water = Math.max(SEA_LEVEL, drawnWater(x, z));
                // Read round the channel rather than at it: the ground under the water is the water's own level, so the
                // depth at the column says nothing about the country a valley has to climb to. Sampled at the reach's
                // own width, the rise is set by the tallest ground the valley will have to meet.
                int depth = riverDepth(x, z, water);
                int rise = Mth.clamp((int) Math.ceil(depth * (double) blocks / RIVER_BANK),
                        RIVER_STEEP_MIN, RIVER_STEEP_MAX);
                banks[row * width + column] = (char) (water + 1);
                rises[row * width + column] = (byte) rise;
                if (tail == queue.length) {
                    queue = java.util.Arrays.copyOf(queue, tail * 2);
                }
                queue[tail++] = row * width + column;
            }
        }
        for (int step = 0; step < RIVER_BANK / blocks; step++) {
            int end = tail;
            for (int at = 0; at < end; at++) {
                int cell = queue[at];
                int bank = banks[cell] + rises[cell];
                int row = cell / width;
                int column = cell - row * width;
                for (int dir = 0; dir < 4; dir++) {
                    int nearRow = row + (dir == 0 ? 1 : dir == 1 ? -1 : 0);
                    int nearColumn = column + (dir == 2 ? 1 : dir == 3 ? -1 : 0);
                    if (nearRow < 0 || nearRow >= height || nearColumn < 0 || nearColumn >= width) {
                        continue;
                    }
                    int nearCell = nearRow * width + nearColumn;
                    if (banks[nearCell] != 0) {
                        continue;
                    }
                    banks[nearCell] = (char) bank;
                    rises[nearCell] = rises[cell];
                    if (tail == queue.length) {
                        queue = java.util.Arrays.copyOf(queue, tail * 2);
                    }
                    queue[tail++] = nearCell;
                }
            }
        }
        riverBanks = banks;
        riverRises = rises;
    }

    /**
     * How high the ground beside a drawn river may stand, or {@link Integer#MAX_VALUE} where no river's valley reaches.
     * <p>
     * Read between cells, like the dale's bank and for the same reason: a cell's own height held for eight blocks of
     * tread would make the slope a staircase of shelves. The roll is added after the read and only by as much as the
     * valley takes off the column, so a column the valley does not touch is not moved and a bank it shaves by a block
     * keeps its edge.
     */
    private static int riverCeiling(int blockX, int blockZ) {
        char[] banks = riverBanks;
        if (banks == null) {
            synchronized (BRUINEN_LOCK) {
                banks = riverBanks;
                if (banks == null) {
                    buildRiverBanks();
                    banks = riverBanks;
                }
            }
        }
        int width = ArdaMasks.maskWidth();
        int height = ArdaMasks.maskHeight();
        if (banks.length == 0 || width <= 0 || height <= 0) {
            return Integer.MAX_VALUE;
        }
        double across = (blockX - ArdaMasks.maskWest()) / (double) ArdaMasks.maskBlocks() - 0.5D;
        double along = (blockZ - ArdaMasks.maskNorth()) / (double) ArdaMasks.maskBlocks() - 0.5D;
        if (across < -0.5D || along < -0.5D || across > width - 0.5D || along > height - 0.5D) {
            return Integer.MAX_VALUE;
        }
        int west = (int) Math.floor(Math.max(0.0D, across));
        int north = (int) Math.floor(Math.max(0.0D, along));
        int east = Math.min(width - 1, west + 1);
        int south = Math.min(height - 1, north + 1);
        double shareX = Math.min(1.0D, Math.max(0.0D, across - west));
        double shareZ = Math.min(1.0D, Math.max(0.0D, along - north));
        double total = 0.0D;
        double sum = 0.0D;
        for (int corner = 0; corner < 4; corner++) {
            int row = corner < 2 ? north : south;
            int column = corner % 2 == 0 ? west : east;
            int value = banks[row * width + column];
            if (value == 0) {
                continue;
            }
            double weight = (corner % 2 == 0 ? 1.0D - shareX : shareX) * (corner < 2 ? 1.0D - shareZ : shareZ);
            total += weight;
            sum += weight * value;
        }
        if (total <= 0.0D) {
            return Integer.MAX_VALUE;
        }
        return (int) Math.round(sum / total);
    }

    /**
     * How high the ground beside a drawn river may stand, or {@link Integer#MAX_VALUE} where no river's valley reaches.
     * <p>
     * The ceiling with the roll on it, and only where it actually takes something off this column: a column the
     * valley does not touch is not moved, and a bank it shaves by a block keeps its edge.
     */
    private static int riverBank(int blockX, int blockZ, int standing) {
        int bank = riverCeiling(blockX, blockZ);
        if (bank == Integer.MAX_VALUE || standing <= bank) {
            return Integer.MAX_VALUE;
        }
        double roll = (fractal(blockX, blockZ, RIVER_ROLL_SCALE, 79) - 0.5D) * 2.0D * RIVER_ROLL
                * Math.min(1.0D, (standing - bank) / (double) RIVER_ROLL);
        return bank + (int) Math.round(roll);
    }

    /**
     * The level drawn water stands at in a column: its channel's own, held to the dale's water where the
     * dale reaches it, or the ground where the map has none. A channel carried out of a build is the one stretch held
     * to neither - see {@link #Channel} - because the build it leaves stands in a hollow.
     */
    private static int drawnWater(int blockX, int blockZ) {
        int channel = channelAt(blockX, blockZ);
        if (channel == Integer.MIN_VALUE) {
            return groundAt(blockX / (double) CELL + ORIGIN_COLUMN,
                    blockZ / (double) CELL + ORIGIN_ROW, blockX, blockZ).height();
        }
        // A carried channel is the one drawn channel whose level is not the drawing's: it is read from the build it
        // leaves. Only ever down, so nothing that is not one of them is moved.
        return Math.min(daleWater(blockX, blockZ, channel), carriedWater(blockX, blockZ));
    }

    /**
     * The vale's own water carried out along the drawn river, in blocks.
     * <p>
     * The vale's apron is a radius and the Bruinen is not: the river was pulled down to the vale's water
     * where the apron came nearest and released again as it left, so the water went down into the vale and
     * climbed back out of it either side. No river does that: water does not climb. The river stands at the
     * vale's own water - two blocks over its floor, rather than the city outlet's eight - along the whole
     * course the drawing gives it past the vale, and only the drawing takes it back, where it is lower.
     * <p>
     * A branch is the same rule read the other way. It comes to the river at the vale's water and falls to
     * it from its own height, one block of fall per {@link #DALE_GRADE} of run along the branch. Held flat
     * at the vale's water from wherever it entered the dale instead, every crossing had to lose its whole
     * height in one step where the dale began, which is the sheer face at the crossings.
     * <p>
     * The run is read on both halves of the river. Held for the leaving half alone, the water coming down
     * into the vale had no fall of its own and took its level straight off the vale's apron, which is a
     * radius: the surface was pulled down across the channel rather than along it - seventeen blocks over
     * forty of drawn channel at the vale's south rim, water lying on a hillside in plates - and at the ford,
     * where the two halves met, it lost twenty-four blocks in one column. Read from the water's own run, a
     * stretch a mile out stands the same whether it is coming or going, and the halves meet at the crossing
     * with nothing between them.
     */
    private static int daleWater(int blockX, int blockZ, int channel) {
        int run = ArdaMasks.daleRun(blockX, blockZ);
        if (run < 0) {
            return channel;
        }
        int fall = VALLEY_FLOOR + DALE_DEPTH + run / DALE_GRADE;
        // The reach's own end: the drawing takes the water back a little at a time, so the fall arrives at
        // the level the drawing gives the branch instead of stopping under it.
        double hand = Mth.clamp((DALE_REACH - run) / (double) DALE_EASE, 0.0D, 1.0D);
        double level = fall * smooth(hand) + channel * (1.0D - smooth(hand));
        return Math.min(channel, (int) Math.round(level));
    }

    /**
     * The height the ground beside the dale's water may stand at, one cell per mask cell, or null until
     * it is built. Every level in it moves with the vale's floor, so a raised world builds it again.
     */
    private static volatile char[] daleBanks;

    /**
     * Builds the bank: how high the ground beside the dale's water may stand, grown out from the water one
     * cell at a time.
     * <p>
     * The bank used to be walked out per column with eight rays, and that is what the canyon walls were: a
     * ray answers the distance along the ray, not the distance to the water, so between two rays the answer
     * came out too long and the cut left a wedge standing between them - a fan of faces down a stretch of
     * river that should have been one slope. Grown cell by cell from the water, a column's answer depends
     * only on how far it really is from the river.
     * <p>
     * The water stands one block out of the ground at the bank's foot and {@link #DALE_RISE} more for every
     * block of run, so the valley is a bowl with the river in the bottom of it rather than a slot with a lid.
     * The rise is per cell rather than per ring: a ring is {@code blocks} of run, and a bank grown a block a
     * ring put the ground twelve blocks over the water across ninety-six of tread and then ended - a shelf,
     * with the edge of the mask standing over it as the wall the save showed back from the river.
     */
    private static void buildDaleBanks() {
        int width = ArdaMasks.maskWidth();
        int height = ArdaMasks.maskHeight();
        int blocks = Math.max(1, ArdaMasks.maskBlocks());
        int west = ArdaMasks.maskWest();
        int north = ArdaMasks.maskNorth();
        if (width <= 0 || height <= 0) {
            daleBanks = new char[0];
            return;
        }
        char[] banks = new char[width * height];
        int[] queue = new int[1 << 14];
        int tail = 0;
        for (int row = 0; row < height; row++) {
            int z = north + row * blocks;
            for (int column = 0; column < width; column++) {
                int x = west + column * blocks;
                // The bank is grown from every drawn river the vale's water reaches, both the water leaving
                // it and the water coming down into it: the dale holds the level on both halves, so the
                // ground beside either has to be graded down to it, or a stretch keeps its drawn edge and
                // stands as a wall with the water at its foot.
                if (ArdaMasks.daleRun(x, z) < 0) {
                    continue;
                }
                if (!ArdaMasks.waterCell(column, row)) {
                    continue;
                }
                banks[row * width + column] = (char) Math.max(SEA_LEVEL, drawnWater(x, z));
                if (tail == queue.length) {
                    queue = java.util.Arrays.copyOf(queue, tail * 2);
                }
                queue[tail++] = row * width + column;
            }
        }
        for (int step = 0; step < DALE_BANK / blocks; step++) {
            int end = tail;
            for (int at = 0; at < end; at++) {
                int cell = queue[at];
                int bank = banks[cell] + blocks * DALE_RISE;
                int row = cell / width;
                int column = cell - row * width;
                for (int dir = 0; dir < 4; dir++) {
                    int nearRow = row + (dir == 0 ? 1 : dir == 1 ? -1 : 0);
                    int nearColumn = column + (dir == 2 ? 1 : dir == 3 ? -1 : 0);
                    if (nearRow < 0 || nearRow >= height || nearColumn < 0 || nearColumn >= width) {
                        continue;
                    }
                    int nearCell = nearRow * width + nearColumn;
                    if (banks[nearCell] != 0) {
                        continue;
                    }
                    banks[nearCell] = (char) bank;
                    if (tail == queue.length) {
                        queue = java.util.Arrays.copyOf(queue, tail * 2);
                    }
                    queue[tail++] = nearCell;
                }
            }
        }
        daleBanks = banks;
    }

    /**
     * How high the ground beside the dale's water may stand, or {@link Integer#MIN_VALUE} where the dale
     * does not reach.
     * <p>
     * Read between cells: the bank rises {@link #DALE_RISE} blocks per block of run on eight-block cells,
     * so a cell's own height held for eight blocks of tread would make the slope a staircase of shelves
     * instead. The roll is added after the read, and only by as much as the bank takes off this column: a
     * column the bank does not touch is not lifted, and a shore the bank shaves by a block keeps its edge.
     */
    private static int daleBank(int blockX, int blockZ, int standing) {
        char[] banks = daleBanks;
        if (banks == null) {
            synchronized (BRUINEN_LOCK) {
                banks = daleBanks;
                if (banks == null) {
                    buildDaleBanks();
                    banks = daleBanks;
                }
            }
        }
        int width = ArdaMasks.maskWidth();
        int height = ArdaMasks.maskHeight();
        if (banks.length == 0 || width <= 0 || height <= 0) {
            return Integer.MIN_VALUE;
        }
        double across = (blockX - ArdaMasks.maskWest()) / (double) ArdaMasks.maskBlocks() - 0.5D;
        double along = (blockZ - ArdaMasks.maskNorth()) / (double) ArdaMasks.maskBlocks() - 0.5D;
        if (across < -0.5D || along < -0.5D || across > width - 0.5D || along > height - 0.5D) {
            return Integer.MIN_VALUE;
        }
        int west = (int) Math.floor(Math.max(0.0D, across));
        int north = (int) Math.floor(Math.max(0.0D, along));
        int east = Math.min(width - 1, west + 1);
        int south = Math.min(height - 1, north + 1);
        double shareX = Math.min(1.0D, Math.max(0.0D, across - west));
        double shareZ = Math.min(1.0D, Math.max(0.0D, along - north));
        double total = 0.0D;
        double sum = 0.0D;
        int corner = banks[north * width + west];
        double weight = (1.0D - shareX) * (1.0D - shareZ);
        if (corner != 0) {
            total += weight;
            sum += weight * corner;
        }
        corner = banks[north * width + east];
        weight = shareX * (1.0D - shareZ);
        if (corner != 0) {
            total += weight;
            sum += weight * corner;
        }
        corner = banks[south * width + west];
        weight = (1.0D - shareX) * shareZ;
        if (corner != 0) {
            total += weight;
            sum += weight * corner;
        }
        corner = banks[south * width + east];
        weight = shareX * shareZ;
        if (corner != 0) {
            total += weight;
            sum += weight * corner;
        }
        if (total <= 0.0D) {
            return Integer.MIN_VALUE;
        }
        int bank = (int) Math.round(sum / total);
        if (standing <= bank) {
            return bank;
        }
        double roll = (fractal(blockX, blockZ, DALE_ROLL_SCALE, 71) - 0.5D) * 2.0D * DALE_ROLL
                * Math.min(1.0D, (standing - bank) / (double) DALE_ROLL);
        return bank + (int) Math.round(roll);
    }

    /**
     * The surface a drawn river's water stands at.
     * <p>
     * The channel is walked out in eight directions, across its own width, and the water takes the lowest
     * channel it meets on the way, never under the sea. A river's surface cannot stand above the water beside
     * drawn up a step becomes one fall instead of a pool waiting to spill into the row below, and where the
     * channel is drawn as a slope the surface descends with it instead of stepping. Only columns the river
     * runs through are asked, so the level is the river's own and never a lake's or the sea's.
     */
    private static int riverLevel(int blockX, int blockZ) {
        int surface = Math.max(SEA_LEVEL, drawnWater(blockX, blockZ));
        for (int step = 0; step < 8; step++) {
            double dirX = Math.cos(step * Math.PI / 4.0D);
            double dirZ = Math.sin(step * Math.PI / 4.0D);
            for (int reach = 2; reach <= RIVER_LEVEL_REACH; reach += 2) {
                int px = blockX + (int) Math.round(dirX * reach);
                int pz = blockZ + (int) Math.round(dirZ * reach);
                if (!ArdaMasks.river(px, pz)) {
                    continue;
                }
                int beside = drawnWater(px, pz);
                if (beside < surface) {
                    surface = Math.max(SEA_LEVEL, beside);
                }
            }
        }
        return surface;
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
                // A drawn river's water stands at its own channel's level, not at the ground the range's
                // lift has raised that channel to: the bank has to reach the water that is there.
                int surface = ArdaMasks.lake(px, pz) ? lakeLevel(px, pz, drawn)
                        : Math.max(SEA_LEVEL, drawnWater(px, pz));
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
        // The build leaves one corner of its own footprint empty - no piece stands there above the lowest
        // band - so the rock fills it at the crown rather than the floor: the city then reads as a massif
        // with a city cut into it, rather than a lawn with a hole in one corner.
        if (blockX >= SHOULDER_WEST && blockX <= SHOULDER_EAST
                && blockZ >= SHOULDER_NORTH && blockZ <= SHOULDER_SOUTH) {
            return new Sample(Country.IMLADRIS, SHOULDER_TOP, false, false, ground.upland());
        }
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
        int given = giveWay(x, z, height);
        if (given < height) {
            // The opening is the massif's own. Past the fall the ground is the drawn country, which nothing
            // here cuts, so the cut is taken back out over the last of the fall rather than run to the seam
            // and stopped there.
            double seam = Mth.clamp((beyond - (ridge + VALLEY_FALL - GIVE_EDGE)) / (double) GIVE_EDGE,
                    0.0D, 1.0D);
            height = (int) Math.round(given + (height - given) * smooth(seam));
        }
        return new Sample(country, height, false, false, ground.upland());
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
        daleBanks = null;
        riverBanks = null;
        riverRises = null;
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
        double run = 0.0D;
        int lastX = (int) Math.round(courseX(0.0D));
        int lastZ = (int) Math.round(courseZ(0.0D));
        for (int step = 1; step <= BRUINEN_STEPS; step++) {
            double share = step / (double) BRUINEN_STEPS;
            int x = (int) Math.round(courseX(share));
            int z = (int) Math.round(courseZ(share));
            run += Math.hypot(x - lastX, z - lastZ);
            lastX = x;
            lastZ = z;
            // The surface may fall no faster than the grade, so the eight blocks between the city's water and
            // the vale are carried down the course rather than dropped at the wall. The water it meets is
            // the river's own, not the ground the drawing lays at the column: the drawn Bruinen's surface is
            // held at the city's water where the branch joins it, and a branch read off the ground under the
            // fork fell eight blocks under the water it feeds there - the wall at the mouth.
            int drawn = drawnWater(x, z);
            int grade = BRUINEN_WATER - (int) Math.floor(run / BRUINEN_GRADE);
            lowest = Math.min(lowest, Math.max(drawn, grade));
            profile[step] = lowest;
        }
        // The join is the drawn river's own water, not the level the last step of the course lands on: the
        // step is read where the drawing lays it, and the drawn Bruinen stands a block higher on the water a
        // little upstream of the ford. The branch arrived at the crossing a block over the water it runs into
        // - the lip the save showed between the two rivers. Water does not stand over the river it feeds.
        profile[BRUINEN_STEPS] = Math.max(profile[BRUINEN_STEPS], riverLevel(
                (int) Math.round(courseX(1.0D)), (int) Math.round(courseZ(1.0D))));
        return profile;
    }

    /**
     * The Bruinen's gorge at a column, or the ground unchanged where the gorge does not reach.
     * <p>
     * The channel is cut into whatever stands there rather than drawn over it: the massif's flank and the
     * shoulder where the valley answers, the drawn country beyond the ridge where the valley has handed the
     * ground back, so the same cut opens the front of the valley and runs out to the ford. Its water falls
     * along the course, its bed lies {@link #BRUINEN_BED} under that, and its walls climb from the water on the
     * same curve the massif gives way on, {@link #MOUTH_RUN} blocks to the crown. The climb stops where the ground is lower than
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
        }        if (at[2] > MOUTH_RUN) {
            // The cut is the massif opening to its own river, and it has finished opening at the end of
            // the climb: past that the wall is the crown and there is nothing here to cut. A column six
            // hundred blocks off the course still projects onto one of its corners, and the vale floor
            // there stands under the branch's water - which read as a river across country the drawing
            // gives no river to, dressed as a river bed where the mountain should be.
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
            // The same curve the massif gives way with, read from the middle of the channel: one profile
            // for the bank and for the cone means the two never meet in a face where one stops applying.
            double wall = Math.max(level, mouthRise(level, Math.abs(side)) + ribs);
            height = (int) Math.round(Math.min(ground.height(), wall));
        }
        // Only the channel carries the river's own level: the walls stand above it, and the valley floor
        // beside the mouth stands under it, so a floor given this level as its water would be flooded to
        // the height of the river thirty blocks away from it.
        int water = out <= 0.0D ? (int) Math.round(level) : SEA_LEVEL;
        // Only the channel is the river. The ground beside it read as river whenever it stood under the
        // water's level, and in the vale that is every column of the floor: a country of river biome and
        // a bed of sand where the drawing has neither, because the floor stands eight blocks under the
        // city's own water.
        boolean channel = out <= 0.0D && height < level;
        return new Sample(ground.country(), height, channel, false, ground.upland(), water);

    }


    /**
     * The height the ground may stand at a run out from water, given that water's level.
     * <p>
     * The toe is nearly flat, so the water's own bank is a few blocks up the slope and no face is left
     * beside it; the middle carries most of the climb; the last few blocks ease into the crown, so the
     * mountainside meets the massif's flat top without a knee in it. Two grades drawn as straight lines
     * meet in an edge wherever one stops applying - and a river mouth is exactly the place where the
     * ground has to leave the water at one rate and reach the shelf at another.
     */
    private static double mouthRise(double level, double run) {
        double share = Mth.clamp(run / (double) MOUTH_RUN, 0.0D, 1.0D);
        return level + (VALLEY_CROWN - level) * smooth(share);
    }

    /**
     * The massif's own height, given way to the water it stands beside, in blocks.
     * <p>
     * The rock stands at the crown where the city's floor ends - which the build's own walls hide - but the
     * drawn Bruinen runs past the valley's east side at the floor's level, and the branch out of the city
     * meets it there: rock two hundred and forty blocks high beside water at seventy-four is not a valley a
     * river runs through, it is a slot cut in a plateau. So the ground climbs away from any water on one curve,
     * {@link #mouthRise}, and both rivers open the massif into a valley instead of a trench.
     * <p>
     * The nearest water is the branch's own course, measured exactly, or the nearest drawn river column found
     * in eight directions: the walk out to it is the bank's run, and its surface is the level the ground has
     * to come down to. Nothing is done where the column already stands no higher than that.
     */
    private static int giveWay(int blockX, int blockZ, int height) {
        // The course is measured where it runs, and its two ends are water: the city's at the mouth and
        // the Bruinen's at the join, which the walk below reaches by its drawn channel. What is not water
        // is a column beyond an end, measured across the course's extension: closing the massif over the
        // mouth is a wall, and opening it six hundred blocks from the river is a vale behind the city.
        // They are different columns, and only one of them is the river's.
        double run = Double.MAX_VALUE;
        int level = SEA_LEVEL;
        if (BRANCH) {
            // Measured from the middle of the channel rather than from its bank, so the rise starts
            // under the water: the rock has given way before the water's own edge, and the cut leaves
            // no face beside the river for the bank to end on.
            double[] at = project(blockX, blockZ);
            // Only the course's own length is water. Past the mouth the branch has not left the city
            // yet, and past the join the water there is the drawn Bruinen's, which the walk below finds
            // for itself. A column beyond an end was measured across the course's extension instead, and
            // the rock behind the walls came down to the river's level six hundred blocks from any water:
            // a vale gouged into the massif at the back of the city, level with the branch and nowhere
            // near it.
            if (at[5] < 0.5D && at[6] < 0.5D) {
                double offsetX = blockX - courseX(at[0]);
                double offsetZ = blockZ - courseZ(at[0]);
                run = Math.abs(offsetX * -at[4] + offsetZ * at[3]);
                level = bruinenWater(at[0]);
            }
        }
        for (int step = 0; step < 8; step++) {
            double dirX = Math.cos(step * Math.PI / 4.0D);
            double dirZ = Math.sin(step * Math.PI / 4.0D);
            for (int reach = 4; reach <= WATER_REACH; reach += 4) {
                int px = blockX + (int) Math.round(dirX * reach);
                int pz = blockZ + (int) Math.round(dirZ * reach);
                // Only water on the branch's own side of the valley opens the massif. The drawn Bruinen runs
                // past the valley's east side as well, and giving way to it there brought the whole eastern
                // flank down to the river - a valley the city has no mouth into, cut into the back of the
                // rock. The valley drains west, where the branch leaves it, so drawn water east of its middle
                // is water the massif stands over rather than water it opens to.
                if (px > VALLEY_X) {
                    break;
                }
                if (!ArdaMasks.river(px, pz)) {
                    continue;
                }
                // The walk is coarse, and four blocks of error in the run is a step in the ground it
                // shapes, so the water's own edge is found a block at a time from where the walk met it.
                int edge = reach;
                for (int back = 1; back <= 3; back++) {
                    if (!ArdaMasks.river(blockX + (int) Math.round(dirX * (reach - back)),
                            blockZ + (int) Math.round(dirZ * (reach - back)))) {
                        break;
                    }
                    edge = reach - back;
                }
                double away = edge - 2.0D;
                if (away < run) {
                    run = away;
                                    // The water it gives way to is the water that is drawn there: the range's lift is not
                // the river's level, and ground lowered to the lifted one still stands over the channel.
                level = Math.max(SEA_LEVEL, drawnWater(px, pz));
                }
                break;
            }
        }
        // The course's own distance is exact however far a column stands from it, so the rock can give way
        // to the river the whole way up its bank. A drawn river is only found within the reach above, and
        // that reach is longer than the climb, so a column past it is already at the crown: the cut ends
        // where the climb does rather than in a step.
        if (run >= MOUTH_RUN) {
            return height;
        }
        double limit = mouthRise(level, Math.max(0.0D, run));
        // The cut is a function of how far the column stands from the water, so every contour on it runs
        // parallel to the river and the bench beside it comes out as plates: the bank at the ford showed a
        // mile of them, one block a plate, all following the channel. The ribs the gorge's wall already has
        // break those contours into spurs and hollows, faded in from the water so the foot of the slope is
        // still the water's own edge - and the cut is never taken under the water, or the bench is pocked
        // with pools the river never ran into.
        if (run > 0.0D) {
            double ribs = (fractal((int) Math.floor(blockX), (int) Math.floor(blockZ), CLIMB_SCALE, 23) - 0.5D)
                    * 2.0D * CLIMB_ROUGH * Math.min(1.0D, run / CLIMB_EASE);
            limit = Math.max(level, limit + ribs);
        }
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
        // The shore is read off the ground that is actually there, not off the drawing: the cells are blended
        // over half a cell either side of a border, so a cell drawn at seventy beside a sea drawn at forty
        // crosses the waterline in a band eighty blocks wide - a beach across country that is nowhere near
        // the water. A hole in the drawing (every cell starts as sea) then reads as a coast on all four sides.
        boolean shore = seaInReach && Math.abs(height - SEA_LEVEL) <= BEACH_HEIGHT;
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
        int height;
        if (mapped != ArdaMasks.NO_GROUND) {
            height = ragged(blockX, blockZ, ArdaMasks.lift(blockX, blockZ, vale(blockX, blockZ,
                    SEA_LEVEL + (int) Math.round(mapped / 65535.0D * (VALLEY_CROWN - SEA_LEVEL))),
                    VALLEY_CROWN));
        } else {
            height = ragged(blockX, blockZ, ArdaMasks.lift(blockX, blockZ,
                    Mth.clamp((int) Math.round(drawn + relief * (rough / 1.75D - 0.5D) * 2.0D), -60,
                            MOUNTAIN_CEILING), VALLEY_CROWN));
        }
        // Gundabad last, so the valley cuts the drawn rock, its crags and its lean-to all at once.
        return gundabadPass(blockX, blockZ, height);
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
        // Measured from the massif's own foot rather than from the end of its fall. The fall blends the
        // crest away to whatever the ground answers, and the apron stood past it, so the ground the fall
        // landed on was the vale floor: a flat ring beyond the ridge where a mountain the drawing puts at
        // a hundred and sixty stood at seventy-four. The apron begins where the foot does, so the fall has
        // drawn country to land on and the range keeps its height past the crest.
        double share = (beyond - VALLEY_RIM) / (double) VALLEY_VALE;

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

    /**
     * The Gundabad pass: the massif brought down to a floor the gate fits in, with its own plateau over the back
     * of it and its walls at the gate's own edges.
     * <p>
     * The notch is drawn to the build rather than to a country. The ground climbs out of the floor at once, two and
     * a half blocks of height for every block of distance from the footprint (`PASS_STEP`), so the gate's ends are
     * into the rock within about thirty blocks; the climb stops where it meets the country's own drawn surface, and
     * past that the mountain is the drawing's, sloping away the way it always did. Behind the gate there is no rock
     * at the back at all: the gate is the only way into the keep, so thirty-two blocks of level plaza stand behind it
     * for the army that camps there (`PASS_PLAZA`), and the climb starts again past it. The surface carries the map's
     * own folded noise and the crests that wander along the pass, so a stepped slope reads as rock rather than as a
     * staircase. The distance the climb is measured over is the distance from the footprint as a whole - across beside
     * the box, north behind the plaza, south down the pass, and both together at the corners - so the sides and the
     * back are one flank and not two ramps meeting in a crease. South of the gate the pass opens - the floor widens from
     * the gate's hundred and sixty to seven hundred and sixty over four hundred blocks, and the walls ease from a
     * cliff into a hillside - so what is behind the gate is a courtyard the hold can stand in, and what is beside
     * it is the mountain it was cut out of.
     * <p>
     * The floor falls as it goes south, from the gate's own hundred and eighty-seven to a hundred and seventy-six
     * at the mouth, where the drawn ground is already that low: the pass drains away from the fortress and joins
     * the Hithaeglir without a seam.
     */
    private static int gundabadPass(int blockX, int blockZ, int height) {
        int east = PASS_X + PASS_MOUTH_HALF + PASS_SIDE_RUN;
        int west = PASS_X - PASS_MOUTH_HALF - PASS_SIDE_RUN;
        if (blockX < west || blockX > east || blockZ < PASS_HEAD_Z - PASS_BACK_REACH
                || blockZ > PASS_MOUTH_Z + PASS_MOUTH_RUN) {
            return height;
        }
        double along = blockZ - PASS_HEAD_Z;
        double length = PASS_MOUTH_Z - PASS_HEAD_Z;
        double span = Mth.clamp(along / length, 0.0D, 1.0D);

        // How open the pass is at this depth: closed at the gate, a courtyard beyond it.
        double opened = smooth(Mth.clamp((along - PASS_GATE_KEEP) / (double) PASS_GATE_EASE, 0.0D, 1.0D));
        double half = PASS_HEAD_HALF + (PASS_MOUTH_HALF - PASS_HEAD_HALF) * opened;
        // The walls' foot beside the gate is the height of its wings, coming down to the floor as the pass opens.
        // The floor rolls only past the gate: under the build it is level to the block, because a floor course
        // that a roll has lifted off the ground by one is a gap under the whole gate.
        double roll = (fractal(blockX, blockZ, PASS_ROLL_SCALE, 23) - 0.5D) * 2.0D * PASS_ROLL
                * Mth.clamp((along - PASS_GATE_KEEP) / 120.0D, 0.0D, 1.0D);
        double floor = PASS_HEAD_FLOOR + (PASS_MOUTH_FLOOR - PASS_HEAD_FLOOR) * span + roll;

        double across = Math.abs(blockX - PASS_X) - half;
        double behind = -along;
        // The mountain's foot starts inboard of the footprint on both sides - thirty-nine blocks on the west, twenty-four
        // on the east - so the ground runs down into the box instead of stopping at its edge, and the gate is placed
        // into the mountain. The foot is the floor, 187, where the climb begins; that is just inside the box now, and
        // the cells the build writes are the ones that carry the floor the rest of the way.
        double shift = blockX < PASS_X ? PASS_FOOT_WEST : PASS_FOOT_EAST;
        double outX = Math.max(0.0D, across + shift);
        double outZ = Math.max(0.0D, behind - PASS_PLAZA);
        double out = Math.sqrt(outX * outX + outZ * outZ);
        if (out <= 0.0D) {
            if (behind > 0.0D && behind <= PASS_PLAZA) {
                // The plaza behind the gate: level to the block, because an army stands on it.
                return (int) Math.round(floor);
            }
            // Inside the pass and clear of both feet: the floor, and only the mouth eases it back into the open ground.
            return blend(floor, height, (along - length) / PASS_MOUTH_RUN);
        }
        // The foot stands highest beside the build's own depth and comes back down to the floor along the courtyard,
        // which is why the lift fades out south of the box.
        double alongWeight = 1.0D - smooth(Mth.clamp((along - length) / 32.0D, 0.0D, 1.0D));
        double lift = PASS_SINK * (outX / (outX + outZ)) * alongWeight;
        double shaped = Math.min(height, floor + lift + out * PASS_STEP);
        // And broken by the map's own folded noise the way a range is, fading in off the build's own edge so the
        // gate still meets the ground exactly where it stands.
        double bite = smooth(Mth.clamp((out - 1.0D) / 8.0D, 0.0D, 1.0D));
        double crag = 1.0D - Math.abs(fractal(blockX, blockZ, PASS_CRAG_SCALE, 37));
        crag += 0.5D * (1.0D - Math.abs(fractal(blockX, blockZ, 11, 41)));
        double spur = (1.0D - Math.abs(fractal(blockX, blockZ, PASS_SPUR_SCALE, 19))) - 0.5D;
        shaped += ((crag / 1.5D - 0.5D) * 2.0D * PASS_CRAG + spur * 2.0D * PASS_SPUR) * bite;
        // Inside the footprint the ground never stands above the build's own top: the build's cells are what carry its
        // roof, and a rock left above them would survive the placing and sit on the parapet. Outside the footprint the
        // mountain is free to stand over the roof, which is the point of it.
        if (across <= 0.0D) {
            shaped = Math.min(shaped, PASS_EDGE_TOP);
        }
        return (int) Math.round(shaped);
    }

    /** The ground between a floor the pass has shaped and the country's own, by how far the shaping has run. */
    private static int blend(double inner, int height, double share) {
        if (share >= 1.0D) {
            return height;
        }
        double weight = 1.0D - smooth(Mth.clamp(share, 0.0D, 1.0D));
        return (int) Math.round(inner * weight + height * (1.0D - weight));
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
        // Mount Gundabad is a country of its own rather than more of the range: the three-peaked massif
        // where the Misty Mountains meet Angmar's mountains, painted after the line so it takes the drawn
        // range's own cells instead of standing beside it. Two columns and two rows is the massif and the
        // shoulders under its peaks - x 5,120 to 6,143 and z -7,680 to -6,145 - which is the ground north
        // of the Ettenmoors and east of Angmar that the maps give the mountain.
        fill(map, Country.GUNDABAD, 20, 5, 21, 6);
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
        // The westernmost of the hills' three cells was never filled: the hills are three cells wide, and the
        // fill was narrowed to two to keep off the Trollshaws' rows, which left the map's default sea standing
        // inside Eriador's eastern edge - a two-cell hole between the Midgewater and the valley, with ground
        // drawn at seventy on one side of it and nothing but the drawing's bed under the hole itself.
        fill(map, Country.FIRIEN, 17, 17, 17, 18);
        // And the same hole at the hills' southern end, where they meet the mountains.
        fill(map, Country.FIRIEN, 20, 19, 20, 19);
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

        // The Sea of Rhun's eastern shore is Rhun's own, and it is painted last of all because it is the
        // one country whose cells were taken from it: the waste's fill runs the whole eastern edge of the
        // map, and a shore painted before it ended up as waste on three sides of the water. The sea itself
        // is at columns 41-46 and rows 17-22, so the shore is the columns east of Rhun's own fill and the
        // row north of the sea's.
        fill(map, Country.RHUN, 44, 16, 46, 22);

        // The cells the poster draws as land but no fill reaches. The grid begins as open sea, so every gap in
        // the fills is a bay of it: these are the cells closed in on every side by land, each taking the country
        // it is surrounded by. Filling one uncovers the next, so the list is what the rule settles on, not what
        // one sweep found. A bay the maps do draw stays, because it reaches water the drawing has open.
        //
        fill(map, Country.FORODWAITH, 3, 1, 3, 1);
        fill(map, Country.HITHAEGLIR, 23, 5, 23, 5);
        fill(map, Country.HITHAEGLIR, 23, 6, 23, 6);
        fill(map, Country.HITHAEGLIR, 23, 7, 23, 7);
        fill(map, Country.GLADDEN, 24, 7, 24, 7);
        fill(map, Country.HITHAEGLIR, 23, 8, 23, 8);
        fill(map, Country.GLADDEN, 24, 8, 24, 8);
        fill(map, Country.HITHAEGLIR, 23, 9, 23, 9);
        fill(map, Country.GLADDEN, 24, 9, 24, 9);
        fill(map, Country.HITHAEGLIR, 23, 10, 23, 10);
        fill(map, Country.GLADDEN, 24, 10, 24, 10);
        fill(map, Country.HITHAEGLIR, 23, 11, 23, 11);
        fill(map, Country.GLADDEN, 24, 11, 24, 11);
        fill(map, Country.HITHAEGLIR, 23, 12, 23, 12);
        fill(map, Country.GLADDEN, 24, 12, 24, 12);
        fill(map, Country.GLADDEN, 24, 13, 24, 13);
        fill(map, Country.GLADDEN, 24, 14, 24, 14);
        fill(map, Country.LINDON, 2, 19, 2, 19);
        fill(map, Country.ERIADOR, 17, 19, 17, 19);
        fill(map, Country.WILD_WOOD, 27, 19, 27, 19);
        fill(map, Country.ROHAN, 21, 21, 22, 21);
        fill(map, Country.ERIADOR, 13, 22, 15, 22);
        fill(map, Country.DUNLAND, 16, 22, 17, 22);
        fill(map, Country.DUNLAND, 20, 22, 21, 22);
        fill(map, Country.ROHAN, 22, 22, 22, 22);
        fill(map, Country.WHITE_MOUNTAINS, 15, 27, 15, 27);
        fill(map, Country.ITHILIEN, 34, 29, 34, 29);
        fill(map, Country.ASH_MOUNTAINS, 35, 29, 35, 29);
        fill(map, Country.NURN, 36, 29, 36, 29);
        fill(map, Country.HARAD, 34, 30, 37, 30);
        fill(map, Country.NURN, 38, 30, 41, 30);
        fill(map, Country.KHAND, 42, 30, 42, 30);
        fill(map, Country.GONDOR, 21, 31, 21, 31);

        // The countries the poster names that no fill had reached. Each had a biome, a name and a height and
        // nowhere in the world to be: Cardolan's hills between the Barrow Downs and the Greyflood (the cells the
        // last sweep had given to Eriador), Minhiriath's coast below the Shire, Enedwaith between it and the White
        // Mountains' western end, Dorwinion on the north-western shore of the Sea of Rhun, and Umbar on the coast
        // below Belfalas.
        fill(map, Country.CARDOLAN, 13, 22, 15, 22);
        fill(map, Country.MINHIRIATH, 5, 22, 10, 22);
        fill(map, Country.MINHIRIATH, 5, 23, 11, 23);
        fill(map, Country.ENEDWAITH, 7, 24, 10, 25);
        fill(map, Country.DORWINION, 38, 16, 40, 17);
        fill(map, Country.UMBAR, 22, 34, 24, 35);
        // Which uncovered a few more cells, taken the same way as the pass above.
        fill(map, Country.MINHIRIATH, 4, 22, 4, 22);
        fill(map, Country.MINHIRIATH, 6, 24, 6, 24);
        fill(map, Country.WHITE_MOUNTAINS, 11, 24, 11, 24);

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
