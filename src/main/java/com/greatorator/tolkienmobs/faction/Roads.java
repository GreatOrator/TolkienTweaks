package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

import java.util.List;

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
            // The wood ruin to Barachen's fire, the first road with no water on it: from the block beside the
            // ruin's door to the block beside the fire, both marked in the scouting world - 4782, 78, -3057 and
            // 4820, 76, -2886, a hundred and seventy-five blocks apart and never over water. The footprint box is
            // the ruin's own, so its surface is chosen by how close a column stands to the walls the road leaves
            // from, and the far end is ground rather than a deck.
            new Route.Riverless(Sites.WOOD_RUIN, 4782, 4795, -3058, -3046, 4782, -3057, 4820, -2886),
            // Thorenhad's camp to that ruin: the block beside the camp's door at 4642, 91, -3315 to the ruin's
            // road end, the pair that were marked together - a doorway and a road end - in the scouting world.
            // The camp's footprint is the sixteen by sixteen its door sits in the middle of, so the road starts
            // one block outside its south edge.
            new Route.Riverless(Sites.ELLADAN_CAMP, 4634, 4649, -3314, -3299, 4642, -3315, 4782, -3057)
            // A road with no water on it is the other kind, and is the whole of what a dry road needs: the build's
            // footprint and its entry point, and the block the road ends at. Nothing is swept, bridged or pillared;
            // the same search finds the way and the same grade lays it, arriving on the ground at the far end:
            // new Route.Riverless("<build>", cityX0, cityX1, cityZ0, cityZ1, pathX, pathZ, farX, farZ)
    );

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
