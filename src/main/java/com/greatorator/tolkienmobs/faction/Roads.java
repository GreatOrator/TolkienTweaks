package com.greatorator.tolkienmobs.faction;

import net.minecraft.core.BlockPos;

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
     * The hold stands at 5516, 187, -7112 in Arda at the head of its own valley, and what the road leaves into is
     * country nobody has walked: the nearest road of any age is the chapter's own at the far end of the old forest
     * road, three and a half thousand blocks south. Both ends are the scout's to pin - {@link #GUNDABAD_GATE} and
     * {@link #GUNDABAD_JOIN}, null until that walk is made - and everything between them is planned: the hold's road
     * is the first route that carries no marks at all, and the two blocks are the whole of the survey it needs.
     */
    public static final String GUNDABAD_ROAD = "tolkienmobs:roads/gundabad_road";

    /**
     * The Gundabad road's two ends, as a scout will pin them: the block at the foot of the hold's own way out, and the
     * mark where the road meets the road it joins.
     * <p>
     * Both are null until that walk is made, and while either is null the route is not in {@link #ROUTES} - it cannot
     * be, since a route is two ends and two ends invented are a road laid across country nobody surveyed. Putting the
     * two marks in these two lines is the whole of what the road is waiting for; everything else about it is worked
     * out from them - its name, the way it goes, where it crosses water and how wide it is laid.
     * <p>
     * They are declared above {@link #ROUTES} on purpose: the list is built once when this class loads, and a mark
     * written below it would still be null at that moment, which is a road that never appears and never says why.
     */
    private static final BlockPos GUNDABAD_GATE = null;
    private static final BlockPos GUNDABAD_JOIN = null;

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
    public static final List<Route> ROUTES = new CopyOnWriteArrayList<>(withGundabad(List.of()));

    /** The sections this session planned at a build, so another world's can be taken back out of the list. */
    private static final Set<String> PLANNED = ConcurrentHashMap.newKeySet();

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

    /** The roads as they stand, with the hold's road planned on the end once its two marks are pinned. */
    private static List<Route> withGundabad(List<Route> walked) {
        List<Route> all = new ArrayList<>(walked);
        all.addAll(RoadPlan.of(GUNDABAD_ROAD, Kind.WILD_WIDE, false, GUNDABAD_GATE, GUNDABAD_JOIN).sections());
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
    private static final Map<String, String> BUILDS = Map.of();

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
