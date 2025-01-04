package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.datagen.loot.TolkienGlobalLootModifierProvider;
import com.greatorator.tolkienmobs.datagen.loot.TolkienLootTableProvider;
import com.greatorator.tolkienmobs.datagen.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class TolkienDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        BlockTagsProvider blockTagsProvider = new TolkienBlockTagProvider(packOutput, lookupProvider, existingFileHelper);

        generator.addProvider(event.includeServer(), new TolkienLootTableProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new TolkienGlobalLootModifierProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new TolkienItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new TolkienFluidTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new TolkienDamageTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new TolkienPoiTypeTagProvider(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new TolkienSoundProvider(packOutput, existingFileHelper));

        generator.addProvider(event.includeClient(), new TolkienItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new TolkienBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new TolkienParticleDescriptionProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new TolkienRecipeProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new TolkienDataRegistryProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new TolkienLangProvider(packOutput));
    }
}