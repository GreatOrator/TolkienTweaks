package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Where each build's road leaves from, and how it crosses the water on the way down.
 * <p>
 * A road is not a generic thing the way a drape is. The drape can read any cut's own outline and know what to do; a
 * road has to be told where it starts and where it ends - numbers a surveyor walks and writes down, not numbers the
 * build carries. They are a {@link Route} per road, kept here so that a second city brings its own road with one line
 * rather than a second copy of the road's code.
 * <p>
 * The city's own footprint is part of the route rather than of the cut: the road reads it three ways - the surface
 * bands are chosen by how close a column is to the walls, the descent may not cross it, and an eased corner may not
 * swing into it. All three are questions about where the road meets the build, which is the route's business.
 * <p>
 * <b>Where the way between the two ends comes from.</b> It used to come from a scout: a hundred ammolite marks on the
 * old forest road alone, each of them found on foot, and a route with no marks on it was one leg - which for a road
 * thousands of blocks long is a bounding box past the search's budget, and was laid straight through whatever stood in
 * it. Every route here now carries two ends and nothing else: {@link RoadPlan} reads the drawing at sixteen blocks a
 * lane and says where the road goes, and the pass searches each of the legs it leaves. The walked network this class
 * used to hold has been erased with it, so a road is made by naming its two ends - or, at a build, by
 * {@code /tmroad build <x> <z>} - and the country between them is planned rather than remembered.
 * <p>
 * One route per road, of whichever kind the country asks for: a {@link Route.Crossing} where the road meets water and
 * has a span to stand up, a {@link Route.Riverless} where it has only two ends. A build without one is generated,
 * draped and left alone: better a city with no road than a road laid to another city's coordinates.
 */
public final class Roads {
    private Roads() {
    }

    /**
     * How far a road's own search may wander either side of a leg, in blocks.
     * <p>
     * A walked road is searched wide, because two marks are the only thing saying where it goes and the search may have
     * to find its way round anything. A planned road is not: {@link RoadPlan} has already chosen the way at sixteen
     * blocks a lane, so the search's business is only to sit that line on the ground it crosses, and a narrow window is
     * what keeps a leg from reading - and generating - the country two hundred blocks either side of it. Measured on
     * the old forest road, a leg at the walked width reads some four hundred chunks; at the planned one, ninety.
     */
    private static final int WANDER = 96;
    /** The same window for a planned road: a hem either side of the line, and no more. */
    private static final int PLANNED_WANDER = 32;

    /**
     * The road out of Gundabad: the hold's way down out of its own gate, and the north's road to everything else.
     * <p>
     * The hold stands at `ArdaMap.keepPlan()` - 5528, 176, -7112 since 21 November - at the head of its own valley,
     * and what the road leaves into is
     * country nobody has walked: the nearest road of any age is the chapter's own at the far end of the old forest
     * road, three and a half thousand blocks south. Its two ends were pinned in a test world - the gate block at
     * 5578, -6769 and the join on the old forest road at 8583, -3617 - and everything between them is planned: the
     * hold's road carries no marks at all, and those two blocks are the whole of the survey it needs.
     * <p>
     * The name is the id the pins were given rather than a name of its own, and written out in full for that reason.
     * A section's id is made of the road's two ends, so a road called {@code gundabad_road} here would plan sections
     * the test world's save has never heard of: the ground would be laid a second time in the world that walked it,
     * and a fresh world would hold a road that is not the road that was walked. Nothing about a road needs a name of
     * its own - the id is the two ends, and those say where it goes.
     */
    public static final String GUNDABAD_ROAD = "tolkienmobs:roads/planned_5578_-6769_8583_-3617";

    /**
     * The Gundabad road's two ends, as they were pinned: the block at the foot of the hold's own way out, and the mark
     * where the road meets the road it joins.
     * <p>
     * Everything else about the road is worked out from them - the way it goes, where it crosses water, how wide its
     * span is and how wide it is laid - so these two lines are the whole of what the road is.
     * <p>
     * They are declared above {@link #ROUTES} on purpose: the list is built once when this class loads, and a mark
     * written below it would still be null at that moment, which is a road that never appears and never says why.
     */
    private static final BlockPos GUNDABAD_GATE = at(5578, -6769);
    private static final BlockPos GUNDABAD_JOIN = at(8583, -3617);

    /** One road the code names: its id, the width it is laid at, and the two blocks it runs between. */
    private record Named(String id, Kind kind, boolean town, BlockPos from, BlockPos to) {
    }

    /** A block of the drawing named by its two coordinates: the height is the plan's business, not the road's. */
    private static BlockPos at(int x, int z) {
        return new BlockPos(x, 0, z);
    }

    /**
     * The network the world is laid with: every road a surveyor has pinned, as the two ends it was pinned between.
     * <p>
     * This is the whole of a road - the way it takes, the bends the pass searches between, where it crosses water and
     * how wide its span is all read off the drawing from these two blocks. It was worked out in a test world by
     * {@code /tmroad pin}, one road at a time, and the ends are copied here so a world made from scratch has the same
     * network without anybody plotting it again; the ids are the ones the pins were given, so a section laid in that
     * world and a section made here are the same road and the same name in the log.
     * <p>
     * The first entry is the hold's road, out of Gundabad's own gate and down to the old forest road
     * (see {@link #GUNDABAD_GATE}). Everything after it is a road the test world lays today, in the order they were
     * walked: the old forest road east of Mirkwood to Glóin's camp; the four legs of the high pass between Rivendell's
     * roads and Glóin's camp; the road out of Rivendell to the meeting plaza, and the plaza's road on to Glóin's camp;
     * the road from Rivendell down to Elladan's camp; and the short leg between Rivendell's south gate and the same
     * road's shoulder, which the first of those two was laid from. All of them are the country's own track, three wide,
     * because nothing that crosses that country is paved.
     * <p>
     * A road is here when it is in the world's store - the roads the test world walked are read back one at a time and
     * written down here with the ends their own ids were made of. A road left out is not a road that waits: a fresh
     * world plans only what is in this list and quietly has no road there, which is how three roads of the vale came to
     * be missing from a world made after them. Nor is a line here a road: a leg pinned in a test world and never laid
     * is a line that plans sections in every fresh world for ground nobody has walked, so the table holds the store's
     * roads and nothing else.
     * <p>
     * Declared above {@link #ROUTES} on purpose, as the hold's two marks are: the list is built once when this class
     * loads, and a road written below it would not be in the list at all.
     */
    /**
     * Whether the surveyed network is laid at all.
     * <p>
     * Off from 24 November 2026 while Rivendell's site is re-scouted: every leg here was surveyed around the city
     * as it stood, and a road whose end moves is a different road. The table below is kept entire, so opening this
     * lays the old lines again; a road re-surveyed from the new city is a new entry under the id its two ends give
     * it.
     */
    private static final boolean NETWORK_OPEN = false;

    // The two long legs are out of the table, at the owner's word on 25 November 2026: the road between Bree
    // and the Shire (walked in `Progress Check 3`, five wide, from Bree's hall front at 1032, -34 to the
    // village's east side at -150, -382) and the road between Rivendell and Bree (marked in `Progress Check 4`,
    // five wide, from the vale's junction at 4648, -3316 to the same hall front at 1031, -36). He is laying that
    // road himself as the next build, so the mod must not stand a second line beside it - not now, and not when
    // the survey is opened again after Rivendell's site is re-scouted.
    //
    // The two ends are written down here rather than the entries left in the table behind a flag, because a leg
    // in the table is a leg a fresh world plans: NETWORK_OPEN only decides whether the table is read, so a
    // session that opened it would put both lines on the ground the owner is building over. An id is a road's
    // two ends, so a leg walked again later is a new entry under a new pair; there is nothing here to restore.
    //
    // What a table cannot take back is the ground a world has already laid. Nothing in the mod lifts a road, and
    // a plan a hand wrote under either id is that save's own, read back by RoadPlans at every start - /tmroad
    // forget standing on the line drops it, and the blocks it laid stay.

    private static final List<Named> NETWORK = List.of(
            new Named(GUNDABAD_ROAD, Kind.WILD, false, GUNDABAD_GATE, GUNDABAD_JOIN),
            new Named("tolkienmobs:roads/planned_11813_-3901_8229_-3654", Kind.WILD, false, at(11813, -3901), at(8229, -3654)),
            new Named("tolkienmobs:roads/planned_4515_-2446_4705_-3169", Kind.WILD, false, at(4515, -2446), at(4705, -3169)),
            new Named("tolkienmobs:roads/planned_4528_-2541_4624_-2480", Kind.WILD, false, at(4528, -2541), at(4624, -2480)),
            new Named("tolkienmobs:roads/planned_4776_-3063_4684_-3050", Kind.WILD, false, at(4776, -3063), at(4684, -3050)),
            new Named("tolkienmobs:roads/planned_4787_-3059_4778_-3059", Kind.WILD, false, at(4787, -3059), at(4778, -3059)),
            new Named("tolkienmobs:roads/planned_4819_-2887_4647_-3315", Kind.WILD, false, at(4819, -2887), at(4647, -3315)),
            new Named("tolkienmobs:roads/planned_5105_-3413_5605_-3622", Kind.WILD, false, at(5105, -3413), at(5605, -3622)),
            new Named("tolkienmobs:roads/planned_5606_-3622_8229_-3654", Kind.WILD, false, at(5606, -3622), at(8229, -3654)),
            new Named("tolkienmobs:roads/planned_5200_-3033_4647_-3315", Kind.WILD, false, at(5200, -3033), at(4647, -3315)),
            new Named("tolkienmobs:roads/planned_5111_-3399_5072_-3221", Kind.WILD, false, at(5111, -3399), at(5072, -3221)));

    /**
     * Every road the world knows about, which is nothing until one is named.
     * <p>
     * The walked network is gone: each road here was a list of a hundred marks and a crossing written out by hand, and
     * the same road now comes from two ends and the drawing. What is left is the list - a road named in the code and a
     * road planned at a build both arrive here, so the corner a road passes through, the chunk that holds it and the
     * pass that lays it all find it the same way. It is writable for the second of those reasons: a planned road is
     * added the moment it is planned, and it is forgotten when the world is. Its note in the save is what brings it
     * back, and a road whose note is still good owes nothing.
     */
    public static final List<Route> ROUTES = new CopyOnWriteArrayList<>(network(List.of()));

    /** The sections this session planned at a build, so another world's can be taken back out of the list. */
    private static final Set<String> PLANNED = ConcurrentHashMap.newKeySet();

    /**
     * Whether a road is one this session planned at a build, as against one the code names in {@link #NETWORK}.
     * <p>
     * The two are different kinds of road to the world. A named road is part of the world from the moment its class
     * loads, so the ground it crosses begins it; a planned one waits for the command that asked for it.
     */
    static boolean planned(Route route) {
        return PLANNED.contains(route.id());
    }

    /** Adds a road this class did not hold, so a road planned at a build is known to the world as it is laid. */
    static void remember(Route route) {
        List<Route> routes = ROUTES;
        // A road named in the code asks for its own sections while this class is still being set up, which is before
        // the list exists: they are already in it by the time it is made, so there is nothing to add.
        if (routes == null) return;
        PLANNED.add(route.id());
        for (Route known : routes) {
            if (!known.id().equals(route.id())) continue;
            // A section named the same as one already here is that section planned again - an id is made of the road's
            // two ends - and the newest reading of the ground is the one to lay. Keeping the first answer is what left
            // a leg pinned a second time standing as the road it used to be: planned again with a bridge over the water
            // it had crossed under, the section without the bridge was the one the world still held.
            if (known != route) routes.set(routes.indexOf(known), route);
            return;
        }
        routes.add(route);
    }

    /**
     * Takes the planned roads back out of the list, before another world's plans are read in.
     * <p>
     * The list and the plan's tables are the class's, not a world's, and a client that swaps one save for another
     * keeps them. Without this the roads of the last world stand in the next one - laid, findable and forgettable, on
     * ground that has never had them. A road named in the code was never added here and is untouched.
     */
    static void clearPlanned() {
        Set<String> taken = Set.copyOf(PLANNED);
        ROUTES.removeIf(route -> taken.contains(route.id()));
        RoadPlan.forget(taken);
        PLANNED.clear();
    }

    /** Every section one planned road was made of, in the order the world holds them. */
    static List<Route> sections(String road) {
        String stem = road + "_";
        List<Route> found = new ArrayList<>();
        for (Route route : ROUTES) {
            if (route.id().equals(road) || route.id().startsWith(stem)) found.add(route);
        }
        return found;
    }

    /**
     * The planned road a section belongs to: the shortest section id it begins with.
     * <p>
     * A road that crosses water is several sections and only the first of them is named after the road itself; the rest
     * carry {@code _2}, {@code _3} for later dry stretches and {@code _bridge_<n>} for the spans. Which road a section
     * belongs to is therefore read off the sections rather than by trimming its suffix, because a road's own name ends
     * in a number too - {@code planned_..._3654} is the road and {@code planned_..._3654_2} is its second stretch, and
     * a rule that strips the last number cannot tell them apart.
     */
    static String roadOf(String section) {
        String best = section;
        for (Route route : ROUTES) {
            String id = route.id();
            if (id.length() >= best.length()) continue;
            if (id.equals(section) || section.startsWith(id + "_")) best = id;
        }
        return best;
    }

    /**
     * Drops every section of one planned road, so none of it is laid again, found or forgotten twice.
     * <p>
     * Forgetting the section at the hand used to leave the road's other sections in the list and in the plan store, and
     * the next start put them all back to be laid as the ground loaded: the old forest road was planned twice in the
     * mountains and stood twice, seven sections each, three bridges over the same water and three roads leaving one
     * spot. A road is forgotten as a road - every section it begins with - and with its sections go their plans, so
     * nothing of it is restored to be laid by a world nobody asked.
     */
    static void forget(String road) {
        Set<String> taken = new HashSet<>();
        for (Route route : sections(road)) taken.add(route.id());
        if (taken.isEmpty()) return;
        PLANNED.removeAll(taken);
        ROUTES.removeIf(route -> taken.contains(route.id()));
        RoadPlan.forget(taken);
    }

    /**
     * The roads as they stand: everything in {@link #NETWORK}, each planned from its own two ends.
     * <p>
     * A road whose ends are not pinned - the hold's, until its walk is made - plans nothing and is not in the list,
     * which is the same answer as having no entry for it at all.
     */
    private static List<Route> network(List<Route> walked) {
        List<Route> all = new ArrayList<>(walked);
        if (!NETWORK_OPEN) return all;
        for (Named road : NETWORK) {
            all.addAll(RoadPlan.of(road.id(), road.kind(), road.town(), road.from(), road.to()).sections());
        }
        return all;
    }

    /**
     * The blocks a road is bent through, in the order it walks them, or none when it is walked straight.
     * <p>
     * A mark a scout stood on outranks a planned one: a route with its own pins is a road somebody has an opinion about,
     * and the plan bends round it rather than through it. Every other route is bent where {@link RoadPlan} put its
     * marks - a leg every few hundred blocks, which is what keeps each of them short enough to be searched.
     */
    public static List<BlockPos> bends(Route route) {
        List<BlockPos> marks = hand(route);
        return marks.isEmpty() ? RoadPlan.bends(route.id()) : marks;
    }

    /** The marks written down for a route by hand, if any. There are none left; the door is kept open. */
    private static List<BlockPos> hand(Route route) {
        return List.of();
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
     * way. A planned road is given the kind it was planned at; a route named here is given the kind written against it.
     */
    private static final Map<String, Kind> KINDS = Map.of();

    /** The kind a road is laid the whole way, or null where the build and the bands decide it. */
    public static Kind kind(Route route) {
        // A banded road has no kind to name: its width is the bands', column by column, from the town's street in to the
        // wild track out in the country.
        if (banded(route)) return null;
        Kind named = KINDS.get(route.id());
        return named != null ? named : RoadPlan.kind(route.id());
    }

    /**
     * The routes that are roads rather than any build's way out: a name, two ends, and no building to wait for.
     * <p>
     * A crossing is usually the city's own business, so it is laid by the dressing that raises the city, and only once
     * that build is standing - nothing begins it from the ground it crosses. A road that carries traffic between
     * places has no such owner: it begins wherever the world first meets it. A planned road is one of the second kind
     * by construction - it exists because somebody asked for it, not because a build is waiting for it.
     */
    private static final Set<String> ROADS = Set.of();

    /** Whether a route is a road rather than a build's own way out, and so begins wherever it is first met. */
    public static boolean road(Route route) {
        return ROADS.contains(route.id()) || RoadPlan.owns(route.id());
    }

    /** How wide a leg's search may wander, which is narrower for a road the drawing laid out than for a walked one. */
    public static int wander(Route route) {
        return RoadPlan.owns(route.id()) ? PLANNED_WANDER : WANDER;
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
     * cast of road that walks a moor is cut into a fell.
     */
    private static final Map<String, Integer> CUTS = Map.of();

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
     * The numbers are the crossing's own: {@code waterTop} is the height of the water's own surface and {@code walkOn}
     * is the height the road is laid at across it - the bed for a ford, the deck for a span. Nothing is measured at lay
     * time beyond the bed a pier is drawn to, because the water a river stands at is the thing most likely to have been
     * made differently by the time a road reaches it, and a crossing that re-read it would put a step at each bank.
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
     * The crossing a column stands on, or null where no span covers it.
     * <p>
     * Asked of the routes themselves rather than of a table of water kept beside them: a span is a
     * {@link Route.Crossing} already - its banks, its deck and its pieces are the crossing - and a road holding its own
     * copy of those numbers is a second place for them to drift apart. Every span answers for every road, because a
     * span is a fact about the water rather than about which road happened to find it.
     * <p>
     * A column is on a crossing when it stands between the span's two banks on the span's own axis and no further out
     * than {@code across} from the line its pieces are stood on. That line is the plan's, and a searched road does not
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
     * A bridge a crossing is stood with, where the standard one will not do: the Last Bridge, and whatever else the
     * country deserves.
     * <p>
     * A span is a {@link Route.Crossing} and carries its own pieces, so a bridge of its own is two names and a block:
     * the abutment and the span between them, read out of the jar's structure folder the way the standard pair are, and
     * the material the pieces' own piers are made of. The pieces are nine columns across, which is the width a span is
     * planned at, and the plan works the rest out from them - how long a piece is, how many of them the water takes - so
     * a pair of another length is a bridge of another length rather than a second plan.
     * <p>
     * {@code support} is not decoration. The pass finds the lowest such block in a column of the span and carries that
     * column down to the bed, so the water runs open underneath rather than through a wall; it has to be a block only
     * the piers are made of - the standard bridge's is oak log under a plank deck - because a support the deck itself is
     * built from would carry every column of the span to the bed.
     *
     * @param name        what the span is called, for the log rather than for a lookup
     * @param at          a block on the water the span crosses, which is the whole of how the span is found, or
     *                    null where the crossing is not known yet: a bridge is listed with its pieces before the
     *                    road it carries has been walked, and a bridge with no block answers no span
     * @param endPiece    the abutment piece, in the jar's structure folder
     * @param middlePiece the span piece between the abutments
     * @param support     the block the pieces' piers are made of, and the block a pier is drawn to the bed in
     */
    public record Bridge(String name, BlockPos at, String endPiece, String middlePiece, Block support) {
    }

    /**
     * How far off a span's own line a bridge's block may stand and still be that span's, in blocks.
     * <p>
     * The block is a place somebody chose on the water rather than a number the plan carries, so it is not held to the
     * plan's line to the block: a bridge named at the middle of a channel reads the same a few blocks either way.
     */
    private static final int BRIDGE_LEEWAY = 8;

    /**
     * The bridges the world is built to, by the water each of them crosses.
     * <p>
     * A crossing is the standard bridge's - nine wide, oak-log piers, {@code bridge_end} and {@code bridge_middle} in
     * the jar - unless a block on its water is named here. A crossing the books give a name to is listed here, and it is
     * stood instead of the standard pair wherever a span covers it. A bridge whose pieces are not in the jar yet is not
     * stood: the pass falls back to the standard pair and says so in the log, so a bridge can be
     * named here before it is built without leaving the road that crosses it unbridged.
     * <p>
     * A span is found by the ground rather than by its section's name. A road over water is cut into sections whose
     * names carry {@code _bridge_<n>}, and n counts from the end the plan walked from: a leg that crosses two rivers
     * has its first span at whichever river its own end met first, so a bridge named for the third crossing of a road
     * would move the moment the plan met another river before it. The water does not move.
     * <p>
     * <b>The Last Bridge is listed, with its pieces and without its crossing.</b> {@code last_end} and
     * {@code last_middle} are the owner's pair for the Last Bridge, where the East-West Road crosses the Mitheithel,
     * and they are in the jar beside the standard pair: an abutment seventeen long and twelve high on its own bank, a
     * span twenty, both nine columns across, and piers of {@code wall_cobbled_dwarven_stone} under a
     * {@code wall_dwarven_stone_bricks} parapet. The block on its water is the one thing left to name. The leg that
     * would have crossed the river left the table on 25 November 2026 and the owner's own road has not been walked, so
     * the entry holds no place at all: it answers no span, every span still stands from the standard pair, and a block
     * on the water his road crosses is the whole of what turns it on. {@code bucklebury_end} and
     * {@code bucklebury_middle} (cobblestone-wall piers) are the pair for the Shire's crossing of the Brandywine and
     * are held for the road to Bucklebury, so they are not in the table at all.
     */
    private static List<Bridge> BRIDGES;

    /**
     * The table, read the first time a span asks for it rather than when this class is loaded.
     * <p>
     * The Last Bridge's piers are made of a block this mod registers, and a table held in an initialiser would ask the
     * registry for it whenever the first line of {@link Roads} happened to run, which is not necessarily after the
     * blocks are in it. One read of the mod's own blocks is cheap, and naming a bridge is still one line here.
     */
    private static List<Bridge> bridges() {
        if (BRIDGES == null) {
            BRIDGES = List.of(
                    new Bridge("Last Bridge", null, "last_end", "last_middle",
                            TolkienBlocks.WALL_COBBLED_DWARVEN_STONE.get()));
        }
        return BRIDGES;
    }

    /**
     * The bridge a span is stood with, or null where it is the standard one.
     * <p>
     * A span covers a bridge's block when the block lies between its banks on the span's own axis and no further from
     * the line its pieces are stood on than half the deck plus {@link #BRIDGE_LEEWAY}: the same reading {@link #water}
     * does for a column, asked once for a bridge rather than a block at a time. A bridge whose crossing is not
     * named yet holds no block, so it covers no span and the standard pair stands.
     */
    public static Bridge bridge(Route.Crossing span) {
        boolean alongX = span.alongX();
        int near = Math.min(span.bank(), span.far());
        int far = Math.max(span.bank(), span.far());
        for (Bridge bridge : bridges()) {
            if (bridge.at() == null) continue;
            int along = alongX ? bridge.at().getX() : bridge.at().getZ();
            if (along < near - 1 || along > far + 1) continue;
            int lateral = (alongX ? bridge.at().getZ() : bridge.at().getX()) - span.line();
            if (Math.abs(lateral) > span.half() + BRIDGE_LEEWAY) continue;
            return bridge;
        }
        return null;
    }

    /**
     * The roads the bands decide, column by column, rather than one kind being laid the whole of them.
     * <p>
     * A settlement's own road is the first: seven blocks of its street inside the wall's band, five where the paving
     * starts, three out in the country, so it widens as it comes in to the gate. A planned road does not take the bands
     * at all - it names its kind when it is planned - so every route here is one width for its whole length.
     */
    private static final Set<String> BANDED = Set.of();

    /** Whether the bands decide a road, column by column, rather than one kind being laid the whole of it. */
    public static boolean banded(Route route) {
        return BANDED.contains(route.id()) || RoadPlan.banded(route.id());
    }

    /**
     * The build a road leaves from, where the road has a name of its own.
     * <p>
     * A road of a build's own is named for the build, and that name is what the save's note of the road is written
     * under and what the build's own road flag is cleared by. A road named for itself says here which build it leaves
     * from, so forgetting the road at the build reaches the build's flag.
     */
    private static final Map<String, String> BUILDS = Map.of(
            // The three roads out of the valley, which leave the city's own ground. Named by the road's two ends
            // because that is what a road's id is: the city's roads were the first to be planned from pins, and their
            // ids are those pins rather than a name. A section of one of these - a bridge, a later dry stretch - carries
            // the road's id with a suffix, which buildOf reads as the same road.
            "tolkienmobs:roads/planned_5200_-3033_4647_-3315", "rivendell",
            "tolkienmobs:roads/planned_5111_-3399_5072_-3221", "rivendell",
            "tolkienmobs:roads/planned_5105_-3413_5605_-3622", "rivendell");

    /**
     * A town a road arrives on, and the block of that town's own ground the road meets: the gate a leg ends at, or the
     * hall front it begins at.
     * <p>
     * The block is what says which end of a road is a town's. A leg over water is cut into a section for every span and
     * every dry stretch between them, and every one of those sections carries the leg's name, so which end of which
     * section actually arrives at a town cannot be read off the id. It can be read off the ground.
     */
    private record Gate(String town, BlockPos at) {
    }

    /** How near a town's own ground a road's end has to stand to be that town's arrival, in blocks. */
    private static final int GATE_REACH = 16;

    /**
     * How far short of a town a road stops while that town is still being raised, in blocks.
     * <p>
     * The ground from there in is the town's: it is laid out of the cut and dressed by the ring round it, and that ring
     * reaches {@link CityCommands#DRAPE_REACH} - thirty-two blocks - past the footprint. A road graded over it while
     * the town is going up is graded against ground that is about to change under it, and the arrival is then taken out
     * with the ring; the pass believes the road is down and never grades that stretch again. Waiting for the town to
     * finish holds the whole road instead, hundreds of blocks of open country the town will never touch. So the road
     * stops a margin past the ring, in country the town leaves as it found it, and the arrival is laid when it stands.
     * <p>
     * The distance is measured from the block the road meets the town on, and the table names a gate or a hall front on
     * the footprint's own edge rather than a block inside the town, which is what makes forty blocks back outside the
     * ring. A road that ended deep inside a town would have to have its hold measured from the footprint instead.
     */
    static final int SHORT_OF_TOWN = 40;

    /**
     * The towns a road runs between, and the ground each of them is met on, by the road it is a section of.
     * <p>
     * A road that starts from a build's door is that build's road and is held by {@link #BUILDS}; a road between two
     * towns is neither town's, and both ends of it arrive on ground that is still being made. Both towns raise
     * themselves out of a cut and dress the ring round it, which is the ground those ends are graded on - so a leg
     * begun while either is going up has its arrival taken out with the ring, and the pass believes the road is down
     * and never grades that stretch again. The measured case was the Shire leg's own arrival (`..._2`), begun at 11:23
     * on 22 November while the village was still rising and never reported laid.
     * <p>
     * The table is empty now. The two legs it named - the road from Bree's hall front to the village's gate, and the
     * road from the vale's junction to the same hall front - were taken out of {@link #NETWORK} at the owner's word on
     * 25 November 2026, and no road left in the table runs between two towns. Nothing else about the hold changes: a
     * leg surveyed between two towns names both of their grounds here again, and the hold is measured from the block it
     * names rather than from the footprint.
     * <p>
     * One end of a leg may name a town and the other none: the leg to Bree named only bree, because its other end, the
     * vale's junction at 4648, -3316, was no town's ground - it left ground that stands and arrived on ground that does
     * not. That is the case {@link #hold} is for, and the reason the table names the ground of an end rather than the
     * ends of the leg.
     */
    private static final Map<String, List<Gate>> TOWNS = Map.of();

    /** The build a road leaves from: its own name where it is that build's road, the build it serves otherwise. */
    public static String build(Route route) {
        String own = buildOf(route.id());
        return own != null ? own : route.id();
    }

    /** The build a route's id belongs to, by the road it is a section of, or null when it serves no build. */
    private static String buildOf(String id) {
        String exact = BUILDS.get(id);
        if (exact != null) return exact;
        for (Map.Entry<String, String> row : BUILDS.entrySet()) {
            if (id.startsWith(row.getKey() + "_")) return row.getValue();
        }
        return null;
    }

    /**
     * Whether the build a road leaves from is still being raised, so the road has nothing to grade against yet.
     * <p>
     * A city is laid a band at a time and its ground is dressed after the last of them, so a road that meets it early
     * grades country that is about to change under it: measured in Road Test 7, the road out of the valley began from
     * the ground it crosses while the city was still going up, and the strip from the first bridge to the road's own
     * end was taken out with the ground the dressing settled afterwards - a gap no second laying filled, because the
     * road believed it was down. So a road of a build's own waits for that build, the way a town's hall does
     * ({@link TownHalls#waits}), and begins from the same ground the moment the build is finished.
     * <p>
     * Only a road that names a build or a town is held: every other road is the country's and has nothing to wait for.
     * A road with a town still going up at one of its ends is not held whole - the pass lays it from the end that
     * stands and stops at the other one's ground ({@link #hold}) - so only a road whose two ends are both still being
     * made has nowhere to work from, and that road waits for both of them.
     */
    public static boolean waits(ServerLevel level, Route route) {
        String own = buildOf(route.id());
        if (own != null && TownHalls.waits(level, own)) return true;
        return hold(level, route, false) != null && hold(level, route, true) != null;
    }

    /**
     * The town at one end of a road that is still being raised, or null when that end has nothing to wait for.
     * <p>
     * Asked of a section's own two ends rather than of the leg it belongs to, because a leg over water is cut into a
     * section for every span and every dry stretch, and only the section that reaches a town is graded on the town's
     * ground. A crossing's ends are banks, so a span is never held: its walls and its abutments are the pass that
     * stands it up, and that pass is the city's own business.
     *
     * @param towardFar which of the section's two ends to ask about
     */
    static Hold hold(ServerLevel level, Route route, boolean towardFar) {
        if (!(route instanceof Route.Riverless dry)) return null;
        int x = towardFar ? dry.farX() : route.pathX();
        int z = towardFar ? dry.farZ() : route.pathZ();
        for (Gate gate : gatesOf(route.id())) {
            if (Math.hypot(x - gate.at().getX(), z - gate.at().getZ()) > GATE_REACH) continue;
            if (TownHalls.waits(level, gate.town())) return new Hold(gate.town(), gate.at());
        }
        return null;
    }

    /** A town at one end of a road that is still being raised, and the ground that road arrives on. */
    record Hold(String town, BlockPos at) {
    }

    /** The towns a route's id runs between, by the road it is a section of: none for a road with no town on it. */
    private static List<Gate> gatesOf(String id) {
        for (Map.Entry<String, List<Gate>> row : TOWNS.entrySet()) {
            if (id.equals(row.getKey()) || id.startsWith(row.getKey() + "_")) return row.getValue();
        }
        return List.of();
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
