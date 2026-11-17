package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.Nullable;

/**
 * The Carrock: the rock the owner shaped in the Anduin, cut and shipped as a build the world can ask for by name.
 * <p>
 * It is not a template but a cut - three pieces and a {@code carrock.parts} table, in the jar at
 * {@code /data/tolkienmobs/structure/carrock/}, in the shape {@link CutCommands} already reads. That is what makes it
 * ready to place: {@code /tmcut table carrock} and {@code /tmcut place carrock <corner>} work in any world off the jar
 * alone, and the same folder is what the world's own pass reads when it stands the rock by itself.
 * <p>
 * <b>The anchor is the cut's own low corner</b> - its north-west bottom block, the corner the table measures its
 * offsets from. The table starts at 0, 0, 0, so the block a placement names is that corner, and the build runs
 * {@link #WIDE} east, {@link #HEIGHT} up and {@link #DEEP} south of it. The pin in {@link Pins} under {@link #NAME} is
 * that same corner, which is why the spot the owner gave is one coordinate and not a box: 8307, 187, -4165.
 * <p>
 * <b>How it comes to stand.</b> It stands itself, the way the Gundabad gate and the keep do: {@link #autospawn} runs as
 * the server comes up, takes that pin, and lays the three pieces from it when this world has no note of the rock - so a
 * world made after this ships holds the Carrock without a command being typed. The note is written beside the cut in the
 * save's own folder once the pieces are down, and every start after that reads it and does nothing. Laying is a write of
 * the cut's own blocks and nothing besides - the pieces carry no air - so a world whose note is missing, or whose pin has
 * been moved, is laid again rather than cut about.
 * <p>
 * <b>How it is bedded.</b> The rock was cut out of a river, so in most worlds it stands with water between its low course
 * and the bed. {@link #seat} reads down every column that carries one of the build's own blocks on its base course and
 * fills the water under it with stone - but only where the water runs straight down to ground, which is the one case
 * where the bed is the river's and not something else's. A column standing on stone already, or hanging over a cave or
 * over open air, is left alone: nothing is bedded to a guess. The pass is idempotent and runs on every start, so a world
 * that has held the rock since before there was a bed gets one the next time it comes up.
 * <p>
 * <b>What was cut.</b> Boxed by four ammolite markers in the Arda save's overworld, all at y 36 in the river: 16694 and
 * 16735 across, 5627 and 5711 south. The markers sat on the water's surface, so the cut starts under them at the rock's
 * own base course, and it carries stone, dirt, gravel, sand, clay, grass, a dirt path and the spruce standing on it,
 * with the water of the river and the markers themselves left out: air, water and ammolite are all absent from the
 * pieces.
 * <p>
 * <b>And what was taken back off it.</b> That first cut carried the ground the rock stands on with it - the riverbed and
 * the clipped banks, a plate no more than six blocks deep that spread over the whole box and came out of the water at
 * the south end as three tongues of bare dirt. Standing at the pin it read as terrain nobody had built, so every column
 * whose highest block stood at or under the plate's top (y 5) was cut away: 2,860 blocks in 1,189 columns, nothing
 * above them touched and the rock's own foot left whole. The pieces hold 36,079 blocks in ten kinds, and the only
 * columns that do not start at the base are fourteen leaf columns of the spruce leaning off the west side.
 */
public final class Carrock {
    private static final Logger LOGGER = LoggerFactory.getLogger(Carrock.class);

    /** The name the build is asked for by: its folder in the jar, and what a pin or a page would call it. */
    public static final String NAME = "carrock";

    /** How much ground the cut covers: east, up, south, from its low corner. */
    public static final int WIDE = 42;
    public static final int HEIGHT = 57;
    public static final int DEEP = 85;

    /** The low corner: where a placement's coordinate goes, and where the cut's own corner stood when it was taken. */
    public static final BlockPos CORNER = new BlockPos(16694, 34, 5627);

    /** Where this world's note about the rock lives: beside its cut, in the save's own folder. */
    private static final String MARKER = "_stands.txt";

    /**
     * How deep under its base the bed will follow water before it gives the column up, in blocks.
     * <p>
     * A river is a handful of courses; the number is only here so a column over something that is not a river at all -
     * a drowned chasm, a rule of water down a cave - is not filled to the world's floor on a guess.
     */
    private static final int REACH = 64;

    /** A world's note about the rock: the corner it is planted on, and the cut it was made from. */
    private record Note(BlockPos corner, String fingerprint) {
    }

    /**
     * Stands the rock at its pin if this world does not already hold it, then beds it into the river.
     * <p>
     * Read at the server's start, before anybody has walked here. The note answers whether the rock is there, so only a
     * world without one has the pieces laid - which is the first start of a world, and the case the whole build is
     * shipped for. The bed is asked for every time: it writes only where water stands under the rock's own base, so a
     * rock already bedded costs a read and nothing else, and a world that has held the rock since before there was a bed
     * gets one on its next start.
     *
     * @return how many pieces were laid, or nothing when the rock already stands or there is nothing to lay
     */
    public static int autospawn(MinecraftServer server) {
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return 0;
        BlockPos pin = Pins.at(level, NAME);
        if (pin == null) return 0;
        String fingerprint = CutCommands.fingerprint(level, NAME);
        Note note = note(level);
        int laid = 0;
        BlockPos base = pin;
        if (note == null || !note.corner().equals(pin) || !note.fingerprint().equals(fingerprint)) {
            List<CutCommands.Part> parts;
            try {
                parts = CutCommands.parts(level, NAME);
            } catch (IOException unreadable) {
                LOGGER.warn("The Carrock: the cut could not be read, so the rock is not laid: {}",
                        unreadable.getMessage());
                return 0;
            }
            if (parts.isEmpty()) {
                LOGGER.warn("The Carrock: there is no {} cut in the jar or in this world's own folder, so nothing is"
                        + " laid", NAME);
                return 0;
            }
            base = plantedAt(parts, pin);
            try {
                for (CutCommands.Part part : parts) {
                    CutCommands.lay(level, NAME, part, base);
                }
            } catch (IOException unreadable) {
                LOGGER.warn("The Carrock: the rock could not be laid: {}", unreadable.getMessage());
                return 0;
            }
            write(level, base, fingerprint);
            LOGGER.info("The Carrock: stood at {}, {} pieces of the {} cut, {} by {} by {} from its low north-west"
                    + " corner", base.toShortString(), parts.size(), fingerprint, WIDE, HEIGHT, DEEP);
            laid = parts.size();
        }
        int bedded = seat(level, base);
        if (bedded > 0) {
            LOGGER.info("The Carrock: {} blocks of stone bedded under the rock at {}, only where the river runs"
                    + " straight down to its bed", bedded, base.toShortString());
        }
        return laid;
    }

    /**
     * Beds the rock into the river: stone under the build's own base, only where the water runs straight down.
     * <p>
     * Under every column that carries one of the build's own blocks on its base course, the column below is read
     * downward: courses of water are counted, and the first block that is not water ends the read. Stone is written only
     * when that block is ground - not air - and at least one course of water was passed, which is what "water straight
     * down to the ground at the base of the river" means. Anything else is left exactly as it is: a column already
     * standing on stone needs no bed, and one hanging over a cave or over open air is not the river's to fill. The stone
     * is plain, not the build's own rock, because what it stands for is the river's bed and not a piece of the Carrock.
     *
     * @return how many blocks of stone were written
     */
    static int seat(ServerLevel level, BlockPos base) {
        load(level, base);
        int bedded = 0;
        for (int x = base.getX(); x < base.getX() + WIDE; x++) {
            for (int z = base.getZ(); z < base.getZ() + DEEP; z++) {
                BlockState foot = level.getBlockState(new BlockPos(x, base.getY(), z));
                // The build's own block, not the river standing in the box: a column of open water on the base course is
                // not a column of the rock, and bedding it would put a stone pillar under nothing at all.
                if (foot.isAir() || !foot.getFluidState().isEmpty()) continue;
                int water = 0;
                int bed = Integer.MIN_VALUE;
                int lowest = Math.max(level.getMinY(), base.getY() - REACH);
                for (int y = base.getY() - 1; y >= lowest; y--) {
                    BlockState state = level.getBlockState(new BlockPos(x, y, z));
                    if (state.is(Blocks.WATER)) {
                        water++;
                        continue;
                    }
                    if (!state.isAir()) bed = y;
                    break;
                }
                if (water == 0 || bed == Integer.MIN_VALUE) continue;
                for (int y = bed + 1; y < base.getY(); y++) {
                    level.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState(), 2);
                    bedded++;
                }
            }
        }
        return bedded;
    }

    /**
     * Where the pieces go for a corner that was named: the same corner the command means.
     * <p>
     * A table that starts its offsets at zero lands its low corner on the block named, and one that starts them forty
     * blocks in lands it forty short - the rule {@code /tmcut place} already plants by, kept here so the world's own
     * pass and a hand's command cannot disagree about which block the pin is.
     */
    private static BlockPos plantedAt(List<CutCommands.Part> parts, BlockPos pointed) {
        int lowX = parts.stream().mapToInt(CutCommands.Part::dx).min().orElse(0);
        int lowY = parts.stream().mapToInt(CutCommands.Part::dy).min().orElse(0);
        int lowZ = parts.stream().mapToInt(CutCommands.Part::dz).min().orElse(0);
        return new BlockPos(pointed.getX() - lowX, pointed.getY() - lowY, pointed.getZ() - lowZ);
    }

    /**
     * This world's note about the rock, or null when it has none it can read.
     * <p>
     * The note answers three questions for nothing: is it this dimension, is it this corner, and is it this cut - the
     * fingerprint being the piece count and a hash of the table, so a re-cut that moved a piece or added one answers
     * differently and the world lays the build it was shipped with.
     */
    @Nullable
    private static Note note(ServerLevel level) {
        Path marker = marker(level);
        if (!Files.isRegularFile(marker)) return null;
        try {
            String[] fields = Files.readString(marker).trim().split("\\s+");
            if (fields.length < 5) return null;
            if (!fields[0].equals(level.dimension().identifier().toString())) return null;
            return new Note(new BlockPos(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]),
                    Integer.parseInt(fields[3])), fields[4]);
        } catch (IOException | NumberFormatException unreadable) {
            return null;
        }
    }

    /** Writes the note that says the rock stands, so the next start leaves it alone. */
    private static void write(ServerLevel level, BlockPos planted, String fingerprint) {
        try {
            Path marker = marker(level);
            Files.createDirectories(marker.getParent());
            Files.writeString(marker, level.dimension().identifier() + " " + planted.getX() + " " + planted.getY() + " "
                    + planted.getZ() + " " + fingerprint);
        } catch (IOException unwritable) {
            LOGGER.warn("The Carrock: the rock stands, but that could not be written: {}", unwritable.getMessage());
        }
    }

    /** Asks for every chunk the rock's box touches, so a world holding none of them can still be read and bedded. */
    private static void load(ServerLevel level, BlockPos base) {
        for (int cx = base.getX() >> 4; cx <= (base.getX() + WIDE - 1) >> 4; cx++) {
            for (int cz = base.getZ() >> 4; cz <= (base.getZ() + DEEP - 1) >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    /** Where this world's note about the rock lives: beside its cut, in the save's own folder. */
    private static Path marker(ServerLevel level) {
        return CutCommands.folder(level).resolve(NAME).resolve(MARKER);
    }

    private Carrock() {
    }
}
