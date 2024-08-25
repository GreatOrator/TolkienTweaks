package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.block.custom.entity.TolkienSignEntity;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LightningBugModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.LocustModel;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.LightningBugRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.LocustRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.PlacardRenderer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class TolkienRegistration {
    public static void initModBusEvents(IEventBus bus) {
        bus.addListener(TolkienRegistration::registerEntityRenderers);
        bus.addListener(TolkienRegistration::registerLayerDefinitions);
    }
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Block Entities
        event.registerBlockEntityRenderer(TolkienBlocks.LIGHTNINGBUG.get(), LightningBugRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCUST.get(), LocustRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.PLACARD_TILE.get(), PlacardRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_SIGN_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_HANGING_SIGN_ENTITY.get(), HangingSignRenderer::new);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //Block Models
        event.registerLayerDefinition(TolkienModelLayers.LIGHTNINGBUG, LightningBugModel::create);
        event.registerLayerDefinition(TolkienModelLayers.LOCUST, LocustModel::create);
    }
}