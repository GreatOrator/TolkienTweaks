package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * Bucklebury, raised by the world rather than by a hand.
 * <p>
 * The town east of the Brandywine is a cut like the Shire's village and Bree's: 478 by 278 columns of the bank, 77
 * high, eighty pieces and a {@code deck} of thirteen in the jar's own {@code structure/bucklebury}. A cut in the jar
 * is a build any world can hold, and this is the pass that puts it down: the four things a build wants in the order it
 * wants them, at the same budget a tick.
 * <ul>
 * <li>the ground, asked for a chunk at a time and taken as it comes, so the first start of a world does not generate
 * nine hundred chunks in one tick;</li>
 * <li>the cut, laid piece by piece at {@link ArdaMap#buckleburyPlan()};</li>
 * <li>the country cleared out of the footprint, a band of columns a tick, so the drawn bank inside the town's own box
 * does not stand through its streets;</li>
 * <li>the ring dressed to the build's outline by {@link Dressing} - the same drape the city, the village and Bree use,
 * with the town's own floor as its deck, read out of the cut's table, and its trees left out of its shape.</li>
 * </ul>
 * <p>
 * It is {@link Bree}'s pass with Bucklebury's numbers, as that was {@link Hobbiton}'s with Bree's: the two builds want
 * the same four things in the same order, and then a fifth - Brandy Hall, stood on the town's own ground once the ring
 * is dressed. Brandy Hall is not {@code TownHalls}': that class stands one hall per alliance at a pin a hand walks, and
 * this is one build in one town, so its box is written here beside the town it belongs to. Bucklebury is a town the
 * catalogue knows now, so it has its alliance's mustering hall as well: that one is pinned in {@code Pins} and raised
 * by {@code TownHalls} at the same points this class raises Brandy Hall, after the pass and never before it. What is
 * not here is the village's own folk: Bucklebury has no faces of its own cut yet.
 * <p>
 * Nothing has to be asked for. A world made after this ships holds the town as soon as a player walks within
 * {@link #REACH} of its ground, which is the rule the village keeps when its config has the start-of-world pass off;
 * {@code /bucklebury raise} asks for it at any time, and {@code /bucklebury drape} dresses the ground round a town that
 * already stands without laying a block of the town again.
 */
@EventBusSubscriber(modid = MODID)
public final class Bucklebury {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bucklebury.class);

    /** The cut's name: its folder in the jar's {@code structure/} and, for a re-cut, in the save's {@code cuts/}. */
    private static final String CUT = "bucklebury";
    /** How much wall clock the pass may take from a tick, in milliseconds: the village's and the town's own quarter. */
    private static final double BUDGET_MS = 5.0D;
    /**
     * How near a player has to be to the footprint for the town to be raised, in blocks.
     * <p>
     * Measured from the box rather than from a corner, as the village's and the town's are: at 478 by 278 a player
     * standing at one wall is not far from the other, and the pass is meant to be seen coming up by whoever finds it.
     */
    private static final int REACH = 192;
    /** How long after a start the pass waits before it will touch anything: a start is busy enough as it is. */
    private static final int GRACE_TICKS = 200;
    /** How many of the town's columns one unit of the clear works: a band across the footprint. */
    private static final int COLUMNS_PER_UNIT = 200;
    /** What this world has made of the town, beside its cut: dimension, corner, cut, drape rules, ground. */
    private static final String MARKER = "_stands.txt";
    /**
     * The drape's own rules, as a number a hand bumps when it changes them.
     * <p>
     * The note's fingerprint answers for the cut's table; this answers for the code that settles the ring round it, so
     * a fix to the drape reaches a world that already holds the town. One is the first: nothing has been raised under
     * these rules yet.
     */
    private static final int DRESS_VERSION = 1;
    /** How far a probe column's top may move and still be the town: growth, mostly, not a town taken away. */
    private static final int PROBE_SLACK = 6;
    /**
     * Where the note looks: the four corners of the footprint, its middle, and two more across it, inset from the
     * edge - the same seven the village and the town keep, in this footprint's own numbers (478 by 278).
     */
    private static final int[][] PROBES = {{8, 8}, {469, 8}, {8, 269}, {469, 269}, {239, 139}, {119, 69},
            {359, 209}};
    /**
     * Brandy Hall: the build, and the corner of the box it is laid from.
     * <p>
     * The owner walked the town and set the water at 1449, 92, -3203 as the spot - the bed of a peninsula in the lake.
     * A hall's box cannot stand there: 118 by 103 laid on that block takes 1,611 columns of the lake with it. So the
     * box is on the first ground east of the lake that holds it whole, inside the town's own cut, and the owner's own
     * walk of it then carried it 26 south and 3 down from where it first stood: x 1561..1678, z -3231..-3129, its west
     * edge two blocks off the lake's shore. Laid the way it was cut, so the front door at 1620, -3136 opens north over
     * the grounds, which is the room the path from it wants.
     * <p>
     * The corner's y is 92, three under the town's own deck line (82 + 13): the build's ground course is laid where the
     * owner read the ground, not on the street's own course.
     */
    private static final String HALL = "tolkienmobs:key_structures/brandyhall";
    private static final BlockPos HALL_AT = new BlockPos(1561, 92, -3231);
    /** How far past the hall's own box its ground is eased back to the country, in blocks. */
    private static final int HALL_DRAPE = 12;

    /** What the note's probes answer: the ground is there, it is gone, or the level holds no chunk to ask. */
    private static final int PRESENT = 1, GONE = 0, UNASKED = -1;

    /** One probe as the note keeps it: the top of a column and the block under it, or -1 and nothing when unread. */
    private record Probe(int top, @Nullable String block) {
    }

    /** The town being raised, or null when there is one standing or nothing to raise. */
    private static Standing standing;
    /**
     * A corner this world notes a town at whose ground could not be read at start, or null.
     * <p>
     * A start holds almost none of the town's chunks, and asking the level to measure one it does not hold generates
     * it there and then. So the note's ground is asked about again on the first tick somebody is near enough for the
     * answer to be free - which is also the only moment a trimmed town matters.
     */
    @Nullable
    private static BlockPos held;

    private Bucklebury() {
    }

    /**
     * Begins raising the town if this world does not already hold it.
     *
     * @return the town being raised, or null when it already stands or there is no cut to raise
     */
    @Nullable
    private static Standing autospawn(MinecraftServer server) {
        if (standing != null) return standing;
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return null;
        BlockPos corner = ArdaMap.buckleburyPlan();
        if (stands(level, corner)) {
            int ground = ground(level, corner);
            if (ground != GONE) {
                held = ground == UNASKED ? corner : null;
                return null;
            }
            LOGGER.info("Bucklebury: this world notes the town at {}, but not its ground; raising it again",
                    corner.toShortString());
        }
        held = null;
        List<CutCommands.Part> parts;
        try {
            parts = CutCommands.parts(level, CUT);
        } catch (IOException unreadable) {
            LOGGER.warn("Bucklebury: the town's cut could not be read, so it is not raised: {}",
                    unreadable.getMessage());
            return null;
        }
        if (parts.isEmpty()) {
            // A pass that has nothing to lay and says nothing is the worst answer a caller can get: name the folder
            // that was read, so a world waiting on a cut is not a world that has to be read to find out.
            LOGGER.warn("Bucklebury: no bucklebury cut to raise - nothing in {}, and nothing in the jar beside it",
                    CutCommands.folder(level).resolve(CUT));
            return null;
        }
        standing = new Standing(parts, corner, CutCommands.Box.of(parts, corner), CutCommands.deck(level, CUT));
        LOGGER.info("Bucklebury: {} pieces of the town are to be laid at {} over {} chunks, when somebody is within {}"
                        + " blocks", parts.size(), corner.toShortString(), standing.groundChunks(), REACH);
        return standing;
    }

    /**
     * Whether this world's town has been raised to the end of its own pass.
     * <p>
     * Read from the note alone, so it answers at a start, where none of the town's chunks are held and asking about
     * its ground would generate them.
     */
    public static boolean complete(ServerLevel level) {
        return stands(level, ArdaMap.buckleburyPlan());
    }

    /**
     * Whether a pass over the town is in hand: planned and waiting for somebody to be near it, or at work now.
     * <p>
     * The question anything standing in the town's own ground should ask before it is put down, the way the village's
     * crossing asks it: the clear takes the country out of the whole footprint, so a body stood there first falls with
     * the hill it was standing on.
     */
    public static boolean atWork() {
        return standing != null;
    }

    /** Whether this world notes a town at that corner, made of this cut and these drape rules. */
    private static boolean stands(ServerLevel level, BlockPos corner) {
        Path marker = marker(level);
        if (!Files.isRegularFile(marker)) return false;
        try {
            String[] fields = Files.readString(marker).trim().split("\\s+");
            if (fields.length < 6 + PROBES.length) return false;
            if (!fields[0].equals(level.dimension().identifier().toString())
                    || Integer.parseInt(fields[1]) != corner.getX()
                    || Integer.parseInt(fields[2]) != corner.getY()
                    || Integer.parseInt(fields[3]) != corner.getZ()) return false;
            if (!fields[4].equals(CutCommands.fingerprint(level, CUT))) return false;
            return Integer.parseInt(fields[5]) == DRESS_VERSION;
        } catch (IOException | NumberFormatException unreadable) {
            return false;
        }
    }

    /**
     * What the note's probes say about the ground the town was dressed into.
     * <p>
     * Every probe is read out of a chunk the level already holds, never by asking it for one, so a column in a chunk
     * nobody has loaded says nothing and the question waits. A column counts as moved when the top of it has moved
     * more than {@link #PROBE_SLACK}, or when the block it stands on is not the block the note wrote down - the second
     * catches a world replaced at the same height, the first a hill taken away. A town trimmed out of the world moves
     * most of them, so most of them is what has to have moved before this says the ground is gone.
     */
    private static int ground(ServerLevel level, BlockPos corner) {
        Path marker = marker(level);
        if (!Files.isRegularFile(marker)) return GONE;
        try {
            String[] fields = Files.readString(marker).trim().split("\\s+");
            if (fields.length < 6 + PROBES.length) return GONE;
            Probe[] now = probe(level, corner);
            int asked = 0;
            int moved = 0;
            for (int at = 0; at < PROBES.length; at++) {
                String[] noted = fields[6 + at].split("@", 2);
                if (noted.length < 2) return GONE;
                int top = Integer.parseInt(noted[0]);
                if (top < 0 || now[at].top() < 0) continue;
                asked++;
                if (Math.abs(now[at].top() - top) > PROBE_SLACK) {
                    moved++;
                } else if (!noted[1].equals("-") && !noted[1].equals(now[at].block())) {
                    moved++;
                }
            }
            if (asked == 0) return UNASKED;
            return moved * 2 > asked ? GONE : PRESENT;
        } catch (IOException | NumberFormatException unreadable) {
            return GONE;
        }
    }

    /** One of the note's places, read as it stands: an unheld chunk says nothing rather than being generated. */
    private static Probe probe(ServerLevel level, BlockPos corner, int at) {
        int x = corner.getX() + PROBES[at][0];
        int z = corner.getZ() + PROBES[at][1];
        if (!level.isLoaded(new BlockPos(x, 0, z))) return new Probe(-1, null);
        int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        if (top <= level.getMinY()) return new Probe(-1, null);
        BlockState under = level.getBlockState(new BlockPos(x, top - 1, z));
        return new Probe(top, BuiltInRegistries.BLOCK.getKey(under.getBlock()).toString());
    }

    /** All of them, in the order the note keeps them. */
    private static Probe[] probe(ServerLevel level, BlockPos corner) {
        Probe[] probes = new Probe[PROBES.length];
        for (int at = 0; at < PROBES.length; at++) probes[at] = probe(level, corner, at);
        return probes;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bucklebury")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source))
                .then(Commands.literal("raise").executes(Bucklebury::raiseNow))
                .then(Commands.literal("drape").executes(Bucklebury::drapeNow)));
    }

    /**
     * Dresses the ground round the town again, and lays nothing of the town.
     * <p>
     * The raise is the only pass that writes the note, so a town whose ring wants dressing again - the ground taken
     * out and made anew, a rule changed, a shelf it left - had no way back to its dressing but to lay the whole cut
     * again, which puts every block of the town back over whatever a hand has done inside since. This is the last
     * stage of the raise on its own: the cut's outline, then the ring at the same budget and with the town's own deck
     * and trees, and the note written when it finishes, so the world knows the ground is dressed and does not raise
     * the town over it. No piece of the town is laid and no country is cleared out of it.
     */
    private static int drapeNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (standing != null) {
            // A raise that has not begun is only waiting for somebody to be near: this is what that somebody wants
            // instead, so it takes the raise's place. One already under way is not interrupted.
            if (standing.began >= 0) {
                source.sendFailure(Component.literal("Bucklebury is already being raised - wait for it to finish."));
                return 0;
            }
            standing = null;
        }
        ServerLevel level = source.getServer().getLevel(TolkienDimensions.ARDA);
        if (level == null) {
            source.sendFailure(Component.literal("There is no Arda on this server."));
            return 0;
        }
        List<CutCommands.Part> parts;
        try {
            parts = CutCommands.parts(level, CUT);
        } catch (IOException unreadable) {
            source.sendFailure(Component.literal("The town's cut could not be read: " + unreadable.getMessage()));
            return 0;
        }
        if (parts.isEmpty()) {
            source.sendFailure(Component.literal("No bucklebury cut to dress - nothing in "
                    + CutCommands.folder(level).resolve(CUT)));
            return 0;
        }
        BlockPos corner = ArdaMap.buckleburyPlan();
        Standing work = new Standing(parts, corner, CutCommands.Box.of(parts, corner), CutCommands.deck(level, CUT));
        work.dressOnly = true;
        work.stage = 3;
        work.found = true;
        standing = work;
        source.sendSuccess(() -> Component.literal("Dressing Bucklebury: the ring round the town from " + parts.size()
                + " pieces of its outline, nothing of the town laid - at most " + BUDGET_MS + " ms a tick."), true);
        return parts.size();
    }

    /** Lays, clears and dresses the town now, wherever the players are, at the same budget a tick. */
    private static int raiseNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Standing work = autospawn(source.getServer());
        if (work == null) {
            source.sendFailure(Component.literal("Bucklebury already stands here, or there is no Bucklebury cut"
                    + " to raise."));
            return 0;
        }
        work.found = true;
        source.sendSuccess(() -> Component.literal("Raising Bucklebury: " + work.parts.size() + " pieces to lay, then"
                + " the country out of it, then the ring - at most " + BUDGET_MS + " ms a tick."), true);
        return work.parts.size();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        standing = null;
        autospawn(event.getServer());
        // A world that already holds the town gets its hall on the next start, and only then: a town still to come
        // clears the country out of its whole footprint and dresses the ring round it, so a hall laid before the pass
        // would be taken away by the ground it stands on.
        ServerLevel arda = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (arda != null && complete(arda)) {
            hall(arda);
            // The mustering hall is the alliance's own, pinned like Hobbiton's and Bree's, so TownHalls raises it: it
            // answers nothing while the note says the town is unfinished, and leaves a hall already standing alone.
            TownHalls.raise(arda, "bucklebury");
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Standing work = standing;
        if (work == null) {
            // A town the note describes but whose ground the start could not read: ask again, once, now that
            // somebody is near enough for the level to hold those chunks without generating one out of this tick.
            if (held == null) return;
            ServerLevel arda = event.getServer().getLevel(TolkienDimensions.ARDA);
            if (arda == null || !near(arda, held)) return;
            int ground = ground(arda, held);
            if (ground == UNASKED) return;
            held = null;
            if (ground == GONE) autospawn(event.getServer());
            return;
        }
        ServerLevel level = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (level == null) return;
        if (work.began < 0) {
            // A hand's ask is taken at once; the world's own waits for the start to settle and for somebody to be near.
            if (!work.found && event.getServer().getTickCount() < GRACE_TICKS) return;
            if (!work.found && !watched(level, work.box)) return;
            work.began = event.getServer().getTickCount();
            if (work.dressOnly) {
                LOGGER.info("Bucklebury: dressing the ground round the town again - {} pieces of its outline, then the"
                        + " ring, at {} ms a tick", work.parts.size(), BUDGET_MS);
            } else {
                LOGGER.info("Bucklebury: raising the town - {} chunks of ground to wait for, then {} pieces and the"
                        + " ring, at {} ms a tick", work.groundChunks(), work.parts.size(), BUDGET_MS);
            }
        }
        long started = System.nanoTime();
        try {
            boolean done = false;
            while (!done) {
                done = work.step(level);
                if (elapsedMs(started) >= BUDGET_MS) break;
            }
            work.ticks++;
            if (done) {
                if (!work.dressOnly) work.release(level);
                standing = null;
                note(level, work.planted);
                // Brandy Hall goes up with the town and after it: this pass has just laid the cut, cleared the
                // footprint and dressed the ring, and a hall stood in that ground first would have been cleared with
                // the country. After the note, so the hall finds the town finished rather than still coming up. The
                // mustering hall follows it for the same reason, and its own wait reads the same note.
                if (!work.dressOnly) {
                    hall(level);
                    TownHalls.raise(level, "bucklebury");
                }
                if (work.dressOnly) {
                    LOGGER.info("Bucklebury: the ground round the town is dressed in {} ticks ({} s)", work.ticks,
                            String.format(Locale.ROOT, "%.1f", work.ticks / 20.0D));
                } else {
                    LOGGER.info("Bucklebury: the town stands at {} in {} ticks ({} s), dressed to its own outline",
                            work.planted.toShortString(), work.ticks,
                            String.format(Locale.ROOT, "%.1f", work.ticks / 20.0D));
                }
            }
        } catch (IOException unreadable) {
            LOGGER.warn("Bucklebury: {} could not be done: {}", work.dressOnly ? "the dressing" : "the raising",
                    unreadable.getMessage());
            if (!work.dressOnly) work.release(level);
            standing = null;
        }
    }

    /** Whether anybody is near enough, measured from the footprint rather than from a corner. */
    private static boolean watched(ServerLevel level, CutCommands.Box box) {
        for (ServerPlayer player : level.players()) {
            BlockPos at = player.blockPosition();
            int dx = Math.max(0, Math.max(box.lowX() - at.getX(), at.getX() - box.highX()));
            int dz = Math.max(0, Math.max(box.lowZ() - at.getZ(), at.getZ() - box.highZ()));
            if (Math.hypot(dx, dz) <= REACH) return true;
        }
        return false;
    }

    /** The same question for a corner rather than a box: what a note's ground is asked behind. */
    private static boolean near(ServerLevel level, BlockPos corner) {
        double reach = (double) (REACH + 300) * (REACH + 300);
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(corner) <= reach) return true;
        }
        return false;
    }

    private static double elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000.0D;
    }

    /** The town in the middle of being raised: its cut, its corner, and how far through the four stages it is. */
    private static final class Standing {
        private final List<CutCommands.Part> parts;
        /** The same pieces in the shape the shared drape reads: the city's, which is how every cut is dressed. */
        private final List<CityCommands.Part> cityParts;
        private final BlockPos planted;
        private final CutCommands.Box box;
        /** The same box in the shared drape's shape, which is the one it lays its ring in. */
        private final CityCommands.Box cityBox;
        /** The town's own floor over its corner, read out of the cut's table: the drape's deck line. */
        private final int deck;
        /** The chunks the town is laid and dressed over, as a rectangle: the footprint and the drape's ring. */
        private final int chunkLowX;
        private final int chunkLowZ;
        private final int chunkWide;
        private final int chunkDeep;
        private CutCommands.Shape shape;
        private Dressing.Job dressing;
        /** The tick the pass began on, -1 until it does, and how many ticks it has taken. */
        private int began = -1;
        private int ticks;
        /** Whether a hand asked for it, rather than a player being near. */
        private boolean found;
        /** Whether this is the drape alone: the ring round a town that already stands. */
        private boolean dressOnly;
        /** 0 asking for ground, 1 laying, 2 clearing, 3 dressing. */
        private int stage;
        private int piece;
        private int column;
        /** How far through the rectangle the ground has been asked for and taken. */
        private int askedUpto;

        private Standing(List<CutCommands.Part> parts, BlockPos planted, CutCommands.Box box, int deck) {
            this.parts = parts;
            this.cityParts = CutCommands.cityParts(parts);
            this.planted = planted;
            this.box = box;
            this.cityBox = CityCommands.Box.of(this.cityParts, planted);
            this.deck = deck;
            int reach = CityCommands.DRAPE_REACH;
            this.chunkLowX = (box.lowX() - reach) >> 4;
            this.chunkLowZ = (box.lowZ() - reach) >> 4;
            this.chunkWide = ((box.highX() + reach) >> 4) - this.chunkLowX + 1;
            this.chunkDeep = ((box.highZ() + reach) >> 4) - this.chunkLowZ + 1;
        }

        /** How many chunks the ground under the town is. */
        private int groundChunks() {
            return this.chunkWide * this.chunkDeep;
        }

        /** Which chunk of the rectangle the nth chunk of it is. */
        private int chunkX(int index) {
            return this.chunkLowX + index / this.chunkDeep;
        }

        private int chunkZ(int index) {
            return this.chunkLowZ + index % this.chunkDeep;
        }

        /** One unit of the stage it is on; true when the whole town is up. */
        private boolean step(ServerLevel level) throws IOException {
            if (this.stage == 0) return awaitGround(level);
            if (this.stage == 1) return layPiece(level);
            if (this.stage == 2) return clearBand(level);
            return dressStep(level);
        }

        /**
         * Asks vanilla for the ground, a chunk a tick, and takes each as it comes.
         * <p>
         * A ticket is what generates a chunk on the game's own threads; the level's own {@code getChunk} is what
         * finishes it, its sections and its light. Asked for as one region that finishing is a single drain of
         * forty-odd seconds - the village measured it - so it is paid a chunk's worth a tick here instead.
         *
         * @return false, always: the wait is the stage
         */
        private boolean awaitGround(ServerLevel level) {
            if (this.askedUpto == 0) {
                LOGGER.info("Bucklebury: asking vanilla for {} chunks of ground under the town", groundChunks());
            }
            if (this.askedUpto < groundChunks()) {
                int at = this.askedUpto++;
                level.getChunkSource().updateChunkForced(new ChunkPos(chunkX(at), chunkZ(at)), true);
                level.getChunk(chunkX(at), chunkZ(at));
                return false;
            }
            this.stage = 1;
            return false;
        }

        private boolean layPiece(ServerLevel level) throws IOException {
            CutCommands.lay(level, CUT, this.parts.get(this.piece), this.planted);
            if (++this.piece < this.parts.size()) return false;
            this.stage = 2;
            return false;
        }

        private boolean clearBand(ServerLevel level) throws IOException {
            if (this.shape == null) this.shape = CutCommands.shape(level, CUT, this.parts);
            int total = this.shape.wide() * this.shape.deep();
            if (total <= 0) {
                this.stage = 3;
                return false;
            }
            int upto = Math.min(total, this.column + COLUMNS_PER_UNIT);
            CutCommands.clearColumns(level, this.planted, this.box, this.shape, this.column, upto);
            this.column = upto;
            if (this.column < total) return false;
            this.stage = 3;
            return false;
        }

        private boolean dressStep(ServerLevel level) throws IOException {
            if (this.dressing == null) {
                // The town's deck is its own floor over its corner, thirteen read out of the cut's table, and its
                // trees are not its shape: both are the village's and the town's answers, in this build's numbers.
                this.dressing = Dressing.begin(level, CutCommands.folder(level).resolve(CUT),
                        piece -> CutCommands.raw(level, CUT, piece), this.cityParts, this.planted, this.cityBox,
                        true, this.deck, CityCommands.DRAPE_REACH);
            }
            return !this.dressing.step(level);
        }

        /** Gives the ground back: the town is up, so nothing needs the chunks held for it. */
        private void release(ServerLevel level) {
            for (int at = 0; at < groundChunks(); at++) {
                level.setChunkForced(chunkX(at), chunkZ(at), false);
            }
        }
    }

    /**
     * Stands Brandy Hall on the town's own ground, unless it is already standing there.
     * <p>
     * Only the build's own blocks are laid, and only its own air with them: the template carries the hall's enclosed air
     * - the rooms, the space under its floors - and none of the air that opens on the box from outside, so the town's
     * streets and the country round the hall stand as they were, and nothing of the build reaches past its own walls.
     * <p>
     * The box used to be swept to air first, to keep the town's ground from standing through the hall's walls. That
     * swept all 595,546 cells of the box, not the build, so it left a box of air round the hall - the owner's own look
     * at it - and it is gone: the template's own air clears what is inside the hall, and what is outside is not the
     * hall's to clear. The air the template carries is every cell of a column that the build draws a floor under and a
     * roof over, which is the whole of its rooms and the space under its floors - 42,420 cells against the 11,609 the
     * first pass found. What it holds no air for, the ground is cut to instead, before the template is laid: {@link
     * #drape}, which takes the box down to the hall's floor course and the ring past it back to the country.
     * <p>
     * The guard is the world's own blocks, not a file: a hall whose sampled blocks are there is left alone, so a player
     * who has been inside Brandy Hall does not have it taken out and laid again on the next start. A box whose chunks
     * are not loaded answers "not standing" - the guard has to be able to say "lay it" - so the chunks are asked for
     * first, as the halls ask for theirs.
     *
     * @return how many blocks the template holds, or nil when it is already standing or is not in the jar
     */
    static int hall(ServerLevel level) {
        Identifier id = Identifier.tryParse(HALL);
        if (id == null) return 0;
        StructureTemplate build = level.getStructureManager().get(id).orElse(null);
        if (build == null) {
            LOGGER.warn("Bucklebury: Brandy Hall is not in the jar - nothing at {}", HALL);
            return 0;
        }
        Vec3i size = build.getSize();
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) return 0;
        BlockPos far = HALL_AT.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1);
        for (int cx = HALL_AT.getX() >> 4; cx <= far.getX() >> 4; cx++) {
            for (int cz = HALL_AT.getZ() >> 4; cz <= far.getZ() >> 4; cz++) level.getChunk(cx, cz);
        }
        if (Sites.standsAt(level, HALL, HALL_AT)) return 0;
        // The ground first, and the hall on it: a cut run after the template was laid would take the hall's own lower
        // walls down with the bank it is clearing.
        int cut = drape(level, size);
        build.placeInWorld(level, HALL_AT, HALL_AT, new StructurePlaceSettings(), level.getRandom(), 2);
        LOGGER.info("Bucklebury: Brandy Hall stands at {} over {} blocks of the build's own, its ground cut to its own"
                + " floor ({} blocks of bank off) and the ring round it eased back to the country",
                HALL_AT.toShortString(), size.getX() * size.getY() * size.getZ(), cut);
        return size.getX() * size.getY() * size.getZ();
    }

    /**
     * Settles the ground the hall is about to stand on: the box cut level with the hall's own floor course, and the
     * ring past it eased back to the country, so the doors on its lower storey open onto ground.
     * <p>
     * The hall's floor is three under the town's own deck line, and the town dressed its ground to its footprint before
     * the hall was laid, so the bank the dressing left on the doors' side stood over them - the owner's own look at it,
     * "just needs a drape action to clear the side of it that is the first floor door". This is that drape, drawn to
     * the hall's own box rather than to the town's outline: the town's ring is measured from the footprint, and the
     * hall stands well inside it, where nothing has been dressed since the cut.
     * <p>
     * The cut is run before the template is laid, never after: a column cut to the hall's floor once the hall stood
     * there would take the hall's own lower walls down with the bank. Only ground is taken away - nothing is filled -
     * and a column whose own block is water is left as it is, the same rule the town's ring and the camp's sweep
     * follow, so the lake two blocks off the west edge keeps its shore.
     *
     * @return how many blocks of ground came off
     */
    private static int drape(ServerLevel level, Vec3i size) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int cut = 0;
        for (int dx = -HALL_DRAPE; dx < size.getX() + HALL_DRAPE; dx++) {
            for (int dz = -HALL_DRAPE; dz < size.getZ() + HALL_DRAPE; dz++) {
                int out = Math.max(0, Math.max(Math.max(-dx, dx - size.getX() + 1),
                        Math.max(-dz, dz - size.getZ() + 1)));
                if (out > HALL_DRAPE) continue;
                int x = HALL_AT.getX() + dx;
                int z = HALL_AT.getZ() + dz;
                if (!level.hasChunkAt(cursor.set(x, HALL_AT.getY(), z))) continue;
                int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                if (surface <= level.getMinY()) continue;
                if (!level.getBlockState(cursor.set(x, surface, z)).getFluidState().isEmpty()) continue;
                // Inside the box the ground is the hall's own floor course; past it the country takes over across the
                // ring, so the join at the wall is the build's height and the join at the ring's edge is the country's.
                int cap = HALL_AT.getY();
                if (out > 0) {
                    double share = out / (double) HALL_DRAPE;
                    double ease = share * share * (3.0D - 2.0D * share);
                    cap = (int) Math.round(HALL_AT.getY() + (surface - HALL_AT.getY()) * ease);
                }
                cut += Drape.column(level, cursor, x, z, surface, cap);
            }
        }
        return cut;
    }

    /** Where this world's note about the town lives: beside its cut, in the save's own folder. */
    private static Path marker(ServerLevel level) {
        return CutCommands.folder(level).resolve(CUT).resolve(MARKER);
    }

    /** Writes the note that says what the town stands on, so the next start knows whether it still holds it. */
    private static void note(ServerLevel level, BlockPos corner) {
        try {
            Path marker = marker(level);
            Files.createDirectories(marker.getParent());
            StringBuilder line = new StringBuilder()
                    .append(level.dimension().identifier()).append(' ')
                    .append(corner.getX()).append(' ').append(corner.getY()).append(' ').append(corner.getZ())
                    .append(' ').append(CutCommands.fingerprint(level, CUT))
                    .append(' ').append(DRESS_VERSION);
            for (Probe probe : probe(level, corner)) {
                line.append(' ').append(probe.top()).append('@')
                        .append(probe.block() == null ? "-" : probe.block());
            }
            Files.writeString(marker, line.toString());
        } catch (IOException unwritable) {
            LOGGER.warn("Bucklebury: the town stands, but that could not be written: {}", unwritable.getMessage());
        }
    }
}
