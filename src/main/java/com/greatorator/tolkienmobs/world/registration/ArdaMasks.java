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
    /** How much height the mountain mask may add to the drawn ground, in blocks. */
    static final int MOUNTAIN_UPLIFT = 150;

    private static final int WEST = -5120;
    private static final int NORTH = -10240;
    private static final int WIDTH = (19456 - WEST) / BLOCKS;
    private static final int HEIGHT = (8192 - NORTH) / BLOCKS;

    private static final boolean[] RIVERS = bits("rivers.png");
    private static final boolean[] LAKES = bits("lakes.png");
    private static final byte[] ROCK = shade("mountains.png");

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

    /** How far the mountain mask raises the ground at a column, in blocks. */
    static int uplift(int blockX, int blockZ) {
        int column = column(blockX);
        int row = row(blockZ);
        if (column < 0 || row < 0) {
            return 0;
        }
        int rock = ROCK[row * WIDTH + column] & 0xFF;
        return (int) Math.round(rock / 255.0D * MOUNTAIN_UPLIFT);
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
