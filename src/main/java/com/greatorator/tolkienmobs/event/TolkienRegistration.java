package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LightningBugModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.LocustModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.PatchModelLoader;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.*;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

public class TolkienRegistration {
    public static void initModBusEvents(IEventBus bus) {
        bus.addListener(TolkienRegistration::registerEntityRenderers);
        bus.addListener(TolkienRegistration::registerLayerDefinitions);
        bus.addListener(TolkienRegistration::registerModelLoaders);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Block Entities
        event.registerBlockEntityRenderer(TolkienBlocks.LIGHTNINGBUG_BLOCK_ENTITY.get(), LightningBugRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCUST_BLOCK_ENTITY.get(), LocustRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), PlacardRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TRINKET_TABLE_BLOCK_ENTITY.get(), TrinketTableRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.FIREPLACE_BLOCK_ENTITY.get(), FireplaceRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_SIGN_BLOCK_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_HANGING_SIGN_BLOCK_ENTITY.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.CAMO_SPAWNER_BLOCK_ENTITY.get(), CamoSpawnerRenderer::new);
    }

    private static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(TolkienMobsMain.prefix("patch"), PatchModelLoader.INSTANCE);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //Block Models
        event.registerLayerDefinition(TolkienModelLayers.LIGHTNINGBUG, LightningBugModel::create);
        event.registerLayerDefinition(TolkienModelLayers.LOCUST, LocustModel::create);
    }
}