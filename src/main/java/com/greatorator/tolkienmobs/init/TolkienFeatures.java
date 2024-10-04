package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.components.config.RootConfig;
import com.greatorator.tolkienmobs.world.components.feature.*;
import com.greatorator.tolkienmobs.world.components.feature.config.HollowLogConfig;
import net.minecraft.core.registries.Registries;
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
    public static final DeferredHolder<Feature<?>, Feature<HollowLogConfig>> SMALL_LOG = FEATURES.register("small_log", () -> new SmallFallenLogFeature(HollowLogConfig.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> RANDOM_RUBBLE = FEATURES.register("random_rubble", () -> new RubbleFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<BlockStateConfiguration>> ROCK_PILE = FEATURES.register("rock_pile", () -> new RockpileFeature(BlockStateConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<RootConfig>> WOOD_ROOTS = FEATURES.register("wood_roots", () -> new WoodRootFeature(RootConfig.CODEC));
}