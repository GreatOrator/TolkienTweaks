package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
    /** Where a pass says what each of its stages cost, which is how the step budgets are checked against real ticks. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoadCommands.class);
    /** Which of the bridge's two templates a planned piece is: the end of the span, or a middle between the ends. */
    private static final int END_PIECE = 0;
    private static final int MIDDLE_PIECE = 1;

    /**
     * How far out from a build its road stops being the town's own street, and then stops being a road at all.
     * <p>
     * Three bands, and a width and a surface for each: the town's own paving seven wide within {@link #URBAN_BAND}, the
     * approach five wide of cobble and path out to {@link #TRANSITION_BAND}, and the country's own track three wide
     * beyond it. They were sixteen and sixty-four; thirty-two and ninety-six are the shape a gate wants - a street that
     * is still a street a bowshot out of the wall, an approach that reaches the first rise - and both are measured from
     * the build's footprint where the road carries one, or from the block it leaves by where it does not.
     */
    private static final int URBAN_BAND = 32;
    private static final int TRANSITION_BAND = 96;

    /** How many passes of the smoothing filter the profile takes, each reaching one more station either way. */
    private static final int PROFILE_PASSES = 8;
    /**
     * The deepest cut or tallest fill one column may take before the road is held back to the ground, where the route
     * has asked for nothing deeper of its own: see {@link Roads#cut}, which is a surveyor's cutting across a face too
     * steep to walk and is the only thing that overrides this.
     */
    private static final int CUT_FILL = 4;
    /**
     * How far a wild track's grade may stand off the ground of its own column, in blocks.
     * <p>
     * A build's way out is a path, not a road anyone takes a cart along, and a path over stepped country steps up it.
     * Held to the four blocks of cut and fill a named road gets - and measured from the relief, the middle of the
     * ground eight stations either side of the column - a track crossing terraced ground cut a notch through the
     * terraces instead: measured in Roads 6 at the third cave's crag, the track ran five blocks into its hillside at
     * the median, with bare dirt walls either side and the terraces stepping over its head. The band here is a block:
     * the grade takes the ground it stands on and the step sweeps turn a terrace into a step two stations long, which
     * is what a path over a terrace is. A named road keeps its four, being a road that was built for wheels and a
     * ditch.
     */
    private static final int WILD_BAND = 1;
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
     * How far from a span's own line a road still counts as standing on that crossing, in blocks.
     * <p>
     * The line is the scout's and the road is a search between marks, so the two do not keep a block apart for long.
     * Asking the surveyed line alone meant the crossing was never met at all: measured at the Old Ford, the walked
     * line ran two to three blocks off the marks, and the deck reached only the columns its half-width covered, while
     * at the Forest River it missed by more and the road dived in and climbed out the far side. A reach of a dozen
     * covers the drift, and nothing else does.
     */
    private static final int CROSSING_ACROSS = 12;
    /**
     * How deep a hole may be carried over on stone before the road gives up on it. A mouth or a shaft is bridged; a
     * ravine sixty blocks down is the country's business, not a road's.
     */
    private static final int HOLE_FILL = 32;
    /**
     * The most one station's surface may rise or fall above the one before it, in blocks - and the quantum the profile
     * is settled on, which is half a block.
     * <p>
     * A whole block was the old step, and a whole block is not a step a walker takes: every riser it allowed was a jump,
     * and the terrace it left had to be edged with a slab afterwards to make two half steps of it. Graded on halves, a
     * riser is half a block at most, and half a block is exactly what a bottom slab is - so the slab is the profile and
     * not a patch laid over it afterwards.
     */
    private static final double MAX_STEP = 0.5D;

    /** The same step in whole blocks, for the two places that lift a grade a whole block rather than a half. */
    private static final int STEP_BLOCKS = 1;
    /** How many times the two step sweeps are alternated after the last pass, so both ends agree. */
    private static final int SETTLE_PASSES = 8;
    /**
     * How many blocks of air stand over a road's own surface, by how far out from the middle of the road the column
     * is: five over the middle and the first column out, four at the second, three at the third, and none past it.
     * <p>
     * A road through a wood used to be cleared to the sky over every column of its band, which in a wood read as a
     * trench cut through the canopy. What a road needs is headroom rather than daylight: this is the tunnel it keeps,
     * wide enough to walk under and narrow enough that the wood above it is still a wood.
     */
    private static final int[] TUNNEL = {5, 5, 4, 3};

    /**
     * How far above its own column's ground a column of the tunnel is cleared: the headroom its distance from the
     * middle of the road asks for, and nothing past the last of the profile.
     */
    private static int canopyReach(int offset) {
        if (offset < 0) return TUNNEL[0];
        return offset < TUNNEL.length ? TUNNEL[offset] : 0;
    }
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
                .then(Commands.literal("build")
                        .executes(RoadCommands::build)
                        .then(Commands.argument("minx", IntegerArgumentType.integer())
                                .then(Commands.argument("minz", IntegerArgumentType.integer())
                                        .executes(RoadCommands::buildHere)
                                        .then(Commands.argument("kind", StringArgumentType.word())
                                                .executes(RoadCommands::buildHere)))))
                // Both ends as numbers, so a leg is pinned by the ground it is meant to cross rather than by where the
                // hand happens to be standing: the first pair is where it leaves, the second where it arrives. It has
                // a literal of its own because a width word after the first pair would read a mistyped third number as
                // a word, and lay the wrong road without a word said about it.
                .then(Commands.literal("pin")
                        .then(Commands.argument("minx", IntegerArgumentType.integer())
                                .then(Commands.argument("minz", IntegerArgumentType.integer())
                                        .then(Commands.argument("tox", IntegerArgumentType.integer())
                                                .then(Commands.argument("toz", IntegerArgumentType.integer())
                                                        .executes(RoadCommands::pinHere)
                                                        .then(Commands.argument("kind", StringArgumentType.word())
                                                                .executes(RoadCommands::pinHere)))))))
                .then(Commands.literal("plan")
                        .then(Commands.argument("minx", IntegerArgumentType.integer())
                                .then(Commands.argument("minz", IntegerArgumentType.integer())
                                        .executes(RoadCommands::planHere)
                                        .then(Commands.argument("kind", StringArgumentType.word())
                                                .executes(RoadCommands::planHere)))))
                .then(Commands.literal("forget").executes(RoadCommands::forget)));
    }

    /**
     * Why the server puts the planned roads back in the tables the pass reads.
     * <p>
     * A road planned at a build is made in a session and would be gone with it: after a restart the list was empty, so
     * a player standing on a road they had laid themselves could not find it, forget it or have it re-laid. The plan is
     * written down as it is laid (see {@link RoadPlans}) and read back here, once, before any of the world's buildings
     * ask for their roads.
     */
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;
        int restored = RoadPlans.load(overworld);
        // Said whether or not anything was read back: a world that lays no road at all has to be told apart from one
        // whose roads are not there, and the list in hand is the first of the two answers.
        LOGGER.info("Roads: {} section{} in hand, {} read back from the save", Roads.ROUTES.size(),
                Roads.ROUTES.size() == 1 ? "" : "s", restored);
    }

    /**
     * What a road pass came to, so the hand can say it and the world's own dressing can only log it.
     * <p>
     * A pass that gave the road up came to nothing: every count is nought, {@code searched} is false, and
     * {@code refusal} says which leg refused it and why. There is no other way to finish with nothing laid - a pass is
     * either graded or refused - so a nought without a refusal is a road that was already down.
     */
    record Road(int cleared, int pieces, int pillars, int along, boolean searched, int[] graded, int[] far,
                String refusal) {
        /** Whether the pass gave the road up rather than laying it. */
        boolean refused() {
            return refusal != null;
        }
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
            // A pass that cannot search its way lays nothing, so every road answered about here was searched.
            if (laid.refused()) {
                source.sendFailure(Component.literal("No road was laid: " + laid.refusal() + "."));
                return 0;
            }
            int[] graded = laid.graded();
            int[] far = laid.far();
            String surface = graded[0] + " columns laid, " + graded[1] + " cut, " + graded[2]
                    + " filled, " + graded[5] + " canopy cleared, " + graded[3] + " left open.";
            if (route instanceof Route.Crossing crossing) {
                source.sendSuccess(() -> Component.literal("Road to the valley: " + laid.cleared()
                        + " old timbers cleared, bridge of " + laid.pieces()
                        + " pieces from " + crossing.bankX() + ", " + crossing.bankZ() + " to " + crossing.farX()
                        + ", " + crossing.farZ() + ", pillars drawn down " + laid.pillars() + " blocks. Approach "
                        + laid.along() + " blocks, searched"
                        + ", steepest graded step " + graded[4] + ": " + surface
                        + " Far side " + far[0] + " columns laid, " + far[1] + " cut, " + far[2]
                        + " filled. Both ends walk in along the span's own line."), true);
            } else {
                Route.Riverless dry = (Route.Riverless) route;
                source.sendSuccess(() -> Component.literal("Road from " + dry.id() + " to " + dry.farX()
                        + ", " + dry.farZ() + ": " + laid.along() + " blocks, searched"
                        + ", steepest graded step " + graded[4] + ": " + surface
                        + " Nothing on the way is water, so nothing is bridged."), true);
            }
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The road could not be built: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Throws away a whole road - every section of it, and its plan - so nothing of it is laid again.
     * <p>
     * A pass carries its alignment: the stations the search found are written down with the grade, and a world
     * restarted mid-road picks them up rather than searching a second time. That is what makes a long road survive a
     * logout, and it is also why a bend recorded after a walk began would never be taken - the saved line predates it.
     * Forgetting the note is the way back to the search. The blocks already laid are left standing.
     * <p>
     * It is the whole road that is forgotten rather than the piece the hand is standing on. A road over water is several
     * sections, and clearing one of them left the rest in the list and in the plan store to be laid again at the next
     * start - the old forest road planned a second time stood beside its own first attempt, seven sections each and
     * three bridges over one stretch of water. So the road is found by the section at the hand, every section it begins
     * with is dropped from the pass, the list and the plan, and the plan store forgets them all.
     */
    private static int forget(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Route route = Roads.nearest(BlockPos.containing(source.getPosition()));
        if (route == null) {
            source.sendFailure(Component.literal("No road has been planned or walked here."));
            return 0;
        }
        String road = Roads.roadOf(route.id());
        int sections = 0;
        for (Route section : Roads.sections(road)) {
            Progress.forget(level, section.id());
            stop(section);
            sections++;
        }
        Progress.forget(level, road);
        stop(route);
        Roads.forget(road);
        int plans = RoadPlans.forget(level, road);
        Sites.forgetRoad(level, Roads.build(route));
        int count = Math.max(sections, 1);
        source.sendSuccess(() -> Component.literal("Forgot the road " + road + ": " + count + " section"
                + (count == 1 ? "" : "s") + " and " + plans + " planned, so nothing of it is left to lay. "
                + "The ground it laid stays standing."), true);
        return 1;
    }

    /**
     * Whether a pass was ever begun for a road.
     * <p>
     * This is what tells a plan from a road: a plan has no note in the save, and a road that has been laid or is being
     * laid has one. A world carries on a road it finds a note for, which is how a road survives a logout - but a plan
     * is a decision waiting for a command, not an order to the world, and starting one on the load of a chunk is how a
     * road planned twice came to be laid twice.
     */
    static boolean begun(ServerLevel level, Route route) {
        return Progress.of(level, route.id()) != null;
    }

    /**
     * A road planned between the caller's own block and a block they name.
     * <p>
     * This is the whole of the survey a road needs now: name both ends - the block underfoot and one other, or two pairs
     * of numbers so the leg is pinned whatever ground the hand is standing on - and {@link RoadPlan} reads the drawing
     * for the country between them - the way round the hills, the legs the pass will
     * search, and every span the water needs. {@code plan} says what it found and lays nothing, which is how a route is
     * judged before any ground is moved. {@code build} starts a pass for each section, which the world steps while the
     * ground under its head is loaded, so the road fills in behind the walk rather than freezing the server for a
     * road's worth of blocks.
     */
    private static int planned(CommandContext<CommandSourceStack> context, boolean lay) {
        CommandSourceStack source = context.getSource();
        BlockPos feet = BlockPos.containing(source.getPosition());
        int x = IntegerArgumentType.getInteger(context, "minx");
        int z = IntegerArgumentType.getInteger(context, "minz");
        // Both ends may be named as numbers, and then the first pair is where the leg starts: the block underfoot is not
        // part of the road at all, so a leg can be pinned from anywhere and the same command can be given twice with
        // the same answer. Without the second pair the block underfoot is the start, which is the short form.
        Integer pinnedX = pinned(context, "tox");
        Integer pinnedZ = pinned(context, "toz");
        boolean bothEnds = pinnedX != null && pinnedZ != null;
        BlockPos from = bothEnds ? new BlockPos(x, feet.getY(), z) : feet;
        BlockPos to = bothEnds ? new BlockPos(pinnedX, feet.getY(), pinnedZ) : new BlockPos(x, feet.getY(), z);
        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            source.sendFailure(Component.literal("A road needs two different ends: it would leave and arrive on "
                    + from.getX() + ", " + from.getZ()
                    + (bothEnds ? ", the same block twice." : ", the block you are standing on. Name where it should"
                    + " arrive: /tmroad build <x> <z>, or name both ends with /tmroad pin <x> <z> <x> <z>.")));
            return 0;
        }
        Choice asked = choice(source, from, context);
        Roads.Kind kind = asked.kind();
        boolean town = asked.town();
        String id = "tolkienmobs:roads/planned_" + from.getX() + "_" + from.getZ() + "_" + to.getX() + "_"
                + to.getZ();
        RoadPlan.Plan plan = RoadPlan.of(id, kind, town, from, to);
        if (plan.refused()) {
            String why = plan.refusal();
            source.sendFailure(Component.literal("No road would be laid: " + why + "."));
            return 0;
        }
        List<Route> sections = plan.sections();
        if (sections.isEmpty()) {
            source.sendFailure(Component.literal("A road needs two ends, and both of those blocks are unset."));
            return 0;
        }
        for (Route section : sections) saySection(section);
        if (lay) {
            // Every section joins the world before any of them is begun, because a road's grade asks where the spans are
            // before it arrives on one, and a span that arrived afterwards would be a step at its own bank.
            for (Route section : sections) Roads.remember(section);
            // Written down before the first pass is begun: a road half laid when the world stops is found again at the
            // next start, and a road laid is still a road somebody can forget and lay a second time.
            RoadPlans.keep(source.getLevel(), sections, kind, town);
            for (Route section : sections) pass(source.getLevel(), section, from.getX(), from.getZ());
        }
        String how = lay ? "laid as the world steps it" : "planned, nothing laid";
        String wide = town ? "town bands (7 in, 5 through, 3 out)" : kind.blocks() + " wide";
        source.sendSuccess(() -> Component.literal("Road " + id + " from " + from.getX() + ", " + from.getZ()
                + " to " + to.getX() + ", " + to.getZ() + ": " + sections.size() + " section"
                + (sections.size() == 1 ? "" : "s")
                + " at " + wide + ", " + how + "."), true);
        return 1;
    }

    /** What one section of a planned road is, in the log: its ends and its bends, or its span and its deck. */
    private static void saySection(Route section) {
        if (section instanceof Route.Crossing span) {
            LOGGER.info("Road plan {}: span {}, {} to {}, {}, deck {}", section.id(), span.bankX(), span.bankZ(),
                    span.farX(), span.farZ(), span.deckY());
            return;
        }
        Route.Riverless dry = (Route.Riverless) section;
        LOGGER.info("Road plan {}: {}, {} to {}, {}, {} bends", section.id(), dry.pathX(), dry.pathZ(), dry.farX(),
                dry.farZ(), Roads.bends(section).size());
    }

    /** One end named as a number, or null when that end was left to the block underfoot. */
    private static Integer pinned(CommandContext<CommandSourceStack> context, String name) {
        try {
            return IntegerArgumentType.getInteger(context, name);
        } catch (IllegalArgumentException absent) {
            return null;
        }
    }

    /** What width a planned road is asked for, and whether the bands decide it rather than one kind. */
    private record Choice(Roads.Kind kind, boolean town) {
    }

    /**
     * The width a road is laid at: the word after the coordinates, or the ground it leaves from when nothing is said.
     * <p>
     * Two towns stand - the Shire's village and the valley - and everything else in the world is a camp, a ruin or a
     * road between them. So a road that starts on a town's own ground takes the town's bands and narrows as it leaves:
     * seven wide of the town's paving inside {@link #URBAN_BAND}, five of cobble and path out to
     * {@link #TRANSITION_BAND}, three of the country's own track beyond. Anywhere else is a wild track three wide,
     * which is what a camp's door wants and what every walked line here was.
     * <p>
     * A word overrides both: {@code wild} (3), {@code wide} (5), {@code main} (7 of the country's own surface),
     * {@code urban} (7 of the town's paving everywhere), {@code town} (the bands, wherever the road starts). The word
     * is how a road between two places is decided on - a hold's road is {@code main} - and it is the only thing about a
     * road's width that has to be chosen at all.
     */
    private static Choice choice(CommandSourceStack source, BlockPos from, CommandContext<CommandSourceStack> context) {
        String named;
        try {
            named = StringArgumentType.getString(context, "kind");
        } catch (IllegalArgumentException absent) {
            return townGround(source.getLevel(), from)
                    ? new Choice(Roads.Kind.WILD_WIDE, true)
                    : new Choice(Roads.Kind.WILD, false);
        }
        return switch (named.toLowerCase(java.util.Locale.ROOT)) {
            case "wild" -> new Choice(Roads.Kind.WILD, false);
            case "main" -> new Choice(Roads.Kind.MAIN, false);
            case "urban" -> new Choice(Roads.Kind.URBAN, false);
            // `town` names no kind on purpose: the bands decide the width column by column.
            case "town" -> new Choice(Roads.Kind.WILD_WIDE, true);
            default -> new Choice(Roads.Kind.WILD_WIDE, false);
        };
    }

    /** Whether the block a road leaves from is a town's own ground, or the approach out of it. */
    private static boolean townGround(ServerLevel level, BlockPos from) {
        for (Places.Place town : Places.towns(level)) {
            BlockPos centre = town.centre();
            if (centre == null) continue;
            double away = Math.hypot(from.getX() - centre.getX(), from.getZ() - centre.getZ());
            if (away <= town.radius() + TRANSITION_BAND) return true;
        }
        return false;
    }

    private static int planHere(CommandContext<CommandSourceStack> context) {
        return planned(context, false);
    }

    private static int buildHere(CommandContext<CommandSourceStack> context) {
        return planned(context, true);
    }

    /** The same road, with both of its ends named: the only form that does not read the caller's own block. */
    private static int pinHere(CommandContext<CommandSourceStack> context) {
        return planned(context, true);
    }

    /** The road itself, without a sender to answer: the pass the hand runs and the world runs for it. */
    static Road road(ServerLevel level, Route route) throws IOException {
        RoadJob job = plan(route);
        // A hand is answered in one call, and a road that stops at a town's edge is only finished by the town's note:
        // stepping that wait here would hold the server's own thread until the town was raised. What the pass has laid
        // is the answer, and the rest is left to the world's pass, which asks again from the ground.
        while (job.step(level) && !job.waiting()) {
            // The hand asked for the road, not for the schedule of it.
        }
        return job.summary() != null ? job.summary() : job.stopped();
    }

    /**
     * The passes this world has in hand, by route, so a road has one pass whichever hand asked for it.
     * <p>
     * A road used to be stepped only while a player was within reach of the ground its head stood on, and only once
     * the build it left from had been found and dressed - so a road existed in two halves, and the second began when
     * somebody finally walked to the other end. The pass is kept by route instead: a road has one pass, any loaded
     * chunk of that road may ask for it, and it belongs to the world rather than to whoever happened to be walking.
     */
    private static final Map<String, RoadJob> PASSES = new HashMap<>();

    /**
     * The roads the ground has refused this run.
     * <p>
     * A refusal is the world's answer about the country a road crosses, not about the hand that asked: the same ground
     * refuses it again a moment later. While a road is only begun by the chunk it passes through that costs nothing -
     * a chunk loads once. A player standing beside a refused road would have the whole search run again every second it
     * takes to look, so the refusal is kept for the run and cleared only when the road is forgotten or the world is
     * loaded again.
     */
    private static final java.util.Set<String> REFUSED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    /**
     * The pass for one route, planned if the world has none for it yet.
     * <p>
     * Two callers can ask for the same road - the chunk it passes through and the build it leaves from - and they must
     * get the same pass rather than two that each lay half of it. A road already down owes nothing and gets none.
     * <p>
     * The point the ask comes from settles which end the road is laid from: the end nearer it is the end the player is
     * standing at, and a dry road is walked outward from there rather than from the build the route was written for.
     */
    static RoadJob pass(ServerLevel level, Route route, int x, int z) {
        // A bridge is not begun from the ground under it: it needs the city's span and abutments first, so a build's
        // crossing stays with the dressing and asks for its pass there. A crossing on a named road has no city to
        // wait for, and begins from the ground it crosses like every other section.
        if (route instanceof Route.Crossing && !Roads.road(route)) return null;
        RoadJob existing = PASSES.get(route.id());
        if (existing != null) return existing;
        if (REFUSED.contains(route.id())) return null;
        if (down(level, route)) return null;
        RoadJob made = new RoadJob(route, x, z);
        PASSES.put(route.id(), made);
        // Said once for each pass, from whichever ground asked for it: a road that was never begun and a road that was
        // begun and refused look the same in a log otherwise, and both look like nothing laid.
        LOGGER.info("Road {}: begun from the ground at {}, {}", route.id(), x, z);
        return made;
    }

    /** The passes in hand, for the tick that steps them and a command that wants to say what is being laid. */
    static Collection<RoadJob> passes() {
        return PASSES.values();
    }

    /** Drops a pass: the road gave up, or the world was asked to forget it. */
    static void stop(RoadJob job) {
        PASSES.remove(job.route().id(), job);
    }

    /** Drops the pass for one road, if the world has one in hand. */
    static void stop(Route route) {
        PASSES.remove(route.id());
        REFUSED.remove(route.id());
    }

    /** Drops every pass for a build's roads, however the road is named. */
    static void stop(String build) {
        PASSES.values().removeIf(job -> build.equals(Roads.build(job.route())));
    }

    /** A road pass waiting to be stepped, for a caller that has ticks rather than a command to answer. */
    static RoadJob plan(Route route) {
        return new RoadJob(route);
    }

    /** What a pass that finds its road already down answers with: nothing laid, because nothing was owed. */
    private static final Road ALREADY_DOWN = new Road(0, 0, 0, 0, false, new int[6], new int[3], null);

    /**
     * Whether this world already has this road down, from the note its own pass leaves when it finishes.
     * <p>
     * This is the question the world's dressing asks before planning a road, and it is answered by the road rather
     * than by a file beside the city: see {@link Progress}, whose every record used to be removed on completion,
     * leaving no way to tell a road that was down from one never begun.
     */
    static boolean down(ServerLevel level, Route route) {
        CompoundTag carried = Progress.of(level, route.id());
        if (carried == null) return false;
        if (standing(level, route, carried)) return true;
        // A note that disagrees with its own ground is walked again, once: the pass is planned, the road laid, and the
        // note it leaves is asked the same way. Once per run, because a station the pass leaves open by design would
        // otherwise plan a road on every chunk load of it.
        return !REWALKED.add(route.id());
    }

    /** The roads whose note was found disagreeing with the ground this run, so a road is walked again but not for ever. */
    private static final java.util.Set<String> REWALKED = new java.util.HashSet<>();

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
    private static boolean standing(ServerLevel level, Route route, CompoundTag note) {
        if (!note.getBooleanOr("down", false)) return false;
        int[] at = note.getIntArray("at").orElse(new int[0]);
        String block = note.getStringOr("block", "");
        if (at.length < 3 || block.isEmpty()) return false;
        BlockPos probe = new BlockPos(at[0], at[1], at[2]);
        if (!block.equals(BuiltInRegistries.BLOCK.getKey(level.getBlockState(probe).getBlock()).toString())) return false;
        // The probe is one block, and the block under a road's first station is a block the country puts there too, so
        // the note of a finished dry road is asked for the road itself: see walked. A crossing's note is written over
        // its span rather than over a walked line, so it is still the probe's to answer.
        return !(route instanceof Route.Riverless) || walked(level, route, note);
    }

    /** How many of a finished road's stations may be missing before its note is not believed, and the least allowed. */
    private static final int MISSING = 2;

    /**
     * How much of the road a note carries still stands: stations from the start of its line, from its end, and from
     * across its middle, each read at the grade the note says it was laid to.
     * <p>
     * A road used to be believed on one block - the ground it met its build on - and a road that begins on a camp's own
     * grass is a note the country satisfies by itself. Measured in Progress Check 1: the note said the Old Forest Road
     * was down for all four thousand and twenty-two of its stations, and the first five hundred and fifty-nine of them,
     * from Gloin's camp east past the Old Ford to the edge of the next region file, carried no road at all - the pass's
     * cursor had been written down ahead of the ground it laid, and no pass was ever planned over that stretch again
     * because its first block was still the grass it had always been. The note carries the way the road took and its
     * grade, so the road itself is asked for now.
     * <p>
     * A station the road is not laid at by design is not counted either way: the columns inside the build's own
     * footprint, which are the build's, and the stations over surveyed water, which are the crossing's. The rest are
     * asked for the block {@link #surface} would have laid there, in the three middle columns of the station - so a
     * road with a block broken out of it is still a road, and a stretch of several hundred blocks with none in it is
     * not.
     *
     * @return whether the road still stands where the note says it does
     */
    private static boolean walked(ServerLevel level, Route route, CompoundTag note) {
        List<int[]> line = unflatten(note.getIntArray("approach").orElse(new int[0]));
        long[] heights = note.getLongArray("height").orElse(new long[0]);
        if (line.size() < 2 || heights.length != line.size()) return false;
        java.util.Set<Integer> stations = new java.util.LinkedHashSet<>();
        for (int end = 0; end < 8; end++) {
            stations.add(Math.min(end, line.size() - 1));
            stations.add(Math.max(0, line.size() - 1 - end));
        }
        for (int through = 1; through < 8; through++) stations.add((int) ((long) through * (line.size() - 1) / 8L));
        int read = 0;
        int missing = 0;
        for (int i : stations) {
            if (i < 0 || i >= line.size()) continue;
            int x = line.get(i)[0];
            int z = line.get(i)[1];
            int y = Mth.floor(Double.longBitsToDouble(heights[i]));
            // A chunk nobody has made is not read at all: the check is meant to cost a handful of blocks, not to
            // generate the country either side of a road on every ask. What is loaded is enough to say the road is
            // missing where a player is standing at it.
            if (!level.hasChunkAt(new BlockPos(x, y, z))) continue;
            if (x >= route.cityX0() && x <= route.cityX1() && z >= route.cityZ0() && z <= route.cityZ1()) continue;
            if (Roads.water(x, z, CROSSING_ACROSS) != null) continue;
            // A station over the drawn river is the crossing's whether or not this run knows the span: a planned road's
            // bridge is a section the plan made, and the plan is not in the save, so without this a good road would be
            // doubted at every station it laid in the water - once per start, which is exactly the re-walk this file's
            // note is meant to avoid.
            if (ArdaMap.sample(x, z).river()) continue;
            read++;
            var laid = surface(route, x, z).getBlock();
            boolean there = false;
            for (int dx = -1; dx <= 1 && !there; dx++) {
                for (int dz = -1; dz <= 1 && !there; dz++) {
                    there = level.getBlockState(new BlockPos(x + dx, y, z + dz)).getBlock() == laid;
                }
            }
            if (!there) missing++;
        }
        if (read < 4) return true;
        return missing <= Math.max(MISSING, read / 5);
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

    /** The span's own columns, from the near bank to the far one along whichever axis it lies on. */
    private static int spanLow(Route.Crossing span) {
        return Math.min(span.bank(), span.far());
    }

    private static int spanHigh(Route.Crossing span) {
        return Math.max(span.bank(), span.far());
    }

    /**
     * The band the sweep takes an older span's timbers out of: the span and one column past either abutment.
     * <p>
     * A pass that only places blocks cannot correct a piece laid in the wrong place, so the sweep reaches past both
     * ends - the column beyond an abutment is where a mislaid end stands.
     */
    private static int sweepLow(Route.Crossing span) {
        return Math.min(span.far() + span.step(), span.bank() - span.step());
    }

    private static int sweepHigh(Route.Crossing span) {
        return Math.max(span.far() + span.step(), span.bank() - span.step());
    }

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
     * of pillars, four thousand settled candidates of the search, then a few hundred columns of the graded roads. Which is the same budget each stage has always had, measured in columns walked rather
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
        private int pillarAt;
        /** The bridge's pieces, east end first; built on the stage's first step and stood one a step after it. */
        private List<int[]> bridge;
        /** The templates this span's bridge is stood from, resolved once when the pass reaches the bridge. */
        private Pieces spanPieces;
        private Descent descent;
        private List<int[]> approach;
        private List<int[]> exit;
        private Graded graded;
        private Graded far;
        /**
         * The stations at a town's end of this road that are held until the town stands, in the order they are walked,
         * beginning with the station the laid road stops on; null when the road arrives on no town that is going up.
         */
        private List<int[]> held;
        /** The town those stations arrive on, so the pass can ask the world when it is standing. */
        private String heldBy;
        /** The grade of the held stretch, once the town stands: null until the world says it does. */
        private Graded tail;
        /** The tick the town was last asked about, so a pass waiting at the town's edge is not a note read a tick. */
        private long askTown;
        /** Whether the pass is stopped at that edge now, for a caller that steps the road rather than the world. */
        private boolean waiting;
        /** The road as the note should carry it and its grade, once the held stretch has been laid. */
        private List<int[]> whole;
        private double[] heights;
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
        /** Why this pass gave the road up, or null while it is still being laid. */
        private String refused;
        /** Whether the refusal has been said to the players near the road; it is said once. */
        private boolean told;
        /** The stations walked so far, across every bend this road is recorded as passing through. */
        private List<int[]> walked;
        /** The bends recorded for this road, or an empty list: a road with none is walked in a single leg. */
        private List<BlockPos> bends;
        /** The leg being walked, and the block it starts from. Leg nought starts at the build's own door. */
        private int leg;
        private int fromX;
        private int fromZ;

        /** The road this pass lays, so a caller holding the pass knows which road it is for. */
        Route route() {
            return route;
        }

        /**
         * Whether this pass lays from the far end towards the build rather than from the build outwards.
         * <p>
         * A road that arrives on a town still being raised is always laid from the other end, whichever end the ask
         * came from, so this is settled where the alignment is rather than where the pass is made.
         */
        private boolean outward;

        private RoadJob(Route route) {
            this(route, route.pathX(), route.pathZ());
        }

        /**
         * One road pass, begun from the end nearer the point it was asked from.
         * <p>
         * A player who comes on a road at its far end and is made to watch it fill in from the other end three hundred
         * blocks away has not been given a road: the pass lays from the ground that asked for it, whichever end that
         * ground is at. A crossing keeps its own order - its span and abutments are its own geometry rather than a
         * place on a map - so only a dry road is ever turned round.
         */
        private RoadJob(Route route, int x, int z) {
            this.route = route;
            this.span = route instanceof Route.Crossing crossing ? crossing : null;
            // A road with no water on it has no span to sweep and nothing to bridge, so its first stage is the
            // search and the whole distance between its ends is the alignment.
            this.stage = span == null ? Stage.SEARCH : Stage.SWEEP;
            // The sweep and the pillars walk the span along its own axis; the sweep starts one column past the
            // abutment at the end away from the near bank, so a piece an older plan left standing is found too.
            this.swept = span == null ? 0 : sweepLow(span);
            this.pillarAt = span == null ? 0 : Math.min(span.far(), span.bank());
            // Which end the ask came from is read along the road's own line rather than as the crow flies: a road
            // bent round a shoulder of hill has its ends nearer each other through the hill than along the road, so
            // a player standing on the bend is placed by the bend they are beside and not by the rock between.
            double met = 0.0D;
            double whole = 0.0D;
            if (span == null && route instanceof Route.Riverless) {
                double[] line = byLine(route, x, z);
                met = line[0];
                whole = line[1];
            }
            this.outward = whole > 0.0D && whole - met < met;
        }

        /**
         * Where a point stands on a route's own line, as the distance walked from the build's end, and how long that
         * line is.
         * <p>
         * The line is the road as it is written down - the build's door, the bends in the order the road meets them,
         * the far end - because that is the road a player is looking at when they come on it. Measured to the ends as
         * the crow flies, a bend-heavy road reads as one straight line through whatever the bends were walked to
         * avoid: standing on the Thorenhad road's own bend, a dozen blocks from the far end's side of the hill, the
         * two ends come out near enough the same distance away and the road sets off from whichever it preferred.
         * <p>
         * The point's place on each leg is the foot of it, held to the leg's own ends: past a bend a point belongs to
         * the leg it is nearest, and past the far end to the far end. A route with no bends is the one straight leg it
         * always was, so a road that was never bent picks its end exactly as it did before.
         */
        private static double[] byLine(Route route, int x, int z) {
            List<int[]> corners = new ArrayList<>();
            corners.add(new int[]{route.pathX(), route.pathZ()});
            for (BlockPos bend : Roads.bends(route)) corners.add(new int[]{bend.getX(), bend.getZ()});
            if (route instanceof Route.Riverless dry) corners.add(new int[]{dry.farX(), dry.farZ()});
            double walked = 0.0D;
            double best = Double.MAX_VALUE;
            double met = 0.0D;
            for (int i = 1; i < corners.size(); i++) {
                int[] from = corners.get(i - 1);
                int[] to = corners.get(i);
                double dx = to[0] - from[0];
                double dz = to[1] - from[1];
                double length = Math.hypot(dx, dz);
                double reach = 0.0D;
                double along = 0.0D;
                if (length > 0.0D) {
                    double t = ((x - from[0]) * dx + (z - from[1]) * dz) / (length * length);
                    along = Math.max(0.0D, Math.min(1.0D, t));
                    reach = along * length;
                }
                double off = Math.hypot(x - (from[0] + dx * along), z - (from[1] + dz * along));
                if (off < best) {
                    best = off;
                    met = walked + reach;
                }
                walked += length;
            }
            return new double[]{met, walked};
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
                // Said in the chat of whoever is standing there, not only in the log: a player who sees nothing laid and
                // nothing said has no way to tell a road the country refused from a command that did not run.
                if (refused != null && !told) {
                    told = true;
                    tell(level);
                }
                // A pass that gave the road up writes nothing: a "down" note for a road that was never graded would say
                // the road was there for ever, which is the fault the note is checked against the ground to avoid.
                if (refused == null && saveWanted()) {
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
            if (outward && route instanceof Route.Riverless dry) return new int[]{dry.farX(), dry.farZ()};
            return new int[]{route.pathX(), route.pathZ()};
        }

        /** One step of whichever stage the pass is in; false once the road is down. */
        private boolean stage(ServerLevel level) throws IOException {
            // Which bridge this span is stood with is the crossing's own answer, resolved once rather than asked
            // at every piece: a named bridge is a pair of names and a pier's material, and the pass has to read
            // the jar to know whether that pair is there at all.
            if (span != null && spanPieces == null) spanPieces = pieces(span);
            switch (stage) {
                case SWEEP -> {
                    int upto = Math.min(sweepHigh(span), swept + COLUMNS_PER_STEP - 1);
                    cleared += sweepColumns(level, span, swept, upto);
                    swept = upto + 1;
                    if (swept > sweepHigh(span)) stage = Stage.BRIDGE;
                }
                case BRIDGE -> {
                    if (bridge == null) bridge = bridgePlan(level, span, spanPieces);
                    if (pieces < bridge.size()) stand(level, spanPieces, bridge.get(pieces++));
                    else stage = Stage.PILLARS;
                }
                case PILLARS -> {
                    int upto = Math.min(spanHigh(span), pillarAt + COLUMNS_PER_STEP - 1);
                    pillarsDrawn += pillarsColumns(level, span, spanPieces.support(), pillarAt, upto);
                    pillarAt = upto + 1;
                    if (pillarAt > spanHigh(span)) stage = Stage.SEARCH;
                }
                case SEARCH -> {
                    if (!walk(level)) {
                        // A leg the ground will not carry gives the whole road up. It used to be answered with one
                        // straight line through everything the search had just refused, which is a road nobody planned
                        // cut through whatever stood in it; now nothing is graded, nothing is written down as down, and
                        // the hand that asked is told which leg and why.
                        if (refused != null) refuse();
                        else align(level);
                    }
                }
                case GRADE -> {
                    int budget = PAVING_PER_STEP;
                    if (graded != null) budget = graded.pave(level, budget);
                    if (far != null) budget = far.pave(level, budget);
                    if (tail != null) budget = tail.pave(level, budget);
                    if ((graded == null || graded.laid()) && (far == null || far.laid())
                            && (tail == null || tail.laid())) stage = Stage.STEPS;
                }
                case STEPS -> {
                    // A road that arrives on a town still being raised stops at its edge and leaves the arrival alone:
                    // the town's ring is about to change that ground, and a grade laid over it now is the stretch the
                    // ring takes out from under the road. The pass waits here - its ground is the town's, so the wait
                    // costs a note a second - and lays the held stations the moment the town writes its note.
                    if (held != null && !held.isEmpty()) {
                        if (!stood(level)) {
                            waiting = true;
                            return true;
                        }
                        waiting = false;
                        if (tail == null) {
                            int[] stop = held.get(held.size() - 1);
                            tail = new Graded(level, route, held, joint(level),
                                    endGround(level, stop[0], stop[1]));
                            LOGGER.info("Road {}: {} is standing, so the last {} stations of the road are laid",
                                    route.id(), heldBy, held.size() - 1);
                        }
                        if (!tail.laid()) {
                            stage = Stage.GRADE;
                            return true;
                        }
                        join();
                    }
                    // There is no lip pass left to run. A road graded on halves lays a slab wherever its height falls
                    // between two blocks, so every step is already half a block and every half step is already laid; the
                    // stage stays only so a pass saved by a version that had one still finishes rather than stalling.
                    done = new Road(cleared, pieces, pillarsDrawn,
                            whole != null ? whole.size() : approach.size(), searched,
                            graded == null ? new int[6] : graded.summary(),
                            far == null ? new int[6] : far.summary(), null);
                    return false;
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
            if (done != null && refused == null) {
                LOGGER.info("Road {}: laid in {} steps, {} of work{}", route.id(), count(taken), millis(count(nanos)),
                        resumed ? ", since the pass was last picked up" : "");
            }
        }

        /** How near a player has to be to the road that refused it to be told in the chat rather than only in the log. */
    private static final int SAY_WITHIN = 128;

    /** How often a pass waiting at a town's edge asks the save whether the town has written its note, in ticks. */
    private static final int ASK_TOWN_EVERY = 20;

        /**
         * Whether the town the held stretch arrives on has written its note, asked once a second.
         * <p>
         * The answer is a note in the save rather than anything in the world, and a waiting pass is stepped as often
         * as its own ground is loaded, so asking every step would be a file read a tick for a town that takes minutes
         * to raise. A second is the clock the world already asks the same question on for the roads it begins.
         */
        private boolean stood(ServerLevel level) {
            long now = level.getGameTime();
            if (now < askTown) return false;
            askTown = now + ASK_TOWN_EVERY;
            return heldBy != null && !TownHalls.waits(level, heldBy);
        }

        /** The height the held stretch starts from: where the road stopped, or the ground it would have left. */
        private int joint(ServerLevel level) {
            if (graded != null) return Mth.floor(graded.end());
            int[] at = held.get(0);
            return endGround(level, at[0], at[1]);
        }

        /**
         * The road as the note should carry it: the stretch laid while the town was going up and the arrival laid
         * after it, as one line and one grade.
         * <p>
         * Without this the note a finished road leaves would describe a road that stops at the town's edge, and the
         * check that asks the ground for the road would never look at the arrival - which is the one stretch a town
         * raised a second time cuts again.
         */
        private void join() {
            if (tail == null) return;
            if (graded == null) {
                // The whole road stood inside the town's ground: there is no first stretch, so the held line is the
                // road and the grade it was laid to is the note's.
                whole = held;
                heights = tail.heights();
                return;
            }
            List<int[]> line = new ArrayList<>(approach);
            for (int at = 1; at < held.size(); at++) line.add(held.get(at));
            double[] before = graded.heights();
            double[] after = tail.heights();
            double[] all = new double[before.length + after.length - 1];
            System.arraycopy(before, 0, all, 0, before.length);
            System.arraycopy(after, 1, all, before.length, after.length - 1);
            whole = line;
            heights = all;
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
            tag.putInt("pillarAt", pillarAt);
            tag.putBoolean("searched", searched);
            if (approach != null) tag.putIntArray("approach", flatten(approach));
            if (exit != null) tag.putIntArray("exit", flatten(exit));
            if (held != null) {
                tag.putIntArray("held", flatten(held));
                tag.putString("heldBy", heldBy == null ? "" : heldBy);
            }
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
            if (standing(level, route, tag)) {
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
            pillarAt = tag.getIntOr("pillarAt", pillarAt);
            searched = tag.getBooleanOr("searched", false);
            List<int[]> carried = unflatten(tag.getIntArray("approach").orElse(new int[0]));
            if (carried.size() < 2) {
                // Nothing to carry on with, so the pass begins at its first stage rather than in the middle of nothing.
                stage = span == null ? Stage.SEARCH : Stage.SWEEP;
                return;
            }
            approach = carried;
            exit = unflatten(tag.getIntArray("exit").orElse(new int[0]));
            List<int[]> carriedHold = unflatten(tag.getIntArray("held").orElse(new int[0]));
            held = carriedHold.isEmpty() ? null : carriedHold;
            heldBy = tag.getStringOr("heldBy", "");
            graded = tag.contains("graded") ? Graded.restore(route, approach, tag.getCompoundOrEmpty("graded")) : null;
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

        /** Whether this pass is stopped at a town's edge, waiting for the town's note before it lays the arrival. */
        boolean waiting() {
            return waiting;
        }

        /** The road as it stands when a pass stops at a town's edge, for a hand that cannot stay to see the town. */
        private Road stopped() {
            return new Road(cleared, pieces, pillarsDrawn, approach.size(), searched,
                    graded == null ? new int[6] : graded.summary(), new int[3], null);
        }

        /**
         * Walks the road a leg at a time: from the build's door to the first recorded bend, from that bend to the
         * next, and from the last bend to the far end. A road with no recorded bends is one leg, which is the walk
         * every road had before there was a way to bend one.
         * <p>
         * The point of a leg is that the line cannot skip it: the next leg starts where the last one stopped, so a
         * bend recorded on the ground is a station the road is walked through rather than a suggestion the walk can
         * price away. A leg the country will not take a road to gives up the whole walked way, and the line is laid
         * straight between the route's ends with the log saying which leg failed.
         *
         * @return whether there is more walking to do, so the tick that ran this can come back to it
         */
        private boolean walk(ServerLevel level) {
            if (bends == null) {
                bends = Roads.bends(route);
                walked = new ArrayList<>();
                leg = 0;
                fromX = route.pathX();
                fromZ = route.pathZ();
                if (!bends.isEmpty()) {
                    LOGGER.info("Road {}: bent through {} recorded point{}", route.id(), bends.size(),
                            bends.size() == 1 ? "" : "s");
                }
            }
            int toX;
            int toZ;
            if (leg < bends.size()) {
                BlockPos bend = bends.get(leg);
                toX = bend.getX();
                toZ = bend.getZ();
            } else if (route instanceof Route.Crossing span) {
                // The approach arrives on the span's own line, a run-in back from the east bank.
                int runIn = span.bank() - span.step() * span.runIn();
                toX = span.x(runIn, 0);
                toZ = span.z(runIn, 0);
            } else {
                Route.Riverless dry = (Route.Riverless) route;
                toX = dry.farX();
                toZ = dry.farZ();
            }
            if (descent == null) descent = new Descent(route, fromX, fromZ, toX, toZ, leg == 0);
            if (descent.step(level, SEARCH_PER_STEP)) return true;
            List<int[]> part = descent.path();
            if (part == null || part.isEmpty()) {
                // The country would not take the road, so the road is not laid: nothing is graded and no note is left
                // behind, so the same road can be planned again over better ground. What the walk could not do is
                // carried out to the hand and to the log rather than swallowed.
                refused = "leg " + (leg + 1) + " of " + (bends.size() + 1) + " found no way from " + fromX + ", "
                        + fromZ + " to " + toX + ", " + toZ + " - " + descent.why();
                LOGGER.info("Road {}: no walked way from {}, {} to {}, {} (leg {}): {}", route.id(), fromX, fromZ,
                        toX, toZ, leg + 1, descent.why());
                searched = false;
                walked = null;
                return false;
            }
            // The leg starts where the last one stopped, so its first station is the station already walked.
            for (int at = walked.isEmpty() ? 0 : 1; at < part.size(); at++) walked.add(part.get(at));
            fromX = toX;
            fromZ = toZ;
            leg++;
            descent = null;
            return leg <= bends.size();
        }

        /** Gives the road up: the pass is finished, nothing was graded, and the reason is kept for whoever asked. */
        private void refuse() {
            done = new Road(0, 0, 0, 0, false, new int[6], new int[3], refused);
            REFUSED.add(route.id());
            LOGGER.warn("Road {}: nothing laid - {}", route.id(), refused);
        }

        /** Says why the road gave up in the chat of the players near the ground it was to be laid on. */
        private void tell(ServerLevel level) {
            BlockPos near = new BlockPos(route.pathX(), level.getMinY(), route.pathZ());
            for (ServerPlayer player : level.players()) {
                if (player.blockPosition().distSqr(near) > SAY_WITHIN * SAY_WITHIN) continue;
                player.sendSystemMessage(Component.literal("No road was laid: " + refused + "."));
            }
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
            List<int[]> found = walked;
            searched = found != null;
            if (!searched) {
                // There is no straight line to fall back to any more: a crossing with no walked way is not laid at all.
                int runIn = span.bank() - span.step() * span.runIn();
                refused = "no walked way to " + span.x(runIn, 0) + ", " + span.z(runIn, 0);
                refuse();
                return;
            }
            List<int[]> line = new ArrayList<>(found);
            // The last stations before the near abutment, dead straight on the span's own line: a piece is a
            // straight span with an abutment at one end, so a road arriving at an angle has no junction to arrive on.
            for (int at = span.bank() - span.step() * (span.runIn() - 1); at != span.bank(); at += span.step()) {
                line.add(new int[]{span.x(at, 0), span.z(at, 0)});
            }
            approach = hairpins(level, route, simplify(line));
            exit = new ArrayList<>();
            int off = span.far() + span.step();
            for (int at = off; at != off + span.step() * span.exitReach(); at += span.step()) {
                exit.add(new int[]{span.x(at, 0), span.z(at, 0)});
            }
            int farOut = span.far() + span.step() * span.exitReach();
            int startY = endGround(level, route.pathX(), route.pathZ());
            int endY = endGround(level, span.x(farOut, 0), span.z(farOut, 0));
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
            List<int[]> found = walked;
            searched = found != null;
            if (!searched) {
                // A dry road with no walked way is not laid either: the straight line through whatever stood between the
                // ends was the one thing this pass must not build.
                refused = "no walked way to " + dry.farX() + ", " + dry.farZ();
                refuse();
                return;
            }
            approach = hairpins(level, route, simplify(found));
            exit = List.of();
            List<int[]> walk = new ArrayList<>(approach);
            int startY = endGround(level, route.pathX(), route.pathZ());
            int endY = endGround(level, dry.farX(), dry.farZ());
            // Which end of the road arrives on a town still being raised, if either does. A road with one town standing
            // is laid from that town towards the other, whichever end the ask came from, so the whole of it is laid
            // except the stretch the other town is about to change.
            Roads.Hold farTown = Roads.hold(level, route, true);
            Roads.Hold nearTown = Roads.hold(level, route, false);
            Roads.Hold town = farTown != null ? farTown : nearTown;
            boolean back = outward;
            if (farTown != null) back = false;
            else if (nearTown != null) back = true;
            if (back) {
                // The same road, walked the other way: the stations are turned round and the two ends' ground goes
                // with them, so the grade is the one it always was and the laying begins where the player stands.
                Collections.reverse(walk);
                int swap = startY;
                startY = endY;
                endY = swap;
            }
            outward = back;
            if (town != null) {
                int cut = walk.size();
                while (cut > 1 && Math.hypot(walk.get(cut - 1)[0] - town.at().getX(),
                        walk.get(cut - 1)[1] - town.at().getZ()) <= Roads.SHORT_OF_TOWN) cut--;
                if (cut < 2) {
                    // The whole road stands inside the town's own ground: there is nothing ahead of the town to lay,
                    // so all of it waits for the note.
                    held = new ArrayList<>(walk);
                    walk = new ArrayList<>(walk.subList(0, 1));
                } else {
                    held = new ArrayList<>(walk.subList(cut - 1, walk.size()));
                    walk = new ArrayList<>(walk.subList(0, cut));
                }
                heldBy = town.town();
                int[] stop = walk.get(walk.size() - 1);
                endY = endGround(level, stop[0], stop[1]);
                LOGGER.info("Road {}: {} is still being raised, so the road stops short of it and the last {}"
                                + " stations wait for its note", route.id(), town.town(), held.size() - 1);
            } else if (back) {
                LOGGER.info("Road {}: met at its far end, so it is laid from {}, {} back to its build",
                        route.id(), dry.farX(), dry.farZ());
            }
            approach = walk;
            graded = walk.size() < 2 ? null : new Graded(level, route, approach, startY, endY);
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
     * <p>
     * A bridge of its own that is not built of those five woods is not cleared by this: a re-run stands its pieces
     * over the ones already there rather than taking them out, because clearing stone out of the box the abutments
     * stand in would take the bank the span rests on.
     */
    private static int sweepColumns(ServerLevel level, Route.Crossing route, int from, int to) {
        int cleared = 0;
        int across0 = -route.half() - 8;
        int across1 = route.half();
        for (int along = from; along <= to; along++) {
            for (int across = across0; across <= across1; across += 8) {
                level.getChunk(route.x(along, across) >> 4, route.z(along, across) >> 4);
            }
            for (int across = across0; across <= across1; across++) {
                int x = route.x(along, across);
                int z = route.z(along, across);
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

    /** The templates a span is stood from, and the block its piers are drawn to the bed in. */
    private record Pieces(String end, String middle, Block support) {
    }

    /**
     * The templates a span is stood from: the bridge its crossing names, or the span's own pieces where none does.
     * <p>
     * A named bridge is the owner's own build and it is in the jar or it is not, so this is asked of the classpath
     * rather than of the world. A bridge named before it is built would otherwise refuse the whole span, which is a road
     * with a river it cannot cross; instead the span is stood with the standard pair and the log says so once for the
     * pass, and the named bridge takes over the day its two pieces are dropped into the jar.
     * <p>
     * The support goes with the pieces rather than with the span: a bridge of stone is drawn down in stone and a timber
     * one in oak log, so the block a pier is carried to the bed in is the bridge's answer and never the standard's.
     */
    private static Pieces pieces(Route.Crossing span) {
        Roads.Bridge named = Roads.bridge(span);
        if (named == null) return new Pieces(span.endPiece(), span.middlePiece(), Blocks.OAK_LOG);
        if (shipped(named.endPiece()) && shipped(named.middlePiece())) {
            LOGGER.info("Road {}: the crossing at {}, {} is stood as the {} bridge", span.id(), span.bankX(),
                    span.bankZ(), named.name());
            return new Pieces(named.endPiece(), named.middlePiece(), named.support());
        }
        LOGGER.info("Road {}: the {} bridge is not in the jar ({} and {}), so its crossing is stood with the standard"
                + " bridge", span.id(), named.name(), named.endPiece(), named.middlePiece());
        return new Pieces(span.endPiece(), span.middlePiece(), Blocks.OAK_LOG);
    }

    /** Whether a piece of that name is in the jar, asked without reading it. */
    private static boolean shipped(String name) {
        try (InputStream in = RoadCommands.class.getResourceAsStream(
                "/data/tolkienmobs/structure/" + name + ".nbt")) {
            return in != null;
        } catch (IOException unreadable) {
            return false;
        }
    }

    /**
     * The bridge's pieces in the order they are stood, the near abutment first and the far one last, each as x, y, z,
     * rotation and the kind of piece it is.
     * <p>
     * The two ends and the middles between them, the near end turned about so its abutment faces the bank it stands on.
     * <p>
     * A turned template is anchored at its own far corner, not its near one: the placement settings rotate about the
     * position handed in, so a nine-wide piece turned through half a circle occupies the nine columns ending at that
     * position rather than the nine starting from it. An end therefore stands over its anchor and the eight columns
     * behind it whichever bank it is on, and that is what decides where the deck begins: a span whose near bank is the
     * high coordinate has both of its ends reaching out over the water, and a span whose near bank is the low one has
     * both of them standing on their own banks. The middles are the columns left between whatever the ends cover.
     * <p>
     * That gap is read from both ends. It used to be walked from the near bank one way only - towards the low
     * coordinate - which is the way the ford happened to be crossed: a road that crossed the other way was planned a
     * deck that started behind its own near abutment, so the loop ran out at its first step and the span was stood as
     * two ends and fifty columns of open water between them (the mountains, 18 November). Both directions are laid now,
     * and a middle that crosses the far abutment is closed over by it, since the far end is always stood last.
     */
    private static List<int[]> bridgePlan(ServerLevel level, Route.Crossing route, Pieces pieces)
            throws IOException {
        StructureTemplate end = template(level, pieces.end());
        StructureTemplate middle = template(level, pieces.middle());
        int endWide = end.getSize().getX();
        int pieceLong = middle.getSize().getX();
        int step = route.step();
        int near = route.bank();
        int far = route.far();
        int low = route.line() - route.half();
        int high = route.line() + route.half();
        // The first column clear of the near abutment, and the last one still in front of the far abutment: the end
        // pieces cover the eight columns behind their anchors, so which column is clear depends on which way the span
        // runs, and the middles fill from one to the other.
        int first = step < 0 ? near - endWide : near + 1;
        int last = step < 0 ? far + endWide : far - 1;
        List<int[]> plan = new ArrayList<>();
        if (!route.alongX()) {
            // A span along z is the ford's plan stood on its side. A turned piece is laid about the origin rather
            // than about its own corner, so COUNTERCLOCKWISE_90 puts a local column at X + z and a local row at Z - x:
            // a piece anchored on the band's low x covers that column and the eight above it, and a middle turned that
            // way runs from seven below its anchor up to it. CLOCKWISE_90 is the same piece the other way about, so a
            // span running towards the high coordinate is stood from the band's high x and its middles reach forwards
            // from their anchors rather than back.
            plan.add(new int[]{low, route.endBase(), near, Rotation.COUNTERCLOCKWISE_90.ordinal(), END_PIECE});
            int cursor = first;
            while (step < 0 ? cursor >= last : cursor <= last) {
                plan.add(step < 0
                        ? new int[]{low, route.bridgeBase(), cursor,
                                Rotation.COUNTERCLOCKWISE_90.ordinal(), MIDDLE_PIECE}
                        : new int[]{high, route.bridgeBase(), cursor,
                                Rotation.CLOCKWISE_90.ordinal(), MIDDLE_PIECE});
                cursor += step * pieceLong;
            }
            plan.add(new int[]{high, route.endBase(), far, Rotation.CLOCKWISE_90.ordinal(), END_PIECE});
            return plan;
        }
        plan.add(new int[]{near, route.endBase(), high, Rotation.CLOCKWISE_180.ordinal(), END_PIECE});

        // Each middle is anchored on its own north-east corner, so a middle running towards the low coordinate ends on
        // the cursor itself and a middle running the other way begins on it. The far end closes whatever the last
        // middle left overlapping, and the near end is never touched because the first middle starts clear of it.
        int cursor = first;
        while (step < 0 ? cursor >= last : cursor <= last) {
            plan.add(step < 0
                    ? new int[]{cursor - pieceLong + 1, route.bridgeBase(), low,
                            Rotation.NONE.ordinal(), MIDDLE_PIECE}
                    : new int[]{cursor + pieceLong - 1, route.bridgeBase(), high,
                            Rotation.CLOCKWISE_180.ordinal(), MIDDLE_PIECE});
            cursor += step * pieceLong;
        }
        plan.add(new int[]{far, route.endBase(), low, Rotation.NONE.ordinal(), END_PIECE});
        return plan;
    }

    /**
     * One of the plan's pieces, stood where the plan puts it.
     * <p>
     * A piece is a whole template - nine by nine by a few blocks of timber, every one of them placed, lit and
     * updated - so the bridge is stood a piece a step rather than as many pieces as the span takes in one tick.
     */
    private static void stand(ServerLevel level, Pieces pieces, int[] piece) throws IOException {
        StructureTemplate template = template(level, piece[4] == END_PIECE ? pieces.end() : pieces.middle());
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
     * own the span stands on stilts that stop in mid-air. What is looked for is the lowest block of the pier's own
     * material - the named bridge's support, or oak log for the standard one - in each column of the span; below it
     * the column is filled with that block while it is air or water, and the fill stops at the first block that is
     * neither - the bed, or the bank under an abutment.
     */
    private static int pillarsColumns(ServerLevel level, Route.Crossing route, Block support, int from, int to) {
        int drawn = 0;
        for (int along = from; along <= to; along++) {
            for (int across = -route.half(); across <= route.half(); across++) {
                int x = route.x(along, across);
                int z = route.z(along, across);
                int base = Integer.MIN_VALUE;
                for (int y = route.bridgeBase() + 6; y >= route.bridgeBase() - 2; y--) {
                    if (level.getBlockState(new BlockPos(x, y, z)).is(support)) base = y;
                }
                if (base == Integer.MIN_VALUE) continue;
                for (int y = base - 1; y > base - PILLAR_FLOOR; y--) {
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState under = level.getBlockState(at);
                    if (!under.isAir() && under.getFluidState().isEmpty()) break;
                    level.setBlock(at, support.defaultBlockState(), 2);
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
        /** The most candidates one search may hold: past it the window narrows, then the line goes straight. */
        private static final long WINDOW = 600_000L;
        /** The least of them a walk may settle before it gives up, so a narrow window is never cut short by its size. */
        private static final int SETTLE_AT_LEAST = 240_000;
        /**
         * How far a road may run for the distance it has to cover: two and a half times as far as the crow flies, so a
         * road that switchbacks up a fell is still a road and one that wanders a meadow is not one the walk will find.
         */
        private static final double DETOUR = 2.5D;
        /** What a diagonal step costs over a straight one: one and the root of two less, which is the eight-way guess. */
        private static final double DIAGONAL = Math.sqrt(2.0D) - 1.0D;
        /** What a step pays for standing a block off the grade between the ends, and how far off it stops counting. */
        private static final double PROFILE_PENALTY = 0.2D;
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

        private Descent(Route route, int startX, int startZ, int goalX, int goalZ, boolean leave) {
            this.route = route;
            // The road leaves the build square to its own wall, and the walk begins where that run-out stops: a
            // searched first step is a diagonal one as often as not, and a road that turns on the doorstep turns
            // through the doorstep. A leg that begins at a bend has no wall to leave, so it begins where it is.
            int[] out = leave ? outward(route, startX, startZ) : new int[]{0, 0};
            this.outX = out[0];
            this.outZ = out[1];
            int fromX = startX + outX * RUN_OUT;
            int fromZ = startZ + outZ * RUN_OUT;
            int[] bounds = window(fromX, fromZ, goalX, goalZ, Roads.wander(route));
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
            // The whole window at the most, not half of it. The price of standing off the grade flattens the cost of
            // the ground around the road, so a walk may have to settle a great deal of what it can see before the far
            // end comes off the queue at all; a limit of half the window stopped it with its way half walked and no
            // word said about it. A step is four thousand candidates, so the whole of even the widest window is a few
            // seconds of ticks rather than a stall.
            this.limit = Math.max(SETTLE_AT_LEAST, nodes);
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
         * The box the search may walk in: the two ends, and {@link Roads#wander} blocks either side of them.
         * <p>
         * The window used to be the valley's own constants. It is the route's now, because a road between two builds
         * is wherever those builds are - so it is measured from the ends, and halved while it would hold more
         * candidates than one server tick should pay for. A route so wide that even the narrowest window is too big
         * answers null, and the pass lays the straight line rather than reserving memory the road is unlikely to use.
         *
         * @return x0, x1, z0, z1, or null when no window fits
         */
        private static int[] window(int startX, int startZ, int goalX, int goalZ, int margin) {
            for (int width = margin; width >= 8; width /= 2) {
                int x0 = Math.min(startX, goalX) - width;
                int x1 = Math.max(startX, goalX) + width;
                int z0 = Math.min(startZ, goalZ) - width;
                int z1 = Math.max(startZ, goalZ) + width;
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
                    if (from[goal] == -1) {
                        // Said out loud, because the pass then lays a straight line through the country and whoever
                        // reads the log should know the road underfoot is not a walked one, nor why there is none.
                        LOGGER.info("Road {}: the walk gave up {} blocks short of {}, {} - {} candidates settled, {} "
                                        + "attempts, {}",
                                route.id(), Math.round(Math.hypot(goalX - startX, goalZ - startZ)), goalX, goalZ,
                                looked, tries + 1,
                                open.isEmpty() ? "nothing left to settle" : "the window's budget spent");
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
            // A road with no build has no wall to leave square to. Its footprint is empty, and the walk from its end
            // begins on the mark the scout stood on rather than sixteen stations out from a corner at the origin.
            if (route.cityX0() >= route.cityX1() || route.cityZ0() >= route.cityZ1()) return new int[]{0, 0};
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
        /**
         * Why the walk found no way, or null when it did.
         * <p>
         * The reason matters to whoever asked, because the faults are different: a window too small for the leg is a
         * route that wants another mark in it, and ground with no way through it is country the road cannot cross at all.
         */
        String why() {
            if (!fits) return "the leg is longer than one window of the search";
            if (goal < 0 || from[goal] == -1) {
                return open.isEmpty() ? "the ground leaves no way through it"
                        : "the window's budget was spent before the far end was reached";
            }
            return null;
        }

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
     * <p>
     * That grade of its own is an approach's, not a road's: a settlement's way out is built, and a build may stand on
     * what it is built on. A road that carries a valley's traffic is the country's, and the country is what it has to
     * be walked on - so a route that names its kind is held to its own column's ground, and the embankment is not a
     * thing it does.
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
         * one column is not built into the road. Laid over rather than followed - a hole is filled on stone - except by
         * a road that names its kind, which is held to its own column's ground instead.
         */
        private final int[] relief;
        private final double[] height;
        private int at;
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
            // Every road starts on its own ground rather than on the relief. Relief is the middle of
            // the ground eight stations either side, and a profile begun there is already a shelf; the band below
            // can only say how far the shelf may drift, never pull it down to the hollows it is passing over.
            // Measured in Roads 5, the Thorenhad road ran level at 80 for three hundred stations over ground between
            // 74 and 79, carrying four to five blocks of stone under it the whole way - the fill that read as the road
            // taking off and staying up. Started on the ground, the same smoothing has a dip to walk into instead of
            // an average to flatten, and the band is then only what keeps a bump from being built into the road.
            // Water keeps the relief, because the river is the bridge's and a station over it has no ground to answer
            // to.
            // A banded road is a built road too: it is a town's street at its own end, and held to a road's cut and fill
            // wherever the bands put it.
            boolean grounded = Roads.kind(route) != null || Roads.banded(route);
            for (int i = 0; i < n; i++) {
                boolean dry = ground[i] != WATER && ground[i] != VOID;
                height[i] = dry ? ground[i] : relief[i];
            }
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
                    boolean dry = ground[i] != WATER && ground[i] != VOID;
                    int base = dry ? ground[i] : relief[i];
                    int band = grounded && dry ? Roads.cut(route, CUT_FILL) : WILD_BAND;
                    height[i] = Mth.clamp(height[i], base - band, base + band);
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
            //
            // A road that names its kind answers to its own column's ground rather than to the relief, and is not
            // carried over anything. Relief is the middle of eight stations either side, and a middle is a height a
            // hillside does not have: read off it the grade ran flat at 81 for two hundred and seventy stations over
            // ground between 75 and 82, and stood as much as eight blocks of stone above the low part of it - a road
            // that had taken off from the ground. The road a valley walks is the ground it crosses, so the band here is
            // the ground's own: a hollow is a dip it walks down into and out of, not an embankment it rides over. Water
            // keeps the relief, because the river is the bridge's and there is no ground in the station to answer to.
            for (int i = 1; i < n - 1; i++) {
                boolean dry = ground[i] != WATER && ground[i] != VOID;
                int base = dry ? ground[i] : relief[i];
                int band = grounded && dry ? Roads.cut(route, CUT_FILL) : WILD_BAND;
                height[i] = Mth.clamp(height[i], base - band, base + band);
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
            // A recorded bend is a mark the scout stood on, and it carries the level they stood at. Only its x and
            // z were read until now, so a mark under the grade - a shelf the scout walked and the road was about to
            // pass over - could not pull the road down to itself: the switchback it pinned was never reached and the
            // road ran on above it, a shelf with a road over it and no way down. A mark may cut and may not lift: the
            // grade is brought down to the mark's own level where that is within what this road may cut of the ground
            // under it, and the stations either side are walked down to meet it a step at a time, which is how the
            // arms of a switchback reach the shelf they were pinned on instead of passing above it.
            // A crossing is a height the road is at rather than one it may sink to. Every station standing on one is
            // put at the crossing's own walk height before the steps are settled, so the sweep out of the bank is a ramp
            // up to the deck instead of a dive into the river, and it is written in as a pin as well so the settle that
            // follows cannot walk the crossing's own stations off it. A mark may cut and may not lift; a crossing is
            // neither - it is the survey, and the water under it is no ground to grade from at all.
            Map<Integer, Integer> pins = marks(route, line);
            for (int i = 0; i < n; i++) {
                // The stations standing in the water, not a band around the surveyor's line: the eased walk does not
                // run the marks exactly, and a crossing matched only within a block of them was never met at all - which
                // is a grade left to dive into the river it was told to cross, and a road climbing out the far side.
                if (ground[i] != WATER) continue;
                Roads.Water over = Roads.water(line.get(i)[0], line.get(i)[1], CROSSING_ACROSS);
                if (over == null) continue;
                height[i] = over.walkOn();
                pins.put(i, over.walkOn());
            }
            for (int settle = 0; settle < SETTLE_PASSES && !pins.isEmpty(); settle++) {
                for (Map.Entry<Integer, Integer> pin : pins.entrySet()) {
                    int i = pin.getKey();
                    if (ground[i] == WATER || ground[i] == VOID) continue;
                    int lowest = ground[i] - Roads.cut(route, CUT_FILL);
                    int mark = Math.max(pin.getValue(), lowest);
                    if (mark < height[i] - MAX_STEP) height[i] = mark;
                }
                for (int i = 1; i < n; i++) {
                    if (pins.containsKey(i)) continue;
                    height[i] = Mth.clamp(height[i], height[i - 1] - MAX_STEP, height[i - 1] + MAX_STEP);
                }
                for (int i = n - 2; i >= 0; i--) {
                    if (pins.containsKey(i)) continue;
                    height[i] = Mth.clamp(height[i], height[i + 1] - MAX_STEP, height[i + 1] + MAX_STEP);
                }
            }
            // The grade is settled on halves and on nothing finer. A profile carrying a third of a block between two
            // stations has no block to lay it with, and whichever neighbour rounds it becomes a step the walker feels;
            // quantised here, where the whole profile is known, the surface a column gets can be read from that column's
            // own height alone instead of from what the column beside it happens to be - which is what left the corners
            // and the diagonals of every terrace open, since a column with no lower neighbour on an axis was never
            // compared against anything.
            for (int i = 0; i < n; i++) height[i] = Math.round(height[i] * 2.0D) / 2.0D;
            for (int settle = 0; settle < SETTLE_PASSES; settle++) {
                for (int i = 1; i < n; i++) {
                    height[i] = Mth.clamp(height[i], height[i - 1] - MAX_STEP, height[i - 1] + MAX_STEP);
                }
                for (int i = n - 2; i >= 1; i--) {
                    height[i] = Mth.clamp(height[i], height[i + 1] - MAX_STEP, height[i + 1] + MAX_STEP);
                }
            }
        }

        /**
         * The stations a road's recorded bends stand on, by the level each mark carries.
         * <p>
         * A mark is placed on the ground the road is walked along, so its height is the surface that road is meant to
         * keep. The station is the one nearest the mark: a corner is eased, so the walked line rounds it rather than
         * passing through the block itself.
         */
        private static Map<Integer, Integer> marks(Route route, List<int[]> line) {
            Map<Integer, Integer> marks = new HashMap<>();
            for (BlockPos bend : Roads.bends(route)) {
                int best = -1;
                double nearest = 4.0D;
                for (int i = 0; i < line.size(); i++) {
                    double away = Math.hypot(line.get(i)[0] - bend.getX(), line.get(i)[1] - bend.getZ());
                    if (away < nearest) {
                        nearest = away;
                        best = i;
                    }
                }
                if (best >= 0) marks.putIfAbsent(best, bend.getY());
            }
            return marks;
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

        /** The station this road has got to, clamped to its own line so a finished road still answers where it ends. */
        int[] head() {
            if (n < 2) return new int[]{route.pathX(), route.pathZ()};
            return line.get(Math.clamp(at, 0, n - 1));
        }

        private int[] summary() {
            return new int[]{laid, cut, filled, open, steepest, cleared};
        }

        /** The height this stretch settled at its last station: where the next stretch of the road has to start. */
        private double end() {
            return n == 0 ? 0.0D : height[n - 1];
        }

        /** The grade as it lies, for a note that has to carry more than one stretch of the same road. */
        private double[] heights() {
            return height;
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
            // How deep this road may cut into the hill it crosses, which is a cutting on a face it was surveyed across
            // and the ordinary band anywhere else.
            int cut = Roads.cut(route, CUT_FILL);
            // A grade saved by a pass that read its ground off chunks nobody had made can carry a height that is not a
            // height in this world - the floor of it - and believing that would lay the rest of the road under the
            // surface it never reaches. A station holding one takes the ground under it instead.
            if (Mth.floor(height[i]) < level.getMinY() || Mth.floor(height[i]) > level.getMaxY()) {
                int before = i > 0 ? Mth.floor(height[i - 1]) : under;
                boolean known = before >= level.getMinY() && before <= level.getMaxY();
                int here = under != VOID && under != WATER ? under : before;
                retarget(i, known ? Math.min(here, before + STEP_BLOCKS) : here);
            }
            if (under != VOID && under != WATER && Mth.floor(height[i]) < under - cut) {
                int before = i > 0 ? Mth.floor(height[i - 1]) : under - cut;
                int lifted = Math.min(under - cut, before + STEP_BLOCKS);
                if (lifted > Mth.floor(height[i])) retarget(i, lifted);
            }
            int target = Mth.floor(height[i]);
            // Whether this station is a half step. Every column of the station takes the same answer, and the answer is
            // the station's own height: a road on halves cannot be a block here and a slab there along one terrace,
            // because the profile never asks for one.
            boolean halfStep = Math.round((height[i] - target) * 2.0D) == 1;
            if (i > 0) steepest = Math.max(steepest, Math.abs(target - Mth.floor(height[i - 1])));
            // A road that names its kind is laid at it the whole way - a main road stays seven wide across open
            // country rather than narrowing to a track between places. Every other takes its build's kind: a city's
            // road widens as it comes in to the gate, a camp's is a wild track from its own door.
            Roads.Kind named = Roads.kind(route);
            Roads.Kind kind = roadKind(route, x, z);
            int half = kind.half();
            // A hairpin is wider than a run, by one kind: the apron gives the turn somewhere to be walked around, and
            // a road that widens two kinds at every switchback is three kinds wide where it turns. A road held to its
            // own kind keeps it through a turn too - the apron is a band's habit, not a named road's.
            if (named == null && turning(line, i)) half += 1;
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
                    // A wild track's edge is worn rather than ruled: one column in three of it is left as the country
                    // has it, which is the reference surface's ragged strip. A road that names its own kind keeps that
                    // width exactly, so a path laid three wide is three wide the whole way.
                    if (named == null && kind == Roads.Kind.WILD && half == kind.half()
                            && (Math.abs(dx) == half || Math.abs(dz) == half)
                            && jitter(at, into) % 3 == 0) {
                        continue;
                    }
                    // The column is read as the world has it, and the world has to have it: a chunk nobody has made
                    // yet reads as nothing at all, and a block written into it would be dropped anyway.
                    level.getChunk(at >> 4, into >> 4);
                    int groundAt = surfaceAt(level, at, into);
                    // A surveyed span owns the water it stands over: the road stops at the bank and picks up on the far
                    // one, because the deck, its piers and its abutments are the crossing route's own pieces, laid by
                    // the pass that walks that route rather than by this one. A ford is the road's own bed and is laid
                    // here. Asked of the ground rather than of the column's idea of itself: what a river column answers
                    // is water, or a lily pad standing on it, and either is water to a bridge.
                    Roads.Water over = Roads.water(at, into, CROSSING_ACROSS);
                    if (over != null && (groundAt == WATER || groundAt == VOID)) {
                        if (over.kind() == Roads.Wet.FORD) {
                            carry(level, at, into, over, dx == 0 && dz == 0 && i % 4 == 0);
                        } else {
                            open++;
                        }
                        continue;
                    }
                    if (groundAt == VOID) {
                        face(level, at, into, target, halfStep);
                        laid++;
                        continue;
                    }
                    if (groundAt == WATER) {
                        // A crossing nobody surveyed is still the river's, and only the road's own three middle
                        // columns are carried over it.
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
                        face(level, at, into, target, halfStep);
                        laid++;
                        continue;
                    }
                    int step = target - groundAt;
                    // Ground above the grade is cut no deeper than the road's own allowance: a hill standing that far
                    // over a profile is a hill the profile was wrong about, and a road that bores into it is a trench.
                    // A road surveyed to be cut across a face has that much allowance, and uses it. Ground below
                    // the grade is filled up to it however deep the hollow - that is the other half of the same
                    // question, and refusing it was the road stopping at the first hole in the ground, since the
                    // profile had already been carried over that hole and the band's own columns had not.
                    if (step < -cut || step > HOLE_FILL) {
                        open++;
                        continue;
                    }
                    level.getChunk(at >> 4, into >> 4);
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
                    face(level, at, into, target, halfStep);
                    laid++;
                    // A road cut through a wood leaves its canopy hanging, so the corridor's own columns are
                    // cleared of leaves and logs above the road's surface.
                }
            }
            // The road's own columns and the one either side of them are cleared to the tunnel's headroom: five
            // blocks over the middle, four at the second column out, three at the edge, and nothing past that. The
            // wood keeps every leaf above the roof, so a road through a wood is a way under its canopy rather than a
            // trench cut through the crowns. A trunk standing in the lane goes whole, over the band's own columns: a
            // tree cut off at the roof leaves the crown it was carrying standing in the air over the road.
            int band = half + 1;
            for (int dx = -band; dx <= band; dx++) {
                for (int dz = -band; dz <= band; dz++) {
                    int offset = Math.max(Math.abs(dx), Math.abs(dz));
                    int reach = canopyReach(offset);
                    if (reach <= 0) continue;
                    int at = x + dx;
                    int into = z + dz;
                    // Nothing beside the road is trimmed inside the build itself, for the same reason nothing of it is
                    // paved: the wall and whatever grows behind it are the build's.
                    if (at >= route.cityX0() && at <= route.cityX1()
                            && into >= route.cityZ0() && into <= route.cityZ1()) {
                        continue;
                    }
                    int base = offset <= half ? target : surfaceAt(level, at, into);
                    if (base == VOID || base == WATER) base = target;
                    // A trunk standing in the lane goes whole: the tunnel's headroom is not the tree's top, and taking
                    // only the headroom out of it leaves a bare pole with the crown it carries hanging over the road.
                    // The column's own top is where that crown ends.
                    if (offset <= half
                            && level.getBlockState(new BlockPos(at, base + reach + 1, into)).is(BlockTags.LOGS)) {
                        reach = Math.max(reach, level.getHeight(Heightmap.Types.MOTION_BLOCKING, at, into) - base);
                    }
                    cleared += trimCanopy(level, at, into, base, reach, offset <= half);
                }
            }
            return counted;
        }

        /**
         * One column's road surface: the block the road is made of, and, on a station that stands half a block above the
         * whole, the slab that makes the half.
         * <p>
         * The slab goes on top rather than being sunk into the block, which is the one thing this rule had to get right:
         * a bottom slab in the column's own cell is half a block lower than the block beside it, so a station the profile
         * puts half a block higher would have been laid half a block lower - every rise built as a fall. Over the block,
         * the surface of a column is its own height, whichever half it falls on, and a walker never drops a half step to
         * take one.
         */
        private void face(ServerLevel level, int x, int z, int target, boolean halfStep) {
            level.setBlock(new BlockPos(x, target, z), surface(route, x, z), 2);
            if (halfStep) level.setBlock(new BlockPos(x, target + 1, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2);
        }

        /**
         * Carries the road over a stretch of water its route was told about.
         * <p>
         * A ford is a bed rather than a deck: the road's own surface is laid at the height the survey gives it and the
         * column under it is made solid down to the river's floor, so a wade is a walk on stone rather than a step off a
         * block left floating over the channel - and the water is left standing over it, because a ford pumped dry is a
         * causeway. A span is the other way round: the deck is laid at that height and only a pier every fourth station
         * of it is drawn down, so the water still runs open under a deck rather than through a wall of stone.
         * <p>
         * A pier is read through water rather than stopped by it - the columns between the deck and the bed are fluid,
         * and fluid is not a floor - and it ends at the first block that is neither air nor water, capped at the bridge
         * pieces' own forty-eight in case there is no bed to find.
         */
        private void carry(ServerLevel level, int at, int into, Roads.Water over, boolean pier) {
            int seat = over.walkOn();
            level.getChunk(at >> 4, into >> 4);
            level.setBlock(new BlockPos(at, seat, into), surface(route, at, into), 2);
            laid++;
            if (over.kind() != Roads.Wet.FORD && !pier) return;
            int floor = Math.max(level.getMinY(), seat - 48);
            for (int y = seat - 1; y >= floor; y--) {
                BlockPos below = new BlockPos(at, y, into);
                BlockState state = level.getBlockState(below);
                if (!state.isAir() && state.getFluidState().isEmpty()) return;
                level.setBlock(below, Blocks.STONE.defaultBlockState(), 2);
                filled++;
            }
        }

        /**
         * Clears the crown over one column, up to {@code reach} above the ground it grows from.
         * <p>
         * Leaves go wherever they stand over the lane. Trunks go only where the road itself runs: a trunk cut at
         * shoulder height leaves the crown it was carrying standing in the air, which looks worse than the wall this
         * was meant to take away. So a tree beside the road keeps its trunk and every leaf still hanging from it, and
         * only the leaves which reach out over the lane are taken.
         *
         * @return how many blocks were cleared, for the pass's own total
         */
        private static int trimCanopy(ServerLevel level, int x, int z, int base, int reach, boolean trunks) {
            int count = 0;
            for (int y = base + 1; y <= base + reach; y++) {
                BlockPos crown = new BlockPos(x, y, z);
                BlockState over = level.getBlockState(crown);
                boolean leaf = over.is(BlockTags.LEAVES);
                if (!leaf && !(trunks && over.is(BlockTags.LOGS))) continue;
                level.setBlock(crown, Blocks.AIR.defaultBlockState(), 2);
                count++;
            }
            return count;
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
            if (Math.abs(ground - cornerGround) > Roads.cut(route, CUT_FILL)) return false;
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

    /**
     * The top block a road could stand on, or {@link #WATER} when that column is the river.
     * <p>
     * A heightmap answers with whatever stops motion, and that includes the crowns of the trees. Read that
     * way, a valley road is a road laid on treetops: it stands up stone columns wherever the canopy was
     * higher than the bank, and it grades against a forest roof instead of a hillside. This walks down past
     * everything that grows - leaves, trunks, vines, snow, the plants between the pavers - to the ground the
     * tree is growing on, and only then asks whether it is water. A column whose topmost log had leaves
     * under it used to stop at the leaves and be read as that height: Roads 7 had road blocks with oak leaves
     * directly beneath them at the junction above Barachen's camp, and the canopy clearing then took the
     * leaves away and left the road hanging in the air over the gap it stood on.
     */
    private static int surfaceAt(ServerLevel level, int x, int z) {
        // The chunk is made before its ground is asked for. A heightmap read off a chunk nobody has made is the
        // floor of the world, and the rest of this class then works around that: a station that could not be read was
        // given the ground of the nearest one that could, so a road planned ahead of the country was planned as a flat
        // line at the height of the stations its own two ends stood on. Measured in Roads 6: Barachen's camp road ran
        // level at 74 for all two hundred and forty-one stations while the country it crossed rose to 90, and the three
        // cave roads ran level at 75 up Eregion's hillside, their climb appearing only at the crag end - the one end the
        // player had already stood at. It also explains which way they walked: the search preferred the corridor it
        // could read, so cave 2 and cave 3 shared nine hundred stations within seven blocks of each other and both set
        // off south before turning for their caves. Ground that has not been made cannot be planned over; make it.
        level.getChunk(x >> 4, z >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        BlockPos at = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(at);
        while (y > level.getMinY() && (state.isAir() || RoadPaint.cover(state))) {
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
     * The surface of one block: the mix of the kind of road the column is, chosen per block from a hash of x and z so
     * that a chunk border never shows, and so the same column takes the same block whenever asked. The kind is the
     * route's own where it names one, a city's bands where the build is a city, and the wild track's everywhere else -
     * so a camp's track is gravel and dirt at its own door, a main road is gravel and dirt seven blocks wide, and only
     * a city's road is paved.
     */
    private static BlockState surface(Route route, int x, int z) {
        Roads.Kind kind = roadKind(route, x, z);
        int roll = jitter(x, z) % 100;
        if (kind == Roads.Kind.URBAN) {
            if (roll < 50) return TolkienBlocks.STONE_PATH.get().defaultBlockState();
            if (roll < 77) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 87) return Blocks.STONE_BRICKS.defaultBlockState();
            if (roll < 95) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }
        if (kind == Roads.Kind.TRANSITION) {
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

    /**
     * The kind of road a column of a route is: the kind the route names, or - where it names none - the bands if the
     * road is one of the banded ones, and a wild track if it is a wild build's own. A camp's door is not a gate, so
     * nothing a wild build walks out of is paved or five wide; a main road between two places is a road at its ends.
     */
    private static Roads.Kind roadKind(Route route, int x, int z) {
        Roads.Kind named = Roads.kind(route);
        if (named != null) return named;
        return Roads.banded(route) ? kindAt(fromWalls(route, x, z)) : Roads.Kind.WILD;
    }

    /**
     * The kind of road a column standing this far outside the walls belongs to: the settlement's own inside the town
     * band, the transition out to the edge of the approach, the wild track beyond it. This is a city's rule, and the
     * rule for a road that names no kind of its own.
     */
    private static Roads.Kind kindAt(int near) {
        if (near <= URBAN_BAND) return Roads.Kind.URBAN;
        if (near <= TRANSITION_BAND) return Roads.Kind.TRANSITION;
        return Roads.Kind.WILD;
    }

    /** How far outside the city's footprint a column stands, in blocks. */
    private static int fromWalls(Route route, int x, int z) {
        if (route.cityX0() == 0 && route.cityX1() == 0 && route.cityZ0() == 0 && route.cityZ1() == 0) {
            // A road with no build behind it has no walls to measure from, so the band is measured from the block it
            // leaves by - the doorway the build's own edge would have been. Without this the four noughts read as a
            // footprint at the world's origin and every column of the road came out past the last band.
            return (int) Math.round(Math.hypot(x - route.pathX(), z - route.pathZ()));
        }
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

        /** Forgets what a pass had done for a route, so the next one searches its way again. */
        static void forget(ServerLevel level, String route) {
            Progress store = instance(level);
            if (ROADS.remove(route) != null) store.setDirty();
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
                tag.putIntArray("approach", flatten(job.whole != null ? job.whole : job.approach));
                double[] heights = job.heights != null ? job.heights
                        : job.graded == null ? null : job.graded.heights();
                if (heights != null) tag.putLongArray("height", bits(heights));
                if (job.done != null) tag.putInt("stations", job.done.along());
            }
            ROADS.put(job.route.id(), tag);
            instance(level).setDirty();
        }
    }
}
