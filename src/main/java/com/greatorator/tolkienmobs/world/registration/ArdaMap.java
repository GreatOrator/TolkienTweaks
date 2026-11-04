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
     * The valley the map overrides rather than draws: its middle, the half-widths of its flat floor, the
     * height of that floor, and how many blocks of rim it takes to climb back to the Trollshaws.
     * <p>
     * A cell of the drawing is 512 blocks and the city that stands here is 554 across, so this ground
     * cannot be a country of the 48 by 36 grid: it is cut out of cells the drawing already fills and
     * blended back into them at its rim. Its middle is the middle of that city - the corner it is
     * planted from, plus half of its 554 by 512 footprint - so a city planted at 4352, -3833 stands in
     * the middle of the floor rather than against one of its walls. The floor is a few blocks smaller
     * than the footprint on every side - 544 by 502 inside a 554 by 512 city - so the mountain's foot
     * begins inside the build and the build embeds itself in it. There is no apron of flat ground in
     * front of the walls for the join to be dressed across, because there is no join: the lowest band of
     * the city is laid into the rising ground, and the rock that was there shows wherever the build has
     * nothing of its own.
     */
    private static final int VALLEY_X = 4628;
    private static final int VALLEY_Z = -3577;
    private static final int VALLEY_HALF_X = 272;
    private static final int VALLEY_HALF_Z = 251;
    private static final int VALLEY_FLOOR = 74;

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
    private static final int VALLEY_CROWN = 318;

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
    private static final int VALLEY_FALL = 400;

    /**
     * The height the mountains round the valley reach, and how far from that they are allowed to wander.
     * <p>
     * The ridge stands above the city's crown at 318, so the city is shut inside the massif and its roof
     * is not the highest thing in the country. Neither the ridge's height nor its distance is one number,
     * though: the drawn noise raises summits and hollows saddles along it, over the scales given here.
     * Fixed, the same numbers are a wall of even height at an even distance - a basin, not a range.
     */
    private static final int VALLEY_CREST = 380;
    private static final int CREST_SWING = 45;
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
    private static final int SHOULDER_TOP = VALLEY_CROWN;
    private static final int SHOULDER_RUN = 110;

    /**
     * The Bruinen: where the city's own channel leaves the walls, the ford below the valley, and the gorge
     * the river is cut through on its way there.
     * <p>
     * The build carries its water out at 82 with its bed seven blocks under that, a little above the valley
     * floor, and the drawing pins the ford to the Trollshaws' cell at 3840, -3328. Between the two the massif
     * stands at the city's crown, so a channel cut by the maps' own river lines - which flatten every column
     * they touch to a level 63 - met rock two hundred and thirty blocks over the water, and the front of the
     * valley was a sliced terrace rather than a bank. The river is cut as its own gorge instead: the build's
     * level at the outlet, the level the drawing's rivers stand at at the ford, a bed under it, and walls that
     * rise from the water at a grade a hillside holds rather than a wall a map cell draws. The ford's own
     * water level replaces {@link #BRUINEN_FORD_WATER} as soon as it is read off the ground there.
     */
    private static final int BRUINEN_OUTLET_X = 4356;
    private static final int BRUINEN_OUTLET_Z = -3528;
    private static final int BRUINEN_WATER = 82;
    private static final int BRUINEN_BED = 7;
    /**
     * How far the channel runs either side of the course, read off the build's own water: twelve blocks
     * across, five to the left of the middle block and seven to the right.
     */
    private static final int BRUINEN_LEFT = 5;
    private static final int BRUINEN_RIGHT = 7;
    private static final int BRUINEN_FORD_X = 3840;
    private static final int BRUINEN_FORD_Z = -3328;
    private static final int BRUINEN_FORD_WATER = SEA_LEVEL;
    private static final double BRUINEN_BANK = 1.2D;
    private static final double BRUINEN_FORD_SHALLOW = 0.8D;

    /** How deep a river's and a lake's bed lies under its water, and how far the grade is read. */
    private static final int RIVER_DEPTH = 4;
    private static final int RIVER_GRADE = 64;
    private static final int LAKE_DEPTH = 6;
    private static final int LAKE_GRADE = 192;
    /** The highest the drawing may raise the ground, leaving the world's ceiling room above it. */
    private static final int MOUNTAIN_CEILING = 420;

    /**
     * The height water stands at over a column, read as the ground's own grade about it.
     * <p>
     * A river is not level and neither is the ground it runs through: the surface is the average of
     * the ground over a square of the map, so the water follows the fall of the country instead of
     * standing at one height from the mountains to the sea.
     */
    private static int grade(int blockX, int blockZ, double shiftX, double shiftZ, int reach) {
        int total = 0;
        int samples = 0;
        for (int dx = -reach; dx <= reach; dx += reach) {
            for (int dz = -reach; dz <= reach; dz += reach) {
                int x = blockX + dx;
                int z = blockZ + dz;
                total += groundAt((x + shiftX) / (double) CELL + ORIGIN_COLUMN,
                        (z + shiftZ) / (double) CELL + ORIGIN_ROW, x, z).height();
                samples++;
            }
        }
        return Math.round(total / (float) samples);
    }

    private static final Country[][] GRID = draw();

    private ArdaMap() {
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
        ERED_MITHRIN(TolkienBiomes.IRON_HILLS, 145, 28, true),
        IRON_HILLS(TolkienBiomes.IRON_HILLS, 145, 28, true),

        // The north-west: Arnor's lost realm and the cold moors beyond it.
        ERIADOR(TolkienBiomes.ERIADOR, 70, 5, true),
        ANGMAR(TolkienBiomes.ANGMAR, 88, 14, true),
        RHUDAUR(TolkienBiomes.BARROW_DOWNS, 86, 13, true),
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
        RHUN(TolkienBiomes.GLADDEN, 70, 7, true),

        // The south: the Mark, the white wall, Gondor and the enemy's country.
        ROHAN(TolkienBiomes.ROHAN, 72, 6, true),
        WHITE_MOUNTAINS(TolkienBiomes.WHITE_MOUNTAINS, 165, 32, true),
        GONDOR(TolkienBiomes.GONDOR, 70, 5, true),
        MARSHES(TolkienBiomes.MARSHES, 64, 3, true),
        DAGORLAD(TolkienBiomes.DAGORLAD, 70, 6, true),
        ITHILIEN(TolkienBiomes.FIRIEN, 74, 8, true),
        MORDOR(TolkienBiomes.MORDOR, 74, 10, true),
        ASH_MOUNTAINS(TolkienBiomes.ASH_MOUNTAINS, 150, 30, true),
        NURN(TolkienBiomes.NURN, 61, 4, true),
        HARAD(TolkienBiomes.HARADWAITH, 72, 8, true);

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
        // The Bruinen is cut into whatever the drawing and the valley answer, because it runs through
        // both: out of the valley's own floor, through the massif's flank, and into the country beyond
        // its ridge.
        return bruinen(blockX, blockZ, readDrawn(blockX, blockZ, shiftX, shiftZ));
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
        Sample valley = valley(blockX, blockZ, ground);
        if (valley != null) {
            return valley;
        }

        // The masks answer where a river or a lake is, and the water stands at the grade of the
        // ground it is cut into rather than at one level for the whole world: a river that starts in
        // the mountains and ends at the sea falls the way the ground under it falls.
        if (ArdaMasks.lake(blockX, blockZ)) {
            // An inland sea is drawn at the height of the sea, so a lake never drains below it.
            int surface = Math.max(SEA_LEVEL, grade(blockX, blockZ, shiftX, shiftZ, LAKE_GRADE));
            return new Sample(ground.country(), Math.min(ground.height(), surface - LAKE_DEPTH), true,
                    false, ground.upland(), surface);
        }
        if (ArdaMasks.river(blockX, blockZ)) {
            int surface = grade(blockX, blockZ, shiftX, shiftZ, RIVER_GRADE);
            return new Sample(ground.country(), Math.min(ground.height(), surface - RIVER_DEPTH), true,
                    false, ground.upland(), surface);
        }
        return new Sample(ground.country(), ground.height(), false, ground.shore(), ground.upland());
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
     * One corner of the floor is not floor: the city's north-east corner has no build in it above its
     * lowest band, because the mountain stood there, so {@link #shoulder} stands the rock back up.
     */
    private static Sample valley(double blockX, double blockZ, Ground ground) {
        double beyondX = Math.abs(blockX - VALLEY_X) - VALLEY_HALF_X;
        double beyondZ = Math.abs(blockZ - VALLEY_Z) - VALLEY_HALF_Z;
        // A distance from the floor's rectangle, not one from each of its four sides: the difference is
        // at the corners, where this rounds the mountain's foot instead of squaring it off.
        double beyond = Math.hypot(Math.max(0.0D, beyondX), Math.max(0.0D, beyondZ));
        int x = (int) Math.floor(blockX);
        int z = (int) Math.floor(blockZ);
        // The ridge is not a ring at one height: it wanders in and out, and its summits rise and fall
        // along it, which is the whole difference between a range of mountains and the rim of a bowl.
        double ridge = VALLEY_RIM * (1.0D + RIDGE_SWING * (fractal(x, z, RIDGE_SCALE, 31) - 0.5D) * 2.0D);
        double crest = VALLEY_CREST + CREST_SWING * (fractal(x, z, CREST_SCALE, 43) - 0.5D) * 2.0D;
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
            height = (int) Math.round(VALLEY_CROWN + (crest - VALLEY_CROWN) * climb(beyond / ridge) + ribs);
        } else {
            double fell = Math.min(1.0D, (beyond - ridge) / (double) VALLEY_FALL);
            height = (int) Math.round(crest + (ground.height() - crest) * smooth(fell));
        }
        int shoulder = shoulder(blockX, blockZ);
        if (shoulder > height) {
            // Rock standing in the city's corner is the mountain's country, not the city's floor.
            return new Sample(ground.country(), shoulder, false, false, ground.upland());
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
    static int massifTop() {
        return VALLEY_CREST + CREST_SWING;
    }

    /**
     * How much of the way up the mountain the ground has climbed at a share of the distance to the ridge.
     * <p>
     * Steepest at the foot and rounded at the top: the ground starts climbing at the city's walls rather
     * than lying flat for a lawn's width, gains most of its height in the first half of the climb, and eases
     * into the ridge so the top is a shoulder rather than a point.
     */
    private static double climb(double share) {
        double t = Math.min(1.0D, share);
        return 1.0D - Math.pow(1.0D - t, 1.2D);
    }

    /**
     * The rock the city's north-east corner was cut into, as a height.
     * <p>
     * The build's own gap is x 4797..4905 by z -3833..-3774, and nothing in the cut stands in it above the
     * lowest band: the mountain was there rather than the city. So the mountain is there again - a shelf at
     * the city's own crown, level with the roofs it meets, that falls away outside the walls over
     * {@link #SHOULDER_RUN} blocks into the massif's foot. Inside the footprint it stops at the gap's own
     * edges, where the build's blocks are, so it can bury nothing the city has.
     */
    private static int shoulder(double blockX, double blockZ) {
        double beyondX = Math.max(Math.max(0.0D, SHOULDER_WEST - blockX), blockX - SHOULDER_EAST);
        double beyondZ = Math.max(Math.max(0.0D, SHOULDER_NORTH - blockZ), blockZ - SHOULDER_SOUTH);
        boolean beyondTheBuild = blockX > SHOULDER_EAST || blockZ < SHOULDER_NORTH;
        if (!beyondTheBuild && (beyondX > 0.0D || beyondZ > 0.0D)) {
            return Integer.MIN_VALUE;
        }
        double beyond = Math.hypot(Math.max(0.0D, beyondX), Math.max(0.0D, beyondZ));
        if (beyond >= SHOULDER_RUN) {
            return Integer.MIN_VALUE;
        }
        return (int) Math.round(SHOULDER_TOP
                - (SHOULDER_TOP - VALLEY_FLOOR) * smooth(beyond / (double) SHOULDER_RUN));
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
        double alongX = BRUINEN_FORD_X - BRUINEN_OUTLET_X;
        double alongZ = BRUINEN_FORD_Z - BRUINEN_OUTLET_Z;
        double share = ((blockX - BRUINEN_OUTLET_X) * alongX + (blockZ - BRUINEN_OUTLET_Z) * alongZ)
                / (alongX * alongX + alongZ * alongZ);
        if (share < 0.0D || share > 1.0D) {
            return ground;
        }
        double offsetX = blockX - (BRUINEN_OUTLET_X + share * alongX);
        double offsetZ = blockZ - (BRUINEN_OUTLET_Z + share * alongZ);
        // Measured across the course rather than as a radius: the river is twelve blocks wide and is not
        // the same width either side of it, so the bank is found by which way the column lies of the flow.
        double along = Math.sqrt(alongX * alongX + alongZ * alongZ);
        double side = (offsetX * -alongZ + offsetZ * alongX) / along;
        double out = side > 0.0D ? side - BRUINEN_RIGHT : -BRUINEN_LEFT - side;
        double level = BRUINEN_WATER + (BRUINEN_FORD_WATER - BRUINEN_WATER) * share;
        double shallow = smooth(Mth.clamp((share - BRUINEN_FORD_SHALLOW) / (1.0D - BRUINEN_FORD_SHALLOW),
                0.0D, 1.0D));
        int height;
        if (out <= 0.0D) {
            height = (int) Math.round(level - BRUINEN_BED * (1.0D - shallow));
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

    /** Whether a cell of the map exists at all, since a search may walk off its edge. */
    static boolean inside(int column, int row) {
        return column >= 0 && column < COLUMNS && row >= 0 && row < ROWS;
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
        if (cells == 0) return BlockPos.ZERO;
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
        int relief = northWest.relief();
        double rough = fractal(blockX, blockZ, 192, 1) + 0.5D * fractal(blockX, blockZ, 96, 2)
                + 0.25D * fractal(blockX, blockZ, 48, 3);
        int height = ArdaMasks.lift(blockX, blockZ, Mth.clamp((int) Math.round(
                drawn + relief * (rough / 1.75D - 0.5D) * 2.0D), -60, MOUNTAIN_CEILING), VALLEY_CROWN);

        boolean shore = seaInReach && Math.abs(drawn - SEA_LEVEL) <= BEACH_HEIGHT;
        return new Ground(northWest, height, drawn, shore, upland);
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
        fill(map, Country.BARROW_DOWNS, 12, 23, 13, 23);
        fill(map, Country.DUNLAND, 12, 23, 20, 26);
        fill(map, Country.DUNLAND, 20, 23, 23, 25);

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
        fill(map, Country.HARAD, 44, 4, 47, 35);

        // The Mark, the white wall across the middle of the south, and Gondor below it.
        fill(map, Country.ROHAN, 23, 20, 29, 24);
        fill(map, Country.WHITE_MOUNTAINS, 11, 25, 34, 26);
        fill(map, Country.GONDOR, 16, 27, 30, 30);

        // The Dead Marshes, the Brown Lands and the Dagorlad, north of Mordor's wall.
        fill(map, Country.MARSHES, 27, 22, 30, 24);
        fill(map, Country.DAGORLAD, 30, 23, 34, 25);
        fill(map, Country.DAGORLAD, 31, 21, 36, 22);

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
