package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Where each build's road leaves from, and how it crosses the water on the way down.
 * <p>
 * A road is not a generic thing the way a drape is. The drape can read any cut's own outline and know what to do; a
 * road has to be told where it starts, which banks a bridge spans, how wide the span is and where the deck sits -
 * numbers a surveyor walks and writes down, not numbers the build carries. They are a {@link Route} per build, kept
 * here so that a second city brings its own road with one line rather than a second copy of the road's code, and so
 * that the valley's numbers stop being constants the pass happens to know.
 * <p>
 * The city's own footprint is part of the route rather than of the cut: the road reads it three ways - the surface
 * bands are chosen by how close a column is to the walls, the descent may not cross it, and an eased corner may not
 * swing into it. All three are questions about where the road meets the build, which is the route's business.
 * <p>
 * One route per build that has a road, of whichever kind the country asks for: a {@link Route.Crossing} where the
 * road meets water and has a span to stand up, a {@link Route.Riverless} where it has only two ends. A build without
 * one is generated, draped and left alone: better a city with no road than a road laid to another city's coordinates.
 */
public final class Roads {
    private Roads() {
    }

    /**
     * The Thorenhad road, which is a road rather than a build's own way out: the main road from Elladan's camp down
     * to the ford's road end. It is named here because a road that carries more than one build's traffic is not any
     * one build's to walk - naming it is what gives it a record of its own in the save, and what a kind is written
     * against when the road is to be laid wider than the country it crosses.
     */
    public static final String THORENHAD_ROAD = "tolkienmobs:roads/thorenhad";

    /**
     * Elladan's camp's own way out to that road: a wild track from the block beside the camp's door. It has a name of
     * its own as well, because the camp's name in the save still holds the note of the main road that used to be
     * walked from the camp's door, and a road that resumed another road's note would lay the wrong country - four
     * hundred blocks of it, three wide, from a camp that wanted eleven.
     */
    public static final String ELLADAN_BRANCH = "tolkienmobs:roads/elledan_branch";

    /** The road down the mountain's west flank, past Barachen's camp and the wood ruin, to the pins below them. */
    public static final String MOUNTAIN_WAY = "tolkienmobs:roads/mountain_way";

    /** The road from the crag in the north-west, round the mountain, down the fall to the ford's own road end. */
    public static final String FORD_WAY = "tolkienmobs:roads/ford_way";

    /** Every build that has a road walked for it. A build missing here gets none. */
    public static final List<Route> ROUTES = List.of(
            // Rivendell: the path's last block at 5200, 134, -3034, down to the crossing the scout pinned on z -3060
            // between the banks at 5055 and 5003. The river runs 5004 to 5051 on that line and stands at 78 under its
            // west half and 76 under its east, so the deck is 80 - two over the highest water on the span - with the
            // abutments' base at 79 and the middles' at 78 under it. The banks read 78 and 80 themselves, so the deck
            // arrives on them rather than over or under them.
            new Route.Crossing("rivendell", 5200, 5753, -3370, -2859, 5200, -3034, 5055, 5003, -3060, 4,
                    78, 79, 80, 16, 32, "bridge_end", "bridge_middle"),
            // Elladan's camp's own way out: the block beside its door at 4648, 156, -3314, over the camp's own
            // paving to the junction pinned at 4638, -3355, where the main road passes. The camp's ground is the
            // camp's, so the road meets its wall and goes no further in.
            new Route.Riverless(ELLADAN_BRANCH, 4640, 4655, -3313, -3298, 4648, -3314, 4638, -3355),
            // The Thorenhad road, from the west of that junction along the shelf to 4745, -3355 - which is as far as
            // it is marked. It is walked through the junction rather than from it so that the strip the scout pinned
            // west of the junction at 4617, -3356 is part of the same road and not a stub of its own. The rest of
            // the road to the ford has still to be walked.
            new Route.Riverless(THORENHAD_ROAD, 4640, 4655, -3313, -3298, 4617, -3356, 4745, -3355),
            // Barachen's camp's own way out: its door at 4819, -2886, west and down to the junction pinned at 4804,
            // -2896, which is the road down the mountain's flank.
            new Route.Riverless(Sites.BARACHENS_CAMP, 4812, 4827, -2885, -2870, 4819, -2886, 4804, -2896),
            // The wood ruin's own way out: out of its south edge at 4787, -3058, round to the junction pinned at
            // 4824, -3055. Its footprint is measured less the row the road leaves on.
            new Route.Riverless(Sites.WOOD_RUIN, 4783, 4795, -3057, -3047, 4787, -3058, 4824, -3055),
            // The road down the mountain's west flank: from the bend it takes off the north road at 4789, -2721,
            // down past Barachen's camp and the wood ruin to the pins below at 4818, -3115. It passes two junctions
            // - the camp's at 4804, -2896 and the ruin's at 4824, -3055 - so those are bends rather than ends.
            new Route.Riverless(MOUNTAIN_WAY, 0, 0, 0, 0, 4789, -2721, 4818, -3115),
            // The road from the crag in the north-west to the ford's own road end: out of the crag at 4460, -2477,
            // south to the country road at 4411, -2635, east along it to 4887, -2691, and then down the massif's
            // west fall - 155 to 89 in two switchbacks - to the bridge's west bank at 5003, -3060, so the dry road
            // and the crossing meet end to end on the same ground.
            new Route.Riverless(FORD_WAY, 0, 0, 0, 0, 4460, -2477, 5003, -3060)
    );

    /**
     * The blocks a road is bent through, by route, in the order the road walks them.
     * <p>
     * These are ammolite markers walked on the ground and recorded here, the way a route's own ends are: a position
     * is written down in the build rather than pinned in the world, so every world gets the same bend and nothing
     * has to be scouted twice. The walk is split at each one - the door to the first bend, bend to bend, and the
     * last bend to the far end - so the line is forced through the point rather than merely nudged toward it.
     * <p>
     * A route with no entry here is walked in one leg, exactly as every road was before there was a way to bend one.
     * The list is in the order the road meets the marks, since the legs are walked in that order.
     */
    private static final Map<String, List<BlockPos>> BENDS = Map.of(
            // The city's road down to the crossing, in the order it is walked: out of the path's end, north-west
            // round the shoulder, then south-west onto the span's line.
            "rivendell", List.of(new BlockPos(5186, 132, -3030), new BlockPos(5178, 122, -3025),
                    new BlockPos(5174, 122, -3004), new BlockPos(5163, 122, -2993),
                    new BlockPos(5138, 121, -3004), new BlockPos(5116, 111, -3008),
                    new BlockPos(5115, 100, -3021), new BlockPos(5109, 92, -3031),
                    new BlockPos(5108, 78, -3054)),
            // The camp's branch turns once, where the camp's own paving ends.
            ELLADAN_BRANCH, List.of(new BlockPos(4644, 155, -3346)),
            // The main road's own line east of the junction, every pin the scout walked.
            THORENHAD_ROAD, List.of(new BlockPos(4638, 155, -3355), new BlockPos(4651, 155, -3358),
                    new BlockPos(4664, 156, -3359), new BlockPos(4684, 156, -3355),
                    new BlockPos(4714, 156, -3355), new BlockPos(4731, 157, -3359)),
            // The camp's track out of its door and onto the junction.
            Sites.BARACHENS_CAMP, List.of(new BlockPos(4819, 158, -2893), new BlockPos(4814, 158, -2896)),
            // The ruin's track: out of its south edge, round to the north, and east onto the junction.
            Sites.WOOD_RUIN, List.of(new BlockPos(4787, 159, -3072), new BlockPos(4802, 159, -3063),
                    new BlockPos(4811, 158, -3058)),
            // The flank road: every pin between the north road and the pins below, which is the switchback the
            // scout walked up the fall.
            MOUNTAIN_WAY, List.of(new BlockPos(4818, 157, -2742), new BlockPos(4790, 158, -2768),
                    new BlockPos(4811, 157, -2781), new BlockPos(4796, 158, -2812),
                    new BlockPos(4811, 158, -2821), new BlockPos(4799, 158, -2836),
                    new BlockPos(4803, 158, -2854), new BlockPos(4806, 158, -2860),
                    new BlockPos(4804, 158, -2890), new BlockPos(4804, 159, -2896),
                    new BlockPos(4804, 158, -2958), new BlockPos(4804, 158, -2998),
                    new BlockPos(4811, 158, -3058), new BlockPos(4824, 158, -3055),
                    new BlockPos(4825, 158, -3069), new BlockPos(4825, 158, -3081),
                    new BlockPos(4825, 159, -3095)),
            // The crag road: south out of the crag, east along the country road, and then every pin of the descent
            // to the ford's own road end.
            FORD_WAY, List.of(new BlockPos(4447, 152, -2526), new BlockPos(4432, 152, -2548),
                    new BlockPos(4427, 153, -2564), new BlockPos(4417, 154, -2586),
                    new BlockPos(4411, 154, -2635), new BlockPos(4409, 155, -2667),
                    new BlockPos(4427, 155, -2689), new BlockPos(4449, 156, -2705),
                    new BlockPos(4461, 156, -2722), new BlockPos(4493, 156, -2722),
                    new BlockPos(4535, 156, -2721), new BlockPos(4577, 157, -2717),
                    new BlockPos(4610, 157, -2717), new BlockPos(4642, 157, -2714),
                    new BlockPos(4665, 157, -2708), new BlockPos(4694, 157, -2709),
                    new BlockPos(4710, 157, -2703), new BlockPos(4735, 157, -2692),
                    new BlockPos(4743, 156, -2677), new BlockPos(4766, 156, -2672),
                    new BlockPos(4789, 156, -2671), new BlockPos(4791, 156, -2674),
                    new BlockPos(4793, 156, -2688), new BlockPos(4835, 156, -2717),
                    new BlockPos(4851, 155, -2695), new BlockPos(4887, 155, -2691),
                    new BlockPos(4910, 108, -2694), new BlockPos(4920, 104, -2713),
                    new BlockPos(4930, 101, -2731), new BlockPos(4955, 96, -2759),
                    new BlockPos(4968, 92, -2779), new BlockPos(4976, 89, -2807),
                    new BlockPos(4957, 89, -2898), new BlockPos(4952, 88, -2928),
                    new BlockPos(4979, 83, -2997), new BlockPos(4995, 81, -3034)));

    /** The blocks a road is bent through, in the order it walks them, or none when it is walked straight. */
    public static List<BlockPos> bends(Route route) {
        return BENDS.getOrDefault(route.id(), List.of());
    }

    /** The kinds of road there are, and the width each is laid at. */
    public enum Kind {
        /** A track through open country: three blocks wide, of whatever the country has underfoot. */
        WILD(3),
        /**
         * The same track, five blocks wide.
         * <p>
         * Wide enough to walk two abreast, and of the country's own gravel, dirt, grass and path the whole way. A road
         * that is walked out before its country is known is laid at this: five wide is enough to see where a marked
         * line actually goes, and the surface stays the country's rather than becoming a town's, because a track
         * across a moor is not a street.
         */
        WILD_WIDE(5),
        /** The approach to a settlement, where the paving starts: five blocks. */
        TRANSITION(5),
        /** A settlement's own street: seven blocks, and paved with the town's own stone. */
        URBAN(7),
        /**
         * A main road: seven blocks wide, of the country's own surface.
         * <p>
         * A street's width without a street's paving. A road that carries a valley's traffic is seven wide wherever
         * it runs, because that is what makes it a road rather than a track - but the stone it is made of is a
         * town's, not a valley's, so a main road across a moor is gravel, coarse dirt, grass and path, the same mix a
         * wild track is laid of. Naming it keeps that width through the open country, which is the whole of what a
         * named kind is for.
         * <p>
         * It keeps to the ground it crosses for the same reason: a main road is walked on the country rather than
         * carried over it on stone, so its grade answers to the ground under it and a hollow is a dip, not an
         * embankment.
         */
        MAIN(7);

        private final int blocks;

        Kind(int blocks) {
            this.blocks = blocks;
        }

        /** How many blocks across the road is. */
        public int blocks() {
            return blocks;
        }

        /** The blocks either side of the road's own line, which is what the pass is laid from. */
        public int half() {
            return (blocks - 1) / 2;
        }
    }

    /**
     * The kind a road is, by route, where it is one kind from end to end rather than the kind of the bands it crosses.
     * <p>
     * The bands are the ordinary rule, and they are the right one for a road that is there because a settlement is:
     * seven at the gate, five where the paving starts, three out in the country. But which road a road is does not
     * follow from where it runs - a main road is a main road across a moor as much as through a town, and a track out
     * to a camp is a track at the camp's own door - so a route may be given its kind, and is then laid at it the whole
     * way. A route with no entry here is laid by the bands, exactly as every road was before there was a way to name
     * one.
     * <p>
     * What a column is made of follows the kind too, because a town's street is paved and a main road is not: naming
     * a kind settles both, and {@link Kind#MAIN} is the one that names a width without the paving.
     */
    private static final Map<String, Kind> KINDS = Map.of(
            // Every road the scout has walked is laid the same way for now: five wide, of the country's own gravel
            // and dirt, so that the line can be judged by walking it before it is given its proper width.
            "rivendell", Kind.WILD_WIDE,
            ELLADAN_BRANCH, Kind.WILD_WIDE,
            THORENHAD_ROAD, Kind.WILD_WIDE,
            Sites.BARACHENS_CAMP, Kind.WILD_WIDE,
            Sites.WOOD_RUIN, Kind.WILD_WIDE,
            MOUNTAIN_WAY, Kind.WILD_WIDE,
            FORD_WAY, Kind.WILD_WIDE
            );

    /** The kind a road is laid the whole way, or null where the build and the bands decide it. */
    public static Kind kind(Route route) {
        return KINDS.get(route.id());
    }

    /**
     * The roads the bands decide, column by column, rather than one kind being laid the whole of them.
     * <p>
     * A settlement's own road is the first: seven blocks of its street inside the wall's band, five where the paving
     * starts, three out in the country, so it widens as it comes in to the gate. A road that is a road rather than a
     * build's way out does not take the bands at all - the valley's main road runs from a camp to a crossing and
     * belongs to neither - it names its kind in {@link #KINDS} instead, so it is the same road for its whole length.
     * <p>
     * Every other route is a wild build's own way out - a camp, a ruin, a cave mouth, which is nearly every build the
     * chapter finalises - and is laid wild from its own door: three blocks wide, of the country's own gravel and dirt.
     * The banded roads are named rather than the wild tracks, so a build added later is a wild one until somebody
     * says otherwise: a city is scouted and furnished as one, where a camp is a clearing with a fire in it.
     */
    private static final Set<String> BANDED = Set.of("rivendell");

    /** Whether the bands decide a road, column by column, rather than one kind being laid the whole of it. */
    public static boolean banded(Route route) {
        return BANDED.contains(route.id());
    }

    /**
     * The build a road leaves from, where the road has a name of its own.
     * <p>
     * A road of a build's own is named for the build, and that name is what the save's note of the road is written
     * under and what the build's own road flag is cleared by. The camp's branch and the main road are named for
     * themselves, so they say here which build they leave from: the camp finds its branch by its footprint, but
     * forgetting the road at the camp has to reach the camp's flag, which is filed under the build and not the road.
     */
    private static final Map<String, String> BUILDS = Map.of(ELLADAN_BRANCH, Sites.ELLADAN_CAMP);

    /** The build a road leaves from: its own name where it is that build's road, the build it serves otherwise. */
    public static String build(Route route) {
        return BUILDS.getOrDefault(route.id(), route.id());
    }

    /**
     * The route of the build standing on this ground, or null when nobody has walked one for it.
     * <p>
     * Matched on the middle of the footprint rather than on its corner, because a route records the build as it was
     * scouted and the footprint the dressing reads comes from the cut: a block of disagreement between them is a
     * piece that was re-cut, not a different city.
     */
    public static Route forGround(int lowX, int lowZ, int highX, int highZ) {
        int midX = (lowX + highX) / 2;
        int midZ = (lowZ + highZ) / 2;
        for (Route route : ROUTES) {
            if (midX >= route.cityX0() - 64 && midX <= route.cityX1() + 64
                    && midZ >= route.cityZ0() - 64 && midZ <= route.cityZ1() + 64) {
                return route;
            }
        }
        return null;
    }

    /** The route nearest a point, for a hand standing beside the build it wants a road for. */
    public static Route nearest(BlockPos at) {
        Route best = null;
        double nearest = Double.MAX_VALUE;
        for (Route route : ROUTES) {
            double away = Math.hypot(at.getX() - route.pathX(), at.getZ() - route.pathZ());
            if (away < nearest) {
                nearest = away;
                best = route;
            }
        }
        return best;
    }
}
