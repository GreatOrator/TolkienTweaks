package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.block.custom.entity.layers.TolkienModelLayers;
import com.greatorator.tolkienmobs.block.custom.entity.model.LightningBugModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.LocustModel;
import com.greatorator.tolkienmobs.block.custom.entity.model.UnbakedPatchModel;
import com.greatorator.tolkienmobs.block.custom.entity.renderer.*;
import com.greatorator.tolkienmobs.entity.item.render.*;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

/**
 * 26.1 removed {@code DimensionSpecialEffects} and the event that registered a dimension's own, so
 * the overworld fog and cloud overrides it used to install have no home here; each biome now carries
 * its own fog distances as environment attributes.
 */
public class TolkienRegistration {
    public static void initModBusEvents(IEventBus bus) {
        bus.addListener(TolkienRegistration::registerEntityRenderers);
        bus.addListener(TolkienRegistration::registerLayerDefinitions);
        bus.addListener(TolkienRegistration::registerBlockStateModels);
        bus.addListener(TolkienRegistration::registerBETypes);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Block Entities
        event.registerBlockEntityRenderer(TolkienBlocks.LIGHTNINGBUG_BLOCK_ENTITY.get(), LightningBugRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCUST_BLOCK_ENTITY.get(), LocustRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.PLACARD_BLOCK_ENTITY.get(), PlacardRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TRINKET_TABLE_BLOCK_ENTITY.get(), TrinketTableRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.FIREPLACE_BLOCK_ENTITY.get(), FireplaceRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_SIGN_BLOCK_ENTITY.get(), StandingSignRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.TOLKIEN_HANGING_SIGN_BLOCK_ENTITY.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.CAMO_SPAWNER_BLOCK_ENTITY.get(), CamoSpawnerRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.MILESTONE_BLOCK_ENTITY.get(), MilestoneRender::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCKABLE_CHEST_BLOCK_ENTITY.get(), LockableChestBlockRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCKABLE_DOUBLE_CHEST_BLOCK_ENTITY.get(), LockableDoubleChestBlockRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCKABLE_TREASURE_CHEST_BLOCK_ENTITY.get(), LockableTreasureChestBlockRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.LOCKABLE_DOUBLE_TREASURE_CHEST_BLOCK_ENTITY.get(), LockableDoubleTreasureChestBlockRenderer::new);
        event.registerBlockEntityRenderer(TolkienBlocks.WELL_BLOCK_ENTITY.get(), WellBlockRenderer::new);

        event.registerEntityRenderer(TolkienEntities.MALLORN_BOAT.get(), (context) -> new MallornBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.MALLORN_CHEST_BOAT.get(), (context) -> new MallornBoatRenderer(context, true));
        event.registerEntityRenderer(TolkienEntities.MIRKWOOD_BOAT.get(), (context) -> new MirkwoodBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.MIRKWOOD_CHEST_BOAT.get(), (context) -> new MirkwoodBoatRenderer(context, true));
        event.registerEntityRenderer(TolkienEntities.CULUMALDA_BOAT.get(), (context) -> new CulumaldaBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.CULUMALDA_CHEST_BOAT.get(), (context) -> new CulumaldaBoatRenderer(context, true));
        event.registerEntityRenderer(TolkienEntities.LEBETHRON_BOAT.get(), (context) -> new LebethronBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.LEBETHRON_CHEST_BOAT.get(), (context) -> new LebethronBoatRenderer(context, true));
        event.registerEntityRenderer(TolkienEntities.FANGORNOAK_BOAT.get(), (context) -> new FangornoakBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.FANGORNOAK_CHEST_BOAT.get(), (context) -> new FangornoakBoatRenderer(context, true));
        event.registerEntityRenderer(TolkienEntities.DEADWOOD_BOAT.get(), (context) -> new DeadwoodBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.DEADWOOD_CHEST_BOAT.get(), (context) -> new DeadwoodBoatRenderer(context, true));
        event.registerEntityRenderer(TolkienEntities.DWARVEN_MAPLE_BOAT.get(), (context) -> new DwarvenMapleBoatRenderer(context, false));
        event.registerEntityRenderer(TolkienEntities.DWARVEN_MAPLE_CHEST_BOAT.get(), (context) -> new DwarvenMapleBoatRenderer(context, true));
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
                TolkienBlocks.MALLORN_HANGING_SIGN.get(), TolkienBlocks.MALLORN_HANGING_WALL_SIGN.get(),
                TolkienBlocks.MIRKWOOD_HANGING_SIGN.get(), TolkienBlocks.MIRKWOOD_HANGING_WALL_SIGN.get(),
                TolkienBlocks.CULUMALDA_HANGING_SIGN.get(), TolkienBlocks.CULUMALDA_HANGING_WALL_SIGN.get(),
                TolkienBlocks.LEBETHRON_HANGING_SIGN.get(), TolkienBlocks.LEBETHRON_HANGING_WALL_SIGN.get(),
                TolkienBlocks.FANGORNOAK_HANGING_SIGN.get(), TolkienBlocks.FANGORNOAK_HANGING_WALL_SIGN.get(),
                TolkienBlocks.DEADWOOD_HANGING_SIGN.get(), TolkienBlocks.DEADWOOD_HANGING_WALL_SIGN.get(),
                TolkienBlocks.DWARVEN_MAPLE_HANGING_SIGN.get(), TolkienBlocks.DWARVEN_MAPLE_HANGING_WALL_SIGN.get());    }

    private static void registerBlockStateModels(RegisterBlockStateModels event) {
        event.registerModel(TolkienMobsMain.prefix("patch"), UnbakedPatchModel.CODEC);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        //Block Models
        event.registerLayerDefinition(TolkienModelLayers.LIGHTNINGBUG, LightningBugModel::create);
        event.registerLayerDefinition(TolkienModelLayers.LOCUST, LocustModel::create);

        // Projectiles
        event.registerLayerDefinition(TolkienModelLayers.MORGUL_CRYSTAL, MorgulCrystalRenderer::createBodyLayer);

        // Boats
        event.registerLayerDefinition(TolkienModelLayers.MALLORN_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.MALLORN_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.MIRKWOOD_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.MIRKWOOD_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.CULUMALDA_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.CULUMALDA_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.LEBETHRON_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.LEBETHRON_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.FANGORNOAK_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.FANGORNOAK_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.DEADWOOD_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.DEADWOOD_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.DWARVEN_MAPLE_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(TolkienModelLayers.DWARVEN_MAPLE_CHEST_BOAT, BoatModel::createChestBoatModel);
    }
}