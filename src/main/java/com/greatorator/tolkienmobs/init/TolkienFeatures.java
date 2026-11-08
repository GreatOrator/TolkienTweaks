package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.components.config.RootConfig;
import com.greatorator.tolkienmobs.world.components.feature.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MALLORN_FALLEN_LEAVES = FEATURES.register("mallorn_fallen_leaves", () -> new FallenMallornLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MIRKWOOD_FALLEN_LEAVES = FEATURES.register("mirkwood_fallen_leaves", () -> new FallenMirkwoodLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CULUMALDA_FALLEN_LEAVES = FEATURES.register("culumalda_fallen_leaves", () -> new FallenCulumaldaLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> LEBETHRON_FALLEN_LEAVES = FEATURES.register("lebethron_fallen_leaves", () -> new FallenLebethronLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> FANGORNOAK_FALLEN_LEAVES = FEATURES.register("fangornoak_fallen_leaves", () -> new FallenFangornOakLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> DWARVEN_MAPLE_FALLEN_LEAVES = FEATURES.register("dwarven_maple_fallen_leaves", () -> new FallenDwarvenMapleLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEBS = FEATURES.register("webs", () -> new WebFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> STONE_SPIKE = FEATURES.register("stone_spike", () -> new StoneSpikeFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MOUNTAIN_PEAK = FEATURES.register("mountain_peak", () -> new MountainPeakFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MALLORN_LOG = FEATURES.register("mallorn_log", () -> new FallenMallornLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MIRKWOOD_LOG = FEATURES.register("mirkwood_log", () -> new FallenMirkwoodLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CULUMALDA_LOG = FEATURES.register("culumalda_log", () -> new FallenCulumaldaLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> LEBETHRON_LOG = FEATURES.register("lebethron_log", () -> new FallenLebethronLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> FANGORNOAK_LOG = FEATURES.register("fangornoak_log", () -> new FallenFangornOakLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> DEADWOOD_LOG = FEATURES.register("deadwood_log", () -> new FallenDeadwoodLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> DWARVEN_LOG = FEATURES.register("dwarven_log", () -> new FallenDwarvenMapleLogFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> RANDOM_RUBBLE = FEATURES.register("random_rubble", () -> new RubbleFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> ROCK_PILE = FEATURES.register("rock_pile", () -> new RockpileFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> STUMPS = FEATURES.register("stumps", () -> new StumpFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<RootConfig>> WOOD_ROOTS = FEATURES.register("wood_roots", () -> new WoodRootFeature(RootConfig.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockStateConfiguration>> UNDERGROUND_PLANTS = FEATURES.register("underground_plants", () -> new UndergroundPlantFeature(BlockStateConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MUD_SPLATTER = FEATURES.register("mud_splatter", () -> new MudSplatterFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> NURN_LAKE = FEATURES.register("nurn_lake", () -> new NurnLakeFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> DEAD_BODIES = FEATURES.register("dead_bodies", () -> new DeadBodiesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MARSH_POOL = FEATURES.register("marsh_pool", () -> new MarshPoolFeature(NoneFeatureConfiguration.CODEC));
    // The chapter's default trees: what a wood grows where nothing else has been decided for it. Each is its own
    // feature because each is its own template, and a tree is placed by laying the template down.
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> NATURAL_TREE_1 = FEATURES.register("natural_tree_1",
            () -> new NaturalTreeFeature(NoneFeatureConfiguration.CODEC, Identifier.fromNamespaceAndPath(MODID, "natural/natural_tree_1")));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> NATURAL_TREE_2 = FEATURES.register("natural_tree_2",
            () -> new NaturalTreeFeature(NoneFeatureConfiguration.CODEC, Identifier.fromNamespaceAndPath(MODID, "natural/natural_tree_2")));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> NATURAL_TREE_3 = FEATURES.register("natural_tree_3",
            () -> new NaturalTreeFeature(NoneFeatureConfiguration.CODEC, Identifier.fromNamespaceAndPath(MODID, "natural/natural_tree_3")));
}