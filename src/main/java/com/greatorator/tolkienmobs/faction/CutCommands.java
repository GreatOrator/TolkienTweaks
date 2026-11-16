package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The hand for laying a cut that is not the city.
 * <p>
 * {@code /tmcity} stands Rivendell up, and it knows the city by heart: the folder its re-cuts are worked out in, the
 * table its pieces are listed in, the cut in the jar it is read from, and the dressing that settles the valley's ground
 * to its walls are four constants and a name. That is the right shape for a cut the pack ships and the campaign walks
 * through, and the wrong shape for the thing this exists for: any other build, cut the same way into 48-cubes with a
 * {@code .parts} table beside it, put down somewhere to be looked at. Before this, doing that meant renaming a build to
 * {@code rivendell} and dropping it into a copy of the save, which also drags in the drape, the apron and the outflow
 * pass - three things tuned to that one city and answerable to nothing about this one.
 * <p>
 * So this is the plain hand, with five words and no opinion about what it is laying: {@code list}, which says what
 * cuts the world is holding; {@code table}, which reads one and says how big it is without laying a block of it;
 * {@code place}, which lays every piece of one from a corner; {@code clear}, which strips the country out of a cut
 * that is already standing, off its top and out of its box; and {@code drape}, which dresses the ring of ground
 * round it to the cut's own outline. It plants no marker, stands nobody and opens no road - a cut laid with it is
 * exactly the blocks that were cut and the ground dressed to them, which is what makes it usable for asking
 * questions of a map rather than for putting a city in a world.
 * <p>
 * A cut is a folder: the pieces, named as the cutter named them, and a table called {@code <cut>.parts} listing each
 * one's offset and its own size, lowest band first. It is read from the world's own {@code tolkienmobs/cuts} folder, so
 * a build under test can be dropped into a save that travels with it, and from the jar's {@code
 * data/tolkienmobs/structure} beside the city's, so a cut that ships needs nothing beside it. The jar is asked first
 * for the same reason the city's is: a folder left over from an older cut cannot quietly win.
 * <p>
 * The corner is the cut's own low corner, in the frame the pieces were cut against rather than in the frame of whoever
 * cut them: a table that starts its offsets at zero and a table that starts them at minus forty both land their
 * north-west bottom block on the block the caller names. Given no corner, it uses the caller's feet on the ground under
 * them, which is the right answer for a building and the wrong one for a map: a build four hundred blocks across laid
 * at whatever height the caller was standing leaves half of itself under the ground and half of it in the air, so a
 * pinned height is worth typing.
 */
@EventBusSubscriber(modid = MODID)
public final class CutCommands {
    /** Gamemaster-level operators, and anyone building in creative. */
    private static final Predicate<CommandSourceStack> MAY_BUILD = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** Where a world keeps the cuts of its own: one folder per cut, in the save. */
    private static final String FOLDER = "cuts";
    /** Where a cut ships: one folder per cut, beside the city's own, in the jar. */
    private static final String JAR_CUT = "/data/tolkienmobs/structure/";
    /** The table inside a cut's folder that says where each piece stands and how big it is. */
    private static final String TABLE = ".parts";
    /** A piece this wide and this tall is one the cutter wrote no size for. */
    private static final int TILE = 48;
    /** Templates already read this session, keyed by cut and piece, so a cut laid twice is parsed once. */
    private static final Map<String, StructureTemplate> LOADED = new HashMap<>();
    /** A cut's own shape, keyed by cut, so a clearing or a drape is answered once. */
    private static final Map<String, Shape> SHAPES = new HashMap<>();

    private CutCommands() {
    }

    /** One piece: its file, where its corner sits in the frame the cut was taken in, and its own size. */
    record Part(String name, int dx, int dy, int dz, int wide, int deep, int tall) {
        /** A piece out of a cut that did not write its sizes was one whole tile each way. */
        Part(String name, int dx, int dy, int dz) {
            this(name, dx, dy, dz, TILE, TILE, TILE);
        }
    }

    /** The ground a cut covers once its corner is planted, in world coordinates. */
    record Box(int lowX, int lowY, int lowZ, int highX, int highY, int highZ) {
        static Box of(List<Part> parts, BlockPos corner) {
            int lowX = parts.stream().mapToInt(Part::dx).min().orElse(0);
            int lowY = parts.stream().mapToInt(Part::dy).min().orElse(0);
            int lowZ = parts.stream().mapToInt(Part::dz).min().orElse(0);
            int highX = parts.stream().mapToInt(part -> part.dx() + part.wide() - 1).max().orElse(0);
            int highY = parts.stream().mapToInt(part -> part.dy() + part.tall() - 1).max().orElse(0);
            int highZ = parts.stream().mapToInt(part -> part.dz() + part.deep() - 1).max().orElse(0);
            return new Box(corner.getX() + lowX, corner.getY() + lowY, corner.getZ() + lowZ,
                    corner.getX() + highX, corner.getY() + highY, corner.getZ() + highZ);
        }

        /** Whether a position stands on this ground, which is where a village's own weather happens. */
        boolean holds(double x, double y, double z) {
            return x >= lowX && x <= highX + 1 && y >= lowY && y <= highY + 1 && z >= lowZ && z <= highZ + 1;
        }

        /** The box as a sentence, in the order a person reads a map: across, then depth, then height. */
        String spoken() {
            return "x " + lowX + " to " + highX + ", z " + lowZ + " to " + highZ
                    + ", y " + lowY + " to " + highY;
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmcut")
                .requires(MAY_BUILD)
                .then(Commands.literal("list").executes(CutCommands::list))
                .then(Commands.literal("table")
                        .then(Commands.argument("cut", StringArgumentType.word())
                                .executes(CutCommands::table)))
                .then(Commands.literal("place")
                        .then(Commands.argument("cut", StringArgumentType.word())
                                .executes(context -> place(context, null))
                                .then(Commands.argument("corner", BlockPosArgument.blockPos())
                                        .executes(context -> place(context, BlockPosArgument.getBlockPos(context, "corner"))))))
                .then(Commands.literal("clear")
                        .then(Commands.argument("cut", StringArgumentType.word())
                                .executes(context -> clear(context, null))
                                .then(Commands.argument("corner", BlockPosArgument.blockPos())
                                        .executes(context -> clear(context, BlockPosArgument.getBlockPos(context, "corner"))))))
                .then(Commands.literal("drape")
                        .then(Commands.argument("cut", StringArgumentType.word())
                                .executes(context -> drape(context, null))
                                .then(Commands.argument("corner", BlockPosArgument.blockPos())
                                        .executes(context -> drape(context, BlockPosArgument.getBlockPos(context, "corner")))))));
    }

    /** Says which cuts this world is holding: the folders in the save, one cut each. */
    private static int list(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Path folder = folder(source.getLevel());
        List<String> names = new ArrayList<>();
        try {
            if (Files.isDirectory(folder)) {
                try (Stream<Path> walk = Files.list(folder)) {
                    walk.filter(Files::isDirectory)
                            .map(path -> path.getFileName().toString())
                            .sorted()
                            .forEach(names::add);
                }
            }
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The world's cuts could not be read: " + failed.getMessage()));
            return 0;
        }
        if (names.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No cuts in " + folder
                    + " - drop a cut in there as one folder holding its pieces and its .parts table."), false);
            return 0;
        }
        String spoken = String.join(", ", names);
        source.sendSuccess(() -> Component.literal("Cuts in this world: " + spoken
                + ". /tmcut table <cut> reads one, /tmcut place <cut> lays it."), false);
        return names.size();
    }

    /** Reads a cut's table and says how big it is, without laying a block of it. */
    private static int table(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "cut");
        try {
            List<Part> parts = parts(source.getLevel(), name);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal(noTable(source.getLevel(), name)));
                return 0;
            }
            Box box = Box.of(parts, BlockPos.ZERO);
            String spoken = box.spoken();
            int pieces = parts.size();
            int bands = bands(parts).size();
            int deck = deck(source.getLevel(), name);
            source.sendSuccess(() -> Component.literal(name + ": " + pieces + " pieces over " + bands
                    + " bands, " + spoken + " from its own corner, floor " + deck
                    + " over it. /tmcut place " + name + " <x> <y> <z> lays it there."), false);
            return pieces;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Lays every piece of a cut from a corner, lowest band first, in one word.
     * <p>
     * A corner given is the cut's own low corner, and the cut's lowest offsets are taken off it exactly as the city's
     * planting takes them off a pin: a table that starts its offsets at zero lands its north-west bottom block on the
     * block named, and so does one that starts them forty blocks in. A corner not given is the caller's feet at the
     * ground under them, which suits a shed and not a map, so the height is worth typing when the height matters.
     */
    private static int place(CommandContext<CommandSourceStack> context, BlockPos corner) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        String name = StringArgumentType.getString(context, "cut");
        try {
            List<Part> parts = parts(level, name);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal(noTable(level, name)));
                return 0;
            }
            BlockPos pointed = corner == null ? feetOnGround(level, source) : corner;
            BlockPos planted = new BlockPos(pointed.getX() - lowest(parts, Part::dx),
                    pointed.getY() - lowest(parts, Part::dy),
                    pointed.getZ() - lowest(parts, Part::dz));
            Box box = Box.of(parts, planted);
            if (box.highY() >= level.getMaxY()) {
                int tall = box.highY() - box.lowY() + 1;
                int overflow = box.highY() - level.getMaxY() + 1;
                source.sendFailure(Component.literal("That is " + tall + " blocks tall, so " + overflow
                        + " of it stand above the sky and cannot be laid. Place the corner at y "
                        + (level.getMaxY() - tall) + " or lower to stand all of it."));
            }
            for (Part part : parts) {
                lay(level, name, part, planted);
            }
            String spoken = box.spoken();
            int pieces = parts.size();
            source.sendSuccess(() -> Component.literal("Laid " + pieces + " pieces of " + name + ": " + spoken
                    + ". Nothing was dressed, planted or stood - the cut is the blocks that were cut."), true);
            return pieces;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Clears the country out of a cut that is already standing: the hill off its top, and the country's own blocks out
     * of its box.
     * <p>
     * A build laid into Arda arrives interred, and clearing only what stands over it is half a job. The other half is
     * what the country leaves <em>inside</em> the footprint: its ground under a tree's canopy, because the canopy is
     * that column's top and the ground under it is below it; its gravel where an old riverbed reaches the build's own
     * height; a shelf of stone wherever a hillside crosses the build's surface. None of that is above the cut, so a
     * pass that stops at the cut's top never sees it, and it reads as stone standing in a village.
     * <p>
     * So this does both, column by column. Above the cut's own top, everything down to that top comes off - hills, the
     * country's trees, a drawn channel standing in the air over a yard. Then, between the cut's top and its bottom,
     * every position the cut leaves empty has the country taken out of it: the cut's own blocks stay exactly as they
     * were cut, and anything that is not one of them is air. What is below the cut is left alone, because below the
     * cut is the country's own ground and the build stands on it.
     */
    private static int clear(CommandContext<CommandSourceStack> context, BlockPos corner) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        String name = StringArgumentType.getString(context, "cut");
        try {
            List<Part> parts = parts(level, name);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal(noTable(level, name)));
                return 0;
            }
            BlockPos pointed = corner == null ? feetOnGround(level, source) : corner;
            BlockPos planted = plantedAt(parts, pointed);
            Box box = Box.of(parts, planted);
            Shape shape = shape(level, name, parts);
            Cleared done = clearColumns(level, planted, box, shape, 0, shape.wide() * shape.deep());
            source.sendSuccess(() -> Component.literal("Cleared " + name + ": " + done.columns()
                    + " columns cut back to the build, " + done.blocks() + " blocks of the country removed, "
                    + done.deepest() + " deep at the worst."), true);
            return done.columns();
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Dresses the ground round a cut that is already standing to its own outline.
     * <p>
     * The same pass the city gets, for the same reason: a build laid on Arda is a rectangle of one country dropped on
     * another, and its edge is a wall wherever the two do not happen to meet. The drape reads the cut's own outline -
     * the highest block the build puts in each of its columns - and settles the ring of ground outside the footprint
     * to it, cutting down what stands over the outline and filling up what stands under it, easing back into the
     * country over {@code Dressing.DRAPE_REACH}. Water at the build's edge is left alone, because filling beside a
     * river is what closes its mouth.
     */
    private static int drape(CommandContext<CommandSourceStack> context, BlockPos corner) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        String name = StringArgumentType.getString(context, "cut");
        try {
            List<Part> parts = parts(level, name);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal(noTable(level, name)));
                return 0;
            }
            BlockPos pointed = corner == null ? feetOnGround(level, source) : corner;
            BlockPos planted = plantedAt(parts, pointed);
            List<CityCommands.Part> cityParts = cityParts(parts);
            CityCommands.Box cityBox = CityCommands.Box.of(cityParts, planted);
            CityCommands.Drape done = Dressing.drape(level, folder(level).resolve(name),
                    piece -> raw(level, name, piece), cityParts, planted, cityBox, true, deck(level, name));
            int cut = done.cut();
            int filled = done.filled();
            int open = done.open();
            Hobbiton.dressed(level, name, planted);
            source.sendSuccess(() -> Component.literal("Ground dressed round " + name + ": " + cut
                    + " columns cut down, " + filled + " built up, " + open + " left for the water."), true);
            return cut + filled;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** What one sweep of a cut's columns took out: how many columns, how many blocks, and the deepest cut. */
    record Cleared(int columns, long blocks, int deepest) {
    }

    /**
     * Clears a run of a cut's columns: the country off their top, and the country out of the cut's own hollows.
     * <p>
     * A run rather than the whole cut because the same sweep is the world's own: a village raised on the first start
     * of a world is cleared a band of columns a tick. What it does per column is the whole of the rule - above the
     * cut's top everything comes off, and between that top and the cut's bottom every position the cut leaves empty is
     * the country's and goes - so a band of it is a band of the same job and not a second idea about clearing.
     */
    static Cleared clearColumns(ServerLevel level, BlockPos planted, Box box, Shape shape, int first, int upto) {
        int wide = shape.wide();
        int columns = 0;
        int deepest = 0;
        long removed = 0L;
        int last = Math.min(upto, wide * shape.deep());
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        for (int index = Math.max(0, first); index < last; index++) {
            int xi = index % wide;
            int zi = index / wide;
            int x = box.lowX() + xi;
            int z = box.lowZ() + zi;
            int top = shape.top()[index];
            if (top == Integer.MIN_VALUE) continue;
            level.getChunk(x >> 4, z >> 4);
            int ceiling = planted.getY() + top;
            int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            int taken = 0;
            for (int y = surface; y > ceiling; y--) {
                at.set(x, y, z);
                if (level.getBlockState(at).isAir()) continue;
                level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                taken++;
            }
            for (int y = ceiling; y >= planted.getY() + shape.bottom()[index]; y--) {
                if (shape.solid(index, y - planted.getY())) continue;
                at.set(x, y, z);
                if (level.getBlockState(at).isAir()) continue;
                level.setBlock(at, Blocks.AIR.defaultBlockState(), 2);
                taken++;
            }
            if (taken > 0) {
                columns++;
                removed += taken;
                deepest = Math.max(deepest, taken);
            }
        }
        return new Cleared(columns, removed, deepest);
    }

    /** A cut's pieces as the shared drape reads them: the same table, in the city's own shape. */
    static List<CityCommands.Part> cityParts(List<Part> parts) {
        List<CityCommands.Part> city = new ArrayList<>();
        for (Part part : parts) {
            city.add(new CityCommands.Part(part.name(), part.dx(), part.dy(), part.dz(),
                    part.wide(), part.deep(), part.tall()));
        }
        return city;
    }

    /** The corner a cut's own low corner goes on: the block named, less the lowest offsets of its table. */
    private static BlockPos plantedAt(List<Part> parts, BlockPos pointed) {
        return new BlockPos(pointed.getX() - lowest(parts, Part::dx),
                pointed.getY() - lowest(parts, Part::dy),
                pointed.getZ() - lowest(parts, Part::dz));
    }

    /** The caller's feet on the ground under them: the corner a cut is laid at when none is named. */
    private static BlockPos feetOnGround(ServerLevel level, CommandSourceStack source) {
        BlockPos feet = BlockPos.containing(source.getPosition());
        int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, feet.getX(), feet.getZ());
        return new BlockPos(feet.getX(), groundY, feet.getZ());
    }

    /** Where a cut's own blocks are: its top and bottom in each column, and a bit for every block it places. */
    record Shape(int wide, int deep, int tall, int low, int[] top, int[] bottom, long[] solid) {
        boolean solid(int column, int localY) {
            int at = column * tall + (localY - low);
            return at >= 0 && at < solid.length * 64 && (solid[at >> 6] & (1L << (at & 63))) != 0L;
        }
    }

    /** A cut's shape, read out of its files: walked once per cut and kept, since a cut does not change in a session. */
    static Shape shape(ServerLevel level, String cut, List<Part> parts) throws IOException {
        Shape cached = SHAPES.get(cut);
        if (cached != null) return cached;
        int lowX = lowest(parts, Part::dx);
        int lowY = lowest(parts, Part::dy);
        int lowZ = lowest(parts, Part::dz);
        int wide = parts.stream().mapToInt(part -> part.dx() + part.wide() - lowX).max().orElse(0);
        int deep = parts.stream().mapToInt(part -> part.dz() + part.deep() - lowZ).max().orElse(0);
        int tall = parts.stream().mapToInt(part -> part.dy() + part.tall() - lowY).max().orElse(0);
        if (wide <= 0 || deep <= 0 || tall <= 0) {
            return new Shape(0, 0, 0, lowY, new int[0], new int[0], new long[0]);
        }
        int[] top = new int[wide * deep];
        int[] bottom = new int[wide * deep];
        Arrays.fill(top, Integer.MIN_VALUE);
        Arrays.fill(bottom, Integer.MAX_VALUE);
        long[] solid = new long[(wide * deep * tall + 63) >> 6];
        for (Part part : parts) {
            CompoundTag piece = raw(level, cut, part.name());
            // The cut is written one palette to a piece, and a piece of a newer cut may carry several; both shapes are
            // read here because the table says nothing about which one the file has. The blocks are taken straight out
            // of the file rather than through a template, so nothing is placed, lit or loaded to find out its shape.
            ListTag palettes = piece.getList("palettes").orElseGet(ListTag::new);
            if (palettes.size() > 0) {
                for (int i = 0; i < palettes.size(); i++) {
                    CompoundTag palette = palettes.getCompoundOrEmpty(i);
                    heights(palette.getList("palette").orElseGet(ListTag::new),
                            palette.getList("blocks").orElseGet(ListTag::new),
                            part, lowX, lowY, lowZ, wide, deep, tall, top, bottom, solid);
                }
            } else {
                heights(piece.getList("palette").orElseGet(ListTag::new),
                        piece.getList("blocks").orElseGet(ListTag::new),
                        part, lowX, lowY, lowZ, wide, deep, tall, top, bottom, solid);
            }
        }
        for (int i = 0; i < bottom.length; i++) {
            if (bottom[i] == Integer.MAX_VALUE) bottom[i] = Integer.MIN_VALUE;
        }
        Shape shape = new Shape(wide, deep, tall, lowY, top, bottom, solid);
        SHAPES.put(cut, shape);
        return shape;
    }

    /** One palette's blocks: a bit for every position the cut fills, and the column's own span. */
    private static void heights(ListTag palette, ListTag blocks, Part part, int lowX, int lowY, int lowZ,
                                int wide, int deep, int tall, int[] top, int[] bottom, long[] solid) {
        String[] names = new String[palette.size()];
        for (int i = 0; i < palette.size(); i++) {
            names[i] = palette.getCompoundOrEmpty(i).getStringOr("Name", "");
        }
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompoundOrEmpty(i);
            int state = block.getIntOr("state", 0);
            if (state < 0 || state >= names.length) continue;
            String name = names[state];
            if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
            ListTag pos = block.getList("pos").orElseGet(ListTag::new);
            if (pos.size() < 3) continue;
            int x = part.dx() - lowX + pos.getIntOr(0, 0);
            int y = part.dy() - lowY + pos.getIntOr(1, 0);
            int z = part.dz() - lowZ + pos.getIntOr(2, 0);
            if (x < 0 || x >= wide || z < 0 || z >= deep || y < 0 || y >= tall) continue;
            int column = z * wide + x;
            int at = column * tall + y;
            solid[at >> 6] |= 1L << (at & 63);
            if (y > top[column]) top[column] = y;
            if (y < bottom[column]) bottom[column] = y;
        }
    }

    /** One file of a cut as the NBT it was written as. */
    static CompoundTag raw(ServerLevel level, String cut, String piece) throws IOException {
        try (InputStream in = open(level, cut, piece)) {
            if (in == null) throw new IOException("no " + piece + " in the " + cut + " cut");
            return NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
    }

    /** The lowest a table's offsets go on one axis. */
    private static int lowest(List<Part> parts, ToIntFunction<Part> axis) {
        return parts.stream().mapToInt(axis).min().orElse(0);
    }

    /** One piece, with its chunks made first: a chunk that is not loaded cannot take blocks. */
    static void lay(ServerLevel level, String cut, Part part, BlockPos origin) throws IOException {
        StructureTemplate template = template(level, cut, part.name());
        BlockPos at = origin.offset(part.dx(), part.dy(), part.dz());
        for (int dx = 0; dx < part.wide(); dx += 16) {
            for (int dz = 0; dz < part.deep(); dz += 16) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
        // The piece is clipped to the sky it stands under rather than trusted to fit it, which is the same clip the
        // city's own laying makes and for the same reason: a block written above the ceiling is dropped without a word,
        // and its block entity is not.
        if (at.getY() >= level.getMaxY()) return;
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setBoundingBox(new BoundingBox(at.getX(), Math.max(at.getY(), level.getMinY()), at.getZ(),
                at.getX() + part.wide() - 1, Math.min(at.getY() + part.tall() - 1, level.getMaxY() - 1),
                at.getZ() + part.deep() - 1));
        // Flags 2|16: tell the clients, and do not run the shape updates. A cut is a build and not a circuit, and
        // every shape update asked the level for the neighbour it stood against - a chunk generation per block at
        // the edge of an ungenerated drawing, and what held one server tick for forty-nine seconds when the
        // village was first raised.
        template.placeInWorld(level, at, at, settings, level.getRandom(), 2 | 16);
    }

    /** A piece's template, read from the cut the first time it is asked for. */
    private static StructureTemplate template(ServerLevel level, String cut, String piece) throws IOException {
        String key = cut + "/" + piece;
        StructureTemplate cached = LOADED.get(key);
        if (cached != null) return cached;
        CompoundTag tag;
        try (InputStream in = open(level, cut, piece)) {
            if (in == null) throw new IOException("no " + piece + " in the " + cut + " cut");
            tag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
        StructureTemplate template = new StructureTemplate();
        template.load(level.holderLookup(Registries.BLOCK), tag);
        LOADED.put(key, template);
        return template;
    }

    /** One file of a cut, from the jar first and the world's own folder second, or nothing at all. */
    private static InputStream open(ServerLevel level, String cut, String file) throws IOException {
        InputStream packed = CutCommands.class.getResourceAsStream(JAR_CUT + cut + "/" + file);
        if (packed != null) return packed;
        Path loose = folder(level).resolve(cut).resolve(file);
        return Files.isRegularFile(loose) ? Files.newInputStream(loose) : null;
    }

    /**
     * How high a cut's own floor stands over the corner it is planted on, in blocks.
     * <p>
     * The drape settles the ring outside a build to the build's outline, and a build's floor is not its roofline: a
     * column whose top is a deck is ground the country meets, and one whose top is a wall is ground the country is cut
     * down to. The city carries the number as a constant of its own, which is right for a floor two over its corner;
     * a village cut out of a hillside has its floor sixteen up, and dressed to the city's number every column outside
     * its walls reads as a roofline. So a cut says what its floor is, in its own table, as a line of its own:
     * <p>
     * <pre>{@code
     * deck 16
     * }</pre>
     * <p>
     * A cut that says nothing gets {@link CityCommands#DECK_LIFT}, which is what every cut laid so far means.
     */
    static int deck(ServerLevel level, String cut) {
        try (InputStream in = open(level, cut, cut + TABLE)) {
            if (in == null) return CityCommands.DECK_LIFT;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            for (String line : reader.lines().toList()) {
                String[] fields = line.trim().split("\\s+");
                if (fields.length < 2 || !"deck".equalsIgnoreCase(fields[0])) continue;
                return Math.max(0, Integer.parseInt(fields[1]));
            }
        } catch (IOException | NumberFormatException unreadable) {
            // A table that cannot be read, or a `deck` line that is not a number, is not a reason to dress the ring to
            // the wrong line: the city's answer is the one every cut used before this line existed.
        }
        return CityCommands.DECK_LIFT;
    }

    /**
     * What this cut is, in one word: its pieces and their contents, hashed.
     * <p>
     * The note beside a standing village records this so a world can tell a cut that has changed since from one that
     * has not - a re-cut, a piece added, a block moved, or a table that differs only in its `deck` line all answer
     * differently, and a world holding an older village then raises it again over what is there. Reading the fourteen
     * megabytes of a village costs a moment once per start, which is the price of the answer being the cut's and not a
     * version number somebody has to remember to type.
     *
     * @return the piece count and the hash of every piece, or what was wrong with reading them
     */
    static String fingerprint(ServerLevel level, String cut) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        try (InputStream table = open(level, cut, cut + TABLE)) {
            if (table == null) return "none";
            byte[] buffer = new byte[8192];
            List<String> pieces = new ArrayList<>();
            for (String line : new BufferedReader(new InputStreamReader(table, StandardCharsets.UTF_8))
                    .lines().toList()) {
                String[] fields = line.trim().split("\\s+");
                if (fields.length >= 4 && !line.isBlank()) pieces.add(fields[0]);
                crc.update(line.trim().getBytes(StandardCharsets.UTF_8));
            }
            for (String piece : pieces) {
                try (InputStream in = open(level, cut, piece)) {
                    if (in == null) return "missing";
                    int read;
                    while ((read = in.read(buffer)) > 0) crc.update(buffer, 0, read);
                }
            }
            return pieces.size() + "-" + Long.toHexString(crc.getValue());
        } catch (IOException unreadable) {
            return "unreadable";
        }
    }

    /** A cut's table, read in the order it is laid: lowest band first, then a sweep across it. */
    static List<Part> parts(ServerLevel level, String cut) throws IOException {
        List<Part> parts = new ArrayList<>();
        try (InputStream in = open(level, cut, cut + TABLE)) {
            if (in == null) return parts;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            for (String line : reader.lines().toList()) {
                String[] fields = line.trim().split("\\s+");
                if (fields.length < 4) continue;
                try {
                    parts.add(new Part(fields[0], Integer.parseInt(fields[1]),
                            Integer.parseInt(fields[2]), Integer.parseInt(fields[3]),
                            size(fields, 4), size(fields, 5), size(fields, 6)));
                } catch (NumberFormatException skipped) {
                    // A line that is not a piece is not a reason to refuse the rest of the cut.
                }
            }
        }
        parts.sort((left, right) -> {
            if (left.dy() != right.dy()) return Integer.compare(left.dy(), right.dy());
            if (left.dz() != right.dz()) return Integer.compare(left.dz(), right.dz());
            return Integer.compare(left.dx(), right.dx());
        });
        return parts;
    }

    /** A size out of the table, or a whole tile when the line does not carry one. */
    private static int size(String[] fields, int at) {
        if (fields.length <= at) return TILE;
        try {
            return Math.max(1, Math.min(TILE, Integer.parseInt(fields[at])));
        } catch (NumberFormatException notANumber) {
            return TILE;
        }
    }

    /** The heights the pieces sit at, lowest first: one of these is one band of the cut. */
    private static List<Integer> bands(List<Part> parts) {
        List<Integer> heights = new ArrayList<>();
        for (Part part : parts) {
            if (!heights.contains(part.dy())) heights.add(part.dy());
        }
        heights.sort(Integer::compareTo);
        return heights;
    }

    /** What to say when a cut is not where it was asked for. */
    private static String noTable(ServerLevel level, String cut) {
        return "No cut named " + cut + " - no " + cut + TABLE + " in " + folder(level).resolve(cut)
                + " nor in the jar's " + JAR_CUT + cut + ". /tmcut list says what this world is holding.";
    }

    /** This world's own folder for cuts: the save's, because a cut laid in a world belongs to that world. */
    static Path folder(ServerLevel level) {
        return level.getServer().getWorldPath(LevelResource.ROOT).resolve(MODID).resolve(FOLDER);
    }
}
