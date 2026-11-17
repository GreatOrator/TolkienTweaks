package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
     * Stands the rock at its pin if this world does not already hold it.
     * <p>
     * Read at the server's start, before anybody has walked here, so it asks nothing of the world that would generate
     * ground a player is not standing on: the note answers whether the rock is there, and only a world without one has
     * the pieces laid - which is the first start of a world, and the case the whole build is shipped for.
     *
     * @return how many pieces were laid, or nothing when the rock already stands or there is nothing to lay
     */
    public static int autospawn(MinecraftServer server) {
        ServerLevel level = server.getLevel(TolkienDimensions.ARDA);
        if (level == null) return 0;
        BlockPos pin = Pins.at(level, NAME);
        if (pin == null) return 0;
        String fingerprint = CutCommands.fingerprint(level, NAME);
        if (notedAt(level, pin, fingerprint)) return 0;
        List<CutCommands.Part> parts;
        try {
            parts = CutCommands.parts(level, NAME);
        } catch (IOException unreadable) {
            LOGGER.warn("The Carrock: the cut could not be read, so the rock is not laid: {}", unreadable.getMessage());
            return 0;
        }
        if (parts.isEmpty()) {
            LOGGER.warn("The Carrock: there is no {} cut in the jar or in this world's own folder, so nothing is laid",
                    NAME);
            return 0;
        }
        BlockPos planted = plantedAt(parts, pin);
        try {
            for (CutCommands.Part part : parts) {
                CutCommands.lay(level, NAME, part, planted);
            }
        } catch (IOException unreadable) {
            LOGGER.warn("The Carrock: the rock could not be laid: {}", unreadable.getMessage());
            return 0;
        }
        write(level, planted, fingerprint);
        LOGGER.info("The Carrock: stood at {}, {} pieces of the {} cut, {} by {} by {} from its low north-west corner",
                planted.toShortString(), parts.size(), fingerprint, WIDE, HEIGHT, DEEP);
        return parts.size();
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
     * Whether this world notes the rock at that corner, made of this cut.
     * <p>
     * The note answers three questions for nothing: is it this dimension, is it this corner, and is it this cut - the
     * fingerprint being the piece count and a hash of the table, so a re-cut that moved a piece or added one answers
     * differently and the world lays the build it was shipped with.
     */
    private static boolean notedAt(ServerLevel level, BlockPos pin, String fingerprint) {
        Path marker = marker(level);
        if (!Files.isRegularFile(marker)) return false;
        try {
            String[] fields = Files.readString(marker).trim().split("\\s+");
            return fields.length >= 5
                    && fields[0].equals(level.dimension().identifier().toString())
                    && Integer.parseInt(fields[1]) == pin.getX()
                    && Integer.parseInt(fields[2]) == pin.getY()
                    && Integer.parseInt(fields[3]) == pin.getZ()
                    && fields[4].equals(fingerprint);
        } catch (IOException | NumberFormatException unreadable) {
            return false;
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

    /** Where this world's note about the rock lives: beside its cut, in the save's own folder. */
    private static Path marker(ServerLevel level) {
        return CutCommands.folder(level).resolve(NAME).resolve(MARKER);
    }

    private Carrock() {
    }
}
