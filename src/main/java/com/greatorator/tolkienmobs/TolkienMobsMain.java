package com.greatorator.tolkienmobs;

import com.greatorator.tolkienmobs.containers.screens.*;
import com.greatorator.tolkienmobs.event.TolkienRegistration;
import com.greatorator.tolkienmobs.fluid.TolkienFluidTypes;
import com.greatorator.tolkienmobs.handler.ColorHandler;
import com.greatorator.tolkienmobs.handler.TolkienDataComponents;
import com.greatorator.tolkienmobs.init.*;
import com.greatorator.tolkienmobs.fluid.TolkienFluidType;
import com.greatorator.tolkienmobs.init.TolkienParticleTypes;
import com.greatorator.tolkienmobs.item.custom.CoinPouchItem;
import com.greatorator.tolkienmobs.item.custom.KeyRingItem;
import com.greatorator.tolkienmobs.particle.LeafParticle;
import com.greatorator.tolkienmobs.particle.provider.TolkienParticleProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.Locale;

import static com.greatorator.tolkienmobs.init.TolkienItems.*;

@Mod(TolkienMobsMain.MODID)
public class TolkienMobsMain {
    public static final String MODID = "tolkienmobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String MODEL_DIR = "textures/entity/";
    private static final String BLOCK_DIR = "textures/block/custom/";

    /*
     TODO
      -Fix Rendering of Coin Pouch and Key Ring States
        -Fix GUI for Key Codes
      -Armor Implemented
      -Bows Implemented
      -Projectiles Implemented
      -Biomes
        -Structures
        -Features
      -Arda Portal
      -Enchantments
      -Functional Blocks
        -Fireplace Recipes
        -Trinket Table Recipes
      -Entities
        -Villager Trades
        -Spawn Eggs
      -Integration
        -Curios
        -JEI
     */

    public TolkienMobsMain(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        modEventBus.addListener(this::commonSetup);

        if (dist.isClient()) {
            TolkienRegistration.initModBusEvents(modEventBus);
        }
        TolkienTabs.register(modEventBus);

        TolkienItems.register(modEventBus);
        TolkienBlocks.register(modEventBus);
        TolkienEntities.register(modEventBus);

        TolkienSounds.SOUND_EVENTS.register(modEventBus);
        TolkienEffects.register(modEventBus);
        TolkienContainers.register(modEventBus);

        TolkienPotions.register(modEventBus);
        TolkienParticleTypes.register(modEventBus);
        TolkienVillagers.register(modEventBus);

        TolkienFluidTypes.register(modEventBus);
        TolkienFluids.register(modEventBus);

        TolkienEnchantmentEffects.register(modEventBus);

        TolkienFeatureModifiers.TRUNK_PLACERS.register(modEventBus);
        TolkienFeatureModifiers.FOLIAGE_PLACERS.register(modEventBus);
        TolkienFeatureModifiers.TREE_DECORATORS.register(modEventBus);
        TolkienFeatureModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
        TolkienFeatureModifiers.FEATURES.register(modEventBus);
        TolkienDataComponents.COMPONENTS.register(modEventBus);
        TolkienRecipesTypes.RECIPE_TYPE.register(modEventBus);
        TolkienRecipeSerializers.RECIPE_SERIALIZER.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, TolkienMobsConfig.SPEC);
        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(ColorHandler::itemColourEvent);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
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
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

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

                ItemProperties.register(COIN_POUCH.get(), resLoc("fullness"), CoinPouchItem::getFullnessPropertyOverride);
                ItemProperties.register(KEY_RING.get(), resLoc("fullness"), KeyRingItem::getFullnessPropertyOverride);
            });
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event){
            event.register(TolkienContainers.COIN_POUCH_CONTAINER.get(), CoinPouchScreen::new);
            event.register(TolkienContainers.KEY_RING_CONTAINER.get(), KeyRingScreen::new);
            event.register(TolkienContainers.KEY_ITEM_CONTAINER.get(), KeyItemScreen::new);
            event.register(TolkienContainers.KEY_CODE_CONTAINER.get(), KeyCodeScreen::new);
            event.register(TolkienContainers.TRINKET_TABLE_CONTAINER.get(), TrinketTableScreen::new);
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
            pEvent.registerSpriteSet(TolkienParticleTypes.FALLING_LEAVES.get(), LeafParticle.Factory::new);
        }
    }

    public static ResourceLocation getModelTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, MODEL_DIR + name);
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

    public static ResourceLocation createResourceLocation(String path)
    {
        return ResourceLocation.tryBuild(MODID, path);
    }
}