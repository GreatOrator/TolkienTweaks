package com.greatorator.tolkienmobs.world.components.structure.placement;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import java.util.Optional;

/**
 * A build that stands where it was scouted rather than wherever a spread picks.
 * <p>
 * Everything the mod generates today is scattered by vanilla's random spread, which is the right answer for a tent
 * met on the way somewhere: the world chooses, and every world with that country in it has a tent without the build
 * knowing where. It is the wrong answer for a place a page walks to. The city, Elladan's camp and everything the
 * campaign names stand on blocks a scout chose, and a spread cannot be asked for a particular block - its cells are a
 * hash of the seed.
 * <p>
 * So this placement answers for exactly one chunk. The anchor is the block the build was scouted on, and the chunk it
 * falls in is the only chunk that ever starts the structure - which is all a structure needs, because a start's pieces
 * then place themselves as the chunks they touch are made. A build of one piece and a build of five hundred cost this
 * class the same.
 * <p>
 * The anchor is not where the build's own corner goes: pieces start at their chunk's corner, and the structure carries
 * the anchor's place inside the chunk as its own offset. The anchor's job is choosing the chunk. Everything else a
 * placement can carry - the locate offset, the frequency, the salt - keeps vanilla's default, because a pinned build
 * has nothing to reduce or nudge: it stands there or it does not.
 */
public class PinnedStructurePlacement extends StructurePlacement {
    public static final MapCodec<PinnedStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("at").forGetter(PinnedStructurePlacement::at)
    ).apply(instance, PinnedStructurePlacement::new));

    /** The block the build was scouted on, in world coordinates. */
    private final BlockPos at;

    public PinnedStructurePlacement(BlockPos at) {
        super(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, 0,
                Optional.<StructurePlacement.ExclusionZone>empty());
        this.at = at;
    }

    public BlockPos at() {
        return at;
    }

    /**
     * True only in the chunk holding the anchor.
     * <p>
     * Shifting the block down by four is a floor division, so a negative anchor lands in the chunk that actually
     * holds it: -3370 is in chunk -211, which starts at -3376.
     */
    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int chunkX, int chunkZ) {
        return chunkX == (at.getX() >> 4) && chunkZ == (at.getZ() >> 4);
    }

    @Override
    public StructurePlacementType<?> type() {
        return TolkienStructures.PINNED_PLACEMENT.get();
    }
}
