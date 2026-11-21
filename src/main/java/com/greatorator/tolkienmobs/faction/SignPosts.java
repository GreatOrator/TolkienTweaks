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
 * the arms in the cells beside the shafts, each pointing away from the post along the road it speaks for. One town
 * down a road gets the single arm; two get the double, whose two boards carry a destination each - and a third town
 * down the same road is left off, since an arm carries two boards and no more.
 * <p>
 * A post is planted once. The plinth is the marker: a target with one already standing beside it is answered and not
 * planted again, so a world may be asked for its posts on every start, on a clock, and by hand, and the same post is
 * never stacked twice. A target whose chunk is not loaded is not answered either - it waits for a pass that finds it
 * loaded, which is why the clock is there rather than a single sweep at the world's start.
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
     * How near two roads have to come before they are one junction, in blocks.
     * <p>
     * The network's ends are pinned blocks rather than surveyed corners, and a plan runs its line from the pin rather
     * than through it, so the two ends of a join can stand a few blocks apart. A dozen is wider than any of those and
     * narrower than the shortest road in the network.
     */
    private static final int MEET = 12;
    /** How far past the road's own edge a post stands, so the band is not narrowed by it, in blocks. */
    private static final int ASIDE = 2;
    /** The widest a road in the network is laid, and so the half-width the shoulder is measured from. */
    private static final int WIDEST_HALF = 3;
    /**
     * How wide a sector one arm speaks for, as the cosine of its half-angle: sixty degrees either side of its line.
     * <p>
     * A board names the towns a traveller would reach by walking the way the arm points, and a town is down that road
     * when it lies within the sector the road opens onto. Half a right angle either side is what a road's own bearing
     * can be read to, and it keeps a town behind the post off a board that faces away from it.
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
        for (Junction junction : junctions(lines)) {
            planted += plant(level, junction.x, junction.z, junction.ways, WIDEST_HALF);
        }
        for (Line line : lines) {
            if (POSTED.contains(key(level, line.road()))) continue;
            boolean settled = true;
            for (int[] mark : spaced(line)) {
                int[] way = new int[]{mark[2], mark[3]};
                // Both ways along the road are spoken for: a traveller arrives from one and leaves by the other.
                List<int[]> ways = List.of(way, new int[]{-way[0], -way[1]});
                int done = plant(level, mark[0], mark[1], ways, line.half());
                if (done < 0) settled = false;
                planted += Math.max(0, done);
            }
            if (settled) POSTED.add(key(level, line.road()));
        }
        return planted;
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
     * meeting another road, and the half-width because the shoulder a post stands on is measured from the band's own
     * edge.
     */
    private static List<Line> lines(ServerLevel level) {
        Set<String> roads = new java.util.LinkedHashSet<>();
        for (Route route : Roads.ROUTES) roads.add(Roads.roadOf(route.id()));
        List<Line> lines = new ArrayList<>();
        for (String road : roads) {
            List<Route> sections = Roads.sections(road);
            if (sections.isEmpty()) continue;
            boolean laid = false;
            int half = WIDEST_HALF;
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
                if (kind != null) half = Math.max(1, kind.half());
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

    /**
     * Where two roads meet.
     * <p>
     * Read from the roads' own ends rather than from the junctions the network was planned at, because the network
     * carries only the ends a surveyor pinned: three of the roads of the vale end on the block 4647, -3315, and the
     * Gundabad road ends on the old forest road's line rather than on its end. Both are junctions to a traveller, so
     * both are found - an end against every other road's ends, and an end against every other road's line. The ways a
     * junction speaks for are the ways its roads leave it, which is what its arms are turned on.
     */
    private static List<Junction> junctions(List<Line> lines) {
        List<Junction> found = new ArrayList<>();
        for (int a = 0; a < lines.size(); a++) {
            for (int b = 0; b < lines.size(); b++) {
                if (a == b) continue;
                Line one = lines.get(a);
                Line other = lines.get(b);
                for (int[] end : List.of(first(one), last(one))) {
                    for (int[] far : List.of(first(other), last(other))) {
                        if (Math.hypot(end[0] - far[0], end[1] - far[1]) > MEET) continue;
                        junction(found, end[0], end[1], away(one, end), away(other, far));
                    }
                    int[] met = onLine(other.points(), end);
                    if (met == null || Math.hypot(end[0] - met[0], end[1] - met[1]) > MEET) continue;
                    junction(found, met[0], met[1], away(one, end), along(other.points(), met));
                    junction(found, met[0], met[1], away(one, end), opposite(along(other.points(), met)));
                }
            }
        }
        return found;
    }

    /** A place roads meet: where it is, and the ways its roads leave it. */
    private record Junction(int x, int z, List<int[]> ways) {
    }

    /** Adds a way to the junction near this point, or starts one there. */
    private static void junction(List<Junction> found, int x, int z, int[]... ways) {
        for (Junction junction : found) {
            if (Math.hypot(junction.x() - x, junction.z() - z) > MEET) continue;
            List<int[]> joined = new ArrayList<>(junction.ways());
            for (int[] way : ways) if (!joined.contains(way)) joined.add(way);
            found.set(found.indexOf(junction), new Junction(junction.x(), junction.z(), joined));
            return;
        }
        List<int[]> list = new ArrayList<>();
        for (int[] way : ways) if (!list.contains(way)) list.add(way);
        found.add(new Junction(x, z, list));
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
     * Plants one post, if the world is owed it.
     * <p>
     * The post belongs beside the road rather than on it, so the target block is the road's own line and the ground is
     * looked for a band's half-width out, first on one side and then on the other: the hillside a road is cut into has
     * a shoulder on one side and a drop on the other, and a post wants the side a walker can stand on. A plinth already
     * standing within a couple of blocks answers the target - the post is planted once - and a chunk that is not made
     * is not looked into at all, since posting a road is not a reason to generate the country either side of it.
     *
     * @return 1 when a post was planted, 0 when there was nothing to do, -1 when the ground is not loaded yet
     */
    private static int plant(ServerLevel level, int x, int z, List<int[]> ways, int half) {
        if (!level.hasChunk(x >> 4, z >> 4)) return -1;
        List<Direction> arms = compass(ways);
        if (arms.isEmpty()) return 0;
        List<BlockPos> posts = shoulders(level, x, z, ways.get(0), half);
        for (BlockPos post : posts) {
            if (!level.hasChunk(post.getX() >> 4, post.getZ() >> 4)) return -1;
            if (standing(level, post)) return 0;
            // A plinth stands on ground: a shoulder in the water or over air is no place for one, and the block
            // itself would drop it at the next update if it were put there.
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

    /** The two shoulders a post may stand on, in the order they are tried: the left of the road, then its right. */
    private static List<BlockPos> shoulders(ServerLevel level, int x, int z, int[] way, int half) {
        double length = Math.hypot(way[0], way[1]);
        if (length == 0.0D) return List.of();
        // Square to the road: the way it is going, turned a quarter turn.
        double acrossX = -way[1] / length;
        double acrossZ = way[0] / length;
        int out = Math.max(1, half) + ASIDE;
        List<BlockPos> posts = new ArrayList<>(2);
        for (int side = 1; side >= -1; side -= 2) {
            int px = (int) Math.round(x + acrossX * out * side);
            int pz = (int) Math.round(z + acrossZ * out * side);
            // A shoulder in a chunk nobody has made is not read at all: posting a road is not a reason to make country.
            if (!level.hasChunk(px >> 4, pz >> 4)) continue;
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, px, pz);
            posts.add(new BlockPos(px, top, pz));
        }
        return posts;
    }

    /** Whether a post already stands at or beside this block, read from the plinth that is the post's own marker. */
    private static boolean standing(ServerLevel level, BlockPos post) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    BlockState state = level.getBlockState(post.offset(dx, dy, dz));
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
     * away is a board nobody needs. The nearest is the one a traveller is going to, so it takes the first board and
     * the next takes the second; a town behind the arm is another arm's business.
     */
    private static Map<Direction, List<String>> named(ServerLevel level, BlockPos post, List<Direction> arms) {
        Map<Direction, List<String>> boards = new LinkedHashMap<>();
        List<Places.Place> towns = new ArrayList<>();
        for (Places.Place town : Places.towns(level)) {
            if (!town.dimension().equals(level.dimension())) continue;
            double away = Math.hypot(town.centre().getX() - post.getX(), town.centre().getZ() - post.getZ());
            if (away < OWN) continue;
            towns.add(town);
        }
        for (Direction arm : arms) {
            List<Places.Place> thatWay = new ArrayList<>();
            for (Places.Place town : towns) {
                double dx = town.centre().getX() - post.getX();
                double dz = town.centre().getZ() - post.getZ();
                double away = Math.hypot(dx, dz);
                if (away < 1.0D) continue;
                double cos = (dx * arm.getStepX() + dz * arm.getStepZ()) / away;
                if (cos < SECTOR) continue;
                thatWay.add(town);
            }
            if (thatWay.isEmpty()) continue;
            thatWay.sort(Comparator.comparingDouble(town -> Math.hypot(town.centre().getX() - post.getX(),
                    town.centre().getZ() - post.getZ())));
            List<String> names = new ArrayList<>(BOARDS);
            for (Places.Place town : thatWay) {
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
     * not stand but beside a post. An arm's cell is the one next to the first shaft, which is low enough to read from
     * the road and leaves the other two courses clear. A cell that already holds something is not taken: the post is
     * scenery beside a road, not a thing that clears the country for itself.
     */
    private static void lay(ServerLevel level, BlockPos post, Map<Direction, List<String>> boards) {
        level.setBlock(post, TolkienBlocks.SIGN_POST_PLINTH.get().defaultBlockState(), 2);
        for (int up = 1; up <= SHAFTS; up++) {
            level.setBlock(post.above(up), TolkienBlocks.SIGN_POST_SHAFT.get().defaultBlockState(), 2);
        }
        level.setBlock(post.above(SHAFTS + 1), TolkienBlocks.SIGN_POST_CAP.get().defaultBlockState(), 2);
        for (Map.Entry<Direction, List<String>> board : boards.entrySet()) {
            BlockPos cell = post.above(1).relative(board.getKey());
            if (!level.getBlockState(cell).isAir()) continue;
            List<String> towns = board.getValue();
            boolean twice = towns.size() > 1;
            BlockState state = (twice ? TolkienBlocks.SIGN_POST_ARM_DOUBLE.get() : TolkienBlocks.SIGN_POST_ARM.get())
                    .defaultBlockState()
                    .setValue(SignPostArmBlock.FACING, board.getKey())
                    .setValue(SignPostArmBlock.LEVEL, twice ? SignPostDoubleArmBlock.LOWER : 0);
            level.setBlock(cell, state, 2);
            if (!(level.getBlockEntity(cell) instanceof SignPostArmBlockEntity arm)) continue;
            for (int index = 0; index < towns.size(); index++) {
                arm.setDestination(index, towns.get(index));
            }
        }
    }
}
