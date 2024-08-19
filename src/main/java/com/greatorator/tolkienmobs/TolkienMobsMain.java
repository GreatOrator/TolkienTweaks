package com.greatorator.tolkienmobs;

import com.greatorator.tolkienmobs.event.TolkienEvents;
import com.greatorator.tolkienmobs.event.TolkienRegistration;
import com.greatorator.tolkienmobs.fluid.TolkienFluid;
import com.greatorator.tolkienmobs.init.*;
import com.greatorator.tolkienmobs.init.types.TolkienFluidTypes;
import com.greatorator.tolkienmobs.init.types.TolkienParticleTypes;
import com.greatorator.tolkienmobs.particle.provider.TolkienParticleProvider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Locale;

@Mod(TolkienMobsMain.MODID)
public class TolkienMobsMain {
    public static final String MODID = "tolkienmobs";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final String MODEL_DIR = "textures/entity/";
    private static final String BLOCK_DIR = "textures/block/custom/";

    public TolkienMobsMain(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        if (dist.isClient()) {
            TolkienRegistration.initModBusEvents(modEventBus);
        }

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        TolkienTabs.register(modEventBus);

        TolkienItems.register(modEventBus);
        TolkienBlocks.register(modEventBus);

        TolkienEntities.register(modEventBus);
        TolkienSounds.register(modEventBus);
        TolkienEffects.register(modEventBus);

        TolkienPotions.register(modEventBus);
        TolkienParticleTypes.register(modEventBus);

        TolkienFluid.register(modEventBus);
        TolkienFluids.register(modEventBus);

        TolkienFeatureModifiers.TRUNK_PLACERS.register(modEventBus);
        TolkienFeatureModifiers.FOLIAGE_PLACERS.register(modEventBus);
        TolkienFeatureModifiers.TREE_DECORATORS.register(modEventBus);
        TolkienFeatureModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, TolkienMobsConfig.SPEC);
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
            pot.addPlant(TolkienBlocks.SAPLING_MALLORN.getId(), TolkienBlocks.POTTED_SAPLING_MALLORN);
            pot.addPlant(TolkienBlocks.SAPLING_MIRKWOOD.getId(), TolkienBlocks.POTTED_SAPLING_MIRKWOOD);
            pot.addPlant(TolkienBlocks.SAPLING_CULUMALDA.getId(), TolkienBlocks.POTTED_SAPLING_CULUMALDA);
            pot.addPlant(TolkienBlocks.SAPLING_LEBETHRON.getId(), TolkienBlocks.POTTED_SAPLING_LEBETHRON);
            pot.addPlant(TolkienBlocks.SAPLING_FANGORNOAK.getId(), TolkienBlocks.POTTED_SAPLING_FANGORNOAK);
            pot.addPlant(TolkienBlocks.SAPLING_DEADWOOD.getId(), TolkienBlocks.POTTED_SAPLING_DEADWOOD);
            pot.addPlant(TolkienBlocks.MUSHROOM_BLOOM_DECAY.getId(), TolkienBlocks.POTTED_MUSHROOM_BLOOM_DECAY);
            pot.addPlant(TolkienBlocks.MUSHROOM_DECAY_BLOOM.getId(), TolkienBlocks.POTTED_MUSHROOM_DECAY_BLOOM);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(TolkienFluids.MITHRIL_FLOWING.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(TolkienFluids.MORGULIRON_FLOWING.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void onClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(((TolkienFluidTypes) TolkienFluid.MOLTEN_MITHRIL_LAVA_FLUID_TYPE.get()).getClientFluidTypeExtensions(),
                    TolkienFluid.MOLTEN_MITHRIL_LAVA_FLUID_TYPE.get());
            event.registerFluidType(((TolkienFluidTypes) TolkienFluid.MOLTEN_MORGULIRON_LAVA_FLUID_TYPE.get()).getClientFluidTypeExtensions(),
                    TolkienFluid.MOLTEN_MORGULIRON_LAVA_FLUID_TYPE.get());
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent pEvent) {
            pEvent.registerSpriteSet(TolkienParticleTypes.MALLORN_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.MIRKWOOD_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.CULUMALDA_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.LEBETHRON_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.FANGORNOAK_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.DEADWOOD_FLAME.get(), TolkienParticleProvider::new);
            pEvent.registerSpriteSet(TolkienParticleTypes.LIGHTNINGBUG.get(), TolkienParticleProvider::new);
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
}