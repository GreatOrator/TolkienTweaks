package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

/**
 * Prints the sections {@link RoadPlan} makes of a leg, off the game: see tools/sampleprobe.gradle.
 * <p>
 * The same reading a fresh world plans from, so a leg can be tried before a world is loaded rather than after.
 * Each argument is one leg, <code>x0,z0,x1,z1[,bridge]</code>, so a whole set of candidate ends is read in one run.
 */
public final class RoadProbe {
    private RoadProbe() {
    }

    public static void main(String[] args) {
        for (String arg : args) {
            String[] parts = arg.split(",");
            if (parts.length < 4) {
                System.out.println("skip " + arg + " - want x0,z0,x1,z1[,bridge]");
                continue;
            }
            String word = parts.length > 4 ? parts[4] : null;
            Roads.Bridge stood = word == null || word.equals("-") ? null : Roads.bridge(word);
            BlockPos from = new BlockPos(Integer.parseInt(parts[0]), 0, Integer.parseInt(parts[1]));
            BlockPos to = new BlockPos(Integer.parseInt(parts[2]), 0, Integer.parseInt(parts[3]));
            report(from, to, stood);
        }
    }

    private static void report(BlockPos from, BlockPos to, Roads.Bridge stood) {
        RoadPlan.Plan plan = RoadPlan.of("probe", Roads.Kind.WILD, false, from, to, stood);
        System.out.printf("from %d, %d to %d, %d%s%n", from.getX(), from.getZ(), to.getX(), to.getZ(),
                stood == null ? "" : " over " + stood.name());
        if (plan.refused()) {
            System.out.println("  refused: " + plan.refusal());
            return;
        }
        System.out.printf("  %d sections%s%n", plan.sections().size(), plan.doubled() ? ", doubled" : "");
        int index = 0;
        for (Route section : plan.sections()) {
            index++;
            if (section instanceof Route.Crossing span) {
                System.out.printf("  %d span  %-24s path %5d, %5d  bank %5d, %5d  far %5d, %5d  along %s"
                                + "  deck %d  width %d%n", index, span.id(), span.pathX(), span.pathZ(),
                        span.bankX(), span.bankZ(), span.farX(), span.farZ(), span.alongX() ? "x" : "z",
                        span.deckY(), Math.abs(span.alongX() ? span.farX() - span.bankX()
                                : span.farZ() - span.bankZ()) + 1);
            } else if (section instanceof Route.Riverless dry) {
                System.out.printf("  %d road  %-24s %5d, %5d -> %5d, %5d%n", index, dry.id(),
                        dry.pathX(), dry.pathZ(), dry.farX(), dry.farZ());
            }
        }
        System.out.println();
    }
}
