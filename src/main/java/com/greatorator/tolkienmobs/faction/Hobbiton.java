package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The Shire's village, raised by the world rather than by a hand.
 * <p>
 * The Gundabad hold and the camps stand themselves on the first start of a world, from a template and a plan that are
 * both the code's. The village had neither: it was a cut, laid by hand at a corner a scout had walked to, cleared and
 * then dressed by three commands with a world rebuilt between the second and the third unless the order was right. That
 * is a fine way to find out whether a build fits the country, and no way at all to ship one - a world made after the
 * river was redrawn has no Shire in it until somebody reads a document and types three commands.
 * <p>
 * So the village is stood by the same pass the hold is, on the same clock, and three rules hold it off the server's own
 * time:
 * <ul>
 * <li><strong>The ground comes first, and the pass does not dig it.</strong> A village is thirteen hundred chunks of
 * Arda that have never been generated. Asking the level for one of them from the server thread answers by generating it
 * there, and a build laid block by block into it asked for one more for every neighbour the block's shape updates
 * touched: the first real attempt measured here held a single server tick for sixty seconds and was killed by the
 * watchdog. So the ground is <em>asked for</em> - the chunks are held with tickets and vanilla generates them on its own
 * worker threads - and the pass does not touch a piece until every chunk it will write into is already there.</li>
 * <li><strong>Nothing is done far from anybody.</strong> A village nobody has walked to is a thousand chunks nobody has
 * looked at. The pass begins when somebody comes within {@link #REACH} of the footprint, or when nobody is on the server
 * at all - a world with no players has nobody to stall, and a server that has its ground ready before the first traveller
 * arrives is a better server.</li>
 * <li><strong>What is done in a tick is bounded by time, not by a count.</strong> The job spends at most
 * {@link #BUDGET_MS} a tick and stops between pieces, bands and chunks, so the world keeps its twenty milliseconds
 * whatever the village is doing to it.</li>
 * </ul>
 * <p>
 * The dressing is {@link Dressing}'s own job, stepped by this class: a second drape rule for a second build is how a
 * country ends up with two ideas about where a wall stops. What each stage cost - ticks waited for ground, pieces laid,
 * columns cleared, ring chunks dressed, and the worst tick of each - is said in the log when the village is up.
 */
@EventBusSubscriber(modid = MODID)
public final class Hobbiton {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hobbiton.class);

    /** The cut's name: its folder in the jar's {@code structure/} and, for a re-cut, in the save's {@code cuts/}. */
    private static final String CUT = "hobbiton";
    /**
     * How much wall clock the village may take from a tick, in milliseconds.
     * <p>
     * The world has fifty a tick at twenty ticks a second, and a server that is already behind has less. A quarter of
     * one is work nobody sees: the pass is a one-time cost paid over a few hundred ticks.
     */
    private static final double BUDGET_MS = 5.0D;
    /**
     * How near a player has to be to the footprint for the village to be raised, in blocks.
     * <p>
     * Measured from the box rather than from a corner, like the city's own reach: the village is five hundred blocks
     * across, so a player standing at one wall is not "far" from the other.
     */
    private static final int REACH = 192;
    /** How long after a start the pass waits before it will touch anything: a start is busy enough as it is. */
    private static final int GRACE_TICKS = 200;
    /** How often a line is said while the village is being raised: ten seconds is long enough to read. */
    private static final int REPORT_TICKS = 200;
    /** What this world has made of the village, beside its cut: the dimension and the corner it stood at. */
    private static final String MARKER = "_stands.txt";

    /** The village being raised, or null when there is one standing or nothing to raise. */
    private static Standing standing;
    /** The tick the job began on, and how many ticks it has taken. */
    private static int began = -1;
    private static int ticks;

    private Hobbiton() {
    }

    /**
     * Begins raising the village if this world does not already hold it.
     *
     * @return how many pieces are to be laid, or nil when the village already stands or the cut is not this build's
     */
    public static int autospawn(MinecraftServer server) {
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return 0;
        BlockPos corner = ArdaMap.hobbitonPlan();
        if (stands(level, corner)) return 0;
        List<CutCommands.Part> parts;
        try {
            parts = CutCommands.parts(level, CUT);
        } catch (IOException unreadable) {
            LOGGER.warn("The Shire: the village's cut could not be read, so it is not raised: {}",
                    unreadable.getMessage());
            return 0;
        }
        if (parts.isEmpty()) return 0;
        if (standing == null) {
            standing = new Standing(parts, corner, CutCommands.Box.of(parts, corner), CutCommands.deck(level, CUT));
            LOGGER.info("The Shire: {} pieces of the village are to be laid at {} over {} chunks, when somebody is"
                            + " within {} blocks", parts.size(), corner.toShortString(),
                    standing.groundChunks(), REACH);
        }
        return parts.size();
    }

    /**
     * Begins the raising now, wherever the players are.
     * <p>
     * The world's own pass waits for somebody to be near enough, which is right for a village nobody has walked to and
     * useless for an operator standing in it - or for measuring what the pass costs. What it does not do is skip the
     * budget: a hand asking for the village gets it at the same five milliseconds a tick as the world does.
     *
     * @return how many pieces are to be laid, or nil when there is nothing to raise here
     */
    public static int raise(MinecraftServer server) {
        int pieces = autospawn(server);
        if (standing != null) found = true;
        return pieces;
    }

    /** Whether a hand has asked for the village, which stands the waiting-for-a-player rule aside for this session. */
    private static boolean found;

    /** Whether the village is standing at its own corner in this world. */
    public static boolean stands(ServerLevel level, BlockPos corner) {
        Path marker = marker(level);
        if (!Files.isRegularFile(marker)) return false;
        try {
            String[] fields = Files.readString(marker).trim().split("\\s+");
            return fields.length >= 4
                    && fields[0].equals(level.dimension().identifier().toString())
                    && Integer.parseInt(fields[1]) == corner.getX()
                    && Integer.parseInt(fields[2]) == corner.getY()
                    && Integer.parseInt(fields[3]) == corner.getZ();
        } catch (IOException | NumberFormatException unreadable) {
            return false;
        }
    }

    /**
     * Notes that a cut which is this village stands at a corner, which is what a hand's own drape leaves behind.
     * <p>
     * The village was laid by three commands in the worlds made before it stood itself, and a world like that has no
     * note of its own: without this, the first start after the village learned to raise itself would lay it again over
     * the one already standing. A drape is the last of the three passes, so a corner that has had one has its village.
     */
    static void dressed(ServerLevel level, String cut, BlockPos planted) {
        if (CUT.equals(cut)) note(level, planted);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("hobbiton")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source))
                .then(Commands.literal("raise").executes(Hobbiton::raiseNow)));
    }

    /** Lays, clears and dresses the village now, at a tick's budget a tick. */
    private static int raiseNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        int pieces = raise(source.getServer());
        if (pieces == 0) {
            source.sendFailure(Component.literal("The village already stands here, or there is no Shire cut to raise."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Raising the Shire: " + pieces
                + " pieces to lay, then the country out of it, then the ring - at most " + BUDGET_MS
                + " ms a tick. The log says what each stage cost when it is up."), true);
        return pieces;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        standing = null;
        began = -1;
        ticks = 0;
        found = false;
        autospawn(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Standing work = standing;
        if (work == null) return;
        ServerLevel level = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (level == null) return;
        int now = event.getServer().getTickCount();
        if (began < 0) {
            if (now < GRACE_TICKS) return;
            if (!found && !watched(level, work.box)) return;
            began = now;
            LOGGER.info("The Shire: raising the village - {} chunks of ground to wait for, then {} pieces,"
                            + " {} columns and the ring, at {} ms a tick", work.groundChunks(), work.parts.size(),
                    work.columns(), BUDGET_MS);
        }
        long started = System.nanoTime();
        try {
            boolean done = false;
            while (!done) {
                done = work.step(level);
                if (elapsedMs(started) >= BUDGET_MS) break;
            }
            ticks++;
            work.tick(elapsedMs(started));
            if (!done) {
                if (ticks % REPORT_TICKS == 0) LOGGER.info("The Shire: {}", work.progress(ticks));
                return;
            }
        } catch (IOException unreadable) {
            LOGGER.warn("The Shire: the village could not be raised: {}", unreadable.getMessage());
            work.release(level);
            standing = null;
            return;
        }
        work.release(level);
        work.report(ticks);
        standing = null;
        note(level, work.planted);
        LOGGER.info("The Shire: the village stands at {}, dressed to its own outline", work.planted.toShortString());
    }

    /**
     * Whether the world should be raising the village now: somebody is near enough to see it, or nobody is on at all.
     * <p>
     * The second half is the other case rather than a loophole. A village nobody has walked to is ground nobody has
     * looked at - but a world where nobody is playing has nobody to stall, and a server that has prepared its Shire
     * before the first traveller arrives beats one that builds it in their face. Single player is the first case from
     * the first tick: the player stands at spawn, the village is a walk away, and nothing is raised until they come.
     */
    /**
     * Whether anybody is near enough to see the village come up.
     * <p>
     * A world with nobody in it raises nothing. That is not only an economy: measured on a server with no players, the
     * first block written into the 1326 freshly generated chunks under the village waited forty-three seconds for the
     * game to finish them, all in one tick - and it waited there because no player was standing in the region to keep
     * its chunks ticking while they finished. The reach rule keeps that cost where the chunks are being ticked anyway,
     * and `/hobbiton raise` is still there for an admin who wants the village up in the dark.
     */
    private static boolean watched(ServerLevel level, CutCommands.Box box) {
        if (level.players().isEmpty()) return false;
        for (ServerPlayer player : level.players()) {
            BlockPos at = player.blockPosition();
            int dx = Math.max(0, Math.max(box.lowX() - at.getX(), at.getX() - box.highX()));
            int dz = Math.max(0, Math.max(box.lowZ() - at.getZ(), at.getZ() - box.highZ()));
            if (Math.hypot(dx, dz) <= REACH) return true;
        }
        return false;
    }

    private static double elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000.0D;
    }

    /**
     * The village in the middle of being raised: its cut, its corner, how far through the four stages it is, and the
     * clock each of them has cost.
     * <p>
     * The stages are the three commands with the ground taken first: wait for the chunks, lay the pieces, clear the
     * country out of the footprint, dress the ring. Every stage is counted separately because the numbers say different
     * things - the wait is vanilla's generation on its own threads, the lay is fifteen million block writes, the clear
     * is twenty-three million reads, the drape is a ring of ground - and only the last three are this pass's own time.
     */
    private static final class Standing {
        private final List<CutCommands.Part> parts;
        /** The same pieces in the shape the shared drape reads: the city's, which is the shape every cut is dressed in. */
        private final List<CityCommands.Part> cityParts;
        private final BlockPos planted;
        private final CutCommands.Box box;
        /** The same box in the shared drape's shape, which is the one it lays its ring in. */
        private final CityCommands.Box cityBox;
        private final int deck;
        /** The chunks the village is laid and dressed over, as a rectangle: the footprint and the drape's ring. */
        private final int chunkLowX;
        private final int chunkLowZ;
        private final int chunkWide;
        private final int chunkDeep;
        private CutCommands.Shape shape;
        private Dressing.Job dressing;
        /** 0 asking for ground, 1 laying, 2 clearing, 3 dressing, 4 up. */
        private int stage;
        private int piece;
        private int column;
        private boolean asked;
        private int askedUpto;
        private int readyUpto;
        private int ground;
        private final java.util.Set<String> waitedFor = new java.util.LinkedHashSet<>();

        private long groundNanos;
        private int groundTicks;
        private long layNanos;
        private long clearNanos;
        private long dressNanos;
        private long worstTickNanos;
        private long worstGroundNanos;
        private long worstLayNanos;
        private int layUnits;
        private int clearUnits;
        private int dressUnits;
        private long blocksCleared;

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

        /** How many chunks the ground under the village is. */
        private int groundChunks() {
            return this.chunkWide * this.chunkDeep;
        }

        /** How many columns the village's footprint has. */
        private int columns() {
            return (this.box.highX() - this.box.lowX() + 1) * (this.box.highZ() - this.box.lowZ() + 1);
        }

        /**
         * One unit of the stage it is on: a count of the ground, a piece of the cut, a band of columns, or a step of
         * the drape.
         *
         * @return whether the whole village is up
         */
        private boolean step(ServerLevel level) throws IOException {
            if (this.stage == 0) return awaitGround(level);
            if (this.stage == 1) return layPiece(level);
            if (this.stage == 2) return clearBand(level);
            if (this.stage == 3) return dressStep(level);
            return true;
        }

        /**
         * Asks vanilla for the ground, and waits until it is there.
         * <p>
         * The chunks are taken with tickets so the game generates them on its own workers and keeps them: asking the
         * level for a block in an ungenerated chunk from the server thread generates it <em>there</em>, and a build laid
         * a block at a time asks for a neighbour for every shape update it writes. That is the difference between a
         * village that costs a few hundred ticks of waiting and one that holds a tick for a minute.
         *
         * @return false, always: the wait is the stage
         */
        private boolean awaitGround(ServerLevel level) {
            long started = System.nanoTime();
            if (!this.asked) {
                this.asked = true;
                LOGGER.info("The Shire: asking vanilla for {} chunks of ground under the village, {} a tick",
                        groundChunks(), ASK_PER_TICK);
            }
            // Tickets, and only tickets: updateChunkForced is the bookkeeping half of setChunkForced. The public setter
            // calls getChunk straight after it, and getChunk - and getChunkFuture, which parks the main thread on the
            // worker pool until the chunk is whole - generate on the server thread. This pass measured both, and a
            // chunk of Arda costs about a thirtieth of a second out of the tick it is asked for: 1326 asked in one go
            // held a tick for 49 seconds, and the futures held it for the minute the watchdog allows. So they are
            // asked for a handful at a time and the pass waits, which turns one freeze into a stretch of ticks a
            // little longer than they should be, on a server nobody has walked into yet.
            int upto = Math.min(groundChunks(), this.askedUpto + ASK_PER_TICK);
            int index = 0;
            for (int cx = this.chunkLowX; cx < this.chunkLowX + this.chunkWide; cx++) {
                for (int cz = this.chunkLowZ; cz < this.chunkLowZ + this.chunkDeep; cz++) {
                    if (index >= this.askedUpto && index < upto) {
                        level.getChunkSource().updateChunkForced(new ChunkPos(cx, cz), true);
                    }
                    index++;
                }
            }
            this.askedUpto = upto;
            int here = 0;
            for (int cx = this.chunkLowX; cx < this.chunkLowX + this.chunkWide; cx++) {
                for (int cz = this.chunkLowZ; cz < this.chunkLowZ + this.chunkDeep; cz++) {
                    if (level.hasChunk(cx, cz)) here++;
                }
            }
            this.ground = here;
            long took = System.nanoTime() - started;
            this.groundNanos += took;
            this.worstGroundNanos = Math.max(this.worstGroundNanos, took);
            this.groundTicks++;
            if (this.askedUpto < groundChunks() || here < groundChunks()) return false;
            if (this.readyUpto < groundChunks()) {
                // The ground is held, but a chunk that is only generated is not yet ready to be written into: the first
                // write into a virgin stretch of Arda asked the level for a chunk that was there and waited, and that
                // wait is the whole region's light and sections finishing - measured at forty-two seconds for the first
                // piece of the village. So the pieces' own ask is made here, a chunk a tick, which is the same cost
                // paid out in ticks that are a little long rather than in one that stops the server.
                int at = 0;
                for (int cx = this.chunkLowX; cx < this.chunkLowX + this.chunkWide; cx++) {
                    for (int cz = this.chunkLowZ; cz < this.chunkLowZ + this.chunkDeep; cz++) {
                        if (at++ == this.readyUpto) level.getChunk(cx, cz);
                    }
                }
                this.readyUpto++;
                long ready = System.nanoTime() - started;
                this.worstGroundNanos = Math.max(this.worstGroundNanos, ready);
                return false;
            }
            this.stage = 1;
            return false;
        }

        private boolean layPiece(ServerLevel level) throws IOException {
            CutCommands.Part part = this.parts.get(this.piece);
            // The ground is under the village by now, but a piece is only laid over ground the game is holding: a block
            // written into a chunk that has gone asks for it back, and generation out of a tick is what held this pass's
            // first tick for forty-four seconds. A piece whose ground is not there is asked for and left for the next.
            int cx0 = Math.floorDiv(this.planted.getX() + part.dx(), 16);
            int cz0 = Math.floorDiv(this.planted.getZ() + part.dz(), 16);
            int cx1 = Math.floorDiv(this.planted.getX() + part.dx() + part.wide() - 1, 16);
            int cz1 = Math.floorDiv(this.planted.getZ() + part.dz() + part.deep() - 1, 16);
            int missing = 0;
            for (int cx = cx0; cx <= cx1; cx++) {
                for (int cz = cz0; cz <= cz1; cz++) {
                    if (level.hasChunk(cx, cz)) continue;
                    level.getChunkSource().updateChunkForced(new ChunkPos(cx, cz), true);
                    missing++;
                }
            }
            if (missing > 0) {
                if (this.waitedFor.add(part.name())) {
                    LOGGER.info("The Shire: piece {} waits for {} of its chunks", part.name(), missing);
                }
                return false;
            }
            long started = System.nanoTime();
            CutCommands.lay(level, CUT, part, this.planted);
            long took = System.nanoTime() - started;
            this.layNanos += took;
            this.worstLayNanos = Math.max(this.worstLayNanos, took);
            this.layUnits++;
            if (took > 500_000_000L) {
                LOGGER.info("The Shire: piece {} took {} ms", part.name(), took / 1_000_000L);
            }
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
            long started = System.nanoTime();
            CutCommands.Cleared done = CutCommands.clearColumns(level, this.planted, this.box, this.shape,
                    this.column, upto);
            this.clearNanos += System.nanoTime() - started;
            this.blocksCleared += done.blocks();
            this.clearUnits++;
            this.column = upto;
            if (this.column < total) return false;
            this.stage = 3;
            return false;
        }

        private boolean dressStep(ServerLevel level) throws IOException {
            if (this.dressing == null) {
                this.dressing = Dressing.begin(level, CutCommands.folder(level).resolve(CUT),
                        piece -> CutCommands.raw(level, CUT, piece), this.cityParts, this.planted, this.cityBox,
                        true, this.deck);
            }
            long started = System.nanoTime();
            boolean more = this.dressing.step(level);
            this.dressNanos += System.nanoTime() - started;
            this.dressUnits++;
            if (more) return false;
            this.stage = 4;
            return true;
        }

        /** Gives the ground back: the village is up, so nothing needs the chunks held for it. */
        private void release(ServerLevel level) {
            if (!this.asked) return;
            for (int cx = this.chunkLowX; cx < this.chunkLowX + this.chunkWide; cx++) {
                for (int cz = this.chunkLowZ; cz < this.chunkLowZ + this.chunkDeep; cz++) {
                    level.setChunkForced(cx, cz, false);
                }
            }
            this.asked = false;
            this.askedUpto = 0;
            this.readyUpto = 0;
        }

        private void tick(double takenMs) {
            this.worstTickNanos = Math.max(this.worstTickNanos, (long) (takenMs * 1_000_000.0D));
        }

        /** How far through the four stages the village is, in one line. */
        private String progress(int ticks) {
            return ticks + " ticks in: ground " + this.ground + "/" + groundChunks() + " chunks, "
                    + this.piece + "/" + this.parts.size() + " pieces laid, " + this.column + " columns cleared, "
                    + this.dressUnits + " dressing steps - " + round(this.worstTickNanos / 1_000_000.0D)
                    + " ms at the worst tick";
        }

        /** Says where the time went: the whole village, then each stage of it. */
        private void report(int ticks) {
            double ground = this.groundNanos / 1_000_000.0D;
            double lay = this.layNanos / 1_000_000.0D;
            double clear = this.clearNanos / 1_000_000.0D;
            double dress = this.dressNanos / 1_000_000.0D;
            LOGGER.info("The Shire: up in {} ticks (~{} s). Ground {} chunks waited for over {} ticks, {} ms"
                            + " (worst count {} ms). Lay {} pieces, {} ms ({} a piece, worst {})."
                            + " Clear {} bands {}/{} columns, {} blocks, {} ms. Dress {} steps, {} ms.",
                    ticks, round(ticks / 20.0D), groundChunks(), this.groundTicks, round(ground),
                    round(this.worstGroundNanos / 1_000_000.0D),
                    this.layUnits, round(lay), round(lay / Math.max(1, this.layUnits)),
                    round(this.worstLayNanos / 1_000_000.0D),
                    this.clearUnits, this.column, columns(), this.blocksCleared, round(clear),
                    this.dressUnits, round(dress));
            LOGGER.info("The Shire: the pass's own time - lay {} ms, clear {} ms, dress {} ms - against {} ms at the"
                            + " worst tick of all of them, of a {} ms budget.",
                    round(lay), round(clear), round(dress), round(this.worstTickNanos / 1_000_000.0D), BUDGET_MS);
        }

        private static double round(double value) {
            return Math.round(value * 10.0D) / 10.0D;
        }
    }

    /** How many of the village's columns one unit of the clear works: a band across the footprint. */
    private static final int COLUMNS_PER_UNIT = 200;

    /** How many chunks of ground are asked for a tick while the village waits for it. */
    private static final int ASK_PER_TICK = 2;


    /** Where this world's note about the village lives: beside its cut, in the save's own folder. */
    private static Path marker(ServerLevel level) {
        return CutCommands.folder(level).resolve(CUT).resolve(MARKER);
    }

    /** Writes the note that says the village stands, so the next start leaves it alone. */
    private static void note(ServerLevel level, BlockPos corner) {
        try {
            Path marker = marker(level);
            Files.createDirectories(marker.getParent());
            Files.writeString(marker, level.dimension().identifier() + " " + corner.getX() + " " + corner.getY()
                    + " " + corner.getZ());
        } catch (IOException unwritable) {
            LOGGER.warn("The Shire: the village stands, but that could not be written: {}",
                    unwritable.getMessage());
        }
    }
}
