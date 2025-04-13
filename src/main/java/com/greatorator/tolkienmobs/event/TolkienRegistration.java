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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

public class TolkienRegistration {
    public static void initModBusEvents(IEventBus bus) {
        bus.addListener(TolkienRegistration::registerEntityRenderers);
        bus.addListener(TolkienRegistration::registerLayerDefinitions);
        bus.addListener(TolkienRegistration::registerModelLoaders);
        bus.addListener(TolkienRegistration::registerBETypes);
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

    private static void registerBETypes(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.SIGN,
                TolkienBlocks.MALLORN_SIGN.get(), TolkienBlocks.MALLORN_WALL_SIGN.get(),
                TolkienBlocks.MIRKWOOD_SIGN.get(), TolkienBlocks.MIRKWOOD_WALL_SIGN.get(),
                TolkienBlocks.CULUMALDA_SIGN.get(), TolkienBlocks.CULUMALDA_WALL_SIGN.get(),
                TolkienBlocks.LEBETHRON_SIGN.get(), TolkienBlocks.LEBETHRON_WALL_SIGN.get(),
                TolkienBlocks.FANGORNOAK_SIGN.get(), TolkienBlocks.FANGORNOAK_WALL_SIGN.get(),
                TolkienBlocks.DEADWOOD_SIGN.get(), TolkienBlocks.DEADWOOD_WALL_SIGN.get(),
                TolkienBlocks.DWARVEN_MAPLE_SIGN.get(), TolkienBlocks.DWARVEN_MAPLE_WALL_SIGN.get());

        event.modify(BlockEntityType.HANGING_SIGN,
                TolkienBlocks.MALLORN_HANGING_SIGN.get(), TolkienBlocks.MALLORN_WALL_SIGN.get(),
                TolkienBlocks.MIRKWOOD_HANGING_SIGN.get(), TolkienBlocks.MIRKWOOD_HANGING_WALL_SIGN.get(),
                TolkienBlocks.CULUMALDA_HANGING_SIGN.get(), TolkienBlocks.CULUMALDA_HANGING_WALL_SIGN.get(),
                TolkienBlocks.LEBETHRON_HANGING_SIGN.get(), TolkienBlocks.LEBETHRON_HANGING_WALL_SIGN.get(),
                TolkienBlocks.FANGORNOAK_HANGING_SIGN.get(), TolkienBlocks.FANGORNOAK_HANGING_WALL_SIGN.get(),
                TolkienBlocks.DEADWOOD_HANGING_SIGN.get(), TolkienBlocks.DEADWOOD_HANGING_WALL_SIGN.get(),
                TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN.get(), TolkienBlocks.DWARVEN_MAPLE_HANGING_WALL_SIGN.get());    }

    private static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(TolkienMobsMain.prefix("patch"), PatchModelLoader.INSTANCE);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //Block Models
        event.registerLayerDefinition(TolkienModelLayers.LIGHTNINGBUG, LightningBugModel::create);
        event.registerLayerDefinition(TolkienModelLayers.LOCUST, LocustModel::create);
    }
}