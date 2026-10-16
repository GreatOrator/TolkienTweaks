package com.greatorator.tolkienmobs.world.components.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
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
 * A lake for Nurn's bitter sea. The vanilla lake feature works inside a sixteen block square, less
 * than one chunk, which reads as a pond; this one digs a bowl wide enough to be a small sea, up to
 * forty eight blocks across - three chunks, inside the four chunk ceiling.
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
 * live. The shore is then laid in grass, {@link #SHORE_WIDTH} blocks out from the water's edge, which
 * is what keeps every lake in the biome bordered by grass instead of by bare dark stone.
 */
public class NurnLakeFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 24;
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
        int radius = MIN_RADIUS + random.nextInt(MAX_RADIUS - MIN_RADIUS + 1);

        int lowest = Integer.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                lowest = Math.min(lowest, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX() + x, origin.getZ() + z));
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
                int ground = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, origin.getX() + x, origin.getZ() + z);
                if (ground > lowest + MAX_BANK) {
                    continue;
                }
                BlockPos column = new BlockPos(origin.getX() + x, 0, origin.getZ() + z);
                if (!canCarve(level, column, waterLevel, radius, x, z)) {
                    continue;
                }
                candidates.add(column);
            }
        }
        List<BlockPos> pond = largestBody(candidates, origin);
        if (pond.size() < MIN_POND) {
            return false;
        }
        for (BlockPos column : pond) {
            carve(level, column, waterLevel, radius, column.getX() - origin.getX(), column.getZ() - origin.getZ());
        }
        layShore(level, origin, radius, waterLevel);
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
    private static List<BlockPos> largestBody(List<BlockPos> candidates, BlockPos origin) {
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
     * Grass over the dry ground around the water, out to {@link #SHORE_WIDTH} blocks, so that every
     * lake is bordered by grass whatever the biome's stone noise is doing nearby. Ground below the
     * water line is left to the water, and anything a structure claimed is left alone.
     */
    private void layShore(WorldGenLevel level, BlockPos origin, int radius, int waterLevel) {
        int outer = radius + SHORE_WIDTH;
        for (int x = -outer; x <= outer; x++) {
            for (int z = -outer; z <= outer; z++) {
                if (x * x + z * z > outer * outer || x * x + z * z <= radius * radius) {
                    continue;
                }
                int worldX = origin.getX() + x;
                int worldZ = origin.getZ() + z;
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ) - 1;
                if (top < waterLevel) {
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
