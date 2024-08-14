package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
        tag(BlockTags.LOGS).add(TolkienBlocks.LOG_MALLORN.get(), TolkienBlocks.WOOD_MALLORN.get(), TolkienBlocks.STRIPPED_MALLORN_LOG.get(), TolkienBlocks.STRIPPED_MALLORN_WOOD.get(),TolkienBlocks.LOG_MIRKWOOD.get(), TolkienBlocks.WOOD_MIRKWOOD.get(), TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get(), TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get(),TolkienBlocks.LOG_CULUMALDA.get(), TolkienBlocks.WOOD_CULUMALDA.get(), TolkienBlocks.STRIPPED_CULUMALDA_LOG.get(), TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get(),TolkienBlocks.LOG_LEBETHRON.get(), TolkienBlocks.WOOD_LEBETHRON.get(), TolkienBlocks.STRIPPED_LEBETHRON_LOG.get(), TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get(),TolkienBlocks.LOG_FANGORNOAK.get(), TolkienBlocks.WOOD_FANGORNOAK.get(), TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get(), TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get(),TolkienBlocks.LOG_DEADWOOD.get(), TolkienBlocks.WOOD_DEADWOOD.get(), TolkienBlocks.STRIPPED_DEADWOOD_LOG.get(), TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get());
        tag(BlockTags.FENCES).add(TolkienBlocks.FENCE_MALLORN.get(), TolkienBlocks.FENCE_MIRKWOOD.get(), TolkienBlocks.FENCE_CULUMALDA.get(), TolkienBlocks.FENCE_LEBETHRON.get(), TolkienBlocks.FENCE_FANGORNOAK.get(), TolkienBlocks.FENCE_DEADWOOD.get());
        tag(BlockTags.FENCE_GATES).add(TolkienBlocks.FENCE_GATE_MALLORN.get(), TolkienBlocks.FENCE_GATE_MIRKWOOD.get(), TolkienBlocks.FENCE_GATE_CULUMALDA.get(), TolkienBlocks.FENCE_GATE_LEBETHRON.get(), TolkienBlocks.FENCE_GATE_FANGORNOAK.get(), TolkienBlocks.FENCE_GATE_DEADWOOD.get());
        tag(BlockTags.WALLS).add(TolkienBlocks.WALL_MITHRIL.get(), TolkienBlocks.WALL_MORGULIRON.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(TolkienBlocks.PRESSURE_PLATE_MALLORN.get(), TolkienBlocks.PRESSURE_PLATE_MIRKWOOD.get(), TolkienBlocks.PRESSURE_PLATE_CULUMALDA.get(), TolkienBlocks.PRESSURE_PLATE_LEBETHRON.get(), TolkienBlocks.PRESSURE_PLATE_FANGORNOAK.get(), TolkienBlocks.PRESSURE_PLATE_DEADWOOD.get());
        tag(BlockTags.PRESSURE_PLATES).add(TolkienBlocks.PRESSURE_PLATE_MITHRIL.get(), TolkienBlocks.PRESSURE_PLATE_MORGULIRON.get());
        tag(BlockTags.DOORS).add(TolkienBlocks.DOOR_MITHRIL.get(), TolkienBlocks.DOOR_MORGULIRON.get(), TolkienBlocks.DOOR_DURIN.get());
        tag(BlockTags.WOODEN_DOORS).add(TolkienBlocks.DOOR_MALLORN.get(), TolkienBlocks.DOOR_MIRKWOOD.get(), TolkienBlocks.DOOR_CULUMALDA.get(), TolkienBlocks.DOOR_LEBETHRON.get(), TolkienBlocks.DOOR_FANGORNOAK.get(), TolkienBlocks.DOOR_DEADWOOD.get());
        tag(BlockTags.PLANKS).add(TolkienBlocks.PLANKS_MALLORN.get(), TolkienBlocks.PLANKS_MIRKWOOD.get(), TolkienBlocks.PLANKS_CULUMALDA.get(), TolkienBlocks.PLANKS_LEBETHRON.get(), TolkienBlocks.PLANKS_FANGORNOAK.get(), TolkienBlocks.PLANKS_DEADWOOD.get());



    }
}
