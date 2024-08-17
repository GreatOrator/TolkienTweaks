package com.greatorator.tolkienmobs.world;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.tree.TolkienDecorators;
import com.greatorator.tolkienmobs.world.tree.components.SpheroidFoliagePlacer;
import com.greatorator.tolkienmobs.world.tree.placers.BranchesConfig;
import com.greatorator.tolkienmobs.world.tree.placers.BranchingLargeTrunkPlacer;
import com.greatorator.tolkienmobs.world.tree.placers.BranchingTrunkPlacer;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;

import java.util.OptionalInt;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienConfiguredFeatures {
    private final static int LEAF_SHAG_FACTOR = 24;
    private static final int canopyDistancing = 5;

    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_KEY = registerKey("mallorn");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_KEY = registerKey("mirkwood");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, MALLORN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()),
                new BranchingLargeTrunkPlacer(10, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()), 4, 1, 10, 4, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MALLORN.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.LIVING_ROOTS))
                .build());
        register(context, MIRKWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MIRKWOOD.get()),
                new SpheroidFoliagePlacer(4.5f, 1.5f, ConstantInt.of(0), 1, 0, -0.25f, LEAF_SHAG_FACTOR),
                new TwoLayersFeatureSize(20, 0, canopyDistancing)).dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK)).build());

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}