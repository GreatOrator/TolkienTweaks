package com.greatorator.tolkienmobs.world.registration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.Arrays;
import java.util.EnumSet;
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
 *
 * <p>Two things the reshape has to do for the steps that come after it. The heightmaps are re-read
 * from the ground it wrote, because world generation places from those heightmaps and a chunk that
 * kept the noise's surface would have the surface rules dress ground that is no longer the surface
 * and leave the drawn ground bare stone, with trees and lakes set inside the hills the map raised.
 * And the drawn water is laid again after the carvers, which cut from the noise's terrain rather
 * than the map's: see {@link #seal}.
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
                    republishGround(chunk);
                    return chunk;
                });
    }

    /**
     * The drawn ground is dressed with {@link TolkienSurfaceRules#ardaSurface()}, not with the noise
     * settings' own rule.
     *
     * <p>The overworld settings Arda reuses carry the overworld's rule, and TerraBlender wraps it so
     * that a mod biome is painted by the mod's rule - but that rule is guarded by
     * {@code abovePreliminarySurface}, which asks how high the noise's own density says the ground
     * is. Arda keeps the noise and throws its height away, so the guard is measured against ground
     * the player never sees and most drawn columns fail it: the materials were skipped and the
     * ground stayed the stone the reshape wrote. Arda therefore lays the surface itself, from the
     * same materials without the guard (see {@link TolkienSurfaceRules#ardaSurface()}).
     *
     * <p>The noise chunk is the one the chunk already made for its own noise, as the vanilla surface
     * pass uses it: the surface system reads its preliminary surface, its aquifers and its carver
     * blending from there, and a second one built here would disagree with the ground it is
     * dressing.
     */
    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState,
                             ChunkAccess chunk) {
        if (SharedConstants.debugVoidTerrain(chunk.getPos()) || SharedConstants.DEBUG_DISABLE_SURFACE) {
            return;
        }

        NoiseGeneratorSettings settings = this.generatorSettings().value();
        NoiseChunk noiseChunk = chunk.getOrCreateNoiseChunk(c -> NoiseChunk.forChunk(c, randomState,
                Beardifier.forStructuresInChunk(structureManager, c.getPos()), settings, fluidPicker(settings),
                Blender.of(region)));
        randomState.surfaceSystem().buildSurface(
                randomState,
                region.getBiomeManager(),
                region.registryAccess().lookupOrThrow(Registries.BIOME),
                settings.useLegacyRandomSource(),
                new WorldGenerationContext(this, region),
                chunk,
                noiseChunk,
                TolkienSurfaceRules.ardaSurface());
    }

    /** The water and lava a column is made of, built the way the vanilla generator builds it. */
    private static Aquifer.FluidPicker fluidPicker(NoiseGeneratorSettings settings) {
        Aquifer.FluidStatus lava = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int seaLevel = settings.seaLevel();
        Aquifer.FluidStatus sea = new Aquifer.FluidStatus(seaLevel, settings.defaultFluid());
        Aquifer.FluidStatus empty = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return (x, y, z) -> SharedConstants.DEBUG_DISABLE_FLUID_GENERATION
                ? empty
                : (y < Math.min(-54, seaLevel) ? lava : sea);
    }

    /**
     * Carving happens after the ground is drawn, so the drawn water is laid again once it is over.
     *
     * <p>A carver cuts where the noise's own terrain was: a cave is dug where the noise would have
     * been underground, which is not always where the drawn ground is, and the aquifer fills what
     * the carver takes out of the noise's water table rather than the drawn one. So a cave cut
     * through the bed of a drawn sea or river, or through the ground under the water the reshape
     * laid, comes out as an air pocket with the drawn water standing over it - water hanging in the
     * air where the map draws a sea. Every column the map floods is therefore laid again here: a
     * floor of stone if the carver took the bed, and water over it up to the waterline. Caves the
     * carver cut below the drawn bed, enclosed in rock the way a cave should be, are left alone.
     */
    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager,
                             StructureManager structureManager, ChunkAccess chunk) {
        super.applyCarvers(region, seed, randomState, biomeManager, structureManager, chunk);
        seal(chunk, randomState);
        republishGround(chunk);
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

                // The noise's own caves are the height the noise arrived at, which is not the
                // height the map draws: a void cut for a mountain that Arda draws as a plain is a
                // hole under a field, and the surface rules would dress its floor as ground. Rock
                // is therefore laid over every void below the drawn top. The carvers cut the caves
                // afterwards, from this column (see getBaseColumn), so the caves a player walks into
                // are cut into the drawn ground and the only voids left under it are the ones they
                // made.
                for (int y = minY; y <= target; y++) {
                    if (chunk.getBlockState(position.set(blockX, y, blockZ)).isAir()) {
                        chunk.setBlockState(position, STONE);
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

    /** Lays the map's water and the drawn bed it stands on back over every column the map floods. */
    private void seal(ChunkAccess chunk, RandomState randomState) {
        BlockPos.MutableBlockPos position = new BlockPos.MutableBlockPos();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int maxY = chunk.getMaxY() - 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockX = minX + x;
                int blockZ = minZ + z;
                ArdaMap.Sample sample = ArdaMap.sample(blockX, blockZ,
                        randomState.sampler().weirdness(), randomState.sampler().erosion());
                int bed = Math.min(maxY, sample.height());
                if (bed >= ArdaMap.SEA_LEVEL) {
                    continue;
                }

                if (chunk.getBlockState(position.set(blockX, bed, blockZ)).isAir()) {
                    chunk.setBlockState(position, STONE);
                }
                for (int y = bed + 1; y <= ArdaMap.SEA_LEVEL; y++) {
                    if (chunk.getBlockState(position.set(blockX, y, blockZ)).isAir()) {
                        chunk.setBlockState(position, WATER);
                    }
                }
            }
        }
    }

    /**
     * Re-reads a chunk's ground into the two heightmaps world generation reads. The reshape writes
     * blocks straight into the sections, which the heightmaps do not notice, and the surface rules
     * walk down from {@code WORLD_SURFACE_WG} while the features and the structures place from it.
     */
    private static void republishGround(ChunkAccess chunk) {
        Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.OCEAN_FLOOR_WG));
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
