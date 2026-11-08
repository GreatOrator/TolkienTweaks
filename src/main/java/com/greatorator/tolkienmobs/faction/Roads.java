package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Map;

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

    /** Every build that has a road walked for it. A build missing here gets none. */
    public static final List<Route> ROUTES = List.of(
            // Rivendell: the path's last block at 5200, 134, -3034, the banks at 5055 and 5006 on z -3075, the deck
            // at 76 - the numbers the scouted valley was walked to, 584 pieces of city above the path's end.
            new Route.Crossing("rivendell", 5200, 5753, -3370, -2859, 5200, -3034, 5055, 5006, -3075, 4,
                    74, 75, 76, 16, 32, "bridge_end", "bridge_middle"),
            // Thorenhad's camp to the ford's own road end, and no further: the block beside the camp's door at
            // 4648, 90, -3314 to the marker at 4972, 86, -3075, where the crossing's exit road stops. The road over
            // the water and this dry one are the two sections of the same road, so this one ends where that one ends
            // rather than at the far bank. A section that ran past the meeting point would grade the same country a
            // second time, from the other side, and the two passes would fight over the blocks between them.
            //
            new Route.Riverless(Sites.ELLADAN_CAMP, 4640, 4655, -3313, -3298, 4648, -3314, 4972, -3075),
            // Barachen's fire to the mark at 4892, 74, -3097, where the Thorenhad road runs past: the road leaves the
            // camp's own door at 4820, 76, -2886 and ends at the mark, so the two roads meet there instead of each
            // grading the country the other has already laid. The camp is sixteen by sixteen from its corner pin at
            // 4812, 77, -2885, and the track is a wild path three blocks wide the whole way - see HALVES.
            new Route.Riverless(Sites.BARACHENS_CAMP, 4812, 4827, -2885, -2870, 4820, -2886, 4892, -3097)
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
            Sites.ELLADAN_CAMP, List.of(new BlockPos(4875, 74, -3179),
                    new BlockPos(4885, 74, -3133), new BlockPos(4903, 74, -3102)));

    /** The blocks a road is bent through, in the order it walks them, or none when it is walked straight. */
    public static List<BlockPos> bends(Route route) {
        return BENDS.getOrDefault(route.id(), List.of());
    }

    /**
     * The kind of road a route is laid as where it is one kind from end to end, by route, as a half-width: one for a
     * wild track three blocks wide, two for the transition's five, three for a settlement's own seven.
     * <p>
     * A route's own bands are the ordinary rule, and they are the right one for a road that carries a town's traffic:
     * seven at the gate, five where the paving starts, three out in the country. A track that only has to be walked -
     * off a camp's door and out to a mark - is a wild track the whole way, and saying so here keeps it three wide at
     * the door, where the bands would have widened it to a road. What a column is made of is still read from the bands,
     * so the track still arrives as the camp's own stone and leaves as the country's gravel; only the width is the
     * route's. A route with no entry here is laid exactly as every road was before there was one.
     */
    private static final Map<String, Integer> HALVES = Map.of(
            // Barachen's fire's track out to the mark where the Thorenhad road runs past it: wild, three wide.
            Sites.BARACHENS_CAMP, 1);

    /** The half-width a road is laid at, or -1 where the country's own rule decides it. */
    public static int half(Route route) {
        return HALVES.getOrDefault(route.id(), -1);
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
