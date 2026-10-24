package com.greatorator.tolkienmobs.world.components.structure;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

/**
 * A jigsaw village that will only start on dry, grassy ground. Vanilla's jigsaw structure picks its
 * start from the world surface heightmap, which over a lake is the top of the water, so a village
 * can be placed standing in one. Here the site is checked before any piece is assembled: the column
 * has to be dry, and the surface rule that colours the biome has to leave grass on it, which is
 * exactly what makes Nurn's villages farmland villages rather than lake dwellings.
 * <p>
 * The codec mirrors vanilla's; block states are not readable this early in generation, so the
 * heightmaps and the surface noise stand in for the terrain that will be there.
 */
public class VillageStructure extends Structure {
    public static final MapCodec<VillageStructure> CODEC = RecordCodecBuilder.<VillageStructure>mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, JigsawStructure.MAX_DEPTH).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, JigsawStructure.MAX_TOTAL_STRUCTURE_RANGE).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
            ).apply(instance, VillageStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final Optional<Identifier> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final DimensionPadding dimensionPadding;
    private final LiquidSettings liquidSettings;

    public VillageStructure(Structure.StructureSettings settings, Holder<StructureTemplatePool> startPool,
                            Optional<Identifier> startJigsawName, int maxDepth, HeightProvider startHeight,
                            boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap,
                            int maxDistanceFromCenter, List<PoolAliasBinding> poolAliases,
                            DimensionPadding dimensionPadding, LiquidSettings liquidSettings) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = poolAliases;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    @Override
    protected Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int startY = this.startHeight.sample(context.random(),
                new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos start = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());
        if (!startsOnDryGround(context, start)) {
            return Optional.empty();
        }
        return JigsawPlacement.addPieces(context, this.startPool, this.startJigsawName, this.maxDepth, start,
                this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter,
                PoolAliasLookup.create(this.poolAliases, start, context.seed()),
                this.dimensionPadding, this.liquidSettings);
    }

    /**
     * Whether the column is dry, answered without touching the chunk. The world surface heightmap
     * counts water and the ocean floor heightmap does not, so the two agree only where nothing is
     * floating above the ground. Nurn's floor is dark stone, so a site no longer has to be grass;
     * what it may not be is a lake.
     */
    private static boolean startsOnDryGround(Structure.GenerationContext context, BlockPos start) {
        ChunkGenerator generator = context.chunkGenerator();
        int surface = generator.getBaseHeight(start.getX(), start.getZ(), Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());
        int floor = generator.getBaseHeight(start.getX(), start.getZ(), Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(), context.randomState());
        return surface <= floor;
    }

    @Override
    public StructureType<?> type() {
        return TolkienStructures.VILLAGE.get();
    }
}
