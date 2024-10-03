package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.components.feature.*;
import com.greatorator.tolkienmobs.world.components.config.RootConfig;
import com.greatorator.tolkienmobs.world.components.feature.tree.components.SpheroidFoliagePlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingLargeTrunkPlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.BranchingTrunkPlacer;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.TreeRootsDecorator;
import com.greatorator.tolkienmobs.world.components.feature.tree.placers.TrunkSideDecorator;
import com.greatorator.tolkienmobs.world.components.placements.AvoidLandmarkModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienFeatureModifiers {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, MODID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, MODID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MODID);

    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<SpheroidFoliagePlacer>> FOLIAGE_SPHEROID = FOLIAGE_PLACERS.register("spheroid_foliage_placer", () -> new FoliagePlacerType<>(SpheroidFoliagePlacer.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TreeRootsDecorator>> TREE_ROOTS = TREE_DECORATORS.register("tree_roots", () -> new TreeDecoratorType<>(TreeRootsDecorator.CODEC));
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TrunkSideDecorator>> TRUNKSIDE_DECORATOR = TREE_DECORATORS.register("trunkside_decorator", () -> new TreeDecoratorType<>(TrunkSideDecorator.CODEC));

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<BranchingTrunkPlacer>> TRUNK_BRANCHING = TRUNK_PLACERS.register("branching_trunk_placer", () -> new TrunkPlacerType<>(BranchingTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<BranchingLargeTrunkPlacer>> TRUNK_LARGE_BRANCHING = TRUNK_PLACERS.register("branching_large_trunk_placer", () -> new TrunkPlacerType<>(BranchingLargeTrunkPlacer.CODEC));

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<AvoidLandmarkModifier>> NO_STRUCTURE = registerPlacer("no_structure", () -> () -> AvoidLandmarkModifier.CODEC);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MALLORN_FALLEN_LEAVES = FEATURES.register("mallorn_fallen_leaves", () -> new FallenMallornLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> MIRKWOOD_FALLEN_LEAVES = FEATURES.register("mirkwood_fallen_leaves", () -> new FallenMirkwoodLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CULUMALDA_FALLEN_LEAVES = FEATURES.register("culumalda_fallen_leaves", () -> new FallenCulumaldaLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> LEBETHRON_FALLEN_LEAVES = FEATURES.register("lebethron_fallen_leaves", () -> new FallenLebethronLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> FANGORNOAK_FALLEN_LEAVES = FEATURES.register("fangornoak_fallen_leaves", () -> new FallenFangornOakLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> DWARVEN_MAPLE_FALLEN_LEAVES = FEATURES.register("dwarven_maple_fallen_leaves", () -> new FallenDwarvenMapleLeavesFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> WEBS = FEATURES.register("webs", () -> new WebFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<RootConfig>> WOOD_ROOTS = FEATURES.register("wood_roots", () -> new WoodRootFeature(RootConfig.CODEC));

    private static <P extends PlacementModifier> DeferredHolder<PlacementModifierType<?>, PlacementModifierType<P>> registerPlacer(String name, Supplier<PlacementModifierType<P>> factory) {
        return PLACEMENT_MODIFIERS.register(name, factory);
    }
}