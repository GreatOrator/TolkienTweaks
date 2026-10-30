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
import com.greatorator.tolkienmobs.init.TolkienTags;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
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
        event.put(TolkienEntities.ENTITY_TTM_FACTIONGUARD.get(), FactionGuardEntity.createAttributes().build());
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

    /**
     * The ground a country's own list of animals can be placed on, over and above vanilla's rule.
     *
     * <p>Vanilla's {@code Animal.checkAnimalSpawnRules} asks for
     * {@code #minecraft:animals_spawnable_on} underfoot, which is grass and nothing else, and the
     * mod's map is not all green: Harad is sand over sandstone, Nurn's dry ground is dark stone, the
     * ranges are stone, gravel and snow, and Mordor, the Ash Mountains and the Misty Mountains wear
     * the mod's own rock. Sheep, aurochs and mumakil were named in those countries' spawn lists and
     * could never be placed in them - not by the tick pass that tops up the mobs around a player, and
     * not by the chunk generation pass either, since both ask this same rule.
     *
     * <p>Only the mod's own biomes are answered, so a vanilla desert is still no pasture for sheep,
     * and because the clause is OR-ed onto a country list this can only ever add a place for an
     * animal the country already carries. Daylight is asked for as well, which is what keeps the
     * flocks to the day the way vanilla's are; a chunk being drawn reads as lit, so the generation
     * pass places them exactly as it places the vanilla animals.
     *
     * <p>A snow drift is pasture as well. {@code Mob.checkMobSpawnRules} asks for a full top face,
     * which a snow layer never has, so the cold countries' floors were refused layer by layer even
     * though the pasture tag names snow: Forochel, its slopes and every range top above the snow
     * line was shut to the bears, rabbits and goats their lists carry. Snow only ever settles on a
     * full block - laying it needs one - so the layer is taken as the floor itself.
     */
    private static boolean checkCountryPastureRules(EntityType<? extends Mob> type, ServerLevelAccessor level,
                                                    EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        if (!level.getBiome(pos).is(TolkienTags.Biomes.OVERWORLD)
                || level.getRawBrightness(pos, 0) <= 8) {
            return false;
        }
        BlockState ground = level.getBlockState(pos.below());
        if (!ground.is(TolkienTags.Blocks.COUNTRY_PASTURE)) {
            return false;
        }
        return ground.getBlock() instanceof SnowLayerBlock
                || Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    /** A mod animal's own rule, with the country pasture clause above added to it. */
    private static boolean checkAnimalSpawnRules(EntityType<? extends Animal> type, ServerLevelAccessor level,
                                                 EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Animal.checkAnimalSpawnRules(type, level, reason, pos, random)
                || checkCountryPastureRules(type, level, reason, pos, random);
    }

    /** A country's rabbit, goat, wolf and bear each have a rule of vanilla's own to keep. */
    private static boolean checkRabbitSpawnRules(EntityType<Rabbit> type, ServerLevelAccessor level,
                                                 EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Rabbit.checkRabbitSpawnRules(type, level, reason, pos, random)
                || checkCountryPastureRules(type, level, reason, pos, random);
    }

    private static boolean checkGoatSpawnRules(EntityType<? extends Animal> type, ServerLevelAccessor level,
                                               EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Goat.checkGoatSpawnRules(type, level, reason, pos, random)
                || checkCountryPastureRules(type, level, reason, pos, random);
    }

    private static boolean checkWolfSpawnRules(EntityType<Wolf> type, ServerLevelAccessor level,
                                               EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return Wolf.checkWolfSpawnRules(type, level, reason, pos, random)
                || checkCountryPastureRules(type, level, reason, pos, random);
    }

    private static boolean checkPolarBearSpawnRules(EntityType<PolarBear> type, ServerLevelAccessor level,
                                                    EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return PolarBear.checkPolarBearSpawnRules(type, level, reason, pos, random)
                || checkCountryPastureRules(type, level, reason, pos, random);
    }

    /**
     * The floor a country's animals stand on.
     *
     * <p>{@code ON_GROUND} asks for a full top face under the spawn position, and a snow layer
     * never has one. That is vanilla's own way of keeping mobs off a snowed-in floor - it is why a
     * snow golem's trail stops them - and it is asked <em>before</em> any spawn rule, so no rule of
     * ours could put an animal back on a drift: a cold country's every snowed column was refused,
     * Forochel and its slopes and every range top above the snow line among them. The floor here is
     * vanilla's own, or the country's pasture with a drift lying on it, and only inside a country of
     * the mod's, so a vanilla plain keeps vanilla's rule and its snow keeps its quiet.
     */
    private static final SpawnPlacementType COUNTRY_GROUND = (level, pos, type) ->
            SpawnPlacementTypes.ON_GROUND.isSpawnPositionOk(level, pos, type)
                    || isCountryPastureFloor(level, pos, type);

    /**
     * A country's own floor: the pasture tag underfoot, with the spawn position and the block above
     * it free - and a drift lying on the pasture counts as the floor itself, since snow only ever
     * settles on a full block, so there is ground under it to stand on.
     */
    private static boolean isCountryPastureFloor(LevelReader level, BlockPos pos, EntityType<?> type) {
        if (type == null || !level.getBiome(pos).is(TolkienTags.Biomes.OVERWORLD)) {
            return false;
        }
        if (!level.getBlockState(pos.below()).is(TolkienTags.Blocks.COUNTRY_PASTURE)) {
            return false;
        }
        BlockPos above = pos.above();
        return NaturalSpawner.isValidEmptySpawnBlock(level, pos, level.getBlockState(pos), level.getFluidState(pos), type)
                && NaturalSpawner.isValidEmptySpawnBlock(level, above, level.getBlockState(above), level.getFluidState(above), type);
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        // Ambient
        event.register(TolkienEntities.ENTITY_TTM_GECKO.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_RAT.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_FROG.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_THRUSH.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_CREBAIN.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SWARM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        // Passive
        event.register(TolkienEntities.ENTITY_TTM_AUROCH.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GOAT.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_SHADOWFAX.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_NAZGULSTEED.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_GOLLUM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(TolkienEntities.ENTITY_TTM_ISTARI.get(), COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

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

        // Pasture
        // The animals the countries list are vanilla's own, so their rules are not taken away: each
        // keeps the rule vanilla gave it and the pasture clause is added to it, answering only on
        // ground a country paints inside a country of the mod's. A vanilla sheep in a vanilla plain
        // is untouched; Nurn's sheep, Harad's mumakil and Forochel's bears and rabbits have ground
        // to stand on. The floor has to be replaced along with the rule - see COUNTRY_GROUND - and
        // the event takes a floor only with REPLACE, which is why each of these names the rule it
        // replaces: sheep, cattle, pigs, chickens and the two equines ask Animal's own, and the
        // rabbit, the goat, the wolf and the bear have a rule apiece.
        event.register(EntityType.SHEEP, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.COW, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.PIG, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.CHICKEN, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.HORSE, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.DONKEY, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.RABBIT, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkRabbitSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.GOAT, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkGoatSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.WOLF, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkWolfSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(EntityType.POLAR_BEAR, COUNTRY_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                TolkienEventBusEvents::checkPolarBearSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
