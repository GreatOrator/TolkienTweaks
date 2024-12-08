package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.entity.ambient.*;
import com.greatorator.tolkienmobs.entity.ambient.model.*;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class TolkienEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GeckoModel.LAYER_LOCATION, GeckoModel::createBodyLayer);
        event.registerLayerDefinition(RatModel.LAYER_LOCATION, RatModel::createBodyLayer);
        event.registerLayerDefinition(SquirrelModel.LAYER_LOCATION, SquirrelModel::createBodyLayer);
        event.registerLayerDefinition(FrogModel.LAYER_LOCATION, FrogModel::createBodyLayer);
        event.registerLayerDefinition(SwarmModel.LAYER_LOCATION, SwarmModel::create);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TolkienEntities.ENTITY_TTM_GECKO.get(), GeckoEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_RAT.get(), RatEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), SquirrelEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_FROG.get(), FrogEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SWARM.get(), SwarmEntity.registerAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(TolkienEntities.ENTITY_TTM_GECKO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_RAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_FROG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SWARM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
