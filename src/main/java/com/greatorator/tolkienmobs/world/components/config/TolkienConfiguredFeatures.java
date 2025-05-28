package com.greatorator.tolkienmobs.world.components.config;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.BramblesBushBlock;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienFeatures;
import com.greatorator.tolkienmobs.world.components.feature.tree.TolkienDecorators;
import com.greatorator.tolkienmobs.world.components.feature.tree.components.SpheroidFoliagePlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchesConfig;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingLargeTrunkPlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
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

public class TolkienConfiguredFeatures {
    private final static int LEAF_SHAG_FACTOR = 24;
    private static final int canopyDistancing = 5;

    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_KEY = registerKey("tree/mallorn");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_MALLORN_KEY = registerKey("tree/hardened_mallorn");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_KEY = registerKey("tree/mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_MIRKWOOD_KEY = registerKey("tree/hardened_mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_KEY = registerKey("tree/fangornoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_FANGORNOAK_KEY = registerKey("tree/hardened_fangornoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLDFORESTOAK_KEY = registerKey("tree/oldforestoak");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_KEY = registerKey("tree/culumalda");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_CULUMALDA_KEY = registerKey("tree/hardened_culumalda");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_KEY = registerKey("tree/lebethron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_LEBETHRON_KEY = registerKey("tree/hardened_lebethron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FIRIEN_KEY = registerKey("tree/culumalda_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_CULUMALDA_FIRIEN_KEY = registerKey("tree/hardened_culumalda_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FIRIEN_KEY = registerKey("tree/lebethron_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_LEBETHRON_FIRIEN_KEY = registerKey("tree/hardened_lebethron_firien");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_KEY = registerKey("tree/deadwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_KEY = registerKey("tree/dwarven_maple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HARDENED_DWARVEN_MAPLE_KEY = registerKey("tree/hardened_dwarven_maple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_BLOOM_DECAY_KEY = registerKey("tree/mushroom_bloom_decay");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOM_DECAY_BLOOM_KEY = registerKey("tree/mushroom_decay_bloom");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_FALLEN_LEAVES = registerKey("ground/mallorn_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_FALLEN_LEAVES = registerKey("ground/mirkwood_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_FALLEN_LEAVES = registerKey("ground/culumalda_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_FALLEN_LEAVES = registerKey("ground/lebethron_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_FALLEN_LEAVES = registerKey("ground/fangornoak_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_MAPLE_FALLEN_LEAVES = registerKey("ground/dwarven_maple_fallen_leaves");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WEBS = registerKey("decoration/webs");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_SPIKE = registerKey("ground/stone_spike");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MALLORN_LOG = registerKey("ground/mallorn_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MIRKWOOD_LOG = registerKey("ground/mirkwood_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CULUMALDA_LOG = registerKey("ground/culumalda_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEBETHRON_LOG = registerKey("ground/lebethron_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FANGORNOAK_LOG = registerKey("ground/fangornoak_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEADWOOD_LOG = registerKey("ground/deadwood_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DWARVEN_LOG = registerKey("ground/dwarven_log");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RANDOM_RUBBLE = registerKey("ground/random_rubble");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROCK_PILE = registerKey("decoration/rock_pile");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STUMPS = registerKey("decoration/stumps");

    public static final ResourceKey<ConfiguredFeature<?, ?>> WOOD_ROOTS_SPREAD = registerKey("ore/wood_roots_spread");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_GRANITE = registerKey("small_granite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_DIORITE = registerKey("small_diorite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_ANDESITE = registerKey("small_andesite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLANT_ROOTS = registerKey("plant_roots");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUD_SPLATTER = registerKey("mud_splatter");

    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_SIMBELMYNE_KEY = registerKey("plants/flower_simbelmyne");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_MIRKWOOD_KEY = registerKey("plants/flower_mirkwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_ALFIRIN_KEY = registerKey("plants/flower_alfirin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_ATHELAS_KEY = registerKey("plants/flower_athelas");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_NIPHREDIL_KEY = registerKey("plants/flower_niphredil");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_SWAMPMILKWEED_KEY = registerKey("plants/flower_swamp_milkweed");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_LILLYOFTHEVALLEY_KEY = registerKey("plants/flower_valley_lilly");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_ELANOR_KEY = registerKey("plants/flower_elanor");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_AEGLOS_KEY = registerKey("plants/flower_aeglos");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_LISSUIN_KEY = registerKey("plants/flower_lissuin");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_MALLOS_KEY = registerKey("plants/flower_mallos");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_BRAMBLES_KEY = registerKey("plants/flower_brambles");

    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_MITHRIL_KEY = registerKey("ore/ore_mithril");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_MITHRIL_KEY = registerKey("ore/nether_ore_mithril");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ORE_MITHRIL_KEY = registerKey("ore/end_ore_mithril");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_MORGULIRON_KEY = registerKey("ore/ore_morguliron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_MORGULIRON_KEY = registerKey("ore/nether_ore_morguliron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ORE_MORGULIRON_KEY = registerKey("ore/end_ore_morguliron");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE_AMMOLITE_KEY = registerKey("ore/ore_ammolite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NETHER_ORE_AMMOLITE_KEY = registerKey("ore/nether_ore_ammolite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> END_ORE_AMMOLITE_KEY = registerKey("ore/end_ore_ammolite");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COAL_ORE = registerKey("coal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> IRON_ORE = registerKey("iron_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GOLD_ORE = registerKey("gold_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> REDSTONE_ORE = registerKey("redstone_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DIAMOND_ORE = registerKey("diamond_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LAPIS_ORE = registerKey("lapis_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_ORE = registerKey("copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLOOM_DECAY_MUSHROOMS = registerKey("bloom_decay_mushrooms");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DECAY_BLOOM_MUSHROOMS = registerKey("decay_bloom_mushrooms");

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?,?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherrackReplaceables = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);

        List<OreConfiguration.TargetBlockState> overworldMithrilOres = List.of(
                OreConfiguration.target(stoneReplaceables, TolkienBlocks.ORE_MITHRIL.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldMorgulironOres = List.of(
                OreConfiguration.target(stoneReplaceables, TolkienBlocks.ORE_MORGULIRON.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, TolkienBlocks.ORE_DEEPSLATE_MORGULIRON.get().defaultBlockState()));
        List<OreConfiguration.TargetBlockState> overworldAmmoliteOres = List.of(
                OreConfiguration.target(stoneReplaceables, TolkienBlocks.ORE_AMMOLITE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, TolkienBlocks.ORE_DEEPSLATE_AMMOLITE.get().defaultBlockState()));

        context.register(COAL_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.COAL_ORE.defaultBlockState(), 16)));
        context.register(IRON_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.IRON_ORE.defaultBlockState(), 9)));
        context.register(GOLD_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.GOLD_ORE.defaultBlockState(), 9)));
        context.register(REDSTONE_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.REDSTONE_ORE.defaultBlockState(), 8)));
        context.register(DIAMOND_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.DIAMOND_ORE.defaultBlockState(), 8)));
        context.register(LAPIS_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.LAPIS_ORE.defaultBlockState(), 7)));
        context.register(COPPER_ORE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.COPPER_ORE.defaultBlockState(), 10)));


        context.register(OVERWORLD_ORE_MITHRIL_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldMithrilOres, 5)));
        context.register(NETHER_ORE_MITHRIL_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(netherrackReplaceables,
                TolkienBlocks.ORE_NETHER_MITHRIL.get().defaultBlockState(), 4)));
        context.register(END_ORE_MITHRIL_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(endReplaceables,
                TolkienBlocks.ORE_END_MITHRIL.get().defaultBlockState(), 6)));
        context.register(OVERWORLD_ORE_MORGULIRON_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldMorgulironOres, 5)));
        context.register(NETHER_ORE_MORGULIRON_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(netherrackReplaceables,
                TolkienBlocks.ORE_NETHER_MORGULIRON.get().defaultBlockState(), 4)));
        context.register(END_ORE_MORGULIRON_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(endReplaceables,
                TolkienBlocks.ORE_END_MORGULIRON.get().defaultBlockState(), 6)));
        context.register(OVERWORLD_ORE_AMMOLITE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(overworldAmmoliteOres, 5)));
        context.register(NETHER_ORE_AMMOLITE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(netherrackReplaceables,
                TolkienBlocks.ORE_NETHER_AMMOLITE.get().defaultBlockState(), 4)));
        context.register(END_ORE_AMMOLITE_KEY, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(endReplaceables,
                TolkienBlocks.ORE_END_AMMOLITE.get().defaultBlockState(), 6)));

        context.register(FLOWER_SIMBELMYNE_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_SIMBELMYNE.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_MIRKWOOD_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_MIRKWOOD.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_ALFIRIN_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_ALFIRIN.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_ATHELAS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_ATHELAS.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_NIPHREDIL_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_NIPHREDIL.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_SWAMPMILKWEED_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_SWAMPMILKWEED.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_LILLYOFTHEVALLEY_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_ELANOR_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_ELANOR.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_AEGLOS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_AEGLOS.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_LISSUIN_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_LISSUIN.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_MALLOS_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_MALLOS.get())
                        ), List.of(Blocks.GRASS_BLOCK))));
        context.register(FLOWER_BRAMBLES_KEY, new ConfiguredFeature<>(Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(TolkienBlocks.FLOWER_BRAMBLES.get()
                                .defaultBlockState().setValue(BramblesBushBlock.AGE, 3))
                        ), List.of(Blocks.GRASS_BLOCK))));

        context.register(MALLORN_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()),
                new BranchingLargeTrunkPlacer(10, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()), 4, 1, 10, 4, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MALLORN.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.LIVING_ROOTS, TolkienDecorators.LIGHTNINGBUG))
                .build()));
        context.register(HARDENED_MALLORN_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()),
                new BranchingLargeTrunkPlacer(10, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MALLORN.get()), 4, 1, 10, 4, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_MALLORN.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.LIVING_ROOTS, TolkienDecorators.LIGHTNINGBUG))
                .build()));
        context.register(MIRKWOOD_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()), 4, 0, 8, 2, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_MIRKWOOD.get()),
                new SpheroidFoliagePlacer(4.5f, 2.25f, ConstantInt.of(0), 1, 0, 0.45f, (int) (LEAF_SHAG_FACTOR * 1.5f)),
                new TwoLayersFeatureSize(4, 1, 1))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_BLOOM, TolkienDecorators.LIVING_ROOTS))
                .build()));
        context.register(HARDENED_MIRKWOOD_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_MIRKWOOD.get()), 4, 0, 8, 2, 0.23, 0.23), false),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_MIRKWOOD.get()),
                new SpheroidFoliagePlacer(4.5f, 2.25f, ConstantInt.of(0), 1, 0, 0.45f, (int) (LEAF_SHAG_FACTOR * 1.5f)),
                new TwoLayersFeatureSize(4, 1, 1))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_BLOOM, TolkienDecorators.LIVING_ROOTS))
                .build()));
        context.register(FANGORNOAK_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()),
                new BranchingLargeTrunkPlacer(6, 6, 6, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_FANGORNOAK.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F), new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)), TolkienDecorators.WALL_BROWN))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(HARDENED_FANGORNOAK_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()),
                new BranchingLargeTrunkPlacer(6, 6, 6, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_FANGORNOAK.get()), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_FANGORNOAK.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, new LeaveVineDecorator(0.25F), new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)), TolkienDecorators.WALL_BROWN))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(CULUMALDA_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_RED))
                .build()));
        context.register(HARDENED_CULUMALDA_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new StraightTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .decorators(ImmutableList.of(TolkienDecorators.WALL_RED))
                .build()));
        context.register(LEBETHRON_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new FancyTrunkPlacer(5, 11, 0),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_LEBETHRON.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .decorators(ImmutableList.of(TolkienDecorators.LOCUST))
                .build()));
        context.register(HARDENED_LEBETHRON_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new FancyTrunkPlacer(5, 11, 0),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_LEBETHRON.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .decorators(ImmutableList.of(TolkienDecorators.LOCUST))
                .build()));
        context.register(DEADWOOD_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DEADWOOD.get()),
                new FancyTrunkPlacer(3, 11, 0),
                BlockStateProvider.simple(Blocks.AIR),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(DWARVEN_MAPLE_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()),
                new BranchingTrunkPlacer(6, 5, 5, 7, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_DWARVEN_MAPLE.get()),
                new SpheroidFoliagePlacer(4.5F, 1.5F, ConstantInt.of(0), 1, 0, -0.25F, 0),
                new TwoLayersFeatureSize(4, 1, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(HARDENED_DWARVEN_MAPLE_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()),
                new BranchingTrunkPlacer(6, 5, 5, 7, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_DWARVEN_MAPLE.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_DWARVEN_MAPLE.get()),
                new SpheroidFoliagePlacer(4.5F, 1.5F, ConstantInt.of(0), 1, 0, -0.25F, 0),
                new TwoLayersFeatureSize(4, 1, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(OLDFORESTOAK_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.DARK_OAK_WOOD),
                new BranchingLargeTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(Blocks.DARK_OAK_WOOD), 4, 0, 10, 4, 0.3, 0.2), false),
                BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(1, 0, 1))
                .decorators(ImmutableList.of(new LeaveVineDecorator(0.25F), new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)), TolkienDecorators.LIGHTNINGBUG, TolkienDecorators.LIVING_ROOTS))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .ignoreVines()
                .build()));
        context.register(CULUMALDA_FIRIEN_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new BranchingTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(HARDENED_CULUMALDA_FIRIEN_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()),
                new BranchingTrunkPlacer(6, 3, 3, 5, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_CULUMALDA.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_CULUMALDA.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(LEBETHRON_FIRIEN_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.LEAVES_LEBETHRON.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(HARDENED_LEBETHRON_FIRIEN_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(TolkienBlocks.WOOD_LEBETHRON.get()), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.HARDENED_LEAVES_LEBETHRON.get()),
                new FancyFoliagePlacer(ConstantInt.of(4), ConstantInt.of(4), 4),
                new ThreeLayersFeatureSize(5, 1, 0, 1, 2, OptionalInt.empty()))
                .dirt(BlockStateProvider.simple(Blocks.GRASS_BLOCK))
                .build()));
        context.register(MUSHROOM_BLOOM_DECAY_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.MUSHROOM_STEM),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.BLOCK_BLOOM_DECAY.get()),
                new SpheroidFoliagePlacer(4.25f, 0F, ConstantInt.of(1), 1, 0, 0F, 0),
                new TwoLayersFeatureSize(11, 0, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.STONE))
                .build()));
        context.register(MUSHROOM_DECAY_BLOOM_KEY, new ConfiguredFeature<>(Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.MUSHROOM_STEM),
                new BranchingTrunkPlacer(10, 5, 5, 12, new BranchesConfig(BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3, 1, 10, 1, 0.3, 0.2), false),
                BlockStateProvider.simple(TolkienBlocks.BLOCK_DECAY_BLOOM.get()),
                new SpheroidFoliagePlacer(4.25f, 0F, ConstantInt.of(1), 1, 0, 0F, 0),
                new TwoLayersFeatureSize(11, 0, canopyDistancing))
                .dirt(BlockStateProvider.simple(Blocks.STONE))
                .build()));

        // Biome Features
        context.register(MALLORN_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.MALLORN_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(MIRKWOOD_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.MIRKWOOD_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(CULUMALDA_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.CULUMALDA_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(LEBETHRON_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.LEBETHRON_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(FANGORNOAK_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.FANGORNOAK_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(DWARVEN_MAPLE_FALLEN_LEAVES, new ConfiguredFeature<>(TolkienFeatures.DWARVEN_MAPLE_FALLEN_LEAVES.get(), FeatureConfiguration.NONE));
        context.register(WEBS, new ConfiguredFeature<>(TolkienFeatures.WEBS.get(), FeatureConfiguration.NONE));
        context.register(STONE_SPIKE, new ConfiguredFeature<>(TolkienFeatures.STONE_SPIKE.get(), FeatureConfiguration.NONE));
        context.register(MALLORN_LOG, new ConfiguredFeature<>(TolkienFeatures.MALLORN_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(MIRKWOOD_LOG, new ConfiguredFeature<>(TolkienFeatures.MIRKWOOD_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(CULUMALDA_LOG, new ConfiguredFeature<>(TolkienFeatures.CULUMALDA_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(LEBETHRON_LOG, new ConfiguredFeature<>(TolkienFeatures.LEBETHRON_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(FANGORNOAK_LOG, new ConfiguredFeature<>(TolkienFeatures.FANGORNOAK_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(DEADWOOD_LOG, new ConfiguredFeature<>(TolkienFeatures.DEADWOOD_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(DWARVEN_LOG, new ConfiguredFeature<>(TolkienFeatures.DWARVEN_LOG.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(RANDOM_RUBBLE, new ConfiguredFeature<>(TolkienFeatures.RANDOM_RUBBLE.get(), FeatureConfiguration.NONE));
        context.register(ROCK_PILE, new ConfiguredFeature<>(TolkienFeatures.ROCK_PILE.get(), FeatureConfiguration.NONE));
        context.register(STUMPS, new ConfiguredFeature<>(TolkienFeatures.STUMPS.get(), FeatureConfiguration.NONE));
        context.register(WOOD_ROOTS_SPREAD, new ConfiguredFeature<>(TolkienFeatures.WOOD_ROOTS.get(), new RootConfig(TolkienDecorators.ROOT_BLEND_PROVIDER, BlockStateProvider.simple(TolkienBlocks.LIVING_ROOTS.get()))));
        context.register(SMALL_GRANITE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.GRANITE.defaultBlockState(), 16)));
        context.register(SMALL_DIORITE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.DIORITE.defaultBlockState(), 16)));
        context.register(SMALL_ANDESITE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), Blocks.ANDESITE.defaultBlockState(), 16)));
        context.register(PLANT_ROOTS, new ConfiguredFeature<>(TolkienFeatures.UNDERGROUND_PLANTS.get(), new BlockStateConfiguration(TolkienBlocks.LIVING_ROOTS.get().defaultBlockState())));
        context.register(MUD_SPLATTER, new ConfiguredFeature<>(TolkienFeatures.MUD_SPLATTER.get(), NoneFeatureConfiguration.INSTANCE));
        context.register(BLOOM_DECAY_MUSHROOMS, new ConfiguredFeature<>(TolkienFeatures.UNDERGROUND_PLANTS.get(), new BlockStateConfiguration(TolkienBlocks.MUSHROOM_BLOOM_DECAY.get().defaultBlockState())));
        context.register(DECAY_BLOOM_MUSHROOMS, new ConfiguredFeature<>(TolkienFeatures.UNDERGROUND_PLANTS.get(), new BlockStateConfiguration(TolkienBlocks.MUSHROOM_DECAY_BLOOM.get().defaultBlockState())));
    }
}