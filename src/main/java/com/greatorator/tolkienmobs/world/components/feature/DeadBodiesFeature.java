package com.greatorator.tolkienmobs.world.components.feature;

import com.greatorator.tolkienmobs.entity.monster.FellSpiritEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * The dead of the marshes: fell spirits lying in the water, and never on the dry ground.
 * <p>
 * The placement hands this feature a column, not a bed: the ocean-floor heightmap gives the first block
 * above the ground, and the pools are dug in the same chunk after the heightmaps were made, so nothing the
 * heightmap says about a column's water can be trusted here. The feature therefore looks for the water
 * itself, a little way around the column it was given, and takes the first solid bed with
 * {@link #MIN_WATER} blocks of water over it. A column with no water in it is refused rather than filled:
 * the marsh is meant to hide its dead in the pools, not to lay them on the mud between them.
 * <p>
 * The body itself is an ordinary fell spirit with its AI off and its lying flag set, so it holds the spot
 * it was laid in - no wandering onto the shore - and the renderer tips it flat. It is persistent, because a
 * body that despawned the moment a player walked away would leave the marsh empty.
 */
public class DeadBodiesFeature extends Feature<NoneFeatureConfiguration> {
    /** How deep the water over a body has to stand, in blocks. */
    private static final int MIN_WATER = 2;
    /** How far from the placed column a bed is looked for, in blocks. */
    private static final int SEARCH = 4;
    /** How many of the marsh's dead one placement may lay, so a pool holds a few and not a single body. */
    private static final int BODIES = 2;
    /** How far below the placed column a bed may be found and still belong to that placement. */
    private static final int DROP = 10;

    public DeadBodiesFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        List<BlockPos> laid = new ArrayList<>();

        // A placement is a patch of marsh, not a single grave: the pool it finds is searched again for
        // another bed, so the water holds a few of the dead rather than one and the rest of it empty.
        for (int attempt = 0; attempt < BODIES; attempt++) {
            BlockPos bed = findBed(level, context.origin(), random, laid);

            if (bed == null || !lay(level, bed, random)) {
                break;
            }
            laid.add(bed);
        }
        return !laid.isEmpty();
    }

    /** Lays one of the marsh's dead on a bed, and answers whether it was added to the level. */
    private static boolean lay(WorldGenLevel level, BlockPos bed, RandomSource random) {
        FellSpiritEntity body = TolkienEntities.ENTITY_TTM_FELLSPIRIT.get()
                .create(level.getLevel(), EntitySpawnReason.CHUNK_GENERATION);
        if (body == null) {
            return false;
        }
        body.snapTo(bed.getX() + 0.5D, bed.getY() + 1.0D, bed.getZ() + 0.5D,
                random.nextFloat() * 360.0F, 0.0F);
        body.setLying(true);
        body.setNoAi(true);
        body.setNoGravity(true);
        body.setPersistenceRequired();
        body.setYHeadRot(body.getYRot());
        return level.getLevel().addFreshEntity(body);
    }

    /**
     * The first solid bed near the placed column with enough water over it to hide a body, or null.
     * <p>
     * Read from the water down rather than from the heightmap: the pool that holds the water was dug after
     * the heightmap this placement was made from, so the heightmap's answer is the ground that was there
     * before the marsh was wet.
     */
    private static BlockPos findBed(WorldGenLevel level, BlockPos origin, RandomSource random,
                                    List<BlockPos> used) {
        // Walk the box from a random corner, so a second body in the same pool is not laid on the first.
        int wayX = random.nextBoolean() ? 1 : -1;
        int wayZ = random.nextBoolean() ? 1 : -1;

        for (int ax = -SEARCH; ax <= SEARCH; ax++) {
            for (int az = -SEARCH; az <= SEARCH; az++) {
                int dx = ax * wayX;
                int dz = az * wayZ;
                for (int y = origin.getY() + 1; y >= origin.getY() - DROP; y--) {
                    BlockPos bed = new BlockPos(origin.getX() + dx, y, origin.getZ() + dz);

                    if (!level.getBlockState(bed).isSolidRender()) {
                        continue;
                    }

                    if (used.contains(bed)) {
                        continue;
                    }
                    if (waterDepth(level, bed) >= MIN_WATER) {
                        return bed;
                    }
                }
            }
        }
        return null;
    }

    /** How many blocks of water stand over a column's floor, read up from the floor itself. */
    private static int waterDepth(WorldGenLevel level, BlockPos floor) {
        BlockPos.MutableBlockPos cursor = floor.mutable();
        int depth = 0;
        while (depth <= 8) {
            cursor.move(0, 1, 0);
            if (!level.getFluidState(cursor).is(FluidTags.WATER)) {
                break;
            }
            depth++;
        }
        return depth;
    }
}
