package com.greatorator.tolkienmobs.datagen;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.List;

/**
 * The desert village is built out of sandstone, and Nurn's village is built out of it too - but out
 * of the dark stone equivalents. Attaching these processors to the village's pool elements leaves
 * the vanilla templates alone and rewrites their blocks as they are placed, which is how the town
 * centre, the farms and the streets are converted along with the houses. A village that lays this
 * mod's own path block uses the same trick to swap the vanilla dirt path under its streets, and
 * Nurn's roads put its path block down in place of the smooth sandstone they walked on.
 */
public final class TolkienProcessorLists {
    public static final ResourceKey<StructureProcessorList> NURN_DARK_STONE = registerKey("nurn_dark_stone");
    public static final ResourceKey<StructureProcessorList> NURN_ROADS = registerKey("nurn_roads");
    public static final ResourceKey<StructureProcessorList> NURN_FARMS = registerKey("nurn_farms");
    public static final ResourceKey<StructureProcessorList> STONE_PATH_STREETS = registerKey("stone_path_streets");

    private TolkienProcessorLists() {}

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        context.register(NURN_DARK_STONE, new StructureProcessorList(List.of(darkStoneRules())));
        context.register(NURN_ROADS, new StructureProcessorList(ImmutableList.<StructureProcessor>builder()
                .add(pathSurface())
                .add(darkStoneRules())
                .build()));
        context.register(STONE_PATH_STREETS, new StructureProcessorList(List.of(stonePathStreets())));
        // The desert farms swap some of their wheat for beetroot and melon stalks. "minecraft:farm_desert"
        // cannot be read from inside this registry's own bootstrap, so those two rules are repeated
        // here: keep them in step with the vanilla list they mirror. The farm's own rules come before
        // the recolour, so the fence keeps its deadwood and the rest of the plot turns dark stone.
        RuleProcessor crops = new RuleProcessor(List.of(
                new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2F), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState()),
                new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState())));
        context.register(NURN_FARMS, new StructureProcessorList(ImmutableList.<StructureProcessor>builder()
                .add(farmFence())
                .add(darkStoneRules())
                .add(crops)
                .build()));
    }

    /**
     * Every sandstone variant a desert village uses, mapped to the dark stone of the same kind.
     * Sand and plain terracotta come along too: they are the streets and the floors, and a strip of
     * desert yellow through a dark stone village looks like a mistake. The glazed terracotta
     * accents are left alone - they are decoration, not desert building material.
     */
    private static RuleProcessor darkStoneRules() {
        return new RuleProcessor(List.of(
                rule(Blocks.SAND, TolkienBlocks.DARK_STONE.get()),
                rule(Blocks.TERRACOTTA, TolkienBlocks.DARK_STONE.get()),
                rule(Blocks.SANDSTONE, TolkienBlocks.SMOOTH_DARK_STONE.get()),
                rule(Blocks.CUT_SANDSTONE, TolkienBlocks.SMOOTH_DARK_STONE.get()),
                rule(Blocks.SMOOTH_SANDSTONE, TolkienBlocks.SMOOTH_DARK_STONE.get()),
                rule(Blocks.CHISELED_SANDSTONE, TolkienBlocks.CHISELED_DARK_STONE_BRICKS.get()),
                rule(Blocks.SANDSTONE_STAIRS, TolkienBlocks.STAIRS_SMOOTH_DARK_STONE.get()),
                rule(Blocks.SMOOTH_SANDSTONE_STAIRS, TolkienBlocks.STAIRS_SMOOTH_DARK_STONE.get()),
                rule(Blocks.SANDSTONE_SLAB, TolkienBlocks.SLAB_SMOOTH_DARK_STONE.get()),
                rule(Blocks.CUT_SANDSTONE_SLAB, TolkienBlocks.SLAB_SMOOTH_DARK_STONE.get()),
                rule(Blocks.SMOOTH_SANDSTONE_SLAB, TolkienBlocks.SLAB_SMOOTH_DARK_STONE.get()),
                rule(Blocks.SANDSTONE_WALL, TolkienBlocks.WALL_SMOOTH_DARK_STONE.get())));
    }

    /**
     * The block Nurn's streets walk on. The desert streets and the plaza around the well are laid in
     * smooth sandstone and everything around them - the walls, the well, the lamp bases - is the
     * sandstone that becomes dark stone, so this one rule turns the surface into this mod's path
     * block without turning the village's buildings into paths. Its rules come first, so they win:
     * a rule processor stops at the first rule that matches.
     */
    private static RuleProcessor pathSurface() {
        return new RuleProcessor(List.of(rule(Blocks.SMOOTH_SANDSTONE, TolkienBlocks.STONE_PATH.get())));
    }

    /**
     * The rim vanilla's desert farms build around their plots is smooth sandstone stairs - the
     * closest thing a sandstone farm has to a fence - and recolouring it kept the shape but read as
     * stone stairs left in a field. It becomes deadwood logs instead, which both looks like the fence
     * it is standing in for and matches the deadwood the rest of Nurn is built from. The rule runs
     * first, so the recolour below never sees these blocks.
     */
    private static RuleProcessor farmFence() {
        return new RuleProcessor(List.of(
                rule(Blocks.SMOOTH_SANDSTONE_STAIRS, TolkienBlocks.LOG_DEADWOOD.get()),
                rule(Blocks.SANDSTONE_STAIRS, TolkienBlocks.LOG_DEADWOOD.get())));
    }

    /**
     * Vanilla's plains streets with the dirt path they lay swapped for this mod's stone path. A
     * street crossing water becomes a plank bridge and a tenth of the rest grows over, exactly as
     * the vanilla list does it; only the path block that is left over differs. "minecraft:street_plains"
     * cannot be read from inside this registry's own bootstrap, so those rules are repeated here:
     * keep them in step with the vanilla list they mirror.
     */
    private static RuleProcessor stonePathStreets() {
        return new RuleProcessor(List.of(
                new ProcessorRule(new BlockMatchTest(Blocks.DIRT_PATH), new BlockMatchTest(Blocks.WATER),
                        Blocks.OAK_PLANKS.defaultBlockState()),
                new ProcessorRule(new RandomBlockMatchTest(Blocks.DIRT_PATH, 0.1F), AlwaysTrueTest.INSTANCE,
                        Blocks.GRASS_BLOCK.defaultBlockState()),
                new ProcessorRule(new BlockMatchTest(Blocks.GRASS_BLOCK), new BlockMatchTest(Blocks.WATER),
                        Blocks.WATER.defaultBlockState()),
                new ProcessorRule(new BlockMatchTest(Blocks.DIRT), new BlockMatchTest(Blocks.WATER),
                        Blocks.WATER.defaultBlockState()),
                rule(Blocks.DIRT_PATH, TolkienBlocks.STONE_PATH.get())));
    }

    private static ProcessorRule rule(Block from, Block to) {
        return new ProcessorRule(new BlockMatchTest(from), AlwaysTrueTest.INSTANCE, to.defaultBlockState());
    }

    private static ResourceKey<StructureProcessorList> registerKey(String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, TolkienMobsMain.resLoc(name));
    }
}
