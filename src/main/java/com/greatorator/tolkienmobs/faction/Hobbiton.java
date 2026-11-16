package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
 * So the village is stood by the same pass the hold is, on the same clock. The three steps are the three commands,
 * in the order they have to happen - the cut laid, the country taken out of it, the ring dressed to its own outline -
 * and the only thing this class decides is <em>when</em> each one is done: a band of columns a tick and a few pieces a
 * tick, because seven hundred blocks of ring dressed in one call is a stall at the exact moment a player is waiting for
 * their world. The dressing itself is {@link Dressing}'s own job, stepped by this class: a second drape rule for a
 * second build is how a country ends up with two ideas about where a wall stops.
 * <p>
 * The water the village carries out is {@link ArdaMap}'s channel for it, and the level it leaves at is read from the
 * pin below, so what is raised here is the build the drawing was cut for.
 */
@EventBusSubscriber(modid = MODID)
public final class Hobbiton {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hobbiton.class);

    /** The cut's name: its folder in the jar's {@code structure/} and, for a re-cut, in the save's {@code cuts/}. */
    private static final String CUT = "hobbiton";
    /**
     * How much of the cut is laid in a tick. A piece is forty-eight cubed, so a handful is already more than a
     * frame's worth of block writes.
     */
    private static final int PIECES_PER_TICK = 4;
    /** How many of the village's columns are cleared in a tick: a band across the footprint, not the whole of it. */
    private static final int COLUMNS_PER_TICK = 3000;
    /** What this world has made of the village, beside its cut: the dimension and the corner it stood at. */
    private static final String MARKER = "_stands.txt";

    /** The village being raised, or null when there is one standing or nothing to raise. */
    private static Standing standing;

    private Hobbiton() {
    }

    /**
     * Begins raising the village if this world does not already hold it.
     * <p>
     * Answers straight away: the work is done a band at a time from {@link #onServerTick}, so a world's first start is
     * not held up by its own village. A world that already has one - by this pass on an earlier start, or by the
     * three commands a hand typed - is left exactly as it is.
     *
     * @return how many pieces are to be laid, or nil when the village already stands or the cut is not this build's
     */
    public static int autospawn(MinecraftServer server) {
        if (standing != null) return 0;
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
        standing = new Standing(parts, corner, CutCommands.Box.of(parts, corner), CutCommands.deck(level, CUT));
        LOGGER.info("The Shire: {} pieces of the village are to be laid at {}", parts.size(), corner.toShortString());
        return parts.size();
    }

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
        try {
            if (!work.lay(level)) return;
            if (!work.clear(level)) return;
            if (!work.dress(level)) return;
        } catch (IOException unreadable) {
            // A cut that cannot be read is not retried every tick for the rest of the session: what is standing is
            // left standing, and the next start asks again.
            LOGGER.warn("The Shire: the village could not be raised: {}", unreadable.getMessage());
            standing = null;
            return;
        }
        standing = null;
        note(level, work.planted);
        LOGGER.info("The Shire: the village stands at {}, dressed to its own outline", work.planted.toShortString());
    }

    /** The village in the middle of being raised: its cut, its corner, and how far through each step it is. */
    private static final class Standing {
        private final List<CutCommands.Part> parts;
        /** The same pieces in the shape the shared drape reads: the city's, which is the shape every cut is dressed in. */
        private final List<CityCommands.Part> cityParts;
        private final BlockPos planted;
        private final CutCommands.Box box;
        /** The same box in the shared drape's shape, which is the one it lays its ring in. */
        private final CityCommands.Box cityBox;
        private final int deck;
        private CutCommands.Shape shape;
        private Dressing.Job dressing;
        private int piece;
        private int column;

        private Standing(List<CutCommands.Part> parts, BlockPos planted, CutCommands.Box box, int deck) {
            this.parts = parts;
            this.cityParts = CutCommands.cityParts(parts);
            this.planted = planted;
            this.box = box;
            this.cityBox = CityCommands.Box.of(this.cityParts, planted);
            this.deck = deck;
        }

        /**
         * Lays the next few pieces of the cut.
         *
         * @return whether the whole cut is laid
         */
        private boolean lay(ServerLevel level) throws IOException {
            int upto = Math.min(this.parts.size(), this.piece + PIECES_PER_TICK);
            for (; this.piece < upto; this.piece++) {
                CutCommands.Part part = this.parts.get(this.piece);
                // The village's box lies far from any world's spawn, so its chunks are nobody's: a piece written into
                // an unopened chunk is a piece that was never laid.
                load(level, this.planted.offset(part.dx(), part.dy(), part.dz()), part.wide(), part.deep());
                CutCommands.lay(level, CUT, part, this.planted);
            }
            return this.piece >= this.parts.size();
        }

        /**
         * Clears the next band of the village's columns.
         *
         * @return whether the whole footprint is clear
         */
        private boolean clear(ServerLevel level) throws IOException {
            if (this.shape == null) {
                this.shape = CutCommands.shape(level, CUT, this.parts);
            }
            int total = this.shape.wide() * this.shape.deep();
            if (total <= 0) return true;
            int upto = Math.min(total, this.column + COLUMNS_PER_TICK);
            CutCommands.clearColumns(level, this.planted, this.box, this.shape, this.column, upto);
            this.column = upto;
            return this.column >= total;
        }

        /**
         * Steps the drape until the ring is dressed.
         *
         * @return whether the ring is dressed
         */
        private boolean dress(ServerLevel level) throws IOException {
            if (this.dressing == null) {
                this.dressing = Dressing.begin(level, CutCommands.folder(level).resolve(CUT),
                        piece -> CutCommands.raw(level, CUT, piece), this.cityParts, this.planted, this.cityBox, true,
                        this.deck);
            }
            return !this.dressing.step(level);
        }
    }

    /** Asks for every chunk a box touches, so the village is laid into ground the game is actually holding. */
    private static void load(ServerLevel level, BlockPos low, int wide, int deep) {
        for (int cx = low.getX() >> 4; cx <= (low.getX() + wide - 1) >> 4; cx++) {
            for (int cz = low.getZ() >> 4; cz <= (low.getZ() + deep - 1) >> 4; cz++) {
                level.getChunk(cx, cz);
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
            LOGGER.warn("The Shire: the village stands, but that could not be written: {}",
                    unwritable.getMessage());
        }
    }
}
