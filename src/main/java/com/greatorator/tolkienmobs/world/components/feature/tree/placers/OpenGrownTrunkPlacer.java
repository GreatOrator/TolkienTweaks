package com.greatorator.tolkienmobs.world.components.feature.tree.placers;

import com.google.common.collect.Lists;
import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.greatorator.tolkienmobs.util.FeatureLogic;
import com.greatorator.tolkienmobs.util.VoxelBresenhamIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * An oak grown in the open: a foot of roots, a bare bole, and a crown of limbs.
 * <p>
 * The shape is read from the three trees built by hand in {@code structure/natural}, which stand thirteen, twenty-four
 * and forty-six blocks tall with crowns five, eleven and nineteen blocks out from the trunk. What makes them a shape
 * and not a template is where the wood is: the trunk is widest at the ground - a ring of roots three blocks out - and
 * has closed to a single block within six of them; the bole is clear for ten, thirteen or forty blocks; and out of its
 * upper half the limbs leave one by one, spiralling round the trunk so no two stand in line, rising as they go. A
 * template lays that down identically every time; this grows it, so a wood is a wood and not a row of copies.
 * <p>
 * The limbs carry no leaves of their own: each end is handed back as a foliage attachment, which is where the tree's
 * foliage placer crowns it, so the crown's size stays the leaf placer's business and the wood stays this one's.
 */
public class OpenGrownTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<OpenGrownTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> trunkPlacerParts(instance).and(instance.group(
            Codec.intRange(0, 6).fieldOf("foot_radius").forGetter(o -> o.footRadius),
            LimbsConfig.CODEC.fieldOf("limbs").forGetter(o -> o.limbs)
    )).apply(instance, OpenGrownTrunkPlacer::new));

    /** How far the foot's roots reach on the ground layer, before the bole closes over them. */
    private final int footRadius;
    private final LimbsConfig limbs;

    public OpenGrownTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int footRadius, LimbsConfig limbs) {
        super(baseHeight, heightRandA, heightRandB);
        this.footRadius = footRadius;
        this.limbs = limbs;
    }

    @Override
    protected TrunkPlacerType<OpenGrownTrunkPlacer> type() {
        return TolkienFeatureModifiers.TRUNK_OPEN_GROWN.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel level, BiConsumer<BlockPos, BlockState> placer,
                                                            RandomSource random, int height, BlockPos startPos,
                                                            TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> crowns = Lists.newArrayList();

        placeBelowTrunkBlock(level, placer, random, startPos.below(), config);

        // The bole: a round column widest at the ground and a single block the rest of the way up, so the foot reads
        // as roots gathered under the trunk rather than as a square of wood.
        float foot = this.footRadius + 0.5F;
        for (int up = 0; up < height; up++) {
            float radius = boleRadius(foot, up);
            int reach = Mth.ceil(radius);
            BlockPos layer = startPos.above(up);
            for (int x = -reach; x <= reach; x++) {
                for (int z = -reach; z <= reach; z++) {
                    if (x * x + z * z <= radius * radius + 0.35F) {
                        placeLog(level, placer, random, layer.offset(x, 0, z), config);
                    }
                }
            }
        }
        crowns.add(new FoliagePlacer.FoliageAttachment(startPos.above(height), 0, false));

        // The limbs: one every share of the bole's upper half, each a little further round the trunk than the last,
        // and each breaking upward near its end the way an open-grown oak's limbs do.
        int count = this.limbs.count() + random.nextInt(this.limbs.randomAddCount() + 1);
        int first = Math.min(height - 1, Mth.floor(height * this.limbs.start()));
        int span = Math.max(1, height - first);
        float offset = random.nextFloat();
        for (int limb = 0; limb < count; limb++) {
            int at = first + span * limb / Math.max(1, count);
            double angle = (double) limb / Math.max(1, count) + offset + (random.nextDouble() - 0.5D) * 0.06D;
            double length = this.limbs.length() + random.nextDouble() * this.limbs.randomAddLength();
            double pitch = Mth.clamp(this.limbs.pitch() + (random.nextDouble() - 0.5D) * 0.08D, 0.05D, 0.5D);

            BlockPos src = startPos.above(Math.min(at, height - 1));
            BlockPos knee = FeatureLogic.translate(src, length * 0.55D, angle, pitch);
            BlockPos tip = FeatureLogic.translate(knee, length * 0.5D, angle, Math.max(0.08D, pitch - 0.1D));

            limb(level, placer, random, src, knee);
            limb(level, placer, random, knee, tip);
            // The end of a limb is the thickest part of it: the four blocks round the last one are wood as well.
            for (BlockPos end : List.of(tip.east(), tip.west(), tip.south(), tip.north())) {
                placeBark(level, placer, random, end);
            }

            crowns.add(new FoliagePlacer.FoliageAttachment(tip, 0, false));
            if (length > 5.0D) crowns.add(new FoliagePlacer.FoliageAttachment(knee, 0, false));
        }

        return crowns;
    }

    /** The width of the bole at a height: the foot's reach at the ground, closed within a few blocks of it. */
    private static float boleRadius(float foot, int up) {
        float neck = 0.45F;
        float spent = Math.max(1F, foot * 2.2F);
        return Math.max(neck, neck + (foot - neck) * Math.max(0F, 1F - up / spent));
    }

    /** Draws a limb from one point to another, a block at a time. */
    private void limb(WorldGenLevel level, BiConsumer<BlockPos, BlockState> placer, RandomSource random, BlockPos from, BlockPos to) {
        for (BlockPos pos : new VoxelBresenhamIterator(from, to)) {
            placeBark(level, placer, random, pos);
        }
    }

    private void placeBark(WorldGenLevel level, BiConsumer<BlockPos, BlockState> placer, RandomSource random, BlockPos pos) {
        if (TreeFeature.validTreePos(level, pos)) {
            placer.accept(pos, this.limbs.limbProvider().getState(level, random, pos));
        }
    }
}
