package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

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
     * One road the code names: its id, the width it is laid at, the two blocks it runs between, and the bridge its
     * spans are stood from where it has one.
     * <p>
     * The bridge is the word a command names it with - {@code bucklebury} - rather than the pair of pieces, so a line
     * here says what the surveyor said and the pair stays in {@link #bridges()} with the rest of that bridge. A road
     * crosses where the country takes it, so which of its sections is a span is the plan's answer and not this
     * table's; the word is what {@link #bridge(String)} answers on when the plan comes back.
     * <p>
     * A word the table does not hold plans the standard pair, exactly as a span with no name gets it. That is the same
     * answer the command gives a leg it crosses no water with, and it is the quieter of the two on purpose: this table
     * is read as the class loads, and a mistyped bridge should plan a road of oak rather than stop a world from
     * starting.
     */
    private record Named(String id, Kind kind, boolean town, BlockPos from, BlockPos to, String bridge) {
    }

    /** A block of the drawing named by its two coordinates: the height is the plan's business, not the road's. */
    private static BlockPos at(int x, int z) {
        return new BlockPos(x, 0, z);
    }

    /**
     * The network the world is laid with: every road a surveyor has pinned, as the two ends it was pinned between.
     * <p>
     * <b>Empty since 26 November 2026, and emptied on purpose.</b> A fresh world is being generated with the Shire, Bree
     * and Rivendell re-based and the Brandywine crossing moved, so nearly every road that was laid out against the old
     * ground has to be rerouted - and a road whose end moves is a different road, with a different id, because an id is
     * its two ends. Fourteen stood here: the hold's road out of Gundabad's gate down to the old forest road; the old
     * forest road east of Mirkwood on to Glóin's camp; the four legs of the high pass; Rivendell's road to the meeting
     * plaza and the plaza's on to Glóin's camp; the road down to Elladan's camp; the short shoulder leg beside
     * Rivendell's south gate; and the three stretches the owner walked between Bree and the Shire on 25 November. They
     * are written down in the changelog of that date and in {@code docs/arda-roads.md} rather than left here behind a
     * switch: a table is what a fresh world plans, and a line left in it lays ground nobody asked for.
     * <p>
     * <b>A road is added back one line at a time, in the order the owner walks them.</b> One {@link Named} entry is a
     * road whose two ends are known, and {@link RoadPlan} works out everything else - the way it takes, the bends, where
     * it crosses water and how wide the span is - from the drawing. There is no switch in front of the table any more:
     * it was {@code NETWORK_OPEN}, off from 24 November while Rivendell was re-scouted, and a switch is one more thing
     * to remember before a road added here lays anything. An empty table plans nothing; the first entry plans that road
     * and no other, which is what step by step means.
     * <p>
     * Declared above {@link #ROUTES} on purpose: the list is built once when this class loads, and a road written below
     * it would not be in the list at all.
     */
    private static final List<Named> NETWORK = List.of(
            // The road the owner walked on 27 November 2026, out of 868, -2518 and away north-west to 597, -2787,
            // which he added to this table himself (`a945a485`): three wide of the country's own surface, and the
            // save he walked it in, `Bucklebury - Redux`, holds it laid across 297 stations. It is written from its
            // far end so that it *ends* on the junction the arm below begins on: the three lines are three roads
            // meeting there - his word on the day, and each of them its own two ends - but with this one the other
            // way round the first two both *began* on 868, -2518, and a fresh world read the pair as two roads out
            // of one block rather than one line through it (28 November 2026).

            /* Hobbiton -> Bucklebury */
            new Named("tolkienmobs:roads/planned_597_-2787_868_-2518", Kind.WILD, false, at(597, -2787),
                    at(868, -2518), null),
            // The two arms on to the bridge, written in the order the road is walked: out of the junction at
            // 868, -2518 and away to the bridge, each beginning on the block the one before it ends on. They read
            // the other way round until 28 November 2026 - from the bridge back to the junction - and a fresh
            // world's sections counted their first stretch from the bridge end because of it.
            //
            // **The junction's own water is why arm two bends west.** The owner's word on 28 November 2026 is
            // that the line from Hobbiton to Bucklebury has one bridge on it and only one - the stone one before
            // the town - because he placed the route to cross nothing else. The drawing puts a second water
            // across any line drawn from 868, -2518 toward the bridge: the Hobbiton side's own, x 864 to 900 and
            // wider, running from the village's south end at z -2866 up to where it meets the town's channel at
            // z -2630, and thirty to forty columns across where a straight line meets it. The walk would not pay
            // the toll for that, and a lane is sixteen blocks of the drawing - so a one-column edge of a channel
            // is invisible to it - and it did what a lane walk does: ran the line down the water's western edge,
            // waded the last column of it for eighteen blocks, and left the dense pass to cut that as an
            // eighteen-column span at x 863. A fresh world stood it (`Bucklebury Ferry`, 28 November 2026) - the
            // second bridge, over water the road was meant to pass round. **820, -2900 is round it:** the map is
            // dry from z -2872 down and west of x 808, so both halves read as one dry section each (`868, -2518`
            // to `820, -2900`, and `820, -2900` to `1140, -3131`) and the only span on the whole line is the
            // stone one. The bend is 809 blocks against the straight line's 671 - 138 longer - and the
            // alternative is the oak bridge standing at x 863.
            //
            // The crossing arm runs from 1140, -3131 to 1380, -3200: out of the block arm two ends on, across the
            // river once, and on to Bucklebury's own west edge. It read 1203, -3131 to 1271, -3131 when the owner
            // first pinned it, and 1140 to 1320 when it was first lengthened; both planned badly, and the second
            // is what a fresh world laid on 28 November 2026 (`Bucklebury Ferry`). **Two ends alone do not make a
            // crossing:** a span takes the sixteen blocks either side of its banks into itself (`EXIT_REACH`), so
            // an end ten blocks off the water plans its stretch of road *backwards* - that was the 1203 end, seven
            // stations running back to 1197 - and that is why the west end stands 75 blocks off the near bank.
            //
            // **A due-east line crosses this river twice.** The channel runs north-north-east - its middle is
            // x 1237 at z -3145 and x 1273 at z -3061 - so a leg along z -3131 runs *along* it, and the walk,
            // which prices water at 220 a lane times its depth and the bank beside it at 400 a step, follows the
            // channel: it crossed at z -3108 and again at z -3085, ran the road between them back west (1283,
            // -3108 to 1215, -3085), and stood a `bucklebury` span on each - five sections, two stone bridges 23
            // blocks apart, and a road that reads as two. The word is the road's, so every span it makes is the
            // stone pair. Aimed at the town the leg crosses once: `tools/sampleprobe` plans three sections - a
            // road in from 1140, -3131, **one** span of thirty-nine columns between the banks at 1239 and 1277 on
            // z -3212 at deck 85, and a road on to 1380, -3200. Length on the old row mends nothing: 1320 gives
            // the same two spans and 1400 still gives two.
            new Named("tolkienmobs:roads/planned_1166_-3126_868_-2518", Kind.WILD, false, at(868, -2518),
                    at(820, -2900), null),
            new Named("tolkienmobs:roads/planned_1140_-3131_820_-2900", Kind.WILD, false, at(820, -2900),
                    at(1140, -3131), null),
            new Named("tolkienmobs:roads/planned_1271_-3108_1166_-3126", Kind.WILD, false, at(1140, -3131),
                    at(1380, -3200), "bucklebury"));

    // What is not in the table, and why - written beside it rather than left to be guessed at:
    // `planned_1284_-3128_1166_-3126` was planned from 1284, -3128 to the same junction with no bridge word, so its
    // span is the standard oak pair, and it crosses the same river 23 blocks north of the stone one (its banks at
    // z -3108, the stone span's at -3131). The owner's word on the day is to remove it: a road that is not in this
    // table is a road a fresh world does not plan, and a world that has already walked it keeps what it laid, because
    // nothing in the mod lifts a road. The line that stood here before the 597 one, `planned_1200_-3500_871_-2510`,
    // went with the same edit - he replaced it - and that save still holds it laid, 307 stations in its `_2` with its
    // span begun and not finished.

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
     * A road whose ends are not pinned plans nothing and is not in the list, which is the same answer as having no
     * entry for it at all; with the table empty, the list is what the world's own plan store adds and nothing else.
     */
    private static List<Route> network(List<Route> walked) {
        List<Route> all = new ArrayList<>(walked);
        for (Named road : NETWORK) {
            Bridge stood = road.bridge() == null ? null : bridge(road.bridge());
            all.addAll(RoadPlan.of(road.id(), road.kind(), road.town(), road.from(), road.to(), stood).sections());
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
     * @param piers       the block the pieces' piers are made of, and the block a pier is drawn to the bed in,
     *                    read when a span is stood ({@link #support()}) rather than when this table is built, because
     *                    the Last Bridge's is a block this mod registers and the table is built as the class loads
     */
    public record Bridge(String name, BlockPos at, String endPiece, String middlePiece, Supplier<Block> piers) {
        /** The block a pier is drawn to the bed in: the pieces' own, read now rather than with the table. */
        public Block support() {
            return piers.get();
        }
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
     * <b>Bucklebury's bridge is the span on the road the owner walked from Bree to the Shire.</b>
     * {@code bucklebury_end} and {@code bucklebury_middle} - cobblestone-wall piers, which is the support the entry
     * names - were drawn for the Shire leg's crossing of the Brandywine and held in the jar while that leg was the
     * mod's. That crossing is no longer the mod's, and his own road meets the water at 438, -49: between banks 456
     * and 420 on z -49 at deck 65, the section the store calls {@code planned_480_-6_381_-5_bridge_1}. The pair is
     * seventeen, twenty and seventeen columns end to end, drawn for a fifty-four-column crossing, and this one is
     * thirty-seven - so the plan lays both ends and one middle, and seventeen of that middle's columns stand inside
     * the far abutment. The deck row is 65 either way and the span stands, but it is not the fit the crossing it was
     * drawn for had.
     * <p>
     * <b>The road the owner walked out of 1271, -3108 names the same pair by word</b>, so the stone bridge is stood
     * on the river west of the town too, wherever that water lies, without a block on it here - his plan of 27
     * November 2026 carries `bucklebury_end` and `bucklebury_middle` for that crossing, and the line in
     * {@link #NETWORK} carries the word that writes them.
     * <p>
     * <b>The Last Bridge is listed, with its pieces and without its crossing.</b> {@code last_end} and
     * {@code last_middle} are the owner's pair for the Last Bridge, where the East-West Road crosses the Mitheithel,
     * and they are in the jar beside the standard pair: an abutment seventeen long and twelve high on its own bank, a
     * span twenty, both nine columns across, and piers of {@code wall_cobbled_dwarven_stone} under a
     * {@code wall_dwarven_stone_bricks} parapet. The block on its water is the one thing left to name. The leg that
     * would have crossed the river left the table on 25 November 2026 and the owner's own road has not been walked
     * over that water at all, so the entry holds no place: it answers no span, and putting a block on the water the
     * road crosses there is the whole of what turns it on.
     * <p>
     * <b>A bridge is found by the water it is named at, or by the pieces a plan was given.</b> The block is how a bridge
     * this table holds is recognised from the ground; a road planned with one named on the command - {@code /tmroad
     * build 480 -6 main bucklebury} - writes that bridge's two pieces into every span it makes instead, so a span is
     * the bridge wherever the water it crosses happens to lie, and the block keeps finding it for a road that was
     * planned without the word.
     */
    private static List<Bridge> BRIDGES;

    /**
     * The table, read the first time a bridge is asked about rather than when this class is loaded.
     * <p>
     * A road in {@link #NETWORK} may name a bridge, and that table is planned as this class loads, so this one is read
     * at class load after all - but only its names, its blocks and its pieces. The Last Bridge's piers are a block this
     * mod registers, and a table whose entries held the block would ask the registry whenever the first line of
     * {@link Roads} happened to run, which is not necessarily after the blocks are in it: the block is therefore held
     * as a supplier and read when a span is stood, so a named road here can say which bridge it is without that.
     * Naming a bridge is still one line either way.
     */
    private static List<Bridge> bridges() {
        if (BRIDGES == null) {
            BRIDGES = List.of(
                    new Bridge("Bucklebury", at(438, -49), "bucklebury_end", "bucklebury_middle",
                            () -> Blocks.COBBLESTONE_WALL),
                    new Bridge("Last Bridge", null, "last_end", "last_middle",
                            () -> TolkienBlocks.WALL_COBBLED_DWARVEN_STONE.get()));
        }
        return BRIDGES;
    }

    /**
     * A bridge by the word a command names it with: its own name with the spaces taken out, in either case.
     * <p>
     * {@code bucklebury} is {@code Bucklebury} and {@code lastbridge} is the {@code Last Bridge}; a word that is only
     * the start of one name answers it too ({@code last}), because the name is what the log says and the word is what a
     * hand types. A name that is exactly one of them outranks a name it merely begins, so a shorter bridge is never
     * lost to a longer one that happens to start the same way. Nothing is guessed: a word this table does not hold
     * answers null, and the caller refuses the command rather than laying the standard bridge under a stone one's name.
     */
    public static Bridge bridge(String name) {
        if (name == null) return null;
        String asked = name.toLowerCase(Locale.ROOT).replace(" ", "");
        if (asked.isEmpty()) return null;
        for (Bridge bridge : bridges()) {
            if (word(bridge).equals(asked)) return bridge;
        }
        for (Bridge bridge : bridges()) {
            if (word(bridge).startsWith(asked)) return bridge;
        }
        return null;
    }

    /** A bridge's name as the word that names it. */
    private static String word(Bridge bridge) {
        return bridge.name().toLowerCase(Locale.ROOT).replace(" ", "");
    }

    /** The words a bridge can be named by, for a command that was given one the table does not hold. */
    public static String bridgeNames() {
        List<String> words = new ArrayList<>();
        for (Bridge bridge : bridges()) words.add(word(bridge));
        return String.join(", ", words);
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
        // A span planned with a bridge's pieces is that bridge's, whatever water it found: the pieces are the
        // surveyor's choice, written into the plan and kept in the world's store, so they outrank the block the table
        // names. Without this, naming a bridge at the command would be undone the moment the span covered another
        // crossing the table holds, and a road re-planned over water it had crossed before would change its bridge.
        for (Bridge bridge : bridges()) {
            if (bridge.endPiece().equals(span.endPiece()) && bridge.middlePiece().equals(span.middlePiece())) {
                return bridge;
            }
        }
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
            // Empty with the network on 26 November 2026: the three roads out of the valley were here, under the ids
            // their own pinned ends gave them, and those ids are gone with the roads. A road re-surveyed out of
            // Rivendell is a new pair of ends and a new entry, and until it is walked there is nothing to name.
            );

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
     * The two legs this named were taken out of {@link #NETWORK} at the owner's word on 25 November 2026, and the
     * road he walked in their place names both of their grounds here again - under the two stretches that end on
     * them, because a road is cut into sections and only one that reaches a town is graded on its ground: Bree's
     * hall front at 1031, -34 under the first, and the village gate at -149, -382 under the last. Its middle stretch
     * meets no town, so it is not here. Nothing else about the hold changes: it is measured from the block the table
     * names rather than from the footprint.
     * <p>
     * One end of a leg may name a town and the other none: the leg to Bree named only bree, because its other end, the
     * vale's junction at 4648, -3316, was no town's ground - it left ground that stands and arrived on ground that does
     * not. That is the case {@link #hold} is for, and the reason the table names the ground of an end rather than the
     * ends of the leg.
     */
    private static final Map<String, List<Gate>> TOWNS = Map.of(
            // Empty with the network on 26 November 2026: the two ends named here were Bree's hall front at 1031, -34
            // and the old village's east side at -149, -382, on the two legs of the road the owner walked between them.
            // Both legs are out of the table, and the village those ends met has moved 2,400 blocks south-east, so the
            // next leg to it names its own gate. The hold itself is unchanged: it is measured from the block a road's
            // own end stands on, not from the footprint.
            );

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
