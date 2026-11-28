package com.greatorator.tolkienmobs.world.registration;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/** Answers what the drawing gives a column, off the game: see tools/sampleprobe.gradle. */
public final class SampleProbe {
    private SampleProbe() {
    }

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "at";
        if (mode.equals("box")) {
            int x0 = Integer.parseInt(args[1]);
            int z0 = Integer.parseInt(args[2]);
            int x1 = Integer.parseInt(args[3]);
            int z1 = Integer.parseInt(args[4]);
            int step = args.length > 5 ? Integer.parseInt(args[5]) : 16;
            for (int z = z0; z <= z1; z += step) {
                for (int x = x0; x <= x1; x += step) {
                    print(x, z);
                }
            }
        } else if (mode.equals("map")) {
            raster(args);
        } else {
            for (int index = 1; index + 1 < args.length + 1 && index + 1 <= args.length; index += 2) {
                if (index + 1 >= args.length + 1) break;
                print(Integer.parseInt(args[index]), Integer.parseInt(args[index + 1]));
            }
        }
    }

    private static void print(int x, int z) {
        ArdaMap.Sample sample = ArdaMap.sample(x, z);
        ArdaMap.Country country = sample.country();
        System.out.printf("%d %d %d %d %s %s %s %s %d%n", x, z, sample.height(), sample.water(),
                sample.river(), sample.shore(), country != null ? country.name() : "null",
                country != null && country.land(), ArdaMasks.ground(x, z));
    }

    /**
     * A window of the drawing as a binary raster, six bytes to a sample, for tools/arda_map.py to paint:
     * the ground the world stands at, the level its water is laid at, whether it is river, shore or a sea
     * country, and which country it reads as. Nothing of a build or a pin is in it - this is `sample`, the
     * same answer the generator's biome source and surface rules start from.
     *
     * <pre>map x0 z0 x1 z1 step out.bin</pre>
     */
    private static void raster(String[] args) throws Exception {
        int x0 = Integer.parseInt(args[1]);
        int z0 = Integer.parseInt(args[2]);
        int x1 = Integer.parseInt(args[3]);
        int z1 = Integer.parseInt(args[4]);
        int step = Integer.parseInt(args[5]);
        File out = new File(args[6]);
        if (out.getParentFile() != null) {
            out.getParentFile().mkdirs();
        }
        int width = (x1 - x0) / step + 1;
        int height = (z1 - z0) / step + 1;
        long started = System.nanoTime();
        try (DataOutputStream stream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(out), 1 << 22))) {
            stream.writeInt(0x41524441);
            stream.writeInt(width);
            stream.writeInt(height);
            stream.writeInt(step);
            stream.writeInt(x0);
            stream.writeInt(z0);
            byte[] row = new byte[width * 6];
            for (int rowIndex = 0; rowIndex < height; rowIndex++) {
                int z = z0 + rowIndex * step;
                for (int column = 0; column < width; column++) {
                    ArdaMap.Sample sample = ArdaMap.sample(x0 + column * step, z);
                    ArdaMap.Country country = sample.country();
                    int flags = 0;
                    if (country != null && country.land()) flags |= 1;
                    if (country != null && !country.land()) flags |= 8;
                    if (sample.river()) flags |= 2;
                    if (sample.shore()) flags |= 4;
                    int at = column * 6;
                    row[at] = (byte) (sample.height() >> 8);
                    row[at + 1] = (byte) sample.height();
                    row[at + 2] = (byte) (sample.water() >> 8);
                    row[at + 3] = (byte) sample.water();
                    row[at + 4] = (byte) flags;
                    row[at + 5] = (byte) (country == null ? 255 : country.ordinal());
                }
                stream.write(row);
                if (rowIndex % 192 == 0) {
                    System.err.printf("%d of %d rows, %.0fs%n", rowIndex, height,
                            (System.nanoTime() - started) / 1.0e9);
                }
            }
        }
        System.out.printf("%dx%d at %d blocks to the pixel -> %s, %.1fs%n", width, height, step,
                out, (System.nanoTime() - started) / 1.0e9);
    }
}
