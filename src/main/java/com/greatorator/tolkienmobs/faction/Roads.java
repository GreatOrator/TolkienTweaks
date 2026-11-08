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

    /** Every build that has a road walked for it. A build missing here gets none. */
    public static final List<Route> ROUTES = List.of(
            // Rivendell: the path's last block at 5200, 134, -3034, the banks at 5055 and 5006 on z -3075, the deck
            // at 76 - the numbers the scouted valley was walked to, 584 pieces of city above the path's end.
            new Route.Crossing("rivendell", 5200, 5753, -3370, -2859, 5200, -3034, 5055, 5006, -3075, 4,
                    74, 75, 76, 16, 32, "bridge_end", "bridge_middle"),
            // Elladan's camp's own way out, and the only road the camp is given: the block beside its door at 4648, 90,
            // -3314, down the camp's own paving to the bend at 4648, 89, -3321, and then over to the main road's own
            // start mark at 4633, 91, -3331 - so the two sections meet end to end, the branch arriving along the line
            // the main road leaves on rather than running on past the junction into the trees. The camp is a wild
            // build, so its own track is wild - three blocks wide - however near the seven-wide road it ends.
            new Route.Riverless(ELLADAN_BRANCH, 4640, 4655, -3313, -3298, 4648, -3314, 4633, -3331),
            // The Thorenhad road, the valley's main road: its end at the mark 4633, 91, -3331 - the ford being the
            // other end, so the road is walked towards the ford - down to the ford's own road end at 4972, 86, -3075,
            // where the crossing's exit road stops, and no further: the road over the water and this dry one are the
            // two sections of the same road, and one that ran past the meeting point would grade the same country a
            // second time from the other side. It is a road rather than a build's way out, so it carries its own name
            // and its own kind: seven wide from the junction to the ford, of the country's own gravel and dirt, which
            // is what `KINDS` says of it. The bands would have made it a town's street at one end and a track at the
            // other, and a main road is neither.
            new Route.Riverless(THORENHAD_ROAD, 4640, 4655, -3313, -3298, 4633, -3331, 4972, -3075),
            // Barachen's fire to the mark at 4892, 74, -3097, where the Thorenhad road runs past: the road leaves the
            // camp's own door at 4820, 76, -2886 and ends at the mark, so the two roads meet there instead of each
            // grading the country the other has already laid. The camp is sixteen by sixteen from its corner pin at
            // 4812, 77, -2885. A wild build's track, so it is laid wild - three blocks wide - the whole way.
            new Route.Riverless(Sites.BARACHENS_CAMP, 4812, 4827, -2885, -2870, 4820, -2886, 4892, -3097),
            // The wood ruin's own way out, walked in Roads 7: out of the ruin's south edge at 4787, 78, -3058, down
            // through the mark the scout set below it at 4787, 78, -3082, and then east to the mark on Barachen's own
            // road at 4892, 74, -3044 - so the ruin joins the track the camp's traffic already uses instead of ending
            // in open country on the pin the crags' roads took for themselves. The ruin was measured at 4783..4795 by
            // -3058..-3047 of stone brick, spruce fence and trapdoor, so its footprint is that box less the row the
            // road leaves on: a road that searched through its own walls would find a way round them.
            new Route.Riverless(Sites.WOOD_RUIN, 4783, 4795, -3057, -3047, 4787, -3058, 4892, -3044),
            // The four crags, one road each, from the ground a road arriving at a crag meets to the pins the scout
            // walked in the country between them. A crag's `.end` pin sits twenty-seven or twenty-eight blocks out
            // from the middle of the rock, which is where a road has to end rather than climbing the crag, so each
            // footprint here is that middle and twenty-eight either side. Which crag takes which pin is the
            // walker's distance and not ours: Matuna's lair is the nearest and takes 4741, 81, -3082, Hoklond's cave
            // is the next and takes 4741, 81, -3086, and Bandrith's and Sholdroth's are the farthest two, which share
            // 4741, 81, -3092.
            new Route.Riverless(Sites.TROLL_CAVE, 4432, 4488, -2505, -2449, 4515, -2446, 4741, -3092),
            new Route.Riverless(Sites.TROLL_CAVE_2, 4540, 4596, -2539, -2483, 4624, -2479, 4741, -3086),
            new Route.Riverless(Sites.TROLL_CAVE_3, 4480, 4536, -2457, -2401, 4562, -2398, 4741, -3092),
            new Route.Riverless(Sites.TROLL_CAVE_4, 4605, 4661, -3109, -3053, 4689, -3050, 4741, -3082)
            // A road with no water on it is the other kind, and is the whole of what a dry road needs: the build's
            // footprint and its entry point, and the block the road ends at. Nothing is swept, bridged or pillared;
            // the same search finds the way and the same grade lays it, arriving on the ground at the far end:
            // new Route.Riverless("<build>", cityX0, cityX1, cityZ0, cityZ1, pathX, pathZ, farX, farZ)
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
            // The Thorenhad road's own line, walked in Roads 3: three marks between the camp and the ford that keep
            // the road off the hill and out of the crossing, in the order the road meets them.
            THORENHAD_ROAD, List.of(new BlockPos(4875, 74, -3179),
                    new BlockPos(4885, 74, -3133), new BlockPos(4903, 74, -3102)),
            // The camp's branch turns once, where the camp's own stone path ends: the road follows the path out of the
            // door and then swings onto the main road's start, so it meets the road it was walked for at a junction
            // rather than carrying straight on past it.
            ELLADAN_BRANCH, List.of(new BlockPos(4648, 89, -3321)),
            // Barachen's track, walked in Roads 5: off the camp's own ground through the mark at 4819, 76, -2903,
            // and then down the shoulder by 4896, 74, -2980 rather than across it in one leg.
            Sites.BARACHENS_CAMP, List.of(new BlockPos(4819, 76, -2903), new BlockPos(4896, 74, -2980)),
            // The wood ruin's road turns once, on the mark the scout set below the ruin in Roads 7: it leaves the
            // ruin southward, then runs east along the country to the camp's road rather than cutting the corner.
            Sites.WOOD_RUIN, List.of(new BlockPos(4787, 78, -3082)));

    /** The blocks a road is bent through, in the order it walks them, or none when it is walked straight. */
    public static List<BlockPos> bends(Route route) {
        return BENDS.getOrDefault(route.id(), List.of());
    }

    /** The kinds of road there are, and the width each is laid at. */
    public enum Kind {
        /** A track through open country: three blocks wide, of whatever the country has underfoot. */
        WILD(3),
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
            // The valley's main road: seven wide from the camp's junction to the ford's road end, and of the
            // country's own surface the whole way. Laid by the bands it was a road only within a mile of the camp and
            // a three-wide track for the rest of its length, which is the one thing a main road cannot be.
            THORENHAD_ROAD, Kind.MAIN
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
