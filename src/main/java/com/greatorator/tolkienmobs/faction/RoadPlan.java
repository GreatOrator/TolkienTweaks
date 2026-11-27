package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A road worked out from two ends, by reading the drawing rather than the ground.
 * <p>
 * A walked road is a list of marks a scout stood on, and a long one is a hundred of them - the old forest road is
 * three and a half thousand blocks and a hundred and one marks, and every one of them had to be found on foot. That
 * is the survey this class replaces. It needs the two blocks a road has to reach and nothing else: the way between
 * them is a search over {@link ArdaMap#sample}, which is a function of the column alone and answers for country no
 * chunk has made. Nothing here reads a block or generates a chunk, so a route four thousand blocks long costs a few
 * thousand noise reads - milliseconds - instead of five thousand chunks of ground.
 * <p>
 * The search is a lane walk rather than a free one. The line is drawn from one end to the other at {@link #LANE}
 * blocks a step, with {@link #REACH} blocks of country either side of it, and each step may shift a couple of lanes
 * across. A road between two places does not need to double back on itself - that is a search's job, and the pass
 * still does it, a leg at a time - so what this has to answer is which way round a hill and where across a river, and
 * a walk that always moves towards its far end answers both while staying cheap. A step pays for the rise it takes
 * over {@link #TARGET_GRADE}, for the turn it makes, and for every lane of drawn river it would cross, so it runs
 * along the contours and crosses the water where the water is narrowest and shallowest - which is the choice a road
 * surveyor makes and the one a walk to markers cannot make at all.
 * <p>
 * The marks it leaves are the bends the pass already knows how to use: one every {@link #MARK} blocks, so each leg of
 * the road is short enough for the pass's own search to refine it against the ground the world actually has. That is
 * the division of labour - this decides where the road goes, the pass decides how it sits - and it is why a leg is
 * searched in a narrow window instead of the bounding box of a whole road. A leg whose climb is more than the
 * grade can lay is broken into switchback marks before it is handed over: the drawing is the only place the climb
 * is known in advance, and a leg that asks for a block of rise in a block of travel is a leg the pass can only
 * answer by laying a staircase.
 * <p>
 * Water is part of the same read. A run of river columns on the chosen line is a crossing: the span's own axis is the
 * way the road is travelling there, the banks are the first dry columns either side of the river, the water is the
 * highest level the drawing gives the span, and the deck stands {@link #DECK_OVER_WATER} blocks over it - the rule
 * both walked spans were built to. The pieces already repeat to any length, so a span is only refused when the water
 * is too wide to bridge at all, and then the log says which stretch and how wide.
 * <p>
 * A road beside a river is not a road across it. A dry lane within {@link #BANK_MARGIN} lanes of drawn water is priced
 * as the bank it is, so the walk steps the line inland rather than skimming the edge where its own sixteen-block
 * readings cannot see the channel - which is what planned two spans over one channel with a stretch of road doubling
 * back between them. That is caught rather than priced: a line that leaves water and meets it again inside
 * {@link #DRY_AFTER_CROSSING} lanes has crossed one channel twice, and the leg is then walked a second time with a
 * lane change refused on every step that leaves water, so the crossing is straight on the lane the road reached the
 * bank on - which is also the only shape a span's banks and approaches can be read for. The first walk is left as it
 * was, so a leg whose own line was sound plans the same line it did before. And water at the two ends of the leg is
 * left out of the road: a road begins and ends on the bank, since a mark in the shallows has no dry ground behind it
 * to stand an abutment on.
 */
final class RoadPlan {
    /** How far apart the plan reads the drawing along the line between the two ends, in blocks. */
    private static final int LANE = 16;
    /** How far either side of that line the road may wander, in blocks. */
    private static final int REACH = 192;
    /**
     * The most lanes one step may shift sideways.
     * <p>
     * Two lanes is thirty-two blocks in a step of sixteen - a road that turns a corner rather than one that sidles. It
     * is also the limit that keeps the plan a road: free to shift any distance, a lane walk will take a detour the
     * grade never needed, and a road that sprawls across its corridor is a scribble rather than a line.
     */
    private static final int SHIFT = 2;
    /** The grade the walk aims for, in blocks of rise per block of travel; anything steeper is penalised. */
    private static final double TARGET_GRADE = 0.35D;
    /** What a rise over the target costs, squared: the same price the pass's own descent pays for a wall face. */
    private static final double GRADE_PENALTY = 12.0D;
    /** What a change of lane costs, so the road runs in legs rather than weaving. */
    private static final double TURN_COST = 1.5D;
    /**
     * What a lane of drawn river costs, per block of its depth.
     * <p>
     * A river is not a wall to this walk - a road has to cross one somewhere - so it is priced instead. The price is
     * per lane and grows with the depth the drawing gives, which makes a shallow narrow stretch cheaper than a deep
     * wide one, and makes a detour to reach one worth taking when the detour is short. Nothing else in the plan
     * decides where a road crosses: this toll is the whole of that decision.
     */
    private static final double RIVER_TOLL = 220.0D;
    /**
     * How many lanes of dry ground a road keeps to after it has left water before it will cross again.
     * <p>
     * A channel is crossed once. Left and met again two lanes later, water is not a second river but the same one the
     * road has just come out of - the walk dipped into its edge, climbed out on the same bank and waded in again - and
     * what that plans is two bridges side by side and a stretch of road that doubles back between them. Four lanes is
     * sixty-four blocks, which is clear of a bend in a channel and short of a second river worth crossing.
     */
    private static final int DRY_AFTER_CROSSING = 4;
    /**
     * What a lane of water met inside {@link #DRY_AFTER_CROSSING} costs, on top of the toll.
     * <p>
     * Priced rather than forbidden: a road with no way round still has to be laid, and a walk that cannot finish is a
     * road given up entirely. The number is large enough that nothing the walk could do about it - a couple of lanes
     * shifted, a bank followed, the deepest part of the channel met once - can compete with it, so the crossing happens
     * once wherever the country allows it at all.
     */
    private static final double CROSS_AGAIN = 12000.0D;
    /**
     * How far off drawn water a dry lane is priced to keep, in lanes.
     * <p>
     * The walk reads the drawing a lane at a time and the line between two lanes is a staircase of single blocks, so a
     * road that skims a bank clips the water's edge between two readings the walk never priced. The dense pass finds
     * those clips, and what they plan is two spans over one channel with a stretch of road doubling back between them -
     * the doubled crossing - or a whole leg given up for water the road never meant to cross. Three lanes is more than
     * a step can shift (see SHIFT), so the lane inside that reach is priced as the bank it is and the walk steps the
     * line inland instead.
     */
    private static final int BANK_MARGIN = 3;
    /** How far that reach is in blocks, which is how the neighbouring columns are read. */
    private static final int BANK_OFFSET = BANK_MARGIN * LANE;
    /** What a dry lane inside {@link #BANK_MARGIN} of drawn water costs, per step. */
    private static final double BANK_COST = 400.0D;
    /** How far apart the bends a planned road is laid through are, in blocks. */
    private static final int MARK = 320;
    /**
     * The steepest rise the grade can lay, in blocks per block of travel.
     * <p>
     * The pass settles a road's surface on half a block a station and a station is a block of travel -
     * `RoadCommands.MAX_STEP` - so one block in two is the most any road can climb: past it the next riser is a jump,
     * and a jump is not a road. The number is read here rather than moved, because a plan and a grade that disagree
     * about it is the fault this is for.
     */
    private static final double LAYABLE_GRADE = 0.5D;
    /**
     * How far off its own line one switchback traverse stands, in blocks.
     * <p>
     * A switchback lengthens a road by crossing the fall rather than by walking up it, so the offset is the whole of
     * what it buys: two marks either side of the line are two offsets apart, and it is that width, not the distance
     * between them along the line, that makes the leg longer than the country's shortest way. A road is seven wide at
     * the most and a hairpin's apron is eleven, so twenty-four is the least two traverses of one switchback may stand
     * apart; the width a leg is given is worked out from the climb it carries, and this is only the floor of it.
     */
    private static final int TRAVERSE = 24;
    /**
     * How many traverses one leg may be broken into.
     * <p>
     * A climb of six hundred blocks wants more than a dozen, and a dozen hairpins in a plan nobody has looked at is
     * not a road either - the pass searches each leg on the live ground and each is a window of its own. Eight is a
     * fell's worth of switchbacks and the most one leg is asked for; past that the leg is left as it is and the pass
     * answers as it always did.
     */
    private static final int TRAVERSE_MOST = 8;
    /**
     * How wide a switchback may be stood when its own marks do not clear the grade, in blocks.
     * <p>
     * The width is what a shoulder rising across the face is answered with: a traverse that still climbs too much is
     * stood further out, which makes the leg it is on longer. Past this the country is a cliff rather than a face and
     * the leg is left alone - a plan no worse than the one that laid the road before.
     */
    private static final int TRAVERSE_WIDEST = 96;
    /** How far either side of a river's middle the plan will look for dry ground, in blocks. */
    private static final int BANK_SEARCH = 96;
    /**
     * The widest water this will bridge, in columns.
     * <p>
     * The pieces repeat, so a span of fifty and a span of thirty-nine are the same plan; a hundred columns of open
     * water is not a bridge but a viaduct, and a road that quietly built one would be a road nobody surveyed standing
     * in a place nobody chose. Past this the plan gives the whole route up rather than lay a line that cannot cross,
     * and the log names the stretch and its width so the crossing can be decided by hand.
     */
    private static final int MAX_SPAN = 96;
    /** How many blocks the bridge is either side of its line: nine wide, the width the pieces come in. */
    private static final int HALF = 4;
    /** How much of a crossing's approach is forced straight onto the span's own line. */
    private static final int RUN_IN = 8;
    /** How far the road runs on past a span before the country has it, and how far its approach begins out. */
    private static final int EXIT_REACH = 16;
    /** How many blocks the deck stands over the highest water on the span: the rule both walked spans use. */
    private static final int DECK_OVER_WATER = 2;
    /** The abutment piece and the span between them, in the jar's structure folder. */
    private static final String END_PIECE = "bridge_end";
    private static final String MIDDLE_PIECE = "bridge_middle";
    /** A bank the plan could not find, as against a coordinate. */
    private static final int NO_BANK = Integer.MIN_VALUE / 8;

    private static final Logger LOGGER = LoggerFactory.getLogger(RoadPlan.class);

    /** The kind each planned section is laid at, by section id, so a planned road keeps the width it was asked for. */
    private static final Map<String, Roads.Kind> KINDS = new ConcurrentHashMap<>();
    /** The bends each planned section is walked through, by section id: the marks a survey would have left. */
    private static final Map<String, List<BlockPos>> MARKS = new ConcurrentHashMap<>();
    /** The sections whose width the bands decide, column by column, rather than one kind for the whole road. */
    private static final Set<String> BANDED = ConcurrentHashMap.newKeySet();

    private RoadPlan() {
    }

    /**
     * The sections of a road between two ends, in the order they are laid.
     * <p>
     * A road that crosses nothing is one {@link Route.Riverless} section. A road that meets water is three or more -
     * the dry road up to the near bank, the span, and the dry road on from the far one - each of which is a route the
     * pass already knows how to lay, with its own name, its own note in the save and its own bends. They meet end to
     * end on the same blocks, so the network sees one road and the pass sees as many short ones.
     * <p>
     * Both marks null is a road nobody has pinned yet: no sections, and nothing read from the map. A plan that refuses
     * is one the drawing will not carry - water past the span this can bridge, or a bank it cannot find - and it answers
     * with the reason rather than laying the road straight through the water it would not bridge.
     */
    static Plan of(String id, Roads.Kind kind, boolean town, BlockPos from, BlockPos to) {
        return of(id, kind, town, from, to, null);
    }

    /**
     * The same plan, with every span in it stood from one bridge's pieces.
     * <p>
     * A bridge is otherwise found from the ground: {@code Roads.BRIDGES} names a block on the water a bridge crosses,
     * and the span covering that block is stood with its pieces. That reading cannot help a leg whose water is not the
     * water the table names - a road planned toward a town crosses where the country takes it - so the surveyor may
     * name the bridge instead, and the plan writes that bridge's two pieces into every span it makes. The pieces are
     * the whole of what a span carries about its bridge, and the world keeps them in its own plan store, so a road
     * planned with the stone bridge is still stone after a restart.
     * <p>
     * A road that crosses nothing is unaffected, and the caller says so rather than leaving a bridge's name on a leg it
     * never stood.
     */
    static Plan of(String id, Roads.Kind kind, boolean town, BlockPos from, BlockPos to, Roads.Bridge bridge) {
        if (from == null || to == null) return new Plan(List.of(), null, false);
        Plan plan = walk(id, from, to, false, false);
        if (plan != null && plan.doubled()) {
            // Water the line met twice is one channel crossed badly, not two rivers. The leg is walked again with
            // a lane change refused on a step that leaves water, and the straight crossing is the plan - a leg whose
            // own line was sound keeps that line exactly, which is what keeps every road the test world laid where
            // it laid it.
            Plan held = walk(id, from, to, false, true);
            if (held != null && !held.refused()) plan = held;
        }
        if (plan != null && plan.refused()) {
            // Water the walk would not bridge is not the end of the road: the leg is walked again with the bank priced,
            // which steps the line inland along it. A road does not need the water's edge; it needs a way round it.
            Plan inland = walk(id, from, to, true, false);
            if (inland != null) plan = inland;
        }
        if (plan != null && plan.refused()) {
            // No road is better than a road through the water: the straight line this used to fall back to is a paved
            // ford nobody chose, with the country either side of it graded down to the riverbed on the way to it.
            LOGGER.info("Road plan {}: nothing laid - {}", id, plan.refusal());
            return plan;
        }
        List<Route> sections = plan == null || plan.sections().isEmpty()
                ? List.of(new Route.Riverless(id, 0, 0, 0, 0, from.getX(), from.getZ(), to.getX(), to.getZ()))
                : plan.sections();
        if (bridge != null) sections = bridged(sections, bridge);
        // The kind and the marks are the plan's answer whether or not the road is laid, so a route can be judged before
        // any ground is moved. The road itself joins ROUTES only when it is laid - a corridor that remembered a planned
        // road would have the world start laying it the next time a chunk of it loaded, which is not what `plan` means.
        for (Route section : sections) {
            KINDS.putIfAbsent(section.id(), kind);
            if (town) BANDED.add(section.id());
        }
        return new Plan(sections, null, false);
    }

    /**
     * The sections with every span stood from one bridge's pieces.
     * <p>
     * A span is a record and its bridge is two of its fields, so standing the road's crossings as the named bridge's is
     * a copy of each span with those two fields written. The banks, the deck and the approaches stay the plan's own: a
     * bridge is as long as the water between its abutments allows rather than as long as it was drawn, so the pieces a
     * span names are all a bridge's plan needs to know.
     */
    private static List<Route> bridged(List<Route> sections, Roads.Bridge bridge) {
        List<Route> stood = new ArrayList<>(sections.size());
        for (Route section : sections) {
            if (!(section instanceof Route.Crossing span)) {
                stood.add(section);
                continue;
            }
            stood.add(new Route.Crossing(span.id(), span.cityX0(), span.cityX1(), span.cityZ0(), span.cityZ1(),
                    span.pathX(), span.pathZ(), span.bankX(), span.bankZ(), span.farX(), span.farZ(), span.half(),
                    span.bridgeBase(), span.endBase(), span.deckY(), span.runIn(), span.exitReach(),
                    bridge.endPiece(), bridge.middlePiece()));
        }
        return stood;
    }

    /** One walk of a leg, or null when the drawing could not be read for it at all. */
    private static Plan walk(String id, BlockPos from, BlockPos to, boolean inland, boolean straight) {
        try {
            return chain(id, from, to, inland, straight);
        } catch (RuntimeException failed) {
            // Reading the drawing is never the reason a road cannot be laid: a plan that could not read it is a road laid
            // straight, which is where every road without marks starts. A refusal is different, and is not caught here.
            LOGGER.warn("Road plan {}: the drawing could not be read for {}, {} to {}, {}: {}", id,
                    from.getX(), from.getZ(), to.getX(), to.getZ(), failed.toString());
            return null;
        }
    }

    /**
     * What a plan came to: the sections to lay in order, the reason it will lay none at all, and whether the line the
     * walk chose leaves water and meets water again - one channel crossed twice, which the plan walks a second time.
     */
    record Plan(List<Route> sections, String refusal, boolean doubled) {
        /** Whether the plan refused the road outright, as against having nothing to say yet. */
        boolean refused() {
            return refusal != null;
        }
    }

    /** One span of a planned line, or the reason the water the line met cannot be crossed. */
    private record Span(Route.Crossing crossing, String refusal) {
    }

    /** The kind a planned section is laid at, or null for a road this did not plan. */
    static Roads.Kind kind(String section) {
        return KINDS.get(section);
    }

    /** The bends a planned section is walked through, or none. */
    static List<BlockPos> bends(String section) {
        return MARKS.getOrDefault(section, List.of());
    }

    /** Whether a section is one this planned, and so begins wherever the world first meets it. */
    static boolean owns(String section) {
        return section != null && KINDS.containsKey(section);
    }

    /** Whether a planned section takes its width from the bands rather than one kind the whole way. */
    static boolean banded(String section) {
        return section != null && BANDED.contains(section);
    }

    /**
     * Puts a section read out of the save back in the tables the pass asks.
     * <p>
     * A plan is three answers rather than one: the width it is laid at, whether the bands decide that width, and the
     * marks the pass searches between. A road read back without them is a name the world knows and a road it cannot
     * lay - one kind short of the road it was - so what {@link RoadPlans} keeps is restored through here.
     */
    static void restore(Route section, Roads.Kind kind, boolean town, List<BlockPos> marks) {
        KINDS.put(section.id(), kind);
        if (town) BANDED.add(section.id());
        if (!marks.isEmpty()) MARKS.put(section.id(), marks);
    }

    /** Drops what one world's plans left, so another world's roads do not open beside them. */
    static void forget(Collection<String> sections) {
        for (String section : sections) {
            KINDS.remove(section);
            MARKS.remove(section);
            BANDED.remove(section);
        }
    }

    /**
     * The sections of one road, or null when the drawing says nothing and the road is to be laid straight.
     * <p>
     * {@code inland} is the second walk, taken only when the first refuses: it prices the lane inside
     * {@link #BANK_MARGIN} lanes of water as the bank it is, so the line steps inland along the bank instead of
     * skimming an edge its own sixteen-block readings cannot see. {@code straight} is the other second walk, taken
     * only when the first line met one channel twice: it refuses a lane change on a step that leaves water, so the
     * crossing is held straight on the lane the road reached the bank on. Neither is part of the first walk, so a leg
     * whose own line was sound plans the same line it did before.
     */
    private static Plan chain(String id, BlockPos from, BlockPos to, boolean inland, boolean straight) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double span = Math.hypot(dx, dz);
        if (span < LANE * 4.0D) return null;
        double ux = dx / span;
        double uz = dz / span;
        int steps = (int) Math.ceil(span / LANE) + 1;
        int middle = REACH / LANE;
        int lanes = middle * 2 + 1;

        // The drawing, once a step, for every lane the plan may use: x, z, the ground's height, the water's level,
        // and whether the column is river at all.
        int[][] drawn = new int[steps * lanes][];
        for (int step = 0; step < steps; step++) {
            double along = span * step / (double) (steps - 1);
            double centreX = from.getX() + ux * along;
            double centreZ = from.getZ() + uz * along;
            for (int lane = 0; lane < lanes; lane++) {
                double across = (lane - middle) * (double) LANE;
                int x = (int) Math.round(centreX - uz * across);
                int z = (int) Math.round(centreZ + ux * across);
                ArdaMap.Sample sample = ArdaMap.sample(x, z);
                boolean wet = sample.river() && sample.water() > sample.height();
                boolean bank = inland && !wet
                        && (isWet((int) Math.round(centreX - uz * (across - BANK_OFFSET)),
                        (int) Math.round(centreZ + ux * (across - BANK_OFFSET)))
                        || isWet((int) Math.round(centreX - uz * (across + BANK_OFFSET)),
                        (int) Math.round(centreZ + ux * (across + BANK_OFFSET))));
                drawn[step * lanes + lane] = new int[]{x, z, sample.height(), sample.water(),
                        sample.river() ? 1 : 0, bank ? 1 : 0};
            }
        }

        // The walk itself: every lane of every step, priced from the cheapest lane of the step before it, and every
        // one of them in one of a few states - wet, or dry with the count of dry steps since the water, held at the
        // reach a crossing is given. The state is what keeps the road from dipping into a channel and climbing out
        // again: water met before it has been dry {@link #DRY_AFTER_CROSSING} lanes is the channel it has just crossed,
        // and is priced as a second bridge over it. Nothing is forbidden outright - the walk has to finish for the road
        // to be planned at all - but nothing the walk could do about it competes with that price.
        int longest = DRY_AFTER_CROSSING + 1;
        double[][] cost = new double[longest][lanes];
        int[][][] came = new int[steps][longest][lanes];
        for (double[] row : cost) Arrays.fill(row, Double.MAX_VALUE);
        for (int[][] states : came) for (int[] row : states) Arrays.fill(row, -1);
        cost[drawn[middle][4] == 1 ? 0 : DRY_AFTER_CROSSING][middle] = 0.0D;
        for (int step = 1; step < steps; step++) {
            double[][] next = new double[longest][lanes];
            for (double[] row : next) Arrays.fill(row, Double.MAX_VALUE);
            for (int lane = 0; lane < lanes; lane++) {
                int[] here = drawn[step * lanes + lane];
                boolean wet = here[4] == 1;
                double toll = wet ? RIVER_TOLL * (1.0D + Math.max(0, here[3] - here[2])) : 0.0D;
                double bank = wet || here[5] == 0 ? 0.0D : BANK_COST;
                for (int back = -SHIFT; back <= SHIFT; back++) {
                    int was = lane + back;
                    if (was < 0 || was >= lanes) continue;
                    int[] there = drawn[(step - 1) * lanes + was];
                    // The second walk refuses a lane change on a step that leaves water. The line between two
                    // lanes is a staircase, so a road that stepped sideways out of a channel could leave it at a
                    // single dry edge block and be back in it a lane later - the doubled crossing. A crossing is
                    // straight, on the lane the road reached the bank on, which is also the shape a span's banks
                    // and approaches are read for.
                    if (straight && back != 0 && there[4] == 1) continue;
                    double rise = Math.abs(here[2] - there[2]);
                    double over = Math.max(0.0D, rise - TARGET_GRADE * LANE);
                    double price = LANE + GRADE_PENALTY * over * over + TURN_COST * Math.abs(back) + toll + bank;
                    for (int state = 0; state < longest; state++) {
                        double before = cost[state][was];
                        if (before == Double.MAX_VALUE) continue;
                        int nextState;
                        double again = 0.0D;
                        if (!wet) {
                            nextState = state == 0 ? 1 : Math.min(DRY_AFTER_CROSSING, state + 1);
                        } else {
                            nextState = 0;
                            // Dry, but not for the reach a crossing is given: this is the water it has just left.
                            if (state > 0 && state < DRY_AFTER_CROSSING) again = CROSS_AGAIN;
                        }
                        double total = before + price + again;
                        if (total >= next[nextState][lane]) continue;
                        next[nextState][lane] = total;
                        came[step][nextState][lane] = was * longest + state;
                    }
                }
            }
            cost = next;
        }
        int endState = -1;
        for (int state = 0; state < longest; state++) {
            if (cost[state][middle] == Double.MAX_VALUE) continue;
            if (endState < 0 || cost[state][middle] < cost[endState][middle]) endState = state;
        }
        if (endState < 0) return null;
        int[] lane = new int[steps];
        lane[steps - 1] = middle;
        int state = endState;
        for (int step = steps - 1; step > 0; step--) {
            int cameFrom = came[step][state][lane[step]];
            if (cameFrom < 0) return null;
            lane[step - 1] = cameFrom / longest;
            state = cameFrom % longest;
        }
        List<int[]> line = new ArrayList<>(steps);
        for (int step = 0; step < steps; step++) line.add(drawn[step * lanes + lane[step]]);
        return sections(id, from, to, line);
    }

    /**
     * The line cut into the routes it is made of, a dry road for every stretch and a span for every river.
     * <p>
     * Each crossing is taken out of the dry road either side of it and becomes a route of its own, so the road stops at
     * the near bank and picks up on the far one exactly as the walked ones do. The two dry sections meet the span on the
     * blocks its own approach and exit stop on, which is the arrangement the high pass road and its bridge already use.
     * <p>
     * The water is found along the line a block at a time rather than at the steps the walk itself is priced at. A step
     * is sixteen blocks - as long as a drawn channel is deep and shorter than one is wide - so a river crossed on a
     * diagonal can lie between two samples with the ground on both of them dry, and a road planned that way is graded
     * straight through a river the drawing never let the plan see: a causeway under water, with the water standing over
     * it and no span above. Reading every block of the line is what turns that into the bridge the crossing was for.
     */
    private static Plan sections(String id, BlockPos from, BlockPos to, List<int[]> line) {
        List<int[]> ground = dense(line);
        List<Route> sections = new ArrayList<>();
        LinkedHashMap<String, List<BlockPos>> marks = new LinkedHashMap<>();
        int cursor = 0;
        int fromX = from.getX();
        int fromZ = from.getZ();
        int dry = 1;
        int bridges = 0;
        // The end of the last run of water on the line, and whether a later run met the same channel again.
        int lastWater = -1;
        boolean doubled = false;
        int index = 0;
        // A mark in the water is not a crossing. Where the road's own first blocks are drawn as river, the road begins
        // on the bank the water ends on - the first dry block of its own line - rather than standing a span from a
        // block that has no dry ground behind it to put an abutment on. That is the answer a surveyor gives when the
        // pin is in the shallows, and it is what lets a leg pinned in a pool still be a road: the walk steps its middle
        // inland, and the water at the two ends is left to the pin.
        if (!ground.isEmpty() && ground.get(0)[4] == 1) {
            while (index < ground.size() && ground.get(index)[4] == 1) index++;
            if (index >= ground.size()) {
                return new Plan(List.of(), "the line is water from end to end, so there is no bank to start the road on", false);
            }
            fromX = ground.get(index)[0];
            fromZ = ground.get(index)[1];
            cursor = index;
        }
        int endX = to.getX();
        int endZ = to.getZ();
        int endIndex = ground.size() - 1;
        while (index < ground.size()) {
            if (ground.get(index)[4] == 0) {
                index++;
                continue;
            }
            int began = index;
            while (index < ground.size() && ground.get(index)[4] == 1) index++;
            int ended = index - 1;
            if (lastWater >= 0 && began - lastWater <= DRY_AFTER_CROSSING * LANE) doubled = true;
            lastWater = ended;
            if (ended == ground.size() - 1) {
                // The far end is in the water too: the road stops at the last dry block it reached, and the water
                // past it is no more a crossing than the water it started in.
                endX = ground.get(began - 1)[0];
                endZ = ground.get(began - 1)[1];
                endIndex = began - 1;
                break;
            }
            Span span = crossing(id + "_bridge_" + (bridges + 1), ground, began, ended);
            if (span.crossing() == null) return new Plan(List.of(), span.refusal(), false);
            Route.Crossing water = span.crossing();
            bridges++;
            int[] near = approach(water, true);
            int[] far = approach(water, false);
            if (Math.hypot(near[0] - fromX, near[1] - fromZ) >= LANE) {
                String name = dry == 1 ? id : id + "_" + dry;
                sections.add(new Route.Riverless(name, 0, 0, 0, 0, fromX, fromZ, near[0], near[1]));
                marks.put(name, marks(ground, cursor, began, false));
                dry++;
            }
            sections.add(water);
            fromX = far[0];
            fromZ = far[1];
            cursor = ended;
        }
        if (Math.hypot(endX - fromX, endZ - fromZ) >= 1.0D) {
            String name = dry == 1 ? id : id + "_" + dry;
            sections.add(new Route.Riverless(name, 0, 0, 0, 0, fromX, fromZ, endX, endZ));
            marks.put(name, marks(ground, cursor, endIndex, true));
        }
        MARKS.putAll(marks);
        return new Plan(sections, null, doubled);
    }

    /**
     * The line's own blocks, one at a time, with the drawing read for each.
     * <p>
     * The walk between two steps is a staircase of single blocks, and it is the blocks a road is really paved along, so
     * the ground is read there rather than at the sixteen-block steps: a channel is wider than one step and a plan that
     * only looks at steps can walk round the edge of one and never see it. The reading stops at the step itself, so no
     * block is added that was not on the line the walk priced.
     */
    private static List<int[]> dense(List<int[]> line) {
        List<int[]> blocks = new ArrayList<>();
        blocks.add(column(line.get(0)));
        for (int at = 1; at < line.size(); at++) {
            int[] was = line.get(at - 1);
            int[] next = line.get(at);
            int guard = 0;
            while ((was[0] != next[0] || was[1] != next[1]) && guard++ < 4096) {
                was = new int[]{was[0] + Integer.signum(next[0] - was[0]),
                        was[1] + Integer.signum(next[1] - was[1])};
                blocks.add(column(was));
            }
        }
        return blocks;
    }

    /** Whether the drawing gives a column water standing over its bed: the test the line is cut on. */
    private static boolean isWet(int x, int z) {
        ArdaMap.Sample sample = ArdaMap.sample(x, z);
        return sample.river() && sample.water() > sample.height();
    }

    /**
     * One block of the line: where it is, the ground's height, the water's level, and whether water stands over it.
     * <p>
     * Water is a column the drawing gives a bed below its own surface - the rivers and the lakes, which both carry the
     * drawing's water rather than a level of their own. Ground that merely stands under the sea's level without any
     * water drawn over it is land: the map raises the shore to hold its water, and a plain the drawing put below the
     * old sea line is a plain.
     */
    private static int[] column(int[] point) {
        ArdaMap.Sample sample = ArdaMap.sample(point[0], point[1]);
        boolean wet = sample.river() && sample.water() > sample.height();
        return new int[]{point[0], point[1], sample.height(), sample.water(), wet ? 1 : 0};
    }

    /**
     * One span, from the run of river the line met.
     * <p>
     * The water is read both ways and the narrower reading is the bridge, because the way a span has to be stood belongs
     * to the channel rather than to the road. A span is on one axis and both approaches are forced onto its line, so a
     * road that arrives diagonally is walked square for the length of the bridge - and the short way across is the
     * bridge. Reading it the road's own way alone goes wrong wherever the run is a few blocks long: the two ends of it
     * differ by whatever the line's rounding gave, a road crossing a north-south channel almost square still comes out
     * with a block or two of travel up the channel in it, and a span stood on that difference is measured *down* the
     * river. That is what refused the mountains' channel at 6471, -3645: forty columns across, a hundred and more down,
     * and the plan said no dry ground within ninety-six.
     * <p>
     * Either reading stands on the line the road *first* meets water on, and takes its banks from the two ends of the
     * run - back from the lane the road arrives at, and on from the lane it leaves by. The middle of a run is no use: a
     * run a few lanes long and a little diagonal has its middle over water either side of where the road actually
     * crosses, so a span stood there lies at an angle and both approaches have to be forced onto a line the road never
     * travelled. The water's level is the highest the run is given, so a span clears the deepest part of the channel
     * rather than its average.
     */
    private static Span crossing(String id, List<int[]> line, int began, int ended) {
        int[] entry = line.get(began);
        int[] exit = line.get(ended);
        boolean travelX = Math.abs(exit[0] - entry[0]) >= Math.abs(exit[1] - entry[1]);
        Cut chosen = pick(cut(entry, exit, true), cut(entry, exit, false), travelX);
        if (!chosen.stood()) return new Span(null, chosen.refusal());
        int deck = Math.max(Math.max(ground(chosen.alongX(), chosen.across(), chosen.bank()),
                        ground(chosen.alongX(), chosen.across(), chosen.far())),
                water(chosen.alongX(), chosen.across(), chosen.bank(), chosen.far()) + DECK_OVER_WATER);
        int approachAlong = chosen.bank() - chosen.step() * EXIT_REACH;
        boolean alongX = chosen.alongX();
        int across = chosen.across();
        int bank = chosen.bank();
        int far = chosen.far();
        return new Span(new Route.Crossing(id, 0, 0, 0, 0,
                alongX ? approachAlong : across, alongX ? across : approachAlong,
                alongX ? bank : across, alongX ? across : bank,
                alongX ? far : across, alongX ? across : far,
                HALF, deck - 2, deck - 1, deck, RUN_IN, EXIT_REACH, END_PIECE, MIDDLE_PIECE), null);
    }

    /** One reading of a span along one axis: where its banks are, and why it could not be stood. */
    private record Cut(boolean alongX, int across, int bank, int far, int step, String refusal) {
        /** Whether a bridge can be stood this way. */
        boolean stood() {
            return this.refusal == null;
        }

        /** Whether both banks of this reading were found, which is what lets it say how wide the water is. */
        boolean banked() {
            return this.bank != NO_BANK && this.far != NO_BANK;
        }

        /** How many columns of water a bridge this way would span. */
        int width() {
            return Math.abs(this.far - this.bank) + 1;
        }
    }

    /**
     * The water read along one axis: the first dry column either side of the run, and what that comes to.
     * <p>
     * Both readings of a crossing are taken, so neither may assume which way the road is going. The axis is the one it
     * was asked for, and the way along it is the way the run itself runs on that axis, because the bank behind the
     * entry is the one on the far side of the road's approach.
     */
    private static Cut cut(int[] entry, int[] exit, boolean alongX) {
        int step = alongX ? (int) Math.signum(exit[0] - entry[0]) : (int) Math.signum(exit[1] - entry[1]);
        if (step == 0) step = 1;
        int across = alongX ? entry[1] : entry[0];
        int arrival = alongX ? entry[0] : entry[1];
        int departure = alongX ? exit[0] : exit[1];
        int bank = dry(alongX, across, arrival, -step);
        int far = dry(alongX, across, departure, step);
        String where = entry[0] + ", " + entry[1];
        if (bank == NO_BANK) {
            return new Cut(alongX, across, bank, far, step, "no dry ground within " + BANK_SEARCH + " blocks of " + where
                    + " the way the road goes");
        }
        if (far == NO_BANK) {
            return new Cut(alongX, across, bank, far, step,
                    "no far bank within " + BANK_SEARCH + " blocks of " + where);
        }
        if ((far - bank) * step <= 0) {
            return new Cut(alongX, across, bank, far, step,
                    "the water at " + where + " runs the wrong way across the road");
        }
        int width = Math.abs(far - bank) + 1;
        if (width > MAX_SPAN) {
            return new Cut(alongX, across, bank, far, step, "the water at " + where + " is " + width
                    + " columns wide, past the " + MAX_SPAN + " this will bridge");
        }
        return new Cut(alongX, across, bank, far, step, null);
    }

    /**
     * Which reading of the water the bridge is stood on: the narrow one.
     * <p>
     * A span is the shortest way across the channel, and the other reading is the one that walks down it - the same
     * river that is forty columns across is a hundred and more along its own length. When both readings come to the
     * same width, a channel crossed at forty-five degrees, the road's own way through decides, which is what the plan
     * did before there were two readings. A reading that found both banks is refused with before one that found none:
     * it can say how wide the water is, and that is the number the crossing gets decided by hand from.
     */
    private static Cut pick(Cut acrossX, Cut acrossZ, boolean travelX) {
        Cut road = travelX ? acrossX : acrossZ;
        Cut other = travelX ? acrossZ : acrossX;
        if (road.stood() && other.stood()) return other.width() < road.width() ? other : road;
        if (road.stood()) return road;
        if (other.stood()) return other;
        return !road.banked() && other.banked() ? other : road;
    }

    /** The block a span's approach starts on, or the one its exit stops on, given how its pieces are stood. */
    private static int[] approach(Route.Crossing span, boolean near) {
        int along = (near ? span.bank() : span.far()) + (near ? -span.step() : span.step()) * EXIT_REACH;
        return span.alongX() ? new int[]{along, span.bankZ()} : new int[]{span.bankX(), along};
    }

    /** The first dry column from a point of the river, walking one way along the span's own axis. */
    private static int dry(boolean alongX, int across, int from, int direction) {
        for (int step = 0; step <= BANK_SEARCH; step++) {
            int along = from + direction * step;
            ArdaMap.Sample sample = read(alongX, across, along);
            if (!sample.river() && sample.height() >= sample.water()) return along;
        }
        return NO_BANK;
    }

    /** The ground the drawing gives a column of the span. */
    private static int ground(boolean alongX, int across, int along) {
        return read(alongX, across, along).height();
    }

    /** The highest water the drawing gives any column between a span's banks. */
    private static int water(boolean alongX, int across, int bank, int far) {
        int highest = Integer.MIN_VALUE;
        for (int along = Math.min(bank, far); along <= Math.max(bank, far); along++) {
            highest = Math.max(highest, read(alongX, across, along).water());
        }
        return highest;
    }

    /** One column of a span, read along its own axis. */
    private static ArdaMap.Sample read(boolean alongX, int across, int along) {
        return alongX ? ArdaMap.sample(along, across) : ArdaMap.sample(across, along);
    }

    /**
     * The marks a dry section is bent through: a waypoint every {@link #MARK} blocks of the planned line, with the
     * switchbacks a leg too steep to lay is broken into.
     * <p>
     * They are not there to hold the road to the line - the pass searches between them - but to keep each leg short
     * enough to be searched in a narrow window. A road four thousand blocks long with no marks on it is one leg, and
     * one leg that long is a bounding box of ten million candidates: past the pass's budget, so it gives up and lays
     * the straight line. Marks every three hundred blocks are what turn that back into a road, and they are the whole
     * of what a scout's ammolite pins were doing.
     * <p>
     * A mark is also where the pass is asked to climb, and until now nothing here asked whether the leg between two
     * of them could be climbed at all. {@link #switchbacks} answers that: a leg whose ends stand more than
     * {@link #LAYABLE_GRADE} apart per block of travel is broken into the traverses it needs, and every other leg is
     * the one mark the spacing gives it - so a stretch of road whose own line was walkable plans exactly the marks it
     * did before.
     *
     * @param endsHere whether the line's own block at {@code high} is where the section ends, and so the far end of a
     *                 leg of its own
     */
    private static List<BlockPos> marks(List<int[]> line, int low, int high, boolean endsHere) {
        List<BlockPos> marks = new ArrayList<>();
        if (high - low < 2) return marks;
        double walked = 0.0D;
        int from = low;
        for (int at = Math.max(low, 1); at < high; at++) {
            int[] before = line.get(at - 1);
            int[] here = line.get(at);
            walked += Math.hypot(here[0] - before[0], here[1] - before[1]);
            if (walked < MARK) continue;
            double left = 0.0D;
            for (int rest = at; rest < high; rest++) {
                int[] one = line.get(rest);
                int[] next = line.get(rest + 1);
                left += Math.hypot(next[0] - one[0], next[1] - one[1]);
            }
            if (left < MARK / 2.0D) break;
            switchbacks(line.get(from), here, marks);
            marks.add(new BlockPos(here[0], here[2], here[1]));
            walked = 0.0D;
            from = at;
        }
        // The last leg runs from the last mark to the section's own end, and the pass reaches that end without a mark
        // to bend through: a climb left over there is the same fault as one in the middle, so it is broken here too.
        if (endsHere) switchbacks(line.get(from), line.get(high), marks);
        return marks;
    }

    /**
     * The marks a leg needs when its climb is more than the grade can lay: the switchbacks that make the road long
     * enough to climb it.
     * <p>
     * A leg is what the pass searches between two marks, and the two marks are what it has to arrive on. A leg whose
     * ends stand more than one block in two apart cannot be walked by that search however it settles the line between
     * them, because the grade it lays is settled on half a block a station - so what has always come back from a
     * climb of more than a few blocks is not a leg at all, but a fault handed to the pass from the wrong end.
     * <p>
     * What the drawing can say is how long the road has to be. A climb of c needs at least c / {@link
     * #LAYABLE_GRADE} of travel, and if the crow line is shorter than that, the road has to cross the fall and come
     * back: each crossing of the line adds two {@link #TRAVERSE} widths to its length, so the count is whatever brings
     * the two together. The marks that come out of it stand alternately either side of the line, and the pass lays a
     * hairpin between them because that is what its own search does with a leg it can walk.
     * <p>
     * The heights are the drawing's, read at the marks themselves rather than along the line, so each traverse is
     * priced on the ground it stands over. A traverse that still climbs too much - a shoulder rising across the face -
     * is answered by standing the marks further out, up to {@link #TRAVERSE_WIDEST}; past that the leg is left as it
     * was, which is the plan that laid this road before this existed and no worse than it.
     */
    private static void switchbacks(int[] start, int[] end, List<BlockPos> marks) {
        double run = Math.hypot(end[0] - start[0], end[1] - start[1]);
        int climb = Math.abs(end[2] - start[2]);
        if (run < 1.0D || climb <= LAYABLE_GRADE * run) return;
        int crossings = (int) Math.ceil(Math.sqrt(Math.max(0.0D, 4.0D * climb * climb - run * run))
                / (2.0D * TRAVERSE));
        crossings = Math.max(2, Math.min(TRAVERSE_MOST, crossings));
        // What each traverse has to be wide for the leg it carries: that leg gains climb/crossings and is asked to
        // run twice that far, and all of the difference from its along-distance is the traverse's.
        double each = climb / (double) crossings;
        double along = run / crossings;
        int width = (int) Math.ceil(Math.sqrt(Math.max(0.0D, 4.0D * each * each - along * along)));
        width = Math.max(TRAVERSE, Math.min(TRAVERSE_WIDEST, width));
        for (; width <= TRAVERSE_WIDEST; width = width * 3 / 2) {
            List<BlockPos> crossed = traverses(start, end, crossings, width);
            if (climbable(start, crossed, end)) {
                marks.addAll(crossed);
                return;
            }
        }
    }

    /** The marks of one switchback: {@code crossings} traverses across the leg, alternately either side of its line. */
    private static List<BlockPos> traverses(int[] start, int[] end, int crossings, int width) {
        double run = Math.hypot(end[0] - start[0], end[1] - start[1]);
        double ux = (end[0] - start[0]) / run;
        double uz = (end[1] - start[1]) / run;
        List<BlockPos> marks = new ArrayList<>(crossings - 1);
        for (int at = 1; at < crossings; at++) {
            double along = run * at / (double) crossings;
            double side = at % 2 == 1 ? width : -width;
            int x = (int) Math.round(start[0] + ux * along - uz * side);
            int z = (int) Math.round(start[1] + uz * along + ux * side);
            marks.add(new BlockPos(x, ArdaMap.sample(x, z).height(), z));
        }
        return marks;
    }

    /**
     * Whether every leg of a broken line, the two ends included, climbs no more than the grade can lay.
     * <p>
     * The marks were placed across the fall and the drawing read at each of them, so this is the check that the
     * switchback drawn is one the country has room for: a shoulder rising across the face makes a traverse steeper
     * than the line it was meant to lengthen, and a leg nobody can walk is what all of this is for.
     */
    private static boolean climbable(int[] start, List<BlockPos> line, int[] end) {
        int[] was = start;
        for (int at = 0; at <= line.size(); at++) {
            int[] here = at == line.size() ? end
                    : new int[]{line.get(at).getX(), line.get(at).getZ(), line.get(at).getY()};
            double run = Math.hypot(here[0] - was[0], here[1] - was[1]);
            if (run < 1.0D) return false;
            if (Math.abs(here[2] - was[2]) > LAYABLE_GRADE * run) return false;
            was = here;
        }
        return true;
    }
}
