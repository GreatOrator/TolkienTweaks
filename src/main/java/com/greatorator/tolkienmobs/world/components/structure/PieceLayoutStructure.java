package com.greatorator.tolkienmobs.world.components.structure;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A build cut into many templates, every piece placed at the offset it was cut at.
 *
 * <p>{@link TemplateLayoutStructure} seats one to five pieces on local ground, choosing where by ring or chain,
 * which is what a barrow field or a camp needs. A city needs the opposite: five hundred and eighty-four pieces,
 * each one standing exactly where the cut took it from the world, on a floor that is a plane rather than a
 * hillside. So this structure carries its own piece table - template, and the offset from the build's corner -
 * and places every piece there, with no ground reading, no randomness and no rotation.
 *
 * <p>Two consequences worth knowing. The ground under a piece is not consulted, so the build stands or is not
 * there exactly as it was scouted: the terrain has to be right before the first chunk is made, which is why the
 * anchor sits on ground the map draws. And the pieces cost nothing at world start, because a structure's start
 * only lists them: each piece places itself when the chunk it touches is generated, so walking into the valley
 * is what builds the part of it being walked into.
 *
 * <p>The table is written by the data generator from the cut's own {@code .parts} file, so the offsets here are
 * the cut's and not a second transcription of them.
 */
public class PieceLayoutStructure extends Structure {
    public static final MapCodec<PieceLayoutStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            BlockPos.CODEC.fieldOf("base").forGetter(structure -> structure.base),
            Piece.CODEC.listOf().fieldOf("pieces").forGetter(structure -> structure.pieces)
    ).apply(instance, PieceLayoutStructure::new));

    /** One template, and where its own corner goes as a step from the build's corner. */
    public record Piece(Identifier template, BlockPos at) {
        public static final Codec<Piece> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("template").forGetter(Piece::template),
                BlockPos.CODEC.fieldOf("at").forGetter(Piece::at)
        ).apply(instance, Piece::new));
    }

    /** The block the build's own north-west bottom corner stands on. */
    private final BlockPos base;
    private final List<Piece> pieces;

    public PieceLayoutStructure(StructureSettings settings, BlockPos base, List<Piece> pieces) {
        super(settings);
        this.base = base;
        this.pieces = List.copyOf(pieces);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        var manager = context.structureTemplateManager();
        var placed = new ArrayList<PoolElementStructurePiece>(this.pieces.size());
        for (Piece piece : this.pieces) {
            // single() hands back a projection rather than an element, so the piece is made rigid here: a cut's
            // piece stands exactly where it was taken, never mirrored or re-drawn.
            var element = StructurePoolElement.single(piece.template().toString())
                    .apply(StructureTemplatePool.Projection.RIGID);
            var at = this.base.offset(piece.at());
            // Built at the origin and then moved, the way the cluster layouts do it: the piece's own box is the
            // template's size, so the move is what carries the piece to the block the cut took it from.
            var standing = new PoolElementStructurePiece(manager, element, BlockPos.ZERO,
                    element.getGroundLevelDelta(), Rotation.NONE,
                    element.getBoundingBox(manager, BlockPos.ZERO, Rotation.NONE),
                    LiquidSettings.IGNORE_WATERLOGGING);
            var box = standing.getBoundingBox();
            standing.move(at.getX() - box.minX(), at.getY() - box.minY(), at.getZ() - box.minZ());
            placed.add(standing);
        }
        if (placed.isEmpty()) return Optional.empty();
        return Optional.of(new GenerationStub(this.base, builder -> placed.forEach(builder::addPiece)));
    }

    @Override
    public StructureType<?> type() {
        return TolkienStructures.PIECE_LAYOUT.get();
    }
}
