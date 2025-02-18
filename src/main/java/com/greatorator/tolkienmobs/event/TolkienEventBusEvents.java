package com.greatorator.tolkienmobs.event;

import com.greatorator.tolkienmobs.entity.ambient.*;
import com.greatorator.tolkienmobs.entity.ambient.model.GeckoModel;
import com.greatorator.tolkienmobs.entity.ambient.model.SwarmModel;
import com.greatorator.tolkienmobs.entity.boss.GoblinKingEntity;
import com.greatorator.tolkienmobs.entity.monster.*;
import com.greatorator.tolkienmobs.entity.npc.*;
import com.greatorator.tolkienmobs.entity.passive.*;
import com.greatorator.tolkienmobs.entity.projectiles.model.BoulderModel;
import com.greatorator.tolkienmobs.entity.projectiles.model.FellBeastFireballModel;
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
        // Ambient
        event.registerLayerDefinition(GeckoModel.LAYER_LOCATION, GeckoModel::createBodyLayer);
        event.registerLayerDefinition(SwarmModel.LAYER_LOCATION, SwarmModel::create);

        // Projectile
        event.registerLayerDefinition(BoulderModel.LAYER_LOCATION, BoulderModel::createBodyLayer);
        event.registerLayerDefinition(FellBeastFireballModel.LAYER_LOCATION, FellBeastFireballModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Ambient
        event.put(TolkienEntities.ENTITY_TTM_GECKO.get(), GeckoEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_RAT.get(), RatEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), SquirrelEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_FROG.get(), FrogEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_THRUSH.get(), ThrushEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_CREBAIN.get(), CrebainEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SWARM.get(), SwarmEntity.registerAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(), GreatEagleEntity.createAttributes().build());

        // Passive
        event.put(TolkienEntities.ENTITY_TTM_AUROCH.get(), AurochEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), MumakilEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_GOAT.get(), GoatEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SHADOWFAX.get(), ShadowfaxEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_NAZGULSTEED.get(), NazgulSteedEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_GOLLUM.get(), GollumEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ISTARI.get(), IstariEntity.createAttributes().build());

        // NPC
        event.put(TolkienEntities.ENTITY_TTM_HUMAN.get(), HumanEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_DWARF.get(), DwarfEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ELVES.get(), ElvesEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_HOBBIT.get(), HobbitEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SOUTHRON.get(), SouthronEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), OrcTraderEntity.createAttributes().build());

        // Hostile
        event.put(TolkienEntities.ENTITY_TTM_BARROW.get(), BarrowWightEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_OATHBREAKER.get(), OathBreakerEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), FellSpiritEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_BRIGAND.get(), BrigandEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_HARADRIM.get(), HaradrimEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ROMIEWALKER.get(), RomieWalkerEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MIMICCHEST.get(), MimicChestEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MORDORORC.get(), MordorOrcEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_URUKHAI.get(), UrukHaiEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_DUERGAR.get(), DuergarEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_GOBLIN.get(), GoblinEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), MirkwoodSpiderEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_ROCKGOLEM.get(), RockGolemEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_HURON.get(), HuronEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MINOTAUR.get(), MinotaurEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_DEEPCLAW.get(), DeepClawEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_TROLL.get(), TrollEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_TREEENT.get(), TreeEntEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), SwampHagEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_WARG.get(), WargEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ELEMENTALGOLEM.get(), ElementalGolemEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_NAZGUL.get(), NazgulEntity.createAttributes().build());

        // Boss
        event.put(TolkienEntities.ENTITY_TTM_GOBLINKING.get(), GoblinKingEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // Ambient
        event.register(TolkienEntities.ENTITY_TTM_GECKO.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_RAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_FROG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_THRUSH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_CREBAIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SWARM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // Passive
        event.register(TolkienEntities.ENTITY_TTM_AUROCH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GOAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SHADOWFAX.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_NAZGULSTEED.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GOLLUM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ISTARI.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // Hostile
        event.register(TolkienEntities.ENTITY_TTM_BARROW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_OATHBREAKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_BRIGAND.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_HARADRIM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ROMIEWALKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_MIMICCHEST.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_MORDORORC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_URUKHAI.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_DUERGAR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GOBLIN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_HURON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_MINOTAUR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_DEEPCLAW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_TROLL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_TREEENT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_WARG.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ELEMENTALGOLEM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_NAZGUL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // Boss
        event.register(TolkienEntities.ENTITY_TTM_GOBLINKING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

    }
}
