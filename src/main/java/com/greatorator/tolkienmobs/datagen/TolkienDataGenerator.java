package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.datagen.loot.TolkienGlobalLootModifierProvider;
import com.greatorator.tolkienmobs.datagen.loot.TolkienLootTableProvider;
import com.greatorator.tolkienmobs.datagen.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import com.greatorator.tolkienmobs.datagen.modelgen.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;


public class TolkienDataGenerator {
    public static void gatherData(GatherDataEvent.Client event) {
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
        event.addProvider(new TolkienVillageTemplateProvider(packOutput, event.getResourceManager(PackType.SERVER_DATA)));
        event.addProvider(new TolkienStructureTagProvider(packOutput, registryProvider.getRegistryProvider()));
        event.addProvider(new TolkienBlockStateProvider(packOutput, existingFileHelper));
        event.addProvider(new TolkienItemModelProvider(packOutput, existingFileHelper));
        event.addProvider(new TolkienTrimSpriteProvider(packOutput, event.getResourceManager(PackType.CLIENT_RESOURCES)));
        event.addProvider(new TolkienSpawnEggTextureProvider(packOutput));
        event.addProvider(new TolkienLangProviderUS(packOutput));
        event.addProvider(new TolkienLangProviderCN(packOutput));
        event.addProvider(new TolkienSoundProvider(packOutput));

        event.addProvider(new TolkienRecipeProvider.Runner(packOutput, lookupProvider));
        event.addProvider(new TolkienLootTableProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienGlobalLootModifierProvider(packOutput, lookupProvider));

        event.addProvider(new TolkienBlockTagProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienItemTagProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienEntityTagProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienFluidTagProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienBiomeTagProvider(packOutput, registryProvider.getRegistryProvider()));
        event.addProvider(new TolkienDamageTagProvider(packOutput, lookupProvider));

        event.addProvider(new TolkienPoiTypeTagProvider(packOutput, lookupProvider));
        event.addProvider(new TolkienParticleDescriptionProvider(packOutput));
    }

}
