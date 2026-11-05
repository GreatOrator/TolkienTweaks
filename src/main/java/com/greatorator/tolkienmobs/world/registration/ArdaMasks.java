package com.greatorator.tolkienmobs.world.registration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * The reference masks, as world generation reads them.
 * <p>
 * Arda's ground began as a hand-drawn 48 by 36 grid with seven river polylines on it. The maps the
 * world was drawn from are rasters, and they are read here instead: a river mask and a lake mask, which
 * say how much of each cell the drawing marks, and a mountain mask whose brightness is height. All cover the
 * world's own frame at {@link #BLOCKS} blocks to the pixel, which is x -5120..19456 by
 * z -10240..8192, or 3072 by 2304 pixels, and they are baked from the 8192 originals by
 * {@code tools/arda_masks.py} using the calibration in {@code Reference/work/calibration.json}.
 * <p>
 * Nothing here is fatal. A missing image leaves its mask empty and the drawing answers as it did
 * before, so a build without the assets still makes a world.
 */
final class ArdaMasks {
    /** Blocks to the pixel in all three masks. */
    static final int BLOCKS = 8;
    private static final int WEST = -5120;
    private static final int NORTH = -10240;
    private static final int WIDTH = (19456 - WEST) / BLOCKS;
    private static final int HEIGHT = (8192 - NORTH) / BLOCKS;

    /** The rivers and lakes masks: the share of each cell the drawing marks, 0 to 255. */
    private static final byte[] RIVERS = coverage("rivers.png");
    /**
     * The Bruinen's own corner, baked again at three blocks to the pixel and read over its own window.
     * <p>
     * Eight-block pixels cannot place a channel twelve blocks wide: the mask says a river runs through
     * an eight-block square, and the river the city runs out to has to be met block for block. So the
     * corner of the map the city stands in has a fine bake of its own, and inside that window it answers
     * instead of the coarse one.
     */
    private static final int BRUINEN_BLOCKS = 3;
    private static final int BRUINEN_WEST = 4200;
    private static final int BRUINEN_NORTH = -4100;
    private static final int BRUINEN_WIDE = (5400 - BRUINEN_WEST) / BRUINEN_BLOCKS;
    private static final int BRUINEN_DEEP = (-2900 - BRUINEN_NORTH) / BRUINEN_BLOCKS;
    private static final boolean[] BRUINEN = fine("bruinen.png");
    private static final byte[] LAKES = coverage("lakes.png");
    /**
     * How much of a cell the drawing has to mark before the game calls it water.
     * <p>
     * Cells are eight blocks across and the drawing's own pixel is four, so a river crossing a cell
     * covers half of it, and a thin tributary drawn along a cell's middle covers a third. Held at a
     * third, the tributary is still a channel and a wide river's bank falls just outside the line drawn.
     */
    private static final double WATER = 0.3D;
    /** The narrowest a drawn channel is cut to, in blocks: a river a person can still stand in. */
    private static final int RIVER_MIN = 3;
    /** How finely, and how far, a channel's width is walked when a third of it is taken off. */
    private static final int RIVER_STEP = 4;
    private static final int RIVER_WALK = 60;
    /**
     * The mountain mask: brightness is height, and its whitest pixel is the crown.
     * <p>
     * It reaches pure white, so the pin is on the ranges' own summits: a column the mask draws white
     * stands at the crown the city's massif reaches, and the ground is raised toward it by the brightness
     * between, which the blur turns into a range rising out of the country.
     */
    private static final byte[] ROCK = coverage("mountains.png");
    /** How bright the mask's brightest pixel is, from 0 to 1: the climb that pins the crown. */
    private static final double PEAK = peak(ROCK);

    /** The topography, unsigned 16-bit at BLOCKS to the pixel; sea is zero. */
    private static final char[] GROUND = ground("height.png");
    /** What {@link #ground} answers where the heightmap has no land. */
    static final int NO_GROUND = Integer.MIN_VALUE;

    private ArdaMasks() {
    }

    /**
     * Whether the river mask runs through a column, narrowed by a third.
     * <p>
     * The rivers the map is drawn from are wide - the bake marks a channel forty blocks across and more
     * - and water laid across the whole of that is a river with no banks worth the name: nothing to
     * bridge, nothing to ford, and every drawn brook the width of a lake. A third of the width is taken
     * off, a sixth from each bank, so a forty-block channel reads at twenty-eight and a wide one at
     * about two-thirds of what it was. The cut is proportional rather than a fixed number of blocks because the
     * drawings are not one width: a channel is cut where the drawing is still water that far in from each
     * bank, and never taken under {@link #RIVER_MIN} blocks, which is the narrowest a channel can be and
     * still be a channel.
     */
    static boolean river(int blockX, int blockZ) {
        int column = Math.floorDiv(blockX - BRUINEN_WEST, BRUINEN_BLOCKS);
        int row = Math.floorDiv(blockZ - BRUINEN_NORTH, BRUINEN_BLOCKS);
        if (column >= 0 && column < BRUINEN_WIDE && row >= 0 && row < BRUINEN_DEEP) {
            return BRUINEN[row * BRUINEN_WIDE + column];
        }
        if (!drawn(blockX, blockZ)) {
            return false;
        }
        int cut = cut(blockX, blockZ);
        return cut == 0
                || (drawn(blockX + cut, blockZ) && drawn(blockX - cut, blockZ)
                && drawn(blockX, blockZ + cut) && drawn(blockX, blockZ - cut));
    }

    /** Whether the drawing marks a column as water, before any narrowing. */
    private static boolean drawn(int blockX, int blockZ) {
        return cover(RIVERS, blockX, blockZ) >= WATER;
    }

    /**
     * How far each bank is taken in, in blocks: a sixth of the channel's width, never enough to leave it
     * under {@link #RIVER_MIN} across.
     */
    private static int cut(int blockX, int blockZ) {
        int width = channelWidth(blockX, blockZ);
        int sixth = width / 6;
        return Math.max(0, Math.min(sixth, (width - RIVER_MIN) / 2));
    }

    /**
     * The channel's width at a column, in blocks.
     * <p>
     * Walked as far as the drawing is water along each of the four axes and read as the shorter of the
     * two chords through the column: the long one runs down the river, the short one crosses it. Every
     * column across a straight channel is on the same chord, so the width is the channel's own and not
     * the reader's distance from the bank.
     */
    static int channelWidth(int blockX, int blockZ) {
        int across = reach(blockX, blockZ, 1, 0) + reach(blockX, blockZ, -1, 0) + 1;
        int along = reach(blockX, blockZ, 0, 1) + reach(blockX, blockZ, 0, -1) + 1;
        return Math.min(across, along);
    }

    /**
     * How many blocks a river column stands from its own bank, from nothing at the water's edge.
     * <p>
     * Walked in single blocks, unlike the width, because this is the slope of the bed rather than a
     * measurement of it: a column one block out of the channel cannot have two blocks of water under it
     * without the bank becoming a wall. Past {@link #RIVER_STEP} blocks the channel is deep enough that
     * the depth cap answers instead, so the walk stops there.
     */
    static int riverEdge(int blockX, int blockZ) {
        for (int step = 1; step <= RIVER_STEP; step++) {
            if (!drawn(blockX + step, blockZ) || !drawn(blockX - step, blockZ)
                    || !drawn(blockX, blockZ + step) || !drawn(blockX, blockZ - step)) {
                return step - 1;
            }
        }
        return RIVER_STEP;
    }

    /** How far the drawing is still water from a column, walking out in one direction. */
    private static int reach(int blockX, int blockZ, int stepX, int stepZ) {
        int out = 0;
        for (int step = RIVER_WALK; step >= RIVER_STEP; step -= RIVER_STEP) {
            if (!drawn(blockX + stepX * step, blockZ + stepZ * step)) {
                continue;
            }
            out = step;
            break;
        }
        return out;
    }

    /**
     * How bright the mountain mask is over a column, from nothing to the pin.
     * <p>
     * Read between the cells like the water masks, because a bright ridge taken nearest would climb in
     * steps of eight blocks and terrace the flanks it is meant to smooth.
     */
    static double rock(int blockX, int blockZ) {
        return PEAK <= 0.0D ? 0.0D : cover(ROCK, blockX, blockZ) / PEAK;
    }

    /**
     * The ground at a column, raised toward the crown by how bright the mountain mask is over it.
     * <p>
     * The whitest pixels are pinned: a full-brightness column stands at {@code crown}, the height the
     * city's massif tops out at, so the drawn ranges and the build reach the same sky. Black leaves the
     * ground exactly as it was drawn, and everything between takes its share of the climb.
     */
    static int lift(int blockX, int blockZ, int height, int crown) {
        double rock = rock(blockX, blockZ);
        if (rock <= 0.0D || crown <= height) {
            return height;
        }
        return height + (int) Math.round(rock * (crown - height));
    }

    /** Whether the lake mask covers a column. */
    static boolean lake(int blockX, int blockZ) {
        return cover(LAKES, blockX, blockZ) >= WATER;
    }

    /**
     * The heightmap at a block column, interpolated between its pixels, or {@link #NO_GROUND} at sea.
     * <p>
     * The map is read at eight blocks to the pixel, so a value taken nearest would step the ground a
     * pixel at a time and make terraces with an eight-block tread. Interpolation costs three multiplies
     * and turns those steps into the slope the map actually draws.
     */
    static int ground(int blockX, int blockZ) {
        if (GROUND.length == 0) {
            return NO_GROUND;
        }
        double across = (blockX - WEST) / (double) BLOCKS;
        double along = (blockZ - NORTH) / (double) BLOCKS;
        if (across < 0.0D || along < 0.0D || across > WIDTH - 1 || along > HEIGHT - 1) {
            return NO_GROUND;
        }
        int west = (int) Math.floor(across);
        int north = (int) Math.floor(along);
        int east = Math.min(WIDTH - 1, west + 1);
        int south = Math.min(HEIGHT - 1, north + 1);
        double shareX = across - west;
        double shareZ = along - north;
        double north1 = GROUND[north * WIDTH + west] * (1.0D - shareX) + GROUND[north * WIDTH + east] * shareX;
        double south1 = GROUND[south * WIDTH + west] * (1.0D - shareX) + GROUND[south * WIDTH + east] * shareX;
        double raw = north1 * (1.0D - shareZ) + south1 * shareZ;
        return raw <= 0.5D ? NO_GROUND : (int) Math.round(raw);
    }

    private static char[] ground(String name) {
        BufferedImage image = read(name);
        if (image == null) {
            return new char[0];
        }
        char[] raster = new char[WIDTH * HEIGHT];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                raster[y * WIDTH + x] = (char) image.getRaster().getSample(x, y, 0);
            }
        }
        return raster;
    }

    /**
     * How much of the cell under a column the mask marks, read between the cells round it.
     * <p>
     * A cell's share stands at its middle, so a bank falls between two cells instead of snapping onto
     * one: read nearest, every edge is a step of eight blocks, and a river running obliquely - which is
     * most of them - is a staircase rather than a river.
     */
    private static double cover(byte[] mask, int blockX, int blockZ) {
        if (mask.length == 0) {
            return 0.0D;
        }
        double across = (blockX - WEST) / (double) BLOCKS - 0.5D;
        double along = (blockZ - NORTH) / (double) BLOCKS - 0.5D;
        if (across < -0.5D || along < -0.5D || across > WIDTH - 0.5D || along > HEIGHT - 0.5D) {
            return 0.0D;
        }
        int west = (int) Math.floor(Math.max(0.0D, across));
        int north = (int) Math.floor(Math.max(0.0D, along));
        int east = Math.min(WIDTH - 1, west + 1);
        int south = Math.min(HEIGHT - 1, north + 1);
        double shareX = Math.min(1.0D, Math.max(0.0D, across - west));
        double shareZ = Math.min(1.0D, Math.max(0.0D, along - north));
        double north1 = share(mask, north, west, east, shareX);
        double south1 = share(mask, south, west, east, shareX);
        return (north1 * (1.0D - shareZ) + south1 * shareZ) / 255.0D;
    }

    private static double share(byte[] mask, int row, int west, int east, double shareX) {
        return (mask[row * WIDTH + west] & 0xFF) * (1.0D - shareX) + (mask[row * WIDTH + east] & 0xFF) * shareX;
    }

    private static double peak(byte[] mask) {
        int brightest = 0;
        for (byte value : mask) {
            brightest = Math.max(brightest, value & 0xFF);
        }
        return brightest / 255.0D;
    }

    private static byte[] coverage(String name) {
        BufferedImage image = read(name);
        if (image == null) {
            return new byte[0];
        }
        byte[] mask = new byte[WIDTH * HEIGHT];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                mask[y * WIDTH + x] = (byte) (image.getRGB(x, y) & 0xFF);
            }
        }
        return mask;
    }

    /** A mask baked over its own window at its own blocks-to-the-pixel. */
    private static boolean[] fine(String name) {
        boolean[] mask = new boolean[BRUINEN_WIDE * BRUINEN_DEEP];
        BufferedImage image = read(name);
        if (image == null || image.getWidth() != BRUINEN_WIDE || image.getHeight() != BRUINEN_DEEP) {
            return mask;
        }
        for (int y = 0; y < BRUINEN_DEEP; y++) {
            for (int x = 0; x < BRUINEN_WIDE; x++) {
                mask[y * BRUINEN_WIDE + x] = (image.getRGB(x, y) & 0xFF) > 100;
            }
        }
        return mask;
    }

    private static BufferedImage read(String name) {
        try (InputStream in = ArdaMasks.class.getResourceAsStream("/assets/tolkienmobs/arda/" + name)) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException | RuntimeException failed) {
            return null;
        }
    }
}
