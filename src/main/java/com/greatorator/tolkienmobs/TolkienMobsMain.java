package com.greatorator.tolkienmobs;

import com.google.common.collect.Maps;
import com.greatorator.tolkienmobs.block.custom.entity.BackpackBlockEntity;
import com.greatorator.tolkienmobs.containers.screens.*;
import com.greatorator.tolkienmobs.datagen.TolkienDataGenerator;
import com.greatorator.tolkienmobs.entity.ambient.render.*;
import com.greatorator.tolkienmobs.entity.boss.render.*;
import com.greatorator.tolkienmobs.entity.item.render.*;
import com.greatorator.tolkienmobs.entity.monster.render.*;
import com.greatorator.tolkienmobs.entity.npc.render.*;
import com.greatorator.tolkienmobs.entity.passive.render.*;
import com.greatorator.tolkienmobs.entity.projectiles.render.BoulderRender;
import com.greatorator.tolkienmobs.entity.projectiles.render.FellBeastFireballRender;
import com.greatorator.tolkienmobs.entity.projectiles.render.TolkienArrowRenderer;
import com.greatorator.tolkienmobs.event.TolkienRegistration;
import com.greatorator.tolkienmobs.event.client.TolkienClientEvents;
import com.greatorator.tolkienmobs.fluid.TolkienFluidType;
import com.greatorator.tolkienmobs.handler.ColorHandler;
import com.greatorator.tolkienmobs.handler.MilestoneHandler;
import com.greatorator.tolkienmobs.init.*;
import com.greatorator.tolkienmobs.network.ClientProxy;
import com.greatorator.tolkienmobs.network.CommonProxy;
import com.greatorator.tolkienmobs.network.PacketHandler;
import com.greatorator.tolkienmobs.network.manager.SpawnFallenLeafFromPacket;
import com.greatorator.tolkienmobs.particle.FellBeastBreathParticle;
import com.greatorator.tolkienmobs.particle.LeafParticle;
import com.greatorator.tolkienmobs.particle.WindParticle;
import com.greatorator.tolkienmobs.particle.provider.TolkienParticleProvider;
import com.greatorator.tolkienmobs.util.GeneralUtility;
import com.greatorator.tolkienmobs.util.TolkienItemProperties;
import com.greatorator.tolkienmobs.world.components.util.TolkienSurfaceRuleManager;
import com.greatorator.tolkienmobs.world.registration.TolkienSurfaceRules;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.util.Locale;

@Mod(TolkienMobsMain.MODID)
public class TolkienMobsMain {
    public static final String MODID = "tolkienmobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String BLOCK_DIR = "textures/block/custom/";
    public static CommonProxy proxy;

    // TODO
    //  -Biomes
    //    -Proper overworld generation or, Separate Dimension
    //    -Structures

    public TolkienMobsMain(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        proxy = GeneralUtility.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::createNewRegistries);

        modEventBus.addListener(this::setupPackets);

        if (dist.isClient()) {
            TolkienRegistration.initModBusEvents(modEventBus);
            TolkienClientEvents.initGameEvents();
        }
        MilestoneHandler.init();
        PacketHandler.register(modEventBus);

        TolkienItems.register(modEventBus);
        TolkienBlocks.register(modEventBus);
        TolkienEntities.register(modEventBus);

        TolkienTabs.register(modEventBus);

        TolkienSounds.SOUND_EVENTS.register(modEventBus);
        modEventBus.addListener(TolkienDataGenerator::gatherData);

        TolkienFeatureModifiers.TREE_DECORATORS.register(modEventBus);
        TolkienFeatures.FEATURES.register(modEventBus);
        TolkienFeatureModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
        TolkienFeatureModifiers.TRUNK_PLACERS.register(modEventBus);
        TolkienFeatureModifiers.FOLIAGE_PLACERS.register(modEventBus);
        TolkienCaveCarvers.CARVER_TYPES.register(modEventBus);
        TolkienStructures.STRUCTURE.register(modEventBus);

        TolkienMobEffects.register(modEventBus);
        TolkienContainers.register(modEventBus);

        TolkienPotions.register(modEventBus);
        TolkienParticleTypes.register(modEventBus);
        TolkienVillagers.register(modEventBus);

        TolkienFluidTypes.register(modEventBus);
        TolkienFluids.register(modEventBus);

        TolkienLootContextParamSets.registerAll();
        TolkienLootModifier.register(modEventBus);
        TolkienEnchantmentEffectComponents.register(modEventBus);
        TolkienEnchantmentEffects.register(modEventBus);
        TolkienLootFunctions.LOOT_FUNCTIONS.register(modEventBus);

        TolkienDataComponents.register(modEventBus);
        TolkienRecipesTypes.register(modEventBus);
        TolkienRecipeSerializers.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, TolkienMobsConfig.SPEC);
        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(ColorHandler::itemColourEvent);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AxeItem.STRIPPABLES = Maps.newHashMap(AxeItem.STRIPPABLES);
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_MALLORN.get(), TolkienBlocks.STRIPPED_MALLORN_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_MIRKWOOD.get(), TolkienBlocks.STRIPPED_MIRKWOOD_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_CULUMALDA.get(), TolkienBlocks.STRIPPED_CULUMALDA_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_LEBETHRON.get(), TolkienBlocks.STRIPPED_LEBETHRON_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_FANGORNOAK.get(), TolkienBlocks.STRIPPED_FANGORNOAK_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_DEADWOOD.get(), TolkienBlocks.STRIPPED_DEADWOOD_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.LOG_DWARVEN_MAPLE.get(), TolkienBlocks.STRIPPED_DWARVEN_MAPLE_LOG.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_MALLORN.get(), TolkienBlocks.STRIPPED_MALLORN_WOOD.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_MIRKWOOD.get(), TolkienBlocks.STRIPPED_MIRKWOOD_WOOD.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_CULUMALDA.get(), TolkienBlocks.STRIPPED_CULUMALDA_WOOD.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_LEBETHRON.get(), TolkienBlocks.STRIPPED_LEBETHRON_WOOD.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_FANGORNOAK.get(), TolkienBlocks.STRIPPED_FANGORNOAK_WOOD.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_DEADWOOD.get(), TolkienBlocks.STRIPPED_DEADWOOD_WOOD.get());
            AxeItem.STRIPPABLES.put(TolkienBlocks.WOOD_DWARVEN_MAPLE.get(), TolkienBlocks.STRIPPED_DWARVEN_MAPLE_WOOD.get());

            FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
            pot.addPlant(TolkienBlocks.FLOWER_SIMBELMYNE.getId(), TolkienBlocks.POTTED_FLOWER_SIMBELMYNE);
            pot.addPlant(TolkienBlocks.FLOWER_MIRKWOOD.getId(), TolkienBlocks.POTTED_FLOWER_MIRKWOOD);
            pot.addPlant(TolkienBlocks.FLOWER_ATHELAS.getId(), TolkienBlocks.POTTED_FLOWER_ATHELAS);
            pot.addPlant(TolkienBlocks.FLOWER_ALFIRIN.getId(), TolkienBlocks.POTTED_FLOWER_ALFIRIN);
            pot.addPlant(TolkienBlocks.FLOWER_NIPHREDIL.getId(), TolkienBlocks.POTTED_FLOWER_NIPHREDIL);
            pot.addPlant(TolkienBlocks.FLOWER_SWAMPMILKWEED.getId(), TolkienBlocks.POTTED_FLOWER_SWAMPMILKWEED);
            pot.addPlant(TolkienBlocks.FLOWER_LILLYOFTHEVALLEY.getId(), TolkienBlocks.POTTED_FLOWER_LILLYOFTHEVALLEY);
            pot.addPlant(TolkienBlocks.FLOWER_ELANOR.getId(), TolkienBlocks.POTTED_FLOWER_ELANOR);
            pot.addPlant(TolkienBlocks.FLOWER_AEGLOS.getId(), TolkienBlocks.POTTED_FLOWER_AEGLOS);
            pot.addPlant(TolkienBlocks.FLOWER_LISSUIN.getId(), TolkienBlocks.POTTED_FLOWER_LISSUIN);
            pot.addPlant(TolkienBlocks.FLOWER_MALLOS.getId(), TolkienBlocks.POTTED_FLOWER_MALLOS);
            pot.addPlant(TolkienBlocks.FLOWER_BRAMBLES.getId(), TolkienBlocks.POTTED_FLOWER_BRAMBLES);
            pot.addPlant(TolkienBlocks.SAPLING_MALLORN.getId(), TolkienBlocks.POTTED_SAPLING_MALLORN);
            pot.addPlant(TolkienBlocks.SAPLING_MIRKWOOD.getId(), TolkienBlocks.POTTED_SAPLING_MIRKWOOD);
            pot.addPlant(TolkienBlocks.SAPLING_CULUMALDA.getId(), TolkienBlocks.POTTED_SAPLING_CULUMALDA);
            pot.addPlant(TolkienBlocks.SAPLING_LEBETHRON.getId(), TolkienBlocks.POTTED_SAPLING_LEBETHRON);
            pot.addPlant(TolkienBlocks.SAPLING_FANGORNOAK.getId(), TolkienBlocks.POTTED_SAPLING_FANGORNOAK);
            pot.addPlant(TolkienBlocks.SAPLING_DEADWOOD.getId(), TolkienBlocks.POTTED_SAPLING_DEADWOOD);
            pot.addPlant(TolkienBlocks.SAPLING_DWARVEN_MAPLE.getId(), TolkienBlocks.POTTED_SAPLING_DWARVEN_MAPLE);
            pot.addPlant(TolkienBlocks.MUSHROOM_BLOOM_DECAY.getId(), TolkienBlocks.POTTED_MUSHROOM_BLOOM_DECAY);
            pot.addPlant(TolkienBlocks.MUSHROOM_DECAY_BLOOM.getId(), TolkienBlocks.POTTED_MUSHROOM_DECAY_BLOOM);

            TolkienSurfaceRuleManager.addSurfaceRules(TolkienSurfaceRuleManager.RuleCategory.OVERWORLD, MODID, TolkienSurfaceRules.tolkienSurface());

        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    public void createNewRegistries(NewRegistryEvent event) {
        event.register(TolkienBiomes.BIOME_LAYER_TYPE);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(Capabilities.ItemHandler.BLOCK,
                (level, pos, state, be, side) -> {
                    if (be instanceof BackpackBlockEntity)
                        return be.getData(TolkienBlocks.BACKPACK_HANDLER);
                    return null;
                },
                TolkienBlocks.BACKPACK.get()
        );
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, TolkienBlocks.BACKPACK_BLOCK_ENTITY.get(), BackpackBlockEntity::getTank);
    }

    public void setupPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();
        registrar.playToClient(SpawnFallenLeafFromPacket.TYPE, SpawnFallenLeafFromPacket.STREAM_CODEC, SpawnFallenLeafFromPacket::handle);
    }

        @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Sheets.addWoodType(TolkienWoodTypes.MALLORN);
            Sheets.addWoodType(TolkienWoodTypes.MIRKWOOD);
            Sheets.addWoodType(TolkienWoodTypes.CULUMALDA);
            Sheets.addWoodType(TolkienWoodTypes.LEBETHRON);
            Sheets.addWoodType(TolkienWoodTypes.FANGORNOAK);
            Sheets.addWoodType(TolkienWoodTypes.DEADWOOD);
            Sheets.addWoodType(TolkienWoodTypes.DWARVEN_MAPLE);
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(TolkienFluids.MITHRIL_FLOWING.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(TolkienFluids.MORGULIRON_FLOWING.get(), RenderType.translucent());
            });

            TolkienItemProperties.addCustomItemProperties();

            //Entity Renders
                // Ambient
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GECKO.get(), GeckoRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_RAT.get(), RatRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_SQUIRREL.get(), SquirrelRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_FROG.get(), FrogRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_THRUSH.get(), ThrushRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_CREBAIN.get(), CrebainRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_SWARM.get(), SwarmRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GREAT_EAGLE.get(), GreatEagleRender::new);

                // Passive
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_AUROCH.get(), AurochRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MUMAKIL.get(), MumakilRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GOAT.get(), GoatRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_SHADOWFAX.get(), ShadowfaxRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_NAZGULSTEED.get(), NazgulSteedRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GOLLUM.get(), GollumRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_ISTARI.get(), IstariRender::new);

                // Monster
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_BARROW.get(), BarrowWightRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_OATHBREAKER.get(), OathBreakerRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_FELLSPIRIT.get(), FellSpiritRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_ENRAGED_FELLSPIRIT.get(), EnragedFellSpiritRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_BRIGAND.get(), BrigandRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_HARADRIM.get(), HaradrimRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_ROMIEWALKER.get(), RomieWalkerRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MIMICCHEST.get(), MimicChestRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MORDORORC.get(), MordorOrcRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_URUKHAI.get(), UrukHaiRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_DUERGAR.get(), DuergarRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GOBLIN.get(), GoblinRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MIRKWOODSPIDER.get(), MirkwoodSpiderRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_ROCKGOLEM.get(), RockGolemRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_HURON.get(), HuronRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MINOTAUR.get(), MinotaurRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_DEEPCLAW.get(), DeepClawRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_TROLL.get(), TrollRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_TREEENT.get(), TreeEntRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_SWAMPHAG.get(), SwampHagRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_WARG.get(), WargRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_ELEMENTALGOLEM.get(), ElementalGolemRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_NAZGUL.get(), NazgulRender::new);

                // Boss
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GOBLINKING.get(), GoblinKingRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_BALROG.get(), BalrogRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_SHELOB.get(), ShelobRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_WITCHKING.get(), WitchKingRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_WATCHER.get(), WatcherRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MITHRILGOLEM.get(), MithrilGolemRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_MORGULIRONGOLEM.get(), MorgulironGolemRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_GWAHIR.get(), GwahirRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_FELL_BEAST.get(), FellBeastRender::new);

                // NPC
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_HUMAN.get(), HumanRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_DWARF.get(), DwarfRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_ELVES.get(), ElvesRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_HOBBIT.get(), HobbitRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_SOUTHRON.get(), SouthronRender::new);
            EntityRenderers.register(TolkienEntities.ENTITY_TTM_ORC_TRADER.get(), OrcTraderRender::new);

                // Projectile
            EntityRenderers.register(TolkienEntities.AMMO_BOULDER.get(), BoulderRender::new);
            EntityRenderers.register(TolkienEntities.AMMO_FELLBEAST_FIREBALL.get(), FellBeastFireballRender::new);
            EntityRenderers.register(TolkienEntities.AMMO_ARROW_GALADHRIM.get(), (context) -> new TolkienArrowRenderer(context, "ammo_galadhrim_arrow"));
            EntityRenderers.register(TolkienEntities.AMMO_ARROW_UTUMNO.get(), (context) -> new TolkienArrowRenderer(context, "ammo_utumno_arrow"));

                // Boats
            EntityRenderers.register(TolkienEntities.MALLORN_BOAT.get(), (context) -> new MallornBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.MALLORN_CHEST_BOAT.get(), (context) -> new MallornBoatRenderer(context, true));
            EntityRenderers.register(TolkienEntities.MIRKWOOD_BOAT.get(), (context) -> new MirkwoodBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.MIRKWOOD_CHEST_BOAT.get(), (context) -> new MirkwoodBoatRenderer(context, true));
            EntityRenderers.register(TolkienEntities.CULUMALDA_BOAT.get(), (context) -> new CulumaldaBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.CULUMALDA_CHEST_BOAT.get(), (context) -> new CulumaldaBoatRenderer(context, true));
            EntityRenderers.register(TolkienEntities.LEBETHRON_BOAT.get(), (context) -> new LebethronBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.LEBETHRON_CHEST_BOAT.get(), (context) -> new LebethronBoatRenderer(context, true));
            EntityRenderers.register(TolkienEntities.FANGORNOAK_BOAT.get(), (context) -> new FangornoakBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.FANGORNOAK_CHEST_BOAT.get(), (context) -> new FangornoakBoatRenderer(context, true));
            EntityRenderers.register(TolkienEntities.DEADWOOD_BOAT.get(), (context) -> new DeadwoodBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.DEADWOOD_CHEST_BOAT.get(), (context) -> new DeadwoodBoatRenderer(context, true));
            EntityRenderers.register(TolkienEntities.DWARVEN_MAPLE_BOAT.get(), (context) -> new DwarvenMapleBoatRenderer(context, false));
            EntityRenderers.register(TolkienEntities.DWARVEN_MAPLE_CHEST_BOAT.get(), (context) -> new DwarvenMapleBoatRenderer(context, true));

                // Items
            EntityRenderers.register(TolkienEntities.MORGUL_CRYSTAL.get(), MorgulCrystalRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event){
            event.register(TolkienContainers.COIN_POUCH_CONTAINER.get(), CoinPouchScreen::new);
            event.register(TolkienContainers.KEY_RING_CONTAINER.get(), KeyRingScreen::new);
            event.register(TolkienContainers.KEY_ITEM_CONTAINER.get(), KeyItemScreen::new);
            event.register(TolkienContainers.KEY_CODE_CONTAINER.get(), KeyCodeScreen::new);
            event.register(TolkienContainers.TRINKET_TABLE_CONTAINER.get(), TrinketTableScreen::new);
            event.register(TolkienContainers.FIREPLACE_CONTAINER.get(), FireplaceScreen::new);
            event.register(TolkienContainers.PIGGY_BANK_CONTAINER.get(), PiggyBankScreen::new);
            event.register(TolkienContainers.BACKPACK_CONTAINER.get(), BackpackBlockScreen::new);
            event.register(TolkienContainers.BACKPACK_UPGRADE_CONTAINER.get(), BackpackUpgradeScreen::new);
            event.register(TolkienContainers.LOCKABLE_CHEST_CONTAINER.get(), LockableChestScreen::new);
            event.register(TolkienContainers.LOCKABLE_TREASURE_CHEST_CONTAINER.get(), LockableTreasureChestScreen::new);
            event.register(TolkienContainers.LOCKABLE_DOUBLE_CHEST_CONTAINER.get(), LockableDoubleChestScreen::new);
            event.register(TolkienContainers.LOCKABLE_DOUBLE_TREASURE_CHEST_CONTAINER.get(), LockableDoubleTreasureChestScreen::new);
            event.register(TolkienContainers.KEY_STONE_CONTAINER.get(), CamoKeyStoneScreen::new);
            event.register(TolkienContainers.CAMO_SPAWNER_CONTAINER.get(), CamoSpawnerScreen::new);
            event.register(TolkienContainers.CAMO_FLUID_CONTAINER.get(), CamoFluidBlockScreen::new);
            event.register(TolkienContainers.MILESTONE_CONTAINER.get(), MilestoneScreen::new);
        }

        @SubscribeEvent
        public static void onClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(((TolkienFluidType) TolkienFluidTypes.MOLTEN_MITHRIL_LAVA_FLUID_TYPE.get()).getClientFluidTypeExtensions(),
                    TolkienFluidTypes.MOLTEN_MITHRIL_LAVA_FLUID_TYPE.get());
            event.registerFluidType(((TolkienFluidType) TolkienFluidTypes.MOLTEN_MORGULIRON_LAVA_FLUID_TYPE.get()).getClientFluidTypeExtensions(),
                    TolkienFluidTypes.MOLTEN_MORGULIRON_LAVA_FLUID_TYPE.get());
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent pEvent) {
            pEvent.registerSpriteSet(TolkienParticleTypes.MALLORN_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.MIRKWOOD_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.CULUMALDA_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.LEBETHRON_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.FANGORNOAK_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.DEADWOOD_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.DWARVEN_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.LIGHTNINGBUG.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.FELLBEAST_BREATH.get(), FellBeastBreathParticle.Provider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.WIND_PARTICLE.get(), WindParticle.Provider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.WANDERING_LIGHTNINGBUG.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.MALLORN_LEAVES.get(), (p_277215_) -> (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> new LeafParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_));
            pEvent.registerSpriteSet(TolkienParticleTypes.MIRKWOOD_LEAVES.get(), (p_277215_) -> (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> new LeafParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_));
            pEvent.registerSpriteSet(TolkienParticleTypes.CULUMALDA_LEAVES.get(), (p_277215_) -> (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> new LeafParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_));
            pEvent.registerSpriteSet(TolkienParticleTypes.LEBETHRON_LEAVES.get(), (p_277215_) -> (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> new LeafParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_));
            pEvent.registerSpriteSet(TolkienParticleTypes.FANGORNOAK_LEAVES.get(), (p_277215_) -> (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> new LeafParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_));
            pEvent.registerSpriteSet(TolkienParticleTypes.DWARVEN_MAPLE_LEAVES.get(), (p_277215_) -> (p_277217_, p_277218_, p_277219_, p_277220_, p_277221_, p_277222_, p_277223_, p_277224_) -> new LeafParticle(p_277218_, p_277219_, p_277220_, p_277221_, p_277215_));
        }
    }

    public static ResourceLocation getBlockModelTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, BLOCK_DIR + name);
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
    }

    public static ResourceLocation resLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.tryBuild(MODID, path);
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registryKey, String name) {
        return ResourceKey.create(registryKey, prefix(name));
    }
}