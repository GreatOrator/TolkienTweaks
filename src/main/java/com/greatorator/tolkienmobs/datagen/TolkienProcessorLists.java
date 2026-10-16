package com.greatorator.tolkienmobs.datagen;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.List;

/**
 * The desert village is built out of sandstone, and Nurn's village is built out of it too - but out
 * of the dark stone equivalents. Attaching these processors to the village's pool elements leaves
 * the vanilla templates alone and rewrites their blocks as they are placed, which is how the town
 * centre, the farms and the streets are converted along with the houses.
 */
public final class TolkienProcessorLists {
    public static final ResourceKey<StructureProcessorList> NURN_DARK_STONE = registerKey("nurn_dark_stone");
    public static final ResourceKey<StructureProcessorList> NURN_FARMS = registerKey("nurn_farms");

    private TolkienProcessorLists() {}

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        context.register(NURN_DARK_STONE, new StructureProcessorList(List.of(darkStoneRules())));
        // The desert farms swap some of their wheat for beetroot and melon stalks. "minecraft:farm_desert"
        // cannot be read from inside this registry's own bootstrap, so those two rules are repeated
        // here: keep them in step with the vanilla list they mirror.
        RuleProcessor crops = new RuleProcessor(List.of(
                new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.2F), AlwaysTrueTest.INSTANCE, Blocks.BEETROOTS.defaultBlockState()),
                new ProcessorRule(new RandomBlockMatchTest(Blocks.WHEAT, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.MELON_STEM.defaultBlockState())));
        context.register(NURN_FARMS, new StructureProcessorList(ImmutableList.<StructureProcessor>builder()
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

    private static ProcessorRule rule(Block from, Block to) {
        return new ProcessorRule(new BlockMatchTest(from), AlwaysTrueTest.INSTANCE, to.defaultBlockState());
    }

    private static ResourceKey<StructureProcessorList> registerKey(String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, TolkienMobsMain.resLoc(name));
    }
}
