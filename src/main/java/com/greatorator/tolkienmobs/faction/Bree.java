package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
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
 * Bree, raised by the world rather than by a hand.
 * <p>
 * The town is a cut - 496 by 672 columns of Bree-land, the town on its plain and Bree-hill behind it - and the
 * hillside it carries is cut off square where the markers stop. {@code ArdaMap.breeHill} lays the rock that carries
 * that edge out into the country, so the hill arrives as part of the world rather than as a wall of its own; this is
 * the pass that then puts the town on it.
 * <p>
 * It is {@link Hobbiton}'s pass with the village taken out of it, because the two builds want the same four things in
 * the same order, and only the numbers are different:
 * <ul>
 * <li>the ground, asked for a chunk a tick and taken as it comes, so the first start of a world does not generate
 * thirteen hundred chunks in one tick;</li>
 * <li>the cut, laid piece by piece at {@code ArdaMap.breePlan()};</li>
 * <li>the country cleared out of the footprint, a band of columns a tick, so the drawn hillside inside the town's own
 * box does not stand through its streets;</li>
 * <li>the ring dressed to the build's outline by {@link Dressing} - the same drape the city and the village use, with
 * the town's own floor as its deck and its trees left out of its shape.</li>
 * </ul>
 * <p>
 * What is not here is the village's own folk and its hall: Bree's people and Bree's hall are a later pass, and the
 * town is worth standing before either is written into it.
 */
@EventBusSubscriber(modid = MODID)
public final class Bree {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bree.class);

    /** The cut's name: its folder in the save's {@code cuts/} and, once it is shipped, in the jar's structure folder. */
    private static final String CUT = "bree";
    /** How much wall clock the pass may take from a tick, in milliseconds. */
    private static final double BUDGET_MS = 5.0D;
    /** How near a player has to be to the footprint for the town to be raised: it is five hundred by seven hundred. */
    private static final int REACH = 192;
    /** How long after a start the pass waits before it will touch anything. */
    private static final int GRACE_TICKS = 200;
    /** How many of the town's columns one unit of the clear works: a band across the footprint. */
    private static final int COLUMNS_PER_UNIT = 200;
    /** What this world has made of the town, beside its cut: dimension, corner, cut, drape rules, ground. */
    private static final String MARKER = "_stands.txt";
    /**
     * The drape's own rules, as a number a hand bumps when it changes them: a fix to the ring has to reach a world
     * that already holds the town, and the cut it is dressed from is unchanged, so the version is what says so.
     */
    private static final int DRESS_VERSION = 1;
    /** How far a probe column's top may move and still be the town: growth, mostly, not a town taken away. */
    private static final int PROBE_SLACK = 6;
    /**
     * Where the note looks: the four corners of the footprint, its middle, and two more across it, inset from the
     * edge - the same seven the village keeps, in this footprint's own numbers (496 by 672).
     */
    private static final int[][] PROBES = {{8, 8}, {487, 8}, {8, 663}, {487, 663}, {247, 335}, {123, 167}, {371, 503}};

    /** What the note's probes answer: the ground is there, it is gone, or the level holds no chunk to ask. */
    private static final int PRESENT = 1, GONE = 0, UNASKED = -1;

    /** One probe as the note keeps it: the top of a column and the block under it, or -1 and nothing when unread. */
    private record Probe(int top, @Nullable String block) {
    }

    /** The town being raised, or null when there is one standing or nothing to raise. */
    private static Standing standing;

    private Bree() {
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
        BlockPos corner = ArdaMap.breePlan();
        if (stands(level, corner) && ground(level, corner) != GONE) return null;
        List<CutCommands.Part> parts;
        try {
            parts = CutCommands.parts(level, CUT);
        } catch (IOException unreadable) {
            LOGGER.warn("Bree: the town's cut could not be read, so it is not raised: {}", unreadable.getMessage());
            return null;
        }
        if (parts.isEmpty()) {
            // A pass that has nothing to lay and says nothing is the worst answer a caller can get: name the
            // folder that was read, so a world waiting on a cut is not a world that has to be read to find out.
            LOGGER.warn("Bree: no bree cut to raise - nothing in {}, and nothing in the jar beside it",
                    CutCommands.folder(level).resolve(CUT));
            return null;
        }
        standing = new Standing(parts, corner, CutCommands.Box.of(parts, corner));
        LOGGER.info("Bree: {} pieces of the town are to be laid at {} over {} chunks, when somebody is within {}"
                        + " blocks", parts.size(), corner.toShortString(), standing.groundChunks(), REACH);
        return standing;
    }

    /**
     * Whether this world's town has been raised to the end of its own pass.
     * <p>
     * Read from the note alone, so it answers at a start, where none of the town's chunks are held.
     */
    public static boolean complete(ServerLevel level) {
        return stands(level, ArdaMap.breePlan());
    }

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
     * Every probe is read out of a chunk the level already holds, never by asking it for one. A column counts as moved
     * when its top has moved more than {@link #PROBE_SLACK}, or when the block it stands on is not the one the note
     * wrote down; most of them moving is a town taken out of the world, one tree is not.
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
        event.getDispatcher().register(Commands.literal("bree")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source))
                .then(Commands.literal("raise").executes(Bree::raiseNow))
                .then(Commands.literal("drape").executes(Bree::drapeNow)));
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
                source.sendFailure(Component.literal("Bree is already being raised - wait for it to finish."));
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
            source.sendFailure(Component.literal("No bree cut to dress - nothing in "
                    + CutCommands.folder(level).resolve(CUT)));
            return 0;
        }
        BlockPos corner = ArdaMap.breePlan();
        Standing work = new Standing(parts, corner, CutCommands.Box.of(parts, corner));
        work.dressOnly = true;
        work.stage = 3;
        work.found = true;
        standing = work;
        source.sendSuccess(() -> Component.literal("Dressing Bree: the ring round the town from " + parts.size()
                + " pieces of its outline, nothing of the town laid - at most " + BUDGET_MS + " ms a tick."), true);
        return parts.size();
    }

    /** Lays, clears and dresses the town now, wherever the players are, at the same budget a tick. */
    private static int raiseNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Standing work = autospawn(source.getServer());
        if (work == null) {
            source.sendFailure(Component.literal("Bree already stands here, or there is no Bree cut to raise."));
            return 0;
        }
        work.found = true;
        source.sendSuccess(() -> Component.literal("Raising Bree: " + work.parts.size() + " pieces to lay, then the"
                + " country out of it, then the ring - at most " + BUDGET_MS + " ms a tick."), true);
        return work.parts.size();
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        standing = null;
        autospawn(event.getServer());
        // A world that already holds the town gets its hall on the next start. This is asked of every world and not
        // only the ones raising a town: raise refuses while the note says the town is unfinished, and a town that is
        // finished has already been through the pass that would have laid over the pin.
        ServerLevel arda = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (arda != null) TownHalls.raise(arda, "bree");
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
            if (work.dressOnly) {
                LOGGER.info("Bree: dressing the ground round the town again - {} pieces of its outline, then the"
                        + " ring, at {} ms a tick", work.parts.size(), BUDGET_MS);
            } else {
                LOGGER.info("Bree: raising the town - {} chunks of ground to wait for, then {} pieces and the ring, at"
                        + " {} ms a tick", work.groundChunks(), work.parts.size(), BUDGET_MS);
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
                // The town's hall goes up with the town and after it: this pass has just laid the cut, cleared the
                // footprint and dressed the ring, and a hall stood in that ground first would have been cleared with
                // the country. After the note, so the raise finds the town finished rather than still coming up.
                if (!work.dressOnly) TownHalls.raise(level, "bree");
                if (work.dressOnly) {
                    LOGGER.info("Bree: the ground round the town is dressed in {} ticks ({} s)", work.ticks,
                            String.format(Locale.ROOT, "%.1f", work.ticks / 20.0D));
                } else {
                    LOGGER.info("Bree: the town stands at {} in {} ticks ({} s), dressed to its own outline",
                            work.planted.toShortString(), work.ticks,
                            String.format(Locale.ROOT, "%.1f", work.ticks / 20.0D));
                }
            }
        } catch (IOException unreadable) {
            LOGGER.warn("Bree: {} could not be done: {}", work.dressOnly ? "the dressing" : "the raising",
                    unreadable.getMessage());
            if (!work.dressOnly) work.release(level);
            standing = null;
        }
    }

    /** Whether anybody is near enough to see the town come up, measured from the footprint rather than a corner. */
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

    /** The town in the middle of being raised: its cut, its corner, and how far through the four stages it is. */
    private static final class Standing {
        private final List<CutCommands.Part> parts;
        /** The same pieces in the shape the shared drape reads: the city's, which is how every cut is dressed. */
        private final List<CityCommands.Part> cityParts;
        private final BlockPos planted;
        private final CutCommands.Box box;
        /** The same box in the shared drape's shape, which is the one it lays its ring in. */
        private final CityCommands.Box cityBox;
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
        /**
         * Whether this is the drape alone: the ring dressed round a town that already stands, with no ground asked
         * for, no piece laid and no country cleared out of it.
         */
        private boolean dressOnly;
        /** 0 asking for ground, 1 laying, 2 clearing, 3 dressing. */
        private int stage;
        private int piece;
        private int column;
        /** How far through the rectangle the ground has been asked for and taken. */
        private int askedUpto;

        private Standing(List<CutCommands.Part> parts, BlockPos planted, CutCommands.Box box) {
            this.parts = parts;
            this.cityParts = CutCommands.cityParts(parts);
            this.planted = planted;
            this.box = box;
            this.cityBox = CityCommands.Box.of(this.cityParts, planted);
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
                LOGGER.info("Bree: asking vanilla for {} chunks of ground under the town", groundChunks());
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
                // The town's deck is its own grass line, five over the marker course, and its trees are not its shape:
                // both are the village's answers, and both are the town's numbers rather than the city's.
                this.dressing = Dressing.begin(level, CutCommands.folder(level).resolve(CUT),
                        piece -> CutCommands.raw(level, CUT, piece), this.cityParts, this.planted, this.cityBox,
                        true, ArdaMap.BREE_DECK_LIFT, CityCommands.DRAPE_REACH);
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
            LOGGER.warn("Bree: the town stands, but that could not be written: {}", unwritable.getMessage());
        }
    }
}
