package com.greatorator.tolkienmobs.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A lake for Nurn's bitter sea, and the meadow that grows around it. Nurn's floor is dark stone;
 * grass only grows near water, and up here that means the ground a lake lays green around its own
 * water, at most {@link #SHORE_WIDTH} blocks from the water's edge.
 * <p>
 * Nurn's ground is eroded badlands country, so it is almost never level over a lake sized disc, and
 * a bowl that insisted on a level site would never be dug. Instead the water line is taken from the
 * lowest ground in the disc, and only the ground within {@link #MAX_BANK} blocks of that line is
 * dug: the higher ground is left standing as the bank. What is left after that is cut down to its
 * largest connected body, so a lake is one sheet of water rather than a scatter of puddles, and a
 * site whose pond would be smaller than {@link #MIN_POND} columns is refused.
 * <p>
 * Every block that will touch water must already be solid, so the bowl is never open to a cave, and
 * the water is placed as source blocks at one level, so a lake cannot drain itself once the chunk is
 * live. The shore is then laid in grass, up to {@link #SHORE_WIDTH} blocks out from the water's edge,
 * on ground that stands no more than {@link #MAX_SHORE_RISE} blocks above the water line.
 * <p>
 * A feature may only write to the chunks one step from the one being decorated, and a write past
 * that is refused block by block with a log line rather than clipped cleanly - a lake that reached
 * further used to leave a straight wall of missing water and a log full of "far chunk" errors. The
 * lake is therefore anchored near the middle of its own chunk, which leaves {@link #WRITE_LIMIT}
 * blocks of room in every direction, and its water and shore are both kept inside that room.
 */
public class NurnLakeFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MIN_RADIUS = 8;
    private static final int MAX_RADIUS = 16;
    /** Depth at the centre of the bowl; the rim is always one block deep. */
    private static final int MAX_DEPTH = 6;
    /**
     * How far above the water line ground may stand and still be dug. Anything higher is a bank, so
     * the lake sits in the low ground of its site rather than under a cliff of its own making.
     */
    private static final int MAX_BANK = 4;
    /** The smallest connected body of water that is worth calling a lake, in columns. */
    private static final int MIN_POND = 60;
    /** How wide the band of grass around the water is laid. */
    private static final int SHORE_WIDTH = 16;
    /**
     * How far above the water line the shore may stand. Higher ground stays dark stone, so a lake
     * is ringed by a meadow rather than by green cliffs.
     */
    private static final int MAX_SHORE_RISE = 12;
    /**
     * How far a lake may reach from the middle of its chunk. A feature may write to the chunks one
     * step away from the decorated one at most, and the middle of a chunk sits {@link #WRITE_LIMIT}
     * blocks from that window's edge.
     */
    private static final int WRITE_LIMIT = 23;

    private static final BlockState WATER = Blocks.WATER.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState GRASS = Blocks.GRASS_BLOCK.defaultBlockState();

    public NurnLakeFeature(Codec<NoneFeatureConfiguration> configIn) {
        super(configIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        if (origin.getY() <= level.getMinBuildHeight() + 4) {
            return false;
        }
        // Anchored inside the decorated chunk rather than wherever the placement modifier put the
        // origin, so the whole lake has the room the write window allows.
        int chunkX = SectionPos.blockToSectionCoord(origin.getX());
        int chunkZ = SectionPos.blockToSectionCoord(origin.getZ());
        int centerX = chunkX * 16 + 7 + random.nextInt(2);
        int centerZ = chunkZ * 16 + 7 + random.nextInt(2);
        int limit = Math.min(Math.min(centerX - (chunkX - 1) * 16, (chunkX + 1) * 16 + 15 - centerX),
                Math.min(centerZ - (chunkZ - 1) * 16, (chunkZ + 1) * 16 + 15 - centerZ));
        int radius = Math.min(MIN_RADIUS + random.nextInt(MAX_RADIUS - MIN_RADIUS + 1), Math.min(limit, WRITE_LIMIT) - 6);
        BlockPos center = new BlockPos(centerX, origin.getY(), centerZ);

        int lowest = Integer.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                lowest = Math.min(lowest, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX + x, centerZ + z));
            }
        }
        // One block under the lowest ground, so no shore column ends up hanging over the water.
        int waterLevel = lowest - 1;
        if (waterLevel <= level.getMinBuildHeight() + 1) {
            return false;
        }

        // Every column the lake could cover is measured first, so a refused site leaves no half dug
        // hole behind and the shape of the pond is known before a single block moves.
        List<BlockPos> candidates = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                int ground = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, centerX + x, centerZ + z);
                if (ground > lowest + MAX_BANK) {
                    continue;
                }
                BlockPos column = new BlockPos(centerX + x, 0, centerZ + z);
                if (!canCarve(level, column, waterLevel, radius, x, z)) {
                    continue;
                }
                candidates.add(column);
            }
        }
        List<BlockPos> pond = largestBody(candidates);
        if (pond.size() < MIN_POND) {
            return false;
        }
        for (BlockPos column : pond) {
            carve(level, column, waterLevel, radius, column.getX() - centerX, column.getZ() - centerZ);
        }
        layShore(level, center, Math.min(radius + SHORE_WIDTH, limit), waterLevel);
        return true;
    }

    /** The depth of this column's bowl, at least one so the rim is under water too. */
    private static int depthAt(int radius, int x, int z) {
        double distance = Math.sqrt(x * x + z * z) / radius;
        return 1 + Mth.floor(MAX_DEPTH * (1.0D - distance * distance));
    }

    /**
     * A column may be dug when its ground reaches the water line, when its top block is real
     * terrain rather than an existing pool, and when everything the water will touch - the bowl
     * walls and its floor included - is solid.
     */
    private static boolean canCarve(WorldGenLevel level, BlockPos column, int waterLevel, int radius, int dx, int dz) {
        int groundTop = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, column.getX(), column.getZ()) - 1;
        int bottom = waterLevel - depthAt(radius, dx, dz);
        if (groundTop < waterLevel || bottom <= level.getMinBuildHeight()) {
            return false;
        }
        for (int y = bottom; y <= groundTop; y++) {
            if (!level.getBlockState(new BlockPos(column.getX(), y, column.getZ())).blocksMotion()) {
                return false;
            }
        }
        return true;
    }

    /** The biggest four neighbour connected body of candidate columns, which becomes the lake. */
    private static List<BlockPos> largestBody(List<BlockPos> candidates) {
        Set<BlockPos> remaining = new HashSet<>(candidates);
        List<BlockPos> largest = List.of();
        while (!remaining.isEmpty()) {
            BlockPos start = remaining.iterator().next();
            remaining.remove(start);
            List<BlockPos> body = new ArrayList<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            body.add(start);
            while (!queue.isEmpty()) {
                BlockPos column = queue.poll();
                for (int[] step : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                    BlockPos neighbour = new BlockPos(column.getX() + step[0], 0, column.getZ() + step[1]);
                    if (remaining.remove(neighbour)) {
                        body.add(neighbour);
                        queue.add(neighbour);
                    }
                }
            }
            if (body.size() > largest.size()) {
                largest = body;
            }
        }
        return largest;
    }

    private void carve(WorldGenLevel level, BlockPos column, int waterLevel, int radius, int dx, int dz) {
        int groundTop = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, column.getX(), column.getZ()) - 1;
        int bottom = waterLevel - depthAt(radius, dx, dz);
        for (int y = groundTop; y > waterLevel; y--) {
            setBlock(level, new BlockPos(column.getX(), y, column.getZ()), AIR);
        }
        for (int y = waterLevel; y >= bottom; y--) {
            BlockPos position = new BlockPos(column.getX(), y, column.getZ());
            if (!level.getBlockState(position).is(BlockTags.FEATURES_CANNOT_REPLACE)) {
                setBlock(level, position, WATER);
            }
        }
    }

    /**
     * Grass over the dry ground around the water, out to {@code outer} blocks, so that the biome's
     * only grass is the meadow around a lake. Water is left alone, high ground above
     * {@link #MAX_SHORE_RISE} stays dark stone, and anything a structure claimed is left alone.
     */
    private void layShore(WorldGenLevel level, BlockPos center, int outer, int waterLevel) {
        for (int x = -outer; x <= outer; x++) {
            for (int z = -outer; z <= outer; z++) {
                if (x * x + z * z > outer * outer) {
                    continue;
                }
                int worldX = center.getX() + x;
                int worldZ = center.getZ() + z;
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ) - 1;
                if (top < waterLevel || top > waterLevel + MAX_SHORE_RISE) {
                    continue;
                }
                BlockPos position = new BlockPos(worldX, top, worldZ);
                BlockState state = level.getBlockState(position);
                if (state.is(BlockTags.FEATURES_CANNOT_REPLACE) || !state.blocksMotion()) {
                    continue;
                }
                if (state.is(GRASS.getBlock())) {
                    continue;
                }
                setBlock(level, position, GRASS);
            }
        }
    }
}
