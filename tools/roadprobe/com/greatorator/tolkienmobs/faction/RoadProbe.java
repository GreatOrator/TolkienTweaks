package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Plans road legs against the drawing alone, and prints what the planner made of them.
 * <p>
 * This is the instrument for a road that refuses: {@link RoadPlan} reads {@code ArdaMap.sample} and nothing else, so a
 * leg can be planned on a machine with no world, no chunks and no client. Run it from the mod root with the mod's own
 * runtime classpath (see {@code docs/next-up.md}, the roads section).
 * <p>
 * usage:
 * <pre>
 *   RoadProbe leg &lt;kind&gt; &lt;x1&gt; &lt;z1&gt; &lt;x2&gt; &lt;z2&gt; [&lt;x1&gt; &lt;z1&gt; &lt;x2&gt; &lt;z2&gt; ...]
 *   RoadProbe marks &lt;kind&gt; &lt;x1&gt; &lt;z1&gt; &lt;x2&gt; &lt;z2&gt;   (the same, with every recorded bend's block)
 *   RoadProbe sample &lt;x&gt; &lt;z&gt;
 *   RoadProbe along &lt;x&gt; &lt;z&gt; &lt;dx&gt; &lt;dz&gt; &lt;steps&gt;   (a column every dx,dz from x,z)
 * </pre>
 * A leg is planned at the kind named, untowned, with both ends at y 0 - the planner reads x and z and never the height.
 */
public final class RoadProbe {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("usage: leg <kind> <x1> <z1> <x2> <z2> [...] | sample <x> <z> | along <x> <z> <dx> <dz> <steps>");
            return;
        }
        switch (args[0]) {
            case "leg" -> legs(args, false);
            case "marks" -> legs(args, true);
            case "sample" -> {
                for (int at = 1; at + 1 < args.length; at += 2) print(Integer.parseInt(args[at]), Integer.parseInt(args[at + 1]));
            }
            case "along" -> {
                int x = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);
                int dx = Integer.parseInt(args[3]);
                int dz = Integer.parseInt(args[4]);
                int steps = Integer.parseInt(args[5]);
                for (int step = 0; step <= steps; step++) print(x + dx * step, z + dz * step);
            }
            default -> System.out.println("unknown mode " + args[0]);
        }
    }

    private static void legs(String[] args, boolean bendBlocks) {
        Roads.Kind kind = Roads.Kind.valueOf(args[1]);
        for (int at = 2; at + 3 < args.length; at += 4) {
            int x1 = Integer.parseInt(args[at]);
            int z1 = Integer.parseInt(args[at + 1]);
            int x2 = Integer.parseInt(args[at + 2]);
            int z2 = Integer.parseInt(args[at + 3]);
            String id = "probe/leg_" + x1 + "_" + z1 + "_" + x2 + "_" + z2;
            BlockPos from = new BlockPos(x1, 0, z1);
            BlockPos to = new BlockPos(x2, 0, z2);
            long began = System.nanoTime();
            RoadPlan.Plan plan = RoadPlan.of(id, kind, false, from, to);
            long took = (System.nanoTime() - began) / 1_000_000L;
            System.out.println("== " + x1 + ", " + z1 + " -> " + x2 + ", " + z2 + "  (" + kind + ", " + took + " ms)");
            if (plan.refused()) {
                System.out.println("   refused: " + plan.refusal());
                continue;
            }
            List<Route> sections = plan.sections();
            System.out.println("   " + sections.size() + " section" + (sections.size() == 1 ? "" : "s"));
            for (Route section : sections) {
                List<BlockPos> bends = RoadPlan.bends(section.id());
                int marks = bends.size();
                if (section instanceof Route.Crossing span) {
                    System.out.println("   span  " + shortName(section.id()) + "  " + (span.alongX() ? "along x" : "along z")
                            + "  bank " + span.bankX() + ", " + span.bankZ() + " -> far " + span.farX() + ", " + span.farZ()
                            + "  width " + (Math.abs(span.farX() - span.bankX()) + Math.abs(span.farZ() - span.bankZ()) + 1)
                            + "  deck " + span.deckY());
                } else if (section instanceof Route.Riverless road) {
                    System.out.println("   road  " + shortName(section.id()) + "  " + road.pathX() + ", " + road.pathZ()
                            + " -> " + road.farX() + ", " + road.farZ() + "  marks " + marks);
                }
                if (bendBlocks) {
                    StringBuilder line = new StringBuilder("      marks");
                    for (BlockPos bend : bends) line.append(' ').append(bend.getX()).append(',').append(bend.getZ());
                    System.out.println(line);
                }
            }
        }
    }

    private static String shortName(String id) {
        int slash = id.lastIndexOf('/');
        return slash < 0 ? id : id.substring(slash + 1);
    }

    private static void print(int x, int z) {
        ArdaMap.Sample sample = ArdaMap.sample(x, z);
        boolean wet = sample.river() && sample.water() > sample.height();
        System.out.println("   " + x + ", " + z + "  " + sample.country()
                + "  height " + sample.height() + "  water " + sample.water()
                + (sample.river() ? "  river" : "") + (wet ? " WET" : "") + (sample.shore() ? " shore" : ""));
    }
}
