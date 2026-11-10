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

    /** Every build that has a road walked for it. A build missing here gets none. */
    public static final List<Route> ROUTES = List.of(
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
            // at -3098. The footprint is the small box the road's own start stands in, not the country either side of
            // the river: a route's box is ground its search may not cross, and a box round the water would forbid the
            // approach that has to reach the abutment.
            new Route.Crossing(HIGH_PASS_BRIDGE, 5060, 5074, -3070, -3050, 5067, -3062, 5065, -3073, 5065, -3098,
                    4, 77, 78, 79, 8, 32, "bridge_end", "bridge_middle"),
            // The dry road north of the crossing, from the block the exit stops on to the scout's mark on the staging
            // plaza at 5124, -3401. Nothing stands behind either end, so the footprint is again only the block the
            // road starts in.
            new Route.Riverless(HIGH_PASS_WAY, 5060, 5074, -3140, -3120, 5065, -3129, 5124, -3401),
            // The climb out of the plaza, from the mark on its north rim to the last marker at 5187, -3658. Its
            // footprint is the small box that mark stands in, which also keeps the search from cutting back across the
            // plaza's own floor to reach the shoulder above it.
            new Route.Riverless(HIGH_PASS_CLIMB, 5112, 5126, -3430, -3414, 5119, -3422, 5187, -3658)
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
                    new BlockPos(5185, 161, -3649)))
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
            Map.entry(HIGH_PASS_BRIDGE, Kind.WILD_WIDE));

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
            HIGH_PASS_BRIDGE);

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
