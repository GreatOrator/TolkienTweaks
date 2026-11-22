package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.SignPostArmBlock;
import com.greatorator.tolkienmobs.block.custom.SignPostDoubleArmBlock;
import com.greatorator.tolkienmobs.block.custom.entity.SignPostArmBlockEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * The fingerposts on the roads.
 * <p>
 * A board is only worth a plinth when it stands where a traveller has to choose, so the network is posted where its
 * lines are long enough to need it: <b>one post every {@link #SPACING} blocks of a laid road</b>, and <b>one at each
 * junction</b> - the places a road ends on another road, whether it stops at the other's end or runs into its side.
 * The arms name the towns that lie that way, and their distances, which is what the arm block already does for itself:
 * {@link SignPostArmBlockEntity#setDestination} measures from the board, so nothing here writes a number.
 * <p>
 * Where a road runs is read off the same plan the pass is laying: each section's own line, its bends and its far end,
 * in the order the plan made them. A span is not walked over: a post on a bridge is a post in the water's way, and the
 * two dry sections either side are joined across it instead. Nothing here reads the drawing, so a road that was never
 * laid is never posted - {@link RoadCommands#down} is asked, and a road whose note does not stand on its own ground is
 * left alone.
 * <p>
 * The post itself is the five blocks a fingerpost is: a plinth, {@link #SHAFTS} courses of shaft, and the cap, with
 * the arms in the cells beside the <b>top</b> course, where a fingerpost's boards are and where a traveller looks for
 * them; a course whose cell is already taken is stepped down to rather than the arm being dropped. Each arm points
 * away from the post along the way it speaks for - the block's own facing is the cell's side, and the blockstate turns
 * the model so its arrow points that way. One town down a road gets the single arm; two get the double, whose two
 * boards carry a destination each - and a third town down the same road is left off, since an arm carries two boards
 * and no more.
 * <p>
 * The post stands <b>beside</b> the road and not on it. The verge is not a width guessed from the road's kind: the
 * pass widens near a town, widens again at a turn, and wears its edge ragged in the country, so the column a post is
 * stood on is chosen by measuring it against every line in the network - its own road and every road that runs near
 * the junction it stands at - and taking the nearest one clear of them all. A post in the middle of a crossing is a
 * post a cart hits, and one five blocks off a 3-wide road was standing in the other road of the join.
 * <p>
 * A post is planted once. The plinth is the marker: a target with one already standing beside it is answered and not
 * planted again, so a world may be asked for its posts on every start, on a clock, and by hand, and the same post is
 * never stacked twice. A join found from two pairs of ends is one join and is folded into one target, and a thousand
 * block mark within {@link #GUARD} blocks of a junction is that junction's post and is not planted beside it. A
 * target whose chunk is not loaded is not answered either - it waits for a pass that finds it loaded, which is why the
 * clock is there rather than a single sweep at the world's start.
 */
@EventBusSubscriber(modid = TolkienMobsMain.MODID)
public final class SignPosts {
    /** Builders and gamemasters, as the road's own hand answers to. */
    private static final Predicate<CommandSourceStack> MAY_BUILD = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** How far apart the posts on one road stand, measured along the road, in blocks. */
    private static final int SPACING = 1000;
    /**
     * How far along a road its first post stands, in blocks.
     * <p>
     * The marks a thousand blocks apart are for a traveller already on the road; the one who has just walked out of a
     * town does not yet know which way the road runs or where it goes. So every laid road is posted once near each of
     * its own ends as well - far enough out to be outside the town's own ground and in sight of the road, close enough
     * that a player leaving the gate meets it before the road has carried them out of sight of the walls.
     */
    private static final int START = 96;
    /**
     * How near two roads have to come before they are one junction, in blocks.
     * <p>
     * The network's ends are pinned blocks rather than surveyed corners, and a plan runs its line from the pin rather
     * than through it, so the two ends of a join can stand a few blocks apart. A dozen is wider than any of those and
     * narrower than the shortest road in the network.
     */
    private static final int MEET = 12;
    /**
     * How near a junction a thousand-block mark has to fall before it is that junction's post, in blocks.
     * <p>
     * A crossing is the place a traveller has to choose, and a second post a few paces down the road from it says the
     * same thing twice - the Gundabad crossing stood two posts ten blocks apart for this reason. Half a dozen posts
     * along one road are set at no cost, so the guard is wide enough to be certain rather than thrifty.
     */
    private static final int GUARD = 32;
    /**
     * How far past a road's own edge a post stands, in blocks.
     * <p>
     * Three rather than one: a road's edge is where its own band ends, and a plinth on the edge is a plinth a cart
     * scrapes. It is also the width that keeps a post out of a road that joins at a shallow angle, where a column a
     * couple of blocks off one line is well inside the other's band.
     */
    private static final int ASIDE = 3;
    /**
     * How far out a verge is looked for before a target is given up on, in blocks.
     * <p>
     * A join where two roads run at a shallow angle has a wide wedge between them, and the post belongs outside it
     * rather than in the mouth of it; a dozen blocks is wider than any wedge in the network and keeps the post within
     * sight of the crossing it speaks for.
     */
    private static final int CLEAR = 12;
    /**
     * How wide a sector one arm speaks for, as the cosine of its half-angle: sixty degrees either side of its line.
     * <p>
     * A board names the towns a traveller would reach by walking the way the arm points, and a town is down that road
     * when it lies within the sector the road opens onto. A town is given to <b>one</b> arm - the one it lies most
     * nearly down - because a post at a crossing has arms for every road that meets there and two towns a few degrees
     * apart belong to different roads: Rivendell was going on the Gundabad arm because both lay west of the join.
     */
    private static final double SECTOR = 0.5D;
    /** How near a town a post may stand and still point at it: closer than this, the post is in the town. */
    private static final int OWN = 64;
    /** How many towns one arm can name: the double arm carries two boards, and the single one. */
    private static final int BOARDS = 2;
    /** The courses of shaft between the plinth and the cap: a post is five blocks tall. */
    private static final int SHAFTS = 3;
    /** How often a world looks for roads that are owed posts, in ticks. Twenty seconds. */
    private static final int EVERY = 400;
    /** The eight columns around a point one block further out, the four square ones before the four diagonal ones. */
    private static final int[][] AROUND = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    private static final Logger LOGGER = LoggerFactory.getLogger(SignPosts.class);

    /** The roads this run has finished posting, so a finished road is not walked again every twenty seconds. */
    private static final Set<String> POSTED = ConcurrentHashMap.newKeySet();

    private SignPosts() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmsign")
                .requires(MAY_BUILD)
                .then(Commands.literal("place").executes(SignPosts::placeCommand)));
    }

    /**
     * Posts whatever the loaded world is owed, now and then.
     * <p>
     * The roads are laid by passes that run as their ground loads, so there is no one moment at which a world has all
     * its roads: a sweep at the start would post the two roads that happened to be down and leave the rest. The clock
     * is what makes this a place that keeps up rather than an event: every {@link #EVERY} ticks the roads that are down
     * are asked again, posts already standing are answered and not stacked, and a road whose every target has been
     * settled is not looked at again this run.
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % EVERY != 0) return;
        for (ResourceKey<Level> key : List.of(TolkienDimensions.ARDA, Level.OVERWORLD)) {
            ServerLevel world = event.getServer().getLevel(key);
            if (world == null) continue;
            int planted = placeAll(world);
            if (planted > 0) LOGGER.info("Sign posts: {} planted in {}", planted, world.dimension().toString());
        }
    }

    /** The hand's way in: post whatever the world is owed, and say how much of it was done. */
    private static int placeCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        POSTED.clear();
        int planted = placeAll(level);
        int roads = lines(level).size();
        source.sendSuccess(() -> Component.literal("Sign posts: " + planted + " planted on " + roads + " laid road"
                + (roads == 1 ? "" : "s") + " here. A post is planted once, so a second place plants only what is owed."),
                true);
        return planted;
    }

    /**
     * Every post one world is owed, planted.
     * <p>
     * Two kinds of target, one way of standing them: the thousand-block marks along each laid road, and the junctions
     * where roads meet. Both are settled through {@link #plant}, so a post is the same five blocks wherever it stands,
     * and both are answered by the plinth that is already there.
     *
     * @return how many posts were planted by this pass
     */
    static int placeAll(ServerLevel level) {
        List<Line> lines = lines(level);
        if (lines.isEmpty()) return 0;
        int planted = 0;
        List<Junction> junctions = junctions(lines);
        for (Junction junction : junctions) {
            planted += plant(level, lines, junction.x, junction.z, junction.ways, junction.half);
        }
        for (Line line : lines) {
            if (POSTED.contains(key(level, line.road()))) continue;
            boolean settled = true;
            for (int[] mark : spaced(line)) {
                // A mark beside a crossing is that crossing's post, and two posts ten paces apart say one thing twice.
                if (byJunction(junctions, mark[0], mark[1])) continue;
                int[] way = new int[]{mark[2], mark[3]};
                // Both ways along the road are spoken for: a traveller arrives from one and leaves by the other.
                List<int[]> ways = List.of(way, new int[]{-way[0], -way[1]});
                int done = plant(level, lines, mark[0], mark[1], ways, line.half());
                if (done < 0) settled = false;
                planted += Math.max(0, done);
            }
            if (settled) POSTED.add(key(level, line.road()));
        }
        return planted;
    }

    /** Whether a mark falls close enough to a junction to be that junction's post rather than one of its own. */
    private static boolean byJunction(List<Junction> junctions, int x, int z) {
        for (Junction junction : junctions) {
            if (Math.hypot(junction.x() - x, junction.z() - z) <= GUARD) return true;
        }
        return false;
    }

    /** A world and a road, so a road posted in one world is not taken as posted in another. */
    private static String key(ServerLevel level, String road) {
        return level.dimension().toString() + "|" + road;
    }

    /**
     * The roads this world has laid, each as the line its dry sections run along.
     * <p>
     * A road is the sections that share its name, in the order the plan made them, and only a road that is down is
     * here at all: a line nobody has graded is a line no traveller walks, and a post beside it would be a post in open
     * country pointing at a road that is not there. The ends are kept with the line because a junction is an end
     * meeting another road, and the half-width because a post is kept clear of the band's own edge - the widest
     * section's, since a road that widens near a town is that wide wherever a post might stand.
     */
    private static List<Line> lines(ServerLevel level) {
        Set<String> roads = new java.util.LinkedHashSet<>();
        for (Route route : Roads.ROUTES) roads.add(Roads.roadOf(route.id()));
        List<Line> lines = new ArrayList<>();
        for (String road : roads) {
            List<Route> sections = Roads.sections(road);
            if (sections.isEmpty()) continue;
            boolean laid = false;
            int half = 1;
            List<int[]> points = new ArrayList<>();
            for (Route section : sections) {
                if (section instanceof Route.Crossing) continue;
                if (!RoadCommands.down(level, section)) continue;
                laid = true;
                Route.Riverless dry = (Route.Riverless) section;
                if (points.isEmpty()) points.add(new int[]{dry.pathX(), dry.pathZ()});
                for (BlockPos bend : Roads.bends(section)) points.add(new int[]{bend.getX(), bend.getZ()});
                points.add(new int[]{dry.farX(), dry.farZ()});
                Roads.Kind kind = Roads.kind(section);
                if (kind != null) half = Math.max(half, kind.half());
            }
            if (!laid || points.size() < 2) continue;
            lines.add(new Line(road, points, half));
        }
        return lines;
    }

    /** One laid road as posting needs it: its name, the line its dry sections run along, and the band's half-width. */
    private record Line(String road, List<int[]> points, int half) {
    }

    /**
     * The marks a road is posted at: every {@link #SPACING} blocks along it.
     * <p>
     * Measured along the line rather than as the crow flies, because a road that switchbacks is longer than the ground
     * it crosses and a spacing taken from the map would put two posts on one bend and none on the next. The point of
     * each mark carries the way the line is going there, which is what the two arms are turned on.
     *
     * @return x, z, and the line's direction there, one mark per thousand blocks
     */
    private static List<int[]> spaced(Line line) {
        List<int[]> marks = new ArrayList<>();
        // The ends first, before the marks along the road: whoever leaves a town with no idea what lies up the road is
        // the traveller a fingerpost is for, and a mile is too far to walk to find out.
        int[] leaving = along(line, START);
        if (leaving != null) marks.add(leaving);
        double span = length(line);
        if (span > START * 2) {
            int[] arriving = along(line, span - START);
            if (arriving != null) marks.add(arriving);
        }
        double walked = 0.0D;
        double next = SPACING;
        List<int[]> points = line.points();
        for (int at = 1; at < points.size(); at++) {
            int[] from = points.get(at - 1);
            int[] to = points.get(at);
            int dx = to[0] - from[0];
            int dz = to[1] - from[1];
            double length = Math.hypot(dx, dz);
            if (length < 0.5D) continue;
            while (walked + length >= next) {
                double along = (next - walked) / length;
                marks.add(new int[]{(int) Math.round(from[0] + dx * along), (int) Math.round(from[1] + dz * along),
                        to[0] - from[0], to[1] - from[1]});
                next += SPACING;
            }
            walked += length;
        }
        return marks;
    }

    /** How long a road's line is, in blocks. */
    private static double length(Line line) {
        double length = 0.0D;
        List<int[]> points = line.points();
        for (int at = 1; at < points.size(); at++) {
            length += Math.hypot(points.get(at)[0] - points.get(at - 1)[0], points.get(at)[1] - points.get(at - 1)[1]);
        }
        return length;
    }

    /**
     * The point a distance along a road from its first end, and the way the road runs there.
     * <p>
     * Measured along the line rather than across the ground, the same way the thousand-block marks are, so a road that
     * switchbacks is posted beside the stretch it leaves the town on rather than in a field a bend away. The point keeps
     * the direction of the stretch it fell on, which is what the arms are turned on.
     *
     * @return x, z and the line's direction there, or null when the road is shorter than the distance
     */
    @Nullable
    private static int[] along(Line line, double distance) {
        double walked = 0.0D;
        List<int[]> points = line.points();
        for (int at = 1; at < points.size(); at++) {
            int[] from = points.get(at - 1);
            int[] to = points.get(at);
            int dx = to[0] - from[0];
            int dz = to[1] - from[1];
            double length = Math.hypot(dx, dz);
            if (length < 0.5D) continue;
            if (walked + length >= distance) {
                double part = (distance - walked) / length;
                return new int[]{(int) Math.round(from[0] + dx * part), (int) Math.round(from[1] + dz * part), dx, dz};
            }
            walked += length;
        }
        return null;
    }

    /**
     * Where two roads meet.
     * <p>
     * Read from the roads' own ends rather than from the junctions the network was planned at, because the network
     * carries only the ends a surveyor pinned: three of the roads of the vale end on the block 4647, -3315, and the
     * Gundabad road ends on the old forest road's line rather than on its end. Both are junctions to a traveller, so
     * both are found - an end against every other road's ends, and an end against every other road's line. The ways a
     * junction speaks for are the ways its roads leave it, which is what its arms are turned on. One crossing found
     * twice - an end against an end and the same end against a line - is folded into one target.
     */
    private static List<Junction> junctions(List<Line> lines) {
        List<Junction> found = new ArrayList<>();
        for (int a = 0; a < lines.size(); a++) {
            for (int b = 0; b < lines.size(); b++) {
                if (a == b) continue;
                Line one = lines.get(a);
                Line other = lines.get(b);
                int half = Math.max(one.half(), other.half());
                for (int[] end : List.of(first(one), last(one))) {
                    for (int[] far : List.of(first(other), last(other))) {
                        if (Math.hypot(end[0] - far[0], end[1] - far[1]) > MEET) continue;
                        junction(found, end[0], end[1], half, away(one, end), away(other, far));
                    }
                    int[] met = onLine(other.points(), end);
                    if (met == null || Math.hypot(end[0] - met[0], end[1] - met[1]) > MEET) continue;
                    junction(found, met[0], met[1], half, away(one, end), along(other.points(), met));
                    junction(found, met[0], met[1], half, away(one, end), opposite(along(other.points(), met)));
                }
            }
        }
        return fold(found);
    }

    /** A place roads meet: where it is, the ways its roads leave it, and the widest band among them. */
    private record Junction(int x, int z, List<int[]> ways, int half) {
    }

    /**
     * Folds the joins that stand within {@link #GUARD} of each other into one, until no two are that near.
     * <p>
     * The same crossing is found more than once - by its ends and by a road's line - and a road's plan runs from its
     * pin rather than through it, so the two readings can stand a few blocks apart; the Gundabad join was read that
     * way and stood two posts. Each fold takes the first-kept position and the wider band, and the ways of both, and
     * the guard is the same one that keeps a mark off a junction, so no crossing can be posted twice.
     */
    private static List<Junction> fold(List<Junction> found) {
        List<Junction> kept = found;
        for (int pass = 0; pass < 8; pass++) {
            List<Junction> folded = new ArrayList<>();
            for (Junction junction : kept) {
                boolean joined = false;
                for (int at = 0; at < folded.size(); at++) {
                    Junction one = folded.get(at);
                    if (Math.hypot(one.x() - junction.x(), one.z() - junction.z()) > GUARD) continue;
                    List<int[]> ways = new ArrayList<>(one.ways());
                    for (int[] way : junction.ways()) if (!same(ways, way)) ways.add(way);
                    folded.set(at, new Junction(one.x(), one.z(), ways, Math.max(one.half(), junction.half())));
                    joined = true;
                    break;
                }
                if (!joined) folded.add(junction);
            }
            if (folded.size() == kept.size()) return folded;
            kept = folded;
        }
        return kept;
    }

    /** Adds a way to the junction near this point, or starts one there. */
    private static void junction(List<Junction> found, int x, int z, int half, int[]... ways) {
        for (int at = 0; at < found.size(); at++) {
            Junction junction = found.get(at);
            if (Math.hypot(junction.x() - x, junction.z() - z) > GUARD) continue;
            List<int[]> joined = new ArrayList<>(junction.ways());
            for (int[] way : ways) if (!same(joined, way)) joined.add(way);
            found.set(at, new Junction(junction.x(), junction.z(), joined, Math.max(junction.half(), half)));
            return;
        }
        List<int[]> list = new ArrayList<>();
        for (int[] way : ways) if (!same(list, way)) list.add(way);
        found.add(new Junction(x, z, list, half));
    }

    /** Whether a list of ways already holds this one: an array compares by identity, so a way is compared by value. */
    private static boolean same(List<int[]> ways, int[] way) {
        for (int[] known : ways) {
            if (known[0] == way[0] && known[1] == way[1]) return true;
        }
        return false;
    }

    /** A road's two ends. */
    private static int[] first(Line line) {
        return line.points().get(0);
    }

    private static int[] last(Line line) {
        return line.points().get(line.points().size() - 1);
    }

    /** The way a road runs from one of its ends, so an arm at that end points along it. */
    private static int[] away(Line line, int[] end) {
        int[] other = Math.hypot(first(line)[0] - end[0], first(line)[1] - end[1]) < 1.0D ? last(line) : first(line);
        return step(end, other);
    }

    /** The point of another road's line nearest a block, or null for a line with no length. */
    private static int[] onLine(List<int[]> points, int[] at) {
        int[] best = null;
        double nearest = Double.MAX_VALUE;
        for (int index = 1; index < points.size(); index++) {
            int[] from = points.get(index - 1);
            int[] to = points.get(index);
            double dx = to[0] - from[0];
            double dz = to[1] - from[1];
            double length = dx * dx + dz * dz;
            double along = length == 0.0D ? 0.0D
                    : Math.clamp(((at[0] - from[0]) * dx + (at[1] - from[1]) * dz) / length, 0.0D, 1.0D);
            int x = (int) Math.round(from[0] + dx * along);
            int z = (int) Math.round(from[1] + dz * along);
            double away = Math.hypot(at[0] - x, at[1] - z);
            if (away >= nearest) continue;
            nearest = away;
            best = new int[]{x, z};
        }
        return best;
    }

    /** The way another road's line runs where a block met it, which is the arm a road running into its side turns on. */
    private static int[] along(List<int[]> points, int[] at) {
        for (int index = 1; index < points.size(); index++) {
            int[] from = points.get(index - 1);
            int[] to = points.get(index);
            int[] met = onLine(List.of(from, to), at);
            if (met == null || Math.hypot(met[0] - at[0], met[1] - at[1]) > MEET) continue;
            return step(from, to);
        }
        return new int[]{1, 0};
    }

    private static int[] opposite(int[] way) {
        return new int[]{-way[0], -way[1]};
    }

    /** One step's direction from one point to another, kept as whole numbers. */
    private static int[] step(int[] from, int[] to) {
        int dx = to[0] - from[0];
        int dz = to[1] - from[1];
        if (dx == 0 && dz == 0) return new int[]{1, 0};
        return new int[]{dx, dz};
    }

    /**
     * How far a block stands from a line, measured square to the nearest stretch of it.
     * <p>
     * Asked of every road in the network rather than of the road a post speaks for, because the reason a post stood in
     * the middle of the Gundabad crossing was that it was five blocks off its own 3-wide road and two blocks inside the
     * road that joins it: a road's own width says nothing about the ground another road has taken.
     */
    private static double near(List<int[]> points, double x, double z) {
        double best = Double.MAX_VALUE;
        for (int index = 1; index < points.size(); index++) {
            int[] from = points.get(index - 1);
            int[] to = points.get(index);
            double dx = to[0] - from[0];
            double dz = to[1] - from[1];
            double length = dx * dx + dz * dz;
            double along = length == 0.0D ? 0.0D
                    : Math.clamp(((x - from[0]) * dx + (z - from[1]) * dz) / length, 0.0D, 1.0D);
            best = Math.min(best, Math.hypot(x - (from[0] + dx * along), z - (from[1] + dz * along)));
        }
        return best;
    }

    /** Whether a column is clear of every road in the network by the verge's own width. */
    private static boolean clear(List<Line> lines, int x, int z) {
        for (Line line : lines) {
            if (near(line.points(), x, z) < line.half() + ASIDE + 0.5D) return false;
        }
        return true;
    }

    /**
     * Plants one post, if the world is owed it.
     * <p>
     * The post belongs beside the road rather than on it, so the ground is looked for from the target outward - a block
     * at a time, in the eight directions round it - and the nearest column clear of every road in the network by half
     * the band and {@link #ASIDE} more is the one the plinth goes on. The ground must be something a plinth can stand
     * on: a shoulder in the water or over a drop is no place for one, and the block itself would drop the post at the
     * next update if it were put there. A plinth already standing within a few blocks answers the target - the post is
     * planted once - and a chunk that is not made is not looked into at all, since posting a road is not a reason to
     * generate the country either side of it.
     *
     * @return 1 when a post was planted, 0 when there was nothing to do, -1 when the ground is not loaded yet
     */
    private static int plant(ServerLevel level, List<Line> lines, int x, int z, List<int[]> ways, int half) {
        if (!level.hasChunk(x >> 4, z >> 4)) return -1;
        List<Direction> arms = compass(ways);
        if (arms.isEmpty()) return 0;
        if (standing(level, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), x, z)) return 0;
        List<BlockPos> posts = verges(level, lines, x, z, half);
        if (posts == null) return -1;
        for (BlockPos post : posts) {
            if (standing(level, post.getY(), post.getX(), post.getZ())) return 0;
            BlockPos below = post.below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) continue;
            Map<Direction, List<String>> boards = named(level, post, arms);
            if (boards.isEmpty()) return 0;
            lay(level, post, boards);
            LOGGER.info("Sign post at {}, {}, {}: {} arm{}", post.getX(), post.getY(), post.getZ(), boards.size(),
                    boards.size() == 1 ? "" : "s");
            return 1;
        }
        return 0;
    }

    /**
     * The columns a post at this target may stand on, the nearest clear one first, or null when not one of them is in
     * a chunk that has been made.
     * <p>
     * The ring is walked outward from one block past the band's own edge - a post two blocks off a 3-wide road is on
     * its edge, not beside it - and every column clear of every road is kept, then the list is ordered by how far each
     * stands from the target. The nearest is what a reader of the board expects: at the crossing, not in the next
     * field.
     * <p>
     * A column in a chunk nobody has made is passed over, not waited for. A target near a chunk border has a ring
     * that reaches over it, and holding the whole post back for the one column it cannot read is a post that never
     * stands: the clock would ask for it every twenty seconds, find the same unread column, and answer the same
     * nothing, however long the road was walked. The nearest column that is loaded is a foot away from the one that
     * is not, and a post a block further along the verge is a post. Only a target with nothing readable at all - the
     * whole ring in country nobody has made - is left for a later pass.
     */
    private static List<BlockPos> verges(ServerLevel level, List<Line> lines, int x, int z, int half) {
        int from = Math.max(1, half) + ASIDE + 1;
        List<BlockPos> posts = new ArrayList<>();
        boolean read = false;
        for (int out = from; out <= from + CLEAR; out++) {
            for (int[] step : AROUND) {
                int px = x + step[0] * out;
                int pz = z + step[1] * out;
                // A verge in a chunk nobody has made is not read at all, and not waited for either: posting a road is
                // not a reason to make country, and one unread column across a border must not hold up the post.
                if (!level.hasChunk(px >> 4, pz >> 4)) continue;
                read = true;
                if (!clear(lines, px, pz)) continue;
                int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, px, pz);
                posts.add(new BlockPos(px, top, pz));
            }
            if (!posts.isEmpty()) break;
        }
        if (!read) return null;
        posts.sort(Comparator.comparingDouble(post -> Math.hypot(post.getX() - x, post.getZ() - z)));
        return posts;
    }

    /**
     * Whether a post already stands at or beside this column, read from the plinth that is the post's own marker.
     * <p>
     * Read over a column's height rather than at one block, because the plinth stands on whatever ground the verge
     * had: the check is asked of the target, whose own height is the road's, and of every verge, whose height is a
     * heightmap read - and the two do not have to agree by a block either way.
     */
    private static boolean standing(ServerLevel level, int top, int x, int z) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockState state = level.getBlockState(new BlockPos(x + dx, top + dy, z + dz));
                    if (state.is(TolkienBlocks.SIGN_POST_PLINTH.get())) return true;
                }
            }
        }
        return false;
    }

    /** The ways a post's arms are turned on, as the four compass directions, with two ways down one line read once. */
    private static List<Direction> compass(List<int[]> ways) {
        List<Direction> arms = new ArrayList<>(4);
        for (int[] way : ways) {
            Direction arm = Math.abs(way[0]) >= Math.abs(way[1])
                    ? (way[0] >= 0 ? Direction.EAST : Direction.WEST)
                    : (way[1] >= 0 ? Direction.SOUTH : Direction.NORTH);
            if (!arms.contains(arm)) arms.add(arm);
        }
        return arms;
    }

    /**
     * The towns each arm names, and nothing for an arm with no town down it.
     * <p>
     * A town is down an arm when it lies inside the sector that arm opens onto - sixty degrees either side of its
     * line - and not when it is the town the post already stands in: a board that says the village is nought blocks
     * away is a board nobody needs. Where two arms could both claim a town it goes to the one it lies most nearly
     * down, and only to that one: a crossing's arms are different roads, and a town down one of them is not a town down
     * the other. The nearest is the one a traveller is going to, so it takes the first board and the next takes the
     * second; a town behind every arm is another post's business.
     */
    private static Map<Direction, List<String>> named(ServerLevel level, BlockPos post, List<Direction> arms) {
        Map<Direction, List<Places.Place>> thatWay = new LinkedHashMap<>();
        for (Direction arm : arms) thatWay.put(arm, new ArrayList<>());
        for (Places.Place town : Places.towns(level)) {
            if (!town.dimension().equals(level.dimension())) continue;
            double dx = town.centre().getX() - post.getX();
            double dz = town.centre().getZ() - post.getZ();
            double away = Math.hypot(dx, dz);
            if (away < OWN) continue;
            Direction best = null;
            double down = SECTOR;
            for (Direction arm : arms) {
                double cos = (dx * arm.getStepX() + dz * arm.getStepZ()) / away;
                if (cos < down) continue;
                if (best != null && cos <= down) continue;
                down = cos;
                best = arm;
            }
            if (best != null) thatWay.get(best).add(town);
        }
        Map<Direction, List<String>> boards = new LinkedHashMap<>();
        for (Direction arm : arms) {
            List<Places.Place> towns = thatWay.get(arm);
            if (towns.isEmpty()) continue;
            towns.sort(Comparator.comparingDouble(town -> Math.hypot(town.centre().getX() - post.getX(),
                    town.centre().getZ() - post.getZ())));
            List<String> names = new ArrayList<>(BOARDS);
            for (Places.Place town : towns) {
                if (names.size() >= BOARDS) break;
                names.add(town.id());
            }
            boards.put(arm, names);
        }
        return boards;
    }

    /**
     * The five blocks of one post, and the boards on its arms.
     * <p>
     * The column goes up first, bottom course upward, because the cap will not stand but on a shaft and an arm will
     * not stand but beside a post. The arms go beside the <b>top</b> course, flush under the cap, where a fingerpost's
     * boards are: a single board takes the highest step of its cell and a double the upper half of it, so the pair
     * hangs where it can be read. A cell already holding something is stepped down a course rather than taken - the
     * post is scenery beside a road, not a thing that clears the country for itself - and an arm that finds no course
     * free is left off, since a board across the road is worse than a board missing.
     */
    private static void lay(ServerLevel level, BlockPos post, Map<Direction, List<String>> boards) {
        level.setBlock(post, TolkienBlocks.SIGN_POST_PLINTH.get().defaultBlockState(), 2);
        for (int up = 1; up <= SHAFTS; up++) {
            level.setBlock(post.above(up), TolkienBlocks.SIGN_POST_SHAFT.get().defaultBlockState(), 2);
        }
        level.setBlock(post.above(SHAFTS + 1), TolkienBlocks.SIGN_POST_CAP.get().defaultBlockState(), 2);
        for (Map.Entry<Direction, List<String>> board : boards.entrySet()) {
            stand(level, post, board.getKey(), board.getValue());
        }
    }

    /** One arm, on the highest course of the post whose cell it can take, lettered from the top board down. */
    private static void stand(ServerLevel world, BlockPos post, Direction arm, List<String> towns) {
        boolean twice = towns.size() > 1;
        int step = twice ? SignPostDoubleArmBlock.UPPER : 3;
        for (int up = SHAFTS; up >= 1; up--) {
            BlockPos cell = post.above(up).relative(arm);
            if (!world.hasChunk(cell.getX() >> 4, cell.getZ() >> 4)) return;
            if (!world.getBlockState(cell).isAir()) continue;
            BlockState state = (twice ? TolkienBlocks.SIGN_POST_ARM_DOUBLE.get() : TolkienBlocks.SIGN_POST_ARM.get())
                    .defaultBlockState()
                    .setValue(SignPostArmBlock.FACING, arm)
                    .setValue(SignPostArmBlock.LEVEL, step);
            world.setBlock(cell, state, 2);
            if (!(world.getBlockEntity(cell) instanceof SignPostArmBlockEntity board)) return;
            for (int index = 0; index < towns.size(); index++) {
                board.setDestination(index, towns.get(index));
            }
            return;
        }
    }
}
