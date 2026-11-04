package com.greatorator.tolkienmobs.world.registration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * The reference masks, as world generation reads them.
 * <p>
 * Arda's ground began as a hand-drawn 48 by 36 grid with seven river polylines on it. The maps the
 * world was drawn from are rasters, and they are read here instead: a river mask, a lake mask, and a
 * mountain mask whose brightness is height - white is summit, black is plain. All three cover the
 * world's own frame at {@link #BLOCKS} blocks to the pixel, which is x -5120..19456 by
 * z -10240..8192, or 1536 by 1152 pixels, and they are baked from the 8192 originals by
 * {@code tools/arda_masks.py} using the calibration in {@code reference/work/calibration.json}.
 * <p>
 * Nothing here is fatal. A missing image leaves its mask empty and the drawing answers as it did
 * before, so a build without the assets still makes a world.
 */
final class ArdaMasks {
    /** Blocks to the pixel in all three masks. */
    static final int BLOCKS = 16;
    private static final int WEST = -5120;
    private static final int NORTH = -10240;
    private static final int WIDTH = (19456 - WEST) / BLOCKS;
    private static final int HEIGHT = (8192 - NORTH) / BLOCKS;

    private static final boolean[] RIVERS = bits("rivers.png");
    private static final boolean[] LAKES = bits("lakes.png");
    private static final byte[] ROCK = shade("mountains.png");

    /** The topography, unsigned 16-bit at BLOCKS to the pixel; sea is zero. */
    private static final char[] GROUND = ground("height.png");
    /** What {@link #ground} answers where the heightmap has no land. */
    static final int NO_GROUND = Integer.MIN_VALUE;

    private ArdaMasks() {
    }

    /** Whether the river mask runs through a column. */
    static boolean river(int blockX, int blockZ) {
        return marked(RIVERS, blockX, blockZ);
    }

    /** Whether the lake mask covers a column. */
    static boolean lake(int blockX, int blockZ) {
        return marked(LAKES, blockX, blockZ);
    }

    /** How bright the mountain mask is over a column, from nothing to full. */
    static double rock(int blockX, int blockZ) {
        int column = column(blockX);
        int row = row(blockZ);
        return column < 0 || row < 0 ? 0.0D : (ROCK[row * WIDTH + column] & 0xFF) / 255.0D;
    }

    /**
     * The drawn ground, raised toward the crown by how bright the mask is over it.
     * <p>
     * White is the crown: a full-brightness column stands at {@code crown}, the height the city's own
     * massif tops out at, so the mask's ranges and the build reach the same sky. Black leaves the drawn
     * ground exactly as the drawing set it, and everything between takes its share of the climb - which
     * is why the mask being blurred helps, as a range then rises out of the country instead of being
     * stood on top of it.
     */
    static int lift(int blockX, int blockZ, int height, int crown) {
        double rock = rock(blockX, blockZ);
        if (rock <= 0.0D || crown <= height) {
            return height;
        }
        return height + (int) Math.round(rock * (crown - height));
    }

    /**
     * The heightmap at a block column, interpolated between its pixels, or {@link #NO_GROUND} at sea.
     * <p>
     * The map is read at sixteen blocks to the pixel, so a value taken nearest would step the ground a
     * pixel at a time and make terraces with a sixteen-block tread. Interpolation costs three multiplies
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

    private static boolean marked(boolean[] mask, int blockX, int blockZ) {
        int column = column(blockX);
        int row = row(blockZ);
        return column >= 0 && row >= 0 && mask[row * WIDTH + column];
    }

    private static int column(int blockX) {
        int column = Math.floorDiv(blockX - WEST, BLOCKS);
        return column >= 0 && column < WIDTH ? column : -1;
    }

    private static int row(int blockZ) {
        int row = Math.floorDiv(blockZ - NORTH, BLOCKS);
        return row >= 0 && row < HEIGHT ? row : -1;
    }

    private static boolean[] bits(String name) {
        boolean[] mask = new boolean[WIDTH * HEIGHT];
        BufferedImage image = read(name);
        if (image == null) {
            return mask;
        }
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                mask[y * WIDTH + x] = (image.getRGB(x, y) & 0xFF) > 100;
            }
        }
        return mask;
    }

    private static byte[] shade(String name) {
        byte[] mask = new byte[WIDTH * HEIGHT];
        BufferedImage image = read(name);
        if (image == null) {
            return mask;
        }
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                mask[y * WIDTH + x] = (byte) (image.getRGB(x, y) & 0xFF);
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
