package com.greatorator.tolkienmobs.datagen.tags;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.util.TolkienTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienBlockTagProvider extends BlockTagsProvider {
    public TolkienBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.LOGS)
                .add(TolkienBlocks.LOG_MALLORN.get())
                .add(TolkienBlocks.WOOD_MALLORN.get()).add(TolkienBlocks.STRIPPED_MALLORN_LOG.get())
                .add(TolkienBlocks.STRIPPED_MALLORN_WOOD.get())
                .add(TolkienBlocks.LOG_MIRKWOOD.get())
                .add(TolkienBlocks.WOOD_MIRKWOOD.get())
                .add(TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get())
                .add(TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get())
                .add(TolkienBlocks.LOG_CULUMALDA.get())
                .add(TolkienBlocks.WOOD_CULUMALDA.get())
                .add(TolkienBlocks.STRIPPED_CULUMALDA_LOG.get())
                .add(TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get())
                .add(TolkienBlocks.LOG_LEBETHRON.get())
                .add(TolkienBlocks.WOOD_LEBETHRON.get())
                .add(TolkienBlocks.STRIPPED_LEBETHRON_LOG.get())
                .add(TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get())
                .add(TolkienBlocks.LOG_FANGORNOAK.get())
                .add(TolkienBlocks.WOOD_FANGORNOAK.get())
                .add(TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get())
                .add(TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get())
                .add(TolkienBlocks.LOG_DEADWOOD.get())
                .add(TolkienBlocks.WOOD_DEADWOOD.get())
                .add(TolkienBlocks.STRIPPED_DEADWOOD_LOG.get())
                .add(TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get());
        tag(BlockTags.FENCES)
                .add(TolkienBlocks.FENCE_MALLORN.get())
                .add(TolkienBlocks.FENCE_MIRKWOOD.get())
                .add(TolkienBlocks.FENCE_CULUMALDA.get())
                .add(TolkienBlocks.FENCE_LEBETHRON.get())
                .add(TolkienBlocks.FENCE_FANGORNOAK.get())
                .add(TolkienBlocks.FENCE_DEADWOOD.get());
        tag(BlockTags.FENCE_GATES)
                .add(TolkienBlocks.FENCE_GATE_MALLORN.get())
                .add(TolkienBlocks.FENCE_GATE_MIRKWOOD.get())
                .add(TolkienBlocks.FENCE_GATE_CULUMALDA.get())
                .add(TolkienBlocks.FENCE_GATE_LEBETHRON.get())
                .add(TolkienBlocks.FENCE_GATE_FANGORNOAK.get())
                .add(TolkienBlocks.FENCE_GATE_DEADWOOD.get());
        tag(BlockTags.WALLS)
                .add(TolkienBlocks.WALL_MITHRIL.get())
                .add(TolkienBlocks.WALL_MORGULIRON.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(TolkienBlocks.PRESSURE_PLATE_MALLORN.get())
                .add(TolkienBlocks.PRESSURE_PLATE_MIRKWOOD.get())
                .add(TolkienBlocks.PRESSURE_PLATE_CULUMALDA.get())
                .add(TolkienBlocks.PRESSURE_PLATE_LEBETHRON.get())
                .add(TolkienBlocks.PRESSURE_PLATE_FANGORNOAK.get())
                .add(TolkienBlocks.PRESSURE_PLATE_DEADWOOD.get());
        tag(BlockTags.PRESSURE_PLATES)
                .add(TolkienBlocks.PRESSURE_PLATE_MITHRIL.get())
                .add(TolkienBlocks.PRESSURE_PLATE_MORGULIRON.get());
        tag(BlockTags.DOORS)
                .add(TolkienBlocks.DOOR_MITHRIL.get())
                .add(TolkienBlocks.DOOR_MORGULIRON.get())
                .add(TolkienBlocks.DOOR_DURIN.get());
        tag(BlockTags.WOODEN_DOORS)
                .add(TolkienBlocks.DOOR_MALLORN.get())
                .add(TolkienBlocks.DOOR_MIRKWOOD.get())
                .add(TolkienBlocks.DOOR_CULUMALDA.get())
                .add(TolkienBlocks.DOOR_LEBETHRON.get())
                .add(TolkienBlocks.DOOR_FANGORNOAK.get())
                .add(TolkienBlocks.DOOR_DEADWOOD.get());
        tag(BlockTags.PLANKS)
                .add(TolkienBlocks.PLANKS_MALLORN.get())
                .add(TolkienBlocks.PLANKS_MIRKWOOD.get())
                .add(TolkienBlocks.PLANKS_CULUMALDA.get())
                .add(TolkienBlocks.PLANKS_LEBETHRON.get())
                .add(TolkienBlocks.PLANKS_FANGORNOAK.get())
                .add(TolkienBlocks.PLANKS_DEADWOOD.get());
        tag(BlockTags.WALL_POST_OVERRIDE)
                .add(TolkienBlocks.TORCH_MALLORN.get())
                .add(TolkienBlocks.TORCH_MIRKWOOD.get())
                .add(TolkienBlocks.TORCH_CULUMALDA.get())
                .add(TolkienBlocks.TORCH_LEBETHRON.get())
                .add(TolkienBlocks.TORCH_FANGORNOAK.get())
                .add(TolkienBlocks.TORCH_DEADWOOD.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(TolkienBlocks.BLOCK_MITHRIL.get())
                .add(TolkienBlocks.ORE_MITHRIL.get())
                .add(TolkienBlocks.ORE_NETHER_MITHRIL.get())
                .add(TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get())
                .add(TolkienBlocks.ORE_END_MITHRIL.get())
                .add(TolkienBlocks.BLOCK_MORGULIRON.get());
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(TolkienBlocks.ORE_MITHRIL.get())
                .add(TolkienBlocks.ORE_NETHER_MITHRIL.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(TolkienBlocks.ORE_DEEPSLATE_MITHRIL.get())
                .add(TolkienBlocks.ORE_END_MITHRIL.get());
        tag(TolkienTags.Blocks.ROOT_TRACE_SKIP);
    }
}
