package com.greatorator.tolkienmobs.world;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.greatorator.tolkienmobs.world.feature.config.RootConfig;
import com.greatorator.tolkienmobs.world.tree.TolkienDecorators;
import com.greatorator.tolkienmobs.world.tree.components.SpheroidFoliagePlacer;
import com.greatorator.tolkienmobs.world.tree.placers.BranchesConfig;
import com.greatorator.tolkienmobs.world.tree.placers.BranchingLargeTrunkPlacer;
import com.greatorator.tolkienmobs.world.tree.placers.BranchingTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.OptionalInt;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienConfiguredFeatures {
    private final static int LEAF_SHAG_FACTOR = 24;
    private static final int canopyDistancing = 5;

    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_KEY = registerKey("mallorn");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_KEY = registerKey("mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_KEY = registerKey("fangornoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLDFORESTOAK_KEY = registerKey("oldforestoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_KEY = registerKey("culumalda");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_KEY = registerKey("lebethron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FIRIEN_KEY = registerKey("culumalda_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FIRIEN_KEY = registerKey("lebethron_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_KEY = registerKey("deadwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_KEY = registerKey("dwarven_maple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_BLOOM_DECAY_KEY = registerKey("mushroom_bloom_decay");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_DECAY_BLOOM_KEY = registerKey("mushroom_decay_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FALLEN_LEAVES = registerKey("fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WEBS = registerKey("webs");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOOD_ROOTS_SPREAD = registerKey("ore/wood_roots_spread");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, MALLORN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()),
                new BranchingLargeTrunkPlacer(10, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()), 4, 1, 10, 4, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MALLORN.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.LIVING_ROOTS, TolkienDecorators.LIGHTNINGBUG))
                .build());
        register(context, MIRKWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MIRKWOOD.get()),
                new SpheroidFoliagePlacer(4.5f, 1.5f, ConstantInt.of(0), 1, 0, -0.25f, LEAF_SHAG_FACTOR),
                new TwoLayersFeatureSize(20, 0, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_BLOOM))
                .build());
        register(context, FANGORNOAK_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()),
                new BranchingLargeTrunkPlacer(6, 6, 6, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_FANGORNOAK.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F), TolkienDecorators.WALL_BROWN))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        register(context, CULUMALDA_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_RED))
                .build());
        register(context, LEBETHRON_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new FancyTrunkPlacer(5, 11, 0),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MIRKWOOD.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .decorators(ImmutableList.of(TolkienDecorators.LOCUST))
                .build());
        register(context, DEADWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DEADWOOD.get()),
                new FancyTrunkPlacer(3, 11, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        register(context, DWARVEN_MAPLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()),
                new BranchingTrunkPlacer(6, 5, 5, 7, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_DWARVEN_MAPLE.get()),
                new SpheroidFoliagePlacer(4.5F, 1.5F, ConstantInt.of(0), 1, 0, -0.25F, 0),
                new TwoLayersFeatureSize(4, 1, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        register(context, OLDFORESTOAK_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_WOOD),
                new BranchingLargeTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(Blocks.DARK_OAK_WOOD), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(new LeaveVineDecorator(0.25F), TolkienDecorators.LIGHTNINGBUG, TolkienDecorators.LIVING_ROOTS))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .ignoreVines()
                .build());
        register(context, CULUMALDA_FIRIEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new BranchingTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        register(context, LEBETHRON_FIRIEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_LEBETHRON.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        register(context, MUSHROOM_BLOOM_DECAY_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.MUSHROOM_STEM),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.BLOCK_BLOOM_DECAY.get()),
                new SpheroidFoliagePlacer(4.25f, 0F, ConstantInt.of(1), 1, 0, 0F, 0),
                new TwoLayersFeatureSize(11, 0, canopyDistancing)).dirt(BlockStateProvider.simple(Blocks.STONE)).build());
        register(context, MUSHROOM_DECAY_BLOOM_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.MUSHROOM_STEM),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.BLOCK_DECAY_BLOOM.get()),
                new SpheroidFoliagePlacer(4.25f, 0F, ConstantInt.of(1), 1, 0, 0F, 0),
                new TwoLayersFeatureSize(11, 0, canopyDistancing)).dirt(BlockStateProvider.simple(Blocks.STONE)).build());

            // Biome Features
        context.register(FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatureModifiers.FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(WEBS, new ConfiguredFeature<>(TolkienFeatureModifiers.WEBS.get(), FeatureConfiguration.NONE));
        context.register(WOOD_ROOTS_SPREAD, new ConfiguredFeature<>(TolkienFeatureModifiers.WOOD_ROOTS.get(), new RootConfig(TolkienDecorators.ROOT_BLEND_PROVIDER, BlockStateProvider.simple(TolkienBlocks.LIVING_ROOTS.get()))));

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}