package com.greatorator.tolkienmobs.world.registration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Arda's terrain: the overworld's own noise, cut to the shape the map draws.
 *
 * <p>The noise still does the work it is good at - the aquifers, the veins, the deep stone and the
 * caves a biome carver goes looking for - but the height it arrives at is replaced with
 * {@link ArdaMap}'s. After the noise has filled a chunk, every column is retargeted: ground above
 * the drawn height is cleared, ground below it is filled with stone, and any column left under the
 * waterline is flooded to sea level. Because the target is a function of the column alone, two
 * chunks that meet agree at their shared border, and because the retarget happens before the surface
 * rules run, the mod's grass, sand, snow and dark stone are laid on the new ground rather than on
 * the noise's.
 *
 * <p>{@link #getBaseColumn} and {@link #getBaseHeight} answer from the same map, which is what keeps
 * structures on the ground: a village reads its site from those two methods, and a village placed
 * from the noise's heights would stand in the air over a drawn plain or bury itself in a drawn
 * mountain. The drawn rivers are cut by the map rather than here, and their beds stand a few blocks
 * under sea level so the water in them is the water the map draws.
 */
public class ArdaChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<ArdaChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(NoiseBasedChunkGenerator::generatorSettings))
            .apply(instance, instance.stable(ArdaChunkGenerator::new)));

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState STONE = Blocks.STONE.defaultBlockState();
    private static final BlockState WATER = Blocks.WATER.defaultBlockState();

    public ArdaChunkGenerator(BiomeSource biomeSource, net.minecraft.core.Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager,
                                                        ChunkAccess centerChunk) {
        return super.fillFromNoise(blender, randomState, structureManager, centerChunk)
                .thenApply(chunk -> {
                    reshape(chunk, randomState);
                    return chunk;
                });
    }

    /** Retargets every column of a chunk to the drawn ground and the drawn water. */
    private void reshape(ChunkAccess chunk, RandomState randomState) {
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int minY = chunk.getMinY();
        int maxY = chunk.getMaxY() - 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockX = minX + x;
                int blockZ = minZ + z;
                ArdaMap.Sample sample = ArdaMap.sample(blockX, blockZ,
                        randomState.sampler().weirdness(), randomState.sampler().erosion());
                int target = Math.max(minY + 1, Math.min(maxY, sample.height()));
                int top = solidTop(chunk, position, blockX, blockZ);

                if (target > top) {
                    for (int y = top + 1; y <= target; y++) {
                        chunk.setBlockState(position.set(blockX, y, blockZ), STONE);
                    }
                } else if (target < top) {
                    for (int y = target + 1; y <= top; y++) {
                        chunk.setBlockState(position.set(blockX, y, blockZ), AIR);
                    }
                }

                // The aquifer only filled what the noise's own terrain left empty under the
                // waterline, so water the map draws over ground the noise made solid has to be
                // laid here instead. Raising a column never needs it: stone was written over any
                // water the noise had put above the old top.
                if (target < ArdaMap.SEA_LEVEL) {
                    for (int y = target + 1; y <= ArdaMap.SEA_LEVEL; y++) {
                        chunk.setBlockState(position.set(blockX, y, blockZ), WATER);
                    }
                }
            }
        }
    }

    /** The topmost block of the noise's own ground in a column, ignoring water and air. */
    private static int solidTop(ChunkAccess chunk, BlockPos.MutableBlockPos position, int blockX, int blockZ) {
        int start = Math.min(chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockX, blockZ), chunk.getMaxY() - 1);
        for (int y = start; y >= chunk.getMinY(); y--) {
            BlockState state = chunk.getBlockState(position.set(blockX, y, blockZ));
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return y;
            }
        }
        return chunk.getMinY();
    }

    /**
     * The drawn surface, for anything that asks the generator instead of a chunk - structure sites,
     * heightmaps and blends all come through here.
     *
     * <p>The type is not decoration. A heightmap that counts fluids - the world surface, and what
     * blocks motion - is read above the water the map stands over its ground, while the ocean floor
     * is the ground itself however deep the water is. Villages are the reason this matters: their
     * site check begins at this height and walks down looking for the first block that is not empty,
     * and a check that started on the bed of a lake went on to approve a flooded site, because soil
     * under water is dry soil. Answering the ocean floor for every type built that village on the
     * bed of the lake the map had drawn over it.
     */
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState) {
        ArdaMap.Sample sample = ArdaMap.sample(x, z, randomState.sampler().weirdness(), randomState.sampler().erosion());
        int ground = sample.height();
        int surface = switch (type) {
            case OCEAN_FLOOR, OCEAN_FLOOR_WG -> ground;
            default -> Math.max(ground, ArdaMap.SEA_LEVEL);
        };
        return Math.max(heightAccessor.getMinY(), Math.min(heightAccessor.getMaxY(), surface + 1));
    }

    /**
     * The drawn column: stone to the drawn height, water over it where the map draws water, air
     * above that. Carvers and blends read this, so a cave cut near the surface is cut in the ground
     * the player will stand on.
     */
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int minY = heightAccessor.getMinY();
        BlockState[] column = new BlockState[heightAccessor.getHeight()];
        Arrays.fill(column, AIR);

        ArdaMap.Sample sample = ArdaMap.sample(x, z, randomState.sampler().weirdness(), randomState.sampler().erosion());
        int top = sample.height();
        int waterTop = top < ArdaMap.SEA_LEVEL ? ArdaMap.SEA_LEVEL : top;

        for (int y = minY; y < minY + column.length; y++) {
            int index = y - minY;
            if (y <= top) {
                column[index] = STONE;
            } else if (y <= waterTop) {
                column[index] = WATER;
            }
        }
        return new NoiseColumn(minY, column);
    }

    /** Names the drawn country on the debug screen, which is how a misplaced border is spotted. */
    @Override
    public void addDebugScreenInfo(List<String> result, RandomState randomState, BlockPos feetPos) {
        ArdaMap.Sample sample = ArdaMap.sample(feetPos.getX(), feetPos.getZ(),
                randomState.sampler().weirdness(), randomState.sampler().erosion());
        result.add("Arda: " + sample.country().name().toLowerCase(java.util.Locale.ROOT)
                + (sample.river() ? " (river)" : "") + " height " + sample.height());
    }
}
