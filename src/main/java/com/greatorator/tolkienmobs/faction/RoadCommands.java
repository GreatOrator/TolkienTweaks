package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.registries.BuiltInRegistries;
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

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import com.mojang.serialization.Codec;
import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <p>
 * Not every road crosses water. A route with no river on it is a {@link Route.Riverless}: the build's entry point and
 * the block the road ends at, and nothing else - no span to sweep, no pieces to stand, no pillars to draw. The search
 * is the same one, run over the whole distance between the two ends instead of up to a run-in, and the grade is the
 * same profile arriving on the ground at the far end rather than on a deck. So a second road is one line in
 * {@link Roads}, of whichever kind its country asks for.
 */
@EventBusSubscriber(modid = MODID)
public final class RoadCommands {
    /** Gamemaster-level operators, and anyone building in creative, as the city's own hand answers to. */
    private static final Predicate<CommandSourceStack> MAY_BUILD = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };


    /** How far a pillar may be drawn down before the pass gives up on finding a bed, in blocks. */
    private static final int PILLAR_FLOOR = 48;
    /** The four sides a terrace's step could face, which are the four sides a lip is drawn on. */
    private static final int[][] BESIDE = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    /** Where a pass says what each of its stages cost, which is how the step budgets are checked against real ticks. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoadCommands.class);
    /** Which of the bridge's two templates a planned piece is: the end of the span, or a middle between the ends. */
    private static final int END_PIECE = 0;
    private static final int MIDDLE_PIECE = 1;

    /** How close the ground has to be to the walls to count as the settlement's own ground, in blocks. */
    private static final int TOWN_BAND = 16;
    private static final int TRANSITION_BAND = 64;
    /** Half-widths: seven of wild road, five of paved. */
    private static final int WILD_HALF = 3;
    private static final int PAVED_HALF = 2;

    /** How many passes of the smoothing filter the profile takes, each reaching one more station either way. */
    private static final int PROFILE_PASSES = 8;
    /** The deepest cut or tallest fill one column may take before the road is held back to the ground. */
    private static final int CUT_FILL = 4;
    /**
     * How far along the line the ground is read to find the height a station answers to, in stations.
     * <p>
     * A road is a grade through country rather than a cast of it. Read off its own column, a boulder three blocks over
     * the grass lifts the profile and the band is filled up to it - a hump in a road that was flat either side - and a
     * cave's mouth twelve blocks down takes the profile with it, after which no band can be cut or filled deep enough,
     * so the road ends at the lip and the hole is left open. The middle of the ground over this window either side is
     * neither: a short rise or a short hole is outvoted by the ground around it, while a hillside the road is meant to
     * climb still is one, because every station of a slope reads the same slope.
     */
    private static final int RELIEF_REACH = 8;
    /**
     * How far the ground on both sides of a column has to stand above it before the column is read as a hole rather
     * than as ground. A hollow a road can walk through is a dip; a cave's mouth is a shaft.
     */
    private static final int HOLE_DEPTH = 4;
    /** How far either side of a column the ground is read to tell a hole from a fall line. */
    private static final int HOLE_REACH = 6;
    /**
     * How deep a hole may be carried over on stone before the road gives up on it. A mouth or a shaft is bridged; a
     * ravine sixty blocks down is the country's business, not a road's.
     */
    private static final int HOLE_FILL = 32;
    /** The most one station's surface may rise or fall above the one before it, in blocks. */
    private static final double MAX_STEP = 1.0D;
    /** How many times the two step sweeps are alternated after the last pass, so both ends agree. */
    private static final int SETTLE_PASSES = 8;
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
    /** How far back from a switchback a hairpin is cut, and the least steps its curve is drawn in. */
    private static final int FILLET = 10;
    private static final int FILLET_STEPS = 12;
    /** How far back an ordinary corner is cut, so a walker carries speed round it instead of slowing. */
    private static final int EASE = 24;
    /** Where a turn stops being a corner and starts doubling back, as the cosine of its angle. */
    private static final double HAIRPIN = -0.3D;
    /** A station's surface, as a sentinel, when the ground there is water: the river is the bridge's. */
    private static final int WATER = Integer.MIN_VALUE / 2;
    /**
     * A column's answer when there is nothing in it: a chunk the world has not made yet reads as air all the way down
     * to the floor. It is not a height and nothing may be graded from it - read as one, it was three hundred stations
     * of the grade falling to the bottom of the world, and a road that stopped at the lip of its own trench.
     */
    private static final int VOID = Integer.MIN_VALUE / 4;

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

    /** What a road pass came to, so the hand can say it and the world's own dressing can only log it. */
    record Road(int cleared, int pieces, int pillars, int along, boolean searched, int[] graded, int[] far) {
    }

    /** Stands the bridge, draws its pillars down, and grades the road either side of it. */
    private static int build(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Route route = Roads.nearest(BlockPos.containing(source.getPosition()));
        if (route == null) {
            source.sendFailure(Component.literal("No build has a road walked for it yet."));
            return 0;
        }
        try {
            Road laid = road(source.getLevel(), route);
            String how = laid.searched() ? "searched" : "straight, the descent could not be searched";
            int[] graded = laid.graded();
            int[] far = laid.far();
            String surface = graded[0] + " columns laid, " + graded[1] + " cut, " + graded[2]
                    + " filled, " + graded[5] + " canopy cleared, " + graded[3] + " left open.";
            if (route instanceof Route.Crossing crossing) {
                source.sendSuccess(() -> Component.literal("Road to the valley: " + laid.cleared()
                        + " old timbers cleared, bridge of " + laid.pieces()
                        + " pieces from " + crossing.eastBankX() + " to " + crossing.westBankX() + " on z "
                        + crossing.bridgeZ() + ", pillars drawn down " + laid.pillars() + " blocks. Approach "
                        + laid.along() + " blocks, " + how
                        + ", steepest graded step " + graded[4] + ": " + surface
                        + " Far side " + far[0] + " columns laid, " + far[1] + " cut, " + far[2]
                        + " filled. Both ends walk in along the span's own line."), true);
            } else {
                Route.Riverless dry = (Route.Riverless) route;
                source.sendSuccess(() -> Component.literal("Road from " + dry.id() + " to " + dry.farX()
                        + ", " + dry.farZ() + ": " + laid.along() + " blocks, " + how
                        + ", steepest graded step " + graded[4] + ": " + surface
                        + " Nothing on the way is water, so nothing is bridged."), true);
            }
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The road could not be built: " + failed.getMessage()));
            return 0;
        }
    }

    /** The road itself, without a sender to answer: the pass the hand runs and the world runs for it. */
    static Road road(ServerLevel level, Route route) throws IOException {
        RoadJob job = plan(route);
        while (job.step(level)) {
            // The hand asked for the road, not for the schedule of it.
        }
        return job.summary();
    }

    /** A road pass waiting to be stepped, for a caller that has ticks rather than a command to answer. */
    static RoadJob plan(Route route) {
        return new RoadJob(route);
    }

    /** What a pass that finds its road already down answers with: nothing laid, because nothing was owed. */
    private static final Road ALREADY_DOWN = new Road(0, 0, 0, 0, false, new int[6], new int[3]);

    /**
     * Whether this world already has this road down, from the note its own pass leaves when it finishes.
     * <p>
     * This is the question the world's dressing asks before planning a road, and it is answered by the road rather
     * than by a file beside the city: see {@link Progress}, whose every record used to be removed on completion,
     * leaving no way to tell a road that was down from one never begun.
     */
    static boolean down(ServerLevel level, Route route) {
        CompoundTag carried = Progress.of(level, route.id());
        return carried != null && standing(level, carried);
    }

    /**
     * Whether the ground still holds the road a note says was laid there.
     * <p>
     * A finished pass writes where its road met the build and what block stood under that point, and the note is only
     * believed while that block is still the block. Without the check a note outlived the ground it was written
     * about: a crossing laid before the valley was cut, or before the city it belongs to was raised, went on saying
     * the road was down for ever, and no pass was ever planned over it again - the world had a road on paper and none
     * underfoot. A note too old to carry the block is not trusted either, since nothing can say what it was written
     * about.
     */
    private static boolean standing(ServerLevel level, CompoundTag note) {
        if (!note.getBooleanOr("down", false)) return false;
        int[] at = note.getIntArray("at").orElse(new int[0]);
        String block = note.getStringOr("block", "");
        if (at.length < 3 || block.isEmpty()) return false;
        BlockPos probe = new BlockPos(at[0], at[1], at[2]);
        return block.equals(BuiltInRegistries.BLOCK.getKey(level.getBlockState(probe).getBlock()).toString());
    }

    /** How many columns of the sweep and the pillars one step may take. */
    private static final int COLUMNS_PER_STEP = 12;
    /**
     * How many columns of the graded roads one step may lay, shared between the two roads of a pass.
     * <p>
     * A station is the road's own band - seven columns across wild, five paved, eleven at a hairpin's apron - and
     * every column of it may be cut or filled up to {@link #CUT_FILL} deep and cleared of canopy above. Eight
     * stations was therefore up to a thousand columns and twelve thousand block writes in one tick; a budget of
     * columns is the same road laid no more than a few hundred columns worth at a time.
     */
    private static final int PAVING_PER_STEP = 256;
    /**
     * How many laid columns one step may walk for a step between terraces.
     * <p>
     * The lip pass is the one that used to take the whole road at once: every terrace of two graded roads, tens of
     * thousands of columns, in the tick after the last station. Four lookups a column is cheap work, but not that
     * much of it at once.
     */
    private static final int LIPS_PER_STEP = 1024;
    /** How many candidate blocks of the descent one step may settle. */
    private static final int SEARCH_PER_STEP = 4096;
    /**
     * How many steps go by between one write of a pass's progress and the next, between the writes at the turns of the
     * pass itself: a stage finished, a road down.
     * <p>
     * A long road is a few hundred steps and the snapshot grows with it, since it carries a terrace for every column
     * laid, so writing it once per tick costs more than the road does. Missing a few steps is not free of consequence
     * and not a problem: a world stopped between two writes re-lays the stations those steps laid, which are the same
     * stations they were the first time.
     */
    private static final int SAVE_EVERY = 8;

    /** The stages of a road pass, in the order they have to happen. */
    private enum Stage {
        SWEEP, BRIDGE, PILLARS, SEARCH, GRADE, STEPS
    }

    /**
     * One road pass, taken a stage at a time.
     * <p>
     * The hand wants the road and gets it in one call; a world dressing itself has a tick, and a tick cannot carry
     * the sweep of the span, six templates, fifty pillars, a hundred and twenty thousand candidate blocks and two
     * graded roads at once. So the pass is a cursor: twelve columns of sweep, one bridge piece a step, twelve columns
     * of pillars, four thousand settled candidates of the search, then a few hundred columns of the graded roads and a
     * thousand of their lips. Which is the same budget each stage has always had, measured in columns walked rather
     * than in stations and pieces: a piece is a whole template and a station a band of up to a hundred and twenty-one
     * columns, so counting those was counting the wrong thing. Nothing here changes what the pass does, only when it
     * does it.
     */
    static final class RoadJob {
        private final Route route;
        /** The span, for a road that crosses water; null for one that has none to cross. */
        private final Route.Crossing span;
        private Stage stage;
        private int swept;
        private int cleared;
        private int pieces;
        private int pillarsDrawn;
        private int pillarX;
        /** The bridge's pieces, east end first; built on the stage's first step and stood one a step after it. */
        private List<int[]> bridge;
        private Descent descent;
        private List<int[]> approach;
        private List<int[]> exit;
        private Graded graded;
        private Graded far;
        private boolean searched;
        /** Whether this pass has already looked for its own progress, which it does once rather than every step. */
        private boolean looked;
        /** The stage the last write was taken in, and the steps since it, so a write is not owed by every tick. */
        private Stage written;
        private int sinceWrite;
        /** Steps taken and wall time spent in each stage, by its ordinal, so a budget can be read as a tick. */
        private final int[] taken = new int[Stage.values().length];
        private final long[] nanos = new long[Stage.values().length];
        /** Whether this pass began somewhere other than its own first stage, which the totals then start from. */
        private boolean resumed;
        private Road done;

        private RoadJob(Route route) {
            this.route = route;
            this.span = route instanceof Route.Crossing crossing ? crossing : null;
            // A road with no water on it has no span to sweep and nothing to bridge, so its first stage is the
            // search and the whole distance between its ends is the alignment.
            this.stage = span == null ? Stage.SEARCH : Stage.SWEEP;
            this.swept = span == null ? 0 : span.westBankX() - 1;
            this.pillarX = span == null ? 0 : span.westBankX();
        }

        /** One step; false when the road is down and {@link #summary} has the answer. */
        boolean step(ServerLevel level) throws IOException {
            if (done != null) return false;
            if (!looked) {
                looked = true;
                CompoundTag carried = Progress.of(level, route.id());
                if (carried != null) restore(carried, level);
                // A pass planned over a road the world already has down owes nothing; it says so at once rather than
                // grading the same country a second time.
                if (done != null) return false;
            }
            try {
                Stage ran = stage;
                long began = System.nanoTime();
                boolean more = stage(level);
                spend(ran, began);
                return more;
            } finally {
                if (saveWanted()) {
                    Progress.remember(this, level);
                    written = stage;
                    sinceWrite = 0;
                }
            }
        }

        /**
         * Where the pass has got to, as the block it is working on: the station it is laying, and the route's own
         * entry point before a grade is settled.
         * <p>
         * A road used to be stepped only while its building was in reach, which lays the first few hundred blocks
         * and then stops: the player walking the road took its budget away with them. Measured from this block, the
         * pass keeps up with whoever is walking it and waits at the block it had reached for the next visit.
         */
        int[] head() {
            if (graded != null) return graded.head();
            return new int[]{route.pathX(), route.pathZ()};
        }

        /** One step of whichever stage the pass is in; false once the road is down. */
        private boolean stage(ServerLevel level) throws IOException {
            switch (stage) {
                case SWEEP -> {
                    int upto = Math.min(span.eastBankX() + 1, swept + COLUMNS_PER_STEP - 1);
                    cleared += sweepColumns(level, span, swept, upto);
                    swept = upto + 1;
                    if (swept > span.eastBankX() + 1) stage = Stage.BRIDGE;
                }
                case BRIDGE -> {
                    if (bridge == null) bridge = bridgePlan(level, span);
                    if (pieces < bridge.size()) stand(level, span, bridge.get(pieces++));
                    else stage = Stage.PILLARS;
                }
                case PILLARS -> {
                    int upto = Math.min(span.eastBankX(), pillarX + COLUMNS_PER_STEP - 1);
                    pillarsDrawn += pillarsColumns(level, span, pillarX, upto);
                    pillarX = upto + 1;
                    if (pillarX > span.eastBankX()) stage = Stage.SEARCH;
                }
                case SEARCH -> {
                    if (descent == null) descent = new Descent(route);
                    if (!descent.step(level, SEARCH_PER_STEP)) align(level);
                }
                case GRADE -> {
                    int budget = PAVING_PER_STEP;
                    if (graded != null) budget = graded.pave(level, budget);
                    if (far != null) budget = far.pave(level, budget);
                    if ((graded == null || graded.laid()) && (far == null || far.laid())) stage = Stage.STEPS;
                }
                case STEPS -> {
                    int budget = LIPS_PER_STEP;
                    if (graded != null) budget = graded.lips(level, budget);
                    if (far != null) budget = far.lips(level, budget);
                    if ((graded == null || graded.lipped()) && (far == null || far.lipped())) {
                        done = new Road(cleared, pieces, pillarsDrawn, approach.size(), searched,
                                graded.summary(), far == null ? new int[6] : far.summary());
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Adds one step's wall time to the stage it ran in, and says so when that stage is done with.
         * <p>
         * A stage's budget is a count of work - columns, pieces, candidates - and a count only means anything against
         * the tick it is spent in. So each stage reports how many steps it took and what a step cost, and the pass
         * reports the same of itself once the road is down. A line at the turn of a stage and a line at the end: a log
         * of every step would be a file, not a log.
         */
        private void spend(Stage ran, long began) {
            int index = ran.ordinal();
            taken[index]++;
            nanos[index] += System.nanoTime() - began;
            if (stage == ran && done == null) return;
            LOGGER.info("Road {}: {} done in {} steps of {} each", route.id(), ran, taken[index],
                    millis(nanos[index] / taken[index]));
            if (done != null) {
                LOGGER.info("Road {}: laid in {} steps, {} of work{}", route.id(), count(taken), millis(count(nanos)),
                        resumed ? ", since the pass was last picked up" : "");
            }
        }

        /** How many of a stage's counts were taken, added up. */
        private static int count(int[] counts) {
            int sum = 0;
            for (int one : counts) sum += one;
            return sum;
        }

        private static long count(long[] counts) {
            long sum = 0;
            for (long one : counts) sum += one;
            return sum;
        }

        /** A duration in millis to one place: a step is fractions of a tick, and a whole pass is seconds. */
        private static String millis(long nanos) {
            return (nanos / 1_000_000L) + "." + (nanos / 100_000L % 10L) + " ms";
        }

        /** Whether this step is worth writing down: the turn of a stage, a road finished, or every few steps between. */
        private boolean saveWanted() {
            if (done != null) return true;
            if (stage != written) return true;
            return ++sinceWrite >= SAVE_EVERY;
        }

        /**
         * Where this pass has got to: the stage it reached, what it has already done, and both graded roads with their
         * own grades and cursors.
         *
         * @return the progress, or null when the road is down and there is nothing left to carry
         */
        private CompoundTag save() {
            if (done != null) return null;
            CompoundTag tag = new CompoundTag();
            tag.putString("stage", stage.name());
            tag.putInt("swept", swept);
            tag.putInt("cleared", cleared);
            tag.putInt("pieces", pieces);
            tag.putInt("pillars", pillarsDrawn);
            tag.putInt("pillarX", pillarX);
            tag.putBoolean("searched", searched);
            if (approach != null) tag.putIntArray("approach", flatten(approach));
            if (exit != null) tag.putIntArray("exit", flatten(exit));
            if (graded != null) tag.put("graded", graded.save());
            if (far != null) tag.put("far", far.save());
            return tag;
        }

        /**
         * Puts the pass back where the last write left it, so no stage whose work is done is run a second time.
         * <p>
         * A stage that carries its own state in the file - the alignment the search found, the grade the profile
         * settled - is resumed past. The search itself is the one that does not: its state is a map of a hundred
         * thousand candidate blocks, so a world stopped inside it starts that stage again rather than carrying it. A
         * pass that cannot be carried is still a pass; the ground it would have laid is the ground it lays.
         */
        private void restore(CompoundTag tag, ServerLevel level) {
            if (standing(level, tag)) {
                // The note a finished pass leaves. This road is down, so this pass is done before it begins.
                done = ALREADY_DOWN;
                return;
            }
            Stage saved;
            try {
                saved = Stage.valueOf(tag.getStringOr("stage", Stage.SEARCH.name()));
            } catch (IllegalArgumentException unknown) {
                return;
            }
            swept = tag.getIntOr("swept", swept);
            cleared = tag.getIntOr("cleared", 0);
            pieces = tag.getIntOr("pieces", 0);
            pillarsDrawn = tag.getIntOr("pillars", 0);
            pillarX = tag.getIntOr("pillarX", pillarX);
            searched = tag.getBooleanOr("searched", false);
            List<int[]> carried = unflatten(tag.getIntArray("approach").orElse(new int[0]));
            if (carried.size() < 2) {
                // Nothing to carry on with, so the pass begins at its first stage rather than in the middle of nothing.
                stage = span == null ? Stage.SEARCH : Stage.SWEEP;
                return;
            }
            approach = carried;
            exit = unflatten(tag.getIntArray("exit").orElse(new int[0]));
            graded = Graded.restore(route, approach, tag.getCompoundOrEmpty("graded"));
            far = exit.isEmpty() ? null : Graded.restore(route, exit, tag.getCompoundOrEmpty("far"));
            stage = saved;
            // What was just read is what the file already holds, so the first step after a restart owes it nothing.
            written = saved;
            resumed = true;
        }

        /** What the pass came to, once {@link #step} has said it is done. */
        Road summary() {
            return done;
        }

        /**
         * The alignment and the two profiles, once the search has finished.
         * <p>
         * The last leg is on the span's own line, so the road walks onto the abutment square rather than meeting it
         * at an angle, and the search turns in a single block: the straight lines are pulled out of its staircase and
         * every corner left is cut back into a curve, so the switchbacks are hairpins a walker can take.
         */
        private void align(ServerLevel level) {
            if (span == null) {
                alignDry(level);
                return;
            }
            List<int[]> found = descent.path();
            searched = found != null;
            if (!searched) {
                LOGGER.info("Road {}: no walked way to {}, {}, so the straight line is laid instead",
                        route.id(), span.eastBankX() + span.runIn(), span.bridgeZ());
                found = straight(route, span.eastBankX() + span.runIn(), span.bridgeZ());
            }
            List<int[]> line = new ArrayList<>(found);
            for (int x = span.eastBankX() + span.runIn() - 1; x >= span.eastBankX() + 1; x--) {
                line.add(new int[]{x, span.bridgeZ()});
            }
            approach = hairpins(level, route, simplify(line));
            exit = new ArrayList<>();
            for (int x = span.westBankX() - 1; x >= span.westBankX() - span.exitReach(); x--) exit.add(new int[]{x, span.bridgeZ()});
            int startY = endGround(level, route.pathX(), route.pathZ());
            int endY = endGround(level, span.westBankX() - span.exitReach(), span.bridgeZ());
            graded = new Graded(level, route, approach, startY, span.deckY());
            far = new Graded(level, route, exit, span.deckY(), endY);
            stage = Stage.GRADE;
        }

        /**
         * The alignment of a road with no water on it: the search's walk from the build's entry point to the far end,
         * simplified and rounded the same way, then graded once from the ground at one end to the ground at the other.
         * <p>
         * There is no run-in and no far side to split off, because there is no deck to arrive on. The far end is a
         * block a surveyor walked, and the ground there is what the grade has to meet.
         */
        private void alignDry(ServerLevel level) {
            Route.Riverless dry = (Route.Riverless) route;
            List<int[]> found = descent.path();
            searched = found != null;
            if (!searched) {
                // Said out loud rather than laid quietly: a road with no walked way is a road laid straight through
                // whatever stands between its ends, and whoever reads the log should know that is what they have.
                LOGGER.info("Road {}: no walked way to {}, {}, so the straight line is laid instead",
                        route.id(), dry.farX(), dry.farZ());
                found = straight(route, dry.farX(), dry.farZ());
            }
            approach = hairpins(level, route, simplify(found));
            exit = List.of();
            int startY = endGround(level, route.pathX(), route.pathZ());
            int endY = endGround(level, dry.farX(), dry.farZ());
            graded = new Graded(level, route, approach, startY, endY);
            far = null;
            stage = Stage.GRADE;
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
    private static int sweepColumns(ServerLevel level, Route.Crossing route, int from, int to) {
        int cleared = 0;
        int z0 = route.bridgeZ() - route.half() - 8;
        int z1 = route.bridgeZ() + route.half();
        for (int x = from; x <= to; x++) {
            for (int z = z0; z <= z1; z += 8) {
                level.getChunk(x >> 4, z >> 4);
            }
            for (int z = z0; z <= z1; z++) {
                for (int y = route.bridgeBase() - PILLAR_FLOOR; y <= route.bridgeBase() + 7; y++) {
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
     * The bridge's pieces in the order they are stood, east end first and west end last, each as x, y, z, rotation
     * and the kind of piece it is.
     * <p>
     * The two ends and the middles between them, east end turned about so its abutment faces the city.
     * <p>
     * A turned template is anchored at its own far corner, not its near one: the placement settings rotate
     * about the position handed in, so a nine-wide piece turned through half a circle occupies the nine columns
     * ending at that position rather than the nine starting from it. The east end is therefore anchored on
     * 5055 and -3071, its own north-east corner, and the middles follow from that corner east to west.
     */
    private static List<int[]> bridgePlan(ServerLevel level, Route.Crossing route) throws IOException {
        StructureTemplate end = template(level, route.endPiece());
        StructureTemplate middle = template(level, route.middlePiece());
        int z0 = route.bridgeZ() - route.half();
        int endWide = end.getSize().getX();
        List<int[]> plan = new ArrayList<>();
        plan.add(new int[]{route.eastBankX(), route.endBase(), route.bridgeZ() + route.half(),
                Rotation.CLOCKWISE_180.ordinal(), END_PIECE});

        // Each middle is anchored on its own north-east corner too, so the piece's last column is the cursor
        // itself and the next free column is one further west. Only middles that leave room for the west end
        // are laid; the end then closes whatever is left.
        int cursor = route.eastBankX() - endWide;
        // Room for one more middle and for the west end behind it: seventeen columns, not eighteen. Asking
        // for eighteen dropped the last middle and left a gap of open water in front of the far abutment.
        while (cursor - route.westBankX() + 1 >= middle.getSize().getX() + endWide) {
            plan.add(new int[]{cursor - middle.getSize().getX() + 1, route.bridgeBase(), z0,
                    Rotation.NONE.ordinal(), MIDDLE_PIECE});
            cursor -= middle.getSize().getX();
        }
        plan.add(new int[]{route.westBankX(), route.endBase(), z0, Rotation.NONE.ordinal(), END_PIECE});
        return plan;
    }

    /**
     * One of the plan's pieces, stood where the plan puts it.
     * <p>
     * A piece is a whole template - nine by nine by a few blocks of timber, every one of them placed, lit and
     * updated - so the bridge is stood a piece a step rather than as many pieces as the span takes in one tick.
     */
    private static void stand(ServerLevel level, Route.Crossing route, int[] piece) throws IOException {
        StructureTemplate template = template(level, piece[4] == END_PIECE ? route.endPiece() : route.middlePiece());
        lay(level, template, new BlockPos(piece[0], piece[1], piece[2]), Rotation.values()[piece[3]]);
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
    private static int pillarsColumns(ServerLevel level, Route.Crossing route, int from, int to) {
        int drawn = 0;
        int z0 = route.bridgeZ() - route.half();
        for (int x = from; x <= to; x++) {
            for (int z = z0; z <= z0 + route.half() * 2; z++) {
                int base = Integer.MIN_VALUE;
                for (int y = route.bridgeBase() + 6; y >= route.bridgeBase() - 2; y--) {
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
     * The price of a step is not only what it does to the height, but where it leaves the road against the grade it has
     * to finish on. A walk that priced only the change would climb a hill beside the valley that led to its own end,
     * because a gentle climb is cheap and a steep descent is not - and then it has to come down in switchbacks at the
     * last, doubling back over ground it has already crossed. Every candidate therefore pays a little for standing off
     * the straight grade between the two ends, up to {@link #PROFILE_OFF} blocks, which is enough to send the road
     * down the valley without forbidding the hill where the hill is the only way over.
     * <p>
     * A road may also run further than the country is wide, but not by any amount. The grade penalty alone will pay any
     * length to keep off one steep step, and the bench of a meadow between two hills has room for a great many long
     * ways round: a walk given no budget came back with three thousand eight hundred stations for a four-hundred-block
     * road, doubling back across the same ground until it had paved a plaza. So the walk carries how far it has run
     * and refuses a detour longer than {@link #DETOUR} times the distance between its ends, trying once more at
     * {@link #WIDER} times as much when the country genuinely needs the switchbacks a short budget will not allow.
     * <p>
     * It falls back to the straight line if the search finds nothing, so the command always lays something.
     */
    private static final class Descent {
        /** How far a walk may wander off the straight line between its ends before it is out of bounds. */
        private static final int MARGIN = 96;
        /** The most candidates one search may hold: past it the window narrows, then the line goes straight. */
        private static final long WINDOW = 600_000L;
        /**
         * How far a road may run for the distance it has to cover: two and a half times as far as the crow flies, so a
         * road that switchbacks up a fell is still a road and one that wanders a meadow is not one the walk will find.
         */
        private static final double DETOUR = 2.5D;
        /** What a diagonal step costs over a straight one: one and the root of two less, which is the eight-way guess. */
        private static final double DIAGONAL = Math.sqrt(2.0D) - 1.0D;
        /** What a step pays for standing a block off the grade between the ends, and how far off it stops counting. */
        private static final double PROFILE_PENALTY = 0.5D;
        private static final double PROFILE_OFF = 24.0D;
        /**
         * How many stations a road runs straight out of its build before the country may turn it.
         * <p>
         * A road leaves a door square to the wall, the way a path out of a gate does. Left to itself the search may
         * take its first step diagonally - and a band seven wide swinging off the door cuts the corner of a build of
         * sixteen, which is a road that took a piece of the camp as it turned. Sixteen stations is far enough out that
         * the turn is made in the country, and short enough to be a driveway rather than a road.
         */
        private static final int RUN_OUT = 16;
        /** How much further a second attempt may run, once the first found no way inside its budget at all. */
        private static final double WIDER = 3.0D;
        private final Route route;
        private final int x0;
        private final int x1;
        private final int z0;
        private final int z1;
        private final int deep;
        private final int limit;
        /** Whether a window could be held at all: a route too wide is laid as the straight line between its ends. */
        private final boolean fits;
        private final int[] ground;
        private final double[] best;
        private final int[] from;
        private final byte[] heading;
        private final boolean[] settled;
        private final PriorityQueue<double[]> open =
                new PriorityQueue<>((left, right) -> Double.compare(left[0], right[0]));
        private final int start;
        private final int goal;
        /**
         * How far each candidate has been walked to reach it, in blocks. Distance is carried apart from cost: the grade
         * penalty is what makes a way expensive, and this is what makes it long.
         */
        private final float[] travelled;
        /** How far apart the two ends are, as the walk may square it, and how much of that a first attempt may spend. */
        private final double straight;
        private double room;
        /** How many times the search has been restarted with a longer road allowed. */
        private int tries;
        /** The two ends in the world, the line between them, and the ground at each once the search has read it. */
        private final int startX;
        private final int startZ;
        private final int goalX;
        private final int goalZ;
        private final int lineX;
        private final int lineZ;
        private final double lineSq;
        /** Which way is out of the build, as one step a station: the wall the road leaves by. */
        private final int outX;
        private final int outZ;
        private double startGround = Double.NaN;
        private double goalGround = Double.NaN;
        private int looked;
        private boolean done;

        private Descent(Route route) {
            this.route = route;
            int startX = route.pathX();
            int startZ = route.pathZ();
            int goalX;
            int goalZ;
            if (route instanceof Route.Crossing crossing) {
                // The approach arrives on the span's own line, a run-in back from the east bank.
                goalX = crossing.eastBankX() + crossing.runIn();
                goalZ = crossing.bridgeZ();
            } else {
                Route.Riverless dry = (Route.Riverless) route;
                goalX = dry.farX();
                goalZ = dry.farZ();
            }
            // The road leaves the build square to its own wall, and the walk begins where that run-out stops: a
            // searched first step is a diagonal one as often as not, and a road that turns on the doorstep turns
            // through the doorstep.
            int[] out = outward(route, startX, startZ);
            this.outX = out[0];
            this.outZ = out[1];
            int fromX = startX + outX * RUN_OUT;
            int fromZ = startZ + outZ * RUN_OUT;
            int[] bounds = window(fromX, fromZ, goalX, goalZ);
            if (bounds == null) {
                this.x0 = 0;
                this.x1 = 0;
                this.z0 = 0;
                this.z1 = 0;
                this.deep = 1;
                this.limit = 0;
                this.fits = false;
                this.ground = new int[0];
                this.best = new double[0];
                this.from = new int[0];
                this.heading = new byte[0];
                this.settled = new boolean[0];
                this.travelled = new float[0];
                this.straight = 0.0D;
                this.startX = 0;
                this.startZ = 0;
                this.goalX = 0;
                this.goalZ = 0;
                this.lineX = 0;
                this.lineZ = 0;
                this.lineSq = 1.0D;
                this.start = -1;
                this.goal = -1;
                return;
            }
            this.x0 = bounds[0];
            this.x1 = bounds[1];
            this.z0 = bounds[2];
            this.z1 = bounds[3];
            this.deep = bounds[3] - bounds[2] + 1;
            int nodes = (bounds[1] - bounds[0] + 1) * deep;
            this.fits = true;
            this.limit = Math.max(120_000, nodes / 2);
            this.ground = new int[nodes];
            this.best = new double[nodes];
            this.from = new int[nodes];
            this.heading = new byte[nodes];
            this.settled = new boolean[nodes];
            this.travelled = new float[nodes];
            this.start = (fromX - x0) * deep + (fromZ - z0);
            this.goal = (goalX - x0) * deep + (goalZ - z0);
            int wide = Math.abs(goalX - startX);
            int deepApart = Math.abs(goalZ - startZ);
            this.straight = Math.max(wide, deepApart) + DIAGONAL * Math.min(wide, deepApart);
            this.room = Math.max(32.0D, straight * DETOUR);
            this.startX = startX;
            this.startZ = startZ;
            this.goalX = goalX;
            this.goalZ = goalZ;
            this.lineX = goalX - startX;
            this.lineZ = goalZ - startZ;
            this.lineSq = Math.max(1.0D, (double) lineX * lineX + (double) lineZ * lineZ);
            Arrays.fill(ground, Integer.MIN_VALUE);
            Arrays.fill(best, Double.MAX_VALUE);
            Arrays.fill(from, -1);
            Arrays.fill(heading, (byte) -1);
            Arrays.fill(travelled, Float.MAX_VALUE);
            best[start] = 0.0D;
            travelled[start] = 0.0F;
            open.add(new double[]{0.0D, start});
        }

        /**
         * The box the search may walk in: the two ends, and {@link #MARGIN} blocks either side of them.
         * <p>
         * The window used to be the valley's own constants. It is the route's now, because a road between two builds
         * is wherever those builds are - so it is measured from the ends, and halved while it would hold more
         * candidates than one server tick should pay for. A route so wide that even the narrowest window is too big
         * answers null, and the pass lays the straight line rather than reserving memory the road is unlikely to use.
         *
         * @return x0, x1, z0, z1, or null when no window fits
         */
        private static int[] window(int startX, int startZ, int goalX, int goalZ) {
            for (int margin = MARGIN; margin >= 8; margin /= 2) {
                int x0 = Math.min(startX, goalX) - margin;
                int x1 = Math.max(startX, goalX) + margin;
                int z0 = Math.min(startZ, goalZ) - margin;
                int z1 = Math.max(startZ, goalZ) + margin;
                if ((long) (x1 - x0 + 1) * (z1 - z0 + 1) <= WINDOW) return new int[]{x0, x1, z0, z1};
            }
            return null;
        }

        /** Settles up to {@code polls} candidates; false once there is nothing more to settle. */
        private boolean step(ServerLevel level, int polls) {
            if (!fits || done) return false;
            if (Double.isNaN(startGround)) {
                startGround = solid(surfaceAt(level, startX, startZ));
                goalGround = solid(surfaceAt(level, goalX, goalZ));
            }
            for (int poll = 0; poll < polls; poll++) {
                if (open.isEmpty() || looked >= limit) {
                    // What stopped it may have been the budget rather than the country: a road up a fell needs more
                    // than a meadow's worth of switchbacks. One longer attempt, and then the walk is what it is.
                    if (tries == 0 && from[goal] == -1) {
                        widen();
                        continue;
                    }
                    done = true;
                    break;
                }
                int node = (int) open.poll()[1];
                if (settled[node]) continue;
                settled[node] = true;
                looked++;
                if (node == goal) {
                    done = true;
                    break;
                }
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
                        if (nx >= route.cityX0() && nx <= route.cityX1() && nz >= route.cityZ0() && nz <= route.cityZ1()) continue;
                        int at = (nx - x0) * deep + (nz - z0);
                        if (settled[at]) continue;
                        if (ground[at] == Integer.MIN_VALUE) ground[at] = surfaceAt(level, nx, nz);
                        if (ground[at] == WATER) continue;
                        int change = Math.abs(ground[at] - ground[node]);
                        double length = Math.sqrt(dx * dx + dz * dz);
                        double over = Math.max(0.0D, change - TARGET_GRADE * length);
                        double cost = best[node] + length + GRADE_PENALTY * over * over;
                        if (heading[node] != -1 && heading[node] != dir) cost += TURN_COST;
                        if (!Double.isNaN(startGround) && !Double.isNaN(goalGround)) {
                            double along = ((nx - startX) * lineX + (nz - startZ) * lineZ) / lineSq;
                            along = Math.max(0.0D, Math.min(1.0D, along));
                            double expect = startGround + (goalGround - startGround) * along;
                            cost += PROFILE_PENALTY
                                    * Math.min(Math.abs(ground[at] - expect), PROFILE_OFF);
                        }
                        float ran = travelled[node] + (float) length;
                        if (ran > room) continue;
                        if (cost >= best[at]) continue;
                        best[at] = cost;
                        travelled[at] = ran;
                        from[at] = node;
                        heading[at] = dir;
                        // The guess is the distance left as the walk may actually cover it. It may step in eight
                        // directions, and a diagonal step is 1.414 of a straight one: measured four ways the guess
                        // overrates every diagonal, which settles a station by a road the walk would not have chosen.
                        int gx = Math.abs(goal / deep - at / deep);
                        int gz = Math.abs(goal % deep - at % deep);
                        double guess = Math.max(gx, gz) + DIAGONAL * Math.min(gx, gz);
                        open.add(new double[]{cost + guess, at});
                    }
                }
            }
            return !done;
        }

        /** A height a grade can be read from, or nothing at all when that ground is water or has not been made. */
        private static double solid(int ground) {
            return ground == WATER || ground == VOID ? Double.NaN : ground;
        }

        /**
         * Starts the search again with a longer road allowed, once a first attempt found no way inside its budget: the
         * country may genuinely need the switchbacks a meadow's worth of road will not buy.
         */
        private void widen() {
            tries++;
            room *= WIDER;
            Arrays.fill(best, Double.MAX_VALUE);
            Arrays.fill(from, -1);
            Arrays.fill(heading, (byte) -1);
            Arrays.fill(settled, false);
            Arrays.fill(travelled, Float.MAX_VALUE);
            best[start] = 0.0D;
            travelled[start] = 0.0F;
            looked = 0;
            open.clear();
            open.add(new double[]{0.0D, start});
        }

        /**
         * Which way is out of the build, from the block the road starts on: the side of the build's own footprint that
         * block stands beyond, and nothing when it stands inside the footprint and says nothing about the way out.
         * <p>
         * A corner reads as the side it is furthest beyond, which for a road leaving a door beside a corner is the
         * side the door faces; a footprint the start is exactly on answers with that side rather than with none, since
         * the city's own path ends on its wall and the road still leaves westward from it.
         */
        private static int[] outward(Route route, int x, int z) {
            int west = route.cityX0() - x;
            int east = x - route.cityX1();
            int north = route.cityZ0() - z;
            int south = z - route.cityZ1();
            int most = Math.max(Math.max(west, east), Math.max(north, south));
            if (most < 0) return new int[]{0, 0};
            if (most == west) return new int[]{-1, 0};
            if (most == east) return new int[]{1, 0};
            if (most == north) return new int[]{0, -1};
            return new int[]{0, 1};
        }

        /** The walk that was found, or null when the search never reached the bend. */
        private List<int[]> path() {
            if (!fits) return null;
            if (goal != start && from[goal] == -1) return null;
            List<int[]> path = new ArrayList<>();
            int node = goal;
            while (node >= 0) {
                path.add(new int[]{x0 + node / deep, z0 + node % deep});
                node = from[node];
            }
            Collections.reverse(path);
            // The way out of the build is the build's own: the stations from its door to the end of the run-out are
            // square to the wall it leaves by, and the searched way starts where they stop.
            List<int[]> whole = new ArrayList<>(path.size() + RUN_OUT);
            if (outX != 0 || outZ != 0) {
                for (int step = 0; step <= RUN_OUT; step++) {
                    whole.add(new int[]{route.pathX() + outX * step, route.pathZ() + outZ * step});
                }
            }
            for (int[] station : path) {
                if (!whole.isEmpty() && whole.get(whole.size() - 1)[0] == station[0]
                        && whole.get(whole.size() - 1)[1] == station[1]) {
                    continue;
                }
                whole.add(station);
            }
            return whole;
        }
    }

    /**
     * Gives a hole in the ground the level of the ground around it, so the road goes over it rather than down into it.
     * <p>
     * A cave's mouth, a ravine's lip, the shaft a stronghold left: each is a column that drops away and comes back
     * within a few blocks, and read as ground it is a grade the road dutifully takes. The profile then dives into the
     * hole, the band cannot be filled to follow it, and the road stops at the lip with a gap beyond it - which is not a
     * road that stopped, it is a road nobody asked what was under it. A column is a hole when the ground on both sides
     * stands {@link #HOLE_DEPTH} above it and the two sides agree with each other; a fall line has one side high and
     * one side low, and is left exactly as it is.
     */
    private static void carryOver(int[] ground) {
        int[] was = ground.clone();
        for (int i = 0; i < ground.length; i++) {
            if (was[i] == WATER || was[i] == VOID) continue;
            for (int reach = 1; reach <= HOLE_REACH && i - reach >= 0 && i + reach < ground.length; reach++) {
                int left = was[i - reach];
                int right = was[i + reach];
                if (left == WATER || right == WATER || left == VOID || right == VOID) break;
                if (left < was[i] + HOLE_DEPTH || right < was[i] + HOLE_DEPTH) continue;
                if (Math.abs(left - right) > HOLE_DEPTH) break;
                ground[i] = Math.max(left, right);
                break;
            }
        }
    }

    /**
     * The ground a station answers to: the middle of the dry ground within {@link #RELIEF_REACH} stations of it.
     * <p>
     * The middle rather than the mean, because the whole point is to ignore what is not country: one knoll or one
     * cave's mouth in the window cannot move a middle, and it moves a mean by every block of itself. Water is left out
     * - the river is the bridge's - and a window with no dry ground in it at all falls back to the height the caller
     * names.
     */
    private static int reliefAt(int[] ground, int at, int fallback) {
        int[] seen = new int[2 * RELIEF_REACH + 1];
        int count = 0;
        for (int i = Math.max(0, at - RELIEF_REACH); i <= Math.min(ground.length - 1, at + RELIEF_REACH); i++) {
            if (ground[i] == WATER || ground[i] == VOID) continue;
            seen[count++] = ground[i];
        }
        if (count == 0) return fallback;
        Arrays.sort(seen, 0, count);
        return seen[count / 2];
    }

    /**
     * Gives a station whose ground could not be read the ground of the nearest station that has some: forward along the
     * line, so a stretch the world has not made yet runs on from the last ground the road actually stood on.
     */
    private static void fillVoid(int[] ground, int fallback) {
        int last = fallback;
        for (int i = 0; i < ground.length; i++) {
            if (ground[i] == VOID) {
                ground[i] = last;
            } else if (ground[i] != WATER) {
                last = ground[i];
            }
        }
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
    private static final class Graded {
        private final Route route;
        private final List<int[]> line;
        private final int n;
        private final int[] ground;
        /**
         * The ground each station answers to: the middle of the dry ground either side of it, so a bump or a hole in
         * one column is not built into the road. Laid over rather than followed - a hole is filled on stone.
         */
        private final int[] relief;
        private final double[] height;
        private final Map<Long, Integer> tops = new LinkedHashMap<>();
        /** Every column laid, in the order it was laid; taken once the lips are asked for, not while laying. */
        private List<Long> order;
        private int at;
        /** How many of the laid columns the lip pass has walked. */
        private int lipsAt;
        private int laid;
        private int cut;
        private int filled;
        private int open;
        private int steepest;
        private int cleared;

        private Graded(ServerLevel level, Route route, List<int[]> line, int startY, int endY) {
            this.route = route;
            this.line = line;
            this.n = line.size();
            this.ground = new int[n];
            this.relief = new int[n];
            this.height = new double[n];
            if (n < 2) return;
            for (int i = 0; i < n; i++) ground[i] = surfaceAt(level, line.get(i)[0], line.get(i)[1]);
            // Read before the profile is settled, not after: a hole the grade has already dived into is a hole the
            // grade cannot be talked out of, because its own heights are what the smoothing is pulling from.
            carryOver(ground);
            // A station whose ground could not be read is given the ground of the nearest station that could: the grade
            // then runs on from country it knows rather than into the floor of the world.
            fillVoid(ground, startY);
            for (int i = 0; i < n; i++) relief[i] = reliefAt(ground, i, startY);
            for (int i = 0; i < n; i++) height[i] = relief[i];
            if (startY != VOID && startY != WATER) height[0] = startY;
            if (endY != VOID && endY != WATER) height[n - 1] = endY;
            for (int pass = 0; pass < PROFILE_PASSES; pass++) {
                for (int i = 1; i < n - 1; i++) {
                    height[i] = (height[i - 1] + 2.0D * height[i] + height[i + 1]) / 4.0D;
                }
                for (int i = 1; i < n; i++) {
                    height[i] = Mth.clamp(height[i], height[i - 1] - MAX_STEP, height[i - 1] + MAX_STEP);
                }
                for (int i = n - 2; i >= 0; i--) {
                    height[i] = Mth.clamp(height[i], height[i + 1] - MAX_STEP, height[i + 1] + MAX_STEP);
                }
                for (int i = 1; i < n - 1; i++) {
                    height[i] = Mth.clamp(height[i], relief[i] - CUT_FILL, relief[i] + CUT_FILL);
                }
                if (startY != VOID && startY != WATER) height[0] = startY;
                if (endY != VOID && endY != WATER) height[n - 1] = endY;
            }
            for (int settle = 0; settle < SETTLE_PASSES; settle++) {
                for (int i = 1; i < n; i++) {
                    height[i] = Mth.clamp(height[i], height[i - 1] - MAX_STEP, height[i - 1] + MAX_STEP);
                }
                for (int i = n - 2; i >= 1; i--) {
                    height[i] = Mth.clamp(height[i], height[i + 1] - MAX_STEP, height[i + 1] + MAX_STEP);
                }
            }
            // The ground has the last word: whatever the sweeps preferred, no station stands more than the road's own
            // budget above the ground it answers to or under it. A hill is walked over, not bored through, a hole is
            // carried over on stone instead of dived into, and a profile that was free to cut a station fifteen blocks
            // under a ridge is held to what a path would actually take off.
            for (int i = 1; i < n - 1; i++) {
                height[i] = Mth.clamp(height[i], relief[i] - CUT_FILL, relief[i] + CUT_FILL);
            }
            // And the steps once more, because the ground's word is a band and not a cliff: wherever the ground under
            // two neighbouring stations was read differently, the clamp above can leave them more than a step apart,
            // and a step is the one thing the road cannot be laid over.
            for (int settle = 0; settle < SETTLE_PASSES; settle++) {
                for (int i = 1; i < n; i++) {
                    height[i] = Mth.clamp(height[i], height[i - 1] - MAX_STEP, height[i - 1] + MAX_STEP);
                }
                for (int i = n - 2; i >= 1; i--) {
                    height[i] = Mth.clamp(height[i], height[i + 1] - MAX_STEP, height[i + 1] + MAX_STEP);
                }
            }
        }

        /** A road with no grade yet: the shape a restored one fills its carried heights into. */
        private Graded(Route route, List<int[]> line) {
            this.route = route;
            this.line = line;
            this.n = line.size();
            this.ground = new int[n];
            this.relief = new int[n];
            this.height = new double[n];
        }

        /** The grade this road settled on, how much of it is laid, and the terraces it has drawn, as a restart needs. */
        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            long[] heights = new long[n];
            for (int i = 0; i < n; i++) heights[i] = Double.doubleToRawLongBits(height[i]);
            tag.putLongArray("height", heights);
            tag.putInt("at", at);
            tag.putInt("laid", laid);
            tag.putInt("cut", cut);
            tag.putInt("filled", filled);
            tag.putInt("open", open);
            tag.putInt("steepest", steepest);
            tag.putInt("cleared", cleared);
            tag.putInt("lips", lipsAt);
            long[] drawn = new long[tops.size() * 2];
            int index = 0;
            for (Map.Entry<Long, Integer> top : tops.entrySet()) {
                drawn[index++] = top.getKey();
                drawn[index++] = top.getValue();
            }
            tag.putLongArray("tops", drawn);
            return tag;
        }

        /**
         * The same road, put back where its grade left it.
         * <p>
         * Its heights are carried rather than read again: the ground under half a laid road is no longer the ground the
         * grade was settled against, and reading it afresh would pull the profile about against the part already walked.
         */
        private static Graded restore(Route route, List<int[]> line, CompoundTag tag) {
            Graded graded = new Graded(route, line);
            long[] heights = tag.getLongArray("height").orElse(new long[0]);
            if (heights.length == graded.n) {
                for (int i = 0; i < graded.n; i++) graded.height[i] = Double.longBitsToDouble(heights[i]);
            }
            graded.at = Math.clamp(tag.getIntOr("at", 0), 0, graded.n);
            graded.laid = tag.getIntOr("laid", 0);
            graded.cut = tag.getIntOr("cut", 0);
            graded.filled = tag.getIntOr("filled", 0);
            graded.open = tag.getIntOr("open", 0);
            graded.steepest = tag.getIntOr("steepest", 0);
            graded.cleared = tag.getIntOr("cleared", 0);
            long[] drawn = tag.getLongArray("tops").orElse(new long[0]);
            for (int i = 0; i + 1 < drawn.length; i += 2) graded.tops.put(drawn[i], (int) drawn[i + 1]);
            graded.lipsAt = Math.clamp(tag.getIntOr("lips", 0), 0, graded.tops.size());
            return graded;
        }

        /**
         * Lays whole stations until the step's budget of columns is spent.
         *
         * @return the budget left for the other road of the pass, which is none when this one spent it all
         */
        private int pave(ServerLevel level, int budget) {
            if (n < 2) return budget;
            while (at < n && budget > 0) budget -= lay(level, at++);
            return budget;
        }

        /** Whether every station of this road is laid. */
        private boolean laid() {
            return n < 2 || at >= n;
        }

        /**
         * Turns the edge of a terrace that stands one block over the next into a bottom slab, so a step is made of
         * two half steps. Only road columns count: a step beside a wall is the wall's business, not the road's.
         * <p>
         * The slab belongs to the higher column. A bottom slab fills the bottom of its cell, so in the lower column
         * it sits a block and a half under the terrace above it: a walker drops a step and a half onto it, and then
         * still has to jump to come back up. In the higher column it is a lip at the terrace's edge, half a block
         * under the floor, and the terrace below the lip is the other half. Read either way along the road that is
         * block, slab, block, slab - a stair a walker takes without breaking stride.
         * <p>
         * Every side of the terrace is checked. Comparing only the two positive ones put the slab on whichever side
         * of the step happened to face the axes and none on the other two, so a diagonal step carried a sliver of
         * slab off one corner instead of a lip along its whole edge.
         * <p>
         * Walked a budget of columns at a time, in the order they were laid, because the whole road at once is tens
         * of thousands of them and the tick after the last station was paying for every one.
         *
         * @return the budget left for the other road of the pass
         */
        private int lips(ServerLevel level, int budget) {
            List<Long> order = order();
            int from = lipsAt;
            int upto = Math.min(order.size(), from + budget);
            for (; lipsAt < upto; lipsAt++) {
                long key = order.get(lipsAt);
                Integer top = tops.get(key);
                if (top == null) continue;
                int x = (int) (key >> 32);
                int z = (int) (key & 0xFFFFFFFFL);
                for (int[] beside : BESIDE) {
                    Integer other = tops.get(key(x + beside[0], z + beside[1]));
                    if (other == null || top != other + 1) continue;
                    level.setBlock(new BlockPos(x, top, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2);
                    break;
                }
            }
            return budget - (upto - from);
        }

        /** Whether every laid column has been looked at for a lip. */
        private boolean lipped() {
            return n < 2 || lipsAt >= order().size();
        }

        private List<Long> order() {
            if (order == null) order = new ArrayList<>(tops.keySet());
            return order;
        }

        /** The station this road has got to, clamped to its own line so a finished road still answers where it ends. */
        int[] head() {
            if (n < 2) return new int[]{route.pathX(), route.pathZ()};
            return line.get(Math.clamp(at, 0, n - 1));
        }

        private int[] summary() {
            return new int[]{laid, cut, filled, open, steepest, cleared};
        }

        /**
         * Puts this station on ground the plan did not know, and eases the stations after it back onto the grade the
         * plan had, a step at a time: meeting the country ahead is a climb, not a cliff and not a road that stops.
         */
        private void retarget(int i, int held) {
            height[i] = held;
            double value = held;
            for (int j = i + 1; j < n; j++) {
                if (height[j] > value + MAX_STEP) {
                    height[j] = value + MAX_STEP;
                } else if (height[j] < value - MAX_STEP) {
                    height[j] = value - MAX_STEP;
                } else {
                    return;
                }
                value = height[j];
            }
        }

        /**
         * One station: its height, its width, and every column of it cut or filled to the grade.
         *
         * @return the columns the station walked, laid or left open, which is what the step's budget is spent on
         */
        private int lay(ServerLevel level, int i) {
            int counted = 0;
            int x = line.get(i)[0], z = line.get(i)[1];
            // The ground under the station is read before anything is written: the grade was settled from the world as
            // it stood when the road was planned, and the country a station is met in may not have been made then. Where
            // that ground stands more than a road's cut above the plan the grade is lifted to it a step at a time, and
            // the stations after are eased back onto the plan - which is what turns a road ending at its own trench
            // into a road climbing the bank in front of it.
            level.getChunk(x >> 4, z >> 4);
            int under = surfaceAt(level, x, z);
            // A grade saved by a pass that read its ground off chunks nobody had made can carry a height that is not a
            // height in this world - the floor of it - and believing that would lay the rest of the road under the
            // surface it never reaches. A station holding one takes the ground under it instead.
            if (Mth.floor(height[i]) < level.getMinY() || Mth.floor(height[i]) > level.getMaxY()) {
                int before = i > 0 ? Mth.floor(height[i - 1]) : under;
                boolean known = before >= level.getMinY() && before <= level.getMaxY();
                int here = under != VOID && under != WATER ? under : before;
                retarget(i, known ? Math.min(here, before + (int) MAX_STEP) : here);
            }
            if (under != VOID && under != WATER && Mth.floor(height[i]) < under - CUT_FILL) {
                int before = i > 0 ? Mth.floor(height[i - 1]) : under - CUT_FILL;
                int lifted = Math.min(under - CUT_FILL, before + (int) MAX_STEP);
                if (lifted > Mth.floor(height[i])) retarget(i, lifted);
            }
            int target = Mth.floor(height[i]);
            if (i > 0) steepest = Math.max(steepest, Math.abs(target - Mth.floor(height[i - 1])));
            int half = fromWalls(route, x, z) <= TRANSITION_BAND ? PAVED_HALF : WILD_HALF;
            // A hairpin is wider than a run: the apron gives the turn somewhere to be walked around.
            if (turning(line, i)) half += 2;
            for (int dx = -half; dx <= half; dx++) {
                for (int dz = -half; dz <= half; dz++) {
                    counted++;
                    int at = x + dx;
                    int into = z + dz;
                    // The build is the build's own ground. A band is seven wide, so a station one block outside a
                    // wall reached three blocks into it, which is how a road turning out of a door took the corner of
                    // the camp with it. Nothing inside the footprint is cut, filled, cleared or paved: the road meets
                    // the wall and the wall is the build's.
                    if (at >= route.cityX0() && at <= route.cityX1()
                            && into >= route.cityZ0() && into <= route.cityZ1()) {
                        continue;
                    }
                    if (half == WILD_HALF && (Math.abs(dx) == WILD_HALF || Math.abs(dz) == WILD_HALF)
                            && jitter(at, into) % 3 != 0) {
                        continue;
                    }
                    // The column is read as the world has it, and the world has to have it: a chunk nobody has made
                    // yet reads as nothing at all, and a block written into it would be dropped anyway.
                    level.getChunk(at >> 4, into >> 4);
                    int groundAt = surfaceAt(level, at, into);
                    if (groundAt == VOID) {
                        level.setBlock(new BlockPos(at, target, into), surface(route, at, into), 2);
                        tops.put(key(at, into), target);
                        laid++;
                        continue;
                    }
                    if (groundAt == WATER) {
                        // The river is the bridge's. Only the road's own three columns are carried over it on
                        // stone, and only where the grade stands above the water line.
                        if (Math.abs(dx) > 1 || Math.abs(dz) > 1) {
                            open++;
                            continue;
                        }
                        int waterTop = level.getHeight(Heightmap.Types.MOTION_BLOCKING, at, into) - 1;
                        if (target <= waterTop) continue;
                        for (int y = waterTop; y < target; y++) {
                            level.setBlock(new BlockPos(at, y, into), Blocks.STONE.defaultBlockState(), 2);
                            filled++;
                        }
                        level.setBlock(new BlockPos(at, target, into), surface(route, at, into), 2);
                        tops.put(key(at, into), target);
                        laid++;
                        continue;
                    }
                    int step = target - groundAt;
                    // Ground above the grade is cut no deeper than CUT_FILL: a hill standing that far over a profile
                    // is a hill the profile was wrong about, and a road that bores into it is a trench. Ground below
                    // the grade is filled up to it however deep the hollow - that is the other half of the same
                    // question, and refusing it was the road stopping at the first hole in the ground, since the
                    // profile had already been carried over that hole and the band's own columns had not.
                    if (step < -CUT_FILL || step > HOLE_FILL) {
                        open++;
                        continue;
                    }
                    level.getChunk(at >> 4, into >> 4);
                    BlockPos column = new BlockPos(at, target, into);
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
                    level.setBlock(column, surface(route, at, into), 2);
                    tops.put(key(at, into), target);
                    laid++;
                    // A road cut through a wood leaves its canopy hanging, so the corridor's own columns are
                    // cleared of leaves and logs above the road's surface.
                    for (int y = target + 1; y <= target + CANOPY_CLEAR; y++) {
                        BlockPos crown = new BlockPos(at, y, into);
                        BlockState over = level.getBlockState(crown);
                        if (!over.is(BlockTags.LEAVES) && !over.is(BlockTags.LOGS)) continue;
                        level.setBlock(crown, Blocks.AIR.defaultBlockState(), 2);
                        cleared++;
                    }
                }
            }
            return counted;
        }
    }

    /**
     * Pulls the straight lines out of the searched walk and rounds what is left into corners a walker can take.
     * <p>
     * The search moves a block at a time and turns within a block, so raw it is an eight-direction staircase
     * whose corners are right angles - nothing a walker can take at speed. Two passes fix that: a
     * Douglas-Peucker simplification keeps only the vertices that carry the shape, and every vertex left is cut
     * back along both of its legs and joined by a curve.
     * <p>
     * How far back is the difference between a road and a hinge. An ordinary bend is eased as wide as its legs
     * allow, up to {@link #EASE}, so a walker carries speed round it; a corner that doubles back is a
     * switchback and stays tight at {@link #FILLET}, because easing one wide would swing the road off the shelf
     * the switchback exists to stay on.
     */
    private static List<int[]> hairpins(ServerLevel level, Route route, List<int[]> line) {
        if (line.size() < 3) return line;
        List<int[]> out = new ArrayList<>();
        out.add(line.get(0));
        for (int k = 1; k < line.size() - 1; k++) {
            int[] back = line.get(k - 1);
            int[] corner = line.get(k);
            int[] next = line.get(k + 1);
            double in = Math.hypot(corner[0] - back[0], corner[1] - back[1]);
            double out2 = Math.hypot(next[0] - corner[0], next[1] - corner[1]);
            if (in < 1.0D || out2 < 1.0D) continue;
            boolean doubling = (corner[0] - back[0]) * (next[0] - corner[0])
                    + (corner[1] - back[1]) * (next[1] - corner[1]) < HAIRPIN * in * out2;
            int widest = (int) Math.max(1.0D,
                    Math.min(doubling ? FILLET : EASE, Math.min(in / 2.0D, out2 / 2.0D)));
            // An eased corner reaches ground the search never walked, so the arc goes out only as far as it
            // can: closed back until it stays off the river, out of the city's footprint, and on ground a
            // corner's own cut and fill could answer for.
            int cornerGround = surfaceAt(level, corner[0], corner[1]);
            int cut = widest;
            List<int[]> curve = eased(back, corner, next, cut);
            while (cut > 1 && !clear(level, route, curve, cornerGround)) {
                cut = Math.max(1, cut / 2);
                curve = eased(back, corner, next, cut);
            }
            for (int[] at : curve) walkTo(out, at[0], at[1]);
        }
        int[] last = line.get(line.size() - 1);
        walkTo(out, last[0], last[1]);
        return out;
    }

    /**
     * One corner's stations: the last {@code cut} blocks of each leg replaced by the curve that joins them.
     * <p>
     * The curve is sampled finely enough that its chords are a couple of blocks long, so the road rounds the
     * bend smoothly rather than in facets - and the samples only set the line, which {@link #walkTo} then fills
     * in a block at a time.
     */
    private static List<int[]> eased(int[] back, int[] corner, int[] next, int cut) {
        int[] from = along(back, corner, cut);
        int[] to = along(corner, next, cut);
        List<int[]> curve = new ArrayList<>();
        curve.add(from);
        int steps = Math.max(FILLET_STEPS, cut);
        for (int s = 1; s <= steps; s++) {
            double t = (double) s / (steps + 1);
            double u = 1.0D - t;
            curve.add(new int[]{
                    (int) Math.round(u * u * from[0] + 2.0D * u * t * corner[0] + t * t * to[0]),
                    (int) Math.round(u * u * from[1] + 2.0D * u * t * corner[1] + t * t * to[1])});
        }
        curve.add(to);
        return curve;
    }

    /**
     * Whether a corner's curve can be laid where it stands.
     * <p>
     * Easing a corner puts road where the search chose not to walk - across the inside of the bend - so the
     * arc is only allowed as far as it reaches ground the road may be laid on at all: never the river, which
     * is the bridge's, never the city's own footprint, and never ground so far above or below the corner that
     * meeting it would take more cut or fill than the corner itself stands on.
     */
    private static boolean clear(ServerLevel level, Route route, List<int[]> curve, int cornerGround) {
        for (int[] at : curve) {
            if (at[0] >= route.cityX0() && at[0] <= route.cityX1() && at[1] >= route.cityZ0() && at[1] <= route.cityZ1()) return false;
            if (cornerGround == WATER) continue;
            int ground = surfaceAt(level, at[0], at[1]);
            if (ground == WATER) return false;
            if (Math.abs(ground - cornerGround) > CUT_FILL) return false;
        }
        return true;
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
        // Douglas-Peucker keeps the bends and drops the straight, and a line with no stations in it is a road with no
        // band in it. A fallback line is nothing but straights: simplified, it came out as two stations four hundred
        // blocks apart, and the grade built a stub at either end and called the road laid. One station kept in every
        // four covers a straight run and leaves a curve exactly as the tolerance drew it.
        for (int i = 0; i < line.size(); i += 4) keep[i] = true;
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

    /**
     * Whether the alignment is doubling back here, which is where the surface is widened into a hairpin's
     * apron.
     * <p>
     * Only a switchback earns the apron. A road that is merely cornering - the right angle onto the bridge's
     * own line, say - is laid at its ordinary width, because widening every bend of more than thirty degrees
     * made the bank bulge out over the river for no reason a walker could see.
     */
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
        return (ax * bx + az * bz) / (la * lb) < HAIRPIN;
    }

    /** The straight line between the road's two ends, for when the search finds no way. */
    private static List<int[]> straight(Route route, int toX, int toZ) {
        List<int[]> line = new ArrayList<>();
        int steps = Math.max(2, Math.abs(route.pathX() - toX) + 1);
        for (int i = 0; i < steps; i++) {
            line.add(new int[]{
                    route.pathX() + (int) Math.round((double) (toX - route.pathX()) * i / steps),
                    route.pathZ() + (int) Math.round((double) (toZ - route.pathZ()) * i / steps)});
        }
        line.add(new int[]{toX, toZ});
        return line;
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
        if (state.isAir()) return VOID;
        if (!state.getFluidState().isEmpty()) return WATER;
        return y;
    }

    /**
     * The ground at one of a road's own ends.
     * <p>
     * The ends used to be read from the plain heightmap, which counts a tree's leaves. A road that began under a
     * canopy therefore began at the top of the wood, and its grade spent the first thirty stations coming down out
     * of a tree: a staircase of stone standing in the air with the path laid along its top, ending at nothing. Every
     * other column of the same road is measured by {@link #surfaceAt}, which walks down past the wood, so the ends
     * are measured the same way and the grade is the ground's from its first block.
     */
    private static int endGround(ServerLevel level, int x, int z) {
        int ground = surfaceAt(level, x, z);
        if (ground == VOID) return VOID;
        return ground == WATER ? level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1 : ground;
    }

    /**
     * The surface of one block: the three bands of `docs/arda-roads.md`, chosen per block from a hash of x
     * and z so that a chunk border never shows, and so the same column takes the same block whenever asked.
     */
    private static BlockState surface(Route route, int x, int z) {
        int near = fromWalls(route, x, z);
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
    private static int fromWalls(Route route, int x, int z) {
        int dx = Math.max(0, Math.max(route.cityX0() - x, x - route.cityX1()));
        int dz = Math.max(0, Math.max(route.cityZ0() - z, z - route.cityZ1()));
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

    /** A line of stations as one int array, and back: two numbers a station, which is all a station is. */
    private static int[] flatten(List<int[]> line) {
        int[] flat = new int[line.size() * 2];
        for (int i = 0; i < line.size(); i++) {
            flat[i * 2] = line.get(i)[0];
            flat[i * 2 + 1] = line.get(i)[1];
        }
        return flat;
    }

    private static List<int[]> unflatten(int[] flat) {
        List<int[]> line = new ArrayList<>(flat.length / 2);
        for (int i = 0; i + 1 < flat.length; i += 2) line.add(new int[]{flat[i], flat[i + 1]});
        return line;
    }

    /**
     * The passes this world has started and has not finished.
     * <p>
     * A pass is a long job - a search over the ground and then the grading of a road several hundred blocks long, eight
     * stations a step - and a world is often stopped in the middle of one. It used to begin again from its first stage
     * on the next start: a bridge swept and stood again, contours searched again for a road already half cut. What a
     * pass has done is written here instead, so the next start picks the road up where it was left.
     * <p>
     * The search is the one stage not written down, for the reason its own javadoc gives: its state is a map of a
     * hundred thousand candidate blocks, and a world stopped inside it starts that stage again. Everything after it -
     * the bridge, the pillars, the alignment and both graded roads - is carried.
     */
    static final class Progress extends SavedData {
        private static final String NAME = "roads";
        private static final Map<String, CompoundTag> ROADS = new HashMap<>();

        static final Codec<Progress> CODEC = CompoundTag.CODEC.xmap(Progress::read, Progress::write);

        static final SavedDataType<Progress> TYPE = new SavedDataType<>(
                Identifier.fromNamespaceAndPath(MODID, NAME), Progress::new, CODEC);

        /** A world with no progress file has no road half-laid, and forgets whatever the last world began. */
        private Progress() {
            this(true);
        }

        private Progress(boolean fresh) {
            super();
            if (fresh) ROADS.clear();
        }

        private static Progress read(CompoundTag tag) {
            ROADS.clear();
            for (String route : tag.keySet()) ROADS.put(route, tag.getCompoundOrEmpty(route));
            return new Progress(false);
        }

        private CompoundTag write() {
            CompoundTag tag = new CompoundTag();
            ROADS.forEach(tag::put);
            return tag;
        }

        private static Progress instance(ServerLevel level) {
            ServerLevel overworld = level.getServer() == null ? null : level.getServer().getLevel(Level.OVERWORLD);
            if (overworld == null) return new Progress(false);
            return overworld.getDataStorage().computeIfAbsent(TYPE);
        }

        /** One alignment as the note carries it: the stations' x and z, in the order they were laid. */
        private static int[] flatten(List<int[]> line) {
            if (line == null) return new int[0];
            int[] out = new int[line.size() * 2];
            for (int i = 0; i < line.size(); i++) {
                out[i * 2] = line.get(i)[0];
                out[i * 2 + 1] = line.get(i)[1];
            }
            return out;
        }

        /** A grade's heights as the note carries them, so the shape of the road can be read back as numbers. */
        private static long[] bits(double[] height) {
            if (height == null) return new long[0];
            long[] out = new long[height.length];
            for (int i = 0; i < height.length; i++) out[i] = Double.doubleToLongBits(height[i]);
            return out;
        }

        /** Where a pass on this route had got to, or null when nobody has begun one. */
        private static CompoundTag of(ServerLevel level, String route) {
            instance(level);
            return ROADS.get(route);
        }

        /** Writes down where a pass has got to; a finished pass leaves nothing behind to carry. */
        private static void remember(RoadJob job, ServerLevel level) {
            CompoundTag tag = job.save();
            if (tag == null) {
                // A finished pass leaves a note rather than nothing. Without it a record could not tell a road that
                // was down from one never begun once it had vanished, so the world's dressing kept that answer in a
                // marker file beside the cut - and a marker written after a pass that failed said the crossing was
                // laid when it was not, and no pass was ever planned over it again.
                tag = new CompoundTag();
                tag.putBoolean("down", true);
                // Where the road met the build, and what the ground under it was, so the note can be checked rather
                // than believed: see standing.
                // The chunk is made before its ground is asked for. A height read off a chunk nobody has made is the
                // floor of the world, and the note written from one said the road met its build at y -65 - a block of
                // nothing in a place nobody would look, and a block that the check below would go on comparing.
                level.getChunk(job.route.pathX() >> 4, job.route.pathZ() >> 4);
                BlockPos entry = new BlockPos(job.route.pathX(), level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        job.route.pathX(), job.route.pathZ()) - 1, job.route.pathZ());
                tag.putIntArray("at", new int[]{entry.getX(), entry.getY(), entry.getZ()});
                tag.putString("block", BuiltInRegistries.BLOCK.getKey(
                        level.getBlockState(entry).getBlock()).toString());
                // And the way the road took to get there. The note used to keep where the road met the build and
                // nothing of the road itself, so a pass that finished took its alignment with it and a laid road was a
                // road nobody could read: no station count, no way to say where it doubled back or how high it had
                // climbed. The record is a few kilobytes against a road of hundreds of blocks, and it is the only
                // account a finished road leaves.
                tag.putIntArray("approach", flatten(job.approach));
                if (job.graded != null) tag.putLongArray("height", bits(job.graded.height));
                if (job.done != null) tag.putInt("stations", job.done.along());
            }
            ROADS.put(job.route.id(), tag);
            instance(level).setDirty();
        }
    }
}
