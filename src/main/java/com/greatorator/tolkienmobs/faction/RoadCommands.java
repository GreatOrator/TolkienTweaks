package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The road out of the valley, and the bridge that carries it over the Bruinen.
 * <p>
 * The road is not painted on the ground any more. Painted, it followed whatever the ground did, and the
 * ground under the build's own path is a wall face: a hundred and thirty-four at the path's last block,
 * a hundred and nine six blocks out, and the river at seventy-four. A road that follows that is a stone
 * staircase down a cliff, which is what the first pass laid.
 * <p>
 * So the pass grades instead: it finds its own way down the mountain, and then cuts and fills a shelf
 * for itself along that way. Two things follow from grading:
 * <ul>
 *   <li>The alignment is searched, not drawn. A weighted walk over the live heightmap prefers level ground
 *   and pays for every block of rise or fall, so it runs along the contours and switchbacks where the face
 *   is too steep to fall straight down - the same choice a road surveyor would make.</li>
 *   <li>The surface follows the ground it crosses, smoothed. A moving average takes the bumps out over a
 *   dozen blocks either way and every pass is pulled back within twelve blocks of the ground, so the road
 *   cuts into the hillside above its line and stands on stone where the hill falls away instead of spanning
 *   the valley, and it arrives at the bridge on the deck's own level.</li>
 * </ul>
 * The bridge is stood up from the cut's own pieces, {@code bridge_end} and {@code bridge_middle}, which are
 * shipped in the jar: two ends and four middles cover the fifty columns between the bank markers exactly.
 * Their pillars are only six blocks long, so every column that carries one has its log drawn down to the bed
 * it stands on - through the water where the span crosses it. Both ends are met along the span's own line.
 * <p>
 * The waypoints are the ammolite markers walked in the scouted world: the path's last block at 5200, 134,
 * -3034 and the two banks at 5055 and 5006 on -3075.
 */
@EventBusSubscriber(modid = MODID)
public final class RoadCommands {
    /** Gamemaster-level operators, and anyone building in creative, as the city's own hand answers to. */
    private static final Predicate<CommandSourceStack> MAY_BUILD = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** The two pieces the bridge is stood up from, in the jar's own structure folder. */
    private static final String BRIDGE_END = "bridge_end";
    private static final String BRIDGE_MIDDLE = "bridge_middle";

    /** The path's last block, and the two banks the bridge joins. */
    private static final int PATH_X = 5200;
    private static final int PATH_Z = -3034;
    private static final int EAST_BANK_X = 5055;
    private static final int WEST_BANK_X = 5006;
    private static final int BRIDGE_Z = -3075;
    /** The bridge is nine wide, as the pieces are, so four either side of its line. */
    private static final int BRIDGE_HALF = 4;
    /**
     * Where the deck sits. Middles carry their planks two blocks up, so a base of 74 puts the walking surface
     * at 76 - two over water that stands at 74. The ends carry a slab one block up instead, so they are placed
     * one higher and meet the middle within half a block, which is the half step the road takes on every rise.
     */
    private static final int BRIDGE_BASE = 74;
    private static final int BRIDGE_END_BASE = 75;
    /** The walking surface at the abutments, which is where the road has to arrive. */
    private static final int DECK_Y = 76;
    /** How far a pillar may be drawn down before the pass gives up on finding a bed, in blocks. */
    private static final int PILLAR_FLOOR = 48;

    /** How much of the approach onto the deck is forced straight on the span's own line, in blocks. */
    private static final int RUN_IN = 16;
    /** How far the road runs on west of the far abutment, before it is left to the drawn country. */
    private static final int EXIT_REACH = 32;

    /** The city's own footprint, which the surface bands are measured from and the descent may not cross. */
    private static final int CITY_X0 = 5200;
    private static final int CITY_X1 = 5753;
    private static final int CITY_Z0 = -3370;
    private static final int CITY_Z1 = -2859;
    /** How close the ground has to be to the walls to count as the settlement's own ground, in blocks. */
    private static final int TOWN_BAND = 16;
    private static final int TRANSITION_BAND = 64;
    /** Half-widths: seven of wild road, five of paved. */
    private static final int WILD_HALF = 3;
    private static final int PAVED_HALF = 2;

    /** How many passes of the smoothing filter the profile takes, each reaching one more station either way. */
    private static final int PROFILE_PASSES = 8;
    /** The deepest cut or tallest fill one column may take before the road is held back to the ground. */
    private static final int CUT_FILL = 12;
    /** The most one station's surface may rise or fall above the one before it, in blocks. */
    private static final double MAX_STEP = 1.0D;
    /** How far above the road a column's trunks and leaves are cleared, so no crown hangs over the grade. */
    private static final int CANOPY_CLEAR = 16;
    /** The grade the descent aims for, in blocks of rise per block of travel; anything steeper is penalised. */
    private static final double TARGET_GRADE = 0.35D;
    /** What a block of grade above the target costs, squared: a wall face becomes expensive to fall down. */
    private static final double GRADE_PENALTY = 12.0D;
    /** What a change of direction costs, so the road runs in legs and turns at its own switchbacks. */
    private static final double TURN_COST = 1.5D;
    /** How far off a straight line the simplified alignment may wander, in blocks. */
    private static final double SIMPLIFY_TOLERANCE = 1.5D;
    /** How far back from a corner a hairpin is cut, and how many steps its curve is drawn in. */
    private static final int FILLET = 10;
    private static final int FILLET_STEPS = 12;
    /** A station's surface, as a sentinel, when the ground there is water: the river is the bridge's. */
    private static final int WATER = Integer.MIN_VALUE / 2;

    /** Templates already read this session, so a re-run does not parse them again. */
    private static final Map<String, StructureTemplate> LOADED = new HashMap<>();

    private RoadCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmroad")
                .requires(MAY_BUILD)
                .then(Commands.literal("build").executes(RoadCommands::build)));
    }

    /** Stands the bridge, draws its pillars down, and grades the road either side of it. */
    private static int build(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        try {
            int cleared = sweep(level);
            int pieces = bridge(level);
            int pillars = pillars(level);

            List<int[]> found = descent(level);
            boolean searched = found != null;
            if (!searched) found = straight();
            // The last leg is on the span's own line, so the road walks onto the abutment square rather than
            // meeting it at an angle: there is no junction piece to dress a diagonal arrival. Only that leg is
            // forced. The search comes all the way to its far end, because ground a forced line crosses is
            // ground the road must climb: the scouted valley stands a rock fin fifty-nine blocks tall on the
            // bank, astride any longer straight run, and a profile that follows the ground walks up onto its
            // top and plunges off the end sixteen blocks before the deck.
            List<int[]> approach = new ArrayList<>(found);
            for (int x = EAST_BANK_X + RUN_IN - 1; x >= EAST_BANK_X + 1; x--) {
                approach.add(new int[]{x, BRIDGE_Z});
            }
            // The search turns in a single block. The straight lines are pulled out of its staircase and
            // every corner left is cut back into a curve, so the switchbacks are hairpins a walker can take.
            approach = hairpins(simplify(approach));
            List<int[]> exit = new ArrayList<>();
            for (int x = WEST_BANK_X - 1; x >= WEST_BANK_X - EXIT_REACH; x--) exit.add(new int[]{x, BRIDGE_Z});

            int startY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, PATH_X, PATH_Z) - 1;
            int endY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, WEST_BANK_X - EXIT_REACH, BRIDGE_Z) - 1;
            int[] graded = grade(level, approach, startY, DECK_Y);
            int[] far = grade(level, exit, DECK_Y, endY);

            int along = approach.size();
            String how = searched ? "searched" : "straight, the descent could not be searched";
            source.sendSuccess(() -> Component.literal("Road to the valley: " + cleared
                    + " old timbers cleared, bridge of " + pieces
                    + " pieces from " + EAST_BANK_X + " to " + WEST_BANK_X + " on z " + BRIDGE_Z
                    + ", pillars drawn down " + pillars + " blocks. Approach " + along + " blocks, " + how
                    + ", steepest graded step " + graded[4] + ": " + graded[0] + " columns laid, "
                    + graded[1] + " cut, " + graded[2] + " filled, " + graded[5] + " canopy cleared, "
                    + graded[3] + " left open."
                    + " Far side " + far[0] + " columns laid, " + far[1] + " cut, " + far[2]
                    + " filled. Both ends walk in along the span's own line."), true);
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The bridge could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Takes the bridge's own timbers out of the water before the pieces are laid again.
     * <p>
     * A pass that only places blocks cannot correct a piece that was placed in the wrong place: the first
     * run's east end is still standing eight blocks north of the deck, and a re-run would leave it there for
     * good. The sweep clears the piece's own woods - log, plank, slab, fence and stair - from the box a span
     * and a mislaid end could occupy, from deep enough to catch the pillars under the water to the top of the
     * railings. Nothing but timber is touched, so the banks, the bed and the river are left exactly as they
     * are.
     */
    private static int sweep(ServerLevel level) {
        int cleared = 0;
        int z0 = BRIDGE_Z - BRIDGE_HALF - 8;
        int z1 = BRIDGE_Z + BRIDGE_HALF;
        for (int x = WEST_BANK_X - 1; x <= EAST_BANK_X + 1; x++) {
            for (int z = z0; z <= z1; z += 8) {
                level.getChunk(x >> 4, z >> 4);
            }
            for (int z = z0; z <= z1; z++) {
                for (int y = BRIDGE_BASE - PILLAR_FLOOR; y <= BRIDGE_BASE + 7; y++) {
                    BlockPos at = new BlockPos(x, y, z);
                    if (!timber(level.getBlockState(at))) continue;
                    level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    /** The five woods the bridge's two pieces are made of. */
    private static boolean timber(BlockState state) {
        return state.is(Blocks.OAK_LOG) || state.is(Blocks.OAK_PLANKS) || state.is(Blocks.OAK_SLAB)
                || state.is(Blocks.JUNGLE_SLAB) || state.is(Blocks.OAK_FENCE) || state.is(Blocks.OAK_STAIRS);
    }

    /**
     * Stands the two ends and the middles between them, east end turned about so its abutment faces the city.
     * <p>
     * A turned template is anchored at its own far corner, not its near one: the placement settings rotate
     * about the position handed in, so a nine-wide piece turned through half a circle occupies the nine columns
     * ending at that position rather than the nine starting from it. The east end is therefore anchored on
     * 5055 and -3071, its own north-east corner, and the middles follow from that corner east to west.
     */
    private static int bridge(ServerLevel level) throws IOException {
        StructureTemplate end = template(level, BRIDGE_END);
        StructureTemplate middle = template(level, BRIDGE_MIDDLE);
        int z0 = BRIDGE_Z - BRIDGE_HALF;
        int endWide = end.getSize().getX();

        lay(level, end, new BlockPos(EAST_BANK_X, BRIDGE_END_BASE, BRIDGE_Z + BRIDGE_HALF), Rotation.CLOCKWISE_180);
        int placed = 1;

        // Each middle is anchored on its own north-east corner too, so the piece's last column is the cursor
        // itself and the next free column is one further west. Only middles that leave room for the west end
        // are laid; the end then closes whatever is left.
        int cursor = EAST_BANK_X - endWide;
        // Room for one more middle and for the west end behind it: seventeen columns, not eighteen. Asking
        // for eighteen dropped the last middle and left a gap of open water in front of the far abutment.
        while (cursor - WEST_BANK_X + 1 >= middle.getSize().getX() + endWide) {
            lay(level, middle, new BlockPos(cursor - middle.getSize().getX() + 1, BRIDGE_BASE, z0), Rotation.NONE);
            cursor -= middle.getSize().getX();
            placed++;
        }
        lay(level, end, new BlockPos(WEST_BANK_X, BRIDGE_END_BASE, z0), Rotation.NONE);
        return placed + 1;
    }

    /** One piece, with every chunk it touches opened first: a block written into a closed chunk is lost. */
    private static void lay(ServerLevel level, StructureTemplate template, BlockPos at, Rotation rotation) {
        int wide = template.getSize().getX();
        int deep = template.getSize().getZ();
        for (int dx = 0; dx <= wide; dx += 8) {
            for (int dz = 0; dz <= deep; dz += 8) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
        template.placeInWorld(level, at, at, settings, level.getRandom(), 2);
    }

    /**
     * Draws every pillar the pieces brought down to the bed under it.
     * <p>
     * The pieces carry six blocks of support and the water here is four or five deep over its bed, so on its
     * own the span stands on stilts that stop in mid-air. What is looked for is the lowest oak log in each
     * column of the span; below it the column is filled with oak log while it is air or water, and the fill
     * stops at the first block that is neither - the bed, or the bank under an abutment.
     */
    private static int pillars(ServerLevel level) {
        int drawn = 0;
        int z0 = BRIDGE_Z - BRIDGE_HALF;
        for (int x = WEST_BANK_X; x <= EAST_BANK_X; x++) {
            for (int z = z0; z <= z0 + BRIDGE_HALF * 2; z++) {
                int base = Integer.MIN_VALUE;
                for (int y = BRIDGE_BASE + 6; y >= BRIDGE_BASE - 2; y--) {
                    if (level.getBlockState(new BlockPos(x, y, z)).is(Blocks.OAK_LOG)) base = y;
                }
                if (base == Integer.MIN_VALUE) continue;
                for (int y = base - 1; y > base - PILLAR_FLOOR; y--) {
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState under = level.getBlockState(at);
                    if (!under.isAir() && under.getFluidState().isEmpty()) break;
                    level.setBlock(at, Blocks.OAK_LOG.defaultBlockState(), 2);
                    drawn++;
                }
            }
        }
        return drawn;
    }

    /**
     * Finds the way down the mountain: an A* walk from the path's last block to the run-in onto the bridge,
     * over the live heightmap, that prefers level ground and pays for every block of rise or fall.
     * <p>
     * That is what makes the road run away from the path and along the face instead of dropping off it. The
     * cost of a step is its length, plus twelve times the square of any grade steeper than {@link #TARGET_GRADE},
     * plus a little for every change of direction. A wall face is therefore not something the walk will fall
     * down: it runs along the contours, turns where the fall line is too steep to take, and comes back lower - a
     * switchback, which is the road alternative to a hole in the grade or an embankment forty blocks tall. Water is refused
     * outright - the river is the bridge's - and so is the city's own footprint.
     * <p>
     * It falls back to the straight line if the search finds nothing, so the command always lays something.
     */
    private static List<int[]> descent(ServerLevel level) {
        final int x0 = 4985, x1 = 5225, z0 = -3135, z1 = -2995;
        final int wide = x1 - x0 + 1, deep = z1 - z0 + 1;
        int[] ground = new int[wide * deep];
        Arrays.fill(ground, Integer.MIN_VALUE);
        double[] best = new double[wide * deep];
        Arrays.fill(best, Double.MAX_VALUE);
        int[] from = new int[wide * deep];
        Arrays.fill(from, -1);
        byte[] heading = new byte[wide * deep];
        Arrays.fill(heading, (byte) -1);
        boolean[] settled = new boolean[wide * deep];

        int start = (PATH_X - x0) * deep + (PATH_Z - z0);
        int goal = (EAST_BANK_X + RUN_IN - x0) * deep + (BRIDGE_Z - z0);
        best[start] = 0.0D;
        PriorityQueue<double[]> open = new PriorityQueue<>((left, right) -> Double.compare(left[0], right[0]));
        open.add(new double[]{0.0D, start});
        int looked = 0;

        while (!open.isEmpty() && looked < 120000) {
            int node = (int) open.poll()[1];
            if (settled[node]) continue;
            settled[node] = true;
            looked++;
            if (node == goal) break;
            int x = x0 + node / deep, z = z0 + node % deep;
            if (ground[node] == Integer.MIN_VALUE) ground[node] = surfaceAt(level, x, z);
            if (ground[node] == WATER) continue;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    byte dir = (byte) ((dx + 1) * 3 + (dz + 1));
                    int nx = x + dx, nz = z + dz;
                    if (nx < x0 || nx > x1 || nz < z0 || nz > z1) continue;
                    // The road never runs through the city; it leaves from the path's own end.
                    if (nx >= CITY_X0 && nx <= CITY_X1 && nz >= CITY_Z0 && nz <= CITY_Z1) continue;
                    int at = (nx - x0) * deep + (nz - z0);
                    if (settled[at]) continue;
                    if (ground[at] == Integer.MIN_VALUE) ground[at] = surfaceAt(level, nx, nz);
                    if (ground[at] == WATER) continue;
                    int change = Math.abs(ground[at] - ground[node]);
                    double length = Math.sqrt(dx * dx + dz * dz);
                    double over = Math.max(0.0D, change - TARGET_GRADE * length);
                    double cost = best[node] + length + GRADE_PENALTY * over * over;
                    if (heading[node] != -1 && heading[node] != dir) cost += TURN_COST;
                    if (cost >= best[at]) continue;
                    best[at] = cost;
                    from[at] = node;
                    heading[at] = dir;
                    double guess = Math.abs(EAST_BANK_X + RUN_IN - nx) + Math.abs(BRIDGE_Z - nz);
                    open.add(new double[]{cost + guess, at});
                }
            }
        }

        // Nothing settled the bend: the caller lays the straight line and says so, rather than this pass
        // pretending that a search which never arrived had found anything.
        if (goal != start && from[goal] == -1) return null;
        List<int[]> path = new ArrayList<>();
        int node = goal;
        while (node >= 0) {
            path.add(new int[]{x0 + node / deep, z0 + node % deep});
            node = from[node];
        }
        Collections.reverse(path);
        // The alignment starts where the city's path ends, exactly once.
        if (path.isEmpty() || path.get(0)[0] != PATH_X || path.get(0)[1] != PATH_Z) {
            path.add(0, new int[]{PATH_X, PATH_Z});
        }
        return path;
    }

    /**
     * Lays one graded road: heights along the alignment, smoothed, held to a step at a time, then eased back
     * toward the ground wherever the cut or fill that would cost stays within budget.
     * <p>
     * The height is a property of the alignment rather than of the ground, which is the whole point: a road
     * that reads as a road keeps its own grade while the ground does what it likes, cutting into the hillside
     * above it and standing on a stone embankment below. The step clamp is a pair of sweeps - forward, then
     * back - so a wall face becomes a stair instead of a cliff, and {@link #PROFILE_PASSES} of them settle the
     * profile.
     *
     * @return columns laid, cut down, filled up, and left open because the ground was too far off the grade
     */
    private static int[] grade(ServerLevel level, List<int[]> line, int startY, int endY) {
        int n = line.size();
        if (n < 2) return new int[]{0, 0, 0, 0};
        double[] height = new double[n];
        int[] ground = new int[n];
        for (int i = 0; i < n; i++) {
            ground[i] = surfaceAt(level, line.get(i)[0], line.get(i)[1]);
            height[i] = ground[i] == WATER ? startY : ground[i];
        }
        height[0] = startY;
        height[n - 1] = endY;
        // The profile follows the ground and is only smoothed. A moving average takes the bumps out - a
        // five-block stair becomes a ramp over a dozen blocks - and every pass is pulled back inside the
        // cut-and-fill budget, so the road hugs the hillside rather than spanning it.
        //
        // A global grade between the two ends was tried first and is wrong here: where the mountain falls
        // faster than any walkable ramp, the ramp stops being a road and becomes a causeway standing in the
        // air on stone columns, and every column whose cut exceeds the budget is left as a hole in the road.
        // The ground is what the road is cut into; the grade is what falls out of smoothing it.
        for (int pass = 0; pass < PROFILE_PASSES; pass++) {
            for (int i = 1; i < n - 1; i++) {
                height[i] = (height[i - 1] + 2.0D * height[i] + height[i + 1]) / 4.0D;
            }
            // A road does not drop. Smoothing rounds a bump, but nothing in it stops one station sitting ten
            // blocks under the one before it where the ground does, so two sweeps pull every station within a
            // step of its neighbours. The surface then falls as a stair of half-steps - the slabs see to the
            // rest - rather than in drops, which is what a walker reads as a cliff in the road.
            for (int i = 1; i < n; i++) {
                height[i] = Mth.clamp(height[i], height[i - 1] - MAX_STEP, height[i - 1] + MAX_STEP);
            }
            for (int i = n - 2; i >= 0; i--) {
                height[i] = Mth.clamp(height[i], height[i + 1] - MAX_STEP, height[i + 1] + MAX_STEP);
            }
            for (int i = 1; i < n - 1; i++) {
                if (ground[i] == WATER) continue;
                height[i] = Mth.clamp(height[i], ground[i] - CUT_FILL, ground[i] + CUT_FILL);
            }
            height[0] = startY;
            height[n - 1] = endY;
        }

        int laid = 0, cut = 0, filled = 0, open = 0, steepest = 0, cleared = 0;
        Map<Long, Integer> tops = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            int target = Mth.floor(height[i]);
            if (i > 0) steepest = Math.max(steepest, Math.abs(target - Mth.floor(height[i - 1])));
            int x = line.get(i)[0], z = line.get(i)[1];
            int half = fromWalls(x, z) <= TRANSITION_BAND ? PAVED_HALF : WILD_HALF;
            // A hairpin is wider than a run: the apron gives the turn somewhere to be walked around.
            if (turning(line, i)) half += 2;
            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    int at = x + dx;
                    int into = z + dz;
                    if (half == WILD_HALF && (Math.abs(dx) == WILD_HALF || Math.abs(dz) == WILD_HALF)
                            && jitter(at, into) % 3 != 0) {
                        continue;
                    }
                    int groundAt = surfaceAt(level, at, into);
                    if (groundAt == WATER) {
                        // The river is the bridge's, but the road still walks to its edge. Where the grade is
                        // above the water line the last verge columns are carried over on stone rather than
                        // stopping short of the bank and leaving a lip for the water to sit against.
                        int waterTop = level.getHeight(Heightmap.Types.MOTION_BLOCKING, at, into) - 1;
                        if (target <= waterTop) continue;
                        level.getChunk(at >> 4, into >> 4);
                        for (int y = waterTop; y < target; y++) {
                            level.setBlock(new BlockPos(at, y, into), Blocks.STONE.defaultBlockState(), 2);
                            filled++;
                        }
                        level.setBlock(new BlockPos(at, target, into), surface(at, into), 2);
                        tops.put(key(at, into), target);
                        laid++;
                        continue;
                    }
                    int step = target - groundAt;
                    // Ground this far off the grade is not cut or filled: the band loses that column and goes
                    // on. The exception is the column the alignment itself runs through - the profile is held
                    // within the budget of the ground under it, so it never needs more - and so the road
                    // narrows against a rock face rather than breaking, which is what a hole in the surface
                    // reads as.
                    if (Math.abs(step) > CUT_FILL && (dx != 0 || dz != 0)) {
                        open++;
                        continue;
                    }
                    level.getChunk(at >> 4, into >> 4);
                    BlockPos column = new BlockPos(at, target, into);
                    // Cut the hillside down to the road, or stand the road up on its own embankment.
                    if (step < 0) {
                        for (int y = groundAt; y > target; y--) {
                            level.setBlock(new BlockPos(at, y, into), Blocks.AIR.defaultBlockState(), 2);
                            cut++;
                        }
                    } else if (step > 0) {
                        for (int y = groundAt + 1; y < target; y++) {
                            level.setBlock(new BlockPos(at, y, into), Blocks.STONE.defaultBlockState(), 2);
                            filled++;
                        }
                    }
                    level.setBlock(column, surface(at, into), 2);
                    tops.put(key(at, into), target);
                    laid++;
                    // A road cut through a wood leaves its canopy hanging: the trunks come away with the
                    // ground, but the crowns above them do not, so a cut stretch sails under a floating
                    // roof. Clear the corridor's own columns of leaves and logs above the road's surface.
                    for (int y = target + 1; y <= target + CANOPY_CLEAR; y++) {
                        BlockPos crown = new BlockPos(at, y, into);
                        BlockState over = level.getBlockState(crown);
                        if (!over.is(BlockTags.LEAVES) && !over.is(BlockTags.LOGS)) continue;
                        level.setBlock(crown, Blocks.AIR.defaultBlockState(), 2);
                        cleared++;
                    }
                }
            }
        }
        steps(level, tops);
        return new int[]{laid, cut, filled, open, steepest, cleared};
    }

    /**
     * Pulls the straight lines out of the searched walk and rounds what is left into hairpins.
     * <p>
     * The search moves a block at a time and turns within a block, so raw it is an eight-direction staircase
     * whose corners are right angles - nothing a walker can take at speed, and on a switchback it is a hinge.
     * Two passes fix that: a Douglas-Peucker simplification keeps only the vertices that carry the shape, and
     * every vertex left is cut back along both of its legs and joined by a curve, so a switchback becomes a
     * hairpin with a radius.
     */
    private static List<int[]> hairpins(List<int[]> line) {
        if (line.size() < 3) return line;
        List<int[]> out = new ArrayList<>();
        out.add(line.get(0));
        for (int k = 1; k < line.size() - 1; k++) {
            int[] back = line.get(k - 1);
            int[] corner = line.get(k);
            int[] next = line.get(k + 1);
            double in = Math.hypot(corner[0] - back[0], corner[1] - back[1]);
            double out2 = Math.hypot(next[0] - corner[0], next[1] - corner[1]);
            int cut = (int) Math.max(1.0D, Math.min(FILLET, Math.min(in / 2.0D, out2 / 2.0D)));
            int[] from = along(back, corner, cut);
            int[] to = along(corner, next, cut);
            walkTo(out, from[0], from[1]);
            for (int s = 1; s <= FILLET_STEPS; s++) {
                double t = (double) s / (FILLET_STEPS + 1);
                double u = 1.0D - t;
                walkTo(out,
                        (int) Math.round(u * u * from[0] + 2.0D * u * t * corner[0] + t * t * to[0]),
                        (int) Math.round(u * u * from[1] + 2.0D * u * t * corner[1] + t * t * to[1]));
            }
            walkTo(out, to[0], to[1]);
        }
        int[] last = line.get(line.size() - 1);
        walkTo(out, last[0], last[1]);
        return out;
    }

    /** The point {@code cut} blocks back from {@code b} along the line from {@code a} to {@code b}. */
    private static int[] along(int[] a, int[] b, int cut) {
        double length = Math.hypot(b[0] - a[0], b[1] - a[1]);
        if (length < 1.0D) return new int[]{b[0], b[1]};
        double t = Math.max(0.0D, 1.0D - cut / length);
        return new int[]{(int) Math.round(a[0] + (b[0] - a[0]) * t),
                (int) Math.round(a[1] + (b[1] - a[1]) * t)};
    }

    /** Steps from the road's current end to a point a block at a time, so the line has no gaps. */
    private static void walkTo(List<int[]> line, int x, int z) {
        int[] last = line.get(line.size() - 1);
        int guard = 0;
        while ((last[0] != x || last[1] != z) && guard++ < 8192) {
            int[] step = {last[0] + Integer.signum(x - last[0]), last[1] + Integer.signum(z - last[1])};
            line.add(step);
            last = step;
        }
    }

    /** Douglas-Peucker: the fewest vertices that still carry the alignment's shape. */
    private static List<int[]> simplify(List<int[]> line) {
        if (line.size() < 3) return line;
        boolean[] keep = new boolean[line.size()];
        keep[0] = true;
        keep[line.size() - 1] = true;
        simplify(line, 0, line.size() - 1, keep);
        List<int[]> out = new ArrayList<>();
        for (int i = 0; i < line.size(); i++) {
            if (keep[i]) out.add(line.get(i));
        }
        return out;
    }

    private static void simplify(List<int[]> line, int from, int to, boolean[] keep) {
        if (to <= from + 1) return;
        int[] a = line.get(from);
        int[] b = line.get(to);
        double worst = -1.0D;
        int at = -1;
        for (int i = from + 1; i < to; i++) {
            double off = offLine(line.get(i), a, b);
            if (off > worst) {
                worst = off;
                at = i;
            }
        }
        if (at > 0 && worst > SIMPLIFY_TOLERANCE) {
            keep[at] = true;
            simplify(line, from, at, keep);
            simplify(line, at, to, keep);
        }
    }

    /** How far a point stands off a line, in blocks. */
    private static double offLine(int[] point, int[] a, int[] b) {
        double dx = b[0] - a[0];
        double dz = b[1] - a[1];
        double length = Math.hypot(dx, dz);
        if (length < 1.0E-6D) return Math.hypot(point[0] - a[0], point[1] - a[1]);
        return Math.abs((point[0] - a[0]) * dz - (point[1] - a[1]) * dx) / length;
    }

    /** Whether the alignment is turning here, which is where the surface is widened into a hairpin's apron. */
    private static boolean turning(List<int[]> line, int i) {
        int back = Math.max(0, i - 3);
        int forth = Math.min(line.size() - 1, i + 3);
        int ax = line.get(i)[0] - line.get(back)[0];
        int az = line.get(i)[1] - line.get(back)[1];
        int bx = line.get(forth)[0] - line.get(i)[0];
        int bz = line.get(forth)[1] - line.get(i)[1];
        double la = Math.hypot(ax, az);
        double lb = Math.hypot(bx, bz);
        if (la < 2.0D || lb < 2.0D) return false;
        return (ax * bx + az * bz) / (la * lb) < 0.85D;
    }

    /** The straight line from the path's end to the run-in, for when the search finds no way down. */
    private static List<int[]> straight() {
        List<int[]> line = new ArrayList<>();
        int steps = PATH_X - EAST_BANK_X - RUN_IN + 1;
        for (int i = 0; i < steps; i++) {
            line.add(new int[]{PATH_X - i, PATH_Z + Math.round((float) (BRIDGE_Z - PATH_Z) * i / steps)});
        }
        return line;
    }

    /**
     * Turns the lower of two road columns that differ by one block into a bottom slab, so a rise is a half
     * step. Only road columns count: a step beside a wall is the wall's business, not the road's.
     */
    private static int steps(ServerLevel level, Map<Long, Integer> tops) {
        int stepped = 0;
        for (Map.Entry<Long, Integer> entry : tops.entrySet()) {
            int x = (int) (entry.getKey() >> 32);
            int z = (int) (entry.getKey() & 0xFFFFFFFFL);
            int y = entry.getValue();
            for (int[] beside : new int[][]{{x + 1, z}, {x, z + 1}}) {
                Integer other = tops.get(key(beside[0], beside[1]));
                if (other == null) continue;
                if (Math.abs(y - other) != 1 || y > other) continue;
                level.setBlock(new BlockPos(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2);
                stepped++;
                break;
            }
        }
        return stepped;
    }

    /** A column's key in the map of what this pass has laid, packed from its x and z. */
    private static long key(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    /**
     * The top block a road could stand on, or {@link #WATER} when that column is the river.
     * <p>
     * A heightmap answers with whatever stops motion, and that includes the crowns of the trees. Read that
     * way, a valley road is a road laid on treetops: it stands up stone columns wherever the canopy was
     * higher than the bank, and it grades against a forest roof instead of a hillside. This walks down past
     * leaves and trunks to the ground the tree is growing on, and only then asks whether it is water.
     */
    private static int surfaceAt(ServerLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        BlockPos at = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(at);
        while (y > level.getMinY() && (state.isAir() || state.is(BlockTags.LOGS))) {
            y--;
            at = at.below();
            state = level.getBlockState(at);
        }
        if (!state.getFluidState().isEmpty()) return WATER;
        return y;
    }

    /**
     * The surface of one block: the three bands of `docs/arda-roads.md`, chosen per block from a hash of x
     * and z so that a chunk border never shows, and so the same column takes the same block whenever asked.
     */
    private static BlockState surface(int x, int z) {
        int near = fromWalls(x, z);
        int roll = jitter(x, z) % 100;
        if (near <= TOWN_BAND) {
            if (roll < 50) return TolkienBlocks.STONE_PATH.get().defaultBlockState();
            if (roll < 77) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 87) return Blocks.STONE_BRICKS.defaultBlockState();
            if (roll < 95) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }
        if (near <= TRANSITION_BAND) {
            if (roll < 50) return Blocks.COBBLESTONE.defaultBlockState();
            if (roll < 77) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 87) return Blocks.COARSE_DIRT.defaultBlockState();
            if (roll < 95) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
        if (roll < 40) return Blocks.GRAVEL.defaultBlockState();
        if (roll < 65) return Blocks.COARSE_DIRT.defaultBlockState();
        if (roll < 85) return Blocks.GRASS_BLOCK.defaultBlockState();
        return Blocks.DIRT_PATH.defaultBlockState();
    }

    /** How far outside the city's footprint a column stands, in blocks. */
    private static int fromWalls(int x, int z) {
        int dx = Math.max(0, Math.max(CITY_X0 - x, x - CITY_X1));
        int dz = Math.max(0, Math.max(CITY_Z0 - z, z - CITY_Z1));
        return (int) Math.round(Math.hypot(dx, dz));
    }

    /** A stable per-block number, so the scatter survives being asked twice and never lines up on a chunk. */
    private static int jitter(int x, int z) {
        return Math.floorMod(x * 73856093 ^ z * 19349663, 1000);
    }

    /** One of the bridge's own pieces, read from the jar the first time it is asked for. */
    private static StructureTemplate template(ServerLevel level, String name) throws IOException {
        StructureTemplate cached = LOADED.get(name);
        if (cached != null) return cached;
        CompoundTag tag;
        try (InputStream in = RoadCommands.class.getResourceAsStream(
                "/data/tolkienmobs/structure/" + name + ".nbt")) {
            if (in == null) throw new IOException("no " + name + ".nbt in the jar");
            tag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
        StructureTemplate template = new StructureTemplate();
        template.load(level.holderLookup(Registries.BLOCK), tag);
        LOADED.put(name, template);
        return template;
    }
}
