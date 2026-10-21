package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.datagen.loot.TolkienGlobalLootModifierProvider;
import com.greatorator.tolkienmobs.datagen.loot.TolkienLootTableProvider;
import com.greatorator.tolkienmobs.datagen.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import com.greatorator.tolkienmobs.datagen.modelgen.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;


public class TolkienDataGenerator {
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        // NeoForge 21.4 removed GatherDataEvent#getExistingFileHelper along with the
        // generator API, so the model providers use their own stand-in, and it dropped the
        // includeServer()/includeClient() flags: one event now covers one whole run, so every
        // provider is added unconditionally through GatherDataEvent#addProvider.
        ExistingFileHelper existingFileHelper = new ExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        TolkienDataRegistryProvider registryProvider = new TolkienDataRegistryProvider(packOutput, lookupProvider);

        event.addProvider(registryProvider);
        event.addProvider(new TolkienVillageTemplateProvider(packOutput, existingFileHelper));
        event.addProvider(new TolkienStructureTagProvider(packOutput, registryProvider.getRegistryProvider()));
        event.addProvider(new TolkienBlockStateProvider(packOutput, existingFileHelper));
        event.addProvider(new TolkienItemModelProvider(packOutput, existingFileHelper));
        event.addProvider(new TolkienLangProviderUS(packOutput));
        event.addProvider(new TolkienLangProviderCN(packOutput));
        event.addProvider(new TolkienSoundProvider(packOutput, existingFileHelper));

        event.addProvider(new TolkienRecipeProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienLootTableProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienGlobalLootModifierProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new TolkienBlockTagProvider(packOutput, lookupProvider, existingFileHelper);

        event.addProvider(blockTagsProvider);
        event.addProvider(new TolkienItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        event.addProvider(new TolkienEntityTagProvider(packOutput, lookupProvider, existingFileHelper));
        event.addProvider(new TolkienFluidTagProvider(packOutput, lookupProvider, existingFileHelper));
        event.addProvider(new TolkienBiomeTagProvider(packOutput, registryProvider.getRegistryProvider(), existingFileHelper));
        event.addProvider(new TolkienDamageTagProvider(packOutput, lookupProvider, existingFileHelper));

        event.addProvider(new TolkienPoiTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
        event.addProvider(new TolkienParticleDescriptionProvider(packOutput, existingFileHelper));
    }

}
