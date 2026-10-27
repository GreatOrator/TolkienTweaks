package com.greatorator.tolkienmobs.event;

import com.google.common.base.Suppliers;
import com.greatorator.tolkienmobs.block.custom.entity.function.ChameleonBlockDynamicBakedModel;
import com.greatorator.tolkienmobs.entity.ambient.*;
import com.greatorator.tolkienmobs.entity.ambient.model.GeckoModel;
import com.greatorator.tolkienmobs.entity.ambient.model.SwarmModel;
import com.greatorator.tolkienmobs.entity.boss.*;
import com.greatorator.tolkienmobs.entity.monster.*;
import com.greatorator.tolkienmobs.entity.npc.*;
import com.greatorator.tolkienmobs.entity.passive.*;
import com.greatorator.tolkienmobs.entity.projectiles.model.BoulderModel;
import com.greatorator.tolkienmobs.entity.projectiles.model.FellBeastFireballModel;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Map;
import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.LOGGER;
import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

@EventBusSubscriber(modid = MODID)
public class TolkienEventBusEvents {
    private static final Supplier<Block[]> chameleonBlocks = Suppliers.memoize(() -> new Block[] {
            TolkienBlocks.CHAMELEON_BLOCK.get(),
            TolkienBlocks.CAMO_CHEST_BLOCK.get(),
            TolkienBlocks.CAMO_FLUID_BLOCK.get(),
            TolkienBlocks.CAMO_GLOWSTONE_BLOCK.get(),
            TolkienBlocks.KEY_STONE_BLOCK.get(),
            TolkienBlocks.CAMO_SMOKER_BLOCK.get(),
            TolkienBlocks.CAMO_SPAWNER_BLOCK.get()
    });

    @SubscribeEvent
    public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event) {
        // 1.21.5 keys the baking result by block state and holds block state models, which are still mutable here.
        Map<BlockState, BlockStateModel> modelRegistry = event.getBakingResult().blockStateModels();

        for (Block block : chameleonBlocks.get()) {
            for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
                BlockStateModel existingModel = modelRegistry.get(blockState);
                if (existingModel == null) {
                    LOGGER.warn("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
                } else if (existingModel instanceof ChameleonBlockDynamicBakedModel) {
                    LOGGER.warn("Tried to replace CamouflagedBakedModel twice");
                } else {
                    ChameleonBlockDynamicBakedModel customModel = new ChameleonBlockDynamicBakedModel(existingModel);
                    modelRegistry.put(blockState, customModel);
                }
            }
        }
    }
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
        event.put(TolkienEntities.ENTITY_TTM_SOUTHRON.get(), SouthronTraderEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), OrcTraderEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ORC.get(), OrcEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SOUTHRON_NPC.get(), SouthronEntity.createAttributes().build());

        // Hostile
        event.put(TolkienEntities.ENTITY_TTM_BARROW.get(), BarrowWightEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_OATHBREAKER.get(), OathBreakerEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), FellSpiritEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_ENRAGED_FELLSPIRIT.get(), EnragedFellSpiritEntity.createAttributes().build());
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
        event.put(TolkienEntities.ENTITY_TTM_BALROG.get(), BalrogEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_SHELOB.get(), ShelobEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_WITCHKING.get(), WitchKingEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_WATCHER.get(), WatcherEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MITHRILGOLEM.get(), MithrilGolemEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_MORGULIRONGOLEM.get(), MorgulironGolemEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_TTM_GWAHIR.get(), GwahirEntity.createAttributes().build());
        event.put(TolkienEntities.ENTITY_FELL_BEAST.get(), FellBeastEntity.createAttributes().build());
    }

    /**
     * The rule a country's traders are placed by: solid ground underfoot, and only while the chunk
     * is being made. {@code Mob.checkMobSpawnRules} is asked without the daylight vanilla's animals
     * want, because generation has not lit the chunk yet.
     */
    private static boolean checkTraderSpawnRules(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                 EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return reason == EntitySpawnReason.CHUNK_GENERATION
                && Mob.checkMobSpawnRules(type, level, reason, pos, random);
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

        // NPC
        // A country's own people spawn from its list now, so they need a placement like any other
        // spawned mob. ON_GROUND with Mob's own rule is the loosest one that still keeps them off
        // water and out of leaves; it asks for solid ground underfoot rather than the grass and
        // daylight vanilla's animals want, which the dwarves' Iron Hills or Harad's sand would refuse.
        event.register(TolkienEntities.ENTITY_TTM_HUMAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_DWARF.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ELVES.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_HOBBIT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ORC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SOUTHRON_NPC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // Merchants
        // A trader is met in the field, not delivered on a timer, so a country's traders are placed
        // only while its chunks are first made. The tick pass that keeps topping up the mobs around
        // a player would otherwise crowd a country whose creature list is nothing but a trader -
        // Mordor's and the Ash Mountains' both are - with merchants standing a few blocks apart,
        // however rare the list makes each chunk of untouched ground. Generation draws them at the
        // rate their weight in the list allows, and a trader met that way stays where he is until
        // his own visit timer runs out.
        event.register(TolkienEntities.ENTITY_TTM_SOUTHRON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkTraderSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkTraderSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // Hostile
        event.register(TolkienEntities.ENTITY_TTM_BARROW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_OATHBREAKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ENRAGED_FELLSPIRIT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
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
