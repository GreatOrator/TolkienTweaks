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

/**
 * The dead of the marshes: fell spirits lying in the water, and never on the dry ground.
 * <p>
 * The placement hands this feature the top of a column's ocean floor, so the bed the body is laid in is
 * the block under the origin and the water over it is read upward from there. A column whose floor is
 * not solid, or which holds less than {@link #MIN_WATER} blocks of water above it, is refused: a body on
 * the mud of a drained bank, or one breaking the surface, is not what the marshes are.
 * <p>
 * The body itself is an ordinary fell spirit with its AI off and its lying flag set, so it holds the spot
 * it was laid in - no wandering onto the shore - and the renderer tips it flat. It is persistent, because a
 * body that despawned the moment a player walked away would leave the marsh empty.
 */
public class DeadBodiesFeature extends Feature<NoneFeatureConfiguration> {
    /** How deep the water over a body has to stand, in blocks. */
    private static final int MIN_WATER = 2;

    public DeadBodiesFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState bed = level.getBlockState(origin);

        if (!bed.isSolidRender()) {
            return false;
        }
        int depth = waterDepth(level, origin);
        if (depth < MIN_WATER) {
            return false;
        }

        RandomSource random = context.random();
        FellSpiritEntity body = TolkienEntities.ENTITY_TTM_FELLSPIRIT.get()
                .create(level.getLevel(), EntitySpawnReason.CHUNK_GENERATION);
        if (body == null) {
            return false;
        }
        body.snapTo(origin.getX() + 0.5D, origin.getY() + 1.0D, origin.getZ() + 0.5D,
                random.nextFloat() * 360.0F, 0.0F);
        body.setLying(true);
        body.setNoAi(true);
        body.setNoGravity(true);
        body.setPersistenceRequired();
        body.setYHeadRot(body.getYRot());
        return level.getLevel().addFreshEntity(body);
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
