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
     */
    @Nullable
    public static Ground find(ServerLevel level, BlockPos near, Predicate<ArdaMap.Country> fits,
                              int wide, int deep, int salt) {
        if (!(level.getChunkSource().getGenerator() instanceof ArdaChunkGenerator)) return null;
        int column = Math.floorDiv(near.getX(), ArdaMap.CELL) + ArdaMap.ORIGIN_COLUMN;
        int row = Math.floorDiv(near.getZ(), ArdaMap.CELL) + ArdaMap.ORIGIN_ROW;
        for (int ring = 0; ring <= CELL_REACH; ring++) {
            for (int across = -ring; across <= ring; across++) {
                for (int along = -ring; along <= ring; along++) {
                    if (Math.max(Math.abs(across), Math.abs(along)) != ring) continue;
                    if (!ArdaMap.inside(column + across, row + along)) continue;
                    ArdaMap.Country country = ArdaMap.cellCountry(column + across, row + along);
                    if (!country.land() || !fits.test(country)) continue;
                    Ground ground = within(country, column + across, row + along, wide, deep, salt);
                    if (ground != null) return ground;
                }
            }
        }
        return null;
    }

    /** The first of one cell's fixed spots that will take the building, or null when none will. */
    @Nullable
    private static Ground within(ArdaMap.Country country, int column, int row,
                                 int wide, int deep, int salt) {
        int room = ArdaMap.CELL - MARGIN * 2 - Math.max(wide, deep);
        if (room <= 0) return null;
        int west = (column - ArdaMap.ORIGIN_COLUMN) * ArdaMap.CELL + MARGIN;
        int north = (row - ArdaMap.ORIGIN_ROW) * ArdaMap.CELL + MARGIN;
        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            long mixed = mix(column, row, salt * 31L + attempt);
            int x = west + Math.floorMod(mixed >>> 20, room);
            int z = north + Math.floorMod(mixed >>> 44, room);
            Ground ground = judge(country, x, z, wide, deep);
            if (ground != null) return ground;
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
