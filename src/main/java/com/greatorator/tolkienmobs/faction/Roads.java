package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
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

    /**
     * The road north out of the crossing towards the high pass: the section the scout walked from the ford's east
     * bank, up the shoulder above the Bruinen, and on north over the vale's own floor.
     * <p>
     * It is a road rather than a build's way out, so it is named and carries no footprint. North of the ford the
     * Bruinen turns east across it, so the road is two sections rather than one: the crossing at
     * {@link #HIGH_PASS_BRIDGE}, which is where the water is, and this, the dry road beyond the far abutment - from
     * the block the crossing's exit stops on at 5065, -3129, down the vale and on north to the staging plaza, where
     * it ends on the mark a scout laid at the disc's own edge, 5124, -3401. Those two are the whole route to the
     * plaza; every pin between them is a bend. The climb out of the plaza is {@link #HIGH_PASS_CLIMB}.
     */
    public static final String HIGH_PASS_WAY = "tolkienmobs:roads/high_pass_way";

    /**
     * The climb out of the staging plaza to the top of the pass: the scout's second walk, from the mark on the plaza's
     * north rim at 5119, -3422, north-west round the shoulder the pass turns behind and then up the face in three
     * switchbacks to the last marker at 5187, -3658.
     * <p>
     * It is a route of its own rather than the tail of {@link #HIGH_PASS_WAY} because of the ground between the two: a
     * plaza is drawn level across its whole disc and eased into the country at the rim, and it answers for its own
     * columns before any road does. A road run through the disc would be graded by the pass and then taken back by the
     * terrain the next time that chunk was raised, which is what left the old end-point unable to carry on. So the
     * first road ends where the plaza begins and this one starts on its far rim, and the twenty-one blocks between them
     * are the staging ground itself - a floor to walk across, with no road laid over it.
     */
    public static final String HIGH_PASS_CLIMB = "tolkienmobs:roads/high_pass_climb";

    /**
     * The bridge the high pass road crosses the Bruinen on, a hundred blocks north of the ford.
     * <p>
     * The four ammolite blocks the scout laid on the river's line were the crossing's own survey: the shore at -3070,
     * the water at -3077 and -3090, and the far side at -3105. Read against the ground, the river there runs east to
     * west across the road, so the span lies along z between the bank at -3073 - the first block the water covers -
     * and the bank at -3098, past the shallows. Twenty-six columns between the two is the near end, one middle and
     * the far end exactly, which is why those two blocks rather than the marked ones: a span of nine, eight and nine
     * is the only length two ends and a middle fit, and a bank chosen two blocks short leaves a gap of open water in
     * front of the far abutment. The water stands at 76 and 77 under the span, so the deck is 79.
     */
    public static final String HIGH_PASS_BRIDGE = "tolkienmobs:roads/high_pass_bridge";

    /**
     * The road east of the climb: from the top of the pass at 5187, -3658 out over the shoulder and through the
     * mountains to the near end of the stream crossing at 6418, -3662.
     * <p>
     * It is a road of its own rather than the tail of {@link #HIGH_PASS_CLIMB} because the climb ends on the pass's
     * own last mark and this one carries on east where the vale gives way to the range. The scout walked it as one
     * line with two marks on it, 5194, -3672 at the shoulder and the crossing's own banks at 6434 and 6477, so the
     * section is two ends and one bend.
     */
    public static final String GLOIN_ROAD = "tolkienmobs:roads/gloin_road";

    /**
     * The stream the road crosses up in the mountains, forty-three columns between the scout's bank markers.
     * <p>
     * The four ammolite blocks at 6434, -3662 and 6477, -3662 are that survey: the water runs 6438 to 6470 on that
     * line and stands at 185 under its middle and 186 at its edges, while the banks read 187 and 189, so the deck is
     * 188 - two over the highest water, as the Bruinen's span is - with the abutments' base at 187 and the middles' at
     * 186. Nine wide, the width the pieces come in, though the road either side of it is five.
     */
    public static final String GLOIN_BRIDGE = "tolkienmobs:roads/gloin_bridge";

    /**
     * The road on from the stream to the end of the scout's walk: 6493, -3662 east along the range's north side and
     * down to 8234, -3590, through the junction at 8211, -3594 where Gloin's camp track comes up onto it.
     */
    public static final String GLOIN_ROAD_EAST = "tolkienmobs:roads/gloin_road_east";

    /**
     * The old forest road: out of Glóin's camp and east, over the Old Ford and the Forest River and on into Mirkwood.
     * <p>
     * The scout walked it in one go and set a mark every forty blocks or so - a hundred and one of them, denser than
     * any road here so far - so the line is the walk itself rather than a search that has to find its own way between
     * two ends: every mark is a bend and the road goes where the marks go. Two of them are pairs rather than singles,
     * and both are bank marks: 8481, -3668 and 8520, -3668 either side of the Old Ford, whose water stands at 186 with
     * thirty-three columns between them, and 11736, -3841 and 11775, -3841 either side of the Forest River, whose
     * water stands at 167 across the same thirty-three. Neither is a wade, so both are spans of their own -
     * {@link #OLD_FORD_BRIDGE} and {@link #FOREST_RIVER_BRIDGE} - and the road leaves the water to them: it stops at
     * the bank and picks up on the far one, and the deck, its piers and its abutments are the crossing's own pieces.
     * What the road keeps is the arrival: every station of it standing in the water is pinned to the crossing's
     * walking surface, so the bank ramps up onto the deck half a block a station instead of diving under it.
     */
    public static final String OLD_FOREST_ROAD = "tolkienmobs:roads/old_forest_road";
    /**
     * The Old Ford's span: the same two pieces as the mountain stream's bridge, stood between the scout's own banks.
     * <p>
     * The survey is the pair of ammolite marks at 8481, -3668 and 8520, -3668, with thirty-three columns of water at
     * 186 between them and the bed eleven under that. Eleven blocks is a swim rather than a wade, so the crossing is a
     * deck: 189, a block over the banks' own height and three over the water, with the abutments based at 188 and the
     * middles at 187. Laid at the bank's own height the deck read as sunk into it, and a span whose walking surface is
     * the ground either side of it is a low culvert rather than a bridge. The near bank is the east one, which
     * is how the pieces are stood - they run from the bank down to the far side - so the span is walked from its east
     * approach at 8536 to the west abutment at 8481, and its approach runs in the same sixteen blocks the stream's
     * crossing has.
     */
    public static final String OLD_FORD_BRIDGE = "tolkienmobs:roads/old_ford_bridge";

    /**
     * The Forest River's span: 11736, -3841 to 11775, -3841, thirty-three columns of water at 167 and the bed eleven
     * under it again.
     * <p>
     * Decked at 169 - the bank's own height and one more, two over the water - with the middles based at 167 and the
     * abutments at 168, so the road steps up onto it from either bank rather than running into it level. A river eleven
     * deep is a swim, not a wade: a ford here would be a road painted on the bottom of a lake.
     */
    public static final String FOREST_RIVER_BRIDGE = "tolkienmobs:roads/forest_river_bridge";

    /**
     * The road out of Gundabad: the hold's way down out of its own gate, and the north's road to everything else.
     * <p>
     * The hold stands at 5516, 187, -7112 in Arda at the head of its own valley, and what the road leaves into is
     * country nobody has walked: the nearest road of any age is the chapter's own at the far end of the old forest
     * road, three and a half thousand blocks south. So this is a road of two ends and the marks between them, like the
     * road over the high pass, and both ends are the scout's to pin - {@link #GUNDABAD_GATE} and
     * {@link #GUNDABAD_JOIN}, null until that walk is made. The name is written here rather than in the list so that
     * the walk has something to be written against, and so that a save which already carries the name finds the road
     * again rather than a second one beside it.
     */
    public static final String GUNDABAD_ROAD = "tolkienmobs:roads/gundabad_road";

    /**
     * The two cave roads of the troll caves' cluster, and the road that carries them on to the country road.
     * <p>
     * Three caves stand close enough together in the north-west that their roads were walked as one place: the scout
     * pinned the cluster itself at 4528, -2475. Each road is walked from its own cave to that mark rather than from the
     * mark outward, so the road a hand forgets standing at a cave's mouth is that cave's road: three routes begun on one
     * block would be three roads a hand could not tell apart. They are named as roads rather than as the caves' own
     * ways out because a road with no build behind it is exactly what a named route is for - the cluster is a place on
     * the road, not a build that owns one.
     */
    public static final String CAVE_WAY_NORTH = "tolkienmobs:roads/cave_way_north";
    public static final String CAVE_WAY_EAST = "tolkienmobs:roads/cave_way_east";
    public static final String TROLL_CAVE_WAY = "tolkienmobs:roads/troll_cave_way";

    /**
     * The wood ruin's way back to the road: out of the foot of its stair, south-east under its south wall, to the
     * junction the mountain way ends at.
     * <p>
     * The ruin has two: its own way out west under the wood, and this one, which finishes the connection back to the
     * road that the mountain way and the Thorenhad road meet on. It is a road of its own rather than part of either
     * because a route is one line, and this line leaves the ruin south-east where the other leaves it west.
     */
    public static final String WOOD_RUIN_LINK = "tolkienmobs:roads/wood_ruin_link";

    /**
     * The Gundabad road's two ends, as a scout will pin them: the block at the foot of the hold's own way out, and the
     * mark where the road meets the road it joins.
     * <p>
     * Both are null until that walk is made, and while either is null the route is not in {@link #ROUTES} - it cannot
     * be, since a route is two ends and two ends invented are a road laid across country nobody surveyed. Putting the
     * two marks in these two lines is the whole of what the road is waiting for; everything else about it is already
     * written - its name, its bends slot and how wide it is laid.
     * <p>
     * They are declared above {@link #ROUTES} on purpose: the list is built once when this class loads, and a mark
     * written below it would still be null at that moment, which is a road that never appears and never says why.
     */
    private static final BlockPos GUNDABAD_GATE = null;
    private static final BlockPos GUNDABAD_JOIN = null;

    /**
     * The hold's road, or null while its two marks are unwalked.
     * <p>
     * Its footprint is empty for now, as every road with no build behind it is: the walk begins on the mark it is
     * given. Once the hold is standing, its cut's own box goes in place of the four noughts, so that the road meets the
     * gate and grades no more of the hold's ground - the way Gloin's camp's track stops at the camp's wall.
     */
    private static Route gundabad() {
        if (GUNDABAD_GATE == null || GUNDABAD_JOIN == null) return null;
        return new Route.Riverless(GUNDABAD_ROAD, 0, 0, 0, 0,
                GUNDABAD_GATE.getX(), GUNDABAD_GATE.getZ(), GUNDABAD_JOIN.getX(), GUNDABAD_JOIN.getZ());
    }

    /** The roads as they have been walked, with the hold's road on the end once its two marks are pinned. */
    private static List<Route> withGundabad(List<Route> walked) {
        Route hold = gundabad();
        if (hold == null) return walked;
        List<Route> all = new ArrayList<>(walked);
        all.add(hold);
        return List.copyOf(all);
    }

    /** Every build that has a road walked for it. A build missing here gets none. */
    public static final List<Route> ROUTES = withGundabad(List.of(
            // Rivendell: the path's last block at 5200, 134, -3034, down to the crossing the scout pinned on z -3060
            // between the banks at 5055 and 5003. The river runs 5004 to 5051 on that line and stands at 78 under its
            // west half and 76 under its east, so the deck is 80 - two over the highest water on the span - with the
            // abutments' base at 79 and the middles' at 78 under it. The banks read 78 and 80 themselves, so the deck
            // arrives on them rather than over or under them.
            new Route.Crossing("rivendell", 5200, 5753, -3370, -2859, 5200, -3034, 5055, -3060, 5003, -3060, 4,
                    78, 79, 80, 16, 32, "bridge_end", "bridge_middle"),
            // Elladan's camp's own way out: the block beside its door at 4648, 156, -3314, over the camp's own
            // paving to the junction pinned at 4638, -3355, where the main road passes. The camp's ground is the
            // camp's, so the road meets its wall and goes no further in.
            new Route.Riverless(ELLADAN_BRANCH, 4640, 4655, -3313, -3298, 4648, -3314, 4638, -3355),
            // The Thorenhad road is the main road's own line, not the camp's: from its west reach at 4560, -3360
            // through the pins at 4588, -3355 and on along the shelf to 4748, -3355, then north and east to the
            // junction at 4818, -3118, where the mountain way ends and the wood ruin's way back meets it. Elladan's
            // branch is the only road here that is the camp's, and it comes down onto this one at the junction pinned
            // at 4638, -3355. The road is walked through that junction rather than from it so that the strip west of
            // it, and now the reach beyond it, is part of the same road and not a stub of its own.
            new Route.Riverless(THORENHAD_ROAD, 4640, 4655, -3313, -3298, 4560, -3360, 4818, -3118),
            // Barachen's camp's own way out: its door at 4819, -2886, west and down to the junction pinned at 4804,
            // -2896, which is the road down the mountain's flank.
            new Route.Riverless(Sites.BARACHENS_CAMP, 4812, 4827, -2885, -2870, 4819, -2886, 4804, -2896),
            // The wood ruin's own way out: from the foot of its own stair at 4787, -3064, south to the junction the
            // scout pinned at 4784, -3069, and away west under the ruin to 4689, -3051. Its footprint is the ruin's own
            // ground, stair and all: measured short of the stair, the road's band reached into it and graded the ruin's
            // own steps into road, so the road climbed the tower instead of leaving it.
            new Route.Riverless(Sites.WOOD_RUIN, 4783, 4791, -3064, -3046, 4787, -3064, 4689, -3051),
            // The ruin's other way: out of the foot of the same stair, south-east under the ruin's south wall to the
            // junction at 4818, -3118, where the mountain way ends and the Thorenhad road arrives - the ruin's road
            // back to everything else.
            new Route.Riverless(WOOD_RUIN_LINK, 4783, 4791, -3064, -3046, 4787, -3064, 4818, -3118),
            // The road down the mountain's west flank: off the north road where the scout pinned the join at 4820,
            // -2708, its first bend at 4810, -2720, and then down — the two doubled pins are the junction at either
            // end of that short piece, which is the last length the network was waiting on.
            // down past Barachen's camp and the wood ruin to the pins below at 4818, -3115. It passes two junctions
            // - the camp's at 4804, -2896 and the ruin's at 4824, -3055 - so those are bends rather than ends.
            new Route.Riverless(MOUNTAIN_WAY, 0, 0, 0, 0, 4820, -2708, 4818, -3118),
            // The road from the crag in the north-west to the ford's own road end: out of the crag at 4460, -2477,
            // south to the country road at 4411, -2635, east along it to 4875, -2691, and then down the massif's
            // west fall from 155 to 80 - the switchback re-pinned across two lines 28 apart, its arms running the width of
            // that band and dropping only two to five blocks each, from the top at 4867, -2688 to 4907, -2706, then back
            // north along the shoulder, the hairpin at 4937, -2627 where the switchback turns, and the long fall south - to the bridge's west bank at 5003, -3060, so the dry road and the
            // crossing meet end to end on the same ground. The line is the scout's own: the descent is bent through
            // the pins they re-laid a few blocks off the straight shot, one for every mark on the way down.
            new Route.Riverless(FORD_WAY, 0, 0, 0, 0, 4460, -2477, 5003, -3060),
            // The caves' cluster: three marks where the cave roads meet, so each road is walked out of its own cave to
            // that junction - the north road from its mouth at 4508, -2429, the east road from 4568, -2511, and the
            // road to the first troll cave from the crag at 4460, -2477 through the junction pinned at 4478, -2489. The
            // crag is the same block the ford road leaves by, so the cluster's traffic and the ford's road meet there.
            new Route.Riverless(CAVE_WAY_NORTH, 0, 0, 0, 0, 4508, -2429, 4528, -2475),
            new Route.Riverless(CAVE_WAY_EAST, 0, 0, 0, 0, 4568, -2511, 4528, -2475),
            new Route.Riverless(TROLL_CAVE_WAY, 0, 0, 0, 0, 4460, -2477, 4528, -2475),
            // The crossing the high pass road begins with, where the Bruinen turns east across the line: the same two
            // pieces as the ford's, stood a quarter turn, so the span runs along z from the bank at -3073 to the one
            // at -3098. No build stands behind the crossing, so it has no footprint: an empty box, and the approach
            // walks in from the ground it starts on. The water keeps the walk off the river by itself - a column the
            // map floods is never a candidate - so there is nothing for a box to guard here.
            new Route.Crossing(HIGH_PASS_BRIDGE, 0, 0, 0, 0, 5067, -3062, 5065, -3073, 5065, -3098,
                    4, 77, 78, 79, 8, 32, "bridge_end", "bridge_middle"),
            // The dry road north of the crossing, from the block the exit stops on to the scout's mark on the staging
            // plaza at 5124, -3401. Nothing stands behind either end, so the footprint is empty: a box round a road's
            // own start is ground it may not cross, which is the one block its walk has to begin on.
            new Route.Riverless(HIGH_PASS_WAY, 0, 0, 0, 0, 5065, -3129, 5124, -3401),
            // The climb out of the plaza, from the mark on its north rim to the last marker at 5187, -3658. Empty
            // box again: the plaza is drawn ground, and the walk reads it for the ground it is.
            new Route.Riverless(HIGH_PASS_CLIMB, 0, 0, 0, 0, 5119, -3422, 5187, -3658),
            // The road east of the climb, from the pass's last mark to the near end of the stream crossing. Empty box,
            // as every road with no build behind it is: the walk begins on the mark it is given.
            new Route.Riverless(GLOIN_ROAD, 0, 0, 0, 0, 5187, -3658, 6418, -3662),
            // The span over the mountain stream. The near bank is the east one, which is how the pieces are stood -
            // they run from the bank down to the far side - so the crossing is walked from its east approach at 6493
            // to the west abutment at 6434, and the road either side of it meets it on the two ends' own blocks.
            new Route.Crossing(GLOIN_BRIDGE, 0, 0, 0, 0, 6493, -3662, 6477, -3662, 6434, -3662,
                    4, 186, 187, 188, 8, 16, "bridge_end", "bridge_middle"),
            // The road on from the crossing to the scout's end point, through the junction the camp track leaves.
            new Route.Riverless(GLOIN_ROAD_EAST, 0, 0, 0, 0, 6493, -3662, 8234, -3590),
            // Gloin's camp's own track: out of the door at 8229, -3652 and south-east up to the junction at 8211,
            // -3594 where the road east of the pass passes. The box is the camp's own ground, 28 by 22 from the
            // corner the pin names, so the track meets its wall and goes no further in.
            new Route.Riverless(Sites.GLOIN_CAMP, 8215, 8242, -3674, -3653, 8229, -3652, 8211, -3594),
            // The old forest road east out of Glóin's camp: from the mark on the camp's own ground at 8231, -3641,
            // down the vale under the range, over the Old Ford, east through Mirkwood's western eaves and over the
            // Forest River to the scout's last mark at 11842, -3877. No build stands behind either end, so the box is
            // empty and the walk begins on the mark it is given; the hundred marks between are its bends, and the two
            // fords are the pass's own to carry.
            new Route.Riverless(OLD_FOREST_ROAD, 0, 0, 0, 0, 8231, -3641, 11842, -3877),
            // The Old Ford's span, walked from its east approach at 8536 onto the bank at 8520 and west across to
            // 8481: deck 189, a block over the banks' own height, over water at 186. Sixteen blocks of approach either
            // side, as the mountain stream's crossing has, so the bridge's own grading meets the road's on the bank.
            new Route.Crossing(OLD_FORD_BRIDGE, 0, 0, 0, 0, 8536, -3668, 8520, -3668, 8481, -3668,
                    4, 187, 188, 189, 8, 16, "bridge_end", "bridge_middle"),
            // The Forest River's span, from its east approach at 11791 onto the bank at 11775 and west across to
            // 11736: deck 169, a block over the bank and two over the water at 167.
            new Route.Crossing(FOREST_RIVER_BRIDGE, 0, 0, 0, 0, 11791, -3841, 11775, -3841, 11736, -3841,
                    4, 167, 168, 169, 8, 16, "bridge_end", "bridge_middle")
            // The Gundabad road is not written here. It joins the list by itself once GUNDABAD_GATE and GUNDABAD_JOIN
            // are pinned: see withGundabad above, and the bends slot below for the marks between them.
    ));

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
    private static final Map<String, List<BlockPos>> BENDS = Map.ofEntries(
            // The city's road down to the crossing, in the order it is walked: out of the path's end, north-west
            // round the shoulder, then south-west onto the span's line.
            Map.entry("rivendell", List.of(new BlockPos(5186, 132, -3030), new BlockPos(5178, 122, -3025),
                    new BlockPos(5174, 122, -3004), new BlockPos(5163, 122, -2993),
                    new BlockPos(5138, 121, -3004), new BlockPos(5116, 111, -3008),
                    new BlockPos(5115, 100, -3021), new BlockPos(5109, 92, -3031),
                    new BlockPos(5108, 78, -3054))),
            // The camp's branch turns once, where the camp's own paving ends.
            Map.entry(ELLADAN_BRANCH, List.of(new BlockPos(4644, 155, -3346))),
            // The main road's own line east of the junction, every pin the scout walked.
            Map.entry(THORENHAD_ROAD, List.of(new BlockPos(4588, 155, -3355), new BlockPos(4617, 155, -3356),
                    new BlockPos(4638, 155, -3355), new BlockPos(4651, 155, -3358),
                    new BlockPos(4664, 156, -3359), new BlockPos(4684, 156, -3355),
                    new BlockPos(4714, 156, -3355), new BlockPos(4731, 157, -3359),
                    new BlockPos(4748, 157, -3355), new BlockPos(4747, 157, -3339),
                    new BlockPos(4747, 157, -3325), new BlockPos(4748, 157, -3292),
                    new BlockPos(4761, 158, -3266), new BlockPos(4774, 158, -3243),
                    new BlockPos(4783, 159, -3215), new BlockPos(4791, 159, -3184),
                    new BlockPos(4799, 159, -3155), new BlockPos(4811, 159, -3143),
                    new BlockPos(4817, 159, -3131))),
            // The ruin's way back to the road, out of the same stair foot and south-east to the junction.
            Map.entry(WOOD_RUIN_LINK, List.of(new BlockPos(4787, 159, -3082), new BlockPos(4793, 159, -3092),
                    new BlockPos(4808, 159, -3096), new BlockPos(4818, 159, -3097))),
            // The camp's track out of its door and onto the junction.
            Map.entry(Sites.BARACHENS_CAMP, List.of(new BlockPos(4819, 158, -2893), new BlockPos(4814, 158, -2896))),
            // The ruin's track: out of the foot of its stair, south to the junction, and west under the wood.
            Map.entry(Sites.WOOD_RUIN, List.of(new BlockPos(4784, 159, -3069), new BlockPos(4772, 159, -3070),
                    new BlockPos(4760, 160, -3070), new BlockPos(4752, 160, -3082),
                    new BlockPos(4725, 160, -3060), new BlockPos(4712, 160, -3049))),
            // The flank road: every pin between the north road and the pins below, which is the switchback the
            // scout walked up the fall.
            Map.entry(MOUNTAIN_WAY, List.of(new BlockPos(4810, 157, -2720), new BlockPos(4818, 157, -2742), new BlockPos(4790, 158, -2768),
                    new BlockPos(4811, 157, -2781), new BlockPos(4796, 158, -2812),
                    new BlockPos(4811, 158, -2821), new BlockPos(4799, 158, -2836),
                    new BlockPos(4803, 158, -2854), new BlockPos(4806, 158, -2860),
                    new BlockPos(4804, 158, -2890), new BlockPos(4804, 159, -2896),
                    new BlockPos(4804, 158, -2958), new BlockPos(4804, 158, -2998),
                    new BlockPos(4811, 158, -3058), new BlockPos(4824, 158, -3055),
                    new BlockPos(4825, 158, -3069), new BlockPos(4825, 158, -3081),
                    new BlockPos(4825, 159, -3095))),
            // The crag road: south out of the crag, east along the country road, and then every pin of the descent
            // to the ford's own road end.
            Map.entry(FORD_WAY, List.of(new BlockPos(4447, 152, -2526), new BlockPos(4432, 152, -2548),
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
                    new BlockPos(4851, 155, -2695), new BlockPos(4867, 155, -2688),
                    new BlockPos(4875, 154, -2691), new BlockPos(4877, 155, -2678),
                    new BlockPos(4883, 153, -2706), new BlockPos(4889, 150, -2678),
                    new BlockPos(4888, 148, -2696), new BlockPos(4889, 145, -2706),
                    new BlockPos(4895, 140, -2678), new BlockPos(4895, 135, -2706),
                    new BlockPos(4901, 130, -2678), new BlockPos(4901, 125, -2706),
                    new BlockPos(4907, 125, -2706), new BlockPos(4910, 124, -2676), new BlockPos(4913, 121, -2669), new BlockPos(4914, 120, -2659),
                    new BlockPos(4921, 120, -2652), new BlockPos(4924, 115, -2646), new BlockPos(4930, 115, -2632), new BlockPos(4932, 114, -2628),
                    new BlockPos(4935, 110, -2632), new BlockPos(4937, 107, -2627), new BlockPos(4937, 106, -2636), new BlockPos(4932, 112, -2636),
                    new BlockPos(4925, 106, -2656), new BlockPos(4921, 104, -2664), new BlockPos(4921, 103, -2671), new BlockPos(4920, 102, -2679),
                    new BlockPos(4914, 100, -2686), new BlockPos(4912, 100, -2699), new BlockPos(4919, 99, -2703), new BlockPos(4924, 97, -2707),
                    new BlockPos(4925, 97, -2717), new BlockPos(4930, 98, -2727), new BlockPos(4935, 98, -2738), new BlockPos(4942, 97, -2747),
                    new BlockPos(4945, 96, -2750), new BlockPos(4948, 96, -2753), new BlockPos(4955, 95, -2759), new BlockPos(4956, 94, -2761),
                    new BlockPos(4958, 93, -2768), new BlockPos(4966, 92, -2778), new BlockPos(4973, 91, -2786), new BlockPos(4973, 90, -2803),
                    new BlockPos(4974, 88, -2822), new BlockPos(4973, 88, -2845), new BlockPos(4968, 89, -2851), new BlockPos(4965, 89, -2866),
                    new BlockPos(4962, 90, -2873), new BlockPos(4957, 90, -2877), new BlockPos(4957, 90, -2886), new BlockPos(4952, 91, -2908),
                    new BlockPos(4948, 90, -2915), new BlockPos(4948, 89, -2922), new BlockPos(4952, 89, -2943), new BlockPos(4954, 88, -2949),
                    new BlockPos(4954, 87, -2954), new BlockPos(4955, 85, -2963), new BlockPos(4962, 85, -2972), new BlockPos(4966, 84, -2981),
                    new BlockPos(4971, 84, -2986), new BlockPos(4979, 84, -3003), new BlockPos(4987, 83, -3016), new BlockPos(4989, 83, -3025),
                    new BlockPos(4992, 82, -3032), new BlockPos(4996, 81, -3034), new BlockPos(4998, 80, -3049))),
            // The caves' cluster, each road in the order it walks out of the junction it shares with the other two.
            Map.entry(CAVE_WAY_NORTH, List.of(new BlockPos(4536, 155, -2443), new BlockPos(4543, 155, -2464))),
            Map.entry(CAVE_WAY_EAST, List.of(new BlockPos(4555, 155, -2517), new BlockPos(4551, 155, -2496))),
            Map.entry(TROLL_CAVE_WAY, List.of(new BlockPos(4478, 153, -2489))),
            // The high pass road north of the crossing. The four blocks the scout laid at -3062 to -3105 are the
            // bridge's own survey and belong to the crossing, whose banks they mark; these are the pins beyond the far
            // abutment, every one the scout walked, because the shoulder above the river turns as it climbs and the
            // vale's north side falls away twenty blocks in less than ninety - legs that long read as a straight line
            // through the country rather than as the line that was walked. The road ends at the staging plaza, on the
            // mark the scout laid at its own edge.
            Map.entry(HIGH_PASS_WAY, List.of(new BlockPos(5071, 78, -3138), new BlockPos(5077, 79, -3153),
                    new BlockPos(5076, 80, -3184), new BlockPos(5077, 81, -3209),
                    new BlockPos(5080, 83, -3230), new BlockPos(5080, 84, -3253),
                    new BlockPos(5070, 85, -3272), new BlockPos(5066, 87, -3292),
                    new BlockPos(5081, 88, -3317), new BlockPos(5080, 86, -3328),
                    new BlockPos(5094, 79, -3347), new BlockPos(5097, 76, -3361),
                    new BlockPos(5113, 74, -3385))),
            // The climb out of the plaza, from the mark on its north rim: north-west round the shoulder the pass turns
            // behind, and then up the face in three switchbacks to 161. Every mark is a pin but three, and those three -
            // the pair at 5164 and 5167 with 5161 and 5170 either side of them, and 5101 at 92 with 5101 at 90 and 5099
            // at 93 either side - stand on the line between their neighbours on a dead-straight run, where a leg already
            // goes. The corners are pins because the search cuts corners: a switchback walked as one leg from 5143,
            // -3599 to 5169, -3606 is a road across the face rather than up it.
            Map.entry(HIGH_PASS_CLIMB, List.of(new BlockPos(5104, 83, -3436),
                    new BlockPos(5103, 86, -3441), new BlockPos(5101, 90, -3447),
                    new BlockPos(5099, 93, -3453), new BlockPos(5096, 95, -3458),
                    new BlockPos(5095, 95, -3466), new BlockPos(5095, 97, -3511),
                    new BlockPos(5105, 98, -3527), new BlockPos(5110, 99, -3546),
                    new BlockPos(5125, 100, -3558), new BlockPos(5140, 103, -3567),
                    new BlockPos(5147, 105, -3567), new BlockPos(5148, 107, -3563),
                    new BlockPos(5152, 109, -3556), new BlockPos(5161, 113, -3556),
                    new BlockPos(5170, 118, -3556), new BlockPos(5171, 118, -3569),
                    new BlockPos(5172, 120, -3576), new BlockPos(5179, 124, -3576),
                    new BlockPos(5179, 127, -3583), new BlockPos(5162, 127, -3592),
                    new BlockPos(5143, 131, -3599), new BlockPos(5143, 137, -3606),
                    new BlockPos(5169, 141, -3606), new BlockPos(5169, 146, -3612),
                    new BlockPos(5169, 153, -3623), new BlockPos(5169, 155, -3628),
                    new BlockPos(5179, 157, -3628), new BlockPos(5184, 160, -3636),
                    new BlockPos(5185, 161, -3649))),
            // The road east of the climb: the shoulder mark at 5194, -3672 is its one bend, and the crossing's banks
            // are the route's own ends rather than marks to be walked through.
            Map.entry(GLOIN_ROAD, List.of(new BlockPos(5194, 161, -3672))),
            // The road east of the crossing, every mark the scout walked after it: the two on the range's north side
            // and the two either side of the junction, with the end point at 8234, -3590 behind them.
            Map.entry(GLOIN_ROAD_EAST, List.of(new BlockPos(8124, 233, -3593), new BlockPos(8177, 227, -3593),
                    new BlockPos(8201, 225, -3594), new BlockPos(8211, 224, -3594))),
            // The camp's track, out of the door and up onto the junction: the two marks between are its bends.
            Map.entry(Sites.GLOIN_CAMP, List.of(new BlockPos(8224, 222, -3633), new BlockPos(8211, 224, -3609))),
            // The old forest road, every mark the scout walked: east out of the camp along the vale, down onto the Old
            // Ford's near bank, over the water and on east through the wood to the Forest River and the last mark. The
            // two bank pairs sit where the road meets water, and the marks either side of them are the approaches the
            // grade is walked in on, so the road comes down to a ford and climbs away from it instead of stepping.
            Map.entry(OLD_FOREST_ROAD, List.of(new BlockPos(8252, 218, -3642), new BlockPos(8266, 216, -3653), new BlockPos(8265, 216, -3666), new BlockPos(8271, 215, -3676),
                                               new BlockPos(8279, 214, -3680), new BlockPos(8316, 207, -3675), new BlockPos(8336, 204, -3670), new BlockPos(8365, 202, -3664),
                                               new BlockPos(8385, 202, -3675), new BlockPos(8404, 197, -3689), new BlockPos(8435, 197, -3693), new BlockPos(8446, 197, -3668),
                                               new BlockPos(8481, 188, -3668), new BlockPos(8520, 188, -3668), new BlockPos(8539, 188, -3668), new BlockPos(8588, 190, -3677),
                                               new BlockPos(8626, 190, -3673), new BlockPos(8646, 190, -3670), new BlockPos(8680, 190, -3679), new BlockPos(8704, 190, -3683),
                                               new BlockPos(8741, 189, -3691), new BlockPos(8805, 189, -3699), new BlockPos(8821, 189, -3701), new BlockPos(8897, 189, -3707),
                                               new BlockPos(8934, 191, -3705), new BlockPos(8983, 193, -3706), new BlockPos(9009, 194, -3703), new BlockPos(9037, 196, -3700),
                                               new BlockPos(9079, 196, -3697), new BlockPos(9098, 196, -3702), new BlockPos(9118, 195, -3707), new BlockPos(9142, 195, -3709),
                                               new BlockPos(9192, 193, -3717), new BlockPos(9236, 194, -3706), new BlockPos(9295, 194, -3700), new BlockPos(9322, 194, -3691),
                                               new BlockPos(9356, 193, -3695), new BlockPos(9408, 193, -3691), new BlockPos(9437, 193, -3688), new BlockPos(9505, 193, -3685),
                                               new BlockPos(9562, 193, -3685), new BlockPos(9602, 192, -3686), new BlockPos(9666, 191, -3685), new BlockPos(9717, 191, -3696),
                                               new BlockPos(9775, 191, -3694), new BlockPos(9797, 190, -3678), new BlockPos(9847, 190, -3679), new BlockPos(9884, 189, -3674),
                                               new BlockPos(9924, 189, -3677), new BlockPos(9986, 189, -3667), new BlockPos(10032, 189, -3663), new BlockPos(10079, 190, -3661),
                                               new BlockPos(10126, 190, -3656), new BlockPos(10147, 191, -3655), new BlockPos(10163, 191, -3651), new BlockPos(10202, 192, -3651),
                                               new BlockPos(10253, 193, -3650), new BlockPos(10309, 193, -3645), new BlockPos(10344, 191, -3631), new BlockPos(10380, 190, -3629),
                                               new BlockPos(10416, 190, -3629), new BlockPos(10435, 190, -3628), new BlockPos(10467, 190, -3629), new BlockPos(10494, 190, -3624),
                                               new BlockPos(10526, 190, -3619), new BlockPos(10576, 188, -3616), new BlockPos(10624, 186, -3611), new BlockPos(10661, 185, -3615),
                                               new BlockPos(10717, 184, -3616), new BlockPos(10730, 183, -3615), new BlockPos(10783, 184, -3614), new BlockPos(10830, 183, -3615),
                                               new BlockPos(10884, 183, -3612), new BlockPos(10957, 183, -3613), new BlockPos(11072, 183, -3619), new BlockPos(11082, 184, -3618),
                                               new BlockPos(11118, 184, -3629), new BlockPos(11153, 184, -3617), new BlockPos(11223, 183, -3618), new BlockPos(11241, 183, -3616),
                                               new BlockPos(11276, 182, -3611), new BlockPos(11306, 180, -3619), new BlockPos(11332, 179, -3612), new BlockPos(11371, 177, -3619),
                                               new BlockPos(11405, 175, -3619), new BlockPos(11415, 175, -3627), new BlockPos(11433, 174, -3646), new BlockPos(11460, 173, -3671),
                                               new BlockPos(11490, 173, -3687), new BlockPos(11515, 173, -3699), new BlockPos(11556, 173, -3715), new BlockPos(11589, 173, -3741),
                                               new BlockPos(11627, 172, -3791), new BlockPos(11664, 171, -3822), new BlockPos(11705, 169, -3842), new BlockPos(11736, 169, -3841),
                                               new BlockPos(11775, 168, -3841), new BlockPos(11828, 168, -3841), new BlockPos(11838, 168, -3855))),
            // The Gundabad road's marks, in the order it walks them: none yet, because the climb out of the vale has
            // not been walked. Every mark on that walk goes here as a BlockPos, one leg per mark, between the two ends
            // above - the gate at the top and the join at the bottom - and the walk is split at each one, so the line is
            // forced through the pin rather than merely nudged toward it.
            Map.entry(GUNDABAD_ROAD, List.of())
    );

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
    private static final Map<String, Kind> KINDS = Map.ofEntries(
            // Every road the scout has walked is laid the same way for now: five wide, of the country's own gravel
            // and dirt, so that the line can be judged by walking it before it is given its proper width.
            Map.entry("rivendell", Kind.WILD_WIDE),
            Map.entry(ELLADAN_BRANCH, Kind.WILD_WIDE),
            Map.entry(THORENHAD_ROAD, Kind.WILD_WIDE),
            Map.entry(Sites.BARACHENS_CAMP, Kind.WILD_WIDE),
            Map.entry(Sites.WOOD_RUIN, Kind.WILD_WIDE),
            Map.entry(MOUNTAIN_WAY, Kind.WILD_WIDE),
            Map.entry(FORD_WAY, Kind.WILD_WIDE),
            Map.entry(CAVE_WAY_NORTH, Kind.WILD_WIDE),
            Map.entry(CAVE_WAY_EAST, Kind.WILD_WIDE),
            Map.entry(TROLL_CAVE_WAY, Kind.WILD_WIDE),
            Map.entry(WOOD_RUIN_LINK, Kind.WILD_WIDE),
            // The high pass road is a section of the wild path, laid five wide like every other walked line, so it
            // can be judged on foot before it is given a width of its own - the bridge included, whose pieces are nine
            // wide whatever the road either side of it is.
            Map.entry(HIGH_PASS_WAY, Kind.WILD_WIDE),
            Map.entry(HIGH_PASS_CLIMB, Kind.WILD_WIDE),
            Map.entry(HIGH_PASS_BRIDGE, Kind.WILD_WIDE),
            // Chapter two's road east of the pass, its span and the camp's own track, five wide like every other
            // walked line so the country's line can be judged on foot before it is given a width of its own.
            Map.entry(GLOIN_ROAD, Kind.WILD_WIDE),
            Map.entry(GLOIN_BRIDGE, Kind.WILD_WIDE),
            Map.entry(GLOIN_ROAD_EAST, Kind.WILD_WIDE),
            Map.entry(Sites.GLOIN_CAMP, Kind.WILD_WIDE),
            // The old forest road is laid five wide of the country's own ground, like every other walked line.
            Map.entry(OLD_FOREST_ROAD, Kind.WILD_WIDE),
            // The Gundabad road is the scout's own line out of the hold, so it is laid the way every walked line here is
            // laid: five wide, of the country's own gravel and dirt, so the line can be judged by walking it before it is
            // given a width of its own. What it is meant to be is a hold's road, which is Kind.MAIN - seven wide of the
            // same country surface, a road rather than a track - and naming it here is what makes that one edit once the
            // walk holds up, with nothing else about it to change.
            Map.entry(GUNDABAD_ROAD, Kind.WILD_WIDE));

    /** The kind a road is laid the whole way, or null where the build and the bands decide it. */
    public static Kind kind(Route route) {
        return KINDS.get(route.id());
    }

    /**
     * The routes that are roads rather than any build's way out: a name, two ends, and no building to wait for.
     * <p>
     * A crossing is usually the city's own business, so it is laid by the dressing that raises the city, and only once
     * that build is standing - nothing begins it from the ground it crosses. A road that carries traffic between
     * places has no such owner: the high pass road begins on the ford's own bank and ends in open country, so its
     * bridge has to be stood the first time somebody walks to it. These are the routes that answer for themselves.
     */
    private static final Set<String> ROADS = Set.of(THORENHAD_ROAD, ELLADAN_BRANCH, MOUNTAIN_WAY, FORD_WAY,
            CAVE_WAY_NORTH, CAVE_WAY_EAST, TROLL_CAVE_WAY, WOOD_RUIN_LINK, HIGH_PASS_WAY, HIGH_PASS_CLIMB,
            HIGH_PASS_BRIDGE, GLOIN_ROAD, GLOIN_BRIDGE, GLOIN_ROAD_EAST,
            OLD_FOREST_ROAD, OLD_FORD_BRIDGE, FOREST_RIVER_BRIDGE, GUNDABAD_ROAD);

    /** Whether a route is a road rather than a build's own way out, and so begins wherever it is first met. */
    public static boolean road(Route route) {
        return ROADS.contains(route.id());
    }

    /**
     * How deep a road may be cut into the ground it crosses, by route; the caller's band where it says nothing.
     * <p>
     * A road is laid within a few blocks of the ground under it, which is what keeps a track a track: a hollow is a dip
     * it walks down into, a rise is a rise it climbs over. That rule is wrong on a face too steep to walk, where the
     * only road there is a shelf cut across the fall. The ground the line crosses there is tens of blocks over it at one
     * mark and under it at the next, and a band of four cannot hold a grade between them - so the pass lifts the line
     * onto every shelf the hill has and the road steps down the face instead of descending it. A route that names a
     * depth here is given a cutting instead: its profile is held to its own smooth descent, the hill above the line is
     * cut away and the ground below it filled, and the road runs at an angle across the face.
     * <p>
     * It is a route's own number rather than a kind's because it is a surveyor's decision, not a country's: the same
     * cast of road that walks a moor is cut into a fell. Nothing here is a trench through a hill the road was never
     * meant to cross - the cut is the road's own corridor, five or seven wide, with the hill above it and the fill
     * below it, and it is only ever as deep as the ground the line stands under.
     */
    private static final Map<String, Integer> CUTS = Map.of(
            // The ford road's descent off the massif's west fall, from the crag's own line at 4875, -2691 down to the
            // pass at 4898, -2697: twenty-seven blocks of fall in twenty-four of line, which no band of four can grade
            // without lifting the road onto the shelves between the two marks. Sixteen is the cutting that holds it.
            FORD_WAY, 16);

    /** How deep a road may be cut into the ground under it: its own allowance, or the caller's band where it has none. */
    public static int cut(Route route, int ordinary) {
        return CUTS.getOrDefault(route.id(), ordinary);
    }

    /** How a road gets over a stretch of water. */
    public enum Wet {
        /**
         * Through it: the road's own surface is the bed of the crossing, laid at the height the surveyor gave it with
         * the water left standing over it to wade. A ford is shallow by definition, so the height it names is one a
         * player walks at rather than one they swim at.
         */
        FORD,
        /**
         * Over it: the road's surface is a deck above the water, carried on a pier every fourth station of it and drawn
         * down to the river's bed, so the water still runs open underneath rather than through a wall of stone.
         */
        SPAN
    }

    /**
     * A stretch of water a road has to be told about, and how it crosses it.
     * <p>
     * The banks are two points on one axis, exactly as a bridge's are, because water does not always run the way the
     * road does: a pair that differs in x is a stretch lying along x with the road crossing it, and a pair that differs
     * in z is the same a quarter turn round. What the two banks share is the line the crossing runs on.
     * <p>
     * The numbers are a survey rather than a guess: {@code waterTop} is the height of the water's own surface and
     * {@code walkOn} is the height the road is laid at across it - the bed for a ford, the deck for a span. Nothing is
     * measured at lay time beyond the bed a pier is drawn to, because the water a river stands at is the thing most
     * likely to have been made differently by the time a road reaches it, and a crossing that re-read it would put a
     * step at each bank. A ford's own height also wants a bend written on the walk either side of it: the pass lowers a
     * station toward a pinned height a step at a time, so a ford ten blocks under its banks is a ramp the marks have to
     * ask for and the crossing only confirms.
     */
    public record Water(int nearX, int nearZ, int farX, int farZ, int waterTop, int walkOn, Wet kind) {
        public Water {
            if (nearX != farX && nearZ != farZ) throw new IllegalArgumentException("a crossing is on one axis");
            if (nearX == farX && nearZ == farZ) throw new IllegalArgumentException("a crossing needs two banks");
        }

        /** Whether a column stands on this crossing: between its banks, and no further out than {@code across}. */
        public boolean covers(int x, int z, int across) {
            boolean alongX = nearZ == farZ;
            int along = alongX ? x : z;
            int nearAlong = alongX ? nearX : nearZ;
            int farAlong = alongX ? farX : farZ;
            if (along < Math.min(nearAlong, farAlong) - 1 || along > Math.max(nearAlong, farAlong) + 1) return false;
            int lateral = alongX ? z - nearZ : x - nearX;
            return Math.abs(lateral) <= across;
        }
    }

    /**
     * The crossing a column stands on, or null where no surveyed span covers it.
     * <p>
     * Asked of the routes themselves rather than of a table of water kept beside them: a span is a
     * {@link Route.Crossing} already - its banks, its deck and its pieces are the survey - and a road holding its own
     * copy of those numbers is a second place for them to drift apart. Every span answers for every road, because a
     * span is a fact about the water rather than about which road happened to find it.
     * <p>
     * A column is on a crossing when it stands between the span's two banks on the span's own axis and no further out
     * than {@code across} from the line its pieces are stood on. That line is the scout's, and a searched road does not
     * run it exactly, so the caller names a reach of its own rather than the span's half-width.
     */
    public static Water water(int x, int z, int across) {
        for (Route route : ROUTES) {
            if (!(route instanceof Route.Crossing span)) continue;
            boolean alongX = span.alongX();
            int along = alongX ? x : z;
            int near = span.bank();
            int far = span.far();
            if (along < Math.min(near, far) - 1 || along > Math.max(near, far) + 1) continue;
            int lateral = alongX ? z - span.line() : x - span.line();
            if (Math.abs(lateral) > across) continue;
            return new Water(span.bankX(), span.bankZ(), span.farX(), span.farZ(), span.deckY() - 2, span.deckY(),
                    Wet.SPAN);
        }
        return null;
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

    /**
     * The route nearest a point, for a hand standing at the build - or the end - it wants a road for.
     * <p>
     * Measured to either end rather than to the start alone, because a road is laid from whichever end the hand stands
     * at: the staging plaza is where the vale's road finishes and the climb out of it begins, and a hand on the plaza's
     * edge has to be answered by the end it is standing on rather than by whichever start happens to be closer.
     */
    public static Route nearest(BlockPos at) {
        Route best = null;
        double nearest = Double.MAX_VALUE;
        for (Route route : ROUTES) {
            double away = Math.min(Math.hypot(at.getX() - route.pathX(), at.getZ() - route.pathZ()),
                    endAway(route, at));
            if (away < nearest) {
                nearest = away;
                best = route;
            }
        }
        return best;
    }

    /** How far a hand stands from a route's other end: a dry road's last marker, or a crossing's far bank. */
    private static double endAway(Route route, BlockPos at) {
        int farX = route.pathX();
        int farZ = route.pathZ();
        if (route instanceof Route.Riverless dry) {
            farX = dry.farX();
            farZ = dry.farZ();
        } else if (route instanceof Route.Crossing span) {
            farX = span.farX();
            farZ = span.farZ();
        }
        return Math.hypot(at.getX() - farX, at.getZ() - farZ);
    }
}
