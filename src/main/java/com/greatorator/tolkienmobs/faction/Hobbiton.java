package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.entity.npc.HobbitEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
 * <li><strong>The village is up before anybody can walk into it.</strong> The portal out of the overworld opens on the
 * village's own crossing, inside its footprint, so a village raised by a player's arrival is one whose clearing pass
 * takes the doorway away under the traveller. So the pass begins at the start of a world, on the server's own quiet
 * time, by {@code raiseShireOnStart} in the mod's server settings - on by default. A world whose setting is off keeps
 * the older rule, and begins when somebody comes within {@link #REACH} of the footprint, which is the shape a hand
 * watching it come up wants; `/hobbiton raise` asks for it at any time in either world. A dedicated server that pauses
 * itself when nobody has been on it does not tick the world at all, so the pass lifts that pause for its own length and
 * gives it back when the village stands (see {@link #holdAwake}).</li>
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
    private static final int GRACE_TICKS = 200;
    /** How many of the village's columns one unit of the clear works: a band across the footprint. */
    private static final int COLUMNS_PER_UNIT = 200;
    /**
     * What this world has made of the village, beside its cut: the dimension, the corner it stood at, what the cut was,
     * and the ground's own top at five places across it.
     */
    private static final String MARKER = "_stands.txt";
    /**
     * The drape's own rules, as a number a hand bumps when it changes them.
     * <p>
     * The fingerprint above answers for the cut's table; this answers for the code that settles the ring round it. A fix to the
     * drape - a mouth filled, a deck read from the build - has to reach a world that already holds the village, and the
     * cut it is dressed from is unchanged, so the version is what says so.
     */
    private static final int DRESS_VERSION = 13;
    /**
     * What the note says instead of {@link #DRESS_VERSION} when the ring was left alone.
     * <p>
     * The note answers "is the village this build would make of this ground already here", and a village raised with
     * {@code dressShireRing} off has no ring to answer for. Kept apart from the version rather than sharing it, so a
     * world raised bare and a world dressed are not the same note: turning the ring back on dresses a village that
     * was raised without it instead of walking past one that was never dressed, and turning it off re-clears a
     * dressed footprint rather than leaving the old ring in place under the new rules.
     */
    private static final int UNDRESSED = 0;
    /** How far a probe column's top may move and still be the village: growth, mostly, not a village taken away. */
    private static final int PROBE_SLACK = 6;
    /**
     * The village's own folk, by name, which is also the name of the face each of them wears.
     * <p>
     * The names are the old pack's: its clone database holds twenty-two hobbits, and thirteen of the fifteen skins under
     * {@code customskins/hobbits} are modern 64x64 art that sits on this model - the two legacy 64x32 ones are left out
     * rather than drawn wrong. A name is where a face is found, by the rule the tall folk are already drawn by:
     * {@code HobbitRender} asks for {@code middle-earth/<slug>.png}, and those thirteen files sit in the mod beside
     * Frodo's and Bilbo's. {@code Hal Cotton} is the one name of ours - the pack's seventh male body has a skin and no
     * name on it - and a name with no drawn face falls back to the clothes of its folk, which is the ordinary state of
     * a pack whose art arrives by degrees.
     */
    private static final String[] FOLK = {
            "Amanda Brockhouse", "Melissa Headstrong", "Belba Butcher", "Gomatrudis Proudmead",
            "Regnetrudis Fallohide", "Ingunde Silentfoot", "Gararic Rumblebelly", "Marcus Hairyfoot",
            "Isembard Underlake", "Godomar Barrowes", "Nick Greenhill", "Leufroy Swiftfoot", "Hal Cotton"
    };
    /**
     * How many of the Shire's own folk a village keeps, and how many columns it looks along to find them a place.
     * <p>
     * One for each face the pack has: a village of the Shire reads as inhabited from the gate at this many, and every
     * one of them is somebody by name. They are stood by the pass rather than left to the spawner, because the chunks
     * under the village are the Shire's and the game fills them as it generates them - which is exactly the ground
     * being taken away.
     */
    private static final int VILLAGERS = FOLK.length, VILLAGER_TRIES = 600;
    /** What the note's probe answers: the ground is there, it is gone, or the level holds no chunk to ask. */
    private static final int PRESENT = 1, GONE = 0, UNASKED = -1;
    /** How near a player has to be for the level to hold the village's chunks, in blocks: the reach, plus half of it. */
    private static final int NEAR = REACH + 300;
    /**
     * Where the note looks: the four corners of the footprint, its middle, and two more across it, inset from the edge.
     * <p>
     * The clear and the drape leave the ground inside a village where the build put it, so what stands on these columns
     * does not move once it stands - except when the chunks are trimmed or the world is replaced under the note, which is
     * the case worth catching. Seven rather than five because the note keeps a majority, and a column or two can move for
     * a reason that is not a village missing: a tree grown, a body of the village's own walking a grass block into dirt.
     */
    private static final int[][] PROBES = {{8, 8}, {542, 8}, {8, 459}, {542, 459}, {275, 233}, {137, 116}, {412, 350}};

    /** One probe as the note keeps it: the top of a column and the block under it, or -1 and nothing when unread. */
    private record Probe(int top, @Nullable String block) {
    }

    /** The village being raised, or null when there is one standing or nothing to raise. */
    private static Standing standing;
    /**
     * The server's own pause-when-empty setting while this pass holds it at zero, or -1 when it holds none.
     * <p>
     * A dedicated server that pauses itself with nobody on it does not tick its world, so the pass - which by design may
     * run with nobody online - is given the time by lifting that pause and giving the number back when it ends.
     */
    private static int heldPause = -1;
    /**
     * A corner this world notes a village at whose ground could not be read at start, or null.
     * <p>
     * A start holds almost none of the village's chunks, and asking the level to measure one it does not hold generates
     * it there and then. So the note's ground is asked about again on the first tick somebody is near enough for the
     * answer to be free - which is also the only moment a trimmed village matters.
     */
    @Nullable
    private static BlockPos held;

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
        if (stands(level, corner)) {
            int ground = ground(level, corner);
            if (ground != GONE) {
                held = ground == UNASKED ? corner : null;
                return null;
            }
            LOGGER.info("The Shire: this world notes the village at {}, but not its ground; raising it again",
                    corner.toShortString());
        }
        held = null;
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

    /**
     * Whether this world notes a village at that corner, made of this cut and these drape rules.
     * <p>
     * Three of the note's four questions are answered here, because they cost nothing: is it the same corner in the
     * same dimension; is it the same cut, piece for piece (a re-cut is not); and did the drape's own rules move since
     * (a fix to the ring is not a fix until it has been run on the world). The fourth - is the ground the drape left
     * still there - needs chunks, and {@link #ground} asks it where they are held.
     */
    /**
     * Whether this world's village has been raised to the end of its own pass.
     * <p>
     * Read from the note alone, so it answers at a start, where none of the village's chunks are held and asking about
     * its ground would generate them. The note is written as the pass finishes, which is the moment a pin taken in the
     * footprint stops being at risk - the town's hall is stood on this answer.
     */
    public static boolean complete(ServerLevel level) {
        return stands(level, ArdaMap.hobbitonPlan());
    }

    /**
     * Whether a pass over the village is in hand: planned and waiting for somebody to be near it, or at work now.
     * <p>
     * The village is cleared out of its footprint in the middle of the pass, and the crossing the Arda portal arrives
     * at stands in that footprint. This is the question the portal asks before it lets a traveller through: while a
     * pass is in hand the crossing is held shut, so a doorway put down at the arrival is not swept away under the
     * traveller a moment later. The pass ends by writing its note, and it is a note this reads only as a fallback.
     */
    public static boolean atWork() {
        return standing != null;
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
            // The note's fifth field is the dressing the village was given, so it has to be the dressing this build
            // would give it: a dressed village is not a bare one, whatever the ring is set to now.
            int noted = Integer.parseInt(fields[5]);
            return TolkienMobsConfig.dressShireRing ? noted == DRESS_VERSION : noted == UNDRESSED;
        } catch (IOException | NumberFormatException unreadable) {
            return false;
        }
    }

    /**
     * What the note's probe says about the ground the village was dressed into.
     * <p>
     * Every probe is read out of a chunk the level already holds, never by asking it for one, so a column in a chunk
     * nobody has loaded says nothing and the question waits. A column counts as moved when the top of it has moved more
     * than {@link #PROBE_SLACK}, or when the block it stands on is not the block the note wrote down - the second catches
     * a world replaced at the same height, the first a hill taken away. A village trimmed out of the world moves most of
     * them, so most of them is what has to have moved before this says the ground is gone; one tree or one trampled
     * grass block does not.
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

    /** Whether somebody is near enough to the village for the level to hold its chunks. */
    private static boolean near(ServerLevel level, BlockPos corner) {
        double reach = (double) NEAR * NEAR;
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(corner) <= reach) return true;
        }
        return false;
    }

    /**
     * Notes that a cut which is this village stands at a corner, which is what a hand's own drape leaves behind.
     * <p>
     * The village was laid by three commands in the worlds made before it stood itself, and a world like that has no
     * note of its own: without this, the first start after the village learned to raise itself would lay it again over
     * the one already standing. A drape is the last of the three passes, so a corner that has had one has its village.
     */
    static void dressed(ServerLevel level, String cut, BlockPos planted) {
        if (CUT.equals(cut)) note(level, planted, true);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("hobbiton")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source))
                .then(Commands.literal("raise").executes(Hobbiton::raiseNow))
                .then(Commands.literal("heal").executes(Hobbiton::healNow)));
    }

    /** Lays, clears and dresses the village now, wherever the players are, at the same budget a tick. */
    /**
     * Puts the village's own ground back where it has been worn away.
     * <p>
     * A village is walked over by its own folk, and a grass block under a body's feet turns to dirt, so a fortnight's
     * walking leaves a trail of bare dirt through the yards behind the hall that reads as ground the drape skipped. It
     * did not skip it: inside the walls the drape takes off what stands over the cut and never rewrites the cut's own
     * blocks, which is what keeps a wall and a floor a wall and a floor. So the one repair that can be made without
     * guessing is asked for by hand: the cut's own grass goes back wherever the world holds plain dirt at the cut's
     * height with nothing solid or wet over it, anywhere in the village's footprint.
     * <p>
     * Nowhere in the footprint is the answer rather than a circle round the hand, because the wear is the village's
     * own folk walking: they walk the whole of it, so a circle round wherever the hand happens to stand misses nearly
     * all of it. The cut is read in full either way, so there is nothing saved by asking for less.
     */
    private static int healNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getServer().getLevel(TolkienDimensions.ARDA);
        if (level == null) {
            source.sendFailure(Component.literal("Arda is not in this world, so there is no village ground to heal."));
            return 0;
        }
        try {
            List<CutCommands.Part> parts = CutCommands.parts(level, CUT);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal("No cut named " + CUT + " is in this world or in the jar."));
                return 0;
            }
            BlockPos corner = ArdaMap.hobbitonPlan();
            List<CityCommands.Part> cityParts = CutCommands.cityParts(parts);
            int healed = Dressing.repair(level, CutCommands.folder(level).resolve(CUT),
                    piece -> CutCommands.raw(level, CUT, piece), cityParts, corner,
                    CityCommands.Box.of(cityParts, corner), true, CutCommands.deck(level, CUT));
            source.sendSuccess(() -> Component.literal("The Shire: " + healed + " column(s) of the village's own"
                    + " ground took their grass back."), true);
            return healed;
        } catch (IOException unreadable) {
            source.sendFailure(Component.literal("The village's cut could not be read: " + unreadable.getMessage()));
            return 0;
        }
    }

    private static int raiseNow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Standing work = autospawn(source.getServer());
        if (work == null) {
            source.sendFailure(Component.literal("The village already stands here, or there is no Shire cut to raise."));
            return 0;
        }
        work.found = true;
        holdAwake(source.getServer());
        source.sendSuccess(() -> Component.literal("Raising the Shire: " + work.parts.size() + " pieces to lay, then"
                + " the country out of it, then the ring - at most " + BUDGET_MS + " ms a tick."), true);
        return work.parts.size();
    }

    /**
     * Refuses a body inside the village while the pass is clearing and dressing it.
     * <p>
     * The chunks under the village are the Shire's, so the game populates them as they are generated - on ground the pass
     * then takes away and lays again. The first measured raise paid for that in a log full of `Hobbit hit the ground too
     * hard`: bodies fell with the hill they were standing on. This is the last gate rather than a spawn rule, because a
     * chunk-generation spawn is finalised before it has a position: the body is refused as it joins the level, when it
     * finally has one, and {@link #inhabit} stands the village's own folk once the ground has settled.
     */
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        CutCommands.Box raising = raising();
        if (raising == null || !(event.getEntity() instanceof Mob body)) return;
        if (raising.holds(body.getX(), body.getY(), body.getZ())) event.setCanceled(true);
    }

    /** The ground being cleared and dressed right now, or null when nothing is being raised. */
    @Nullable
    private static CutCommands.Box raising() {
        return standing == null ? null : standing.box;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        standing = null;
        Standing work = autospawn(event.getServer());
        // A world that already holds the village gets its hall on the next start. This is asked of every world and not
        // only the ones raising a village, because raise refuses while the note says the village is unfinished - and a
        // village that is finished has already been through the pass that would have laid over the pin.
        ServerLevel arda = event.getServer().getLevel(TolkienDimensions.ARDA);
        if (arda != null) TownHalls.raise(arda, "hobbiton");
        if (work == null || !TolkienMobsConfig.raiseShireOnStart) return;
        work.found = true;
        holdAwake(event.getServer());
        LOGGER.info("The Shire: raising the village at the start, with nobody near it, as the config asks");
    }

    /**
     * Lifts the server's own pause for the length of the pass, and why it has to.
     * <p>
     * A dedicated server stops ticking its world once nobody has been on it for {@code pause-when-empty-seconds}, and
     * the stop is total: measured on 20 November on a fresh level with the property set to 5 s, the log reads
     * {@code Server empty for 5 seconds, pausing} part way through the pass and the pass never took another step. The
     * start-of-world pass is exactly the case that runs with nobody online, so on a server left to its own settings it
     * would never finish - which is the whole point of raising the village before anybody arrives. In the source, the
     * paused branch of {@code MinecraftServer.tickServer} returns before the tick event and before the level's own
     * tick, so there is nothing for the pass to be woken by: the setting itself is the thing to move.
     * <p>
     * The server's own setting is put back the moment the pass ends, so the file it is written in is changed twice and
     * ends as it began. A server stopped mid-pass leaves it at zero, which costs that server the idle time it would
     * have saved and nothing else.
     */
    private static void holdAwake(MinecraftServer server) {
        if (heldPause >= 0) return;
        if (!(server instanceof DedicatedServer dedicated)) return;
        int seconds = dedicated.pauseWhenEmptySeconds();
        if (seconds <= 0) return;
        heldPause = seconds;
        dedicated.setPauseWhenEmptySeconds(0);
        LOGGER.info("The Shire: this server pauses when empty after {} s, which is shorter than the pass; the pause is"
                + " lifted until the village stands", seconds);
    }

    /** Gives the server its pause back, if this pass took it. */
    private static void releaseAwake(MinecraftServer server) {
        if (heldPause < 0) return;
        int seconds = heldPause;
        heldPause = -1;
        if (!(server instanceof DedicatedServer dedicated)) return;
        dedicated.setPauseWhenEmptySeconds(seconds);
        LOGGER.info("The Shire: the server's pause when empty is back to {} s", seconds);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        Standing work = standing;
        if (work == null) {
            // A village the note describes but whose ground the start could not read: ask again, once, now that
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
            work.ticks++;
            if (done) {
                work.release(level);
                standing = null;
                releaseAwake(event.getServer());
                note(level, work.planted, work.dress);
                // The town's hall goes up with the village and after it: this pass has just laid the cut, cleared the
                // footprint and dressed the ring, and a hall stood in that ground first would have been cleared with the
                // country. Before the folk, so nobody is stood inside its walls.
                TownHalls.raise(level, "hobbiton");
                inhabit(level, work);
                LOGGER.info("The Shire: the village stands at {} in {} ticks ({} s){}", work.planted.toShortString(),
                        work.ticks, String.format(Locale.ROOT, "%.1f", work.ticks / 20.0D),
                        work.dress ? ", dressed to its own outline"
                                : ", on the country as the cut lays it, with the ring left alone");
            }
        } catch (IOException unreadable) {
            LOGGER.warn("The Shire: the village could not be raised: {}", unreadable.getMessage());
            work.release(level);
            standing = null;
            releaseAwake(event.getServer());
        }
    }

    /** A server stopped with a pass half done gives its pause back before it goes. */
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        releaseAwake(event.getServer());
    }

    /**
     * Whether anybody is near enough to see the village come up.
     * <p>
     * Only asked when the world's setting leaves the pass until somebody is near. That is not only an economy: measured
     * on a server with no players, the first block written into the 1326 freshly generated chunks under the village
     * waited forty-three seconds for the game to finish them, all in one tick - and it waited there because no player
     * was standing in the region to keep its chunks ticking while they finished. Asked a chunk at a time, that drain is
     * paid a chunk's worth a tick instead (see {@link Standing#awaitGround}), which is what lets the pass run at a start
     * with nobody in the world at all.
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
        /** The tick the pass began on, -1 until it does, and how many ticks it has taken. */
        /** Whether this world's pass dresses the ring, taken once when the pass is made rather than read a step at a
         * time: a setting changed under a pass in hand would leave half a ring behind it. */
        private final boolean dress;
        private int began = -1;
        private int ticks;
        /** Whether a hand asked for it, or the config did, rather than a player being near. */
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
            this.dress = TolkienMobsConfig.dressShireRing;
            // The ring is the only reason the pass holds chunks outside the footprint, so a bare village asks for its
            // own ground and nothing else: the country round it is left to generate as it would with the cut laid by
            // itself, which is the whole of what the ring was there to hide.
            int reach = this.dress ? CityCommands.DRAPE_REACH : 0;
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
            if (!this.dress) return true;
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

    /**
     * Stands the village's own folk on the deck it was dressed to.
     * <p>
     * Only the flat of the village will do: a column whose top is within a block of the cut's own floor, with room to
     * stand and something to stand on. Roofs, tree crowns and the hillside outside the walls are all a block or ten over
     * that, so they are passed over rather than filled with hobbits on a slope. A world that already holds some of them
     * is topped up to {@link #VILLAGERS} and no further, so a village re-raised over itself does not collect a crowd.
     */
    private static void inhabit(ServerLevel level, Standing work) {
        CutCommands.Box box = work.box;
        List<HobbitEntity> living = level.getEntitiesOfClass(HobbitEntity.class, new AABB(box.lowX(), box.lowY(),
                box.lowZ(), box.highX() + 1, box.highY() + 1, box.highZ() + 1));
        int there = living.size();
        // A village's names are its own: somebody already called Belba is not stood again as somebody else, and a
        // village raised a second time keeps the people it has.
        List<String> taken = new ArrayList<>();
        for (HobbitEntity villager : living) {
            Component named = villager.getCustomName();
            if (named != null) taken.add(named.getString());
        }
        int deck = work.planted.getY() + work.deck + 1;
        RandomSource random = RandomSource.create(work.planted.asLong());
        int stood = 0;
        List<String> called = new ArrayList<>();
        for (int tries = 0; there + stood < VILLAGERS && tries < VILLAGER_TRIES; tries++) {
            int x = random.nextInt(box.lowX(), box.highX() + 1);
            int z = random.nextInt(box.lowZ(), box.highZ() + 1);
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            if (Math.abs(top - deck) > 1) continue;
            BlockPos spot = new BlockPos(x, top, z);
            if (!level.getBlockState(spot).getCollisionShape(level, spot).isEmpty()) continue;
            if (!level.getBlockState(spot.above()).getCollisionShape(level, spot.above()).isEmpty()) continue;
            if (level.getBlockState(spot.below()).getCollisionShape(level, spot.below()).isEmpty()) continue;
            String name = null;
            for (String candidate : FOLK) {
                if (!taken.contains(candidate)) {
                    name = candidate;
                    break;
                }
            }
            if (name == null) break;
            Entity body = TolkienEntities.ENTITY_TTM_HOBBIT.get().create(level, EntitySpawnReason.EVENT);
            if (!(body instanceof Mob hobbit)) continue;
            taken.add(name);
            hobbit.setCustomName(Component.literal(name));
            hobbit.setCustomNameVisible(true);
            hobbit.snapTo(x + 0.5D, top, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            hobbit.setPersistenceRequired();
            if (!level.addFreshEntity(hobbit)) continue;
            stood++;
            called.add(name);
        }
        LOGGER.info("The Shire: {} of the Shire's folk are standing in the village at {}, {} were there already{}",
                stood, work.planted.toShortString(), there,
                called.isEmpty() ? "" : " - " + String.join(", ", called));
    }

    /** Writes the note that says what the village stands on, so the next start knows whether it still holds it. */
    private static void note(ServerLevel level, BlockPos corner, boolean dressed) {
        try {
            Path marker = marker(level);
            Files.createDirectories(marker.getParent());
            StringBuilder line = new StringBuilder()
                    .append(level.dimension().identifier()).append(' ')
                    .append(corner.getX()).append(' ').append(corner.getY()).append(' ').append(corner.getZ())
                    .append(' ').append(CutCommands.fingerprint(level, CUT))
                    .append(' ').append(dressed ? DRESS_VERSION : UNDRESSED);
            for (Probe probe : probe(level, corner)) {
                line.append(' ').append(probe.top()).append('@')
                        .append(probe.block() == null ? "-" : probe.block());
            }
            Files.writeString(marker, line.toString());
        } catch (IOException unwritable) {
            LOGGER.warn("The Shire: the village stands, but that could not be written: {}", unwritable.getMessage());
        }
    }
}
