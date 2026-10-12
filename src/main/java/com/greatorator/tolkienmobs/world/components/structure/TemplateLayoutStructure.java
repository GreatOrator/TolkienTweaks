package com.greatorator.tolkienmobs.world.components.structure;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Places rigid NBTs on local terrain, with optional house clusters or a buried companion. */
public class TemplateLayoutStructure extends Structure {
    public static final MapCodec<TemplateLayoutStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.pool),
            Codec.intRange(1, 5).optionalFieldOf("min_count", 1).forGetter(s -> s.minCount),
            Codec.intRange(1, 5).optionalFieldOf("max_count", 1).forGetter(s -> s.maxCount),
            Codec.INT.optionalFieldOf("top_offset").forGetter(s -> s.topOffset),
            StructureTemplatePool.CODEC.optionalFieldOf("companion_pool").forGetter(s -> s.companion)
    ).apply(instance, TemplateLayoutStructure::new));

    private final Holder<StructureTemplatePool> pool;
    private final int minCount;
    private final int maxCount;
    private final Optional<Integer> topOffset;
    private final Optional<Holder<StructureTemplatePool>> companion;
    public static final int MIN_GAP = 5;
    public static final int MAX_GAP = 10;
    private static final int MAX_RADIUS = 112;

    public TemplateLayoutStructure(StructureSettings settings, Holder<StructureTemplatePool> pool,
                                   int minCount, int maxCount, Optional<Integer> topOffset,
                                   Optional<Holder<StructureTemplatePool>> companion) {
        super(settings);
        if (minCount < 1 || maxCount < minCount || maxCount > 5) {
            throw new IllegalArgumentException("Require 1 <= min_count <= max_count <= 5");
        }
        this.pool = pool;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.topOffset = topOffset;
        this.companion = companion;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();
        var pieces = new ArrayList<PoolElementStructurePiece>();
        var first = makePiece(context, pool);
        first.move(x - first.getBoundingBox().minX(), 0, z - first.getBoundingBox().minZ());
        if (!ground(context, first, topOffset)) return Optional.empty();
        pieces.add(first);
        int count = minCount + context.random().nextInt(maxCount - minCount + 1);
        for (int i = 1; i < count; i++) {
            if (!append(context, pieces, pool, topOffset, x, z)) return Optional.empty();
        }
        if (companion.isPresent() && !append(context, pieces, companion.get(), Optional.of(2), x, z)) {
            return Optional.empty();
        }
        var box = first.getBoundingBox();
        int centerX = (box.minX() + box.maxX()) / 2;
        int centerZ = (box.minZ() + box.maxZ()) / 2;
        return Optional.of(new GenerationStub(new BlockPos(centerX, surface(context, centerX, centerZ), centerZ),
                builder -> pieces.forEach(builder::addPiece)));
    }

    private static PoolElementStructurePiece makePiece(GenerationContext context, Holder<StructureTemplatePool> pool) {
        var element = pool.value().getRandomTemplate(context.random());
        var rotation = Rotation.getRandom(context.random());
        var manager = context.structureTemplateManager();
        return new PoolElementStructurePiece(manager, element, BlockPos.ZERO, element.getGroundLevelDelta(),
                rotation, element.getBoundingBox(manager, BlockPos.ZERO, rotation), LiquidSettings.IGNORE_WATERLOGGING);
    }

    private static boolean append(GenerationContext context, List<PoolElementStructurePiece> pieces,
                                  Holder<StructureTemplatePool> pool, Optional<Integer> topOffset, int x, int z) {
        for (int attempt = 0; attempt < 64; attempt++) {
            var piece = makePiece(context, pool);
            var box = piece.getBoundingBox();
            var parent = pieces.get(context.random().nextInt(pieces.size())).getBoundingBox();
            int gap = MIN_GAP + context.random().nextInt(MAX_GAP - MIN_GAP + 1);
            int minX = (parent.minX() + parent.maxX() - box.getXSpan() + 1) / 2;
            int minZ = (parent.minZ() + parent.maxZ() - box.getZSpan() + 1) / 2;
            switch (context.random().nextInt(4)) {
                case 0 -> minX = parent.maxX() + gap + 1;
                case 1 -> minX = parent.minX() - gap - box.getXSpan();
                case 2 -> minZ = parent.maxZ() + gap + 1;
                default -> minZ = parent.minZ() - gap - box.getZSpan();
            }
            piece.move(minX - box.minX(), 0, minZ - box.minZ());
            if (box.minX() < x - MAX_RADIUS || box.maxX() > x + MAX_RADIUS
                    || box.minZ() < z - MAX_RADIUS || box.maxZ() > z + MAX_RADIUS) continue;
            if (pieces.stream().anyMatch(other -> tooClose(box, other.getBoundingBox()))) continue;
            if (!ground(context, piece, topOffset)) continue;
            pieces.add(piece);
            return true;
        }
        return false;
    }

    private static boolean tooClose(BoundingBox a, BoundingBox b) {
        // Horizontal clearance applies even when houses stand at different elevations.
        return a.minX() <= b.maxX() + MIN_GAP && a.maxX() >= b.minX() - MIN_GAP
                && a.minZ() <= b.maxZ() + MIN_GAP && a.maxZ() >= b.minZ() - MIN_GAP;
    }

    private static int surface(GenerationContext context, int x, int z) {
        return context.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());
    }

    private static boolean ground(GenerationContext context, PoolElementStructurePiece piece, Optional<Integer> topOffset) {
        var box = piece.getBoundingBox();
        int x = (box.minX() + box.maxX()) / 2;
        int z = (box.minZ() + box.maxZ()) / 2;
        int ground = surface(context, x, z);
        var biome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(ground),
                QuartPos.fromBlock(z), context.randomState().sampler());
        if (!context.validBiome().test(biome)) return false;
        int bottom = topOffset.map(offset -> ground + offset - box.getYSpan() + 1).orElse(ground);
        if (bottom < context.heightAccessor().getMinBuildHeight()
                || bottom + box.getYSpan() > context.heightAccessor().getMaxBuildHeight()) return false;
        piece.move(0, bottom - box.minY(), 0);
        return true;
    }

    @Override
    public StructureType<?> type() {
        return TolkienStructures.TEMPLATE_LAYOUT.get();
    }
}
