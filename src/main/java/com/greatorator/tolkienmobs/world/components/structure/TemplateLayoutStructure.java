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

/**
 * Places rigid NBTs on local terrain, as either a loose chain of neighbours or, in circle mode, a
 * ring of pieces standing gap_min..gap_max blocks apart. Every piece gets its own random facing.
 */
public class TemplateLayoutStructure extends Structure {
    /** Clearance between neighbours of a chain layout, in blocks. */
    public static final int DEFAULT_GAP_MIN = 5;
    public static final int DEFAULT_GAP_MAX = 10;
    private static final int MIN_RADIUS = 1;
    private static final int MAX_RADIUS = 112;
    /** A ring of at most five mounds never needs a wider radius, and the search stays cheap. */
    private static final int RING_LIMIT = 48;
    private static final int RING_ATTEMPTS = 4;
    /** A few degrees of wobble keeps rings from looking stamped without breaking the circle. */
    private static final int RING_JITTER = 5;

    public static final MapCodec<TemplateLayoutStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.pool),
            Codec.intRange(1, 5).optionalFieldOf("min_count", 1).forGetter(s -> s.minCount),
            Codec.intRange(1, 5).optionalFieldOf("max_count", 1).forGetter(s -> s.maxCount),
            Codec.INT.optionalFieldOf("top_offset").forGetter(s -> s.topOffset),
            StructureTemplatePool.CODEC.optionalFieldOf("companion_pool").forGetter(s -> s.companion),
            Codec.intRange(1, 64).optionalFieldOf("gap_min", DEFAULT_GAP_MIN).forGetter(s -> s.gapMin),
            Codec.intRange(1, 64).optionalFieldOf("gap_max", DEFAULT_GAP_MAX).forGetter(s -> s.gapMax),
            Codec.BOOL.optionalFieldOf("circle", false).forGetter(s -> s.circle)
    ).apply(instance, TemplateLayoutStructure::new));

    private final Holder<StructureTemplatePool> pool;
    private final int minCount;
    private final int maxCount;
    private final Optional<Integer> topOffset;
    private final Optional<Holder<StructureTemplatePool>> companion;
    private final int gapMin;
    private final int gapMax;
    private final boolean circle;

    public TemplateLayoutStructure(StructureSettings settings, Holder<StructureTemplatePool> pool,
                                   int minCount, int maxCount, Optional<Integer> topOffset,
                                   Optional<Holder<StructureTemplatePool>> companion) {
        this(settings, pool, minCount, maxCount, topOffset, companion,
                DEFAULT_GAP_MIN, DEFAULT_GAP_MAX, false);
    }

    public TemplateLayoutStructure(StructureSettings settings, Holder<StructureTemplatePool> pool,
                                   int minCount, int maxCount, Optional<Integer> topOffset,
                                   Optional<Holder<StructureTemplatePool>> companion,
                                   int gapMin, int gapMax, boolean circle) {
        super(settings);
        if (minCount < 1 || maxCount < minCount || maxCount > 5) {
            throw new IllegalArgumentException("Require 1 <= min_count <= max_count <= 5");
        }
        if (gapMin < 1 || gapMax < gapMin) {
            throw new IllegalArgumentException("Require 1 <= gap_min <= gap_max");
        }
        this.pool = pool;
        this.minCount = minCount;
        this.maxCount = maxCount;
        this.topOffset = topOffset;
        this.companion = companion;
        this.gapMin = gapMin;
        this.gapMax = gapMax;
        this.circle = circle;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();
        return this.circle ? ring(context, x, z) : chain(context, x, z);
    }

    private static boolean validBiome(GenerationContext context, int x, int y, int z) {
        var biome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y),
                QuartPos.fromBlock(z), context.randomState().sampler());
        return context.validBiome().test(biome);
    }

    /**
     * A cluster laid out as one circle: every piece stands on it at an evenly spaced angle, so the
     * circle's centre - and with it the structure's reported position - stays inside the chunk that
     * owns the structure. A ring that cannot be seated at all is no cluster.
     */
    private Optional<GenerationStub> ring(GenerationContext context, int x, int z) {
        var anchor = makePiece(context, pool);
        var anchorBox = anchor.getBoundingBox();
        anchor.move(x - anchorBox.minX(), 0, z - anchorBox.minZ());
        int centerX = (anchorBox.minX() + anchorBox.maxX()) / 2;
        int centerZ = (anchorBox.minZ() + anchorBox.maxZ()) / 2;
        int centerY = surface(context, centerX, centerZ);
        // One cheap refusal for chunks that cannot hold a cluster at all, before any piece is built.
        if (!validBiome(context, centerX, centerY, centerZ)) return Optional.empty();
        int count = minCount + context.random().nextInt(maxCount - minCount + 1);
        double heading = context.random().nextDouble() * 360.0;
        var seated = seatRing(context, count, heading, centerX, centerZ);
        if (seated == null) return Optional.empty();
        // A piece whose own ground refuses only shortens the ring: "up to max_count" allows that.
        var pieces = new ArrayList<PoolElementStructurePiece>();
        for (var piece : seated) {
            if (ground(context, piece, topOffset)) pieces.add(piece);
        }
        if (pieces.isEmpty()) return Optional.empty();
        return Optional.of(new GenerationStub(new BlockPos(centerX, centerY, centerZ),
                builder -> pieces.forEach(builder::addPiece)));
    }

    /**
     * Seats the cluster on a common circle. The radius is the smallest that keeps every neighbour
     * gap_min..gap_max blocks apart, and the search is geometry only, so a placement scan never pays
     * for a height lookup per radius tried; only the seated pieces get looked up in the world.
     */
    private List<PoolElementStructurePiece> seatRing(GenerationContext context, int count, double heading,
                                                     int centerX, int centerZ) {
        for (int attempt = 0; attempt < RING_ATTEMPTS; attempt++) {
            var pieces = new ArrayList<PoolElementStructurePiece>();
            var angles = new double[count];
            for (int i = 0; i < count; i++) {
                pieces.add(makePiece(context, pool));
                angles[i] = Math.toRadians(heading + 360.0 * i / count
                        + context.random().nextInt(-RING_JITTER, RING_JITTER + 1));
            }
            for (int radius = MIN_RADIUS; radius <= RING_LIMIT; radius++) {
                seatRingAt(pieces, angles, centerX, centerZ, radius);
                if (ringFits(pieces, centerX, centerZ)) return pieces;
            }
        }
        return null;
    }

    private static void seatRingAt(List<PoolElementStructurePiece> pieces, double[] angles,
                                  int centerX, int centerZ, int radius) {
        for (int i = 0; i < pieces.size(); i++) {
            var piece = pieces.get(i);
            var box = piece.getBoundingBox();
            int minX = centerX + (int) Math.round(Math.cos(angles[i]) * radius) - box.getXSpan() / 2;
            int minZ = centerZ + (int) Math.round(Math.sin(angles[i]) * radius) - box.getZSpan() / 2;
            piece.move(minX - box.minX(), 0, minZ - box.minZ());
        }
    }

    /** True when nothing overlaps, near the chunk that owns the cluster, and each piece has a neighbour. */
    private boolean ringFits(List<PoolElementStructurePiece> pieces, int centerX, int centerZ) {
        for (var piece : pieces) {
            var box = piece.getBoundingBox();
            if (box.minX() < centerX - MAX_RADIUS || box.maxX() > centerX + MAX_RADIUS
                    || box.minZ() < centerZ - MAX_RADIUS || box.maxZ() > centerZ + MAX_RADIUS) return false;
        }
        for (int i = 0; i < pieces.size(); i++) {
            var box = pieces.get(i).getBoundingBox();
            int nearest = Integer.MAX_VALUE;
            for (int j = 0; j < pieces.size(); j++) {
                if (i == j) continue;
                int gap = gapXZ(box, pieces.get(j).getBoundingBox());
                if (gap < gapMin) return false;
                nearest = Math.min(nearest, gap);
            }
            if (pieces.size() > 1 && nearest > gapMax) return false;
        }
        return true;
    }

    /** Blocks of clearance along the tighter horizontal axis; negative when the boxes overlap. */
    private static int gapXZ(BoundingBox a, BoundingBox b) {
        int x = Math.max(a.minX() - b.maxX(), b.minX() - a.maxX()) - 1;
        int z = Math.max(a.minZ() - b.maxZ(), b.minZ() - a.maxZ()) - 1;
        return Math.max(x, z);
    }

    private Optional<GenerationStub> chain(GenerationContext context, int x, int z) {
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

    private boolean append(GenerationContext context, List<PoolElementStructurePiece> pieces,
                           Holder<StructureTemplatePool> pool, Optional<Integer> topOffset, int x, int z) {
        for (int attempt = 0; attempt < 64; attempt++) {
            var piece = makePiece(context, pool);
            var box = piece.getBoundingBox();
            var parent = pieces.get(context.random().nextInt(pieces.size())).getBoundingBox();
            int gap = gapMin + context.random().nextInt(gapMax - gapMin + 1);
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

    private boolean tooClose(BoundingBox a, BoundingBox b) {
        // Horizontal clearance applies even when houses stand at different elevations.
        return a.minX() <= b.maxX() + gapMin && a.maxX() >= b.minX() - gapMin
                && a.minZ() <= b.maxZ() + gapMin && a.maxZ() >= b.minZ() - gapMin;
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
        if (!validBiome(context, x, ground, z)) return false;
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
