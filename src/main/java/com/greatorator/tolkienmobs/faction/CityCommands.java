package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;
import com.greatorator.tolkienmobs.world.components.structure.PieceLayoutStructure;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.level.storage.LevelResource;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * The hand for standing the city up while it is still being cut.
 * <p>
 * The city ships with the mod: five hundred and eighty-four templates, a hundred and four megabytes of them, under
 * {@code data/tolkienmobs/structure/rivendell} in the jar, beside the two tables that say where each piece stands.
 * A world therefore gets the city with nothing beside it. The folder the cut is worked out in - {@code
 * run/rivendell_art}, beside the game directory - is still read, but only for a file the jar does not carry: a re-cut
 * dropped there is picked up without rebuilding the pack, and the jar is asked first, so a shipped world is the cut
 * the pack was tested with.
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
 * A world that generated the city needs none of these words, and gets no marker either: the world places the
 * pieces as its chunks are made, and a marker is a file in the game directory rather than in a save, so there
 * is nothing for a generated world to write one with. Every hand here therefore reads a city the world built
 * for itself as well as one it planted - the start in the chunk the pin falls in is the proof, and a world that
 * has one is reported as standing with every band down - so {@code status}, {@code goto}, {@code drape},
 * {@code apron} and {@code folk} answer for it, {@code band} finds nothing left to lay, and {@code origin} and
 * {@code summon} refuse to plant, which is what stops a second city going up on the first.
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

    /**
     * Where a world keeps its own re-cut of the city, and the marker that says how far it was laid: in the save,
     * not beside it.
     * <p>
     * Both used to live in a folder beside the game directory, which is one folder for every world at once and
     * belongs to none of them: two worlds shared one answer about what had been dressed, a save copied to another
     * machine carried its city and not the record of it, and a release left the mod's own working files lying around
     * the player's home. A save is the thing that travels and the thing that is self-contained, so what a world has
     * done is kept in the world. The cut itself is read from the jar; a world's folder only ever holds a re-cut and
     * the markers, which is what a world's business is.
     */
    private static final String FOLDER = "rivendell";
    /** Where the cut ships: the pieces and the two tables that place them, in the jar. */
    private static final String JAR_CUT = "/data/tolkienmobs/structure/rivendell/";
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
    /**
     * Thirty-two, not sixty-four. At sixty-four the ring reached past the branch the river leaves the city
     * by, and a pass that settles the ground over that much of the outflow overwrites the water: the river
     * left the build. Thirty-two is where the settled ring still meets the drawn country without touching
     * the branch.
     */
    static final int DRAPE_REACH = 60;
    /**
     * How far over the city's own floor a column's top may stand and still be a deck and not a wall: the
     * floor tile and the course laid on it. Read as a lift over the build's corner rather than as a height in
     * the world, because a world that raises the valley raises the build with it. A fixed 76 did for a world
     * whose floor was 74, and in a raised one it made every column of the city a wall: the drape then cut the
     * ground outside every open face down to the roofline, which is the trench this rule exists to keep shut.
     */
    static final int DECK_LIFT = 2;
    /**
     * How far over the city's floor water at the build's edge may stand and still be its outflow. The city
     * drains at its floor, where the branch takes the water eight blocks over it; water at the edge above that
     * is a spring held up in the build, and the ground outside it has to hold the water in.
     */
    static final int OUTLET_LIFT = 24;
    /**
     * How deep the ground in front of a mouth is taken out, in blocks, so the water the build carries out has a bed
     * to run in rather than a bar of country standing across it.
     * <p>
     * The mouth's own column was left alone on purpose - a pass that fills the ground over the water a build drains
     * by is a pass that dams it - but leaving it alone left everything <em>in front</em> of it alone too, and the
     * columns of the ring are read off the wall they clamp to: every column out to the reach in front of a mouth
     * reads as the mouth, so none of them was cut, and the country's own bank stood four to six blocks across the
     * outflow. The build's water then ran into a wall eight blocks out. Three blocks is a river's own depth at a
     * mouth this size, and it is taken off the ring only, never off the water: a column that is already water is
     * left as it is.
     */
    static final int MOUTH_DEPTH = 3;

    /** What a drape did: columns cut down, columns built up, and columns left for the water. */
    record Drape(int cut, int filled, int open, int skipped, int orphaned, int inside) {
    }
    /** Templates already read this session, so the ten megabytes are parsed once. */
    private static final Map<String, StructureTemplate> LOADED = new HashMap<>();
    /** The corner last read out of the marker, and the dimension it was read for. */
    private static BlockPos PLANTED;
    private static String PLANTED_IN;
    /** The corner this dimension's own generation would put the city on, worked out once. */
    private static BlockPos WOULD_BUILD;
    private static String WOULD_BUILD_IN;
    /**
     * How often a "no city here" answer may be asked again, in ticks.
     * <p>
     * A generated city is not there when the world is new: the valley is empty until a player walks into it, so an
     * answer read once and kept would lock the campaign out of a city it could have found the next minute. A second
     * between asks is faster than anyone walks into a valley and cheap enough to be worth it.
     */
    private static final int RECHECK_TICKS = 20;
    private static long ASK_AGAIN;
    /**
     * How near the pin a player has to be for the city to raise itself, in blocks across the ground.
     * <p>
     * The rule the camps are held to: a chunk nobody is looking at is a chunk the game will not keep, and a block
     * written into one is quietly dropped. A city is five hundred blocks across, so the reach is measured from the pin
     * rather than from a middle nobody can see the edge of, and it is generous enough to cover the walk in from the
     * shoulder.
     */
    private static final int ROUSE_REACH = 160;
    /**
     * How many ticks one piece of the city is given.
     * <p>
     * A piece is a forty-eight cube - a hundred thousand block writes, lit and updated. The hand lays all five hundred
     * and eighty-four in one word because an operator asked for them and is waiting for the answer; a world raising the
     * city under a walking player cannot spend minutes in a tick. Four ticks a piece is a city in about two minutes
     * with nothing but a piece ever carried.
     */
    private static final int PIECE_TICKS = 4;
    /**
     * The slowest cadence the raise drops to when the world is behind, in ticks: a piece a second.
     * <p>
     * Four ticks a piece is the shape the raise is meant to keep. It is not a promise to keep it while the server is
     * over budget: a piece is a hundred thousand writes, and one added to a tick that is already late is what turns a
     * raise into a freeze. So the cadence walks between the two on the tick's own health, and comes back down again
     * when the world does.
     */
    private static final int SLOWEST_PIECE_TICKS = 20;
    /** The average tick the raise will not add to, in milliseconds. Past it the next piece waits for a calmer tick. */
    private static final double BUSY_TICK = 45.0D;
    /**
     * How many passes in a row may lay nothing before the raise makes the ground for the band itself.
     * <p>
     * A pass with nothing laid is ordinary: the raise is paced, so the tick between two of them is not a stall, and one
     * that lands while the world is behind is the pacing doing its job. A run of them is different - it is the band with
     * no made ground left under it - and the fix for that is to ask the world for the ground rather than to wait longer
     * for it. Five passes is a second to five seconds depending on the cadence, which is long enough that the answer is
     * not a piece the player is about to load by walking, and short enough that nobody watching thinks the city stopped.
     */
    private static final int STALL_PASSES = 5;
    /** The cadence in flight, walked between the two by {@link #paceFor}. */
    private static int pace = PIECE_TICKS;
    /** The raise in flight: the corner it is laying, the band it is on, and how many pieces of that band are down. */
    private static BlockPos RAISING;
    private static int raisingBand = -1;
    /** The band's pieces in the order they come down, nearest the player first. */
    private static List<Part> raisingOrder = List.of();
    /** Which of that order are down, so a piece left waiting for its ground is not counted twice. */
    private static final Set<Integer> raisingDone = new HashSet<>();
    /** Passes in a row that laid nothing, so a band out of made ground is told apart from the pacing. */
    private static int raisingWaiting;
    /** Whether this band has already said what it is waiting on, so a band that stalls speaks once and not every pass. */
    private static boolean raisingSpoke;
    /** The cut's pieces, read once: the raise is asked every few ticks and the table is five hundred lines. */
    private static List<Part> CUT_PARTS;
    /** Where the raise says what went down, on the channel the hand prints to. */
    private static final Logger LOGGER = LogUtils.getLogger();

    /** One piece: its file, and where its corner sits in the frame the cut was taken in. */
    record Part(String name, int dx, int dy, int dz, int wide, int deep, int tall) {
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
    record Box(int lowX, int lowY, int lowZ, int highX, int highY, int highZ) {
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
        Path folder = folder(level);
        try {
            Mark mark = mark(level, folder);
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
        for (String line : lines(folder, FOLK)) {
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
     * The city the world built for itself, or null when it has not reached the pin.
     * <p>
     * Worldgen places the cut from the pin, so the corner is the pin less the cut's own low offsets - the same
     * arithmetic {@link #plant} does - and the proof that it happened is the structure start in the chunk that
     * corner falls in. Reading the world's blocks instead would call a mountain that has had a city cut into it a
     * city, and reading the marker would answer nothing at all, since a generated world has none.
     */
    private static BlockPos generated(ServerLevel level, Path folder, List<Part> parts) {
        BlockPos corner = generatedCorner(level, parts);
        if (corner == null || !cutStandsAt(level, corner, true)) return null;
        // A start is the plan; the city is the blocks. See {@link #standing}.
        return standing(level, folder, parts, corner) ? corner : null;
    }

    /** The corner the world would generate the city's cut at here, or null where it generates none. */
    private static BlockPos generatedCorner(ServerLevel level, List<Part> parts) {
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return null;
        BlockPos pin = Pins.at(level, Campaign.SUMMON_PLACE);
        return pin == null ? null : atPin(parts, pin);
    }

    /**
     * The corner the cut's own low offsets put the city's lowest corner on when the pin is that corner's block.
     * <p>
     * The pin is written where the scout stood on the city's low corner, so the corner is the pin less the offsets the
     * cut was taken at - the same arithmetic the hand planting from the valley does, kept here so the raise and the
     * command cannot disagree about which block the city starts from.
     */
    private static BlockPos atPin(List<Part> parts, BlockPos pin) {
        return new BlockPos(pin.getX() - parts.stream().mapToInt(Part::dx).min().orElse(0),
                pin.getY() - parts.stream().mapToInt(Part::dy).min().orElse(0),
                pin.getZ() - parts.stream().mapToInt(Part::dz).min().orElse(0));
    }

    /**
     * Raises the city from its pin as a player comes to the valley, the way the camps are raised from theirs.
     * <p>
     * A world that generates the city has it without this: the start goes down with the chunk the pin falls in, and a
     * piece goes down with every chunk made around it, so the build arrives with the ground. This is for the world
     * where that did not happen - and for a world walked before the cut was generated at all - and it lays the same
     * pieces from the same pin, a piece at a time, so a city appears where somebody can see it rather than when a
     * chunk is asked for in the dark.
     * <p>
     * Nothing here decides where the city goes: the pin does, exactly as {@code /tmcity origin} does. Nothing here
     * lays a second one either - the generated start and a marker planted by hand are both read first, and a city that
     * stands is left standing. What it does write is the marker the hand would have written, so the dressing pass, the
     * road and the city's people all find the city they expect.
     */
    static void rouse(ServerLevel level) {
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return;
        // The city's ground is not laid while its site is re-scouted. A world that still holds the old pin - or the
        // marker a raise left - must not put the cut back into ground the drawing now makes: the pin is a saved file
        // in that world and would outlive the source table it came from.
        if (!ArdaMap.planted()) {
            forgetRaise();
            return;
        }
        pace = paceFor(level);
        if (level.getGameTime() % pace != 0) return;
        BlockPos pin = Pins.at(level, Campaign.SUMMON_PLACE);
        if (pin == null) {
            forgetRaise();
            return;
        }
        // A player in the Shire is not worth reading the cut for; the box test comes after the read, and this is the
        // cheap question that keeps that read from happening anywhere in the world at all.
        if (!within(level, pin, COARSE_REACH)) return;
        Path folder = folder(level);
        try {
            if (CUT_PARTS == null) {
                CUT_PARTS = parts(folder);
                if (CUT_PARTS.isEmpty()) return;
            }
            List<Part> parts = CUT_PARTS;
            List<Integer> bands = bands(parts);
            Mark mark = mark(level, folder);
            // A corner planted in another country is not this world's city, whatever the marker says.
            if (mark != null && elsewhere(mark, level) != null) mark = null;
            if (mark != null && mark.band() >= bands.size()) {
                forgetRaise();
                return;
            }
            BlockPos corner = mark == null ? atPin(parts, pin) : mark.origin();
            // The pin is the city's own low corner and the city is five hundred blocks across, so a player at its far
            // end - or on the road up to it - is a long way from that corner by the pin's own measure. The cut's box
            // is what "in the valley" means, and it is the box the dressing pass already works from.
            if (!near(level, Box.of(parts, corner))) return;
            if (mark == null) {
                Campaign.setValleyOrigin(corner);
                write(folder, new Mark(corner, 0, false, level.dimension().identifier().toString()));
                forgetRaise();
                LOGGER.info("Rivendell: raised from the pin at {} - {} bands, {} pieces, a piece at a time",
                        Box.of(parts, corner).spoken(), bands.size(), parts.size());
                return;
            }
            layPiece(level, folder, parts, bands, mark);
        } catch (IOException unreadable) {
            LOGGER.warn("Rivendell: the cut could not be read, so the city is not raised: {}",
                    unreadable.getMessage());
        }
    }

    /** One piece of the band the marker is on, and the band's turn when its last piece is down. */
    private static void layPiece(ServerLevel level, Path folder, List<Part> parts, List<Integer> bands, Mark mark)
            throws IOException {
        if (RAISING == null || !RAISING.equals(mark.origin()) || raisingBand != mark.band()) {
            RAISING = mark.origin();
            raisingBand = mark.band();
            raisingDone.clear();
            raisingWaiting = 0;
            raisingSpoke = false;
            raisingOrder = nearest(parts, bands.get(mark.band()), mark.origin(), level);
        }
        for (int index = 0; index < raisingOrder.size(); index++) {
            if (raisingDone.contains(index)) continue;
            Part part = raisingOrder.get(index);
            BlockPos at = mark.origin().offset(part.dx(), part.dy(), part.dz());
            // A piece that would stand above the sky is not laid at all: the world drops those blocks without a word,
            // and asking for them again next tick would drop them again. The sky is the block above the last one that
            // can be stood on, so a piece whose top is the sky itself is one block too tall.
            if (at.getY() + part.tall() - 1 >= level.getMaxY() || at.getY() < level.getMinY()) {
                raisingDone.add(index);
                continue;
            }
            // A piece waits for ground the world already has rather than making it. Nine chunk columns of generation
            // inside a tick, for a piece the player may be four hundred blocks from, was the raise's own worst cost:
            // the chunks come with the player's own view, so the city fills in as it is walked.
            if (!made(level, at, part.wide(), part.deep())) continue;
            lay(level, folder, part, mark.origin(), false);
            // The lowest band is the city's floor, so it is the one that needs ground under it.
            if (mark.band() == 0) Sites.underpin(level, at, part.wide(), part.deep(), UNDERPIN);
            raisingDone.add(index);
            raisingWaiting = 0;
            return;
        }
        if (raisingDone.size() < raisingOrder.size()) {
            // Nothing left in this band is standing on made ground. The player's own view is what makes ground, and a
            // band is a whole slice of a city five hundred blocks across, so most of it is beyond the view of whoever
            // is standing in one corner of it: the near pieces come down, and the far ones wait for a walk that has no
            // reason to happen. Waited on, that is a city that stops halfway and says nothing, which is exactly what a
            // player reports. A pass or two of silence is the pacing; a run of them is the band out of ground.
            if (++raisingWaiting < STALL_PASSES) return;
            raisingWaiting = 0;
            makeGround(level, folder, mark, bands.size());
            return;
        }
        Mark next = new Mark(mark.origin(), mark.band() + 1, mark.apron(), mark.dimension());
        write(folder, next);
        // The next band's order is not built here: the pass that reads this marker builds it. Leaving raisingBand
        // on the band just written made that pass believe the order was already in hand, and an empty order is every
        // piece down - so every band after the first closed in a single pass and the raise reported a whole city with
        // nothing standing above its floor. No band of a real cut is empty, since the bands are the cut's own heights.
        if (raisingOrder.isEmpty()) {
            LOGGER.warn("Rivendell: band {} of {} closed with no pieces in hand - the marker is moved on, but nothing"
                    + " of that band was laid", mark.band() + 1, bands.size());
        }
        // -1 is "no order yet", the state a corner starts in, so the next pass builds the next band's order.
        raisingBand = -1;
        raisingOrder = List.of();
        raisingDone.clear();
        if (next.band() < bands.size()) {
            LOGGER.info("Rivendell: band {} of {} is down", next.band(), bands.size());
        } else {
            LOGGER.info("Rivendell: the whole city is down at {} - the ground is dressed from here",
                    Box.of(parts, mark.origin()).spoken());
            forgetRaise();
        }
    }

    /**
     * Makes the ground for one piece of a band that has run out of it, and says once what the band is waiting on.
     * <p>
     * The raise stands its pieces on ground the world already has, and that is right: nine chunk columns of generation
     * inside a tick, for a piece the player may be four hundred blocks from, is the raise's own worst cost, and the
     * player's own view is the thing that pays it as they walk. A band is a whole horizontal slice of a city five
     * hundred blocks across, though, and the raise will not move to the next band until every piece of this one is
     * down, so a standing player leaves the far ends of a band un-made for ever: everything in reach goes down and the
     * band stops at the edge of the view. So when a band has laid nothing for {@link #STALL_PASSES} passes, one piece's
     * ground is made for it - the nearest one left, the one the player can see - and the band goes on. One piece a pass
     * is the cost the player's own walking pays, and the cadence walks up when the world cannot take even that.
     * <p>
     * It says so once per band rather than every pass: a band that is waiting is worth one line, and the band's own
     * "is down" line says how it ended.
     */
    private static void makeGround(ServerLevel level, Path folder, Mark mark, int total) throws IOException {
        for (int index = 0; index < raisingOrder.size(); index++) {
            if (raisingDone.contains(index)) continue;
            Part part = raisingOrder.get(index);
            BlockPos at = mark.origin().offset(part.dx(), part.dy(), part.dz());
            if (!raisingSpoke) {
                raisingSpoke = true;
                LOGGER.info("Rivendell: band {} of {} is out of made ground - {} pieces left, the nearest at {}, {} blocks"
                                + " from the nearest player; its ground is made for it from here",
                        mark.band() + 1, total, raisingOrder.size() - raisingDone.size(), at.toShortString(),
                        Math.round(fromNearestPlayer(level, at)));
            }
            lay(level, folder, part, mark.origin(), true);
            // The lowest band is the city's floor, so it is the one that needs ground under it.
            if (mark.band() == 0) Sites.underpin(level, at, part.wide(), part.deep(), UNDERPIN);
            raisingDone.add(index);
            return;
        }
    }

    /** How far the nearest player stands from a piece across the ground, in blocks; zero when nobody is in the world. */
    private static double fromNearestPlayer(ServerLevel level, BlockPos at) {
        double closest = Double.MAX_VALUE;
        for (ServerPlayer player : level.players()) {
            closest = Math.min(closest, Math.hypot(player.getX() - at.getX(), player.getZ() - at.getZ()));
        }
        return closest == Double.MAX_VALUE ? 0.0D : closest;
    }

    /**
     * A band's pieces in the order they come down: nearest the player first.
     * <p>
     * A band is a whole horizontal slice of the city - a hundred and thirty-two pieces at its widest - so the cut's own
     * order puts its pieces down in a sweep that starts wherever the table starts, often four hundred blocks from
     * whoever is standing in the valley. Ordered by distance the raise works where it is being watched, and the rest of
     * the band follows the player as they walk the streets already standing.
     */
    private static List<Part> nearest(List<Part> parts, int height, BlockPos origin, ServerLevel level) {
        List<Part> band = new ArrayList<>();
        for (Part part : parts) {
            if (part.dy() == height) band.add(part);
        }
        List<ServerPlayer> players = level.players();
        if (players.isEmpty() || band.size() < 2) return band;
        band.sort(Comparator.comparingDouble(part -> {
            double x = origin.getX() + part.dx() + part.wide() / 2.0D;
            double z = origin.getZ() + part.dz() + part.deep() / 2.0D;
            double closest = Double.MAX_VALUE;
            for (ServerPlayer player : players) {
                closest = Math.min(closest, Math.hypot(player.getX() - x, player.getZ() - z));
            }
            return closest;
        }));
        return band;
    }

    /**
     * Whether every chunk a piece stands in is already made.
     * <p>
     * The raise lays into the world as the player has it, and a chunk that is not loaded is a chunk the world would
     * have to generate for it - which is the cost the raise must not pay a piece at a time. A piece whose ground the
     * world has not made yet is left for a later pass: the chunks arrive with the player's own view, so the city comes
     * down as it is walked rather than all at once from wherever it was first seen.
     */
    private static boolean made(ServerLevel level, BlockPos at, int wide, int deep) {
        for (int dx = 0; dx < wide; dx += 16) {
            for (int dz = 0; dz < deep; dz += 16) {
                if (level.getChunkSource().getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4,
                        ChunkStatus.FULL, false) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Whether a player is near enough to the pin for the city's ground to be the game's to write in. */
    /**
     * Whether anyone is within the raise's reach of the city, measured from its footprint rather than its corner.
     * <p>
     * The pin is the cut's own low corner, and the city covers five hundred-odd blocks either way from it: a player
     * standing at the far end of the valley, or on the road climbing into it, is more than a hundred and sixty blocks
     * from that corner and would stand in an empty valley for ever. Measured from the box, anyone in the valley - or
     * on the road up to it - is near enough for the work to start, and the raise keeps going as they walk the streets
     * the pieces have already stood.
     */
    private static boolean near(ServerLevel level, Box box) {
        for (ServerPlayer player : level.players()) {
            int dx = Math.max(0, Math.max(box.lowX() - player.getBlockX(), player.getBlockX() - box.highX()));
            int dz = Math.max(0, Math.max(box.lowZ() - player.getBlockZ(), player.getBlockZ() - box.highZ()));
            if (Math.hypot(dx, dz) <= ROUSE_REACH) return true;
        }
        return false;
    }

    /** How far from the pin anybody has to be before the cut is not read at all, in blocks: the whole city, and a walk. */
    private static final int COARSE_REACH = 900;

    /** Whether anyone is within a radius of one block, across the ground: the question asked before a cut is read. */
    private static boolean within(ServerLevel level, BlockPos at, int reach) {
        for (ServerPlayer player : level.players()) {
            int dx = player.getBlockX() - at.getX();
            int dz = player.getBlockZ() - at.getZ();
            if (Math.hypot(dx, dz) <= reach) return true;
        }
        return false;
    }

    /**
     * The cadence this tick's piece is laid on.
     * <p>
     * Four ticks a piece is a city in about two minutes, and that is the shape the raise keeps while the world keeps up
     * with itself. A piece is a hundred thousand writes, though, and adding them to a tick that is already over its
     * budget is how a raise becomes a freeze: past {@link #BUSY_TICK} the cadence walks up to
     * {@link #SLOWEST_PIECE_TICKS}, a piece a second, and back down again as the world recovers.
     */
    private static int paceFor(ServerLevel level) {
        float spent = level.getServer().getCurrentSmoothedTickTime();
        if (spent > BUSY_TICK) return Math.min(SLOWEST_PIECE_TICKS, pace + 1);
        if (spent < BUSY_TICK / 2.0F) return Math.max(PIECE_TICKS, pace - 1);
        return pace;
    }

    /** Drops what the raise remembers of a city that is standing, or of a pin that is no longer there. */
    private static void forgetRaise() {
        RAISING = null;
        raisingBand = -1;
        raisingOrder = List.of();
        raisingDone.clear();
        raisingWaiting = 0;
        raisingSpoke = false;
    }

    /**
     * Whether the world generated a cut build in the chunk a corner falls in.
     * <p>
     * The chunk is made rather than only looked for: the start is written into the chunk the pin falls in, and a
     * world nobody has walked into the valley of yet would otherwise answer "not yet" to a question about what it
     * will do the moment someone does - which is exactly the answer that would let a second planting go ahead. One
     * chunk is a small thing to make for a question this decides.
     * <p>
     * A start is the plan of a build, though, and not the build: see {@link #standing} for the second half of the
     * question, which is the one that matters.
     */
    private static boolean cutStandsAt(ServerLevel level, BlockPos corner, boolean make) {
        var chunk = make ? level.getChunk(corner.getX() >> 4, corner.getZ() >> 4)
                : level.getChunkSource().getChunk(corner.getX() >> 4, corner.getZ() >> 4, ChunkStatus.FULL, false);
        if (chunk == null) return false;
        for (var structure : chunk.getAllStarts().keySet()) {
            if (structure instanceof PieceLayoutStructure) return true;
        }
        return false;
    }

    /** How many of the cut's own blocks are looked for before a world's structure start is believed to be a city. */
    private static final int STANDING_SAMPLES = 12;
    /**
     * How far inside a piece a sample has to sit to be the build rather than the ground it stands on, in blocks.
     * <p>
     * The city's floor is the block its corner stands on, so the blocks just above that corner are air in a valley
     * nothing has been laid in: a sample taken six blocks into the piece and higher is a question about the build,
     * and a sample on the piece's own top face is a question about the grass.
     */
    private static final int INSIDE = 6;
    /** The blocks read out of each piece, once: the probe is asked whenever a city is decided. */
    private static final Map<String, List<Probe>> PROBES = new HashMap<>();

    /** One of a piece's own blocks: where it sits from the piece's corner, and the block the cut says is there. */
    private record Probe(int x, int y, int z, String block) {
    }

    /**
     * Whether a world's structure start is a city, or only the plan of one.
     * <p>
     * A start is written into the chunk its anchor falls in and lists every piece of the cut, and that is all vanilla
     * can do with a build this size: a chunk carries references only to starts within eight chunks of itself, so a
     * city five hundred blocks across is placed in a ring round its anchor and nowhere else. A world that planted a
     * start for Rivendell therefore held the plan and not the city - the corner under it was grass - and every hand
     * that read the start as the city was told there was nothing to do: the raise stood aside for it, `/tmcity summon`
     * refused to plant over it, the band hand found nothing left to lay, and the dresser cut the mountain down round a
     * city that was not there. One save showed it exactly: the start's own chunk held all 584 pieces, a piece six
     * chunks out matched its template to the block, a piece twenty-one chunks out matched nothing at all, and the
     * ground under the anchor was short grass.
     * <p>
     * So the world's claim is checked against the world's own blocks. The piece the city is planted on - the lowest
     * band's own low corner, which is the block the pin names - has a dozen of its blocks looked for where the cut puts
     * them: stone where the valley has air is a city, air where the cut has stone is a start with nothing under it. A
     * majority answers, so a corner that has been walked on, had its grass broken or its flowers picked still counts as
     * standing, and only chunks the world already has are read - a probe that has to generate the ground it is asking
     * about is no better than the guess it replaces.
     */
    private static boolean standing(ServerLevel level, Path folder, List<Part> parts, BlockPos corner) {
        if (parts.isEmpty()) return false;
        // The pieces are read lowest band first, then by z, then by x: the first is the cut's own low corner.
        Part ground = parts.get(0);
        List<Probe> probes = probes(folder, ground);
        if (probes.isEmpty()) return false;
        BlockPos at = corner.offset(ground.dx(), ground.dy(), ground.dz());
        int seen = 0;
        int found = 0;
        for (Probe probe : probes) {
            BlockPos block = at.offset(probe.x(), probe.y(), probe.z());
            if (level.getChunkSource().getChunk(block.getX() >> 4, block.getZ() >> 4, ChunkStatus.FULL, false) == null) {
                continue;
            }
            seen++;
            if (BuiltInRegistries.BLOCK.getKey(level.getBlockState(block).getBlock()).toString()
                    .equals(probe.block())) {
                found++;
            }
        }
        return seen > 0 && found * 2 >= seen;
    }

    /** A dozen of a piece's own blocks, read out of its file and kept, because a city is decided more than once. */
    private static List<Probe> probes(Path folder, Part part) {
        List<Probe> known = PROBES.get(part.name());
        if (known != null) return known;
        List<Probe> probes = readProbes(folder, part);
        PROBES.put(part.name(), probes);
        return probes;
    }

    /**
     * The samples themselves: non-air blocks from the inside of the piece, taken across the whole file with a stride,
     * and blocks from its surface only when it has nothing inside it to ask about.
     */
    private static List<Probe> readProbes(Path folder, Part part) {
        try {
            CompoundTag tag = raw(folder, part.name());
            ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
            ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
            if (palette.isEmpty() || blocks.isEmpty()) return List.of();
            String[] names = new String[palette.size()];
            for (int index = 0; index < palette.size(); index++) {
                names[index] = palette.getCompoundOrEmpty(index).getStringOr("Name", "");
            }
            List<Probe> inside = new ArrayList<>();
            List<Probe> anywhere = new ArrayList<>();
            int stride = Math.max(1, blocks.size() / (STANDING_SAMPLES * 16));
            for (int index = 0; index < blocks.size(); index += stride) {
                CompoundTag block = blocks.getCompoundOrEmpty(index);
                int state = block.getIntOr("state", -1);
                if (state < 0 || state >= names.length) continue;
                if (names[state].endsWith("air")) continue;
                ListTag pos = block.getList("pos").orElseGet(ListTag::new);
                if (pos.size() < 3) continue;
                Probe probe = new Probe(pos.getIntOr(0, 0), pos.getIntOr(1, 0), pos.getIntOr(2, 0), names[state]);
                if (anywhere.size() < STANDING_SAMPLES) anywhere.add(probe);
                if (inside.size() < STANDING_SAMPLES && probe.y() >= INSIDE && probe.x() >= INSIDE
                        && probe.z() >= INSIDE && probe.x() < part.wide() - INSIDE
                        && probe.z() < part.deep() - INSIDE) {
                    inside.add(probe);
                }
            }
            List<Probe> chosen = inside.size() * 2 >= STANDING_SAMPLES ? inside : anywhere;
            return List.copyOf(chosen);
        } catch (IOException | RuntimeException unreadable) {
            LOGGER.warn("Rivendell: the cut could not be read to see whether the city stands: {}",
                    unreadable.getMessage());
            return List.of();
        }
    }

    /**
     * What this world's city is: the corner the hand planted, or the one the world generated for itself.
     * <p>
     * A marker wins where there is one, because it is a decision - a corner planted by hand after a re-cut is where
     * that world's city stands, whatever the world made when it was new. With no marker, a generated city is read as
     * planted and whole: every band of it is down, because the world places a piece when the chunk it touches is
     * made, so the hands that stand someone in it, dress its ground or put its people in it work on it unchanged,
     * and the hand that lays bands finds nothing left to lay instead of laying the city again.
     */
    private static Mark mark(ServerLevel level, Path folder) throws IOException {
        Mark planted = read(folder);
        if (planted != null) return planted;
        List<Part> parts = parts(folder);
        BlockPos built = generated(level, folder, parts);
        if (built == null) return null;
        return new Mark(built, bands(parts).size(), false, level.dimension().identifier().toString());
    }

    /**
     * The corner of a city this world has finished, or null while it is still being stood.
     * <p>
     * The dressing pass runs off this rather than off {@link #plantedCorner}, because a corner is written down when
     * the first band is planted: dressing the ground round a city one band high would cut the mountain down to a
     * roofline that is not there yet. A generated city is whole as soon as its start exists; a planted one when the
     * marker says its last band is down.
     */
    static BlockPos stoodCity(ServerLevel level) {
        BlockPos corner = plantedCorner(level);
        if (corner == null) return null;
        Path folder = folder(level);
        try {
            Mark mark = read(folder);
            if (mark == null) return corner;
            return mark.band() >= bands(parts(folder)).size() ? corner : null;
        } catch (IOException unreadable) {
            return null;
        }
    }

    /**
     * Where this world's city stands, or null when it has none.
     * <p>
     * This is what lets the campaign dress a city it did not build: the marker holds the corner the cut was laid
     * from and the dimension it was laid in, so a body whose visit finds no city here is told so rather than walked
     * to a frame that belongs to another country. A city the world generated has no marker, so it is found the way
     * the commands find it - the structure start in the chunk the pin falls in - and only in a chunk that is
     * already made, which a player walking into the valley has just made.
     */
    static BlockPos plantedCorner(ServerLevel level) {
        // Read once and kept: this is asked on the tick, whenever a player is near the valley, and a file
        // read a second is not what walking into Rivendell should cost. Every write to the marker clears
        // it, so a city planted or re-cut by hand is picked up on the next visit either way.
        String here = level.dimension().identifier().toString();
        if (here.equals(PLANTED_IN) && PLANTED != null) return PLANTED;
        // A world that generated the city has no marker to read, so the answer can legitimately be nothing yet:
        // the valley is empty until someone walks into it, and that happens while the game is running. A "no" is
        // therefore asked again rather than kept, at a rate too slow to matter and too fast to miss the walk in.
        long now = level.getGameTime();
        if (PLANTED == null && now < ASK_AGAIN) return null;
        ASK_AGAIN = now + RECHECK_TICKS;
        PLANTED_IN = here;
        try {
            Mark mark = read(folder(level));
            boolean mine = mark != null && (mark.dimension() == null || mark.dimension().equals(here));
            BlockPos corner = mine ? mark.origin() : null;
            if (corner == null) {
                // The chunk is not made here, only looked for: a tick that ran whenever a player was anywhere near
                // the valley must never generate a few hundred chunks of it to find out whether it holds a city. What
                // is looked for is the city and not the plan of one: a start alone is a world that planted the cut
                // somewhere and laid nothing, and dressing the ground round that is dressing round an empty valley.
                BlockPos built = wouldGenerate(level);
                if (built != null && cutStandsAt(level, built, false)
                        && standing(level, folder(level), parts(folder(level)), built)) {
                    corner = built;
                }
            }
            PLANTED = corner;
        } catch (IOException unreadable) {
            PLANTED = null;
        }
        return PLANTED;
    }

    /**
     * The corner this dimension's own generation would put the city on, or null where it generates none.
     * <p>
     * Fixed rather than lived: the pin and the cut's low offsets do not change while the game runs, so this is worked
     * out once and kept. Whether a city is actually there is a different question and is asked every time, because
     * that answer changes as the valley is made.
     */
    private static BlockPos wouldGenerate(ServerLevel level) {
        String here = level.dimension().identifier().toString();
        if (here.equals(WOULD_BUILD_IN)) return WOULD_BUILD;
        WOULD_BUILD_IN = here;
        WOULD_BUILD = null;
        try {
            WOULD_BUILD = generatedCorner(level, parts(folder(level)));
        } catch (IOException unreadable) {
            WOULD_BUILD = null;
        }
        return WOULD_BUILD;
    }

    /** The city's people as the build's own table gives them, for a visit that dresses a planted city. */
    static List<Campaign.FolkSpot> plantedFolk(ServerLevel level) {
        try {
            return folkSpots(folder(level));
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
        Path folder = folder(level);
        try {
            List<Part> parts = parts(folder);
            if (parts.isEmpty()) {
                source.sendFailure(Component.literal("No pieces in " + folder + " - is the cut there?"));
                return false;
            }
            // Planting is the one word that can put a second city down, so it is the one word that asks whether
            // the world already built this one. In the valley the corner comes from the pin rather than from the
            // caller's feet, so a hand planting over a generated city would land on the same blocks - and lay
            // every one of them again, a hundred megabytes of templates, to no purpose.
            BlockPos built = generated(level, folder, parts);
            if (built != null) {
                String where = Box.of(parts, built).spoken();
                source.sendFailure(Component.literal("This world generated the city itself, at " + where
                        + " - nothing to plant, and a planting here would lay a second copy over it. "
                        + "/tmcity status reads it, /tmcity goto stands you in it, and /tmcity relay takes it "
                        + "over if you have a re-cut to lay."));
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
            if (box.highY() >= level.getMaxY()) {
                int overflow = box.highY() - level.getMaxY() + 1;
                int fits = level.getMaxY() - tall;
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
        Path folder = folder(level);
        try {
            Mark mark = mark(level, folder);
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
                    if (at.getY() + part.tall() - 1 >= level.getMaxY() || at.getY() < level.getMinY()) {
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
        Path folder = folder(level);
        try {
            Mark mark = mark(level, folder);
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
        Path folder = folder(level);
        try {
            Mark mark = mark(level, folder);
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
        Path folder = folder(level);
        try {
            Mark mark = mark(level, folder);
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
        Path folder = folder(level);
        try {
            Mark planted = read(folder);
            Mark mark = planted != null ? planted : mark(level, folder);
            List<Part> parts = parts(folder);
            if (mark == null) {
                int waiting = parts.size();
                source.sendSuccess(() -> Component.literal("Nothing planted here, and the world has not "
                        + "generated a city in this country; " + waiting + " pieces waiting in " + folder), false);
                return 0;
            }
            Box box = Box.of(parts, mark.origin());
            BlockPos middle = box.middle();
            String spoken = box.spoken();
            int band = mark.band();
            int total = bands(parts).size();
            boolean blended = mark.apron();
            // Said out loud, because it is the difference between a city an operator put down band by band and one
            // the world made: the second has no marker, so nothing else would say where it came from.
            boolean made = planted == null;
            source.sendSuccess(() -> Component.literal((made ? "City generated by the world " : "City ")
                    + spoken + "; middle " + middle.toShortString() + "; band " + band + " of " + total
                    + "; shoulder " + (blended ? "blended" : "not blended")), false);
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

    /** One piece, with its chunks made first: a chunk that is not loaded cannot take blocks. */
    private static void lay(ServerLevel level, Path folder, Part part, BlockPos origin) throws IOException {
        lay(level, folder, part, origin, true);
    }

    /**
     * One piece. {@code make} says whether the ground under it may be made for it.
     * <p>
     * The hand's own bands say yes: an operator asked for the band with a command and is waiting for the answer, and
     * making the ground is the price of answering. The raise says no - it stands pieces on ground the player's own view
     * has already brought in, and leaves the rest for a later pass.
     */
    private static void lay(ServerLevel level, Path folder, Part part, BlockPos origin, boolean make)
            throws IOException {
        StructureTemplate template = template(level, folder, part.name());
        BlockPos at = origin.offset(part.dx(), part.dy(), part.dz());
        if (make) {
            for (int dx = 0; dx < part.wide(); dx += 16) {
                for (int dz = 0; dz < part.deep(); dz += 16) {
                    level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
                }
            }
        }
        // The piece is clipped to the sky it stands under rather than trusted to fit it. A cut made where the sky is
        // taller carries block entities above this one's ceiling; the world drops a block written up there without a
        // word, while its block entity is still written down, so a loading chunk finds a dummy at a position no
        // section holds and the point-of-interest update for it throws sectionPos out of bounds. Clipped, nothing
        // above the last block that can be stood on is placed at all - neither the block nor the block entity.
        if (at.getY() >= level.getMaxY()) return;
        StructurePlaceSettings settings = new StructurePlaceSettings();
        settings.setBoundingBox(new BoundingBox(at.getX(), Math.max(at.getY(), level.getMinY()), at.getZ(),
                at.getX() + part.wide() - 1, Math.min(at.getY() + part.tall() - 1, level.getMaxY() - 1),
                at.getZ() + part.deep() - 1));
        template.placeInWorld(level, at, at, settings, level.getRandom(), 2);
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
        Path folder = folder(level);
        try {
            Mark mark = mark(level, folder);
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
                    + " left for the water, " + done.skipped() + " left alone, " + done.orphaned()
                    + " cleared of a tree standing on nothing, " + done.inside()
                    + " inside the walls cut down to the cut."), true);
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
     * spring held up in the build: the pool is never cut out of, and the ground standing over it is carried
     * down to the pool's own surface, because a mountain left over a spring is the same shelf the drape takes
     * off every other face. Water out in the ring is neither: it is a pool lying on the
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
        // The ring is dressed by the same job the world runs for itself, stepped to the end in one call:
        // an operator looking at a city wants an answer now, and the pass is identical either way.
        return Dressing.drape(level, folder, parts, origin, box);
    }

    /**
     * One file of the cut, from the jar first and a re-cut's folder second.
     * <p>
     * The city ships in the mod, so a world gets it with nothing beside it: the pieces and their tables are
     * inside the jar. The folder is still asked, but only for a file the jar does not carry, because a re-cut
     * is worked out in {@code run/rivendell_art} long before it is packed and dropping one there should not
     * mean rebuilding the pack to look at it. The jar goes first so that a folder left over from an old cut
     * cannot quietly win: a shipped world is the cut the pack was tested with.
     */
    private static InputStream cut(Path folder, String name) throws IOException {
        InputStream packed = CityCommands.class.getResourceAsStream(JAR_CUT + name);
        if (packed != null) return packed;
        Path loose = folder.resolve(name);
        if (Files.isRegularFile(loose)) return Files.newInputStream(loose);
        return null;
    }

    /** Every line of one of the cut's two tables, from the jar or from a re-cut's folder, or nothing at all. */
    private static List<String> lines(Path folder, String name) throws IOException {
        try (InputStream in = cut(folder, name)) {
            if (in == null) return List.of();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines().toList();
            }
        }
    }

    /** A piece's own NBT, as the cut wrote it, for a pass that has to know its shape rather than its size. */
    static CompoundTag raw(Path folder, String name) throws IOException {
        try (InputStream in = cut(folder, name)) {
            if (in == null) throw new IOException("no " + name + " in the cut");
            return NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
    }

    /** A piece's template, read from the cut the first time it is asked for. */
    private static StructureTemplate template(ServerLevel level, Path folder, String name) throws IOException {
        StructureTemplate cached = LOADED.get(name);
        if (cached != null) return cached;
        CompoundTag tag;
        try (InputStream in = cut(folder, name)) {
            if (in == null) throw new IOException("no " + name + " in the cut");
            tag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
        StructureTemplate template = new StructureTemplate();
        template.load(level.holderLookup(Registries.BLOCK), tag);
        LOADED.put(name, template);
        return template;
    }

    /** The table of pieces, read in the order they are laid: lowest band first, then a sweep across it. */
    static List<Part> parts(Path folder) throws IOException {
        List<Part> parts = new ArrayList<>();
        for (String line : lines(folder, PARTS)) {
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

    /**
     * Whether the valley has been laid to the top of its own cut.
     * <p>
     * The marker counts bands, so the last band down is the city finished - the same test the band hand makes before it
     * finds nothing left to lay. This is what a hall pinned inside the valley waits for, because a city raised band by
     * band would lay over a hall stood early in exactly the way the Shire's village would.
     */
    public static boolean complete(ServerLevel level) {
        try {
            Path folder = folder(level);
            Mark mark = read(folder);
            if (mark == null) return false;
            return mark.band() >= bands(parts(folder)).size();
        } catch (IOException unreadable) {
            return false;
        }
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

    /** This world's own folder for the city: the save's, with dimension and corner keyed in the marker itself. */
    static Path folder(ServerLevel level) {
        return level.getServer().getWorldPath(LevelResource.ROOT).resolve(MODID).resolve(FOLDER);
    }
}
