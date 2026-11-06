package com.greatorator.tolkienmobs.world.components.structure;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Optional;

/**
 * A build that was scouted on the ground and stands on it: the pieces of one pool, seated where the anchor says.
 * <p>
 * The mod's scattered structures all start where their chunk's corner is, because the spread chose the chunk and
 * the inside of it is as good as anywhere. A pinned build does not have that freedom: the camp stands on the bench
 * the scout walked, and the bench is not aligned to a chunk. {@code offset} carries the difference between the
 * chunk's corner and the block the build's own corner goes on, so the anchor picks the chunk and the offset places
 * the pieces inside it - two numbers, both written by one datagen pass, that cannot drift apart.
 * <p>
 * The pieces are seated by the ground under them rather than by a height written down: {@code WORLD_SURFACE_WG} is
 * read for the start's column, so a build on a hillside stands on the hillside in every world the drawn ground
 * makes, and the beard fills under it. That is what lets one pin serve every world, which is the whole point of
 * pinning rather than planting.
 */
public class PinnedStructure extends Structure {
    public static final MapCodec<PinnedStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            BlockPos.CODEC.optionalFieldOf("offset", BlockPos.ZERO).forGetter(structure -> structure.offset)
    ).apply(instance, PinnedStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    /** How far the pieces start from the chunk's own north-west corner. */
    private final BlockPos offset;

    public PinnedStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, BlockPos offset) {
        super(settings);
        this.startPool = startPool;
        this.offset = offset;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        BlockPos start = new BlockPos(context.chunkPos().getMinBlockX() + this.offset.getX(),
                this.offset.getY(), context.chunkPos().getMinBlockZ() + this.offset.getZ());
        // One piece from the pool, projected onto the ground it stands on. The start is a jigsaw start with no
        // connectors to follow, which is how a rigid NBT is placed - the same path every other pinned build takes.
        return JigsawPlacement.addPieces(context, this.startPool, Optional.empty(), 1, start, false,
                Optional.of(Heightmap.Types.WORLD_SURFACE_WG), new JigsawStructure.MaxDistance(80),
                PoolAliasLookup.EMPTY, JigsawStructure.DEFAULT_DIMENSION_PADDING,
                JigsawStructure.DEFAULT_LIQUID_SETTINGS);
    }

    @Override
    public StructureType<?> type() {
        return TolkienStructures.PINNED.get();
    }
}
