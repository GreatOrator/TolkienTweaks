package com.greatorator.tolkienmobs.world.components.structure;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;

public class SurfaceStructures extends Structure {

    // Supports larger jigsaw layouts while sharing the standalone NBT placement path.
    public static final MapCodec<SurfaceStructures> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(SurfaceStructures.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings),
                    Codec.BOOL.optionalFieldOf("use_surface_biome", false).forGetter(structure -> structure.useSurfaceBiome)
            ).apply(instance, SurfaceStructures::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int size;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;
    private final boolean useSurfaceBiome;

    public SurfaceStructures(Structure.StructureSettings config,
                             Holder<StructureTemplatePool> startPool,
                             Optional<ResourceLocation> startJigsawName,
                             int size,
                             HeightProvider startHeight,
                             Optional<Heightmap.Types> projectStartToHeightmap,
                             int maxDistanceFromCenter,
                             DimensionPadding dimensionPadding,
                             LiquidSettings liquidSettings,
                             boolean useSurfaceBiome)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
        this.useSurfaceBiome = useSurfaceBiome;
    }

    /** Defaults for a standalone, rigid NBT projected onto the terrain surface. */
    public SurfaceStructures(StructureSettings settings, Holder<StructureTemplatePool> startPool, int heightOffset) {
        this(settings, startPool, heightOffset, 1);
    }

    public SurfaceStructures(StructureSettings settings, Holder<StructureTemplatePool> startPool,
                             int heightOffset, int depth) {
        this(settings, startPool, Optional.empty(), depth,
                ConstantHeight.of(VerticalAnchor.absolute(heightOffset)),
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), 80,
                JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS, false);
    }

    /** Underground pieces use absolute height; biome eligibility follows the land above them. */
    public static SurfaceStructures underground(StructureSettings settings, Holder<StructureTemplatePool> pool,
                                                int heightOffset, int depth) {
        return new SurfaceStructures(settings, pool, Optional.empty(), depth,
                UniformHeight.of(VerticalAnchor.absolute(-32 + heightOffset), VerticalAnchor.absolute(heightOffset)),
                Optional.empty(), 80, JigsawStructure.DEFAULT_DIMENSION_PADDING,
                JigsawStructure.DEFAULT_LIQUID_SETTINGS, true);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        int startY = this.startHeight.sample(context.random(),
                new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        // Structure's normal biome filter controls where this may generate. There
        // is no arbitrary altitude cap, so mountain biomes work as well as plains.
        var stub = JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName,
                this.size, blockPos, false, this.projectStartToHeightmap,
                this.maxDistanceFromCenter, PoolAliasLookup.EMPTY, this.dimensionPadding, this.liquidSettings);
        if (!this.useSurfaceBiome) return stub;
        // Only move the biome sampling point, never the generated pieces. This
        // lets a cave biome below Hithaeglir/Mirkwood coexist with our structures.
        return stub.map(start -> {
            var pos = start.position();
            int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(),
                    Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
            return new GenerationStub(new BlockPos(pos.getX(), surfaceY, pos.getZ()), start.generator());
        });
    }

    @Override
    public StructureType<?> type() {
        return TolkienStructures.SURFACE_STRUCTURES.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}