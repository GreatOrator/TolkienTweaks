package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
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

import javax.annotation.Nullable;

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
 * there, and generation out of a tick is what held a first attempt at this for the minute the watchdog allows. So the
 * chunks are held with tickets a chunk at a time, vanilla generates them on its own threads, and each is taken as it
 * comes so the game's finishing of its sections and light is paid a chunk's worth a tick and never a region's.</li>
 * <li><strong>Nothing is done far from anybody.</strong> A village nobody has walked to is a thousand chunks nobody has
 * looked at. The pass begins when somebody comes within {@link #REACH} of the footprint - not in an empty world, where
 * there is no player keeping the chunks ticking while they finish. `/hobbiton raise` is the way to ask for it anyway.</li>
 * <li><strong>What is done in a tick is bounded by time, not by a count.</strong> The job spends at most
 * {@link #BUDGET_MS} a tick and stops between pieces, bands and chunks, so the world keeps its twenty milliseconds
 * whatever the village is doing to it.</li>
 * </ul>
 * <p>
 * The dressing is {@link Dressing}'s own job, stepped by this class: a second drape rule for a second build is how a
 * country ends up with two ideas about where a wall stops.
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
    private static final int GRACE_TICKS = 200;    /** How many of the village's columns one unit of the clear works: a band across the footprint. */
    private static final int COLUMNS_PER_UNIT = 200;
    /** What this world has made of the village, beside its cut: the dimension and the corner it stood at. */
    private static final String MARKER = "_stands.txt";

    /** The village being raised, or null when there is one standing or nothing to raise. */
    private static Standing standing;

    private Hobbiton() {
    }

    /**
     * Begins raising the village if this world does not already hold it.
     *
     * @return the village being raised, or null when it already stands or the cut is not this build's
     */
    @Nullable
    private static Standing autospawn(MinecraftServer server) {
        if (standing != null) return standing;
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return null;
        BlockPos corner = ArdaMap.hobbitonPlan();
        if (stands(level, corner)) return null;
        List<CutCommands.Part> parts;
        try {
            parts = CutCommands.parts(level, CUT);
        } catch (IOException unreadable) {
            LOGGER.warn("The Shire: the village's cut could not be read, so it is not raised: {}",
                    unreadable.getMessage());
            return null;
        }
        if (parts.isEmpty()) return null;
        standing = new Standing(parts, corner, CutCommands.Box.of(parts, corner), CutCommands.deck(level, CUT));
        LOGGER.info("The Shire: {} pieces of the village are to be laid at {} over {} chunks, when somebody is"
                        + " within {} blocks", parts.size(), corner.toShortString(), standing.groundChunks(), REACH);
        return standing;
    }

    /** Whether the village is standing at its own corner in this world. */
    private static boolean stands(ServerLevel level, BlockPos corner) {
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

    /** Lays, clears and dresses the village now, wherever the players are, at the same budget a tick. */
    private static int raiseNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Standing work = autospawn(source.getServer());
        if (work == null) {
            source.sendFailure(Component.literal("The village already stands here, or there is no Shire cut to raise."));
            return 0;
        }
        work.found = true;
        source.sendSuccess(() -> Component.literal("Raising the Shire: " + work.parts.size() + " pieces to lay, then"
                + " the country out of it, then the ring - at most " + BUDGET_MS + " ms a tick."), true);
        return work.parts.size();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        standing = null;
        autospawn(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Standing work = standing;
        if (work == null) return;
        ServerLevel level = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (level == null) return;
        if (work.began < 0) {
            // A hand's ask is taken at once; the world's own waits for the start to settle and for somebody to be near.
            if (!work.found && event.getServer().getTickCount() < GRACE_TICKS) return;
            if (!work.found && !watched(level, work.box)) return;
            work.began = event.getServer().getTickCount();
            LOGGER.info("The Shire: raising the village - {} chunks of ground to wait for, then {} pieces and the"
                            + " ring, at {} ms a tick", work.groundChunks(), work.parts.size(), BUDGET_MS);
        }
        long started = System.nanoTime();
        try {
            boolean done = false;
            while (!done) {
                done = work.step(level);
                if (elapsedMs(started) >= BUDGET_MS) break;
            }
            if (done) {
                work.release(level);
                standing = null;
                note(level, work.planted);
                LOGGER.info("The Shire: the village stands at {}, dressed to its own outline",
                        work.planted.toShortString());
            }
        } catch (IOException unreadable) {
            LOGGER.warn("The Shire: the village could not be raised: {}", unreadable.getMessage());
            work.release(level);
            standing = null;
        }
    }

    /**
     * Whether anybody is near enough to see the village come up.
     * <p>
     * A world with nobody in it raises nothing. That is not only an economy: measured on a server with no players, the
     * first block written into the 1326 freshly generated chunks under the village waited forty-three seconds for the
     * game to finish them, all in one tick - and it waited there because no player was standing in the region to keep
     * its chunks ticking while they finished. The reach rule keeps that cost where the chunks are being ticked anyway.
     */
    private static boolean watched(ServerLevel level, CutCommands.Box box) {
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
     * The village in the middle of being raised: its cut, its corner, and how far through the four stages it is.
     * <p>
     * The stages are the three commands with the ground taken first: wait for the chunks, lay the pieces, clear the
     * country out of the footprint, dress the ring.
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
        /** The tick the pass began on, -1 until it does, and whether a hand asked for it rather than a player being near. */
        private int began = -1;
        private boolean found;
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

        /** How many chunks the ground under the village is. */
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
            return dressStep(level);
        }

        /**
         * Asks vanilla for the ground, a chunk a tick, and takes each as it comes.
         * <p>
         * A ticket is what generates a chunk, on the game's own threads; the level's own `getChunk` is what finishes it,
         * its sections and its light. Measured on a fresh world, that finishing costs about a thirtieth of a second a
         * chunk - and asked for as one region it is a single drain: the first write anywhere in the 1326 chunks of the
         * village stopped a tick for forty-three seconds. So the ground is asked for and taken a chunk at a time, which
         * is the same cost in ticks that run long rather than in one that holds the server.
         *
         * @return false, always: the wait is the stage
         */
        private boolean awaitGround(ServerLevel level) {
            if (this.askedUpto == 0) {
                LOGGER.info("The Shire: asking vanilla for {} chunks of ground under the village", groundChunks());
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
                this.dressing = Dressing.begin(level, CutCommands.folder(level).resolve(CUT),
                        piece -> CutCommands.raw(level, CUT, piece), this.cityParts, this.planted, this.cityBox,
                        true, this.deck);
            }
            return !this.dressing.step(level);
        }

        /** Gives the ground back: the village is up, so nothing needs the chunks held for it. */
        private void release(ServerLevel level) {
            for (int at = 0; at < groundChunks(); at++) {
                level.setChunkForced(chunkX(at), chunkZ(at), false);
            }
        }
    }

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
            LOGGER.warn("The Shire: the village stands, but that could not be written: {}", unwritable.getMessage());
        }
    }
}
