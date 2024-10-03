package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LightningBugModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.LocustModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.PatchModelLoader;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.LightningBugRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.LocustRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.PlacardRenderer;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.TrinketTableRenderer;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.item.custom.CoinPouchItem;
import com.greatorator.tolkienmobs.item.custom.KeyRingItem;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import static com.greatorator.tolkienmobs.init.TolkienItems.COIN_POUCH;
import static com.greatorator.tolkienmobs.init.TolkienItems.KEY_RING;

public class TolkienRegistration {
    public static void initModBusEvents(IEventBus bus) {
        bus.addListener(TolkienRegistration::registerEntityRenderers);
        bus.addListener(TolkienRegistration::registerLayerDefinitions);
        bus.addListener(TolkienRegistration::registerModelLoaders);
        bus.addListener(TolkienRegistration::registerPropertyOverride);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Block Entities
        event.registerBlockEntityRenderer(TolkienBlocks.LIGHTNINGBUG_BLOCK_ENTITY.get(), LightningBugRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCUST_BLOCK_ENTITY.get(), LocustRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), PlacardRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TRINKET_TABLE_BLOCK_ENTITY.get(), TrinketTableRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_SIGN_BLOCK_ENTITY.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_HANGING_SIGN_BLOCK_ENTITY.get(), HangingSignRenderer::new);
    }

    private static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(TolkienMobsMain.prefix("patch"), PatchModelLoader.INSTANCE);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //Block Models
        event.registerLayerDefinition(TolkienModelLayers.LIGHTNINGBUG, LightningBugModel::create);
        event.registerLayerDefinition(TolkienModelLayers.LOCUST, LocustModel::create);
    }

    private static void registerPropertyOverride(ModelEvent.ModifyBakingResult event) {
        ItemProperties.register(COIN_POUCH.get(), CoinPouchItem.FILLED_PREDICATE, (stack, level, entity, seed) -> CoinPouchItem.getFullnessPropertyOverride(stack));
//        ItemProperties.register(KEY_RING.get(), ResourceLocation.parse("fullness"), KeyRingItem::getFullnessPropertyOverride);
    }
}