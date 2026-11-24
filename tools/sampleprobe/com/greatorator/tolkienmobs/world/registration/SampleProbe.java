package com.greatorator.tolkienmobs.world.registration;

/** Answers what the drawing gives a column, off the game: see tools/sampleprobe.gradle. */
public final class SampleProbe {
    private SampleProbe() {
    }

    public static void main(String[] args) {
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
}
