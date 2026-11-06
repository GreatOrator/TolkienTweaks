package com.greatorator.tolkienmobs.faction;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The road out of the valley, and the bridge that carries it over the Bruinen.
 * <p>
 * A road in this pack is a surface and nothing else: it follows the ground and replaces the top block,
 * so a slope is a staircase of half steps rather than a cut corridor. The bridge is the exception, because
 * shaped ground cannot span water - it is stood up from the cut's own two pieces, {@code bridge_end} and
 * {@code bridge_middle}, which are shipped in the jar.
 * <p>
 * Three things about the ground here decided the shape of this pass, all of them measured in the scouted
 * world rather than guessed:
 * <ul>
 *   <li>The city's west wall outside the built path stands at 134 and the Bruinen at 74, so the approach
 *   falls sixty blocks over a hundred and forty-five. Painted, that reads as a stepped road downhill.</li>
 *   <li>The drawn water is about forty-five blocks wide at the bank, and the two markers are fifty columns
 *   apart end to end, which is exactly two ends and four middles.</li>
 *   <li>The pieces carry their own pillars, but only six blocks of them, so every column that holds one has
 *   its log drawn down to the bed it stands on - through the water where the span crosses it. A bridge that
 *   stops at the water line is a bridge floating on the river.</li>
 * </ul>
 * <p>
 * Both ends of the bridge are approached along the span's own axis, on one z, because a road that arrives
 * diagonally has to be modelled as a junction the pieces do not have.
 * <p>
 * The waypoints are the ammolite markers walked in the scouted world: the path's last block at 5200, 134,
 * -3034 and the two banks at 5055 and 5006 on -3075.
 */
@EventBusSubscriber(modid = MODID)
public final class RoadCommands {
    /** Gamemaster-level operators, and anyone building in creative, as the city's own hand answers to. */
    private static final Predicate<CommandSourceStack> MAY_BUILD = source -> {
        ServerPlayer player = source.getPlayer();
        return (player != null && player.isCreative())
                || Commands.hasPermission(Commands.LEVEL_GAMEMASTERS).test(source);
    };

    /** The two pieces the bridge is stood up from, in the jar's own structure folder. */
    private static final String BRIDGE_END = "bridge_end";
    private static final String BRIDGE_MIDDLE = "bridge_middle";

    /** The path's last block, and the two banks the bridge joins. */
    private static final int PATH_X = 5200;
    private static final int PATH_Z = -3034;
    private static final int EAST_BANK_X = 5055;
    private static final int WEST_BANK_X = 5006;
    private static final int BRIDGE_Z = -3075;
    /** The bridge is nine wide, as the pieces are, so four either side of its line. */
    private static final int BRIDGE_HALF = 4;
    /**
     * Where the deck sits. Middlies carry their planks two blocks up, so a base of 74 puts the walking
     * surface at 76 - two over water that stands at 74. The ends carry a slab one block up instead, so they
     * are placed one higher: the walking surface meets the middle's within half a block, which is the same
     * half step the road takes everywhere else.
     */
    private static final int BRIDGE_BASE = 74;
    private static final int BRIDGE_END_BASE = 75;
    /** How far a pillar may be drawn down before the pass gives up on finding a bed, in blocks. */
    private static final int PILLAR_FLOOR = 48;

    /**
     * The approach, in two legs: the fall from the wall, then a straight run along the bridge's line.
     * <p>
     * The last leg is the one that matters - it is on BRIDGE_Z and parallel to the span, so the road walks
     * onto the abutment instead of meeting it at an angle. It stops one block short of the east bank, where
     * the piece itself begins.
     */
    private static final int[][] APPROACH = {
            {PATH_X, PATH_Z},
            {5120, BRIDGE_Z},
            {EAST_BANK_X + 1, BRIDGE_Z},
    };
    /** The far side: straight out of the span along the same line, then on to the drawn country. */
    private static final int[][] EXIT = {
            {WEST_BANK_X - 1, BRIDGE_Z},
            {WEST_BANK_X - 32, BRIDGE_Z},
    };

    /** The city's own footprint, which the surface bands are measured from. */
    private static final int CITY_X0 = 5200;
    private static final int CITY_X1 = 5753;
    private static final int CITY_Z0 = -3370;
    private static final int CITY_Z1 = -2859;
    /** How close the ground has to be to the walls to count as the settlement's own ground, in blocks. */
    private static final int TOWN_BAND = 16;
    private static final int TRANSITION_BAND = 64;
    /** Half-widths: seven of wild road, five of paved. */
    private static final int WILD_HALF = 3;
    private static final int PAVED_HALF = 2;

    /** Templates already read this session, so a re-run does not parse them again. */
    private static final Map<String, StructureTemplate> LOADED = new HashMap<>();

    private RoadCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("tmroad")
                .requires(MAY_BUILD)
                .then(Commands.literal("build").executes(RoadCommands::build)));
    }

    /** Stands the bridge, draws its pillars down, and paints the road either side of it. */
    private static int build(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        try {
            int pieces = bridge(level);
            int pillars = pillars(level);
            int[] approach = paint(level, APPROACH);
            int[] exit = paint(level, EXIT);
            source.sendSuccess(() -> Component.literal("Road to the valley: bridge of " + pieces
                    + " pieces from " + EAST_BANK_X + " to " + WEST_BANK_X + " on z " + BRIDGE_Z
                    + ", pillars drawn down " + pillars + " blocks; approach " + approach[0]
                    + " columns laid, " + approach[1] + " stepped with slabs; far side " + exit[0]
                    + " columns, " + exit[1] + " stepped. Both ends walk in along the span's own line."), true);
            return 1;
        } catch (IOException failed) {
            source.sendFailure(Component.literal("The bridge could not be read: " + failed.getMessage()));
            return 0;
        }
    }

    /**
     * Stands the two ends and the middles between them, east end turned about so its abutment faces the
     * city: the piece carries its abutment at its low x, and the road arrives from the east.
     */
    private static int bridge(ServerLevel level) throws IOException {
        StructureTemplate end = template(level, BRIDGE_END);
        StructureTemplate middle = template(level, BRIDGE_MIDDLE);
        int z0 = BRIDGE_Z - BRIDGE_HALF;
        // East abutment: turned, so the piece's own low-x abutment lands on the city's side of the water.
        lay(level, end, new BlockPos(EAST_BANK_X - end.getSize().getX() + 1, BRIDGE_END_BASE, z0),
                Rotation.CLOCKWISE_180);
        int placed = 1;
        // Middles walk west from the east abutment, eight columns each, until the west abutment's turn.
        for (int at = EAST_BANK_X - end.getSize().getX(); at >= WEST_BANK_X + end.getSize().getX(); at -= 8) {
            lay(level, middle, new BlockPos(at, BRIDGE_BASE, z0), Rotation.NONE);
            placed++;
        }
        lay(level, end, new BlockPos(WEST_BANK_X, BRIDGE_END_BASE, z0), Rotation.NONE);
        return placed + 1;
    }

    /** One piece, with every chunk it touches opened first: a block written into a closed chunk is lost. */
    private static void lay(ServerLevel level, StructureTemplate template, BlockPos at, Rotation rotation) {
        int wide = template.getSize().getX();
        int deep = template.getSize().getZ();
        for (int dx = 0; dx <= wide; dx += 8) {
            for (int dz = 0; dz <= deep; dz += 8) {
                level.getChunk((at.getX() + dx) >> 4, (at.getZ() + dz) >> 4);
            }
        }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
        template.placeInWorld(level, at, at, settings, level.getRandom(), 2);
    }

    /**
     * Draws every pillar the pieces brought down to the bed under it.
     * <p>
     * The pieces carry six blocks of support and the water here is four or five deep over its bed, so on its
     * own the span stands on stilts that stop in mid-air. What is looked for is the lowest oak log in each
     * column of the span; below it the column is filled with oak log while it is air or water, and the fill
     * stops at the first block that is neither - the bed, or the bank under an abutment.
     */
    private static int pillars(ServerLevel level) {
        int drawn = 0;
        int z0 = BRIDGE_Z - BRIDGE_HALF;
        for (int x = WEST_BANK_X; x <= EAST_BANK_X; x++) {
            for (int z = z0; z <= z0 + BRIDGE_HALF * 2; z++) {
                int base = Integer.MIN_VALUE;
                for (int y = BRIDGE_BASE + 6; y >= BRIDGE_BASE - 2; y--) {
                    if (level.getBlockState(new BlockPos(x, y, z)).is(Blocks.OAK_LOG)) base = y;
                }
                if (base == Integer.MIN_VALUE) continue;
                for (int y = base - 1; y > base - PILLAR_FLOOR; y--) {
                    BlockPos at = new BlockPos(x, y, z);
                    BlockState under = level.getBlockState(at);
                    if (!under.isAir() && under.getFluidState().isEmpty()) break;
                    level.setBlock(at, Blocks.OAK_LOG.defaultBlockState(), 2);
                    drawn++;
                }
            }
        }
        return drawn;
    }

    /** Paints one polyline, and steps its rises, answering with columns laid and columns stepped. */
    private static int[] paint(ServerLevel level, int[][] line) {
        Map<Long, Integer> tops = new LinkedHashMap<>();
        int columns = 0;
        for (int leg = 0; leg + 1 < line.length; leg++) {
            int x0 = line[leg][0];
            int z0 = line[leg][1];
            int x1 = line[leg + 1][0];
            int z1 = line[leg + 1][1];
            int steps = Math.max(Math.abs(x1 - x0), Math.abs(z1 - z0));
            for (int i = 0; i <= steps; i++) {
                int x = x0 + Math.round((x1 - x0) * (float) i / steps);
                int z = z0 + Math.round((z1 - z0) * (float) i / steps);
                columns += lay(level, x, z, tops);
            }
        }
        return new int[]{columns, steps(level, tops)};
    }

    /** Lays the road's own width over one point of the line: a square, ragged only on the wild band. */
    private static int lay(ServerLevel level, int x, int z, Map<Long, Integer> tops) {
        int half = fromWalls(x, z) <= TRANSITION_BAND ? PAVED_HALF : WILD_HALF;
        int laid = 0;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                int at = x + dx;
                int into = z + dz;
                // The wild band's edge is a block of hash jitter; the paved bands keep a clean edge.
                if (half == WILD_HALF && (Math.abs(dx) == WILD_HALF || Math.abs(dz) == WILD_HALF)
                        && jitter(at, into) % 3 != 0) {
                    continue;
                }
                laid += column(level, at, into, tops);
            }
        }
        return laid;
    }

    /** Paints the top block of one column, unless the column is water, air or a tree's own body. */
    private static int column(ServerLevel level, int x, int z, Map<Long, Integer> tops) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
        BlockPos at = new BlockPos(x, y, z);
        level.getChunk(x >> 4, z >> 4);
        BlockState under = level.getBlockState(at);
        if (under.isAir() || !under.getFluidState().isEmpty()) return 0;
        if (under.is(BlockTags.LEAVES) || under.is(BlockTags.LOGS)) return 0;
        level.setBlock(at, surface(x, z), 2);
        tops.put(key(x, z), y);
        return 1;
    }

    /**
     * Turns the lower of two road columns that differ by one block into a bottom slab, so a rise is a half
     * step. Only road columns count: a step beside a wall is the wall's business, not the road's.
     */
    private static int steps(ServerLevel level, Map<Long, Integer> tops) {
        int stepped = 0;
        for (Map.Entry<Long, Integer> entry : tops.entrySet()) {
            int x = (int) (entry.getKey() >> 32);
            int z = (int) (entry.getKey() & 0xFFFFFFFFL);
            Integer y = entry.getValue();
            for (int[] beside : new int[][]{{x + 1, z}, {x, z + 1}}) {
                Integer other = tops.get(key(beside[0], beside[1]));
                if (other == null) continue;
                int lower = Math.min(y, other);
                if (Math.abs(y - other) != 1 || lower != y) continue;
                level.setBlock(new BlockPos(x, y, z), Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2);
                stepped++;
                break;
            }
        }
        return stepped;
    }

    /** A column's key in the map of what this pass has laid, packed from its x and z. */
    private static long key(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    /**
     * The surface of one block: the three bands of `docs/arda-roads.md`, chosen per block from a hash of x
     * and z so that a chunk border never shows, and so the same column takes the same block whenever asked.
     */
    private static BlockState surface(int x, int z) {
        int near = fromWalls(x, z);
        int roll = jitter(x, z) % 100;
        if (near <= TOWN_BAND) {
            if (roll < 50) return TolkienBlocks.STONE_PATH.get().defaultBlockState();
            if (roll < 77) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 87) return Blocks.STONE_BRICKS.defaultBlockState();
            if (roll < 95) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }
        if (near <= TRANSITION_BAND) {
            if (roll < 50) return Blocks.COBBLESTONE.defaultBlockState();
            if (roll < 77) return Blocks.DIRT_PATH.defaultBlockState();
            if (roll < 87) return Blocks.COARSE_DIRT.defaultBlockState();
            if (roll < 95) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            return Blocks.GRASS_BLOCK.defaultBlockState();
        }
        if (roll < 40) return Blocks.GRAVEL.defaultBlockState();
        if (roll < 65) return Blocks.COARSE_DIRT.defaultBlockState();
        if (roll < 85) return Blocks.GRASS_BLOCK.defaultBlockState();
        return Blocks.DIRT_PATH.defaultBlockState();
    }

    /** How far outside the city's footprint a column stands, in blocks. */
    private static int fromWalls(int x, int z) {
        int dx = Math.max(0, Math.max(CITY_X0 - x, x - CITY_X1));
        int dz = Math.max(0, Math.max(CITY_Z0 - z, z - CITY_Z1));
        return (int) Math.round(Math.hypot(dx, dz));
    }

    /** A stable per-block number, so the scatter survives being asked twice and never lines up on a chunk. */
    private static int jitter(int x, int z) {
        return Math.floorMod(x * 73856093 ^ z * 19349663, 1000);
    }

    /** One of the bridge's own pieces, read from the jar the first time it is asked for. */
    private static StructureTemplate template(ServerLevel level, String name) throws IOException {
        StructureTemplate cached = LOADED.get(name);
        if (cached != null) return cached;
        CompoundTag tag;
        try (InputStream in = RoadCommands.class.getResourceAsStream(
                "/data/tolkienmobs/structure/" + name + ".nbt")) {
            if (in == null) throw new IOException("no " + name + ".nbt in the jar");
            tag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
        StructureTemplate template = new StructureTemplate();
        template.load(level.holderLookup(Registries.BLOCK), tag);
        LOADED.put(name, template);
        return template;
    }
}
