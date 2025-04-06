package com.greatorator.tolkienmobs.world;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.block.custom.BramblesBushBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienFeatures;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.world.components.config.RootConfig;
import com.greatorator.tolkienmobs.world.components.feature.config.HollowLogConfig;
import com.greatorator.tolkienmobs.world.components.feature.tree.TolkienDecorators;
import com.greatorator.tolkienmobs.world.components.feature.tree.components.SpheroidFoliagePlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchesConfig;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingLargeTrunkPlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingTrunkPlacer;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.OptionalInt;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienConfiguredFeatures {
    private final static int LEAF_SHAG_FACTOR = 24;
    private static final int canopyDistancing = 5;

    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_KEY = registerConfiguredKey("tree/mallorn");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_KEY = registerConfiguredKey("tree/mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_KEY = registerConfiguredKey("tree/fangornoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLDFORESTOAK_KEY = registerConfiguredKey("tree/oldforestoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_KEY = registerConfiguredKey("tree/culumalda");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_KEY = registerConfiguredKey("tree/lebethron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FIRIEN_KEY = registerConfiguredKey("tree/culumalda_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FIRIEN_KEY = registerConfiguredKey("tree/lebethron_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_KEY = registerConfiguredKey("tree/deadwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_KEY = registerConfiguredKey("tree/dwarven_maple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_BLOOM_DECAY_KEY = registerConfiguredKey("tree/mushroom_bloom_decay");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_DECAY_BLOOM_KEY = registerConfiguredKey("tree/mushroom_decay_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_FALLEN_LEAVES = registerConfiguredKey("ground/mallorn_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_FALLEN_LEAVES = registerConfiguredKey("ground/mirkwood_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FALLEN_LEAVES = registerConfiguredKey("ground/culumalda_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FALLEN_LEAVES = registerConfiguredKey("ground/lebethron_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_FALLEN_LEAVES = registerConfiguredKey("ground/fangornoak_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_FALLEN_LEAVES = registerConfiguredKey("ground/dwarven_maple_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WEBS = registerConfiguredKey("decoration/webs");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_SPIKE = registerConfiguredKey("ground/stone_spike");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_SMALL_LOG = registerConfiguredKey("ground/mallorn_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_SMALL_LOG = registerConfiguredKey("ground/mirkwood_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_SMALL_LOG = registerConfiguredKey("ground/culumalda_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_SMALL_LOG = registerConfiguredKey("ground/lebethron_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_SMALL_LOG = registerConfiguredKey("ground/fangornoak_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_SMALL_LOG = registerConfiguredKey("ground/deadwood_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_SMALL_LOG = registerConfiguredKey("ground/dwarven_small_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RANDOM_RUBBLE = registerConfiguredKey("ground/random_rubble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROCK_PILE = registerConfiguredKey("decoration/rock_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WOOD_ROOTS_SPREAD = registerConfiguredKey("ore/wood_roots_spread");

    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_SIMBELMYNE_KEY = registerConfiguredKey("plants/flower_simbelmyne");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_MIRKWOOD_KEY = registerConfiguredKey("plants/flower_mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_ALFIRIN_KEY = registerConfiguredKey("plants/flower_alfirin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_ATHELAS_KEY = registerConfiguredKey("plants/flower_athelas");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_NIPHREDIL_KEY = registerConfiguredKey("plants/flower_niphredil");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_SWAMPMILKWEED_KEY = registerConfiguredKey("plants/flower_swamp_milkweed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_LILLYOFTHEVALLEY_KEY = registerConfiguredKey("plants/flower_valley_lilly");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_ELANOR_KEY = registerConfiguredKey("plants/flower_elanor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_AEGLOS_KEY = registerConfiguredKey("plants/flower_aeglos");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_LISSUIN_KEY = registerConfiguredKey("plants/flower_lissuin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_MALLOS_KEY = registerConfiguredKey("plants/flower_mallos");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_BRAMBLES_KEY = registerConfiguredKey("plants/flower_brambles");

    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_MITHRIL_KEY = registerConfiguredKey("ore/ore_mithril");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_MITHRIL_KEY = registerConfiguredKey("ore/nether_ore_mithril");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ORE_MITHRIL_KEY = registerConfiguredKey("ore/end_ore_mithril");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_MORGULIRON_KEY = registerConfiguredKey("ore/ore_morguliron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_MORGULIRON_KEY = registerConfiguredKey("ore/nether_ore_morguliron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ORE_MORGULIRON_KEY = registerConfiguredKey("ore/end_ore_morguliron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_AMMOLITE_KEY = registerConfiguredKey("ore/ore_ammolite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_AMMOLITE_KEY = registerConfiguredKey("ore/nether_ore_ammolite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ORE_AMMOLITE_KEY = registerConfiguredKey("ore/end_ore_ammolite");

    public static final Music FANGORN_AMBIENCE = new Music(TolkienSounds.MUSIC_DISC_MINASTIRITH, 1200, 12000, true);

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?,?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherrackReplaceables = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);

        List<OreConfiguration.TargetBlockState> overworldMithrilOres = List.of(
                OreConfiguration.target(stoneReplaceables, TolkienBlocks.ORE_MITHRIL.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldMorgulironOres = List.of(
                OreConfiguration.target(stoneReplaceables, TolkienBlocks.ORE_MORGULIRON.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, TolkienBlocks.ORE_DEEPSLATE_MORGULIRON.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldAmmoliteOres = List.of(
                OreConfiguration.target(stoneReplaceables, TolkienBlocks.ORE_AMMOLITE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, TolkienBlocks.ORE_DEEPSLATE_AMMOLITE.get().defaultBlockState()));

        registerConfigured(context, OVERWORLD_ORE_MITHRIL_KEY, Feature.ORE, new OreConfiguration(overworldMithrilOres, 5));
        registerConfigured(context, NETHER_ORE_MITHRIL_KEY, Feature.ORE, new OreConfiguration(netherrackReplaceables,
                TolkienBlocks.ORE_NETHER_MITHRIL.get().defaultBlockState(), 4));
        registerConfigured(context, END_ORE_MITHRIL_KEY, Feature.ORE, new OreConfiguration(endReplaceables,
                TolkienBlocks.ORE_END_MITHRIL.get().defaultBlockState(), 6));
        registerConfigured(context, OVERWORLD_ORE_MORGULIRON_KEY, Feature.ORE, new OreConfiguration(overworldMorgulironOres, 5));
        registerConfigured(context, NETHER_ORE_MORGULIRON_KEY, Feature.ORE, new OreConfiguration(netherrackReplaceables,
                TolkienBlocks.ORE_NETHER_MORGULIRON.get().defaultBlockState(), 4));
        registerConfigured(context, END_ORE_MORGULIRON_KEY, Feature.ORE, new OreConfiguration(endReplaceables,
                TolkienBlocks.ORE_END_MORGULIRON.get().defaultBlockState(), 6));
        registerConfigured(context, OVERWORLD_ORE_AMMOLITE_KEY, Feature.ORE, new OreConfiguration(overworldAmmoliteOres, 5));
        registerConfigured(context, NETHER_ORE_AMMOLITE_KEY, Feature.ORE, new OreConfiguration(netherrackReplaceables,
                TolkienBlocks.ORE_NETHER_AMMOLITE.get().defaultBlockState(), 4));
        registerConfigured(context, END_ORE_AMMOLITE_KEY, Feature.ORE, new OreConfiguration(endReplaceables,
                TolkienBlocks.ORE_END_AMMOLITE.get().defaultBlockState(), 6));

        registerConfigured(context, FLOWER_SIMBELMYNE_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_SIMBELMYNE.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_MIRKWOOD_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_MIRKWOOD.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_ALFIRIN_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_ALFIRIN.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_ATHELAS_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_ATHELAS.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_NIPHREDIL_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_NIPHREDIL.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_SWAMPMILKWEED_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_SWAMPMILKWEED.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_LILLYOFTHEVALLEY_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_ELANOR_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_ELANOR.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_AEGLOS_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_AEGLOS.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_LISSUIN_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_LISSUIN.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_MALLOS_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_MALLOS.get())
                        ), List.of(Blocks.GRASS_BLOCK)));
        registerConfigured(context, FLOWER_BRAMBLES_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_BRAMBLES.get()
                                .defaultBlockState().setValue(BramblesBushBlock.AGE, 3))
                        ), List.of(Blocks.GRASS_BLOCK)));

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
        context.register(STONE_SPIKE, new ConfiguredFeature<>(Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(ImmutableList.of(new WeightedPlacedFeature(PlacementUtils.inlinePlaced(features.getOrThrow(STONE_SPIKE)), 0.05F)), PlacementUtils.inlinePlaced(features.getOrThrow(STONE_SPIKE)))));
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