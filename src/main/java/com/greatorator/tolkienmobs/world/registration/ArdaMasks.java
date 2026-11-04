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
 * say how much of each cell the drawing marks, and a mountain mask that is reference only now. All cover the
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

    /** The topography, unsigned 16-bit at BLOCKS to the pixel; sea is zero. */
    private static final char[] GROUND = ground("height.png");
    /** What {@link #ground} answers where the heightmap has no land. */
    static final int NO_GROUND = Integer.MIN_VALUE;

    private ArdaMasks() {
    }

    /** Whether the river mask runs through a column. */
    static boolean river(int blockX, int blockZ) {
        int column = Math.floorDiv(blockX - BRUINEN_WEST, BRUINEN_BLOCKS);
        int row = Math.floorDiv(blockZ - BRUINEN_NORTH, BRUINEN_BLOCKS);
        if (column >= 0 && column < BRUINEN_WIDE && row >= 0 && row < BRUINEN_DEEP) {
            return BRUINEN[row * BRUINEN_WIDE + column];
        }
        return cover(RIVERS, blockX, blockZ) >= WATER;
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
