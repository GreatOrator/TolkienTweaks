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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
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
import java.util.Arrays;
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
 * {@code /tmcity}, with nine hands: {@code origin}, which plants the corner where the caller stands;
 * {@code summon}, which plants it, lays the whole city in one word and stands its people, for putting the
 * build alone into an empty world; {@code folk}, which stands the city's people where the build marks
 * them; {@code band [count]}, which lays the next band of the city and stops, because a whole
 * city is two thousand chunks and those are generated chunk by chunk; {@code relay}, which puts the counter back at
 * the bottom of the city it already stands in; {@code apron}, which blends the shoulder into the ground
 * once the last band is down; {@code drape}, which cuts and fills the ground outside the walls to
 * the city's own outline; {@code goto}, which stands the caller on the middle of the city; and
 * {@code status}, which says how much is standing.
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
    /** The people the build marks out for itself: a name or none, and a step from the corner it was cut at. */
    private static final String FOLK = "rivendell.npcs";
    /** A piece is this wide and this tall, which is also the height of one band. */
    private static final int TILE = 48;
    /** How deep under the lowest band the ground is filled, in blocks. */
    private static final int UNDERPIN = 24;
    /**
     * How far out from the city's own footprint the ground is dressed to it, in blocks.
     * <p>
     * The massif is drawn by a height function that has never heard of the build, so where it meets the
     * city it is whatever the rules happened to make: rock above the roofline at one wall, a channel cut
     * through the mountain at another, and a seam where a river's bank stopped applying four blocks from
     * a wall. This pass makes the ground answer to the build instead - cut down to the city's own outline
     * and filled up to it - and fades back into the drawn country over this distance, so neither end of
     * it is a step.
     */
    private static final int DRAPE_REACH = 32;
    /**
     * The height at or under which the build's own top is a deck and not a wall. The city's floor is at 74
     * and its deck tiles lie on 75, so a column that tops out here has nothing built over it: the rock
     * standing above such a column is the roofline over the city, not a wall at its edge.
     */
    private static final int DECK_TOP = 76;
    /**
     * How far over the city's floor water at the build's edge may stand and still be its outflow. The city
     * drains at its floor, where the branch takes the water eight blocks over it; water at the edge above that
     * is a spring held up in the build, and the ground outside it has to hold the water in.
     */
    private static final int OUTLET_LIFT = 24;

    /** What a drape did: columns cut down, columns built up, and columns left for the water. */
    private record Drape(int cut, int filled, int open) {
    }
    /** Templates already read this session, so the ten megabytes are parsed once. */
    private static final Map<String, StructureTemplate> LOADED = new HashMap<>();
    /** The corner last read out of the marker, and the dimension it was read for. */
    private static BlockPos PLANTED;
    private static String PLANTED_IN;

    /** One piece: its file, and where its corner sits in the frame the cut was taken in. */
    private record Part(String name, int dx, int dy, int dz, int wide, int deep, int tall) {
        /** A piece out of a cut that did not write its sizes was one whole tile each way. */
        Part(String name, int dx, int dy, int dz) {
            this(name, dx, dy, dz, TILE, TILE, TILE);
        }
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
            int highX = parts.stream().mapToInt(part -> part.dx() + part.wide() - 1).max().orElse(0);
            int highY = parts.stream().mapToInt(part -> part.dy() + part.tall() - 1).max().orElse(0);
            int highZ = parts.stream().mapToInt(part -> part.dz() + part.deep() - 1).max().orElse(0);
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
                .then(Commands.literal("summon").executes(CityCommands::summon))
                .then(Commands.literal("folk").executes(CityCommands::folk))
                .then(Commands.literal("band")
                        .executes(context -> band(context, 1))
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 8))
                                .executes(context -> band(context, IntegerArgumentType.getInteger(context, "count")))))
                .then(Commands.literal("relay").executes(CityCommands::relay))
                .then(Commands.literal("apron").executes(CityCommands::apron))
                .then(Commands.literal("drape").executes(CityCommands::drape))
                .then(Commands.literal("goto").executes(CityCommands::visit))
                .then(Commands.literal("status").executes(CityCommands::status)));
    }

    /**
     * Stands the city's people where the build marks them: the three Elves of the line at the spots set
     * down for them, the fifteen unnamed walkers at theirs, and the rest of the crowd wherever the city has
     * ground under it. A body counts by its tag, so saying it twice stands nobody twice.
     */
    private static int folk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            Mark mark = read(folder);
            if (mark == null) {
                source.sendFailure(Component.literal("No corner planted yet - run /tmcity summon where it should stand."));
                return 0;
            }
            String elsewhere = elsewhere(mark, level);
            if (elsewhere != null) {
                source.sendFailure(Component.literal(elsewhere));
                return 0;
            }
            List<Campaign.FolkSpot> spots = folkSpots(folder);
            int stood = spots.isEmpty() ? Campaign.standCityFolk(level, mark.origin())
                    : Campaign.standCityFolk(level, mark.origin(), spots);
            source.sendSuccess(() -> Component.literal("Stood " + stood + " of the city's folk"
                    + (stood == 0 ? " - every mark already has its body." : ".")), true);
            return stood;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The city could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /** The city's people as the build's own table gives them, or nothing when the cut carries no table. */
    private static List<Campaign.FolkSpot> folkSpots(Path folder) throws IOException {
        List<Campaign.FolkSpot> spots = new ArrayList<>();
        Path table = folder.resolve(FOLK);
        if (!Files.isRegularFile(table)) return spots;
        for (String line : Files.readAllLines(table)) {
            String[] fields = line.trim().split("\s+");
            if (fields.length < 4) continue;
            try {
                String name = "-".equals(fields[0]) ? "" : fields[0];
                spots.add(new Campaign.FolkSpot(name, Integer.parseInt(fields[1]),
                        Integer.parseInt(fields[2]), Integer.parseInt(fields[3])));
            } catch (NumberFormatException skipped) {
                // A line that is not a body is not a reason to leave the rest of them unstood.
            }
        }
        return spots;
    }

    /**
     * Where the city was planted in this world, or null when nothing is planted here.
     * <p>
     * This is what lets the campaign dress a city it did not build: the marker holds the corner the
     * cut was laid from and the dimension it was laid in, so a body whose visit finds no city here is
     * told so rather than walked to a frame that belongs to another country.
     */
    static BlockPos plantedCorner(ServerLevel level) {
        // Read once and kept: this is asked on the tick, whenever a player is near the valley, and a file
        // read a second is not what walking into Rivendell should cost. Every write to the marker clears
        // it, so a city planted or re-cut by hand is picked up on the next visit either way.
        String here = level.dimension().identifier().toString();
        if (here.equals(PLANTED_IN)) return PLANTED;
        try {
            Mark mark = read(folder());
            boolean mine = mark != null && (mark.dimension() == null || mark.dimension().equals(here));
            PLANTED = mine ? mark.origin() : null;
        } catch (IOException unreadable) {
            PLANTED = null;
        }
        PLANTED_IN = here;
        return PLANTED;
    }

    /** The city's people as the build's own table gives them, for a visit that dresses a planted city. */
    static List<Campaign.FolkSpot> plantedFolk() {
        try {
            return folkSpots(folder());
        } catch (IOException unreadable) {
            return List.of();
        }
    }

    /** Plants the city's corner at the caller's feet, and forgets whatever was standing before. */
    private static int origin(CommandContext<CommandSourceStack> context) {
        return plant(context.getSource()) ? 1 : 0;
    }

    /**
     * Plants the city where the caller stands and lays every band of it at once, in whatever country
     * they are in.
     * <p>
     * {@code origin} and {@code band} are the pair for building a city by hand, one band at a time,
     * because two thousand chunks of blocks is a stall the server sits through. This is the one word for
     * putting the whole city down: the same planting, then every band that is left, then the shoulder.
     * It is for making a world that holds the build and nothing else, where the build can be looked at
     * without the ground around it arguing about what is missing.
     */
    private static int summon(CommandContext<CommandSourceStack> context) {
        if (!plant(context.getSource())) return 0;
        int laid = band(context, 99);
        // A city laid and left empty of its people is half a city, so the crowd is stood with it.
        if (laid > 0) folk(context);
        return laid;
    }

    /** Plants the corner and writes the marker, saying why not when it cannot. */
    private static boolean plant(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        Path folder = folder();
        try {
            List<Part> parts = parts(folder);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal("No pieces in " + folder + " - is the cut there?"));
                return false;
            }
            // The city's lowest corner goes under the caller's feet, so the band that carries the ground
            // lands where they are standing and the rest of the city rises from there.
            int lowX = parts.stream().mapToInt(Part::dx).min().orElse(0);
            int lowY = parts.stream().mapToInt(Part::dy).min().orElse(0);
            int lowZ = parts.stream().mapToInt(Part::dz).min().orElse(0);
            BlockPos feet = BlockPos.containing(source.getPosition());
            // The city stands on the ground under its own corner, not at whatever height the caller happens
            // to be at: a corner planted from the air leaves the whole build hanging over the valley with a
            // gap under it, and the ground flowing away from it in every direction.
            int cornerX = feet.getX();
            int cornerZ = feet.getZ();
            int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, cornerX, cornerZ);
            // In the valley the corner may already be standing on the hillside the city is laid into, so
            // the ground under it is three blocks up the climb and planting on that would stand the whole
            // build a dozen blocks in the air. The city's floor is the valley's floor, and that is the cap.
            if (TolkienDimensions.ARDA.equals(level.dimension())) {
                groundY = Math.min(groundY, ArdaMap.valleyFloor() + 1);
            }
            BlockPos planted = new BlockPos(cornerX - lowX, groundY - lowY, cornerZ - lowZ);
            // In the valley the pin is where the ground was scouted, so the pin decides where the city
            // stands and the caller's feet decide it everywhere else. A corner planted a few blocks out
            // puts the whole build a few blocks out, and which block the caller happened to stand on is
            // not a decision worth leaving to chance.
            BlockPos pin = TolkienDimensions.ARDA.equals(level.dimension())
                    ? Pins.at(level, Campaign.SUMMON_PLACE)
                    : null;
            if (pin != null) {
                // The pin is the cut's own low corner rather than the middle of the city: the scout wrote
                // down the block the corner goes on, so the corner is what the pin means.
                planted = new BlockPos(pin.getX() - lowX, pin.getY() - lowY, pin.getZ() - lowZ);
            }
            // The marked spots are written in this same frame, so the folk know where they are the moment
            // the corner is planted - otherwise the city would stand empty of the people meant to live in it.
            Campaign.setValleyOrigin(planted);
            write(folder, new Mark(planted, 0, false, level.dimension().identifier().toString()));
            Box box = Box.of(parts, planted);
            String spoken = box.spoken();
            int bands = bands(parts).size();
            int pieces = parts.size();
            source.sendSuccess(() -> Component.literal("City planted at " + spoken
                    + (pin == null ? "" : ", corner on the pin")
                    + "; " + bands + " bands, " + pieces + " pieces. Run /tmcity band to lay it, /tmcity goto to stand in it."), true);
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
            return true;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return false;
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
            // Which bands this call actually lays, so the answer can name them: the count is how many
            // bands to lay, not which one, and a caller who reads it the other way - band 1, band 2,
            // band 3 - lays six bands in three calls and then finds the last three have nothing to do.
            int from = mark.band();
            for (int placed = 0; placed < count && mark.band() < bands.size(); placed++) {
                int height = bands.get(mark.band());
                for (Part part : parts) {
                    if (part.dy() != height) continue;
                    BlockPos at = mark.origin().offset(part.dx(), part.dy(), part.dz());
                    // A piece that would stand above the sky is not laid at all, and is counted rather
                    // than reported as laid: the world drops a block above its own ceiling without a
                    // word, so the old count said "laid" for pieces that put nothing anywhere.
                    if (at.getY() + part.tall() - 1 > level.getMaxY() || at.getY() < level.getMinY()) {
                        over++;
                        continue;
                    }
                    lay(level, folder, part, mark.origin());
                    // The lowest band is the city's floor, so it is the one that needs ground under it.
                    if (mark.band() == 0) Sites.underpin(level, at, part.wide(), part.deep(), UNDERPIN);
                    laid++;
                }
                mark = new Mark(mark.origin(), mark.band() + 1, mark.apron(), mark.dimension());
                // The mark is written band by band rather than once at the end: a run that stops partway -
                // a relog, a crash, a stall long enough to be taken for one - then resumes at the band it
                // reached instead of being redone from the bottom, and what is on the ground matches the
                // number the next call reads.
                write(folder, mark);
                com.mojang.logging.LogUtils.getLogger().info("Rivendell: band {} of {} is down",
                        mark.band(), bands.size());
            }
            // The ground is not walked out from the walls any more. The mountain's foot is drawn inside
            // the city's own footprint (see ArdaMap's valley), so the lowest band is laid into rising rock
            // and a skirt cut round it would take away exactly the rock the build is meant to sit in.
            // /tmcity apron is still there, for a city planted on ground that does want bedding in.
            boolean finished = mark.band() >= bands.size();
            // A city is only half standing if the mountain it was cut into is still arguing with it, so
            // the ground is dressed the moment the last band is down rather than by a second word.
            final Drape dressed = finished
                    ? drapeOrSayWhy(level, folder, parts, mark.origin(), Box.of(parts, mark.origin()))
                    : null;
            write(folder, mark);
            int soFar = laid;
            int skipped = over;
            boolean whole = finished;
            int total = bands.size();
            int first = Math.min(from, total - 1) + 1;
            int last = Math.min(mark.band(), total);
            if (soFar == 0 && skipped == 0) {
                source.sendSuccess(() -> Component.literal("Nothing left to lay - all " + total
                        + " bands are down. /tmcity goto stands you in the middle of it."), true);
                return 0;
            }
            int left = Math.max(0, total - last);
            String which = last <= first ? "band " + first + " of " + total
                    : "bands " + first + "-" + last + " of " + total;
            String sky = skipped == 0 ? "" : "; " + skipped
                    + " were above the sky and left out - /tmcity origin planted lower would take them";
            // How many more bands are waiting, so nobody has to be told what to type next.
            String spoken = whole ? "; the whole city is down - /tmcity goto to stand in it"
                    : "; " + left + (left == 1 ? " band" : " bands") + " to go";
            String dressedUp = dressed == null ? ""
                    : "; the ground was dressed to the city's own outline: " + dressed.cut()
                    + " columns cut down, " + dressed.filled() + " filled up, " + dressed.open()
                    + " left for the water";
            source.sendSuccess(() -> Component.literal("Laid " + soFar + " pieces; " + which
                    + " down" + sky + spoken + dressedUp), true);
            return laid;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Lays the city again where it already stands, without planting a new corner.
     * <p>
     * A corner is planted once and the bands walk up from it, so a piece re-cut after the city was laid
     * is never laid at all: the counter is at the top and has nothing left to give. This puts the counter
     * back at the bottom and keeps the corner, which is all a re-lay needs - a piece laid over itself
     * writes the same blocks to the same places, so nothing is doubled, and a cut that gained blocks
     * fills them in. What it cannot do is take blocks away: a piece that lost some leaves those standing.
     */
    private static int relay(CommandContext<CommandSourceStack> context) {
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
            write(folder, new Mark(mark.origin(), 0, false, mark.dimension()));
            Campaign.setValleyOrigin(mark.origin());
            Box box = Box.of(parts, mark.origin());
            String spoken = box.spoken();
            int count = bands(parts).size();
            int pieces = parts.size();
            source.sendSuccess(() -> Component.literal("Same corner, back to the bottom: " + spoken
                    + "; " + count + " bands, " + pieces + " pieces. Run /tmcity band " + count
                    + " to lay it over what is already standing."), true);
            return 1;
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
            Sites.Apron done = skirt(level, parts, mark.origin());
            Sites.Spillway water = waterline(level, parts, mark.origin());
            write(folder, new Mark(mark.origin(), mark.band(), true, mark.dimension()));
            // The count is the point of the report: a shoulder that moved nothing at all is a city that
            // reads as a block cut out of the ground, and no message that only says it ran would show that.
            source.sendSuccess(() -> Component.literal("Shoulder walked out from the walls: "
                    + done.cut() + " columns cut down, " + done.filled() + " built up, " + done.left()
                    + " already standing on its line. Water line: " + water.open() + " outlets, "
                    + water.filled() + " columns built out under the ones that stand high."), true);
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
        for (int dx = 0; dx < part.wide(); dx += 16) {
            for (int dz = 0; dz < part.deep(); dz += 16) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
        template.placeInWorld(level, at, at, new StructurePlaceSettings(), level.getRandom(), 2);
    }

    /** The water line of the whole city: the same box the shoulder is measured on. */
    private static Sites.Spillway waterline(ServerLevel level, List<Part> parts, BlockPos origin) {
        Box box = Box.of(parts, origin);
        int wide = box.highX() - box.lowX() + 1;
        int deep = box.highZ() - box.lowZ() + 1;
        return Sites.waterline(level, new BlockPos(box.lowX(), box.lowY(), box.lowZ()), wide, deep);
    }

    /** The shoulder of the whole city: its own footprint, its own lowest corner, and the ground as base. */
    private static Sites.Apron skirt(ServerLevel level, List<Part> parts, BlockPos origin) {
        Box box = Box.of(parts, origin);
        int wide = box.highX() - box.lowX() + 1;
        int deep = box.highZ() - box.lowZ() + 1;
        return Sites.apron(level, new BlockPos(box.lowX(), box.lowY(), box.lowZ()), wide, deep,
                box.lowY());
    }

    /** Dresses the ground to the city's own outline, saying so in the log when it cannot rather than losing the band. */
    private static Drape drapeOrSayWhy(ServerLevel level, Path folder, List<Part> parts, BlockPos origin, Box box) {
        try {
            return drape(level, folder, parts, origin, box);
        } catch (IOException failed) {
            com.mojang.logging.LogUtils.getLogger().warn("Rivendell: the ground was not dressed: {}",
                    failed.getMessage());
            return null;
        }
    }

    /** Cuts and fills the ring of ground outside the city's footprint to the build's own outline. */
    private static int drape(CommandContext<CommandSourceStack> context) {
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
            Drape done = drape(level, folder, parts, mark.origin(), Box.of(parts, mark.origin()));
            source.sendSuccess(() -> Component.literal("Ground dressed to the city's own outline: "
                    + done.cut() + " columns cut down, " + done.filled() + " built up, " + done.open()
                    + " left for the water."), true);
            return done.cut() + done.filled();
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The cut could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Cuts and fills the ground round the city until it meets the build's own outline.
     * <p>
     * The outline is read from the cut rather than from the world: every piece's template is read, and the
     * highest block of the build in each column is that column's outline. Reading the world instead would
     * measure whatever the city is standing in - the rock it was laid into - and a drape drawn from that
     * would agree with the mistake it is there to remove.
     * <p>
     * Each column outside the footprint is capped at its nearest outline column where the wall is, easing
     * to what the ground already stands at {@link #DRAPE_REACH} out: the join at the wall is the build's
     * shape and the join in the country is the country's. Above the cap is cut away and below it is
     * filled, so a notch the river's corridor opened behind the walls is closed and a shelf the mountain
     * left standing over the roofline comes down.
     * <p>
     * Water at the build's own edge is left exactly as it is where it is the city's outflow - the channel the
     * river runs in - because filling beside a river is what closes its mouth, and the mouth is the one thing
     * the valley was drawn round. Water at the edge standing above {@link #OUTLET_LIFT} over the floor is a
     * spring held up in the build: the ground outside is banked to its surface and never cut, so the pool is
     * held in rather than fed out down the flank. Water out in the ring is neither: it is a pool lying on the
     * drawn ground, and the pass works on the ground under it, cutting from the pool's surface so the water
     * goes with the ground beneath it.
     * <p>
     * Where the build's own edge is a deck and not a wall - its floor tiles at 75, with nothing built over
     * them - the ground outside is carried to the rock standing over that deck inside the box and is never
     * cut. A deck is not a roofline: cutting a mountain down to it opened a trench two hundred blocks deep
     * round every face the build leaves open, and the wall of that trench was the mountain's own shape -
     * which is exactly what a wall round the city must not be.
     */
    private static Drape drape(ServerLevel level, Path folder, List<Part> parts, BlockPos origin, Box box)
            throws IOException {
        int wide = box.highX() - box.lowX() + 1;
        int deep = box.highZ() - box.lowZ() + 1;
        int[] outline = new int[wide * deep];
        Arrays.fill(outline, Integer.MIN_VALUE);
        int[] watered = new int[wide * deep];
        Arrays.fill(watered, Integer.MIN_VALUE);
        for (Part part : parts) {
            CompoundTag tag = raw(folder, part.name());
            ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
            String[] names = new String[palette.size()];
            for (int i = 0; i < palette.size(); i++) {
                names[i] = palette.getCompoundOrEmpty(i).getStringOr("Name", "");
            }
            ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompoundOrEmpty(i);
                int state = block.getIntOr("state", 0);
                if (state < 0 || state >= names.length) continue;
                String name = names[state];
                if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
                ListTag pos = block.getList("pos").orElseGet(ListTag::new);
                if (pos.size() < 3) continue;
                int gx = origin.getX() + part.dx() + pos.getIntOr(0, 0) - box.lowX();
                int gz = origin.getZ() + part.dz() + pos.getIntOr(2, 0) - box.lowZ();
                if (gx < 0 || gx >= wide || gz < 0 || gz >= deep) continue;
                int at = gx + gz * wide;
                int y = origin.getY() + part.dy() + pos.getIntOr(1, 0);
                if (y > outline[at]) outline[at] = y;
                if (name.contains("water")) watered[at] = Math.max(watered[at], y);
            }
        }
        // A wall with a doorway in it is still a wall: a column the build leaves empty takes the height of
        // the columns beside it, so the drape follows the shape of the city rather than its holes.
        for (int pass = 0; pass < 2; pass++) {
            int[] grown = outline.clone();
            for (int gz = 0; gz < deep; gz++) {
                for (int gx = 0; gx < wide; gx++) {
                    int at = gx + gz * wide;
                    if (outline[at] != Integer.MIN_VALUE) continue;
                    int best = Integer.MIN_VALUE;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            int nx = gx + dx;
                            int nz = gz + dz;
                            if (nx < 0 || nx >= wide || nz < 0 || nz >= deep) continue;
                            best = Math.max(best, outline[nx + nz * wide]);
                        }
                    }
                    grown[at] = best;
                }
            }
            outline = grown;
        }
        // A column is a watercourse only where the water is what stands on top of it - a channel the river
        // can run in. Water buried in the build is not an outlet: the spring's aqueduct is under a roof at
        // 239, and reading it as one left a hole in the draping in front of the wall it runs in.
        boolean[] channel = new boolean[wide * deep];
        for (int i = 0; i < channel.length; i++) {
            channel[i] = outline[i] != Integer.MIN_VALUE && watered[i] == outline[i];
        }
        // Every chunk the ring touches is loaded first: a block written into a chunk that is not loaded is
        // dropped without a word, which is the same fault the city's own pieces are loaded for.
        for (int chunkX = (box.lowX() - DRAPE_REACH) >> 4; chunkX <= (box.highX() + DRAPE_REACH) >> 4; chunkX++) {
            for (int chunkZ = (box.lowZ() - DRAPE_REACH) >> 4; chunkZ <= (box.highZ() + DRAPE_REACH) >> 4; chunkZ++) {
                level.getChunk(chunkX, chunkZ);
            }
        }
        int cut = 0;
        int filled = 0;
        int open = 0;
        for (int x = box.lowX() - DRAPE_REACH; x <= box.highX() + DRAPE_REACH; x++) {
            for (int z = box.lowZ() - DRAPE_REACH; z <= box.highZ() + DRAPE_REACH; z++) {
                if (x >= box.lowX() && x <= box.highX() && z >= box.lowZ() && z <= box.highZ()) continue;
                int gx = Mth.clamp(x, box.lowX(), box.highX()) - box.lowX();
                int gz = Mth.clamp(z, box.lowZ(), box.highZ()) - box.lowZ();
                int at = gx + gz * wide;
                int wall = outline[at];
                if (wall == Integer.MIN_VALUE) continue;
                // Whether the ground outside may only be built up here and never cut. Two edges answer so: a
                // deck, which is a floor tile rather than a roofline, and a spring, whose water the ground
                // outside has to hold rather than drain.
                boolean banked = wall <= DECK_TOP;
                if (banked) {
                    // Where the build's edge is a deck and not a wall, the ground outside is made to meet the
                    // rock standing over that deck inside the box, and is never cut down to the deck itself.
                    // The deck sits at 75 under a roofline at 318: cutting to it opened a trench the depth of
                    // the mountain round every face the build leaves open, and the face of that trench was the
                    // mountain's own shape. The mountain at a deck's back is the city's back wall, not a peak.
                    int inside = gx + box.lowX();
                    int insideZ = gz + box.lowZ();
                    wall = Math.max(wall, level.getHeight(Heightmap.Types.MOTION_BLOCKING, inside, insideZ) - 1);
                }
                if (channel[at]) {
                    if (watered[at] - (box.lowY() - 1) <= OUTLET_LIFT) {
                        // The city's outflow: the branch takes the water out at the floor, so the ground beside
                        // it is left exactly as the cut left it and the mouth stays open.
                        open++;
                        continue;
                    }
                    // Water standing this high in the build is a spring held up in it, not its outflow. The
                    // ground outside is banked to the water's own surface and never cut, so the pool is held
                    // in the build instead of finding a way out down the back of it - which is what cutting
                    // the ring to a spring's edge did: a false river down the flank, fed from the top.
                    banked = true;
                }
                int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
                if (top <= level.getMinY()) continue;
                // Everything standing in the column, water included: a pool is cut away with the ground it
                // lies on, and the cut starts at the pool's own surface so no water is left over air.
                int surface = top;
                BlockState skin = level.getBlockState(new BlockPos(x, top, z));
                if (!skin.getFluidState().isEmpty()) {
                    // Water at the build's own edge is its channel, and was answered above. Water out here is a
                    // pool lying on the drawn ground - a spring's basin, a hollow that holds the rain - so the
                    // pass reads down to the ground under it. Skipping the column instead left a pillar of
                    // ground standing in the ring wherever a single puddle reached the top of a column.
                    while (top > level.getMinY() + 1
                            && !level.getBlockState(new BlockPos(x, top, z)).getFluidState().isEmpty()) {
                        top--;
                    }
                    skin = level.getBlockState(new BlockPos(x, top, z));
                    if (!skin.getFluidState().isEmpty()) continue;
                }
                int outX = Math.max(0, Math.max(box.lowX() - x, x - box.highX()));
                int outZ = Math.max(0, Math.max(box.lowZ() - z, z - box.highZ()));
                double share = Math.min(1.0D, Math.hypot(outX, outZ) / (double) DRAPE_REACH);
                double ease = share * share * (3.0D - 2.0D * share);
                int cap = (int) Math.round(wall + (top - wall) * ease);
                if (top > cap && !banked) {
                    for (int y = surface; y > cap; y--) {
                        level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                    }
                    level.setBlock(new BlockPos(x, cap, z), skin, 2);
                    cut++;
                } else if (top < cap) {
                    for (int y = top + 1; y < cap; y++) {
                        level.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState(), 2);
                    }
                    level.setBlock(new BlockPos(x, cap, z),
                            skin.isSolid() ? skin : Blocks.STONE.defaultBlockState(), 2);
                    filled++;
                }
            }
        }
        return new Drape(cut, filled, open);
    }

    /** A piece's own NBT, as the cut wrote it, for a pass that has to know its shape rather than its size. */
    private static CompoundTag raw(Path folder, String name) throws IOException {
        try (InputStream in = Files.newInputStream(folder.resolve(name))) {
            return NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
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
                // A cut writes the piece's own size after its offset, because a box that is not a multiple
                // of 48 ends in slivers - the last band of the new cut is four blocks tall - and a piece
                // that claimed a whole tile was refused for standing above a sky it never reached.
                parts.add(new Part(fields[0], Integer.parseInt(fields[1]),
                        Integer.parseInt(fields[2]), Integer.parseInt(fields[3]),
                        size(fields, 4), size(fields, 5), size(fields, 6)));
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

    /** A size out of the table, or a whole tile when the line does not carry one. */
    private static int size(String[] fields, int at) {
        if (fields.length <= at) return TILE;
        try {
            return Math.max(1, Math.min(TILE, Integer.parseInt(fields[at])));
        } catch (NumberFormatException notANumber) {
            return TILE;
        }
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
        // Every change to the marker goes through here, so this is the one place the cache is dropped.
        PLANTED_IN = null;
    }

    private static Path folder() {
        return FMLPaths.GAMEDIR.get().resolve(FOLDER);
    }
}
