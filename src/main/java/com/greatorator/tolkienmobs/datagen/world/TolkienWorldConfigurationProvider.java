package com.greatorator.tolkienmobs.datagen.world;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienFeatureModifiers;
import com.greatorator.tolkienmobs.init.TolkienFeatures;
import com.greatorator.tolkienmobs.world.components.config.RootConfig;
import com.greatorator.tolkienmobs.world.components.feature.config.HollowLogConfig;
import com.greatorator.tolkienmobs.world.components.feature.tree.TolkienDecorators;
import com.greatorator.tolkienmobs.world.components.feature.tree.components.SpheroidFoliagePlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchesConfig;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingLargeTrunkPlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.OptionalInt;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienWorldConfigurationProvider {
    private final static int LEAF_SHAG_FACTOR = 24;
    private static final int canopyDistancing = 5;

    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_KEY = registerConfiguredKey("mallorn");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_KEY = registerConfiguredKey("mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_KEY = registerConfiguredKey("fangornoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLDFORESTOAK_KEY = registerConfiguredKey("oldforestoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_KEY = registerConfiguredKey("culumalda");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_KEY = registerConfiguredKey("lebethron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FIRIEN_KEY = registerConfiguredKey("culumalda_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FIRIEN_KEY = registerConfiguredKey("lebethron_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_KEY = registerConfiguredKey("deadwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_KEY = registerConfiguredKey("dwarven_maple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_BLOOM_DECAY_KEY = registerConfiguredKey("mushroom_bloom_decay");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_DECAY_BLOOM_KEY = registerConfiguredKey("mushroom_decay_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_FALLEN_LEAVES = registerConfiguredKey("mallorn_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_FALLEN_LEAVES = registerConfiguredKey("mirkwood_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FALLEN_LEAVES = registerConfiguredKey("culumalda_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FALLEN_LEAVES = registerConfiguredKey("lebethron_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_FALLEN_LEAVES = registerConfiguredKey("fangornoak_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_FALLEN_LEAVES = registerConfiguredKey("dwarven_maple_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WEBS = registerConfiguredKey("webs");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_SPIKE = registerConfiguredKey("stone_spike");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_SMALL_LOG = registerConfiguredKey("mallorn_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_SMALL_LOG = registerConfiguredKey("mirkwood_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_SMALL_LOG = registerConfiguredKey("culumalda_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_SMALL_LOG = registerConfiguredKey("lebethron_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_SMALL_LOG = registerConfiguredKey("fangornoak_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_SMALL_LOG = registerConfiguredKey("deadwood_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_SMALL_LOG = registerConfiguredKey("dwarven_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RANDOM_RUBBLE = registerConfiguredKey("random_rubble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROCK_PILE = registerConfiguredKey("rock_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOOD_ROOTS_SPREAD = registerConfiguredKey("ore/wood_roots_spread");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?,?>> context) {
        registerConfigured(context, MALLORN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()),
                new BranchingLargeTrunkPlacer(10, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()), 4, 1, 10, 4, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MALLORN.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.LIVING_ROOTS, TolkienDecorators.LIGHTNINGBUG))
                .build());
        registerConfigured(context, MIRKWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()), 4, 0, 8, 2, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MIRKWOOD.get()),
                new SpheroidFoliagePlacer(4.5f, 2.25f, ConstantInt.of(0), 1, 0, 0.45f, (int) (LEAF_SHAG_FACTOR * 1.5f)),
                new TwoLayersFeatureSize(4, 1, 1))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_BLOOM, TolkienDecorators.LIVING_ROOTS))
                .build());
        registerConfigured(context, FANGORNOAK_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()),
                new BranchingLargeTrunkPlacer(6, 6, 6, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_FANGORNOAK.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F), new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)), TolkienDecorators.WALL_BROWN))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        registerConfigured(context, CULUMALDA_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_RED))
                .build());
        registerConfigured(context, LEBETHRON_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new FancyTrunkPlacer(5, 11, 0),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MIRKWOOD.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .decorators(ImmutableList.of(TolkienDecorators.LOCUST))
                .build());
        registerConfigured(context, DEADWOOD_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DEADWOOD.get()),
                new FancyTrunkPlacer(3, 11, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        registerConfigured(context, DWARVEN_MAPLE_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()),
                new BranchingTrunkPlacer(6, 5, 5, 7, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_DWARVEN_MAPLE.get()),
                new SpheroidFoliagePlacer(4.5F, 1.5F, ConstantInt.of(0), 1, 0, -0.25F, 0),
                new TwoLayersFeatureSize(4, 1, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        registerConfigured(context, OLDFORESTOAK_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_WOOD),
                new BranchingLargeTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(Blocks.DARK_OAK_WOOD), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(new LeaveVineDecorator(0.25F), new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)), TolkienDecorators.LIGHTNINGBUG, TolkienDecorators.LIVING_ROOTS))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .ignoreVines()
                .build());
        registerConfigured(context, CULUMALDA_FIRIEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new BranchingTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        registerConfigured(context, LEBETHRON_FIRIEN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_LEBETHRON.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build());
        registerConfigured(context, MUSHROOM_BLOOM_DECAY_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.MUSHROOM_STEM),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.BLOCK_BLOOM_DECAY.get()),
                new SpheroidFoliagePlacer(4.25f, 0F, ConstantInt.of(1), 1, 0, 0F, 0),
                new TwoLayersFeatureSize(11, 0, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.STONE))
                .build());
        registerConfigured(context, MUSHROOM_DECAY_BLOOM_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.MUSHROOM_STEM),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.BLOCK_DECAY_BLOOM.get()),
                new SpheroidFoliagePlacer(4.25f, 0F, ConstantInt.of(1), 1, 0, 0F, 0),
                new TwoLayersFeatureSize(11, 0, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.STONE))
                .build());

        // Biome Features
        context.register(MALLORN_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.MALLORN_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(MIRKWOOD_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.MIRKWOOD_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(CULUMALDA_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.CULUMALDA_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(LEBETHRON_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.LEBETHRON_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(FANGORNOAK_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.FANGORNOAK_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(DWARVEN_MAPLE_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.DWARVEN_MAPLE_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(WEBS, new ConfiguredFeature<>(TolkienFeatures.WEBS.get(), FeatureConfiguration.NONE));
        context.register(STONE_SPIKE, new ConfiguredFeature<>(TolkienFeatures.STONE_SPIKE.get(), FeatureConfiguration.NONE));
        context.register(MALLORN_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_MALLORN.get().defaultBlockState(), TolkienBlocks.LOG_MALLORN.get().defaultBlockState())));
        context.register(MIRKWOOD_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_MIRKWOOD.get().defaultBlockState(), TolkienBlocks.LOG_MIRKWOOD.get().defaultBlockState())));
        context.register(CULUMALDA_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_CULUMALDA.get().defaultBlockState(), TolkienBlocks.LOG_CULUMALDA.get().defaultBlockState())));
        context.register(LEBETHRON_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_LEBETHRON.get().defaultBlockState(), TolkienBlocks.LOG_LEBETHRON.get().defaultBlockState())));
        context.register(FANGORNOAK_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_FANGORNOAK.get().defaultBlockState(), TolkienBlocks.LOG_FANGORNOAK.get().defaultBlockState())));
        context.register(DEADWOOD_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_DEADWOOD.get().defaultBlockState(), TolkienBlocks.LOG_DEADWOOD.get().defaultBlockState())));
        context.register(DWARVEN_SMALL_LOG, new ConfiguredFeature<>(TolkienFeatures.SMALL_LOG.get(), new HollowLogConfig(TolkienBlocks.LOG_DWARVEN_MAPLE.get().defaultBlockState(), TolkienBlocks.LOG_DWARVEN_MAPLE.get().defaultBlockState())));
        context.register(RANDOM_RUBBLE, new ConfiguredFeature<>(TolkienFeatures.RANDOM_RUBBLE.get(), FeatureConfiguration.NONE));
        context.register(ROCK_PILE, new ConfiguredFeature<>(TolkienFeatures.ROCK_PILE.get(), new BlockStateConfiguration(TolkienBlocks.ROCKPILE.get().defaultBlockState())));
        context.register(WOOD_ROOTS_SPREAD, new ConfiguredFeature<>(TolkienFeatures.WOOD_ROOTS.get(), new RootConfig(TolkienDecorators.ROOT_BLEND_PROVIDER, BlockStateProvider.simple(TolkienBlocks.LIVING_ROOTS.get()))));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerConfiguredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void registerConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                                    ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}