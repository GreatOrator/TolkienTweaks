package com.greatorator.tolkienmobs.block.custom;

import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class MushroomsBlock extends MushroomBlock {
    private final ResourceKey<ConfiguredFeature<?, ?>> featureSupplier;

    public MushroomsBlock(ResourceKey<ConfiguredFeature<?, ?>> feature, Properties properties) {
        super(feature, properties);
        this.featureSupplier = feature;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        net.neoforged.neoforge.common.util.TriState soilDecision = blockstate.canSustainPlant(level, blockpos, net.minecraft.core.Direction.UP, state);
        return blockstate.is(TolkienTags.Blocks.DECAY_GROW_BLOCK)
                ? true
                : soilDecision.isDefault() ? (level.getRawBrightness(pos, 0) < 13 && this.mayPlaceOn(blockstate, level, blockpos)) : soilDecision.isTrue();
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean growMushroom(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        Optional<? extends Holder<ConfiguredFeature<?, ?>>> optional = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(this.featureSupplier);

        // Neo: Fire the BlockGrowFeatureEvent and update the result of the Optional local with the new feature.
        var event = net.neoforged.neoforge.event.EventHooks.fireBlockGrowFeature(level, random, pos, optional.orElse(null));
        if (event.isCanceled()) {
            return false;
        }
        optional = Optional.ofNullable(event.getFeature());

        if (optional.isEmpty()) {
            return false;
        } else {
            level.removeBlock(pos, false);
            if (optional.get().value().place(level, level.getChunkSource().getGenerator(), random, pos)) {
                return true;
            } else {
                level.setBlock(pos, state, 3);
                return false;
            }
        }
    }
}