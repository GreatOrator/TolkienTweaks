package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The hand for standing the city up while it is still being cut.
 * <p>
 * The valley's own pieces are shipped in the jar, so the campaign can read them from any world at any
 * time. The city is not: three hundred and fifty-eight templates and ten megabytes of them, cut from a
 * world that is still being worked on, and copied into the pack only once the cut is final. Until then
 * this reads them from the folder the cut was written to - {@code run/rivendell_art}, beside the game
 * directory - so a re-cut is picked up by walking back to it rather than by rebuilding the jar.
 * <p>
 * It exists for the two things that need a city in a world before the pack has one: looking at the
 * terrain, and walking the campaign's pages through a valley of the right size. One root,
 * {@code /tmcity}, with five hands: {@code origin}, which plants the corner where the caller stands;
 * {@code band [count]}, which lays the next band of the city and stops, because a whole city is two
 * thousand chunks and those are generated chunk by chunk; {@code apron}, which blends the shoulder into
 * the ground once the last band is down; {@code goto}, which stands the caller on the middle of the
 * city; and {@code status}, which says how much is standing.
 * <p>
 * Every number it prints is a world coordinate of the city itself rather than of the table the pieces
 * are cut against. The two differ by the width of the cut's own frame - five hundred blocks or more -
 * so the frame is kept to itself: a message that names it sends whoever is testing the build to a
 * hillside a long walk from the build.
 * <p>
 * It answers to the same caller the arrangement commands do: a gamemaster-level operator, or a player
 * in creative.
 */
@EventBusSubscriber(modid = MODID)
public final class CityCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_BUILD = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** The folder the cut was written to, in the game directory, and the table of where each piece goes. */
    private static final String FOLDER = "rivendell_art";
    private static final String PARTS = "rivendell.parts";
    /** Where the marker lives: how much of the city is standing, and where its corner was planted. */
    private static final String MARKER = "_laid.txt";
    /** A piece is this wide and this tall, which is also the height of one band. */
    private static final int TILE = 48;
    /** How deep under the lowest band the ground is filled, in blocks. */
    private static final int UNDERPIN = 24;
    /** How many rings the shoulder is tapered over. */
    private static final int RINGS = 4;

    /** Templates already read this session, so the ten megabytes are parsed once. */
    private static final Map<String, StructureTemplate> LOADED = new HashMap<>();

    /** One piece: its file, and where its corner sits in the frame the cut was taken in. */
    private record Part(String name, int dx, int dy, int dz) {
    }

    /**
     * The ground the city covers once its corner is planted, in world coordinates.
     * <p>
     * The pieces are cut against a frame that is wider than the city - the box the cut was taken out of
     * - so a piece's offset is not where it lands. This is the one place that difference is worked out,
     * and every message and every teleport reads the city through it.
     */
    private record Box(int lowX, int lowY, int lowZ, int highX, int highY, int highZ) {
        static Box of(List<Part> parts, BlockPos origin) {
            int lowX = parts.stream().mapToInt(Part::dx).min().orElse(0);
            int lowY = parts.stream().mapToInt(Part::dy).min().orElse(0);
            int lowZ = parts.stream().mapToInt(Part::dz).min().orElse(0);
            int highX = parts.stream().mapToInt(Part::dx).max().orElse(0) + TILE - 1;
            int highY = parts.stream().mapToInt(Part::dy).max().orElse(0) + TILE - 1;
            int highZ = parts.stream().mapToInt(Part::dz).max().orElse(0) + TILE - 1;
            return new Box(origin.getX() + lowX, origin.getY() + lowY, origin.getZ() + lowZ,
                    origin.getX() + highX, origin.getY() + highY, origin.getZ() + highZ);
        }

        BlockPos middle() {
            return new BlockPos((this.lowX + this.highX) / 2, (this.lowY + this.highY) / 2,
                    (this.lowZ + this.highZ) / 2);
        }

        String spoken() {
            return this.lowX + ".." + this.highX + " x, " + this.lowZ + ".." + this.highZ + " z, y "
                    + this.lowY + ".." + this.highY;
        }
    }

    /**
     * How far through the city a world is: the corner, the next band, whether the shoulder is done, and
     * the dimension it was planted in.
     * <p>
     * The marker sits in the game directory rather than in a save, so it survives being taken to another
     * world. The dimension is written down with it for that reason: a corner planted in one country and
     * bands laid in another raises the city in a place nobody is looking at.
     */
    private record Mark(BlockPos origin, int band, boolean apron, String dimension) {
    }

    private CityCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmcity")
                .requires(MAY_BUILD)
                .then(Commands.literal("origin").executes(CityCommands::origin))
                .then(Commands.literal("band")
                        .executes(context -> band(context, 1))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 8))
                                .executes(context -> band(context, IntegerArgumentType.getInteger(context, "count")))))
                .then(Commands.literal("apron").executes(CityCommands::apron))
                .then(Commands.literal("goto").executes(CityCommands::visit))
                .then(Commands.literal("status").executes(CityCommands::status)));
    }

    /** Plants the city's corner at the caller's feet, and forgets whatever was standing before. */
    private static int origin(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            List<Part> parts = parts(folder);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal("No pieces in " + folder + " - is the cut there?"));
                return 0;
            }
            // The city's lowest corner goes under the caller's feet, so the band that carries the ground
            // lands where they are standing and the rest of the city rises from there.
            int lowX = parts.stream().mapToInt(Part::dx).min().orElse(0);
            int lowY = parts.stream().mapToInt(Part::dy).min().orElse(0);
            int lowZ = parts.stream().mapToInt(Part::dz).min().orElse(0);
            BlockPos feet = BlockPos.containing(source.getPosition());
            BlockPos planted = feet.offset(-lowX, -lowY, -lowZ);
            // The marked spots are written in this same frame, so the folk know where they are the moment
            // the corner is planted - otherwise the city would stand empty of the people meant to live in it.
            Campaign.setValleyOrigin(planted);
            write(folder, new Mark(planted, 0, false, level.dimension().identifier().toString()));
            Box box = Box.of(parts, planted);
            String spoken = box.spoken();
            int bands = bands(parts).size();
            int pieces = parts.size();
            source.sendSuccess(() -> Component.literal("City planted at " + spoken + "; " + bands
                    + " bands, " + pieces + " pieces. Run /tmcity band to lay it, /tmcity goto to stand in it."), true);
            // A city with a mountain in it is taller than the sky it is planted under, and the world does
            // not refuse blocks above its ceiling out loud - it drops them. Said here rather than after
            // the laying, when the only sign of it is a build with the tops of its towers missing.
            int tall = box.highY() - box.lowY() + 1;
            if (box.highY() > level.getMaxY()) {
                int overflow = box.highY() - level.getMaxY();
                int fits = level.getMaxY() - tall + 1;
                source.sendFailure(Component.literal("That is " + tall + " blocks tall, so " + overflow
                        + " of it stand above the sky and cannot be laid. Plant the corner at y " + fits
                        + " or lower to stand the whole city."));
            }
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** Lays the next band or bands of the city, and the shoulder once the last one is down. */
    private static int band(CommandContext<CommandSourceStack> context, int count) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            Mark mark = read(folder);
            if (mark == null) {
                source.sendFailure(Component.literal("No corner planted yet - run /tmcity origin where it should stand."));
                return 0;
            }
            String elsewhere = elsewhere(mark, level);
            if (elsewhere != null) {
                source.sendFailure(Component.literal(elsewhere));
                return 0;
            }
            List<Part> parts = parts(folder);
            List<Integer> bands = bands(parts);
            int laid = 0;
            int over = 0;
            for (int placed = 0; placed < count && mark.band() < bands.size(); placed++) {
                int height = bands.get(mark.band());
                for (Part part : parts) {
                    if (part.dy() != height) continue;
                    BlockPos at = mark.origin().offset(part.dx(), part.dy(), part.dz());
                    // A piece that would stand above the sky is not laid at all, and is counted rather
                    // than reported as laid: the world drops a block above its own ceiling without a
                    // word, so the old count said "laid" for pieces that put nothing anywhere.
                    if (at.getY() + TILE - 1 > level.getMaxY() || at.getY() < level.getMinY()) {
                        over++;
                        continue;
                    }
                    lay(level, folder, part, mark.origin());
                    // The lowest band is the city's floor, so it is the one that needs ground under it.
                    if (mark.band() == 0) Sites.underpin(level, at, TILE, TILE, UNDERPIN);
                    laid++;
                }
                mark = new Mark(mark.origin(), mark.band() + 1, mark.apron(), mark.dimension());
            }
            boolean finished = mark.band() >= bands.size();
            if (finished && !mark.apron()) {
                skirt(level, parts, mark.origin());
                mark = new Mark(mark.origin(), mark.band(), true, mark.dimension());
            }
            write(folder, mark);
            int soFar = laid;
            int skipped = over;
            boolean whole = finished;
            int done = Math.min(mark.band(), bands.size());
            int total = bands.size();
            String sky = skipped == 0 ? "" : "; " + skipped
                    + " were above the sky and left out - /tmcity origin planted lower would take them";
            String spoken = whole ? " - the whole city is down; /tmcity goto to stand in it" : "";
            source.sendSuccess(() -> Component.literal("Laid " + soFar + " pieces; " + done + " of "
                    + total + " bands down" + sky + spoken), true);
            return laid;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** Blends the shoulder by hand, for a city whose last band is already down. */
    private static int apron(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            Mark mark = read(folder);
            if (mark == null) {
                source.sendFailure(Component.literal("No corner planted yet - run /tmcity origin first."));
                return 0;
            }
            String elsewhere = elsewhere(mark, level);
            if (elsewhere != null) {
                source.sendFailure(Component.literal(elsewhere));
                return 0;
            }
            List<Part> parts = parts(folder);
            skirt(level, parts, mark.origin());
            write(folder, new Mark(mark.origin(), mark.band(), true, mark.dimension()));
            source.sendSuccess(() -> Component.literal("Shoulder blended over " + RINGS + " rings."), true);
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** Stands the caller on the middle of the city, which is where a build is walked from. */
    private static int visit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            Mark mark = read(folder);
            if (mark == null) {
                source.sendFailure(Component.literal("No corner planted yet - run /tmcity origin where it should stand."));
                return 0;
            }
            String elsewhere = elsewhere(mark, level);
            if (elsewhere != null) {
                source.sendFailure(Component.literal(elsewhere));
                return 0;
            }
            List<Part> parts = parts(folder);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal("No pieces in " + folder + " - is the cut there?"));
                return 0;
            }
            BlockPos middle = Box.of(parts, mark.origin()).middle();
            // On top of whatever is standing in the middle rather than inside it: the middle of a city
            // is usually a wall, and a caller dropped into one is a caller who has to dig out.
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, middle.getX(), middle.getZ()) + 2;
            ServerPlayer player = source.getPlayerOrException();
            player.teleportTo(middle.getX() + 0.5D, top, middle.getZ() + 0.5D);
            BlockPos landed = new BlockPos(middle.getX(), top, middle.getZ());
            source.sendSuccess(() -> Component.literal("Stood on the middle of the city at "
                    + landed.toShortString()), true);
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** Says where the city stands and how much of it is standing. */
    private static int status(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            Mark mark = read(folder);
            List<Part> parts = parts(folder);
            if (mark == null) {
                int waiting = parts.size();
                source.sendSuccess(() -> Component.literal("Nothing planted; " + waiting
                        + " pieces waiting in " + folder), false);
                return 0;
            }
            Box box = Box.of(parts, mark.origin());
            BlockPos middle = box.middle();
            String spoken = box.spoken();
            int band = mark.band();
            int total = bands(parts).size();
            boolean blended = mark.apron();
            source.sendSuccess(() -> Component.literal("City " + spoken + "; middle "
                    + middle.toShortString() + "; band " + band + " of " + total + "; shoulder "
                    + (blended ? "blended" : "not blended")), false);
            String elsewhere = elsewhere(mark, level);
            if (elsewhere != null) source.sendFailure(Component.literal(elsewhere));
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** The complaint a corner planted in another dimension earns, or null when it was planted here. */
    private static String elsewhere(Mark mark, ServerLevel level) {
        String here = level.dimension().identifier().toString();
        if (mark.dimension() == null || mark.dimension().equals(here)) return null;
        return "That corner was planted in " + mark.dimension() + ", not in " + here
                + " - run /tmcity origin here to plant this country's own.";
    }

    /** One piece, with its chunks loaded first: a chunk that is not loaded cannot take blocks. */
    private static void lay(ServerLevel level, Path folder, Part part, BlockPos origin) throws IOException {
        StructureTemplate template = template(level, folder, part.name());
        BlockPos at = origin.offset(part.dx(), part.dy(), part.dz());
        for (int dx = 0; dx < TILE; dx += 16) {
            for (int dz = 0; dz < TILE; dz += 16) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
        template.placeInWorld(level, at, at, new StructurePlaceSettings(), level.getRandom(), 2);
    }

    /** The shoulder of the whole city: its own footprint, its own lowest corner, and the ground as base. */
    private static void skirt(ServerLevel level, List<Part> parts, BlockPos origin) {
        Box box = Box.of(parts, origin);
        int wide = box.highX() - box.lowX() + 1;
        int deep = box.highZ() - box.lowZ() + 1;
        Sites.apron(level, new BlockPos(box.lowX(), box.lowY(), box.lowZ()), wide, deep, RINGS, box.lowY());
    }

    /** A piece's template, read from the cut the first time it is asked for. */
    private static StructureTemplate template(ServerLevel level, Path folder, String name) throws IOException {
        StructureTemplate cached = LOADED.get(name);
        if (cached != null) return cached;
        CompoundTag tag;
        try (InputStream in = Files.newInputStream(folder.resolve(name))) {
            tag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
        StructureTemplate template = new StructureTemplate();
        template.load(level.holderLookup(Registries.BLOCK), tag);
        LOADED.put(name, template);
        return template;
    }

    /** The table of pieces, read in the order they are laid: lowest band first, then a sweep across it. */
    private static List<Part> parts(Path folder) throws IOException {
        List<Part> parts = new ArrayList<>();
        Path table = folder.resolve(PARTS);
        if (!Files.isRegularFile(table)) return parts;
        for (String line : Files.readAllLines(table)) {
            String[] fields = line.trim().split("\\s+");
            if (fields.length < 4) continue;
            try {
                parts.add(new Part(fields[0], Integer.parseInt(fields[1]),
                        Integer.parseInt(fields[2]), Integer.parseInt(fields[3])));
            } catch (NumberFormatException skipped) {
                // A line that is not a piece is not a reason to refuse the rest of the city.
            }
        }
        parts.sort((left, right) -> {
            if (left.dy() != right.dy()) return Integer.compare(left.dy(), right.dy());
            if (left.dz() != right.dz()) return Integer.compare(left.dz(), right.dz());
            return Integer.compare(left.dx(), right.dx());
        });
        return parts;
    }

    /** The heights the pieces sit at, lowest first: one of these is one band of the city. */
    private static List<Integer> bands(List<Part> parts) {
        List<Integer> heights = new ArrayList<>();
        for (Part part : parts) {
            if (!heights.contains(part.dy())) heights.add(part.dy());
        }
        heights.sort(Integer::compareTo);
        return heights;
    }

    /** The marker as it stands, or null in a world that has not been planted. */
    private static Mark read(Path folder) throws IOException {
        Path marker = folder.resolve(MARKER);
        if (!Files.isRegularFile(marker)) return null;
        String[] fields = Files.readString(marker).trim().split("\\s+");
        if (fields.length < 5) return null;
        String dimension = fields.length > 5 ? fields[5] : null;
        return new Mark(new BlockPos(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]),
                Integer.parseInt(fields[2])), Integer.parseInt(fields[3]), "1".equals(fields[4]), dimension);
    }

    private static void write(Path folder, Mark mark) throws IOException {
        Files.createDirectories(folder);
        Files.writeString(folder.resolve(MARKER), mark.origin().getX() + " " + mark.origin().getY()
                + " " + mark.origin().getZ() + " " + mark.band() + " " + (mark.apron() ? 1 : 0)
                + (mark.dimension() == null ? "" : " " + mark.dimension()));
    }

    private static Path folder() {
        return FMLPaths.GAMEDIR.get().resolve(FOLDER);
    }
}
