package com.greatorator.tolkienmobs.world.registration;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * Finds ground for a building by reading the drawing instead of the world.
 *
 * <p>A structure is normally placed by arithmetic on its chunk coordinates: the seed and the chunk
 * decide whether a chunk is a start chunk, and nothing is said about the ground until somebody is
 * close enough for it to be generated. That is how an eye of ender can point at a stronghold nobody
 * has ever been near, and it is the whole of the trick - the locating never needs a chunk.
 *
 * <p>Arda can go further, because its ground is not noise the world has to make. {@link ArdaMap}
 * draws the countries, their heights and their rivers once, over all 24,576 by 18,432 blocks of the
 * world, so any column can be read before a chunk of it exists. A site is therefore chosen by
 * looking, and what is looked for is the thing {@code Sites} would otherwise have had to find by
 * walking: ground in the country the structure belongs to, dry, clear of a river, and level enough
 * across the footprint it is going to fill.
 *
 * <p>The spot inside a cell is fixed by the cell and by the structure's own name, never by the player,
 * so it is the same ground for everybody. A page that says "a camp in Dunland" means one camp, and two
 * people sent there meet at it.
 *
 * <p>Two approximations are deliberate. The drawing is read without the world's warp, which moves the
 * drawn borders up to 192 blocks about, because the warp belongs to a generator this class is not
 * handed; and the ground under a site is the drawn ground, which the surface rules and the features
 * will dress and plant afterwards. Both are why the build still judges the real ground before it lays
 * anything down.
 */
public final class ArdaSurvey {
    /** How many cells out from the player's own are searched: the whole map, and then some. */
    private static final int CELL_REACH = 28;
    /** How many fixed spots inside one cell are tried before that cell is given up. */
    private static final int ATTEMPTS = 16;
    /** How far inside a cell a building is kept from that cell's own edge, in blocks. */
    private static final int MARGIN = 96;
    /** How far above the waterline a footprint's lowest column must stand to be dry ground. */
    private static final int DRY = 4;

    private ArdaSurvey() {
    }

    /** Ground the map offers a building: the country it stands in, and the middle of its floor. */
    public record Ground(ArdaMap.Country country, BlockPos centre) {
    }

    /**
     * The nearest ground to a point that will take a building of this size, or null when the map
     * draws none - which is the answer for a level that is not Arda's.
     *
     * @param fits the countries the building may stand in
     * @param wide the footprint's width, in blocks, along x
     * @param deep the footprint's depth, in blocks, along z
     * @param salt what makes two structures pick different ground in the same country
     * @param away how close to {@code near} the ground may be, in blocks, so a page is a walk rather
     *             than a build rising at the page-taker's own feet
     * @param within how far from {@code near} the ground may be at the furthest, in blocks, so a place
     *               the books put somewhere stays in that part of the map rather than anywhere the
     *               country happens to reach; zero means no limit
     */
    @Nullable
    public static Ground find(ServerLevel level, BlockPos near, Predicate<ArdaMap.Country> fits,
                              int wide, int deep, int salt, int away, int within) {
        return find(level, near, fits, wide, deep, salt, away, within, spot -> true);
    }

    /**
     * The same search, with a say over the ground it comes back with. A name that has to stand clear
     * of the names already written refuses the spots that would crowd them and the spiral carries on
     * to the next cell - which is the difference between a country's places and one place announced
     * under three names.
     */
    @Nullable
    public static Ground find(ServerLevel level, BlockPos near, Predicate<ArdaMap.Country> fits,
                              int wide, int deep, int salt, int away, int within,
                              Predicate<BlockPos> accept) {
        if (!(level.getChunkSource().getGenerator() instanceof ArdaChunkGenerator)) return null;
        int column = Math.floorDiv(near.getX(), ArdaMap.CELL) + ArdaMap.ORIGIN_COLUMN;
        int row = Math.floorDiv(near.getZ(), ArdaMap.CELL) + ArdaMap.ORIGIN_ROW;
        for (int ring = 0; ring <= CELL_REACH; ring++) {
            for (int across = -ring; across <= ring; across++) {
                for (int along = -ring; along <= ring; along++) {
                    if (Math.max(Math.abs(across), Math.abs(along)) != ring) continue;
                    if (!ArdaMap.inside(column + across, row + along)) continue;
                    // The cell is judged by its middle: a landmark allowed two cells of drift may take
                    // ground in the one next door, which is still the country the books name, and may
                    // not take ground on the far side of the map.
                    if (!nearby(near, column + across, row + along, within)) continue;
                    // Read off the ground rather than off the drawing: the valley is cut into cells the
                    // grid gives to the Trollshaws, so a name surveyed for it has no cell of its own.
                    ArdaMap.Country country = ArdaMap.countryAt(column + across, row + along);
                    if (!country.land() || !fits.test(country)) continue;
                    Ground ground = within(country, column + across, row + along, wide, deep, salt,
                            near, away, accept);
                    if (ground != null) return ground;
                }
            }
        }
        return null;
    }

    /** The first of one cell's fixed spots that will take the building, or null when none will. */
    @Nullable
    private static Ground within(ArdaMap.Country country, int column, int row,
                                 int wide, int deep, int salt, BlockPos near, int away,
                                 Predicate<BlockPos> accept) {
        int room = ArdaMap.CELL - MARGIN * 2 - Math.max(wide, deep);
        if (room <= 0) return null;
        int west = (column - ArdaMap.ORIGIN_COLUMN) * ArdaMap.CELL + MARGIN;
        int north = (row - ArdaMap.ORIGIN_ROW) * ArdaMap.CELL + MARGIN;
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            long mixed = mix(column, row, salt * 31L + attempt);
            int x = west + Math.floorMod(mixed >>> 20, room);
            int z = north + Math.floorMod(mixed >>> 44, room);
            if (!clear(near, x + wide / 2, z + deep / 2, away)) continue;
            Ground ground = judge(country, x, z, wide, deep);
            if (ground != null && accept.test(ground.centre())) return ground;
        }
        return null;
    }

    /**
     * Whether this footprint will take a building, judged from the drawing alone: an even stride of
     * columns across it, no river among them, none of them under water, all of them in the country
     * the cell was drawn as, and no more rise and fall between the lowest and the highest than a
     * building of this size will sit on.
     */
    @Nullable
    private static Ground judge(ArdaMap.Country country, int x, int z, int wide, int deep) {
        int stride = Math.max(4, Math.max(wide, deep) / 8);
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int dx = 0; dx <= wide; dx += stride) {
            for (int dz = 0; dz <= deep; dz += stride) {
                ArdaMap.Sample sample = ArdaMap.sample(x + dx, z + dz);
                if (sample.river() || sample.country() != country) return null;
                if (sample.height() < ArdaMap.SEA_LEVEL + DRY) return null;
                low = Math.min(low, sample.height());
                high = Math.max(high, sample.height());
            }
        }
        if (high - low > dropAllowed(wide, deep)) return null;
        return new Ground(country, new BlockPos(x + wide / 2, high + 1, z + deep / 2));
    }

    /**
     * How much rise and fall a building will still sit on: two blocks, widened by the building's own
     * size, so a tent wants a field and a castle will take a hillside. One rule, read here against the
     * drawing and again against the real ground when the building is laid.
     */
    public static int dropAllowed(int wide, int deep) {
        return Math.max(2, Math.max(wide, deep) / 8);
    }

    /** Whether a cell's middle is within {@code within} blocks of the point the search started at. */
    private static boolean nearby(BlockPos near, int column, int row, int within) {
        if (within <= 0) return true;
        long dx = (long) (column - ArdaMap.ORIGIN_COLUMN) * ArdaMap.CELL + ArdaMap.CELL / 2 - near.getX();
        long dz = (long) (row - ArdaMap.ORIGIN_ROW) * ArdaMap.CELL + ArdaMap.CELL / 2 - near.getZ();
        return dx * dx + dz * dz <= (long) within * within;
    }

    /**
     * Whether a spot is at least {@code away} blocks from the point the search started at. The map is
     * read a cell at a time, and the player's own cell is searched first, so without this a landmark
     * whose country they are already standing in is built exactly where they are standing.
     */
    private static boolean clear(BlockPos near, int x, int z, int away) {
        if (away <= 0) return true;
        long dx = x - near.getX();
        long dz = z - near.getZ();
        return dx * dx + dz * dz >= (long) away * away;
    }

    /** A well-mixed number from a cell and a structure, so one cell's ground is chosen once. */
    private static long mix(int column, int row, long salt) {
        long value = column * 0x9E3779B97F4A7C15L ^ row * 0xC2B2AE3D27D4EB4FL ^ salt;
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        return value ^ (value >>> 33);
    }
}
